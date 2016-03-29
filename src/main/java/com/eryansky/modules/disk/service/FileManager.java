/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.FileShare;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FileSizeType;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.entity._enum.FolderType;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.sys.service.UserManager;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 文件管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-22
 */
@Service
public class FileManager extends EntityManager<File, String> {

    @Autowired
    private FolderManager folderManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private FileShareManager fileShareManager;
	@Autowired
	private IFileManager iFileManager;

    private HibernateDao<File, String> fileDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        fileDao = new HibernateDao<File, String>(sessionFactory, File.class);
    }

    @Override
    protected HibernateDao<File, String> getEntityDao() {
        return fileDao;
    }

    /**
     * 根据文件标识获取文件
     * @param code 文件标识
     * @param excludeFileId 排除的文件ID  可为null
     * @return
     */
    private List<File> getFileByCode(String code,String excludeFileId){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(),code);
        hql.append("from File f where f.status <> :p1  and f.code = :p2");
        if(excludeFileId != null){
            hql.append(" and f.id <> :excludeFileId");
            parameter.put("excludeFileId",excludeFileId);
        }
        List<File> list = getEntityDao().find(hql.toString(),parameter);
        return list;
    }

    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @return
     */
    public List<File> getFolderFiles(String folderId) {
		return getFolderFiles(folderId, null);
	}
	/**
	 * 查找文件夹下所有文件
	 * @param folderId 文件夹ID
	 * @param fileSuffixs 文件后缀名
	 * @return
	 */
	public List<File> getFolderFiles(String folderId,List<String> fileSuffixs){
		Validate.notNull(folderId,"参数[folderId]不能为null.");
		Parameter parameter = new Parameter(folderId);
		StringBuilder hql = new StringBuilder();
		hql.append("from File f where  f.folder.id = :p1");
		if(Collections3.isNotEmpty(fileSuffixs)){
			hql.append(" and f.fileSuffix in (:fileSuffixs)");
			parameter.put("fileSuffixs",fileSuffixs);
		}

		return getEntityDao().find(hql.toString(), parameter);
	}

    public void deleteFile(String fileId){
        File file = getEntityDao().load(fileId);
        try {
            //检查文件是否被引用
            List<File> files = this.getFileByCode(file.getCode(),fileId);
            if(Collections3.isEmpty(files)){
				iFileManager.deleteFile(file.getFilePath());
                logger.debug("删除文件：{}", new Object[]{file.getFilePath()});
            }
//            file.setStatus(StatusState.lock.getValue());
//            this.update(file);
            getEntityDao().delete(file);
        } catch (IOException e) {
            logger.error("删除文件[{}]失败,{}",new Object[]{file.getFilePath(),e.getMessage()});
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    
    
    
	/**
	 *
	 * 文件删除,并删除其关联的分享信息--->文件分享对象在文件删除时级联删除；文件分享对象关联的文件对象需手动删除
	 */
	public void removeFolderFile(List<String> fileIds) {
		if (Collections3.isNotEmpty(fileIds)) {
			for (String fileId : fileIds) {
				List<FileShare> shareList = getEntityDao().get(fileId)
						.getFileShareList();
				if (Collections3.isNotEmpty(shareList)) {
					for (FileShare share : shareList) {
						List<String> sharedFileList = share.getSharedFileList();// 分享关联的文件;
						removeFolderFile(sharedFileList);
					}
				}
				deleteFile(fileId);
			}
		}
	}

    /**
     *
     * 文件删除
     * @param fileIds 文件集合
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public void deleteFolderFiles(List<String> fileIds) throws DaoException, SystemException, ServiceException {
        if (Collections3.isNotEmpty(fileIds)) {
            for (String fileId : fileIds) {
                deleteFile(fileId);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }
    }
    
    /**
	 *
	 * 文件级联删除
	 * 
	 * @param fileCodes
	 *            文件编码集合
	 * @throws Exception
	 */
	public void cascadeDelFile(List<String> fileCodes)throws Exception {
		if (Collections3.isNotEmpty(fileCodes)) {
			for (String code : fileCodes) {
				List<File> fileList = findBy("code", code);
				if (Collections3.isNotEmpty(fileList)) {
					for (File file : fileList) {
						List<FileShare> share = fileShareManager.findBy("file",
								file);
						if (Collections3.isNotEmpty(share)) {
							fileShareManager.deleteAll(share);
						}
						deleteFile(file.getId());
					}
				}
			}
		} else {
			logger.warn("参数[ids]为空.");
		}

	}

	public Page<File> findPage(Page<File> page, List<String> folderIds,
			String fileName) {
		if (Collections3.isNotEmpty(folderIds)) {
			StringBuffer hql = new StringBuffer();
			Parameter parameter = new Parameter(StatusState.DELETE.getValue(),folderIds);
			hql.append("from File f where f.status <> :p1");
			hql.append(" and f.folder.id in (:p2) ");
			if (StringUtils.isNotBlank(fileName)) {
				hql.append(" and f.name like :fileName");
				parameter.put("fileName", "%" + fileName + "%");
			}
			hql.append(" order by f.createTime desc ");
			logger.debug(hql.toString());
			page = getEntityDao().findPage(page, hql.toString(), parameter);
		} else {
			page.setTotalCount(0);
		}
		return page;
	}

    /**
     * 查找用户已用个人存储空间 单位：字节
     * @param userId 用户ID
     * @return
     */
    public long getUserUsedStorage(String userId){
        Validate.notNull(userId, "参数[userId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("select sum(f.fileSize) from File f where f.status <> :p1 and f.folder.folderAuthorize = :p2 and f.userId = :p3");
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(), FolderAuthorize.User.getValue(),userId);
        List<Object> list = getEntityDao().find(hql.toString(),parameter);

        long count = 0L;
        if (list.size() > 0) {
            count = list.get(0) == null ? 0:(Long)list.get(0);
        }
        return count;
    }


    /**
     * 查找部门已用存储空间 单位：字节
     * @param organId 部门ID
     * @return
     */
    public long getOrganUsedStorage(String organId){
        Validate.notNull(organId, "参数[organId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("select sum(f.fileSize) from File f where f.status <> :p1 and f.folder.folderAuthorize = :p2 and f.folder.organId = :p3 and f.folder.type <> :p4 ");
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(), FolderAuthorize.Organ.getValue(),organId, FolderType.SHARE.getValue());
        List<Object> list = getEntityDao().find(hql.toString(),parameter);

        long count = 0L;
        if (list.size() > 0) {
            count = list.get(0) == null ? 0:(Long)list.get(0);
        }
        return count;
    }


    /**
     * 根据ID查找
     * @param fileIds
     * @return
     */
    public List<File> findFilesByIds(List<String> fileIds){
        Parameter parameter = new Parameter(fileIds);
        return getEntityDao().find("from File f where f.id in (:p1)",parameter);
    }
    
    
	/**
	 * 
	 * 收藏文件
	 * 
	 * @param file
	 *            文件对象
	 * @param userId
	 *            收藏用户
	 */
	public void collectFile(File file, String userId) {
		if (file != null) {
			File newFile = file.copy();
			Folder folder = folderManager.initHideForCollect(userId);
			newFile.setFolder(folder);
			newFile.setUserId(userId);
			save(newFile);
		}

	}
	

	/**
	 * 文件检索
	 * 
	 * @param userId
	 *            是否指定权限人员
	 * @param fileName
	 *            文件名称
	 * @param folderAuthorize
	 *            云盘类型
	 * @param fileSizeType
	 *            文件大小类型
	 * @param startTime
	 *            上传时间启
	 * @param endTime
	 *            上传时间止
	 * @param ownerIds
	 *            上传人
	 * @return
	 */

	public Page<File> searchFilePage(Page<File> page, String userId,
			String fileName, Integer folderAuthorize, Integer fileSizeType,
			Date startTime, Date endTime, List<String> ownerIds) {
		StringBuffer hql = new StringBuffer();
		Parameter patameter = new Parameter(StatusState.NORMAL.getValue(),
				FolderType.SHARE.getValue());
		hql.append("  select fl from File fl , Folder fd  where fl.status = :p1 and fl.folder.id = fd.id  and fd.type <> :p2 and fd.status = :p1  ");

		if (folderAuthorize != null) {
			if (userId != null) {
				if (FolderAuthorize.User.getValue().equals(folderAuthorize)
						|| FolderAuthorize.Collect.getValue().equals(
								folderAuthorize)) {
					hql.append(" and fd.userId = :userId ");
					patameter.put("userId", userId);
				} else if (FolderAuthorize.Organ.getValue().equals(
						folderAuthorize)) {
					List<String> userOrganIds = userManager.getById(userId)
							.getOrganIds();
					if (Collections3.isNotEmpty(userOrganIds)) {
						hql.append(" and ( fd.organId in ( :userOrganIds ))");
						patameter.put("userOrganIds", userOrganIds);
					}
				}
			}
			hql.append("  and fd.folderAuthorize = :folderAuthorize  ");
			patameter.put("folderAuthorize", folderAuthorize);

		} else {
			if (userId != null) {
				hql.append(" and  ( ( fd.userId = :userId and ( fd.folderAuthorize = :userAuthorize or fd.folderAuthorize = :collectAuthorize ) ) or fd.folderAuthorize = :publicAuthorize   ");
				patameter.put("userId", userId);
				patameter.put("userAuthorize", FolderAuthorize.User.getValue());
				patameter.put("collectAuthorize",
						FolderAuthorize.Collect.getValue());
				patameter.put("publicAuthorize",
						FolderAuthorize.Public.getValue());
				List<String> userOrganIds = userManager.getById(userId)
						.getOrganIds();
				if (Collections3.isNotEmpty(userOrganIds)) {
					hql.append(" or ( fd.organId in ( :userOrganIds ) and fd.folderAuthorize = :organAuthorize )");
					patameter.put("userOrganIds", userOrganIds);
					patameter.put("organAuthorize",
							FolderAuthorize.Organ.getValue());
				}
				hql.append(" ) ");
			} else {
				hql.append("  and ( fd.folderAuthorize  = :userAuthorize or fd.folderAuthorize  = :collectAuthorize or fd.folderAuthorize  = :publicAuthorize or fd.folderAuthorize  = :organAuthorize ) ");
				patameter.put("userAuthorize", FolderAuthorize.User.getValue());
				patameter.put("collectAuthorize",
						FolderAuthorize.Collect.getValue());
				patameter.put("publicAuthorize",
						FolderAuthorize.Public.getValue());
				patameter.put("organAuthorize",
						FolderAuthorize.Organ.getValue());

			}

		}
		if (StringUtils.isNotBlank(fileName)) {
			hql.append("  and fl.name like  :fileName ");
			patameter.put("fileName", "%" + fileName + "%");
		}
		if (fileSizeType != null) {
			Long minSize = 10 * 1024 * 1024L;
			Long maxSize = 100 * 1024 * 1024L;
			if (FileSizeType.MIN.getValue().equals(fileSizeType)) {
				hql.append("  and fl.fileSize < :fileSize  ");
				patameter.put("fileSize", minSize);
			} else if (FileSizeType.MIDDEN.getValue().equals(fileSizeType)) {
				hql.append("  and fl.fileSize >= :minSize  ");
				hql.append("  and fl.fileSize <= :maxSize  ");
				patameter.put("minSize", minSize);
				patameter.put("maxSize", maxSize);
			} else if (FileSizeType.MAX.getValue().equals(fileSizeType)) {
				hql.append("  and fl.fileSize > :fileSize  ");
				patameter.put("fileSize", maxSize);
			}
		}
		if (startTime != null) {
			hql.append("  and fl.createTime >= :startTime  ");
			patameter.put("startTime", startTime);
		}
		if (endTime != null) {
			hql.append("  and fl.createTime <= :endTime   ");
			patameter.put("endTime", endTime);
		}
		if (Collections3.isNotEmpty(ownerIds)) {
			hql.append("  and fl.userId in ( :ownerIds )    ");
			patameter.put("ownerIds", ownerIds);
		}
		hql.append("  order by fl.folder.folderAuthorize asc,  fl.createTime desc  ");
		return getEntityDao().findPage(page, hql.toString(), patameter);
	}

	/**
	 * 统计文件大小
	 * @param fileIds 文件ID集合
	 * @return
	 */
	public long countFileSize(List<String> fileIds){
		Parameter parameter = new Parameter(fileIds);
		StringBuffer hql = new StringBuffer();
		hql.append("select sum(f.fileSize) from File f where f.id in (:p1)");
		List<Long> list = getEntityDao().find(hql.toString(),parameter);
		return list.get(0);
	}
	
}
