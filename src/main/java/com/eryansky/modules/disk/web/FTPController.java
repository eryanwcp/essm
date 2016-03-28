/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.modules.disk.extend.FTPManager;
import com.eryansky.modules.disk.extend.IFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-06-26 
 */
@Controller
@RequestMapping(value = "${adminPath}/disk/ftp")
public class FTPController extends SimpleController {

    @Autowired
    private IFileManager iFileManager;


    /**
     * 启动FTP客户端管理工具
     * @return
     */
    @RequestMapping(value = {"init"})
    @ResponseBody
    public Result init(Boolean reconnect,Long connectTime) {
        if(iFileManager instanceof FTPManager) {
            FTPManager ftpManager = (FTPManager) iFileManager;
            if(reconnect != null){
                ftpManager.setReconnect(reconnect);
                if(connectTime != null){
                    ftpManager.setConnectTime(connectTime);
                }
            }
            ftpManager.init();
        }else{
            Result.successResult().setMsg(iFileManager.getClass().getName());
        }

        return Result.successResult();
    }

}