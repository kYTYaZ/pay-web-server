package com.validate.common;

import java.io.IOException;
import java.io.InputStream;

public class DownloadLimiter extends InputStream{
	private InputStream is = null;
	private BandwidthLimiter bandwidthLimiter;
	
	public DownloadLimiter(InputStream is ,BandwidthLimiter bankBandwidthLimiter){
		this.is = is;
		this.bandwidthLimiter = bankBandwidthLimiter;
	}
	
	@Override
	public int read() throws IOException {
		if(this.bandwidthLimiter != null){
			this.bandwidthLimiter.limitNextBytes();
			return this.is.read();
		}
		return 0;
	}
	
	public int read(byte b[],int off,int len) throws IOException{
		if(bandwidthLimiter != null){
			bandwidthLimiter.limitNextBytes(len);
		}
		return this.is.read(b, off, len);
	}

}
