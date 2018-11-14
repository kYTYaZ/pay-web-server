package com.wldk.framework.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferedWriterUtils {
	/** 日志 */
	protected static Logger log = LoggerFactory.getLogger(BufferedWriter.class);
	/** 商户明细 */
	public BufferedWriter bufferedWriter;

	/** 文件夹 */
	public String mkdirFile = "";
	/** 文件名 */
	public String fileName = "";

	public String filePath = "";

	private File file;

	public BufferedWriterUtils(String path, String fileName) throws Exception {
		this.mkdirFile = path;
		this.fileName = fileName;
		this.filePath = this.mkdirFile + "/" + this.fileName;
		this.file = new File(this.filePath);
		fileUrl();
		bufferedWriter = new BufferedWriter(new FileWriter(this.file));
	}

	/**
	 * 写入数据
	 * 
	 * @param fileValue
	 * @throws IOException
	 */
	public void writeLines(String fileValue) throws IOException {
		log.info("write[" + fileValue + "]");
		// 获取一行数据
		this.bufferedWriter.write(fileValue);
		this.bufferedWriter.newLine();
	}

	/**
	 * 刷新缓存
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		this.bufferedWriter.flush();
	}

	/**
	 * 关闭写入
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		IOUtils.closeQuietly(this.bufferedWriter);
	}
	
	
	/**
	 * 写入数据
	 * @param list
	 * @throws IOException
	 */
	public void writeLines(List<Object[]> list) throws IOException {
		// 获取一行数据
		try {
			log.info("开始写入["+file.getPath()+"]数据文件...");
			// 创建一个迭代器
		   for(Object[] objs:list){
			   StringBuffer str=new StringBuffer();
			   for(int i=0;i<objs.length;i++){
				   Object obj=objs[i];
				   str.append(obj);
				   if(i!=(objs.length-1)){
					   str.append(","); 
				   }
			   }
				log.info("write[" + str.toString() + "]");
			   this.bufferedWriter.write(str.toString());
			   this.bufferedWriter.write(0x0D);
			   this.bufferedWriter.write(0x0A);
			   this.bufferedWriter.newLine();
		   }
			this.bufferedWriter.flush();
			log.info("写入数据成功...");
		} finally {
			// 关闭
			IOUtils.closeQuietly(this.bufferedWriter);
		}
		
		
	}

	/**
	 * 创建文件路径路径
	 * 
	 */
	public String fileUrl() throws Exception {
		/**
		 * 判读文件夹是否存在
		 */
		log.info("mkdirFile=" + mkdirFile);
		File dirFile = new File(mkdirFile);
		if (!dirFile.exists()) {
			boolean isMkDirs = dirFile.mkdirs();
			if (!isMkDirs) {
				throw new Exception(dirFile.getName() + "创建目录失败！");
			}
		}
		// / 查找文件，假如不存在，就创建
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}

		}
		log.info("创建文件：" + fileName);
		return fileName;

	}

	public static String fileUrl(String mkdirFile, String fileName)
			throws Exception {
		/**
		 * 判读文件夹是否存在
		 */
		log.info("mkdirFile=" + mkdirFile);
		File dirFile = new File(mkdirFile);
		if (!dirFile.exists()) {
			boolean isMkDirs = dirFile.mkdirs();
			if (!isMkDirs) {
				throw new Exception(dirFile.getName() + "创建目录失败！");
			}
		}
		// / 查找文件，假如不存在，就创建
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}

		}
		log.info("创建文件：" + fileName);
		return fileName;

	}

}
