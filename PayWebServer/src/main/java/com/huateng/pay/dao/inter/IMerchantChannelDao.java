package com.huateng.pay.dao.inter;

import java.util.List;
import java.util.Map;

public interface IMerchantChannelDao {

	List<Map<String, Object>> queryChannel(Map<String, String> queryParams);

	List<Map<String, Object>> querySubmerChannelRateInfo(Map<String, String> queryParams);

	boolean insertSubmerChannelRate(Map<String, String> paramString);

	List<Map<String, Object>> querySubmerIsExist(Map<String, String> paramString);

	boolean updateSubmerChannelRateInfoByPrimaryKey(Map<String, String> paramString);

}
