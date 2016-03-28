/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.eryansky;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;
import com.eryansky.modules.mail.task.MailAsyncTaskService;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.Role;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.RoleManager;
import com.eryansky.modules.sys.service.UserManager;
import org.apache.commons.collections.ListUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
public class HibernateTest {

    private static Logger logger = LoggerFactory.getLogger(HibernateTest.class);

    @Autowired
    private OrganManager organManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private MailAsyncTaskService mailTaskService;

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

    /**
     * 批量生成机构编码
     */
    @Test
    public void setOrganCode() {
        Organ organ = organManager.getById("200");
        rOrgan(organ, "", 0);
    }

    //递归
    private void rOrgan(Organ organ, String prefix, int index) {
        String code = prefix + String.format("%02d", index);
        organ.setCode(code);
        organ.setSysCode(code);
        organManager.update(organ);
        List<Organ> childOrgans = organManager.findByParent(organ.getId(), StatusState.NORMAL.getValue());
        index = 0;
        for (Organ childOrgan : childOrgans) {
            prefix = organ.getSysCode();
            rOrgan(childOrgan, prefix, index++);
        }

        System.out.println(organ.getSysCode());
    }

    @Test
    public void genOrgan() {
        Organ rootOrgan = organManager.loadById("1");
        for(int i=0;i<20;i++){
            Organ organ = new Organ();
            organ.setParent(rootOrgan);
            String code = "00" + String.format("%02d", i);
            organ.setName("第一层机构_"+i);
            organ.setSysCode(code);
            organ.setManagerUserId("1");
            organ.setOrderNo(1);
            organManager.save(organ);
            for(int j=0;j<100;j++){
                Organ organ_2 = new Organ();
                organ_2.setParent(organ);
                String organ_2_code = "00" + String.format("%02d", j);
                organ_2.setName("第二层机构_"+j);
                organ_2.setSysCode(organ_2_code);
                organ_2.setManagerUserId("1");
                organManager.save(organ_2);
            }

        }
    }


    @Test
    public void parentIds() {
        List<Organ> rootOrgans = organManager.findRoots();
        update(rootOrgans);
    }

    private void update(List<Organ> organs){
        for(Organ organ:organs){
            try {
                organManager.update(organ);
            } catch (Exception e) {
                e.printStackTrace();
            }
            update(organ.getSubOrgans());
        }
    }

    @Test
    public void organTree() {
        Date d1 = Calendar.getInstance().getTime();
        List<TreeNode> treeNodes = null;
        List<TreeNode> titleList = Lists.newArrayList();
        String userId = "1";

        treeNodes = organManager.findOrganTree(userId,true);
        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        System.out.println(JsonMapper.getInstance().toJson(unionList));

        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }

    @Test
    public void resource() {
        Date d1 = Calendar.getInstance().getTime();
        List<com.eryansky.modules.sys.entity.Resource> list = resourceManager.findAppAndMenuResourcesByUserId("ac9c62d1646942348eac686a7d41b0dc");
        System.out.println(list.size());
        for(com.eryansky.modules.sys.entity.Resource r:list){
            System.out.println(JsonMapper.getInstance().toJson(r));
        }

        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }

    @Test
    public void roleuserPage() {
        Date d1 = Calendar.getInstance().getTime();
        Page<User> page = new Page<User>(1,20);
        page = roleManager.findPageRoleUsers(page,"a47a816aa7e84dc7a8fcc92e93589bcc",null);
        System.out.println(page.getTotalCount());
        for(User user:page.getResult()){
            System.out.println(user.getName());
        }
        Date d2 = Calendar.getInstance().getTime();
//        System.out.println(d2.getTime() - d1.getTime());
    }

    @Test
    public void removeRoleUser() {
        Date d1 = Calendar.getInstance().getTime();
        String roleId = "a47a816aa7e84dc7a8fcc92e93589bcc";
        List<String> roleUserIds = Lists.newArrayList();
        roleUserIds.add("7063ac9f3b044a34bf860f2f479903b8");
        Role role = roleManager.loadById(roleId);
        List<User> roleUsers = roleManager.findRoleUsers(roleId);
        Iterator<User> iterator = roleUsers.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            for(String userId:roleUserIds){
                if(userId.equals(user.getId())){
                    iterator.remove();
                    break;
                }
            }
        }
        role.setUsers(roleUsers);
        roleManager.update(role);
        Date d2 = Calendar.getInstance().getTime();
//        System.out.println(d2.getTime() - d1.getTime());
    }


    @Test
    public void mail(){
        try {
//            mailTaskService.addUserMailMonitor("1");
            EmailUtils.syncToInbox("1","adbaea8f99e840b0a9a0622b221a033a");
            Thread.sleep(10*60*1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void organs(){
        try {
            List<String> list = organManager.findChildsDepartmentOrganIds("113600");
            System.out.println(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
