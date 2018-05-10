/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Post;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.*;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 岗位管理 Controller
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-09 14:07
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/post")
public class PostController extends SimpleController{

    @Autowired
    private PostService postService;
    @Autowired
    private OrganService organService;
    @Autowired
    private UserService userService;

    @ModelAttribute("model")
    public Post get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return postService.get(id);
        }else{
            return new Post();
        }
    }

    @Logging(value = "岗位管理",logType = LogType.access)
    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/post";
    }

    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public String datagrid(String organId,String query,HttpServletRequest request) {
        Page<Post> page = new Page<Post>(request);
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Post model = new Post();
        model.setOrganId(organId);
        model.setQuery(query);
        if(StringUtils.isBlank(model.getOrganId())){
            model.setOrganId(sessionInfo.getLoginOrganId());
        }

        page = postService.findPage(page,model);
        Datagrid<Post> dg = new Datagrid<Post>(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJson(dg);
        return json;
    }

    /**
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public ModelAndView input(@ModelAttribute("model") Post model) throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/sys/post-input");
        modelAndView.addObject("model",model);
        List<String> organIds = organService.findAssociationOrganIdsByPostId(model.getId());
        modelAndView.addObject("organIds",organIds);
        return modelAndView;
    }

    @Logging(value = "岗位管理-保存岗位",logType = LogType.access)
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model")Post model,String organId) {
        Result result;
        Validate.notNull(organId, "参数[organId]不能为null");
        // 编码重复校验
        if (StringUtils.isNotBlank(model.getCode())) {
            Post checkPost = postService.getPostByOC(organId, model.getCode());
            if (checkPost != null && !checkPost.getId().equals(model.getId())) {
                result = new Result(Result.WARN, "编码为[" + model.getCode() + "]已存在,请修正!", "code");
                logger.debug(result.toString());
                return result;
            }
        }

        postService.savePost(model);
        result = Result.successResult();
        return result;
    }

    /**
     * 根据ID集合批量删除.
     *
     * @param ids 主键ID集合
     * @return
     */
    @Logging(value = "岗位管理-删除岗位",logType = LogType.access)
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        postService.deleteByIds(ids);
        return Result.successResult();
    }




    /**
     * 设置岗位用户页面.
     */
    @RequestMapping(value = {"user"})
    public String user(@ModelAttribute("model")Post model, Model uiModel) throws Exception {
        uiModel.addAttribute("organId", model.getOrganId());
        List<String> userIds = userService.findUserIdsByPostIdAndOrganId(model.getId(),model.getOrganId());
        uiModel.addAttribute("userIds", userIds);
        return "modules/sys/post-user";
    }

    /**
     * 修改岗位用户.
     */
    @Logging(value = "岗位管理-岗位用户",logType = LogType.access)
    @RequestMapping(value = {"updatePostUser"})
    @ResponseBody
    public Result updatePostUser(@ModelAttribute("model") Post model,
                                 @RequestParam(value = "userIds", required = false)Set<String> userIds) throws Exception {
        postService.savePostOrganUsers(model.getId(),model.getOrganId(),userIds);
        return Result.successResult();
    }


    /**
     * 岗位所在部门下的人员信息
     * @param postId
     * @return
     */
    @RequestMapping(value = {"postOrganUsers/{postId}"})
    @ResponseBody
    public Datagrid<User> postOrganUsers(@PathVariable String postId) {
        List<User> users = userService.findUsersByPostId(postId);
        Datagrid<User> dg;
        if(Collections3.isEmpty(users)){
           dg = new Datagrid(0, Lists.newArrayList());
        }else{
           dg = new Datagrid<User>(users.size(), users);
        }
        return dg;
    }

    /**
     * 用户可选岗位列表 TODO
     * @param selectType {@link SelectType}
     * @param userId 用户ID
     * @return
     */
    @RequestMapping(value = {"combobox"})
    @ResponseBody
    public List<Combobox> combobox(String selectType,String userId,String organId){
        List<Post> list = postService.findPostsByOrganId(organId);
        List<Combobox> cList = Lists.newArrayList();

        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        for (Post r : list) {
            Combobox combobox = new Combobox(r.getId() + "", r.getName());
            cList.add(combobox);
        }
        return cList;
    }
}
