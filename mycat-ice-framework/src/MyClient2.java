import com.my.demo.MyServicePrx;

import io.mycat.ice.utils.ICEClientUtil;

public class MyClient2 {

	public static void main(String[] args)
	{
		MyServicePrx mysvcPrx=(MyServicePrx) ICEClientUtil.getSerivcePrx(MyServicePrx.class);
		System.out.println(mysvcPrx.hellow());
	}
}
