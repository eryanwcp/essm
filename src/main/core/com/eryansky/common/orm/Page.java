/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.utils.CookieUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 *
 * 注意所有序号从1开始.
 *
 * @param <T> Page中记录的类型.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class Page<T> {
	//-- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	public static final int DEFAULT_PAGESIZE = 20;
	public static final int PAGESIZE_ALL = -1;//不分页

	//-- 分页参数 --//
	protected int pageNo = 1;
	protected int pageSize = -1;//页面大小，设置为“-1”表示不进行分页（分页无效）
	protected String orderBy = null;
	protected String order = null;
	protected boolean autoCount = true;

	//-- 返回结果 --//
	protected List<T> result = Lists.newArrayList();
	protected long totalCount = -1;

	private int totalPage = -1; // 总页数

	private int first;// 首页索引
	private int last;// 尾页索引
	private int prev;// 上一页索引
	private int next;// 下一页索引

	private boolean firstPage;//是否是第一页
	private boolean lastPage;//是否是最后一页


	private int length = 8;// 显示页面长度
	private int slider = 1;// 前后显示页面长度

	private String funcName = "page"; // 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。

	private String funcParam = ""; // 函数的附加参数，第三个参数值。

	private String message = ""; // 设置提示消息，显示在“共n条”之后


	//-- 构造函数 --//
	public Page() {
	}

	public Page(int pageSize) {
		this(0, pageSize);
	}

	/**
	 * 构造方法
	 * @param pageNo 当前页码
	 * @param pageSize 分页大小
	 */
	public Page(int pageNo, int pageSize) {
		this(pageNo, pageSize, 0);
	}

	/**
	 * 构造方法
	 * @param pageNo 当前页码
	 * @param pageSize 分页大小
	 * @param count 数据条数
	 */
	public Page(int pageNo, int pageSize, long count) {
		this(pageNo, pageSize, count, new ArrayList<T>());
	}

	/**
	 * 构造方法
	 * @param pageNo 当前页码
	 * @param pageSize 分页大小
	 * @param count 数据条数
	 * @param list 本页数据对象列表
	 */
	public Page(int pageNo, int pageSize, long count, List<T> list) {
		this.setTotalCount(count);
		this.setPageNo(pageNo);
		this.pageSize = pageSize;
		this.setResult(list,false);
	}

	/**
	 * 构造方法 用于easyui
	 * @param request http请求对象
	 */
	public Page(HttpServletRequest request) {
		String _pageNo = WebUtils.getParameter(request, "page");
		String _pageSize = WebUtils.getParameter(request,"rows");
		String sort = WebUtils.getParameter(request,"sort");
		String order = WebUtils.getParameter(request,"order");
		this.setPageSize(StringUtils.isBlank(_pageSize) ? Page.DEFAULT_PAGESIZE:Integer.valueOf(_pageSize));
		this.setPageNo(StringUtils.isBlank(_pageNo) ? pageNo:Integer.valueOf(_pageNo));
		if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(order)) {
			this.setOrder(order);
			this.setOrderBy(sort);
		}
	}

	/**
	 * 构造方法 for bootstrap
	 * @param request 传递 repage 参数，来记住页码
	 * @param response 用于设置 Cookie，记住页码
	 */
	public Page(HttpServletRequest request, HttpServletResponse response){
		this(request, response, DEFAULT_PAGESIZE);
	}

	/**
	 * 构造方法 for bootstrap
	 * @param request 传递 repage 参数，来记住页码
	 * @param response 用于设置 Cookie，记住页码
	 * @param defaultPageSize 默认分页大小，如果传递 -1 则为不分页，返回所有数据
	 */
	public Page(HttpServletRequest request, HttpServletResponse response, int defaultPageSize){
		// 设置页码参数（传递repage参数，来记住页码）
		String no = request.getParameter("pageNo");
		if (org.apache.commons.lang3.StringUtils.isNumeric(no)){
			CookieUtils.setCookie(response, "pageNo", no);
			this.setPageNo(Integer.parseInt(no));
		}else if (request.getParameter("repage")!=null){
			no = CookieUtils.getCookie(request, "pageNo");
			if (org.apache.commons.lang3.StringUtils.isNumeric(no)){
				this.setPageNo(Integer.parseInt(no));
			}
		}
		// 设置页面大小参数（传递repage参数，来记住页码大小）
		String size = request.getParameter("pageSize");
		if (org.apache.commons.lang3.StringUtils.isNumeric(size)){
			CookieUtils.setCookie(response, "pageSize", size);
			this.setPageSize(Integer.parseInt(size));
		}else if (request.getParameter("repage")!=null){
			no = CookieUtils.getCookie(request, "pageSize");
			if (StringUtils.isNumeric(no)){
				this.setPageSize(Integer.parseInt(no));
			}
		}else if (defaultPageSize != -1){
			this.pageSize = defaultPageSize;
		}
		// 设置排序参数
		String orderBy = request.getParameter("orderBy");
		if (org.apache.commons.lang3.StringUtils.isNotBlank(orderBy)){
			this.setOrderBy(orderBy);
		}
	}



	/**
	 * 初始化参数
	 */
	public Page<T> initialize(){

		//1
		this.first = 1;

		this.last = (int)(totalCount / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);

		if (this.totalCount % this.pageSize != 0 || this.last == 0) {
			this.last++;
		}

		if (this.last < this.first) {
			this.last = this.first;
		}

		if (this.pageNo <= 1) {
			this.pageNo = this.first;
			this.firstPage=true;
		}

		if (this.pageNo >= this.last) {
			this.pageNo = this.last;
			this.lastPage=true;
		}

		if (this.pageNo < this.last - 1) {
			this.next = this.pageNo + 1;
		} else {
			this.next = this.last;
		}

		if (this.pageNo > 1) {
			this.prev = this.pageNo - 1;
		} else {
			this.prev = this.first;
		}

		//2
		if (this.pageNo < this.first) {// 如果当前页小于首页
			this.pageNo = this.first;
		}

		if (this.pageNo > this.last) {// 如果当前页大于尾页
			this.pageNo = this.last;
		}
		return this;
	}


	//-- 分页参数访问函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public Page<T> setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
		return this;
	}

	/**
	 * 返回Page对象自身的setPageNo函数,可用于连续设置。
	 */
	public Page<T> pageNo(final int thePageNo) {
		setPageNo(thePageNo);
		return this;
	}

	/**
	 * 获得每页的记录数量, 默认为-1.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量.
	 */
	public Page<T> setPageSize(final int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	/**
	 * 返回Page对象自身的setPageSize函数,可用于连续设置。
	 */
	public Page<T> pageSize(final int thePageSize) {
		setPageSize(thePageSize);
		return this;
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
	 */
	public int getFirst() {
		return ((pageNo - 1) * pageSize) + 1;
	}

	/**
	 * 获得排序字段,无默认值. 多个排序字段时用','分隔.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段,多个排序字段时用','分隔.
	 */
	public Page<T> setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	/**
	 * 返回Page对象自身的setOrderBy函数,可用于连续设置。
	 */
	public Page<T> orderBy(final String theOrderBy) {
		setOrderBy(theOrderBy);
		return this;
	}

	/**
	 * 获得排序方向, 无默认值.
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 设置排序方式向.
	 *
	 * @param order 可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public Page<T> setOrder(final String order) {
		String lowcaseOrder = StringUtils.lowerCase(order);

		//检查order字符串的合法值
		String[] orders = StringUtils.split(lowcaseOrder, ',');
		for (String orderStr : orders) {
			if (!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr)) {
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
			}
		}

		this.order = lowcaseOrder;
		return this;
	}

	/**
	 * 返回Page对象自身的setOrder函数,可用于连续设置。
	 */
	public Page<T> order(final String theOrder) {
		setOrder(theOrder);
		return this;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
	}

	/**
	 * 获得查询对象时是否先自动执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 设置查询对象时是否自动先执行count查询获取总记录数.
	 */
	public Page<T> setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
		return this;
	}

	/**
	 * 返回Page对象自身的setAutoCount函数,可用于连续设置。
	 */
	public Page<T> autoCount(final boolean theAutoCount) {
		setAutoCount(theAutoCount);
		return this;
	}

	//-- 访问查询结果函数 --//

	/**
	 * 获得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public Page<T> setResult(final List<T> result) {
		return setResult(result,true);
	}
	/**
	 * 设置页内的记录列表.
	 */
	public Page<T> setResult(final List<T> result,boolean flag) {
		this.result = result;
		if(flag){
			initialize();
		}
		return this;
	}


	/**
	 * 获得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public Page<T> setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
		return this;
	}


	/**
	 * 设置总页数
	 * @param totalPage
	 * @author 尔演&Eryan eryanwcp@gmail.com
	 * @date   2012-7-29 上午3:46:25
	 */
	public Page<T> setTotalPage(int totalPage) {
		this.totalPage = totalPage;
		return this;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0) {
			return -1;
		}

		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return pageNo + 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return pageNo - 1;
		} else {
			return pageNo;
		}
	}


	/**
	 * 尾页索引
	 * @return
	 */
	@JsonIgnore
	public int getLast() {
		return last;
	}

	/**
	 * 获取页面总数
	 * @return getLast();
	 */
	@JsonIgnore
	public int getTotalPage() {
		return getLast();
	}

	/**
	 * 是否为第一页
	 * @return
	 */
	@JsonIgnore
	public boolean isFirstPage() {
		return firstPage;
	}

	/**
	 * 是否为最后一页
	 * @return
	 */
	@JsonIgnore
	public boolean isLastPage() {
		return lastPage;
	}

	/**
	 * 上一页索引值
	 * @return
	 */
	@JsonIgnore
	public int getPrev() {
		if (isFirstPage()) {
			return pageNo;
		} else {
			return pageNo - 1;
		}
	}

	/**
	 * 下一页索引值
	 * @return
	 */
	@JsonIgnore
	public int getNext() {
		if (isLastPage()) {
			return pageNo;
		} else {
			return pageNo + 1;
		}
	}

	/**
	 * 获取 Hibernate FirstResult
	 */
	public int getFirstResult(){
		int firstResult = (getPageNo() - 1) * getPageSize();
		if (firstResult >= getTotalCount()) {
			firstResult = 0;
		}
		return firstResult;
	}

	public int getLastResult(){
		int lastResult = getFirstResult()+getMaxResults();
		if(lastResult>getTotalCount()) {
			lastResult =(int) getTotalCount();
		}
		return lastResult;
	}
	/**
	 * 获取 Hibernate MaxResults
	 */
	public int getMaxResults(){
		return getPageSize();
	}

	public static int countOffset(final int pageSize, final int pageNo) {
		final int offset = pageSize * (pageNo - 1);
		return offset;
	}

	public static int countTotalPage(final int pageSize, final long totalCount) {
		int pages = (int) (totalCount % pageSize == 0 ? totalCount / pageSize : totalCount
				/ pageSize + 1);
		return pages;
	}

	/**
	 * 获取分页函数的附加参数
	 * @return
	 */
	@JsonIgnore
	public String getFuncParam() {
		return funcParam;
	}

	/**
	 * 设置分页函数的附加参数
	 * @return
	 */
	public Page<T> setFuncParam(String funcParam) {
		this.funcParam = funcParam;
		return this;
	}

	/**
	 * 设置提示消息，显示在“共n条”之后
	 * @param message
	 */
	public Page<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * 默认输出当前分页标签
	 * <div class="page">${page}</div>
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		if (pageNo == first) {// 如果是首页
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>\n");
		} else {
			sb.append("<li><a href=\"javascript:\" onclick=\""+funcName+"("+prev+","+pageSize+",'"+funcParam+"');\">&#171; 上一页</a></li>\n");
		}

		int begin = pageNo - (length / 2);

		if (begin < first) {
			begin = first;
		}

		int end = begin + length - 1;

		if (end >= last) {
			end = last;
			begin = end - length + 1;
			if (begin < first) {
				begin = first;
			}
		}

		if (begin > first) {
			int i = 0;
			for (i = first; i < first + slider && i < begin; i++) {
				sb.append("<li><a href=\"javascript:\" onclick=\""+funcName+"("+i+","+pageSize+",'"+funcParam+"');\">"
						+ (i + 1 - first) + "</a></li>\n");
			}
			if (i < begin) {
				sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
			}
		}

		for (int i = begin; i <= end; i++) {
			if (i == pageNo) {
				sb.append("<li class=\"active\"><a href=\"javascript:\">" + (i + 1 - first)
						+ "</a></li>\n");
			} else {
				sb.append("<li><a href=\"javascript:\" onclick=\""+funcName+"("+i+","+pageSize+",'"+funcParam+"');\">"
						+ (i + 1 - first) + "</a></li>\n");
			}
		}

		if (last - end > slider) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>\n");
			end = last - slider;
		}

		for (int i = end + 1; i <= last; i++) {
			sb.append("<li><a href=\"javascript:\" onclick=\""+funcName+"("+i+","+pageSize+",'"+funcParam+"');\">"
					+ (i + 1 - first) + "</a></li>\n");
		}

		if (pageNo == last) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">下一页 &#187;</a></li>\n");
		} else {
			sb.append("<li><a href=\"javascript:\" onclick=\""+funcName+"("+next+","+pageSize+",'"+funcParam+"');\">"
					+ "下一页 &#187;</a></li>\n");
		}

		sb.append("<li class=\"disabled controls\"><a href=\"javascript:\">当前 ");
		sb.append("<input type=\"text\" value=\""+pageNo+"\" onkeypress=\"var e=window.event||this;var c=e.keyCode||e.which;if(c==13)");
		sb.append(funcName+"(this.value,"+pageSize+",'"+funcParam+"');\" onclick=\"this.select();\"/> / ");
		sb.append("<input type=\"text\" value=\""+pageSize+"\" onkeypress=\"var e=window.event||this;var c=e.keyCode||e.which;if(c==13)");
		sb.append(funcName+"("+pageNo+",this.value,'"+funcParam+"');\" onclick=\"this.select();\"/> 条，");
		sb.append("共 " + totalCount + " 条"+(message!=null?message:"")+"</a></li>\n");

		sb.insert(0,"<ul>\n").append("</ul>\n");

		sb.append("<div style=\"clear:both;\"></div>");

//		sb.insert(0,"<div class=\"page\">\n").append("</div>\n");

		return sb.toString();
	}

	/**
	 * 获取分页HTML代码
	 * @return
	 */
	public String getHtml(){
		return toString();
	}

}
