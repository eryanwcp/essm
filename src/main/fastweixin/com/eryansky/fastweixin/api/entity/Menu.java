package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.eryansky.fastweixin.exception.WeixinException;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单对象，包含所有菜单按钮
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class Menu extends BaseModel {

    /**
     * 一级菜单列表，最多3个
     */
    private List<MenuButton> button;

    /**
     * 菜单匹配规则
     *
     * @since 1.3.7
     */
    private Matchrule matchrule;

    /**
     * 菜单ID，查询时会返回，删除个性化菜单时会用到
     *
     * @since 1.3.7
     */
    @JSONField(name = "menuid")
    private String menuId;

    public List<MenuButton> getButton() {
        return button;
    }

    public Menu setButton(List<MenuButton> button) {
        if (null == button || button.size() > 3) {
            throw new WeixinException("主菜单最多3个");
        }
        this.button = button;
        return this;
    }

    public Matchrule getMatchrule() {
        return matchrule;
    }

    public Menu setMatchrule(Matchrule matchrule) {
        this.matchrule = matchrule;
        return this;
    }

    public String getMenuId() {
        return menuId;
    }

    public Menu setMenuId(String menuId) {
        this.menuId = menuId;
        return this;
    }

    public Menu addButton(MenuButton singleButton){
        if(button == null){
            button = new ArrayList<MenuButton>(3);
        }
        if(button.size() == 3){
            throw new WeixinException("一级菜单最多3个");
        }

        this.button.add(singleButton);
        return this;
    }
}
