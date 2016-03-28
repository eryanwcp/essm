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
import com.eryansky.modules.cms.mapper.Comment;
import com.eryansky.modules.cms.service.CommentService;
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
 * 评论Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/comment")
public class CommentController extends SimpleController {

	@Autowired
	private CommentService commentService;
	
	@ModelAttribute
	public Comment get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return commentService.get(id);
		}else{
			return new Comment();
		}
	}
	
	@RequiresPermissions("cms:comment:view")
	@RequestMapping(value = {"list", ""})
	public String list(Comment comment, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Comment> page = commentService.findPage(new Page<Comment>(request, response), comment);
        model.addAttribute("page", page);
		return "modules/cms/commentList";
	}

	@RequiresPermissions("cms:comment:edit")
	@RequestMapping(value = "_save")
	public String save(Comment comment, RedirectAttributes redirectAttributes) {
		if (beanValidator(redirectAttributes, comment)){
			if (comment.getAuditUser() == null){
				comment.setAuditUser(SecurityUtils.getCurrentUser());
				comment.setAuditDate(new Date());
			}
			comment.setStatus(Comment.STATUS_NORMAL);
			commentService.save(comment);
//			addMessage(redirectAttributes, DictUtils.getDictLabel(comment.getDelFlag(), "cms_del_flag", "保存")
//					+"评论'" + StringUtils.abbr(StringUtils.replaceHtml(comment.getContent()),50) + "'成功");
            //TODO
            addMessage(redirectAttributes, comment.getStatusView()
                    +"评论'" + StringUtils.abbr(StringUtils.replaceHtml(comment.getContent()), 50) + "'成功");
		}
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/comment/?repage&delFlag=2";
	}
	
	@RequiresPermissions("cms:comment:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		commentService.delete(new Comment(id), isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"恢复审核":"删除")+"评论成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/comment/?repage&delFlag=2";
	}

}
