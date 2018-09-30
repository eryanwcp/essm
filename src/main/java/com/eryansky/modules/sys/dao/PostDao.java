/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;

import com.eryansky.modules.sys.mapper.Post;

import java.util.List;

/**
 * 岗位表
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@MyBatisDao
public interface PostDao extends CrudDao<Post> {

    Post getByCode(Parameter parameter);

    List<Post> findQuery(Parameter parameter);

    List<Post> findPost(Parameter parameter);

    List<Post> findPostsByOrganId(Parameter parameter);

    List<Post> findPostsByUserId(Parameter parameter);

    List<String> findPostIdsByUserId(Parameter parameter);

    /**
     * 删除岗位机构关联信息
     * @param parameter id:岗位ID
     */
    int deletePostOrgansByPostId(Parameter parameter);

    /**
     * 插入岗位机构关联信息
     * @param parameter id:岗位ID ids:机构IDS
     */
    int insertPostOrgans(Parameter parameter);


    /**
     * 删除岗位用户关联信息
     * @param parameter id:岗位ID
     */
    int deletePostUsersByPostId(Parameter parameter);

    /**
     * 删除岗位用户关联信息(指定机构)
     * @param parameter id:岗位ID organId:机构ID
     */
    int deletePostUsersByPostIdAndOrganId(Parameter parameter);

    /**
     * 插入岗位用户关联信息
     * @param parameter id:岗位ID ids:用户IDS
     */
    int insertPostUsers(Parameter parameter);


}
