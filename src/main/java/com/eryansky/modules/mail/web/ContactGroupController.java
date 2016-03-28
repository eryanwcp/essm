/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.mail._enum.ContactGroupType;
import com.eryansky.modules.mail.entity.ContactGroup;
import com.eryansky.modules.mail.entity.MailContact;
import com.eryansky.modules.mail.service.ContactGroupManager;
import com.eryansky.modules.mail.service.MailContactManager;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.utils.SelectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-07
 */
@Controller
@RequestMapping(value = "${adminPath}/mail/contactGroup")
public class ContactGroupController extends BaseController<ContactGroup, String> {

    @Autowired
    private ContactGroupManager contactGroupManager;
    @Autowired
    private MailContactManager mailContactManager;

    @Override
    public EntityManager<ContactGroup, String> getEntityManager() {
        return contactGroupManager;
    }


    @RequestMapping(value = {""})
    public String list() {
        return "modules/mail/contactGroup";
    }

    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model")ContactGroup model) {
        return "modules/mail/contactGroup-input";
    }

    /**
     * 邮件联系人编辑
     */
    @RequestMapping(value = {"inputContactGroupMail"})
    public String inputContactGroupMail(@ModelAttribute("model")MailContact model){
        return "modules/mail/contactGroupMail-input";
    }
    /**
     * 联系人组 保存
     * @param model
     * @return
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    @Override
    public Result save(@ModelAttribute("model")ContactGroup model) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result=Result.errorResult();
        if(contactGroupManager.checkExist(sessionInfo.getUserId(),model.getContactGroupType(),model.getName(), model.getId())==null){
            model.setUserId(sessionInfo.getUserId());
            return super.save(model);
        }else{
            result.setMsg("用户组["+model.getName()+"]已存在");
        }
        return result;
    }

    /**
     * 个人 联系人组树形菜单 查询用
     * @return
     */
    @RequestMapping(value = {"groupTree"})
    @ResponseBody
    public List<TreeNode> groupTree(Integer contactGroupType) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<TreeNode>  treeNodes = Lists.newArrayList();
        TreeNode rootNode = new TreeNode("","全部群组");
        treeNodes.add(rootNode);
        List<ContactGroup> list = contactGroupManager.findUserContactGroups(sessionInfo.getUserId(), contactGroupType);
        for (ContactGroup contactGroup : list) {
            TreeNode treeNode = new TreeNode(contactGroup.getId().toString(), contactGroup.getName());
            if(contactGroupType == null){
                treeNode.addAttributes("contactGroupType", contactGroup.getContactGroupType());
            }
            rootNode.addChild(treeNode);
        }
        return treeNodes;
    }


    /**
     * 个人 联系人组树形菜单
     * @return
     */
    @RequestMapping(value = {"tree"})
    @ResponseBody
    public List<TreeNode> tree(Integer contactGroupType) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<TreeNode>  treeNodes = Lists.newArrayList();
        List<ContactGroup> list = contactGroupManager.findUserContactGroups(sessionInfo.getUserId(), contactGroupType);
        for(ContactGroup contactGroup:list){
            TreeNode treeNode = new TreeNode(contactGroup.getId().toString(),contactGroup.getName());
            treeNodes.add(treeNode);
        }


        return treeNodes;
    }

    /**
     * 联系人组 删除
     * @param ids
     * @return
     */
    @RequestMapping(value = {"remove"})
    @ResponseBody
    @Override
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        contactGroupManager.deleteByIds(ids);
        return Result.successResult();
    }

    /**
     *
     * @param contactGroupId 角色ID
     * @return
     */
    @RequestMapping(value = {"select"})
    public ModelAndView selectPage(String contactGroupId) {
        ModelAndView modelAndView = new ModelAndView("modules/sys/user-select");
        List<User> users = null;
        ContactGroup contactGroup = contactGroupManager.loadById(contactGroupId);
        List<String> excludeUserIds = contactGroup.getObjectIds();
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
     * 添加联系人表单页面
     * @param contactGroupId 联系人组ID
     * @return
     */
    @RequestMapping(value = {"contactGroupUser"})
    @ResponseBody
    public ModelAndView contactGroupUser(String contactGroupId) {
        ModelAndView modelAndView = new ModelAndView("modules/mail/contactGroup-user");
        return modelAndView;
    }

    /**
     * 添加联系人
     * @param addObjectIds 新增联系人 ID集合
     * @return
     */
    @RequestMapping(value = {"addContactGroupUser"})
    @ResponseBody
    public Result addContactGroupUser(@ModelAttribute("model")ContactGroup model,
                                      @RequestParam(value = "addObjectIds", required = false) List<String> addObjectIds){
        List<String> newObjectIds = Collections3.aggregate(model.getObjectIds(), addObjectIds);
        model.setObjectIds(newObjectIds);
        getEntityManager().saveEntity(model);
        return Result.successResult();
    }
    /**
     * 移除联系人
     * @param removeObjectIds 需要移除的联系人 ID集合
     * @return
     */
    @RequestMapping(value = {"removeContactGroupUser"})
    @ResponseBody
    public Result removeContactGroupUser(@ModelAttribute("model")ContactGroup model,
                                         @RequestParam(value = "removeObjectIds", required = true) List<String> removeObjectIds){
        if(model.getId() != null){
            List<String> oldIds = model.getObjectIds();
            List<String> newIds = Collections3.subtract(oldIds, removeObjectIds);
            model.setObjectIds(newIds);
            getEntityManager().saveEntity(model);

        }

        return Result.successResult();
    }

    /**
     * 联系人列表
     * @param id 联系人组ID
     * @param query 用户登录名或姓名
     * @return
     */
    @RequestMapping(value = {"contactGroupUserDatagrid"})
    @ResponseBody
    public String contactGroupUserDatagrid(String id,String query) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String json= "[]";
        if(StringUtils.isNotBlank(id)) {
            Page<User> page = new Page<User>(SpringMVCHolder.getRequest());
            page = contactGroupManager.findContactGroupUsers(page,id, query);
            Datagrid<User> dg = new Datagrid<User>(page.getTotalCount(),page.getResult());
            json = JsonMapper.getInstance().toJson(dg, User.class,
                    new String[]{"id", "loginName", "name", "sexView", "defaultOrganName", "email", "mobile", "tel"});
        }
        return json;
    }

    /**
     * 联系人列表
     * @param contactGroupId 联系人组ID
     * @param loginNameOrName 用户登录名或姓名
     * @return
     */
    @RequestMapping(value = {"selectContactGroupUserDatagrid"})
    @ResponseBody
    public String selectContactGroupUserDatagrid(String contactGroupId,String loginNameOrName) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        ContactGroup contactGroup = getEntityManager().loadById(contactGroupId);
        List<User> users = Lists.newArrayList();
        if(contactGroup != null){
            users = contactGroupManager.findContactGroupUsers(contactGroupId, loginNameOrName);
        }
        String json = JsonMapper.getInstance().toJson(users,User.class,
                new String[]{"id","loginName","name","sexView","organNames"});
        return json;
    }

    /**
     * 日志类型下拉列表.
     */
    @RequestMapping(value = {"contactGroupTypeCombobox"})
    @ResponseBody
    public List<Combobox> contactGroupTypeCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }

        ContactGroupType[] lts = ContactGroupType.values();
        for (int i = 0; i < lts.length; i++) {
            Combobox combobox = new Combobox();
            combobox.setValue(lts[i].getValue().toString());
            combobox.setText(lts[i].getDescription());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort() throws Exception {
        Result result;
        Integer maxSort = contactGroupManager.getMaxSort();
        result = new Result(Result.SUCCESS, null, maxSort);
        logger.debug(result.toString());
        return result;
    }
}