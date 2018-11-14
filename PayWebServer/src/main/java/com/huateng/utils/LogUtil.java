package com.huateng.utils;

import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志打印
 * 
 * @author zhaodk
 *
 */
public class LogUtil {
	private static final Logger GATELOG = LoggerFactory
			.getLogger("BSZ_PAY_LOG");
	private static final Logger GATELOG_ERROR = LoggerFactory
			.getLogger("BSZ_ERR_LOG");

	private static final Logger GATELOG_MESSAGE = LoggerFactory
			.getLogger("SDK_MSG_LOG");
	static final String LOG_STRING_REQ_MSG_BEGIN = "============================== BSZPAY REQ MSG BEGIN ==============================";
	static final String LOG_STRING_REQ_MSG_END = "==============================  BSZPAY REQ MSG END  ==============================";
	static final String LOG_STRING_RSP_MSG_BEGIN = "============================== BSZPAY RSP MSG BEGIN ==============================";
	static final String LOG_STRING_RSP_MSG_END = "==============================  BSZPAY RSP MSG END  ==============================";

	public static void writeLog(String cont) {
		GATELOG.info(cont);
	}

	public static void writeLog(String cont, String frameId) {
		GATELOG.info("[" + frameId + "]" + cont);
	}

	public static void writeErrorLog(String cont) {
		GATELOG_ERROR.error(cont);
	}

	public static void writeErrorLog(String cont, String frameId) {
		GATELOG_ERROR.error("[" + frameId + "]" + cont);
	}

	public static void writeErrorLog(String cont, Throwable ex) {
		GATELOG_ERROR.error(cont, ex);
	}

	public static void writeErrorLog(String cont, String frameId, Throwable ex) {
		GATELOG_ERROR.error("[" + frameId + "]" + cont, ex);
	}

	public static void writeMessage(String msg) {
		GATELOG_MESSAGE.info(msg);
	}

	public static void printRequestLog(Map<String, String> reqParam) {
		writeMessage("============================== BSZPAY REQ MSG BEGIN ==============================");
		Iterator it = reqParam.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			writeMessage("[" + (String) en.getKey() + "] = ["
					+ (String) en.getValue() + "]");
		}
		writeMessage("==============================  BSZPAY REQ MSG END  ==============================");
	}

	public static void printResponseLog(String res) {
		writeMessage("============================== BSZPAY RSP MSG BEGIN ==============================");
		writeMessage(res);
		writeMessage("==============================  BSZPAY RSP MSG END  ==============================");
	}

	public static void trace(String cont) {
		if (GATELOG.isTraceEnabled())
			GATELOG.trace(cont);
	}

	public static void debug(String cont) {
		if (GATELOG.isDebugEnabled())
			GATELOG.debug(cont);
	}
}
