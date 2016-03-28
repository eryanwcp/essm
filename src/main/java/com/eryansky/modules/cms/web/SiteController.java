/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.web;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.cms.CookieUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 站点Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/site")
public class SiteController extends SimpleController {

	@Autowired
	private SiteService siteService;

    @ModelAttribute
	public Site get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return siteService.get(id);
		}else{
			return new Site();
		}
	}

	@RequiresPermissions("cms:site:view")
	@RequestMapping(value = {"list", ""})
	public String list(Site site, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Site> page = siteService.findPage(new Page<Site>(request, response), site);
        model.addAttribute("page", page);
		return "modules/cms/siteList";
	}

	@RequiresPermissions("cms:site:view")
	@RequestMapping(value = "form")
	public String form(Site site, Model model) {
		model.addAttribute("site", site);
		return "modules/cms/siteForm";
	}

	@RequiresPermissions("cms:site:edit")
	@RequestMapping(value = "_save")
	public String save(Site site, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, site)){
			return form(site, model);
		}
		siteService.save(site);
		addMessage(redirectAttributes, "保存站点'" + site.getName() + "'成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/site/?repage";
	}
	
	@RequiresPermissions("cms:site:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {

		if (Site.isDefault(id)){
			addMessage(redirectAttributes, "删除站点失败, 不允许删除默认站点");
		}else{
			siteService.delete(new Site(id), isRe);
			addMessage(redirectAttributes, (isRe!=null&&isRe?"恢复":"")+"删除站点成功");
		}
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/site/?repage";
	}
	
	/**
	 * 选择站点
	 * @param id
	 * @return
	 */
	@RequiresPermissions("cms:site:select")
	@RequestMapping(value = "select")
	public String select(String id, boolean flag, HttpServletResponse response){
		if (id!=null){
			CmsUtils.putCache("siteId", id);
			// 保存到Cookie中，下次登录后自动切换到该站点
			CookieUtils.setCookie(response, "siteId", id);
		}
		if (flag){
			return "redirect:"+ AppConstants.getAdminPath();
		}
		return "modules/cms/siteSelect";
	}
}
