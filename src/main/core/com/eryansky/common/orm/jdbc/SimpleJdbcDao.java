/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
/**
 * Spring JDBC模板.
 * @author 尔演&Eryan eryanwcp@gmail.com
 *
 */
@SuppressWarnings("unchecked")
@Transactional
public class SimpleJdbcDao {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Spring  JdbcTemplate
     */
    protected JdbcTemplate jdbcTemplate;
    /**
     *  Spring  SimpleJdbcInsert
     */
    protected SimpleJdbcInsert simpleJdbcInsert;

    public SimpleJdbcDao(){

    }

    /**
     * 构造方法
     * @param dataSource 数据源
     */
    public SimpleJdbcDao(DataSource dataSource){
        jdbcTemplate=new JdbcTemplate(dataSource);
        simpleJdbcInsert=new SimpleJdbcInsert(dataSource);
    }



    /**
     * 执行insert，update，delete等操作<br>
     * 例如insert into users (name,login_name,password) values(:name,:loginName,:password)<br>
     * 参数用冒号,参数为bean的属性名
     * @param sql
     * @param bean
     */
    public int executeForObject(final String sql,Object bean){
        Assert.hasText(sql,"sql语句不正确!");
        if(bean!=null){
            return jdbcTemplate.update(sql, paramBeanMapper(bean));
        }else{
            return jdbcTemplate.update(sql);
        }
    }

    /**
     * 执行insert，update，delete等操作<br>
     * 例如insert into users (name,login_name,password) values(:name,:login_name,:password)<br>
     * 参数用冒号,参数为Map的key名
     * @param sql
     * @param parameters
     */
    public int update(final String sql,Map parameters){
        Assert.hasText(sql,"sql语句不正确!");
        if(parameters!=null){
            return jdbcTemplate.update(sql, parameters);
        }else{
            return jdbcTemplate.update(sql);
        }
    }

    /**
     * update更新
     * @param sql  sql语句
     * @param param 参数
     * @return
     */
    public int update(String sql,List<Object> param) {
        Assert.hasText(sql,"sql语句不正确!");
        if(param!=null){
            return jdbcTemplate.update(sql, param);
        }else{
            return jdbcTemplate.update(sql);
        }
    }

    /**
     * update更新
     * @param sql  sql语句
     * @param param 参数
     * @return
     */
    public int update(String sql, Object... param) {
        Assert.hasText(sql,"sql语句不正确!");
        if(param!=null){
            return jdbcTemplate.update(sql, param);
        }else{
            return jdbcTemplate.update(sql);
        }
    }

    /*
     * 批量处理操作
     * 例如：update t_actor set first_name = :firstName, last_name = :lastName where id = :id
     * 参数用冒号
     */
    public int[] batchUpdate(final String sql,List<Object[]> batch ){
        Assert.hasText(sql,"sql语句不正确!");
        int[] updateCounts = jdbcTemplate.batchUpdate(sql,batch);
        return updateCounts;
    }

    public long executeForObjectReturnPk(final String sql,Object bean){
        Assert.hasText(sql,"sql语句不正确!");
        if(bean!=null){
            return jdbcTemplate.update(sql, paramBeanMapper(bean));
        }else{
            return jdbcTemplate.update(sql);
        }
    }



    /**
     * 根据sql语句，返回对象集合
     * @param sql 语句(参数用冒号加参数名，例如select * from tb where id=:id)
     * @param clazz 类型
     * @param parameters 参数集合(key为参数名，value为参数值)
     * @return bean对象集合
     */
    @Transactional(readOnly = true)
    public List query(final String sql,Class clazz,Map parameters){
        Assert.hasText(sql,"sql语句不正确!");
        Assert.notNull(clazz,"集合中对象类型不能为空!");
        if(parameters!=null){
            return jdbcTemplate.query(sql, resultBeanMapper(clazz),parameters);
        }else{
            return jdbcTemplate.query(sql, resultBeanMapper(clazz));
        }
    }


    /**
     * 使用指定的检索标准检索数据并返回数据
     * @param sql SQL语句
     * @param param 参数
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryForList(String sql, Object... param) {
        Assert.hasText(sql,"sql语句不正确!");
        if(param!=null){
            return jdbcTemplate.queryForList(sql, param);
        }else{
            return jdbcTemplate.queryForList(sql);
        }
    }

    /**
     * 根据sql语句，返回Map对象集合
     * @param sql 语句(参数用冒号加参数名，例如select count(*) from tb where id=:id)
     * @param parameters 参数集合(key为参数名，value为参数值)
     * @return bean对象
     */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> queryForList(final String sql,Map parameters){
        Assert.hasText(sql,"sql语句不正确!");
        if(parameters!=null){
            return jdbcTemplate.queryForList(sql, parameters);
        }else{
            return jdbcTemplate.queryForList(sql);
        }
    }


