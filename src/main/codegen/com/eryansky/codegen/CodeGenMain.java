package com.eryansky.codegen;

import com.eryansky.codegen.db.DataSource;
import com.eryansky.codegen.db.DbFactory;
import com.eryansky.codegen.util.Resources;
import com.eryansky.codegen.vo.DbConfig;
import com.eryansky.codegen.vo.Table;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.utils.AppConstants;

import java.util.List;

public class CodeGenMain {

    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/mp?useUnicode=true&characterEncoding=UTF-8"; // 数据库访问串
    public static final String USERNAME = "root";
    public static final String PASSWORD = "password";
    public static final String SCHEMA = "";


    public static void main(String[] args) {
//        DbConfig dbConfig = new DbConfig(DRIVER,URL,USERNAME,PASSWORD);
        DbConfig dbConfig = new DbConfig(DRIVER,AppConstants.getJdbcUrl(), AppConstants.getJdbcUserName(),AppConstants.getJdbcPassword());

        System.out.println(Resources.BASE_PACKAGE+"."+Resources.MODULE);
        System.out.println(Resources.JAVA_STROE_PATH);
        System.out.println(Resources.JSP_STORE_PATH);
        List<Table> tables = null;
        Builder builder = null;
        DataSource db = null;
        String t = "t_sys_user";//表 通配"%"
        Table table = null;
        try {
            db = DbFactory.create(dbConfig);
            tables = db.getTables(t);
            System.out.println(JsonMapper.getInstance().toJson(tables));
            builder = new Builder(db,tables);
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
