/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.entity.FileNotice;
import com.eryansky.modules.sys.service.UserManager;

/**
 * 云盘动态管理
 * 
 * @author xwj 2015年1月21日 16:39:38
 */
@Service
public class FileNoticeManager extends EntityManager<FileNotice, String> {

	private HibernateDao<FileNotice, String> noticeDao;
	@Autowired
	private FolderManager folderManager;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private UserManager userManager;

	/**
	 * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		noticeDao = new HibernateDao<FileNotice, String>(sessionFactory,
				FileNotice.class);
	}

	@Override
	protected HibernateDao<FileNotice, String> getEntityDao() {
		return noticeDao;
	}

	/**
	 * 指定用户权限下的云盘信息动态
	 * 
	 * @param page
	 * @param userId
	 *            指定用户
	 * @return
	 */
	public Page<FileNotice> diskNoticePage(Page<FileNotice> page, String userId) {
		StringBuffer hql = new StringBuffer("");
		Parameter patameter = new Parameter();
		hql.append(" from FileNotice n ");
		if (userId != null) {
			hql.append("  where  :userId  in elements(n.receiveUserList) ");
			patameter.put("userId", userId);
			List<String> userOrganIds = userManager.getById(userId).getOrganIds();
			if (Collections3.isNotEmpty(userOrganIds)) {
				for (int i = 0; i < userOrganIds.size(); i++) {
					hql.append(" or ( :organId").append(i)
							.append("  in  elements(n.receiveOrganList) )");
					patameter.put("organId" + i, userOrganIds.get(i));
				}
			}
			hql.append(" or (  not exists elements(n.receiveUserList)   and  not exists elements(n.receiveOrganList) )");
		}
		hql.append(" order by n.createTime desc  ");

		return getEntityDao().findPage(page, hql.toString(), patameter);
	}

}
