/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI基本参数工具类
 * =============================================================================
 */
package com.huateng.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.huateng.pay.common.constants.CupsConstants;
import com.unionpay.acp.sdk.SDKUtil;

/**
 * 
 * @ClassName SDKConfig
 * @Description acpsdk_cups配置文件acp_sdk.properties配置信息类
 * @date 2016-7-22 下午4:04:55
 *
 */
public class CupsPayConfig {
	public static final String FILE_NAME = "acp_sdk.properties";
	/** 前台请求URL. */
	private String frontRequestUrl;
	/** 后台请求URL. */
	private String backRequestUrl;
	/** 单笔查询 */
	private String singleQueryUrl;
	/** 批量查询 */
	private String batchQueryUrl;
	/** 批量交易 */
	private String batchTransUrl;
	/** 文件传输 */
	private String fileTransUrl;
	/** 签名证书路径. */
	private String signCertPathCups;
	/** 签名证书密码. */
	private String signCertPwdCups;
	/** 签名证书类型. */
	private String signCertTypeCups;
	/** 加密公钥证书路径. */
	private String encryptCertPathCups;
	/** 验证签名公钥证书目录. */
	private String validateCertDirCups;
	/** 按照商户代码读取指定签名证书目录. */
	private String signCertDirCups;
	/** 磁道加密证书路径. */
	private String encryptTrackCertPathCups;
	/** 磁道加密公钥模数. */
	private String encryptTrackKeyModulusCups;
	/** 磁道加密公钥指数. */
	private String encryptTrackKeyExponentCups;
	/** 有卡交易. */
	private String cardRequestUrl;
	/** app交易 */
	private String appRequestUrl;
	/** 证书使用模式(单证书/多证书) */
	private String singleMode;
	/** 安全密钥(SHA256和SM3计算时使用) */
	private String secureKey;
	/** 中级证书路径  */
	private String middleCertPath;
	/** 根证书路径  */
	private String rootCertPath;
	/** 是否验证验签证书CN，除了false都验  */
	private boolean ifValidateCNNameCups = true;
	/** 是否验证https证书，默认都不验  */
	private boolean ifValidateRemoteCertCups = false;
	/** signMethod，没配按01吧  */
	private String signMethodCups = "01";
	/** version，没配按5.0.0  */
	private String version = "5.0.0";
	/** frontUrl  */
	private String frontUrl;
	/** backUrl  */
	private String backUrl;

	/*缴费相关地址*/
	private String jfFrontRequestUrl;
	private String jfBackRequestUrl;
	private String jfSingleQueryUrl;
	private String jfCardRequestUrl;
	private String jfAppRequestUrl;
	
	private String qrcBackTransUrl;
	private String qrcB2cIssBackTransUrl;
	private String qrcB2cMerBackTransUrl;

	/** 配置文件中的前台URL常量. */
	public static final String SDK_FRONT_URL = "acpsdk_cups.frontTransUrl";
	/** 配置文件中的后台URL常量. */
	public static final String SDK_BACK_URL = "acpsdk_cups.backTransUrl";
	/** 配置文件中的单笔交易查询URL常量. */
	public static final String SDK_SIGNQ_URL = "acpsdk_cups.singleQueryUrl";
	/** 配置文件中的批量交易查询URL常量. */
	public static final String SDK_BATQ_URL = "acpsdk_cups.batchQueryUrl";
	/** 配置文件中的批量交易URL常量. */
	public static final String SDK_BATTRANS_URL = "acpsdk_cups.batchTransUrl";
	/** 配置文件中的文件类交易URL常量. */
	public static final String SDK_FILETRANS_URL = "acpsdk_cups.fileTransUrl";
	/** 配置文件中的有卡交易URL常量. */
	public static final String SDK_CARD_URL = "acpsdk_cups.cardTransUrl";
	/** 配置文件中的app交易URL常量. */
	public static final String SDK_APP_URL = "acpsdk_cups.appTransUrl";

