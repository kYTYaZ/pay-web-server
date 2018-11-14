package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.utils.Signature;
import com.huateng.utils.Util;

/**
 * 下载微信对账单请求对象
 * @author guohuan
 *
 */
public class DownloadBillReqData  extends WxBaseData{
    //每个字段具体的意思请查看API文档
    private String device_info = "";
    private String nonce_str = "";
    private String bill_date = "";
    private String bill_type = "";
    private String sign;

    /**
     * 请求对账单下载服务
     * @param deviceInfo 商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
     * @param billDate 下载对账单的日期，格式：yyyyMMdd 例如：20140603
     * @param billType 账单类型
     *                 	ALL，返回当日所有订单信息，默认值
					    SUCCESS，返回当日成功支付的订单
					    REFUND，返回当日退款订单
					    REVOKED，已撤销的订单
     */
    public DownloadBillReqData(Map<String, Object> dataMap,String key){
    	super(dataMap);

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(dataMap.get("appid").toString());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(dataMap.get("mch_id").toString());

        //商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
        setDevice_info(dataMap.get("deviceInfo").toString());

        setBill_date(dataMap.get("billDate").toString());

        setBill_type(dataMap.get("billType").toString());

        //随机字符串，不长于32 位
        setNonce_str(Util.getRandomStringByLength(32));

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap(),key);
        setSign(sign);//把签名数据设置到Sign这个属性中

    }
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }


}
