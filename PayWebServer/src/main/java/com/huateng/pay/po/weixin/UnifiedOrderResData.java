package com.huateng.pay.po.weixin;


/**
 * 统一下单返回报文对象
 */
public class UnifiedOrderResData {
    //协议层
    private String return_code = "";
    private String return_msg = "";


    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }
}