    /**
     * 根据sql语句，返回Map对象,对于某些项目来说，没有准备Bean对象，则可以使用Map代替Key为字段名,value为值
     * @param sql 语句(参数用冒号加参数名，例如select count(*) from tb where id=:id)
     * @param parameters 参数集合(key为参数名，value为参数值)
     * @return bean对象
     */
    @Transactional(readOnly = true)
    public Map<String,Object> queryForMap(final String sql,Map parameters){
        Assert.hasText(sql,"sql语句不正确!");
        if(parameters!=null){
            return jdbcTemplate.queryForMap(sql, parameters);
        }else{
            return jdbcTemplate.queryForMap(sql);
        }
    }
    /**
     * 根据sql语句，返回Map对象,对于某些项目来说，没有准备Bean对象，则可以使用Map代替Key为字段名,value为值
     * @param sql 语句(参数用冒号加参数名，例如select count(*) from tb where id=:id)
     * @param param 参数集合(key为参数名，value为参数值)
     * @return bean对象
     */
    @Transactional(readOnly = true)
    public Map<String,Object> queryForMap(final String sql,Object... param){
        Assert.hasText(sql,"sql语句不正确!");
        if(param!=null){
            return jdbcTemplate.queryForMap(sql, param);
        }else{
            return jdbcTemplate.queryForMap(sql);
        }
    }

    /**
     * 根据sql语句，返回对象
     * @param sql 语句(参数用冒号加参数名，例如select * from tb where id=:id)
     * @param clazz 类型
     * @param parameters 参数集合(key为参数名，value为参数值)
     * @return bean对象
     */
    @Transactional(readOnly = true)
    public Object queryForObject(final String sql,Class clazz,Map parameters){
        try{
            Assert.hasText(sql,"sql语句不正确!");
            Assert.notNull(clazz,"对象类型不能为空!");
            if(parameters!=null){
                return jdbcTemplate.queryForObject(sql, resultBeanMapper(clazz), parameters);
            }else{
                return jdbcTemplate.queryForObject(sql, resultBeanMapper(clazz),Long.class);
            }
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据sql语句，返回数值型返回结果
     * @param sql 语句(参数用冒号加参数名，例如select count(*) from tb where id=:id)
     * @param parameters 参数集合(key为参数名，value为参数值)
     * @return bean对象
     */
    @Transactional(readOnly = true)
    public long queryForLong(final String sql,Map parameters){
        Assert.hasText(sql,"sql语句不正确!");
        if(parameters != null){
            return  jdbcTemplate.queryForObject(sql, new Object[]{parameters},Long.class);
        }
        return  jdbcTemplate.queryForObject(sql,Long.class);
    }


    /**
     * 使用指定的检索标准检索数据并分页返回数据For JDBC-采用预处理方式
     * @param sql SQL语句
     * @param param 参数
     */
    @Transactional(readOnly = true)
    public long queryForLong(String  sql,Object... param) {
        Assert.hasText(sql,"sql语句不正确!");
        if(param != null){
            return  jdbcTemplate.queryForObject(sql, param, Long.class);
        }
        return  jdbcTemplate.queryForObject(sql,Long.class);
    }

    /**
     * 使用指定的检索标准检索数据并分页返回数据For JDBC-采用预处理方式
     * @param sql SQL语句
     * @param param 参数
     * @return
     */
    @Transactional(readOnly = true)
    public int queryForInt(String sql, Object... param) {
        Assert.hasText(sql,"sql语句不正确!");
        if(param != null){
            return  jdbcTemplate.queryForObject(sql, param,Integer.class);
        }
        return  jdbcTemplate.queryForObject(sql,Integer.class);
    }



    protected ParameterizedBeanPropertyRowMapper resultBeanMapper(Class clazz) {
        return ParameterizedBeanPropertyRowMapper.newInstance(clazz);
    }

    protected BeanPropertySqlParameterSource paramBeanMapper(Object object) {
        return new BeanPropertySqlParameterSource(object);
    }
}
