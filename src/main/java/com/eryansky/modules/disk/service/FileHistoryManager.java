/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.google.common.collect.Lists;
import com.eryansky.modules.disk.entity.FileHistory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 文件访问历史管理
 * 
 * @author xwj 2015年1月19日 11:43:13
 */
@Service
public class FileHistoryManager extends EntityManager<FileHistory, String> {

	private HibernateDao<FileHistory, String> historyDao;
	@Autowired
	private FolderManager folderManager;
	@Autowired
	private FileManager fileManager;

	/**
	 * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		historyDao = new HibernateDao<FileHistory, String>(sessionFactory,
				FileHistory.class);
	}

	@Override
	protected HibernateDao<FileHistory, String> getEntityDao() {
		return historyDao;
	}

	/**
	 * 获取指定人员的云盘操作记录
	 * 
	 * @param page
	 * @param userId
	 * @param fileOperate
	 *            {@link com.eryansky.modules.disk.entity._enum.FileOperate}
	 * @param fileName
	 * @return
	 */
	public Page<FileHistory> findHistoryPage(Page<FileHistory> page,
											 String userId, Integer fileOperate, String fileName) {
		StringBuffer hql = new StringBuffer("");
		List<Object> params = Lists.newArrayList();
		hql.append(" select h from FileHistory h, File  f  where  f.id  = h.fileId ");
		if (userId != null) {
			hql.append("  and h.userId = ? ");
			params.add(userId);
		}
		if (fileOperate != null) {
			hql.append(" and h.operateType = ? ");
			params.add(fileOperate);
		}
		if (StringUtils.isNotBlank(fileName)) {
			hql.append("and f.name like ? ");
			params.add("%" + fileName + "%");
		}
		hql.append("order by  h.operateTime desc ");

		return getEntityDao().findPage(page, hql.toString(), params.toArray());
	}

	/**
	 * 查找指定用户指定文件的历史记录
	 * 
	 * @param fileId
	 *            文件
	 * @param userId
	 *            用户
	 * @return
	 */
	public FileHistory findUniqueForfileCode(String fileId, String userId) {
		Validate.notNull(fileId, "参数[fileId]不能为null.");
		Validate.notNull(userId, "参数[userId]不能为null.");
		StringBuffer hql = new StringBuffer("");
		Parameter parmas = new Parameter(fileId, userId);
		hql.append(" select h from FileHistory h where h.fileId = :p1 and h.userId = :p2 ");
		return (FileHistory) getEntityDao().createQuery(hql.toString(), parmas)
				.uniqueResult();
	}

	/**
	 * 清除访问记录
	 * @param maoth 保留几个月时间
	 */
	public void clearHistory(int maoth) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		Date date = calendar.getTime();
        Parameter parameter = new Parameter(date);
		StringBuffer hql = new StringBuffer("delete from FileHistory t where t.operateTime < :p1 ");
		getEntityDao().createQuery(hql.toString(), parameter).executeUpdate();
	}
}
