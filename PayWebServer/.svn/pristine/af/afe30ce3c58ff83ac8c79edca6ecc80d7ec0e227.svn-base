package com.wldk.framework.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wldk.framework.utils.DataUtil;
import com.wldk.framework.utils.ImageUtil;

/**
 * 页面nmg图片翻译加载
 * 
 * @author zhaodk
 * @since 2011-5-17 16:35:12
 * 
 */
public class DisplayNmgServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -933675486189369930L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataUtil.exportImage(response, ImageUtil.getNmgFile(request));
	}
}
