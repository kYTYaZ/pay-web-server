package com.wldk.framework.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件下载
 * 
 * @author Administrator
 * 
 */
public class DownLoadUtil {

	private static Logger logger = LoggerFactory.getLogger(DownLoadUtil.class);
	
	/**
	 * 取得附件名称
	 * 
	 * @param file_name
	 * @return
	 */
	public static String getAttachName(String file_name) {
		if (file_name == null)
			return "";
		file_name = file_name.trim();
		int iPos = 0;
		iPos = file_name.lastIndexOf("\\");
		if (iPos > -1) {
			file_name = file_name.substring(iPos + 1);
		}
		iPos = file_name.lastIndexOf("/");
		if (iPos > -1) {
			file_name = file_name.substring(iPos + 1);
		}
		iPos = file_name.lastIndexOf(File.separator);
		if (iPos > -1) {
			file_name = file_name.substring(iPos + 1);
		}
		return file_name;
	}

	/**
	 * UTF-8转码
	 * 
	 * @param s
	 * @return
	 */
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
//					System.out.println(ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		String s_utf8 = sb.toString();
		sb.delete(0, sb.length());
		sb.setLength(0);
		sb = null;
		return s_utf8;
	}

	/**
	 * 取得下载文件的真实全路径名称
	 * 
	 * @param request
	 * @param file_name
	 * @return
	 */
	public static String getRealName(HttpServletRequest request, String file_name) {
		if (request == null || file_name == null)
			return null;
		file_name = file_name.trim();
		if (file_name.equals(""))
			return null;
//		String file_path = request.getRealPath(file_name);
//		if (file_path == null)
//			return null;
		File file = new File(file_name);
		if (!file.exists())
			return null;
		return file.getPath();
	}

	/**
	 * 文件下载
	 * 
	 * @param file_name
	 * @param request
	 * @param response
	 * @throws ServletException
	 */
	public static void doDownLoad(String file_name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		InputStream inStream = null;
		String attch_name = "";

		byte[] b = new byte[1024];
		int len = 0;

		// 取得附件的名称
		attch_name = DownLoadUtil.getAttachName(file_name);
		file_name = DownLoadUtil.getRealName(request, file_name);
		if (file_name == null) {
			throw new Exception("文件不存在,或者禁止下载");
		}
		attch_name = DownLoadUtil.toUtf8String(attch_name);
		// 读到流中
		inStream = new FileInputStream(new File(file_name));
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/octet-stream;charset=GBK");
		// 设置输出的格式
		response.reset();
		response.addHeader("Content-Disposition", "attachment; filename=\"" + attch_name + "\"");
		OutputStream out =response.getOutputStream();
		// 循环取出流中的数据
		while ((len = inStream.read(b)) != -1) {
			out.write(b, 0, len);//直接弹出下载框
		}
		out.close();
		inStream.close();
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

	/**
	 * 文件下载
	 * 
	 * @param response
	 * @param request
	 * @param file
	 */
	public static void download(File file,HttpServletResponse response) throws Exception {
		OutputStream out = null;
			if (!file.exists()) {
				throw new Exception("原文件：" + file.getPath() + "不存在...");
			} else {
				response.setContentType("application/octet-stream");
				// 在content-disposition指明attachment、filename等属性
				response.setHeader("content-disposition", "attachment;filename=\"" + file.getName() + "\";size=" + file.length());
				out = response.getOutputStream();
				writeFile(file, out);
			}
	}

	/**
	 * 文件写入指定目录
	 * 
	 * @param file
	 * @param out
	 * @throws Exception
	 */
	public static void writeFile(File file, OutputStream out) throws Exception {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int len = 0;
			byte[] buf = new byte[512];
			while ((len = bis.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			if (bis != null)
				try {
					bis.close();
				} catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
		}

	}
}
