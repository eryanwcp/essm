/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.modules.sys._enum.LogType;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.modules.sys._enum.AreaType;
import com.eryansky.modules.sys.mapper.Area;
import com.eryansky.modules.sys.service.AreaService;
import com.eryansky.modules.sys.utils.AreaUtils;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 区域Controller
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
public class AreaController extends SimpleController {

	@Autowired
	private AreaService areaService;
	
	@ModelAttribute()
	public Area get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return areaService.get(id);
		}else{
			return new Area();
		}
	}

	@Logging(value = "区域管理",logType = LogType.access)
	@RequiresPermissions("sys:area:view")
	@RequestMapping(value = {"list", ""})
	public String list(Area area, Model model, HttpServletRequest request, HttpServletResponse response) {
		List<Area> list = null;
		String parentId = area.getParentId();
		if(StringUtils.isNotBlank(parentId) && !"0".equals(parentId)){
//			list = areaService.findByParentId(parentId);//查找下级
			list = areaService.findOwnAndChild(parentId);//查找（所有）下级
		}else{
			list = areaService.findAreaUp();
		}
		model.addAttribute("list", list);
		model.addAttribute("parentId", parentId);
		model.addAttribute("rootId", "0".equals(parentId) ? parentId:AreaUtils.get(parentId).getParentId());
		return "modules/sys/areaList";
	}

	@RequiresPermissions("sys:area:view")
	@RequestMapping(value = "form")
	public String form(Area area, Model model) {
		if (area.getParent()==null||area.getParent().getId()==null){
			area.setParent(new Area(OrganUtils.getOrganExtendByUserId(SecurityUtils.getCurrentUserId()).getId()));
		}
		area.setParent(areaService.get(area.getParentId()));
//		// 自动获取排序号
		if (StringUtils.isBlank(area.getId())){
			int size = 0;
			List<Area> list = areaService.findAll();
			for (int i=0; i<list.size(); i++){
				Area e = list.get(i);
				if (e.getParent()!=null && area.getParent() != null &&  e.getParent().getId()!=null
						&& e.getParent().getId().equals(area.getParent().getId())){
					size++;
				}
			}
			if(area.getParent() != null && StringUtils.isNotBlank(area.getParent().getCode())){
				area.setCode(area.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size : 1), 4, "0"));
			}
		}
		model.addAttribute("area", area);

		model.addAttribute("areas", AreaType.values());
		return "modules/sys/areaForm";
	}

	@Logging(value = "区域管理-保存区域",logType = LogType.access)
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "save")
	public String save(Area area, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, area)){
			return form(area, model);
		}
		areaService.save(area);
		addMessage(redirectAttributes, "保存区域'" + area.getName() + "'成功");
		return "redirect:" + AppConstants.getAdminPath() + "/sys/area/";
	}

	@Logging(value = "区域管理-删除区域",logType = LogType.access)
	@RequiresPermissions("sys:area:edit")
	@RequestMapping(value = "delete")
	public String delete(Area area, RedirectAttributes redirectAttributes) {
//		if (Area.isRoot(id)){
//			addMessage(redirectAttributes, "删除区域失败, 不允许删除顶级区域或编号为空");
//		}else{
			areaService.delete(area);
			addMessage(redirectAttributes, "删除区域成功");
//		}
		return "redirect:" + AppConstants.getAdminPath() + "/sys/area/";
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<TreeNode> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<TreeNode> treeNodes = Lists.newArrayList();
		List<Area> list = areaService.findAll();
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				TreeNode treeNode = new TreeNode(e.getId(),e.getName());
				treeNode.setpId(e.getParentId());
                treeNodes.add(treeNode);
			}
		}
		return treeNodes;
	}

	/**
	 * 省、市、县数据
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "areaUpData")
	public List<TreeNode> provinceCityAreaData(HttpServletResponse response) {
		List<TreeNode> treeNodes= Lists.newArrayList();
		List<Area> list = areaService.findAreaUp();
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			TreeNode treeNode = new TreeNode(e.getId(),e.getName());
			treeNode.setpId(e.getParentId());
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}

	/**
	 * 区县及下级数据
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "areaDownData")
	public List<TreeNode> areaData(HttpServletResponse response) {
		List<TreeNode> treeNodes= Lists.newArrayList();
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		String areaId = OrganUtils.getAreaId(sessionInfo.getLoginCompanyId());
		List<Area> list = areaService.findAreaDown(areaId);
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			TreeNode treeNode = new TreeNode(e.getId(),e.getName());
			treeNode.setpId(e.getParentId());
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}


    /**
     * 查找自己和子区域
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ownAndChildData")
    public List<TreeNode> ownAndChildData(HttpServletResponse response) {
        List<TreeNode> treeNodes= Lists.newArrayList();
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		String areaId = OrganUtils.getAreaId(sessionInfo.getLoginCompanyId());
		List<Area> list = areaService.findOwnAndChild(areaId);
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			TreeNode treeNode = new TreeNode(e.getId(),e.getName());
			treeNode.setpId(e.getParentId());
			treeNodes.add(treeNode);
		}
        return treeNodes;
    }
}
