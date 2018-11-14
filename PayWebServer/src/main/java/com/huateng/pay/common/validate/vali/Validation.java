package com.huateng.pay.common.validate.vali;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;


public class Validation {
	
	private static Logger logger = LoggerFactory.getLogger(Validation.class);
	
	public static OutputParam validate(InputParam input, String[] ary,
			String validateName) {
		OutputParam out = new OutputParam();
		for (int i = 0; i < ary.length; i++) {
			String key = ary[i];
			String value = StringUtil.toString(input.getValue(key));
			if (StringUtil.isEmpty(value)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(key+"不能为空");
				break;
			}
		}
		
		if(!StringConstans.returnCode.FAIL.equals(out.getReturnCode())){
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			out.setReturnMsg(validateName+"字段校验通过");
		}
		logger.info(validateName+"字段校验返回报文:"+out.toString());
		return out;
	}

}
