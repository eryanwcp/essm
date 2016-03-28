/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.vo;

import java.io.Serializable;

/**
 * 字段
 * @author 温春平 wencp@jx.tobacco.gov.cn
 */
public class ColumnVo implements Serializable {

    public static final int DEFAULT_LENGTH = 255;
    public static final int DEFAULT_PRECISION = 19;
    public static final int DEFAULT_SCALE = 2;
    private static final long serialVersionUID = 8481141362803875233L;
    /**
     * 字段中文名
     */
     private String name = null;
    /**
     * 字段名code
     */
    private String code = null;
    /**
     * 字段类型
     */
    private String dataType;
    /**
     * 长度
     */
    private int dataLength;
    /**
     * 是否主键
     */
    private boolean primaryKey = false;
    /**
     * 是否自增长
     */
    private boolean autoIncrement = false;
    /**
     * 是否允许空
     */
    private boolean notNull = false;
    /**
     * 是否允许重复
     */
    private boolean unique = false;
    /**
     * 默认值
     */
    private boolean useDefaultValue = false;
    /**
     * 默认值
     */
    private String defaultValue = null;
    /**
     * 总共的数字位数
     */
    private int precision;
    /**
     * 小数点右边的数
     */
    private int scale;
    /**
     * 注释
     */
    private String comment;

    public ColumnVo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }


    public boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean getUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean getUseDefaultValue() {
        return useDefaultValue;
    }

    public void setUseDefaultValue(boolean useDefaultValue) {
        this.useDefaultValue = useDefaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ColumnVo{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dataType='" + dataType + '\'' +
                ", dataLength=" + dataLength +
                ", primaryKey=" + primaryKey +
                ", autoIncrement=" + autoIncrement +
                ", notNull=" + notNull +
                ", unique=" + unique +
                ", useDefaultValue=" + useDefaultValue +
                ", defaultValue='" + defaultValue + '\'' +
                ", precision=" + precision +
                ", scale=" + scale +
                ", comment='" + comment + '\'' +
                '}';
    }
}
