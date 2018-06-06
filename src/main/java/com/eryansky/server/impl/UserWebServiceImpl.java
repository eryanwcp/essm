/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.server.impl;


import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.server.UserWebService;
import com.eryansky.server.result.GetUserResult;
import com.eryansky.server.result.WSResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * UserWebService服务端实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-11 下午9:29:12 
 *
 */
//serviceName与portName属性指明WSDL中的名称, endpointInterface属性指向Interface定义类.
@WebService(serviceName = "UserService", portName = "UserServicePort", endpointInterface = "com.eryansky.server.UserWebService")
public class UserWebServiceImpl implements UserWebService {

	private static Logger logger = LoggerFactory.getLogger(UserWebServiceImpl.class);

	private static UserService userService = SpringContextHolder.getBean(UserService.class);

	/**
     */
	public GetUserResult getUser(@WebParam(name = "arg0") String loginName) {
		//校验请求参数
		try {
			System.out.println(loginName);
			Assert.notNull(loginName, "loginName参数为空");
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			return WSResult.buildResult(GetUserResult.class, WSResult.PARAMETER_ERROR, e.getMessage());
		}

		//获取用户
		try {

			GetUserResult result = new GetUserResult();
			User user = userService.getUserByLoginName(loginName);
			result.setUser(user);

			return result;
		} catch (Exception e) {
			String message = "用户不存在(loginName:" + loginName + ")";
			logger.error(message, e);
			return WSResult.buildResult(GetUserResult.class, WSResult.PARAMETER_ERROR, message);
		}
	}

	@Override
	public String getUser2(@WebParam(name = "arg0") String name) {
		return "hello "+name;
	}
}
