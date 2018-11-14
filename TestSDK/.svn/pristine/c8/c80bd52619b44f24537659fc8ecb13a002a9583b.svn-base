package com.trade.ali.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.common.dicts.Dict;
import com.util.HttpUtil;
import com.util.PropertyUtil;

/**
 * 模拟支付宝通知高并发
 * @author Administrator
 *
 */
public class NotifyTest {
	
	public static void main(String[] args) {
		
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < 3000; i++) {
			final int index = i;
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String notifyUrl = PropertyUtil.getProperty(Dict.ali_notify_url);
					Map<String,String> map = new HashMap<String, String>();
					try {
						map.put("inputParam", index+"");
						String respMsg = HttpUtil.httpRequest(notifyUrl, map);
						System.out.println(respMsg);
					} catch (Exception e) {
						System.out.println(index);
						e.printStackTrace();
					}
				}
				
			});
		}
	}

}
