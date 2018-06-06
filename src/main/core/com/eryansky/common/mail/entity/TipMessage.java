/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.mail.entity;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
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
