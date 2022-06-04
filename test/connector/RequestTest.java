package connector;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import util.TestUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestTest {
    private static final String validRequest="GET /index.html HTTP/1.1";
    @Test
    public void givenValidRequest(){
        //Request request = TestUtils.createRequest(validRequest);
        InputStream is = new ByteArrayInputStream(validRequest.getBytes(StandardCharsets.UTF_8));
        Request request = new Request(is);
        request.parse();
        String uri = request.getRequestUri();
        System.out.println(uri);
        Assert.assertEquals("/index.html",request.getRequestUri());
    }

}
