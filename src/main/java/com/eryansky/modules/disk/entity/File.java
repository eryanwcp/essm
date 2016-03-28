package com.eryansky.modules.disk.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.disk.entity._enum.FileType;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.disk.utils.FileUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 文件
 */
@Entity
@Table(name = "T_DISK_FILE")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler",
		"fieldHandler", "folder", "fileShareList" })
@JsonFilter(" ")
@Delete(propertyName = "status", type = PropertyType.S)
@SuppressWarnings("serial")
public class File extends DataEntity<File> implements Serializable {

	/**
	 * 文件名
	 */
	private String name;

	/**
	 * 文件标识 用户ID + "_" + hex(md5(filename + now nano time + counter++)) $
	 * {@link com.eryansky.core.web.upload.FileUploadUtils.encodingFilenamePrefix}
	 * 区别于文件的md5
	 */
	private String code;
	/**
	 * 存储路径
	 */
	private String filePath;

	/**
	 * 文件大小 单位 字节
	 */
	private Long fileSize;
	/**
	 * 文件后缀
	 */
	private String fileSuffix;
	/**
	 * 关键字
	 */
	private String keyword;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 文件分类 {@link com.eryansky.modules.disk.entity._enum.FileType}
	 */
	private Integer fileType = FileType.Other.getValue();
	/**
	 * 所属文件夹
	 */
	private Folder folder;
	/**
	 * 所属用户
	 */
	private String userId;
	/**
	 * 文件来源用户
	 */
	private String shareUserId;

	/**
	 * 操作权限
	 */
	private List<String> operate_all;

	/**
	 * 分享对象集合
	 */
	private List<FileShare> fileShareList = Lists.newArrayList();

	/**
	 * 构造方法
	 */
	public File() {
	}

	@Column(length = 512, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 128, nullable = false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	@Column(length = 1024)
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(length = 36)
	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	@Column(length = 128)
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Column(length = 255)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getFileType() {
		return fileType;
	}

	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "FOLDER_ID")
	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	@OneToMany(cascade = { CascadeType.REFRESH, CascadeType.MERGE,
			CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "file")
	// 出现mapby为被维护端|||默认为延迟加载
	public List<FileShare> getFileShareList() {
		return fileShareList;
	}

	public void setFileShareList(List<FileShare> fileShareList) {
		this.fileShareList = fileShareList;
	}

	@Column(length = 36)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(length = 36)
	public String getShareUserId() {
		return shareUserId;
	}

	public void setShareUserId(String shareUserId) {
		this.shareUserId = shareUserId;
	}

	public void setOperate_all(List<String> operate_all) {
		this.operate_all = operate_all;
	}

	@Transient
	public List<String> getOperate_all() {
		return operate_all;
	}

	/**
	 * 文件上传人---区别收藏操作
	 */
	@Transient
	public String getUserName() {
		if (shareUserId != null) {
			return UserUtils.getUserName(shareUserId);
		} else {
			return UserUtils.getUserName(userId);
		}
	}

	/**
	 * 文件拥有者
	 */
	@Transient
	public String getOwnerName() {
		return UserUtils.getUserName(userId);
	}


	@Transient
	public String getFileDir() {
		return StringUtils.substringBeforeLast(filePath,"/")+"/";
	}

	@Transient
	public String getFileName() {
		return StringUtils.substringAfterLast(filePath,"/");
	}

	/**
	 * 文件所处位置
	 */
	@Transient
	public String getLocation() {
		return FileUtils.getFileLocationName(folder);
	}

	@Transient
	public String getPrettyFileSize() {
		return PrettyMemoryUtils.prettyByteSize(fileSize);
	}

	/**
	 * disk.jsp页面下载方法入参共用性
	 */
	@Transient
	public String getFileId() {
		return this.id;
	}

	/**
	 * 文件复制
	 * 
	 * @return
	 */
	public File copy() {
		File f = new File();
		f.setName(this.getName());
		f.setFilePath(this.filePath);
		f.setCode(this.getCode());
		f.setFileSuffix(this.getFileSuffix());
		f.setFileSize(this.getFileSize());
		f.setFileType(this.getFileType());
		f.setKeyword(this.getKeyword());
		f.setRemark(this.getRemark());
		f.setShareUserId(this.getUserId());
		return f;
	}

	/**
	 * 获取对应磁盘文件
	 * 
	 * @return
	 */
	@Transient
	public java.io.File getDiskFile() {
		return DiskUtils.getDiskFile(this);
	}

}
