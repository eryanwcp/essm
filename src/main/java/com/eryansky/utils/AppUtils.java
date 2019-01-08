/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-19 
 */
public class AppUtils {

    private static ServletContext servletContext;

    private AppUtils(){

    }

    public static void init(ServletContext sContext) {
        servletContext = sContext;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * 返回程序的物理安装路径
     *
     * @return String
     */
    public static String getAppAbsolutePath() {
        return servletContext.getRealPath("/");
    }

    public static String toJson(Object object){
        String json = JsonMapper.getInstance().toJson(object);
        return json;
    }

    /**
     * url and para separator *
     */
    public static final String URL_AND_PARA_SEPARATOR = "?";
    /**
     * parameters separator *
     */
    public static final String PARAMETERS_SEPARATOR = "&";
    /**
     * paths separator *
     */
    public static final String PATHS_SEPARATOR = "/";
    /**
     * equal sign *
     */
    public static final String EQUAL_SIGN = "=";

    /**
     * join paras
     *
     * @param parasMap paras map, key is para name, value is para value
     * @return join key and value with {@link #EQUAL_SIGN}, join keys with {@link #PARAMETERS_SEPARATOR}
     */
    public static String joinParas(Map<String, String> parasMap) {
        if (parasMap == null || parasMap.size() == 0) {
            return null;
        }

        StringBuilder paras = new StringBuilder();
        Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
            paras.append(entry.getKey()).append(EQUAL_SIGN).append(entry.getValue());
            if (ite.hasNext()) {
                paras.append(PARAMETERS_SEPARATOR);
            }
        }
        return paras.toString();
    }

