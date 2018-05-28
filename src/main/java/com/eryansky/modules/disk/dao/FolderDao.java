/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.disk.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;

import com.eryansky.core.orm.mybatis.dao.TreeDao;
import com.eryansky.modules.disk.mapper.Folder;

import java.util.List;

/**
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-04
 */
@MyBatisDao
public interface FolderDao extends TreeDao<Folder> {


    int deleteCascadeByFolderId(Folder entity);

    List<Folder> findChild(Parameter parameter);

    List<Folder> findChilds(Parameter parameter);

    List<String> findChildsIds(Parameter parameter);

    List<Folder> findFolders(Parameter parameter);

    List<Folder> findFoldersByUserId(Parameter parameter);
}
