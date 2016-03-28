package com.eryansky.fastweixin.company.api.response;

import com.eryansky.fastweixin.api.response.BaseResponse;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 *  Response -- 标签信息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetTagInfoResponse extends BaseResponse {

    @JSONField(name = "userlist")
    private List<Map<String, String>> users;
    @JSONField(name = "partylist")
    private List<Integer> partys;

    public List<Map<String, String>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<String, String>> users) {
        this.users = users;
    }

    public List<Integer> getPartys() {
        return partys;
    }

    public void setPartys(List<Integer> partys) {
        this.partys = partys;
    }
}
