/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.RoleType;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.mapper.Role;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.*;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 角色Role管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends SimpleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;

    @ModelAttribute("model")
    public Role get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return roleService.get(id);
        }else{
            return new Role();
        }
    }

    @Logging(value = "角色管理",logType = LogType.access)
    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/role";
    }

    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public String datagrid() {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        // 自动构造属性过滤器
        Page<Role> p = new Page<Role>(request);

        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Role entity = new Role();
        String organId = sessionInfo.getLoginCompanyId();
        if (!SecurityUtils.isCurrentUserAdmin()) {
            entity.setOrganId(organId);
        }
        p = roleService.findPage(p,entity);
        Datagrid<Role> datagrid = new Datagrid<Role>(p.getTotalCount(), p.getResult());
        String json = JsonMapper.getInstance().toJson(datagrid,Role.class,
                new String[]{"id","name","code","isSystemView","organName","dataScopeView","resourceNames","dataScope","remark"});
        return json;
    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model") Role model, Model uiModel) throws Exception {
        if(StringUtils.isBlank(model.getId()) && !SecurityUtils.isCurrentUserAdmin()){
            model.setIsSystem(YesOrNo.NO.getValue());
        }
        uiModel.addAttribute("organIds", roleService.findRoleOrganIds(model.getId()));
        uiModel.addAttribute("model", model);
        uiModel.addAttribute("roleTypes", RoleType.values());
        return "modules/sys/role-input";
    }

    /**
     * 保存.
     */
    @Logging(value = "角色管理-保存角色",logType = LogType.access)
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Role role) {
        Result result;
        // 编码重复校验
        if (StringUtils.isNotBlank(role.getCode())) {
            Role checkRole = roleService.getByCode(role.getCode());
            if (checkRole != null && !checkRole.getId().equals(role.getId())) {
                result = new Result(Result.WARN, "编码为[" + role.getCode() + "]已存在,请修正!", "code");
                logger.debug(result.toString());
                return result;
            }
        }

        roleService.saveRole(role);
        result = Result.successResult();
        return result;
    }

    /**
     * 删除.
     */
    @Logging(value = "角色管理-删除角色",logType = LogType.access)
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        Result result;
        roleService.deleteByIds(ids);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }



    /**
     * 设置资源 页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"resource"})
    public String resource(@ModelAttribute("model") Role model,Model uiModel) throws Exception {
        List<TreeNode> treeNodes = resourceService.findTreeNodeResourcesWithPermissions(SecurityUtils.getCurrentUserId());
        String resourceComboboxData = JsonMapper.getInstance().toJson(treeNodes);
        logger.debug(resourceComboboxData);
        uiModel.addAttribute("resourceComboboxData", resourceComboboxData);
        uiModel.addAttribute("model", model);
        List<String> resourceIds = resourceService.findResourceIdsByRoleId(model.getId());
        uiModel.addAttribute("resourceIds", resourceIds);
        return "modules/sys/role-resource";
    }

    /**
     * 设置角色资源
     *
     * @return
     */
    @Logging(value = "角色管理-角色资源",logType = LogType.access)
    @RequestMapping(value = {"updateRoleResource"})
    @ResponseBody
    public Result updateRoleResource(@RequestParam(value = "resourceIds", required = false) Set<String> resourceIds,
                                     @ModelAttribute("model") Role role) {
        roleService.saveRoleResources(role.getId(),resourceIds);
        return Result.successResult();
    }

    /**
     * 角色用户 页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"user"})
    public String user(@ModelAttribute("model")Role model, Model uiModel) throws Exception {
        List<String> userIds = userService.findUserIdsByRoleId(model.getId());
        uiModel.addAttribute("userIds",userIds);
        return "modules/sys/role-user";
    }

    /**
     * 角色用户数据
     * @param roleId 角色ID
     * @param name 角色ID
     * @return
     */
    @RequestMapping(value = {"userDatagrid"})
    @ResponseBody
    public String userDatagrid(@RequestParam(value = "roleId", required = true)String roleId,
                               String name){
        Page<User> page = new Page<User>(SpringMVCHolder.getRequest());
        page = userService.findPageRoleUsers(page,roleId,name);
        Datagrid<User> dg = new Datagrid<User>(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,User.class,
                new String[]{"id","name","sexView","sort","defaultOrganName"});

        return json;
    }

    /**
     *
     * @param roleId 角色ID
     * @return
     */
    @RequestMapping(value = {"select"})
    public ModelAndView selectPage(String roleId) {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-select");
        List<User> users = null;
        List<String> excludeUserIds = userService.findUserIdsByRoleId(roleId);
        modelAndView.addObject("users", users);
        modelAndView.addObject("excludeUserIds", excludeUserIds);
        if(Collections3.isNotEmpty(excludeUserIds)){
            modelAndView.addObject("excludeUserIdStrs", Collections3.convertToString(excludeUserIds,","));
        }
        modelAndView.addObject("dataScope", "2");//不分级授权
        modelAndView.addObject("cascade", "true");//不分级授权
        modelAndView.addObject("multiple", "");
        modelAndView.addObject("userDatagridData",JsonMapper.getInstance().toJson(new Datagrid()));
        return modelAndView;
    }

    /**
     * 添加角色关联用户
     * @param model 角色
     * @param userIds 用户ID
     * @return
     */
    @Logging(value = "角色管理-添加关联用户",logType = LogType.access)
    @RequestMapping(value = {"addRoleUser"})
    @ResponseBody
    public Result addRoleUser(@ModelAttribute("model") Role model,
                              @RequestParam(value = "userIds", required = true)Set<String> userIds){
        roleService.insertRoleUsers(model.getId(),userIds);
        return Result.successResult();
    }


    /**
     * 移除角色关联用户
     * @param model 角色
     * @param userIds 用户IDS
     * @return
     */
    @Logging(value = "角色管理-移除关联用户",logType = LogType.access)
    @RequestMapping(value = {"removeRoleUser"})
    @ResponseBody
    public Result removeRoleUser(@ModelAttribute("model") Role model,
                                 @RequestParam(value = "userIds", required = true)Set<String> userIds){
        roleService.deleteRoleUsersByRoleIdANDUserIds(model.getId(),userIds);
        return Result.successResult();
    }


    /**
     * 设置角色用户
     *
     * @return
     * @throws Exception
     */
    @Logging(value = "角色管理-保存角色用户",logType = LogType.access)
    @RequestMapping(value = {"updateRoleUser"})
    @ResponseBody
    public Result updateRoleUser(@ModelAttribute("model") Role model,
                                 @RequestParam(value = "userIds", required = false) Set<String> userIds) throws Exception {
        roleService.saveRoleUsers(model.getId(),userIds);
        return Result.successResult();
    }

    /**
     * 数据范围下拉列表
     * @param selectType {@link SelectType}
     * @return
     */
    @RequestMapping(value = {"dataScope"})
    @ResponseBody
    public List<Combobox> dataScope(String selectType){
        DataScope[] list = DataScope.values();
        List<Combobox> cList = Lists.newArrayList();

        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        for (DataScope r : list) {
            Combobox combobox = new Combobox(r.getValue() + "", r.getDescription());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 角色下拉框列表.
     */
    @RequestMapping(value = {"combobox"})
    @ResponseBody
    public List<Combobox> combobox(String selectType) throws Exception {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String organId = sessionInfo.getLoginCompanyId();

        List<Role> list = SecurityUtils.isCurrentUserAdmin() ? roleService.findAll():roleService.findOrganRolesAndSystemRoles(organId);

        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if (titleCombobox != null) {
            cList.add(titleCombobox);
        }
        for (Role r : list) {
            Combobox combobox = new Combobox(r.getId(), r.getName());
            cList.add(combobox);
        }
        return cList;
    }



    /**
     * 机构树.
     */
    @RequestMapping(value = {"tree"})
    @ResponseBody
    public List<TreeNode> tree(String selectType) throws Exception {
        List<TreeNode> treeNodes = Lists.newArrayList();
        List<Role> list = roleService.findAll();

        TreeNode titleTreeNode =SelectType.treeNode(selectType);
        if(titleTreeNode != null){
            treeNodes.add(titleTreeNode);
        }

        for(Role r:list){
            TreeNode treeNode = new TreeNode(r.getId(),r.getName());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }
}
