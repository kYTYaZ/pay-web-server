package com.huateng.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.wldk.framework.interceptor.BusinessException;
import com.wldk.framework.mapping.MappingContext;

public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	/**
     * 将一个字符串集合以会换行的形式写入一个新文件中.
     * @param f txt文件.
     * @param array 字符串集合.
     * @throws BusinessException 异常.
     */
    public static void writeToTxt(File f, List<String> array,String charset) throws Exception {
    	OutputStreamWriter writer = null;
    	try {
    		// 构建FileOutputStream对象,文件不存在会自动新建
    		FileOutputStream fop = new FileOutputStream(f);
    		for(String str : array){
    			// 构建OutputStreamWriter对象,参数可以指定编码
//    			writer = new OutputStreamWriter(fop, "UTF-8");
    			writer = new OutputStreamWriter(fop, charset);
    			// 写入到缓冲区
    			writer.append(str);
    			// 换行
    			writer.append("\r\n");
    			// 刷新
    			writer.flush();
    		}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {    		
			//关闭写入流,同时会把缓冲区内容写入文件,所以上面的注释掉
    		try {
    			if(writer != null){
    				writer.close();
    			}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
    }
    
    /**
	  * 写入文件
	  * @param b
	  */
	 public static void writeToFileAppend(byte[] b){
		 File file = new File("E:\\resp_22.txt");
		 
		 try {
			 if (!file.exists()) {
				file.createNewFile();
			 }
			 
			 FileOutputStream fop = new FileOutputStream(file);
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fop));
			 bw.write(new String(b,"UTF-8"));
			 bw.flush();
			 bw.close();
			 
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 logger.error(e.getMessage(),e);
		 }
	 }
	 
	 /**
	  * 写入文件
	  * @param b
	  */
	 public static void writeToFile(String fileContent,File file) throws  Exception{
		 try {
			 if (!file.exists()) {
				file.createNewFile();
			 }
			 
			 FileOutputStream fop = new FileOutputStream(file);
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fop));
			 bw.write(fileContent);
			 bw.flush();
			 bw.close();
			 
		 } catch (IOException e) {
			 logger.error(e.getMessage(),e);
			 throw e;
		 }
	 }
    
    /**
	 * 读取文件内容
	 */
	 public static byte[] readToByte(String fileName) {
		 File file = new File(fileName);
         Long filelength = file.length();     //获取文件长度
         byte[] filecontent = new byte[filelength.intValue()];
         try {
             FileInputStream in = new FileInputStream(file);
             in.read(filecontent);
             in.close();
         } catch (FileNotFoundException e) {
        	 logger.error(e.getMessage(),e);
         } catch (IOException e) {
        	 logger.error(e.getMessage(),e);
         }
         return filecontent;
	 }
	 
	 /**
	  * 读取文件内容
	  */
	public static String readToString(String fileName,String charset) {
		StringBuffer fileContent = new StringBuffer();
		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, charset);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.append(line).append("\r\n");
			}
			br.close();
			fis.close();
			isr.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return fileContent.toString();
	}
	 
	 public static ArrayList<String> readFileToList(File f) {
	    	ArrayList<String> list = null;
	    	FileReader fileReader = null;
	    	BufferedReader bufferedReader = null;
	        try {
	        	list = new ArrayList<String>();
	        	fileReader = new FileReader(f);
	        	bufferedReader = new BufferedReader(fileReader);
	        	
				String readString = null;
				// 一次读入一行，直到读入null为文件结束
				while ((readString= bufferedReader.readLine()) != null) {
				    list.add(readString);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} finally {
	            if (bufferedReader != null) {
	            	try {
						bufferedReader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
	            if(fileReader != null){
	            	try {
						fileReader.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
	            }
			}
	        return list;
	    }
	 
	 
	 /**
	  * 解析微信对账单内容
	  * @return
	  */
	public static boolean parseWxBill(File file,File wxFile) {
		RandomAccessFile raf = null;
		BufferedWriter writer = null;
	  	BufferedReader bufferedReader = null;
	  	InputStreamReader in = null;
		try {
	  
        	in = new InputStreamReader(new FileInputStream(file),"utf-8");
        	bufferedReader = new BufferedReader(in,5*1024*1024);
        	
			wxFile.delete();
			wxFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(wxFile,true);
			BufferedOutputStream bos = new BufferedOutputStream(fos,5 * 1024 *1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos,"utf-8");
			writer = new BufferedWriter(osw);
			writer.write(new char[100]);
			writer.write("\n");
			
			String bill = null;////微信返回的内容
			// 一次读入一行，直到读入null为文件结束
			while ((bill= bufferedReader.readLine()) != null) {
				if(bill.matches("^`\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.*$")){
					//获取微信对账单明细内容
					String[] billArr = bill.split(",");
					String txnSeqId = billArr[6].replace("`", "");// 二维码前置订单号
					String wxOrderNo = billArr[5].replace("`", "");// 微信订单号
					String txnTime = billArr[0].replaceAll("[- :`]", "");
					String txnDt = txnTime.substring(0, 8);// 微信交易日期
					String txnTm = txnTime.substring(8, 14);// 微信交易时间
					String tradeMoney = billArr[12].replace("`", "");// 交易金额
					String feeMoney = billArr[22].replace("`", "");// 手续费
					String revokeMoney = billArr[16].replace("`", "");// 退款金额
					String transType = billArr[9].replace("`", "");// 交易类型(微信对应交易状态)
					String txnType = "1";
					
					// 撤销交易
					if ("REVOKED".equals(transType)) {
						tradeMoney = revokeMoney;
						txnType = "2";
					}
					
					String settleMoney = new BigDecimal(tradeMoney).subtract(new BigDecimal(feeMoney).abs()).toString();// 结算金额

					StringBuffer billBuffer = new StringBuffer();
					billBuffer.append(txnDt).append(",")// 交易日期
							.append(txnTm).append(",")// 交易时间
							.append(txnSeqId).append(",")// 商户订单号（二维码前置订单号）
							.append(wxOrderNo).append(",")// 微信订单号
							.append(txnType).append(",")// 交易类型（1-消费 2-撤销）
							.append("0").append(",")// 交易状态 0-成功
							.append("156").append(",")// 货币种类 156
							.append(tradeMoney).append(",")// 交易金额（原始交易金额）
							.append(feeMoney).append(",")// 交易手续费
							.append(settleMoney);// 结算金额（实际清算金额）

					writer.write(billBuffer.toString());
					writer.write("\n");
				}else if(bill.matches("^`\\d+.*$")){
					// 获取微信对账单汇总列信息
					String[] array = bill.split(",");
					int totalNum = Integer.parseInt(array[0].replace("`", ""));
//					String totalNum = array[0].replace("`", "");
					String totalAmt = array[1].replace("`", "");
					String totalRefundAmt = array[2].replace("`", "");
					String totalFeeAmt = array[4].replace("`", "");
					String total = totalNum + "," + totalAmt + "," + totalRefundAmt + "," + totalFeeAmt;// 文件头汇总信息
					
					raf = new RandomAccessFile(wxFile, "rw");
					raf.writeBytes(total);
				}else {
					logger.debug("微信对账单不满足格式::"+bill);
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("解析微信对账单内容：" + e.getMessage(),e);
			return false;
		}finally{
			try {
				if(raf != null){
					raf.close();
				}
				if(writer != null){
					writer.flush();
					writer.close();
				}
				if(bufferedReader != null){
					bufferedReader.close();
				}
				
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				return false;
			}
		}
	}
	
	/**
	 * 微信多费率对账单汇总
	 * @param billDate
	 * @return
	 */
	public static boolean sumWxBill(String billDate) {
		File sumfile = null;
		FileOutputStream fos = null;
		BufferedWriter writer = null;
		RandomAccessFile raf = null;
		try {
			Map<String, Object[]> map = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			String path = Constants.getParam("wx_downloadBill_path");
			sumfile = new File(path + billDate + "_2PAYCFT");
			if(sumfile.exists()) {
				sumfile.delete();
				sumfile.createNewFile();
			}
			fos = new FileOutputStream(sumfile, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos,5 * 1024 *1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "utf-8");
			writer = new BufferedWriter(osw);
			writer.write(new char[100]); //占位符
			writer.write("\n");
			BigDecimal totalNum = BigDecimal.ZERO;
			BigDecimal totalAmt = BigDecimal.ZERO;
			BigDecimal totalRefundAmt = BigDecimal.ZERO;
			BigDecimal totalFeeAmt = BigDecimal.ZERO;
			while (it.hasNext()) {
				String key = it.next();
				Object[] obj = map.get(key);
				String CHANNEL = StringUtil.toString(obj[0]);
				if (!StringConstans.PAY_ChANNEL.WX.equals(CHANNEL)) {
					continue;
				}

				String rate = StringUtil.toString(obj[1]);
				String fileName = path + billDate + "_" + rate + "_2PAYCFT";
				File file = new File(fileName);
				if (!file.exists()) {
					logger.info("微信合成对账单费率"+rate+"不存在");
					continue;
				}
				
				InputStreamReader in = null;
				BufferedReader bufReader = null;
				
				try {
					in = new InputStreamReader(new FileInputStream(file),"utf-8");
					bufReader = new BufferedReader(in,5 * 1024 *1024);
					String bill = null;
					boolean firstLine = true;
					while ((bill = bufReader.readLine()) != null) {
						if(firstLine) {
							String sumValue = bill;
							String[] sums = sumValue.split(",");
							String totalNum_i = sums[0].trim();
							String totalAmt_i = sums[1].trim();
							String totalRefundAmt_i = sums[2].trim();
							String totalFeeAmt_i = sums[3].trim();
							totalNum = totalNum.add(new BigDecimal(totalNum_i));
							totalAmt = totalAmt.add(new BigDecimal(totalAmt_i));
							totalRefundAmt = totalRefundAmt.add(new BigDecimal(totalRefundAmt_i));
							totalFeeAmt = totalFeeAmt.add(new BigDecimal(totalFeeAmt_i));
							firstLine = false;
						}else {
							  writer.write(bill);
							  writer.write("\n");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("微信多费率对账单汇总：" + e.getMessage(),e);
				} finally {
					if(bufReader!=null) {
						bufReader.close();
					}
					if(in!=null) {
						in.close();
					}
				}
			}
			
			 writer.flush();
			 fos.flush();

			String total = totalNum + "," + totalAmt + "," + totalRefundAmt + "," + totalFeeAmt;// 文件头汇总信息
			
			raf = new RandomAccessFile(sumfile, "rw");
			raf.writeBytes(total.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("微信多费率对账单汇总：" + e.getMessage(),e);
			return false;
		} finally {
			if(raf!=null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
		}

		return true;
	}
	
	
	
	
	
	/**
	  * 解析支付宝对账单内容
	  * @return
	  */
	public static boolean parseAlipayBill(File file,File aliPayFile) {
		BufferedWriter writer = null;
		BufferedReader bufReader = null;
		InputStreamReader in = null;
		RandomAccessFile raf = null;
		FileOutputStream fos = null;
		try {
			
			in = new InputStreamReader(new FileInputStream(file),"GBK");
			bufReader = new BufferedReader(in,5 * 1024 *1024);
	
			aliPayFile.delete();
			aliPayFile.createNewFile();
			
			fos = new FileOutputStream(aliPayFile, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos,5 * 1024 *1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "GBK");
			writer = new BufferedWriter(osw);
			writer.write(new char[100]); //占位符
			writer.write("\n");
			
			// 总订单数
			int orderCount = 0;

			// 总的手续费
			BigDecimal totalFeeAmt = new BigDecimal(0);

			// 总交易金额
			BigDecimal totalTradeAmt = new BigDecimal(0);

			// 实收金额
			BigDecimal totalReceiptAmt = new BigDecimal(0);

			// 支付宝返回的内容
			String bill = null;
			
			// 一次读入一行，直到读入null为文件结束
			while ((bill = bufReader.readLine()) != null) {
				
				if (bill.matches("^\\d+.*$")) {
					
					// 获取支付宝对账单明细内容
					String[] billArr = bill.split(",");
					
					String txnTime = billArr[5].replaceAll("[- :]","");
					String txnDt = txnTime.substring(0,8);  //支付宝交易日期
					String txnTm = txnTime.substring(8,14);  //支付宝交易时间
					String alipayOrderNo = billArr[0].trim(); //支付宝订单号
					String txnSeqId = billArr[1].trim();  //二维码前置订单号
					String termId = billArr[9].trim();//终端号
					String tradeMoney = billArr[11]; //订单金额
					String paidMoney = billArr[12]; //实收金额	
					String merchantBenefitAmt = billArr[16].trim();//商户优惠金额
					String certiVerAmt = billArr[17]; //券核销金额
					String merchantRedPacktAmt = billArr[19]; //商家红包
					String refundNo = billArr[21].trim();//退款批次号
					String feeMoney = billArr[22];//手续费
					String netIncome = billArr[23];//净收金额
					String alipayMerId = billArr[24];//商户识别号
									
					String txnType = "1"; //消费
					
					//总订单数
					orderCount++;
					
					//订单金额
					totalTradeAmt = totalTradeAmt.add(new BigDecimal(tradeMoney));
					
					//商户实收金额
					totalReceiptAmt = totalReceiptAmt.add(new BigDecimal(paidMoney));
					
					//手续费总额
					totalFeeAmt = totalFeeAmt.add(new BigDecimal(feeMoney));
					
					
					//撤销交易
					if(billArr[11].matches("(^\\-\\d*)|(^\\-\\d*\\.\\d{1,2})")){
						txnType = "2";	
					}				
					
					StringBuffer billBuffer = new StringBuffer();
					
					billBuffer.append(txnDt).append(",")// 交易日期
							  .append(txnTm).append(",")// 交易时间
							  .append(alipayOrderNo).append(",")//支付宝订单号
							  .append(txnSeqId).append(",")// 商户订单号（二维码前置订单号）
							  .append(txnType).append(",")// 交易类型（1-消费 2-撤销）
							  .append(termId).append(",")// 终端号		
							  .append(new BigDecimal(tradeMoney).abs()).append(",")// 交易金额（原始交易金额）
							  .append(new BigDecimal(paidMoney).abs()).append(",")// 实收金额	
							  .append(new BigDecimal(merchantBenefitAmt).abs()).append(",")//商户优惠金额
							  .append(new BigDecimal(certiVerAmt).abs()).append(",")//券核销金额
							  .append(new BigDecimal(merchantRedPacktAmt).abs()).append(",")//商家红包
							  .append(refundNo).append(",")//退款批次号
							  .append(new BigDecimal(feeMoney).abs()).append(",")// 交易手续费
							  .append(new BigDecimal(netIncome).abs()).append(",")// 净收金额
							  .append(alipayMerId);// 商户识别号

					  writer.write(billBuffer.toString());
					  writer.write("\n");
				} else {
					System.out.println("::"+bill+"::");
				}
			}
	    
			//所有的信息，文件头加说明
			StringBuffer totalBuffer = new StringBuffer();
			totalBuffer.append(orderCount).append(",")
			 		   .append(StringUtil.amountTo12Str(totalTradeAmt.abs().toString())).append(",")
			           .append(StringUtil.amountTo12Str(totalReceiptAmt.abs().toString())).append(",")
			           .append(StringUtil.amountTo12Str(totalFeeAmt.abs().toString()));
			 
			 raf = new RandomAccessFile(aliPayFile, "rw");
			 raf.writeBytes(totalBuffer.toString());
			
			 writer.flush();
			 fos.flush();
			 
			 return true;
			 
		} catch (Exception e) {
			logger.error("解析支付宝对账单内容：" + e.getMessage(),e);
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (bufReader != null) {
					bufReader.close();
				}
				if (in != null) {
					in.close();
				}
				if (raf != null) {
					raf.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				return false;
			}
		}
	}
	
	/**
	 * ali多费率对账单汇总
	 * @param billDate
	 * @return
	 */
	public static boolean sumAliBill(String billDate) {
		File sumfile = null;
		FileOutputStream fos = null;
		BufferedWriter writer = null;
		RandomAccessFile raf = null;
		try {
			Map<String, Object[]> map = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			String path = Constants.getParam("alipay_downloadBill_path");
			sumfile = new File(path + billDate + "_2PAYZFB");
			if(sumfile.exists()) {
				sumfile.delete();
				sumfile.createNewFile();
			}
			fos = new FileOutputStream(sumfile, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos,5 * 1024 *1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "GBK");
			writer = new BufferedWriter(osw);
			writer.write(new char[100]); //占位符
			writer.write("\n");
			BigDecimal totalNum = BigDecimal.ZERO;
			BigDecimal totalAmt = BigDecimal.ZERO;
			BigDecimal totalRefundAmt = BigDecimal.ZERO;
			BigDecimal totalFeeAmt = BigDecimal.ZERO;
			while (it.hasNext()) {
				String key = it.next();
				Object[] obj = map.get(key);
				String CHANNEL = StringUtil.toString(obj[0]);
				if (!StringConstans.PAY_ChANNEL.ALI.equals(CHANNEL)) {
					continue;
				}

				String RATE = StringUtil.toString(obj[1]);
				String fileName = path + billDate + "_" + RATE + "_2PAYZFB";
				File file = new File(fileName);
				if (!file.exists()) {
					continue;
				}
				
				InputStreamReader in = null;
				BufferedReader bufReader = null;
				
				try {
					in = new InputStreamReader(new FileInputStream(file),"GBK");
					bufReader = new BufferedReader(in,5 * 1024 *1024);
					String bill = null;
					boolean firstLine = true;
					while ((bill = bufReader.readLine()) != null) {
						if(firstLine) {
							String sumValue = bill;
							String[] sums = sumValue.split(",");
							String totalNum_i = sums[0].trim();
							String totalAmt_i = sums[1].trim();
							String totalRefundAmt_i = sums[2].trim();
							String totalFeeAmt_i = sums[3].trim();
							totalNum = totalNum.add(new BigDecimal(totalNum_i));
							totalAmt = totalAmt.add(new BigDecimal(totalAmt_i));
							totalRefundAmt = totalRefundAmt.add(new BigDecimal(totalRefundAmt_i));
							totalFeeAmt = totalFeeAmt.add(new BigDecimal(totalFeeAmt_i));
							firstLine = false;
						}else {
							  writer.write(bill);
							  writer.write("\n");
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("支付宝多费率对账单汇总：" + e.getMessage(),e);
				} finally {
					if(bufReader!=null) {
						bufReader.close();
					}
					if(in!=null) {
						in.close();
					}
				}

			}
			
			 writer.flush();
			 fos.flush();

			// 文件头汇总信息
			
			StringBuffer totalBuffer = new StringBuffer();
			totalBuffer.append(totalNum).append(",")
			 		   .append(StringUtil.amountTo12Str2(totalAmt.abs().toString())).append(",")
			           .append(StringUtil.amountTo12Str2(totalRefundAmt.abs().toString())).append(",")
			           .append(StringUtil.amountTo12Str2(totalFeeAmt.abs().toString()));
			
			 raf = new RandomAccessFile(sumfile, "rw");
			 raf.writeBytes(totalBuffer.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("支付宝多费率对账单汇总：" + e.getMessage(),e);
			return false;
		} finally {
			if(raf!=null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}
			}
		}

		return true;
	}

	
	public static List<String> unZip(File file) throws Exception{
		
		List<String> list = new ArrayList<String>();
		
		ZipInputStream zip =null;
		BufferedOutputStream bos = null ;
		
		if(!file.exists()){
			throw new Exception ("解压文件,找不到指定的文件");
		}
		
		try{
			zip = new ZipInputStream(new FileInputStream(file));
			ZipEntry entry =null ;
			
			while((entry = zip.getNextEntry())!=null){
				File target = new File(Constants.getParam("alipay_downloadBill_path"),entry.getName());
				if(!entry.isDirectory()){
					if(!target.getParentFile().exists()){
						target.getParentFile().mkdirs();
					}
					
					bos = new BufferedOutputStream(new FileOutputStream(target));
				
					int read = 0;
					byte [] buffer = new byte[1024 * 10];
					
					while((read = zip.read(buffer,0,buffer.length)) != -1){
						bos.write(buffer,0,read);
					}
					
					bos.flush();
					
					list.add(entry.getName());
					
				}else{
					if(!target.exists()){
						target.mkdirs();
					}
				}
			}
			
			return list;
			
		}catch(IOException e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(zip != null){
				zip.closeEntry();
				zip.close();
			}	
			
			if(bos != null){
				bos.close();
			}
		}
	}
	
	/**
	 * 创建三码合一微信对账单
	 * @param mapList
	 * @param billDate
	 * @return
	 * @throws IOException 
	 */
	public static void writeIntoWxFile(Map<String, Object> order, File file,
			BufferedWriter writer) throws IOException {

		StringBuffer sb = new StringBuffer();
		String settleDate = ObjectUtils.toString(order.get("settleDate"));
		String wxTradeDate = settleDate.substring(0, 8);
		String wxTradeTime = settleDate.substring(8, 14);
		String wxOrderNo = ObjectUtils.toString(order.get("wxOrderNo"));
		String txnSeqId = ObjectUtils.toString(order.get("txnSeqId"));
		String txnDt = ObjectUtils.toString(order.get("txnDt"));
		String txnTm = ObjectUtils.toString(order.get("txnTm"));
		String merOrder = String.format("%s%s%s", txnSeqId,txnDt,txnTm);
		String orgCode = ObjectUtils.toString(order.get("orgCode"));
		String acctNo = ObjectUtils.toString(order.get("acctNo"));
		String transType = "1";
		String status = StringConstans.OrderState.STATE_02;
		String currencyType = StringConstans.CurrencyCode.CNY;
		String tradeMoney = ObjectUtils.toString(order.get("tradeMoney"));
		String bankFee = ObjectUtils.toString(order.get("bankFee"));
		String wxFee = ObjectUtils.toString(order.get("wxFee"));
		String payAccessType = StringConstans.PAYACCESSTYPE.ACCESS_WX;
		String settlement = StringUtil.formateFeeAmt(new BigDecimal(tradeMoney)
							.subtract(new BigDecimal(wxFee))
							.subtract(new BigDecimal(bankFee)).toString());
		
		sb.append(wxTradeDate)
		  .append(wxTradeTime)
		  .append(StringUtil.padding(merOrder, 2, 32, ' '))
		  .append(StringUtil.padding(wxOrderNo, 2, 32, ' '))
		  .append(StringUtil.padding(acctNo, 2, 22, ' '))
		  .append(transType)
		  .append(StringUtil.padding(status, 2, 4, ' '))
		  .append(currencyType)
		  .append(tradeMoney)
		  .append(StringUtil.formateFeeAmt(bankFee))
		  .append(StringUtil.formateFeeAmt(wxFee))
		  .append(settlement)
		  .append(payAccessType)
		  .append(StringUtil.padding(orgCode, 2, 20, ' '));
		sb.append("\n");

		writer.write(sb.toString());
		writer.flush();

	}
	
	/**
	 * 创建三码合一支付宝对账单
	 * @param mapList
	 * @param billDate
	 * @return
	 * @throws IOException 
	 */
	public static void writeIntoAlipayFile(Map<String, Object> order, File file, BufferedWriter writer) throws IOException {
		
		StringBuffer sb = new StringBuffer();

		String settleDate = ObjectUtils.toString(order.get("settleDate"));
		String alipayTradeDate = settleDate.substring(0, 8);
		String alipayTradeTime = settleDate.substring(8, 14);
		String alipayOrderNo = ObjectUtils.toString(order.get("alipayTradeNo"));
		String txnSeqId = ObjectUtils.toString(order.get("txnSeqId"));
		String txnDt = ObjectUtils.toString(order.get("txnDt"));
		String txnTm = ObjectUtils.toString(order.get("txnTm"));
		String outOrderNo = String.format("%s%s%s", txnSeqId,txnDt,txnTm);
		String txnType = "1";
		String tradeMoney = ObjectUtils.toString(order.get("tradeMoney"));
		String receiptAmount = ObjectUtils.toString(order.get("receiptAmount"));
		String bankFee = ObjectUtils.toString(order.get("bankFee"));
		String alipayFee = ObjectUtils.toString(order.get("alipayFee"));
		String subAlipayMerId = ObjectUtils.toString(order.get("subAlipayMerId"));
		String acctNo = ObjectUtils.toString(order.get("acctNo"));
		String orgCode = ObjectUtils.toString(order.get("orgCode"));
		String settlement = StringUtil.formateFeeAmt(new BigDecimal(tradeMoney)
								.subtract(new BigDecimal(alipayFee))
								.subtract(new BigDecimal(bankFee)).toString());
		
		sb.append(alipayTradeDate) 							// 交易日期
		  .append(alipayTradeTime)							// 交易时间
		  .append(StringUtil.padding(alipayOrderNo, 2, 64, ' ')) // 支付宝交易号
		  .append(StringUtil.padding(outOrderNo, 2, 32, ' '))	// 商户订单号
		  .append(StringUtil.padding(txnType, 2, 2, ' '))     // 业务类型
		  .append(tradeMoney)									// 订单金额
		  .append(receiptAmount)								// 商家实收金额
		  .append(StringUtil.padding("", 2, 64, ' '))			// 退款批次号
		  .append(StringUtil.formateFeeAmt(bankFee))			// 银行手续费
		  .append(StringUtil.formateFeeAmt(alipayFee))			// 支付宝手续费
		  .append(settlement)								    // 实收净额为交易金额减去所有手续费
		  .append(StringUtil.padding(subAlipayMerId, 2, 30, ' '))	// 商户识别号
		  .append(StringUtil.padding(acctNo, 2, 22, ' '))		// 商户账户
		  .append(StringUtil.padding(orgCode, 2, 6, ' ')); 		// 机构号
		  sb.append("\n");
		  
		  writer.write(sb.toString());
		  writer.flush(); 
			
	}
	
	/**
	 * 向response写入对象流
	 * @param obj
	 * @param out
	 * @throws Exception
	 */
	public static void  writeResponse(Object obj,OutputStream out){
	
		ObjectOutputStream objOut = null;
		
		try {
			
			logger.info("-----------  写入response 对象流 流程  START ----------------");
			
			objOut = new ObjectOutputStream(new BufferedOutputStream(out));
			objOut.writeObject(obj);
			
			logger.info("-----------  写入response 对象流 流程  END ----------------");
			
		} catch (Exception e) {
			logger.error("写入response 对象流 流程出现异常:" + e.getMessage(),e);
		}finally{
			try {
				if(objOut !=null){
					objOut.flush();
					objOut.close();
				}
				if(out != null){
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				logger.error("写入response对象流 关闭流出现异常:" + e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 将一组文件打成tar包
	 */
	public static File packByTar(File [] sources,File target) {
		FileOutputStream out = null;
		FileInputStream in = null;
		TarArchiveOutputStream os = null;
		try {
			out = new FileOutputStream(target);
			os = new TarArchiveOutputStream(out);
			for (File file : sources) {
				os.putArchiveEntry(new TarArchiveEntry(file));
				in = new FileInputStream(file);
				IOUtils.copy(in, os);
				in.close();//sky
				os.closeArchiveEntry();
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e){
			logger.error(e.getMessage(),e);
		} finally {
			if(os!=null){
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if(out!=null){
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return target;
	}
	
	/**
	 * 将文件用gzip打包
	 */
	public static void compress(String tar) {

		String target = tar + ".gz";
		FileInputStream in = null;
		GZIPOutputStream out = null;
		try {

			in = new FileInputStream(tar);
			out = new GZIPOutputStream(new FileOutputStream(target));
			byte[] array = new byte[10240];
			int number = -1;
			while ((number = in.read(array)) != -1) {
				out.write(array, 0, number);

			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{

			try {
				if(in!=null){
					in.close();
				}
				if(out!=null){
					out.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}

	}
	
	public static void main(String[] args) {
//		Map<String, Object[]> map = new HashMap<String, Object[]>();
//		map.put("1", new String[] {"WX","00"});
//		map.put("2", new String[] {"WX","10"});
//		map.put("3", new String[] {"WX","20"});
//		map.put("4", new String[] {"WX","60"});
		
		String fileName = "D:\\zlg\\down\\20889112124162010156_20180919\\";
		File file = new File(fileName+"1321836301All2018-09-19_2018-09-19.csv");
//		FileUtil.writeToFile(respString, file);
		File wxFile = new File(fileName+"wxsettle");
		boolean flag = FileUtil.parseWxBill(file,wxFile);
		System.out.println(flag);
	}
}
