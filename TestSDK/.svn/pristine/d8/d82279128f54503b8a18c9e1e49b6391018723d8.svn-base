package com.trade.sycntest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mer {
	private String merId;
	private String merName;
	
	public Mer(String merId) {
		this.merId = merId;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getMerName() {
		return merName;
	}
	public void setMerName(String merName) {
		this.merName = merName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((merId == null) ? 0 : merId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mer other = (Mer) obj;
		if (merId == null) {
			if (other.merId != null)
				return false;
		} else if (!merId.equals(other.merId))
			return false;
		return true;
	}
	
	
	private synchronized void tt() {
		try {
			System.out.println("start");
			Thread.sleep(1000*10);
			ttt();
			System.out.println("end");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ttt() {
		System.out.println("込込込込");
		Thread t = Thread.currentThread();
		System.out.println(t.getId());
	}
	
	public static Integer v() {
		Integer itt = 5;
		try {
			 return itt=6;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return itt=7;
		} finally {
			return itt=8;
		}
	}
	
	public static void main(String[] args) {

		 try {
			 System.out.println(v());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
