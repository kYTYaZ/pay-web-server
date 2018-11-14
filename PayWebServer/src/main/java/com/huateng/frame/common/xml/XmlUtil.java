/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: XmlUtil.java
 * Author:   justin
 * Date:     2014-7-31 下午6:02:53
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.frame.common.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.java2xml.Java2XmlObject;

/**
 * 对象和xml之间相互转换工具类
 * 
 * @author sunguohua
 */
public class XmlUtil {
    private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    /**
     * 
     * 对象转xml方法
     * 
     * @param obj 需要转的对象
     * @param fileName freemarker模板文件名
     * @return
     */
    public static String bean2Xml(Object obj, String fileName) {
        Java2XmlObject java2Xml = new Java2XmlObject();
        String resultXml = "";
        try {
            resultXml = java2Xml.fromJavaToXml(obj, fileName);
        } catch (FrameException e) {
            logger.error("对象转xml异常：" + e.getMessage(), e);
        }
        return resultXml;
    }
    
    /**
     * 
     * xml字符串转对象
     * @param obj  要转的对象
     * @param xmlStr xml字符串
     * @return
     * @see 1.0
     * @since 1.0
     */
    public static Object xml2Bean(Object obj, String xmlStr) {
        Java2XmlObject java2Xml = new Java2XmlObject();
        Object resultObj = null;
        try {
            resultObj = java2Xml.fromXmlToJava(obj, xmlStr);
        } catch (FrameException e) {
            logger.error("xml转对象异常：" + e.getMessage(), e);
        }
        return resultObj;
    }

}
