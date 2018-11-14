package com.huateng.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.wldk.framework.utils.DateUtils;

public class ProtocolCodeFilter {
	
	//微信对账单下载命令前缀
	private  static  final  String  WX_BILL_INTERFACE = "wxbill";
	//微信对账单下载单个费率
	private  static  final  String  WX_BILL_SINGLE = "wxdown";
	//微信对账单合成
	private  static  final  String  WX_BILL_SUM = "wxmerg";
	//多费率配置重新加载内存
	private  static  final  String  MORE_FEE = "morefe";
	//备份订单表前缀
	private  static  final  String  BACK_UP_INTERFACE = "bakord";
	//支付宝对账单下载命令前缀
	private  static  final  String  ALIPAY_BILL_INTERFACE = "albill";
	//三码合一查询微信和支付宝订单前缀
	private  static  final  String  TC_QUERY_ORDER = "unknow";
	//三码合一下载微信和支付宝消费对账单前缀
	private  static  final  String  TC_WX_AND_ALIPAY_BILL = "wabill";
	//三码合一下载流水文件前缀
	private  static  final  String  TC_ORDER = "record";
	//向核心发起入账交易前缀
	private  static  final  String  T0_ACCOUNTED = "settle";
	
	private  static Logger logger = LoggerFactory.getLogger(ProtocolCodeFilter.class);
	
