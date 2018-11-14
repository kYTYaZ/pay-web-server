/*@(#)
 * 
 * Project: bcas_pose
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   Administrator        2013-2-27        first release
 *
 * 
 * Copyright Notice:
 * =============================================================================
 *       Copyright 2013 Huateng Software, Inc. All rights reserved.
 *
 *       This software is the confidential and proprietary information of
 *       Shanghai HUATENG Software Co., Ltd. ("Confidential Information").
 *       You shall not disclose such Confidential Information and shall use it
 *       only in accordance with the terms of the license agreement you entered
 *       into with Huateng.
 *
 * Warning:
 * =============================================================================
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class IncludeHtmlTag extends TagSupport {
	/**
	 * <code>serialVersionUID</code> 的注释
	 */
	private static final long serialVersionUID = -2847978397079808458L;

	/** 日志 */
//	private Logger log = Logger.getLogger(this.getClass());

//	/** 分页对象 */
//	private Pagination page;
//
//	/** 回调函数 */
//	private String callbackFunction;
//
//	private ResourceBundle res;
//
//	public String getCallbackFunction() {
//		return callbackFunction;
//	}
//
//	public void setCallbackFunction(String callbackFunction) {
//		this.callbackFunction = callbackFunction;
//	}

//	public int doStartTag() throws JspException {
//		JspWriter writer = pageContext.getOut();
//		try {
//				try {
//					writer.write(getHtml());
//				} catch (Exception de) {
//					throw new JspException(de);
//				}
//		} catch (JspException e) {
//			log.error(e);
//		}
//
//		return SKIP_BODY;
//	}
    
    
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().write(getHtml());
            return SKIP_BODY;
        } catch (Exception e) {
            throw new JspException(e.toString());
        }
    }
    
    
    

	protected String getHtml() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(icludeHtml());
		sb.append(createFirstPageHtml());
		return sb.toString();
	}

	protected String icludeHtml() throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\"> ");
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> ");
		return sb.toString();
	}

	protected String createFirstPageHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE8\" /> ");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" /> ");
		sb.append("<meta http-equiv=\"pragma\" content=\"no-cache\"> ");
		sb.append("<meta http-equiv=\"cache-control\" content=\"no-cache\"> ");
		sb.append("<meta http-equiv=\"expires\" content=\"0\"> ");
		// log.debug(sb.toString());
		return sb.toString();
	}
//
//	protected String createPreviousPageHtml() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<div id=\"page-prev\">");
//		if (page.getRecordCount() <= 0 || page.getPageCount() <= 1 || page.getCurrentPage() == 1) {
//			sb.append("<input id=\"page-prev-disabled-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("pre.page") + "\" disabled ");
//		} else {
//			sb.append("<input id=\"page-prev-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("pre.page") + "\" ");
//		}
//		sb.append("onclick=\"turnPage(" + page.getPrePage() + "," + callbackFunction + ")\"/>");
//		sb.append("</div>");
//		return sb.toString();
//	}
//
//	protected String createNextPageHtml() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<div id=\"page-next\">");
//		if (page.getRecordCount() <= 0 || page.getCurrentPage() == page.getPageCount()) {
//			sb.append("<input id=\"page-next-disabled-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("next.page") + "\" disabled ");
//		} else {
//			sb.append("<input id=\"page-next-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("next.page") + "\" ");
//		}
//		sb.append("onclick=\"turnPage(" + page.getNextPage() + "," + callbackFunction + ")\"/>");
//		sb.append("</div>");
//		return sb.toString();
//	}
//
//	protected String createLastPageHtml() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("<div id=\"page-last\">");
//		if (page.getRecordCount() <= 0 || page.getCurrentPage() == page.getPageCount()) {
//			sb.append("<input id=\"page-last-disabled-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("last.page") + "\" disabled ");
//		} else {
//			sb.append("<input id=\"page-last-btn\" type=\"button\" " + "onfocus=\"this.blur()\" title=\"" + res.getString("last.page") + "\" ");
//		}
//		sb.append("onclick=\"turnPage(" + page.getPageCount() + "," + callbackFunction + ")\"/>");
//		sb.append("</div>");
//		return sb.toString();
//	}
//
//	/**
//	 * 显示共多少条记录用的html字符串
//	 * 
//	 * @return
//	 */
//	protected String createRecordCountPageHtml() {
//		StringBuffer sb = new StringBuffer();
//		// sb.append("<div id=\"page-total\">Total: " + page.getRecordCount()
//		// + " record(s)</div>");
//		sb.append("<div id=\"page-total\">" + res.getString("total.recordcount") + ": <b>" + page.getRecordCount() + "</b> " + res.getString("tail") + "</div>");
//		return sb.toString();
//	}
//
//	/**
//	 * 显示每页记录数用的html字符串
//	 * 
//	 * @return
//	 */
//	protected String createRecordPerPageHtml() {
//		StringBuffer sb = new StringBuffer();
//		// sb.append("<div id=\"page-total\">Per Page: " + page.getRecordCount()
//		// + " record(s)</div>");
//		// 计算当前选择的记录数
//		int selectedCount = 0;
//		Object obj = ActionContext.getContext().getActionInvocation().getAction();
//		if (obj != null && (obj instanceof BaseAction)) {
//			BaseAction action = (BaseAction) obj;
//			String selected = action.getAlreadySelected();
//			if (selected != null && !selected.equals("")) {
//				selectedCount = StringUtils.split(selected, ",").length;
//			}
//		}
//		sb.append("<div id=\"page-total\">(" + res.getString("per.page") + ": <b>" + page.getRecordPerPage() + "</b> " + res.getString("tail") + "," + res.getString("seleced.count") + ": <b>"
//				+ selectedCount + "</b>" + res.getString("tail") + ")</div>");
//		sb.append("<input type=\"hidden\" name=\"page.recordCount\" value=\"" + page.getRecordCount() + "\"/>");
//		sb.append("<input type=\"hidden\" name=\"page.signature\" value=\"" + page.getSignature() + "\"/>");
//		sb.append("<input type=\"hidden\" name=\"page.pageCount\" value=\"" + page.getPageCount() + "\"/>");
//		return sb.toString();
//	}

	public static void main(String[] args) throws Exception {
		ResourceBundle res = ResourceBundle.getBundle("com.wldk.framework.web.taglib.resources");
		System.out.println(res.getString("page"));
	}
}
