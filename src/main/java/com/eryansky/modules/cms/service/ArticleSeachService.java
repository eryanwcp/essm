package com.eryansky.modules.cms.service;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.cms.dao.ArticleDao;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wltea.analyzer.lucene.IKAnalyzer;


@Aspect
@Service
@Transactional(readOnly = true)
public class ArticleSeachService extends CrudService<ArticleDao, Article> {
	@Autowired
	private ArticleDataService articleDataService;
	@Autowired
	private CategoryService categoryService;

	Directory directory = null;
	private Analyzer analyzer = new IKAnalyzer();

	public ArticleSeachService() {
//		try {
//			directory = new SimpleFSDirectory(new File(AppConstants.getCMSLuceneArticle()));
//		} catch (IOException e) {
//			logger.error(e.getMessage(),e);
//		}
	}

	/**
	 * 将检索到的数据进行分页
	 * @param page
	 * @param article
	 * @param qlist
	 * @return
	 */
	public Page<Article> find(Page<Article> page, Article article,List<Article> qlist) {
		article.setEntityPage(page);
		page.setResult(qlist);
		return page;
	}
	
	@SuppressWarnings("unused")
	public Page<Article> search(Page<Article> page, String q, String categoryId, String beginDate, String endDate){
		if(StringUtils.isBlank(q)){
			return page;
		}

		try {
			List<Article> qlist = new ArrayList<Article>();
			directory = new SimpleFSDirectory(new File(AppConstants.getCMSLuceneArticle()));
			IndexSearcher indexSearcher = new IndexSearcher(directory,true);
			logger.info(">>> 2.开始读取索引... ... 通过关键字：【 " + q + " 】");
			long beginTime = new Date().getTime();
			// ################# 搜索相似度最高的记录 ###################

			String[] fields = new String[]{"title","keywords","description","articleData.content"};

//			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);
			//生成Query对象
//			Query query = parser.parse(q);
			BooleanQuery query = new BooleanQuery();
			try {
				if (StringUtils.isNotBlank(q)) {
					for (String field : fields) {
						QueryParser parser = new QueryParser(org.apache.lucene.util.Version.LUCENE_36, field, analyzer);
						query.add(parser.parse(q), BooleanClause.Occur.SHOULD);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// 设置过滤条件
			List<BooleanClause> bcList = Lists.newArrayList();

//			bcList.add(new BooleanClause(new TermQuery(new Term(Article.STATUS_DELETE, Article.STATUS_NORMAL)), BooleanClause.Occur.MUST));
			if (StringUtils.isNotBlank(categoryId)){
				bcList.add(new BooleanClause(new TermQuery(new Term("category.ids", categoryId)), BooleanClause.Occur.MUST));
			}

			if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
				bcList.add(new BooleanClause(new TermRangeQuery("updateTime", beginDate,
						endDate, true, true), BooleanClause.Occur.MUST));
			}
			Filter filter = null;
			if(Collections3.isNotEmpty(bcList)){
				BooleanQuery queryFilter = new BooleanQuery();
				BooleanClause[] booleanClauses = bcList.toArray(new BooleanClause[bcList.size()]);
				for (BooleanClause booleanClause : booleanClauses) {
					queryFilter.add(booleanClause);
				}

				filter = new CachingWrapperFilter(new QueryWrapperFilter(queryFilter));
			}

			TopDocs topDocs = indexSearcher.search(query, filter, 1000);
			logger.info("*** 共匹配：" + topDocs.totalHits + "个 ***");
			Article article = null;
			
			
			ScoreDoc[] scoreDocs=topDocs.scoreDocs;
			if(page.getPageSize() == -1){
				page.setPageSize(Page.DEFAULT_PAGESIZE);
			}
			//查询起始记录位置
	        int begin = page.getPageNo() == 1 ? page.getPageNo()-1:((page.getPageNo()-1)*page.getPageSize());//当前页
			//查询终止记录位置
			int end = Math.min(begin + page.getPageSize(), scoreDocs.length);//page.getPageSize()：没页条数，scoreDocs.length：总条数
			for (int i = begin; i < end; i++) {
				int docId = scoreDocs[i].doc;
	        	Document document = indexSearcher.doc(docId);
	        	article = new Article();
				// 设置高亮显示格式
				SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'><strong>",
						"</strong></font>");
				/* 语法高亮显示设置 */
				Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(100));
				// 设置高亮 设置 title,content 字段
				String id = document.get("id");
				String title = document.get("title");
				String content = document.get("content");
				String keywords = document.get("keywords");
				String description = document.get("description");
				String category_id = document.get("category_id");
				String hits = document.get("hits");
				String createTime = document.get("createTime");
				String updateTime = document.get("updateTime");
				String cteateUser = document.get("cteateUser");
				TokenStream titleTokenStream = analyzer.tokenStream("title", new StringReader(title));
				String highLightTitle = highlighter.getBestFragment(titleTokenStream, title);

				String highLightDescription = null;
				if(StringUtils.isNotBlank(description)){
					TokenStream descriptionTokenStream = analyzer.tokenStream("description", new StringReader(description));
					highLightDescription = highlighter.getBestFragment(descriptionTokenStream, description);
				}

				//将检索的结果绑定到对象
				article.setId(id);
				article.setTitle(highLightTitle);
				article.setDescription(highLightDescription);
				article.setKeywords(keywords);
				article.setCategory(new Category(category_id));
				article.setHits(Integer.parseInt(hits));
				article.setCreateUser(UserUtils.getUserName(cteateUser));
				article.setCreateTime(DateUtils.parseDate(createTime));
				article.setUpdateTime(DateUtils.parseDate(updateTime));
				article.setCategory(categoryService.get(category_id))
					.setArticleData(articleDataService.get(id));

				//把对象添加到list
				qlist.add(article);
	        }


			long endTime = new Date().getTime();
			logger.info(">>> 3.搜索完毕... ... 共花费：" + (endTime - beginTime) + "毫秒...");
			indexSearcher.close();
			
			//return qlist;
			if(article==null)
				return page;
			else{
				page.setTotalCount(topDocs.totalHits);
				return find(page, article, qlist);
			}
				
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return page;
		}
	}
	

	

