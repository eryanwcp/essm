/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.io;

import com.eryansky.common.exception.ServiceException;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 文件工具类.
 * @author     : 尔演&Eryan eryanwcp@gmail.com
 * @date       : 2013-1-19 下午4:41:56
 */
public class FileUtil {

	/**
	 * 生成随机的文件名 将原始文件名去掉,改为一个UUID的文件名,后缀名以原文件名的后缀为准
	 * 
	 * @param fileName
	 *            原始文件名+后缀
	 * @return
	 */
	public static String generateUUIDFileName(String fileName) {

		UUID uuid = UUID.randomUUID();
		String str = fileName;
		System.out.println(str);
		str = uuid.toString() + "." + str.substring(str.lastIndexOf(".") + 1);
		return str;
	}

	/**
	 * 获得一个文件全路径中的文件名
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件名
	 */
	public static String getFileName(String filePath) {

		filePath = filePath.replace("\\", "/");
		if (filePath.indexOf("/") != -1) {
			return filePath.substring(filePath.lastIndexOf("/") + 1);
		}
		return filePath;
	}

	/**
	 * 拷贝文件
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 */
	public static void copyFile(File src, File dst) {
		try {
			FileInputStream in = null;
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(dst);
				in = new FileInputStream(src);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String dstpath = dst.getAbsolutePath();
				if (dstpath.lastIndexOf("/") != -1) {
					dstpath = dstpath.subSequence(0, dstpath.lastIndexOf("/"))
							.toString();
				} else {
					dstpath = dstpath.subSequence(0, dstpath.lastIndexOf("\\"))
							.toString();
				}
				createDirectory(dstpath);
				copyFile(src, dst);
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 目录不存在的话创建目录
	 * 
	 * @param Directorypath
	 */
	public static void createDirectory(String Directorypath) {
		File file = new File(Directorypath);
		if (!file.exists()) {
			file.mkdir();
			file.mkdirs();
		}
	}

	/**
	 * 目录不存在的话创建目录
	 * 
	 * @param dir
	 */
	public static String checkSaveDir(String dir) {

		File dirFile = new File(dir);
		boolean flag = true;
		if (!dirFile.exists()) {
			flag = dirFile.mkdirs();
		}
		if (flag)
			return dirFile.getAbsolutePath();
		else
			return null;
	}

	/**
	 * 删除文件
	 * 
	 * @param files
	 */
	public static void deleteFile(File... files) {
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.exists()) {
				f.delete();
			}
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param file
	 */
	public static void deleteDirectory(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File[] files = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteDirectory(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			System.out.println("所删除的文件不存在！" + '\n');
		}
	}

	// 文件名转码
	public static String encodeDownloadFileName(String fileName, String agent)
			throws IOException {
		String codedfilename = null;
		if (agent != null) {
			agent = agent.toLowerCase();
		}
		if (null != agent && -1 != agent.indexOf("msie")) {
			String prefix = fileName.lastIndexOf(".") != -1 ? fileName
					.substring(0, fileName.lastIndexOf(".")) : fileName;
			String extension = fileName.lastIndexOf(".") != -1 ? fileName
					.substring(fileName.lastIndexOf(".")) : "";
			String name = prefix;
			int limit = 150 - extension.length();
			if (name.getBytes().length != name.length()) {// zn
				if (getEncodingByteLen(name) >= limit) {
					name = subStr(name, limit);
				}
			} else {// en
				limit = prefix.length() > limit ? limit : prefix.length();
				name = name.substring(0, limit);
			}
			name = URLEncoder.encode(name + extension, "UTF-8").replace('+',
					' ');
			codedfilename = name;
		} else if (null != agent && -1 != agent.indexOf("firefox")) {
			codedfilename = "=?UTF-8?B?"
					+ (new String(Base64.encodeBase64(fileName
							.getBytes("UTF-8")))) + "?=";
		} else if (null != agent && -1 != agent.indexOf("safari")) {
			codedfilename = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		} else if (null != agent && -1 != agent.indexOf("applewebkit")) {
			codedfilename = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
		} else {
			codedfilename = URLEncoder.encode(fileName, "UTF-8").replace('+',
					' ');
		}
		return codedfilename;
	}

	private static int getEncodingByteLen(String sub) {
		int zhLen = (sub.getBytes().length - sub.length()) * 2;
		int enLen = sub.length() * 2 - sub.getBytes().length;
		return zhLen + enLen;
	}

	// 限制名字的长度
	private static String subStr(String str, int limit) {
		String result = str.substring(0, 17);
		int subLen = 17;
		for (int i = 0; i < limit; i++) {
			if (limit < getEncodingByteLen(str.substring(0, (subLen + i) > str
					.length() ? str.length() : (subLen)))) {
				result = str.substring(0, subLen + i - 1);
				break;
			}
			if ((subLen + i) > str.length()) {
				result = str.substring(0, str.length() - 1);
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static String getAppPath(Class cls) {
		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		/*------------------------------------------------------------ 
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径 
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要 
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的 
		  中文及空格路径 
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("realPath----->"+realPath);
		return realPath;
	}
	
	
	/**
     * 读入文件为字符串
     * @param file
     * @return
     */
    public static String readFile(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String data = null;
            while((data = br.readLine())!=null){
                sb.append(data).append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new ServiceException("找不到指定的文件", e);
        } catch (IOException e) {
            throw new ServiceException("读取文件内容时IO异常", e);
        }
        return sb.toString();
    }
    
    /**
     * 指定编码读入文件为字符串
     * @param file
     * @param charSet
     * @return
     */
    public static String readFile(File file, String charSet) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
            String data = null;
            while((data = br.readLine())!=null){
                sb.append(data).append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new ServiceException("找不到指定的文件", e);
        } catch (IOException e) {
            throw new ServiceException("读取文件内容时IO异常", e);
        }
        return sb.toString();
    }
    
}
