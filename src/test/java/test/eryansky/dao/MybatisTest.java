/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.eryansky.dao;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-12 22:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml"})
public class MybatisTest {


}
