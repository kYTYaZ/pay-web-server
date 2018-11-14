package com.huateng.utils;

import com.liferay.portal.kernel.util.ServerDetector;

public class WebContainerInfo {
 
	public static String getServerName() {
		String serverName = null;
		if (ServerDetector.isWebLogic()) {
			serverName = "WebLogic";
		} else if (ServerDetector.isTomcat()) {
			serverName = "Tomcat";
		} else if (ServerDetector.isWebSphere()) {
			serverName = "WebSphere";
		} else if (ServerDetector.isSupportsComet()) {
			serverName = "SupportsComet";
		} else if (ServerDetector.isResin()) {
			serverName = "Resin";
		} else if (ServerDetector.isOC4J()) {
			serverName = "OC4J";
		} else if (ServerDetector.isJOnAS()) {
			serverName = "JOnAS";
		} else if (ServerDetector.isJetty()) {
			serverName = "Jetty";
		} else if (ServerDetector.isJBoss()) {
			serverName = "JBoss";
		} else if (ServerDetector.isGeronimo()) {
			serverName = "Geronimo";
		} else if (ServerDetector.isGlassfish()) {
			serverName = "Glassfish";
		} else if (ServerDetector.isGlassfish2()) {
			serverName = "Glassfish2";
		} else if (ServerDetector.isGlassfish3()) {
			serverName = "Glassfish3";
		}
		return serverName;
	}
	
	public static void main(String[] args) {
		System.out.println(getServerName());
	}
}
