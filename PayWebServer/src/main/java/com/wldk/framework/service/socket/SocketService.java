package com.wldk.framework.service.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket服务器
 * 
 * @author zhaodk
 * 
 */
public class SocketService extends Thread {
	private final int port = 41003;

	private ServerSocket serverSocket;

	protected Logger log = LoggerFactory.getLogger(SocketService.class);

	private transient boolean watchdog = false;

	public void run() {
		try {
			log.info("SocketThread被开启");
			service();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}

	public SocketService() {
		try {
			/** 创建一个服务器监听port端口的客户连接，其连接队列长度3 */
			serverSocket = new ServerSocket();
			serverSocket.getReceiveBufferSize();
			serverSocket.bind(new InetSocketAddress(port), 3);
//			System.out.println("SocketService服务器启动成功......");
			log.debug("SocketService服务器启动成功......");
		} catch (Exception e) {
			// log.info(e);
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * 服务启动
	 * 
	 * @throws Exception
	 */
	public void service() throws Exception {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept();
				try {
					byteTitle(socket);
					socket.close();

				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			} catch (IOException ioe) {
				log.error(ioe.getMessage(),ioe);
			}
			if (watchdog) {
				break;
			}

		}

	}

	/**
	 * 关闭服务
	 */
	public void stopSocket() {
		watchdog = true;
		if (serverSocket != null) {
			try {
				serverSocket.close();
				// System.out.println("关闭Scoket服务器成功!");
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	public static void main(String[] args) throws Exception {

	}

	/**
	 * 报文接收处理
	 * 
	 * @param socket
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String byteTitle(Socket socket) throws Exception {
		String byteTitleName = null;
		log.info("客户端开始访问=" + "客户端机子的地址是 :" + socket.getInetAddress() + "-=" + socket.getInetAddress().getHostAddress() + " =:" + socket.getPort() + ":" + socket.getReceiveBufferSize());
//		String prot = socket.getInetAddress().getHostAddress();
		InputStream dd = socket.getInputStream();
		byte[] by = new byte[100];
		int read = dd.read(by);
		if(read == -1){
			log.info("流中没有数据!");
		}
		dd.close();
		byteTitleName = new String(by);
		byteTitleName = byteTitleName.trim();
		log.info("接收客户端报头：" + byteTitleName.trim());
		/** 判断是否有报文存在 fanhuidingzhika|ICD20120202.fil,ICD20120202.txt|146 */
		if (byteTitleName != null && StringUtils.isNotEmpty(byteTitleName.trim())) {
//			/** 数据源 */
//			DataUtilsFromWork dac = new DataUtilsFromWork();
//			/** 0:报文头、1：个性化文件名，及文件标示名、2：作业ID */
//			String[] socketTitle = byteTitleName.split("\\|");
//			if (socketTitle.length == 3) {
//				if (socketTitle[1] != null && socketTitle[2] != null && socketTitle[0] != null) {
//					/** 解析报文体 */
//					Object[] socketFile = socketFile(socketTitle[1]);
//					/** 查询系统报文定义参数 */
//					List<Object[]> list = dac.query("select GLB_PARAM_IP,GLB_SOCKET_TITLE,JOB_TYPE from GLB_PARAM WHERE GLB_PARAM_TYPE='" + DataUtilServieImpl.GLB_PARAM_KEY("SocketInput") + "'");
//					/** 查询报文作业类型是否存在 */
//					Object[] job_Type2 = dac.queryObje("SELECT DISTINCT JOB_TYPE,JOB_UNIT_STATE FROM JOB_UNIT WHERE ID=" + socketTitle[2]);
//					if (job_Type2 != null && job_Type2.length > 0) {
//						if (job_Type2[0] != null && !"".equals(job_Type2[0]) && job_Type2[1] != null && !"".equals(job_Type2[1])) {
//							/** 作业类型 */
//							String job_Type = String.valueOf(job_Type2[0]);
//							/** 作业状态 */
//							String JOB_UNIT_STATE = String.valueOf(job_Type2[1]);
//							if (JobUnit.JOB_STATE.get("job_State_ZK_2").equals(JOB_UNIT_STATE)) {
//								if (list != null && list.size() > 0) {
//									for (Object[] object : list) {
//										if (socketTitle[0].indexOf(object[1].toString()) >= 0 && prot.trim().equals(object[0].toString().trim())) {
//											if (object[2] != null && !"".equals(object[2])) {
//												if (JobUnit.job_Type_ID.equals(object[2].toString()) && JobUnit.job_Type_ID.equals(job_Type)) {
//													DebitCardSocket debitCardSocket = new DebitCardSocket();
//													/** 获得DPS编号 */
//													String dpsCode = socketTitle[0].substring(object[1].toString().length());
//													if (dpsCode.length() < 1) {
//														DataUtilServieImpl.updateLogSocketErrSql("接收IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "失败，DPS编号长度小于1！", prot.trim());
//													}
//													/** 制卡处理 */
//													debitCardSocket.updateMake(object[2].toString(), socketTitle[2], (ArrayList<String>) socketFile[0], socketFile[1].toString(), prot.trim(), dpsCode);
//													DataUtilServieImpl.updateLogSocketSql("接收借记IC定制卡 IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "成功,文件名：" + socketTitle[1] + ",开始处理！", prot
//															.trim());
//													break;
//												} else if (JobUnit.job_Type_IY.equals(object[2].toString()) && JobUnit.job_Type_IY.equals(job_Type)) {
//													DataUtilServieImpl.updateLogSocketSql("接收借记IC预制卡 IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "成功,文件名：" + socketTitle[1] + "开始处理！", prot
//															.trim());
//													IcyuzhiCardSocket icyuzhisocket = new IcyuzhiCardSocket();
//													icyuzhisocket.updateMake(object[2].toString(), socketTitle[2], (ArrayList<String>) socketFile[0], socketFile[1].toString(), prot.trim());
//													break;
//												} else if (JobUnit.job_Type_IX.equals(object[2].toString()) && JobUnit.job_Type_IX.equals(job_Type)) {
//													DataUtilServieImpl.updateLogSocketSql("接收IC贷记卡 IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "成功,文件名：" + socketTitle[1] + "开始处理！", prot
//															.trim());
//													CreditCardSocket creditsocket = new CreditCardSocket();
//													creditsocket.updateMake(object[2].toString(), socketTitle[2], (ArrayList<String>) socketFile[0], socketFile[1].toString());
//													break;
//												} else {
//													String job_Type3 = job_Type != null && !"".equals(job_Type) ? job_Type.toString() : "";
//													String err = "接收报文失败，报文内容不正确，" + byteTitleName.trim() + " 或作业编号：" + socketTitle[2] + "对应的作业类型:" + job_Type3 + "不正确";
//													DataUtilServieImpl.updateLogSocketErrSql(err, prot.trim());
//												}
//											} else {
//												DataUtilServieImpl.updateLogSocketErrSql("接收报文失败，系统参数报文接收中作业类型配置为空！", prot.trim());
//											}
//										} else {
//											DataUtilServieImpl.updateLogSocketErrSql("接收IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "失败，与该种作业系统参数Socket参数配置不匹配！", prot.trim());
//										}
//									}
//								} else {
//									DataUtilServieImpl.updateLogSocketErrSql("接收IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "失败，未在系统参数Socket参数配置！", prot.trim());
//									log.debug("系统参数Socket参数未配置...");
//								}
//							} else {
//								DataUtilServieImpl.updateLogSocketErrSql("接收报文失败，接收的作业编号" + socketTitle[2] + "对应系统作业表JOB_UNIT中作业状态不正确不能处理报文", prot.trim());
//							}
//						} else {
//							DataUtilServieImpl.updateLogSocketErrSql("接收报文失败，接收的作业编号" + socketTitle[2] + "对应系统作业表JOB_UNIT中作业类型或状态为空", prot.trim());
//						}
//					} else {
//						DataUtilServieImpl.updateLogSocketErrSql("接收报文失败，接收的作业编号" + socketTitle[2] + "对应系统作业表JOB_UNIT中作业不存在", prot.trim());
//					}
//				} else {
//					DataUtilServieImpl.updateLogSocketErrSql("接收报文失败，报文部分参数丢失：" + byteTitleName + "！", prot.trim());
//				}
//
//			} else {
//				DataUtilServieImpl.updateLogSocketErrSql("接收IP：" + prot.trim() + " 报文内容：" + byteTitleName.trim() + "失败，报文格式错误必须！", prot.trim());
//			}

		}
		return null;
	}

	/**
	 * 解析报文体
	 * 
	 * @param socketFile
	 * @return
	 * @throws Exception
	 */
	public Object[] socketFile(String socketFile) throws Exception {
		Object[] obj = new Object[2];
		String[] h = socketFile.split(",");
		List<String> list = new ArrayList<String>();
		String fff = null;
		for (int i = 0; i < h.length; i++) {
			if (i != (h.length - 1)) {
				list.add(h[i]);
			} else {
				fff = h[i];
			}
		}
		obj[0] = list;
		obj[1] = fff;
		return obj;
	}

	/**
	 * 回复报文
	 * 
	 * @param title
	 */
	public void returnTitle(String title) {
		if (title != null) {
			log.info("开始处理数据>>>>");
//			System.out.println("开始处理数据>>>");
			try {
				/**
				 * 正式
				 */
				boolean sign = true;
				if (sign) {
					socket();
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * 发送SocketChannel请求客户端
	 * 
	 * @throws Exception
	 */
	public void socket() throws Exception {
		try {
			SocketChannel socketChannel = createSocketChannel();// 打开连接通道
			byteTitle2(socketChannel);
			socketChannel.close();
			log.info("发送报文到客户端成功");
		} catch (IOException e) {
			// log.info(e);
			log.error(e.getMessage(),e);
		}

	}

	/**
	 * 发送报头
	 * 
	 * @param socketChannel
	 * @return
	 * @throws Exception
	 */
	public void byteTitle2(SocketChannel channel) throws Exception {
		// System.out.println("发送报文到客户端");
		// System.out.println("开始写入报文");
		// log.info("开始写入报文");
		try {
			ByteBuffer buffer = ByteBuffer.allocate(13);// 定义容量为1024
			String byteTitle = "|RSKFTP|0033|";
			// System.out.println("byteTitle客户端=" + byteTitle);
			buffer.put(byteTitle.getBytes());
			buffer.flip();
			channel.write(buffer);
			buffer.clear();
		} catch (Exception e) {
			log.info("写入报文失败");
			log.info(e.getMessage(),e);
		}

	}

	/**
	 * 连接主机
	 * 
	 * @return
	 * @throws IOException
	 */
	public SocketChannel createSocketChannel() throws Exception {
		int prot = 22000;// 主机端口号
		String host = "130.233.1.23";// 主机IP
		SocketChannel sChannel = null;
		log.info("开始连接客户端回复报头");
		try {
			sChannel = SocketChannel.open();
			sChannel.connect(new InetSocketAddress(host, prot));
			log.info("连接客户端成功IP端口=" + host + prot);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return sChannel;
	}

}