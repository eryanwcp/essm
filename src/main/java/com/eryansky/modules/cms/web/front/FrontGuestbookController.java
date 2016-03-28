/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.web.front;

import com.eryansky.common.orm.Page;
import com.eryansky.common.web.servlet.ValidateCodeServlet;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.cms.mapper.Guestbook;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.GuestbookService;
import com.eryansky.modules.cms.utils.CmsUtils;
import com.eryansky.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 留言板Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-15
 */
@Mobile
@Controller
@RequestMapping(value = "${frontPath}/guestbook")
public class FrontGuestbookController extends SimpleController {
	
	@Autowired
	private GuestbookService guestbookService;

	/**
	 * 留言板
	 */
	@RequestMapping(value = "", method= RequestMethod.GET)
	public String guestbook(@RequestParam(required=false, defaultValue="1") Integer pageNo,
			@RequestParam(required=false, defaultValue="30") Integer pageSize, Model model) {
		Site site = CmsUtils.getSite(Site.defaultSiteCode());
		model.addAttribute("site", site);
		
		Page<Guestbook> page = new Page<Guestbook>(pageNo, pageSize);
		Guestbook guestbook = new Guestbook();
		guestbook.setStatus(Guestbook.STATUS_NORMAL);
		page = guestbookService.findPage(page, guestbook);
		model.addAttribute("page", page);
		return "modules/cms/front/themes/"+site.getTheme()+"/frontGuestbook";
	}
	
	/**
	 * 留言板-保存留言信息
	 */
	@RequestMapping(value = "", method= RequestMethod.POST)
	public String guestbookSave(Guestbook guestbook, String validateCode, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		if (StringUtils.isNotBlank(validateCode)){
			if (ValidateCodeServlet.validate(request, validateCode)){
				guestbook.setIp(request.getRemoteAddr());
				guestbook.setCreateTime(new Date());
				guestbook.setStatus(Guestbook.STATUS_AUDIT);
				guestbookService.save(guestbook);
				addMessage(redirectAttributes, "提交成功，谢谢！");
			}else{
				addMessage(redirectAttributes, "验证码不正确。");
			}
		}else{
			addMessage(redirectAttributes, "验证码不能为空。");
		}
		return "redirect:"+ AppConstants.getFrontPath()+"/guestbook?type="+guestbook.getType();
	}

}
