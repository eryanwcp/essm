package com.eryansky.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetMaterialListResponse extends BaseResponse {

    private static final Logger LOG = LoggerFactory.getLogger(GetMaterialListResponse.class);

    @JSONField(name="total_count")
    private int totalCount;// 该类型素材总数
    @JSONField(name="item_count")
    private int itemCount;// 本次获取的数量
    @JSONField(name="item")
    private List<Map<String, Object>> items;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
