package com.eryansky.modules.sys.web.mobile;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.VersionLogService;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import com.google.common.collect.Maps;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 手机端入口
 */
@Mobile
@Controller
@RequestMapping("${mobilePath}")
public class MobileIndexController extends SimpleController {

    @Autowired
    private VersionLogService versionLogService;

    @Logging(logType = LogType.access,value = "移动门户（网页版）")
    @RequestMapping("")
    public ModelAndView index(){
        return new ModelAndView("layout/index");
    }

    @Logging(logType = LogType.access,value = "移动门户（APP版）")
    @RequestMapping(value = {"content"})
    public ModelAndView content() {
        return new ModelAndView("layout/index_content");
    }




    /**
     * 下载页面
     * @return
     */
    @Mobile(value = MobileValue.PC)
    @RequiresUser(required = false)
    @RequestMapping("download")
    public ModelAndView download(String versionLogType,String versionCode){
        ModelAndView modelAndView = new ModelAndView("mobile/download");
        VersionLog versionLog = null;
        boolean likeIOS = AppUtils.likeIOS(UserAgentUtils.getHTTPUserAgent(SpringMVCHolder.getRequest()));
        boolean likeAndroid = AppUtils.likeAndroid(UserAgentUtils.getHTTPUserAgent(SpringMVCHolder.getRequest()));
        if(versionLogType == null){
            if(likeIOS){
                versionLogType = VersionLogType.iPhoneAPP.getValue();
            }else{
                versionLogType = VersionLogType.Android.getValue();
            }
        }else{
            if(VersionLogType.iPhoneAPP.getValue().equals(versionLogType)){
                likeIOS = true;
                likeAndroid = false;
            }
        }
        if(StringUtils.isNotBlank(versionCode)){
            versionLog = versionLogService.getByVersionCode(versionLogType,versionCode);
        }else{
            versionLog = versionLogService.getLatestVersionLog(versionLogType);
        }
        modelAndView.addObject("versionLogType",versionLogType);
        modelAndView.addObject("versionCode",versionCode);
        modelAndView.addObject("model",versionLog);
        modelAndView.addObject("likeAndroid",likeAndroid);
        modelAndView.addObject("likeIOS",likeIOS);
        return modelAndView;
    }


    /**
     * 查找更新
     */
    @RequiresUser(required = false)
    @ResponseBody
    @RequestMapping(value = {"getNewVersion/{versionLogType}"})
    public Result getNewVersion(@PathVariable String versionLogType){
        Result result = null;
        VersionLog max = versionLogService.getLatestVersionLog(versionLogType);
        result = Result.successResult().setObj(max);
        return result;
    }

    private static final String MIME_ANDROID_TYPE = "application/vnd.android.package-archive";
    /**
     * APP下载
     *
     * @param response
     * @param request
     * @param versionCode 版本号
     * @param versionLogType {@link com.eryansky.modules.sys._enum.VersionLogType}
     *            文件ID
     */
    @Mobile(value = MobileValue.PC)
    @Logging(logType = LogType.access,value = "APP[#versionCode]下载")
    @RequiresUser(required = false)
    @RequestMapping(value = { "downloadApp/{versionLogType}" })
    public ModelAndView fileDownload(HttpServletResponse response,
                                     HttpServletRequest request,
                                     String versionCode,
                                     @PathVariable String versionLogType) {
        VersionLog versionLog = null;
        if(StringUtils.isNotBlank(versionCode)){
            versionLog = versionLogService.getByVersionCode(versionLogType,versionCode);
        }else{
            versionLog = versionLogService.getLatestVersionLog(versionLogType);
        }
        if(versionLog != null && versionLog.getFileId() != null){
            try {
                File file = DiskUtils.getFile(versionLog.getFileId());
                if(VersionLogType.Android.getValue().equals(versionLogType)) {
                    response.setContentType(MIME_ANDROID_TYPE);
                }

                WebUtils.setDownloadableHeader(request, response, file.getName());
                file.getDiskFile();
                java.io.File tempFile = file.getDiskFile();
                FileCopyUtils.copy(new FileInputStream(tempFile), response.getOutputStream());
            }catch (Exception e){
                throw new ActionException(e);
            }
        }else {
            throw new ActionException("下载文件不存在！");
        }
        return null;
    }

    /**
     * 文件删除
     * @param fileId
     * @return
     */
    @RequestMapping(value = { "deleteFile" })
    @ResponseBody
    public Result deleteFile(@RequestParam(value = "fileId") String fileId) {
        DiskUtils.deleteFile(fileId);
        return  Result.successResult();
    }

    /**
     * 图片文件上传
     */
    @RequestMapping(value = { "base64ImageUpLoad" })
    @ResponseBody
    public Result base64ImageUpLoad(@RequestParam(value = "base64Data",required = false) String base64Data) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Exception exception = null;
        File file = null;
        try {

            String dataPrix = "";
            String data = "";

            if(base64Data == null || "".equals(base64Data)){
                return Result.errorResult().setMsg("上传失败，上传图片数据为空");
            }else{
                String [] d = base64Data.split("base64,");
                if(d != null && d.length == 2){
                    dataPrix = d[0];
                    data = d[1];
                }else{
                    return Result.errorResult().setMsg("上传失败，数据不合法");
                }
            }

            String suffix = "";
            if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){//data:image/jpeg;base64,base64编码的jpeg图片数据
                suffix = ".jpg";
            } else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){//data:image/x-icon;base64,base64编码的icon图片数据
                suffix = ".ico";
            } else if("data:image/gif;".equalsIgnoreCase(dataPrix)){//data:image/gif;base64,base64编码的gif图片数据
                suffix = ".gif";
            } else if("data:image/png;".equalsIgnoreCase(dataPrix)){//data:image/png;base64,base64编码的png图片数据
                suffix = ".png";
            }else{
                return Result.errorResult().setMsg("上传图片格式不合法");
            }
            String tempFileName = Identities.uuid() + suffix;

            //因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
//            byte[] bs = EncodeUtils.base64Decode(data);
            byte[] bs = Base64Utils.decodeFromString(data);

            file = DiskUtils.saveSystemFile("IMAGE",sessionInfo,new ByteArrayInputStream(bs), tempFileName);
            file.setStatus(StatusState.LOCK.getValue());
            DiskUtils.saveFile(file);
            result = Result.successResult().setObj(file).setMsg("文件上传成功！");
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
     * 图片文件上传
     */
    @RequestMapping(value = { "imageUpLoad" })
    @ResponseBody
    public Result imageUpLoad(@RequestParam(value = "uploadFile",required = false) MultipartFile multipartFile) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Exception exception = null;
        File file = null;
        try {
            file = DiskUtils.saveSystemFile("IMAGE",sessionInfo, multipartFile);
            file.setStatus(StatusState.LOCK.getValue());
            DiskUtils.saveFile(file);
            Map<String,Object> _data = Maps.newHashMap();
            String data = "data:image/jpeg;base64," + Base64Utils.encodeToString(FileCopyUtils.copyToByteArray(new FileInputStream(file.getDiskFile())));
            _data.put("file",file);
            _data.put("data",data);
            _data.put("url",AppConstants.getAdminPath()+ "/disk/fileDownload/"+file.getId());
            result = Result.successResult().setObj(_data).setMsg("文件上传成功！");
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
}