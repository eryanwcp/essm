/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.web;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.mail._enum.ContactGroupType;
import com.eryansky.modules.mail.entity.ContactGroup;
import com.eryansky.modules.mail.entity.MailContact;
import com.eryansky.modules.mail.service.ContactGroupManager;
import com.eryansky.modules.mail.service.MailContactManager;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-19 
 */
@Controller
@RequestMapping(value = "${adminPath}/mail/mailContact")
public class MailContactController extends BaseController<MailContact, String> {
    @Autowired
    private MailContactManager mailContactManager;
    @Autowired
    private ContactGroupManager contactGroupManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private OrganManager organManager;

    @Override
    public EntityManager<MailContact, String> getEntityManager() {
        return mailContactManager;
    }

    @RequestMapping(value = {"input"})
    public ModelAndView input(@ModelAttribute("model")MailContact model,String contactGroupId) {
        ModelAndView modelAndView = new ModelAndView("modules/mail/contactGroupMail-input");
        modelAndView.addObject("contactGroupId",contactGroupId);
        return modelAndView;
    }

    /**
     * 联系人列表
     * @param id 联系人组ID
     * @param query 用户登录名或姓名
     * @return
     */
    @RequestMapping(value = {"contactGroupMailDatagrid"})
    @ResponseBody
    public String contactGroupMailDatagrid(String id,String query) {
        String json = "[]";
        if(StringUtils.isNotBlank(id)) {
            Page<MailContact> page = new Page<MailContact>(SpringMVCHolder.getRequest());// 分页对象
            page = mailContactManager.getMailContactUsers(id, query, page);
            Datagrid<MailContact> dg = new Datagrid<MailContact>(page.getTotalCount(), page.getResult());
            json = JsonMapper.getInstance().toJson(dg);
        }
        return json;
    }

