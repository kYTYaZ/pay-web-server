package com.validate;

import com.common.dicts.Dict;

public class C2BValidation {

	// ��������У���ֶ�
	public static String[] vali_reverse = { Dict.respCode, Dict.respDesc,
			Dict.settleKey, Dict.settleDate };
	// ��ѯ����У���ֶ�
	public static String[] vali_query = { Dict.respCode, Dict.respDesc,
			Dict.origRespCode, Dict.origRespMsg, Dict.settleKey,
			Dict.settleDate, Dict.voucherNum, Dict.accNo, Dict.cardAttr,
			Dict.issCode };
	// C2B���ѷ���У���ֶ�
	public static String[] vali_consume = { Dict.respCode, Dict.respDesc,
			Dict.orderNo, Dict.orderTime, Dict.settleKey, Dict.settleDate,
			Dict.voucherNum, Dict.accNo, Dict.cardAttr };

}
