package io.mycat.ice.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class RemoteJarServiceLoader {

	protected static org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteJarServiceLoader.class);

	public Object loadService(JSONObject jarversionJson, String jarSite, ServiceInfo serviceInfo) {
		String jars[] = serviceInfo.getJarNames().split(";");
		StringBuffer sb = new StringBuffer();
		for (String jar : jars) {
			String realJar = getRealJarName(jar, jarversionJson);
			CurrentJarVersion.getInstance().pubCurrentJarVersion(jar, realJar);
			sb.append(realJar).append(";");

		}
		serviceInfo.setJarNames(sb.toString().substring(0, sb.toString().length() - 1));
		return createObjectFromURLClassLoad(jarSite, serviceInfo);
	}

	private String getRealJarName(String jarName, JSONObject jarMap) {
		Object jarVersion = jarMap.get(jarName);
		String realJar = jarName;
		if (jarVersion != null) {
			realJar = jarName + "-" + jarVersion.toString() + ".jar";

		}
		return realJar;
	}

	private URLClassLoader getURLClassLoader(String jarSite, String jarNames) throws MalformedURLException {
		URLClassLoader loader = null;

		String fileNames[] = jarNames.split(";");
		if (fileNames != null && fileNames.length > 0) {
			URL urls[] = new URL[fileNames.length];
			for (int i = 0; i < fileNames.length; i++) {
				try {
					urls[i] = new URL(jarSite + "/" + fileNames[i]);
				} catch (MalformedURLException e) {
					throw new RuntimeException("bad url", e);
				}
			}
			loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		}

		return loader;
	}

	private Object createObjectFromURLClassLoad(String jarSite, ServiceInfo serviceInfo) {
		URLClassLoader loader = null;
		try {
			logger.info("load remote jars for " + serviceInfo.getName() + ",from " + jarSite + " ,jars:"
					+ serviceInfo.getJarNames());
			loader = getURLClassLoader(jarSite, serviceInfo.getJarNames());
		} catch (MalformedURLException e) {
			logger.error("JarLoadUtil newInstance", e);
		}
		Class<?> clazz = null;
		try {
			clazz = loader.loadClass(serviceInfo.getServentClassName());
		} catch (ClassNotFoundException e) {
			logger.error("JarLoadUtil newInstance", e);
		}
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("JarLoadUtil newInstance", e);
		}
		loader = null;
		return obj;
	}
}
