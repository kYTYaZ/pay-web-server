package com.huateng.pay.handler.processor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.po.notify.NotifyHandlerResult;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.po.notify.NotifyMessageReviceResult;
import com.huateng.pay.po.notify.NotifyMessage.OnlineNotifyMessage;
import com.huateng.pay.po.notify.NotifyMessage.SettleMessageToCore;
import com.huateng.pay.po.notify.NotifyMessage.ThreeCodeNotifyMessage;
import com.huateng.pay.po.settle.SettleMessageReviceResult;
import com.huateng.utils.EBCDICGBK;
import com.huateng.utils.TcpClient;
import com.huateng.utils.Util;

/**
 * @author Administrator
 * 此类用于处理接收的消息并将其转发
 */
public class NotifyProcessor {
	
	private  Logger logger = LoggerFactory.getLogger(NotifyProcessor.class);
	
	public NotifyHandlerResult  notifyHandler(NotifyMessage notifyMessage) throws Exception{
	
		NotifyHandlerResult result = new NotifyHandlerResult();
	
		//logger.info("[通知外围系统] 通知开始");
			
		Object obj = Util.getNotifyBeanNotNull(notifyMessage);
		
		Preconditions.checkState(obj != null," notifyMessage通知对对象不能同时为空");
		
		String xml =  obj.toString();
		if(! (obj instanceof SettleMessageToCore)){
			 xml  = Util.objToXml(obj);
		}
		
		String sendMessage = String.format("%06d%s", xml.getBytes("UTF-8").length,xml);
		logger.info("[通知外围系统] 通知报文内容:" + sendMessage);

	
		if(obj instanceof OnlineNotifyMessage){
			this.handlerOnlineNotifyMessage(notifyMessage, result, sendMessage);
		}
		
		if(obj instanceof ThreeCodeNotifyMessage){
			this.handlerThreeCodeNotifyMessage(notifyMessage, result, sendMessage);
		}
		
		if(obj instanceof SettleMessageToCore){
			this.handlerSettleNotifyMessage(notifyMessage, result, sendMessage);
			//this.querySettleHandlerResult(notifyMessage, result, sendMessage);
		}
		
		//logger.info("[通知外围系统] 通知结束");

		return result;
	}
	
	
	
	
	private void handlerSettleNotifyMessage(NotifyMessage notifyMessage,NotifyHandlerResult result,String sendMessage) throws Exception {
		String ip = Constants.getParam("send_to_core_ip");
		int port = Integer.valueOf(Constants.getParam("send_to_core_port"));
		SettleMessageReviceResult settleResult = new SettleMessageReviceResult();
		
		 //交易码
		String txnNum = Constants.getParam("settle_txn_num");    
		
		//交易流水
		String txnSeqId = notifyMessage.getTxnSeqId();
		
		//前置流水
		String head = this.assembleMessageHead(notifyMessage, sendMessage, txnNum);
		logger.info("[T+0清算]报文头内容" + head);

		//入账标识
		String accountedFlag = null;
		
		
		//报文头转码
		byte[] headToEBCD = EBCDICGBK.gbkToEBCDIC(head.getBytes());  
//		EBCDICGBK.trace(headToEBCD);
		
		//报文体内容
		String message = sendMessage.substring(20);
		logger.info("[T+0清算] 报文体长度" + message.getBytes().length);
		
		//报文体转ebcd码
//		EBCDICGBK.demoChangeStringToHex(message);
		byte[] bodyToEBCD =EBCDICGBK.gbkToEBCDIC(message.getBytes("gbk"));
//		EBCDICGBK.trace(bodyToEBCD);
		
		//报文长度，（包括自身长度（8字节），再加上报文头和报文体长度）
		int toCoreLen = headToEBCD.length + bodyToEBCD.length + 8; 
		String toCoreLenStr = String.format("%08d", toCoreLen);
		//报文长度转ebcd码
		byte[] coreLenToEBCD = EBCDICGBK.gbkToEBCDIC(toCoreLenStr.getBytes());   
//		EBCDICGBK.trace(coreLenToEBCD);
		
		//渠道头长度
		int channelLen = 35;
		
		//报文总长，渠道头长度+报文总长（右对齐）
		int len = 0;           
		len += channelLen;         
		len += toCoreLen; 
		String lenStr = String.format("%8s", len);
		logger.info("[T+0清算] 报文总长度" + lenStr);                    
		
		//系统代码
		String posPlatformId = Constants.getParam("pos_platform_id");
		//渠道头内容
		//String channelHead = "3410CP 0SS3401  56284397   ";
		String channelHead = posPlatformId + "10CP 0SS" + posPlatformId + "01  56284397   ";
		
		//拼接渠道报文
		byte[] total = new byte[len]; 
		//8字节总长度 
		System.arraycopy(lenStr.getBytes(), 0, total, 0, lenStr.length());
		//拼接渠道头
		System.arraycopy(channelHead.getBytes(), 0, total, lenStr.length(), channelHead.length());
		//拼接发送核心报文长度
		System.arraycopy(coreLenToEBCD, 0, total, lenStr.length()+channelHead.length(), coreLenToEBCD.length);
		//拼接上送核心报文头
		System.arraycopy(headToEBCD, 0, total, lenStr.length()+channelHead.length()+ coreLenToEBCD.length, headToEBCD.length);
		//拼接上送核心报文体
		System.arraycopy(bodyToEBCD, 0, total, lenStr.length()+channelHead.length()+ coreLenToEBCD.length+ headToEBCD.length, bodyToEBCD.length);     
		EBCDICGBK.trace(total);
		
		String reciveContent=null;
		try {
			TcpClient tcpClient = this.initTcpConnect(ip,port);
			reciveContent = tcpClient.sendToCore(total, true);
			logger.info("[T+0清算] 接收返回信息:" + reciveContent);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			if (e instanceof SocketException || e instanceof SocketTimeoutException) {
				logger.error("[T+0清算] 连接异常或接收返回信息超时" + e.getMessage(), e);
				this.querySettleHandlerResult(notifyMessage, result, sendMessage);
				return;
			}
		}
		
		if(StringUtil.isEmpty(reciveContent)){
			this.setHandlerResult(result, false, "接收返回值为空");
			return;
		}
		
		//核心返回交易状态 —— 位置固定
		String status = reciveContent.substring(35, 36);
		logger.debug("[T+0清算] 核心返回交易状态：" + status);
		
		//入账标识
		accountedFlag = "";
		if (StringConstans.CoreBackInfo.COREBACKSTATUS.equals(status)) {
			int backIndex = reciveContent.lastIndexOf(StringConstans.CoreBackInfo.ACCOUNTEDRESPOND);
			String backLenStr = reciveContent.substring(backIndex-6, backIndex); //BWO24511前6位表示长度
			int backLen = Integer.parseInt(backLenStr.trim());
			settleResult.setEnterAccountDate(reciveContent.substring(backIndex+8, backIndex+16));
			settleResult.setCoreOrderNumber(reciveContent.substring(backIndex+16, backIndex+27));
			String respond = reciveContent.substring(backIndex, backIndex + backLen -8);
			int length = respond.getBytes("gbk").length - respond.length();
			if (length != 0) {
				accountedFlag = reciveContent.substring(backIndex + 107 - length -2, backIndex + 108 - length - 2);
				//accountedFlag = reciveContent.substring(backIndex + 107 - length, backIndex + 108 - length);
			} else {
				accountedFlag = reciveContent.substring(backIndex + 107, backIndex + 108);
			}	
			logger.debug("[T+0清算] 核心返回入账标识：" + accountedFlag);
		} else {
			accountedFlag = StringConstans.SettleInfo.ACCOUNTEDTHREE;
		}
		
		
		if (StringConstans.SettleInfo.ACCOUNTEDONE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDSUCCESS;
		}else if (StringConstans.SettleInfo.ACCOUNTEDTWO.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDFAIL;
		}else if (StringConstans.SettleInfo.ACCOUNTEDTHREE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN;
		}else {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN;
		} 
		
		
		settleResult.setTxnSeqId(txnSeqId);
		settleResult.setAccountedFlag(accountedFlag);
		result.setSettleResult(settleResult);
		this.setHandlerResult(result, true, null);
		
	}
	
