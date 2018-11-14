package com.wldk.framework.db.parse;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.db.ParameterProvider;

/**
 * 使用Velocity模板引擎解析SQL查询字符串的实现类<Br>
 * 
 * @author Administrator
 * 
 */
public abstract class AbstractVelocitySQLParse implements SQLParse {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractVelocitySQLParse.class);
	
	/** 日志 */
	protected Logger log = LoggerFactory.getLogger(getClass());
	/** SQL模板文件的存放路径常量 */
//	private final static String PATH = "WEB-INF/sql";
//	private final static String PATH = "sql";
	/** Velocity上下文环境 */
	private VelocityContext context;
	/** 参数提供器 */
	private ParameterProvider parameters;
	/** 模板文件文 */
	private String filename;

	/** 无参构造方法 */
	public AbstractVelocitySQLParse(String filename) throws Exception {
		this.filename = filename;
		init();
	}

	/** 有参数构造方法 */
	public AbstractVelocitySQLParse(ParameterProvider parameters, String filename) throws Exception {
		this(filename);
		this.parameters = parameters;
	}

	/** 设置模板中的参数，由实现类根据需求实现 */
	public abstract void setParameters() throws Exception;

	/** SQL模板解析 */
	public String parse() throws Exception {
		// TODO Auto-generated method stub
		if (this.context != null) {
			setParameters();
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(filename, "UTF-8", this.context, w);
			String sql = w.toString();
			return sql;
		} else {
			log.error("解析SQL模板失败 [ 上下文：VelocityContext为空 " + "]");
		}
		return null;
	}

	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

	public ParameterProvider getParameters() {
		return parameters;
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * 初始化Velocity环境<br>
	 * 
	 * @throws Exception
	 */
//	private void init() throws Exception {
//		String loader = System.getProperty("webApp.path") + PATH;
//		System.out.println("Velocity Loader:" + loader);
//		 log.debug("Velocity Loader:" + loader);
//		Properties p = new Properties();
//		p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, loader);
//		p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
//		p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
//		Velocity.init(p);
//		this.context = new VelocityContext();
//	}
	private void init() throws Exception {
		try {
//		String loader = System.getProperty(GlobalParameterListener.WEB_INFO)
//				+ System.getProperty("file.separator") + PATH;
////		System.out.println("loader：[ "+loader+" ] ");
//		Properties p = new Properties();
//		p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, loader);
//		p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
//		p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
//		//从指定目录提取vm文件
//		Velocity.init(p);

			// 现在仅仅实例化对象VelocityContext
			this.context = new VelocityContext();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/** 测试方法 */
	public static void main(String[] args) throws Exception {
		// VelocitySQLParse sqlParse = new VelocitySQLParse() {
		// public void setParameters() {
		// getContext().put("line_no", "pg000000240280");
		// }
		// };
		// System.out.println(sqlParse.parse("/source/web/WEB-INF/sql/A01.vm"));
	}
}
