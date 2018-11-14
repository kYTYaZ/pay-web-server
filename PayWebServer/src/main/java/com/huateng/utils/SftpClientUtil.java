package com.huateng.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpClientUtil {

	/**
	 * 初始化日志引擎
	 */
	private final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

	/** Sftp */
	ChannelSftp sftp = null;
	/** sshSession */
	Session sshSession = null;
	/** 主机 */
	private String host = "";
	/** 端口 */
	private int port = 0;
	/** 用户名 */
	private String username = "";
	/** 密码 */
	private String password = "";

	/**
	 * 构造函数
	 * @param host 主机
	 * @param port 端口
	 * @param username 用户名
	 * @param password 密码
	 */
	public SftpClientUtil(String host, int port, String username,
			String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * 构造函数 默认端口22
	 * @param host 主机
	 * @param username 用户名
	 * @param password 密码
	 */
	public SftpClientUtil(String host, String username,
			String password) {
		this(host,22,username,password);
	}
	
	/**
	 * 连接sftp服务器
	 * @throws Exception
	 */
	public void connect()  throws Exception {
		try {
			logger.info("-------------连接SFTP服务器        START-------------------");
			
			JSch jsch = new JSch();
		    sshSession = jsch.getSession(this.username, this.host,
					this.port);
			
			logger.info("[连接SFTP服务器] , Session created");
			
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect(20000);
			logger.info("[连接SFTP服务器] , Session connected.");
			
			logger.info("[连接SFTP服务器] , Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			this.sftp = (ChannelSftp) channel;
			
			logger.info("[连接SFTP服务器] , Connected to " + this.host + ".");
			
			logger.info("-------------连接SFTP服务器  成功  END-------------------");
		} catch (Exception e) {
			logger.error("连接SFTP服务器出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 关闭与SFTP服务器的连接
	 * 
	 * @throws Exception
	 */
	public void disconnect() throws Exception {
		try {
			logger.info("-------------关闭与SFTP服务器的连接            START-------------------");

			if (this.sftp != null) {
				if (this.sftp.isConnected()) {
					this.sftp.disconnect();
				}
			}
			if (this.sshSession != null) {
				if (this.sshSession.isConnected()) {
					this.sshSession.disconnect();
				}
			}
			logger.info("-------------关闭与SFTP服务器的连接   成功     END-------------------");
		} catch (Exception e) {
			logger.error("关闭与SFTP服务器的连接出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 上传单个文件
	 * @param directory 上传的目录
	 * @param uploadFile 要上传的文件
	 * @throws Exception
	 */
	public void upload(String directory, String uploadFile) throws Exception {
		try {
			logger.info("-------------上传 " + uploadFile + " 文件            START-------------------");
			this.sftp.cd(directory);
			File file = new File(uploadFile);
			this.sftp.put(new FileInputStream(file), file.getName());
			logger.info("-------------上传 " + uploadFile + " 文件   成功     END-------------------");
		} catch (Exception e) {
			logger.error("上传 " + uploadFile + " 文件出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 上传目录下全部文件
	 * @param directory 上传的目录
	 * @throws Exception
	 */
	public void uploadByDirectory(String directory) throws Exception {
		try {
			String uploadFile = "";
			List<String> uploadFileList = this.listFiles(directory);
			Iterator<String> it = uploadFileList.iterator();

			while (it.hasNext()) {
				uploadFile = it.next().toString();
				this.upload(directory, uploadFile);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 下载单个文件
	 * @param directory 下载目录
	 * @param downloadFile 下载的文件
	 * @param saveDirectory  存在本地的路径
	 * @throws Exception
	 */
	public void download(String directory, String downloadFile,
			String saveDirectory) throws Exception {
		try {
			logger.info("-------------下载 " + downloadFile + " 文件            START-------------------");
			String saveFile = saveDirectory + "//" + downloadFile;
			this.sftp.cd(directory);
			File file = new File(saveFile);
			this.sftp.get(downloadFile, new FileOutputStream(file));
			logger.info("-------------下载 " + downloadFile + " 文件   成功     END-------------------");
		} catch (Exception e) {
			logger.error("下载 " + downloadFile + " 文件出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 下载目录下全部文件
	 * @param directory 下载目录
	 * @param saveDirectory存在本地的路径
	 * @throws Exception
	 */
	public void downloadByDirectory(String directory, String saveDirectory)
			throws Exception {
		try {
			String downloadFile = "";
			List<String> downloadFileList = this.listFiles(directory);
			Iterator<String> it = downloadFileList.iterator();

			while (it.hasNext()) {
				downloadFile = it.next().toString();
				if (downloadFile.toString().indexOf(".") < 0) {
					continue;
				}
				this.download(directory, downloadFile, saveDirectory);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 删除文件
	 * @param directory 要删除文件所在目录           
	 * @param deleteFile 要删除的文件          
	 * @throws Exception
	 */
	public void delete(String directory, String deleteFile) throws Exception {
		try {
			logger.info("-------------删除 " + deleteFile + " 文件            START-------------------");
			this.sftp.cd(directory);
			this.sftp.rm(deleteFile);
			logger.info("-------------删除 " + deleteFile + " 文件   成功     END-------------------");
		} catch (Exception e) {
			logger.error("删除 " + deleteFile + " 文件出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	/**
	 * 列出目录下的文件
	 * @param directory 要列出的目录
	 * @return list 文件名列表
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<String> listFiles(String directory) throws Exception {
		Vector fileList = null;
		List<String> fileNameList = null;
		try {
			
			fileNameList = new ArrayList<String>();

			fileList = this.sftp.ls(directory);
			Iterator it = fileList.iterator();

			while (it.hasNext()) {
				String fileName = ((LsEntry) it.next()).getFilename();
				if (".".equals(fileName) || "..".equals(fileName)) {
					continue;
				}
				fileNameList.add(fileName);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return fileNameList;
	}

	/**
	 * 更改文件名
	 * @param directory 文件所在目录
	 * @param oldFileNm 原文件名
	 * @param newFileNm 新文件名
	 * @throws Exception
	 */
	public void rename(String directory, String oldFileNm, String newFileNm)
			throws Exception {
		try {
			logger.info("-------------更改 " + oldFileNm + " 文件名            START-------------------");
			this.sftp.cd(directory);
			this.sftp.rename(oldFileNm, newFileNm);
			logger.info("-------------更改 " + oldFileNm + " 文件名   成功     END-------------------");
		} catch (Exception e) {
			logger.error("更改 " + oldFileNm + " 文件名出现异常：" + e.getMessage(),e);
			throw e;
		}
		
	}

	public void cd(String directory) throws Exception {
		this.sftp.cd(directory);
	}
	
	public void quit() throws Exception {
		this.sftp.quit();
	}

	public InputStream get(String directory) throws Exception {
		InputStream streatm = this.sftp.get(directory);
		return streatm;
	}

	public static void main(String[] args) throws Exception{
		
		SftpClientUtil clientUtil = null;
			clientUtil = new SftpClientUtil("158.222.68.110",  "root","123456");
			clientUtil.connect();
			clientUtil.download("//home//wasadmin//deploy20160927","PayWebServer.war","E:");
			clientUtil.quit();
			clientUtil.disconnect();
		
	}
	
}
