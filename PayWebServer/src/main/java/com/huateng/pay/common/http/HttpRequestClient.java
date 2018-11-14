package com.huateng.pay.common.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.po.weixin.WxBaseData;
import com.huateng.utils.WebContainerInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.wldk.framework.utils.MappingUtils;

public class HttpRequestClient {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestClient.class);

    private static String keyManager = "";
     static {
    	
    	 String serverName = WebContainerInfo.getServerName();
    	 if("WebSphere".equals(serverName)) {
    		 keyManager = "IbmX509";
    	 } else {
    		 keyManager = "SunX509";
    	 }
     }
     
     
    /**
     * 
     * 发送https请求
     * 
     * @param requestUrl
     * @param paramMap
     * @return
     * @throws FrameException
     * @see 1。0
     * @since 1.0
     */
     public static String httpsRequest(String requestUrl, Map<String, String> paramMap) throws FrameException {
       StringBuffer buffer = new StringBuffer();
       try {
           Proxy proxy = null;
           if ("true".equals(Constants.getParam("httpsRequestProxy"))) {
               proxy = setProxy();
           }

           logger.debug("通知url:" + requestUrl + "  通知参数:" + paramMap);

           // 创建SSLContext对象，并使用我们指定的信任管理器初始化
           TrustManager[] tm = { new MyX509TrustManager() };
           // SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
           SSLContext sslContext = SSLContext.getInstance("TLS");
           sslContext.init(null, tm, new java.security.SecureRandom());
           // 从上述SSLContext对象中得到SSLSocketFactory对象
           SSLSocketFactory ssf = sslContext.getSocketFactory();

           HostnameVerifier hv = new HostnameVerifier() {
               @Override
               public boolean verify(String urlHostName, SSLSession session) {
                   return urlHostName.equals(session.getPeerHost());
               }
           };
           HttpsURLConnection.setDefaultHostnameVerifier(hv);

           URL url = new URL(requestUrl);
           HttpsURLConnection httpUrlConn = null;
           if (proxy == null) {
               httpUrlConn = (HttpsURLConnection) url.openConnection();
           } else {
               httpUrlConn = (HttpsURLConnection) url.openConnection(proxy);
           }

           httpUrlConn.setSSLSocketFactory(ssf);

           httpUrlConn.setDoOutput(true);
           httpUrlConn.setDoInput(true);
           httpUrlConn.setUseCaches(false);
           // 设置请求方式（GET/POST）
           httpUrlConn.setRequestMethod("POST");

           StringBuffer params = new StringBuffer();
           if (paramMap != null) {
               Set<String> keySet = paramMap.keySet();
               Iterator<String> iterator = keySet.iterator();
               int i = 0;
               while (iterator.hasNext()) {
                   String key = iterator.next();
                   if (i > 0) {
                       params.append("&");
                   }
                   params.append(key + "=" + paramMap.get(key));
                   i++;
               }
           }
           logger.info("发送服务器报文：" + params.toString());
           OutputStream outputStream = httpUrlConn.getOutputStream();
           // 注意编码格式，防止中文乱码
           outputStream.write(params.toString().getBytes("UTF-8"));
           outputStream.close();

           // 将返回的输入流转换成字符串
           InputStream inputStream = httpUrlConn.getInputStream();
           InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
           BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

           String str = null;
           while ((str = bufferedReader.readLine()) != null) {
               buffer.append(str);
           }
           bufferedReader.close();
           inputStreamReader.close();
           // 释放资源
           inputStream.close();
           inputStream = null;
           httpUrlConn.disconnect();
           
          logger.info("接收服务器响应结果：" + buffer.toString());
       } catch (ConnectException ce) {
           logger.error("发送https连接异常：" + ce.getMessage(),ce);
           throw new FrameException(ce.getMessage());
       } catch (Exception e) {
           logger.error("发送https请求异常：" + e.getMessage(),e);
           throw new FrameException(e.getMessage());
       }
       
       return buffer.toString();
   }

    

    /**
     * 
     * 发送http请求
     * 
     * @param requestUrl 请求的url
     * @param paramMap 请求中所需要传的参数
     * @return
     * @throws FrameException
     */
    public static String httpRequest(String requestUrl, Map<String, String> paramMap) throws FrameException {
    	
    	StringBuffer buffer = new StringBuffer();
    	
    	try {
        	
        	 URL url = new URL(requestUrl);

             Proxy proxy = null;
             if ("true".equals(Constants.getParam("httpsRequestProxy"))) {
                 proxy = setProxy();
             }

             logger.debug("通知url:" + requestUrl + "  通知参数:" + paramMap);

             HttpURLConnection httpURLConnection = null;
             if (proxy == null) {
            	 httpURLConnection = (HttpURLConnection)url.openConnection();
             } else {
            	 httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
             }
             
             // Post 请求不能使用缓存
             httpURLConnection.setUseCaches(false);
             httpURLConnection.setDoInput(true);
             httpURLConnection.setDoOutput(true);
             httpURLConnection.setRequestMethod("POST");
             httpURLConnection.setConnectTimeout(30000);
             httpURLConnection.setReadTimeout(300000);
             httpURLConnection.connect();
             // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成
             httpURLConnection.connect();
             // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
             // 所以在开发中不调用上述的connect()也可以)。
             OutputStream outStrm = httpURLConnection.getOutputStream();
             StringBuffer params = new StringBuffer();
             if (paramMap != null) {
            	 int i = 0;
            	 for (Entry<String, String> entry : paramMap.entrySet()) {
            		 if (i > 0) {
            			 params.append("&");
            		 };
                     params.append(entry.getKey() + "=" + entry.getValue());
                     i++;
				}
             } 
             logger.info("发送服务器报文：" + params.toString());
             outStrm.write(params.toString().getBytes("utf-8"));
             outStrm.flush();
             outStrm.close();
             
             // 将返回的输入流转换成字符串
             InputStream inputStream = httpURLConnection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

             String str = null;
             while ((str = bufferedReader.readLine()) != null) {
                 buffer.append(str);
             }
             bufferedReader.close();
             inputStreamReader.close();
             // 释放资源
             inputStream.close();
             inputStream = null;
             httpURLConnection.disconnect();
             logger.info("接收服务器响应结果：" + buffer.toString());
        } catch (Exception e) {
            logger.error("发送http请求异常：" + e.getMessage(),e);
            throw new FrameException(e.getMessage());
        }
        
        return buffer.toString();
    }



    private static Proxy setProxy() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(MappingUtils.getString("SYSPARAM", "proxyIp"),
                Integer.parseInt(MappingUtils.getString("SYSPARAM", "proxyPort"))));
        logger.debug("http代理ip：" + MappingUtils.getString("SYSPARAM", "proxyIp") + " http代理端口:"
                + MappingUtils.getString("SYSPARAM", "proxyPort"));
        return proxy;
    }
    
    /**
     * 发送微信https请求
     * @param requestUrl
     * @param xmlObj
     * @return 
     * @throws FrameException
     * @throws Exception
     */
    public static String sendWxHttpsReq(Map<String,String> requestMap, Object xmlObj)
            throws FrameException {
        StringBuffer buffer = new StringBuffer(500000);
        String result = "";
        try {
            String requestUrl = requestMap.get(Dict.requestUrl);
            String pfxPath = requestMap.get(Dict.pfxPath);
            String pfxPwd = requestMap.get(Dict.pfxPwd);
        	
            Proxy proxy = null;
            if ("true".equals(Constants.getParam("httpsRequestProxy"))) {
                proxy = setProxy();
            }
            
            XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
            
            //将要提交给API的数据对象转换成XML格式数据Post给API
            String postDataXML = xStreamForRequestPostData.toXML(xmlObj);
            logger.info("请求url:" + requestUrl);
            logger.info("请求xml报文:" + postDataXML);
            
            KeyStore ks = KeyStore.getInstance("PKCS12");

            FileInputStream fis = new FileInputStream(pfxPath);

            ks.load(fis, pfxPwd.toCharArray());
            KeyManagerFactory kf = KeyManagerFactory.getInstance(keyManager);
            kf.init(ks, pfxPwd.toCharArray());
            KeyManager[] km = kf.getKeyManagers();
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(km, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            HostnameVerifier hv = new HostnameVerifier() {
                @Override
                public boolean verify(String urlHostName, SSLSession session) {
                    return urlHostName.equals(session.getPeerHost());
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            URL url = new URL(requestUrl);
            HttpsURLConnection httpsUrlConn = null;
            if (proxy == null) {
                httpsUrlConn = (HttpsURLConnection) url.openConnection();
            } else {
                httpsUrlConn = (HttpsURLConnection) url.openConnection(proxy);
            }

            httpsUrlConn.setSSLSocketFactory(ssf);

            httpsUrlConn.setDoOutput(true);
            httpsUrlConn.setDoInput(true);
            httpsUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpsUrlConn.setRequestMethod("POST");

            OutputStream outputStream = httpsUrlConn.getOutputStream();
            // 注意编码格式，防止中文乱码
            outputStream.write(postDataXML.getBytes("UTF-8"));
            outputStream.close();

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpsUrlConn.getInputStream();
            DownloadLimiter download = new DownloadLimiter(inputStream, new BandwidthLimiter(Integer.valueOf(Constants.getParam("download_speed"))));
            InputStreamReader inputStreamReader = new InputStreamReader(download, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            if(postDataXML.contains("<bill_date>")){
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str).append("\n");
                }
        	}else{
        		while ((str = bufferedReader.readLine()) != null) {
        			buffer.append(str);
        		}
        	}
            
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpsUrlConn.disconnect();
            result = buffer.toString();
            logger.info("接收服务器响应结果：" + buffer.toString());
        } catch (ConnectException ce) {
            logger.error("发送https连接异常：" + ce.getMessage(),ce);
            throw new FrameException(ce.getMessage());
        } catch (Exception e) {
            logger.error("发送https请求异常：" + e.getMessage(),e);
            throw new FrameException(e.getMessage());
        }
        return result;
    }
    
    /**
     * 发送微信https请求
     * @param requestUrl
     * @param xmlObj
     * @return 
     * @throws FrameException
     * @throws Exception
     */
    public static String sendWxHttpsReq(Map<String,String> requestMap, WxBaseData xmlObj)
            throws FrameException {
        StringBuffer buffer = new StringBuffer(500000);
        String result = "";
        try {
            String requestUrl = requestMap.get(Dict.requestUrl);
            String pfxPath = requestMap.get(Dict.pfxPath);
            String pfxPwd = requestMap.get(Dict.pfxPwd);
        	
            Proxy proxy = null;
            if ("true".equals(Constants.getParam("httpsRequestProxy"))) {
                proxy = setProxy();
            }
            
//            XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
            
            //将要提交给API的数据对象转换成XML格式数据Post给API
//            String postDataXML = xStreamForRequestPostData.toXML(xmlObj);
            String postDataXML = WxBaseData.mapToXml(xmlObj.toMap());
            logger.info("请求url:" + requestUrl);
            logger.info("请求xml报文:" + postDataXML);
            
            KeyStore ks = KeyStore.getInstance("PKCS12");

            FileInputStream fis = new FileInputStream(pfxPath);

            ks.load(fis, pfxPwd.toCharArray());
            KeyManagerFactory kf = KeyManagerFactory.getInstance(keyManager);
            kf.init(ks, pfxPwd.toCharArray());
            KeyManager[] km = kf.getKeyManagers();
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(km, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            HostnameVerifier hv = new HostnameVerifier() {
                @Override
                public boolean verify(String urlHostName, SSLSession session) {
                    return urlHostName.equals(session.getPeerHost());
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            URL url = new URL(requestUrl);
            HttpsURLConnection httpsUrlConn = null;
            if (proxy == null) {
                httpsUrlConn = (HttpsURLConnection) url.openConnection();
            } else {
                httpsUrlConn = (HttpsURLConnection) url.openConnection(proxy);
            }

            httpsUrlConn.setSSLSocketFactory(ssf);

            httpsUrlConn.setDoOutput(true);
            httpsUrlConn.setDoInput(true);
            httpsUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpsUrlConn.setRequestMethod("POST");

            OutputStream outputStream = httpsUrlConn.getOutputStream();
            // 注意编码格式，防止中文乱码
            outputStream.write(postDataXML.getBytes("UTF-8"));
            outputStream.close();

            // 将返回的输入流转换成字符串
            InputStream inputStream = httpsUrlConn.getInputStream();
            DownloadLimiter download = new DownloadLimiter(inputStream, new BandwidthLimiter(Integer.valueOf(Constants.getParam("download_speed"))));
            InputStreamReader inputStreamReader = new InputStreamReader(download, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            boolean downLoadFlag = postDataXML.contains("<bill_date>");
            if(downLoadFlag){
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str).append("\n");
                }
        	}else{
        		while ((str = bufferedReader.readLine()) != null) {
        			buffer.append(str);
        		}
        	}
            
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpsUrlConn.disconnect();
            result = buffer.toString();
            if (!downLoadFlag||(result.contains("<xml>"))) {
                logger.info("接收服务器响应结果：" + result);
            }
        } catch (ConnectException ce) {
            logger.error("发送https连接异常：" + ce.getMessage(),ce);
            throw new FrameException(ce.getMessage());
        } catch (Exception e) {
            logger.error("发送https请求异常：" + e.getMessage(),e);
            throw new FrameException(e.getMessage());
        }
        return result;
    }
    
    
    /**
     * 
     * @param requestUrl
     * @param xmlObj
     * @param isApp
     * @return
     * @throws FrameException
     */
    public static void alipayHttpRequest(String requestUrl,File file) throws FrameException {
        
    	InputStream inputStream = null;
    	BufferedInputStream bufferedInput = null;
    	OutputStream out =null;
    	BufferedOutputStream bufferedOut = null;
    	
    	try {
        	
        	 URL url = new URL(requestUrl);

             Proxy proxy = null;
             if ("true".equals(Constants.getParam("httpsRequestProxy"))) {
                 proxy = setProxy();
             }

             logger.debug("请求url:" + requestUrl );

             HttpURLConnection httpURLConnection = null;
             if (proxy == null) {
            	 httpURLConnection = (HttpURLConnection)url.openConnection();
             } else {
            	 httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
             }
             
             // Post 请求不能使用缓存
             httpURLConnection.setUseCaches(false);
             httpURLConnection.setDoInput(true);
             httpURLConnection.setDoOutput(true);
             httpURLConnection.setRequestMethod("GET");
             httpURLConnection.setConnectTimeout(30000);
             httpURLConnection.setReadTimeout(300000);
             httpURLConnection.connect();
         
             // 将返回的输入流转换成字符串
             inputStream = httpURLConnection.getInputStream();
             bufferedInput = new BufferedInputStream(inputStream);
             out= new FileOutputStream(file);
             bufferedOut = new BufferedOutputStream(out);
              
              int count =0;
              byte [] bt = new byte [1024];
              
             while ((count = bufferedInput.read(bt)) != -1) {
            	 bufferedOut.write(bt,0,count);
             }
             
             //释放资源
             out.flush();
             bufferedOut.flush();
             httpURLConnection.disconnect();
             
        } catch (Exception e) {
            logger.error("发送http请求异常：" + e.getMessage(),e);
            throw new FrameException(e.getMessage());
        }finally{
        	try {
        		if(inputStream != null){
        			inputStream.close();
        		}
        		if(bufferedInput != null){
        			bufferedInput.close();
        		}
        		if(out != null){
        			out.close();
        		}
                if(bufferedOut != null){			
                    bufferedOut.close();
                }
			} catch (IOException e) {
				logger.error("发送http请求异常：" + e.getMessage(),e);
				throw new FrameException(e.getMessage());		
        	}
        }
        
    }
    
    
    /**
     * 证书认证
     * @param keyStore
     * @return
     */
    public static CloseableHttpClient createSSLInsecureClient(KeyStore keyStore) {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                                .loadTrustMaterial(keyStore, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain,
                                String authType) throws CertificateException {
                    return true;
                }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return  HttpClients.createDefault();
    }
    public static void main(String[] args) {
		httpRequest("http://openapi.alipay.com/gateway.do", null);
	}
}
