/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.service;

import com.eryansky.common.orm.Page;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.cms.dao.SiteDao;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.modules.sys.entity.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 站点Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-01-15
 */
@Service
@Transactional(readOnly = true)
public class SiteService extends CrudService<SiteDao, Site> {

	public Page<Site> findPage(Page<Site> page, Site site) {
//		DetachedCriteria dc = siteDao.createDetachedCriteria();
//		if (StringUtils.isNotEmpty(site.getName())){
//			dc.add(Restrictions.like("name", "%"+site.getName()+"%"));
//		}
//		dc.add(Restrictions.eq(Site.FIELD_DEL_FLAG, site.getStatus()));
//		//dc.addOrder(Order.asc("id"));
//		return siteDao.find(page, dc);

		site.getSqlMap().put("site", dataScopeFilter((User)site.getCurrentUser(), "o", "u"));

		return super.findPage(page, site);
	}

	@Transactional(readOnly = false)
	public void save(Site site) {
		if (site.getCopyright()!=null){
			site.setCopyright(StringEscapeUtils.unescapeHtml4(site.getCopyright()));
		}
		super.save(site);
		CmsUtils.removeCache("site_" + site.getId());
		CmsUtils.removeCache("siteList");
	}
	
	@Transactional(readOnly = false)
	public void delete(Site site, Boolean isRe) {
		site.setStatus(isRe != null && isRe ? Site.STATUS_NORMAL : Site.STATUS_DELETE);
		super.delete(site);
		//siteDao.DELETE(id);
		CmsUtils.removeCache("site_"+site.getId());
		CmsUtils.removeCache("siteList");
	}

	public List<Site> findAll() {
		return super.findList(new Site());
	}

	public Site getByCode(String siteCode) {
		Site site = new Site();
		site.setCode(siteCode);
		return dao.getByCode(site);
	}
}
