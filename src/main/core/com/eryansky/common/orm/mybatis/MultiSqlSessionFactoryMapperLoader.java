/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.mybatis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mybatis的mapper文件中的sql语句被修改后, 只能重启服务器才能被加载, 非常耗时,所以就写了一个自动加载的类,
 * 配置后检查xml文件更改,如果发生变化,重新加载xml里面的内容.
 */
//@Service
//@Lazy(false)
public class MultiSqlSessionFactoryMapperLoader implements DisposableBean, InitializingBean, ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(MultiSqlSessionFactoryMapperLoader.class);

	private ConfigurableApplicationContext context = null;
	private HashMap<String, String> fileMapping = new HashMap<String, String>();
	private Scanner scanner = null;
	private ScheduledExecutorService service = null;
	private Map<String,Resource[]> beanResourcesMap = null;
	private Map<String,SqlSessionFactoryBean> beanSessionFactoryMap = null;
	private List<Resource> resourceList = Lists.newArrayList();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = (ConfigurableApplicationContext) applicationContext;

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			service = Executors.newScheduledThreadPool(1);
			
			// 获取xml所在包
			beanSessionFactoryMap = context.getBeansOfType(SqlSessionFactoryBean.class);
			beanResourcesMap = Maps.newHashMap();
			for(String beanName:beanSessionFactoryMap.keySet()){
				SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean)context.getBean(beanName);
				Field sqlSessionFactoryBeanField = sqlSessionFactoryBean.getClass().getDeclaredField("mapperLocations");
				sqlSessionFactoryBeanField.setAccessible(true);
				Resource[] mapperLocationResources =  (Resource[]) sqlSessionFactoryBeanField.get(sqlSessionFactoryBean);
				beanResourcesMap.put(beanName,mapperLocationResources);
				if(mapperLocationResources != null && mapperLocationResources.length > 0){
					resourceList.addAll(Lists.newArrayList(mapperLocationResources));
				}
				if(logger.isDebugEnabled() && mapperLocationResources != null){
					for(Resource resource: mapperLocationResources){
						logger.debug(resource.getFile().getAbsolutePath());
					}
				}
			}


			// 触发文件监听事件
			scanner = new Scanner();
			scanner.scan();

			service.scheduleAtFixedRate(new Task(), 5, 5, TimeUnit.SECONDS);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	class Task implements Runnable {
		@Override
		public void run() {
			try { if (scanner.isChanged()) {
					logger.debug("*Mapper.xml文件改变,重新加载.");
					scanner.reloadXML();
					logger.debug("加载完毕.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings({ "rawtypes" })
	class Scanner {
		
		private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

		public Scanner() {
		}

		public Resource[] getResource(String basePackage, String pattern) throws IOException {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(context.getEnvironment().resolveRequiredPlaceholders(
							basePackage)) + "/" + pattern;
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			return resources;
		}

		public void reloadXML() throws Exception {
			for(String beanName:beanSessionFactoryMap.keySet()){
				SqlSessionFactoryBean sqlSessionFactoryBean = (SqlSessionFactoryBean)context.getBean(beanName);
				SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
				Configuration configuration = factory.getConfiguration();
				// 移除加载项
				removeConfig(configuration);
				Field sqlSessionFactoryBeanField = sqlSessionFactoryBean.getClass().getDeclaredField("mapperLocations");
				sqlSessionFactoryBeanField.setAccessible(true);
				Resource[] mapperLocationResources =  (Resource[]) sqlSessionFactoryBeanField.get(sqlSessionFactoryBean);

				if (mapperLocationResources != null) {
					for (int i = 0; i < mapperLocationResources.length; i++) {
						Resource resource = mapperLocationResources[i];
						if (resource == null ) {
							continue;
						}
						try {
							XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(),
									configuration, resource.toString(), configuration.getSqlFragments());
							xmlMapperBuilder.parse();
						} catch (Exception e) {
							throw new NestedIOException("Failed to parse mapping resource: '" + resource + "'", e);
						} finally {
							ErrorContext.instance().reset();
						}
					}
				}
			}


		}

		private void removeConfig(Configuration configuration) throws Exception {
			Class<?> classConfig = configuration.getClass();
			clearMap(classConfig, configuration, "mappedStatements");
			clearMap(classConfig, configuration, "caches");
			clearMap(classConfig, configuration, "resultMaps");
			clearMap(classConfig, configuration, "parameterMaps");
			clearMap(classConfig, configuration, "keyGenerators");
			clearMap(classConfig, configuration, "sqlFragments");

			clearSet(classConfig, configuration, "loadedResources");

		}

		private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Map mapConfig = (Map) field.get(configuration);
			mapConfig.clear();
		}

		private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Set setConfig = (Set) field.get(configuration);
			setConfig.clear();
		}

		public void scan() throws IOException {
			if (!fileMapping.isEmpty()) {
				return;
			}
			if (resourceList != null) {
				for (int i = 0; i < resourceList.size(); i++) {
					Resource resource = resourceList.get(i);
					String multi_key = getValue(resource);
					fileMapping.put(resource.getFilename(), multi_key);
				}
			}
		}

		private String getValue(Resource resource) throws IOException {
			String contentLength = String.valueOf((resource.contentLength()));
			String lastModified = String.valueOf((resource.lastModified()));
			return new StringBuilder(contentLength).append(lastModified).toString();
		}

		public boolean isChanged() throws IOException {
			boolean isChanged = false;
			if (resourceList != null) {
				for (int i = 0; i < resourceList.size(); i++) {
					Resource resource = resourceList.get(i);
					String name = resource.getFilename();
					String value = fileMapping.get(name);
					String multi_key = getValue(resource);
					if (!multi_key.equals(value)) {
						logger.debug("文件【"+name+"】改变");
						isChanged = true;
						fileMapping.put(name, multi_key);
					}
				}
			}
			return isChanged;
		}
	}

	@Override
	public void destroy() throws Exception {
		if (service != null) {
			service.shutdownNow();
		}
	}

}
