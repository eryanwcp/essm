package com.eryansky.fastweixin.company.api.response;

import com.eryansky.fastweixin.api.response.BaseResponse;
import com.eryansky.fastweixin.company.api.entity.QYMenu;

/**
 *  Response -- 获取菜单
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetQYMenuResponse extends BaseResponse {

    private QYMenu menu;

    public QYMenu getMenu() {
        return menu;
    }

    public void setMenu(QYMenu menu) {
        this.menu = menu;
    }
}
