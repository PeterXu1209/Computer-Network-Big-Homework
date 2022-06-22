package connector;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/*basic response format:
* HTTP/1.1 200 OK
* */
public class Response implements ServletResponse {
    private static final int Buffer=1024;
    Request request;
    OutputStream os;
    public Response(OutputStream os){
        this.os = os;
    }
    public void setRequest(Request request){
        this.request = request;
    }
    //从request资源获取uri后将资源作为数据通过os返回给客户
    public void sendStaticResource() throws IOException{
        File file = new File(ConnectorUtils.WEB_ROOT,request.getRequestUri());
        try{
            write(file,HTTPStatus.SC_OK);
        }catch (IOException e){//若发生问题
            write(new File(ConnectorUtils.WEB_ROOT,"404.html"),HTTPStatus.SC_NOT_FOUND);//找不到请求的文件，返回404
        }
    }
    private void write(File resource,HTTPStatus status ) throws IOException {
        try(FileInputStream fis = new FileInputStream(resource)){
            //剥离逻辑，避免重复与重构
            os.write(ConnectorUtils.renderStatus(status).getBytes(StandardCharsets.UTF_8));
            byte[] buffer = new byte[Buffer];
            int length = 0;
            while((length = fis.read(buffer,0,Buffer))!=-1){
                os.write(buffer,0,length);//资源对应的文件具体内容
            }
        }
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
    //返回writer实例
    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter writer = new PrintWriter(os,true);//打开自动刷新
        return writer;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
