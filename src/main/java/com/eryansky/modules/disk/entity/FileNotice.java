package com.eryansky.modules.disk.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.disk.entity._enum.FileOperate;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.YesOrNo;

/**
 * 
 * @author xwj 2015年1月21日 16:38:39 云盘动态
 *
 */
@Entity
@Table(name = "T_DISK_FILE_NOTICE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler",
		"fieldHandler", "file", "receiveUserList", "reveiveOrganList" })
@JsonFilter(" ")
@SuppressWarnings("serial")
public class FileNotice extends DataEntity<FileNotice>{

	/**
	 * 文件名
	 */
	private File file;

	/**
	 * 操作用户
	 */
	private String userId;
	/**
	 * 操作方式
	 */
	private Integer operateType;
	/**
	 * 文件位置
	 */
	private Integer location;

	/**
	 * 已读、未读
	 */
	private Integer isRead;
	/**
	 * 文件是否有效
	 */
	private Integer isActive = YesOrNo.YES.getValue();
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 接收通知的用户
	 */
	private List<String> receiveUserList = Lists.newArrayList();
	/**
	 * 接收通知的机构
	 */
	private List<String> receiveOrganList = Lists.newArrayList();

	public FileNotice() {
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "FILE_ID")
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Column(length = 36)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	@ElementCollection
	@CollectionTable(name = "T_DISK_NOTICE_USER", joinColumns = { @JoinColumn(name = "NOTICE_ID") })
	@Column(name = "USER_ID",length = 36)
	public List<String> getReceiveUserList() {
		return receiveUserList;
	}

	public void setReceiveUserList(List<String> receiveUserList) {
		this.receiveUserList = receiveUserList;
	}

	@ElementCollection
	@CollectionTable(name = "T_DISK_NOTICE_ORGAN", joinColumns = { @JoinColumn(name = "NOTICE_ID") })
	@Column(name = "ORGAN_ID",length = 36)
	public List<String> getReceiveOrganList() {
		return receiveOrganList;
	}

	public void setReceiveOrganList(List<String> receiveOrganList) {
		this.receiveOrganList = receiveOrganList;
	}

	@Transient
	public String getFileName() {
		String fileName = null;
		if (file != null) {
			fileName = file.getName();
		}
		return fileName;

	}

	@Transient
	public String getOperateUserName() {
		return UserUtils.getUserName(userId);
	}

	@Transient
	public String getOperateDesc() {
		String desc = null;
		if (operateType != null) {
			return FileOperate.getFileOperate(operateType).getDescription();
		}
		return desc;

	}

	@Transient
	public String getLocationDsc() {
		String desc = null;
		if (location != null) {
			desc = FolderAuthorize.getFolderAuthorize(location)
					.getDescription();
		}
		return desc;
	}

}
