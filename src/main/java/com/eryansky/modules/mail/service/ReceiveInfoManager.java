/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.modules.mail.entity.ReceiveInfo;
import org.apache.commons.lang3.Validate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-13 
 */
@Service
public class ReceiveInfoManager extends EntityManager<ReceiveInfo, String> {

    private HibernateDao<ReceiveInfo, String> receiveInfoDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        receiveInfoDao = new HibernateDao<ReceiveInfo, String>(sessionFactory, ReceiveInfo.class);
    }

    @Override
    protected HibernateDao<ReceiveInfo, String> getEntityDao() {
        return receiveInfoDao;
    }


    public ReceiveInfo checkExist(String emailId,String receiveObjectId, Integer sendType){
        Validate.notBlank(emailId, "参数[emailId]不能为空或null");
        Validate.notBlank(receiveObjectId, "参数[receiveObjectId]不能为空或null");
        Parameter parameter = new Parameter(emailId,receiveObjectId,sendType);
        StringBuffer hql = new StringBuffer();
        hql.append("from ReceiveInfo e where e.emailId = :p1 and e.receiveObjectId = :p2 and e.receiveType = :p3");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<ReceiveInfo> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     *
     * @param emailId
     * @param receiveType
     * @return
     */
    public List<ReceiveInfo> findReceiveInfos(String emailId, Integer receiveType){
        Validate.notBlank(emailId, "参数[emailId]不能为空或null");
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("from ReceiveInfo e where  e.emailId = :p1 ");
        if(receiveType != null){
            hql.append(" and e.receiveType = :receiveType");
            parameter.put("receiveType", receiveType);
        }
        List<ReceiveInfo> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }


    /**
     *  删除某个邮件的 接收记录
     * @param emailId
     * @param receiveType
     * @return
     */
    public int deleteReceiveInfos(String emailId, Integer receiveType){
        Validate.notBlank(emailId, "参数[emailId]不能为空或null");
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from ReceiveInfo e where  e.emailId = :p1 ");
        if(receiveType != null){
            hql.append(" and e.receiveType = :receiveType");
            parameter.put("receiveType", receiveType);
        }
        int result = getEntityDao().createQuery(hql.toString(), parameter).executeUpdate();
        return result;
    }

    /**
     *
     * @param emailId
     * @param receiveType
     * @return
     */
    public List<String> findReceiveObjectIds(String emailId, Integer receiveType){
        Validate.notBlank(emailId,"参数[emailId]不能为空或null");
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("select e.receiveObjectId from ReceiveInfo e where  e.emailId = :p1 ");
        if(receiveType != null){
            hql.append("and e.receiveType = :receiveType");
            parameter.put("receiveType", receiveType);
        }
        List<String> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }


    public ReceiveInfo checkExistReceiveObjectId(String emailId, String receiveObjectId, Integer receiveType){
        Validate.notBlank(emailId,"参数[emailId]不能为空或null");
        Validate.notBlank(receiveObjectId,"参数[receiveObjectId]不能为空或null");
        Parameter parameter = new Parameter(emailId,receiveObjectId);
        StringBuffer hql = new StringBuffer();
        hql.append("select e from ReceiveInfo e where  e.emailId = :p1 and e.receiveObjectId = :p2 ");
        if(receiveType != null){
            hql.append("and e.receiveType = :receiveType");
            parameter.put("receiveType", receiveType);
        }
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<ReceiveInfo> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     * 物理删除
     * @param emailId 邮件ID
     * @return
     */
    public int deleteByEmailId(String emailId){
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from ReceiveInfo r where r.emailId = :p1");
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
        hql.append("delete from ReceiveInfo r where r.emailId in (:p1)");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }

}
