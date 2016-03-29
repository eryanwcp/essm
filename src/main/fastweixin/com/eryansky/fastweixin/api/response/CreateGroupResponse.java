package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.Group;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class CreateGroupResponse extends BaseResponse {

//    private String id;
//    private String name;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
	
	private Group group;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	
}
