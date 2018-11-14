package com.huateng.pay.services.db;

import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

public interface IStaticQRCodeDataService {
	/**
	 * 保存静态二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam saveStaticQRCodeInfo(InputParam inputParam) throws FrameException;

	/**
	 * 查询静态二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryStaticQRCodeInfo(InputParam inputParam) throws FrameException;
	
	/**
	 * 更新静态二维码状态信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam updateStaticQRCodeStatus(InputParam inputParam) throws FrameException;

	/**
	 * 获取静态二维码序列号
	 * @return
	 * @throws FrameException
	 */
	public String getStaticQRSeqNo() throws FrameException;
	
	 /**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
    public String getStaticQRCodeTxnNo() throws FrameException;

    /**
     * 查询静态二维码信息（返回list）
     * @param inputParam
     * @return
     * @throws FrameException
     */
    public List<Map<String, Object>> queryStaticQRCodeList(InputParam inputParam) throws FrameException;
}
