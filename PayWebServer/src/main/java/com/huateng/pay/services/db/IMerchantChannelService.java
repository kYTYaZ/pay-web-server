package com.huateng.pay.services.db;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

public interface IMerchantChannelService {

	OutputParam queryMerchantChannel(InputParam inputParam);
	
	OutputParam querySubmerChannelRateInfo(InputParam inputParam);

	OutputParam insertSubmerChannelRate(InputParam insertInput);

	OutputParam querySubmerIsExist(InputParam queryInput);
	
	OutputParam updateSubmerChannelRateInfoByPrimaryKey(InputParam insertInput);
	
}
