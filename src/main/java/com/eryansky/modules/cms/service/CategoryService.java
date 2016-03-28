/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.service;

import com.eryansky.common.orm.Page;
import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.cms.dao.CategoryDao;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.modules.sys.entity.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 栏目Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-5-31
 */
@Service
@Transactional(readOnly = true)
public class CategoryService extends TreeService<CategoryDao, Category> {

	public static final String CACHE_CATEGORY_LIST = "categoryList";


	@Autowired
	private SiteService siteService;
	@SuppressWarnings("unchecked")
	public List<Category> findByUser(boolean isCurrentSite, String module){
		
		List<Category> list = (List<Category>) CmsUtils.getCache(CACHE_CATEGORY_LIST);
		if (list == null){
			User user = SecurityUtils.getCurrentUser();
			Category category = new Category();
			category.getSqlMap().put("dsf", dataScopeFilter(user, "o", "u"));
			category.setSite(new Site());
			category.setParent(new Category());
			list = dao.findList(category);
			// 将没有父节点的节点，找到父节点
			Set<String> parentIdSet = Sets.newHashSet();
			for (Category e : list){
				if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())){
					boolean isExistParent = false;
					for (Category e2 : list){
						if (e.getParent().getId().equals(e2.getId())){
							isExistParent = true;
							break;
						}
					}
					if (!isExistParent){
						parentIdSet.add(e.getParent().getId());
					}
				}
			}
			if (parentIdSet.size() > 0){
				//FIXME 暂且注释，用于测试
//				dc = dao.createDetachedCriteria();
//				dc.add(Restrictions.in("id", parentIdSet));
//				dc.add(Restrictions.eq("status", Category.DEL_FLAG_NORMAL));
//				dc.addOrder(Order.asc("site.id")).addOrder(Order.asc("sort"));
//				list.addAll(0, dao.find(dc));
			}
			CmsUtils.putCache(CACHE_CATEGORY_LIST, list);
		}
		
		if (isCurrentSite){
			List<Category> categoryList = Lists.newArrayList(); 
			for (Category e : list){
				if (Category.isRoot(e.getId()) || (e.getSite()!=null && e.getSite().getId() !=null 
						&& e.getSite().getCode().equals(Site.getCurrentSiteCode()))){
					if (StringUtils.isNotEmpty(module)){
						if (module.equals(e.getModule()) || "".equals(e.getModule())){
							categoryList.add(e);
						}
					}else{
						categoryList.add(e);
					}
				}
			}
			return categoryList;
		}
		return list;
	}

	public List<Category> findByParentId(String parentId, String siteId){
		Category parent = new Category();
		parent.setId(parentId);
		Category entity = new Category();
		entity.setParent(parent);
		Site site = new Site();
		site.setId(siteId);
		entity.setSite(site);
		return dao.findByParentIdAndSiteId(entity);
	}
	
	public Page<Category> find(Page<Category> page, Category category) {
		category.setEntityPage(page);
		category.setInMenu(BaseEntity.SHOW);
		page.setResult(dao.findModule(category));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(Category category) {
		Site site = siteService.getByCode(Site.getCurrentSiteCode());
		category.setSite(site);
		if (StringUtils.isNotBlank(category.getViewConfig())){
            category.setViewConfig(StringEscapeUtils.unescapeHtml4(category.getViewConfig()));
        }
		super.save(category);
		CmsUtils.removeCache(CACHE_CATEGORY_LIST);
		CmsUtils.removeCache("mainNavList_"+category.getSite().getId());
	}
	
	@Transactional(readOnly = false)
	public void delete(Category category) {
		super.delete(category);
		CmsUtils.removeCache(CACHE_CATEGORY_LIST);
		CmsUtils.removeCache("mainNavList_"+category.getSite().getId());
	}
	
	/**
	 * 通过编号获取栏目列表
	 */
	public List<Category> findByIds(String ids) {
		List<Category> list = Lists.newArrayList();
		String[] idss = StringUtils.split(ids,",");
		if (idss.length>0){
			for(String id : idss){
				Category e = dao.get(id);
				if(null != e){
					list.add(e);
				}

			}
		}
		return list;
	}


	public List<Category> findByParentIdAndSiteId(Category entity){
		return dao.findByParentIdsLike(entity);
	}

	/**
	 * 根据编码查找
	 * @param categoryCode
	 * @return
	 */
	public Category getByCode(String categoryCode) {
		Category category = new Category();
		category.setCode(categoryCode);
		return dao.getByCode(category);
	}

	public List<Category> findByParentIdsLike(String s) {
		return dao.findByParentIdsLike(new Category(s));
	}
}