	public static OutputParam handleProtocolCode(String reviceMsg){
		logger.info("报文解析请求内容:"+reviceMsg);
		//协议格式
		String  protocol = null;
		OutputParam  outputParam = new OutputParam();
		try {
			
			//报文长度
			String protocolLength = reviceMsg.substring(0,6);
			
			//去除报文长度后的内容
			String protocolContent = reviceMsg.replaceAll("[\\t\\n\\r]", "").substring(6);
			
			logger.debug("----------解析报文长度:[" + protocolLength + "]----------");

			if(StringUtils.isNotEmpty(protocolLength) && protocolLength.matches("\\d{6}")){
				
				//报文前缀  为了区分  微信对账单   备份订单表  支付宝对账单 
				String protocolContentPrefix = protocolContent.substring(0,6);
				
				if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(WX_BILL_INTERFACE)){
					
					logger.info("----------判断报文为下载微信对账单的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					//需要下载的对账单的日期
					String protocolContentDate = protocolContent.substring(6);
					
					if(!protocolContentDate.matches("\\d{8}")){
						logger.debug("----------报文格式不正确----------");
						outputParam.setReturnMsg("报文格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[下载微信对账单] 日期:[" + protocolContentDate + "]");
					
					paramMap.put("protocolContent", protocolContentDate);	
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.WX_BILL_DOWN);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(WX_BILL_SINGLE)){
					
					logger.info("----------判断报文为下载微信对账单单个费率的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					if(protocolContent==null||protocolContent.length()!=16) {
						logger.debug("----------报文格式不正确"+protocolContent);
						outputParam.setReturnMsg("-报文格式不正确"+protocolContent);
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					//需要下载的对账单的日期
					String billDate = protocolContent.substring(6,14);
					String rate = protocolContent.substring(14,16);
					
					if(!DateUtils.isDate(billDate)){
						logger.debug("----------日期格式不正确"+billDate);
						outputParam.setReturnMsg("日期格式不正确"+billDate);
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[下载微信对账单] 日期:[" + billDate + "]费率["+rate+"]");
					
					paramMap.put("billDate", billDate);	
					paramMap.put("rate", rate);	
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.WX_BILL_SINGLE_DOWN);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(WX_BILL_SUM)){
					
					logger.info("----------判断报文为下载微信对账单 合成---------");
					
					if(protocolContent==null||protocolContent.length()!=14) {
						logger.debug("----------报文格式不正确"+protocolContent);
						outputParam.setReturnMsg("-报文格式不正确"+protocolContent);
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					//需要下载的对账单的日期
					String billDate = protocolContent.substring(6);
					
					if(!DateUtils.isDate(billDate)){
						logger.debug("----------微信对账单合成日期格式不正确----------");
						outputParam.setReturnMsg("微信对账单合成日期格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[下载微信对账单] 日期:[" + billDate + "]");
					
					paramMap.put("billDate", billDate);	
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.WX_BILL_SUM_DOWN);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(MORE_FEE)){
					
					logger.info("----------判断报文为下载微信对账单 合成---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.MORE_FEE_CONFIG);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(BACK_UP_INTERFACE)){
					
					logger.info("----------判断报文为备份订单表的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();

					//备份订单表的日期
					String protocolContentDate = protocolContent.substring(6);
					
					if(!protocolContentDate.matches("\\d{8}")){
						logger.debug("----------报文格式不正确----------");
						outputParam.setReturnMsg("报文格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[备份订单表] 日期:[" + protocolContentDate + "]");
					
					paramMap.put("protocolContent", protocolContentDate);	
				
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.BACK_UP_ORDER);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix) && protocolContentPrefix.startsWith(ALIPAY_BILL_INTERFACE)){
					
					logger.info("----------判断报文为下载支付宝对账单的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					//需要下载的对账单的日期
					String protocolContentDate = protocolContent.substring(6);
					
					if(!protocolContentDate.matches("\\d{8}") && !protocolContentDate.matches("\\d{6}")){
						logger.debug("----------报文格式不正确----------");
						outputParam.setReturnMsg("报文格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[下载支付宝对账单] 日期:[" + protocolContentDate + "]");
					
					paramMap.put("alipayBillDate", protocolContentDate);	
					paramMap.put("transType", StringConstans.TransType.TRANS_DOWN_FILE);
					paramMap.put("billType", StringConstans.BillType.BILLTYPE_ALIPAY);
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.ALIPAY_BILL_DOWN);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix)&&protocolContentPrefix.startsWith(TC_QUERY_ORDER)){
					
					logger.info("----------判断报文为查询三码合一订单信息的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					String protocolContentMsg = protocolContent.substring(6);
					if(JsonUtil.isJson(protocolContentMsg)){
						paramMap = JsonUtil.parseJSON2Map(protocolContentMsg,2000);
					} else {
						logger.debug("----------报文格式不正确，请传入正确的json格式----------");
						outputParam.setReturnMsg("报文格式不正确，请传入正确的json格式");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.TC_QUERY_ORDER);
					
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix)&&protocolContentPrefix.startsWith(TC_WX_AND_ALIPAY_BILL)){
					
					logger.info("----------判断报文为生成三码合一微信和支付宝对账单的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					//需要查询三码合一订单信息交易日期
					String protocolContentDate = protocolContent.substring(6);
					
					if(!protocolContentDate.matches("\\d{8}") && !protocolContentDate.matches("\\d{6}")){
						logger.debug("----------报文格式不正确----------");
						outputParam.setReturnMsg("报文格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[查询三码合一订单信息] 日期:[" + protocolContentDate + "]");
					
					paramMap.put("orderDate", protocolContentDate);	
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.TC_WX_AND_ALIPAY_BILL);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix)&&protocolContentPrefix.startsWith(TC_ORDER)){
					
					logger.info("----------判断报文为下载三码合一流水记录的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					//需要查询三码合一订单信息交易日期
					String protocolContentDate = protocolContent.substring(6);
					
					if(!protocolContentDate.matches("\\d{8}") && !protocolContentDate.matches("\\d{6}")){
						logger.debug("----------报文格式不正确----------");
						outputParam.setReturnMsg("报文格式不正确");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					logger.debug("[查询三码合一订单信息] 日期:[" + protocolContentDate + "]");
					
					paramMap.put("orderDate", protocolContentDate);	
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.TC_ORDER_BILL);
					
				}else if(StringUtils.isNotEmpty(protocolContentPrefix)&&protocolContentPrefix.startsWith(T0_ACCOUNTED)){
					
					logger.info("----------判断报文为向核心发送入账交易的报文---------");
					
					protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					String protocolContentMsg = protocolContent.substring(6);
					if(JsonUtil.isJson(protocolContentMsg)){
						paramMap = JsonUtil.parseJSON2Map(protocolContentMsg,2000);
					} else {
						logger.debug("----------报文格式不正确，请传入正确的json格式----------");
						outputParam.setReturnMsg("报文格式不正确，请传入正确的json格式");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						return outputParam;
					}
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", StringConstans.TxnServiceCode.T0_ACCOUNTED_TXN);
					
					
				} else {
					Map<String, Object> paramMap = new HashMap<String, Object>();
						
					//手机银行的JSON格式
					if(JsonUtil.isJson(protocolContent)){
						protocol =  StringConstans.ProtocolType.PROTOCOL_JSON;
						paramMap = JsonUtil.parseJSON2Map(protocolContent,2000);
					}else{
						protocol =  StringConstans.ProtocolType.PROTOCOL_XML;
						paramMap = Util.getMapFromXML(protocolContent);//接收请求的xml字符串转成map集合
					}
					logger.info("判断报文格式是:"+protocol);
					
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					outputParam.setReturnObj(paramMap);
					outputParam.putValue("protocol",protocol);
					outputParam.putValue("serviceCode", paramMap.get("txnCode"));
				} 
			}else{				
				outputParam.setReturnMsg("报文格式不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("protocol",null);
				outputParam.putValue("serviceCode", null);	
			}
			
		} catch (Exception e) {
			logger.error("报文解析失败"+ e.getMessage(),e);	
			outputParam.setReturnMsg("报文解析失败");
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("protocol",null);
			outputParam.putValue("serviceCode", null);	
		} 
		logger.info("报文解析返回内容:"+outputParam.toString());
		return outputParam;
	}
}
