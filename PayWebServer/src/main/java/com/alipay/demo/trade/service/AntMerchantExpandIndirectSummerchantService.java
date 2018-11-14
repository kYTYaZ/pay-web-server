package com.alipay.demo.trade.service;

import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectCreateRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectModifyRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectQueryRequetBuilder;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectCreateResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectModifyResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectQueryResult;

public interface AntMerchantExpandIndirectSummerchantService {
	
	/**
	 * 增加商户
	 * @return
	 */
	public AntMerchantExpandIndirectCreateResult  createSubmerchant(AntMerchantExpandIndirectCreateRequetBuilder builer);
	

	/**
	 * 修改商户
	 * @return
	 */
	public AntMerchantExpandIndirectModifyResult  modifySubmerchant(AntMerchantExpandIndirectModifyRequetBuilder builder);
 

	/**
	 * 查询商户
	 * @return
	 */
	public AntMerchantExpandIndirectQueryResult  querySubmerchant(AntMerchantExpandIndirectQueryRequetBuilder builder);
 
}
