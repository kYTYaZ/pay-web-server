/**
 * 
 */
package com.wldk.framework.system.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件头常量
 * 
 * @author Administrator
 * 
 */
public enum FileHeader {
	RAR(new Integer[] { 0x52, 0x61, 0x72, 0x21 }), GZ(new Integer[] { 0x1F,
			0x8B, 0x08, 0x00 }), TARZ(new Integer[] { 0x1F, 0x9D, 0x90, 0x44,
			0x86, 0xC0 });
	/** 文件头的值 */
	private Integer[] header;
	private Logger log = LoggerFactory.getLogger(getClass());

	FileHeader(Integer[] header) {
		this.header = header;
	}

	/**
	 * @return the header
	 */
	public Integer[] getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(Integer[] header) {
		this.header = header;
	}

	/**
	 * 判断指定的文件是否是符合当前文件头
	 * 
	 * @param file
	 * @return
	 */
	public boolean isSame(File file) {
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			byte[] b = new byte[this.header.length];
			int read = input.read(b); // 读取指定字节数
			if(read == -1){
				log.info("流中没有数据!");
			}
			for (int i = 0, n = b.length; i < n; i++) {
				if (Byte.valueOf(b[i]).intValue() != header[i]) {
					return false;
				}
			}
			return true;
		} catch (IOException ioe) {
			log.error(ioe.getMessage(),ioe);
			return false;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

//	public static void main(String[] args) throws Exception {
//		File file = new File("D:\\内部通讯录0906.rar");
//		System.out.println(Arrays.toString(FileHeader.RAR.getHeader()));
//		boolean same = FileHeader.RAR.isSame(file);
//		System.out.println(same);
//		if (file.exists()) {
//			System.out.println("deleted->" + FileUtils.deleteQuietly(file));
//		}
//
//	}
}
