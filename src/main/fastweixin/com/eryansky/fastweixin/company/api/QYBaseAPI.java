package com.eryansky.fastweixin.company.api;

import com.eryansky.fastweixin.api.enums.ResultType;
import com.eryansky.fastweixin.api.response.BaseResponse;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.util.BeanUtil;
import com.eryansky.fastweixin.util.CollectionUtil;
import com.eryansky.fastweixin.util.NetWorkCenter;

import java.io.File;
import java.util.List;

/**
 * API基类，提供一些通用方法
 * 包含自动刷新token、通用get post请求等
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public abstract class QYBaseAPI {

    protected static final String BASE_API_URL = "https://qyapi.weixin.qq.com/";

    protected final QYAPIConfig config;

    /**
     * 构造方法，设置apiConfig
     *
     * @param config 微信API配置对象
     */
    protected QYBaseAPI(QYAPIConfig config) {
        this.config = config;
    }

    /**
     * 通用post请求
     *
     * @param url  地址，其中token用#代替
     * @param json 参数，json格式
     * @return 请求结果
     */
    protected BaseResponse executePost(String url, String json) {
        return executePost(url, json, null);
    }

    /**
     * 通用post请求
     *
     * @param url  地址，其中token用#代替
     * @param json 参数，json格式
     * @param file 上传的文件
     * @return 请求结果
     */
    protected BaseResponse executePost(String url, String json, File file) {
        BaseResponse response;
        BeanUtil.requireNonNull(url, "url is null");
        List<File> files = null;
        if (null != file) {
            files = CollectionUtil.newArrayList(file);
        }
        //需要传token
        String postUrl = url.replace("#", config.getAccessToken());
        response = NetWorkCenter.post(postUrl, json, files);
        return response;
    }


    /**
     * 通用get请求
     *
     * @param url 地址，其中token用#代替
     * @return 请求结果
     */
    protected BaseResponse executeGet(String url) {
        BaseResponse response;
        BeanUtil.requireNonNull(url, "url is null");
        //需要传token
        String getUrl = url.replace("#", config.getAccessToken());
        response = NetWorkCenter.get(getUrl);

        return response;
    }

    /**
     * 判断本次请求是否成功
     *
     * @param errCode 错误码
     * @return 是否成功
     */
    protected boolean isSuccess(String errCode) {
        return ResultType.SUCCESS.getCode().toString().equals(errCode);
    }
}
