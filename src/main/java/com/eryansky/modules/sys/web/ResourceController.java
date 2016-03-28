/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源权限Resource管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/resource")
public class ResourceController extends BaseController<Resource,String> {

    @Autowired
    private ResourceManager resourceManager;

    @Override
    public EntityManager<Resource, String> getEntityManager() {
        return resourceManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/resource";
    }


    /**
     * 删除.
     */
    @RequestMapping(value = {"_delete/{id}"})
    @ResponseBody
    public Result _delete(@PathVariable String id) {
        Result result;
        if(StringUtils.isNotBlank(id)){
            List<String> ids = Lists.newArrayList();
            ids.add(id);
            resourceManager.deleteByIds(ids);
            result = Result.successResult();
        }else{
            result = new Result().setCode(Result.WARN).setMsg("参数[id]为空.");
        }

        if(logger.isDebugEnabled()){
            logger.debug(result.toString());
        }
        return result;
    }

    /**
     * @param resource
     * @param parentId 上级ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model") Resource resource, String parentId, Model model) throws Exception {
        model.addAttribute("parentId", parentId);
        return "modules/sys/resource-input";
    }

    @RequestMapping(value = {"treegrid"})
    @ResponseBody
    public Datagrid<Resource> treegrid(String sort, String order) throws Exception {
        List<PropertyFilter> filters = Lists.newArrayList();
        List<Resource> list = getEntityManager().find(filters, sort, order);
        Datagrid<Resource> dg = new Datagrid<Resource>(list.size(), list);
        return dg;
    }


    /**
     * 保存.
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Resource resource,String _parentId)  {
        getEntityManager().evict(resource);
        Result result = null;
        resource.setParent(null);

        // 设置上级节点
        if (StringUtils.isNotBlank(_parentId)) {
            Resource parentResource = resourceManager.loadById(_parentId);
            if (parentResource == null) {
                logger.error("父级资源[{}]已被删除.", _parentId);
                throw new ActionException("父级资源已被删除.");
            }
            resource.setParent(parentResource);
        }

        if (StringUtils.isNotBlank(resource.getId())) {
            if (resource.getId().equals(resource.get_parentId())) {
                result = new Result(Result.ERROR, "[上级资源]不能与[资源名称]相同.",
                        null);
                logger.debug(result.toString());
                return result;
            }
        }
        resourceManager.saveEntity(resource);
        result = Result.successResult();
        return result;
    }

    /**
     * 资源树.
     */
    @RequestMapping(value = {"tree"})
    @ResponseBody
    public List<TreeNode> tree(String selectType) throws Exception {
        List<TreeNode> treeNodes = null;
        List<TreeNode> titleList = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            titleList.add(selectTreeNode);
        }
        treeNodes = resourceManager.findTreeNodeResources();
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * 资源类型下拉列表.
     */
    @RequestMapping(value = {"resourceTypeCombobox"})
    @ResponseBody
    public List<Combobox> resourceTypeCombobox(String selectType, String parentId) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox selectCombobox = SelectType.combobox(selectType);
        if(selectCombobox != null){
            cList.add(selectCombobox);
        }

        Integer parentType = null;
        if(StringUtils.isNotBlank(parentId)){
            Resource resource = resourceManager.loadById(parentId);
            parentType = resource.getType();
        }

        ResourceType parentResourceType = ResourceType.getResourceType(parentType);
        if (parentResourceType != null) {
            if (parentResourceType.equals(ResourceType.app)) {
                Combobox combobox = new Combobox(ResourceType.app.getValue().toString(),ResourceType.app.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.menu.getValue().toString(),ResourceType.menu.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.function.getValue().toString(),ResourceType.function.getDescription());
                cList.add(combobox);
            }else if (parentResourceType.equals(ResourceType.menu)) {
                Combobox combobox = new Combobox(ResourceType.menu.getValue().toString(),ResourceType.menu.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.function.getValue().toString(),ResourceType.function.getDescription());
                cList.add(combobox);
            } else if (parentResourceType.equals(ResourceType.function)) {
                Combobox combobox = new Combobox(ResourceType.function.getValue().toString(),ResourceType.function.getDescription());
                cList.add(combobox);
            }
        } else {
            Combobox combobox = new Combobox(ResourceType.app.getValue().toString(),ResourceType.app.getDescription());
            cList.add(combobox);
            combobox = new Combobox(ResourceType.menu.getValue().toString(),ResourceType.menu.getDescription());
            cList.add(combobox);
            combobox = new Combobox(ResourceType.function.getValue().toString(),ResourceType.function.getDescription());
            cList.add(combobox);
        }

        return cList;
    }

    /**
     * 父级资源下拉列表.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"parent"})
    @ResponseBody
    public List<TreeNode> parent(@ModelAttribute("model") Resource resource, String selectType) {
        List<TreeNode> treeNodes = null;
        List<TreeNode> titleList = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            titleList.add(selectTreeNode);
        }
        treeNodes = resourceManager.findTreeNodeResourcesWithExclude(resource.getId());
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort() throws Exception {
        Result result;
        Integer maxSort = resourceManager.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        logger.debug(result.toString());
        return result;
    }


    @Autowired
    private SiteService siteService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 同步全部CMS栏目到资源
     * @param status 状态 {@link com.eryansky.common.orm.entity.StatusState}
     * @return
     */
    @RequestMapping(value = {"synchronousCMSCategory"})
    @ResponseBody
    public Result synchronousCMSCategory(String status){
        List<Site> siteList = siteService.findAll();
        for (Site site : siteList) {
            resourceManager.iSynchronous(ResourceType.CMS.getValue(), CmsUtils.SITE_PREFIX + site.getId(), site.getName(), null, StatusState.NORMAL.getValue());
            List<Category> list = categoryService.findByParentId("1", site.getId());
            for (Category category : list) {
                recursive(category, site.getId(), CmsUtils.SITE_PREFIX + site.getId(), status);
            }

        }

        return Result.successResult();
    }

    private void recursive(Category category, String siteId, String parentId, String status){
        Category parentCategory = category.getParent();
        if (parentId == null && parentCategory != null) {
            parentId = CmsUtils.CATEGORY_PREFIX + parentCategory.getId();
        }

        resourceManager.iSynchronous(ResourceType.CMS.getValue(), CmsUtils.CATEGORY_PREFIX + category.getId(), category.getName(), parentId, status);
        List<Category> categoryList = categoryService.findByParentId(category.getId(), siteId);
        if(Collections3.isNotEmpty(categoryList)){
            for(Category category1:categoryList){
                recursive(category1, siteId, category1.getParent() == null ? null : CmsUtils.CATEGORY_PREFIX + category1.getParent().getId(), status);
            }
        }
    }

}
