package com.wldk.framework.dao;

import com.wldk.framework.db.DataOperationCreator;
import com.wldk.framework.db.DataSourceName;
import com.wldk.framework.db.ParameterProvider;
import com.wldk.framework.db.TransactionProxy;
import com.wldk.framework.db.parse.VelocitySQLParse;
import com.wldk.framework.utils.StringUtil;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTransactionManager {
    /** 日志对象 */
    protected static Logger log = LoggerFactory.getLogger(JdbcTransactionManager.class);
    private DataOperationCreator dac;
    protected DataSourceName dsName;
    
    public JdbcTransactionManager() {
        this.dsName = DataSourceName.DB2;
        dac = DataOperationCreator.getInstance(dsName);
    }

    /**
     * 通过vm模板执行数据库更新、插入语句
     * 
     * @param paramProvider 参数
     * @param fileName vm模板（sql模板）
     * @return
     * @throws Exception
     */
    public boolean updateSql(Map<String, String> param, String fileName) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(param);
        return setTransactionProxyVm(paramProvider, fileName);

    }

    /**
     * 单个事物SQL执行((支持：新增、修改、删除))
     * 
     * @param sql
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(ParameterProvider paramProvider, String vmName) throws Exception {
        if (StringUtil.isEmpty(vmName)) {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
            return false;
        } else {
            if (dac != null) {
                TransactionProxy tx = WldkJdbcUtil.currentProxy();
                tx.setTarget(dac.createUpdate(new VelocitySQLParse(paramProvider, vmName)));
                String transFilter = WldkJdbcUtil.getTransFilter();
                if(StringUtil.isEmpty(transFilter)){
                    tx.commit();
                }
                return true;
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
                return false;
            }

        }
    }
    
    /**
     * 通过vm模板执行数据库查询(不带分页)
     * 
     * @param parameter 参数
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String[]> parameter, String filename) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return queryMap(paramProvider, filename);
    }
    
    /**
     * 创建查询命令对象(不带分页)
     * 
     * @param paramProvider 查询参数
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Map<String, Object>> queryMap(ParameterProvider paramProvider, String filename) throws Exception {
        return (List<Map<String, Object>>) DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), null).executeMap();
    }
}
