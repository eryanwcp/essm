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
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail._enum.RecycleBinFromBox;
import com.eryansky.modules.mail.entity.Inbox;
import com.eryansky.modules.mail.entity.Outbox;
import com.eryansky.modules.mail.entity.RecycleBin;
import com.eryansky.modules.mail.vo.EmailQueryVo;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 回收站
 */
@Service
public class RecycleBinManager extends EntityManager<RecycleBin, String> {


	private HibernateDao<RecycleBin, String> recycleBinDao;
    @Autowired
    private OutboxManager outboxManager;
    @Autowired
    private InboxManager inboxManager;
    @Autowired
    private EmailManager emailManager;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
    	recycleBinDao = new HibernateDao<RecycleBin, String>(sessionFactory, RecycleBin.class);
    }

    @Override
    protected HibernateDao<RecycleBin, String> getEntityDao() {
        return recycleBinDao;
    }


    /**
     * 根据邮件ID查找
     * @param emailId 邮件ID
     * @return
     */
    public List<RecycleBin> getByEmailId(String emailId){
        Parameter parameter = new Parameter(emailId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from RecycleBin r where r.emailId = :p1 and r.status = :p2");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 垃圾箱 分页查询
     * @param page
     * @param userId 用户ID
     *
     * @param emailQueryVo 查询条件
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public Page<RecycleBin> findRecycleBinPageForUser(Page<RecycleBin> page, String userId, EmailQueryVo emailQueryVo) throws SystemException,
            ServiceException, DaoException {
    	Assert.notNull(page, "参数[page]为空!");
    	StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        hql.append("select rb from RecycleBin rb  where  rb.status = :p1 ");
        hql.append(" and  rb.userId = :userId");
        parameter.put("userId", userId);

        if(emailQueryVo != null){
            if(StringUtils.isNotBlank(emailQueryVo.getMailAccountId())){
                hql.append(" and rb.mailAccountId = :mailAccountId");
                parameter.put("mailAccountId",emailQueryVo.getMailAccountId());
            }else if("".equals(emailQueryVo.getMailAccountId())){
                hql.append(" and (rb.mailAccountId is null or rb.mailAccountId = '')");
            }else{
                hql.append(" and (rb.mailAccountId is null or rb.mailAccountId = '' " +
                        "or exists (select m.id from MailAccount m where m.activate = :activate and m.status = :p1 and rb.mailAccountId = m.id))");
                parameter.put("activate", AccountActivite.ACTIVITE.getValue());
            }
            if (Collections3.isNotEmpty(emailQueryVo.getSendObjectIds()) ||
                    StringUtils.isNotBlank(emailQueryVo.getTitle()) ||
                    StringUtils.isNotBlank(emailQueryVo.getContent())) {
                hql.append(" and rb.emailId in (")
                        .append("select e.id from Email e where 1=1 ");
                if(Collections3.isNotEmpty(emailQueryVo.getSendObjectIds())){
                    hql.append(" and e.sender in (:sendObjectIds)");
                    parameter.put("sendObjectIds",emailQueryVo.getSendObjectIds());
                }
                if(StringUtils.isNotBlank(emailQueryVo.getTitle())){
                    hql.append(" and e.title like :title");
                    parameter.put("title", "%" + emailQueryVo.getTitle() + "%");
                }
                if (StringUtils.isNotBlank(emailQueryVo.getContent())) {
                    hql.append(" and e.content like :content");
                    parameter.put("content", "%" + emailQueryVo.getContent() + "%");
                }

//                if (emailQueryVo.getSendObjectId() != null) {
//                    hql.append(" and e.sendId = :sendId");
//                    parameter.put("sendId", emailQueryVo.getSendObjectId());
//                }
                hql.append(")");

            }


            if (emailQueryVo.getStartTime() != null && emailQueryVo.getEndTime() != null) {
                hql.append(" and  (rb.delTime between :startTime and :endTime)");
                parameter.put("startTime", emailQueryVo.getStartTime());
                parameter.put("endTime", emailQueryVo.getEndTime());
            }else if (emailQueryVo.getStartTime() != null) {
                hql.append(" and  rb.delTime >= :startTime");
                parameter.put("startTime", emailQueryVo.getStartTime());
            }else if (emailQueryVo.getEndTime() != null) {
                hql.append(" and  rb.delTime <= :endTime");
                parameter.put("endTime", emailQueryVo.getEndTime());
            }
        }


        hql.append(" order by rb.delTime desc");

        return recycleBinDao.findPage(page, hql.toString(), parameter);
    }

    /**
     * 恢复
     * @param userId 用户ID
     * @param recycleBinIds 垃圾邮件ID
     */
    public void recoveryByIds(String userId, List<String> recycleBinIds){
        Validate.notNull(userId,"参数[userId]不能为空!");
        for(String recycleBinId:recycleBinIds){
            RecycleBin recycleBin = this.loadById(recycleBinId);
            Integer fromBox = recycleBin.getFromBox();
            if (RecycleBinFromBox.Outbox.getValue().equals(fromBox) || RecycleBinFromBox.Draftbox.getValue().equals(fromBox)) {
                Outbox outbox = outboxManager.getOutboxByEmailId(recycleBin.getEmailId());
                outbox.setStatus(StatusState.NORMAL.getValue());
                outboxManager.update(outbox);
            } else  if (RecycleBinFromBox.Inbox.getValue().equals(fromBox)){
                Inbox inbox = inboxManager.getUserInboxByEmailId(recycleBin.getEmailId(), userId);
                inbox.setStatus(StatusState.NORMAL.getValue());
                inboxManager.update(inbox);
            }
            this.delete(recycleBin);
        }
    }

    /**
     * 清空垃圾箱
     * @param recycleBinIds 垃圾邮件ID集合
     */
    public void clearByIds(List<String> recycleBinIds){
        for(String recycleBinId:recycleBinIds){
            clearById(recycleBinId);
        }

    }
    /**
     * 清空垃圾箱
     * @param recycleBinId 垃圾邮件ID
     */
    @Logging(value = "清空垃圾箱[{0}]")
    public void clearById(String recycleBinId){
        RecycleBin recycleBin = this.loadById(recycleBinId);
        Integer fromBox = recycleBin.getFromBox();
        if (RecycleBinFromBox.Outbox.getValue().equals(fromBox) || RecycleBinFromBox.Draftbox.getValue().equals(fromBox) ) {//发件箱、草稿箱
            Outbox outbox = outboxManager.getOutboxByEmailId(recycleBin.getEmailId());
            outbox.setStatus(StatusState.DELETE.getValue());
            outboxManager.update(outbox);
        } else if (RecycleBinFromBox.Inbox.getValue().equals(fromBox)){//收件箱
            Inbox inbox = inboxManager.getUserInboxByEmailId(recycleBin.getEmailId(), recycleBin.getUserId());
            inbox.setStatus(StatusState.DELETE.getValue());
            inboxManager.update(inbox);
        }
        this.delete(recycleBin);
    }


    /**
     * 物理删除
     * @param emailId 邮件ID
     * @return
     */
    public int deleteByEmailId(String emailId){
        Parameter parameter = new Parameter(emailId);
        StringBuffer hql = new StringBuffer();
        hql.append("delete from RecycleBin r where r.emailId = :p1");
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
        hql.append("delete from RecycleBin r where r.emailId in (:p1)");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }
}
