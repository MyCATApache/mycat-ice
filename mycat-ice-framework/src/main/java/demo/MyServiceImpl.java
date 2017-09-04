package demo;

import org.slf4j.LoggerFactory;

import com.my.demo.MyService;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectAdapter;

import io.mycat.ice.server.ServantLifcycle;

public class MyServiceImpl implements MyService,ServantLifcycle {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MyServiceImpl.class);

    @Override
	public String hellow(Current current) {
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
