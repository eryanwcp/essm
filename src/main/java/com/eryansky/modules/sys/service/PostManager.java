/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.Post;
import com.eryansky.modules.sys.entity.User;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理 Service
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-09 14:07
 */
@Service
public class PostManager extends
        EntityManager<Post, String> {

    private HibernateDao<Post, String> postDao;

    @Autowired
    private UserManager userManager;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        postDao = new HibernateDao<Post, String>(
                sessionFactory, Post.class);
    }

    @Override
    protected HibernateDao<Post, String> getEntityDao() {
        return postDao;
    }



    public Page<Post> findPage(Page<Post> p, String organId,String nameOrCode) throws DaoException, SystemException, ServiceException {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select p from Post p where p.status = :p1");
        if(StringUtils.isNotBlank(organId)){
            hql.append(" and (p.organId = :organId or :organId in elements(p.organIds))");
            parameter.put("organId",organId);
        }
        if(StringUtils.isNotBlank(nameOrCode)){
            hql.append(" and (p.name like :nameOrCode or p.code like :nameOrCode)");
            parameter.put("nameOrCode","%"+nameOrCode+"%");
        }
        return getEntityDao().findPage(p, hql.toString(),parameter);
    }

    /**
     * 根据机构ID以及岗位名称查找
     * @param organId 机构ID
     * @param postName 岗位名称
     */
    public Post getPostByON(String organId,String postName){
        Validate.notNull(organId, "参数[organId]不能为null");
        Validate.notNull(postName, "参数[postName]不能为null或空");
        Parameter parameter = new Parameter(organId,postName);
        List<Post> list = getEntityDao().find("from Post p where p.organId = :p1 and p.name = :p2",parameter);
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     * 根据机构ID以及岗位编码查找
     * @param organId 机构ID
     * @param postCode 岗位编码
     */
    public Post getPostByOC(String organId,String postCode){
        Validate.notNull(organId, "参数[organId]不能为null");
        Validate.notNull(postCode, "参数[postCode]不能为null或空");
        Parameter parameter = new Parameter(organId,postCode);
        List<Post> list = getEntityDao().find("from Post p where p.organId = :p1 or :organId in elements(p.organIds) and p.code = :p2",parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 机构岗位
     * @param organId
     * @return
     */
    public List<Post> getOrganPosts(String organId){
        Validate.notNull(organId, "参数[organId]不能为null");
        Parameter parameter = new Parameter(organId,StatusState.NORMAL.getValue());
        List<Post> list = getEntityDao().find("from Post p where (p.organId = :p1 or :p1 in elements(p.organIds)) and p.status = :p2",parameter);
        return list;
    }


    /**
     * 得到岗位所在部门的所有用户
     * @param postId 岗位ID
     * @return
     */
    public List<User> getPostOrganUsersByPostId(String postId){
        Validate.notNull(postId, "参数[postId]不能为null");
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),postId);
        StringBuffer hql = new StringBuffer();
        hql.append("from User u left join u.posts p where u.status = :p1 and p.id = :p2 order by u.orderNo asc");
        return getEntityDao().find(hql.toString(),parameter);
    }


    /**
     * 用户可选岗位列表
     * @param userId 用户ID 如果用户为null 则返回所有
     * @return
     */
    public List<Post> getSelectablePostsByUserId(String userId) {
        Validate.notNull(userId, "参数[userId]不能为null");
        List<Post> list = null;
        Parameter parameter = new Parameter();
        StringBuffer hql = new StringBuffer();
        hql.append("from Post p where p.status = :status ");
        parameter.put("status",StatusState.NORMAL.getValue());
        User user = userManager.loadById(userId);
        List<String> userOrganIds = user.getOrganIds();
        if(Collections3.isNotEmpty(userOrganIds)){
            hql.append(" and  (p.organId in (:userOrganIds)");
            for(int i=0;i<userOrganIds.size();i++){
                String userOrganId = userOrganIds.get(i);
                hql.append(" or :userOrganId_").append(i).append(" in elements(p.organIds)");
                parameter.put("userOrganId_"+i,userOrganId);
            }
            hql.append(")");
            parameter.put("userOrganIds",userOrganIds);
        }else{
            logger.warn("用户[{}]未设置部门.", new Object[]{user.getLoginName()});
            return null;
        }
        list = getEntityDao().find(hql.toString(),parameter);
        return list;
    }
}