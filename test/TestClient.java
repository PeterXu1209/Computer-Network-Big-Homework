import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",9091);

        OutputStream os = socket.getOutputStream();
        os.write("GET /index.html HTTP/1.1".getBytes(StandardCharsets.UTF_8));
        InputStream is = socket.getInputStream();
        socket.shutdownOutput();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
    }
}
