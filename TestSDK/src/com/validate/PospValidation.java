package com.validate;

import com.common.dicts.Dict;

public class PospValidation {

	// posp�˿�
	public static final String[] vali_PospRefund = { Dict.txnSeqId, Dict.txnTime, Dict.outRefundNo, Dict.refundAmount,
			Dict.initTxnSeqId, Dict.initOrderNumber, Dict.refundStatus, Dict.msg };

	// posp�˿��ѯ
	public static final String[] vali_PospRefundQuery = { Dict.txnSeqId, Dict.txnTime, Dict.outRefundNo,
			Dict.refundAmount, Dict.initTxnSeqId, Dict.refundStatus, Dict.msg, Dict.refundReason };

}
