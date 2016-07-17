package io.mycat.ice.server;

import Ice.ObjectAdapter;

/**
 * Servant生命周期接口
 * 
 * @author wuzhih
 *
 */
public interface ServantLifcycle {
	/**
	 * Servant提供服务之前会被触发此接口调用，可以用于初始化资源，比如建立连接池等
	 */
	public void init(ObjectAdapter adapter);
	/**
	 * Servant被销毁的时候触发此调用，用于释放资源
	 */
	public void destroy();
}
