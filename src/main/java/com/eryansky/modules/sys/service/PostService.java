/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.collections.Collections3;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.sys.mapper.Post;
import com.eryansky.modules.sys.dao.PostDao;
import com.eryansky.core.orm.mybatis.service.CrudService;

import java.util.List;
import java.util.Set;

/**
 * 岗位表 service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
@Transactional(readOnly = true)
public class PostService extends CrudService<PostDao, Post> {

    @Autowired
    private PostDao dao;


    /**
     * 根据ID删除
     * @param id
     */
    public void deleteById(String id) {
        delete(new Post(id));
    }

    /**
     * 根据IDS删除
     * @param ids
     */
    public void deleteByIds(List<String> ids) {
        if(!Collections3.isEmpty(ids)){
            for(String id :ids){
                deleteById(id);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }
    }


    public void savePost(Post entity) {
        super.save(entity);
        savePostOrgans(entity.getId(),entity.getOrganIds());
    }

    public Page<Post> findPage(Page<Post> p, Post entity) {
        Parameter parameter = new Parameter();
        parameter.put(Post.FIELD_STATUS,Post.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,new Post().getDbName());
        parameter.put(BaseInterceptor.PAGE,p);
        parameter.put("organId",entity.getOrganId());
        parameter.put("query", entity.getQuery());
        return p.setResult(dao.findQuery(parameter));
    }


    /**
     * 根据编码查找
     * @param postCode
     * @return
     */
    public Post getPostByCode(String postCode){
        Validate.notNull(postCode, "参数[postCode]不能为null或空");
        Parameter parameter = new Parameter();
        parameter.put("code",postCode);
        List<Post> list = dao.findPost(parameter);
        if(Collections3.isNotEmpty(list) && list.size() > 1){
            logger.warn("岗位编码为["+postCode+"]包含"+list.size()+"条数据");
        }
        return list.get(0);
    }

    /**
     * 根据机构ID以及岗位编码查找
     * @param organId 机构ID
     * @param postCode 岗位编码
     */
    public Post getPostByOC(String organId, String postCode){
        Validate.notNull(organId, "参数[organId]不能为null");
        Validate.notNull(postCode, "参数[postCode]不能为null或空");
        Parameter parameter = new Parameter();
        parameter.put(Post.FIELD_STATUS,Post.STATUS_NORMAL);
        parameter.put("code",postCode);
        parameter.put("organId",organId);
        List<Post> list = dao.findPost(parameter);
        if(Collections3.isNotEmpty(list) && list.size() > 1){
            logger.warn("岗位编码为["+postCode+"]包含"+list.size()+"条数据");
        }
        return Collections3.isNotEmpty(list) ? list.get(0):null;
    }

    /**
     * 机构岗位
     * @param organId
     * @return
     */
    public List<Post> findPostsByOrganId(String organId){
        Validate.notNull(organId, "参数[organId]不能为null");
        Parameter parameter = new Parameter();
        parameter.put(Post.FIELD_STATUS,Post.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findPostsByOrganId(parameter);
    }

    /**
     * 用户岗位
     * @param userId
     * @return
     */
    public List<Post> findPostsByUserId(String userId){
        Validate.notNull(userId, "参数[userId]不能为null");
        Parameter parameter = new Parameter();
        parameter.put(Post.FIELD_STATUS,Post.STATUS_NORMAL);
        parameter.put("userId",userId);
        return dao.findPostsByUserId(parameter);
    }



    /**
     * 保存岗位机构关联信息
     * 保存之前先删除原有
     * @param id 岗位ID
     * @param ids 机构IDS
     */
    @Transactional(readOnly = false)
    public void savePostOrgans(String id, Set<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deletePostOrgansByPostId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertPostOrgans(parameter);
        }
    }

    /**
     * 保存岗位用户关联信息
     * 保存之前先删除原有
     * @param id 岗位ID
     * @param ids 用户IDS
     */
    @Deprecated
    @Transactional(readOnly = false)
    public void savePostUsers(String id, Set<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deletePostUsersByPostId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertPostUsers(parameter);
        }
    }


    /**
     * 保存岗位用户关联信息
     * 保存之前先删除原有
     * @param id 岗位ID
     * @param organId 机构ID
     * @param ids 用户IDS
     */
    @Transactional(readOnly = false)
    public void savePostOrganUsers(String id,String organId, Set<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("organId",organId);
        parameter.put("ids",ids);
        dao.deletePostUsersByPostIdAndOrganId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertPostUsers(parameter);
        }
    }





}
