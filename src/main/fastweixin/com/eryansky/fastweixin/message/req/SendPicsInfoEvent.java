package com.eryansky.fastweixin.message.req;

import java.util.List;
import java.util.Map;

public class SendPicsInfoEvent extends BaseEvent {
	
	private String eventKey;
	private Integer count;
	private List<Map> picList;
	
	public SendPicsInfoEvent(String eventKey, Integer count, List<Map> picList) {
		super();
		this.eventKey = eventKey;
		this.count = count;
		this.picList = picList;
	}

	public String getEventKey() {
		return eventKey;
	}

	public SendPicsInfoEvent setEventKey(String eventKey) {
		this.eventKey = eventKey;
		return this;
	}

	public Integer getCount() {
		return count;
	}

	public SendPicsInfoEvent setCount(Integer count) {
		this.count = count;
		return this;
	}

	public List<Map> getPicList() {
		return picList;
	}

	public SendPicsInfoEvent setPicList(List<Map> picList) {
		this.picList = picList;
		return this;
	}

    @Override
    public String toString() {
        return "SendPicsInfoEvent{" +
                "eventKey='" + eventKey + '\'' +
                ", count=" + count +
                ", picList=" + picList +
                '}';
    }
}
