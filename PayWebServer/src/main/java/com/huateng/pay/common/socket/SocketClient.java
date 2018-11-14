/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: SocketClient.java
 * Author:   justin
 * Date:     2014-8-11 上午9:13:23
 * Description: //模块目的、功能描述
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.common.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.util.Constants;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉 〈方法简述 - 方法描述〉
 * 
 * @author justin
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SocketClient {
    private Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private String ipAddress;
    private String port;
    private Socket socket;
    private OutputStream pw;
    private InputStream is;

    public SocketClient(String ipAddress, int port) throws FrameException {
        try {
        	
        	//logger.debug("[socket连接],IP:[" + ipAddress + "],PORT:[" + port+ "]");
        	
            socket = new Socket(ipAddress, port);
            socket.setSoTimeout(Integer.parseInt(Constants.getParam("timeout")));
            
            //logger.debug("创建socket连接成功");
            
        } catch (UnknownHostException e) {
            logger.error("[socket连接] SocketClient创建失败:" + e.getMessage() + "IP:[" + ipAddress + "],PORT:[" + port+ "]");
            throw new FrameException("网络异常,请稍后再试");
        } catch (IOException e) {
            logger.error("[socket连接] SocketClient创建失败:" + e.getMessage() + "IP:[" + ipAddress + "],PORT:[" + port+ "]");
            throw new FrameException("socket连接失败");
        }
    }

    public void getOItream() throws FrameException {
        try {
            is = socket.getInputStream();
            pw = socket.getOutputStream();
        } catch (IOException e) {
            logger.error("[socket连接] method[getOItream] error:" + e.getMessage());
            throw new FrameException("[socket连接] socket获取流失败");
        }
    }

    public String sendBytes(String sendMessage,String charset) throws FrameException {
        String strXML = "";//具体报文内容
        try {

            getOItream();
            pw.write(sendMessage.getBytes(charset));
            pw.flush();
            
            logger.debug("[socket连接] SocketClient method[sendBytes] info: 发送报文完成，开始接收数据");
            
			String xmlLength = "";//6位报文长度内容
			byte[] bt = new byte[6];
			int length = is.read(bt);
			xmlLength = new String(bt, 0, length,charset);
			logger.debug("[socket连接] SocketClient method[sendBytes] info: 读取报文头长度xmlLength:[" + xmlLength +"],根据报文头长度开始读取正文信息");
			
			byte[] content = new byte[Integer.parseInt(xmlLength)];
			int contentLen = is.read(content);
			strXML = new String(content, 0, contentLen,charset);
            
			logger.debug("[socket连接] SocketClient method[sendBytes] info:接收正文信息完成, 报文正文内容" + strXML);
            
        } catch (IOException e) {
            logger.error("[socket连接] SocketClient method[sendBytes] error:" + e.getMessage());
            throw new FrameException("[socket连接] socket异常");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("[socket连接] 关闭流失败:" + e.getMessage());
                    throw new FrameException("[socket连接] 关闭流失败");
                }
            }
            if (pw != null) {
                try {
                    pw.close();
                } catch (IOException e) {
                    logger.error("[socket连接] 关闭流失败:" + e.getMessage());
                    throw new FrameException("[socket连接] 关闭流失败");
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("[socket连接] 关闭socket连接失败:" + e.getMessage());
                    throw new FrameException("[socket连接]关闭socket连接失败");
                }
            }

        }
        return strXML;
    }

    public static void main(String[] args) {

            // String filePath = "pmx201.ftl";
            // Pmx201 pmx = new Pmx201();
            // pmx.setBzh("bzj");
            // pmx.setCzy("czy");
            // pmx.setDdxh("ddxh");
            // pmx.setDfhm("dfhm");
            // System.out.println(XmlUtil.bean2Xml(pmx,filePath));
            SocketClient socketClient = new SocketClient("166.10.193.83", 6500);
            String outString = "pmx201  00000503<body_req><bzh>11</bzh><jgm>706660500</jgm><sblx>P</sblx><ddxh>1   </ddxh><xmbh>20140925</xmbh><qdh>TYSD</qdh><czy>0032</czy><jym>x201</jym><dfjzdw>0</dfjzdw><dfrmxbz>2</dfrmxbz><dfzh>7066605001192241360018</dfzh><dzbz>1</dzbz><flzwlx>1</flzwlx><hmjcbz>0</hmjcbz><jfjzdw>0</jfjzdw><jfrmxbz>3</jfrmxbz><jfzh>6228558888160897323</jfzh><kmmmbz>1</kmmmbz><wwlsh>10001955</wwlsh><jfzym>1401</jfzym><wwrq>20141013</wwrq><fse>1.0</fse><jmmm>83210d2ec2f6</jmmm><jmzl>8</jmzl><zfmm>83210d2ec2f6</zfmm></body_req>";
            // String outString =
            // "pms342  00000168<body_req><ddxh>1   </ddxh><xmbh>20140925</xmbh><qdh>TYSD</qdh><wwlsh>10002581</wwlsh><wwrq>20141009</wwrq><czy>0032</czy><jym>s342</jym><jgm>706660500</jgm></body_req>";
            byte[] bb = outString.getBytes();
            byte[] bbNew = Arrays.copyOf(bb, bb.length + 1);
            bbNew[bb.length] = (byte) 0xff;
          //  String outStr = socketClient.sendBytes(bbNew);
          //  System.out.println(outStr);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