    /**
     * 添加邮件联系人
     * @param model
     * @return
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result save(@ModelAttribute("model")MailContact model,String contactGroupId) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        MailContact mc = mailContactManager.checkExist(contactGroupId, model.getEmail());
        if (mc == null || (StringUtils.isNotBlank(model.getId()) &&
                model.getId().equals(mc.getId()))) {
            model.setUserId(sessionInfo.getUserId());
            getEntityManager().saveEntity(model);
            ContactGroup contactGroup = contactGroupManager.loadById(contactGroupId);
            if(!contactGroup.getObjectIds().contains(model.getId())){
                contactGroup.getObjectIds().add(model.getId());
                contactGroupManager.update(contactGroup);
            }
            result = Result.successResult();
            return result;
        } else {
            result = Result.errorResult().setMsg("该邮箱已存在");
        }
        return result;
    }


    /**
     * 自动添加邮件联系人
     * @param email
     * @return
     */
    @RequestMapping(value = "autoAdd")
    @ResponseBody
    public Result autoAdd(String email) {
        if (StringUtils.isBlank(email) || !SysUtils.checkEmail(email)) {
            return Result.errorResult().setMsg("[" + email + "]邮件格式不正确");
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        MailContact mailContact = mailContactManager.checkUserMailContactExist(sessionInfo.getUserId(),email);
        if(mailContact == null){
            mailContact = new MailContact();
            mailContact.setUserId(sessionInfo.getUserId());
            mailContact.setEmail(email);
            ContactGroup contactGroup = contactGroupManager.saveDefaultMailContactGroupIfNotExist(sessionInfo.getUserId());

            mailContact.setName(StringUtils.substringBefore(email, "@"));
            getEntityManager().save(mailContact);
            if(!contactGroup.getObjectIds().contains(mailContact.getId())){
                contactGroup.getObjectIds().add(mailContact.getId());
                contactGroupManager.update(contactGroup);
            }
        }

        Map<String,String> map = Maps.newHashMap();
        map.put("id",PREFIX_MAILCONTACT+mailContact.getId());
        map.put("name",mailContact.getName());
        map.put("group",mailContact.getName());
        return Result.successResult().setObj(map);
    }



    public static final String PREFIX_USER = "UU_";//用户
    public static final String PREFIX_USER_GROUP = "UG_";//用户组
    public static final String PREFIX_ORGAN = "OG_";//部门
    public static final String PREFIX_MAILCONTACT = "CC_";//邮件联系人
    public static final String PREFIX_CONTACT_GROUP = "CG_";//邮件联系人组

    /**
     * 自动添加邮件联系人
     * @param mailAccountId 邮件账号ID
     * @param query 关键字
     * @param includeIds 包含的ID
     * @return
     */
    @RequestMapping(value = {"multiSelectPrefix"})
    @ResponseBody
    public String multiSelectPrefix(String mailAccountId,String query,
                                    @RequestParam(value = "includeIds", required = false)List<String> includeIds) {
        List<Map<String,String>> list = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<String> includeUserIds = Lists.newArrayList();
        List<String> includeContactIds = Lists.newArrayList();
        if(Collections3.isNotEmpty(includeIds)){
            for(String includeId:includeIds){
                if(StringUtils.startsWith(includeId,MailContactController.PREFIX_USER)){
                    includeUserIds.add(StringUtils.substringAfter(includeId,MailContactController.PREFIX_USER));
                }else if(StringUtils.startsWith(includeId,MailContactController.PREFIX_MAILCONTACT)){
                    includeContactIds.add(StringUtils.substringAfter(includeId,MailContactController.PREFIX_MAILCONTACT));
                }
            }


        }

        //系统用户
        List<User> users = userManager.findWithInclude(includeUserIds,query);
        for(User user:users){
            Map<String,String> map = Maps.newHashMap();
            map.put("id",PREFIX_USER+user.getId());
            map.put("name",user.getName());
            map.put("group","系统用户");
            list.add(map);
        }

        //部门
        List<Organ> organs = organManager.findDepartmensWithInclude(null, query);
        for(Organ organ:organs){
            Map<String,String> map = Maps.newHashMap();
            map.put("id",PREFIX_ORGAN+organ.getId());
            map.put("name",organ.getName());
            map.put("group","部门");
            list.add(map);
        }


        //用户组、邮件联系人组
        List<ContactGroup> contactGroups = contactGroupManager.findUserContactGroupsWithInclude(sessionInfo.getUserId(), query);
        for(ContactGroup contactGroup:contactGroups){
            Map<String,String> map = Maps.newHashMap();
            String prefix = null;
            String group = null;
            boolean addContact = true;
            if(ContactGroupType.System.getValue().equals(contactGroup.getContactGroupType())){
                prefix = PREFIX_USER_GROUP;
                group = "用户组";
            }else if(ContactGroupType.Mail.getValue().equals(contactGroup.getContactGroupType())){
                prefix = PREFIX_CONTACT_GROUP;
                group = "邮件组";
                if(StringUtils.isBlank(mailAccountId)){
                    addContact = false;
                }
            }
            map.put("id", prefix +contactGroup.getId());
            map.put("name",contactGroup.getName());
            if(addContact){
                map.put("group",group);
            }
        }


        //联系人
        if(StringUtils.isNotBlank(mailAccountId)){
            List<MailContact> mailContacts = mailContactManager.findUserMailContactsWithInclude(sessionInfo.getUserId(), includeContactIds, query);
            for(MailContact mailContact:mailContacts){
                Map<String,String> map = Maps.newHashMap();
                map.put("id",PREFIX_MAILCONTACT+mailContact.getId());
                map.put("name",mailContact.getName());
                map.put("group","我的联系人");
                list.add(map);
            }
        }

        return JsonMapper.getInstance().toJson(list);
    }


    /**
     * 自动添加邮件联系人
     * @param mailAccountId 邮件账号ID
     * @return
     */
    @RequestMapping(value = {"multiSelect"})
    @ResponseBody
    public String multiSelect(String mailAccountId) {
        List<Map<String,String>> list = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();

        //系统用户
        List<User> users = userManager.findAllNormal();
        for(User user:users){
            Map<String,String> map = Maps.newHashMap();
            map.put("id",user.getId());
            map.put("name",user.getName());
            map.put("group","系统用户");
            list.add(map);
        }


        //用户组、邮件联系人组
        List<ContactGroup> contactGroups = contactGroupManager.findUserContactGroups(sessionInfo.getUserId(),null);
        for(ContactGroup contactGroup:contactGroups){
            Map<String,String> map = Maps.newHashMap();
            map.put("id", contactGroup.getId());
            map.put("name",contactGroup.getName());
            map.put("group","用户组");
            list.add(map);
        }


        //联系人
        if(StringUtils.isNotBlank(mailAccountId)){
            List<MailContact> mailContacts = mailContactManager.findUserMailContacts(sessionInfo.getUserId());
            for(MailContact mailContact:mailContacts){
                Map<String,String> map = Maps.newHashMap();
                map.put("id",mailContact.getId());
                map.put("name",mailContact.getName());
                map.put("group","我的联系人");
                list.add(map);
            }
        }

        return JsonMapper.getInstance().toJson(list);
    }
}