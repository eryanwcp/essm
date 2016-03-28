package com.eryansky.codegen.vo;

import java.util.List;

public class Table {

	private String schema;
	private String catalog;
	private String tableName;

	private String tableType;// view, table etc..
	private String remark;
	//
	private String entityName;// entityName
	
	private List<Column> primaryKey;
	private List<Column> foreignKeys;// 外键

	private List<Column> columns;// 所有列


	public Table() {
	}

	public Table(String tableName) {
		this.tableName = tableName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<Column> getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(List<Column> primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<Column> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(List<Column> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Table [catalog=" + catalog + ", columns=" + columns + ", entityName=" + entityName + ", foreignKeys=" + foreignKeys + ", primaryKey=" + primaryKey + ", schema=" + schema + ", tableName=" + tableName + ", tableType=" + tableType + "]";
	}


}
