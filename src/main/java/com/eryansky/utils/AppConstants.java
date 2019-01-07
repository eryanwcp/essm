/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.modules.sys.service.ConfigService;

import java.io.File;

/**
 * 系统使用的静态变量.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-03-17 上午8:25:36
 */
public class AppConstants extends SysConstants {

    public static long SYS_INIT_TIME = System.currentTimeMillis();

    /**
     * 修改用户密码 个人(需要输入原始密码)
     */
    public static final String USER_UPDATE_PASSWORD_YES = "1";
    /**
     * 修改用户密码 个人(不需要输入原始密码)
     */
    public static final String USER_UPDATE_PASSWORD_NO = "0";

    /**
     * 系统管理员角色编号
     */
    public static final String ROLE_SYSTEM_MANAGER = "system_manager";
    /**
     * 电子邮件 管理员角色编号
     */
    public static final String ROLE_EMAIL_MANAGER = "email_manager";
    /**
     * 通知 管理员角色编号
     */
    public static final String ROLE_NOTICE_MANAGER = "notice_manager";
    /**
     * 云盘管理员
     */
    public static final String ROLE_DISK_MANAGER = "disk_manager";

    private static PropertiesLoader config = null;

    /**
     * 配置文件路径
     */
    public static final String CONFIG_FILE_PATH = "config.properties";

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    private static final class Static {
        private static ConfigService configService = SpringContextHolder.getBean(ConfigService.class);
    }

    /**
     * 获取jdbc交校验sql
     */
    public static String getJdbcValidationQuery() {
        return SysConstants.getAppConfig().getProperty("jdbc.validationQuery");
    }

    /**
     * 获取管理端根路径
     */
    public static String getAdminPath() {
        return SysConstants.getAppConfig().getProperty("adminPath");
    }

    /**
     * 获取前端根路径
     */
    public static String getFrontPath() {
        return SysConstants.getAppConfig().getProperty("frontPath");
    }

    /**
     * 获取移动端根路径
     */
    public static String getMobilePath() {
        return SysConstants.getAppConfig().getProperty("mobilePath");
    }

    /**
     * 获取URL后缀
     */
    public static String getUrlSuffix() {
        return SysConstants.getAppConfig().getProperty("urlSuffix");
    }

    /**
     * 配置文件(config.properties)
     */
    public static PropertiesLoader getConfig() {
        if (config == null) {
            config = new PropertiesLoader(CONFIG_FILE_PATH);
        }
        return config;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        return getConfig().getProperty(key);
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key, String defaultValue) {
        return getConfig().getProperty(key, defaultValue);
    }

    /**
     * 查找属性对应的属性值
     *
     * @param code 属性名称
     * @return
     */
    public static String getConfigValue(String code) {
        return getConfigValue(code, null);
    }

    /**
     * 查找属性对应的属性值
     *
     * @param code         属性名称
     * @param defaultValue 默认值
     * @return
     */
    public static String getConfigValue(String code, String defaultValue) {
        if (isdevMode()) {//调试模式 从本地配置文件读取
            return getConfig(code, defaultValue);
        }
        String configValue = Static.configService.getConfigValueByCode(code);
        if (StringUtils.isBlank(configValue)) {
            return getConfig().getProperty(code, defaultValue);
        }
        return configValue == null ? defaultValue : configValue;
    }



    /**
     * 日志保留时间 天(默认值:30).
     */
    public static int getLogKeepTime() {
        String code = "system.logKeepTime";
        return Integer.valueOf(getConfigValue(code, "30"));
    }

    /**
     * 应用文件 系统日志文件保存路径
     *
     * @return
     */
    public static String getLogPath(String defaultPath) {
        String code = "system.logPath";
        String value = getConfigValue(code,defaultPath);
        if (StringUtils.isBlank(value)) {
            value = defaultPath;
        }
        return value;
    }

    /**
     * 应用文件 磁盘绝对路径
     *
     * @return
     */
    public static String getAppBasePath() {
        String code = "app.basePath";
        return getConfigValue(code);
    }


    /**
     * 应用文件存储目录 放置于webapp下 应用相对路径
     * 自动化部署 不推荐使用
     * 建议使用{@link AppConstants#getDiskBasePath()}
     *
     * @return
     */
    @Deprecated
    public static String getDiskBaseDir() {
        String code = "disk.baseDir";
        return getConfigValue(code);
    }


