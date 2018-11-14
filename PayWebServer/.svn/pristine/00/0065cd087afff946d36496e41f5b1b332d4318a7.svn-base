package com.huateng.pay.services.db;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

public interface IThreeCodeStaticQRCodeDataService {
	/**
	 * 获取静态二维码序列号
	 * @return
	 * @throws FrameException
	 */
	public String getThreeCodeStaticQRSeqNo() throws FrameException;
	
	 /**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
    public String getThreeCodeStaticQRCodeTxnNo() throws FrameException;
    /**
	 * 查询三码合一静态二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryThreeCodeStaticQRCodeInfo(InputParam inputParam) throws FrameException;
	
	/**
	 * 更新三码合一静态二维码状态信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam updateThreeCodeStaticQRCodeStatus(InputParam inputParam) throws FrameException;
	
	/**
	 * 保存三码合一静态二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam saveThreeCodeStaticQRCodeInfo(InputParam inputParam) throws FrameException;
	
	/**
	 * 批量保存三码合一静态二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam updateBatchThreeCodeStaticQRCode(InputParam inputParam) throws FrameException;
}
