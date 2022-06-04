package connector;
import jakarta.servlet.*;

import java.io.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
public class Request implements ServletRequest {
    private static final int Buffer =1024;
    private InputStream is;
    private String uri;//请求的文件
    public Request(InputStream is){
        this.is = is;
    }
    public String getRequestUri(){
        return uri;
    }
    public void parse(){
        int length=0;
        byte[] buffer = new byte[Buffer];
        try {
            length = is.read(buffer);//获取读了多少字节
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder request = new StringBuilder();
        for (int i = 0; i < length; i++) {
            request.append((char)buffer[i]);
        }
        uri = parseUri(request.toString());
    }
    private String parseUri(String str){
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
    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getProtocolRequestId() {
        return null;
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }
}
