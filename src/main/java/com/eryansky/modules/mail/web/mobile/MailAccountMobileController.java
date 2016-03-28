/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.web.mobile;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.utils.SelectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-17 
 */
@Mobile
@Controller
@RequestMapping(value = "${mobilePath}/mail/mailAccount")
public class MailAccountMobileController extends BaseController<MailAccount, String> {
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

    @RequestMapping(value = "mailAccount_input")
    public ModelAndView accountinput(){
        return new ModelAndView("modules/mail/mailAccount-input");
    }

    /**
     * 个人 联系人组树形菜单 查询用
     * @return
     */
    @RequestMapping(value = {"userMailAccounts"})
    @ResponseBody
    public String userMailAccounts() {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String json = null;
        Integer activate = null;
        List<MailAccount> list = mailAccountManager.findUserMailAcoounts(sessionInfo.getUserId(), activate);
        Datagrid<MailAccount> dg = new Datagrid<MailAccount>(list.size(), list);
        json = JsonMapper.getInstance().toJson(dg);
        return json;
    }

    @RequestMapping(value = {"input"})
    public String input(@ModelAttribute("model")MailAccount model) {
        return "modules/mail/mailAccount-input";
    }
}