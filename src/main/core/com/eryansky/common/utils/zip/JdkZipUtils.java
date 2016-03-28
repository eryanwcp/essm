/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 基于JDK的Zip压缩工具类
 * 
 * <pre>
 * 存在问题：压缩时如果目录或文件名含有中文，压缩后会变成乱码
 * </pre>
 * 
 * @author 尔演&Eryan
 */
public class JdkZipUtils {

	public static final int BUFFER_SIZE_DIFAULT = 1024;

	public static void makeZip(String[] inFilePaths, String zipFilePath)
			throws Exception {
		File[] inFiles = new File[inFilePaths.length];
		for (int i = 0; i < inFilePaths.length; i++) {
			inFiles[i] = new File(inFilePaths[i]);
		}
		makeZip(inFiles, zipFilePath);
	}

	public static void makeZip(File[] inFiles, String zipFilePath)
			throws Exception {
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFilePath)));
		for (int i = 0; i < inFiles.length; i++) {
			doZipFile(zipOut, inFiles[i], inFiles[i].getParent());
		}
		zipOut.flush();
		zipOut.close();
	}

	private static void doZipFile(ZipOutputStream zipOut, File file,
			String dirPath) throws FileNotFoundException, IOException {
		if (file.isFile()) {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			String zipName = file.getPath().substring(dirPath.length());
			while (zipName.charAt(0) == '\\' || zipName.charAt(0) == '/') {
				zipName = zipName.substring(1);
			}
			ZipEntry entry = new ZipEntry(zipName);
			zipOut.putNextEntry(entry);
			byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
			int size;
			while ((size = bis.read(buff, 0, buff.length)) != -1) {
				zipOut.write(buff, 0, size);
			}
			zipOut.closeEntry();
			bis.close();
		} else {
			File[] subFiles = file.listFiles();
			for (File subFile : subFiles) {
				doZipFile(zipOut, subFile, dirPath);
			}
		}
	}

	public static void unZip(String zipFilePath, String storePath)
			throws IOException {
		unZip(new File(zipFilePath), storePath);
	}

	public static void unZip(File zipFile, String storePath) throws IOException {
		if (new File(storePath).exists()) {
			new File(storePath).delete();
		}
		new File(storePath).mkdirs();

		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();

			if (zipEntry.isDirectory()) {
			} else {
				String zipEntryName = zipEntry.getName();
				if (zipEntryName.indexOf(File.separator) > 0) {
					String zipEntryDir = zipEntryName.substring(0, zipEntryName
							.lastIndexOf(File.separator) + 1);
					String unzipFileDir = storePath + File.separator
							+ zipEntryDir;
					File unzipFileDirFile = new File(unzipFileDir);
					if (!unzipFileDirFile.exists()) {
						unzipFileDirFile.mkdirs();
					}
				}

				InputStream is = zip.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(new File(storePath
						+ File.separator + zipEntryName));
				byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
				int size;
				while ((size = is.read(buff)) > 0) {
					fos.write(buff, 0, size);
				}
				fos.flush();
				fos.close();
				is.close();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String rootDir = "D:\\chenfeng";
		File[] inFiles = new File(rootDir).listFiles();
		String zipPath = "D:\\ZipDemo.zip";

		makeZip(inFiles, zipPath);

		unZip(zipPath, "D:\\chenfeng_zip");
	}
}