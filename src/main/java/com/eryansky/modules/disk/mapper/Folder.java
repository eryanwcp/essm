package com.eryansky.modules.disk.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.modules.disk._enum.FolderType;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;


/**
 * 文件夹
 */
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
     * 授权 {@link FolderAuthorize}
     */
    private String folderAuthorize = FolderAuthorize.User.getValue();
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
    private String type = FolderType.NORMAL.getValue();

    /**
     * 构造方法
     */
    public Folder() {
    }

    public Folder(String id) {
        super(id);
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderAuthorize() {
        return folderAuthorize;
    }

    public void setFolderAuthorize(String folderAuthorize) {
        this.folderAuthorize = folderAuthorize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

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

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return UserUtils.getUserName(userId);
    }

    public String getOrganName() {
        return OrganUtils.getOrganName(organId);

    }
}
