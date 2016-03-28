/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.web;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Guestbook;
import com.eryansky.modules.cms.service.GuestbookService;
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
import java.util.Date;

/**
 * 留言Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/guestbook")
public class GuestbookController extends SimpleController {

	@Autowired
	private GuestbookService guestbookService;
	
	@ModelAttribute
	public Guestbook get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return guestbookService.get(id);
		}else{
			return new Guestbook();
		}
	}
	
	@RequiresPermissions("cms:guestbook:view")
	@RequestMapping(value = {"list", ""})
	public String list(Guestbook guestbook, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Guestbook> page = guestbookService.findPage(new Page<Guestbook>(request, response), guestbook);
        model.addAttribute("page", page);
		return "modules/cms/guestbookList";
	}

	@RequiresPermissions("cms:guestbook:view")
	@RequestMapping(value = "form")
	public String form(Guestbook guestbook, Model model) {
		model.addAttribute("guestbook", guestbook);
		return "modules/cms/guestbookForm";
	}

	@RequiresPermissions("cms:guestbook:edit")
	@RequestMapping(value = "_save")
	public String save(Guestbook guestbook, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, guestbook)){
			return form(guestbook, model);
		}
		if (guestbook.getReUser() == null){
			guestbook.setReUser(SecurityUtils.getCurrentUser());
			guestbook.setReDate(new Date());
		}
		guestbookService.save(guestbook);
        //TODO
//		addMessage(redirectAttributes, DictUtils.getDictLabel(guestbook.getDelFlag(), "cms_del_flag", "保存")
//				+"留言'" + guestbook.getName() + "'成功");
		addMessage(redirectAttributes, guestbook.getStatusView()
				+"留言'" + guestbook.getName() + "'成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/guestbook/?repage&status=2";
	}
	
	@RequiresPermissions("cms:guestbook:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		guestbookService.delete(new Guestbook(id), isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"恢复审核":"删除")+"留言成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/guestbook/?repage&status=2";
	}

}
