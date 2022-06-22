import connector.Request;
import connector.Response;
import processor.ServletProcessor;
import processor.StaticProcessor;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class prorotype2 {
    private static ServerSocket server;
    private static int port=9091;
    public static void main(String[] args){
        try {
            server = new ServerSocket(port);
            System.out.println("启动服务器，监听端口"+port);
            while(true){
                Socket socket = server.accept();
                new Thread(()->{
                    try{
                        InputStream is = socket.getInputStream();
                        OutputStream os = socket.getOutputStream();
                        System.out.println("here");
                        Request request = new Request(is);
                        request.parse();
                        Response response = new Response(os);
                        response.setRequest(request);

                        StaticProcessor processor = new StaticProcessor();
                        processor.process(request,response);

                        Close(socket);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Close(server);
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

}
