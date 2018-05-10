/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.server.result;

import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.server.WsConstants;

import javax.xml.bind.annotation.XmlType;


/**
 * GetUser方法的返回结果.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-11 下午9:28:05 
 *
 */
@XmlType(name = "GetUserResult", namespace = WsConstants.NS)
public class GetUserResult extends WSResult {

	private User user;

	public GetUserResult() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
