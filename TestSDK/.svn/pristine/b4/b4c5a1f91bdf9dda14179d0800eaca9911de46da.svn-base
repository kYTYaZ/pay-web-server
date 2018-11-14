package com.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;

public class SocketUtil {

	public static String socketConnect(String reqData) {
		String respData = null;

		try {
			String ip = PropertyUtil.getProperty(Dict.socket_ip);
			int host = Integer.parseInt(PropertyUtil.getProperty(Dict.socket_host));
			
			Socket socket = new Socket(ip, host);
			OutputStreamWriter writer = new OutputStreamWriter(
					new BufferedOutputStream(socket.getOutputStream()),
					SDKConstant.UTF8);

			System.out.println("socket请求报文:" + reqData);

			writer.write(reqData);
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), SDKConstant.UTF8));
			respData = reader.readLine();
			System.out.println("socket返回报文:" + respData);

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respData;

	}

}
