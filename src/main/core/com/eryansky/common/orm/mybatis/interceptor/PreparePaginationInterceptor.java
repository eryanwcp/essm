/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.mybatis.interceptor;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.Properties;

/**
 * Mybatis数据库分页插件，拦截StatementHandler的prepare方法
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-7-16
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})
})
public class PreparePaginationInterceptor extends BaseInterceptor {
    
    private static final long serialVersionUID = 1L;

    public PreparePaginationInterceptor() {
        super();
    }

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            final RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            final BaseStatementHandler delegate =  ReflectionUtils.getFieldValue(statementHandler, DELEGATE);
            final MappedStatement mappedStatement = ReflectionUtils.getFieldValue(delegate, MAPPED_STATEMENT);

//            //拦截需要分页的SQL
////            if (mappedStatement.getId().matches(_SQL_PATTERN)) { 
//            if (StringUtils.indexOfIgnoreCase(mappedStatement.getId(), _SQL_PATTERN) != -1) {
                BoundSql boundSql = delegate.getBoundSql();
                //分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject == null) {
                    log.error("参数未实例化");
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    final Connection connection = (Connection) ivk.getArgs()[0];
                    final String sql = boundSql.getSql();
                    //记录统计
                    final int count = SQLHelper.getCount(sql, connection, mappedStatement, parameterObject, boundSql, log);
                    Page<Object> page = null;
                    page = convertParameter(parameterObject, page);
                    page.setTotalCount(count);
                    String pagingSql = SQLHelper.generatePageSql(sql, page, DIALECT);
                    if (log.isDebugEnabled()) {
                        log.debug("PAGE SQL:" + pagingSql);
                    }
                    //将分页sql语句反射回BoundSql.
                    ReflectionUtils.setFieldValue(boundSql, "sql", pagingSql);
                }
                
                if (boundSql.getSql() == null || "".equals(boundSql.getSql())){
                    return null;
                }
                
            }
//        }
        return ivk.proceed();
    }


    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        initProperties(properties);
    }
}
