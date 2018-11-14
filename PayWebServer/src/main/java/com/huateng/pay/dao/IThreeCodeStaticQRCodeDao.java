package com.huateng.pay.dao;

import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;

/**
 * 订单dao接口类
 *
 */

public interface IThreeCodeStaticQRCodeDao {
	/**
	 * 查询三码合一静态二维码
	 * @author zyx
	 *
	 */
	public List<Map<String, Object>> queryThreeCodeStaticQRCode(Map<String,String> queryMap) throws FrameException;
	
	/**
	 * 保存三码合一静态二维码
	 * @author zyx
	 *
	 */
	public boolean saveThreeCodeStaticQRCode(Map<String, String> saveParam) throws FrameException;
	
	/**
	 * 更新三码合一静态二维码
	 * @author zyx
	 *
	 */
	public boolean updateThreeCodeStaticQRCode(Map<String, String> updateParam) throws FrameException;
	
	/**
	 * 批量更新三码合一静态二维码
	 * @param updateParam
	 * @return
	 */
	public boolean updateBatchThreeCodeStaticQRCode(List<Map<String, String>> updateParam) throws FrameException;
	
}
