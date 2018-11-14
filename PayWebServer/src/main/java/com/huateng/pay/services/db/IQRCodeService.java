package com.huateng.pay.services.db;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

public interface IQRCodeService {
	/**
	 * 保存二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam saveQRCodeInfo(InputParam inputParam) throws FrameException;

	/**
	 * 查询二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryQRCodeInfo(InputParam inputParam) throws FrameException;
	
	/**
	 * 更新二维码状态信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam updateQRCodeStatus(InputParam inputParam) throws FrameException;

	/**
	 * 获取二维码序列号
	 * @return
	 * @throws FrameException
	 */
	public String getQRSeqNo() throws FrameException;
}
