/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import java.util.List;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.disk.utils.DiskUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.entity._enum.FolderType;
import com.eryansky.modules.disk.web.DiskController;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;

/**
 * 文件夹管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-22
 */
@Service
public class FolderManager extends EntityManager<Folder, String> {

    @Autowired
    private UserManager userManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private OrganManager organManager;

    private HibernateDao<Folder, String> folderDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        folderDao = new HibernateDao<Folder, String>(sessionFactory, Folder.class);
    }

    @Override
    protected HibernateDao<Folder, String> getEntityDao() {
        return folderDao;
    }


    /**
     * 删除文件夹 包含子级文件夹以及文件
     * @param folderId
     * @param folderId
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public void deleteFolderAndFiles(String folderId) throws DaoException, SystemException, ServiceException {
        Validate.notNull(folderId,"参数[folderId]不能为null.");
        List<String> fileIds = Lists.newArrayList();
        List<String> folderIds = Lists.newArrayList();
        recursiveFolderAndFile(folderIds, fileIds, folderId);
        fileManager.deleteFolderFiles(fileIds);
        this.deleteByIds(folderIds);

    }

	/**
	 * 递归 查找文件夹下的文件夹以及文件
	 * 
	 * @param folderIds
	 *            父文件夹
	 * @param fileIds
	 *            文件Id集合
	 * @param folderId
	 *            文件夹Id集合
	 */
	private void recursiveFolderAndFile(List<String> folderIds,
			List<String> fileIds, String folderId) {
		folderIds.add(folderId);
		if (Collections3.isNotEmpty(fileIds)) {
			List<File> folderFiles = fileManager.getFolderFiles(folderId);
			for (File folderFile : folderFiles) {
				fileIds.add(folderFile.getId());
			}
		}
		List<Folder> childFolders = this
				.getChildFoldersByByParentFolderId(folderId);
		if (Collections3.isNotEmpty(childFolders)) {
			for (Folder childFolder : childFolders) {
				recursiveFolderAndFile(folderIds, fileIds, childFolder.getId());
			}
		}
	}

	/**
	 * 递归 查找文件夹下的子文件夹集合
	 * 
	 * @param folderList
	 *            子文件夹集合
	 * @param folder
	 *            父文件夹
	 */
	private void recursiveFolder(List<Folder> folderList, Folder folder) {
		List<Folder> childFolders = this
				.getChildFoldersByByParentFolderId(folder.getId());
		if (Collections3.isNotEmpty(childFolders)) {
			for (Folder childFolder : childFolders) {
				if (folderList != null) {
					folderList.add(childFolder);
				}
				recursiveFolder(folderList, childFolder);
			}
		}
	}



    /**
     * 获取用户文创建的文件夹
     * @param userId 用户ID
     * @return
     */
    public List<Folder> getFoldersByUserId(String userId){
        Validate.notNull(userId,"参数[userId]不能为null.");
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(),userId, FolderType.NORMAL.getValue());
        return getEntityDao().find("from Folder f where f.status <> :p1 and f.userId = :p2 and f.type = :p3 ",parameter);
    }

    /**
     * 某个用户是否可以操作文件夹
     * @param userId 用户ID
     * @param folder 文件夹
     * @return
     */
    public boolean isOperateFolder(String userId,Folder folder){
		String _userId = userId;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(_userId == null){
            _userId = sessionInfo.getUserId();
        }

        boolean operateAble =  DiskUtils.isDiskAdmin(_userId);
        if(!operateAble){
            if(sessionInfo.getUserId().equals(folder.getUserId())){
                operateAble = true;
            }
        }
        return operateAble;
    }
    
	/**
	 * 是否允操作文件夹
	 * 
	 * @param folderId
	 *            文件夹ID
	 * @param isAdmin
	 *            是否是管理员
	 * @return
	 */
	public boolean isOperateFolder(String folderId, boolean isAdmin) {
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		Folder folder = loadById(folderId);
		boolean operateAble = isAdmin;
		if (!operateAble && folder != null) {
			if (sessionInfo.getUserId().equals(folder.getUserId())) {
				operateAble = true;
			}
		}
		return operateAble;
	}

	/**
	 * 我的分享和分享给我不由此进入；其他类型云盘查询隐藏文件夹
	 * @param userId
	 *            指定登录人
	 * @param folderAuthorize
	 *            指定云盘类型--> 不含我的分享和分享给我
	 * @param folderOrgan
	 *            指定隶属机构
	 * @return
	 */
	public List<Folder> getAuthorizeFolders(String userId,
			Integer folderAuthorize, String folderOrgan) {
		Validate.notNull(userId, "参数[userId]不能为null.");
		Parameter parameter = new Parameter(StatusState.DELETE.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append(" from Folder f where f.status <> :p1 ");
		
		hql.append(" and  f.type <> :folderType  ");
		parameter.put("folderType", FolderType.NORMAL.getValue());
		
		if (folderAuthorize != null) { // 正常情况下是仅选中了云盘类型树节点
			hql.append(" and f.folderAuthorize = :folderAuthorize ");
			parameter.put("folderAuthorize", folderAuthorize);

			if (FolderAuthorize.User.getValue().equals(folderAuthorize)
					|| FolderAuthorize.Collect.getValue().equals(
							folderAuthorize)) { // 我的云盘和我的收藏 过滤当前登陆人
				hql.append(" and f.userId = :userId ");
				parameter.put("userId", userId);
			} else if (FolderAuthorize.Organ.getValue().equals(folderAuthorize)) {// 部门云盘过滤当前登陆人部门
				User user = userManager.loadById(userId);
				if (user != null) {
					List<String> userOrganIds = user.getOrganIds();
					if (Collections3.isNotEmpty(userOrganIds)) {
						hql.append(" and f.organId in ( :userOrganIds ) ");
						parameter.put("userOrganIds", userOrganIds);
					}
				}
			}
		} else if (folderOrgan != null) { // 正常情况下是仅选中了部门树节点
			hql.append("  and f.organId = :folderOrgan  ");
			parameter.put("folderOrgan", folderOrgan);
		}
		/* 获取指定用户所有有权限的文件夹
		  else {
			hql.append(" and ( ( f.userId = :userId and f.folderAuthorize = :p3 ) or f.folderAuthorize = :p4 ");
			parameter.put("userId", userId);
			parameter.put("p3", FolderAuthorize.User.getValue());
			parameter.put("p4", FolderAuthorize.Public.getValue());

			User user = userManager.loadById(userId);
			if (user != null) {
				List<String> userRoleIds = user.getRoleIds();
				if (Collections3.isNotEmpty(userRoleIds)) {
					hql.append(" or f.roleId in ( :userRoleIds ) ");
					parameter.put("userRoleIds", userRoleIds);
				}
				List<String> userOrganIds = user.getOrganIds();
				if (Collections3.isNotEmpty(userRoleIds)) {
					hql.append(" or f.organId in ( :userOrganIds ) ");
					parameter.put("userOrganIds", userOrganIds);
				}
			}

			hql.append(")");

		}*/
		hql.append(" order by f.folderAuthorize asc,f.createTime desc ");
		logger.debug(hql.toString());
		return getEntityDao().find(hql.toString(), parameter);
	}

    /**
     * 获取部门下的文件夹
     * @param organId 机构ID
     * @param userId 用户ID
     * @param excludeUserOrganFolder 是否排除用户在部门的文件夹
     * @param parentFolderId
     * @return
     */
    public List<Folder> getOrganFolders(String organId,String userId,boolean excludeUserOrganFolder,String parentFolderId){
        Validate.notNull(organId,"参数[organId]不能为null.");
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(), FolderAuthorize.Organ.getValue(), organId, FolderType.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status <> :p1 and f.folderAuthorize = :p2 and f.organId = :p3 and f.type = :p4 ");
        if(userId != null){
            hql.append(" and f.userId ");
            if(excludeUserOrganFolder){
                hql.append(" <> ");
            }else{
                hql.append(" = ");
            }
            hql.append(" :userId ");
            parameter.put("userId",userId);
        }
        if(parentFolderId != null){
            hql.append(" and f.parentId = :parentFolderId");
            parameter.put("parentFolderId",parentFolderId);
        }else{
            hql.append(" and f.parentId is null");
        }
        hql.append(" order by f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     *
     * @param folderAuthorize {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param userId 用户ID
     * @param organId 机构ID
     * @param excludeFolderId 排除的文件夹ID
     * @param isCascade 是否级联
     * @return
     */
    /*public List<TreeNode> getFolders(Integer folderAuthorize,String userId,String organId,String excludeFolderId,boolean isCascade){
        Validate.notNull(folderAuthorize,"参数[folderAuthorize]不能为null.");
        List<Folder> folders = this.getFoldersByFolderAuthorize(folderAuthorize,userId,organId,null,null);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(Folder folder:folders){
            if(!folder.getId().equals(excludeFolderId)){
                this.recursiveFolderTreeNode(treeNodes,folder,excludeFolderId,isCascade);
            }
        }
        return treeNodes;
    }*/
    
    /**
    *
    * @param folderAuthorize {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
    * @param userId 用户ID
    * @param organId 机构ID
    * @param excludeFolderId 排除的文件夹ID
    * @param isAdmin  是否有管理员权限
    * @param isCascade 是否级联
    * @return
    */
    public List<TreeNode> getFolders(Integer folderAuthorize,String userId,String organId,String excludeFolderId,Boolean isAdmin, boolean isCascade){
        Validate.notNull(folderAuthorize,"参数[folderAuthorize]不能为null.");
        List<Folder> folders = this.getFoldersByFolderAuthorize(folderAuthorize,userId,organId,null,null);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(Folder folder:folders){
            if(!folder.getId().equals(excludeFolderId)){
                this.recursiveFolderTreeNode(treeNodes,folder,excludeFolderId,isAdmin,isCascade);
            }
        }
        return treeNodes;
    }

	/**
	 * 递归文件夹树
	 * @param treeNodes
	 *            传入的树节点
	 * @param folder
	 *            树节点隶属的文件夹
	 * @param excludeFolderId
	 *            父文件夹
	 * @param isAdmin
	 *            是否有管理员权限
	 * @param isCascade
	 *            是否递归
	 */
	public void recursiveFolderTreeNode(List<TreeNode> treeNodes,
			Folder folder, String excludeFolderId, Boolean isAdmin,
			boolean isCascade) {
		TreeNode treeNode = new TreeNode(folder.getId().toString(),
				folder.getName());
		treeNode.getAttributes().put(DiskController.NODE_TYPE,
				DiskController.NType.Folder.toString());
		if (isAdmin != null) {
			treeNode.getAttributes().put(DiskController.NODE_OPERATE,
					isOperateFolder(folder.getId(), isAdmin));
		}
		treeNode.getAttributes().put(DiskController.NODE_USERNAME,
				folder.getUserName());
		treeNode.setIconCls("icon-folder");
		treeNodes.add(treeNode);
		if (isCascade) {
			List<Folder> childFolders = this
					.getChildFoldersByByParentFolderId(folder.getId());
			List<TreeNode> childTreeNodes = Lists.newArrayList();
			for (Folder childFolder : childFolders) {
				if (!childFolder.getId().equals(excludeFolderId)) {
					this.recursiveFolderTreeNode(childTreeNodes, childFolder,
							excludeFolderId, isAdmin, isCascade);
				}
			}
			if (Collections3.isNotEmpty(childTreeNodes)) {
				treeNode.setState(TreeNode.STATE_CLOASED);
				for (TreeNode childTreeNode : childTreeNodes) {
					treeNode.addChild(childTreeNode);
				}
			}
		}

	}
    
        /**
	 * 
	 * @param treeNodes
	 *            传入的树节点
	 * @param folder
	 *            树节点隶属的文件夹
	 * @param excludeFolderId
	 *            父文件夹
	 * @param isCascade
	 *            是否递归
	 */
    /*public void recursiveFolderTreeNode(List<TreeNode> treeNodes,Folder folder,Long excludeFolderId, boolean isCascade){
        TreeNode treeNode = new TreeNode(folder.getId().toString(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE, DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_USERNAME, folder.getUserName());
        treeNode.setIconCls("icon-folder");
        treeNodes.add(treeNode);
        if(isCascade){
            List<Folder> childFolders = this.getChildFoldersByByParentFolderId(folder.getId());
            List<TreeNode> childTreeNodes = Lists.newArrayList();
            for(Folder childFolder:childFolders){
                if(!folder.getId().equals(excludeFolderId)){
                    this.recursiveFolderTreeNode(childTreeNodes,childFolder,excludeFolderId,isCascade);
                }
            }
            for(TreeNode childTreeNode:childTreeNodes){
                treeNode.addChild(childTreeNode);
            }
        }

    }*/

    /**
     * 查询某个授权类型下的文件夹
     * 0个人：个人文件夹 部门：部门下的文件夹（包含自己在部门下建立的文件夹） 角色：角色下的文件夹
     * @param folderAuthorize
     * @param userId
     * @param organId
     * @param roleId
     * @param parentFolderId 上级文件夹 null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
	public List<Folder> getFoldersByFolderAuthorize(Integer folderAuthorize,
													String userId, String organId, String roleId, String parentFolderId) {
		Validate.notNull(folderAuthorize, "参数[folderAuthorize]不能为null.");
		Parameter parameter = new Parameter(StatusState.DELETE.getValue(),
				folderAuthorize, FolderType.NORMAL.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append("from Folder f where f.status <> :p1 and f.folderAuthorize = :p2  and f.type = :p3 ");
		if (FolderAuthorize.User.getValue().equals(folderAuthorize)
				|| FolderAuthorize.Collect.getValue().equals(folderAuthorize)
				|| FolderAuthorize.Share.getValue().equals(folderAuthorize) 
				|| FolderAuthorize.ReceivePerson.getValue().equals(folderAuthorize)) {
			Validate.notNull(userId, "参数[userId]不能为null.");
			hql.append(" and f.userId = :userId");
			parameter.put("userId", userId);
		} else if (FolderAuthorize.Organ.getValue().equals(folderAuthorize)) {
			Validate.notNull(organId, "参数[organId]不能为null.");
			if (userId != null) {
				hql.append(" and f.userId = :userId");
				parameter.put("userId", userId);
			}
			hql.append(" and f.organId = :organId");
			parameter.put("organId", organId);
		} else if (FolderAuthorize.Role.getValue().equals(folderAuthorize)) {
			Validate.notNull(roleId, "参数[roleId]不能为null.");
			hql.append(" and f.roleId = :roleId");
			parameter.put("roleId", roleId);
		} else if (FolderAuthorize.Public.getValue().equals(folderAuthorize)) {
			if (userId != null) {
				hql.append(" and f.userId = :userId");
				parameter.put("userId", userId);
			}
		} else {
			throw new ServiceException("无法识别参数[folderAuthorize]："
					+ folderAuthorize);
		}

		if (parentFolderId != null) {
			hql.append(" and f.parentId = :parentFolderId");
			parameter.put("parentFolderId", parentFolderId);
		} else {
			hql.append(" and (f.parentId is null or f.parentId = '')");
		}
		hql.append(" order by f.createTime desc");
		logger.debug(hql.toString());
		return getEntityDao().find(hql.toString(), parameter);
	}

    /**
     * 根据父级ID查找子级文件夹
     * @param parentFolderId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> getChildFoldersByByParentFolderId(String parentFolderId){
        Parameter parameter = new Parameter(StatusState.DELETE.getValue(), FolderType.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status <> :p1  and f.type = :p2 ");
        if(parentFolderId != null){
            hql.append(" and f.parentId = :parentFolderId");
            parameter.put("parentFolderId",parentFolderId);
        }else{
            hql.append(" and f.parentId is null");
        }
        hql.append(" order by f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

	/**
	 * 判断和创建个人云盘的默认文件夹
	 */
	public Folder initHideForUser(String userId) {
		Parameter parameter = new Parameter(FolderType.HIDE.getValue(), userId, FolderAuthorize.User.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append(" from Folder f where f.type = :p1 and f.userId = :p2 and f.folderAuthorize = :p3 ");
		Folder folder = getEntityDao().findUnique(hql.toString(), parameter);
		if (folder == null) {
			folder = new Folder();// 创建默认文件夹
			folder.setUserId(userId);
			folder.setType(FolderType.HIDE.getValue());
			folder.setFolderAuthorize(FolderAuthorize.User.getValue());
			folder.setName(FolderType.HIDE.getDescription());
			save(folder);
		}
		return folder;
	}

	/**
	 * 判断和创建公共云盘的默认文件夹
	 */
	public Folder initHideForPublic() {
		Parameter parameter = new Parameter(FolderType.HIDE.getValue(),
				FolderAuthorize.Public.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append("from Folder f where f.type = :p1  and f.folderAuthorize = :p2 ");
		Folder folder = getEntityDao().findUnique(hql.toString(), parameter);
		if (folder == null) {
			folder = new Folder(); // 创建默认文件夹
			folder.setUserId("1");
			folder.setFolderAuthorize(FolderAuthorize.Public.getValue());
			folder.setName(FolderType.HIDE.getDescription());
			folder.setType(FolderType.HIDE.getValue());
			save(folder);
		}
		return folder;
	}

	/**
	 * 判断和创建部门云盘下各个部门的默认文件夹
	 * 
	 * @param organId
	 *            返回默认文件夹隶属的部门Id
	 * @param isAdmin
	 *            是否管理员权限
	 * @return
	 */
	public Folder initHideForOrgan(String organId) {
		Folder folder = null;
		if (organId != null) {
			Parameter parameter = new Parameter(FolderType.HIDE.getValue(),
					organId, FolderAuthorize.Organ.getValue());
			StringBuffer hql = new StringBuffer();
			hql.append("from Folder f where f.type = :p1  and  f.organId = :p2 and f.folderAuthorize = :p3 ");
			folder = getEntityDao().findUnique(hql.toString(), parameter);
			if (folder == null) {
				folder = new Folder();
				folder.setUserId("1");
				folder.setType(FolderType.HIDE.getValue());
				folder.setFolderAuthorize(FolderAuthorize.Organ.getValue());
				folder.setName(FolderType.HIDE.getDescription());
				folder.setOrganId(organId);
				save(folder);
			}
		}
		return folder;
	}


	/**
	 * 
	 *  判断和创建我的收藏默认文件夹
	 */
	public Folder initHideForCollect(String userId) {
		Parameter parameter = new Parameter(FolderType.HIDE.getValue(),
				FolderAuthorize.Collect.getValue(),userId);
		StringBuffer hql = new StringBuffer();
		hql.append("from Folder f where f.type = :p1  and f.folderAuthorize = :p2 and f.userId = :p3 ");
		Folder folder = getEntityDao().findUnique(hql.toString(), parameter);
		if (folder == null) {
			folder = new Folder(); // 创建默认文件夹
			folder.setUserId(userId);
			folder.setFolderAuthorize(FolderAuthorize.Collect.getValue());
			folder.setName(FolderType.HIDE.getDescription());
			folder.setType(FolderType.HIDE.getValue());
			save(folder);
		}
		return folder;
	}

	/**
	 * 查找默认文件夹,无则初始化---> 针对 我的云盘、部门云盘 、公共云盘
	 * 
	 * @param folderAuthorize
	 *            云盘类型Id
	 * @param userId
	 *             用户Id
	 * @param organId
	 *            部门Id
	 */
	public Folder initHideFolder(Integer folderAuthorize, String userId, String organId) {
		Folder folder = null;
		if (FolderAuthorize.User.getValue().equals(folderAuthorize)) {
			folder = initHideForUser(userId);
		} else if (FolderAuthorize.Public.getValue().equals(folderAuthorize)) {
			folder = initHideForPublic();
		} else if (organId != null) {
			folder = initHideForOrgan(organId);
		}
		return folder;

	}
	
	/**
	 * 给公共云盘创建分享文件夹
	 * @return
	 */
	public Folder initShareForPublic() {
		Parameter parameter = new Parameter(FolderType.SHARE.getValue(),
				FolderAuthorize.Public.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append("from Folder f where f.type = :p1  and f.folderAuthorize = :p2 ");
		Folder folder = getEntityDao().findUnique(hql.toString(), parameter);
		if (folder == null) {
			folder = new Folder(); // 创建默认文件夹
			folder.setUserId("1");
			folder.setFolderAuthorize(FolderAuthorize.Public.getValue());
			folder.setName(FolderType.SHARE.getDescription());
			folder.setType(FolderType.SHARE.getValue());
			save(folder);
		}
		return folder;

	}

	/**
	 * 给部门创建分享文件夹
	 * 
	 * @param organId
	 *            指定部门
	 * @return
	 */
	public Folder initShareForOrgan(String organId) {
		Folder folder = null;
		if (organId != null) {
			Parameter parameter = new Parameter(FolderType.SHARE.getValue(),
					organId, FolderAuthorize.Organ.getValue());
			StringBuffer hql = new StringBuffer();
			hql.append("from Folder f where f.type = :p1  and  f.organId = :p2 and f.folderAuthorize = :p3 ");
			folder = getEntityDao().findUnique(hql.toString(), parameter);
			if (folder == null) {
				folder = new Folder();
				folder.setUserId("1");
				folder.setType(FolderType.SHARE.getValue());
				folder.setFolderAuthorize(FolderAuthorize.Organ.getValue());
				folder.setName(FolderType.SHARE.getDescription());
				folder.setOrganId(organId);
				save(folder);
			}
		}
		return folder;

	}

	/**
	 * 保存文件。 若为编辑部门下文件时需要判断是否有子文件夹并更新子文件夹的部门信息
	 * 
	 * @param folder
	 *            文件夹对象
	 */
	public void saveFolder(Folder folder) {
		String folderId = folder.getId();
		String folderOrganId = folder.getOrganId();
		if (StringUtils.isNotBlank(folderId) && StringUtils.isNotBlank(folderOrganId)) {
			List<Folder> children = Lists.newArrayList();
			recursiveFolder(children, folder);
			if (Collections3.isNotEmpty(children)) {
				for (Folder child : children) {
					child.setOrganId(folderOrganId);
				}
				saveOrUpdate(children);// 更新子文件夹的部门Id
			}
		}
		saveEntity(folder);
	}



	
}
