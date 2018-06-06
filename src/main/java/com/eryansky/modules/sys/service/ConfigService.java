/**  *  Copyright (c) 2012-2018 http://www.eryansky.com  *  *  Licensed under the Apache License, Version 2.0 (the "License");  */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.ConfigDao;
import com.eryansky.modules.sys.mapper.Config;
import com.eryansky.utils.AppConstants;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Properties;

/**
 * 系统配置参数
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-18
 */
@Service
@Transactional(readOnly = true)
public class ConfigService extends CrudService<ConfigDao,Config>{

    public Page<Config> findPage(Page<Config> page, String query) {
        Parameter parameter = new Parameter();
        parameter.put("query",query);
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        page.setResult(dao.findQueryList(parameter));
        return page;
    }

    /**
     * 根据标识查找
     * @param code 配置标识
     * @return
     */
    public Config getConfigByCode(String code){
        Validate.notBlank("code", "参数[code]不能为空.");
        Config config = new Config();
        config.setCode(code);
        return dao.getBy(config);
    }

    /**
     * 根据标识查找
     * @param code 配置标识
     * @return
     */
    public String getConfigValueByCode(String code){
        Validate.notBlank("code", "参数[code]不能为空.");
        Config config = getConfigByCode(code);
        return config == null ? null:config.getValue();
    }

    /**
     * 从配置文件同步
     * @param overrideFromProperties
     */
    @Transactional(readOnly = false)
    public void syncFromProperties(Boolean overrideFromProperties){
        PropertiesLoader propertiesLoader = AppConstants.getConfig();
        Properties properties = propertiesLoader.getProperties();
        for(String key:properties.stringPropertyNames()){
            Config config = getConfigByCode(key);
            if(config == null){
                config = new Config(key,properties.getProperty(key),null);
                this.save(config);
            }else{
                if(overrideFromProperties != null && overrideFromProperties){
                    config.setValue(properties.getProperty(key));
                    this.save(config);
                }
            }

        }
    }

    /**
     * 删除 物理删除
     * @param ids
     */
    @Transactional(readOnly = false)
    public void deleteByIds(List<String> ids){
        if(Collections3.isNotEmpty(ids)){
            for(String id:ids){
                dao.delete(new Config(id));
            }
        }
    }
}
