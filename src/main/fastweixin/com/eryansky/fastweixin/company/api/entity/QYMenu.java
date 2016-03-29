package com.eryansky.fastweixin.company.api.entity;

import com.eryansky.fastweixin.api.entity.BaseModel;
import com.eryansky.fastweixin.exception.WeixinException;

import java.util.ArrayList;
import java.util.List;

/**
 *  微信完整菜单
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYMenu extends BaseModel {

    private List<QYMenuButton> button;// 一级菜单。总共3个

    public List<QYMenuButton> getButton() {
        return button;
    }

    public QYMenu setButton(List<QYMenuButton> button) {
        if(button.size() > 3){
            throw new WeixinException("一级菜单最多3个");
        }
        this.button = button;
        return this;
    }

    public QYMenu addButton(QYMenuButton singleButton){
        if(button == null){
            button = new ArrayList<QYMenuButton>(3);
        }
        if(button.size() == 3){
            throw new WeixinException("一级菜单最多3个");
        }

        this.button.add(singleButton);
        return this;
    }
}
