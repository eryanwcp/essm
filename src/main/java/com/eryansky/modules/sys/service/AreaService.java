/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.modules.sys.dao.AreaDao;
import com.eryansky.modules.sys.mapper.Area;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 区域Service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaDao, Area> {

	@Autowired
	private AreaDao areaDao;

	@Cacheable(value = { CacheConstants.AREA_LIST_CACHE})
	public List<Area> findAll(){
		List<Area> areaList = areaDao.findAllList(new Area());
		return areaList;
	}

	@CacheEvict(value = {CacheConstants.AREA_LIST_CACHE},allEntries = true)
	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
	}

	@CacheEvict(value = {CacheConstants.AREA_LIST_CACHE},allEntries = true)
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
	}

	/**
	 * 根据编码查找
	 * @param code 编码
	 * @return
	 */
	public Area getByCode(String code){
        return areaDao.getByCode(code);
    }

	/**
	 * 查找区县及以上
	 * @return
	 */
	public List<Area> findAreaUp(){
		Area entity = new Area();
		List<Area> areaList = areaDao.findAreaUp(entity);
		return areaList;
	}
	/**
	 * 查找区县及以下
	 * @return
	 */
	public List<Area> findAreaDown(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("areaId",parentId);
		parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
		List<Area> areaList = areaDao.findAreaDown(parameter);
		return areaList;
	}

	/**
	 * 查找本机以及以下
	 * @param parentId
	 * @return
	 */
	public List<Area> findOwnAndChild(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("parentId",parentId);
		parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
		List<Area> areaList = areaDao.findOwnAndChild(parameter);
		return areaList;
	}

	/**
	 * 查找所属下级
	 * @param parentId
	 * @return
	 */
	public List<Area> findByParentId(String parentId){
		Parameter parameter = Parameter.newParameter();
		parameter.put("parentId",parentId);
		parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
		List<Area> areaList = areaDao.findByParentId(parameter);
		return areaList;
	}




}