	/**
	 * 查证交易
	 */
	private void querySettleHandlerResult(NotifyMessage notifyMessage,NotifyHandlerResult result,String sendMessage) throws Exception {
		String ip = Constants.getParam("send_to_core_ip");
		int port = Integer.valueOf(Constants.getParam("send_to_core_port"));
		
		notifyMessage.setTimes(notifyMessage.getTimes() + 1);
		//System.out.println("当前次数:" + notifyMessage.getTimes());
		
		
		if (notifyMessage.getTimes() > 5) {
			this.setHandlerResult(result, false, "入账交易失败");
			return;
		}
		
		SettleMessageReviceResult settleResult = new SettleMessageReviceResult();
		
		 //交易码
		String txnNum = Constants.getParam("query_txn_num");    
		
		//交易流水
		String txnSeqId = notifyMessage.getTxnSeqId();
		
		//pos平台编号
		String posPlatformId = Constants.getParam("pos_platform_id");
		
		//前置日期
		String txnDt = String.format("%-8s", sendMessage.substring(6, 14));
		
		String txnDate = txnDt.substring(6, 8);
		
		BigDecimal addDate = new BigDecimal(txnDate).add(new BigDecimal("31"));
		logger.debug("[T+0清算 轮询] 日期+31后=" + addDate);
		
		//拼接前置流水  2位系统代码+2位日期+交易流水后8位
		StringBuffer preStreamBuf = new StringBuffer();
		int index = txnSeqId.length() == 12? 4:2;
		preStreamBuf.append(posPlatformId)
				 .append(addDate.toString())
				 .append(txnSeqId.substring(index, txnSeqId.length()));
		
		//前置流水
		String preStream = String.format("%-12s", preStreamBuf.toString());
		
		//报文头
		String head = this.assembleMessageHead(notifyMessage, sendMessage, txnNum);
		logger.info("[T+0清算 轮询]报文头内容" + head);

		//报文体内容
		StringBuffer messageBuf = new StringBuffer();
		String message = messageBuf.append(txnDt).append(preStream).toString();
		logger.info("[T+0清算 轮询] 报文体内容" + message);
		
		//入账标识
		String accountedFlag = null;
		
		
		//报文头转EBCD码
		byte[] headToEBCD = EBCDICGBK.gbkToEBCDIC(head.getBytes());  
//		EBCDICGBK.trace(headToEBCD);
		
		//报文体转ebcd码
//		EBCDICGBK.demoChangeStringToHex(message);
		byte[] bodyToEBCD =EBCDICGBK.gbkToEBCDIC(message.getBytes("gbk"));
//		EBCDICGBK.trace(bodyToEBCD);
		
		//报文长度转ebcd码（报文长度——包括自身长度（8字节），再加上报文头和报文体长度）
		int toCoreLen = headToEBCD.length + bodyToEBCD.length + 8; 
		String toCoreLenStr = String.format("%08d", toCoreLen);
		byte[] coreLenToEBCD = EBCDICGBK.gbkToEBCDIC(toCoreLenStr.getBytes());   
//		EBCDICGBK.trace(coreLenToEBCD);
		
		//渠道头长度（定长）
		int channelLen = 35;
		
		//报文总长，渠道头长度+报文总长
		int len = 0;
		len += channelLen;         
		len += toCoreLen;      
		
		//报文总长度   右对齐
		String lenStr = String.format("%8s", len);
		logger.info("[T+0清算 轮询] 报文总长度" + lenStr);  
		
		//渠道头内容
		//String channelHead = "3410CP 0SS3401  56284397   ";
		String channelHead = posPlatformId + "10CP 0SS" + posPlatformId + "01  56284397   ";
		
		//拼接渠道报文
		byte[] total = new byte[len];
		 //8字节总长度
		System.arraycopy(lenStr.getBytes(), 0, total, 0, lenStr.length());
		//拼接渠道头
		System.arraycopy(channelHead.getBytes(), 0, total, lenStr.length(), channelHead.length());
		//拼接发送核心报文长度
		System.arraycopy(coreLenToEBCD, 0, total, lenStr.length()+channelHead.length(), coreLenToEBCD.length);
		//拼接上送核心报文头
		System.arraycopy(headToEBCD, 0, total, lenStr.length()+channelHead.length()+ coreLenToEBCD.length, headToEBCD.length);
		//拼接上送核心报文体
		System.arraycopy(bodyToEBCD, 0, total, lenStr.length()+channelHead.length()+ coreLenToEBCD.length+ headToEBCD.length, bodyToEBCD.length);     
		EBCDICGBK.trace(total);
		
		TcpClient tcpClient = this.initTcpConnect(ip,port);
		String reciveContent = tcpClient.sendToCore(total, true);
		logger.info("[T+0清算 轮询] 接收返回信息:" + reciveContent);
		
		if(StringUtil.isEmpty(reciveContent)){
			this.setHandlerResult(result, false, "接收返回值为空");
			return;
		}
		
		String status = reciveContent.substring(35, 36);
		logger.debug("[T+0清算  轮询] 核心返回交易状态：" + status);
		if (StringConstans.CoreBackInfo.COREBACKSTATUS.equals(status)) {
			int backIndex = reciveContent.indexOf(StringConstans.CoreBackInfo.QUERYRESPOND);
			String backLenStr = reciveContent.substring(backIndex-6, backIndex); //BWO24511前6位表示长度
			int backLen = Integer.parseInt(backLenStr.trim());
			if (17 == backLen) {
				//说明没有查到结果,则发起入账交易
				logger.debug("[T+0清算  轮询] 连接正常但没有找到这笔记录，重新发起入账");
				this.handlerSettleNotifyMessage(notifyMessage, result, sendMessage);
				return;
			}else {
				//accountedFlag = reciveContent.substring(backIndex + 8 + 219, backIndex + 8 + 219 + 22).trim();
				accountedFlag = reciveContent.substring(backIndex + 8 + 219, backIndex + 8 + 219 + 22).substring(4, 5);
				logger.debug("[T+0清算  轮询] 核心返回入账标识：" + accountedFlag);
			}
			
		} else {
			accountedFlag = StringConstans.SettleInfo.ACCOUNTEDTHREE;
		}
		
		/*if (StringConstans.SettleInfo.ACCOUNTEDONE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDSUCCESS;
		} 
		
		if (StringConstans.SettleInfo.ACCOUNTEDTWO.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDFAIL;
		} 
		
		if (StringConstans.SettleInfo.ACCOUNTEDTHREE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN;
		} */
			
		if (StringConstans.SettleInfo.ACCOUNTEDONE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDSUCCESS;
		}else if (StringConstans.SettleInfo.ACCOUNTEDTWO.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDFAIL;
		}else if (StringConstans.SettleInfo.ACCOUNTEDTHREE.equals(accountedFlag)) {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN;
		}else {
			accountedFlag = StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN;
		} 
		
		settleResult.setTxnSeqId(txnSeqId);
		settleResult.setAccountedFlag(accountedFlag);
		result.setSettleResult(settleResult);
		this.setHandlerResult(result, true, null);
		
	}
	
