package connector;
import java.io.*;
//搭建响应报文的基本格式
//未完成
public class ConnectorUtils {
    public static final String WEB_ROOT=System.getProperty("user.dir")+File.separator+"webroot";//资源主目录
    public static final String PROTOCOL = "HTTP/1.1";
    public static final String NEWLINE = "\n";//换行
    public static final String CARRIAGE="\r";//回车
    public static final String SPACE = " ";//空格
    public static String renderStatus(HTTPStatus status){
        StringBuilder sb = new StringBuilder(PROTOCOL)
                .append(SPACE)
                .append(status.getStatusCode())
                .append(SPACE)
                .append(status.getReason())
                .append(CARRIAGE)
                .append(NEWLINE)
                //与上部间隔
                .append(CARRIAGE)
                .append(NEWLINE);
                //响应头之后独立的回车换行
        return sb.toString();
    }
}
