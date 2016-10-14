/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.reflection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;


/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-1-19 下午4:36:03
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

    public BeanUtils() {
        super();
    }

    private static void convert(Object dest, Object orig) throws
            IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            DynaProperty origDescriptors[] =
                    ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((DynaBean) orig).get(name);
                    try {
                        copyProperty(dest, name, value);
                    } catch (Exception e) {
                        ; // Should not happen
                    }

                }
            }
        } else if (orig instanceof Map) {
            Iterator names = ((Map) orig).keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if (PropertyUtils.isWriteable(dest, name)) {
                    Object value = ((Map) orig).get(name);
                    try {
                        copyProperty(dest, name, value);
                    } catch (Exception e) {
                        ; // Should not happen
                    }

                }
            }
        } else
      /* if (orig is a standard JavaBean) */ {
            PropertyDescriptor origDescriptors[] =
                    PropertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
//              String type = origDescriptors[i].getPropertyType().toString();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (PropertyUtils.isReadable(orig, name) &&
                        PropertyUtils.isWriteable(dest, name)) {
                    try {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        copyProperty(dest, name, value);
                    } catch (IllegalArgumentException ie) {
                        ; // Should not happen
                    } catch (Exception e) {
                        ; // Should not happen
                    }

                }
            }
        }

    }


    /**
     * 对象拷贝
     * 数据对象空值不拷贝到目标对象
     *
     * @param databean
     * @param tobean
     * @throws NoSuchMethodException
     * copy
     */
    public static void copyBeanNotNull2Bean(Object databean, Object tobean) throws Exception {
        PropertyDescriptor origDescriptors[] =
                PropertyUtils.getPropertyDescriptors(databean);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
//          String type = origDescriptors[i].getPropertyType().toString();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (PropertyUtils.isReadable(databean, name) &&
                    PropertyUtils.isWriteable(tobean, name)) {
                try {
                    Object value = PropertyUtils.getSimpleProperty(databean, name);
                    if (value != null) {
                        copyProperty(tobean, name, value);
                    }
                } catch (IllegalArgumentException ie) {
                    ; // Should not happen
                } catch (Exception e) {
                    ; // Should not happen
                }

            }
        }
    }


    /**
     * 把orig和dest相同属性的value复制到dest中
     *
     * @param dest
     * @param orig
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void copyBean2Bean(Object dest, Object orig) throws Exception {
        convert(dest, orig);
    }

    public static void copyBean2Map(Map map, Object bean) {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            String propname = pd.getName();
            try {
                Object propvalue = PropertyUtils.getSimpleProperty(bean, propname);
                map.put(propname, propvalue);
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            } catch (InvocationTargetException e) {
                //e.printStackTrace();
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 将Map内的key与Bean中属性相同的内容复制到BEAN中
     *
     * @param bean       Object
     * @param properties Map
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void copyMap2Bean(Object bean, Map properties) throws
            IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            try {
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {
                    continue;
                }
                String className = clazz.getName();
                if (className.equalsIgnoreCase("java.sql.Timestamp")) {
                    if (value == null || value.equals("")) {
                        continue;
                    }
                }
                setProperty(bean, name, value);
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
    }


    /**
     * 自动转Map key值大写
     * 将Map内的key与Bean中属性相同的内容复制到BEAN中
     *
     * @param bean       Object
     * @param properties Map
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void copyMap2Bean_Nobig(Object bean, Map properties) throws
            IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            name = name.toLowerCase();
            try {
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {
                    continue;
                }
                String className = clazz.getName();
                if (className.equalsIgnoreCase("java.sql.Timestamp")) {
                    if (value == null || value.equals("")) {
                        continue;
                    }
                }
                setProperty(bean, name, value);
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
    }

    /**
     * Map内的key与Bean中属性相同的内容复制到BEAN中
     * 对于存在空值的取默认值
     *
     * @param bean         Object
     * @param properties   Map
     * @param defaultValue String
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void copyMap2Bean(Object bean, Map properties, String defaultValue) throws
            IllegalAccessException, InvocationTargetException {
        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            // Identify the property name and value(s) to be assigned
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            try {
                Class clazz = PropertyUtils.getPropertyType(bean, name);
                if (null == clazz) {
                    continue;
                }
                String className = clazz.getName();
                if (className.equalsIgnoreCase("java.sql.Timestamp")) {
                    if (value == null || value.equals("")) {
                        continue;
                    }
                }
                if (className.equalsIgnoreCase("java.lang.String")) {
                    if (value == null) {
                        value = defaultValue;
                    }
                }
                setProperty(bean, name, value);
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
    }

    /**
     * 对一个bean进行深度复制，所有的属性节点全部会被复制
     *
     * @param source
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T deepCopyBean(T source) {
        if (source == null) {
            return null;
        }
        try {
            if (source instanceof Collection) {
                return (T) deepCopyCollection((Collection) source);
            }
            if (source.getClass().isArray()) {
                return (T) deepCopyArray(source);
            }
            if (source instanceof Map) {
                return (T) deepCopyMap((Map) source);
            }
            if (source instanceof Date) {
                return (T) new Date(((Date) source).getTime());
            }
            if (source instanceof Timestamp) {
                return (T) new Timestamp(((Timestamp) source).getTime());
            }
            // 基本类型直接返回原值
            if (source.getClass().isPrimitive() || source instanceof String || source instanceof Boolean
                    || Number.class.isAssignableFrom(source.getClass())) {
                return source;
            }
            if (source instanceof StringBuilder) {
                return (T) new StringBuilder(source.toString());
            }
            if (source instanceof StringBuffer) {
                return (T) new StringBuffer(source.toString());
            }
            Object dest = source.getClass().newInstance();
            BeanUtilsBean bean = BeanUtilsBean.getInstance();
            PropertyDescriptor[] origDescriptors = bean.getPropertyUtils().getPropertyDescriptors(source);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue;
                }

                if (bean.getPropertyUtils().isReadable(source, name) && bean.getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        Object value = deepCopyBean(bean.getPropertyUtils().getSimpleProperty(source, name));
                        bean.getPropertyUtils().setSimpleProperty(dest, name, value);
                    } catch (NoSuchMethodException e) {
                    }
                }
            }
            return (T) dest;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Collection deepCopyCollection(Collection source)
            throws InstantiationException, IllegalAccessException {
        Collection dest = source.getClass().newInstance();
        for (Object o : source) {
            dest.add(deepCopyBean(o));
        }
        return dest;
    }

    private static Object deepCopyArray(Object source)
            throws InstantiationException, IllegalAccessException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        int length = Array.getLength(source);
        Object dest = Array.newInstance(source.getClass().getComponentType(), length);
        if (length == 0) {
            return dest;
        }
        for (int i = 0; i < length; i++) {
            Array.set(dest, i, deepCopyBean(Array.get(source, i)));
        }
        return dest;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map deepCopyMap(Map source)
            throws InstantiationException, IllegalAccessException {
        Map dest = source.getClass().newInstance();
        for (Object o : source.entrySet()) {
            Map.Entry e = (Map.Entry) o;
            dest.put(deepCopyBean(e.getKey()), deepCopyBean(e.getValue()));
        }
        return dest;
    }

}
