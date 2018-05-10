/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Dictionary;
import com.eryansky.modules.sys.mapper.DictionaryItem;
import com.eryansky.modules.sys.service.DictionaryItemService;
import com.eryansky.modules.sys.service.DictionaryService;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典Dictionary管理 Controller层.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:36:24
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping(value = "${adminPath}/sys/dictionaryItem")
public class DictionaryItemController extends SimpleController {

    @Autowired
    private DictionaryItemService dictionaryItemService;
    @Autowired
    private DictionaryService dictionaryService;

    @ModelAttribute
    public DictionaryItem get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return dictionaryItemService.get(id);
        }else{
            return new DictionaryItem();
        }
    }

    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public Datagrid<DictionaryItem> datagrid(DictionaryItem dictionaryItem, HttpServletRequest request, HttpServletResponse response) {
        Page<DictionaryItem> page = new Page<DictionaryItem>(request);
        page = dictionaryItemService.findPage(page, dictionaryItem);
        Datagrid<DictionaryItem> datagrid = new Datagrid<DictionaryItem>(page.getTotalCount(), page.getResult());
        return datagrid;
    }

    /**
     * 保存
     *
     * @param dictionaryItem
     * @param dictionaryId   字典ID
     * @param parentId       上级ID
     * @return
     */
    @Logging(value = "字典管理-保存字典项",logType = LogType.access)
    @RequestMapping(value = {"save"})
    @ResponseBody
    public Result save(@ModelAttribute DictionaryItem dictionaryItem, String dictionaryId, String parentId) {
        Result result = null;

        // 编码是否重复校验
        DictionaryItem checkCodeDictionaryItem = dictionaryItemService.getByCode(dictionaryId,dictionaryItem.getCode());
        if (checkCodeDictionaryItem != null
                && !checkCodeDictionaryItem.getId().equals(dictionaryItem.getId())) {
            result = new Result(Result.WARN, "编码为[" + dictionaryItem.getCode()
                    + "]已存在,请修正!", "code");
            logger.debug(result.toString());
            return result;
        }

        Dictionary dictionary = new Dictionary(dictionaryId);
        DictionaryItem parent = new DictionaryItem(parentId);
        dictionaryItem.setDictionary(dictionary);
        dictionaryItem.setParent(parent);


        dictionaryItemService.save(dictionaryItem);
        result = Result.successResult();
        logger.debug(result.toString());
        return result;
    }

    @Logging(value = "字典管理-删除字典项",logType = LogType.access)
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids", required = false) List<String> ids) {
        dictionaryItemService.deleteByIds(ids);
        return Result.successResult();
    }

    /**
     * combotree下拉列表数据.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"combotree"})
    @ResponseBody
    public List<TreeNode> combotree(@ModelAttribute("model") DictionaryItem dictionaryItem, String selectType) throws Exception {
        List<TreeNode> titleList = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if (selectTreeNode != null) {
            titleList.add(selectTreeNode);
        }
        List<TreeNode> treeNodes = dictionaryItemService
                .getByDictionaryId(dictionaryItem.getDictionaryId(), dictionaryItem.getId(), true);

        List<TreeNode> unionList = ListUtils.union(titleList, treeNodes);
        return unionList;
    }


    /**
     * 排序最大值.
     */
    @RequestMapping(value = {"maxSort"})
    @ResponseBody
    public Result maxSort() throws Exception {
        Integer maxSort = dictionaryItemService.getMaxSort();
        Result result = new Result(Result.SUCCESS, null, maxSort);
        return result;
    }

}
