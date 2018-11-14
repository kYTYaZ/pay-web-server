package com.wldk.framework.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
/**
 * 数据工具类
 * @author Administrator
 *
 */
public class DataUtil {
    public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
	 public static final String CONTENT_TYPE_ZIP = "application/zip";
	
	/**
	 * 左边补0
	 * 
	 * @param str
	 * @param i
	 * @return
	 */
	public static String fillString(String str, int i) {
		if (str.length() > i)
			return str.substring(0, i);
		int len = str.length();
		// 不足i位左边补0
		for (int j = 0; j < i - len; j++) {
			str = "0" + str;
		}
		return str;
	}

	public static String rightString(String str, int i) {
		if (str.length() > i)
			return str.substring(0, i);
		int len = str.length();
		
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		// 不足i位左边补0
		for (int j = 0; j < i - len; j++) {
			sb.append("0");
//			str = str+"0";
		}
		return sb.toString();
	}
	/**
	 * 字符串截取转换
	 * 
	 * @param p_Param
	 * @param p_Delim
	 * @return
	 */
	public static String[] string2Array(String p_Param, String p_Delim) {
		if (p_Param == null || StringUtils.isEmpty(p_Param)) {
			return null;
		} else {
			if (p_Delim == null || StringUtils.isEmpty(p_Delim)) {
				return null;
			} else {
				return p_Param.split("\\" + p_Delim);
			}
		}
	}
	public static String SQLArray(String p_Param, String p_Delim) {
		if (p_Param == null || StringUtils.isEmpty(p_Param)) {
			return null;
		} else {
			if (p_Delim == null || StringUtils.isEmpty(p_Delim)) {
				return null;
			} else {
				String[] Array=p_Param.split("\\" + p_Delim);
				if(Array!=null && Array.length>0){
					StringBuffer SQL=new StringBuffer();
					SQL.append(" in (");
					for(int i=0;i<Array.length;i++){
						SQL.append("'"+Array[i]+"'");
						if(i!=(Array.length-1)){
							SQL.append(",");
						}
					}
					SQL.append(")");
					return SQL.toString();
				}else{
					return null;
				}
			}
		}
	}
	
    /**
     * 导出ZIP文件
     * 
     * @param response
     * @param filePath
     * @throws Exception
     */
    public static void exportZip(HttpServletResponse response, String filePath) throws Exception {
        int index = filePath.lastIndexOf("/");
        String fileName = filePath.substring(index + 1);

        File zipFile = new File(filePath + ".zip");
        if (zipFile.exists()) {
            response.setContentType(CONTENT_TYPE_ZIP);
            String headerDisp = "attachment;filename=" + fileName + ".zip";
            headerDisp = new String(headerDisp.getBytes("GBK"), "iso8859-1");
            response.setHeader("content-disposition", headerDisp);
            ServletOutputStream out = null;
            byte[] buf = new byte[8192];
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(zipFile.getPath()));
                int len = 0;
                out = response.getOutputStream();
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (Exception e) {
                throw new Exception("导出ZIP文件异常,请于系统管理员联系！");
            } finally {
            	if(in != null){
            		in.close();
            	}
            	if(out != null){
            		out.close();
            	}
//                in.close();
//                out.close();
            }
        } else {
            throw new Exception("文件不存在");
        }
    }	
	  /**
     * 输出图片
     * 
     * @param response
     * @param imgFile 图片文件
     * @throws IOException
     */
    public static void exportImage(HttpServletResponse response, ImageUtil bean) throws IOException {
        if (bean.getImgType() != null && StringUtils.isNotEmpty(bean.getImgType())) {
            response.setContentType("image/" + bean.getImgType());
        } else {
            response.setContentType("image/" + ImageUtil.IMG_TYPE_JPG);
        }
        ServletOutputStream os = response.getOutputStream();
        InputStream is = new FileInputStream(bean.getFile());
        byte[] bs = new byte[10240];
        int n = 0;
        while ((n = is.read(bs)) > 0)
            os.write(bs, 0, n);
        os.close();
        is.close();
    }
	
	public static void main(String[] args) {
//		// System.out.println(f[0] + "=" + f[1]);
//		String s="22正确";
//		String s1="22正确";
//		if(s==s1){
//			System.out.println("正确11");
//		}
//		if("22".equals("22")){
//			System.out.println("正确22");
//		}
		
//		System.out.println(fillString("FFFF", 10));
		
	}

}
