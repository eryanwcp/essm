/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
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
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail._enum.EmailReadStatus;
import com.eryansky.modules.mail._enum.RecycleBinFromBox;
import com.eryansky.modules.mail.entity.Inbox;
import com.eryansky.modules.mail.entity.RecycleBin;
import com.eryansky.modules.mail.vo.EmailQueryVo;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 收件箱 Service
 */
@Service
public class InboxManager extends EntityManager<Inbox, String> {

    @Autowired
    private EmailManager emailManager;
    @Autowired
    private RecycleBinManager recycleBinManager;


    private HibernateDao inboxDao;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        inboxDao = new HibernateDao<Inbox, String>(sessionFactory, Inbox.class);
    }

    @Override
    protected HibernateDao<Inbox, String> getEntityDao() {
        return inboxDao;
    }

    /**
     * @param emailId 邮件ID
     * @param userId 用户ID
     * @return
     */
    public Inbox getUserInboxByEmailId(String emailId, String userId) {
        Parameter parameter = new Parameter(emailId, userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Inbox i where i.emailId = :p1 and i.userId = :p2");
        List<Inbox> list = getEntityDao().find(hql.toString(), parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     *
     * @param emailId 邮件ID
     * @return
     */
    public List<Inbox> getInboxsByEmailId(String emailId) {
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Inbox i where i.emailId = :p1");
        List<Inbox> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     *
     * @param page
     * @param emailId 邮件ID
     * @return
     */
    public Page<Inbox> findPageByEmailId(Page<Inbox> page, String emailId) {
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Inbox i where i.emailId = :p1 order by i.readTime");
        return getEntityDao().findPage(page,hql.toString(),parameter);
    }


    /**
     * 查找用户收件箱
     * @param page
     * @param userId 用户Id
     * @param emailQueryVo 查询条件
     * @return
     */
    public Page<Inbox> findInboxPageForUser(Page<Inbox> page, String userId, EmailQueryVo emailQueryVo) {
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select i from Inbox i where i.userId = :p1 and i.status = :p2");
        if(emailQueryVo != null){
            if(StringUtils.isNotBlank(emailQueryVo.getMailAccountId())){
                hql.append(" and i.mailAccountId = :mailAccountId ");
                parameter.put("mailAccountId",emailQueryVo.getMailAccountId());
            }else if("".equals(emailQueryVo.getMailAccountId())){
                hql.append(" and (i.mailAccountId is null or i.mailAccountId = '')");
            }else{
                hql.append(" and (i.mailAccountId is null or i.mailAccountId = '' " +
                        "or  exists (select i.id from MailAccount m where m.activate = :activate and m.status = :p2 and i.mailAccountId = m.id))");
                parameter.put("activate", AccountActivite.ACTIVITE.getValue());
            }
            if(emailQueryVo.getInboxReadStatus() != null){
                hql.append(" and i.isRead = :isRead");
                parameter.put("isRead",emailQueryVo.getInboxReadStatus());
            }

            if(StringUtils.isNotBlank(emailQueryVo.getTitle())
                    || StringUtils.isNotBlank(emailQueryVo.getContent())
                    || Collections3.isNotEmpty(emailQueryVo.getSendObjectIds())){
                hql.append(" and i.emailId in (")
                        .append("select e.id from Email e where 1=1 ");
                if(StringUtils.isNotBlank(emailQueryVo.getTitle())){
                    hql.append(" and e.title like :title");
                    parameter.put("title","%"+emailQueryVo.getTitle()+"%");
                }
                if(StringUtils.isNotBlank(emailQueryVo.getContent())){
                    hql.append(" and e.content like :content");
                    parameter.put("content","%" + emailQueryVo.getContent() + "%");
                }
                if (Collections3.isNotEmpty(emailQueryVo.getSendObjectIds())) {
                    hql.append(" and e.sender in (:sendObjectIds)");
                    parameter.put("sendObjectIds", emailQueryVo.getSendObjectIds());
                }

                hql.append(")");

            }

            if (emailQueryVo.getStartTime() != null && emailQueryVo.getEndTime() != null) {
                hql.append(" and  (i.receiveTime between :startTime and :endTime)");
                parameter.put("startTime", emailQueryVo.getStartTime());
                parameter.put("endTime", emailQueryVo.getEndTime());
            }else if (emailQueryVo.getStartTime() != null) {
                hql.append(" and  i.receiveTime >= :sendTime");
                parameter.put("sendTime", emailQueryVo.getStartTime());
            }else if (emailQueryVo.getEndTime() != null) {
                hql.append(" and  i.receiveTime <= :endTime");
                parameter.put("endTime", emailQueryVo.getEndTime());
            }
        }
        hql.append(" order by i.receiveTime desc");

        page = getEntityDao().findPage(page,hql.toString(),parameter);


        //重新计算总数 特殊处理 hql包含distinct语句导致总数出错问题
        String fromHql = hql.toString();
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");

        String countHql = "select count(distinct i.id) " + fromHql;
        Query query = getEntityDao().createQuery(countHql, parameter);
        List<Object> list = query.list();
        Long count = 0L;
        if (list.size() > 0) {
            count = (Long)list.get(0);
        } else {
            count = Long.valueOf(list.size());
        }
        page.setTotalCount(count);
        return page;
    }

    /**
     * 查找用户最近收件邮件
     * @param userId 用户ID
     * @param maxSize 数量 默认值：{@link Page.DEFAULT_PAGESIZE}
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public List<Inbox> getUserNewInboxs(String userId,Integer maxSize) throws SystemException,
            ServiceException, DaoException {
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue(), AccountActivite.ACTIVITE.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select i from Inbox i,Email e where i.emailId = e.id and i.userId = :p1 and i.status = :p2");
        hql.append(" and (i.mailAccountId is null or i.mailAccountId = '' " +
                "or exists (select m.id from MailAccount m where m.activate = :p3 and m.status = :p2 and i.mailAccountId = m.id))");
        hql.append(" order by i.isRead asc,i.receiveTime desc");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setFirstResult(0);
        query.setMaxResults(maxSize == null ? Page.DEFAULT_PAGESIZE : maxSize);
        List<Inbox> list = query.list();
        return list;
    }


    /**
     * 查找用户未读邮件数量
     * @param mailAccountId 账号ID
     * @param userId 用户ID
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public long getUserUnreadEmailNum(String mailAccountId,String userId) throws SystemException,
            ServiceException, DaoException {
        Parameter parameter = new Parameter(userId, EmailReadStatus.unreaded.getValue(), StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append(" select count(*) from Inbox i where i.userId = :p1 and i.isRead = :p2 and i.status = :p3");
        if(StringUtils.isNotBlank(mailAccountId)){
            hql.append(" and i.mailAccountId = :mailAccountId");
            parameter.put("mailAccountId",mailAccountId);
        }else if("".equals(mailAccountId)){
            hql.append(" and (i.mailAccountId is null or i.mailAccountId = '')");
        }else{
            hql.append(" and (i.mailAccountId is null or i.mailAccountId = '' " +
                    "or exists (select i.id from MailAccount m where m.activate = :activate and m.status = :p3 and i.mailAccountId = m.id))");
            parameter.put("activate", AccountActivite.ACTIVITE.getValue());
        }
        List<Long> list = getEntityDao().find(hql.toString(), parameter);
        return list.get(0);
    }


    /**
     * 判断用户是否阅读邮件
     * @param userId 用户ID
     * @param emailId 邮件ID
     * @return
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     * @throws com.eryansky.common.exception.DaoException
     */
    public boolean isRead(String userId, String emailId) throws SystemException,
            ServiceException, DaoException {
        Parameter parameter = new Parameter(userId,emailId, EmailReadStatus.readed.getValue());
        List<Object> list = getEntityDao().find(
                "select count(*) from Inbox i where i.userId= :p1 and i.emailId = :p2 and i.isRead = :p3",
                parameter);
        Long count = (Long) list.get(0);
        if (count > 0L) {
            return true;
        }
        return false;
    }


    /**
     * 从收件箱删除到回收站
     * @param inboxIds 收件箱ID集合
     */
    public void deleteToRecycleBin(List<String> inboxIds){
        if(Collections3.isNotEmpty(inboxIds)){
            Date nowTime = Calendar.getInstance().getTime();
            for(String inboxId:inboxIds){
                Inbox inbox = this.loadById(inboxId);
                inbox.setStatus(StatusState.LOCK.getValue());
                this.update(inbox);
                RecycleBin recycleBin = new RecycleBin();
                recycleBin.setMailAccountId(inbox.getMailAccountId());
                recycleBin.setUserId(inbox.getUserId());
                recycleBin.setEmailId(inbox.getEmailId());
                recycleBin.setDelTime(nowTime);
                recycleBin.setFromBox(RecycleBinFromBox.Inbox.getValue());
                recycleBinManager.save(recycleBin);
            }
        }

    }

    /**
     * 物理删除
     * @param emailId 邮件ID
     * @return
     */
    public int deleteByEmailId(String emailId){
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from Inbox i where i.emailId = :p1");
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
        hql.append("delete from Inbox i where i.emailId in (:p1)");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }


    /**
     * 收件箱设置为已读
     * @param inboxIds 收件箱ID集合
     */
    public void markEmailReaded(List<String> inboxIds) {
        if (Collections3.isNotEmpty(inboxIds)) {
            for (String id : inboxIds) {
                this.setRead(id);
            }
        } else {
            logger.warn("参数[entitys]为空.");
        }

    }

    /**
     * 设置已读
     * @param inboxId 收件箱ID
     */
    public int setRead(String inboxId) {
        Parameter parameter = new Parameter(inboxId, EmailReadStatus.readed.getValue(),  Calendar.getInstance().getTime());
        return getEntityDao().createQuery("update Inbox i set i.isRead = :p2,i.readTime = :p3 where i.id = :p1 ", parameter).executeUpdate();
    }

    /**
     * 设置已读
     * @param userId 用户ID
     * @param emailId 邮件ID
     */
    public int setUserEmailRead(String userId, String emailId) {
        Parameter parameter = new Parameter(EmailReadStatus.readed.getValue(),  Calendar.getInstance().getTime(),userId,emailId);
        return getEntityDao().createQuery("update Inbox i set i.isRead = :p1,i.readTime = :p2 where i.userId = :p3 and i.emailId = :p4 ", parameter).executeUpdate();
    }
}
