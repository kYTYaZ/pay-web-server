/**
 * 
 */
package com.wldk.framework.db.parse;


/**
 * SQL解析器接口，用于解析SQL查询字符串<br>
 * 
 * @author Administrator
 * 
 */
public interface SQLParse {
	/** 解析方法 */
	String parse() throws Exception;
}
