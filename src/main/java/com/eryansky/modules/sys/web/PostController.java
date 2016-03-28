/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.modules.sys.entity.Post;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.PostManager;
import com.eryansky.modules.sys.service.UserManager;
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

/**
 * 岗位管理 Controller
 * @author : 温春平 wencp@jx.tobacco.gov.cn
 * @date : 2014-06-09 14:07
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/post")
public class PostController
        extends BaseController<Post,String> {

    @Autowired
    private PostManager postManager;
    @Autowired
    private OrganManager organManager;
    @Autowired
    private UserManager userManager;

    @Override
    public EntityManager<Post, String> getEntityManager() {
        return postManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/post";
    }

    @RequestMapping(value = {"_datagrid"})
    @ResponseBody
    public String datagrid(HttpServletRequest request,String organId,String nameOrCode) {
        Page<Post> page = new Page<Post>(SpringMVCHolder.getRequest());
        page = postManager.findPage(page,organId,nameOrCode);
        Datagrid<Post> dg = new Datagrid<Post>(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJsonWithExcludeProperties(dg,Post.class,
                new String[]{"userNames","organIdsNames"});
        return json;
    }

    /**
     * @param post
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"input"})
    public ModelAndView input(@ModelAttribute("model") Post post) throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/sys/post-input");
        return modelAndView;
    }

    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model")Post model,String organId) {
        Result result;

        Validate.notNull(organId, "参数[organId]不能为null");
        // 名称重复校验
        Post nameCheckPost = postManager.getPostByON(organId,model.getName());
        if (nameCheckPost != null && !nameCheckPost.getId().equals(model.getId())) {
            result = new Result(Result.WARN, "名称为[" + model.getName() + "]已存在,请修正!", "name");
            logger.debug(result.toString());
            return result;
        }

        // 编码重复校验
        if (StringUtils.isNotBlank(model.getCode())) {
            Post checkPost = postManager.getPostByOC(organId, model.getCode());
            if (checkPost != null && !checkPost.getId().equals(model.getId())) {
                result = new Result(Result.WARN, "编码为[" + model.getCode() + "]已存在,请修正!", "code");
                logger.debug(result.toString());
                return result;
            }
        }

        getEntityManager().saveEntity(model);
        result = Result.successResult();
        return result;
    }



    /**
     * 设置岗位用户页面.
     */
    @RequestMapping(value = {"user"})
    public String user(@ModelAttribute("model")Post post,Model model) throws Exception {
        model.addAttribute("organId", post.getOrganId());
        return "modules/sys/post-user";
    }

    /**
     * 修改岗位用户.
     */
    @RequestMapping(value = {"updatePostUser"})
    @ResponseBody
    public Result updatePostUser(@ModelAttribute("model") Post model,
                                 @RequestParam(value = "userIds", required = false)List<String> userIds) throws Exception {
        Result result;
        List<User> us = Lists.newArrayList();
        if(Collections3.isNotEmpty(userIds)){
            for (String id : userIds) {
                User user = userManager.loadById(id);
                us.add(user);
            }
        }

        model.setUsers(us);
        getEntityManager().saveEntity(model);
        result = Result.successResult();
        return result;
    }


    /**
     * 岗位所在部门下的人员信息
     * @param postId
     * @return
     */
    @RequestMapping(value = {"postOrganUsers/{postId}"})
    @ResponseBody
    public Datagrid<User> postOrganUsers(@PathVariable String postId) {
        List<User> users = postManager.getPostOrganUsersByPostId(postId);
        Datagrid<User> dg;
        if(Collections3.isEmpty(users)){
           dg = new Datagrid(0, Lists.newArrayList());
        }else{
           dg = new Datagrid<User>(users.size(), users);
        }
        return dg;
    }

    /**
     * 用户可选岗位列表
     * @param selectType {@link SelectType}
     * @param userId 用户ID
     * @return
     */
    @RequestMapping(value = {"combobox"})
    @ResponseBody
    public List<Combobox> combobox(String selectType,String userId){
        List<Post> list = postManager.getSelectablePostsByUserId(userId);
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