	/** 以下缴费产品使用，其余产品用不到，无视即可 */
	// 前台请求地址
	public static final String JF_SDK_FRONT_TRANS_URL= "acpsdk_cups.jfFrontTransUrl";
	// 后台请求地址
	public static final String JF_SDK_BACK_TRANS_URL="acpsdk_cups.jfBackTransUrl";
	// 单笔查询请求地址
	public static final String JF_SDK_SINGLE_QUERY_URL="acpsdk_cups.jfSingleQueryUrl";
	// 有卡交易地址
	public static final String JF_SDK_CARD_TRANS_URL="acpsdk_cups.jfCardTransUrl";
	// App交易地址
	public static final String JF_SDK_APP_TRANS_URL="acpsdk_cups.jfAppTransUrl";
	// 人到人
	public static final String QRC_BACK_TRANS_URL="acpsdk_cups.qrcBackTransUrl";
	// 人到人
	public static final String QRC_B2C_ISS_BACK_TRANS_URL="acpsdk_cups.qrcB2cIssBackTransUrl";
	// 人到人
	public static final String QRC_B2C_MER_BACK_TRANS_URL="acpsdk_cups.qrcB2cMerBackTransUrl";
	
	
	/** 配置文件中签名证书路径常量. */
	public static final String CUPS_SDK_SIGNCERT_PATH = "acpsdk_cups.signCert.path";
	/** 配置文件中签名证书密码常量. */
	public static final String CUPS_SDK_SIGNCERT_PWD = "acpsdk_cups.signCert.pwd";
	/** 配置文件中签名证书类型常量. */
	public static final String CUPS_SDK_SIGNCERT_TYPE = "acpsdk_cups.signCert.type";
	/** 配置文件中密码加密证书路径常量. */
	public static final String CUPS_SDK_ENCRYPTCERT_PATH = "acpsdk_cups.encryptCert.path";
	/** 配置文件中磁道加密证书路径常量. */
	public static final String CUPS_SDK_ENCRYPTTRACKCERT_PATH = "acpsdk_cups.encryptTrackCert.path";
	/** 配置文件中磁道加密公钥模数常量. */
	public static final String CUPS_SDK_ENCRYPTTRACKKEY_MODULUS = "acpsdk_cups.encryptTrackKey.modulus";
	/** 配置文件中磁道加密公钥指数常量. */
	public static final String CUPS_SDK_ENCRYPTTRACKKEY_EXPONENT = "acpsdk_cups.encryptTrackKey.exponent";
	/** 配置文件中验证签名证书目录常量. */
	public static final String CUPS_SDK_VALIDATECERT_DIR = "acpsdk_cups.validateCert.dir";

	/** 配置文件中是否加密cvn2常量. */
	public static final String SDK_CVN_ENC = "acpsdk_cups.cvn2.enc";
	/** 配置文件中是否加密cvn2有效期常量. */
	public static final String SDK_DATE_ENC = "acpsdk_cups.date.enc";
	/** 配置文件中是否加密卡号常量. */
	public static final String SDK_PAN_ENC = "acpsdk_cups.pan.enc";
	/** 配置文件中证书使用模式 */
	public static final String SDK_SINGLEMODE = "acpsdk_cups.singleMode";
	/** 配置文件中安全密钥 */
	public static final String SDK_SECURITYKEY = "acpsdk_cups.secureKey";
	/** 配置文件中根证书路径常量  */
	public static final String SDK_ROOTCERT_PATH = "acpsdk_cups.rootCert.path";
	/** 配置文件中根证书路径常量  */
	public static final String SDK_MIDDLECERT_PATH = "acpsdk_cups.middleCert.path";
	/** 配置是否需要验证验签证书CN，除了false之外的值都当true处理 */
	public static final String CUPS_SDK_IF_VALIDATE_CN_NAME = "acpsdk_cups.ifValidateCNName";
	/** 配置是否需要验证https证书，除了true之外的值都当false处理 */
	public static final String CUPS_SDK_IF_VALIDATE_REMOTE_CERT = "acpsdk_cups.ifValidateRemoteCert";
	/** signmethod */
	public static final String CUPS_SDK_SIGN_METHOD ="acpsdk_cups.signMethod";
	/** version */
	public static final String SDK_VERSION = "acpsdk_cups.version";
	/** 后台通知地址  */
	public static final String SDK_BACKURL = "acpsdk_cups.backUrl";
	/** 前台通知地址  */
	public static final String SDK_FRONTURL = "acpsdk_cups.frontUrl";
	/** 操作对象. */
	private static CupsPayConfig config = new CupsPayConfig();
	/** 属性文件对象. */
	private Properties properties;

