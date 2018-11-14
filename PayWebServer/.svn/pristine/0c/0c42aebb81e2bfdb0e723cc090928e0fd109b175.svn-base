package com.alipay.demo.trade.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.demo.trade.model.builder.RequestBuilder;

/**
 * Created by liuyangkly on 15/10/22.
 */
abstract class AbsAlipayService {
	protected Logger log = LoggerFactory.getLogger(this.getClass());

    // 验证bizContent对象
    protected void validateBuilder(RequestBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("builder should not be NULL!");
        }

        if (!builder.validate()) {
            throw new IllegalStateException("builder validate failed! " + builder.toString());
        }
    }

    // 调用AlipayClient的execute方法，进行远程调用
	protected AlipayResponse getResponse(AlipayClient client, AlipayRequest request) {
        try {
            AlipayResponse response = client.execute(request);
            if (response != null) {
                log.info("响应报文:" + response.getBody());
            }
            return response;

        } catch (AlipayApiException e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }
}
