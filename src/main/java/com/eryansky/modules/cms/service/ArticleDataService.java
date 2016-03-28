/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.service;

import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.cms.dao.ArticleDataDao;
import com.eryansky.modules.cms.mapper.ArticleData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 站点Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-01-15
 */
@Service
@Transactional(readOnly = true)
public class ArticleDataService extends CrudService<ArticleDataDao, ArticleData> {

}
