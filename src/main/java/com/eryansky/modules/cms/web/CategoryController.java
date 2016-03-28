/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.cms.web;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.FileTplService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.cms.utils.TplUtils;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 栏目Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-4-21
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/category")
public class CategoryController extends SimpleController {

	@Autowired
	private CategoryService categoryService;
    @Autowired
   	private FileTplService fileTplService;
    @Autowired
   	private SiteService siteService;
	
	@ModelAttribute("category")
	public Category get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return categoryService.get(id);
		}else{
			return new Category();
		}
	}

	@RequiresPermissions("cms:category:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		List<Category> list = Lists.newArrayList();
		List<Category> sourcelist = categoryService.findByUser(true, null);
		Category.sortList(list, sourcelist, "1");
        model.addAttribute("list", list);
		return "modules/cms/categoryList";
	}

	@RequiresPermissions("cms:category:view")
	@RequestMapping(value = "form")
	public String form(Category category, Model model) {
		if (category.getParent()==null||category.getParent().getId()==null){
			category.setParent(new Category("1"));
		}
		category.setParent(categoryService.get(category.getParent().getId()));
		if (category.getOrganId() != null){
            if(category.getParent() != null){
			    category.setOrganId(category.getParent().getOrganId());
            }
		}
        model.addAttribute("listViewList",getTplContent(Category.DEFAULT_TEMPLATE));
        model.addAttribute("category_DEFAULT_TEMPLATE",Category.DEFAULT_TEMPLATE);
        model.addAttribute("contentViewList",getTplContent(Article.DEFAULT_TEMPLATE));
        model.addAttribute("article_DEFAULT_TEMPLATE", Article.DEFAULT_TEMPLATE);
		model.addAttribute("category", category);
		return "modules/cms/categoryForm";
	}
	
	@RequiresPermissions("cms:category:edit")
	@RequestMapping(value = "_save")
	public String save(Category category, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, category)){
			return form(category, model);
		}
		categoryService.save(category);
		addMessage(redirectAttributes, "保存栏目'" + category.getName() + "'成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/category/";
	}
	
	@RequiresPermissions("cms:category:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		if (Category.isRoot(id)){
			addMessage(redirectAttributes, "删除栏目失败, 不允许删除顶级栏目或编号为空");
		}else{
			categoryService.delete(id);
			addMessage(redirectAttributes, "删除栏目成功");
		}
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/category/";
	}

	/**
	 * 批量修改栏目排序
	 */
	@RequiresPermissions("cms:category:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
    	int len = ids.length;
    	Category[] entitys = new Category[len];
    	for (int i = 0; i < len; i++) {
    		entitys[i] = categoryService.get(ids[i]);
    		entitys[i].setSort(sorts[i]);
    		categoryService.save(entitys[i]);
    	}
    	addMessage(redirectAttributes, "保存栏目排序成功!");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/category/";
	}
	
//	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<TreeNode> treeData(String module, @RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<TreeNode> mapList = Lists.newArrayList();
		List<Category> list = categoryService.findByUser(true, module);
		for (int i=0; i<list.size(); i++){
			Category e = list.get(i);
            if(!SecurityUtils.isPermitted(e.getId())){//栏目权限
                continue;
            }
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				TreeNode treeNode = new TreeNode(e.getId(),e.getName());
				treeNode.setpId(e.getParent()!=null?e.getParentId():"0");
				treeNode.addAttributes("module",e.getModule());
				mapList.add(treeNode);
			}
		}
		return mapList;
	}

    private List<String> getTplContent(String prefix) {
   		List<String> tplList = fileTplService.getNameListByPrefix(siteService.getByCode(Site.getCurrentSiteCode()).getSolutionPath());
   		tplList = TplUtils.tplTrim(tplList, prefix, "");
   		return tplList;
   	}

}
