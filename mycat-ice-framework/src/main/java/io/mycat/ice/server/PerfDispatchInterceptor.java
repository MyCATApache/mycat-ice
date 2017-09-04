package io.mycat.ice.server;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.slf4j.LoggerFactory;

import com.zeroc.Ice.DispatchInterceptor;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.OutputStream;
import com.zeroc.Ice.Request;
import com.zeroc.Ice.UserException;

public class PerfDispatchInterceptor extends com.zeroc.Ice.DispatchInterceptor {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(PerfDispatchInterceptor.class);
	private static final Map<String, com.zeroc.Ice.Object> id2ObjectMAP = new java.util.concurrent.ConcurrentHashMap<>();
	private static final PerfDispatchInterceptor self = new PerfDispatchInterceptor();

	public static PerfDispatchInterceptor getINSTANCE() {
		return self;
	}

	public static DispatchInterceptor addICEObject(String id, com.zeroc.Ice.Object iceObj) {
		id2ObjectMAP.put(id, iceObj);
		return self;
	}

	/**
	 * 此方法可以做任何拦截，类似AOP.
	 * 
	 * @throws UserException
	 */
	@Override
	public CompletionStage<OutputStream> dispatch(Request request) throws UserException {
		Identity theId = request.getCurrent().id;

		// request.getCurrent().con会打印出来 local address = 16.156.210.172:50907
		// （回车换行） remote address = 16.156.210.172:51147 这样的信息
		// 其中 local address 为被访问的服务的地址端口，Remote Address为客户端的地址端口
		String inf = "dispach req, method:" + request.getCurrent().operation + ", service:" + theId.name + ", facet:"
				+ request.getCurrent().facet + ", server address:" + request.getCurrent().con;
		long currentTime = System.currentTimeMillis();
		String threadName = Thread.currentThread().getName();

		logger.info(threadName + ":" + inf + "; begin");
		try {
			CompletionStage<OutputStream> reslt = id2ObjectMAP
					.get(request.getCurrent().id.toString() + request.getCurrent().facet).ice_dispatch(request);
			long costTime = System.currentTimeMillis() - currentTime;
			logger.info(threadName + ":" + inf + " success; cost time: " + costTime);
			return reslt;
		} catch (UserException e) {
			logger.error(inf + " error ", e);
			throw e;
		}
	}

	public static void removeICEObject(Identity id) {
		logger.info("remove ice object " + id);
		id2ObjectMAP.remove(id);
	}

	public static com.zeroc.Ice.Object getICEObject(Identity id) {
		logger.info("remove ice object " + id);
		return id2ObjectMAP.get(id);
	}
}
