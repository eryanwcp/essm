/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.mail.entity.MailContact;
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
public class MailContactManager extends EntityManager<MailContact, String> {

    private HibernateDao<MailContact, String> mailContactDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        mailContactDao = new HibernateDao<MailContact, String>(sessionFactory, MailContact.class);
    }

    @Override
    protected HibernateDao<MailContact, String> getEntityDao() {
        return mailContactDao;
    }

    public MailContact checkExist(String contactGroupId,String email){
        Parameter parameter = new Parameter(contactGroupId,email,StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.id in (select elements(c.objectIds) from ContactGroup c where c.id = :p1) and m.email= :p2 and m.status =:p3");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<MailContact> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }

    public MailContact checkUserMailContactExist(String userId,String email){
        Parameter parameter = new Parameter(userId,email,StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.id in (select elements(c.objectIds) from ContactGroup c where c.status = :p3 and c.userId = :p1) and m.email= :p2 and m.status =:p3");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<MailContact> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 查询联系人组下的联系人
     * @param contactGroupId 联系人组ID
     * @param query 查询条件
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Page<MailContact> getMailContactUsers(String contactGroupId,String query,Page<MailContact> page)
            throws DaoException, SystemException, ServiceException {
        Validate.notNull(contactGroupId, "参数[contactGroupId]不能为null.");
        Parameter parameter = new Parameter(contactGroupId,StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.id in (select elements(c.objectIds) from ContactGroup c where c.id = :p1) and m.status = :p2");
        if(StringUtils.isNotBlank(query)){
            hql.append(" and (m.name like :query or m.email like :query)");
            parameter.put("query","%"+query+"%");
        }
        return getEntityDao().findPage(page, hql.toString(), parameter);
    }
    /**
     * 查找用户所有联系人
     * @param userId
     * @return
     */
    public List<MailContact> findUserMailContacts(String userId){
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.userId = :p1 and m.status = :p2");
        List<MailContact> list =  getEntityDao().find(hql.toString(),parameter);
        return list;
    }

    /**
     * 查询指定条件的数据
     * @param userId
     * @param includeIds
     * @param query
     * @return
     */
    public List<MailContact> findUserMailContactsWithInclude(String userId,List<String> includeIds,String query){
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.userId = :p1 and m.status = :p2");
        if(Collections3.isEmpty(includeIds) && StringUtils.isBlank(query)){
            hql.append(" and 1 <> 1 ");
        } else if(Collections3.isNotEmpty(includeIds) && StringUtils.isNotBlank(query)){
            hql.append(" and (m.id in (:includeIds) or m.name like :query or m.email like :query) ");
            parameter.put("includeIds",includeIds);
            parameter.put("query","%"+query+"%");
        }else{
            if(Collections3.isNotEmpty(includeIds)){
                hql.append(" and m.id in (:includeIds)");
                parameter.put("includeIds",includeIds);
            }else if(StringUtils.isNotBlank(query)){
                hql.append(" and (m.name like :query or m.email like :query))");
                parameter.put("query","%"+query+"%");
            }
        }

        List<MailContact> list =  getEntityDao().find(hql.toString(),parameter);
        return list;
    }


    /**
     * 查找分组下的联系人
     * @param contactGroupId
     * @return
     */
    public List<MailContact> findGroupMailContacts(String contactGroupId){
        Parameter parameter = new Parameter(contactGroupId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.id in (select elements(c.objectIds) from ContactGroup c where c.id = :p1) and m.status = :p2");
        List<MailContact> list =  getEntityDao().find(hql.toString(),parameter);
        return list;
    }


}