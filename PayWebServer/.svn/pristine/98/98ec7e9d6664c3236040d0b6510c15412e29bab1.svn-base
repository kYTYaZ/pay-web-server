package com.wldk.framework.exception;

public class BassException extends RuntimeException {
    /**
     */
    private static final long serialVersionUID = 9003763200624128778L;

    //异常代码   
    private String key;  
      
    private Object[] values;//一些其他信息   
      
    public BassException() {  
        super();  
    }  
  
    public BassException(String message, Throwable throwable) {  
        super(message, throwable);  
    }  
  
    public BassException(String message) {  
        super(message);  
    }  
  
    public BassException(Throwable throwable) {  
        super(throwable);  
    }  
      
    public BassException(String message,String key){  
        super(message);  
        this.key = key;  
    }  
      
    public BassException(String message,String key,Object value){  
        super(message);  
        this.key = key;  
        this.values = new Object[]{value};  
    }  
      
    public BassException(String message,String key,Object[] values){  
        super(message);  
        this.key = key;  
        this.values = values;  
    }  
  
    public String getKey() {  
        return key;  
    }  
  
    public Object[] getValues() {  
        return values;  
    } 
}
