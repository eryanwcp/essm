package com.eryansky.modules.cms.web.mobile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eryansky.common.orm.Page;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.ArticleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.cms.service.ArticleService;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.utils.CmsUtils;

/**
 * 文章 手机端入口
 */
@Mobile
@Controller
@RequestMapping("${mobilePath}/cms")
public class CMSMobileController extends SimpleController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleDataService articleDataService;

    /**
     * 新闻文章
     *
     * @return
     */
    @RequiresUser(required = false)
    @RequestMapping("index-{siteCode}${urlSuffix}")
    public ModelAndView index(@PathVariable String siteCode, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAnView = new ModelAndView("modules/cms/frontIndex");
        Site site = CmsUtils.getSite(siteCode);     
        modelAnView.addObject("site", site);
        return modelAnView;
    }

    /**
     * 新闻文章
     *
     * @return
     */
    @Deprecated
    @RequestMapping("articleList")
    @ResponseBody
    public String articleList(String siteCode,String categoryCode, HttpServletRequest request, HttpServletResponse response) {
        Page<Article> page = new Page<Article>(request, response);
        Site site = CmsUtils.getSite(siteCode);
        Article article=null;
        if ("main".equals(categoryCode)) {
        	//site站点在移动设备显示的栏目
        	Category category=new Category();
        	category.setSite(site);
        	category.setDeviceConfig("2");
        	//推荐文章
            article = new Article();
            article.setCategory(category);
            article.setPosid("3");
        }else{
        	Category category = categoryService.getByCode(categoryCode);
            article = new Article(category);
        }
        article.setStatus(Article.STATUS_NORMAL);
        page = articleService.findPage(page, article, false);
        String json = JsonMapper.getInstance().toJson(page.getResult(), Article.class,
                new String[]{"id", "title", "description", "color", "thumb", "image", "updateTime"});

        return "{\"totalPages\":" + page.getTotalPages() + ",\"rows\":" + json + "}";
    }

    /**
     * 查看新闻文章
     *
     * @param articleId 文章ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"view-{articleId}${urlSuffix}"})
    @RequiresUser(required = false)
    public ModelAndView articleView(@PathVariable String articleId) {
        ModelAndView modelAndView = new ModelAndView("modules/cms/frontViewArticle");
        // 获取文章内容
        Article article = articleService.get(articleId);
        if (article==null || !Article.STATUS_NORMAL.equals(article.getStatus())){
            return new ModelAndView("error/404");
        }
        article.setArticleData(articleDataService.get(article.getId()));
        modelAndView.addObject("article",article);
        return modelAndView;
    }

}