	@SuppressWarnings("unused")
	public boolean createIndex() {
		// 检查索引是否存在
		boolean flag = false;
		if (this.isIndexExisted()){
			flag = true;
		}

		List<Article> list = dao.findAllList(new Article());
		try {
			directory = new SimpleFSDirectory(new File(AppConstants.getCMSLuceneArticle()));
			IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_36, analyzer));
			if(flag){
				indexWriter.deleteAll();
				indexWriter.optimize();
				indexWriter.commit();
			}

			long begin = new Date().getTime();
			for (Article art : list) {
				art.setArticleData(articleDataService.get(art.getId()));
				//创建索引库的文档,添加需要存放的列
				Document doc = new Document();
				String id = art.getId() == null ? "" : art.getId().trim();
				String title = art.getTitle() == null ? "" : art.getTitle().trim();
				String keywords = art.getKeywords() == null ? "" : art.getKeywords();
				String description = art.getDescription() == null ? "" : art.getDescription();
				String content = art.getArticleData() == null ? "" : art.getArticleData().getContent();
				String category_id = art.getCategory().getId() == null ? "" : art.getCategory().getId();
				String hits = art.getHits() == null ? "0" : art.getHits()+"";
				String fmt = "yyyyMMdd";
				//需要将日期格式化
				String createTime = art.getCreateTime() == null ? "" : DateUtils.formatDateTime(art.getCreateTime());
				String updateTime = art.getUpdateTime() == null ? "" : DateUtils.formatDateTime(art.getUpdateTime());
				//这里cteateUser 为用户的ID
				String cteateUser = art.getCreateUser() ==null ? "" : art.getCreateUser();
				
				doc.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.YES));
				doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
				doc.add(new Field("keywords", keywords, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
				doc.add(new Field("description", description, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
				doc.add(new Field("articleData.content", content, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
				doc.add(new Field("category_id", category_id, Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.YES));
				doc.add(new Field("hits", hits, Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.YES));
				doc.add(new Field("createTime", createTime, Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.YES));
				doc.add(new Field("updateTime", updateTime, Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.YES));
				doc.add(new Field("cteateUser", cteateUser, Field.Store.YES, Field.Index.ANALYZED,Field.TermVector.YES));
				indexWriter.addDocument(doc);
			}
			long end = new Date().getTime();
			logger.info(">>> 1.存入索引完毕.. 共花费：" + (end - begin) + "毫秒...");

			indexWriter.optimize();
			indexWriter.commit();
			indexWriter.close();
			return true;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
	}

	/**
	 * check Index is Existed
	 * 
	 * @return true or false
	 */
	private boolean isIndexExisted() {
		try {
			File dir = new File(AppConstants.getCMSLuceneArticle());
			//if (dir.listFiles().length > 0)
			if (dir.exists())
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 切面 保存文章
	 * @param point
	 */
	@After("execution(* com.eryansky.modules.cms.service.ArticleService.save(..))")
	public void saveArticle(JoinPoint point) {
		Object[] objects = point.getArgs();
		Article article = (Article) objects[0];
		if (article != null) {

		}

		createIndex();

	}

	/**
	 * 切面 删除文章
	 * @param point
	 */
	@After("execution(* com.eryansky.modules.cms.service.ArticleService.delete(..))")
	public void deleteArticle(JoinPoint point) {
		createIndex();
	}
}
