/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.sys.mapper.Dictionary;
import com.eryansky.modules.sys.mapper.DictionaryItem;
import com.eryansky.modules.sys.service.DictionaryItemService;
import com.eryansky.modules.sys.service.DictionaryService;
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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-07 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
//        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-ehcache.xml" })
public class BootstrapIconTest {

    private static Logger logger = LoggerFactory.getLogger(BootstrapIconTest.class);

    @Autowired
    private DictionaryItemService dictionaryItemService;
    @Autowired
    private DictionaryService dictionaryService;

    @Resource(name = "sessionFactory")
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

    @Test
    public void importIcon() throws Exception{
        ArrayList<String> updatefile = this.loadfile();
        System.out.println(JsonMapper.getInstance().toJson(updatefile));
        System.out.println(updatefile.size());

        Dictionary dictionary = dictionaryService.getByCode("cms_bootstrap_icon");
        for(int i=0;i<updatefile.size();i++){
            String icon = updatefile.get(i);
            DictionaryItem dictionaryItem = new DictionaryItem();
            dictionaryItem.setDictionary(dictionary);
            dictionaryItem.setOrderNo(dictionaryItemService.getMaxSort());
            dictionaryItem.setName(icon);
            dictionaryItem.setCode(dictionary.getCode() + String.format("%03d", i));
            dictionaryItem.setValue(icon);
            dictionaryItemService.save(dictionaryItem);
        }
    }

    /**
     * 加载文件
     * @return
     * @throws Exception
     * @author 温春平&wencp wencp@strongit.com.cn
     * @date：    2012-2-17 下午5:22:50
     */
    private ArrayList<String> loadfile() throws Exception{
        InputStream inStream = getClass().getResourceAsStream("/bootstrap-icon.css");
        InputStreamReader isr = new InputStreamReader(inStream);
        BufferedReader bfr = new BufferedReader(isr);
        String line ;
        ArrayList<String> arrayList = new ArrayList<String>();
        while( (line = bfr.readLine()) !=null ){
            if(line.startsWith(".icon-")){
                arrayList.add(line.replace(".","").replaceAll(" \\{",""));
            }
        }
        bfr.close();
        isr.close();
        inStream.close();
        return arrayList;
    }


}
