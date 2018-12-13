/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.weixin;

import com.eryansky.fastweixin.company.api.QYMenuAPI;
import com.eryansky.fastweixin.company.api.QYMessageAPI;
import com.eryansky.fastweixin.company.api.QYUserAPI;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.company.api.entity.QYMenu;
import com.eryansky.fastweixin.company.api.entity.QYMenuButton;
import com.eryansky.fastweixin.company.api.enums.QYMenuType;
import com.eryansky.fastweixin.company.api.enums.QYResultType;
import com.eryansky.fastweixin.company.api.response.GetQYSendMessageResponse;
import com.eryansky.fastweixin.company.api.response.GetQYUserInfoResponse;
import com.eryansky.fastweixin.company.message.QYTextMsg;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;
import com.eryansky.modules.weixin.utils.WeixinUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-07 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-j2cache.xml" })
public class WeixinTest {

    private static Logger logger = LoggerFactory.getLogger(WeixinTest.class);

    @Autowired
    private QYAPIConfig qyapiConfig;

    public static void main(String[] args) {
        WeixinTest weixinTest = new WeixinTest();
        weixinTest.weixinQYMenu();
    }
    @Test
    public void weixinQYMenu() {
        QYMenu menu = new QYMenu();
        List<QYMenuButton> btns = Lists.newArrayList();

        QYMenuButton noitceMenu = new QYMenuButton();
        noitceMenu.setName("互动");
        noitceMenu.setKey("menu_hd");
        noitceMenu.setType(QYMenuType.CLICK);

        List<QYMenuButton> noitceMenus = Lists.newArrayList();
        QYMenuButton noitceMenu1 = new QYMenuButton();
        noitceMenu1.setName("最新通知");
        noitceMenu1.setKey("menu_notice");
        noitceMenu1.setType(QYMenuType.CLICK);
        noitceMenus.add(noitceMenu1);

        QYMenuButton noitceMenu2 = new QYMenuButton();
        noitceMenu2.setName("我的消息");
        noitceMenu2.setKey("menu_message");
        noitceMenu2.setType(QYMenuType.CLICK);
        noitceMenus.add(noitceMenu2);

        QYMenuButton noitceMenu3 = new QYMenuButton();
        noitceMenu3.setName("主页");
        noitceMenu3.setKey("menu_main");
        noitceMenu3.setType(QYMenuType.VIEW);
        String url = "http://www.jfit.com.cn/labor_dev/m/qyweixin/index";
        noitceMenu3.setUrl(WeixinUtils.getOauth2URL(url));
        noitceMenus.add(noitceMenu3);

        noitceMenu.setSubButton(noitceMenus);


        btns.add(noitceMenu);
        menu.setButton(btns);
        QYMenuAPI menuAPI = new QYMenuAPI(qyapiConfig);
        QYResultType resultType = menuAPI.create(menu, "7");
        System.out.println(resultType.toString());
    }

    @Test
    public void user() {
        QYUserAPI qyUserAPI = new QYUserAPI(qyapiConfig);
        GetQYUserInfoResponse qyUserInfoResponse = qyUserAPI.get("superadmin");
        System.out.println(qyUserInfoResponse);
    }

    @Test
    public void message() {
        QYMessageAPI qyMessageAPI = new QYMessageAPI(qyapiConfig);
        QYTextMsg qyTextMsg = new QYTextMsg();
        qyTextMsg.setConetnt("123");
        qyTextMsg.setAgentId("7");
        qyTextMsg.setToUser("superadmin");
        GetQYSendMessageResponse qySendMessageResponse = qyMessageAPI.send(qyTextMsg);
        System.out.println(JsonMapper.getInstance().toJson(qySendMessageResponse));
    }

}
