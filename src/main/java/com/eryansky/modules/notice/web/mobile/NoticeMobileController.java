/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.web.mobile;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.sys._enum.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-01 
 */
@Mobile
@Controller
@RequestMapping(value = "${mobilePath}/notice")
public class NoticeMobileController extends SimpleController {

    @Autowired
    private NoticeReceiveInfoService noticeReceiveInfoService;

    @Logging(logType = LogType.access,value = "我的通知")
    @RequestMapping(value = {""})
    public String list() {
        return "modules/notice/notice";
    }


    /**
     *
     * @return
     */
    @RequestMapping("noticePage")
    @ResponseBody
    public String noticePage() {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(SpringMVCHolder.getRequest());
        if (sessionInfo != null) {
            page = noticeReceiveInfoService.findReadNoticePage(page,new NoticeReceiveInfo(), sessionInfo.getUserId(),null);
        }
        Datagrid dg = new Datagrid(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,NoticeReceiveInfo.class,
                new String[]{"id","title","isRead","isTop","title","noticeId","typeView",
                        "isReadView","publishTime"});
        return json;
    }
}