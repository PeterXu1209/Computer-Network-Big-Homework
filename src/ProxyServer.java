import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
public class ProxyServer {
    private static ServerSocketChannel server;
    private static Selector selector;

    public static void main(String[] args) throws IOException {
        try {
            String host = "localhost";
            int destintaion_port = 8081;
            int local_port = 8026;
            System.out.println("为端口 " + destintaion_port + ":" +"启动代理服务器"+ "在端口" + local_port);
            RunServer(host, destintaion_port, local_port); //它会永远启动
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void RunServer(String host, int destintaion_port, int local_port) throws IOException {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);//非阻塞式的
        server.socket().bind(new InetSocketAddress(local_port));
        selector=Selector.open();//IO多路复用
        server.register(selector, SelectionKey.OP_ACCEPT);//有新的客户端连接，服务器监听到了客户连接
        System.out.println("启动服务器，监听端口:"+local_port);
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //一个一个处理这些事件
            for (SelectionKey key : selectionKeys) {
                //处理被触发的事件
                handles(key,destintaion_port, local_port,host);
            }
            //消除这些key
            selectionKeys.clear();
        }
    }
    private static void Close(Closeable closeable){
        if(closeable!=null){
            try{
                closeable.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private static void handles(SelectionKey key,int destintaion_port, int local_port,String host) throws IOException {
        //先处理accept事件，有客户端发送请求
        if (key.isAcceptable()) {//如果是accept
            ServerSocketChannel server = (ServerSocketChannel) key.channel();//得到对应的ServerSocketChannel
            SocketChannel client = server.accept();//accept客户的请求
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);//绑定Read事件
        }
        //再处理READ事件
        else {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            SocketChannel client = (SocketChannel) key.channel();
            key.cancel();//解锁selector与socketchannel的关系，不需要保持与客户端的连接。
            client.configureBlocking(true);
            Socket clientSocket = client.socket();
            final InputStream streamFromClient = clientSocket.getInputStream();
            final OutputStream streamToClient = clientSocket.getOutputStream();
            Socket server = null;
            try {
                server = new Socket(host, destintaion_port);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(streamToClient);
                out.print("代理服务器无法连接到 " + host + ":" + destintaion_port + ":\n" + e + "\n");
                out.flush();
                client.close();
            }
            final InputStream streamFromServer = server.getInputStream();
            final OutputStream streamToServer = server.getOutputStream();
            Thread t = new Thread(() -> {
                try {
                    int _bytesRead;
                    while ((_bytesRead = streamFromClient.read(request)) != -1) {
                        streamToServer.write(request, 0, _bytesRead);
                        streamToServer.flush();
                    }
                } catch (IOException e) {
                }
                try {
                    streamToServer.close();
                } catch (IOException e) {
                }
            });
            t.start();
            //读取服务器的相应，将其发送会客户端
            int bytesRead;
            try {
                while ((bytesRead = streamFromServer.read(reply)) != -1) {
                    streamToClient.write(reply, 0, bytesRead);
                    streamToClient.flush();
                }
            } catch (IOException e) {
            }//关闭与客户端的连接
            streamToClient.close();
            if(server!=null){
                Close(server);
            }
            if(client!=null){
                Close(client);
            }
        }
    }
}

