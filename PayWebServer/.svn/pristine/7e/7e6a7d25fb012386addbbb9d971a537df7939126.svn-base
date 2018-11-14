package com.huateng.frame.exception;
/**
 * 
 * 自定义框架异常类 
 * 插件的异常均抛出该类
 * @author sunguohua
 */
public class FrameException extends RuntimeException {
private static final long serialVersionUID = -6947666243476697869L;
    /**
     * 空构造方法
     */
    public FrameException() {
        super("调用服务出错。。。");
    }
    
    /**
     * 自定义错误日志
     * @param e
     */
    public FrameException(String e){
        super(e);
    }
    
    /**
     * 抛出错误信息
     * @param e
     */
    public FrameException(Throwable e){
        super(e);
    }
    
    /**
     * 抛出自定义错误日志和错误信息
     * @param er
     * @param e
     */
    public FrameException(String er,Throwable e){
        super(er, e);
    }
}
