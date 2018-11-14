package com.wldk.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Administrator
 * 
 */
public class CheckHelper {
	private String userManageUrl = null;

	public String getUserManageUrl() {
		return this.userManageUrl;
	}

	public void setUserManageUrl(String url) {
		this.userManageUrl = (url + (url.endsWith("/") ? "" : "/"));
	}

//	public String[] check(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String key = request.getParameter("key");
//		if ((key == null) || (key.length() == 0))
//			return null;
//		URL url = new URL(this.userManageUrl + "check.go?key=" + key);
//		byte[] bs = new byte[1024];
//		InputStream is = url.openConnection().getInputStream();
//		try {
//			int n = is.read(bs);
//			String code = new String(bs, 0, n);
//			if (code.startsWith("00")) {
//				String[] arrayOfString = { code.substring(2), key };
//				return arrayOfString;
//			}
//			response.sendRedirect(this.userManageUrl + "index.jsp?code=" + code);
//			return null;
//		} finally {
//			is.close();
//		}
//	}

	public boolean isFromServer(HttpServletRequest request) throws Exception {
		String key = request.getParameter("key");
		if (key == null)
			return false;
		URL url = new URL(this.userManageUrl + "formserver.go?key=" + key);
		byte[] bs = new byte[1024];
		int n = url.openConnection().getInputStream().read(bs);
		String code = new String(bs, 0, n);
		return code.startsWith("true");
	}

	public List<String> parseData(HttpServletRequest request) throws Exception {
		String data = request.getParameter("data");
		if ((data == null) || (data.length() == 0))
			return null;
		URL url = new URL(this.userManageUrl + "parse.go?data=" + data);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "utf-8"),
				2048);
		try {
			String l = br.readLine();
			if ((l == null) || (!"true".equals(l)))
				return null;
			List res = new ArrayList();
			while ((l = br.readLine()) != null)
				res.add(l);
			List localList1 = res;
			return localList1;
		} finally {
			br.close();
		}
	}

	public String getTimeOutUrl() {
		return this.userManageUrl + "index.jsp?code=03";
	}

	public String getUpdatePwdUrl() throws IOException {
		return this.userManageUrl + "updatepwd.go";
	}

	public String getSitesUrl(String key) throws IOException {
		return this.userManageUrl + "sites.go?key=" + key;
	}

	public String getSignOutUrl(String key) throws IOException {
		return this.userManageUrl + "signout.go?key=" + key;
	}
}