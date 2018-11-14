package com.huateng.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.wldk.framework.utils.StringUtil;

public class QRCodeUtil {
	private static Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);
//	private static final  String DYNAMIC_QR_CODE_FLAG_PREFIX = "0";
//	private static final  String STATIC_QR_CODE_FLAG_PREFIX = "1";
//	private static final  String QR_CODE_POSP = "0";
//	private static final  String QR_CODE_MOBILE = "1";
	
	/**
	 * 生成二维码密文
	 * @param inputParam
	 * @return
	 */
	public static  OutputParam encryptQRCodeCode(InputParam inputParam){
			logger.info("[生成二维码密文]  encryptQRCodeCode 方法开始  请求信息" + inputParam.toString());
			OutputParam  output = new OutputParam();
			
			try {
				
				//服务码
				String txnServiceCode = String.format("%s", inputParam.getValue("txnServiceCode"));
				
				//二维码前缀类型
				String qrCodePrefixType = String.format("%s", inputParam.getValue("localQrType"));
				
	 			if(StringConstans.TxnServiceCode.LOCAL_MOBILE_MICRO_ENCODE.equals(txnServiceCode)
						|| StringConstans.TxnServiceCode.LOCAL_POSP_UNIFIED_ENCODE.equals(txnServiceCode)
						|| StringConstans.TxnServiceCode.LOCAL_CREATE_STATIC_QR.equals(txnServiceCode)
						|| StringConstans.TxnServiceCode.CREATE_STATIC_QR_OF_THREE_CODE.equals(txnServiceCode)){
								
					//二维码动静类型
					String qrCodeType = "";
					
					//二维码有效期
					String qrCodeExpire = "";
				
					if(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE.getType().equals(qrCodePrefixType)
							|| QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType().equals(qrCodePrefixType)){
										
						//动态二维码标识
						qrCodeType = StringConstans.QRCodeType.DYNAMIC;
						
						//二维码有效期
						qrCodeExpire = Constants.getParam("qrcode_expire");
					}
					
					if(QRCodeTypeEnum.DYNAMIC_QR_CODE_POSP.getType().equals(qrCodePrefixType)
							|| QRCodeTypeEnum.DYNAMIC_QR_CODE_PAY_PLATFORM.getType().equals(qrCodePrefixType)){
						
						//动态二维码标识
						qrCodeType = StringConstans.QRCodeType.DYNAMIC;
						
						//二维码有效期
						qrCodeExpire = Constants.getParam("qrcode_expire");
					}
					
					if(QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(qrCodePrefixType)){
						
						//静态二维码标识
						qrCodeType = StringConstans.QRCodeType.STATIC;
						
						//二维码有效期
						qrCodeExpire = StringConstans.StaticQRCodeExpire.STATIC_QR_CODE_EXPIRE;
					}
					
					if(QRCodeTypeEnum.STATIC_QR_CODE_OTHER.getType().equals(qrCodePrefixType)){
						
						//静态二维码标识
						qrCodeType = StringConstans.QRCodeType.STATIC;
						
						//二维码有效期
						qrCodeExpire = StringConstans.StaticQRCodeExpire.STATIC_QR_CODE_EXPIRE;
					}
					
					if(QRCodeTypeEnum.STATIC_QR_CODE_THREE_CODE.getType().equals(qrCodePrefixType)){
						
						//静态二维码标识
						qrCodeType = StringConstans.QRCodeType.STATIC;
						
						//二维码有效期
						qrCodeExpire = StringConstans.StaticQRCodeExpire.STATIC_QR_CODE_EXPIRE;
					}
					
					//时间
					String dateTime = DateUtil.getDateStr("MMddHHmmss");
					
					//序列号
					String seqNo = String.format("%s", inputParam.getValue("qrSeqNo"));
					
					//二维码明文没有类型
					String qrCodeProClaimed  = String.format("%s%s",dateTime,seqNo);
					
					logger.debug("[生成二维码密文] 明文长度：[" + qrCodeProClaimed.length() +"]");
					
					//密文
					String cipherText = String.format("%s", ThreeDES.DES_3(qrCodeProClaimed,Constants.getParam("des_key"),0));
					
					logger.debug("[生成二维码密文] 密文未含前缀长度：[" + cipherText.length() +"]");
					
					//密文字符串
	 				String qrCodecipherText = String.format("%s%s", qrCodePrefixType,cipherText);
					
					//二维码明文含有类型两位
					String qrCodeProClaimedHasPrefix  = String.format("%s%s", qrCodePrefixType,qrCodeProClaimed);
					
					logger.debug("[生成二维码密文] 密文长度：[" + cipherText.length() +"]");
					
					output.setReturnCode(StringConstans.returnCode.SUCCESS);
					output.putValue("qrCodeProClaimed", qrCodeProClaimedHasPrefix);
					output.putValue("qrCodecipherText", qrCodecipherText);
					output.putValue("qrCodeStatus", StringConstans.QRCodeStatus.ENABLE);
					output.putValue("qrCodeType", qrCodeType);
					output.putValue("qrCodeExpire",qrCodeExpire );
					output.putValue("generatDt", DateUtil.getCurrentDateTime());
					
				}

			} catch (Exception e) {
				logger.error("生成二维码密文失败" + e.getMessage(), e);
				output.setReturnCode(StringConstans.returnCode.FAIL);
				output.setReturnMsg("生成二维码密文失败");	
			} 
			logger.info("[生成二维码密文]  encryptQRCodeCode 方法开始  请求信息" + inputParam.toString());
			return output;
			
		}
		
	/**
	 * 生成二维码明文
	 * @param inputParam
	 * @return
	 */
	public static  OutputParam decryptQRCodeCode (String qrCodecipherText){
		
		OutputParam  output = new OutputParam();
		
		try {
			
			//校验二维码密文长度
			if(StringUtil.isEmpty(qrCodecipherText) || qrCodecipherText.length() != 18){
				output.setReturnCode(StringConstans.returnCode.FAIL);
				output.setReturnMsg("二维码密文长度不正确");
				return output;
			}
			
			logger.debug("[解密二维码密文] 密文长度：[" + qrCodecipherText.length() +"]");
			
			//二维码类型
			String prefix = qrCodecipherText.substring(0,2);
			
			if(!QRCodeTypeEnum.isContains(prefix)){
				output.setReturnCode(StringConstans.returnCode.FAIL);
				output.setReturnMsg("二维码类型不正确");
				return output;
			}
			
			//需要解密的二维码信息
			qrCodecipherText = qrCodecipherText.substring(2,18);
			
			//明文
			String qrCodeProClaimed = String.format("%s%s",prefix,ThreeDES.DES_3(qrCodecipherText,Constants.getParam("des_key"),1));
				
			logger.debug("[解密二维码密文] 明文长度：[" + qrCodeProClaimed.length() +"]");
			
			output.setReturnCode(StringConstans.returnCode.SUCCESS);
			output.putValue("qrCodeProClaimed", qrCodeProClaimed);
	
		} catch (Exception e) {
			logger.error("获取二维码明文失败"+e.getMessage(),e);
			output.setReturnCode(StringConstans.returnCode.FAIL);
			output.setReturnMsg("获取二维码明文失败");	
		}
		
		return output;
	}
}
