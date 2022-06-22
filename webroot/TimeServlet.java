import connector.ConnectorUtils;
import connector.HTTPStatus;
import jakarta.servlet.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

//生成动态资源 服务器真正加载准备动态资源的位置
public class TimeServlet implements Servlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }
/*important动态确定内容
返回请求的时刻*/
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        PrintWriter out = servletResponse.getWriter();
        out.println(ConnectorUtils.renderStatus(HTTPStatus.SC_OK));
        out.println("What time is it now?");
        out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
