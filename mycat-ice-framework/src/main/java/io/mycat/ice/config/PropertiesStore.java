package io.mycat.ice.config;

/**
 * 负责加载配置变量，操作PropertiesHolder对象
 *
 * @author wuzhih
 */
public abstract class PropertiesStore {
    /**
     * 获取某个资源所对应的配置变量集（PropertiesHolder）,这里的资源主要是指Ice服务，
     * 通常一个服务对应一套PropertiesHolder
     * 如果对应的PropertiesHolder不存在，则装载defaultphId对应的PropertiesHolder，如果还不存在，则返回空的PropertiesHolder
     *
     * @param phId
     * @param defaultphId
     * @return
     */
    public abstract PropertiesHolder loadPropertiesHolder(String phId, String defaultphId);

    /**
     * 持久化保存配置变量
     *
     * @param ph
     */
    public abstract void updatePropertiesHolder(PropertiesHolder ph);
}
