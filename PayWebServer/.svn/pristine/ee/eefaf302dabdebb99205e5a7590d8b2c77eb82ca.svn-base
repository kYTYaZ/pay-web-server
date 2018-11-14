package com.huateng.pay.control;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.control.ICommonService;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;

/**
 * 此方法为统一的webservice出口类 支付网关后台往外部公布的接口只有这一个
 *
 * @author sunguohua
 */
public class CommonServiceImpl implements ICommonService, BeanFactoryAware, Serializable {
    private static final long serialVersionUID = -2344854762441712580L;

    private BeanFactory factory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.factory = beanFactory;
    }

    /*
     * (non-Javadoc)
     * @see com.huateng.frame.control.ICommonService#executeService(java.lang.String)
     */
    @Override
    public String executeService(String inputJson) {
        try {
            InputParam inputParam = (InputParam) JsonUtil.json2Bean(inputJson, InputParam.class);
            String serviceNm = inputParam.getServiceName();
            String serviceMethod = inputParam.getServiceMethod();
            Object serviceObject = factory.getBean(serviceNm);
            Method method = serviceObject.getClass().getMethod(serviceMethod, InputParam.class);
            OutputParam outputParam = (OutputParam) method.invoke(serviceObject, inputParam);
            return JsonUtil.bean2Json(outputParam);
        } catch (Exception e) {
            OutputParam outputParam = new OutputParam();
            outputParam.setReturnCode(StringConstans.returnCode.FAIL);
            outputParam.setReturnMsg("交易异常");
            return JsonUtil.bean2Json(outputParam);
        }
    }

}
