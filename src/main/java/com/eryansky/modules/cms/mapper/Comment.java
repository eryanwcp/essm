/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * 评论Entity
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-05-15
 */
public class Comment extends DataEntity<Comment> {

	private static final long serialVersionUID = 1L;
	private Category category;// 分类编号
	private String contentId;	// 归属分类内容的编号（Article.id、Photo.id、Download.id）
	private String title;	// 归属分类内容的标题（Article.title、Photo.title、Download.title）
	private String content; // 评论内容
	private String name; 	// 评论姓名
	private String ip; 		// 评论IP
	private User auditUser; // 审核人
	private Date auditDate;	// 审核时间

	public Comment() {
		super();
		this.status = STATUS_AUDIT;
	}
	
	public Comment(String id){
		this();
		this.id = id;
	}
	
	public Comment(Category category){
		this();
		this.category = category;
	}
	


	@NotNull
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@NotNull
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	@Length(min=1, max=255)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Length(min=1, max=255)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(User auditUser) {
		this.auditUser = auditUser;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


}