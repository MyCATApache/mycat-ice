package io.mycat.ice.config;

/**
 * 加载本地properies文件作为配置仓库，其中phId为properties文件的名字（不包括后缀）
 * 本地文件包括：
 * 程序运行时候的当前目录
 * classpath里的资源文件
 *
 * @author wuzhih
 */
public class LocalPropertiesStore extends PropertiesStore {

    @Override
    public PropertiesHolder loadPropertiesHolder(String phId, String defaultphId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updatePropertiesHolder(PropertiesHolder ph) {
        // TODO Auto-generated method stub

    }

}
