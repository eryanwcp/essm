package test.utils;

import java.io.*;


/**
 * 简单的代码行数统计
 */
public class CodeCount {

    static long normalLine = 0;
    static long commentLine = 0;
    static long whiteLine = 0;

    public static void main(String args[]) {
        File f = new File("E:\\MyProject\\GitHub\\essm\\src\\main\\core");
        r(f, ".java");
        p("注释的代码行数:" + commentLine);
        p("空白的代码行数:" + whiteLine);
        p("有效的代码行数:" + normalLine);
        long total = commentLine + normalLine + normalLine;
        p("总行数:" + total);
    }

    /**
     * 递归遍历
     *
     * @param dir    目录
     * @param suffix 匹配后缀
     */
    public static void r(File dir, String suffix) {
        File[] files = dir.listFiles();
        for (File eachfile : files) {
            if (eachfile.isFile()) {
                if (eachfile.getName().matches(".*\\" + suffix)) {
                    countcode(eachfile);
                }
            } else {
                r(eachfile, suffix);
            }
        }
    }


    public static void p(Object obj) {
        System.out.println(obj);
    }

    public static void countcode(File f) {
        BufferedReader br = null;
        boolean bln = false;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = "";
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.matches("^[\\s&&[^\\n]]*$")) {
                        whiteLine += 1;
                    } else if (line.startsWith("/*") && !line.equals("*/")) {
                        commentLine += 1;
                        bln = true;
                    } else if (bln == true) {
                        commentLine += 1;
                        if (line.endsWith("*/")) {
                            bln = false;
                        }
                    } else if (line.startsWith("/*") && line.endsWith("*/")) {
                        commentLine += 1;
                    } else if (line.startsWith("//")) {
                        commentLine += 1;
                    } else {
                        normalLine += 1;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}