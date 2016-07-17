package io.mycat.ice.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * by song lin;wuzh
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ServiceController {
	protected static org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceController.class);
	private RemoteJarServiceLoader loader = new RemoteJarServiceLoader();
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	private final static ConcurrentHashMap<String, ServiceInfo> serviceMap = new ConcurrentHashMap<String, ServiceInfo>();

	private JSONObject prevJarVersionInf;

	private final String jarSite;

	public ServiceController(String jarSite) {
		this.jarSite = jarSite;
		this.prevJarVersionInf=loadVersionFile();
		service.scheduleAtFixedRate(new AutoUpdateTask(), 0l, 1l, TimeUnit.SECONDS);
	}

	public JSONObject loadVersionFile() {

		String version = null;
		try {
			version = loadHTTPURLContent(jarSite + "/version.json");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (null == version) {
			return null;
		}
		JSONObject jarMap = (JSONObject) JSON.parse(version);
		return jarMap;
	}

	private void registerService(String serviceName, ServiceInfo serviceInfo) {
		if (serviceMap.containsKey(serviceName)) {
			serviceMap.remove(serviceName);
		}
		serviceMap.put(serviceName, serviceInfo);
	}

	public Object loadService(String serviceName, ServiceInfo serviceInfo) {
		registerService(serviceName, serviceInfo);
		return loader.loadService(prevJarVersionInf,this.jarSite, serviceInfo);
	}
	
	private  String loadHTTPURLContent(String httpPath) {
		java.net.HttpURLConnection con = null;
		try {
			java.net.URL url = new URL(httpPath);
			con = (HttpURLConnection) url.openConnection();
			con.connect();
			java.io.ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream ins = con.getInputStream();
			byte[] buf = new byte[1024 * 4];
			int readed = 0;
			while ((readed = ins.read(buf)) != -1) {
				out.write(buf, 0, readed);
			}

			return out.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private class AutoUpdateTask implements Runnable {

		@Override
		public void run() {
			if (null == serviceMap || serviceMap.isEmpty()) {
				return;
			}

			for (Map.Entry<String, ServiceInfo> entry : serviceMap.entrySet()) {
				// serviceLoader.loadService((ServiceInfo) entry.getValue());
			}
		}
	}
}
