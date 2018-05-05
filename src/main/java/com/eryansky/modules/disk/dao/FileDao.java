/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.disk.dao;

import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;

import com.eryansky.modules.disk.mapper.File;

import java.util.List;

/**
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-04
 */
@MyBatisDao
public interface FileDao extends CrudDao<File> {

    List<File> findByCode(Parameter parameter);

    List<File> findFilesByIds(Parameter parameter);

    List<File> findFolderFiles(Parameter parameter);

    List<File> findAdvenceQueryList(Parameter parameter);

    Long countFileSize(Parameter parameter);
}
