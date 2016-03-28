package com.eryansky.modules.disk.entity;

import com.eryansky.common.utils.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.utils.FileUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author xwj 2015年1月7日 16:37:39 文件分享
 *
 */
@Entity
@Table(name = "T_DISK_FILE_SHARE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler",
		"fieldHandler", "file", "sharedUserList", "sharedFileList" })
@JsonFilter(" ")
@SuppressWarnings("serial")
public class FileShare extends BaseEntity<FileShare> {

	/**
	 * 文件名
	 */
	private File file;

	/**
	 * 分享用户
	 */
	private String userId;

	/**
	 * 分享时间
	 */
	private Date shareTime;
	/**
	 * 操作权限
	 */
	private List<String> operate_all;

	/**
	 * 被分享的人
	 */
	private List<String> sharedUserList = Lists.newArrayList();
	/**
	 * 被分享的文件夹
	 */
	private List<String> sharedFileList = Lists.newArrayList();
	/**
	 * 接收位置
	 */
	private String receiveLocation;

	public FileShare() {
	}

	@Column(length = 36)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@ManyToOne
	@JoinColumn(name = "FILE_ID")
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@ElementCollection
	@CollectionTable(name = "T_DISK_FILE_SHARED_USER", joinColumns = { @JoinColumn(name = "SHARE_ID") })
	@Column(name = "USER_ID",length = 36)
	public List<String> getSharedUserList() {
		return sharedUserList;
	}

	public void setSharedUserList(List<String> sharedUserList) {
		this.sharedUserList = sharedUserList;
	}

	@ElementCollection
	@CollectionTable(name = "T_DISK_FILE_SHARED_FILE", joinColumns = { @JoinColumn(name = "SHARE_ID") })
	@Column(name = "File_ID",length = 36)
	public List<String> getSharedFileList() {
		return sharedFileList;
	}

	public void setSharedFileList(List<String> sharedFileList) {
		this.sharedFileList = sharedFileList;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "SHARE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getShareTime() {
		return shareTime;
	}

	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}

	@Transient
	public String getName() {
		String name = null;
		if (file != null) {
			name = file.getName();
		}
		return name;
	}

	@Transient
	public String getFileId() {
		String fileId = null;
		if (file != null) {
			fileId = file.getId();
		}
		return fileId;
	}

	@Transient
	public String getPrettyFileSize() {
		String size = null;
		if (file != null) {
			size = file.getPrettyFileSize();
		}
		return size;
	}

	@Transient
	public String getUserName() {
		String userName = null;
		if (file != null) {
			userName = file.getUserName();
		}
		return userName;
	}

	@Transient
	public String getShareUserName() {
		return UserUtils.getUserName(userId);
	}

	@Transient
	public List<String> getOperate_all() {
		return operate_all;
	}

	public void setOperate_all(List<String> operate_all) {
		this.operate_all = operate_all;
	}

	@Transient
	public String getReceiveLocation() {
		if (Collections3.isNotEmpty(sharedUserList)) {
			receiveLocation = FolderAuthorize.User.getDescription() + ":"
					+ UserUtils.getUserNames(sharedUserList);
		} else if (Collections3.isNotEmpty(sharedFileList)) {
			receiveLocation = FileUtils.getShareLocationName(sharedFileList);
		} else {
			receiveLocation = "";
		}
		return receiveLocation;
	}

	public void setReceiveLocation(String receiveLocation) {
		this.receiveLocation = receiveLocation;
	}

}
