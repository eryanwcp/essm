/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.aop;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.service.ResourceManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 同步站点/栏目到资源 将站点/栏目作为资源权限
 * <br/>将该类注入spring即可
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-09-29
 */
@Aspect
public class CMSToResourceAspect {

    private static Logger logger = LoggerFactory.getLogger(CMSToResourceAspect.class);

    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private SiteService siteService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增/修改站点同步
     *
     * @param point
     */
    @After("execution(* com.eryansky.modules.cms.service.SiteService.save(..))")
    public void saveSite(JoinPoint point) {
        Object[] objects = point.getArgs();
        Site site = (Site) objects[0];
        if (site != null) {
            String parentId = null;
            try {
                resourceManager.iSynchronous(ResourceType.CMS.getValue(),CmsUtils.SITE_PREFIX + site.getId(), site.getName(), parentId, StatusState.LOCK.getValue());
                logger.debug("同步[新增/修改]站点【{},{}】到资源成功。", new Object[]{site.getId(), site.getName()});
            } catch (Exception e) {
                logger.error("同步[新增/修改]站点【" + site.getId() + "," + site.getName() + "】到资源失败," + e.getMessage(), e);
            }
        }
    }

    /**
     * 删除站点同步
     *
     * @param point
     */
    @After("execution(* com.eryansky.modules.cms.service.SiteService.delete(..))")
    public void deleteSite(JoinPoint point) {
        Object[] objects = point.getArgs();
        String siteId = (String) objects[0];
        try {
            resourceManager.iDeleteResource(ResourceType.CMS.getValue(),CmsUtils.SITE_PREFIX + siteId);
            logger.debug("同步[删除]站点[{}]到资源成功。", siteId);
        } catch (Exception e) {
            logger.error("同步[删除]站点【" + siteId + "】到资源失败," + e.getMessage(), e);
        }
    }


    /**
     * 新增/修改栏目同步
     *
     * @param point
     */
    @After("execution(* com.eryansky.modules.cms.service.CategoryService.save(..))")
    public void saveCategory(JoinPoint point) {
        Object[] objects = point.getArgs();
        Category category = (Category) objects[0];
        if (category != null) {
            String parentCode = null;
            Category parentCategory = category.getParent();
            if (parentCategory != null && !parentCategory.getId().equals("1")) {
                parentCode = CmsUtils.CATEGORY_PREFIX + parentCategory.getId();
            }else{
                Site site = siteService.getByCode(category.getSite().getCode());
                parentCode = CmsUtils.SITE_PREFIX + site.getId();
            }
            try {
                resourceManager.iSynchronous(ResourceType.CMS.getValue(),CmsUtils.CATEGORY_PREFIX + category.getId(), category.getName(), parentCode, StatusState.LOCK.getValue());
                logger.debug("同步[新增/修改]栏目【{},{}】到资源成功。", new Object[]{category.getId(), category.getName()});
            } catch (Exception e) {
                logger.error("同步[新增/修改]栏目【" + category.getId() + "," + category.getName() + "】到资源失败," + e.getMessage(), e);
            }
        }
    }

    /**
     * 删除栏目同步
     *
     * @param point
     */
    @After("execution(* com.eryansky.modules.cms.service.CategoryService.delete(..))")
    public void deleteCategory(JoinPoint point) {
        Object[] objects = point.getArgs();
        String categoryId = (String) objects[0];
        try {
            resourceManager.iDeleteResource(ResourceType.CMS.getValue(),CmsUtils.CATEGORY_PREFIX + categoryId);
            logger.debug("同步[删除]栏目[{}]到资源成功。", categoryId);
        } catch (Exception e) {
            logger.error("同步[删除]栏目【" + categoryId + "】到资源失败," + e.getMessage(), e);
        }
    }

}
