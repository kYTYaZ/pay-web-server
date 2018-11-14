package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;

/**
 * 微信关闭订单申请请求对象
 * @author guohuan
 *
 */
public class CloseReqData  extends WxBaseData{

    //每个字段具体的意思请查看API文档
    private String sub_mch_id = "";
    private String nonce_str = "";
    private String out_trade_no = "";
    private String sign;
    
    /**
     * 微信关闭订单服务
     * @param outTradeNo 商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     */
      public CloseReqData(Map<String, Object> dataMap,String key){
    	  super(dataMap);
        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(dataMap.get("appid").toString());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(dataMap.get("mch_id").toString());
        
        if(!StringUtil.isEmpty(dataMap.get("subMchId"))){
        	setSub_mch_id(dataMap.get("subMchId").toString());
        }

        //商户系统自己生成的唯一的订单号
        setOut_trade_no(dataMap.get("outTradeNo").toString());

        //随机字符串，不长于32 位
        setNonce_str(Util.getRandomStringByLength(32));

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap(),key);
        setSign(sign);//把签名数据设置到Sign这个属性中

    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSub_mch_id() {
		return sub_mch_id;
	}

	public void setSub_mch_id(String sub_mch_id) {
		this.sub_mch_id = sub_mch_id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


}
