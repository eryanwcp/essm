/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.modules.notice._enum.ReceiveObjectType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.DiskManager;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReceiveScope;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.notice.service.NoticeSendInfoService;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.notice.task.NoticeAsyncTaskService;
import com.eryansky.modules.notice.utils.NoticeUtils;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.SelectType;
import com.eryansky.utils.YesOrNo;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 通知管理
 */
@Controller
@RequestMapping(value = "${adminPath}/notice")
public class NoticeController extends SimpleController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private DiskManager diskManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private NoticeReceiveInfoService noticeReceiveInfoService;
    @Autowired
    private NoticeSendInfoService noticeSendInfoService;
    @Autowired
    private NoticeAsyncTaskService noticeAsyncTaskService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private OrganManager organManager;

    /**
     * 操作类型
     */
    public enum OperateType{
        Save,Publish,RePublish,Repeat//保存、发送、重新发布、转发
    }

    @ModelAttribute
    public Notice get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return noticeService.get(id);
        }else{
            return new Notice();
        }
    }

    /**
     * 通知发布（通知管理）
     * @param noticeId 通知ID
     * @return
     */
    @Mobile
    @RequestMapping(value = { ""})
    public ModelAndView list(String noticeId) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice");
        modelAndView.addObject("noticeId",noticeId);
        return modelAndView;
    }


    /**
     * 发布通知列表
     * @param noticeQueryVo {@link com.eryansky.modules.notice.vo.NoticeQueryVo} 查询条件
     * @return
     */
    @RequestMapping(value = { "datagrid" })
    @ResponseBody
    public String datagrid(NoticeQueryVo noticeQueryVo) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Page<Notice> page = new Page<Notice>(SpringMVCHolder.getRequest());
        String userId = sessionInfo.getUserId();// 发布人ID
        if (NoticeUtils.isNoticeAdmin(userId)) {
            userId = null;// 管理员 查询所有
        }
        noticeQueryVo.syncEndTime();
        page = noticeService.findPage(page, new Notice(), userId, noticeQueryVo);
        Datagrid<Notice> dg = new Datagrid<Notice>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg);
        return json;
    }



    /**
     * 查看通知读取情况
     * @param id 通知ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "readInfo/{id}" })
    public ModelAndView readInfo(@PathVariable String id)
            throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-readInfo");
        modelAndView.addObject("noticeId",id);
        return modelAndView;
    }

    /**
     * 通知阅读情况
     * @param id 通知ID
     * @return
     */
    @RequestMapping(value = { "readInfoDatagrid/{id}" })
    @ResponseBody
    public String readInfoDatagrid(@PathVariable String id) {
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(SpringMVCHolder.getRequest());
        NoticeReceiveInfo entity = new NoticeReceiveInfo();
        entity.setNoticeId(id);
        page = noticeReceiveInfoService.findNoticeReceiveInfos(page, entity);
        Datagrid<NoticeReceiveInfo> dg = new Datagrid<NoticeReceiveInfo>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,NoticeReceiveInfo.class,
                new String[]{"id","userName","organName","isReadView"});
        return json;
    }


    /**
     * 查看通知
     * @param id 通知ID
     * @return
     * @throws Exception
     */
    @Mobile
    @RequestMapping(value = { "view/{id}" })
    public ModelAndView view(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-view");
        List<com.eryansky.modules.disk.entity.File> files = null;
        Notice model = noticeService.get(id);
        if(Collections3.isNotEmpty(model.getFileIds())){
            files = diskManager.findFilesByIds(model.getFileIds());
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            NoticeReceiveInfo receiveInfo = noticeReceiveInfoService.getUserNotice(sessionInfo.getUserId(), model.getId());
            if(receiveInfo != null){
                receiveInfo.setIsRead(YesOrNo.YES.getValue());
                receiveInfo.setReadTime(Calendar.getInstance().getTime());
                noticeReceiveInfoService.save(receiveInfo);
            }
        }
        modelAndView.addObject("files", files);
        modelAndView.addObject("model", model);
        return modelAndView;
    }


    /**
     * @param notice
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "input" })
    public ModelAndView input(@ModelAttribute Notice notice,OperateType operateType) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-input");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String loginUserId = sessionInfo.getUserId();
        List<com.eryansky.modules.disk.entity.File> files = null;
        if (OperateType.Repeat.equals(operateType) ) {// 转发
            List<String> newFileIds = Lists.newArrayList();
            if (Collections3.isNotEmpty(notice.getFileIds())) {// 文件拷贝
                List<File> sourceFiles = diskManager.findFilesByIds(notice.getFileIds());
                List<File> newFiles = new ArrayList<File>(sourceFiles.size());
                newFileIds = Lists.newArrayList();
                for (File sourceFile : sourceFiles) {
                    File file = sourceFile.copy();
                    file.setStatus(StatusState.LOCK.getValue());
                    file.setFolder(DiskUtils.getUserNoticeFolder(loginUserId));
                    file.setUserId(loginUserId);
                    diskManager.saveFile(file);
                    newFileIds.add(file.getId());
                    newFiles.add(file);
                }

                files = newFiles;
            }
            notice = notice.repeat();
            notice.setFileIds(newFileIds);
        }
        if (Collections3.isNotEmpty(notice.getFileIds())) {
            files = diskManager.findFilesByIds(notice.getFileIds());
        }



        modelAndView.addObject("files", files);
        modelAndView.addObject("effectTime", DateUtils.format(notice.getEffectTime(),Notice.DATE_TIME_SHORT_FORMAT));
        modelAndView.addObject("operateType", operateType);
        modelAndView.addObject("model", notice);
        return modelAndView;
    }


    /**
     * @param query 关键字
     * @param includeIds 包含的ID
     * @param dataScope {@link DataScope}
     * @return
     */
    @RequestMapping(value = {"multiSelectPrefix"})
    @ResponseBody
    public String multiSelectPrefix(String query,String dataScope,
                                    @RequestParam(value = "includeIds", required = false)List<String> includeIds) {
        List<User> list = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if((StringUtils.isNotBlank(dataScope)  && dataScope.equals(DataScope.COMPANY.getValue()))){
            User user = userManager.loadById(sessionInfo.getUserId());
            String organId = user.getCompanyId();
            List<String> organIds = organManager.findOrganChildsDepartmentOrganIds(organId);
            list = userManager.findUsersByOrganIds(organIds);
        }else{
            list = userManager.findWithInclude(includeIds, query);
        }
        //系统用户



        return JsonMapper.getInstance().toJson(list,User.class,new String[]{"id","name","defaultOrganName"});
    }


    /**
     * 保存
     * @param notice
     * @param operateType {@link OperateType}
     * @param noticeUserIds
     * @param noticeOrganIds
     * @param fileIds 页面文件ID集合
     * @return
     */
    @RequestMapping(value = { "_save" })
    @ResponseBody
    public Result _save(
            @ModelAttribute("model") Notice notice,OperateType operateType,
            @RequestParam(value = "_noticeUserIds", required = false) List<String> noticeUserIds,
            @RequestParam(value = "_noticeOrganIds", required = false) List<String> noticeOrganIds,
            @RequestParam(value = "_fileIds", required = false)List<String> fileIds) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result;

        //更新文件为有效状态 上传的时候为lock状态
        if(Collections3.isNotEmpty(fileIds)){
            List<File> noticeFiles = diskManager.findFilesByIds(fileIds);
            for(File noticeFile:noticeFiles){
                noticeFile.setStatus(StatusState.NORMAL.getValue());
                diskManager.updateFile(noticeFile);
            }
        }

        List<String> oldFileIds = null;//原有文件的ID
        if(StringUtils.isNotBlank(notice.getId())){
            oldFileIds = noticeService.getFileIds(notice.getId());//原有文件的ID
        }
        List<String> newFileIds = fileIds;//当前文件的ID
        List<String> removeFileIds =  Lists.newArrayList();//删除的文件ID
        if(Collections3.isEmpty(newFileIds)){
            removeFileIds = oldFileIds;
        }else{
            if(Collections3.isNotEmpty(oldFileIds)){
                for(String oldFileId:oldFileIds){
                    if(!newFileIds.contains(oldFileId)){
                        removeFileIds.add(oldFileId);
                    }
                }
            }

        }
        //组件上移除文件
        if(Collections3.isNotEmpty(removeFileIds)){
            fileManager.deleteFolderFiles(removeFileIds);
        }
        notice.setFileIds(fileIds);

        if(notice.getUserId() == null){
            notice.setUserId(sessionInfo.getUserId());
            notice.setOrganId(sessionInfo.getLoginOrganId());
        }

        noticeService.save(notice,true);
        saveNoticeSendInfos(noticeUserIds,notice.getId(), ReceiveObjectType.User.getValue());
        saveNoticeSendInfos(noticeOrganIds,notice.getId(),ReceiveObjectType.Organ.getValue());

        if(OperateType.Publish.equals(operateType)) {
            notice.setMode(NoticeMode.Publishing.getValue());
            noticeService.save(notice);
            noticeAsyncTaskService.publish(notice.getId());
        }

        result = Result.successResult();
        return result;
    }

    private void saveNoticeSendInfos(List<String> ids, String noticeId,Integer receieveObjectType){
        if(Collections3.isNotEmpty(ids)) {
            for(String id : ids){
                NoticeSendInfo noticeSendInfo = new NoticeSendInfo();
                noticeSendInfo.setReceiveObjectType(receieveObjectType);
                noticeSendInfo.setNoticeId(noticeId);
                noticeSendInfo.setReceiveObjectId(id);
                noticeSendInfoService.save(noticeSendInfo);
            }
        }

    }


    /**
     * 标记为已读
     * @param ids
     * @return
     */
    @RequestMapping(value = { "markReaded" })
    @ResponseBody
    public Result markReaded(
            @RequestParam(value = "ids", required = false) List<String> ids) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        for (String id : ids) {
            NoticeReceiveInfo noticeReceiveInfo = noticeReceiveInfoService.getUserNotice(sessionInfo.getUserId(), id);
            noticeReceiveInfo.setIsRead(YesOrNo.YES.getValue());
            noticeReceiveInfoService.save(noticeReceiveInfo);
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 发布通知
     * @param id 通知ID
     * @return
     */
    @RequiresPermissions("notice:publish")
    @RequestMapping(value = { "publish/{id}" })
    @ResponseBody
    public Result publish(@PathVariable String id) {
        noticeService.publish(id);
        return Result.successResult();
    }

    /**
     * 终止通知
     * @param id 通知ID
     * @return
     */
    @RequestMapping(value = { "invalid/{id}" })
    @ResponseBody
    public Result invalid(@PathVariable String id) {
        Result result;
        Notice notice =noticeService.get(id);
        notice.setMode(NoticeMode.Invalidated.getValue());
        notice.setInvalidTime(Calendar.getInstance().getTime());
        noticeService.save(notice);
        result = Result.successResult();
        return result;
    }

    /**
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = { "_remove" })
    @ResponseBody
    public Result _remove(
            @RequestParam(value = "ids", required = false) List<String> ids) {
        Result result = null;
        if(Collections3.isNotEmpty(ids)){
            for(String id:ids){
                noticeService.removeNotice(id);
            }
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 文件上传
     */
    @RequestMapping(value = { "upload" })
    @ResponseBody
    public static Result upload( @RequestParam(value = "uploadFile", required = false)MultipartFile multipartFile) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Exception exception = null;
        File file = null;
        try {
            file = DiskUtils.saveNoticeFile(sessionInfo,multipartFile);
            file.setStatus(StatusState.LOCK.getValue());
            DiskUtils.updateFile(file);
            result = Result.successResult().setObj(file.getId()).setMsg("文件上传成功！");
        } catch (InvalidExtensionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (ActionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (IOException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } finally {
            if (exception != null) {
                if(file != null){
                    DiskUtils.deleteFile(file.getId());
                }
            }
        }
        return result;

    }


    /**
     * 删除附件
     * @param notice
     * @param fileId
     * @return
     */
    @RequestMapping(value = { "delUpload" })
    @ResponseBody
    public Result delUpload(@ModelAttribute("model") Notice notice,@RequestParam String fileId) {
        Result result = null;
        notice.getFileIds().remove(fileId);
        noticeService.save(notice);
        DiskUtils.deleteFile(fileId);

        result = Result.successResult();
        return result;
    }

    /**
     * 是否置顶 下拉列表
     * @param selectType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "isTopCombobox" })
    @ResponseBody
    public List<Combobox> IsTopCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        IsTop[] _emums = IsTop.values();
        for (IsTop column : _emums) {
            Combobox combobox = new Combobox(column.getValue().toString(),
                    column.getDescription());
            cList.add(combobox);
        }
        return cList;
    }



    /**
     * 我的通知
     * @return
     */
    @RequestMapping(value = { "read" })
    public ModelAndView readList() {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-read");
        return modelAndView;
    }

    /**
     * 我的通知
     * @param noticeQueryVo 查询条件
     * @return
     */
    @RequestMapping(value = { "readDatagrid" })
    @ResponseBody
    public String noticeReadDatagrid(NoticeQueryVo noticeQueryVo) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(SpringMVCHolder.getRequest());
        noticeQueryVo.syncEndTime();
        page = noticeReceiveInfoService.findReadNoticePage(page,new NoticeReceiveInfo(), sessionInfo.getUserId(),noticeQueryVo);
        Datagrid<NoticeReceiveInfo> dg = new Datagrid<NoticeReceiveInfo>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,Notice.class,
                new String[]{"id","noticeId","title","type","typeView","publishUserName","publishTime","isReadView"});
        return json;
    }

    /**
     * 通知数量
     * @return
     */
    @RequestMapping(value = { "myMessage"})
    @ResponseBody
    public Result myMessage(HttpServletRequest request,HttpServletResponse response){
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        long noticeScopes = 0;
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(request);
        page = noticeReceiveInfoService.findUserUnreadNotices(page,sessionInfo.getUserId());
        if(Collections3.isNotEmpty(page.getResult())){
            noticeScopes = page.getTotalCount();
        }
        Map<String,Long> map = Maps.newHashMap();
        map.put("noticeScopes", noticeScopes);
        result = Result.successResult().setObj(map);
        return result;
    }

    /**
     * 未读通知列表
     * @return
     */
    @RequestMapping(value = { "myUnreadNotice"})
    @ResponseBody
    public String myUnreadNotice(HttpServletRequest request,HttpServletResponse response){
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(request);
        page = noticeReceiveInfoService.findUserUnreadNotices(page,sessionInfo.getUserId());
        result = Result.successResult().setObj(page.getResult());
        String json = JsonMapper.getInstance().toJson(result, NoticeReceiveInfo.class,
                new String[]{"noticeId","title"});
        return json;
    }

    @RequestMapping(value = { "receiveScopeCombobox" })
    @ResponseBody
    public List<Combobox> receiveScopeCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        NoticeReceiveScope[] _emums = NoticeReceiveScope.values();
        for (NoticeReceiveScope column : _emums) {
            if(!SecurityUtils.isCurrentUserAdmin() && column.getValue().equals(NoticeReceiveScope.ALL.getValue())){
                continue;
            }
            Combobox combobox = new Combobox(column.getValue().toString(),
                    column.getDescription());
            cList.add(combobox);
        }
        return cList;
    }


}
