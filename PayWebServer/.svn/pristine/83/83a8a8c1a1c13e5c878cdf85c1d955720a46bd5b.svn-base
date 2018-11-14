package com.huateng.pay.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alipay.demo.trade.config.Configs;

public class AlipayParamListner implements ServletContextListener{

	public void init() {
		Configs.init("constants.properties");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		init();
	}

}
