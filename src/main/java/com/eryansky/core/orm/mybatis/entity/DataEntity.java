/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.entity;

import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.persistence.IDataEntity;
import com.eryansky.common.utils.Identities;
import com.eryansky.core.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * 数据Entity类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 */
public abstract class DataEntity<T> extends BaseEntity<T>  implements IDataEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * {@link StatusState}
	 */
	public static final String STATUS_NORMAL = "0";//正常
	public static final String STATUS_DELETE = "1";//删除
	public static final String STATUS_AUDIT = "2";//审核
	public static final String STATUS_LOCK = "3";//锁定

	public static final String FIELD_STATUS = "status";//字段


	/**
	 * 记录状态标志位 {@link StatusState}
	 */
	protected String status = StatusState.NORMAL.getValue();

	/**
	 * 操作版本(乐观锁,用于并发控制)
	 */
	protected Integer version;

	/**
	 * 记录创建者用户登录名
	 */
	protected String createUser;
	/**
	 * 记录创建时间
	 */
	protected Date createTime;

	/**
	 * 记录更新用户 用户登录名
	 */
	protected String updateUser;
	/**
	 * 记录更新时间
	 */
	protected Date updateTime;

	public DataEntity(){
		super();
	}

	public DataEntity(String id){
		super(id);
	}

	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@Override
	public void prePersist(){
		// 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
		if (!this.isNewRecord){
			setId(Identities.uuid2());
		}
		String user = SecurityUtils.getCurrentUserId();
		this.createUser = user;
		this.createTime = Calendar.getInstance().getTime();
		this.updateUser = user;
		this.updateTime = this.createTime;
	}

	@Override
	public void preUpdate() {
		String user = SecurityUtils.getCurrentUserId();
		this.updateUser = user;
		this.updateTime = Calendar.getInstance().getTime();
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public String getStatusView() {
		StatusState s = StatusState.getByValue(status);
		String str = "";
		if (s != null) {
			str = s.getDescription();
		}
		return str;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Integer getVersion() {
		return version;
	}

	@Override
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String getCreateUser() {
		return createUser;
	}

	@Override
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getUpdateUser() {
		return updateUser;
	}

	@Override
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
	@Override
	public Date getUpdateTime() {
		return updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


}
