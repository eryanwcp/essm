package com.eryansky.codegen;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eryansky.codegen.convert.ConvertHandler;
import com.eryansky.codegen.db.DataSource;
import com.eryansky.codegen.generate.Generate;
import com.eryansky.codegen.generate.JavaGenerate;
import com.eryansky.codegen.generate.JspGenerate;
import com.eryansky.codegen.util.FileType;
import com.eryansky.codegen.vo.Column;
import com.eryansky.codegen.vo.Table;

public class Builder {

	private List<Table> tables = null;
	private Generate generate = null;
	private DataSource dataSource = null;


    public Builder(DataSource dataSource,List<Table> tables) throws Exception {
        this.tables = tables;
        this.dataSource = dataSource;
    }

	public void build() {
		try {
			init();
			entityGenerate();
			daoGenerate();
			serviceGenerate();
			controllerGenerate();
			jspListGenerate();
			jspInputGenerate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void jspInputGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JspGenerate(FileType.JSP_INPUT);
			generate.generate(table);
		}
	}

	public void jspListGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JspGenerate(FileType.JSP_LIST);
			generate.generate(table);
		}
	}

	public void controllerGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JavaGenerate(FileType.CONTROLLER);
			generate.generate(table);
		}
	}


	public void serviceGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JavaGenerate(FileType.SERVICE);
			generate.generate(table);
		}
	}

	public void entityGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JavaGenerate(FileType.ENTITY);
			generate.generate(table);
		}
	}

	public void daoGenerate() throws Exception {
		for (Table table : tables) {
			generate = new JavaGenerate(FileType.DAO);
			generate.generate(table);
		}
	}

	/**
	 * 读取table metadata
	 * 
	 * @throws Exception
	 * @throws Exception
	 */
	public void init() throws Exception {
		try {
			for (Table table : tables) {
				table.setColumns(dataSource.getColumns(table.getTableName()));
				table.setPrimaryKey(dataSource.getPrimaryKeys(table.getTableName()));
				new ConvertHandler().convert(table);
				cleanColumn(table);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			dataSource.close();
		}
	}

	/**
	 * 根据essh进行转换时，主键ID为Long 类型，pojo均继承BaseEntity，所以pojo里不需要再次声明ID变量。
	 * 需要从columns中清除该列，避免pojo重复生成
	 */
	private void cleanColumn(Table table) {
        //要被排除的属性
        List<Column> cleanColumns  = new ArrayList<Column>() ;
		for (Column col : table.getColumns()) {
             //主键ID
			for (int i = 0; i < table.getPrimaryKey().size(); i++) {
				Column pk = table.getPrimaryKey().get(i);
				if (pk.getColumnName().equalsIgnoreCase(col.getColumnName())) {
					table.getPrimaryKey().remove(i);
					table.getPrimaryKey().add(i, col);
				}
			}
            //继承至父类属性
            if(col.getColumnName().equalsIgnoreCase("version")
                    ||col.getColumnName().equalsIgnoreCase("status")
                    ||col.getColumnName().equalsIgnoreCase("create_time")
                    ||col.getColumnName().equalsIgnoreCase("create_user")
                    ||col.getColumnName().equalsIgnoreCase("update_time")
                    ||col.getColumnName().equalsIgnoreCase("update_user"))    {
                cleanColumns.add(col);
            }

		}
		// remove object
		for (Column pk : table.getPrimaryKey())
			table.getColumns().remove(pk);
        //排除父类的属性
        for (Column column : cleanColumns)
            table.getColumns().remove(column);
	}

	public static void main(String args[]) {

	}

}
