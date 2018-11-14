package com.huateng.pay.dao.inter;

import java.util.Map;

public interface ITblMerTradeLimitDao {

	/**
	 * 查询当日限额与当月限额
	 * @param paramMap
	 */
	public Map<String, Object>  queryLimitByDtAndAcctNo(Map<String, String> paramMap);
	
	/**
	 * 根据时间与卡号查询信息
	 * @param paramMap
	 */
	public Map<String, Object>  queryByDtAndAcctNo(Map<String, String> paramMap);
	
	/**
	 * 根据时间与卡号查询信息
	 * @param paramMap
	 */
	public Map<String, Object>  queryByAcctNo(Map<String, String> paramMap);
	
	/**
	 * 根据时间与卡号查询当月限额
	 * @param paramMap
	 */
	public Map<String, Object>  queryMonthAmtByDtAndAcctNo(Map<String, String> paramMap);
	
	/**
	 * 新增限额信息
	 */
	public boolean insertLimitTbl(Map<String, String> paramMap);
	
	/**
	 * 更新限额信息
	 */
	public boolean updateLimitTbl(Map<String, String> paramMap);
	
	/**
	 * 删除上个月的限额信息
	 */
	public void deleteLimitTbl(Map<String, String> deleteMap);
}
