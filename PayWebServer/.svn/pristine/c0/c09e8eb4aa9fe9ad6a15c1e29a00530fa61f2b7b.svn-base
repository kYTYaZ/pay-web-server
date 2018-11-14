/**
 * 
 */
package com.wldk.framework.mapping.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * 解析XML配置文件的实现类
 * 
 * @author Administrator
 * 
 */
public class SAXConfigurator {
	/** 单例对象 */
	private static SAXConfigurator instance = new SAXConfigurator();

	/**
	 * 构造方法
	 * 
	 */
	protected SAXConfigurator() {
	}

	/** 返回单例 */
	public static SAXConfigurator getInstance() {
		return instance;
	}

	/***************************************************************************
	 * 内部接口定义，用于实现根据各种不同的输入参数来获取JDOM的Document对象<br>
	 * 
	 * @author Administrator
	 * 
	 */
	private interface BuilderAction {
		Document bulid(SAXBuilder builder) throws JDOMException, IOException;
	}

	/**
	 * 提供XML配置文件的url形式来解析映射配置文件，并将映射结果保存进代码映射资源库<br>
	 */
	public Document doConfigure(final URL url) {
		// TODO Auto-generated method stub
		// 实现解析的方法
		BuilderAction action = new BuilderAction() {
			public Document bulid(final SAXBuilder builder)
					throws JDOMException, IOException {
				return builder.build(url);
			}

			public String toString() {
				return "file [" + url.toString() + "]";
			}
		};
		return doConfigure(action);
	}

	/**
	 * 重载方法，提供XML配置文件的文件名形式来解析映射配置文件，并将映射结果保存进代码映射资源库<br>
	 * 
	 * @param filename
	 *            XML的文件名
	 * @param repository
	 *            代码映射库对象
	 */
	public Document doConfigure(final String filename) {
		BuilderAction action = new BuilderAction() {
			public Document bulid(final SAXBuilder builder)
					throws JDOMException, IOException {
				return builder.build(new File(filename));
			}

			public String toString() {
				return "file [" + filename + "]";
			}
		};
		return doConfigure(action);
	}

	/**
	 * 重载方法，提供XML配置文件的字节流形式来解析映射配置文件，并将映射结果保存进代码映射资源库<br>
	 * 
	 * @param inputStream
	 *            XML文件的输入流
	 * @param repository
	 *            代码映射库对象
	 * @throws FactoryConfigurationError
	 */
	public Document doConfigure(final InputStream inputStream)
			throws FactoryConfigurationError {
		BuilderAction action = new BuilderAction() {
			public Document bulid(final SAXBuilder builder)
					throws JDOMException, IOException {
				// TODO Auto-generated method stub
				InputSource in = new InputSource(inputStream);
				return builder.build(in);
			}

			public String toString() {
				return "input stream [" + inputStream.toString() + "]";
			}
		};
		return doConfigure(action);
	}

	/**
	 * 重载方法，提供XML配置文件的字符流形式来解析映射配置文件，并将映射结果保存进代码映射资源库<br>
	 * 
	 * @param reader
	 *            XML文件的Reader对象
	 * @param repository
	 *            代码映射库对象
	 * @throws FactoryConfigurationError
	 */
	public Document doConfigure(final Reader reader)
			throws FactoryConfigurationError {
		BuilderAction action = new BuilderAction() {
			public Document bulid(final SAXBuilder builder)
					throws JDOMException, IOException {
				InputSource inputSource = new InputSource(reader);
				return builder.build(inputSource);
			}

			public String toString() {
				return "reader [" + reader.toString() + "]";
			}
		};
		return doConfigure(action);
	}

	/**
	 * 调用JDOM库，解析XML文件并将返回的Document对象传给代码映射资源库进行解析<br>
	 * 
	 * @param action
	 *            创建Document对象的命令对象
	 * @param repository
	 *            代码映射资源库对象
	 * @throws FactoryConfigurationError
	 */
	private final Document doConfigure(final BuilderAction action)
			throws FactoryConfigurationError {
		try {
			SAXBuilder builder = new SAXBuilder();
			builder.setValidation(false);
			return action.bulid(builder);
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}

	public Document configure(String filename) throws FactoryConfigurationError {
		return doConfigure(filename);
	}

	public Document configure(URL url) throws FactoryConfigurationError {
		return doConfigure(url);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
