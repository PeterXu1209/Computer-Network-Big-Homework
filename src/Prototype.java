import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class Prototype {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9091);
            while (true) {
                Socket socket = serverSocket.accept();
                OutputStream clientOutStream = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String info;
                clientOutStream.write(
                        ("HTTP/1.1 200\n"
                                + "Content-Type: text/html\n"
                                + "\n"
                                + "<h1> Hello, world! </h1>").getBytes()
                );

                //System.out.println(info);
                clientOutStream.flush();
                clientOutStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
