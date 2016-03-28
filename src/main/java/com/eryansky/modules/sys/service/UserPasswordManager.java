/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.entity.UserPassword;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-05-14 
 */
@Service
public class UserPasswordManager extends EntityManager<UserPassword, String> {

    private HibernateDao<UserPassword, String> userPasswordDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        userPasswordDao = new HibernateDao<UserPassword, String>(sessionFactory, UserPassword.class);
    }

    @Override
    protected HibernateDao<UserPassword, String> getEntityDao() {
        return userPasswordDao;
    }

    public UserPassword getLatestUserPasswordByUserId(String userId){
        List<UserPassword> userPasswords = getUserPasswordsByUserId(userId,1);
        return Collections3.isEmpty(userPasswords) ? null:userPasswords.get(0);
    }

    /**
     * 查询某个用户ID秘密修改记录
     * <br/>根据修改时间 降序排列
     * @param userId 用户ID
     * @param maxSize 最大记录数 为null是查询所有
     * @return
     */
    public List<UserPassword> getUserPasswordsByUserId(String userId,Integer maxSize){
        Parameter parameter = new Parameter(userId, StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from UserPassword u where u.userId = :p1 and u.status = :p2 order by u.modifyTime desc");

        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        if(maxSize != null){
            query.setMaxResults(maxSize);
        }

        return query.list();
    }

    /**
     * 新增修改密码记录
     * @param user
     * @return
     */
    public UserPassword addUserPasswordUpdate(User user){
        UserPassword userPassword = new UserPassword(user.getId(),user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        this.save(userPassword);
        return userPassword;
    }



}
