package com.eryansky.codegen.util;



/**
 * 生成文件的文件类型.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:33:01 
 * @version 1.0
 */
public enum FileType {
	ENTITY("entity", Resources.ENTITY_PACKAGE,Resources.ENTITY_TEMPLATE,".java"), 
	DAO("dao", Resources.DAO_PACKAGE,Resources.DAO_TEMPLATE,"Dao.java"),
	DAO_XML("daoXML", Resources.DAO_PACKAGE,Resources.DAO_XML_TEMPLATE,"Dao.xml"),
	SERVICE("service", Resources.SERVICE_PACKAGE,Resources.SERVICE_TEMPLATE,"Service.java"),
	CONTROLLER("controller", Resources.CONTROLLER_PACKAGE,Resources.CONTROLLER_TEMPLATE,"Controller.java"),
	JSP_LIST("jsp_list", null,Resources.JSP_LIST_TEMPLATE,"List.jsp"),
	JSP_INPUT("jsp_input", null,Resources.JSP_INPUT_TEMPLATE,"Form.jsp");
	
	
	// 成员变量
	private String type;//文件类型
	private String pakage;//包声明
	private String template;//模板
	private String fileNameExtension;//文件扩展

	// 构造方法
	private FileType(String type, String pkage,String template,String fileNameExtension) {
		this.type = type;
		this.pakage = pkage;
		this.template=template;
		this.fileNameExtension=fileNameExtension;
	}

	public String getType() {
		return type;
	}

	public String getPakage() {
		return pakage;
	}
	
	public String getTemplate(){
		return template;
	}
	
	public String getFileNameExtension(){
		return fileNameExtension;
	}

	public static void main(String[] args) {
		System.out.println(FileType.ENTITY.getPakage());
		System.out.println(FileType.ENTITY.getTemplate());
	}

}
