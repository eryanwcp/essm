/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.service;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.mapper.Role;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.RoleUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service基类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 */
@Transactional(readOnly = true)
public abstract class BaseService {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 数据范围过滤
     * @param user 当前用户对象，通过“entity.getCurrentUser()”获取
     * @param officeAlias 机构表别名，多个用“,”逗号隔开。
     * @param userAlias 用户表别名，多个用“,”逗号隔开，传递空，忽略此参数
     * @return 标准连接条件对象
     */
    public static String dataScopeFilter(User user, String officeAlias, String userAlias) {
        StringBuilder sqlString = new StringBuilder();

        // 进行权限过滤，多个角色权限范围之间为或者关系。
        List<String> dataScope = Lists.newArrayList();

        // 超级管理员，跳过权限过滤
        if (user != null && !user.getId().equals(User.SUPERUSER_ID)) {
            boolean isDataScopeAll = false;
            for (Role r : RoleUtils.findRolesByUserId(user.getId())) {
                if(StringUtils.isBlank(r.getDataScope())){
                    continue;
                }

                for (String oa : StringUtils.split(officeAlias, ",")) {
                    if (!dataScope.contains(r.getDataScope()) && StringUtils.isNotBlank(oa)) {
                        if (DataScope.ALL.getValue().equals(r.getDataScope())) {
                            isDataScopeAll = true;
                        } else if (DataScope.COMPANY_AND_CHILD.getValue().equals(r.getDataScope())) {
                            OrganExtend company = OrganUtils.getCompanyByUserId(user.getId());
                            sqlString.append(" OR " + oa + ".id = '" + company.getId() + "'");
                            sqlString.append(" OR " + oa + ".parent_ids LIKE '" + company.getParentIds() + company.getId() + ",%'");
                        } else if (DataScope.COMPANY.getValue().equals(r.getDataScope())) {
                            OrganExtend company = OrganUtils.getCompanyByUserId(user.getId());
                            sqlString.append(" OR " + oa + ".id = '" + company.getId() + "'");
                            // 包括本公司下的部门 （type=0:公司；type=1：部门）
                            sqlString.append(" OR (" + oa + ".parent_id = '" + company.getId() + "' AND " + oa + ".type = '1')");
                        } else if (DataScope.OFFICE_AND_CHILD.getValue().equals(r.getDataScope())) {
                            OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                            sqlString.append(" OR " + oa + ".id = '" + organExtend.getId() + "'");
                            sqlString.append(" OR " + oa + ".parent_ids LIKE '" + organExtend.getParentIds() + organExtend.getId() + ",%'");
                        } else if (DataScope.OFFICE.getValue().equals(r.getDataScope())) {
                            OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                            sqlString.append(" OR " + oa + ".id = '" + organExtend.getId() + "'");
                        } else if (DataScope.CUSTOM.getValue().equals(r.getDataScope())) {
                            sqlString.append(" OR EXISTS (SELECT 1 FROM t_sys_role_organ WHERE role_id = '" + r.getId() + "'");
                            sqlString.append(" AND organ_id = " + oa + ".id)");
                        }
                        dataScope.add(r.getDataScope());
                    }
                }
            }
            // 如果没有全部数据权限，并设置了用户别名，则当前权限为本人；如果未设置别名，当前无权限为已植入权限
            if (!isDataScopeAll) {
                if (StringUtils.isNotBlank(userAlias)) {
                    for (String ua : StringUtils.split(userAlias, ",")) {
                        sqlString.append(" OR " + ua + ".id = '" + user.getId() + "'");
                    }
                } else {
                    for (String oa : StringUtils.split(officeAlias, ",")) {
                        //sqlString.append(" OR " + oa + ".id  = " + user.getOffice().getId());
                        sqlString.append(" OR " + oa + ".id IS NULL");
                    }
                }
            } else {
                // 如果包含全部权限，则去掉之前添加的所有条件，并跳出循环。
                sqlString = new StringBuilder();
            }
        }
        if (StringUtils.isNotBlank(sqlString.toString())) {
            return " AND (" + sqlString.substring(4) + ")";
        }
        return "";
    }

