/*******************************************************************************************
 *   Creation:
 *   Author:        zhangchao
 *   Date:          2012-12-19 
 *   Description:   
 *                  
 *   Version:       1.0.0.0
 *   Modification:
 *   Author:
 *   Date:
 *   Description:
 *   Version:
 *******************************************************************************************/
package org.zhangpan.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Administrator
 * 
 */
public class FileUtils {

	/**
	 * 设置文件为隐藏文件
	 * 
	 * @param file
	 */
	public static void setFileHidden(File file) {
		String sets = "attrib +H \"" + file.getAbsolutePath() + "\"";
		try {
			Runtime.getRuntime().exec(sets);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileType(File file) {
		String fileName = file.getName();
		int idx = fileName.lastIndexOf(".");
		if (idx == -1) {
			throw new RuntimeException("Invalid file name");
		}

		return fileName.substring(idx + 1);
	}

	public static String getFileName(File file) {
		String fileName = file.getName();
		int idx = fileName.lastIndexOf(".");
		if (idx == -1) {
			throw new RuntimeException("Invalid file name");
		}

		return fileName.substring(0, idx);
	}

	public static void deleteFile(File file) {
		if (file == null) {
			return;
		}

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File file2 : children) {
				deleteFile(file2);
			}
		} else {
			file.delete();
		}
	}

	public static void deleteChildren(File file) {
		if (file == null || file.isFile()) {
			return;
		}
		File[] children = file.listFiles();
		for (File file2 : children) {
			deleteFile(file2);
		}
	}

	public static String readFileByLines(File file, int lineNum, int size) {
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null
					&& (line >= lineNum)
					&& (line < (lineNum + size))) {
				// 显示行号
				sb.append(tempString);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return sb.toString();
	}

	public static void writeFile(File file, String date) {
		// 使用这种方法可以防止java不能访问隐藏文件
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "rw");
			byte[] bytes = date.getBytes();
			rf.write(bytes);
			rf.setLength(bytes.length);
			rf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rf != null) {
					rf.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
