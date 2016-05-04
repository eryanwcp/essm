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
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.encode.Encryption;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.google.common.collect.Lists;
import com.eryansky.core.excelTools.ExcelUtils;
import com.eryansky.core.excelTools.JsGridReportBase;
import com.eryansky.core.excelTools.TableData;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.sys._enum.*;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.entity.UserPassword;
import com.eryansky.modules.sys.service.*;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import org.apache.commons.fileupload.FileUploadBase;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 用户User管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-21 上午12:20:13
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController<User,String> {


    @Autowired
    private UserManager userManager;
    @Autowired
    private OrganManager organManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private PostManager postManager;
    @Autowired
    private UserPasswordManager userPasswordManager;

    @Override
    public EntityManager<User, String> getEntityManager() {
        return userManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/user";
    }

    /**
     *
     * @param userIds 已选择的用户ID
     * @param excludeUserIds 排除用户ID
     * @param dataScope {@link DataScope}
     * @param multiple
     * @return
     */
    @RequestMapping(value = {"select"})
    public ModelAndView selectPage(@RequestParam(value = "userIds", required = false)List<String> userIds,
                                   @RequestParam(value = "excludeUserIds", required = false)List<String> excludeUserIds,
                             String dataScope,Boolean multiple,@RequestParam(value = "cascade",required = false,defaultValue = "false")Boolean cascade) {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-select");
        List<User> users = Lists.newArrayList();
        if (Collections3.isNotEmpty(userIds)) {
            Criterion inUserCriterion = Restrictions.in("id",userIds);
            users = userManager.findByCriteria(inUserCriterion);

        }
        modelAndView.addObject("users", users);
        modelAndView.addObject("excludeUserIds", excludeUserIds);
        if(Collections3.isNotEmpty(excludeUserIds)){
            modelAndView.addObject("excludeUserIdStrs", Collections3.convertToString(excludeUserIds,","));
        }
        modelAndView.addObject("dataScope", dataScope);
        modelAndView.addObject("multiple", multiple);
        modelAndView.addObject("cascade", cascade);
        modelAndView.addObject("userDatagridData",
                JsonMapper.getInstance().toJson(new Datagrid(users.size(),users),User.class,
                    new String[]{"id","name","sexView","defaultOrganName"}));
        return modelAndView;
    }



    @RequestMapping(value = {"datagridSelectUser"})
    @ResponseBody
    public String datagridSelectUser(String organId, String loginNameOrName,
                                      @RequestParam(value = "excludeUserIds", required = false)List<String> excludeUserIds) {
        Page<User> page = new Page<User>(SpringMVCHolder.getRequest());
        page = userManager.findUsersByOrgan(page,organId, loginNameOrName,excludeUserIds);
        Datagrid<User> dg = new Datagrid<User>(page.getTotalCount(), page.getResult());
        return JsonMapper.getInstance().toJson(dg,User.class,new String[]{"id","name","sexView","defaultOrganName"});
    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public ModelAndView input(@ModelAttribute("model") User model) throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-input");
        return modelAndView;
    }


    @RequestMapping(value = {"_remove"})
    @ResponseBody
    @Override
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        Result result;
        userManager.deleteByIds(ids);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 自定义查询
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"userDatagrid"})
    @ResponseBody
    public Datagrid<User> userDatagrid(String organId,String loginNameOrName,Integer userType) {
        Page<User> page = new Page<User>(SpringMVCHolder.getRequest());
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(StringUtils.isBlank(organId)){
            organId = sessionInfo.getLoginOrganId();
        }

        page = userManager.findPage(page,organId, loginNameOrName, userType);
        Datagrid<User> dg = new Datagrid<User>(page.getTotalCount(), page.getResult());
        return dg;
    }

    /**
     * 用户列表
     *
     * @return
     * @throws Exception
     *
     */
    @RequestMapping(value = {"userList"})
    @ResponseBody
    public String userList(@RequestParam(value = "excludeUserIds", required = false)List<String> excludeUserIds,String dataScope,
                           @RequestParam(value = "includeUserIds", required = false)List<String> includeUserIds,
                           HttpServletRequest request, HttpServletResponse response,String query) {
        List<User> list = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if((StringUtils.isBlank(dataScope) || SecurityUtils.isCurrentUserAdmin())){
            list = userManager.findWithInclude(includeUserIds, query);
        }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.ALL.getValue()))){
            list = userManager.findAllNormalWithExclude(excludeUserIds);
        }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.COMPANY_AND_CHILD.getValue()))){
            User user = userManager.loadById(sessionInfo.getUserId());
            String organId = user.getCompanyId();
            list = userManager.findOwnerAndChildsUsers(organId, excludeUserIds);
        }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.COMPANY.getValue()))){
            User user = userManager.loadById(sessionInfo.getUserId());
            String organId = user.getCompanyId();
            List<String> organIds = organManager.findOrganChildsDepartmentOrganIds(organId);
            list = userManager.findUsersByOrganIds(organIds);
        }else if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.OFFICE_AND_CHILD.getValue()))){
            User user = userManager.loadById(sessionInfo.getUserId());
            String organId = user.getOfficeId();
            list = userManager.findOwnerAndChildsUsers(organId, excludeUserIds);
        }else{
            User user = userManager.loadById(sessionInfo.getUserId());
            String organId = user.getCompanyId();
            list = userManager.findOwnerAndChildsUsers(organId,excludeUserIds);
        }


        String json = JsonMapper.getInstance().toJson(list,User.class,
                new String[]{"id","name","defaultOrganName"});