	/**
	 * 拼装报文头
	 * @param notifyMessage
	 * @param sendMessage
	 * @param txnNum
	 * @return
	 */
	private String assembleMessageHead(NotifyMessage notifyMessage, String sendMessage, String txnNum) {
		//交易日期  格式yyyy-mm-dd
		String txnDt = DateUtil.dateFormat(sendMessage.substring(6, 14));             
		
		//交易时间  hh:mm:ss
		String txnTm = DateUtil.tmFormat(sendMessage.substring(6, 20));      
		 
		//pos平台编号
		String posPlatformId = Constants.getParam("pos_platform_id"); 
		
		//交易流水
		String txnSeqId = notifyMessage.getTxnSeqId();
		
		String txnDate = sendMessage.substring(12, 14);
		
		logger.debug("[T+0组装报文头]交易日期：" + txnDate);
		
		BigDecimal addDate = new BigDecimal(txnDate).add(new BigDecimal("31"));
		logger.debug("[T+0组装报文头] 日期+31后=" + addDate);
		
		//拼接前置流水  2位系统代码+2位日期+交易流水后8位
		StringBuffer preStream = new StringBuffer();
		int index = txnSeqId.length() == 12? 4:2;
		preStream.append(posPlatformId)
				 .append(addDate.toString())
				 .append(txnSeqId.substring(index, txnSeqId.length()));
		
		String channelId = Constants.getParam("channel_id");
		
		StringBuffer headBuf = new StringBuffer();
		headBuf.append("DB1100000000   146@RQHDR  ")
			   .append("REQ")               /*信息类型*/
			   .append("00")                /*交互模式1+翻页模式1*/
			   .append("   " + channelId)   /*预留3+渠道代码+渠道分析码*/
			   .append(" ")                 /*客户端应用种类代码*/
			   .append("      ")            /*柜员输入交易码*/
			   .append(txnNum)              /*交易码*/
			   .append(txnDt)               /*交易日期*/
			   .append(txnTm)               /*交易时间*/
			   .append("999000   ")         /*交易机构号（6） + 交易部门（3）*/
			   .append("01")                /*交易地区*/
			   .append("999  1")            /*联社号前三位+'  1'*/
			   .append("        ")          /*终端号*/
			   .append("999S400")           /*柜员号*/
			   .append("       ")           /*静态授权柜员号*/
			   .append("0")                 /*已预警和授权标识*/
			   .append("0")                 /*交易性质 0-正常交易*/
			   .append(posPlatformId)       /*发起方系统代码*/
			   .append("0")                 /*老系统交易标识 0-非老系统*/
			   .append(preStream.toString())//前置流水
			  /* .append("342400000006")*/
			   .append("                       ")       /*抹账渠道交易流水号12+抹账核心交易流水号11*/
			   .append("   828@RQDTL  ");               /*交易请求输入字段*/    
		
		return headBuf.toString();
	}
	
