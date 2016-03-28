package com.eryansky.modules.mail.web.mobile;

import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.DiskManager;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail.entity.Email;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.entity.Outbox;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.InboxManager;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.modules.mail.service.OutboxManager;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.mail.web.EmailController;
import com.eryansky.modules.sys._enum.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by liyiru on 2015-8-17.
 */
@Mobile
@Controller
@RequestMapping(value = "${mobilePath}/mail")
public class  EmailMobileController extends SimpleController {

    @Autowired
    private MailAccountManager mailAccountManager;
    @Autowired
    private EmailManager emailManager;
    @Autowired
    private DiskManager diskManager;
    @Autowired
    private InboxManager inboxManager;
    @Autowired
    private OutboxManager outboxManager;

    @Logging(logType = LogType.access,value = "企业邮件")
    @RequestMapping(value = "")
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("modules/mail/email");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<MailAccount> mailAccounts = mailAccountManager.findUserMailAcoounts(sessionInfo.getUserId(), AccountActivite.ACTIVITE.getValue());
        modelAndView.addObject("mailAccounts", mailAccounts);
        return modelAndView;
    }

    @Logging(logType = LogType.access,value = "查看邮件[{0}]")
    @RequestMapping(value = { "view/{id}" })
    public ModelAndView view(@PathVariable String id,String mailAccountId,EmailController.BoxType boxType) {
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-view");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Email model = emailManager.loadById(id);
        List<File> files = null; // 挂接的附件

        if (Collections3.isNotEmpty(model.getFileIds())) {
            files = diskManager.findFilesByIds(model.getFileIds());
        }
        String userId = sessionInfo.getUserId();
        EmailUtils.setUserEmailRead(userId, id);

        Outbox outbox = outboxManager.getOutboxByEmailId(id);
        modelAndView.addObject("outbox", outbox);

        modelAndView.addObject("files", files);
        modelAndView.addObject("model", model);
        modelAndView.addObject("mailAccountId",mailAccountId);
        modelAndView.addObject("boxType",boxType);
        return modelAndView;
    }

    @RequestMapping(value = "input")
     public ModelAndView input(){
        return new ModelAndView("modules/mail/email-input");
    }
}
