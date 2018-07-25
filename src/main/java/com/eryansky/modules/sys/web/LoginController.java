/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Menu;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.web.servlet.ValidateCodeServlet;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.CookieUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.modules.sys.service.ResourceService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.utils.AppUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityType;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.utils.CacheUtils;
import com.eryansky.modules.sys.mapper.Resource;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 用户登录/注销等前端交互入口
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-05-02 19:50
 */
@Controller
@RequestMapping(value = "${adminPath}/login")
public class LoginController extends SimpleController {

    @Autowired
    private UserService userService;
    @Autowired
    private ResourceService resourceService;

    /**
     * 欢迎页面
     * @return
     * @throws Exception
     */
    @Mobile(value = MobileValue.ALL)
    @RequiresUser(required = false)
    @RequestMapping(value = {"welcome", ""})
    public ModelAndView welcome() throws Exception {
        ModelAndView modelAndView = new ModelAndView("login");
        String loginName = CookieUtils.getCookie(SpringMVCHolder.getRequest(), "loginName");
        boolean isValidateCodeLogin = isValidateCodeLogin(loginName, false, false);
        modelAndView.addObject("isValidateCodeLogin",isValidateCodeLogin);
        return modelAndView;
    }


    /**
     * 是否是验证码登录
     * @param useruame 用户名
     * @param isFail 计数加1
     * @param clean 计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap==null){
            loginFailMap = Maps.newHashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum==null){
            loginFailNum = 0;
        }
        if (isFail){
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean){
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }

    /**
     * 登录用户数校验
     */
    private void checkLoginLimit(){
        int maxSize = AppConstants.getSessionUserMaxSize();
        int sessionUser = SecurityUtils.getSessionUser().getRows().size();
        if(maxSize < 0){//系统维护
            throw new SystemException("系统正在维护，请稍后再试！");
        }else if(maxSize != 0){//0 为不限制
            if(sessionUser >= maxSize){
                throw new SystemException("系统当前登录用户数量过多，请稍后再试！");
            }
        }
    }
    /**
     * 登录验证
     *
     * @param loginName 用户名
     * @param password  密码
     * @param validateCode 验证码
     * @param theme     主题
     * @param request
     * @return
     */
    @RequiresUser(required = false)
    @ResponseBody
    @RequestMapping(value = {"login"})
    public Result login(@RequestParam(required = true) String loginName,
                        @RequestParam(required = true) String password,
                        String validateCode,
                        String theme, HttpServletRequest request,Model uiModel) {
        //登录限制
        checkLoginLimit();

        Result result = null;
        String msg = null;
        final String VALIDATECODE_TIP= "密码输入错误超过3次，请输入验证码!";
        boolean isValidateCodeLogin = isValidateCodeLogin(loginName, false, false);
        if (isValidateCodeLogin && UserAgentUtils.isComputer(request)) {
            if(StringUtils.isBlank(validateCode)){
                return Result.errorResult().setMsg("密码输入错误超过3次，请输入验证码!").setObj(isValidateCodeLogin);
            }
            if(!ValidateCodeServlet.validate(request, validateCode)){
                msg = "验证码不正确或验证码已过期!";
                return Result.errorResult().setMsg(msg).setObj(isValidateCodeLogin);
            }
        }

        // 获取用户信息
        User user = userService.getUserByLP(loginName, Encrypt.e(password));
        if (user == null) {
            msg = "用户名或密码不正确!";
        } else if (user.getStatus().equals(StatusState.LOCK.getValue())) {
            msg = "该用户已被锁定，暂不允许登陆!";
        }
        if (msg != null) {
            isValidateCodeLogin = isValidateCodeLogin(loginName, true, false);
            if(isValidateCodeLogin){
                msg += VALIDATECODE_TIP;
            }
            result = new Result(Result.ERROR, msg, isValidateCodeLogin);
        } else {
            if(AppConstants.getIsSecurityOn()){
                List<SessionInfo> userSessionInfos = SecurityUtils.getSessionUser(loginName);
                if(AppConstants.getUserSessionSize() > 0 &&  userSessionInfos.size() >= AppConstants.getUserSessionSize() ){
                    result = new Result(Result.ERROR, "已达到用户最大会话登录限制["+AppConstants.getUserSessionSize()+"，请注销其它登录信息后再试！]", AppConstants.getUserSessionSize());
                    return result;
                }
            }

            //将用户信息放入session中
            SecurityUtils.putUserToSession(request, user);
            logger.info("用户{}登录系统,IP:{}.", user.getLoginName(), SpringMVCHolder.getIp());

            //设置调整URL 如果session中包含未被授权的URL 则跳转到该页面
//            String resultUrl = request.getContextPath()+ AppConstants.getAdminPath()  + "/login/index?theme=" + theme;
            String resultUrl = request.getContextPath()+ AppConstants.getAdminPath();
            Object unAuthorityUrl = request.getSession().getAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL);
            if (unAuthorityUrl != null) {
                resultUrl = unAuthorityUrl.toString();
                //清空未被授权的URL
                request.getSession().setAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL, null);
            }
            //返回
            result = new Result(Result.SUCCESS, "用户验证通过!", resultUrl);
            isValidateCodeLogin(loginName, false, true);
        }

        return result;
    }



    /**
     * 用户注销
     * @param request
     * @return
     */
    @RequestMapping(value = {"logout"},method = RequestMethod.POST)
    @ResponseBody
    public Result postlogout(HttpServletRequest request) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            // 退出时清空session中的内容
            String sessionId = SecurityUtils.getNoSuffixSessionId(request.getSession());
            //由监听器更新在线用户列表
            SecurityUtils.removeUserFromSession(sessionId, SecurityType.logout);
            logger.info("用户{}退出系统.", sessionInfo.getLoginName());
        }
        return Result.successResult();
    }


    /**
     * 用户注销
     * @param request
     * @return
     */
    @RequestMapping(value = {"logout"},method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            // 退出时清空session中的内容
            String sessionId = SecurityUtils.getNoSuffixSessionId(request.getSession());
            //由监听器更新在线用户列表
            SecurityUtils.removeUserFromSession(sessionId, SecurityType.logout);
            logger.info("用户{}退出系统.", sessionInfo.getLoginName());
        }
        return "redirect:/";
    }

    @RequestMapping(value = {"index"})
    public String index(String theme) {
        //根据客户端指定的参数跳转至 不同的主题 如果未指定 默认:index
        if (StringUtils.isNotBlank(theme) && (theme.equals("app") || theme.equals("index"))) {
            return "layout/" + theme;
        } else {
            return "layout/index";
        }
    }


    /**
     * 导航菜单.
     */
    @ResponseBody
    @RequestMapping(value = {"navTree"})
    public List<TreeNode> navTree(HttpServletResponse response) {
        WebUtils.setNoCacheHeader(response);
        List<TreeNode> treeNodes = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            treeNodes = resourceService.findNavTreeNodeWithPermissions(sessionInfo.getUserId());
        }
        return treeNodes;
    }

    /**
     * 当前在线用户
     *
     * @throws Exception
     */
    @RequiresUser(required = true)
    @RequestMapping(value = {"onlineDatagrid"})
    @ResponseBody
    public Datagrid<SessionInfo> onlineDatagrid(HttpServletRequest request) throws Exception {
        Page<SessionInfo> page = new Page<>(request);
        page = SecurityUtils.findSessionUserPage(page);
        Datagrid<SessionInfo> dg = new Datagrid<>(page.getTotalCount(),page.getResult());
        return dg;
    }


    /**
     * 桌面版 开始菜单
     */
    @RequestMapping(value = {"startMenu"})
    @ResponseBody
    public List<Menu> startMenu() {
        List<Menu> menus = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        menus = resourceService.findNavMenuWithPermissions(sessionInfo.getUserId());
        return menus;
    }


    /**
     * 桌面版 桌面应用程序列表
     */
    @RequestMapping(value = {"apps"})
    @ResponseBody
    public List<Menu> apps() {
        List<Menu> menus = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            List<Resource> resources = resourceService.findMenuResourcesWithPermissions(sessionInfo.getUserId());
            for (Resource resource : resources) {
                if (resource != null && StringUtils.isNotBlank(resource.getUrl())) {
                    Menu menu = resourceToMenu(resource);
                    menus.add(menu);
                }

            }
        }
        return menus;
    }

    /**
     * 资源转M
     *
     * @param resource  资源
     * @return
     */
    private Menu resourceToMenu(Resource resource) {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        Assert.notNull(resource, "参数resource不能为空");
        String head = this.getHeadFromUrl(request.getRequestURL().toString());
        Menu menu = new Menu(resource.getId(),resource.getName());
        String url = resource.getUrl();
        if (url.startsWith("http")) {
            url =  resource.getUrl();
        } else if (url.startsWith("/")) {
            url = head + request.getContextPath()  + url;
        } else {
            url = head + request.getContextPath() + AppConstants.getAdminPath()+ "/" + url;
        }
        menu.setHref(url);
        return menu;
    }

    /**
     * 根据URL地址获取请求地址前面部分信息
     *
     * @param url
     * @return
     */
    private String getHeadFromUrl(String url) {
        int firSplit = url.indexOf("//");
        String proto = url.substring(0, firSplit + 2);
        int webSplit = url.indexOf("/", firSplit + 2);
        int portIndex = url.indexOf(":", firSplit);
        String webUrl = url.substring(firSplit + 2, webSplit);
        String port = "";
        if (portIndex >= 0) {
            webUrl = webUrl.substring(0, webUrl.indexOf(":"));
            port = url.substring(portIndex + 1, webSplit);
        } else {
            port = "80";
        }
        return proto + webUrl + ":" + port;
    }


    /**
     * 异步方式返回session信息
     */
    @RequestMapping(value = {"sessionInfo"})
    @ResponseBody
    public Result sessionInfo() {
        Result result = Result.successResult();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        result.setObj(sessionInfo);
        if (logger.isDebugEnabled()) {
            logger.debug(result.toString());
        }
        return result;
    }




}
