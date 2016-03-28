/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
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
import com.eryansky.modules.mail._enum.ContactGroupType;
import com.eryansky.modules.mail.entity.ContactGroup;
import com.eryansky.modules.mail.entity.MailContact;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.UserManager;
import org.apache.commons.lang3.Validate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * 联系人组 Service
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-05
 */
@Service
public class ContactGroupManager extends EntityManager<ContactGroup, String> {

    @Autowired
    private MailContactManager mailContactManager;
    @Autowired
    private UserManager userManager;

    private HibernateDao<ContactGroup, String> contactGroupDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        contactGroupDao = new HibernateDao<ContactGroup, String>(sessionFactory, ContactGroup.class);
    }

    @Override
    protected HibernateDao<ContactGroup, String> getEntityDao() {
        return contactGroupDao;
    }

    public List<ContactGroup> findAllNormal() throws DaoException, SystemException, ServiceException {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        return getEntityDao().find("from ContactGroup c where c.status = :p1",parameter);
    }

    /**
     * 查找用户分组信息
     * @param userId 用户ID
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<ContactGroup> findUserContactGroups(String userId) throws DaoException, SystemException, ServiceException {
        return findUserContactGroups(userId,null,null);
    }

    /**
     * 查找用户分组信息
     * @param userId 用户ID
     * @param contactGroupType {@link ContactGroupType}
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<ContactGroup> findUserContactGroups(String userId,Integer contactGroupType) throws DaoException, SystemException, ServiceException {
        return findUserContactGroups(userId,contactGroupType,null);
    }

    /**
     * 查找用户分组信息
     * @param userId 用户ID
     * @param contactGroupType {@link ContactGroupType}
     * @param query 查询关键字
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<ContactGroup> findUserContactGroups(String userId,Integer contactGroupType,String query) throws DaoException, SystemException, ServiceException {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from ContactGroup c where c.status = :p1 and c.userId = :p2");
        if(contactGroupType != null){
            hql.append(" and c.contactGroupType = :contactGroupType");
            parameter.put("contactGroupType",contactGroupType);
        }
        if(StringUtils.isNotBlank(query)){
            hql.append(" and c.name like :query ");
            parameter.put("query","%"+query+"%");
        }

        hql.append(" order by c.orderNo");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 查询指定条件的数据 否则返回null
     * @param userId
     * @param query
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<ContactGroup> findUserContactGroupsWithInclude(String userId,String query) throws DaoException, SystemException, ServiceException {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from ContactGroup c where c.status = :p1 and c.userId = :p2");
        if(StringUtils.isBlank(query)){
            hql.append(" and 1 <> 1 ");
        }else{
            hql.append(" and c.name like :query ");
            parameter.put("query","%"+query+"%");
        }

        hql.append(" order by c.orderNo");
        return getEntityDao().find(hql.toString(),parameter);
    }
    public ContactGroup saveDefaultUserContactGroupIfNotExist(String userId) throws DaoException, SystemException, ServiceException {
        return checkUserDefaultContractGroup(userId,ContactGroupType.Mail.getValue());
    }


    public ContactGroup saveDefaultMailContactGroupIfNotExist(String userId) throws DaoException, SystemException, ServiceException {
        return checkUserDefaultContractGroup(userId,ContactGroupType.Mail.getValue());
    }


    /**
     *
     * @param userId
     * @param contactGroupType {@link com.eryansky.modules.mail._enum.ContactGroupType}
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public ContactGroup checkUserDefaultContractGroup(String userId,Integer contactGroupType) throws DaoException, SystemException, ServiceException {
        Validate.notBlank(userId, "参数[userId]不能为空或null.");
        Validate.notNull(contactGroupType, "参数[contactGroupType]不能为null.");
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId,contactGroupType,Boolean.TRUE);
        Query query = getEntityDao().createQuery("from ContactGroup c where c.status = :p1 and c.userId = :p2 and c.contactGroupType = :p3 and c.isDefault = :p4", parameter);
        query.setMaxResults(1);
        List<ContactGroup> list = query.list();
        ContactGroup contactGroup = null;
        if(list.isEmpty()){
            contactGroup = new ContactGroup();
            contactGroup.setContactGroupType(contactGroupType);
            contactGroup.setUserId(userId);
            contactGroup.setIsDefault(Boolean.TRUE);
            contactGroup.setName("默认组");
            this.save(contactGroup);
        }else{
            contactGroup = list.get(0);
        }
        return contactGroup;
    }

    /**
     * 删除联系人组 同时删除联系人
     * @param ids
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Override
    public void deleteByIds(List<String> ids) throws DaoException, SystemException, ServiceException {
        if (!Collections3.isEmpty(ids)) {
            for (String id : ids) {
                ContactGroup contactGroup = this.loadById(id);
                contactGroup.setStatus(StatusState.DELETE.getValue());
                contactGroup.getObjectIds().clear();
                getEntityDao().update(contactGroup);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }
    }

    /**
     * 查询联系人组下的联系人
     * @param contactGroupId 联系人组ID
     * @param loginNameOrName 登录名或姓名
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<User> findContactGroupUsers(String contactGroupId, String loginNameOrName) throws DaoException, SystemException, ServiceException {
        Validate.notNull(contactGroupId,"参数[contactGroupId]不能为null.");
        Parameter parameter = new Parameter();
        StringBuffer hql = new StringBuffer();
        hql.append("from User u where u.id in(select elements(g.objectIds) from ContactGroup g where g.id = :contactGroupId)");
        parameter.put("contactGroupId",contactGroupId);
        if(StringUtils.isNotBlank(loginNameOrName)){
            hql.append(" and (u.loginName like :loginName or u.name like :name)");
            parameter.put("loginName","%"+loginNameOrName+"%");
            parameter.put("name","%"+loginNameOrName+"%");
        }
        hql.append(" order by u.orderNo");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 联系人组 用户（分页）
     * @param page
     * @param contactGroupId
     * @param loginNameOrName
     * @return
     */
    public Page<User> findContactGroupUsers(Page<User> page, String contactGroupId, String loginNameOrName) {
        Parameter parameter = new Parameter();
        StringBuffer hql = new StringBuffer();
        hql.append("select u from User u where u.id in(select elements(g.objectIds) from ContactGroup g where g.id = :contactGroupId)");
        parameter.put("contactGroupId",contactGroupId);
        if(StringUtils.isNotBlank(loginNameOrName)){
            hql.append(" and (u.loginName like :loginName or u.name like :name)");
            parameter.put("loginName","%"+loginNameOrName+"%");
            parameter.put("name","%"+loginNameOrName+"%");
        }
        hql.append(" order by u.orderNo");
        return userManager.findPage(page,hql.toString(),parameter);
    }


    /**
     * 检查改组是否存在
     * @param userId 用户Id
     * @param contactGroupType {@link ContactGroupType}
     * @param name 名称
     * @param contactGroupId contactGroupId
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public ContactGroup checkExist(String userId,Integer contactGroupType,String name, String contactGroupId){
        Parameter parameter = new Parameter(userId,name,contactGroupType);
        StringBuffer hql = new StringBuffer();
        hql.append("select c from ContactGroup c where c.userId = :p1 and c.name =:p2 and c.contactGroupType = :p3");
        if(StringUtils.isNotBlank(contactGroupId)){
            hql.append(" and c.id <> :contactGroupId");
            parameter.put("contactGroupId",contactGroupId);
        }
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<ContactGroup> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() throws DaoException, SystemException,
            ServiceException {
        Iterator<?> iterator = contactGroupDao.createQuery(
                "select max(m.orderNo)from ContactGroup m ").iterate();
        Integer max = 0;
        while (iterator.hasNext()) {
            // Object[] row = (Object[]) iterator.next();
            max = (Integer) iterator.next();
            if (max == null) {
                max = 0;
            }
        }
        return max;
    }

    /**
     * 查找分组下的 用户
     * @param contactGroupId
     * @return
     */
    public List<User> findContactGroupUsers(String contactGroupId){
        Parameter parameter = new Parameter(contactGroupId);
        StringBuffer hql = new StringBuffer();
        hql.append("select u from User u where u.id in (select elements(g.objectIds) from ContactGroup g where g.id = :p1)");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 查找分组下的邮件联系人
     * @param contactGroupId
     * @return
     */
    public List<MailContact> findContactGroupMailContacts(String contactGroupId){
        Parameter parameter = new Parameter(contactGroupId);
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailContact m where m.id in (select elements(g.objectIds) from ContactGroup g where g.id = :p1)");
        return getEntityDao().find(hql.toString(),parameter);
    }

}