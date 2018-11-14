package com.huateng.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import it.sauronsoftware.base64.Base64;

public class QRUtil {
	
	private static Logger logger = LoggerFactory.getLogger(QRUtil.class);
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	public  static final String IMAGE_TYPE = "png";
	
	/**
	 * 
	 * @param map
	 */
	public  static  String  createQRImage(String codeUrl){
			
		try {
			
			Map<EncodeHintType, Object> qrMap = new Hashtable<EncodeHintType, Object>();
			qrMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
			qrMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			
			BitMatrix bit = new MultiFormatWriter().encode(codeUrl,BarcodeFormat.QR_CODE, WIDTH, HEIGHT,qrMap);
			
			ByteArrayOutputStream out  = new ByteArrayOutputStream();
			
			MatrixToImageWriter.writeToStream(bit, IMAGE_TYPE, out);
	
			String imageBase64 =  imageEncodeBase64(out);
			
			return imageBase64;
			
		} catch (Exception e) {
			logger.error("[生成二维码图片失败]" + e.getMessage(), e);
		}	
		
		return null;
	} 
	
	
	/**
	 * 将图片编码成base64
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static String imageEncodeBase64(ByteArrayOutputStream out) throws IOException{
		
		String base64 = new String(Base64.encode(out.toByteArray()));
		
		return base64;
		
	}
}
