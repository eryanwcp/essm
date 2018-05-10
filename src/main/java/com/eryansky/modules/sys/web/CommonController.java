/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.MediaTypes;
import com.eryansky.modules.sys.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

/**
 * 提供公共方法的Controller.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-2-25 下午1:59:38
 */
@Controller
@RequestMapping("${adminPath}/common")
public class CommonController extends SimpleController {

    @Autowired
    private SystemService systemService;
    /**
     * 字段校验 TODO
     *
     * @param entityName 实体类名称 例如: "Resource"
     * @param fieldName  属性名称
     * @param fieldValue 属性值
     * @param rowId      主键ID
     * @return
     */
    @RequestMapping("fieldCheck")
    @ResponseBody
    public Result fieldCheck(String entityName, String fieldName, String fieldValue, String rowId) {
        String entityId =  null;
        boolean isCheck = true;// 是否通过检查
        if (entityId != null) {
            if (rowId != null) {
                if (!rowId.equals(entityId)) {
                    isCheck = false;
                }
            } else {
                isCheck = false;
            }

        }
        return new Result(Result.SUCCESS, null, isCheck);
    }

    /**
     * JsonP跨域输出示例
     * @param callbackName 回调方法
     * @return
     */
    @RequestMapping(value = "mashup", produces = MediaTypes.JAVASCRIPT_UTF_8)
    @ResponseBody
    public String mashup(@RequestParam("callback") String callbackName) {

        // 设置需要被格式化为JSON字符串的内容.
        Map<String, String> map = Collections.singletonMap("content", "<p>你好，世界！</p>");

        // 渲染返回结果.
        return JsonMapper.getInstance().toJsonP(callbackName, map);
    }

}
