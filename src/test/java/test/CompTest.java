/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package test;

import com.beust.jcommander.internal.Lists;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.encode.Encrypt;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-22 
 */
public class CompTest {

    private static String dir1 = "C:\\Users\\ChunPing\\Desktop\\classes";
    private static String dir2 = "D:\\work_jf\\workspace\\nportal\\classes\\artifacts\\nportal_war_exploded\\WEB-INF\\classes";

    public static void main(String[] args) {

        System.out.println(EncodeUtils.urlDecode("_ns:YlAtMTQ2ZGIyNjU0NGUtMTAwMDF8ZDB8Zg__"));
        Map<String,String> map1 = Maps.newHashMap();
        Map<String,String> map2 = Maps.newHashMap();
        File dir1 = new File(CompTest.dir1);
        r1(dir1, map1);

        File dir2 = new File(CompTest.dir2);
        r2(dir2, map2);

        Iterator<String> it = map1.keySet().iterator();
        while (it.hasNext()){
            String str = it.next();
            if(map2.containsKey(str)){
                it.remove();
            }
        }

        for(String str1:map1.keySet()){
            System.out.println(map1.get(str1));
        }

    }

    private static void r1(File dir,Map<String,String> map){
        File[] files = dir.listFiles();
        for(File f:files){
            if(f.isFile()){
                map.put(f.getName(),f.getAbsolutePath());
            }else{
                r1(f, map);
            }
        }
    }

    private static void r2(File dir,Map<String,String> map){
        File[] files = dir.listFiles();
        for(File f:files){
            if(f.isFile()){
                map.put(f.getName(), f.getAbsolutePath());
            }else{
                r2(f, map);
            }
        }
    }
}
