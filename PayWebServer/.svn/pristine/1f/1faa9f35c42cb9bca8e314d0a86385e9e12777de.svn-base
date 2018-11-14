package com.wldk.framework.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.utils.RandomValidateCode;

/**
 * 
 * 系统登陆验证码<br>
 * 〈功能详细描述〉 〈方法简述 - 方法描述〉
 * 
 * @author Administrator
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RandomValidateCodeImageServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(RandomValidateCodeImageServlet.class);
	
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
		response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expire", 0);
		RandomValidateCode randomValidateCode = new RandomValidateCode();
		try {
			request.getSession().setAttribute("certCode", randomValidateCode.getRandcode(request, response)); // 输出图片方法
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}