/**
 * 
 */
package com.wldk.framework.system.commandline;

import java.io.File;

/**
 * 命令行执行接口
 * 
 * @author Administrator
 * 
 */
public interface Execute {
	/**
	 * 命令行执行接口
	 * 
	 * @return 0 表示执行成功，1表示执行失败
	 */
	int execute();

	/**
	 * 获取压缩密码
	 * 
	 * @return
	 */
	String getPwd();

	/**
	 * 设置压缩密码
	 * 
	 * @param pwd
	 */
	void setPwd(String pwd);

	/**
	 * 获取解压缩目录
	 * 
	 * @return
	 */
	File getUcDir();

	/**
	 * 设置解压缩目录
	 * 
	 * @param file
	 */
	void setUcDir(File dir);
}
