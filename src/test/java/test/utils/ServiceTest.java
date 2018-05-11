/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys.mapper.Dictionary;
import com.eryansky.modules.sys.mapper.DictionaryItem;
import com.eryansky.modules.sys.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-07 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-task.xml",
        "classpath:applicationContext-ehcache.xml" })
public class ServiceTest {

    private static Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganService organService;
    @Autowired
    private PostService postService;
    @Autowired
    private RoleService roleService;


    @Test
    public void security() throws Exception{
        System.out.println(SecurityUtils.isCurrentUserAdmin());;

    }



}
