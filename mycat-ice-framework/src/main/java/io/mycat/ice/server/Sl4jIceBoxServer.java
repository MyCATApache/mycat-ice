package io.mycat.ice.server;

import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Util;
import com.zeroc.IceBox.Server;

public class Sl4jIceBoxServer {

	public static void main(String[] args)
	{
		  InitializationData initData = new InitializationData();
	        initData.properties =Util.createProperties();
	        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");
	        initData.logger=new Sl4jLogerI("system");

	        Server server = new Server();
	        System.exit(server.main("IceBox.Server", args, initData));
	}
}
