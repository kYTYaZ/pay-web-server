package com.wldk.framework.db;

import java.io.Serializable;


/**
 * 封装分页相关属性的实体类
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-11-14 <br>
 * Author: Administrator <br>
 * 
 */
public class PageVariable implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1489201351020727740L;

	/** 最大显示页数 */
    public static final int MAX_SHOW_PAGE = 20;

    /** 总记录数 */
    private int recordCount = 0;

    /** 总页数 */
    private int pageCount = 0;

    /** 每页的记录数 */
    private int recordPerPage = 10;

    /** 当前页，从1开始计数 */
    private int currentPage = 1;

    /** 当前页记录数 */
    private int recordCountCurrentPage = 10;

    /** 当前起始页 */
    private int startPage;

    /** 当前结束页 */
    private int endPage;

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * 默认构造方法
     */
    public PageVariable() {
        this.startPage = 1;
        this.endPage = MAX_SHOW_PAGE;
    }

    /**
     * 构造方法
     * 
     * @param p_RecordPerPage 每页的记录数
     * @param p_CurrentPage 当前页，从1开始计数
     */
    public PageVariable(int pRecordPerPage, int pCurrentPage) {
        this();
        if (pRecordPerPage < 0)
            throw new IllegalArgumentException("每页记录数不得小于0！");
        if (pCurrentPage < 0)
            throw new IllegalArgumentException("当前页不得小于0！");
        recordPerPage = pRecordPerPage;
        currentPage = pCurrentPage;
    }

    /**
     * 构造方法
     * 
     * @param pCurrentPage 当前页，从1开始计数
     */
    public PageVariable(int pCurrentPage) {
        this();
        if (pCurrentPage < 0)
            throw new IllegalArgumentException("当前页不得小于0！");

        currentPage = pCurrentPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    /**
     * 当前页记录数，初始是0
     * 
     * @return 当前页记录数
     */
    public int getRecordCountCurrentPage() {
        return recordCountCurrentPage;
    }

    /**
     * 当前页记录数
     * 
     * @param pRecordCountCurrentPage 当前页记录数
     */
    public void setRecordCountCurrentPage(int pRecordCountCurrentPage) {
        if (pRecordCountCurrentPage < 0)
            throw new IllegalArgumentException("当前页记录数不得小于0！");

        this.recordCountCurrentPage = pRecordCountCurrentPage;
    }

    /**
     * 总记录数，初始是0
     * 
     * @return 总记录数
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * 设置总记录数
     * 
     * @param recordCount 总记录数
     */
    public void setRecordCount(int recordCount) {
        if (recordCount < 0)
            throw new IllegalArgumentException("总记录数不得小于0！");

        this.recordCount = recordCount;
        if (recordCount == 0) {
            currentPage = 1;
            pageCount = 0;
            recordCountCurrentPage = 0;
        } 
//        else {
//            pageCount = (recordCount + recordPerPage - 1) / recordPerPage;
//            if (currentPage < 0) {
//                throw new IllegalArgumentException("当前页不得小于0！");
//            } else if (currentPage > pageCount) {
//                currentPage = pageCount;
//            }
//            if (getHasNext()) {
//                recordCountCurrentPage = recordPerPage;
//            } else {
//                recordCountCurrentPage = recordCount % recordPerPage;
//            }
//
//        }
    }

    /**
     * 总页数，初始是0
     * 
     * @return 总页数
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 设置总页数
     * 
     * @param pageCount 总页数
     */
    public void setPageCount(int pageCount) {
        if (pageCount < 0)
            throw new IllegalArgumentException("总页数不得小于0！");

        this.pageCount = pageCount;
    }

    /**
     * 每页的记录数 初始是10
     * 
     * @return 每页的记录数
     */
    public int getRecordPerPage() {
        return recordPerPage;
    }

    /**
     * 设置每页的记录数
     * 
     * @param recordPerPage 每页的记录数
     */
    public void setRecordPerPage(int pRecordPerPage) {
        if (pRecordPerPage < 0)
            throw new IllegalArgumentException("每页的记录数不得小于0！");

        this.recordPerPage = pRecordPerPage;
    }

    /**
     * 当前页，初始是0
     * 
     * @return 当前页
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页
     * 
     * @param currentPage 当前页，从1开始计数
     */
    public void setCurrentPage(int pCurrentPage) {
        if (pCurrentPage < 0)
            throw new IllegalArgumentException("当前页不得小于0！");

        this.currentPage = pCurrentPage;
    }

    /**
     * 是否有前页
     * 
     * @return
     */
    public boolean getHasPre() {
        if (this.currentPage > 1)
            return true;
        return false;
    }

    /**
     * 是否有后页
     * 
     * @return
     */
    public boolean getHasNext() {
        if (this.currentPage < this.pageCount)
            return true;
        return false;
    }

    /**
     * 获取前页
     * 
     * @return
     */
    public int getPrePage() {
        if (getHasPre())
            return this.currentPage - 1;
        return 0;
    }

    /**
     * 获取后页
     * 
     * @return
     */
    public int getNextPage() {
        if (getHasNext())
            return this.currentPage + 1;
        return 0;
    }

    /**
     * 设置起始页和结束页的状态
     * 
     * @return
     */
    public void setupStartPageAndEndPage() {
        if (this.currentPage >= 1 && this.currentPage <= MAX_SHOW_PAGE) { // 如果当前页是在第一段的范围中
            this.startPage = 1;
            this.endPage = MAX_SHOW_PAGE;
            if (this.endPage > this.pageCount) {
                this.endPage = this.pageCount;
            }
        } else {
            if (this.currentPage < this.startPage) {
                this.startPage -= MAX_SHOW_PAGE;
                this.endPage = this.startPage + MAX_SHOW_PAGE - 1;
                if (this.startPage >= 1 && this.endPage <= MAX_SHOW_PAGE) {
                }
            } else if (this.currentPage > this.endPage) {
                this.startPage = this.currentPage;
                this.endPage = this.startPage + MAX_SHOW_PAGE - 1;
                if (this.endPage > this.pageCount) {
                    this.endPage = this.pageCount;
                }
            }
        }
    }

    /**
     * 字符串描述
     * 
     * @return
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PageVariable is");
        sb.append(System.getProperty("line.separator"));
        sb.append("总记录数 is ");
        sb.append(this.recordCount);
        sb.append(System.getProperty("line.separator"));
        sb.append("总页数 is ");
        sb.append(this.pageCount);
        sb.append(System.getProperty("line.separator"));
        sb.append("每页记录数 is ");
        sb.append(this.recordPerPage);
        sb.append(System.getProperty("line.separator"));
        sb.append("当前页 is ");
        sb.append(this.currentPage);
        sb.append(System.getProperty("line.separator"));
        sb.append("当前起始页 is ");
        sb.append(this.startPage);
        sb.append(System.getProperty("line.separator"));
        sb.append("当前结束页 is ");
        sb.append(this.endPage);
        sb.append("signature is");
        sb.append(signature);
        return sb.toString();
    }
}