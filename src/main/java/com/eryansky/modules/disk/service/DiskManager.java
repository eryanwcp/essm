/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.utils.AppConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-12-10
 */
@Service
public class DiskManager {

    @Autowired
    private FolderManager folderManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private IFileManager iFileManager;


    /**
     * 根据编码获取 获取系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @return
     */
    public Folder checkSystemFolderByCode(String code){
        Validate.notBlank(code, "参数[code]不能为null.");
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(), FolderAuthorize.SysTem.getValue(),code);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 and f.folderAuthorize = :p2 and f.code = :p3");
        List<Folder> list =  folderManager.getEntityDao().find(hql.toString(), parameter);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folderManager.saveOrUpdate(folder);
        }
        return folder;
    }


    /**
     * 根据编码获取 获取用户的系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    public Folder checkSystemFolderByCode(String code,String userId){
        Validate.notBlank(code, "参数[code]不能为null.");
        Validate.notNull(userId, "参数[userId]不能为null.");
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(), FolderAuthorize.SysTem.getValue(),code,userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 and f.folderAuthorize = :p2 and f.code = :p3 and f.userId = :p4");
        List<Folder> list =  folderManager.getEntityDao().find(hql.toString(), parameter);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folder.setUserId(userId);
            folderManager.saveOrUpdate(folder);
        }
        return folder;
    }

    /**
     * 保存系统文件
     * @param folderCode
     * @param file
     * @return
     */
    public File saveSystemFile(String folderCode,File file){
        Validate.notBlank(folderCode, "参数[folderCode]不能为null.");
        Validate.notNull(file, "参数[file]不能为null.");
        Folder folder = checkSystemFolderByCode(folderCode);
        file.setFolder(folder);
        fileManager.save(file);
        return file;
    }

    /**
     * 保存文件
     * @param fileId 文件ID
     * @return
     */
    public File getFileById(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        return fileManager.loadById(fileId);
    }


    /**
     * 保存文件
     * @param file
     * @return
     */
    public File saveFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.save(file);
        return file;
    }

    /**
     * 修改文件
     * @param file
     * @return
     */
    public File updateFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.update(file);
        return file;
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public void deleteFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.deleteFile(file.getId());
    }

    /**
     * 删除文件
     * @param fileId
     */
    public void deleteFile(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        fileManager.deleteFile(fileId);
    }




    /**
     * 根据ID查找
     * @param fileIds 文件ID集合
     * @return
     */
    public List<File> findFilesByIds(List<String> fileIds){
        Validate.notEmpty(fileIds, "参数[fileIds]不能为null.");
        if(Collections3.isNotEmpty(fileIds)){
            return fileManager.findFilesByIds(fileIds);
        }else{
        	return null;
        }
    }


    /**
     * 文件上传
     * @param sessionInfo
     * @param folder
     * @param uploadFile
     * @throws Exception
     */
	public File fileUpload(SessionInfo sessionInfo, Folder folder,
			MultipartFile uploadFile) throws Exception {
		File file = null;
/*		Exception exception = null;
*/
        java.io.File tempFile = null;
        try {
			String relativeDir = DiskUtils.getRelativePath(folder, sessionInfo.getUserId());
			String fullName = uploadFile.getOriginalFilename();
			String code = FileUploadUtils.encodingFilenamePrefix(sessionInfo.getUserId().toString(),
					fullName);
            String storePath = iFileManager.getStorePath(folder,sessionInfo.getUserId(),uploadFile.getOriginalFilename());


            String fileTemp = AppConstants.getDiskTempDir() + java.io.File.separator + code;
            tempFile = new java.io.File(fileTemp);
            FileOutputStream fos = FileUtils.openOutputStream(tempFile);
            IOUtils.copy(uploadFile.getInputStream(), fos);

            iFileManager.saveFile(storePath,fileTemp,false);
			file = new File();
			file.setFolder(folder);
			file.setCode(code);
			file.setUserId(sessionInfo.getUserId());
			file.setName(fullName);
			file.setFilePath(storePath);
			file.setFileSize(uploadFile.getSize());
			file.setFileSuffix(FilenameUtils.getExtension(fullName));
			fileManager.save(file);
		}catch (Exception e) {
            // exception = e;
            throw new ServiceException(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage(), e);
        } finally {
//			if (exception != null && file != null) {
//				DiskUtils.deleteFile(null, file.getId());
//			}
            if(tempFile != null && tempFile.exists()){
                tempFile.delete();
            }

		}
		return file;

	}

}
