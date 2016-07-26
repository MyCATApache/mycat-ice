package io.mycat.ice.config;

/**
 * 提供简单的静态方法获取服务对应的配置变量，可能来自Zookeeper,Database或者某个文件
 *
 * @author wuzhih
 */
public class ConfigTool {
    static final PropertiesStore propStore;

    static {
        propStore = new LocalPropertiesStore();
    }

    /**
     * 获取某个资源所对应的配置变量集（PropertiesHolder）,这里的资源主要是指Ice服务，通常一个服务对应一套PropertiesHolder
     * 如果对应的PropertiesHolder不存在，则装载defaultphId对应的PropertiesHolder
     *
     * @param phId
     * @param defaultphId
     * @return
     */
    public static PropertiesHolder getPropertiesHolder(String phId, String defaultphId) {
        return propStore.loadPropertiesHolder(phId, defaultphId);
    }
}
