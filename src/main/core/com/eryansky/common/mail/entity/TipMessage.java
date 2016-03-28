/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.common.mail.entity;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public class TipMessage {
	private String title;
	private String titleURL;
	
	private String smallTitle;
	private String smallTitleURL;
	
	private String content;
	private String contentURL;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSmallTitle() {
		return smallTitle;
	}

	public void setSmallTitle(String smallTitle) {
		this.smallTitle = smallTitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitleURL() {
		return titleURL;
	}

	public void setTitleURL(String titleURL) {
		this.titleURL = titleURL;
	}

	public String getSmallTitleURL() {
		return smallTitleURL;
	}

	public void setSmallTitleURL(String smallTitleURL) {
		this.smallTitleURL = smallTitleURL;
	}

	public String getContentURL() {
		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}
}
