package com.myutils.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author julyme
 * @date 2017年10月19日
 */
public class PropertiesUtils {
	
	private static final String file_name = "myUtils.properties";
	
	private static Properties prop = loadProperties(file_name);
	
	private  static ResourceBundle appRb = ResourceBundle.getBundle("myUtils");
	
	public static String getString(String key, String defaults){
		try {
			return appRb.getString(key);
		} catch (Exception e) {
		}
		
		return defaults;
	}

	public static String getString(String key) {
		if (prop==null) {
			prop = loadProperties(file_name);
			return prop.getProperty(key);
		}
		return prop.getProperty(key);
	}
	
	
	/**
	 * load properties
	 * @param propertyFileName
	 * @return
	 */
	public static Properties loadProperties(String propertyFileName) {
		Properties prop = new Properties();
		InputStreamReader  in = null;
		try {
			URL url = null;
			ClassLoader loder = Thread.currentThread().getContextClassLoader();
			url = loder.getResource(propertyFileName); 
			in = new InputStreamReader(new FileInputStream(url.getPath()), "UTF-8");
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
	
}
