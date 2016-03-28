package com.eryansky.modules.sys.web.mobile;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Result;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.VersionLogService;
import com.eryansky.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;

/**
 * 手机端入口
 */
@Mobile
@Controller
@RequestMapping("${mobilePath}")
public class MobileIndexController extends SimpleController {

    @Autowired
    private VersionLogService versionLogService;
    @Autowired
    private FileManager fileManager;

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
    @Mobile(able = false)
    @RequiresUser(required = false)
    @RequestMapping("download")
    public ModelAndView download(Integer versionLogType,String versionCode){
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
        }
        if(StringUtils.isNotBlank(versionCode)){
            versionLog = versionLogService.getByVersionCode(versionLogType,versionCode);
        }else{
            versionLog = versionLogService.getLatestVersionLog(versionLogType);
        }
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
    public Result getNewVersion(@PathVariable Integer versionLogType){
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
    @Logging(logType = LogType.access,value = "APP[{2}]下载")
    @RequiresUser(required = false)
    @RequestMapping(value = { "downloadApp/{versionLogType}" })
    public ModelAndView fileDownload(HttpServletResponse response,
                                     HttpServletRequest request,
                                     String versionCode,
                                     @PathVariable Integer versionLogType) {
        VersionLog versionLog = null;
        if(StringUtils.isNotBlank(versionCode)){
            versionLog = versionLogService.getByVersionCode(versionLogType,versionCode);
        }else{
            versionLog = versionLogService.getLatestVersionLog(versionLogType);
        }
        if(versionLog != null && versionLog.getFileId() != null){
            try {
                File file = fileManager.getById(versionLog.getFileId());
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
}