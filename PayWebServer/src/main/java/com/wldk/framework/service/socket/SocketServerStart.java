package com.wldk.framework.service.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerStart {
	/** 日志 */
	protected Logger log = LoggerFactory.getLogger(SocketServerStart.class);

	public SocketServerStart() {
		SocketService st = new SocketService();
		st.start();
	}

}
