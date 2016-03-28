/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package test.db;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.service.ResourceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * CMS栏目同步到资源权限
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-09-29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
//        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-ehcache.xml" })
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
//@Transactional(readOnly = false)
public class CMSToResourceTest {

    @Autowired
    private SiteService siteService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ResourceManager resourceManager;

    /**
     * 同步全部CMS栏目到资源
     * @return
     */
    @Test
    public void synchronousCMSCategory(){
        List<Site> siteList = siteService.findAll();
        for (Site site : siteList) {
            resourceManager.iSynchronous(ResourceType.CMS.getValue(),CmsUtils.SITE_PREFIX + site.getId(), site.getName(), null, StatusState.NORMAL.getValue());
            List<Category> list = categoryService.findByParentId("1", site.getId());
            for (Category category : list) {
                recursive(category, site.getId(), CmsUtils.SITE_PREFIX + site.getId(), null);
            }

        }
    }

    private void recursive(Category category, String siteId, String parentId, String status){
        Category parentCategory = category.getParent();
        if (parentId == null && parentCategory != null) {
            parentId = CmsUtils.CATEGORY_PREFIX + parentCategory.getId();
        }

        resourceManager.iSynchronous(ResourceType.CMS.getValue(),CmsUtils.CATEGORY_PREFIX + category.getId(), category.getName(), parentId, status);
        List<Category> categoryList = categoryService.findByParentId(category.getId(), siteId);
        if(Collections3.isNotEmpty(categoryList)){
            for(Category category1:categoryList){
                recursive(category1, siteId, category1.getParent() == null ? null : CmsUtils.CATEGORY_PREFIX + category1.getParent().getId(), status);
            }
        }
    }
}
