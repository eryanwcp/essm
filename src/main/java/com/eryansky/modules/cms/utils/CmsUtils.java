/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.cms.utils;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.cms.mapper.Link;
import com.eryansky.modules.cms.mapper.Site;
import com.eryansky.modules.cms.service.ArticleService;
import com.eryansky.modules.cms.service.CategoryService;
import com.eryansky.modules.cms.service.LinkService;
import com.eryansky.modules.cms.service.SiteService;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.CacheUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-16 19:51
 */
public class CmsUtils {


    private static SiteService siteService = SpringContextHolder.getBean(SiteService.class);
    private static CategoryService categoryService = SpringContextHolder.getBean(CategoryService.class);
    private static ArticleService articleService = SpringContextHolder.getBean(ArticleService.class);
    private static LinkService linkService = SpringContextHolder.getBean(LinkService.class);
    private static ResourceManager resourceManager = SpringContextHolder.getBean(ResourceManager.class);
    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);

    private static final String CMS_CACHE = "cmsCache";
    public static final String SITE_PREFIX = "SITE_";
    public static final String CATEGORY_PREFIX = "CATEGORY_";
    /**
     * 获得站点列表
     */
    public static List<Site> getSiteList(){
        @SuppressWarnings("unchecked")
        List<Site> siteList = (List<Site>) CacheUtils.get(CMS_CACHE, "siteList");
        if (siteList == null){
            Page<Site> page = new Page<Site>(1, -1);
            page = siteService.findPage(page, new Site());
            siteList = page.getResult();
            CacheUtils.put(CMS_CACHE, "siteList", siteList);
        }
        return siteList;
    }

    /**
     * 获得站点信息
     * @param siteCode 站点编号
     */
    public static Site getSite(String siteCode){
        String code = "1";
        if (StringUtils.isNotBlank(siteCode)){
            code = siteCode;
        }
        for (Site site : getSiteList()){
            if (site.getCode().equals(code)){
                return site;
            }
        }
        return null;
    }


    /**
     * 获得主导航列表
     * @param siteCode 站点编号
     */
    public static List<Category> getMainNavList(String siteCode){
        @SuppressWarnings("unchecked")
        List<Category> mainNavList = (List<Category>) CacheUtils.get(CMS_CACHE, "mainNavList_" + siteCode);
        if (Collections3.isEmpty(mainNavList)){
            Category category = new Category();
            category.setModule(null);
            Site site = siteService.getByCode(siteCode);
            category.setSite(site);
            category.setParent(new Category("1"));
            category.setInMenu(Category.SHOW);
            Page<Category> page = new Page<Category>(1, -1);
            page = categoryService.find(page, category);
            mainNavList = page.getResult();
            CacheUtils.put(CMS_CACHE, "mainNavList_" + siteCode, mainNavList);
        }
        return mainNavList;
    }


    /**
     * 获取栏目
     * @param categoryCode 栏目编号
     * @return
     */
    public static Category getCategory(String categoryCode){
        return categoryService.getByCode(categoryCode);
    }

    /**
     * 获得栏目列表
     * @param siteCode 站点编号
     * @param parentId 分类父编号
     * @param number 获取数目 -1表示不分页
     * @param param  预留参数，例： key1:'value1', key2:'value2' ...
     *               module	栏目模型（article：文章；picture：图片；download：下载；link：链接；special：专题）
     *               deviceConfig	显示设备参数，多选（1：PC端是否显示；2：移动端是否显示；）
     */
    public static List<Category> getCategoryList(String siteCode, String parentId, int number, String param){
        Page<Category> page = new Page<Category>(1, number, -1);
        Category category = new Category();
        category.setModule(null);
        Site site = siteService.getByCode(siteCode);
        category.setSite(site);
        category.setParent(new Category(parentId));
        if (StringUtils.isNotBlank(param)){
            Map map = JsonMapper.getInstance().fromJson("{"+param+"}", Map.class);
            String deviceConfig = (String)map.get("deviceConfig");
            if(StringUtils.isNotBlank(deviceConfig)){
                category.setDeviceConfig(deviceConfig);
            }
            String module = (String)map.get("module");
            if(StringUtils.isNotBlank(module)){
                category.setModule(module);
            }
        }
        page = categoryService.find(page, category);
        return page.getResult();
    }

    /**
     * 获取栏目
     * @param categoryIds 栏目编号
     * @return
     */
    public static List<Category> getCategoryListByIds(String categoryIds){
        return categoryService.findByIds(categoryIds);
    }

    /**
     * 获取文章
     * @param articleId 文章编号
     * @return
     */
    public static Article getArticle(String articleId){
        return articleService.get(articleId);
    }

    /**
     * 获取文章列表
     * @param siteCode 站点编号
     * @param categoryCode 分类编号
     * @param number 获取数目
     * @param param  预留参数，例： key1:'value1', key2:'value2' ...
     * 			posid	推荐位（1：首页焦点图；2：栏目页文章推荐；3：首页推荐）
     * 			deviceConfig	显示设备参数，多选（1：PC端是否显示；2：移动端是否显示；）
     * 			image	文章图片（1：有图片的文章）
     *          orderBy 排序字符串
     * @return
     */
    public static List<Article> getArticleList(String siteCode, String categoryCode, int number, String param){
        Page<Article> page = new Page<Article>(1, number, -1);
        Category category = null;
        if(StringUtils.isNotBlank(categoryCode)){
            category = categoryService.getByCode(categoryCode);
        }
        Article article = new Article(category);
        if (StringUtils.isNotBlank(param)){
            @SuppressWarnings({ "rawtypes" })
            Map map = JsonMapper.getInstance().fromJson("{"+param+"}", Map.class);
            String deviceConfig = (String)map.get("deviceConfig");
            if(StringUtils.isNotBlank(deviceConfig)){
                if(category == null){
                    category = new Category();
                    category.setDeviceConfig(deviceConfig);
                    article.setCategory(category);
                }
            }

            Integer posid = (Integer)map.get("posid");
            if (posid != null){
                article.setPosid(String.valueOf(posid));
            }
            if (new Integer(1).equals(map.get("image"))){
                article.setImage(Article.YES);
            }
            if (StringUtils.isNotBlank((String) map.get("orderBy"))){
                page.setOrderBy((String)map.get("orderBy"));
            }
        }
        article.setStatus(Article.STATUS_NORMAL);
        page = articleService.findPage(page, article, false);
        return page.getResult();
    }
    
    /**
     * 获取文章列表
     * @param siteCode 站点编号
     * @param categoryCode 分类编号
     * @param number 获取数目
     * @param param  预留参数，例： key1:'value1', key2:'value2' ...
     * 			posid	推荐位（1：首页焦点图；2：栏目页文章推荐；3：首页推荐）
     * 			deviceConfig	显示设备参数，多选（1：PC端是否显示；2：移动端是否显示；）
     * 			image	文章图片（1：有图片的文章）
     *          orderBy 排序字符串
     * @return
     */
    public static Page<Article> getArticlePage(String siteCode, String categoryCode, String param){
        Page<Article> page = new Page<Article>(1,10);
        Category category = null;
        if(StringUtils.isNotBlank(categoryCode)){
            category = categoryService.getByCode(categoryCode);
        }
        Article article = new Article(category);
        if (StringUtils.isNotBlank(param)){
            @SuppressWarnings({ "rawtypes" })
            Map map = JsonMapper.getInstance().fromJson("{"+param+"}", Map.class);
            String deviceConfig = (String)map.get("deviceConfig");
            if(StringUtils.isNotBlank(deviceConfig)){
                if(category == null){
                    category = new Category();
                    category.setDeviceConfig(deviceConfig);
                    article.setCategory(category);
                }
            }

            Integer posid = (Integer)map.get("posid");
            if (posid != null){
                article.setPosid(String.valueOf(posid));
            }
            if (new Integer(1).equals(map.get("image"))){
                article.setImage(Article.YES);
            }
            if (StringUtils.isNotBlank((String) map.get("orderBy"))){
                page.setOrderBy((String)map.get("orderBy"));
            }
        }
        article.setStatus(Article.STATUS_NORMAL);
        page = articleService.findPage(page, article, false);
        return page;
    }    

    /**
     * 获取链接
     * @param linkId 文章编号
     * @return
     */
    public static Link getLink(String linkId){
        return linkService.get(linkId);
    }

    /**
     * 获取链接列表
     * @param siteCode 站点编号
     * @param categoryCode 分类编号
     * @param number 获取数目
     * @param param  预留参数，例： key1:'value1', key2:'value2' ...
     * @return
     */
    public static List<Link> getLinkList(String siteCode, String categoryCode, int number, String param){
        Page<Link> page = new Page<Link>(1, number, -1);
        Category category = categoryService.getByCode(categoryCode);
        Link link = new Link(category);
        if (StringUtils.isNotBlank(param)){
            @SuppressWarnings({ "unused", "rawtypes" })
            Map map = JsonMapper.getInstance().fromJson("{"+param+"}", Map.class);
        }
        link.setStatus(Link.STATUS_NORMAL);
        page = linkService.findPage(page, link, false);
        return page.getResult();
    }


    // ============== Cms Cache ==============

    public static Object getCache(String key) {
        return CacheUtils.get(CMS_CACHE, key);
    }

    public static void putCache(String key, Object value) {
        CacheUtils.put(CMS_CACHE, key, value);
    }

    public static void removeCache(String key) {
        CacheUtils.remove(CMS_CACHE, key);
    }



    /**
     * 获得文章动态URL地址
     * @param article
     * @return url
     */
    public static String getUrlDynamic(Article article) {
        if(StringUtils.isNotBlank(article.getLink())){
            return article.getLink();
        }
        StringBuilder str = new StringBuilder();
        str.append(SpringMVCHolder.getRequest().getContextPath()).append(AppConstants.getFrontPath());
        str.append("/view-").append(article.getCategory().getCode()).append("-").append(article.getId()).append(AppConstants.getUrlSuffix());
        return str.toString();
    }

    /**
     * 获得栏目动态URL地址
     * @param category
     * @return url
     */
    public static String getUrlDynamic(Category category) {
        if(StringUtils.isNotBlank(category.getHref())){
            if(!category.getHref().contains("://")){
                return SpringMVCHolder.getRequest().getContextPath()+ AppConstants.getFrontPath()+category.getHref();
            }else{
                return category.getHref();
            }
        }
        StringBuilder str = new StringBuilder();
        str.append(SpringMVCHolder.getRequest().getContextPath()).append(AppConstants.getFrontPath());
        str.append("/list-").append(category.getCode()).append(AppConstants.getUrlSuffix());
        return str.toString();
    }

    /**
     * 从图片地址中去除ContextPath地址
     * @param src
     * @return src
     */
    public static String formatImageSrcToDb(String src) {
        if(StringUtils.isBlank(src)) return src;
        if(src.startsWith(SpringMVCHolder.getRequest().getContextPath() + "/userfiles")){
            return src.substring(SpringMVCHolder.getRequest().getContextPath().length());
        }else{
            return src;
        }
    }

    /**
     * 从图片地址中加入ContextPath地址
     * @param src
     * @return src
     */
    public static String formatImageSrcToWeb(String src) {
        if(StringUtils.isBlank(src)) return src;
        if(src.startsWith(SpringMVCHolder.getRequest().getContextPath() + "/userfiles")){
            return src;
        }else{
            return SpringMVCHolder.getRequest().getContextPath()+src;
        }
    }

    public static void addViewConfigAttribute(Model model, String param){
        if(StringUtils.isNotBlank(param)){
            @SuppressWarnings("rawtypes")
            Map map = JsonMapper.getInstance().fromJson(param, Map.class);
            if(map != null){
                for(Object o : map.keySet()){
                    model.addAttribute("viewConfig_"+o.toString(), map.get(o));
                }
            }
        }
    }

    public static void addViewConfigAttribute(Model model, Category category){
        List<Category> categoryList = Lists.newArrayList();
        Category c = category;
        boolean goon = true;
        do{
            if(c.getParent() == null || c.getParent().isRoot()){
                goon = false;
            }
            categoryList.add(c);
            c = c.getParent();
        }while(goon);
        Collections.reverse(categoryList);
        for(Category ca : categoryList){
            addViewConfigAttribute(model, ca.getViewConfig());
        }
    }


    /**
     * 判断栏目是否需要授权
     *
     * @param categoryId
     * @return
     */
    public static Boolean categoryNeedAuthorize(String categoryId) {
        Resource resource = resourceManager.iGetReource(ResourceType.CMS.getValue(),CATEGORY_PREFIX + categoryId, StatusState.NORMAL.getValue());
        return resource == null ? false : true;
    }

    /**
     * 是否有某个栏目的权限
     *
     * @param categoryId
     * @return
     */
    public static Boolean hasCategory(String categoryId) {
        if (!categoryNeedAuthorize(categoryId)) {
            return true;
        }
		/*子孙栏目如果一个不需要权限，则父级栏目不需要权限访问*/
        Category category = categoryService.get(categoryId);
        List<Category> children = categoryService.findByParentIdsLike("%," + categoryId + ",%");
        if (CollectionUtils.isNotEmpty(children)) {
            for (Category c : children) {
                if (!categoryNeedAuthorize(c.getId())) {
                    return true;
                }
            }
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            return SecurityUtils.isPermitted(CATEGORY_PREFIX + categoryId);
        }
        return false;
    }

    /**
     * 判断站点是否需要授权
     *
     * @param siteId
     * @return
     */
    public static Boolean siteNeedAuthorize(String siteId) {
        Resource resource = resourceManager.iGetReource(ResourceType.CMS.getValue(), SITE_PREFIX + siteId, StatusState.NORMAL.getValue());
        return resource == null ? false : true;
    }

    /**
     * 是否有某个站点的权限
     *
     * @param siteId
     * @return
     */
    public static Boolean hasSite(String siteId) {
        if (!siteNeedAuthorize(siteId)) {
            return true;
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            return SecurityUtils.isPermitted(SITE_PREFIX + siteId);
        }
        return false;
    }

    /**
     * 发送局长邮箱
     *
     * @param title 标题
     * @param content 邮件内容
     * @return
     */
    public static void sendToDirectorEmail(String title,String content) {
        String directorLoginName = AppConstants.getConfigValue("cms.director.loginName");
        User user = userManager.getUserByLoginName(directorLoginName);
        if(user == null){
            throw new SystemException("未配置局长邮箱["+directorLoginName+"].");
        }
        EmailUtils.sendAnonymousEmail(title, content, user.getId());
    }
}
