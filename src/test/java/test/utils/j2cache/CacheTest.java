/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils.j2cache;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.*;
import com.eryansky.utils.CacheConstants;
import com.eryansky.utils.CacheUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2018-05-11 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-ehcache.xml" })
public class CacheTest {

    private static Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheChannel cacheChannel;


    @Test
    public void cache() throws Exception{
        List<TreeNode> treeNodes = resourceService.findTreeNodeResourcesWithPermissions("1");
        System.out.println(JsonMapper.toJsonString(treeNodes));
        System.out.println(CacheUtils.get(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,"1"));
        System.out.println(CacheUtils.keys(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE));

    }

    @Test
    public void cache2() throws Exception{
        User user = userService.get("1");
        for(int i=0;i<10000;i++){
            String sessionId = Identities.uuid2() + "."+i;
            SecurityUtils.putUserToSession(sessionId,user);
        }
    }




}
