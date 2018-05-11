/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.BaseDao;
import com.eryansky.modules.sys.mapper.OrganExtend;
import org.apache.ibatis.annotations.Param;

/**
 * 系统DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2017-09-19
 */
@MyBatisDao
public interface SystemDao extends BaseDao {
    /**
     * 创建organ扩展表
     * @return
     */
    int createOrganExtend();

    /**
     * organ表同步到扩展表
     * @param id 机构ID
     * @return
     */
    int insertToOrganExtend(@Param("id") String id);

    /**
     * 删除organ扩展表数据
     * @param id 机构ID
     * @return
     */
    int deleteOrganExtend(@Param("id") String id);

    OrganExtend getOrganExtend(Parameter parameter);

    OrganExtend getOrganCompany(Parameter parameter);

    OrganExtend getOrganExtendByUserId(Parameter parameter);

    OrganExtend getCompanyByUserId(Parameter parameter);

	
}