    /**
     * join paras with encoded value
     *
     * @param parasMap
     * @return
     * @see #joinParas(Map)
     * @see StringUtils#utf8Encode(String)
     */
    public static String joinParasWithEncodedValue(Map<String, Object> parasMap) {
        StringBuilder paras = new StringBuilder("");
        if (parasMap != null && parasMap.size() > 0) {
            Iterator<Map.Entry<String, Object>> ite = parasMap.entrySet().iterator();
            try {
                while (ite.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) ite.next();
                    paras.append(entry.getKey()).append(EQUAL_SIGN).append(StringUtils.utf8Encode((String) entry.getValue()));
                    if (ite.hasNext()) {
                        paras.append(PARAMETERS_SEPARATOR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paras.toString();
    }

    /**
     * append a key and value pair to url
     *
     * @param url
     * @param paraKey
     * @param paraValue
     * @return
     */
    public static String appendParaToUrl(String url, String paraKey, String paraValue) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        if (!url.contains(URL_AND_PARA_SEPARATOR)) {
            sb.append(URL_AND_PARA_SEPARATOR);
        } else {
            sb.append(PARAMETERS_SEPARATOR);
        }
        return sb.append(paraKey).append(EQUAL_SIGN).append(paraValue).toString();
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param url url地址
     * @return url请求参数部分
     */
    public static Map<String, String> urlRequest(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = truncateUrlPage(url);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (StringUtils.isNotBlank(arrSplitEqual[0])) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


    //         \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    private static String androidReg = "\\bandroid|Nexus\\b";
    private static String iosReg = "ip(hone|od|ad)";

    private static Pattern androidPat = Pattern.compile(androidReg, Pattern.CASE_INSENSITIVE);
    private static Pattern iosPat = Pattern.compile(iosReg, Pattern.CASE_INSENSITIVE);

    /**
     *
     * @param userAgent
     * @return
     */
    public static boolean likeAndroid(String userAgent){
        if(null == userAgent){
            userAgent = "";
        }
        // 匹配
        Matcher matcherAndroid = androidPat.matcher(userAgent);
        return matcherAndroid.find();
    }

    /**
     *
     * @param userAgent
     * @return
     */
    public static boolean likeIOS(String userAgent){
        if(null == userAgent){
            userAgent = "";
        }
        // 匹配
        Matcher matcherIOS = iosPat.matcher(userAgent);
        return matcherIOS.find();
    }


    /**
     * 查找父级节点
     * @param parentId 父ID
     * @param treeNodes 节点集合
     * @return
     */
    public static TreeNode getParentTreeNode(String parentId, List<TreeNode> treeNodes){
        return getParentTreeNode(parentId,null,treeNodes);
    }
    /**
     * 查找父级节点
     * @param parentId 父ID
     * @param type 节点类型
     * @param treeNodes 节点集合
     * @return
     */
    public static TreeNode getParentTreeNode(String parentId,String type, List<TreeNode> treeNodes){
        TreeNode t = null;
        for(TreeNode treeNode:treeNodes){
            String _type = (String)treeNode.getAttributes().get("nType");
            if(type != null && type.equals(_type) && parentId.equals(treeNode.getId())){
                t = treeNode;
                break;
            }else if(parentId.equals(treeNode.getId())){
                t = treeNode;
                break;
            }

        }
        return t;
    }

    /**
     * 按树形结构排列
     * @param treeNodes
     * @return
     */
    public static List<TreeNode> toTreeTreeNodes(List<TreeNode> treeNodes){
        if(Collections3.isEmpty(treeNodes)){
            return Collections.emptyList();
        }
        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Map<String,TreeNode> tempMap = Maps.newLinkedHashMap();

        for(TreeNode treeNode:treeNodes){
            tempMap.put(treeNode.getId(),treeNode);
            tempTreeNodes.add(treeNode);
        }


        Set<String> keyIds = tempMap.keySet();
        Set<String> removeKeyIds = Sets.newHashSet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            String key = iteratorKey.next();
            TreeNode treeNode = null;
            for(TreeNode treeNode1:tempTreeNodes){
                if(treeNode1.getId().equals(key)){
                    treeNode = treeNode1;
                    break;
                }
            }

            if(StringUtils.isNotBlank(treeNode.getpId())){
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(),(String)treeNode.getAttributes().get("nype"), tempTreeNodes);
                if(pTreeNode != null){
                    for(TreeNode treeNode2:tempTreeNodes){
                        if(treeNode2.getId().equals(pTreeNode.getId())){
                            pTreeNode.setState(TreeNode.STATE_CLOASED);
                            if(Collections3.isEmpty(treeNode.getChildren())){
                                treeNode.setState(TreeNode.STATE_OPEN);
                            }
                            treeNode2.addChild(treeNode);
                            removeKeyIds.add(treeNode.getId());
                            break;
                        }
                    }

                }
            }

        }

        //remove
        if(Collections3.isNotEmpty(removeKeyIds)){
            keyIds.removeAll(removeKeyIds);
        }

        List<TreeNode> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            String _key = iteratorKey.next();
            TreeNode treeNode = null;
            for(TreeNode treeNode4:tempTreeNodes){
                if(treeNode4.getId().equals(_key)){
                    treeNode = treeNode4;
                    if(Collections3.isEmpty(treeNode.getChildren())){
                        treeNode.setState(TreeNode.STATE_OPEN);
                    }
                    result.add(treeNode);
                    break;
                }
            }

        }
        return result;
    }


    /**
     * 得到分页后的数据
     *
     * @param a
     * @param pageNo 页码
     * @param pageSize 页大小热水
     * @return 分页后结果
     */
    public static <T> List<T> getPagedList(List<T> a,int pageNo,int pageSize) {
        int fromIndex = (pageNo - 1) * pageSize;
        if (fromIndex >= a.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNo * pageSize;
        if (toIndex >= a.size()) {
            toIndex = a.size();
        }
        return a.subList(fromIndex, toIndex);
    }

    /**
     * emoji表情替换
     *
     * @param source 原字符串
     * @param slipStr emoji表情替换成的字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source,String slipStr) {
        if(StringUtils.isNotBlank(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        }else{
            return source;
        }
    }
}
