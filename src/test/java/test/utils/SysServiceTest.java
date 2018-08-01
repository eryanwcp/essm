/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.Resource;
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
        "classpath:applicationContext-j2cache.xml" })
public class SysServiceTest {

    private static Logger logger = LoggerFactory.getLogger(SysServiceTest.class);

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
    @Autowired
    private CacheChannel cacheChannel;


    @Test
    public void security() throws Exception{

    }

    @Test
    public void resource() throws Exception{
        List<Resource> resources = resourceService.findResourcesWithPermissions("1");
        System.out.println(resources.size());
        System.out.println(JsonMapper.toJsonString(resources));

        resources = resourceService.findAppResourcesWithPermissions("1");
        System.out.println(resources.size());
        System.out.println(JsonMapper.toJsonString(resources));

        resources = resourceService.findAppAndMenuWithPermissions("1");
        System.out.println(resources.size());
        System.out.println(JsonMapper.toJsonString(resources));

        resources = resourceService.findAuthorityResourcesByUserId("1");
        System.out.println(resources.size());
        System.out.println(JsonMapper.toJsonString(resources));


        System.out.println(resourceService.isUserPermittedResourceCode("1",""));

    }

    @Test
    public void cache() throws Exception{
        List<TreeNode> treeNodes = resourceService.findTreeNodeResourcesWithPermissions("1");
        System.out.println(JsonMapper.toJsonString(treeNodes));
        System.out.println(CacheUtils.get(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,"1"));
        System.out.println(CacheUtils.keys(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE));

    }


    @Test
    public void cache3() throws Exception{
        System.out.println(cacheChannel.regions());
        cacheChannel.set("sessionCache","1","2");
        System.out.println(cacheChannel.get("sessionCache","1"));;
        System.out.println(cacheChannel.keys(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE));

    }


    @Test
    public void organ() throws Exception{
//        List<Organ> companys = organService.findCompanys();
//        System.out.println(JsonMapper.toJsonString(companys));
//        List<TreeNode> treeNodes = organService.findCompanysTree();
//        System.out.println(JsonMapper.toJsonString(treeNodes));
//        System.out.println(organService.findChilds("1"));
//        System.out.println(organService.findChilds("4eebbb72532c4b8ebca0e68ac0b189ad"));
//        System.out.println(organService.findOwnerAndChilds("1"));
//        System.out.println(organService.findOwnerAndChilds("4eebbb72532c4b8ebca0e68ac0b189ad"));
//        System.out.println(organService.findDepartmentAndGroupOrganExtendsByCompanyId("1"));
//        System.out.println(organService.findRoots());




    }



}
