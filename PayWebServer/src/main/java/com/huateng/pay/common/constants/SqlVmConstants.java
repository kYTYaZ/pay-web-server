package com.huateng.pay.common.constants;

/**
 * 
 * sql模板常量名类
 * 
 * @author sunguohua
 */
public final class SqlVmConstants {

    public interface Order {
        public final String ORDER_001 = "SQL_order_001.vm";
    }
    
    public interface C2BOrder {
        public final String C2B_ORDER_001 = "SQL_cups_order_001.vm";
    }
    public interface C2BOtherOrder {
    	public final String C2B_ORDER_OTHER_001 = "SQL_cups_order_other_001.vm";
    }

    public interface Common {
        public final String COMMON_001 = "SQL_common_001.vm";
    }
    
    public interface Sequence{
        public final String SEQ_001 = "SQL_sequence_001.vm";
    }
    
    public interface Bind{
    	public final String BIND_001 = "SQL_bind_001.vm";
    }
    
    public interface QRCode{
    	public final String QRCode_001 = "SQL_QRCode_001.vm";
    }
    
    public interface TimingTask{
    	public final String TIMING_TASK_001 = "SQL_timingtask_001.vm";
    }
    
    public interface TakeTimingTaskResult{
    	public final String TAKE_TIMING_TASK_RESULT_001 = "SQL_taketimingtask_result_001.vm";
    }
    
    public interface StaticQRCode{
    	public final String Static_QRCode_001 = "SQL_StaticQRCode_001.vm";
    	public final String Static_QRCode_002 = "SQL_ThreeCodeStaticQRCode_001.vm";
    }
    
    public interface LimitTable{
    	public final String Trade_Limit_001="SQL_Limit_001.vm";
    }
    /**
     * 机构号对应渠道sql文本
     * @author Administrator
     *
     */
	public interface MerChantChannel {
		String MERCHANT_CHANNEL_001 = "SQL_merchant_channel_001.vm";
	}
}
