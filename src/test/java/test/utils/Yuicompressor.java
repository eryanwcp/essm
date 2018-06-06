/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package test.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 静态文件css/js文件压缩
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14 
 */
public class Yuicompressor {

    private static final String[] DIRS = new String[]{"src/main/webapp/static/app"};

    //    java -jar yuicompressor-2.4.7.jar --type js --charset utf-8 ../src/main/webapp/static/app/modules/disk/disk.js -o ../src/main/webapp/static/app/modules/disk/disk-min.js
    public static void main(String[] args) {
        for(String dir:DIRS){
            r(new File(dir));
        }
        System.out.println(new File("").getAbsolutePath());
    }

    public static void r(File fileOrDir){
        File[] fileNames = fileOrDir.listFiles(new StaticFileFilter());
        for(File f:fileNames){
            if(f.isDirectory()){
                r(f);
            }else{
                StringBuffer cmd = new StringBuffer();
                cmd.append("java -jar doc/yuicompressor-2.4.7.jar --charset utf-8 --type ");
                String fileName = f.getAbsolutePath();
                if(fileName.endsWith(".js")){
                    cmd.append("js ")
                            .append(fileName)
                            .append(" -o ")
                            .append(fileName.replaceAll(".js", ".min.js"))
                    .append("\r\n");

                }else if(fileName.endsWith(".css")){
                    cmd.append("css ")
                            .append(fileName)
                            .append(" -o ")
                            .append(fileName.replaceAll(".css", ".min.css"))
                            .append("\r\n");
                }else{
                    System.out.println("error "+ fileName);
                }
                System.out.println(cmd);
                execCommand(cmd.toString());

            }


        }
    }

    static class StaticFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory())
                return true;
            else {
                String name = pathname.getName();
                if ((name.endsWith(".js") && !name.endsWith(".min.js")) || (name.endsWith(".css") && !name.endsWith(".min.css")))
                    return true;
                else
                    return false;
            }
        }
    }


    public static String execCommand(String command){
        Runtime runtime = Runtime.getRuntime();
        String errorMSG = "";

        try {
            String[] args = new String[]{"cmd","/c",command};
            //String[] args = new String[]{"sh","-?/c",command};

            Process pro = runtime.exec(args);
            //Process pro = runtime.exec("c://///////.exe");

            InputStream in = pro.getErrorStream();
            InputStreamReader isr = new InputStreamReader(in);

            BufferedReader br = new BufferedReader(isr);

            String line = null;

            while ( (line = br.readLine()) != null){
                errorMSG += line+"\n";
                System.out.println(errorMSG);
            }

            //检查命令是否失败
            try {
                if(pro.waitFor()!=0){
                    System.err.println("exit value:" + pro.exitValue());
                }
            } catch (InterruptedException e) {
                System.err.println();
                e.printStackTrace();

            }

        } catch (IOException e) {
            System.out.println("error Message:"+e.getMessage());
            e.printStackTrace();
        } finally{
            return errorMSG;
        }

    }


}


