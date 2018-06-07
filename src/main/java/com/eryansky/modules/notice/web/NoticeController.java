/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.service.OrganService;
import com.eryansky.modules.sys.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReceiveScope;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.notice.service.NoticeSendInfoService;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.utils.SelectType;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 通知管理
 */
@Controller
@RequestMapping(value = "${adminPath}/notice")
public class NoticeController extends SimpleController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeReceiveInfoService noticeReceiveInfoService;
    @Autowired
    private NoticeSendInfoService noticeSendInfoService;
    @Autowired
    private UserService userService;

    /**
     * 操作类型
     */
    public enum OperateType{
        Save,Publish,RePublish,Repeat//保存、发送、重新发布、转发
    }

    @ModelAttribute("model")
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
        Page<Notice> page = new Page<Notice>(SpringMVCHolder.getRequest());
//        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
//        String userId = sessionInfo.getUserId();// 发布人ID
//        if (NoticeUtils.isNoticeAdmin(userId)) {
//            userId = null;// 管理员 查询所有
//        }
        noticeQueryVo.syncEndTime();
        page = noticeService.findPage(page, new Notice(), null, noticeQueryVo);
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
    public ModelAndView readInfo(@PathVariable String id){
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
    @Mobile(value = MobileValue.ALL)
    @RequestMapping(value = { "view/{id}" })
    public ModelAndView view(@PathVariable String id){
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-view");
        List<File> files = DiskUtils.findFilesByIds(noticeService.findFileIdsByNoticeId(id));
        Notice model = noticeService.get(id);
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            NoticeReceiveInfo receiveInfo = noticeReceiveInfoService.getUserNotice(sessionInfo.getUserId(), id);
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
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "input" })
    public ModelAndView input(@ModelAttribute("model") Notice model, OperateType operateType) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/notice-input");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String loginUserId = sessionInfo.getUserId();
        List<File> files = Collections.EMPTY_LIST;
        List<String> fileIds = Collections.EMPTY_LIST;
        List<String> receiveUserIds = Collections.EMPTY_LIST;
        List<String> receiveOrganIds = Collections.EMPTY_LIST;
        if (OperateType.Repeat.equals(operateType) ) {// 转发
            List<String> newFileIds = Lists.newArrayList();
            if (Collections3.isNotEmpty(model.getFileIds())) {// 文件拷贝
                List<File> sourceFiles = DiskUtils.findFilesByIds(model.getFileIds());
                List<File> newFiles = new ArrayList<File>(sourceFiles.size());
                newFileIds = Lists.newArrayList();
                for (File sourceFile : sourceFiles) {
                    File file = sourceFile.copy();
                    file.setFolderId(DiskUtils.getSystemFolderByCode(Notice.FOLDER_NOTICE,loginUserId).getId());
                    file.setUserId(loginUserId);
                    DiskUtils.saveFile(file);
                    newFileIds.add(file.getId());
                    newFiles.add(file);
                    DiskUtils.saveFile(file);
                }

                files = newFiles;
            }
            model = model.repeat();
            fileIds = newFileIds;
            model.setFileIds(newFileIds);
        }else if(!model.getIsNewRecord()){//修改
            fileIds = noticeService.findFileIdsByNoticeId(model.getId());
            model.setFileIds(fileIds);
            files = DiskUtils.findFilesByIds(fileIds);

            receiveUserIds =  noticeSendInfoService.findUserIdsByNoticeId(model.getId());
            receiveOrganIds =  noticeSendInfoService.findorganIdsByNoticeId(model.getId());

        }
        modelAndView.addObject("files", files);
        modelAndView.addObject("fileIds", fileIds);
        modelAndView.addObject("receiveUserIds", receiveUserIds);
        modelAndView.addObject("receiveOrganIds", receiveOrganIds);
        modelAndView.addObject("operateType", operateType);
        modelAndView.addObject("model", model);
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
            list = userService.findUsersByCompanyId(sessionInfo.getLoginCompanyId());
        }else{
            list = userService.findWithInclude(includeIds, query);
        }
        return JsonMapper.getInstance().toJson(list,User.class,new String[]{"id","name","defaultOrganName"});
    }


    /**
     * 保存
     * @param model
     * @param operateType {@link OperateType}
     * @param noticeUserIds
     * @param noticeOrganIds
     * @param fileIds 页面文件ID集合
     * @return
     */
    @RequestMapping(value = { "save" })
    @ResponseBody
    public Result save(@ModelAttribute("model")Notice model, OperateType operateType,
                       @RequestParam(value = "_noticeUserIds", required = false) List<String> noticeUserIds,
                       @RequestParam(value = "_noticeOrganIds", required = false) List<String> noticeOrganIds,
                       @RequestParam(value = "fileIds", required = false)List<String> fileIds) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result;
        if(StringUtils.isBlank(model.getUserId())){
            model.setUserId(sessionInfo.getUserId());
            model.setOrganId(sessionInfo.getLoginOrganId());
        }
        noticeService.saveNoticeAndFiles(model,OperateType.Publish.equals(operateType),noticeUserIds,noticeOrganIds,fileIds);
        result = Result.successResult();
        return result;
    }


    /**
     * 标记为已读
     * @param ids
     * @return
     */
    @RequestMapping(value = { "markReaded" })
    @ResponseBody
    public Result markReaded(@RequestParam(value = "ids", required = false) List<String> ids) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        noticeReceiveInfoService.markUserNoticeReaded(sessionInfo.getUserId(),ids);
        return Result.successResult();
    }

    /**
     * 发布通知
     * @param id 通知ID
     * @return
     */
    @RequiresPermissions("model:publish")
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
        Notice notice = noticeService.get(id);
        notice.setMode(NoticeMode.Invalidated.getValue());
        notice.setInvalidTime(Calendar.getInstance().getTime());
        noticeService.save(notice);
        return Result.successResult();
    }

    /**
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = { "remove" })
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        noticeService.deleteByIds(ids);
        return Result.successResult();
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
            file = DiskUtils.saveSystemFile(Notice.FOLDER_NOTICE,sessionInfo,multipartFile);
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
     * @param model
     * @param fileId
     * @return
     */
    @RequestMapping(value = { "delUpload" })
    @ResponseBody
    public Result delUpload(@ModelAttribute("model") Notice model, @RequestParam String fileId) {
        List<String> fileIds = new ArrayList<String>(1);
        fileIds.add(fileId);
        noticeService.deleteNoticeFiles(model.getId(),fileIds);
        DiskUtils.deleteFile(fileId);
        return Result.successResult();
    }

    /**
     * 是否置顶 下拉列表
     * @param selectType
     * @return
     */
    @RequestMapping(value = { "isTopCombobox" })
    @ResponseBody
    public List<Combobox> IsTopCombobox(String selectType) {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        IsTop[] _emums = IsTop.values();
        for (IsTop column : _emums) {
            Combobox combobox = new Combobox(column.getValue(),column.getDescription());
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

    /**
     * 接收范围选择
     * @param selectType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "receiveScopeCombobox" })
    @ResponseBody
    public List<Combobox> receiveScopeCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        NoticeReceiveScope[] _emums = NoticeReceiveScope.values();
        boolean isAdmin = SecurityUtils.isCurrentUserAdmin();
        for (NoticeReceiveScope column : _emums) {
            if(!isAdmin && column.getValue().equals(NoticeReceiveScope.ALL.getValue())){
                continue;
            }
            Combobox combobox = new Combobox(column.getValue(), column.getDescription());
            cList.add(combobox);
        }
        return cList;
    }


}
