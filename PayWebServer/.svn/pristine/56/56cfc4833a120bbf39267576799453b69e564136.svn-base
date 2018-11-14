package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;

/**
 * 微信退款查询请求对象
 * @author guohuan
 *
 */
public class RefundQueryReqData  extends WxBaseData{
    //每个字段具体的意思请查看API文档
    private String sub_mch_id = "";
    private String device_info = "";
    private String nonce_str = "";
    private String transaction_id = "";
    private String out_trade_no = "";
    private String sign;

    /**
     * 请求退款查询服务
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo 商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param deviceInfo 微信支付分配的终端设备号，与下单一致
     * @param outRefundNo 商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param refundID 来自退款API的成功返回，微信退款单号refund_id、out_refund_no、out_trade_no 、transaction_id 四个参数必填一个，如果同事存在优先级为：refund_id>out_refund_no>transaction_id>out_trade_no
     */
      public RefundQueryReqData(Map<String, Object> dataMap,String key){
    	  super(dataMap);
    
        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(dataMap.get("appid").toString());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(dataMap.get("mch_id").toString());
        
        if(!StringUtil.isEmpty(dataMap.get("subMchId"))){
        	setSub_mch_id(dataMap.get("subMchId").toString());
        }

        //transaction_id是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。
        setTransaction_id(dataMap.get("transactionID").toString());

        //商户系统自己生成的唯一的订单号
        setOut_trade_no(dataMap.get("outTradeNo").toString());

        //微信支付分配的终端设备号，与下单一致
        setDevice_info(dataMap.get("deviceInfo").toString());

        //商户系统自己管理的退款号，商户自身必须保证这个号在系统内唯一
        setOut_refund_no(dataMap.get("outRefundNo").toString());

        setRefund_id(dataMap.get("wxRefundNo").toString());

        //随机字符串，不长于32 位
        setNonce_str(Util.getRandomStringByLength(32));

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap(),key);
        setSign(sign);//把签名数据设置到Sign这个属性中

    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    private String out_refund_no;
    private String refund_id;

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
