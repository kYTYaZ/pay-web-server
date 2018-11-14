package com.wldk.framework.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 风控商户文件写入公共类
 * 
 * @author zhaodk
 *
 */
public class FileOutput {
	
	private static Logger logger = LoggerFactory.getLogger(FileOutput.class);
	
	/** 单例 */
	private static FileOutput instance = new FileOutput();
	/** 日志 */
	protected static Logger log = LoggerFactory.getLogger(FileOutput.class);
	/** 商户明细 */
	public FileOutputStream out_T05_DUBIOUS_TXN;

	/** 文件夹(目录+日期) */
	public static String mkdirFile = "";

	public FileOutput() {
		// init();
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 *
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static String getPath(String filename) {
		AppVars av = AppVars.getAppVars();
		File res = new File("" + av.getVar(AppVars.WEB_APP_REAL_PATH) + av.getVar("log-store") + File.separator
				+ DateUtils.getCurDateyyyyMMdd());
		mkdirFile = res.getPath();
		return mkdirFile + "/" + filename;
	}

	/**
	 * 
	 * 功能描述:数据初始化 <br>
	 * 〈功能详细描述〉
	 *
	 * @param fileNamePath 文件存放地址及文件名
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public void init(String fileNamePath) throws Exception {
		try {
			this.out_T05_DUBIOUS_TXN = new FileOutputStream(fileUrl(fileNamePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建文件路径路径
	 * 
	 */
	public static String fileUrl(String name) throws Exception {
//      System.out.println("开始检测文件目录是否存在，文件是否存在..................");
		/**
		 * 判读文件夹是否存在
		 */
//        System.out.println("mkdirFile=" + mkdirFile);
		File dirFile = new File(mkdirFile);
		if (!dirFile.exists()) {
			boolean isMkDirs = dirFile.mkdirs();
			if (!isMkDirs) {
				throw new Exception(dirFile.getName() + "创建目录失败！");
			}
		}
		// / 查找文件，假如不存在，就创建
		File file = new File(name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

		}
		log.info("创建文件：" + name);
		return name;

	}

	/**
	 * 获取单例对象的方法
	 * 
	 * @return
	 */
	public static FileOutput instance() {
		return instance;
	}

	/**
	 * 写入商户明细
	 * 
	 * @param file
	 */
	public void exeportout_T05_DUBIOUS_TXN(String file) {
		try {
//          out_T05_DUBIOUS_TXN_STAT.write(0x0D);
//          out_T05_DUBIOUS_TXN_STAT.write(0x0A); 
			out_T05_DUBIOUS_TXN.write(file.toString().getBytes());
			out_T05_DUBIOUS_TXN.write("\r\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * 功能描述:写入文件 <br>
	 * 〈功能详细描述〉
	 *
	 * @param filename   文件名
	 * @param fileValues 文件内容
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static String insertfile(String filename, String fileValues) throws Exception {
		FileOutput f = new FileOutput();
		String path = getPath(filename);
		f.init(path);
		f.exeportout_T05_DUBIOUS_TXN(fileValues);
		return path;

	}

	/**
	 * 关闭流
	 */
	public void fileClose() {
		try {
//            System.out.println("关闭流");
			out_T05_DUBIOUS_TXN.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
	}

}
