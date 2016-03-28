/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
        "classpath:applicationContext-task.xml",
        "classpath:applicationContext-ehcache.xml" })
public class WeixinTest {

    private static Logger logger = LoggerFactory.getLogger(WeixinTest.class);

    @Autowired
    private QYAPIConfig qyapiConfig;

    @Resource(name = "defaultSessionFactory")
    private SessionFactory sessionFactory;

    @After
    public void close() {
        SessionHolder holder = (SessionHolder) TransactionSynchronizationManager
                .getResource(sessionFactory);
        SessionFactoryUtils.closeSession(holder.getSession());
        TransactionSynchronizationManager.unbindResource(sessionFactory);
    }

    @Before
    public void init() {
        Session s = sessionFactory.openSession();
        TransactionSynchronizationManager.bindResource(sessionFactory,
                new SessionHolder(s));
    }

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
//        https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx9af4b4425e654a20&redirect_uri=http://www.jfit.com.cn/labor_dev/m/qyweixin/index&response_type=code&scope=snsapi_base&state=1#wechat_redirect
        noitceMenu3.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx9af4b4425e654a20&redirect_uri=http://www.jfit.com.cn/labor_dev/m/qyweixin/index&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
//        noitceMenu3.setUrl("http://www.jfit.com.cn/labor_dev/m/qyweixin/index");
        noitceMenus.add(noitceMenu3);

        noitceMenu.setSubButton(noitceMenus);


        QYMenuButton libMenu = new QYMenuButton();
        libMenu.setName("我的图书");
        libMenu.setKey("menu_lib");
        libMenu.setType(QYMenuType.CLICK);
        List<QYMenuButton> libMenus = Lists.newArrayList();

        QYMenuButton libMenu1 = new QYMenuButton();
        libMenu1.setType(QYMenuType.SCANCODE_PUSH);
        libMenu1.setName("扫一扫");
        libMenu1.setKey("menu_libMenu1");
        libMenus.add(libMenu1);

        QYMenuButton libMenu3 = new QYMenuButton();
        libMenu3.setType(QYMenuType.VIEW);
        String toURL = "http://www.jfit.com.cn/labor_lyr/m/qyweixin/labor/qr";
        libMenu3.setName("扫一扫（跳转）");
        libMenu3.setKey("menu_libMenu3");
        libMenu3.setUrl(WeixinUtils.getOauth2URL(toURL));
        libMenus.add(libMenu3);

        QYMenuButton libMenu2 = new QYMenuButton();
        libMenu2.setType(QYMenuType.CLICK);
        libMenu2.setName("我的图书");
        libMenu2.setKey("menu_libMenu2");
        libMenus.add(libMenu2);

        QYMenuButton libMenu4 = new QYMenuButton();
        libMenu4.setType(QYMenuType.VIEW);
        String toURL4 = "http://www.jfit.com.cn/labor_lyr/m/libary";
        libMenu4.setName("图书搜索");
        libMenu4.setKey("menu_libMenu4");
        libMenu4.setUrl(WeixinUtils.getOauth2URL(toURL4));
        libMenus.add(libMenu4);

        libMenu.setSubButton(libMenus);


        QYMenuButton countMenu = new QYMenuButton();
        countMenu.setName("积分");
        countMenu.setKey("menu_countMenu");
        countMenu.setType(QYMenuType.CLICK);
        List<QYMenuButton> countMenus = Lists.newArrayList();


        QYMenuButton countMenu1 = new QYMenuButton();
        countMenu1.setType(QYMenuType.CLICK);
        countMenu1.setName("积分查询");
        countMenu1.setKey("menu_countMenu1");
        countMenus.add(countMenu1);

        QYMenuButton countMenu2 = new QYMenuButton();
        countMenu2.setType(QYMenuType.CLICK);
        countMenu2.setName("积分兑换");
        countMenu2.setKey("menu_countMenu1");
        countMenus.add(countMenu2);

        countMenu.setSubButton(countMenus);

        btns.add(noitceMenu);
        btns.add(libMenu);
        btns.add(countMenu);
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
