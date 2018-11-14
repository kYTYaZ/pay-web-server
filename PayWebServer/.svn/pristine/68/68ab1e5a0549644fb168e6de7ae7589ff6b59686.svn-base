/**
 * 
 */
package com.wldk.framework.db.parse;

import com.wldk.framework.db.Parameter;
import com.wldk.framework.db.ParameterProvider;


/**
 * Velocity解析器的默认实现<br>
 * 
 * @author Administrator
 * 
 */
public class VelocitySQLParse extends AbstractVelocitySQLParse {
	/**
	 * 构造方法
	 * 
	 * @param filename
	 *            模板文件名称
	 * @throws Exception
	 */
	public VelocitySQLParse(String filename) throws Exception {
		super(filename);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构造方法
	 * 
	 * @param parameters
	 *            参数提供器
	 * @param filename
	 *            模板文件名称
	 * @throws Exception
	 */
	public VelocitySQLParse(ParameterProvider parameters, String filename)
			throws Exception {
		super(parameters, filename);
		// TODO Auto-generated constructor stub
	}

	/*
	 * 将参数传递到模板上
	 * 
	 * @see com.bankcomm.data.parse.VelocitySQLParse#setParameters()
	 */
	
	public void setParameters() throws Exception {
		// TODO Auto-generated method stub
		ParameterProvider parameters = getParameters();
		if (parameters != null) {
			for (Parameter param : parameters.getParameters()) {
				if (param != null) {
					// 设置参数
					getContext().put(param.getField(), param.getValue());
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
