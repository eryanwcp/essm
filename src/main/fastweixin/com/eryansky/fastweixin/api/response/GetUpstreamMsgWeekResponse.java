package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.UpstreamMsgWeek;

import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetUpstreamMsgWeekResponse extends BaseResponse {

    private List<UpstreamMsgWeek> list;

    public List<UpstreamMsgWeek> getList() {
        return list;
    }

    public void setList(List<UpstreamMsgWeek> list) {
        this.list = list;
    }
}
