//真正的处理用户发送的请求，并且将response准备好的组件
package processor;
import connector.Request;
import connector.Response;

import java.io.IOException;
import java.nio.*;
public class StaticProcessor {
    public void process(Request request, Response response){
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
