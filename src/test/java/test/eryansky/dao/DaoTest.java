/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.eryansky.dao;

import com.eryansky.common.orm.jdbc.JdbcDao;
import com.eryansky.common.utils.mapper.JsonMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-07 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
//        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-ehcache.xml" })
public class DaoTest {

    private static Logger logger = LoggerFactory.getLogger(DaoTest.class);

    @Autowired
    private JdbcDao jdbcDao;

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @After
    public void close() {
        SessionHolder holder = (SessionHolder) TransactionSynchronizationManager
                .getResource(sessionFactory);
        SessionFactoryUtils.closeSession(holder.getSession());
        TransactionSynchronizationManager.unbindResource(sessionFactory);
    }

    @Before
    public void init() {
        Session s = sessionFactory.openSession();
        TransactionSynchronizationManager.bindResource(sessionFactory,
                new SessionHolder(s));
    }

    /**
     * 批量生成机构编码
     */
    @Test
    public void queryForList() {
        try {
            String sql = "select * from t_sys_role ";
            List<Map<String, Object>> list = jdbcDao.queryForList(sql);
            System.out.println(JsonMapper.getInstance().toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
