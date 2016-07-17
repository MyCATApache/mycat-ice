package io.mycat.ice.server;
/**
 * by song lin 
 */
import java.util.concurrent.ConcurrentHashMap;

public class CurrentJarVersion {
	private final static ConcurrentHashMap<String, String> currentJarVersionMap = new ConcurrentHashMap<String, String>();
	public static CurrentJarVersion getInstance() {
		return CurrentJarVersionHolder.getCurrentJarVersion();
	}

	private CurrentJarVersion() {
	}

	public void pubCurrentJarVersion(String jarName,String version) {
		if(currentJarVersionMap.containsKey(jarName))
			currentJarVersionMap.remove(jarName);
		currentJarVersionMap.put(jarName, version);
	}
	
	public String getCurrentJarVersion(String jarName) {
		return currentJarVersionMap.get(jarName);
	}
	
	private static class CurrentJarVersionHolder{
		private static CurrentJarVersion currentJarVersion = new CurrentJarVersion();
		public static CurrentJarVersion getCurrentJarVersion()
		{
			return currentJarVersion;
		}
	} 
}
