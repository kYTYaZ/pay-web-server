/**
 * 
 */
package com.wldk.framework.mapping.web.util;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.velocity.app.Velocity;

import com.wldk.framework.system.web.util.GlobalParameterListener;

/**
 * @author Administrator
 * 
 */
public class MappingConfigListener implements ServletContextListener 
{
	/** 应用程序的sql文件存在文件夹*/
	private final static String PATH = "sql";
	
	public void contextDestroyed(ServletContextEvent event) 
	{
		MappingWebConfigurer.shutdownMapping(event.getServletContext());
	}

	public void contextInitialized(ServletContextEvent event) 
	{
		//1.初始化Velocity
		//这里初始化Velocity，以免出现多次Velocity.init()时报错。这样改动较小
		String loader = System.getProperty(GlobalParameterListener.WEB_INFO)
				+ System.getProperty("file.separator") + PATH;
		Properties p = new Properties();
		p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, loader);
		p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, "true");//开启缓存
		p.setProperty("file.resource.loader.modificationCheckInterval", "0");//检查模板是否需要更新 0不检查
		Velocity.init(p);
		
		//工程启动直接加载所有模板到缓存中
		File file = new File(loader);
		for(File f : file.listFiles()){
			Velocity.getTemplate(f.getName(),"UTF-8");
		}
		
		//2.启动加载mapping-config.xml中的元数据
		MappingWebConfigurer.initMapping(event.getServletContext());
	}

}
