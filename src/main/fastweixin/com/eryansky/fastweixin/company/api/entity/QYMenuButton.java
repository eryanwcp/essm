package com.eryansky.fastweixin.company.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.eryansky.fastweixin.api.entity.BaseModel;
import com.eryansky.fastweixin.company.api.enums.QYMenuType;
import com.eryansky.fastweixin.exception.WeixinException;

import java.util.ArrayList;
import java.util.List;

/**
 *  菜单按钮对象
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYMenuButton extends BaseModel {

    private QYMenuType type;// 菜单类型
    private String name;// 菜单显式的名称
    private String key;// 菜单类型不为view或为空时可用。代表事件名称
    private String url;// 菜单类型为view时可用。代表点击按钮后跳转的页面地址
    @JSONField(name = "sub_button")
    private List<QYMenuButton> subButton;

    public QYMenuType getType() {
        return type;
    }

    public QYMenuButton setType(QYMenuType type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public QYMenuButton setName(String name) {
        this.name = name;
        return this;
    }

    public String getKey() {
        return key;
    }

    public QYMenuButton setKey(String key) {
        this.key = key;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public QYMenuButton setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<QYMenuButton> getSubButton() {
        return subButton;
    }

    public QYMenuButton setSubButton(List<QYMenuButton> subButton) {
        if(subButton.size() > 5){
            throw new WeixinException("二级菜单最多5个");
        }
        this.subButton = subButton;
        return this;
    }

    public QYMenuButton addSubButton(QYMenuButton menuButton){
        if(subButton == null){
            subButton = new ArrayList<QYMenuButton>(5);
        }
        if(subButton.size() > 5){
            throw new WeixinException("二级菜单最多5个");
        }

        this.subButton.add(menuButton);
        return this;
    }
}
