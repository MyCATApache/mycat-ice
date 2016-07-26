package demo;

import Ice.Current;
import Ice.ObjectAdapter;
import com.my.demo._MyServiceDisp;
import io.mycat.ice.server.ServantLifcycle;
import org.slf4j.LoggerFactory;

public class MyServiceImpl extends _MyServiceDisp implements ServantLifcycle {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MyServiceImpl.class);

    @Override
    public String hellow(Current __current) {

        return "Hello world";
    }

    @Override
    public void init(ObjectAdapter adapter) {
        String jdbcURL = adapter.getCommunicator().getProperties().getProperty("jdbc_url");
        logger.info("create jdbc pool for " + jdbcURL);

    }

    @Override
    public void destroy() {
        logger.info("close jdbc pool ");
    }

}
