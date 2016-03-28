/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 机构Organ管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-09-09 下午21:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/organ")
public class OrganController extends BaseController<Organ,String> {

    @Autowired
    private OrganManager organManager;
    @Autowired
    private UserManager userManager;

    @Override
    public EntityManager<Organ, String> getEntityManager() {
        return organManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/organ";
    }

    /**
     * @param organ
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public ModelAndView input(@ModelAttribute("model") Organ organ, String parentId, Model model) throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/sys/organ-input");
        modelAndView.addObject("parentId",parentId);
        return  modelAndView;
    }


    @RequestMapping(value = {"treegrid"})
    @ResponseBody
    public String treegrid(String parentId) throws Exception {
        List<Organ> list = null;
        if(StringUtils.isBlank(parentId)){
            if(SecurityUtils.isCurrentUserAdmin()){
                list = organManager.findDataByParent(null, null);
            }else{
                String organId = SecurityUtils.getCurrentUser().getCompanyId();
                list = new ArrayList<Organ>(1);
                list.add(organManager.loadById(organId));
            }

        }else{
            list = organManager.findDataByParent(parentId,null);
        }

//        Datagrid<Organ> dg = new Datagrid<Organ>(list.size(), list);
//        String json = JsonMapper.getInstance().toJson(dg);
        String json = JsonMapper.getInstance().toJson(list,Organ.class,
                new String[]{"id","name","shortName","sysCode","code","_parentId","managerUserName","superManagerUserName",
                        "address","phone","mobile","fax","type","typeView","orderNo","statusView","state"});
        return json;
    }

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return
     */
    @RequestMapping(value = {"delete/{id}"})
    @ResponseBody
    public Result delete(@PathVariable String id) {
        organManager.deleteById(id);
        return Result.successResult();
    }

    /**
     * 保存.
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Organ organ,String _parentId) {
        getEntityManager().evict(organ);
        Result result = null;
        organ.setParent(null);
        // 设置上级节点
        if (StringUtils.isNotBlank(_parentId)) {
            Organ parentOrgan = organManager.loadById(_parentId);
            if (parentOrgan == null) {
                logger.error("父级机构[{}]已被删除.", _parentId);
                throw new ActionException("父级机构已被删除.");
            }
            organ.setParent(parentOrgan);
        }
        try {
            organManager.saveEntity(organ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 设置机构用户 页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"user"})
    public String user(@ModelAttribute("model") Organ organ, Model model) throws Exception {
        List<User> organUsers = organ.getUsers();
        String organUserCombogridData = JsonMapper.getInstance().toJson(organUsers,User.class,
                new String[]{"id","name","sexView","defaultOrganName"});
        logger.debug(organUserCombogridData);
        model.addAttribute("organUserCombogridData", organUserCombogridData);
//        List<String> childUserIds = userManager.findOwnerAndChildsUserIds(organ.getId());
//        List<User> list = userManager.findAllNormalWithExclude(childUserIds);
//        List<User> list = userManager.findAllNormal();
//        String usersCombogridData = JsonMapper.getInstance().toJson(list,User.class,
//                new String[]{"id","name","sexView","defaultOrganName"});
//        model.addAttribute("usersCombogridData", usersCombogridData);

        return "modules/sys/organ-user";
    }


    /**
     * 设置机构用户
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"updateOrganUser"})
    @ResponseBody
    public Result updateOrganUser(@ModelAttribute("model") Organ organ) throws Exception {
        getEntityManager().evict(organ);
        Result result;
        organManager.saveEntity(organ);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     *
     * @param selectType
     * @param dataScope {@link DataScope}
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"tree"})
    @ResponseBody
    public List<TreeNode> tree(String parentId,String selectType,String dataScope,
                               @RequestParam(value = "cascade",required = false,defaultValue = "false")Boolean cascade) throws Exception {
        List<TreeNode> treeNodes = null;
        List<TreeNode> titleList = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            titleList.add(selectTreeNode);
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String _parentId = parentId;
        if(StringUtils.isBlank(parentId)){
            String organId = sessionInfo.getLoginOrganId();
            if(SecurityUtils.isCurrentUserAdmin() || (StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.ALL.getValue()))){
                organId = null;
            }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.COMPANY_AND_CHILD.getValue()))){
                User user = userManager.loadById(sessionInfo.getUserId());
                organId = user.getCompanyId();

            }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.OFFICE_AND_CHILD.getValue()))){
                User user = userManager.loadById(sessionInfo.getUserId());
                organId = user.getOfficeId();
            }
            _parentId = organId;
        }

        treeNodes = organManager.findOrganTree(_parentId,true,cascade);
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }

    /**
     * 机构类型下拉列表.
     * @param  parentId 父级机构ID
     */
    @RequestMapping(value = {"organTypeCombobox"})
    @ResponseBody
    public List<Combobox> organTypeCombobox(String selectType, String parentId) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        Integer parentType = null;
        if(StringUtils.isNotBlank(parentId)){
            Organ organ = organManager.loadById(parentId);
            if(organ != null){
                parentType = organ.getType();
            }
        }

