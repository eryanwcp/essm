package com.eryansky.fastweixin.api.entity;

/**
 * 标签对象
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-14
 */
public class Tag extends BaseModel {
    /**
     * 标签id，由微信分配
     */
    private Integer id;
    /**
     * 标签名，UTF8编码
     */
    private String name;
    /**
     * 此标签下粉丝数
     */
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}