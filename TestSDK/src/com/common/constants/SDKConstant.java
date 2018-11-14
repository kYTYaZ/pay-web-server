package com.common.constants;

public class SDKConstant {

	/**
	 * �ַ�����
	 */
	public final static String UTF8 = "UTF-8";
	/**
	 * ����
	 */
	public final static String currencyCode = "156";

	/**
	 * ״̬��
	 * 
	 * @author Administrator
	 * 
	 */
	public interface TXN_STA {
		/**
		 * ��ʼ
		 */
		public static String STA_01 = "01";
		/**
		 * �ɹ�
		 */
		public static String STA_02 = "02";
		/**
		 * ʧ��
		 */
		public static String STA_03 = "03";
		/**
		 * ������
		 */
		public static String STA_06 = "06";

	}

	/**
	 * �������
	 * 
	 * @author Administrator
	 * 
	 */
	public interface TXN_CODE {
		/**
		 * C2B���ɶ�ά��
		 */
		public static String CODE_1005 = "1005";
		/**
		 * C2B��ɨ��ѯ������ά��
		 */
		public static String CODE_1009 = "1009";
		/**
		 * C2B���ѽӿ�
		 */
		public static String CODE_9001 = "9001";
		/**
		 * C2B���ѳ����ӿ�
		 */
		public static String CODE_9002 = "9002";
		/**
		 * C2B��ѯ
		 */
		public static String CODE_9003 = "9003";
		/**
		 * ���ռҲ�ѯ�����һ��ˮ
		 */
		public static String CODE_3006 = "3006";
		/**
		 * ΢���̻�����
		 */
		public static String CODE_5001 = "5001";
		/**
		 * ֧�����̻�����
		 */
		public static String CODE_8003 = "8003";
		/**
		 * ����΢�Ŷ��˵�
		 */
        public static String WX_BILL_DOWN = "3002";
        
		/**
		 * ����΢�Ŷ��˵� ��������
		 */
		public static String WX_BILL_SINGLE_DOWN = "3012";

		/**
		 * ����΢�Ŷ��˵��ϳ�
		 */
		public static String WX_BILL_SUM_DOWN = "3013";
		
		/**
		 * ����΢�Ŷ��˵��ϳ�
		 */
		public static String MORE_FEE_CONFIG = "3014";
        /**
         * ֧�������˵�����
         */
        public static String ALIPAY_BILL_DOWN = "8011";
        /**
         * C2B������ά����ɨӪ����ѯ
         */
        public static String C2BEWM_MARKET_QUERY = "1010";
        /**
		 * C2B��ɨ����
		 */
		public static String CODE_4002 = "4002";

	}

	/**
	 * 
	 * �������ͣ������ཻ�׽��롢΢���ཻ�׽��룩
	 * 
	 */
	public interface PAYACCESSTYPE {
		/**
		 * ����֧������
		 */
		public static String ACCESS_NATIVE = "01";
		/**
		 * ΢��֧������
		 */
		public static String ACCESS_WX = "02";
		/**
		 * ֧��������
		 */
		public static String ACCESS_ALIPAY = "03";
		/**
		 * ���ջ���ɨһ��ͨ
		 */
		public static String ACCESS_FSHL = "04";
		/**
		 * ������ά��
		 */
		public static String ACCESS_CUPS = "05";
	}
	
	/**
	 * 
	 * socketǰ׺
	 * 
	 */
	public interface PREFIX {
		/**
		 * �����һ��ѯ΢�ź�֧��������ǰ׺
		 */
		public static String TC_QUERY_ORDER = "unknow";
		/**
		 * ����ķ������˽���ǰ׺
		 */
		public static String T0_ACCOUNTED = "settle";
	}
	
	public interface ByMerIdOrSubmerId {
		public static String MERID = "MERID";
		public static String SUBMERID = "SUBMERID";
	}

}
