/**
 * Special Declaration: These technical material reserved as the technical 
 * secrets by Bankit TECHNOLOGY have been protected by the "Copyright Law" 
 * "ordinances on Protection of Computer Software" and other relevant 
 * administrative regulations and international treaties. Without the written 
 * permission of the Company, no person may use (including but not limited to 
 * the illegal copy, distribute, display, image, upload, and download) and 
 * disclose the above technical documents to any third party. Otherwise, any 
 * infringer shall afford the legal liability to the company.
 *
 * 特别声明：本技术材料受《中华人民共和国著作权法》、《计算机软件保护条例》
 * 等法律、法规、行政规章以及有关国际条约的保护，浙江宇信班克信息技术有限公
 * 司享有知识产权、保留一切权利并视其为技术秘密。未经本公司书面许可，任何人
 * 不得擅自（包括但不限于：以非法的方式复制、传播、展示、镜像、上载、下载）使
 * 用，不得向第三方泄露、透露、披露。否则，本公司将依法追究侵权者的法律责任。
 * 特此声明！
 *
 * Copyright(C) 2013 Bankit Tech, All rights reserved.
 */

/*
 * com.ecc.ebcd.EBCD.java
 * Created by dcz @ 2013-7-20 下午1:09:46
 */

package com.huateng.utils;

/**
* <DL>
* <DT><B> 标题. </B></DT>
* <p>
* <DD>详细介绍</DD>
* </DL>
* <p>
* 
* <DL>
* <DT><B>使用范例</B></DT>
* <p>
* <DD>使用范例说明</DD>
* </DL>
* <p>
* 
* @author dcz $Author$
* @author 浙江宇信班克信息技术有限公司
* @version $Id$
*/
public class EBCD
{

    private int k;

    private int v;

    /**
     * @param k
     * @param v
     */
    public EBCD(int k, int v)
    {
        super();
        this.k = k;
        this.v = v;
    }

    /**
     * @return 返回字段k的值.
     */
    public int getK()
    {
        return k;
    }

    /**
     * @param k 用以设置字段k的值.
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * @return 返回字段v的值.
     */
    public int getV()
    {
        return v;
    }

    /**
     * @param v 用以设置字段v的值.
     */
    public void setV(int v)
    {
        this.v = v;
    }
}
