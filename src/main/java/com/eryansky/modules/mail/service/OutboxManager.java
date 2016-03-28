/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail._enum.OutboxMode;
import com.eryansky.modules.mail._enum.ReceiveType;
import com.eryansky.modules.mail._enum.RecycleBinFromBox;
import com.eryansky.modules.mail.entity.Outbox;
import com.eryansky.modules.mail.entity.ReceiveInfo;
import com.eryansky.modules.mail.entity.RecycleBin;
import com.eryansky.modules.mail.vo.EmailQueryVo;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 发件箱 Service
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 */
@Service
public class OutboxManager extends EntityManager<Outbox, String> {
    @Autowired
    private RecycleBinManager recycleBinManager;
    @Autowired
    private ReceiveInfoManager receiveInfoManager;

	private HibernateDao outboxDao;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        outboxDao = new HibernateDao<Outbox, String>(sessionFactory, Outbox.class);
    }

    @Override
    protected HibernateDao<Outbox, String> getEntityDao() {
        return outboxDao;
    }


    /**
     *
     * @param emailId 邮件ID
     * @return
     */
    public Outbox getOutboxByEmailId(String emailId) {
        Parameter parameter = new Parameter(emailId);
        List<Outbox> list = getEntityDao().find("from Outbox o where o.emailId = :p1", parameter);
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * 收件人信息
     * @param emailId 邮件ID
     * @return
     */
    public List<ReceiveInfo> findToReceiveInfos(String emailId) {
        return receiveInfoManager.findReceiveInfos(emailId, ReceiveType.TO.getValue());
    }

    public List<String> findToReceiveObjectIds(String emailId) {
        return receiveInfoManager.findReceiveObjectIds(emailId, ReceiveType.TO.getValue());
    }


    /**
     * 抄送信息
     * @param emailId 邮件ID
     * @return
     */
    public List<ReceiveInfo> findCcReceiveInfos(String emailId) {
        return receiveInfoManager.findReceiveInfos(emailId, ReceiveType.CC.getValue());
    }

    public List<String> findCcReceiveObjectIds(String emailId) {
        return receiveInfoManager.findReceiveObjectIds(emailId, ReceiveType.CC.getValue());
    }

    /**
     * 密送信息
     *  @param emailId 邮件ID
     * @return
     */
    public List<ReceiveInfo> findBccReceiveInfos(String emailId) {
        return receiveInfoManager.findReceiveInfos(emailId, ReceiveType.BCC.getValue());
    }

    public List<String> findBccReceiveObjectIds(String emailId, Integer mailType) {
        return receiveInfoManager.findReceiveObjectIds(emailId, ReceiveType.BCC.getValue());
    }



    /**
     * 查找用户发件箱（草稿箱）
     * @param page
     * @param userId 用户Id
     * @param emailQueryVo 查询条件
     * @param isDraft 是否查询草稿 否则查询已发件
     * @return
     */
    public Page<Outbox> findOutboxPageForUser(Page<Outbox> page, String userId,EmailQueryVo emailQueryVo,boolean isDraft) {
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select o from Outbox o where o.userId = :p1 and o.status = :p2");
        if(isDraft){
            hql.append(" and (o.outboxMode = :outboxMode or o.outboxMode = :outboxFailMode)");
            parameter.put("outboxMode", OutboxMode.Draft.getValue());
            parameter.put("outboxFailMode", OutboxMode.SendFail.getValue());
        }else{
            hql.append(" and o.outboxMode = :outboxMode ");
            parameter.put("outboxMode", OutboxMode.Sent.getValue());
        }
        if(emailQueryVo != null){
            if(StringUtils.isNotBlank(emailQueryVo.getMailAccountId())){
                hql.append(" and o.mailAccountId = :mailAccountId");
                parameter.put("mailAccountId",emailQueryVo.getMailAccountId());
            }else if("".equals(emailQueryVo.getMailAccountId())){
                hql.append(" and (o.mailAccountId is null or o.mailAccountId = '')");
            }else{
                hql.append(" and (o.mailAccountId is null or o.mailAccountId = '' " +
                        "or exists (select m.id from MailAccount m where m.activate = :activate and m.status = :p2 and o.mailAccountId = m.id))");
                parameter.put("activate", AccountActivite.ACTIVITE.getValue());
            }
            if(StringUtils.isNotBlank(emailQueryVo.getTitle()) || StringUtils.isNotBlank(emailQueryVo.getContent())){
                hql.append(" and o.emailId in (")
                        .append("select e.id from Email e where 1=1 ");
                if(StringUtils.isNotBlank(emailQueryVo.getTitle())){
                    hql.append(" and e.title like :title");
                    parameter.put("title","%"+emailQueryVo.getTitle()+"%");
                }
                if(StringUtils.isNotBlank(emailQueryVo.getContent())){
                    hql.append(" and e.content like :content");
                    parameter.put("content","%" + emailQueryVo.getContent() + "%");
                }
                hql.append(")");

            }
            if(Collections3.isNotEmpty(emailQueryVo.getReceiveObjectIds())){
                hql.append(" and o.id in (")
                .append(" select s.outboxId from SendInfo s where 1=1 ");
                hql.append(" and s.receiveObjectId in :receiverObjectIds");
                parameter.put("receiverObjectIds",emailQueryVo.getReceiveObjectIds());
                hql.append(")");
            }

            if (emailQueryVo.getStartTime() != null && emailQueryVo.getEndTime() != null) {
                hql.append(" and  (o.updateTime between :startTime and :endTime)");
                parameter.put("startTime", emailQueryVo.getStartTime());
                parameter.put("endTime", emailQueryVo.getEndTime());
            }else if (emailQueryVo.getStartTime() != null) {
                hql.append(" and  o.updateTime >= :sendTime");
                parameter.put("sendTime", emailQueryVo.getStartTime());
            }else if (emailQueryVo.getEndTime() != null) {
                hql.append(" and  o.updateTime <= :endTime");
                parameter.put("endTime", emailQueryVo.getEndTime());
            }
        }
        hql.append(" order by o.createTime desc");

        return getEntityDao().findPage(page, hql.toString(), parameter);
    }



    /**
     * 设置邮件状态
     * @param emailId
     * @param outboxMode {@link OutboxMode}
     */
    public void setEmailStatus(String emailId, Integer outboxMode){
    	Parameter parameter = new Parameter(outboxMode, emailId);
        getEntityDao().createQuery("update Outbox o set o.outboxMode = :p1 where o.emailId = :p2 ", parameter).executeUpdate();
    }


    /**
     * 从发件箱(包含草稿箱)删除到回收站
     * @param outboxIds 发件箱ID集合
     */
    public void deleteToRecycleBin(List<String> outboxIds){
        if(Collections3.isNotEmpty(outboxIds)){
            Date nowTime = Calendar.getInstance().getTime();
            for(String outboxId:outboxIds){
                Outbox outbox = this.loadById(outboxId);
                outbox.setStatus(StatusState.LOCK.getValue());
                this.update(outbox);
                RecycleBin recycleBin = new RecycleBin();
                recycleBin.setMailAccountId(outbox.getMailAccountId());
                recycleBin.setUserId(outbox.getUserId());
                recycleBin.setEmailId(outbox.getEmailId());
                recycleBin.setDelTime(nowTime);
                Integer frombox = null;
                if(OutboxMode.Sent.getValue().equals(outbox.getOutboxMode())){
                    frombox = RecycleBinFromBox.Outbox.getValue();
                }else if(OutboxMode.Draft.getValue().equals(outbox.getOutboxMode()) || OutboxMode.SendFail.getValue().equals(outbox.getOutboxMode())){
                    frombox = RecycleBinFromBox.Draftbox.getValue();
                }
                recycleBin.setFromBox(frombox);
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
        hql.append("delete from Outbox o where o.emailId = :p1");
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
        hql.append("delete from Outbox o where o.emailId in (:p1)");
        return getEntityDao().batchExecute(hql.toString(),parameter);
    }



}
