package com.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {


	/**
	 * 
	 * 发送http请求
	 * 
	 * @param requestUrl
	 *            请求的url
	 * @param paramMap
	 *            请求中所需要传的参数
	 * @return
	 * @throws FrameException
	 */
	@SuppressWarnings("unused")
	public static String httpRequest(String requestUrl, Map<String, String> paramMap)
			throws Exception {

		StringBuffer buffer = new StringBuffer();

		try {

			URL url = new URL(requestUrl);

			Proxy proxy = null;

			HttpURLConnection httpURLConnection = null;
			if (proxy == null) {
				httpURLConnection = (HttpURLConnection) url.openConnection();
			} else {
				httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
			}

			// Post 请求不能使用缓存
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(30000);
			httpURLConnection.setReadTimeout(300000);
			httpURLConnection.connect();
			// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成
			httpURLConnection.connect();
			// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
			// 所以在开发中不调用上述的connect()也可以)。
			OutputStream outStrm = httpURLConnection.getOutputStream();
			StringBuffer params = new StringBuffer();
			if (paramMap != null) {
				int i = 0;
				for (Entry<String, String> entry : paramMap.entrySet()) {
					if (i > 0) {
						params.append("&");
					}
					;
					params.append(entry.getKey() + "=" + entry.getValue());
					i++;
				}
			}

			outStrm.write(params.toString().getBytes("utf-8"));
			outStrm.flush();
			outStrm.close();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpURLConnection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpURLConnection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
	
	@SuppressWarnings("unused")
	public static String httpRequest(String requestUrl, String msg) throws Exception {
		
		StringBuffer buffer = new StringBuffer();
		
		try {
			
			URL url = new URL(requestUrl);
			
			Proxy proxy = null;
			
			HttpURLConnection httpURLConnection = null;
			if (proxy == null) {
				httpURLConnection = (HttpURLConnection) url.openConnection();
			} else {
				httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
			}
			
			// Post 请求不能使用缓存
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(30000);
			httpURLConnection.setReadTimeout(300000);
			httpURLConnection.connect();
			// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成
			httpURLConnection.connect();
			// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
			// 所以在开发中不调用上述的connect()也可以)。
			OutputStream outStrm = httpURLConnection.getOutputStream();
			
			outStrm.write(msg.toString().getBytes("utf-8"));
			outStrm.flush();
			outStrm.close();
			
			// 将返回的输入流转换成字符串
			InputStream inputStream = httpURLConnection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpURLConnection.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
	}
}
