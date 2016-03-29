package com.eryansky.fastweixin.api.entity;
/**
 *  分组信息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class Group extends BaseModel {

    private Integer id;
    private String  name;
    private Integer count;

    public Integer getId() {
        return id;
    }

    public Group setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public Group setCount(Integer count) {
        this.count = count;
        return this;
    }
}
