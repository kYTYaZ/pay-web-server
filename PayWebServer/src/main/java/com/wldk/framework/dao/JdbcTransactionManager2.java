package com.wldk.framework.dao;

import com.wldk.framework.db.DataOperationCreator;
import com.wldk.framework.db.DataSourceName;
import com.wldk.framework.db.PageVariable;
import com.wldk.framework.db.ParameterProvider;
import com.wldk.framework.db.TransactionManager;
import com.wldk.framework.db.TransactionProxy;
import com.wldk.framework.db.parse.VelocitySQLParse;
import com.wldk.framework.utils.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库操作集合（支持事物）
 * 
 * @author zhaodk
 * 
 */
public abstract class JdbcTransactionManager2 {

    /** 日志对象 */
    protected static Logger log = LoggerFactory.getLogger(JdbcTransactionManager2.class);

    protected DataSourceName dsName;

    /**
     * 构造方法模式，默认数据源：
     */
    protected JdbcTransactionManager2() {
        this.dsName = DataSourceName.DB2;
    }

    /**
     * 构造方法
     */
    protected JdbcTransactionManager2(DataSourceName dsName) {
        this.dsName = dsName;
    }

    /**
     * 单个事物VM模板SQL执行(支持：新增、修改、删除)
     * 
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param vmName SQL模板
     * @return boolean boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(ParameterProvider paramProvider, String vmName) throws Exception {
        if (StringUtil.isEmpty(vmName)) {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
            return false;
        } else {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    tx.setTarget(dac.createUpdate(new VelocitySQLParse(paramProvider, vmName)));
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                    return false;
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
                return false;
            }

        }
    }

    /**
     * 单个事物SQL执行((支持：新增、修改、删除))
     * 
     * @param sql
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(String sql) throws Exception {
        if (StringUtil.isEmpty(sql)) {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
            return false;
        } else {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    tx.setTarget(dac.createUpdate(sql));
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                    return false;
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
                return false;
            }
        }
    }

    /**
     * 执行insert、update((支持：新增、修改、删除))
     * 
     * @param sql
     * @return 成功条数
     * @throws Exception
     */
    public int updateSql(String sql) throws Exception {
        Integer effect = 0;
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                effect = dac.createUpdate(sql).execute();
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }

