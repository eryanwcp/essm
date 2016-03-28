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
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.cms.Word2Html;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.ArticleDataService;
import com.eryansky.modules.cms.service.ArticleService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 文章Controller
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/article")
public class ArticleController extends SimpleController {

	@Autowired
	private ArticleService articleService;
	@Autowired
	private ArticleDataService articleDataService;
	@Autowired
	private CategoryService categoryService;
    @Autowired
   	private FileTplService fileTplService;
    @Autowired
   	private SiteService siteService;
	
	@ModelAttribute
	public Article get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return articleService.get(id);
		}else{
			return new Article();
		}
	}
	
	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = {"list", ""})
	public String list(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
//		for (int i=0; i<10000000; i++){
//			Article a = new Article();
//			a.setCategory(new Category(article.getCategory().getId()));
//			a.setTitle("测试测试测试测试测试测试测试测试"+a.getCategory().getId());
//			a.setArticleData(new ArticleData());
//			a.getArticleData().setContent(a.getTitle());
//			articleService.save(a);
//		}
        Page<Article> page = articleService.findPage(new Page<Article>(request, response), article, true);
        model.addAttribute("page", page);
		return "modules/cms/articleList";
	}

	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = "form")
	public String form(Article article, Model model) {
        // 如果当前传参有子节点，则选择取消传参选择
		if (article.getCategory()!=null && StringUtils.isNotBlank(article.getCategory().getId())){
			Category category = categoryService.getByCode(Site.getCurrentSiteCode());
			List<Category> list = categoryService.findByParentId(article.getCategory().getId(), category.getId());
			if (list.size() > 0){
				article.setCategory(null);
			}else{
				article.setCategory(categoryService.get(article.getCategory().getId()));
			}
		}
		if(StringUtils.isNotBlank(article.getId())){
			article.setArticleData(articleDataService.get(article.getId()));
		}
        model.addAttribute("contentViewList",getTplContent());
        model.addAttribute("article_DEFAULT_TEMPLATE",Article.DEFAULT_TEMPLATE);
		model.addAttribute("article", article);
		return "modules/cms/articleForm";
	}

	@RequiresPermissions("cms:article:edit")
	@RequestMapping(value = "_save")
	public String save(Article article, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, article)){
			return form(article, model);
		}
		articleService.save(article);
		addMessage(redirectAttributes, "保存文章'" + StringUtils.abbr(article.getTitle(), 50) + "'成功");
		String categoryId = article.getCategory()!=null?article.getCategory().getId():null;
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/article/?repage&category.id="+(categoryId!=null?categoryId:"");
	}
	
	@RequiresPermissions("cms:article:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, String categoryId, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		// 如果没有审核权限，则不允许删除或发布。
		if (!SecurityUtils.isPermitted("cms:article:audit")){
			addMessage(redirectAttributes, "你没有删除或发布权限");
		}
		articleService.delete(new Article(id), isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"发布":"删除")+"文章成功");
		return "redirect:"+ AppConstants.getAdminPath()+"/cms/article/?repage&category.id="+(categoryId!=null?categoryId:"");
	}

	/**
	 * 文章选择列表
	 */
	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = "selectList")
	public String selectList(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
        list(article, request, response, model);
		return "modules/cms/articleSelectList";
	}
	
	/**
	 * 通过编号获取文章标题
	 */
	@RequiresPermissions("cms:article:view")
	@ResponseBody
	@RequestMapping(value = "findByIds")
	public String findByIds(String ids) {
		List<Object[]> list = articleService.findByIds(ids);
		return JsonMapper.nonDefaultMapper().toJson(list);
	}

    private List<String> getTplContent() {
   		List<String> tplList = fileTplService.getNameListByPrefix(siteService.getByCode(Site.getCurrentSiteCode()).getSolutionPath());
   		tplList = TplUtils.tplTrim(tplList, Article.DEFAULT_TEMPLATE, "");
   		return tplList;
   	}

    /**
     * word导入
     * @param response
     * @param multipartFile
     */
    @RequestMapping(value = "importDoc")
    public void importDoc(HttpServletResponse response,
                          @RequestParam(value = "uploadFile", required = false) MultipartFile multipartFile) {
        OutputStream outputStream = null;
        Exception exception = null;
        try {
            String html = Word2Html.convert2Html(SpringMVCHolder.getRequest(),multipartFile.getInputStream(), null);
            outputStream = response.getOutputStream();
            response.setContentType("Content-Type:text/html;charset=utf-8");
            outputStream.write(html.getBytes("utf-8"));
//                    WebUtils.renderHtml(resp, Jsoup.parse(html).outerHtml());
        } catch (TransformerException e) {
            exception = e;
            logger.error(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            exception = e;
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            exception = e;
            logger.error(e.getMessage(), e);
        }finally {
            try {
                if(exception !=null){
                    outputStream.write("正文导入失败".getBytes("utf-8"));
                }
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

}
