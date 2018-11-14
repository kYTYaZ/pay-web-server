package com.wldk.framework.dao;

import com.wldk.framework.db.DataSourceName;
import com.wldk.framework.db.PageVariable;
import com.wldk.framework.db.ParameterProvider;
import com.wldk.framework.db.parse.VelocitySQLParse;
import com.wldk.framework.spring.db.dialect.SpringDialect;
import com.wldk.framework.spring.db.dialect.SpringDialectFactory;
import com.wldk.framework.utils.FieldToPropertyUtils;
import com.wldk.framework.utils.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring数据库操作基类
 * @author Administrator
 *
 */
public class JdbcSpringDaoFromWorkManagerUtil {
	
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**Spring*/
	protected JdbcTemplate jdbcTemplate;
	/**数据库类型*/
	protected DataSourceName dsName;
	/**
	 * 构造方法模式，默认数据源：
	 */
	protected JdbcSpringDaoFromWorkManagerUtil() {
		this.dsName = DataSourceName.DB2;
	}

    /**
     * 构造方法
     * @return
     */
	protected JdbcSpringDaoFromWorkManagerUtil(DataSourceName dsName) {
		this.dsName = dsName;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	 /**
     * 数据库批量执行删除
     * @param SQL 操作语句集合
     * @return
     * @throws Exception
     */
	public int[] executeDelete(String[] SQL) throws Exception {
		return executeUpdate(SQL);
	}
	
	 /**
     * 数据库批量执行更新
     * @param SQL 操作语句集合
     * @return
     * @throws Exception
     */
	public int[] executeUp(String[] SQL) throws Exception {
		return executeUpdate(SQL);
	}
	 /**
     * 数据库批量执行保存
     * @param SQL 操作语句集合
     * @return
     * @throws Exception
     */
	public int[] executeSQL(String[] SQL) throws Exception {
		return executeUpdate(SQL);
	}

	 /**
     * 数据库执行sql语句
     * @param SQL 操作语句集合
     * @return
     * @throws Exception
     */
	public void  executeSQL(String SQL) throws Exception {
		executeSql(SQL);
	}
	
	 /**
     * 数据库模板执行删除
     * @param SQL 操作语句
     * @return
     * @throws Exception
     */
	public int executeDelete(String SQL)throws Exception {
		return executeUpdate(SQL);
	}
	
	/**
	 * 数据库模版执行查询语句
	 * @param SQL 查询语句
	 * @return
	 * @throws Exception
	 */
	public int queryTotalNumber(Map<String, String> queryMap, String filename)throws Exception{
		
		ParameterProvider paramProvider = new ParameterProvider();
	    paramProvider.addParameters(queryMap);
		String sql = new VelocitySQLParse(paramProvider, filename).parse();
		return queryForTotalNumber(sql);
	}
	
	/**
	 * 查询某列数据返回list
	 * @param queryMap
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public List<String> queryForList(Map<String, String> queryMap, String filename) throws Exception {

		ParameterProvider paramProvider = new ParameterProvider();
		paramProvider.addParameters(queryMap);
		String sql = new VelocitySQLParse(paramProvider, filename).parse();
		return queryOneColumn(sql);
	}
	
	
	
    /**
     * 数据库模板执行更新
     * @param SQL 操作语句
     * @return
     * @throws Exception
     */
	public int executeUp(String SQL)throws Exception {
		return this.executeUpdate(SQL);
	}
	
    /**
     * 数据库模板执行保存
     * @param paramProvider 更新参数参数
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
	public int executeSave(String SQL)throws Exception {
		return this.executeUpdate(SQL);
	}
	
	
    /**
     * 数据库模板执行删除
     * @param paramProvider 参数对象的提供器封装类
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
	public boolean executeDelete(ParameterProvider paramProvider, String filename)throws Exception {
		return executeUpdate(paramProvider, filename);
	}
	
    /**
     * 数据库模板执行更新
     * @param paramProvider  更新参数参数
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
	public boolean executeUpdate(ParameterProvider paramProvider, String filename)throws Exception {
		return this.executeUpdate(new VelocitySQLParse(paramProvider,filename).parse())>=1?true:false;
	}
	
	/**
     * 数据库模板执行更新
     * @param paramProvider  更新参数参数
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
    public boolean executeUpdate(Map<String,String> params, String filename)throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(params);
        return this.executeUpdate(new VelocitySQLParse(paramProvider,filename).parse())>=1?true:false;
    }
    
    /**
     * 数据库模板执行更新
     * @param paramProvider  更新参数参数
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
    public void execute(Map<String,String> params, String filename)throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(params);
        this.executeSQL(new VelocitySQLParse(paramProvider,filename).parse());
    }
    
    /**
     * 数据库模板批量执行更新
     * @param paramProvider  更新参数参数
     * @param filename vm模板（SQL模板）
     * @return sky
     * @throws Exception
     */
    public boolean execute(List<Map<String,String>> params, String filename)throws Exception {
    	boolean flag = true;
        ParameterProvider paramProvider = new ParameterProvider();
        String[] strArray = new String [params.size()] ;
        for (int i = 0; i < params.size(); i++) {
        	paramProvider.addParameters(params.get(i));
        	strArray[i] = new VelocitySQLParse(paramProvider,filename).parse();
		}
        int[] result= this.executeUpdate(strArray);
        for (int i = 0; i < result.length; i++) {
        	if(result[i] < 1){
        		flag = false;
        	}
		}
        return flag;
    }
	
    /**
     * 数据库模板执行保存
     * @param paramProvider 更新参数参数
     * @param filename vm模板（SQL模板）
     * @return
     * @throws Exception
     */
	public int executeSave(ParameterProvider paramProvider, String filename)throws Exception {
		return this.executeUpdate(new VelocitySQLParse(paramProvider,filename).parse());
	}
	
     /**
      * 数据库查询（不支持分页)
      * @param paramProvider 查询参数
      * @param filename vm模板（SQL模板）
      * @param dsName 数据库驱动类型
      * @return
      * @throws Exception
      */
	public List<Map<String, Object>> queryMap(ParameterProvider paramProvider,String filename, DataSourceName dsName) throws Exception {
		return getResultMap(this.jdbcTemplate, paramProvider, filename, null,dsName);
	}
   /**
    * 数据库查询（支持分页）
    * @param paramProvider 查询参数
    * @param filename vm模板（SQL模板）
    * @param page  分页对象
    * @param dsName 数据库驱动类型
    * @return
    * @throws Exception
    */
	public List<Map<String, Object>> queryMap(ParameterProvider paramProvider, String filename,PageVariable page, 
			DataSourceName dsName) throws Exception {
		return getResultMap(this.jdbcTemplate, paramProvider, filename, page,dsName);
	}
   
	/**
	 * 数据库查询（支持分页,数据库类型默认Oracle）
	 * @param paramProvider 查询参数
	 * @param filename vm模板（SQL模板）
	 * @param page 分页对象
	 * @return
	 */
	public List<Map<String, Object>> queryMap(ParameterProvider paramProvider, String filename, PageVariable page)throws Exception {
		return getResultMap(this.jdbcTemplate, paramProvider, filename, page,this.dsName);
	}
    
	/**
	 * 数据库查询（不支持分页,数据库类型默认Oracle）
	 * @param paramProvider  查询参数
	 * @param filename  vm模板（SQL模板）
	 * @return 结果集合(List)
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryMap(ParameterProvider paramProvider,String filename) throws Exception {
		return getResultMap(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
	}
	
	/**
     * 数据库查询（不支持分页,数据库类型默认Oracle）
     * @param paramProvider  查询参数
     * @param filename  vm模板（SQL模板）
     * @return 结果集合(List)
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String,String[]> params,String filename) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(params);
        return getResultMap(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
    }
    
    /**
     * 数据库查询（返回单条结果集,数据库类型默认Oracle）
     * @param params 查询参数
     * @param filename   vm模板（SQL模板）
     * @return 结果集合(Map)
     * @throws Exception
     */
    public Map<String, Object> queryMapObjectMap(Map<String,String[]> params,String filename) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(params);
        List<Map<String, Object>> list= getResultMap(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
        if(list!=null && list.size()>0){
        	return list.get(0);
        }else{
        	return new HashMap<String, Object>();
        }
    }
    
	/**
     * 数据库查询（不支持分页,数据库类型默认Oracle）
     * @param paramProvider  查询参数
     * @param filename  vm模板（SQL模板）
     * @return 结果集合(List)
     * @throws Exception
     */
    public  List<Map<String, Object>> queryMapS(Map<String,String> params, String filename)throws Exception {
    	ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(params);
        return getResultMap(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
    } 
    
    /**
     * 数据库查询（返回单条结果集,数据库类型默认Oracle）
     * @param params 查询参数
     * @param filename   vm模板（SQL模板）
     * @return 结果集合(Map)
     * @throws Exception
     */
    public Map<String, Object> queryMapObjectMapS(Map<String,String> params,String filename) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(params);
        List<Map<String, Object>> list= getResultMap(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
        if(list!=null && list.size()>0){
        	return list.get(0);
        }else{
        	return new HashMap<String, Object>();
        }
    }

	/**
	 * 数据库查询（支持分页）
	 * @param SQL 查询语句
	 * @param page 分页对象
	 * @param dsName 数据库驱动类型
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryPageMap(String SQL,PageVariable page, DataSourceName dsName) throws Exception {
		return getResultMap(this.jdbcTemplate, SQL, page, dsName);
	}
     
	/**
	 * 数据库查询（支持分页,数据库类型默认Oracle）
	 * @param SQL 查询语句
	 * @param page 分页对象
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryPageMap(String SQL, PageVariable page)throws Exception {
		return getResultMap(this.jdbcTemplate, SQL, page, this.dsName);
	}
    
	/**
	 * 数据库查询（不支持分页）
	 * @param SQL 查询语句
	 * @param dsName 数据库驱动类型
	 * @return 
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryeMap(String SQL, DataSourceName dsName)throws Exception {
		return getResultMap(this.jdbcTemplate, SQL, null, dsName);
	}
     
	/**
	 * 数据库查询（不支持分页,数据库类型默认Oracle）
	 * @param SQL 查询语句
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryeMap(String SQL) throws Exception {
		return getResultMap(this.jdbcTemplate, SQL, null, this.dsName);
	}
	

    /**
     * 数据库查询（不支持分页)
     * @param paramProvider 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库驱动类型
     * @return
     * @throws Exception
     */
	public List<Object[]> queryArrays(ParameterProvider paramProvider,String filename, DataSourceName dsName) throws Exception {
		return getResultArrays(this.jdbcTemplate, paramProvider, filename, null,dsName);
	}
  /**
   * 数据库查询（支持分页）
   * @param paramProvider 查询参数
   * @param filename vm模板（SQL模板）
   * @param page  分页对象
   * @param dsName 数据库驱动类型
   * @return
   * @throws Exception
   */
	public List<Object[]> queryArrays(ParameterProvider paramProvider, String filename,PageVariable page, 
			DataSourceName dsName) throws Exception {
		return getResultArrays(this.jdbcTemplate, paramProvider, filename, page,dsName);
	}
  
	/**
	 * 数据库查询（支持分页,数据库类型默认Oracle）
	 * @param paramProvider 查询参数
	 * @param filename vm模板（SQL模板）
	 * @param page 分页对象
	 * @return
	 */
	public List<Object[]> queryArrays(ParameterProvider paramProvider, String filename, PageVariable page)throws Exception {
		return getResultArrays(this.jdbcTemplate, paramProvider, filename, page,this.dsName);
	}
   
	/**
	 * 数据库查询（不支持分页,数据库类型默认Oracle）
	 * @param paramProvider  查询参数
	 * @param filename  vm模板（SQL模板）
	 * @return 结果集合(List)
	 * @throws Exception
	 */
	public List<Object[]> queryArrays(ParameterProvider paramProvider,String filename) throws Exception {
		return getResultArrays(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
	}
	
	/**
    * 数据库查询（不支持分页,数据库类型默认Oracle）
    * @param paramProvider  查询参数
    * @param filename  vm模板（SQL模板）
    * @return 结果集合(List)
    * @throws Exception
    */
   public List<Object[]> queryArrays(Map<String,String[]> params,String filename) throws Exception {
       ParameterProvider paramProvider = new ParameterProvider();
       paramProvider.addParameter(params);
       return getResultArrays(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
   }
   
   /**
    * 数据库查询（返回单条结果集,数据库类型默认Oracle）
    * @param params 查询参数
    * @param filename   vm模板（SQL模板）
    * @return 结果集合(Map)
    * @throws Exception
    */
   public Object[] queryArrayObject(Map<String,String[]> params,String filename) throws Exception {
       ParameterProvider paramProvider = new ParameterProvider();
       paramProvider.addParameter(params);
       List<Object[]> list= getResultArrays(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
       if(list!=null && list.size()>0){
       	return list.get(0);
       }else{
       	return new Object[0];
       }
   }
   
	/**
    * 数据库查询（不支持分页,数据库类型默认Oracle）
    * @param paramProvider  查询参数
    * @param filename  vm模板（SQL模板）
    * @return 结果集合(List)
    * @throws Exception
    */
   public List<Object[]> queryArray(Map<String,String> params,String filename) throws Exception {
       ParameterProvider paramProvider = new ParameterProvider();
       paramProvider.addParameters(params);
       return getResultArrays(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
   }

   /**
    * 数据库查询（返回单条结果集,数据库类型默认Oracle）
    * @param params 查询参数
    * @param filename   vm模板（SQL模板）
    * @return 结果集合(Map)
    * @throws Exception
    */
   public Object[] queryArrayObjectS(Map<String,String> params,String filename) throws Exception {
       ParameterProvider paramProvider = new ParameterProvider();
       paramProvider.addParameters(params);
       List<Object[]> list= getResultArrays(this.jdbcTemplate, paramProvider, filename, null,this.dsName);
       if(list!=null && list.size()>0){
       	return list.get(0);
       }else{
       	return new Object[0];
       }
   }
	/**
	 * 数据库查询（支持分页）
	 * @param SQL 查询语句
	 * @param page 分页对象
	 * @param dsName 数据库驱动类型
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryArrays(String SQL,PageVariable page, DataSourceName dsName) throws Exception {
		return getResultArrays(this.jdbcTemplate, SQL, page, dsName);
	}
    
	/**
	 * 数据库查询（支持分页,数据库类型默认Oracle）
	 * @param SQL 查询语句
	 * @param page 分页对象
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryArrays(String SQL, PageVariable page)throws Exception {
		return getResultArrays(this.jdbcTemplate, SQL, page, this.dsName);
	}
   
	/**
	 * 数据库查询（不支持分页）
	 * @param SQL 查询语句
	 * @param dsName 数据库驱动类型
	 * @return 
	 * @throws Exception
	 */
	public List<Object[]> queryArrays(String SQL, DataSourceName dsName)throws Exception {
		return getResultArrays(this.jdbcTemplate, SQL, null, dsName);
	}
    
	/**
	 * 数据库查询（不支持分页,数据库类型默认Oracle）
	 * @param SQL 查询语句
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryArrays(String SQL)throws Exception{
	    return getResultArrays(this.jdbcTemplate, SQL, null, this.dsName);
	}
	
	
	/**
	 * SQL数据库驱动查询（支持分页）
	 * @param jdbcTemplate  spring数据源
	 * @param SQL 查询语句
	 * @param page  分页对象
	 * @param dsName 数据库驱动类型
	 * @return
	 * @throws Exception
	 */
	protected List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,String SQL, PageVariable page, DataSourceName dsName)
			throws Exception {
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		long startTime=System.currentTimeMillis();  
		/**根据数据库驱动类型选择数据库模板*/
		SpringDialect dialect = SpringDialectFactory.getInstance().getDialect(dsName);
		if(dialect!=null){
			/**支持分页*/
			if (page != null) {
				list=dialect.getResultMap(jdbcTemplate, SQL);
			}
			/**不支持分页*/
			list=dialect.getResultMap(jdbcTemplate, SQL, page);
		}else{
			log.error("无对应的SQL数据库驱动类型...");
		}
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return list;
	}

    /**
     * SQL模板数据库驱动查询（支持分页）
     * @param jdbcTemplate spring数据源
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param filename SQL模板
     * @param page  分页对象
     * @param dsName 数据库驱动类型
     * @return
     * @throws Exception
     */
	protected List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,ParameterProvider paramProvider, String filename,
			PageVariable page, DataSourceName dsName)throws Exception{
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		long startTime=System.currentTimeMillis();  
		/**根据数据库驱动类型选择数据库模板*/
		SpringDialect dialect = SpringDialectFactory.getInstance().getDialect(dsName);
		if(dialect!=null){
			/**支持分页*/
			if (page != null) {
				list=dialect.getResultMap(jdbcTemplate, new VelocitySQLParse(paramProvider, filename), page);
			}else{
				String sql = new VelocitySQLParse(paramProvider, filename).parse();
				this.log.debug("SQL:[" + sql.trim() + "]");
				if (StringUtil.isNotEmpty(sql)) {
					list=FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
				} else {
					this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
					throw new SQLException("Can not acquire SQL of Pagination ");
				}
			}
			
		}else{
			log.error("无对应的SQL数据库驱动类型...");
		}
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return list;
	}
	

	
	/**
	 * SQL数据库驱动查询（支持分页）
	 * @param jdbcTemplate spring数据源
	 * @param SQL 操作语句
	 * @param page 分页对象
	 * @param dsName 数据库驱动类型
	 * @return
	 * @throws Exception
	 */
	protected List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,String SQL, PageVariable page, DataSourceName dsName)
            throws Exception {
		List<Object[]> list=new ArrayList<Object[]>();
		long startTime=System.currentTimeMillis();  
        /**根据数据库驱动类型选择数据库模板*/
        SpringDialect dialect = SpringDialectFactory.getInstance().getDialect(dsName);
        if(dialect!=null){
			/**支持分页*/
			if (page != null) {
				list=dialect.getResultArrays(jdbcTemplate, SQL, page);
			}
        	
			list=dialect.getResultArrays(jdbcTemplate, SQL);
        }else{
			log.error("无对应的SQL数据库驱动类型...");
        }
        long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
        return list;
        
    }	
	
    /**
     * SQL模板数据库驱动查询（支持分页）
     * @param jdbcTemplate spring数据源
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param filename SQL模板
     * @param page  分页对象
     * @param dsName 数据库驱动类型
     * @return
     * @throws Exception
     */
	protected List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,ParameterProvider paramProvider, String filename,
			PageVariable page, DataSourceName dsName) throws Exception {
		List<Object[]> list=new ArrayList<Object[]>();
		long startTime=System.currentTimeMillis();  
		/**根据数据库驱动类型选择数据库模板*/
		SpringDialect dialect = SpringDialectFactory.getInstance().getDialect(dsName);
		if(dialect!=null){
			/**支持分页*/
			if (page != null) {
				list=dialect.getResultArrays(jdbcTemplate, new VelocitySQLParse(paramProvider, filename), page);
			}
			/**不支持分页*/
			list= dialect.getResultArrays(jdbcTemplate, new VelocitySQLParse(paramProvider, filename));
		}else{
			log.error("无对应的SQL数据库驱动类型...");
		}
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return list;	
	}
	
    /**
     * 数据库更新操作(支持：新增、修改、删除)
     * @param SQL 操作语句
     * @return
     * @throws Exception
     */
	protected int executeUpdate(String SQL) throws Exception {
		int count=0;
		long startTime=System.currentTimeMillis();  
		this.log.debug("SQL:[" + SQL.trim() + "]");
		count=this.jdbcTemplate.update(SQL);
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return count;
	}
	/**
	 * 数据库批量更新操作(支持：新增、修改、删除)
	 * @param SQL
	 * @return 操作语句
	 * @throws Exception
	 */
	protected int[] executeUpdate(String[] SQL) throws Exception {
		int[] count=new int[]{0};
		long startTime=System.currentTimeMillis();  
		this.log.debug("SQL:[" + SQL + "]");
		count=this.jdbcTemplate.batchUpdate(SQL);
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return count;
	}
	
	/**
	 *执行sql语句
	 * @param SQL
	 * @throws Exception
	 */
	protected void executeSql(String SQL) throws Exception {
		long startTime=System.currentTimeMillis();  
		this.log.debug("SQL:[" + SQL + "]");
		this.jdbcTemplate.execute(SQL);
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
	}
	
	/**
	 *查询符合条件的总数
	 * @param SQL
	 * @throws Exception
	 */
	protected int queryForTotalNumber(String SQL) throws Exception {
		long startTime=System.currentTimeMillis();  
		this.log.debug("SQL:[" + SQL + "]");
		int totalNumber = this.jdbcTemplate.queryForInt(SQL);
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return totalNumber;
	}
	
	protected List<String> queryOneColumn(String SQL)throws Exception{
		long startTime=System.currentTimeMillis();  
		this.log.debug("SQL:[" + SQL + "]");
		List<String> result = this.jdbcTemplate.queryForList(SQL, String.class);
		long endTime=System.currentTimeMillis();  
		log.debug("SQL执行共耗时："+(endTime-startTime)+" ms ]"); 
		return result;
	}
}