        OrganType _enumParentType = OrganType.getOrganType(parentType);
        if (_enumParentType != null) {
            if (_enumParentType.equals(OrganType.organ)) {
                OrganType[] rss = OrganType.values();
                for (int i = 0; i < rss.length; i++) {
                    Combobox combobox = new Combobox();
                    combobox.setValue(rss[i].getValue().toString());
                    combobox.setText(rss[i].getDescription());
                    cList.add(combobox);
                }
            } else if (_enumParentType.equals(OrganType.department)) {
                Combobox departmentCombobox = new Combobox(OrganType.department.getValue().toString(), OrganType.department.getDescription().toString());
                Combobox groupCombobox = new Combobox(OrganType.group.getValue().toString(), OrganType.group.getDescription().toString());
                cList.add(departmentCombobox);
                cList.add(groupCombobox);
            } else if (_enumParentType.equals(OrganType.group)) {
                Combobox groupCombobox = new Combobox(OrganType.group.getValue().toString(), OrganType.group.getDescription().toString());
                cList.add(groupCombobox);
            }
        } else {
            Combobox groupCombobox = new Combobox(OrganType.organ.getValue().toString(), OrganType.organ.getDescription().toString());
            cList.add(groupCombobox);
        }
        return cList;
    }

    /**
     * 父级机构下拉列表.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"parentOrgan"})
    @ResponseBody
    public List<TreeNode> parentOrgan(String selectType, @ModelAttribute("model") Organ organ) throws Exception {
        List<TreeNode> treeNodes = null;
        List<TreeNode> titleList = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            titleList.add(selectTreeNode);
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String excludeOrganId = organ.getId();
        String organId = null;
        TreeNode parentTreeNode = null;
        if(SecurityUtils.isCurrentUserAdmin()){
            organId = null;
        }else{
            User user = userManager.loadById(sessionInfo.getUserId());
            organId = user.getCompanyId();
            Organ o = organManager.loadById(organId);
            if(o.getGrade() >= organ.getGrade()){
                parentTreeNode = organManager.organToTreeNode(organ.getParent());
                excludeOrganId = null;
            }
        }
        treeNodes = organManager.findOrganTree(organId,excludeOrganId);
        if(parentTreeNode != null){
            treeNodes.add(parentTreeNode.setState(TreeNode.STATE_OPEN));
        }
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
        Integer maxSort = organManager.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        logger.debug(result.toString());
        return result;
    }

    /**
     * 栏目编辑，展示的栏目树
     *
     * @param extId
     * @param type
     * @param grade
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<TreeNode> treeData(@RequestParam(required = false) String extId,
                                              @RequestParam(required = false) Integer type,
                                              @RequestParam(required = false) Integer grade,
                                              HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
//        List<TreeNode> mapList = Lists.newArrayList();
//        List<Organ> list = organManager.findAllNormal();
//        for (int i = 0; i < list.size(); i++) {
//            Organ e = list.get(i);
//            //extId不为空说明是编辑的情况，编辑时需要过滤掉当前节点及其子节点
//            if ((StringUtils.isBlank(extId) || (StringUtils.isNotBlank(extId) && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) && (type == null || (type != null && e.getType() <= type)) && (grade == null || (grade != null && e.getGrade() <= grade))) {
//                TreeNode treeNode = organManager.organToTreeNode(e);
//                mapList.add(treeNode);
//            }
//        }
        List<TreeNode> mapList = organManager.findOrganTree(null, extId);
        return mapList;
    }

    /**
     * 同步机构所有父级ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "syncAllParentIds")
    public Result syncAllParentIds(){
        organManager.syncAllParentIds();
        return Result.successResult();
    }
}