    /**
     * 数据范围过滤（符合业务表字段不同的时候使用，采用exists方法）
     * @param entity 当前过滤的实体类
     * @param sqlMapKey sqlMap的键值，例如设置“dsf”时，调用方法：${sqlMap.sdf}
     * @param officeWheres office表条件，组成：部门表字段=业务表的部门字段
     * @param userWheres user表条件，组成：用户表字段=业务表的用户字段
     * @example
     * 		dataScopeFilter(user, "dsf", "id=a.office_id", "id=a.create_by");
     * 		dataScopeFilter(entity, "dsf", "code=a.jgdm", "no=a.cjr"); // 适应于业务表关联不同字段时使用，如果关联的不是机构id是code。
     */
    public static void dataScopeFilter(BaseEntity<?> entity, String sqlMapKey, String officeWheres, String userWheres) {
        User user = SecurityUtils.getCurrentUser();
        // 如果是超级管理员，则不过滤数据
        if (user.getId().equals(User.SUPERUSER_ID)) {
            return;
        }

        // 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
        StringBuilder sqlString = new StringBuilder();
        // 获取到最大的数据权限范围
        String roleId = "";
        int dataScopeInteger = Integer.valueOf(DataScope.SELF.getValue());
        for (Role r : RoleUtils.findRolesByUserId(user.getId())) {
            if(StringUtils.isBlank(r.getDataScope())){
                continue;
            }
            int ds = Integer.valueOf(r.getDataScope());
            if (ds == Integer.valueOf(DataScope.CUSTOM.getValue())) {
                roleId = r.getId();
                dataScopeInteger = ds;
                break;
            } else if (ds < dataScopeInteger) {
                roleId = r.getId();
                dataScopeInteger = ds;
            }
        }
        String dataScopeString = String.valueOf(dataScopeInteger);

        // 生成部门权限SQL语句
        for (String where : StringUtils.split(officeWheres, ",")) {
            if (DataScope.COMPANY_AND_CHILD.getValue().equals(dataScopeString)) {
                // 包括本公司下的部门 （type=0:公司；type=1：部门）
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_organ");
                sqlString.append(" WHERE type='1'");
                OrganExtend company = OrganUtils.getOrganCompany(user.getId());
                sqlString.append(" AND (id = '" + company.getId() + "'");
                sqlString.append(" OR parent_ids LIKE '" + company.getParentIds() + company.getId() + ",%')");
                sqlString.append(" AND " + where + ")");
            } else if (DataScope.COMPANY.getValue().equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_organ");
                sqlString.append(" WHERE type='0'");
                OrganExtend company = OrganUtils.getCompanyByUserId(user.getId());
                sqlString.append(" AND id = '" + company.getId() + "'");
                sqlString.append(" AND " + where + ")");
            } else if (DataScope.OFFICE_AND_CHILD.getValue().equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_organ");
                OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                sqlString.append(" WHERE (id = '" + organExtend.getId() + "'");
                sqlString.append(" OR parent_ids LIKE '" + organExtend.getParentIds() + organExtend.getId() + ",%')");
                sqlString.append(" AND " + where + ")");
            } else if (DataScope.OFFICE.getValue().equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_organ");
                OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                sqlString.append(" WHERE id = '" + organExtend.getId() + "'");
                sqlString.append(" AND " + where + ")");
            } else if (DataScope.CUSTOM.getValue().equals(dataScopeString)) {//TODO
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_role_organ ro123456, t_sys_organ o123456");
                sqlString.append(" WHERE ro123456.organ_id = o123456.id");
                sqlString.append(" AND ro123456.role_id = '" + roleId + "'");
                sqlString.append(" AND o123456." + where + ")");
            }
        }
        // 生成个人权限SQL语句
        for (String where : StringUtils.split(userWheres, ",")) {
            if (DataScope.SELF.getValue().equals(dataScopeString)) {
                sqlString.append(" AND EXISTS (SELECT 1 FROM t_sys_user");
                sqlString.append(" WHERE id='" + user.getId() + "'");
                sqlString.append(" AND " + where + ")");
            }
        }


        // 设置到自定义SQL对象
        entity.getSqlMap().put(sqlMapKey, sqlString.toString());

    }


}
