package com.eryansky.fastweixin.company.api.response;/**
 * Created by Nottyjay on 2015/6/11.
 */

import com.eryansky.fastweixin.api.response.BaseResponse;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class GetQYUserInviteResponse extends BaseResponse {
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
