/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: UUID.java
 * Author:   justin
 * Date:     2014-8-7 上午11:31:35
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.common.util;

import com.huateng.frame.common.date.DateUtil;

/**
 * 生成唯一的订单号
 * 
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public class UUID {

    private static int seq = 10000000;

    public String getSysRefCode() {
        String dateStr = DateUtil.getDDHHMMSS();
        return dateStr + getSeq();
    }

    private synchronized static int getSeq() {
        if (seq >= 9999) {
            seq = 1000;
        }
        return ++seq;
    }

    public static String getUUID(int length) {
        String formatPattern = "";
        switch (length) {
            case 10: // 加上四位流水号
                formatPattern = DateUtil.HHMMSS;
                break;
            case 19:
                formatPattern = DateUtil.YYMMDDHHMMSSSSS;
                break;
            case 21:
                formatPattern = DateUtil.YYYYMMDDHHMMSSSSS;
                break;
            case 8:
                formatPattern = DateUtil.YYYYMMDD;
                break;
            default:
                formatPattern = DateUtil.YYMMDD;
                break;
        }
        String dateStr = DateUtil.getDateStr(formatPattern);
        return dateStr + getSeq();
    }

    public synchronized static String getUUID() {
        return String.valueOf(getSeq());
    }
}