        return effect;
    }

    /**
     * 数据删除
     * 
     * @param sql 数据库语句
     * @return
     * @throws Exception
     */
    public int delete(String sql) throws Exception {
        return updateSql(sql);
    }

    /**
     * 数据库删除
     * 
     * @param sql 数据库语句
     * @param dsName 数据库类型
     * @return
     * @throws Exception
     */
    public int delete(String sql, DataSourceName dsName) throws Exception {
        return updateSql(sql, dsName);
    }

    /**
     * 执行insert、update ((支持：新增、修改、删除))
     * 
     * @param sql
     * @return 成功条数
     * @throws Exception
     */
    public int updateSql(String sql, DataSourceName dsName) throws Exception {
        Integer effect = 0;
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                effect = dac.createUpdate(sql).execute();
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return effect;
    }

    /**
     * 批量事物VM模板SQL执行 (支持：新增、修改、删除)
     * 
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param vmName SQL模板
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(Map<ParameterProvider, String> map) throws Exception {
        if (map != null) {
            if (!map.isEmpty() && map.size() > 0) {
                Iterator<Map.Entry<ParameterProvider, String>> it = map.entrySet().iterator();
                DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
                if (dac != null) {
                    TransactionProxy tx = TransactionManager.getInstance().getProxy();
                    if (tx != null) {
                        while (it.hasNext()) {
                            Map.Entry<ParameterProvider, String> em = it.next();
                            tx.setTarget(dac.createUpdate(new VelocitySQLParse(em.getKey(), em.getValue())));
                        }
                        this.commitTransaction(tx);
                        return true;
                    } else {
                        log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                    }
                } else {
                    log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
                }

            } else {
                log.error("数据库操作执行失败: [ 批量操作集为空 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 批量操作集为空 ]");
        }
        return false;
    }

    /**
     * 批量事物VM模板SQL执行(支持：新增、修改、删除)
     * 
     * @param list <Object[]> ,Object[0]: ParameterProvider,Object[1] SQL模板
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(List<Object[]> list) throws Exception {
        if (list != null && list.size() > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (Object[] obj : list) {
                        tx.setTarget(dac.createUpdate(new VelocitySQLParse((ParameterProvider) obj[0], obj[1]
                                .toString())));
                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }

            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }

        } else {
            log.error("数据库操作执行失败: [ 批量操作集为空 ]");
        }
        return false;
    }

    /**
     * 批量事物SQL执行(支持：新增、修改、删除)
     * 
     * @param Object []:SQL
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(Object[] SQL) throws Exception {
        if (SQL != null && SQL.length > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (Object obj : SQL) {
                        tx.setTarget(dac.createUpdate(obj.toString()));
                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return false;
    }

    /**
     * 批量事物SQL执行(支持：新增、修改、删除)
     * 
     * @param SQL (List)
     * @return
     * @throws Exception
     */
    public boolean setTransactionProxyList(List<String> SQL) throws Exception {
        if (SQL != null && SQL.size() > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (String obj : SQL) {
                        tx.setTarget(dac.createUpdate(obj));
                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }

        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return false;
    }

    /**
     * 单个事物VM模板SQL执行(支持：新增、修改、删除)
     * 
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param vmName SQL模板
     * @return boolean boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(ParameterProvider paramProvider, String vmName, DataSourceName dsName)
            throws Exception {
        if (StringUtil.isEmpty(vmName)) {
            log.error("数据库操作执行失败: [ SQL模板编号为空 ]");
            return false;
        } else {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    tx.setTarget(dac.createUpdate(new VelocitySQLParse(paramProvider, vmName)));
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        }
        return false;

    }

    /**
     * 单个事物SQL执行(支持：新增、修改、删除)
     * 
     * @param sql
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(String sql, DataSourceName dsName) throws Exception {
        if (StringUtil.isEmpty(sql)) {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
            return false;
        } else {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    tx.setTarget(dac.createUpdate(sql));
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        }
        return false;
    }

    /**
     * 批量事物VM模板SQL执行(支持：新增、修改、删除)
     * 
     * @param paramProvider 参数对象的提供器封装类，更新参数的操作
     * @param vmName SQL模板
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(Map<ParameterProvider, String> map, DataSourceName dsName) throws Exception {
        if (map != null) {
            if (!map.isEmpty() && map.size() > 0) {
                Iterator<Map.Entry<ParameterProvider, String>> it = map.entrySet().iterator();
                DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
                if (dac != null) {
                    TransactionProxy tx = TransactionManager.getInstance().getProxy();
                    if (tx != null) {
                        while (it.hasNext()) {
                            Map.Entry<ParameterProvider, String> em = it.next();
                            tx.setTarget(dac.createUpdate(new VelocitySQLParse(em.getKey(), em.getValue())));
                        }
                        this.commitTransaction(tx);
                        return true;
                    } else {
                        log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                    }

                } else {
                    log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
                }

            } else {
                log.error("数据库操作执行失败: [ 无法执行不存在的SQL集合语句 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL集合语句 ]");
        }
        return false;
    }

    /**
     * 批量事物VM模板SQL执行(支持：新增、修改、删除)
     * 
     * @param list <Object[]> ,Object[0]: ParameterProvider,Object[1] SQL模板
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(List<Object[]> list, DataSourceName dsName) throws Exception {
        if (list != null && list.size() > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (Object[] obj : list) {
                        tx.setTarget(dac.createUpdate(new VelocitySQLParse((ParameterProvider) obj[0], obj[1]
                                .toString())));
                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL集合语句 ]");
        }
        return false;
    }

    /**
     * 批量事物SQL执行(支持：新增、修改、删除)
     * 
     * @param Object []:SQL
     * @return boolean true：成功、false：失败
     * @throws Exception
     */
    public boolean setTransactionProxyVm(Object[] SQL, DataSourceName dsName) throws Exception {
        if (SQL != null && SQL.length > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (Object obj : SQL) {
                        tx.setTarget(dac.createUpdate(obj.toString()));

                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }

        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL集合语句 ]");
        }
        return false;
    }

    /**
     * 批量事物SQL执行(支持：新增、修改、删除)
     * 
     * @param SQL (List)
     * @return
     * @throws Exception
     */
    public boolean setTransactionProxyList(List<String> SQL, DataSourceName dsName) throws Exception {
        if (SQL != null && SQL.size() > 0) {
            DataOperationCreator dac = DataOperationCreator.getInstance(dsName);
            if (dac != null) {
                TransactionProxy tx = TransactionManager.getInstance().getProxy();
                if (tx != null) {
                    for (String obj : SQL) {
                        tx.setTarget(dac.createUpdate(obj));

                    }
                    this.commitTransaction(tx);
                    return true;
                } else {
                    log.error("数据库操作执行失败: [ 创建数据库事物失败 ]");
                }
            } else {
                log.error("数据库操作执行失败: [ 获取数据库连接失败 ]");
            }
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL集合语句 ]");
        }
        return false;
    }

    /**
     * 执行数据库查询(不支持分页,默认数据类型Oracle)
     * 
     * @param sql 查询语句
     * @return 结果集合（List<Object[]>）
     */
    public List<Object[]> query(String sql) throws SQLException {
        List<Object[]> rows = new ArrayList<Object[]>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(null).query(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(不支持分页)
     * 
     * @param sql 查询语句
     * @param dsName 数据类型
     * @return 结果集合（List<Object[]>）
     */
    public List<Object[]> query(String sql, DataSourceName dsName) throws SQLException {
        List<Object[]> rows = new ArrayList<Object[]>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(null).query(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     */
    public List<Object[]> queryPage(String sql, PageVariable page, DataSourceName dsName) throws SQLException {
        List<Object[]> rows = new ArrayList<Object[]>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).query(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @param dsName 数据库类型
     * @return 结果集合
     */
    public List<Map<String, Object>> queryPageMap(String sql, PageVariable page, DataSourceName dsName)
            throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页,数据库类型默认Oracle)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @return 结果集合（List<Object[]>）
     */
    public List<Object[]> queryPage(String sql, PageVariable page) throws SQLException {
        List<Object[]> rows = new ArrayList<Object[]>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).query(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页,数据库类型默认Oracle)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @return 结果集合
     */
    public List<Map<String, Object>> queryPageMap(String sql, PageVariable page) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 创建查询命令对象(不带分页)
     * 
     * @param paramProvider 查询参数
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Object[]> query(ParameterProvider paramProvider, String filename) throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), null).execute();
    }

    /**
     * 创建查询命令对象(不带分页，自定义数据库类型)
     * 
     * @param paramProvider 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Object[]> query(ParameterProvider paramProvider, String filename, DataSourceName dsName)
            throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), null).execute();
    }

    /**
     * 创建查询命令对象(支持分页)
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Object[]> query(ParameterProvider paramProvider, PageVariable page, String filename,
            DataSourceName dsName) throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), page).execute();
    }

    /**
     * 创建查询命令对象(支持分页)
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Object[]> query(ParameterProvider paramProvider, PageVariable page, String filename) throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), page).execute();
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Object[]> query(Map<String, String[]> parameter, String filename, PageVariable page,
            DataSourceName dsName) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return query(paramProvider, page, filename, dsName);
    }

    /**
     * 通过vm模板执行数据库查询(不带分页)
     * 
     * @param parameter 参数
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Object[]> query(Map<String, String[]> parameter, String filename) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return query(paramProvider, filename);
    }

    /**
     * 通过vm模板执行数据库查询
     * 
     * @param parameter 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Object[]> query(Map<String, String[]> parameter, String filename, DataSourceName dsName)
            throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return query(paramProvider, filename, dsName);
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Object[]> query(Map<String, String[]> parameter, String filename, PageVariable page) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return query(paramProvider, page, filename);
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMaps(Map<String, String[]> parameter, String filename, PageVariable page)
            throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return queryMap(paramProvider, page, filename);
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

    /**
     * 创建查询命令对象(支持分页)
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Map<String, Object>> queryMap(ParameterProvider paramProvider, PageVariable page, String filename)
            throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), page).executeMap();
    }

    /**
     * 创建查询命令对象(不带分页，自定义数据库类型)
     * 
     * @param paramProvider 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Map<String, Object>> queryMap(ParameterProvider paramProvider, String filename, DataSourceName dsName)
            throws Exception {
        return (List<Map<String, Object>>) DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), null).executeMap();
    }

    /**
     * 创建查询命令对象(支持分页)
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    private List<Map<String, Object>> queryMap(ParameterProvider paramProvider, PageVariable page, String filename,
            DataSourceName dsName) throws Exception {
        return DataOperationCreator.getInstance(dsName)
                .createQuery(new VelocitySQLParse(paramProvider, filename), page).executeMap();
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
     * 通过vm模板执行数据库查询
     * 
     * @param parameter 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String[]> parameter, PageVariable page, String filename)
            throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return queryMap(paramProvider, filename, dsName);
    }

    /**
     * 通过vm模板执行数据库查询
     * 
     * @param parameter 查询参数
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String[]> parameter, String filename, DataSourceName dsName)
            throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return queryMap(paramProvider, filename, dsName);
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @param dsName 数据库类型
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String[]> parameter, PageVariable page, String filename,
            DataSourceName dsName) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameter(parameter);
        return queryMap(paramProvider, page, filename, dsName);
    }

    /**
     * 执行数据库查询(不支持分页,默认数据类型Oracle)
     * 
     * @param sql 查询语句
     * @return 结果集合（List）
     */
    public List<Map<String, Object>> queryMap(String sql) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(null).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(不支持分页)
     * 
     * @param sql 查询语句
     * @param dsName 数据类型
     * @return 结果集合（List）
     */
    public List<Map<String, Object>> queryMap(String sql, DataSourceName dsName) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(null).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页,默认数据类型Oracle)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @return 结果集合（List）
     * @throws SQLException
     */
    public List<Map<String, Object>> queryMap(String sql, PageVariable page) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 执行数据库查询(支持分页)
     * 
     * @param sql 查询语句
     * @param page 分页对象
     * @param dsName 数据库类型
     * @return 结果集合（List）
     * @throws SQLException
     */
    public List<Map<String, Object>> queryMap(String sql, PageVariable page, DataSourceName dsName) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (sql != null && StringUtils.isNotEmpty(sql.trim())) {
            rows = DataOperationCreator.getInstance(dsName).createQuery(page).queryMap(sql);
        } else {
            log.error("数据库操作执行失败: [ 无法执行空SQL语句 ]");
        }
        return rows;
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String> parameter, String filename, PageVariable page)
            throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(parameter);
        return queryMap(paramProvider, page, filename);
    }

    /**
     * 通过vm模板查询数据（支持分页）
     * 
     * @param paramProvider 查询参数
     * @param page 分页对象
     * @param filename vm模板（SQL模板）
     * @return 结果集合（List<Object[]>）
     * @throws Exception
     */
    public List<Map<String, Object>> queryMap(Map<String, String> parameter, String filename, PageVariable page,
            DataSourceName dsName) throws Exception {
        ParameterProvider paramProvider = new ParameterProvider();
        paramProvider.addParameters(parameter);
        return queryMap(paramProvider, page, filename, dsName);
    }

    /**
     * 事物提交
     * 
     * @throws Exception
     */
    protected void commitTransaction(TransactionProxy tx) throws Exception {
        if (tx != null) {
            tx.commit();
        } else {
            log.error("数据库操作执行失败: [ 事物不存在 ]");
        }

    }

}
