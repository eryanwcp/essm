/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.FileNotice;
import com.eryansky.modules.disk.entity.FileShare;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FileOperate;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.web.DiskController.NType;

/**
 * 文件分享管理
 * 
 * @author xwj 2015年1月8日 10:06:34
 */
@Service
public class FileShareManager extends EntityManager<FileShare, String> {

	private HibernateDao<FileShare, String> shareDao;
	@Autowired
	private FolderManager folderManager;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private FileNoticeManager fileNoticeManager;

	/**
	 * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		shareDao = new HibernateDao<FileShare, String>(sessionFactory,
				FileShare.class);
	}

	@Override
	protected HibernateDao<FileShare, String> getEntityDao() {
		return shareDao;
	}

	/**
	 * 分享文件给个人--->不生成文件夹副本
	 * 
	 * @modify by xwj 2015年1月21日 18:13:16 增加动态通知
	 * @param file
	 *            文件对象
	 * @param userIds
	 *            接收人集合
	 */
	public void shareFileToPerson(File file, String shareUserId,
			List<String> userIds) {
		if (Collections3.isNotEmpty(userIds)) {
			FileShare shareFile = new FileShare();
			shareFile.setFile(file);
			shareFile.setUserId(shareUserId);
			shareFile.setSharedUserList(userIds);
			shareFile.setShareTime(Calendar.getInstance().getTime());
			save(shareFile);

			FileNotice fileNotice = new FileNotice();
			fileNotice.setFile(file);
			fileNotice.setUserId(shareUserId);
			fileNotice.setOperateType(FileOperate.SHARE.getValue());
			fileNotice.setReceiveUserList(userIds);
			fileNotice.setLocation(FolderAuthorize.Share.getValue());
			fileNoticeManager.save(fileNotice);
		}

	}

	/**
	 * 获取我的分享文件
	 * 
	 * @param page
	 * @param userId
	 *            分享人
	 * @param fileName
	 *            分享文件名称
	 */
	public Page<FileShare> findSharePage(Page<FileShare> page, String userId,
			String fileName) {
		if (userId != null) {
			StringBuffer hql = new StringBuffer();
			Parameter parameter = new Parameter(userId,
					StatusState.DELETE.getValue());
			hql.append(" from FileShare  s  where s.userId  = :p1  and EXISTS ( from File f where f.id = s.file.id  and f.status <> :p2 ");
			if (StringUtils.isNotBlank(fileName)) {
				hql.append(" and  f.name like CONCAT('%', :fileName, '%') ");
				parameter.put("fileName", fileName);
			}
			hql.append(" )  order by s.shareTime desc ");
			page = getEntityDao().findPage(page, hql.toString(), parameter);
		} else {
			page.setTotalCount(0);
		}
		return page;
	}

	/**
	 * 获取分享给我的文件
	 * 
	 * @param page
	 * @param userId
	 *            接收人
	 * @param fileName
	 *            分享文件名称
	 * @return
	 */
	public Page<FileShare> findReceivePage(Page<FileShare> page, String userId,
			String fileName) {
		if (userId != null) {
			StringBuffer hql = new StringBuffer();
			Parameter parameter = new Parameter(StatusState.DELETE.getValue());
			hql.append(" select s from FileShare s, File  f  where f.id = s.file.id and f.status <> :p1 ");
			if (StringUtils.isNotBlank(fileName)) {
				hql.append("  and f.name like :fileName ");
				parameter.put("fileName", "%" + fileName + "%");
			}
			hql.append("  and  :userId in elements(s.sharedUserList)  ");
			parameter.put("userId", userId);
			hql.append(" order by s.shareTime desc ");
			page = getEntityDao().findPage(page, hql.toString(), parameter);

		} else {
			page.setTotalCount(0);
		}
		return page;
	}

