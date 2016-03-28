/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.web;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.FileTplService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.utils.AppConstants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 站点Controller
 * @author SongLai
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/template")
public class TemplateController extends SimpleController {

    @Autowired
   	private FileTplService fileTplService;
    @Autowired
   	private SiteService siteService;

    @RequiresPermissions("cms:template:edit")
   	@RequestMapping(value = "")
   	public String index() {
   		return "modules/cms/tplIndex";
   	}

    @RequiresPermissions("cms:template:edit")
   	@RequestMapping(value = "tree")
   	public String tree(Model model) {
        Site site = siteService.getByCode(Site.getCurrentSiteCode());
   		model.addAttribute("templateList", fileTplService.getListForEdit(site.getSolutionPath()));
   		return "modules/cms/tplTree";
   	}

    @RequiresPermissions("cms:template:edit")
   	@RequestMapping(value = "form")
   	public String form(String name, Model model) {
        model.addAttribute("template", fileTplService.getFileTpl(name));
   		return "modules/cms/tplForm";
   	}

    @RequiresPermissions("cms:template:edit")
   	@RequestMapping(value = "help")
   	public String help() {
   		return "modules/cms/tplHelp";
   	}

    @RequestMapping(value = "save")
    public String save(@RequestParam(value = "name", required = true)String name,
                       @RequestParam(value = "filename", required = true)String filename,
                       String source, Model model,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest request){
        String path = StringUtils.substringBeforeLast(name, "/");
        String oldFileName = StringUtils.substringAfterLast(name,"/");
        File file = new File(name);
        if(!oldFileName.equals(filename)){
            file.delete();
            file = new File(path+"/"+filename);
        }

        try {
            FileUtils.writeStringToFile(file, source, "utf-8");
            addMessage(redirectAttributes, "保存文件'" + filename + "'成功");
        } catch (IOException e) {
            addMessage(redirectAttributes, "保存文件'" + filename + "'失败");
        }
        Site site = siteService.getByCode(Site.getCurrentSiteCode());
        return "redirect:" + AppConstants.getAdminPath() + "/cms/template/form?name="+site.getSolutionPath()+"/"+filename;
    }
}
