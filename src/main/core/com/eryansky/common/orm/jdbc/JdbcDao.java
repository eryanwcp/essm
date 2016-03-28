/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.common.orm.PageSqlUtils;
import org.springframework.util.Assert;

/**
 * Spring Jdbc工具类.
 * <br>支持MySQL、Oracle、postgresql、SQL Server分页查询.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-2-12 下午9:36:42
 */
@Repository
@Transactional
public class JdbcDao extends SimpleJdbcDao{

    public JdbcDao(){

    }

    @Autowired
    public JdbcDao(@Qualifier("dataSource") DataSource dataSource) {
        super(dataSource);
    }


    /**
     * 使用指定的检索标准检索数据并分页返回数据-采用预处理方式.<br/>
     * 支持MySQL、Oracle、postgresql、SQL Server分页查询. <br/>
     * 例如：
     * @param sql SQL语句
     * @param page 第几页
     * @param rows 页大小
     * @return
     */
    @Transactional(readOnly = true)
    public  List<Map<String, Object>>  queryForList(String  sql,  int page, int rows,Object... param){
        Assert.hasText(sql,"sql语句不正确!");
        //封装分页SQL
        sql = PageSqlUtils.createPageSql(sql,page,rows);
        if(param == null){
            return jdbcTemplate.queryForList(sql);
        }
        return jdbcTemplate.queryForList(sql,param);
    }


}
