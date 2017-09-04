package io.mycat.ice.utils;

import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.lang.reflect.Method;
import java.util.*;

import com.zeroc.Ice.Communicator;

public class ICEClientUtil {
    private static volatile Communicator ic = null;
    @SuppressWarnings("rawtypes")
    private static Map<Class, ObjectPrx> cls2PrxMap = new HashMap<>();
    // 独立在Grid之外启动的Servant的Endpoint地址
    private static Map<String, String> standAloneServiceEndptMap = new HashMap<String, String>();
    private static volatile long lastAccessTimestamp;
    private static volatile MonitorThread nonitorThread;
    private static long idleTimeOutSeconds = 0;
    private static String iceLocator = null;
    private static final String locatorKey = "--Ice.Default.Locator";

    public static Communicator getICECommunictor() {
        if (ic == null) {
            synchronized (ICEClientUtil.class) {
                if (ic == null) {
                    if (iceLocator == null) {
                        ResourceBundle rb = ResourceBundle.getBundle("iceclient", Locale.ENGLISH);

                        iceLocator = rb.getString(locatorKey);
                        idleTimeOutSeconds = Integer.parseInt(rb.getString("idleTimeOutSeconds"));
                        System.out.println("Ice client's locator is " + iceLocator + " proxy cache time out seconds :"
                                + idleTimeOutSeconds);
                        loadStandAloneProxys(rb);
                    }
                    String[] initParams = new String[]{locatorKey + "=" + iceLocator};
                    // , "--Ice.Default.PreferSecure=1"
                    // String[] initParams = new String[] { locatorKey + "="
                    // + iceLocator };

                    ic = Util.initialize(initParams);
                    createMonitorThread();
                }
            }
        }
        lastAccessTimestamp = System.currentTimeMillis();
        return ic;
    }

    private static void loadStandAloneProxys(ResourceBundle rb) {
        final String sevicePrefix = "local.";
        Enumeration<String> propKeys = rb.getKeys();
        while (propKeys.hasMoreElements()) {
            String key = propKeys.nextElement();
            if (key.startsWith(sevicePrefix)) {
                String serviceName = key.substring(sevicePrefix.length());
                String endpt = rb.getString(key);
                standAloneServiceEndptMap.put(serviceName, endpt);
                System.out.println("find local service:" + serviceName + " at endpoint:" + endpt);
            }
        }
    }

    private static void createMonitorThread() {
        nonitorThread = new MonitorThread();
        nonitorThread.setDaemon(true);
        nonitorThread.start();
    }

    public static void closeCommunicator(boolean removeServiceCache) {
        synchronized (ICEClientUtil.class) {
            if (ic != null) {
                safeShutdown();
                nonitorThread.interrupt();
                if (removeServiceCache && !cls2PrxMap.isEmpty()) {
                    try {
                        cls2PrxMap.clear();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }

    }

    private static void safeShutdown() {
        try {
            ic.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ic.destroy();
            ic = null;
        }
    }

    /**
     * 仅限于Ice服务内部之间非异步方法的场景
     *
     * @param communicator
     * @param serviceCls
     * @return ObjectPrx
     */
    @SuppressWarnings("rawtypes")
    public static ObjectPrx getSerivcePrx(Communicator communicator, Class serviceCls) {
        return createIceProxy(communicator, serviceCls);

    }

    @SuppressWarnings("rawtypes")
    private static ObjectPrx createIceProxy(Communicator communicator, Class serviceCls) {
        String serviceName = serviceCls.getSimpleName();
        int pos = serviceName.lastIndexOf("Prx");
        if (pos <= 0) {
            throw new java.lang.IllegalArgumentException("Invalid ObjectPrx class ,class name must end with Prx");
        }
        String realSvName = serviceName.substring(0, pos);
        ObjectPrx base = null;
        // 检查是否是在Grid之外单独启动的Servant
        String localEndp = standAloneServiceEndptMap.get(realSvName);
        if (localEndp != null) {
            base = communicator.stringToProxy(localEndp);
        } else {
            base = communicator.stringToProxy(realSvName);
        }
        return createObjectPrxFromEndpoint(communicator, base, serviceCls);
    }

    @SuppressWarnings("rawtypes")
    private static ObjectPrx createObjectPrxFromEndpoint(Communicator communicator, com.zeroc.Ice.ObjectPrx base,
                                                             Class serviceCls) {
        try {
            Method m1 = serviceCls.getDeclaredMethod("uncheckedCast", ObjectPrx.class);
            ObjectPrx proxy = (ObjectPrx) m1.invoke(serviceCls, base);
            return proxy;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 用于客户端API获取ICE服务实例的场景
     *
     * @param serviceCls
     * @return ObjectPrx
     */
    @SuppressWarnings("rawtypes")
    public static ObjectPrx getSerivcePrx(Class serviceCls) {
        ObjectPrx proxy = cls2PrxMap.get(serviceCls);
        if (proxy != null) {
            lastAccessTimestamp = System.currentTimeMillis();
            return proxy;
        }

        proxy = createIceProxy(getICECommunictor(), serviceCls);
        cls2PrxMap.put(serviceCls, proxy);
        lastAccessTimestamp = System.currentTimeMillis();
        return proxy;
    }

    static class MonitorThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000L);
                    if (lastAccessTimestamp + idleTimeOutSeconds * 1000L < System.currentTimeMillis()) {
                        closeCommunicator(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
