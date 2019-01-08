/**
 * Copyright &copy; 2012-2018 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.eryansky.modules.weixin.web;

import com.eryansky.fastweixin.company.message.req.QYBaseEvent;
import com.eryansky.fastweixin.company.message.req.QYEnterAgentEvent;
import com.eryansky.fastweixin.company.message.req.QYTextReqMsg;
import com.eryansky.fastweixin.company.message.resp.QYBaseRespMsg;
import com.eryansky.fastweixin.company.message.resp.QYTextRespMsg;
import com.eryansky.fastweixin.servlet.QYWeixinControllerSupport;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.weixin.utils.WeixinConstants;
import com.eryansky.modules.weixin.utils.WeixinUtils;
import com.eryansky.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;


/**
 * 集成微信(企业号)
 */
@RequiresUser(required = false)
@Controller
@RequestMapping(value = "${mobilePath}/qyweixin")
public class QYWeixinController extends QYWeixinControllerSupport {

    private static final Logger log = LoggerFactory.getLogger(QYWeixinController.class);
    private static final String TOKEN = "myToken";


    //设置TOKEN，用于绑定微信服务器
    @Override
    protected String getToken() {
//		return TOKEN;
        return WeixinConstants.getToken();
    }

    @Override
    protected String getCropId() {
        return WeixinConstants.getCropId();
    }


    //使用安全模式时设置：密钥
    //不再强制重写，有加密需要时自行重写该方法
    @Override
    protected String getAESKey() {
        return WeixinConstants.getAESKey();
    }

    @Override
    protected QYBaseRespMsg handleTextMsg(QYTextReqMsg msg) {
        String content = msg.getContent();
        log.info("用户发送到服务器的内容:{}", content);
        String toURL = AppConstants.getAppURL()+"/m/qyweixin/index";
        String url = WeixinUtils.getOauth2URL(toURL);
        log.debug(url);
        QYTextRespMsg qyTextRespMsg = new QYTextRespMsg("欢迎您！").addLink(AppConstants.getAppName(),url);
        return qyTextRespMsg;
    }

    @Override
    protected QYBaseRespMsg handleEnterAgentEvent(QYEnterAgentEvent event) {
        String toURL = AppConstants.getAppURL()+"/m/qyweixin/index";
        String url = WeixinUtils.getOauth2URL(toURL);
        log.debug(url);
        QYTextRespMsg qyTextRespMsg = new QYTextRespMsg("欢迎您！").addLink(AppConstants.getAppName(),url);
        return qyTextRespMsg;
    }

    @Override
    protected QYBaseRespMsg handleSubScribe(QYBaseEvent event) {
        QYTextRespMsg qyTextRespMsg = new QYTextRespMsg("感谢您的关注！");
        return qyTextRespMsg;
    }

    /**
     * 应用首页
     * @param request
     * @return
     */
    @Mobile
    @RequiresUser(required = true)
    @Logging(logType = LogType.access,value = "主页（微信）")
    @RequestMapping("index")
    public ModelAndView index(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("layout/index");
        return  modelAndView;
    }
}