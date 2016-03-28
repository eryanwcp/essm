/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.eryansky.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 反射工具类.
 *
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-24 下午4:17:27 
 *
 */
public class ReflectionUtils {
    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);


    /**
     * 调用Getter方法.
     *
     * @param target
     *            目标对象Object
     * @param propertyName
     *            属性字段名称
     *
     * @return Object
     */
    public static <T> T invokeGetter(Object target, String propertyName) {
        String getterMethodName = "get" + org.apache.commons.lang3.StringUtils.capitalize(propertyName);
        return (T) invokeMethod(target, getterMethodName, new Class[] {},
                new Object[] {});
    }


    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
        invokeMethodByName(obj, setterMethodName, new Object[] { value });
    }

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    public static Object invokeGetter2(Object obj, String propertyName) {
        Object object = obj;
        for (String name : org.apache.commons.lang3.StringUtils.split(propertyName, ".")){
            String getterMethodName = GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
        }
        return object;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static void invokeSetter2(Object obj, String propertyName, Object value) {
        Object object = obj;
        String[] names = org.apache.commons.lang3.StringUtils.split(propertyName, ".");
        for (int i=0; i<names.length; i++){
            if(i<names.length-1){
                String getterMethodName = GETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
            }else{
                String setterMethodName = SETTER_PREFIX + org.apache.commons.lang3.StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[] { value });
            }
        }
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     *
     * @param target
     *            目标对象Object
     * @param fieldName
     *            字段名称
     *
     * @return Object
     */
    public static <T> T getFieldValue(final Object target,
                                      final String fieldName) {
        Assert.notNull(target, "target不能为空");
        Assert.notNull(fieldName, "fieldName不能为空");

        Field field = getAccessibleField(target, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("找不到字段 [" + fieldName
                    + "] 在对象  [" + target + "] 里");
        }

        Object result = null;
        try {
            result = field.get(target);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return (T) result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     *
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {//NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     *
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     *
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao<User>
     *
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     */
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     *
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     */
    public static Class getClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getTargetClass(Object target) {
        Assert.notNull(target, "target不能为空");
        return getTargetClass(target.getClass());

    }

    /**
     * 获取Class如果被cglib AOP过的对象或对象为CGLIB的Class，将获取真正的Class类型
     *
     * @param targetClass  对象
     *
     * @return Class
     */
    public static Class<?> getTargetClass(Class<?> targetClass) {

        Assert.notNull(targetClass, "targetClass不能为空");

        Class clazz = targetClass;
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }


    /**
     * 获取对象中的注解
     *
     * @param target
     *            目标对象Object
     * @param annotationClass
     *            注解
     *
     * @return Object
     */
    public static <T> T getAnnotation(Object target, Class annotationClass) {
        Assert.notNull(target, "target不能为空");
        return (T) getAnnotation(target.getClass(), annotationClass);
    }

    /**
     * 获取对象中的注解
     *
     * @param targetClass
     *            目标对象Class
     * @param annotationClass
     *            注解类型Class
     *
     * @return Object
     */
    public static <T extends Annotation> T getAnnotation(Class targetClass,
                                                         Class annotationClass) {
        Assert.notNull(targetClass, "targetClass不能为空");
        Assert.notNull(annotationClass, "annotationClass不能为空");

        if (targetClass.isAnnotationPresent(annotationClass)) {
            return (T) targetClass.getAnnotation(annotationClass);
        }

        return null;
    }

    /**
     * 获取Object对象中所有annotationClass类型的注解
     *
     * @param target
     *            目标对象Object
     * @param annotationClass
     *            Annotation类型
     *
     * @return {@link java.lang.annotation.Annotation}
     */
    public static <T extends Annotation> List<T> getAnnotations(Object target,
                                                                Class annotationClass) {
        Assert.notNull(target, "target不能为空");
        return getAnnotations(getTargetClass(target), annotationClass);
    }

    /**
     *
     * 获取对象中的所有annotationClass注解
     *
     * @param targetClass
     *            目标对象Class
     * @param annotationClass
     *            注解类型Class
     *
     * @return List
     */
    public static <T extends Annotation> List<T> getAnnotations(
            Class targetClass, Class annotationClass) {
        Assert.notNull(targetClass, "targetClass不能为空");
        Assert.notNull(annotationClass, "annotationClass不能为空");

        List<T> result = new ArrayList<T>();
        Annotation annotation = targetClass.getAnnotation(annotationClass);
        if (annotation != null) {
            result.add((T) annotation);
        }
        Constructor[] constructors = targetClass.getDeclaredConstructors();
        // 获取构造方法里的注解
        CollectionUtils.addAll(result,
                getAnnotations(constructors, annotationClass).iterator());

        Field[] fields = targetClass.getDeclaredFields();
        // 获取字段中的注解
        CollectionUtils.addAll(result, getAnnotations(fields, annotationClass)
                .iterator());

        Method[] methods = targetClass.getDeclaredMethods();
        // 获取方法中的注解
        CollectionUtils.addAll(result, getAnnotations(methods, annotationClass)
                .iterator());

        for (Class<?> superClass = targetClass.getSuperclass(); superClass == null
                || superClass == Object.class; superClass = superClass
                .getSuperclass()) {
            List<T> temp = (List<T>) getAnnotations(superClass, annotationClass);
            if (CollectionUtils.isNotEmpty(temp)) {
                CollectionUtils.addAll(result, temp.iterator());
            }
        }

        return result;
    }

    /**
     * 获取field的annotationClass注解
     *
     * @param field
     *            field对象
     * @param annotationClass
     *            annotationClass注解
     *
     * @return {@link java.lang.annotation.Annotation}
     */
    public static <T extends Annotation> T getAnnotation(Field field,
                                                         Class annotationClass) {

        Assert.notNull(field, "field不能为空");
        Assert.notNull(annotationClass, "annotationClass不能为空");

        field.setAccessible(true);
        if (field.isAnnotationPresent(annotationClass)) {
            return (T) field.getAnnotation(annotationClass);
        }
        return null;
    }

    /**
     * 获取field数组中匹配的annotationClass注解
     *
     * @param fields
     *            field对象数组
     * @param annotationClass
     *            annotationClass注解
     *
     * @return List
     */
    public static <T extends Annotation> List<T> getAnnotations(Field[] fields,
                                                                Class annotationClass) {

        if (ArrayUtils.isEmpty(fields)) {
            return null;
        }

        List<T> result = new ArrayList<T>();

        for (Field field : fields) {
            field.setAccessible(true);
            Annotation annotation = getAnnotation(field, annotationClass);
            if (annotation != null) {
                result.add((T) annotation);
            }
        }

        return result;
    }

    /**
     * 获取method的annotationClass注解
     *
     * @param method
     *            method对象
     * @param annotationClass
     *            annotationClass注解
     *
     * @return {@link java.lang.annotation.Annotation}
     */
    public static <T extends Annotation> T getAnnotation(Method method,
                                                         Class annotationClass) {

        Assert.notNull(method, "method不能为空");
        Assert.notNull(annotationClass, "annotationClass不能为空");

        method.setAccessible(true);
        if (method.isAnnotationPresent(annotationClass)) {
            return (T) method.getAnnotation(annotationClass);
        }
        return null;
    }

    /**
     * 获取method数组中匹配的annotationClass注解
     *
     * @param methods
     *            method对象数组
     * @param annotationClass
     *            annotationClass注解
     *
     * @return List
     */
    public static <T extends Annotation> List<T> getAnnotations(
            Method[] methods, Class annotationClass) {

        if (ArrayUtils.isEmpty(methods)) {
            return null;
        }

        List<T> result = new ArrayList<T>();

        for (Method method : methods) {

            Annotation annotation = getAnnotation(method, annotationClass);
            if (annotation != null) {
                result.add((T) annotation);
            }
        }

        return result;
    }

    /**
     * 获取constructor的annotationClass注解
     *
     * @param constructor
     *            constructor对象
     * @param annotationClass
     *            annotationClass注解
     *
     * @return {@link java.lang.annotation.Annotation}
     */
    public static <T extends Annotation> T getAnnotation(
            Constructor constructor, Class annotationClass) {

        Assert.notNull(constructor, "constructor不能为空");
        Assert.notNull(annotationClass, "annotationClass不能为空");

        constructor.setAccessible(true);

        if (constructor.isAnnotationPresent(annotationClass)) {
            return (T) constructor.getAnnotation(annotationClass);
        }

        return null;
    }

    /**
     * 获取constructors数组中匹配的annotationClass注解
     *
     * @param constructors
     *            constructor对象数组
     * @param annotationClass
     *            annotationClass注解
     *
     * @return List
     */
    public static <T extends Annotation> List<T> getAnnotations(
            Constructor<T>[] constructors, Class annotationClass) {

        if (ArrayUtils.isEmpty(constructors)) {
            return null;
        }

        List<T> result = new ArrayList<T>();

        for (Constructor constructor : constructors) {
            Annotation annotation = getAnnotation(constructor, annotationClass);
            if (annotation != null) {
                result.add((T) annotation);
            }
        }

        return result;
    }
}
