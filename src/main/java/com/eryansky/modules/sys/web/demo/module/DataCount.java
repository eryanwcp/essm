/*
 * Created on 2012-12-20
 * DataCount.java V1.0
 *
 * Copyright Notice =========================================================
 * This file contains proprietary information of Eastcom Technologies Co. Ltd.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2012 =======================================================
 */
package com.eryansky.modules.sys.web.demo.module;

/**
 * 
 * Title: eed <br>
 * Description: <br>
 * Copyright: eastcom Copyright (C) 2009 <br>
 * 
 * @author <a href="mailto:邮箱">jiangxd</a><br>
 * @e-mail: jiangxd@eastcom-sw.com<br>
 * @version 2.0 <br>
 * @creatdate 2012-12-20 下午05:49:35 <br>
 * 
 */
public class DataCount {

	private String province;
	private String city;
	private String area;
	private Integer dog;
	private Integer cat;
	private Integer people;
	public DataCount(){}
	public DataCount(String province, String city, String area, Integer dog,
			Integer cat, Integer people) {
		super();
		this.province = province;
		this.city = city;
		this.area = area;
		this.dog = dog;
		this.cat = cat;
		this.people = people;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Integer getDog() {
		return dog;
	}
	public void setDog(Integer dog) {
		this.dog = dog;
	}
	public Integer getCat() {
		return cat;
	}
	public void setCat(Integer cat) {
		this.cat = cat;
	}
	public Integer getPeople() {
		return people;
	}
	public void setPeople(Integer people) {
		this.people = people;
	}
}
