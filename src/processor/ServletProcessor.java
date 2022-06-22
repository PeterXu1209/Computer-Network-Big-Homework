package processor;

import connector.ConnectorUtils;
import connector.Request;
import connector.Response;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

//url class loader
public class ServletProcessor {
    URLClassLoader getServletLoader() throws MalformedURLException {
        File webroot = new File(ConnectorUtils.WEB_ROOT);
        URL webrootUrl = webroot.toURI().toURL();
        return new URLClassLoader(new URL[]{webrootUrl});
    }
    Servlet getServlet(URLClassLoader loader, Request request) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // /servlet/TimeServlet
        String uri = request.getRequestUri();
        String servletName = uri.substring(uri.lastIndexOf("/")+1);
        Class servletClass = loader.loadClass(servletName);
        Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();
        return servlet;
    }
    public void process(Request request, Response response) throws MalformedURLException {
        URLClassLoader loader = getServletLoader();
        try {
            Servlet servlet = getServlet(loader,request);
            servlet.service(request,response);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (ServletException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
