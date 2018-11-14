package com.huateng.pay.services.bill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.util.StringUtil;

public class Dispatcher {

	private List<FileHandler> handlers = new ArrayList<FileHandler>(8);

	public void record(Map<String, Object> order) {

		try {
			for (FileHandler fh : handlers) {
				fh.record(order);
			}
		} catch (Exception e) {
			throw new FrameException("对账单生成异常");
		}
	}

	public void doneWork() {
		try {
			for (FileHandler fh : handlers) {
				fh.doneWorkWithDeepSigh();
			}
		} catch (IOException e) {
			throw new FrameException("对账单生成异常");
		}
	}

	public void addTargetFile(FileHandler fileHandler) {

		if (StringUtil.isEmpty(fileHandler)) {
			throw new NullPointerException("file handler is null");
		}
		this.handlers.add(fileHandler);
	}

	public void removeTargetFile(FileHandler fileHandler) {
		if (StringUtil.isEmpty(fileHandler)) {
			throw new NullPointerException("file handler is null");
		}
		this.handlers.remove(fileHandler);
	}

	public void addWxAndAlipay(String billDate) {

		FileHandler alipayT0File = FileHandler.createAlipayT0File(billDate);
		FileHandler alipayT1File = FileHandler.createAlipayT1File(billDate);
		FileHandler wxT0File = FileHandler.createWxT0File(billDate);
		FileHandler wxT1File = FileHandler.createWxT1File(billDate);

		handlers.add(alipayT0File);
		handlers.add(alipayT1File);
		handlers.add(wxT0File);
		handlers.add(wxT1File);
	}

	public void createEmptyFile() {

		try {
			for (FileHandler fh : handlers) {
				fh.usedWhenThereisNoContextToRecord();
			}
		} catch (Exception e) {
			throw new FrameException("对账单生成异常");
		}

	}

}
