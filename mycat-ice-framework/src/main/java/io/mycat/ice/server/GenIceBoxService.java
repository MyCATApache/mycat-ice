package io.mycat.ice.server;

import java.util.Map;

import org.slf4j.LoggerFactory;

import Ice.Communicator;
import Ice.Identity;
import Ice.ObjectAdapter;
import IceBox.Service;

public class GenIceBoxService implements Service {

	protected ObjectAdapter _adapter;
	protected Identity id;
	protected static org.slf4j.Logger logger = LoggerFactory.getLogger(GenIceBoxService.class);
	protected static Sl4jLogerI iceLogger = new Sl4jLogerI("communicator");
	private volatile static ServiceController serviceCtrl;

	@Override
	public void start(String name, Communicator communicator, String[] args) {

		String loadJars = null;
		String jarSite = null;
		String sharedJarNames = null;
		String myJars=null;
		String allJars="";
		final String PREX = "LoadJarsFromRemote";
		Map<String, String> map = communicator.getProperties().getPropertiesForPrefix(PREX);
		if (map != null && !map.isEmpty()) {
			loadJars = map.get(PREX + ".Enabled");
			jarSite = map.get(PREX + ".Site");
			sharedJarNames = map.get(PREX + ".SharedJars");
			myJars=communicator.getProperties().getProperty("myjars");
			if(sharedJarNames!=null && !sharedJarNames.trim().isEmpty())
			{
				allJars=sharedJarNames.trim();
			}
			if(myJars!=null && !myJars.trim().isEmpty())
			{
				allJars=myJars.trim()+";"+allJars;
			}
		}

		boolean isLoadJar = (null == loadJars) ? false : Boolean.parseBoolean(loadJars);
		ServiceInfo serviceInfo = new ServiceInfo();
		String servantClassName = communicator.getProperties().getProperty("servantClassName");

		serviceInfo.setJarNames(allJars);
		serviceInfo.setServentClassName(servantClassName);
		serviceInfo.setArgs(args);
		serviceInfo.setCommunicator(communicator);
		serviceInfo.setName(name);
		serviceInfo.setService(this);

		Ice.Util.setProcessLogger(iceLogger);

		// 创建objectAdapter，这里和service同名
		_adapter = communicator.createObjectAdapter(name);
		id = communicator.stringToIdentity(name);

		Ice.Object object = null;
		if (isLoadJar) {
			logger.info("load jars from remote for service " + name);
			if (serviceCtrl == null) {
				synchronized (ServiceController.class) {
					serviceCtrl = new ServiceController(jarSite);
				}
			}
			object = (Ice.Object) serviceCtrl.loadService(name, serviceInfo);
		} else {
			logger.info("load jars from local for service " + name);
			try {
				object = (Ice.Object) Class.forName(servantClassName).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (null != object) {
			_adapter.add(PerfDispatchInterceptor.addICEObject(id.toString(), object), id);
		}

		if (object instanceof ServantLifcycle) {
			try {

				((ServantLifcycle) object).init(this._adapter);
			} catch (Exception e) {
				logger.error("Service init failed,class name:" + servantClassName, e);
				return;
			}
		} else {
			logger.warn("Service not implemented ServantLifcycle! ,class name:" + servantClassName);
		}

		_adapter.activate();
		logger.info(name + " service started successful,class name:" + servantClassName);

	}

	@Override
	public void stop() {
		logger.info("stopping service " + id + " ....");
		_adapter.destroy();
		PerfDispatchInterceptor.removeICEObject(id);
		logger.info("stopped service " + id + " stoped");

	}

}