	/**
	 * 文件分享给部门--->该类型分享生成文件副本存于部门分享文件夹中
	 * 
	 * @param file
	 *            分享文件对象
	 * @param userId
	 *            分享人
	 * @param organIds
	 *            接收部门集合
	 */
	public void shareFileToOrgan(File file, String userId, List<String> organIds) {
		if (Collections3.isNotEmpty(organIds)) {
			List<String> sharedFileList = Lists.newArrayList();
			Date nowTime = Calendar.getInstance().getTime();
			for (String organId : organIds) {
				Folder folder = folderManager.initShareForOrgan(organId);
				File newFile = file.copy();
				newFile.setUserId(userId);
				newFile.setCreateTime(nowTime);
				newFile.setShareUserId(null);
				newFile.setFolder(folder);
				fileManager.save(newFile);
				sharedFileList.add(newFile.getId());

			}
			FileShare shareFile = new FileShare();
			shareFile.setFile(file);
			shareFile.setUserId(userId);
			shareFile.setSharedFileList(sharedFileList);
			shareFile.setShareTime(nowTime);
			save(shareFile);

			FileNotice fileNotice = new FileNotice();
			fileNotice.setFile(file);
			fileNotice.setUserId(userId);
			fileNotice.setOperateType(FileOperate.SHARE.getValue());
			fileNotice.setReceiveOrganList(organIds);
			fileNotice.setLocation(FolderAuthorize.Organ.getValue());
			fileNoticeManager.save(fileNotice);
		}

	}

	/**
	 * 文件分享给公共--->该类型分享生成文件副本存于公共分享文件夹中
	 * 
	 * @param file
	 *            分享文件对象
	 * @param userId
	 *            分享人
	 */
	public void shareFileToPublic(File file, String userId) {
		Date nowTime = Calendar.getInstance().getTime();
		Folder folder = folderManager.initShareForPublic();
		File newFile = file.copy();
		newFile.setUserId(userId);
		newFile.setShareUserId(null);
		newFile.setFolder(folder);
		newFile.setCreateTime(nowTime);
		fileManager.save(newFile);

		FileShare shareFile = new FileShare();
		shareFile.setFile(file);
		shareFile.setUserId(userId);
		shareFile.setSharedFileList(Lists.newArrayList(newFile.getId()));
		shareFile.setShareTime(nowTime);
		save(shareFile);

		FileNotice fileNotice = new FileNotice();
		fileNotice.setFile(file);
		fileNotice.setUserId(userId);
		fileNotice.setOperateType(FileOperate.SHARE.getValue());
		fileNotice.setLocation(FolderAuthorize.Public.getValue());
		fileNoticeManager.save(fileNotice);

	}

	/**
	 * 取消分享
	 * 
	 * @param shareId
	 */
	public void removeShare(String shareId) {
		if (shareId != null) {
			FileShare fileShare = getById(shareId);
			List<String> sharedFileList = fileShare.getSharedFileList();
			if (Collections3.isNotEmpty(sharedFileList)) {
				fileManager.deleteFolderFiles(sharedFileList);
			}
			delete(fileShare);
		}

	}

	/**
	 * 取消接受分享
	 * 
	 * @param pageId
	 *            记录Id
	 * @param userId
	 *            指定人Id
	 * @param nodeType
	 *            选中节点的类型
	 * @param nodeId
	 *            选中节点Id
	 */
	public void removeReceive(String pageId, String userId, String nodeType,
			Integer nodeId) {
		Validate.notNull(pageId, "参数[pageId]不能为null.");
		if (NType.FolderAuthorize.toString().equals(nodeType)
				&& FolderAuthorize.ReceivePerson.getValue().equals(nodeId)) {// 分享给我时,入参为分享Id
			FileShare fileShare = getById(pageId);
			List<String> sharedUserList = fileShare.getSharedUserList();
			if (Collections3.isNotEmpty(sharedUserList)) {
				int index = sharedUserList.indexOf(userId);
				if (index > -1) {
					sharedUserList.remove(index);
				}
			}
			saveOrUpdate(fileShare);
		} else {// 部门云盘或者公共云盘 或部门节点时,入参为文件Id
			FileShare fileShare = this.findUniqueShareByFileId(pageId);
			if (fileShare != null) {
				fileShare.getSharedFileList().remove(pageId);
				saveOrUpdate(fileShare);
				fileManager.deleteFile(pageId);
			}
		}

	}

	/**
	 * 根据文件Id获取惟一的分享对象
	 * 
	 * @param fileId
	 * @return
	 */
	public FileShare findUniqueShareByFileId(String fileId) {
		Validate.notNull(fileId, "参数[fileId]不能为null.");
		StringBuffer hql = new StringBuffer();
		hql.append(" from FileShare s where ?  in elements(s.sharedFileList) ");
		FileShare share = getEntityDao().findUnique(hql.toString(), fileId);
		return share;
	}

}
