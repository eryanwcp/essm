package com.eryansky.modules.disk.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;
import com.eryansky.modules.disk.entity._enum.FileOperate;
import com.eryansky.modules.disk.utils.FileUtils;

/**
 * 
 * @author xwj 2015年1月19日 09:29:02 文件操作历史
 *
 */
@Entity
@Table(name = "T_DISK_FILE_HISTORY")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler",
		"fieldHandler", "file" })
@JsonFilter(" ")
public class FileHistory extends BaseEntity<FileHistory> {

	/**
	 * 文件名
	 */
	private String fileId;

	/**
	 * 分享用户
	 */
	private String userId;

	/**
	 * 操作时间
	 */
	private Date operateTime;
	/**
	 * 操作类型
	 */
	private Integer operateType;

	/**
	 * 文件是否有效
	 */
	private Boolean isActive = true;

	public FileHistory() {
	}

	@Column(length = 36)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(length = 36)
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@JsonFormat(pattern = BaseEntity.DATE_TIME_FORMAT, timezone = BaseEntity.TIMEZONE)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	@Transient
	public String getOperateTypeDesc() {
		return FileOperate.getFileOperate(operateType).getDescription();
	}

	@Transient
	public String getFileName() {
		String fileName = "";
		if (fileId != null) {
			fileName = FileUtils.getFile(fileId).getName();
		}
		return fileName;
	}


	@Transient
	public String getFileUserName() {
		String fileUserName = "";
		if (fileId != null) {
			fileUserName = FileUtils.getFile(fileId).getUserName();
		}
		return fileUserName;
	}

	@Transient
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
