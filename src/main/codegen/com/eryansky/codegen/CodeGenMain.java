package com.eryansky.codegen;

import com.eryansky.codegen.db.DataSource;
import com.eryansky.codegen.db.DbFactory;
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
//        DbConfig dbConfig = new DbConfig("com.ibm.db2.jcc.DB2Driver",
//                "jdbc:db2://10.36.12.10:50002/dbdw:progressiveStreaming=2;currentSchema=DW;",
//                "db2instz","jxycjf2013");
//        dbConfig.setSchema("DW");
//        DbConfig dbConfig = new DbConfig("com.ibm.db2.jcc.DB2Driver",
//                "jdbc:db2://10.36.12.10:50002/dbods:progressiveStreaming=2;currentSchema=WULIU;",
//                "db2instz","jxycjf2013");
        List<Table> tables = null;
        Builder builder = null;
        DataSource db = null;
        String t = "T_BIZ_CRM_CUST_INFO_DETAIL";//表 通配"%"
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
