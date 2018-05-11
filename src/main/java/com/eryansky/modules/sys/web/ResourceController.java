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
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.mapper.Area;
import com.eryansky.modules.sys.mapper.Resource;
import com.eryansky.modules.sys.service.ResourceService;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class ResourceController extends SimpleController {

    @Autowired
    private ResourceService resourceService;

    @ModelAttribute("model")
    public Resource get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return resourceService.get(id);
        }else{
            return new Resource();
        }
    }

    @Logging(value = "资源管理",logType = LogType.access)
    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/resource";
    }

    @RequestMapping(value = {"treegrid"})
    @ResponseBody
    public Datagrid<Resource> treegrid(Resource model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource entity = new Resource();
        List<Resource> list = resourceService.findList(entity);
        Datagrid<Resource> dg = new Datagrid<Resource>(list.size(), list);
        return dg;
    }


    /**
     * @param model
     * @param parentId 上级ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model") Resource model, String parentId, Model uiModel) throws Exception {
        uiModel.addAttribute("parentId", parentId);
        uiModel.addAttribute("model", model);
        return "modules/sys/resource-input";
    }




    /**
     * 保存.
     */
    @Logging(value = "资源管理-保存资源",logType = LogType.access)
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Resource resource,String _parentId)  {
        Result result = null;
        resource.setParent(null);

        // 设置上级节点
        if (StringUtils.isNotBlank(_parentId)) {
            Resource parentResource = resourceService.get(_parentId);
            if (parentResource == null) {
                logger.error("父级资源[{}]已被删除.", _parentId);
                throw new ActionException("父级资源已被删除.");
            }
            resource.setParent(parentResource);
        }

        if (StringUtils.isNotBlank(resource.getId())) {
            if (resource.getId().equals(resource.getParentId())) {
                result = new Result(Result.ERROR, "[上级资源]不能与[资源名称]相同.",
                        null);
                logger.debug(result.toString());
                return result;
            }
        }
        resourceService.saveResource(resource);
        result = Result.successResult();
        return result;
    }


    /**
     * 删除.
     */
    @Logging(value = "资源管理-删除资源",logType = LogType.access)
    @RequestMapping(value = {"delete/{id}"})
    @ResponseBody
    public Result delete(@PathVariable String id) {
        Result result;
        if(StringUtils.isNotBlank(id)){
            List<String> ids = Lists.newArrayList();
            ids.add(id);
            resourceService.deleteByIds(ids);
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
        treeNodes = resourceService.findTreeNodeResources();
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

        String parentType = null;
        if(StringUtils.isNotBlank(parentId)){
            Resource resource = resourceService.get(parentId);
            parentType = resource.getType();
        }

        ResourceType parentResourceType = ResourceType.getByValue(parentType);
        if (parentResourceType != null) {
            if (parentResourceType.equals(ResourceType.app)) {
                Combobox combobox = new Combobox(ResourceType.app.getValue(),ResourceType.app.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.menu.getValue(),ResourceType.menu.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.function.getValue(),ResourceType.function.getDescription());
                cList.add(combobox);
            }else if (parentResourceType.equals(ResourceType.menu)) {
                Combobox combobox = new Combobox(ResourceType.menu.getValue(),ResourceType.menu.getDescription());
                cList.add(combobox);
                combobox = new Combobox(ResourceType.function.getValue(),ResourceType.function.getDescription());
                cList.add(combobox);
            } else if (parentResourceType.equals(ResourceType.function)) {
                Combobox combobox = new Combobox(ResourceType.function.getValue(),ResourceType.function.getDescription());
                cList.add(combobox);
            }
        } else {
            Combobox combobox = new Combobox(ResourceType.app.getValue(),ResourceType.app.getDescription());
            cList.add(combobox);
            combobox = new Combobox(ResourceType.menu.getValue(),ResourceType.menu.getDescription());
            cList.add(combobox);
            combobox = new Combobox(ResourceType.function.getValue(),ResourceType.function.getDescription());
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
        treeNodes = resourceService.findTreeNodeResourcesWithExclude(resource.getId());
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
        Integer maxSort = resourceService.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        logger.debug(result.toString());
        return result;
    }


}
