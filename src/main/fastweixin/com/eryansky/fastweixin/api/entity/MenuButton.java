package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.eryansky.fastweixin.api.enums.MenuType;
import com.eryansky.fastweixin.exception.WeixinException;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单按钮对象
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class MenuButton extends BaseModel {

    /**
     * 菜单类别
     */
    private MenuType type;

    /**
     * 菜单上显示的文字
     */
    private String name;

    /**
     * 菜单key，当MenuType值为CLICK时用于区别菜单
     */
    private String key;

    /**
     * 菜单跳转的URL，当MenuType值为VIEW时用
     */
    private String url;

    /**
     * 菜单显示的永久素材的MaterialID,当MenuType值为media_id和view_limited时必需
     */
    @JSONField(name = "media_id")
    private String mediaId;

    /**
     * 二级菜单列表，每个一级菜单下最多5个
     */
    @JSONField(name = "sub_button")
    private List<MenuButton> subButton;

    public MenuType getType() {
        return type;
    }

    public MenuButton setType(MenuType type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public MenuButton setName(String name) {
        this.name = name;
        return this;
    }

    public String getKey() {
        return key;
    }

    public MenuButton setKey(String key) {
        this.key = key;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MenuButton setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMediaId() {
        return mediaId;
    }

    public MenuButton setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public List<MenuButton> getSubButton() {
        return subButton;
    }

    public MenuButton setSubButton(List<MenuButton> subButton) {
        if (null == subButton || subButton.size() > 5) {
            throw new WeixinException("子菜单最多只有5个");
        }
        this.subButton = subButton;
        return this;
    }

    public MenuButton addSubButton(MenuButton menuButton){
        if(subButton == null){
            subButton = new ArrayList<MenuButton>(5);
        }
        if(subButton.size() > 5){
            throw new WeixinException("二级菜单最多5个");
        }

        this.subButton.add(menuButton);
        return this;
    }
}
