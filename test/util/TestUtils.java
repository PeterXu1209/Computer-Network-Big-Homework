package util;
import connector.Request;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    public static Request createRequest(String requeststr){
        InputStream is = new ByteArrayInputStream(requeststr.getBytes(StandardCharsets.UTF_8));
        Request request = new Request(is);
        request.parse();
        return request;
    }
    public static String readFileToString(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
