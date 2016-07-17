package io.mycat.ice.config;
/**
 * 当Prop的值发生变化后，回调通知
 * @author wuzhih
 *
 */
public interface PropUpdatedHandler {

	public void valueChanged(String propName,String oldVal ,String newValue);
	
}
