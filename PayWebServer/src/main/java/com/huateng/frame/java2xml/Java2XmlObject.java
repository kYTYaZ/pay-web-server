/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: Java2XmlObject.java
 * Author:   justin
 * Date:     2014-7-31 下午3:56:20
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.frame.java2xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.huateng.frame.common.TransFormat;
import com.huateng.frame.exception.FrameException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * java对象转xml字符串 该类提供普通java的bean对象以及map对象，将其转换为xml格式的报文格式 xml报文格式通过定义freemarker模板中xml模板文件。 将java对象的数据动态填充至模板文件中
 * 以及提供xml格式的文件转换为普通的java对象，或者Map对象
 * 
 * @author sunguohua
 */
public class Java2XmlObject {
    private Configuration cfg;

    private String filePath = "/xmltemplates";
    public Java2XmlObject() {
        cfg = new Configuration();
    }

    /**
     * 
     * 将java的对象转换为xml格式数据
     * 
     * @param bean 定义的java对象
     * @param fileName xml文件名称
     * @return
     * @throws FrameException
     */
    public String fromJavaToXml(Object bean, String fileName) throws FrameException {
        String result = "";
        try {
            cfg.setClassForTemplateLoading(this.getClass(), filePath);
            Template template = cfg.getTemplate(fileName);
            StringWriter sw = new StringWriter();
            if (bean instanceof Map) {
                template.process(bean, sw);
            } else {
                Map<String, Object> data = TransFormat.bean2Map(bean,2000);
                template.process(data, sw);
            }
            result = sw.toString();
        } catch (TemplateException e) {
            throw new FrameException("freemarker模板异常");
        } catch (IOException e) {
            throw new FrameException("文件找不到:" + filePath);
        }
        return result;

    }

    /**
     * 
     * 将xml解析为java对象
     * 
     * @param obj 要转换的对象
     * @param xmlStr 字符串
     * @return
     * @throws FrameException
     * @see 1.0
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Object fromXmlToJava(Object obj, String xmlStr) throws FrameException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(new ByteArrayInputStream(xmlStr.getBytes("utf-8")));
            Element element = doc.getRootElement();// 根节点
            List<Element> childElements = element.getChildren();
            Method[] methods = obj.getClass().getDeclaredMethods(); // 对象自定义的方法
            for (Element e : childElements) {
                String key = e.getName();
                String value = e.getValue();
                for (Method me : methods) {
                    String meName = me.getName();
                    if (meName.contains("set") && key.equals(meName.substring(3, meName.length()).toLowerCase())) {
                        me.invoke(obj, new Object[] { value });
                    }
                }
            }
        } catch (JDOMException e) {
            throw new FrameException("xml转对象出错:" + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new FrameException("xml转对象出错:" + e.getMessage());
        } catch (IOException e) {
            throw new FrameException("xml转对象出错:" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new FrameException("xml转对象出错:" + e.getMessage());
        }
        return obj;
    }
}
