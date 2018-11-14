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

/**
 * @author Administrator
 * 
 */
public class TarUncompressExecute extends RarUncompressExecute {
	/** 命令行 */
	private static final String CMD_STR = "tar Czxvf ${rarFile} ${ucDir}";

	public TarUncompressExecute(File rarFile) {
		super(rarFile);
		// TODO Auto-generated constructor stub
	}

	/*
	 * 执行命令行
	 * 
	 * @see com.mingdeng.core.system.commandline.RarUncompressExecute#execute()
	 */
	
	public int execute() {
		// TODO Auto-generated method stub
		// 拼装命令行参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("rarFile", getRarFile().getAbsolutePath());
		if (getUcDir() == null || getUcDir().isFile()) {
			setUcDir(new File(".")); // 当前目录
		}
		params.put("ucDir", getUcDir().getAbsolutePath());
		// 解析命令行
		CommandLine commandLine = CommandLine.parse(CMD_STR, params);
		System.out.print("[" + commandLine + "]");
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(5 * 60 * 10000); // 超时5分钟
		executor.setWatchdog(watchdog);
		try {
			return executor.execute(commandLine);
		} catch (IOException e) {
			// System.err.println(e);
			log.error(e.getMessage(),e);
		}
		return 1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
