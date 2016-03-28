package com.eryansky.codegen.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * 文件操作工具类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:37:21 
 * @version 1.0
 */
public class FileUtil {
	public static void create(String dir, String fileName, String content) throws FileNotFoundException, IOException, Exception {
		File dirFile = new File(dir);
		if (!dirFile.isDirectory())
			dirFile.mkdirs();
		File f = new File(dirFile.getAbsolutePath() + "/" + fileName);
		create(f, content.getBytes());
	}

	/**
	 * 生成一个文件
	 * 
	 * @param createFile
	 * @param cb
	 * @throws Exception
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public static void create(File createFile, byte[] cb) throws Exception, FileNotFoundException, IOException {
		FileOutputStream output = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输出流并对它进行缓冲
			output = new FileOutputStream(createFile);
			outBuff = new BufferedOutputStream(output);
			outBuff.write(cb);
			outBuff.flush();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e1) {
			throw e1;
		} finally {
			try {
				if (outBuff != null)
					outBuff.close();
				if (output != null)
					output.close();
			} catch (IOException e1) {
				throw e1;
			}
		}
	}

}