	private CupsPayConfig() {
		super();
	}

	/**
	 * 获取config对象.
	 * @return
	 */
	public static CupsPayConfig getConfig() {
		return config;
	}

	/**
	 * 从properties文件加载
	 * 
	 * @param rootPath
	 *            不包含文件名的目录.
	 */
	public void loadPropertiesFromPath(String rootPath) {
		if (StringUtils.isNotBlank(rootPath)) {
			LogUtil.writeLog("从路径读取配置文件: " + rootPath+File.separator+FILE_NAME);
			File file = new File(rootPath + File.separator + FILE_NAME);
			InputStream in = null;
			if (file.exists()) {
				try {
					in = new FileInputStream(file);
					properties = new Properties();
					properties.load(in);
					loadProperties(properties);
				} catch (FileNotFoundException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				} finally {
					if (null != in) {
						try {
							in.close();
						} catch (IOException e) {
							LogUtil.writeErrorLog(e.getMessage(), e);
						}
					}
				}
			} else {
				// 由于此时可能还没有完成LOG的加载，因此采用标准输出来打印日志信息
				LogUtil.writeErrorLog(rootPath + FILE_NAME + "不存在,加载参数失败");
			}
		} else {
			loadPropertiesFromSrc();
		}

	}

	/**
	 * 从classpath路径下加载配置参数
	 */
	public void loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			LogUtil.writeLog("从classpath: " +CupsPayConfig.class.getClassLoader().getResource("").getPath()+" 获取属性文件"+FILE_NAME);
			in = CupsPayConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
			if (null != in) {
				properties = new Properties();
				try {
					properties.load(in);
				} catch (IOException e) {
					throw e;
				}
			} else {
				LogUtil.writeErrorLog(FILE_NAME + "属性文件未能在classpath指定的目录下 "+CupsPayConfig.class.getClassLoader().getResource("").getPath()+" 找到!");
				return;
			}
			loadProperties(properties);
		} catch (IOException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LogUtil.writeErrorLog(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 根据传入的 {@link #load(java.util.Properties)}对象设置配置参数
	 * 
	 * @param pro
	 */
	public void loadProperties(Properties pro) {
		LogUtil.writeLog("开始从属性文件中加载配置项");
		String value = null;
		
		value = pro.getProperty(CUPS_SDK_SIGNCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPathCups = value.trim();
			LogUtil.writeLog("配置项：私钥签名证书路径==>"+CUPS_SDK_SIGNCERT_PATH +"==>"+ value+" 已加载");
		}
		value = pro.getProperty(CUPS_SDK_SIGNCERT_PWD);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertPwdCups = value.trim();
			LogUtil.writeLog("配置项：私钥签名证书密码==>"+CUPS_SDK_SIGNCERT_PWD +" 已加载");
		}
		value = pro.getProperty(CUPS_SDK_SIGNCERT_TYPE);
		if (!SDKUtil.isEmpty(value)) {
			this.signCertTypeCups = value.trim();
			LogUtil.writeLog("配置项：私钥签名证书类型==>"+CUPS_SDK_SIGNCERT_TYPE +"==>"+ value+" 已加载");
		}
		value = pro.getProperty(CUPS_SDK_ENCRYPTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptCertPathCups = value.trim();
			LogUtil.writeLog("配置项：敏感信息加密证书==>"+CUPS_SDK_ENCRYPTCERT_PATH +"==>"+ value+" 已加载");
		}
		value = pro.getProperty(CUPS_SDK_VALIDATECERT_DIR);
		if (!SDKUtil.isEmpty(value)) {
			this.validateCertDirCups = value.trim();
			LogUtil.writeLog("配置项：验证签名证书路径(这里配置的是目录，不要指定到公钥文件)==>"+CUPS_SDK_VALIDATECERT_DIR +"==>"+ value+" 已加载");
		}
		value = pro.getProperty(SDK_FRONT_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACK_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_BATTRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_FILETRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.getProperty(SDK_SIGNQ_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.getProperty(SDK_CARD_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.getProperty(SDK_APP_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.getProperty(CUPS_SDK_ENCRYPTTRACKCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPathCups = value.trim();
		}

		value = pro.getProperty(SDK_SECURITYKEY);
		if (!SDKUtil.isEmpty(value)) {
			this.secureKey = value.trim();
		}
		value = pro.getProperty(SDK_ROOTCERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.rootCertPath = value.trim();
		}
		value = pro.getProperty(SDK_MIDDLECERT_PATH);
		if (!SDKUtil.isEmpty(value)) {
			this.middleCertPath = value.trim();
		}

		/**缴费部分**/
		value = pro.getProperty(JF_SDK_FRONT_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfFrontRequestUrl = value.trim();
		}

		value = pro.getProperty(JF_SDK_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfBackRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_SINGLE_QUERY_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfSingleQueryUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_CARD_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfCardRequestUrl = value.trim();
		}
		
		value = pro.getProperty(JF_SDK_APP_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.jfAppRequestUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_ISS_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cIssBackTransUrl = value.trim();
		}
		
		value = pro.getProperty(QRC_B2C_MER_BACK_TRANS_URL);
		if (!SDKUtil.isEmpty(value)) {
			this.qrcB2cMerBackTransUrl = value.trim();
		}

		value = pro.getProperty(CUPS_SDK_ENCRYPTTRACKKEY_EXPONENT);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyExponentCups = value.trim();
		}

		value = pro.getProperty(CUPS_SDK_ENCRYPTTRACKKEY_MODULUS);
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackKeyModulusCups = value.trim();
		}
		
		value = pro.getProperty(CUPS_SDK_IF_VALIDATE_CN_NAME);
		if (!SDKUtil.isEmpty(value)) {
			if( CupsConstants.FALSE_STRING.equals(value.trim()))
					this.ifValidateCNNameCups = false;
		}
		
		value = pro.getProperty(CUPS_SDK_IF_VALIDATE_REMOTE_CERT);
		if (!SDKUtil.isEmpty(value)) {
			if( CupsConstants.TRUE_STRING.equals(value.trim()))
					this.ifValidateRemoteCertCups = true;
		}
		
		value = pro.getProperty(CUPS_SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethodCups = value.trim();
		}
		
		value = pro.getProperty(CUPS_SDK_SIGN_METHOD);
		if (!SDKUtil.isEmpty(value)) {
			this.signMethodCups = value.trim();
		}
		value = pro.getProperty(SDK_VERSION);
		if (!SDKUtil.isEmpty(value)) {
			this.version = value.trim();
		}
		value = pro.getProperty(SDK_FRONTURL);
		if (!SDKUtil.isEmpty(value)) {
			this.frontUrl = value.trim();
		}
		value = pro.getProperty(SDK_BACKURL);
		if (!SDKUtil.isEmpty(value)) {
			this.backUrl = value.trim();
		}
	}


	public String getFrontRequestUrl() {
		return frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPathCups() {
		return signCertPathCups;
	}

	public void setSignCertPathCups(String signCertPathCups) {
		this.signCertPathCups = signCertPathCups;
	}

	public String getSignCertPwdCups() {
		return signCertPwdCups;
	}

	public void setSignCertPwdCups(String signCertPwdCups) {
		this.signCertPwdCups = signCertPwdCups;
	}

	public String getSignCertTypeCups() {
		return signCertTypeCups;
	}

	public void setSignCertTypeCups(String signCertTypeCups) {
		this.signCertTypeCups = signCertTypeCups;
	}

	public String getEncryptCertPathCups() {
		return encryptCertPathCups;
	}

	public void setEncryptCertPathCups(String encryptCertPathCups) {
		this.encryptCertPathCups = encryptCertPathCups;
	}
	
	public String getValidateCertDirCups() {
		return validateCertDirCups;
	}

	public void setValidateCertDirCups(String validateCertDirCups) {
		this.validateCertDirCups = validateCertDirCups;
	}

	public String getSingleQueryUrl() {
		return singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public String getSignCertDirCups() {
		return signCertDirCups;
	}

	public void setSignCertDirCups(String signCertDirCups) {
		this.signCertDirCups = signCertDirCups;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getCardRequestUrl() {
		return cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}
	
	public String getEncryptTrackCertPathCups() {
		return encryptTrackCertPathCups;
	}

	public void setEncryptTrackCertPathCups(String encryptTrackCertPathCups) {
		this.encryptTrackCertPathCups = encryptTrackCertPathCups;
	}
	
	public String getJfFrontRequestUrl() {
		return jfFrontRequestUrl;
	}

	public void setJfFrontRequestUrl(String jfFrontRequestUrl) {
		this.jfFrontRequestUrl = jfFrontRequestUrl;
	}

	public String getJfBackRequestUrl() {
		return jfBackRequestUrl;
	}

	public void setJfBackRequestUrl(String jfBackRequestUrl) {
		this.jfBackRequestUrl = jfBackRequestUrl;
	}

	public String getJfSingleQueryUrl() {
		return jfSingleQueryUrl;
	}

	public void setJfSingleQueryUrl(String jfSingleQueryUrl) {
		this.jfSingleQueryUrl = jfSingleQueryUrl;
	}

	public String getJfCardRequestUrl() {
		return jfCardRequestUrl;
	}

	public void setJfCardRequestUrl(String jfCardRequestUrl) {
		this.jfCardRequestUrl = jfCardRequestUrl;
	}

	public String getJfAppRequestUrl() {
		return jfAppRequestUrl;
	}

	public void setJfAppRequestUrl(String jfAppRequestUrl) {
		this.jfAppRequestUrl = jfAppRequestUrl;
	}

	public String getSingleMode() {
		return singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}

	public String getEncryptTrackKeyExponentCups() {
		return encryptTrackKeyExponentCups;
	}

	public void setEncryptTrackKeyExponentCups(String encryptTrackKeyExponentCups) {
		this.encryptTrackKeyExponentCups = encryptTrackKeyExponentCups;
	}

	public String getEncryptTrackKeyModulusCups() {
		return encryptTrackKeyModulusCups;
	}

	public void setEncryptTrackKeyModulusCups(String encryptTrackKeyModulusCups) {
		this.encryptTrackKeyModulusCups = encryptTrackKeyModulusCups;
	}
	
	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String securityKey) {
		this.secureKey = securityKey;
	}
	
	public String getMiddleCertPath() {
		return middleCertPath;
	}

	public void setMiddleCertPath(String middleCertPath) {
		this.middleCertPath = middleCertPath;
	}
	
	public boolean isIfValidateCNNameCups() {
		return ifValidateCNNameCups;
	}

	public void setIfValidateCNNameCups(boolean ifValidateCNNameCups) {
		this.ifValidateCNNameCups = ifValidateCNNameCups;
	}

	public boolean isIfValidateRemoteCertCups() {
		return ifValidateRemoteCertCups;
	}

	public void setIfValidateRemoteCertCups(boolean ifValidateRemoteCertCups) {
		this.ifValidateRemoteCertCups = ifValidateRemoteCertCups;
	}

	public String getSignMethodCups() {
		return signMethodCups;
	}

	public void setSignMethodCups(String signMethodCups) {
		this.signMethodCups = signMethodCups;
	}
	public String getQrcBackTransUrl() {
		return qrcBackTransUrl;
	}

	public void setQrcBackTransUrl(String qrcBackTransUrl) {
		this.qrcBackTransUrl = qrcBackTransUrl;
	}

	public String getQrcB2cIssBackTransUrl() {
		return qrcB2cIssBackTransUrl;
	}

	public void setQrcB2cIssBackTransUrl(String qrcB2cIssBackTransUrl) {
		this.qrcB2cIssBackTransUrl = qrcB2cIssBackTransUrl;
	}

	public String getQrcB2cMerBackTransUrl() {
		return qrcB2cMerBackTransUrl;
	}

	public void setQrcB2cMerBackTransUrl(String qrcB2cMerBackTransUrl) {
		this.qrcB2cMerBackTransUrl = qrcB2cMerBackTransUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getRootCertPath() {
		return rootCertPath;
	}

	public void setRootCertPath(String rootCertPath) {
		this.rootCertPath = rootCertPath;
	}
	
}
