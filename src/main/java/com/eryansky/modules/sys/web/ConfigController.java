/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.modules.sys.mapper.Config;
import com.eryansky.modules.sys.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-05-14 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/config")
public class ConfigController extends SimpleController {
    @Autowired
    private ConfigService configService;


    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/config";
    }

    @ModelAttribute
    public Config get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return configService.get(id);
        }else{
            return new Config();
        }
    }
    @RequestMapping(value = {"input"})
    public String input() {
        return "modules/sys/config-input";
    }

    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public Datagrid<Config> datagrid(Config model, HttpServletRequest request, HttpServletResponse response,
                                     String query) {
        Page<Config> page = new Page<Config>(request);
        page = configService.findPage(page,query);
        Datagrid datagrid = new Datagrid(page.getTotalCount(), page.getResult());
        return datagrid;
    }


    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute Config model) {
        Result result;
        // 属性名重复校验
        Config checkConfig = configService.getConfigByCode(model.getCode());
        if (checkConfig != null && !checkConfig.getId().equals(model.getId())) {
            result = new Result(Result.WARN, "属性名为[" + model.getCode() + "]已存在,请修正!", "code");
            logger.debug(result.toString());
            return result;
        }

        configService.save(model);
        result = Result.successResult();
        return result;
    }

    /**
     * 从配置文件同步
     * @param overrideFromProperties
     * @return
     */
    @RequestMapping(value = {"syncFromProperties"})
    @ResponseBody
    public Result syncFromProperties(Boolean overrideFromProperties){
        Result result;
        configService.syncFromProperties(overrideFromProperties);
        result = Result.successResult();
        return result;
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false)List<String> ids){
        configService.deleteByIds(ids);
        return Result.successResult();
    }

}