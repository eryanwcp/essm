/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.web;

import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.utils.SelectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-17 
 */
@Mobile
@Controller
@RequestMapping(value = "${adminPath}/mail/mailAccount")
public class MailAccountController extends BaseController<MailAccount, String> {
    @Autowired
    private MailAccountManager mailAccountManager;

    @Override
    public EntityManager<MailAccount, String> getEntityManager() {
        return mailAccountManager;
    }

    @RequestMapping(value = {""})
    public String list() {
        return "modules/mail/mailAccount";
    }

    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model")MailAccount model) {
        return "modules/mail/mailAccount-input";
    }

    @RequestMapping(value = {"data"})
    public MailAccount data(@ModelAttribute("model")MailAccount model) {
        return model;
    }

    /**
     * 联系人组 保存
     * @param model
     * @param type
     * @return
     */
    @RequestMapping(value = {"_save"})
    @ResponseBody
    public Result _save(@ModelAttribute("model")MailAccount model,String type) {
        Result result=null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String userId=sessionInfo.getUserId();
        if(mailAccountManager.checkExistByMail(userId, model.getMailAddress(), model.getId())!=null){
            result=Result.errorResult().setMsg("该邮箱已存在");
        }else{
            model.setUserId(userId);
            model.setSenderContentType(ServerConfig.ContentType.HTML.toString());
            model.setSenderProtocol(ServerConfig.Protocol.SMTP.toString());
            if(StringUtils.isBlank(model.getReceiverEncryptionType())){
                model.setReceiverEncryptionType(ServerConfig.EncryptionType.NONE.toString());
            }
            if(StringUtils.isBlank(model.getSenderEncryptionType())){
                model.setSenderEncryptionType(ServerConfig.EncryptionType.NONE.toString());
            }

            if(StringUtils.isBlank(model.getName())){
                model.setName(StringUtils.substringAfter(model.getUsername(),"@"));
            }
            model.setIsReceiverNeedAuth(false);
            model.setIsSenderNeedAuth(false);
            if(StringUtils.isNotBlank(type)){
                if(mailAccountManager.connect(model)){
                    result=Result.successResult().setMsg("连接成功！");
                }else{
                    result=Result.successResult().setMsg("连接失败！");
                }
            }else{
                mailAccountManager.saveEntity(model);
                result=Result.successResult();
            }
        }
        return result;
    }

    /**
     * 个人 联系人组树形菜单 查询用
     * @return
     */
    @RequestMapping(value = {"mailAccountDatagrid"})
    @ResponseBody
    public String mailAccountDatagrid() {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String json=null;
        Page<MailAccount> page = new Page<MailAccount>(SpringMVCHolder.getRequest());// 分页对象
        page = mailAccountManager.findPageUserMailAcoounts(sessionInfo.getUserId(), page);
        Datagrid<MailAccount> dg = new Datagrid<MailAccount>(page.getTotalCount(), page.getResult());
        json = JsonMapper.getInstance().toJson(dg);
        return json;
    }

    /**
     * 移除
     * @param ids 需要移除的 ID集合
     * @return
     */
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = true) List<String> ids){
        getEntityManager().deleteByIds(ids);
        return Result.successResult();
    }

    @RequestMapping(value = {"combobox"})
    @ResponseBody
    public List<Combobox> combobox(String selectType){
        List<MailAccount> list = mailAccountManager.getAllNormal();
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if (titleCombobox != null) {
            cList.add(titleCombobox);
        }
        for (MailAccount e : list) {
            Combobox combobox = new Combobox(e.getId(),e.getName());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 检查是否连通
     * @param id
     * @return
     */
    @RequestMapping(value = {"test"})
    @ResponseBody
    public Result test(String id){
        Result result = null;
        MailAccount mailAccount = mailAccountManager.getById(id);
        if (mailAccountManager.connect(mailAccount)) {
            result = Result.successResult().setMsg("连接成功！");
        } else {
            result = Result.successResult().setMsg("连接失败！");
        }
        return result;
    }

    /**
     * 联系人组 测试
     * @param model
     * @return
     */
    @RequestMapping(value = {"testForm"})
    @ResponseBody
    public Result testForm(@ModelAttribute("model")MailAccount model) {
        Result result=null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String userId=sessionInfo.getUserId();
        model.setUserId(userId);
        if(StringUtils.isBlank(model.getReceiverEncryptionType())){
            model.setReceiverEncryptionType(ServerConfig.EncryptionType.NONE.toString());
        }
        if(StringUtils.isBlank(model.getSenderEncryptionType())){
            model.setSenderEncryptionType(ServerConfig.EncryptionType.NONE.toString());
        }


        model.setSenderContentType(ServerConfig.ContentType.HTML.toString());
        model.setSenderProtocol(ServerConfig.Protocol.SMTP.toString());
        model.setIsReceiverNeedAuth(false);
        model.setIsSenderNeedAuth(false);
        if(mailAccountManager.connect(model)){
            result=Result.successResult().setMsg("连接成功！");
            result.setCode(1);
        }else{
            result=Result.successResult().setMsg("连接失败！");
            result.setCode(2);
        }
        return result;
    }

}