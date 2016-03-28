/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker模板引擎工具类.
 * Utilities of <a href="http://www.freemarker.org">FreeMarker</a>
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class FreeMarkers {

    /**
     * 渲染模板字符串。
     */
    public static String renderString(String templateString, Map<String, ?> model) {
        try {
            StringWriter result = new StringWriter();
            Template t = new Template("default", new StringReader(templateString), new Configuration());
            t.process(model, result);
            return result.toString();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 渲染Template文件.
     */
    public static String renderTemplate(Template template, Object model) {
        try {
            StringWriter result = new StringWriter();
            template.process(model, result);
            return result.toString();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 创建默认配置，设定模板目录.
     */
    public static Configuration buildConfiguration(String directory) throws IOException {
        Configuration cfg = new Configuration();
        Resource path = new DefaultResourceLoader().getResource(directory);
        cfg.setDirectoryForTemplateLoading(path.getFile());
        return cfg;
    }
}
