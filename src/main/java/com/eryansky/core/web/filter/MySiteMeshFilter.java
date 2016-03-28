/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.web.filter;

import com.eryansky.common.web.filter.BaseFilter;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.compatability.DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;
import com.opensymphony.sitemesh.webapp.ContainerTweaks;
import com.opensymphony.sitemesh.webapp.ContentBufferingResponse;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 以com.opensymphony.sitemesh.webapp.SiteMeshFilter为原型改造而来
 * <br/>支持黑名单、白名单 {@link BaseFilter}
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-07 
 */
public class MySiteMeshFilter extends BaseFilter {

    private FilterConfig filterConfig;
    private ContainerTweaks containerTweaks;
    private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";


    @Override
    public void init() throws ServletException {
        this.filterConfig = config;
        containerTweaks = new ContainerTweaks();
    }

    public void destroy() {
        filterConfig = null;
        containerTweaks = null;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);

        ContentProcessor contentProcessor = initContentProcessor(webAppContext);
        DecoratorSelector decoratorSelector = initDecoratorSelector(webAppContext);

        if (filterAlreadyAppliedForRequest(request)) {
            // Prior to Servlet 2.4 spec, it was unspecified whether the filter should be called again upon an include().
            chain.doFilter(request, response);
            return;
        }

        if (!contentProcessor.handles(webAppContext)) {
            // Optimization: If the content doesn't need to be processed, bypass SiteMesh.
            chain.doFilter(request, response);
            return;
        }

        if (containerTweaks.shouldAutoCreateSession()) {
            // Some containers (such as Tomcat 4) will not allow sessions to be created in the decorator.
            // (i.e after the response has been committed).
            request.getSession(true);
        }

        try {

            Content content = obtainContent(contentProcessor, webAppContext, request, response, chain);

            if (content == null) {
                request.setAttribute(ALREADY_APPLIED_KEY, null);
                return;
            }

            Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
            decorator.render(content, webAppContext);

        } catch (IllegalStateException e) {
            // Some containers (such as WebLogic) throw an IllegalStateException when an error page is served.
            // It may be ok to ignore this. However, for safety it is propegated if possible.
            if (!containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()) {
                throw e;
            }
        } catch (RuntimeException e) {
            if (containerTweaks.shouldLogUnhandledExceptions()) {
                // Some containers (such as Tomcat 4) swallow RuntimeExceptions in filters.
                servletContext.log("Unhandled exception occurred whilst decorating page", e);
            }
            throw e;
        } catch (ServletException e) {
            request.setAttribute(ALREADY_APPLIED_KEY, null);
            throw e;
        }
    }

    protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext) {
        // TODO: Remove heavy coupling on horrible SM2 Factory
        Factory factory = Factory.getInstance(new Config(filterConfig));
        factory.refresh();
        return new PageParser2ContentProcessor(factory);
    }

    protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
        // TODO: Remove heavy coupling on horrible SM2 Factory
        Factory factory = Factory.getInstance(new Config(filterConfig));
        factory.refresh();
        return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
    }

    /**
     * Continue in filter-chain, writing all content to buffer and parsing
     * into returned {@link com.opensymphony.module.sitemesh.Page} object. If
     * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is returned.
     */
    private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext,
                                  HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, contentProcessor, webAppContext);
        chain.doFilter(request, contentBufferingResponse);
        // TODO: check if another servlet or filter put a page object in the request
        //            Content result = request.getAttribute(PAGE);
        //            if (result == null) {
        //                // parse the page
        //                result = pageResponse.getPage();
        //            }
        webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
        return contentBufferingResponse.getContent();
    }

    private boolean filterAlreadyAppliedForRequest(HttpServletRequest request) {
        if (request.getAttribute(ALREADY_APPLIED_KEY) == Boolean.TRUE) {
            return true;
        } else {
            request.setAttribute(ALREADY_APPLIED_KEY, Boolean.TRUE);
            return false;
        }
    }
}
