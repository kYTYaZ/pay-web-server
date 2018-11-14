/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: TmkIssuedUtil.java
 * Author:   sunguohua
 * Date:     2014-7-11 上午10:47:49
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.common.socket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.DataUtils;
import com.wldk.framework.utils.MappingUtils;

/**
 * 调用后台交易接口
 * 
 * @author sunguohua
 */
public class SocketTxnUtil {
    private static Logger log = LoggerFactory.getLogger(SocketTxnUtil.class);
    private static String SUCCESS = "00";// 收到应答，交易成功
    private static String NO_RESPONSE = "02";// 未收到对方应答
    private static String ERROR_RESPONSE = "03";// 收到异常的应答结果
    private static String TXN_EXCEPTION = "05";// 发生了未知的异常
    private static String retCode = SUCCESS;

    private static String mng;

    private static String socketLen;

    private static String mngType;

    //报文交易码
    public static void setMngType(String type) {
        mngType = type;
    }
    //报文数据长度
    public static void setSocketLen(String len) {
        socketLen = len;
    }
    //报文数据
    public static void setMng(String msg) {
        mng = msg;
    }

    public static Map<String,String> socketTxn() {

        Map<String,String> returnMap = new HashMap<String,String>();
        // 拼报文
        String strAllCmd = "";
        mngType ="6241";//交易码
        String msgLen="060";//60域错有长度
        StringBuffer socketCmd = new StringBuffer();        
        socketCmd.append(mngType);
        socketCmd.append(msgLen);
        socketCmd.append(mng);
        
        socketLen = new String(DataUtils.hexStringToByte("00"))
                + new String(DataUtils.hexStringToByte(DataUtils.toHexString(socketCmd.length())));
               
        // 添加总长度，拼出最终消息
        strAllCmd = socketLen + socketCmd.toString();
        log.info("报文：[" + socketLen + socketCmd.toString() + "]");

        /* <TCP/IP>数据传送 */
        try {
            // 可以起动socket
            String socketadd = MappingUtils.getString("SYSPARAM", "socketTxnIp");
            
            log.info("the socketadd is : " + socketadd);

            String socketport = MappingUtils.getString("SYSPARAM", "socketTxnPort");
            
            log.info("the socketport is : " + socketport);

            // 启动socket
            log.info("启动 <秘钥转换> 命令！");

            SocketConnectionClient cl = new SocketConnectionClient();
            cl.makeConnection(socketadd, Integer.parseInt(socketport));
            int rst = cl.sendMessage(strAllCmd);// 发送报文 ，返回报文长度
            log.info("rst = " + rst);
            // 等待获取对方响应
            String res = cl.getResult();
            long begintime = System.currentTimeMillis();
            long endtime = 0;
            while (res == null || res.length() == 0) {
                Thread.sleep(20);
                res = cl.getResult();
                endtime = System.currentTimeMillis();
                if ((endtime - begintime) / 1000 >= 60) {
                    break;
                }
            }

            log.info("返回码：" + res);
            // 结束会话
            rst = cl.sendMessage("quit");
            if (rst == -1) {
                log.info("client.close()");
                // return retCode;
            } else {
                cl.stopClient();
            }
            String xym = "";
            try{
            	res=res.substring(2, res.length()-6).trim();
            	xym=res.substring(4, 6);
            }catch(Exception e){
            	retCode = TXN_EXCEPTION;
                returnMap.put("retCode", retCode);
                returnMap.put("retMsg", "返回报文格式异常");
                return returnMap;
            }
            
            //log.debug("chkMng = " + chkMng);
            String retStr = "";
            if (res == "") {// 没应答
                retCode = NO_RESPONSE;
            } else if (!"00".equals(xym)) {// 报文异常
                retCode = ERROR_RESPONSE;
            } else {// 正常报文
                retCode = SUCCESS;
                retStr = res;//正确后台返回报文
                log.debug("send success!!!!");               
            }
            returnMap.put("retCode", retCode);
            returnMap.put("retMsg", retStr.substring(retStr.length()-12, retStr.length()));
            log.debug("in socketTxn retCode=" + retCode);
            return returnMap;

        } catch (Exception e) {
            retCode = TXN_EXCEPTION;
            String  retStr = e.getMessage();
            returnMap.put("retCode", retCode);
            returnMap.put("retMsg", retStr);
            return returnMap;
        }
    }
}
