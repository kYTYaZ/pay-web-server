package com.huateng.pay.dao.inter;

import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;

public interface IQRCodeDao {
    /**
	 * 保存二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public boolean saveQRCodeInfo(Map<String, String> orderParam) throws FrameException;

	/**
	 * 查询二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public List<Map<String, Object>> queryQRCodeInfo(Map<String, String> orderParam) throws FrameException;
	
	/**
	 * 更新二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public boolean updateQRCodeInfo(Map<String, String> orderParam) throws FrameException;
	
}
