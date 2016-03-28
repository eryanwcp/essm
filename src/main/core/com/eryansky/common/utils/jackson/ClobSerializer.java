/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.jackson;

import com.eryansky.common.utils.io.ClobUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Clob;
/**
 * 自定义Jackson Clob类型转换.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-4-1 下午2:49:07 
 *
 */
public class ClobSerializer extends JsonSerializer<Clob> {

		@Override
		public void serialize(Clob arg0, JsonGenerator arg1,
				SerializerProvider arg2) throws IOException,
				JsonProcessingException {
			String json = ClobUtil.getString(arg0);
			arg1.writeString(json);
		}
}