    /**
     * 云盘存储路径 磁盘绝对路径
     *
     * @return
     */
    public static String getDiskBasePath() {
        String code = "disk.basePath";
        String diskBasePath = getConfigValue(code);
        if (StringUtils.isBlank(diskBasePath)) {
            diskBasePath = getAppBasePath() + File.separator + "disk";
        }
        return diskBasePath;
    }


    /**
     * 文件缓存目录
     *
     * @return
     */
    public static String getDiskTempDir() {
        String code = "disk.tempDir";
        String tempDir = getConfigValue(code);
        if (StringUtils.isBlank(tempDir)) {
            tempDir = getAppBasePath() + File.separator + "temp";
        }
        FileUtils.checkSaveDir(tempDir);
        return tempDir;
    }

    /**
     * 单个文件上传最大 单位：字节
     *
     * @return
     */
    public static Integer getDiskMaxUploadSize() {
        String code = "disk.maxUploadSize";
        return Integer.valueOf(getConfigValue(code));
    }

    /**
     * 云盘附件上传大小限制 (Accepts units B KB MB GB)
     *
     * @return
     */
    public static String getPrettyDiskMaxUploadSize() {
        Integer maxUploadSize = getDiskMaxUploadSize();
        return PrettyMemoryUtils.prettyByteSize(maxUploadSize);
    }

    /**
     * 通知附件上传大小限制
     *
     * @return
     */
    public static Integer getNoticeMaxUploadSize() {
        String code = "notice.maxUploadSize";
        String value = getConfigValue(code);
        if (StringUtils.isBlank(value)) {
            return getDiskMaxUploadSize();
        }
        Integer maxUploadSize = Integer.valueOf(value);
        return maxUploadSize;
    }

    /**
     * 通知附件上传大小限制 (Accepts units B KB MB GB)
     *
     * @return
     */
    public static String getPrettyNoticeMaxUploadSize() {
        Integer maxUploadSize = getNoticeMaxUploadSize();
        return PrettyMemoryUtils.prettyByteSize(maxUploadSize);
    }


    /**
     * 启用安全检查
     *
     * @return
     */
    public static boolean getIsSecurityOn() {
        String code = "security.on";
        String value = getConfigValue(code, "false");
        return "true".equals(value) || "1".equals(value);
    }


    /**
     * 系统最大登录用户数
     *
     * @return
     */
    public static int getSessionUserMaxSize() {
        String code = "sessionUser.MaxSize";
        String value = getConfigValue(code);
        return Integer.valueOf(value);
    }


    /**
     * 获取用户可创建会话数量 默认值：0
     * 0 无限制
     *
     * @return
     */
    public static int getUserSessionSize() {
        String code = "sessionUser.UserSessionSize";
        String value = getConfigValue(code);
        return StringUtils.isBlank(value) ? 0 : Integer.valueOf(value);
    }

    /**
     * 非法登录次数不超过X次
     *
     * @return
     */
    public static int getLoginAgainSize() {
        String code = "password.loginAgainSize";
        String value = getConfigValue(code);
        return StringUtils.isBlank(value) ? 3 : Integer.valueOf(value);
    }

    /**
     * 用户密码更新周期 （天） 默认值：30
     *
     * @return
     */
    public static int getUserPasswordUpdateCycle() {
        String code = "password.updateCycle";
        String value = getConfigValue(code);
        return StringUtils.isBlank(value) ? 30 : Integer.valueOf(value);
    }

    /**
     * 用户密码至少多少次内不能重复 默认值：5
     *
     * @return
     */
    public static int getUserPasswordRepeatCount() {
        String code = "password.repeatCount";
        String value = getConfigValue(code);
        return StringUtils.isBlank(value) ? 5 : Integer.valueOf(value);
    }


    /**
     * webserice发布地址
     *
     * @return
     */
    public static String getWebServiceUrl() {
        String code = "webservice.url";
        return getConfigValue(code);
    }

    /**
     * 当前应用服务地址（包含应用上下文）
     * @return
     */
    public static String getAppURL() {
        String code = "app.url";
        return getConfigValue(code);
    }


    /**
     * 应用名称
     * @return
     */
    public static String getAppName() {
        String code = "app.name";
        return getConfigValue(code);
    }

    /**
     * 应用简称
     * @return
     */
    public static String getAppShortName() {
        String code = "app.shortName";
        return getConfigValue(code);
    }

    /**
     * 应用名称全称
     * @return
     */
    public static String getAppFullName() {
        String code = "app.fullName";
        return getConfigValue(code);
    }

}
