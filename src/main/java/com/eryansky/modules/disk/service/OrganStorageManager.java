/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.modules.disk.entity.OrganStorage;
import com.eryansky.utils.AppConstants;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门云盘存储空间配置 管理
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-24
 */
@Service
public class OrganStorageManager extends EntityManager<OrganStorage, String> {

    private HibernateDao<OrganStorage, String> organStorageDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        organStorageDao = new HibernateDao<OrganStorage, String>(sessionFactory, OrganStorage.class);
    }

    @Override
    protected HibernateDao<OrganStorage, String> getEntityDao() {
        return organStorageDao;
    }


    /**
     * 查找部门云盘存储空间配置信息
     * @param organId 部门ID
     * @return
     */
    public OrganStorage getOrganStorage(String organId){
        Validate.notNull(organId, "参数[organId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("from OrganStorage e where e.organId = :p1");
        Parameter parameter = new Parameter(organId);
        List<OrganStorage> list =  getEntityDao().find(hql.toString(),parameter);
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     * 查找部门可用存储字节数
     * @param organId 用户ID
     * @return
     */
    public long getOrganAvaiableStorage(String organId) {
        Validate.notNull(organId, "参数[organId]不能为null.");
        OrganStorage organStorage = getOrganStorage(organId);
        int diskOrganLimitSize = AppConstants.getDiskOrganLimitSize().intValue();
        if (organStorage != null && organStorage.getLimitSize() != null) {
            diskOrganLimitSize = organStorage.getLimitSize();
        }
        return Long.valueOf(diskOrganLimitSize) * 1024L * 1024L;
    }
}
