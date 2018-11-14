package com.validate.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Test {
	public static void main(String[] args) {
		try {
			long l1 = System.currentTimeMillis();
			File file = new File("D:\\82374\\download\\dbvis9.1.9.zip");
			InputStream inputStream = new FileInputStream(file);
			DownloadLimiter download = new DownloadLimiter(inputStream, new BandwidthLimiter(10));
			InputStreamReader inputStreamReader = new InputStreamReader(download, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
//			           buffer.append(str).append("\n");
			}
			long l2 = System.currentTimeMillis();
			System.out.println("end:" + (l2 - l1));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
