/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.mail._enum.ContactGroupType;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;
import java.util.List;

/**
 * 自定义联系人组
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-05
 */
@Entity
@Table(name = "T_MAIL_CONTACT_GROUP")
@Delete(propertyName = "status", type = PropertyType.S)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ContactGroup extends DataEntity<ContactGroup> {

    /**
     * 联系人组类型 {@link ContactGroupType}
     */
    private Integer contactGroupType = ContactGroupType.System.getValue();
    /**
     * 默认组 每种类型的联系人组仅1个为默认值true
     */
    private Boolean isDefault = Boolean.FALSE;
    /**
     * 联系人组名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 所属用户
     */
    private String userId;
    /**
     * 来源用户 用于标识共享用户组
     */
    private String originUserId;
    /**
     * 系统用户 MailType.System ${@link com.eryansky.modules.sys.entity.User}
     * 系统用户 MailType.Mail ${@link com.eryansky.modules.mail.entity.MailContact}
     *
     */
    private List<String> objectIds = Lists.newArrayList();
    /**
     * 排序号
     */
    private Integer orderNo;


    public ContactGroup() {}

    @Column(length = 1)
    public Integer getContactGroupType() {
        return contactGroupType;
    }

    public void setContactGroupType(Integer contactGroupType) {
        this.contactGroupType = contactGroupType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Column(length = 128)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(length = 36)
    public String getOriginUserId() {
        return originUserId;
    }

    public void setOriginUserId(String originUserId) {
        this.originUserId = originUserId;
    }

    @ElementCollection
    @CollectionTable(name = "T_MAIL_CONTACT_GROUP_OBJECT", joinColumns = {@JoinColumn(name = "CONTACT_GROUP_ID")})
    @Column(name = "OBJECT_ID",length = 36)
    public List<String> getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(List<String> objectIds) {
        this.objectIds = objectIds;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Transient
    public String getUserName() {
        return UserUtils.getUserName(userId);
    }

    /**
     * 类型描述.
     */
    @Transient
    public String getContactTypeView() {
        ContactGroupType ss = ContactGroupType.getContactGroupType(contactGroupType);
        String str = "";
        if (ss != null) {
            str = ss.getDescription();
        }
        return str;
    }

    @Transient
    @JsonIgnore
    public ContactGroup copy(String userId){
        ContactGroup contactGroup = new ContactGroup();
        contactGroup.setName(this.name);
        contactGroup.setContactGroupType(contactGroupType);
        contactGroup.setOriginUserId(this.userId);
        contactGroup.setObjectIds(this.objectIds);
        contactGroup.setOrderNo(this.orderNo);
        contactGroup.setRemark(this.remark);
        contactGroup.setUserId(userId);
        return contactGroup;
    }
}
