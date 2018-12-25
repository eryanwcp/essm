/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.mapper;

import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class JsonMapper  extends ObjectMapper{

	private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

	private static JsonMapper mapper;


    public JsonMapper() {
        this(null);
    }

    public JsonMapper(Include include) {
		//设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }
        // 允许单引号、允许不带引号的字段名称
        this.enableSimple();
        //解决hibernate延时加载设置
//        this.registerHibernate4Module();
        // 设置默认日期格式
        this.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        this.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);// 空值处理为空串
//        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>(){
//            @Override
//            public void serialize(Object value, JsonGenerator jgen,
//                                  SerializerProvider provider) throws IOException,
//                    JsonProcessingException {
//                jgen.writeString("");
//            }
//        });
	}

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper getInstance() {
        if (mapper == null){
            mapper = new JsonMapper();
        }
        return mapper;
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }


    /**
     * 允许单引号
     * 允许不带引号的字段名称
     */
    public JsonMapper enableSimple() {
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return this;
    }

	/**
	 * Object可以是POJO，也可以是Collection或数组。
	 * 如果对象为Null, 返回"null".
	 * 如果集合为空集合, 返回"[]".
	 */
    public String toJson(Object object) {

        try {
            return this.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }
    
    /**
     * 输出JSONP格式数据.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 将对象转换成json字符串格式
     *
     * @param object     需要转换的对象(注意，需要在要转换的对象中定义JsonFilter注解)
     * @param properties 需要转换的属性
     */
    public String toJson(Object object, String[] properties) {
        return toJson(object,object.getClass(), properties);
    }

    /**
     * 将对象转换成json字符串格式
     *
     * @param object     需要转换的对象(注意，需要在要转换的对象中定义JsonFilter注解)
     * @param clazz      过滤类型 添加@JsonFilter注解的类型 {@link JsonFilter} 示例：@JsonFilter(" ")
     * @param properties 需要转换的属性
     */
    public String toJson(Object object,Class<?> clazz, String[] properties) {
        try {
            return this.writer(
                    new SimpleFilterProvider().addFilter(
                            AnnotationUtils.getValue(
                                    AnnotationUtils.findAnnotation(clazz, JsonFilter.class))
                                    .toString(), SimpleBeanPropertyFilter
                                    .filterOutAllExcept(properties)
                    )
            )
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }

    }

    /**
     * 将对象转换成json字符串格式
     *
     * @param object             需要转换的对象(注意，需要在要转换的对象中定义JsonFilter注解)
     * @param properties2Exclude 需要排除的属性
     */
    public String toJsonWithExcludeProperties(Object object, String[] properties2Exclude)  {
        return toJsonWithExcludeProperties(object,object.getClass(),properties2Exclude);
    }

    /**
     * 将对象转换成json字符串格式
     *
     * @param object             需要转换的对象(注意，需要在要转换的对象中定义JsonFilter注解)
     * @param clazz      过滤类型 添加@JsonFilter注解的类型 {@link JsonFilter} 示例：@JsonFilter(" ")
     * @param properties2Exclude 需要排除的属性
     */
    public String toJsonWithExcludeProperties(Object object,Class<?> clazz, String[] properties2Exclude)  {
        try {
            return this.writer(
                    new SimpleFilterProvider().addFilter(
                            AnnotationUtils.getValue(
                                    AnnotationUtils.findAnnotation(clazz, JsonFilter.class))
                                    .toString(), SimpleBeanPropertyFilter
                                    .serializeAllExcept(properties2Exclude)
                    )
            )
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }

    }


	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null.
	 * 如果JSON字符串为"[]",返回空集合.
	 * 
	 * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句:
	 * List<MyBean> beanList = binder.getMapper().readValue(listString, new TypeReference<List<MyBean>>() {});
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return this.readValue(jsonString, clazz);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

    /**
     * 获取泛型的Collection Type
     * @param jsonString json字符串
     * @param collectionClass 泛型的Collection
     * @param elementClasses 元素类型
     */
    public <T> T fromJson(String jsonString, Class<?> collectionClass, Class<?>... elementClasses) throws Exception {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        JavaType javaType = createCollectionType(collectionClass, elementClasses);
        return fromJson(jsonString,javaType);
    }
	
	/**
	 * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调用本函数.
	 * @see #createCollectionType(Class, Class...)
	 */
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return (T) this.readValue(jsonString, javaType);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}
	
    
    /**
     * 构造泛型的Collection Type如:
     * ArrayList<MyBean>, 则调用constructCollectionType(ArrayList.class,MyBean.class)
     * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.getTypeFactory().constructParametrizedType(collectionClass,collectionClass, elementClasses);
    }


    /**
     * 当JSON里只含有Bean的部分属性时，更新一個已存在Bean，只覆盖该部分的属性.
     */
    @SuppressWarnings("unchecked")
    public <T> T update(String jsonString, T object) {
        try {
            return (T) this.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }
    
    /**
	 * 设定是否使用Enum的toString函数來读写Enum,
	 * 为False时使用Enum的name()读写來读写Enum, 默认为False.
	 * 注意本函数一定要在Mapper创建后, 所有的读写动作之前调用.
	 */
	public JsonMapper enableEnumUseToString() {
        this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return this;
	}

	/**
	 * 支持使用Jaxb的Annotation，使得POJO上的annotation不用与Jackson耦合。
	 * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
	 */
	public JsonMapper enableJaxbAnnotation() {
		JaxbAnnotationModule module = new JaxbAnnotationModule();
        this.registerModule(module);
        return this;
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public ObjectMapper getMapper() {
		return this;
	}

    /**
     * 对象转换为JSON字符串
     * @param object
     * @return
     */
    public static String toJsonString(Object object){
        return JsonMapper.getInstance().toJson(object);
    }

    /**
     * JSON字符串转换为对象
     * @param jsonString
     * @param clazz
     * @return
     */
    public static Object fromJsonString(String jsonString, Class<?> clazz){
        return JsonMapper.getInstance().fromJson(jsonString, clazz);
    }
}
