package com.huateng.pay.po.notify;

import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.xml.bind.UnmarshalException;

import com.huateng.pay.po.notify.NotifyMessage.OnlineNotifyMessage;

public class NotifyExceptionRety {
	
	public boolean retryNotify(Throwable ex,int retryCount,Object obj){
		
		if(obj instanceof OnlineNotifyMessage){
			return false;
		}
		
		if(ex instanceof IllegalArgumentException){
			return  false;
		}
		
		if(ex instanceof IllegalStateException){
			return  false;
		}
		
		if(ex instanceof NullPointerException){
			return  false;
		}
		
		if(ex instanceof UnmarshalException){
			return  false;
		}
		
		if(ex instanceof SocketTimeoutException  || ex instanceof SocketException){
			return !(retryCount > 2);
		}
		
		if(ex instanceof InterruptedIOException){
			return  true;
		}
		
		if(ex instanceof Exception){
			return  true;
		}
		
		return false;
		
	}
}