	private void handlerOnlineNotifyMessage(NotifyMessage notifyMessage,NotifyHandlerResult result,String sendMessage) throws Exception{
		
		String ip = Constants.getParam("online_notify_ip");
		int port = Integer.valueOf(Constants.getParam("online_notify_port"));

		TcpClient tcpClient = this.initTcpConnect(ip,port);
		tcpClient.send(sendMessage, false);
			
		logger.info("[通知外围系统] 通知结束");
			
		this.setHandlerResult(result, true, null);
	
	}
	
	private void handlerThreeCodeNotifyMessage(NotifyMessage notifyMessage,NotifyHandlerResult result,String sendMessage) throws Exception{
			
		notifyMessage.setTimes(notifyMessage.getTimes() + 1);
			
		String ip = Constants.getParam("online_notify_ip");
		int port = Integer.valueOf(Constants.getParam("online_notify_port"));

		TcpClient tcpClient = this.initTcpConnect(ip,port);
		String reciveContent = tcpClient.send(sendMessage, true);
		logger.info("[通知外围系统] 接收返回信息:" + reciveContent);
		
		if(StringUtil.isEmpty(reciveContent)){
			this.setHandlerResult(result, false, "接收返回值为空");
			return;
		}
			
		NotifyMessageReviceResult  reviceResult = Util.xmlToObj(reciveContent, NotifyMessageReviceResult.class);

		if(!StringConstans.returnCode.SUCCESS.equals(reviceResult.getReturnCode())){
			this.setHandlerResult(result, false, reviceResult.getReturnMsg());
			return;
		}
			
		this.setHandlerResult(result, true, null);
	}
	
	
	
	/**
	 * 初始化tcp连接
	 * @return
	 * @throws IOException
	 */
	private  TcpClient initTcpConnect(String ip,int port) throws IOException{
		
		logger.debug("[通知外围系统] IP=" + ip + ",port:" + port);
		
		TcpClient tcpClient = new TcpClient(ip, port,StringConstans.Charsets.UTF_8);
		tcpClient.connect();
		
		return tcpClient;
	}
	
	
	/**
	 * 设置处理结果
	 * @param result
	 * @param flag
	 * @param msg
	 */
	private  void setHandlerResult(NotifyHandlerResult result,boolean flag,String msg){
	
		String date = DateUtil.getDateStr("yyyy-MM-dd HH:mm:ss");
		
		if(flag){
			result.setResultCode(StringConstans.returnCode.SUCCESS);
			result.setDate(date);
			result.setResultMsg("处理成功");
			return;
		}
		
		result.setResultCode(StringConstans.returnCode.FAIL);
		result.setDate(date);
		result.setResultMsg("处理失败");
		result.setFailureReason(msg);
	}
	
	
}
