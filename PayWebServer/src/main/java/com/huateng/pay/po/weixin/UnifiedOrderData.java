package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;

/**
 * 统一下单API需要提交的数据
 */
public class UnifiedOrderData  extends WxBaseData{

    //每个字段具体的意思请查看API文档
    private String sub_mch_id = "";
    private String device_info = "";
    private String nonce_str = "";
    private String body = "";
    private String attach = "";
    private String out_trade_no = "";
    private int total_fee = 0;
    private String spbill_create_ip = "";
    private String time_start = "";
    private String time_expire = "";
    private String goods_tag = "";
    private String notify_url = "";
    private String trade_type = "";
    private String product_id = "";
    private String limit_pay = "";
    private String sub_appid = "";
    private String sub_openid = "";
    private String openid = "";
    private String sign = "";
    

    /**
     * @param body 要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
     * @param attach 支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回
     * @param outTradeNo 商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
     * @param totalFee 订单总金额，单位为“分”，只能整数
     * @param deviceInfo 商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
     * @param spBillCreateIP 订单生成的机器IP
     * @param timeStart 订单生成时间， 格式为yyyyMMddHHmmss，如2009年12 月25 日9 点10 分10 秒表示为20091225091010。时区为GMT+8 beijing。该时间取自商户服务器
     * @param timeExpire 订单失效时间，格式同上
     * @param goodsTag 商品标记，微信平台配置的商品标记，用于优惠券或者满减使用
     * @param notifyUrl 通知地址，接收微信支付异步通知回调地址
     * @param tradeType 交易类型，取值如下：JSAPI，NATIVE，APP，WAP
     * @param productId 商品ID，trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义
     * @param limitPay 指定支付方式    no_credit:指定不使用信用卡
     * @param sub_appid 子公众号    代理商户模式(公众号支付上送)
     * @param sub_openid 子用户标识    no_credit:指定不使用信用卡
     * 
     */
      public UnifiedOrderData(Map<String, Object> dataMap,String key){
    	  super(dataMap);
        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(dataMap.get("appid").toString());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(dataMap.get("mch_id").toString());

        if(!StringUtil.isEmpty(dataMap.get("subMchId"))){
        	setSub_mch_id(dataMap.get("subMchId").toString());
        }
        
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(dataMap.get("body").toString());

        //支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回，有助于商户自己可以注明该笔消费的具体内容，方便后续的运营和记录
        setAttach(dataMap.get("attach").toString());

        //商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
        setOut_trade_no(dataMap.get("out_trade_no").toString());

        //订单总金额，单位为“分”，只能整数
        setTotal_fee(Integer.parseInt(dataMap.get("total_fee").toString()));

        //商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
        setDevice_info(dataMap.get("device_info").toString());

        //订单生成的机器IP
        setSpbill_create_ip(dataMap.get("spbill_create_ip").toString());

        //订单生成时间， 格式为yyyyMMddHHmmss，如2009年12 月25 日9 点10 分10 秒表示为20091225091010。时区为GMT+8 beijing。该时间取自商户服务器
        setTime_start(dataMap.get("time_start").toString());

        //订单失效时间，格式同上
        setTime_expire(dataMap.get("time_expire").toString());
        
      //商品标记，微信平台配置的商品标记，用于优惠券或者满减使用
        if(!StringUtil.isEmpty(dataMap.get("goods_tag"))){
        	setGoods_tag(dataMap.get("goods_tag").toString());
        }
        

        //随机字符串，不长于32 位
        setNonce_str(Util.getRandomStringByLength(32));
        
        //通知地址
        setNotify_url(dataMap.get("notify_url").toString());
        
        //交易类型
        setTrade_type(dataMap.get("trade_type").toString());
        
        //商品ID，trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义
        if(!StringUtil.isEmpty(dataMap.get("product_id"))){
        	setProduct_id(dataMap.get("product_id").toString());
        }

        //指定支付方式
        if(!StringUtil.isEmpty(dataMap.get("limitPay"))){
        	setLimit_pay(dataMap.get("limitPay").toString());
        }
        
        //子公众号
        if(!StringUtil.isEmpty(dataMap.get("sub_appid"))){
        	setSub_appid(dataMap.get("sub_appid").toString());
        }
        //子用户标志
        if(!StringUtil.isEmpty(dataMap.get("sub_openid"))){
        	setSub_openid(dataMap.get("sub_openid").toString());
        }
        
        //用户标志
        if(!StringUtil.isEmpty(dataMap.get("openId"))){
        	setOpenid(dataMap.get("openId").toString());
        }
        
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSub_mch_id() {
		return sub_mch_id;
	}

	public void setSub_mch_id(String sub_mch_id) {
		this.sub_mch_id = sub_mch_id;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getSub_appid() {
		return sub_appid;
	}

	public void setSub_appid(String sub_appid) {
		this.sub_appid = sub_appid;
	}

	public String getSub_openid() {
		return sub_openid;
	}

	public void setSub_openid(String sub_openid) {
		this.sub_openid = sub_openid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


}
