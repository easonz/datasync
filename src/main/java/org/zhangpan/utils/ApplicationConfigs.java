package org.zhangpan.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfigs {

	private static Logger logger = LoggerFactory.getLogger(ApplicationConfigs.class);
	private static String fileName = "application.properties";
	private static String configFilePath = "";
	private static Properties properties = null;


	public ApplicationConfigs() {

		if (properties == null) {
			properties = new Properties();
			URL configFile = Thread.currentThread().getContextClassLoader()
					.getResource(fileName);
			//configFilePath = configFile.getPath();
			configFilePath = fileName;
			logger.info("load properties file : " + configFilePath);
			InputStream is = null;
			try {
				is = new FileInputStream(configFilePath);
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void setValue(String key, String value) {
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(configFilePath);
			properties.setProperty(key, value);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			properties.store(fos, " no comments");
			logger.info("store property " + key + " : " + value);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 取出Property。
	 */
	private static String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty;
		}
		return properties.getProperty(key);
	}

	/**
	 * 取出String类型的Property,如果都为Null则抛出异常.
	 */
	public String getProperty(String key) {
		String value = getValue(key);
		if (value == null) {
			logger.info("has no property : " + key);
			//throw new NoSuchElementException();
		}
		return value;
	}

	/**
	 * 取出String类型的Property.如果都为Null則返回Default值.
	 */
	public String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}

	/**
	 * 取出Integer类型的Property.如果都为Null或内容错误则抛出异常.
	 */
	public Integer getInteger(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Integer.valueOf(value);
	}

	/**
	 * 取出Integer类型的Property.如果都为Null則返回Default值，如果内容错误则抛出异常
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Double类型的Property.如果都为Null或内容错误则抛出异常.
	 */
	public Double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Double.valueOf(value);
	}

	/**
	 * 取出Double类型的Property.如果都为Null則返回Default值，如果内容错误则抛出异常
	 */
	public Double getDouble(String key, Integer defaultValue) {
		String value = getValue(key);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	/**
	 * 取出Boolean类型的Property.如果都为Null抛出异常,如果内容不是true/false则返回false.
	 */
	public Boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Boolean.valueOf(value);
	}

	/**
	 * 取出Boolean类型的Propert.如果都为Null則返回Default值,如果内容不为true/false则返回false.
	 */
	public Boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}

}
