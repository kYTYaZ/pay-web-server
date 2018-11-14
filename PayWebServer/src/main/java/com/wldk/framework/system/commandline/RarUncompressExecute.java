/**
 * 
 */
package com.wldk.framework.system.commandline;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RAR解压缩命令行执行实现
 * 
 * @author Administrator
 * 
 */
public class RarUncompressExecute implements Execute {
	/** 日志 */
	protected Logger log = LoggerFactory.getLogger(getClass());
	/** 解压缩密码 */
	private String pwd;
	/** 压缩文件 */
	private File rarFile;
	/** 解压缩文件存放的目录 */
	private File ucDir;
	private static final String CMD_STR = "rar x -o+ ${pwd} ${rarFile} ${ucDir}";

	/**
	 * 构造方法
	 * 
	 * @param rarFile
	 */
	public RarUncompressExecute(File rarFile) {
		this.rarFile = rarFile;
	}

	/**
	 * 构造方法
	 * 
	 * @param rarFile
	 * @param ucDir
	 */
	public RarUncompressExecute(File rarFile, File ucDir) {
		this(rarFile);
		this.ucDir = ucDir;
	}

	/**
	 * 构造方法
	 * 
	 * @param rarFile
	 * @param ucDir
	 * @param pwd
	 */
	public RarUncompressExecute(File rarFile, File ucDir, String pwd) {
		this(rarFile, ucDir);
		this.pwd = pwd;
	}

	/*
	 * RAR解压缩命令行执行实现
	 * 
	 * @see com.mingdeng.core.system.commandline.Execute#execute()
	 */
	public int execute() {
		// TODO Auto-generated method stub
		// 拼装命令行参数
		HashMap<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(pwd)) {
			params.put("pwd", "-p" + pwd);
		} else {
			params.put("pwd", "");
		}
		params.put("rarFile", rarFile.getAbsolutePath());
		if (ucDir == null || ucDir.isFile()) {
			ucDir = new File("."); // 当前目录
		}
		params.put("ucDir", ucDir.getAbsolutePath());
		// 解析命令行
		CommandLine commandLine = CommandLine.parse(CMD_STR, params);
		log.debug("[" + commandLine + "]");
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(5 * 60 * 10000); // 超时5分钟
		executor.setWatchdog(watchdog);
		try {
			return executor.execute(commandLine);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return 1;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the rarFile
	 */
	public File getRarFile() {
		return rarFile;
	}

	/**
	 * @param rarFile
	 *            the rarFile to set
	 */
	public void setRarFile(File rarFile) {
		this.rarFile = rarFile;
	}

	/**
	 * @return the ucDir
	 */
	public File getUcDir() {
		return ucDir;
	}

	/**
	 * @param ucDir
	 *            the ucDir to set
	 */
	public void setUcDir(File ucDir) {
		this.ucDir = ucDir;
	}

	/**
	 * testing
	 * 
	 * @param args
	 * @throws Exception
	 */
//	public static void main(String[] args) throws Exception {
//		String pwd = null;
//		File rarFile = new File("D:\\a.rar");
//		File ucDir = new File("F:\\test");
//		RarUncompressExecute rarExec = new RarUncompressExecute(rarFile, ucDir,
//				pwd);
//		System.out.print("开始执行解压缩[" + rarFile + "]...");
//		int res = rarExec.execute();
////		System.out.println("OK[" + res + "].");
//	}
}
