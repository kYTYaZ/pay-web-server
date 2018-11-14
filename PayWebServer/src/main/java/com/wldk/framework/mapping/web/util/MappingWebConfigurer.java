/**
 * 
 */
package com.wldk.framework.mapping.web.util;

import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;

import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;

import com.wldk.framework.db.DataSourceName;
import com.wldk.framework.mapping.MappingContext;
import com.wldk.framework.mapping.xml.Attribute;
import com.wldk.framework.mapping.xml.EntityElement;
import com.wldk.framework.mapping.xml.MappingElement;
import com.wldk.framework.mapping.xml.Node;
import com.wldk.framework.mapping.xml.OptionElement;
import com.wldk.framework.mapping.xml.SAXConfigurator;
import com.wldk.framework.mapping.xml.SqlElement;
import com.wldk.framework.utils.StringUtil;

/**
 * @author Administrator
 * 
 */
public class MappingWebConfigurer {
	/** Parameter specifying the location of the datasource config file */
	public static final String CONFIG_LOCATION_PARAM = "mappingConfigLocation";
	/** Parameter specifying whether to expose the web app root system property */
	public static final String EXPOSE_WEB_APP_ROOT_PARAM = "mappingExposeWebAppRoot";
	/** log4j */
	private static Logger log = LoggerFactory.getLogger(MappingWebConfigurer.class);

	/**
	 * 初始化
	 * 
	 * @param servletContext
	 */
	public static void initMapping(ServletContext servletContext) 
	{
		log.info("Initializing Mapping Context...");
		// Expose the web app root system property.
		if (exposeWebAppRoot(servletContext)) {
			WebUtils.setWebAppRootSystemProperty(servletContext);
		}

		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location != null) {
			try {
				if (!ResourceUtils.isUrl(location)) {
					location = SystemPropertyUtils.resolvePlaceholders(location);
					location = WebUtils.getRealPath(servletContext, location);
				}
				location = SystemPropertyUtils.resolvePlaceholders(location);
				URL url = ResourceUtils.getURL(location);
               // System.setProperty("entityExpansionLimit", "64000000");
				// Write log message to log.
				log.info("Mapping Configuration from [" + url + "]");
				// 读取XML配置文件
				Document doc = SAXConfigurator.getInstance().configure(url);
				// 解析XML配置文件
				log.debug("解析内存配置：[ "+url+" ] 文件开始 ...");
				doConfigure(doc);
				log.debug("解析内存配置：[ "+url+" ] 文件完成 ...");
				// 初始化映射
				MappingContext.getInstance().init();
			} catch (FileNotFoundException ex) {
				log.error("系统内存：[ 初始化失败 ]  ");
				log.error("Invalid 'mappingConfigLocation' parameter: " +ex.getMessage());
			    ex.printStackTrace();
				throw new IllegalArgumentException("Invalid 'mappingConfigLocation' parameter: " + ex.getMessage());
			} catch (Exception e) {
				log.error("Invalid Parse: " + e.getMessage(),e);
				throw new IllegalArgumentException("Invalid Parse: " + e.getMessage());
			}
		}
		log.info("Initialized Mapping Context is completed.");
	}

	public static void shutdownMapping(ServletContext servletContext) {
		log.info("Shutting down Mapping Context...");
		try {
			MappingContext.getInstance().shutdown();
		} finally {
			// Remove the web app root system property.
			if (exposeWebAppRoot(servletContext)) {
				WebUtils.removeWebAppRootSystemProperty(servletContext);
			}
		}
		log.info("Shutdown Mapping Context is Completed.");
	}

	/**
	 * 解析XML，并生成元数据对象
	 * 
	 * @param doc
	 * @throws Exception
	 */
	protected static void doConfigure(Document doc) throws Exception {
		// 代码映射上下文对象
		MappingContext context = MappingContext.getInstance();
		Element root = doc.getRootElement(); // 获取根节点对象
		List mappingNodes = root.getChildren(Node.MAPPING.getNodeName());
		for (int i = 0, n = mappingNodes.size(); i < n; i++) {
			Element mappingNode = (Element) mappingNodes.get(i);
			if (mappingNode != null) {
				// 初始化配置元数据
				MappingElement mappingElem = new MappingElement();
				// 映射键值
				String id = mappingNode.getAttributeValue(Attribute.ID.getAttribute());
				String dsName = mappingNode.getAttributeValue(Attribute.DSNAME.getAttribute());
				// 设置属性
				mappingElem.setId(id);
				mappingElem.setDsName(DataSourceName.ds(dsName));
				if (!dsName.equalsIgnoreCase(DataSourceName.MANUAL.getDs())) { // 从数据库中获取映射
					SqlElement sqlElem = new SqlElement();
					// 获取SQL语句
					String sql = mappingNode.getChildText(Node.SQL.getNodeName());
					sqlElem.setSql(sql);
					mappingElem.setSql(sqlElem);
				} else { // 手动获取映射
					// 获取entity节点
					Element entityNode = mappingNode.getChild(Node.ENTITY.getNodeName());
					if (entityNode != null) {
						EntityElement entityElem = new EntityElement();
						// 获取option节点列表
						List optionNodes = entityNode.getChildren(Node.OPTION.getNodeName());
						if (optionNodes != null) {
							for (int j = 0, m = optionNodes.size(); j < m; j++) {
								Element optionNode = (Element) optionNodes.get(j);
								if (optionNode != null) {
									OptionElement optionElem = new OptionElement();
									String name = optionNode.getAttributeValue(Attribute.NAME.getAttribute());
                                    //"#"表示："<"，"$"表示：">"，"|"表示："/>"
//									System.out.println("name="+name);
                                    name=StringUtil.replaceAll(name, new String[]{"(",")","|"}, new String[]{"<",">","/>"});
//                                    System.out.println("name="+name);
									String value = optionNode.getAttributeValue(Attribute.VALUE.getAttribute());
									optionElem.setName(name);
									optionElem.setValue(value);
									entityElem.getOptions().add(optionElem);
								}
							}
						}
						mappingElem.setEntity(entityElem);
					}
				}
				// 加入配置元数据
				context.addMetadata(id, mappingElem);
			}
		}
		// log.debug(context.getMetadata());
	}

	/**
	 * Return whether to expose the web app root system property, checking the
	 * corresponding ServletContext init parameter.
	 * 
	 * @see #EXPOSE_WEB_APP_ROOT_PARAM
	 */
	private static boolean exposeWebAppRoot(ServletContext servletContext) {
		String exposeWebAppRootParam = servletContext.getInitParameter(EXPOSE_WEB_APP_ROOT_PARAM);
		return (exposeWebAppRootParam == null || Boolean.valueOf(exposeWebAppRootParam).booleanValue());
	}

	public static void main(String[] args) throws Exception{
//		System.out.println(Node.MAPPING.getNodeName());
			//内存初始化
			MappingContext.getInstance().init();
	}
}
