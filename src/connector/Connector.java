package connector;

import jakarta.servlet.Servlet;
import processor.ServletProcessor;
import processor.StaticProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class Connector implements Runnable{
    private static final int PORT=8081;
    private ServerSocketChannel server;
    private Selector selector;
    private int port;
    public Connector(){
        this(PORT);
    }
    public Connector(int port) {
        this.port=port;
    }
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run(){
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);//非阻塞式的
            server.socket().bind(new InetSocketAddress(port));

            selector=Selector.open();//IO多路复用
            server.register(selector, SelectionKey.OP_ACCEPT);//有新的客户端连接，服务器监听到了客户连接
            System.out.println("启动服务器，监听端口"+port);
            while(true){
                selector.select();//监听是否有这个事件发生
                Set<SelectionKey>selectionKeys=selector.selectedKeys();
                //一个一个处理这些事件
                for(SelectionKey key:selectionKeys){
                    //处理被触发的事件
                    handles(key);
                }
                //消除这些key
                selectionKeys.clear();
            }
//            server = new ServerSocket(port);
//            System.out.println("启动服务器，监听端口"+port);
//
//            while(true){
//                Socket socket = server.accept();
//                InputStream is = socket.getInputStream();
//                OutputStream os = socket.getOutputStream();
//
//                Request request = new Request(is);
//                request.parse();
//                Response response = new Response(os);
//                response.setRequest(request);
//
//                StaticProcessor processor = new StaticProcessor();
//                processor.process(request,response);
//
//                Close(socket);
//        }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Close(server);
        }

    }
    private void Close(Closeable closeable){
        if(closeable!=null){
            try{
                closeable.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void handles(SelectionKey key) throws IOException{
        //先处理accept事件，有客户端发送请求
        if (key.isAcceptable()) {//如果是accept
            ServerSocketChannel server = (ServerSocketChannel) key.channel();//得到对应的ServerSocketChannel
            SocketChannel client = server.accept();//accept客户的请求
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);//绑定Read事件
        }
        //再处理READ事件
        else{
            SocketChannel client = (SocketChannel) key.channel();
            key.cancel();//解锁selector与socketchannel的关系，不需要保持与客户端的连接，这里只需要发送一次消息然后关闭连接就足够了。
            client.configureBlocking(true);
            Socket clientSocket = client.socket();
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            Request request = new Request(is);
            request.parse();

            Response response = new Response(os);
            response.setRequest(request);
            if(request.getRequestUri().startsWith("/servlet/")){
                ServletProcessor processor = new ServletProcessor();
                processor.process(request,response);
            }else{
                StaticProcessor processor = new StaticProcessor();
                processor.process(request,response);

            }
            Close(client);
        }

    }

}
