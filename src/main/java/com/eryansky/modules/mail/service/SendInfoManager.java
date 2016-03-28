/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.modules.mail.entity.SendInfo;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-18 
 */
@Service
public class SendInfoManager extends EntityManager<SendInfo, String> {

    private HibernateDao<SendInfo, String> senderInfoDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        senderInfoDao = new HibernateDao<SendInfo, String>(sessionFactory, SendInfo.class);
    }

    @Override
    protected HibernateDao<SendInfo, String> getEntityDao() {
        return senderInfoDao;
    }


    /**
     * 查找发件箱发件配置信息
     * @param outboxId
     * @return
     */
    public List<SendInfo> findOutboxSendInfos(String outboxId,Integer receiveType){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(outboxId);
        hql.append("from SendInfo s where s.outboxId = :p1");
        if(receiveType != null){
            hql.append(" and s.receiveType =  :receiveType");
            parameter.put("receiveType",receiveType);
        }
        List<SendInfo> list = getEntityDao().find(hql.toString(), parameter);
        return  list;
    }


    /**
     * 查找发件箱发件配置信息
     * @param outboxId
     * @return
     */
    public List<String> findOutboxSendInfoReceiveObjectIds(String outboxId,Integer receiveType){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(outboxId);
        hql.append("select s.receiveObjectId from SendInfo s where s.outboxId = :p1");
        if(receiveType != null){
            hql.append(" and s.receiveType =  :receiveType");
            parameter.put("receiveType",receiveType);
        }
        List<String> list = getEntityDao().find(hql.toString(), parameter);
        return  list;
    }

    /**
     * 查找站内邮件 发件配置信息
     * @param emailId 邮件ID
     * @param receiveType 接受类型 {@link com.eryansky.modules.mail._enum.ReceiveType}
     * @return
     */
    public List<SendInfo> findMailSendInfos(String emailId,Integer receiveType){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(emailId);
        hql.append("select s from SendInfo s where s.outboxId in (select o.id from Outbox o where o.emailId = :p1)");
        if(receiveType != null){
            hql.append(" and s.receiveType =  :receiveType");
            parameter.put("receiveType",receiveType);
        }
        List<SendInfo> list = getEntityDao().find(hql.toString(), parameter);
        return  list;
    }


    /**
     * 物理删除
     * @param emailId 邮件ID
     * @return
     */
    public int deleteByEmailId(String emailId){
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from SendInfo s where s.emailId = :p1");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }

    /**
     * 物理删除
     * @param emailIds 邮件ID
     * @return
     */
    public int deleteByEmailIds(List<String> emailIds){
        Parameter parameter = new Parameter(emailIds);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from SendInfo s where s.emailId in (:p1)");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }
}
