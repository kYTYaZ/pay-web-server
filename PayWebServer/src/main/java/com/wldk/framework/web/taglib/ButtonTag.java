package com.wldk.framework.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ButtonTag extends TagSupport{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8005197736914719435L;
	/** 回调函数 */
    private String onclick;
    private String buttonName;
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
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
        return sb.toString();
    }

    protected String icludeHtml() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(" <li>");
        sb.append("<a class=\"new_button\"  onclick=\""+onclick+"\">");
        sb.append("<span>");
        sb.append(" <img align=\"absMiddle\"  src="+imgUrl+"  complete=\"complete\" />");
        sb.append(buttonName);
        sb.append("</span>");
        sb.append("</a>");
        sb.append("</li>");
        
        return sb.toString();
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }
}
