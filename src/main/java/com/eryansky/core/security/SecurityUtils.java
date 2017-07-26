/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.IpUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys.entity.Post;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.entity.Role;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 系统使用的特殊工具类 简化代码编写.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-18 上午8:25:36
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static ResourceManager resourceManager = SpringContextHolder.getBean(ResourceManager.class);
    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);
    private static ApplicationSessionContext applicationSessionContext = ApplicationSessionContext.getInstance();

    /**
     * 是否授权某个资源
     *
     * @param resourceCode 资源编码
     * @return
     */
    public static Boolean isPermitted(String resourceCode) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (sessionInfo.isSuperUser()) {// 超级用户
                flag = true;
            } else {
//                flag = resourceManager.isUserPermittedResourceCode(sessionInfo.getUserId(), resourceCode);
                for (Permisson permisson:sessionInfo.getPermissons()) {
                    if (resourceCode.equalsIgnoreCase(permisson.getCode())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 授权某个URL地址
     * @param url
     * @return
     */
    public static Boolean isPermittedUrl(String url) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (sessionInfo.isSuperUser()) {// 超级用户
                flag = true;
            } else {
                boolean needInterceptor = resourceManager.isInterceptorUrl(url);//是否需要拦截
                if(needInterceptor){
                    for (Permisson permisson:sessionInfo.getPermissons()) {
                        if(!flag && StringUtils.isNotBlank(permisson.getMarkUrl())){
                            String[] markUrls = permisson.getMarkUrl().split(";");
                            for(int i=0;i<markUrls.length;i++){
                                if(StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i],url)){
                                    flag = true;
                                    break;
                                }
                            }
                        }

                    }
                }else{//无需拦截
                    flag = true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 是否授权某个角色
     *
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(String roleCode) {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return isPermittedRole(sessionInfo.getUserId(), roleCode);
    }

    /**
     * 判断某个用户是否授权某个角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(String userId, String roleCode) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户[" + userId + "]不存在.");
            }


            if (sessionInfo.isSuperUser()) {// 超级用户
                flag = true;
            } else {
                for (PermissonRole permissonRole : sessionInfo.getPermissonRoles()) {
                    if (roleCode.equalsIgnoreCase(permissonRole.getCode())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * 获取当前用户最大的数据权限范围
     * @return
     */
    public static String getUserMaxRoleDataScope(){
        return isCurrentUserAdmin() ? DataScope.ALL.getValue() : getUserMaxRoleDataScope(getCurrentUserId());
    }

    /**
     * 判断当前用户是否授权所有数据
     * @return
     */
    public static boolean isPermittedMaxRoleDataScope(){
        return isPermittedMaxRoleDataScope(getCurrentUserId());
    }

    /**
     * 判断用户是否授权所有数据
     * @return
     */
    public static boolean isPermittedMaxRoleDataScope(String userId){
        return isCurrentUserAdmin() || DataScope.ALL.getValue().equals(getUserMaxRoleDataScope(userId));
    }

    /**
     * 获取用户最大的数据权限范围
     * @param userId
     * @return
     */
    public static String getUserMaxRoleDataScope(String userId){
        User user = UserUtils.getUser(userId);
        // 获取到最大的数据权限范围
        int dataScopeInteger = Integer.valueOf(DataScope.SELF.getValue());
        for (Role r : user.getRoles()) {
            if(StringUtils.isBlank(r.getDataScope())){
                continue;
            }
            int ds = Integer.valueOf(r.getDataScope());
            if (ds == Integer.valueOf(DataScope.CUSTOM.getValue())) {
                dataScopeInteger = ds;
                break;
            } else if (ds < dataScopeInteger) {
                dataScopeInteger = ds;
            }
        }
        String dataScopeString = String.valueOf(dataScopeInteger);
        return dataScopeString;
    }

    /**
     * User转SessionInfo.
     *
     * @param user
     * @return
     */
    public static SessionInfo userToSessionInfo(User user) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(user.getId());
        sessionInfo.setName(user.getName());
        sessionInfo.setLoginName(user.getLoginName());
        sessionInfo.setRoleIds(user.getRoleIds());
        sessionInfo.setRoleNames(user.getRoleNames());
        sessionInfo.setLoginOrganId(user.getDefaultOrganId());
        sessionInfo.setLoginOrganSysCode(user.getDefaultOrganSysCode());
        sessionInfo.setLoginOrganName(user.getDefaultOrganName());
        sessionInfo.setOrganNames(user.getOrganNames());
        sessionInfo.setName(user.getName());
        return sessionInfo;
    }

    /**
     * 判断当前用户是否有某个岗位
     *
     * @param postCode 角色编码
     * @return
     */
    public static Boolean hasPost(String postCode) {
        return hasPost(null,postCode);
    }

    /**
     * 判断某个用户是否有某个刚问
     *
     * @param userId   用户ID
     * @param postCode 角色编码
     * @return
     */
    public static Boolean hasPost(String userId, String postCode) {
        boolean flag = false;
        try {
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (userId == null) {
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户[" + userId + "]不存在.");
            }

            if(sessionInfo != null && userId.equals(sessionInfo.getUserId())){
                for(String pCode:sessionInfo.getPostCodes()){
                    if (postCode.equalsIgnoreCase(pCode)) {
                        return true;
                    }
                }
            }

            User user = UserUtils.getUser(userId);
            for (Post post : user.getPosts()) {
                if (postCode.equalsIgnoreCase(post.getCode())) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * 将用户放入session中.
     *
     * @param user
     */
    public static void putUserToSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        if(logger.isDebugEnabled()){
            logger.debug("putUserToSession:{}", sessionId);
        }
        SessionInfo sessionInfo = userToSessionInfo(user);
        sessionInfo.setIp(IpUtils.getIpAddr(request));
        sessionInfo.setUserAgent(UserAgentUtils.getHTTPUserAgent(request));
        sessionInfo.setDeviceType(UserAgentUtils.getDeviceType(request).toString());
        sessionInfo.setBrowserType(UserAgentUtils.getBrowser(request).getName());
        sessionInfo.setId(sessionId);
        List<Resource> resources = resourceManager.findResourcesByUserId(sessionInfo.getUserId());
        if (Collections3.isNotEmpty(resources)) {
            for(Resource resource:resources){
                if(StringUtils.isNotBlank(resource.getCode()) || StringUtils.isNotBlank(resource.getMarkUrl())){
                    sessionInfo.addPermissons(new Permisson(resource.getCode(),resource.getMarkUrl()));
                }
            }
        }
        List<Role> roles = user.getRoles();
        if (Collections3.isNotEmpty(roles)) {
            for(Role role:roles){
                sessionInfo.addPermissonRoles(new PermissonRole(role.getCode()));
            }
        }

        List<Post> posts = user.getPosts();
        if (Collections3.isNotEmpty(posts)) {
            for(Post post:posts){
                if(StringUtils.isNotBlank(post.getCode())){
                    sessionInfo.getPostCodes().add(post.getCode());
                }
            }
        }

        applicationSessionContext.addSession(sessionInfo);
    }

    /**
     * 获取当前用户session信息.
     */
    public static SessionInfo getCurrentSessionInfo() {
        SessionInfo sessionInfo = null;
        try {
//            System.out.println(UserAgentUtils.getUserAgent(SpringMVCHolder.getRequest())+" "+SpringMVCHolder.getRequest().getRequestURI()+" "+SpringMVCHolder.getSession().getId());
            sessionInfo = applicationSessionContext.getSession(SpringMVCHolder.getSession().getId());
        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
        }
        return sessionInfo;
    }

    /**
     * 获取当前登录用户信息.
     */
    public static User getCurrentUser() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        User user = null;
        if(sessionInfo != null){
            user = userManager.loadById(sessionInfo.getUserId());
        }
        return user;
    }

    /**
     * 获取当前登录用户信息.
     */
    public static String getCurrentUserId() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        if(sessionInfo != null){
            return sessionInfo.getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录用户账号信息.
     */
    public static String getCurrentUserLoginName() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        if(sessionInfo != null){
            return sessionInfo.getLoginName();
        }
        return null;
    }

    /**
     * 判断当前用户登录用户 是否是超级管理员
     * @return
     */
    public static boolean isCurrentUserAdmin() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return isUserAdmin(sessionInfo.getUserId());
    }


    /**
     * 判断是否是超级管理员
     * @param userId 用户ID
     * @return
     */
    public static boolean isUserAdmin(String userId) {
        User superUser = userManager.getSuperUser();
        boolean flag = false;
        if (userId != null && superUser != null
                && userId.equals(superUser.getId())) {// 超级用户
            flag = true;
        }
        return flag;
    }

    /**
     * 根据用户ID获取用户对象
     * @param userId
     * @return
     */
    public static User getUserById(String userId) {
        User user = null;
        if(userId != null){
            user = userManager.loadById(userId);
        }
        return user;
    }

    /**
     * 用户下线
     * @param sessionId sessionID
     */
    public static void offLine(String sessionId){
        removeUserFromSession(sessionId, SecurityType.offline);
    }

    /**
     * 用户下线
     * @param sessionIds sessionID集合
     */
    public static void offLine(List<String> sessionIds){
        if(Collections3.isNotEmpty(sessionIds)){
            for(String sessionId:sessionIds){
                removeUserFromSession(sessionId, SecurityType.offline);
            }
        }
    }

    /**
     * 全部下线
     */
    public static void offLineAll(){
        List<SessionInfo> sessionInfos = SecurityUtils.getSessionUser().getRows();
        for(SessionInfo sessionInfo:sessionInfos){
            removeUserFromSession(sessionInfo.getId(), SecurityType.offline);
        }
    }

    /**
     * 将用户信息从session中移除
     *
     * @param sessionId session ID
     */
    public static void removeUserFromSession(String sessionId, SecurityType securityType) {
        SessionInfo _sessionInfo = applicationSessionContext.getSession(sessionId);
        if(_sessionInfo != null){
            userManager.logout(_sessionInfo.getUserId(),securityType);
        }
        applicationSessionContext.removeSession(sessionId);

    }

    /**
     * 查看当前登录用户信息
     * @return
     */
    public static Datagrid<SessionInfo> getSessionUser() {
        List<SessionInfo> sessionInfoData= applicationSessionContext.getSessionInfoData();
        //排序
        Collections.sort(sessionInfoData, new Comparator<SessionInfo>() {
            @Override
            public int compare(SessionInfo o1, SessionInfo o2) {
                return o2.getLoginTime().compareTo(o1.getLoginTime());
            }
        });

        Datagrid<SessionInfo> dg = new Datagrid<SessionInfo>(sessionInfoData.size(), sessionInfoData);
        return dg;
    }


    /**
     * 查看某个用户登录信息
     * @param loginName 登录帐号
     * @return
     */
    public static List<SessionInfo> getSessionUser(String loginName) {
        Datagrid<SessionInfo> datagrid = getSessionUser();
        List<SessionInfo> sessionInfos = Lists.newArrayList();
        for(SessionInfo sessionInfo: datagrid.getRows()){
            if(sessionInfo.getLoginName().equals(loginName)){
                sessionInfos.add(sessionInfo);
            }
        }
        return sessionInfos;
    }

    /**
     * 根据SessionId查找对应的SessionInfo信息
     * @param sessionId
     * @return
     */
    public static SessionInfo getSessionInfo(String sessionId) {
        return applicationSessionContext.getSession(sessionId);
    }

    public static boolean isMobileLogin(){
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return sessionInfo != null && sessionInfo.isMobileLogin();
    }

}

