/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: IVerifyService.java
 * Author:   justin
 * Date:     2014-8-6 下午2:20:34
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.services.local;

import java.util.Map;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

/**
 * 本行二维码业务处理接口类
 * 
 * @author guohuan
 * @see 1.0
 * @since 1.0
 */
public interface ILocalBankService {
	/**
	 * 手机获取二维码
	 * @param paramMap
	 * @return
	 */
	public  OutputParam getMobileQRcode(InputParam inputParam);
	/**
	 * 手机解密PSOP二维码信息[手机扫POSP]
	 * @param paramMap
	 * @return
	 */
	public  OutputParam mobileDecryptPospQRcode(InputParam inputParam);
	
	/**
	 * 手机解密手机二维码信息[面对面转账]
	 * @param paramMap
	 * @return
	 */
	public  OutputParam mobileDecrytMobileQRcode(InputParam inputParam);
	
	/**
	 * 本行被扫下单[POSP扫手机银行]
	 * @param intPutParam
	 * @return
	 */
	public  OutputParam pospMicroConsume(InputParam inputParam);
	
	/**
	 * 本行主扫下单[POSP获取二维码]
	 * @param intPutParam
	 * @return
	 */
	public  OutputParam pospUnifiedConsume(InputParam inputParam);
	
	/**
	 * 处理手机银行支付通知
	 * @param intPutParam
	 * @return
	 */
	public  OutputParam handleMobilePayNotify(InputParam inputParam);
	
	
	/**
	 * 通知支付
	 * @param intPutParam
	 * @return
	 */
	public  OutputParam notifyPay(InputParam inputParam);
	
	/**
	 * 查询单订单状态[POSP查询订单状态]
	 * @param inputParam
	 * @return
	 */
	public  OutputParam pospQueryState(InputParam inputParam);
	
	/**
	 * 更新订单状态[更新订单状态]
	 * @param inputParam
	 * @return
	 */
	public  OutputParam updateOrderState(InputParam inputParam);
	
	/**
	 * 处理本行的三码合一
	 * @param inputParam
	 * @return
	 */
	public OutputParam  handlerLocalThreedQRCode(InputParam inputParam);
	
	
	/***************************************************************************丰收互联***********************************************************************************************/
	
	/**
	 * 创建丰收互联订单
	 * @param inputParam
	 * @return
	 */
	public OutputParam createMobileFrontThreeQrCode(InputParam inputParam);
	
	/**
	 * 处理丰收互联通知
	 * @param inputParam
	 * @return
	 */
	public OutputParam handlerMobileFrontNotify(InputParam inputParam);
	
	
	/***************************************************************************丰收互联***********************************************************************************************/
	
	/**
	 * 查询丰收互联订单
	 */
	public OutputParam queryFshlOrder(Map<String, String> queryMap);
	
	public OutputParam queryLimitAmt(InputParam inputParam,OutputParam outputParam);
	public OutputParam refund(InputParam input);
	public OutputParam refundQuery(InputParam input);
}
