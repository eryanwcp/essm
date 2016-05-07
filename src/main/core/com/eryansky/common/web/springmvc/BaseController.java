/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.springmvc;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateWebUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.reflection.BeanUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;


/**
 * 控制器支持类基类
 * @param <T> 实体类
 * @param <PK> 主键
 */
public abstract class BaseController<T, PK extends Serializable> extends SimpleController {

    protected Class<T> entityClass;

    protected BaseController() {
        this.entityClass = ReflectionUtils.getClassGenricType(getClass());
    }

    /**
     * EntityManager.
     */
    public abstract EntityManager<T, PK> getEntityManager();

    @ModelAttribute("model")
    public T getModel(@RequestParam(value = "id", required = false) PK id, Model model) {
        T cloneEntity = null;
        boolean flag = (id != null);
        if(id != null && id instanceof String){
            flag = StringUtils.isNotBlank((String)id);
        }
        if (flag) {
            T entity = getEntityManager().getById(id);
            //对象拷贝
            if(entity != null){
                try {
                    cloneEntity = (T) BeanUtils.cloneBean(entity);
                } catch (Exception e) {
                    cloneEntity = entity;
                    logger.error(e.getMessage(),e);
                }
            }
            else{
//                throw new ActionException("ID为["+id+"]的记录不存在或已被其它用户删除！");
            }
//            model.addAttribute("model", cloneEntity);
        }
        try {
            if(cloneEntity == null){
                cloneEntity = (T) entityClass.newInstance();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        model.addAttribute("model",cloneEntity);
        return cloneEntity;
    }


    /**
     * 新增或修改.
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute("model")T model) {
        if(model != null){
            getEntityManager().evict(model);
        }
        getEntityManager().saveEntity(model);
        return Result.successResult();
    }

    /**
     * 根据ID删除
     *
     * @param id 主键ID
     * @return
     */
    @RequestMapping(value = {"delete/{id}"})
    @ResponseBody
    public Result delete(@PathVariable PK id) {
        getEntityManager().deleteById(id);
        return Result.successResult();
    }

    /**
     * 根据ID集合批量删除.
     *
     * @param ids 主键ID集合
     * @return
     */
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<PK> ids) {
        getEntityManager().deleteByIds(ids);
        return Result.successResult();
    }


    /**
     * EasyUI 列表数据
     * @return
     */
    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public Datagrid<T> datagrid() {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        // 自动构造属性过滤器
        List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(request);
        Page<T> p = new Page<T>(request);
        p = getEntityManager().findPage(p, filters,true);
        Datagrid<T> datagrid = new Datagrid<T>(p.getTotalCount(), p.getResult());
        return datagrid;
    }

    /**
     * 初始化数据绑定
     * 1. 设置被排除的属性 不自动绑定
     * 2. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 3. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        //设置被排除的属性 不自动绑定
        Object annotationValue = AnnotationUtils.getValue(AnnotationUtils.findAnnotation(entityClass, JsonIgnoreProperties.class));
        if (annotationValue != null) {
            String[] jsonIgnoreProperties = (String[]) annotationValue;
            binder.setDisallowedFields(jsonIgnoreProperties);
        }
    }

}
