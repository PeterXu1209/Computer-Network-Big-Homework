package connector;

import org.junit.jupiter.api.Test;
import util.TestUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class Responsetest {
    
    public static final String validRequest="GET /index.html HTTP/1.1";
    public static final String invalidRequest="GET /hello.html HTTP/1.1";
    public static final String status200 = "HTTP/1.1 200 OK\r\n\r\n";
    public static final String status404 ="HTTP/1.1 404 File Not Found\r\n\r\n";
    @Test
    public void givenValidRequest() throws IOException {

        Request request = TestUtils.createRequest(validRequest);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response response = new Response(out);
        response.setRequest(request);
        response.sendStaticResource();
        String resource = TestUtils.readFileToString(ConnectorUtils.WEB_ROOT+ request.getRequestUri());
        //Assert.asserEquals
    }


}
