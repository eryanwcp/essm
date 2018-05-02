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
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys._enum.RoleType;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.entity.Role;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.RoleManager;
import com.eryansky.modules.sys.service.UserManager;
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
import java.util.Iterator;
import java.util.List;

/**
 * 角色Role管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends BaseController<Role,String> {

    @Autowired
    private RoleManager roleManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private UserManager userManager;

    @Override
    public EntityManager<Role, String> getEntityManager() {
        return roleManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/role";
    }

    @RequestMapping(value = {"_datagrid"})
    @ResponseBody
    public String _datagrid() {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        // 自动构造属性过滤器
        List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(request);
        Page<Role> p = new Page<Role>(request);

        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String organId = sessionInfo.getLoginCompanyId();
        PropertyFilter propertyFilter = new PropertyFilter("EQS_organId",organId);
        if (!SecurityUtils.isCurrentUserAdmin()) {
            filters.add(propertyFilter);
        }

        p = getEntityManager().findPage(p, filters,true);
        Datagrid<Role> datagrid = new Datagrid<Role>(p.getTotalCount(), p.getResult());
        String json = JsonMapper.getInstance().toJson(datagrid,Role.class,
                new String[]{"id","name","code","isSystemView","organName","dataScopeView","resourceNames","dataScope","remark"});
        return json;
    }

    /**
     * @param role
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model") Role role,Model uiModel) throws Exception {
        if(StringUtils.isBlank(role.getId()) && !SecurityUtils.isCurrentUserAdmin()){
            role.setIsSystem(YesOrNo.NO.getValue());
        }
        uiModel.addAttribute("model", role);
        uiModel.addAttribute("roleTypes", RoleType.values());
        return "modules/sys/role-input";
    }

    /**
     * 删除.
     */
    @RequestMapping(value = {"_remove"})
    @ResponseBody
    public Result _remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        Result result;
        roleManager.deleteByIds(ids);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 保存.
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") Role role) {
//        getEntityManager().evict(role);
        Result result;
        // 编码重复校验
        if (StringUtils.isNotBlank(role.getCode())) {
            Role checkRole = roleManager.findUniqueBy("code", role.getCode());
            if (checkRole != null && !checkRole.getId().equals(role.getId())) {
                result = new Result(Result.WARN, "编码为[" + role.getCode() + "]已存在,请修正!", "code");
                logger.debug(result.toString());
                return result;
            }
        }

        roleManager.saveEntity(role);
        result = Result.successResult();
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
        List<TreeNode> treeNodes = null;
        if(SecurityUtils.isCurrentUserAdmin()){
            treeNodes = resourceManager.findTreeNodeResources();
        }else{
            treeNodes = resourceManager.findTreeNodeResourcesWithPermissions(SecurityUtils.getCurrentUserId());
        }
        String resourceComboboxData = JsonMapper.getInstance().toJson(treeNodes);
        logger.debug(resourceComboboxData);
        uiModel.addAttribute("resourceComboboxData", resourceComboboxData);
        uiModel.addAttribute("model", model);
        return "modules/sys/role-resource";
    }

    /**
     * 设置角色资源
     *
     * @return
     */
    @RequestMapping(value = {"updateRoleResource"})
    @ResponseBody
    public Result updateRoleResource(@RequestParam(value = "resourceIds", required = false) List<String> resourceIds,
                                     @ModelAttribute("model") Role role) {
        Result result;
        //设置用户角色信息
        List<Resource> resourceList = Lists.newArrayList();
        if (Collections3.isNotEmpty(resourceIds)) {
            for (String resourceId : resourceIds) {
                Resource resource = resourceManager.loadById(resourceId);
                resourceList.add(resource);
            }
        }
        role.setResources(resourceList);

        roleManager.saveEntity(role);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 角色用户 页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"user"})
    public String user(@ModelAttribute("model")Role role,Model model) throws Exception {
        model.addAttribute("userIds",JsonMapper.getInstance().toJson(role.getUserIds()));
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
        page = roleManager.findPageRoleUsers(page,roleId,name);
        Datagrid<User> dg = new Datagrid<User>(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,User.class,
                new String[]{"id","name","sexView","orderNo","defaultOrganName"});

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
        Role role = roleManager.loadById(roleId);
        List<String> excludeUserIds = role.getUserIds();
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
     * @param roleId 角色Id
     * @param userIds 用户ID
     * @return
     */
    @RequestMapping(value = {"addRoleUser"})
    @ResponseBody
    public Result addRoleUser(String roleId,
                              @RequestParam(value = "userIds", required = true)List<String> userIds){
        Role role = roleManager.loadById(roleId);
        List<User> roleUsers = role.getUsers();
        for(String userId:userIds){
            boolean flag = true;
            for(User roleUser:roleUsers){
                if(userId.equals(roleUser.getId())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                User addUser = userManager.loadById(userId);
                roleUsers.add(addUser);
            }

        }
        roleManager.update(role);
        return Result.successResult();
    }


    /**
     * 移除角色关联用户
     * @param roleId 角色Id
     * @param userIds 用户ID
     * @return
     */
    @RequestMapping(value = {"removeRoleUser"})
    @ResponseBody
    public Result removeRoleUser(String roleId,
                                 @RequestParam(value = "userIds", required = true)List<String> userIds){
        Role role = roleManager.loadById(roleId);
        List<User> roleUsers = role.getUsers();
        Iterator<User> iterator = roleUsers.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            for(String userId:userIds){
                if(userId.equals(user.getId())){
                    iterator.remove();
                    break;
                }
            }
        }
        roleManager.update(role);
        return Result.successResult();
    }


    /**
     * 设置机构用户
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"updateRoleUser"})
    @ResponseBody
    public Result updateRoleUser(@RequestParam(value = "userIds", required = false) List<String> userIds,
                                 @ModelAttribute("model") Role role) throws Exception {
        getEntityManager().evict(role);
        Result result;
        //设置用户角色信息
        List<User> userList = Lists.newArrayList();
        if (Collections3.isNotEmpty(userIds)) {
            for (String userId : userIds) {
                User user = userManager.loadById(userId);
                userList.add(user);
            }
        }

        role.setUsers(userList);

        roleManager.saveEntity(role);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
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
        User user = SecurityUtils.getCurrentUser();
        String organId = user.getCompanyId();

        List<Role> list = roleManager.findOrganRolesAndSystemRoles(organId);
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
        List<Role> list = roleManager.getAll();

        TreeNode titleTreeNode =SelectType.treeNode(selectType);
        if(titleTreeNode != null){
            treeNodes.add(titleTreeNode);
        }

        for(Role r:list){
            TreeNode treeNode = new TreeNode(r.getId().toString(),
                    r.getName());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }
}
