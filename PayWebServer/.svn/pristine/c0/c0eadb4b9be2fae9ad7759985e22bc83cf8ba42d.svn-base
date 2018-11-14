package com.huateng.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.huateng.pay.common.util.Constants;

public class TcpClient {

	private static Logger logger = LoggerFactory.getLogger(TcpClient.class);
	
	private String ip;
	private int port;
	private String charset;
	private Socket  socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	
	public TcpClient(String ip,int port,String charset){
		this.ip = ip;
		this.port = port;
		this.charset = charset;
	}
	
	public void connect(int connectTimeOut,int soTimeout) throws IOException{
		socket = new Socket();
		SocketAddress  socketAddress = new InetSocketAddress(ip, port);
		socket.connect(socketAddress,connectTimeOut);
		socket.setSoTimeout(soTimeout);
	}
	
	public void connect() throws IOException{
		socket = new Socket();
		int connectTimeOut = Integer.valueOf(Constants.getParam("connectTimeout"));
		int soTimeout = Integer.valueOf(Constants.getParam("soTimeout"));
		SocketAddress  socketAddress = new InetSocketAddress(ip, port);
		socket.connect(socketAddress,connectTimeOut);
		socket.setSoTimeout(soTimeout);
	}
	
	public void setTimeOut(int timeout) throws SocketException{
		socket.setSoTimeout(timeout);
	}
	
	public String send(String message,boolean flag) throws Exception{
		try {
			this.write(message);
			if(flag) return this.revice();
		} finally{
			this.destory();
		}	
		return null;
	}
	
	public String sendToCore(byte[] message,boolean flag) throws Exception{
		try {
			this.writeToCore(message);
			if(flag) return this.reviceFromCore();
		} finally{
			this.destory();
		}	
		return null;
	}
	
	private void writeToCore(byte[] message) throws IOException{
		Preconditions.checkNotNull(socket, "tcp socket 不能为空");
		outputStream = socket.getOutputStream();		
		outputStream.write(message);
		outputStream.flush();
		
	}

	private String reviceFromCore() throws Exception {
		//try {
			Preconditions.checkNotNull(socket, "tcp socket 不能为空");
			inputStream = socket.getInputStream();
			byte[] bt8 = new byte[8];
			inputStream.read(bt8);
			byte[] bt8ToASCII = EBCDICGBK.ebcdicToGBK(bt8);
			String length = new String(bt8ToASCII,charset);
			System.out.println("长度" + Integer.valueOf(length.trim()));
			byte[] bt = new byte[Integer.valueOf(length.trim())];
			inputStream.read(bt);
			byte[] btToASCII = EBCDICGBK.ebcdicToGBK(bt);
			EBCDICGBK.trace(btToASCII);
			String content = new String(btToASCII, "gbk");
			
			return content;
	}

	private void write(String message) throws IOException{
		Preconditions.checkNotNull(socket, "tcp socket 不能为空");
		outputStream = socket.getOutputStream();		
		outputStream.write(message.getBytes(charset));
		outputStream.flush();
	}
	
	private String  revice() throws Exception{
		
		Preconditions.checkNotNull(socket, "tcp socket 不能为空");
		
		inputStream = socket.getInputStream();
		
		//读取报文长度
		byte [] bt6 = new  byte[6];
		inputStream.read(bt6);
		
		//报文头长度
		String length = new String(bt6,charset);
		
		//读取报文内容
		byte[] bt = new byte[Integer.valueOf(length)];
		inputStream.read(bt);
		
		String content = new String(bt,charset);
		
		return content;
	}
	
	
	private  void  destory() {
		try {
			if(socket != null){
				socket.close();
			}
			if(inputStream != null){
				inputStream.close();
			}
			if(outputStream != null){
				outputStream.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
