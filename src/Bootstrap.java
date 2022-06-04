import connector.Connector;

import java.io.File;

public class Bootstrap {
    public static void main(String[] args) {
        Connector connector = new Connector();
        connector.start();
        //System.out.println(System.getProperty("user.dir")+ File.separator+"webroot");
    }
}
//非阻塞式NIO
/*
//
使用Channel代替Stream
使用Selector监控多条Channel
*/