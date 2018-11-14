/**
 * 
 */
package com.wldk.framework.system.commandline;

import java.io.File;

import com.wldk.framework.system.file.FileHeader;

/**
 * 工厂方法
 * 
 * @author Administrator
 * 
 */
public class ExecuteFactory {
	/**
	 * 工厂方法
	 * 
	 * @return
	 */
	public static Execute getExecute(FileHeader header, File f) {
		switch (header) {
		case RAR:
			return new RarUncompressExecute(f);
		case GZ:
		case TARZ:
			return new TarUncompressExecute(f);
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
