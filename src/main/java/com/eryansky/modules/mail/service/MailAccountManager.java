/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail.entity.MailAccount;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eryansky.common.orm.Page;

import java.util.List;

/**
 * 邮箱帐号
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-17 
 */
@Service
public class MailAccountManager extends EntityManager<MailAccount, String> {

    private HibernateDao<MailAccount, String> mailAccountDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        mailAccountDao = new HibernateDao<MailAccount, String>(sessionFactory, MailAccount.class);
    }

    @Override
    protected HibernateDao<MailAccount, String> getEntityDao() {
        return mailAccountDao;
    }

    @Override
    public void saveEntity(MailAccount entity) throws DaoException, SystemException, ServiceException {
        super.saveEntity(entity);
    }

    @Override
    public void deleteByIds(List<String> ids) throws DaoException, SystemException, ServiceException {
        super.deleteByIds(ids);
    }

    public List<MailAccount> getAllNormal(){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from MailAccount e where e.status = :p1");
        return  getEntityDao().find(hql.toString(), parameter);
    }

    /**
     * 用户邮件账号列表
     * @param userId
     * @return
     */
    public Page<MailAccount> findPageUserMailAcoounts(String userId, Page<MailAccount> page){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from MailAccount e where e.status = :p1 and e.userId =:p2");
        return  getEntityDao().findPage(page, hql.toString(), parameter);
    }

    /**
     * 用户邮件账号列表
     * @param userId
     * @param activate {@link AccountActivite}
     * @return
     */
    public List<MailAccount> findUserMailAcoounts(String userId, Integer activate){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("select e from MailAccount e where e.status = :p1 and e.userId =:p2");
        if(activate != null){
            hql.append(" and e.activate = :activate");
            parameter.put("activate",activate);
        }
//        hql.append(" order by e.orderNo asc");
        return  getEntityDao().find(hql.toString(), parameter);
    }

    /**
     * 用户邮件账号列表
     * @param userId
     * @return
     */
    public List<String> getUserMailAcoountIds(String userId,Integer activate){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("select e.id from MailAccount e where e.status = :p1 and e.userId =:p2");
        if(activate != null){
            hql.append(" and e.activate = :activate");
            parameter.put("activate",activate);
        }
//        hql.append(" order by e.orderNo asc");
        return  getEntityDao().find(hql.toString(), parameter);
    }




    /**
     * 通过邮箱检查该账户是否存在
     * @param userId
     * @param mailAddress
     * @return
     */
    public MailAccount checkExistByMail(String userId,String mailAddress,String id){
        Parameter parameter = new Parameter(userId,mailAddress,StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select m from MailAccount m where m.userId =:p1 and m.mailAddress= :p2 and m.status = :p3");
        if(StringUtils.isNotBlank(id)){
            hql.append(" and (m.id <> :id)");
            parameter.put("id",id);
        }
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<MailAccount> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }



    /**
     * 查找用户接收邮件的账号ID
     * @param userId
     * @param emailId
     * @return
     */
    public String getUserReceiveMailAccountIdByEmailId(String userId, String emailId){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId,emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("select i.mailAccountId from Inbox i where i.status = :p1 and i.userId = :p2 and i.emailId= :p3");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<String> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }



    /**
     * 检查是否连通
     * @param userId
     * @param mailAccountId
     * @return
     */
    public boolean isConnectAccount(String userId,String mailAccountId){
        MailAccount mailAccount = this.getById(mailAccountId);
        Receiver receiver = null;
        boolean open = false;
        try {
            receiver = Receiver.make(mailAccount.toAccount());
            open = receiver.open();
        } catch (Exception e) {
//			e.printStackTrace();
            throw new SystemException(e.getMessage(),e);
        } finally {
            try {
                receiver.close();
            } catch (Exception e) {
            }
        }
        return open;
    }

    /**
     * 检查连通
     * @param mailAccount
     * @return
     */
    public boolean connect(MailAccount mailAccount){
        Receiver receiver = null;
        boolean open = false;
        try {
            mailAccount.setConnectionTimeout(5*1000);
            receiver = Receiver.make(mailAccount.toAccount());
            open = receiver.open();
        } catch (Exception e) {
//			e.printStackTrace();
            throw new SystemException(e.getMessage(),e);
        } finally {
            try {
                receiver.close();
            } catch (Exception e) {
            }
        }
        return open;
    }
}
