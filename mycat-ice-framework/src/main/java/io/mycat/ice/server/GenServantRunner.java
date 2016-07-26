package io.mycat.ice.server;

import Ice.Object;

/**
 * 运行一个servant类，方便本地测试用
 *
 * @author wuzhih
 */
public class GenServantRunner {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing params: serviceName servantClassName \r\n Example:\r\n  MyService com.my.demo.MyServiceImpl");
            System.exit(-1);
        }
        String serviceName = args[0];
        String servantClass = args[1];
        int status = 0;
        Ice.Communicator ic = null;
        try {

            ic = Ice.Util.initialize();
            // 创建名为MyServiceAdapter的ObjectAdapter，使用缺省的通信协议（TCP/IP 端口为20000的请求）
            Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints(
                    serviceName + "Adapter", "default -p 20000");
            // 实例化一个服务对象(Servant)
            Ice.Object servant = (Object) Class.forName(servantClass).newInstance();
            // 将Servant增加到ObjectAdapter中，并将Servant关联到ID为MyService的Ice Object
            adapter.add(servant, Ice.Util.stringToIdentity(serviceName));
            // 激活ObjectAdapter
            adapter.activate();
            // 让服务在退出之前，一直持续对请求的监听
            System.out.print("server started for " + serviceName);
            ic.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
        } finally {
            if (ic != null) {
                ic.destroy();
            }
        }
        System.exit(status);
    }

}
