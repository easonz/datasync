package org.zhangpan.datasync.foldermonitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.zhangpan.utils.Constants;

public class FileHelper {

	private static String ENCODE_UTF8 = "utf-8";

	private static Set<String> excludeFiles = new HashSet<String>();
	static {
		excludeFiles.add(Constants.INFO_FILENAME);
		excludeFiles.add(Constants.APP_CONFIG_FILE);
		excludeFiles.add(Constants.LOCAL_CONFIG_FILE);
	}

	public static boolean isExclude(String fileName) {
		for (String name : excludeFiles) {
			if (fileName.endsWith(name)) {
				return true;
			}
		}
		return false;
	}

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

	/**
	 * 获取文件夹下面所有的文件
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] getFiles(File dir) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().equals(Constants.INFO_FILENAME)) {
					return false;
				}
				return pathname.isFile();
			}
		});
	}

	/**
	 * 获取文件夹下面所有的文件夹
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] getDirs(File dir) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
	}

	/**
	 * 创建空xml文档
	 * 
	 * @return
	 */
	public static Document getEmptyDoc() {
		Document doc = DocumentHelper.createDocument();
		Element methods = doc.addElement("nodes");
		return doc;
	}

	/**
	 * 从文件中读入xml
	 * 
	 * @param file
	 * @return
	 * @throws FolderCheckException
	 */
	public static Document fileToXml(File file) throws FolderCheckException {
		try {
			Document doc = new SAXReader().read(file);
			return doc;
		} catch (DocumentException e) {
			throw new FolderCheckException(e);
		}
	}

	/**
	 * 格式化输出xml字符串
	 * 
	 * @param doc
	 * @return
	 * @throws FolderCheckException
	 */
	public static void xmlToFile(Document doc, File file) throws FolderCheckException {

		try {

			ByteArrayOutputStream outs = new ByteArrayOutputStream();

			// 打印出Pretty的样式。即元素之间有换行。
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(outs, format);
			writer.write(doc);

			String xmlStr = outs.toString(ENCODE_UTF8);
			outs.close();
			byte[] xmlBytes = xmlStr.getBytes();
			// 使用这种方法可以防止java不能访问隐藏文件
			RandomAccessFile rf = new RandomAccessFile(file, "rw");
			rf.write(xmlBytes);
			rf.setLength(xmlBytes.length);
			rf.close();

		} catch (Exception e) {
			throw new FolderCheckException(e);
		}
	}

	/**
	 * 获取文件md5值
	 * 
	 * @param file
	 * @return
	 * @throws FolderCheckException
	 */
	public static String getMd5(File file) throws FolderCheckException {
		try {
			if (file.isDirectory())
				return "dir:" + file.getName();
			return DigestUtils.md5Hex(new FileInputStream(file));
		} catch (IOException e) {
			throw new FolderCheckException(e);
		}
	}

}
