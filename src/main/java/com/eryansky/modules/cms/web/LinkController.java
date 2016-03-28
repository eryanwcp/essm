/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.cms.web;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Link;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.LinkService;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 链接Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/link")
public class LinkController extends SimpleController {

	@Autowired
	private LinkService linkService;
	@Autowired
	private CategoryService categoryService;
	
	@ModelAttribute
	public Link get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return linkService.get(id);
		}else{
			return new Link();
		}
	}
	
	@RequiresPermissions("cms:link:view")
	@RequestMapping(value = {"list", ""})
	public String list(Link link, HttpServletRequest request, HttpServletResponse response, Model model) {
//		User user = UserUtils.getUser();
//		if (!user.isAdmin() && !SecurityUtils.getSubject().isPermitted("cms:link:audit")){
//			link.setUser(user);
//		}
        Page<Link> page = linkService.findPage(new Page<Link>(request, response), link, true);
        model.addAttribute("page", page);
		return "modules/cms/linkList";
	}

	@RequiresPermissions("cms:link:view")
	@RequestMapping(value = "form")
	public String form(Link link, Model model) {
		// 如果当前传参有子节点，则选择取消传参选择
		if (link.getCategory()!=null && StringUtils.isNotBlank(link.getCategory().getId())){
			List<Category> list = categoryService.findByParentId(link.getCategory().getId(), Site.getCurrentSiteCode());
			if (list.size() > 0){
				link.setCategory(null);
			}
		}
		model.addAttribute("link", link);
		return "modules/cms/linkForm";
	}

	@RequiresPermissions("cms:link:edit")
	@RequestMapping(value = "_save")
	public String save(Link link, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, link)){
			return form(link, model);
		}
		linkService.save(link);
		addMessage(redirectAttributes, "保存链接'" + StringUtils.abbr(link.getTitle(), 50) + "'成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/link/?repage&category.id="+link.getCategory().getId();
	}
	
	@RequiresPermissions("cms:link:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, Long categoryId, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		linkService.delete(new Link(id), isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"发布":"删除")+"链接成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/link/?repage&category.id="+categoryId;
	}

	/**
	 * 链接选择列表
	 */
	@RequiresPermissions("cms:link:view")
	@RequestMapping(value = "selectList")
	public String selectList(Link link, HttpServletRequest request, HttpServletResponse response, Model model) {
        list(link, request, response, model);
		return "modules/cms/linkSelectList";
	}
	
	/**
	 * 通过编号获取链接名称
	 */
	@RequiresPermissions("cms:link:view")
	@ResponseBody
	@RequestMapping(value = "findByIds")
	public String findByIds(String ids) {
		List<Object[]> list = linkService.findByIds(ids);
		return JsonMapper.getInstance().toJson(list);
	}

}
