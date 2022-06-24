import connector.ConnectorUtils;
import connector.HTTPStatus;

import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
            int flag=1;
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
            AtomicReference<String> url = null;

            Thread t = new Thread(() -> {
                try {
                    int _bytesRead;
                    byte[] buffer = new byte[1024];

                    while ((_bytesRead = streamFromClient.read(request)) != -1) {
                        String temp=null;
                        StringBuilder _request = new StringBuilder();
                        for (int i = 0; i < _bytesRead; i++) {
                            _request.append((char)request[i]);
                        }
                        temp=parseUri(_request.toString());
                        System.out.println(temp);
                        if(temp.startsWith("/www")){
                            String real = temp.toString();
                            real=real.substring(1);
                            real="https://"+real;
                            System.out.println(real);
                            String read = sendRequest(real,null);
                            //System.out.println(read);
                            streamToClient.write(ConnectorUtils.renderStatus(HTTPStatus.SC_OK).getBytes(StandardCharsets.UTF_8));
                            streamToClient.write(read.getBytes(StandardCharsets.UTF_8));
                            streamToClient.flush();
                            streamToClient.close();
                        }
                        if(temp.startsWith("https")){
                            String real = temp.toString();
                            real=real.substring(1);
                            //real="https://"+real;
                            System.out.println(real);
                            String read = sendRequest(real,null);
                            //System.out.println(read);
                            streamToClient.write(ConnectorUtils.renderStatus(HTTPStatus.SC_OK).getBytes(StandardCharsets.UTF_8));
                            streamToClient.write(read.getBytes(StandardCharsets.UTF_8));
                            streamToClient.flush();
                            streamToClient.close();
                        }
                        streamToServer.write(request, 0, _bytesRead);
                        streamToServer.flush();
                    }
                } catch (Exception e) {
                }
                try {
                    streamToServer.close();
                } catch (IOException e) {
                }
            });
            t.start();
            //读取服务器的相应，将其发送会客户端
            if(url==null){
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
            else if(url!=null){
                String real = url.toString();
                real=real.substring(1);
                String read = sendRequest(real,null);
                System.out.println(read);
            }
        }
    }
    /**
     * url:  请求的网页地址
     * data: 提交的数据,填null使用get方法.
     */
    public static String sendRequest(String url, String data) throws IOException {
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            URL httpUrl = null; // HTTP URL类 用这个类来创建连接
            // 创建URL
            httpUrl = new URL(url);
            // 建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            if (data == null)
                conn.setRequestMethod("GET");
            else
                conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "keep-alive");
            //conn.setRequestProperty("Cookie","");
            //conn.setRequestProperty("Origin", "http://www.baidu.com");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

            conn.setUseCaches(false);// 设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            // POST请求
            if (data != null) {
                out = new OutputStreamWriter(conn.getOutputStream());
                out.write(data);
                out.flush();
            }

            // 读取响应
            reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()
                            , "utf-8")
            );
            String str = null;

            while ((str = reader.readLine()) != null) {
                //lines = new String(lines.getBytes(), "utf-8");
                //response += lines;
                response.append(str+"\r\n");
            }
            reader.close();
            // 断开连接
            conn.disconnect();

            // System.out.print(response.toString());

        } catch (Exception e) {
            System.out.println("发送 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return response.toString();
    }
    public static String parseUri(String str){
        int index1,index2;
        index1=str.indexOf(' ');
        if (index1!=-1){//确定格式是否正确
            index2=str.indexOf(' ',index1+1);
            if(index2!=-1&&index2>index1){//确定格式是否正确
                return str.substring(index1+1,index2);
            }
        }
        return "";
    }



}

