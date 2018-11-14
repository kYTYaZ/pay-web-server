package com.huateng.utils;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.Constants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SftpUtil.class);
	
	private ChannelSftp channelSftp;
	private String userName;
	private String password;
	private String host;
	private int port;
	
	
	public SftpUtil(String userName,String password,String host,int port){
		this.userName = userName;
		this.password = password;
		this.host = host;
		this.port = port;
	}
	
	public SftpUtil(String userName,String password,String host){
		this.userName = userName;
		this.password = password;
		this.host = host;
	}
	
	private  ChannelSftp _connect(){
		
		Session session = null;
		ChannelSftp channelSftp = null;
		
		JSch  jsch = new JSch();
		
		try {
			
			if(port <= 0){
				session = jsch.getSession(userName, host);
			}else{
				session = jsch.getSession(userName, host, port);
			}
			
			if(session == null){
				throw new Exception("连接sftp失败");
			}
			
			int timeOut = Integer.valueOf(Constants.getParam("timeout"));
			
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking","no");
			session.setTimeout(timeOut);
			session.connect();
			
			
			Channel channel = session.openChannel("sftp");
			channel.connect(timeOut);
			
			channelSftp  = (ChannelSftp) channel;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return channelSftp;
	}
	
	/**
	 * 连接host
	 */
	public void connect(){
		channelSftp = this._connect();
	}
	
	/**
	 * 下载文件
	 * @param fileName
	 * @param dir
	 * @param out
	 * @throws Exception
	 */
	public void downFile(String fileName,String dir,OutputStream out) throws Exception{
		
		if(channelSftp == null){
			throw new Exception("channelSftp 为 null");
		}	
		try {
			channelSftp.cd(dir);
			channelSftp.get(fileName, out);
		} catch (SftpException e) {
			throw new Exception("获取文件失败：" + e.getMessage());
		}finally{
			if(out != null){
				out.flush();
				out.close();
			}
			if(channelSftp != null){
				channelSftp.disconnect();
			}
			
		}
	}
	
	/**
	 * 上传文件
	 * @param fileName
	 * @param dir
	 * @param in
	 * @param mode
	 * @throws Exception
	 */
	public void uploadFile(String fileName,String dir,InputStream in,int mode) throws Exception{
		if(channelSftp == null){
			throw new Exception("channelSftp 为 null");
		}	
		try {
			channelSftp.cd(dir);
			channelSftp.put(in, fileName, mode);
		} catch (SftpException e) {
			throw new Exception("上传文件失败：" + e.getMessage());
		}finally{
			if(in != null){
				in.close();
			}
			if(channelSftp != null){
				channelSftp.disconnect();
			}
		}
	}
}