//                new String[]{"id","name","sexView","defaultOrganName"});
        WebUtils.setExpiresHeader(response, 5 * 60 * 1000);
        return json;
    }

    /**
     * 自定义查询用户列表
     * @param dataScope
     * @param request
     * @param response
     * @param query
     * @return
     */
    @RequestMapping(value = {"customUserList"})
    @ResponseBody
    public String userList(String dataScope,
                           HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "includeUserIds", required = false)List<String> includeUserIds,
                           String query) {
        List<User> list = userManager.findWithInclude(includeUserIds,query);
        String json = JsonMapper.getInstance().toJson(list,User.class,
                new String[]{"id","name","defaultOrganName"});
//                new String[]{"id","name","sexView","defaultOrganName"});
        WebUtils.setExpiresHeader(response, 5 * 60 * 1000);
        return json;
    }

    /**
     * 获取机构用户
     * @param organId 机构ID
     * @return
     */
    @RequestMapping(value = {"combogridOrganUser"})
    @ResponseBody
    public String combogridOrganUser(@RequestParam(value = "organId", required = true)String organId) {
        List<User> users = userManager.findOrganUsers(organId);
        Datagrid dg = new Datagrid(users.size(),users);
        return JsonMapper.getInstance().toJson(dg,User.class,
                new String[]{"id","loginName","name","sexView","defaultOrganName","organNames"});
    }


    /**
     * 保存.
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model") User user) {
        getEntityManager().evict(user);//如过本方法中有对model.setXX操作 则需执行evict方法 防止Hibernate session自动同步
        Result result = null;
        // 名称重复校验
        User nameCheckUser = userManager.getUserByLoginName(user.getLoginName());
        if (nameCheckUser != null && !nameCheckUser.getId().equals(user.getId())) {
            result = new Result(Result.WARN, "登录名为[" + user.getLoginName() + "]已存在,请修正!", "loginName");
            logger.debug(result.toString());
            return result;
        }

        if (StringUtils.isBlank(user.getId())) {// 新增
            try {
                user.setOriginalPassword(Encryption.encrypt(user.getPassword()));
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            user.setPassword(Encrypt.e(user.getPassword()));
        } else {// 修改
            User superUser = userManager.getSuperUser();
            User sessionUser = SecurityUtils.getCurrentUser();
            if (superUser.getId().equals(user.getId()) && !sessionUser.getId().equals(superUser.getId())) {
                result = new Result(Result.ERROR, "超级用户信息仅允许自己修改!",null);
                logger.debug(result.toString());
                return result;
            }
        }

        userManager.saveEntity(user);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    /**
     * 修改用户密码页面.
     */
    @RequestMapping(value = {"password"})
    public String password(@ModelAttribute("model") User model) throws Exception {
        return "modules/sys/user-password";

    }

    /**
     * 修改用户密码.
     * @param id 用户ID
     * @param upateOperate 需要密码"1" 不需要密码"0".
     * @param password 原始密码
     * @param newPassword 新密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"updateUserPassword"})
    @ResponseBody
    public Result updateUserPassword(@RequestParam(value = "id", required = true)String id,
                                     @RequestParam(value = "upateOperate", required = true)String upateOperate,
                                     String password,
                                     @RequestParam(value = "newPassword", required = true)String newPassword) throws Exception {
        Result result;
        User u = userManager.loadById(id);
        if (u != null) {
            boolean isCheck = true;
            //需要输入原始密码
            if (AppConstants.USER_UPDATE_PASSWORD_YES.equals(upateOperate)) {
                String originalPassword = u.getPassword(); //数据库存储的原始密码
                String pagePassword = password; //页面输入的原始密码（未加密）
                checkSecurity(newPassword);

                if (!originalPassword.equals(Encrypt.e(pagePassword))) {
                    isCheck = false;
                }
            }
            //不需要输入原始密码
            if (AppConstants.USER_UPDATE_PASSWORD_NO.equals(upateOperate)) {
                isCheck = true;
            }
            if (isCheck) {
                u.setOriginalPassword(Encryption.encrypt(newPassword));
                u.setPassword(Encrypt.e(newPassword));
                userManager.saveEntity(u);
                UserUtils.addUserPasswordUpdate(u);

                result = Result.successResult();
            } else {
                result = new Result(Result.WARN, "原始密码输入错误.", "password");
            }
        } else {
            throw new ActionException("用户【"+id+"】不存在或已被删除.");
        }
        logger.debug(result.toString());
        return result;
    }

    public void checkSecurity(String pagePassword){
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(AppConstants.getIsSecurityOn()){
            int max = AppConstants.getUserPasswordRepeatCount();
            List<UserPassword> userPasswords = userPasswordManager.getUserPasswordsByUserId(sessionInfo.getUserId(),max);
            if(Collections3.isNotEmpty(userPasswords)){
                for(UserPassword userPassword:userPasswords){
                    if (userPassword.getPassword().equals(Encrypt.e(pagePassword))) {
                        throw new ActionException("你输入的密码在最近"+max+"次以内已使用过，请更换！");
                    }
                }
            }
        }
    }

    /**
     * 修改用户密码 批量、无需输入原密码.
     * @param userIds 用户ID集合
     * @param newPassword 新密码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"_updateUserPassword"})
    @ResponseBody
    public Result updateUserPassword(@RequestParam(value = "userIds", required = false) List<String> userIds,
                                     @RequestParam(value = "newPassword", required = true)String newPassword) throws Exception {
        userManager.updateUserPassword(userIds,newPassword);
        return Result.successResult();
    }


    /**
     * 修改用户角色页面.
     */
    @RequestMapping(value = {"role"})
    public ModelAndView role(@ModelAttribute("model") User model) throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-role");
        return modelAndView;
    }

    /**
     * 修改用户角色.
     */
    @RequestMapping(value = {"updateUserRole"})
    @ResponseBody
    public Result updateUserRole(@RequestParam(value = "userIds", required = false) List<String> userIds,
                                 @RequestParam(value = "roleIds", required = false) List<String> roleIds) throws Exception {
        Result result = null;
        userManager.updateUserRole(userIds,roleIds);
        userManager.clearCache();
        result = Result.successResult();
        return result;
    }

    /**
     * 设置组织机构页面.
     */
    @RequestMapping(value = {"organ"})
    public String organ(@ModelAttribute("model") User user, Model model) throws Exception {
        //设置默认组织机构初始值
        List<Combobox> defaultOrganCombobox = Lists.newArrayList();
        if (user.getId() != null) {
            List<Organ> organs = user.getOrgans();
            Combobox combobox;
            if (!Collections3.isEmpty(organs)) {
                for (Organ organ : organs) {
                    combobox = new Combobox(organ.getId().toString(), organ.getName());
                    defaultOrganCombobox.add(combobox);
                }
            }
        }
        String defaultOrganComboboxData = JsonMapper.nonDefaultMapper().toJson(defaultOrganCombobox);
        logger.debug(defaultOrganComboboxData);
        model.addAttribute("defaultOrganComboboxData", defaultOrganComboboxData);
        return "modules/sys/user-organ";
    }

    /**
     * 设置用户机构 批量更新用户 机构信息
     * @param userIds 用户Id集合
     * @param organIds 所所机构ID集合
     * @param defaultOrganId 默认机构
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"updateUserOrgan"})
    @ResponseBody
    public Result updateUserOrgan(@RequestParam(value = "userIds", required = false) List<String> userIds,
                                  @RequestParam(value = "organIds", required = false) List<String> organIds, String defaultOrganId) throws Exception {
        Result result = null;
        userManager.updateUserOrgan(userIds, organIds, defaultOrganId);
        result = Result.successResult();
        return result;

    }

    /**
     * 设置用户岗位页面.
     */
    @RequestMapping(value = {"post"})
    public String post(@ModelAttribute("model") User model,String organId,Model uiModel) throws Exception {
        uiModel.addAttribute("organId",organId);
        return "modules/sys/user-post";
    }

    /**
     * 修改用户岗位.
     * @param userIds 用户Id集合
     * @param postIds 岗位ID集合
     */
    @RequestMapping(value = {"updateUserPost"})
    @ResponseBody
    public Result updateUserPost(@RequestParam(value = "userIds", required = false)List<String> userIds,
                                 @RequestParam(value = "postIds", required = false) List<String> postIds) {
        Result result = null;
        userManager.updateUserPost(userIds,postIds);
        result = Result.successResult();
        return result;
    }

    /**
     * 修改用户资源页面.
     */
    @RequestMapping(value = {"resource"})
    public String resource(@ModelAttribute("model") User model,Model uiModel) throws Exception {
        List<TreeNode> treeNodes = resourceManager.findTreeNodeResources();
        String resourceComboboxData = JsonMapper.getInstance().toJson(treeNodes);
        logger.debug(resourceComboboxData);
        uiModel.addAttribute("resourceComboboxData", resourceComboboxData);
        return "modules/sys/user-resource";
    }

    /**
     * 修改用户资源.
     * @param userIds 用户ID集合
     * @param resourceIds 资源ID集合
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"updateUserResource"})
    @ResponseBody
    public Result updateUserResource(@RequestParam(value = "userIds", required = false) List<String> userIds,
                                     @RequestParam(value = "resourceIds", required = false)List<String> resourceIds) throws Exception {
        Result result = null;
        userManager.updateUserResource(userIds,resourceIds);
        userManager.clearCache();
        result = Result.successResult();
        return result;

    }

    /**
     * 头像 文件上传
     * @param request
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = {"upload"})
    @ResponseBody
    public Result upload(HttpServletRequest request,
                         @RequestParam(value = "uploadFile", required = false)MultipartFile multipartFile) {
        Result result = null;
        try {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            com.eryansky.modules.disk.entity.File file = DiskUtils.saveSystemFile(DiskUtils.FOLDER_USER_PHOTO, sessionInfo, multipartFile);
            String filename =  DiskUtils.getVirtualFilePath(file);
            result = Result.successResult().setObj(filename);
        } catch (InvalidExtensionException e) {
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG+e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (IOException e){
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG+e.getMessage());
        }

        return result;
    }


    /**
     * 性别下拉框
     *
     * @throws Exception
     */
    @RequestMapping(value = {"sexTypeCombobox"})
    @ResponseBody
    public List<Combobox> sexTypeCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        SexType[] _enums = SexType.values();
        for (int i = 0; i < _enums.length; i++) {
            Combobox combobox = new Combobox(_enums[i].getValue().toString(), _enums[i].getDescription());
            cList.add(combobox);
        }
        return cList;
    }




    /**
     *
     * @param q 查询关键字
     * @return
     * @throws Exception
     */
    @RequiresUser(required = false)
    @RequestMapping(value = {"autoComplete"})
    @ResponseBody
    public List<String> autoComplete(String q) throws Exception {
        List<String> cList = Lists.newArrayList();
        List<PropertyFilter> filters = Lists.newArrayList();
        PropertyFilter propertyFilter = new PropertyFilter("LIKES_name",q);
        PropertyFilter statusFilter = new PropertyFilter("EQS_status",StatusState.NORMAL.getValue());
        filters.add(propertyFilter);
        filters.add(statusFilter);
        Page<User> page = new Page<User>(SpringMVCHolder.getRequest());
        page = userManager.findPage(page,filters);
        for (User user:page.getResult()) {
            cList.add(user.getName());
        }
        return cList;
    }

    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort(){
        Result result;
        Integer maxSort = userManager.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        return result;
    }



    /**
     * 修改用户信息.
     */
    @RequestMapping("userInfoInput")
    public ModelAndView userInfoInput() {
        ModelAndView modelAndView = new ModelAndView("layout/north-userInfoInput");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        User user = userManager.loadById(sessionInfo.getUserId());
        JsonMapper jsonMapper = JsonMapper.getInstance();
//        解决hibernate延时加载设置
        jsonMapper.registerHibernate4Module();
        modelAndView.addObject("userJson",jsonMapper.toJson(user));
        return modelAndView;
    }

    /**
     * 保存用户信息.
     */
    @RequestMapping("saveUserinfo")
    @ResponseBody
    public Result saveUserinfo(@ModelAttribute("model")User model) throws Exception {
        Result result = null;
        userManager.saveEntity(model);
        result = Result.successResult();
        return result;
    }

    /**
     * 机构用户树
     * @param checkedUserIds 选中的用户ID集合
     * @return
     */
    @RequiresUser(required = false)
    @RequestMapping(value = { "organUserTreePage" })
    public ModelAndView organUserTreePage(String parentId,
                                          @RequestParam(value = "checkedUserIds", required = false)List<String> checkedUserIds,
                                          @RequestParam(value = "checkbox",defaultValue = "true")Boolean checkbox,
                                          @RequestParam(value = "cascade",defaultValue = "true")Boolean cascade) {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-tree");
//        List<TreeNode> treeNodes = organManager.findOrganUserTree(parentId, checkedUserIds,false);
        modelAndView.addObject("checkedUserIds",checkedUserIds);
        modelAndView.addObject("checkbox",checkbox);
        modelAndView.addObject("cascade",cascade);
//        modelAndView.addObject("organUserTreeData",JsonMapper.getInstance().toJson(treeNodes));
        return modelAndView;
    }


    @RequiresUser(required = false)
    @RequestMapping(value = { "organUserTree" })
    @ResponseBody
    public List<TreeNode> organUserTree(String parentId,
                                          @RequestParam(value = "checkedUserIds", required = false)List<String> checkedUserIds,
                                          @RequestParam(value = "checkbox",defaultValue = "true")Boolean checkbox,
                                        @RequestParam(value = "cascade",defaultValue = "true")Boolean cascade) {
        List<TreeNode> treeNodes = organManager.findOrganUserTree(parentId, checkedUserIds,cascade);
        return treeNodes;
    }

    /**
     * 排序调整
     * @param upUserId
     * @param downUserId
     * @param moveUp
     * @return
     */
    @RequestMapping(value = {"changeOrderNo"})
    @ResponseBody
    public Result changeOrderNo(@RequestParam(required = true) String upUserId,
                                @RequestParam(required = true)String downUserId,
                                boolean moveUp){
        userManager.changeOrderNo(upUserId,downUserId,moveUp);
        return Result.successResult();
    }

    /**
     * 锁定用户 批量
     * @param userIds
     * @param status {@link com.eryansky.common.orm.entity.StatusState}
     * @return
     */
    @RequestMapping(value = {"lock"})
    @ResponseBody
    public Result lock(@RequestParam(value = "userIds", required = false) List<String> userIds,
                       @RequestParam(required = false,defaultValue = User.STATUS_DELETE)String status){
        userManager.lockUsers(userIds,status);
        return Result.successResult();
    }

    /**
     * 查看用户密码
     * @param loginName
     * @return
     * @throws Exception
     */
    @RequestMapping("viewUserPassword")
    @ResponseBody
    public Result viewUserPassword(String loginName) throws Exception{
        Result result = Result.successResult();
        User user = userManager.getUserByLoginName(loginName);
        if(user != null && user.getOriginalPassword() != null){
            result.setObj(Encryption.decrypt(user.getOriginalPassword().trim()));
        }
        return result;
    }

    /**
     * 多Sheet Excel导出，获取的数据格式是List<Object[]>
     * @return
     * @throws Exception
     */
    @RequestMapping("export")
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("application/msexcel;charset=UTF-8");
        List<User> users = userManager.findAllNormal();

        List<Object[]> list = new ArrayList<Object[]>();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            list.add(new Object[]{user.getCompanyName(),user.getOfficeName(),user.getLoginName(),user.getName(),user.getSexView(),
                    user.getTel(),user.getMobile(),user.getEmail()});
        }

        List<TableData> tds = new ArrayList<TableData>();

        //Sheet2
        String[] hearders = new String[] {"单位","部门","账号", "姓名", "性别", "电话","手机号码","邮箱"};//表头数组
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(hearders),null);
        td.setSheetTitle("普通表头示例");
        tds.add(td);

        String title = "用户信息导出示例";
        JsGridReportBase report = new JsGridReportBase(request, response);
        report.exportToExcel(title, SecurityUtils.getCurrentSessionInfo().getName(), tds);

    }
}
