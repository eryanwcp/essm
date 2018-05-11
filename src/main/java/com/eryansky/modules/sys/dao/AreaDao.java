/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.entity.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.core.orm.mybatis.dao.TreeDao;
import com.eryansky.modules.sys.mapper.Area;

import java.util.List;

/**
 * 区域DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {

    Area getByCode(String code);

    List<Area> findAreaUp(Area area);

    List<Area> findAreaDown(Parameter parameter);

    /**
     * 查找自己以及子区域
     * @param parameter
     * @return
     */
    List<Area> findOwnAndChild(Parameter parameter);

    List<Area> findByParentId(Parameter parameter);

}
