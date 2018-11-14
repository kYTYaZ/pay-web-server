package com.huateng.pay.services.scheduler;

import com.huateng.frame.param.InputParam;


public interface ISchedulerService 
{
   
   /**
    * 发送请求到微信下载对账单
    */
   public void downloadWxBill(); 
   
   /**
	 * 定时从二维码信息将数据拷贝到历史表
	 */
   public void timingCopyTblEwmInfoToHis() ;
	/**
	 * 定时删除二维码历史表中的数据
	 */
   public void timingDelteTblEwmInfo() ;
	/**
	 *  定时从订单表信息将数据拷贝到历史表
	 */
   public void timingCopyTblOrderInfoToHis() ;
	/**
	 * 定时删除订单表中的历史数据
	 */
   public void timingDelteTblOrderInfo() ;
   
   /**
    * 发送请求到支付宝下载对账单
    */
   public void downloadAliPayBill(); 
   
   /**
    * 定时查询01和06状态的订单
    */
   public void queryIndefiniteOrder(InputParam input);
   
   /**
    * 定时查询01
    * @param input
    */
   public void queryRefundOrder();
   
   /**
    * 定时生成三码合一对账单
    */
   public void creatThreeCodeWxAndAipayBills();
   
   /**
    * 下载三码合一订单信息
    */
   public void downLodaThreeCodeOrder();
   
   /**
    * 打包七天前日志并压缩
    */
   public void packLogByZip();
   
   /**
    * 查询一码通入账状态未知的交易，并重新发起入账
    */
   public void queryUnknowStautsAndSettle(InputParam input);
   
}
