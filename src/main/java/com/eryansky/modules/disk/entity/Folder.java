package com.eryansky.modules.disk.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.entity._enum.FolderType;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 文件夹
 */
@Entity
@Table(name = "T_DISK_FOLDER")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler","fieldHandler"})
@Delete(propertyName = "status", type = PropertyType.S)
@SuppressWarnings("serial")
public class Folder extends DataEntity<Folder> {
    /**
     * 名称
     */
    private String name;
    /**
     * 存储路径
     */
    private String path;
    /**
     * 大小限制 单位：M 无限制：0
     */
    private Integer limitSize;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 授权 {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     */
    private Integer folderAuthorize = FolderAuthorize.User.getValue();
    /**
     * 文件夹标识 授权类型为System时使用
     */
    private String code;
    /**
     * 所属用户
     */
    private String userId;
    /**
     * 所属部门
     */
    private String organId;
    /**
     * 授权角色
     */
    private String roleId;

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 文件夹类型
     */
    private Integer type = FolderType.NORMAL.getValue();

    /**
     * 构造方法
     */
    public Folder() {
    }

    @Column(length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    @Column(length = 512)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getFolderAuthorize() {
        return folderAuthorize;
    }

    public void setFolderAuthorize(Integer folderAuthorize) {
        this.folderAuthorize = folderAuthorize;
    }

    @Column(length = 64)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Column(length = 36)
    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    @Column(length = 36)
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(length = 36)
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Column(length = 2)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Transient
    public String getUserName() {
        return UserUtils.getUserName(userId);
    }

    @Transient
    public String getOrganName() {
        return OrganUtils.getOrganName(organId);

    }
}
