/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.service;

import com.eryansky.common.orm.Page;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.cms.dao.CommentDao;
import com.eryansky.modules.cms.mapper.Comment;
import com.eryansky.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 评论Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-01-15
 */
@Service
@Transactional(readOnly = true)
public class CommentService extends CrudService<CommentDao, Comment> {

	public Page<Comment> findPage(Page<Comment> page, Comment comment) {
//		DetachedCriteria dc = commentDao.createDetachedCriteria();
//		if (StringUtils.isNotBlank(comment.getContentId())){
//			dc.add(Restrictions.eq("contentId", comment.getContentId()));
//		}
//		if (StringUtils.isNotEmpty(comment.getTitle())){
//			dc.add(Restrictions.like("title", "%"+comment.getTitle()+"%"));
//		}
//		dc.add(Restrictions.eq(Comment.FIELD_DEL_FLAG, comment.getStatus()));
//		dc.addOrder(Order.desc("id"));
//		return commentDao.find(page, dc);
		comment.getSqlMap().put("dsf", dataScopeFilter((User)comment.getCurrentUser(), "o", "u"));
		
		return super.findPage(page, comment);
	}
	
	public void delete(Comment entity, Boolean isRe) {
		super.delete(entity);
	}
}
