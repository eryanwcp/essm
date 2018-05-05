package com.eryansky.modules.disk.mapper;

import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.disk._enum.FileType;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.disk.utils.FileUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonFilter;

import java.io.Serializable;
import java.util.List;

/**
 * 文件
 */
@JsonFilter(" ")
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
	 * 文件分类 {@link FileType}
	 */
	private String fileType = FileType.Other.getValue();
	/**
	 * 所属文件夹
	 */
	private String folderId;
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

	private String query;

	/**
	 * 构造方法
	 */
	public File() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getShareUserId() {
		return shareUserId;
	}

	public void setShareUserId(String shareUserId) {
		this.shareUserId = shareUserId;
	}

	public void setOperate_all(List<String> operate_all) {
		this.operate_all = operate_all;
	}

	public List<String> getOperate_all() {
		return operate_all;
	}

	/**
	 * 文件上传人---区别收藏操作
	 */
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
	public String getOwnerName() {
		return UserUtils.getUserName(userId);
	}


	public String getFileDir() {
		return StringUtils.substringBeforeLast(filePath,"/")+"/";
	}

	public String getFileName() {
		return StringUtils.substringAfterLast(filePath,"/");
	}

	/**
	 * 文件所处位置
	 */
	public String getLocation() {
		return FileUtils.getFileLocationName(folderId);
	}

	public String getPrettyFileSize() {
		return PrettyMemoryUtils.prettyByteSize(fileSize);
	}

	/**
	 * disk.jsp页面下载方法入参共用性
	 */
	public String getFileId() {
		return this.id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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
	public java.io.File getDiskFile() {
		return DiskUtils.getDiskFile(this);
	}

}
