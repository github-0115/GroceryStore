package uyun.show.server.domain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uyun.whale.common.util.data.AbstractDynamicObject;
import uyun.whale.common.util.error.ErrorUtil;
import uyun.whale.common.util.system.SystemConfig;
import uyun.whale.common.util.text.TextUtil;

import java.io.*;
import java.util.*;

public class Config extends AbstractDynamicObject {
	private static final String MICRO_START = "${";
	private static final String MICRO_END = "}";
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	private static Config instance;
	private Properties remoteProperties = new Properties();
	private Properties localProperties = new Properties();

	public synchronized static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	public Config() {
		loadLocalProperties();
	}

	Properties getRemoteProperties() {
		return remoteProperties;
	}

	Properties getLocalProperties() {
		return localProperties;
	}

	private void loadLocalProperties() {
		if (!loadLocalProperties("show-server.properties") || !loadLocalProperties("common.properties"))
			logger.warn("Cannot load default local properties files");
		loadLocalProperties("common.properties");
		loadLocalProperties("show-server.properties");
	}

	private boolean loadLocalProperties(String filename) {
		InputStream is;
		File file = searchFile(filename);
		if (file == null) {
			return false;
		}

		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			ErrorUtil.warn(logger, "file not exists: " + file, e);
			logger.info("Load config from default");
			is = Config.class.getResourceAsStream("config-default.properties");
		}

		try {
			localProperties.load(is);
		} catch (Throwable e) {
			ErrorUtil.warn(logger, "read default config failed", e);
		}
		try {
			is.close();
		} catch (IOException e) {
			ErrorUtil.warn(logger, "close file failed", e);
		}
		return true;
	}

	private static File searchFile(String filename) {
		String[] searchPaths = new String[] {
				"./",
				"/conf/lang",
				"/conf/",
				"/../conf/",
				"/../../conf/",
				"/config/",
				"/lib/",
				"/../lib/",
				"/../front/src/main/resources/conf/",
				"/../../build/src/main/resources/conf/",
				"/../../front/build/src/main/resources/conf/",
		};

		String userDir = SystemConfig.getUserDir();
		for (String path : searchPaths) {
			File file = new File(userDir, path + filename);
			logger.debug("Test config file: {}", file);
			if (file.exists()) {
				logger.info("Load config from :" + file);
				return file;
			}
		}

		return null;
	}

	private static String replaceAll(String text, String findString, String replaceString) {
		while (true) {
			int pos = text.indexOf(findString);
			if (pos < 0)
				break;

			text = text.substring(0, pos) + replaceString + text.substring(pos + findString.length());
		}
		return text;
	}

	private String searchProperty(String key) {
		Object value = remoteProperties.get(key);
		if (value == null) {
			value = System.getProperty(key);
			if (value == null)
				value = localProperties.get(key);
		}
		return value == null ? null : value.toString();
	}

	public Object get(String key) {
		String value = ConfigUtils.getValue(key, searchProperty(key));
		if (value != null) {
			while (true) {
				String item = TextUtil.between(value, MICRO_START, MICRO_END);
				if (item == null)
					break;
				String itemValue = searchProperty(item);
				if (itemValue != null)
					value = replaceAll(value, MICRO_START + item + MICRO_END, itemValue);
				else
					throw new RuntimeException("undefined variable: " + MICRO_START + item + MICRO_END);
			}
		}
		return value;
	}

	@Override
	@SuppressWarnings("unused")
	public void set(String s, Object o) {
		localProperties.setProperty(s, o.toString());
	}

	public boolean isDeveloperMode() {
		return get("bat.developer.mode", false);
	}
	
	public boolean isChinese() {
		return "zh_CN".equals(get("uyun.lang", "zh_CN"));
		
	}

	public void dumpConfigItem() {
		if (!logger.isInfoEnabled())
			return;

		Enumeration<?> iter = remoteProperties.propertyNames();
		List<String> names = new ArrayList<>();
		while (iter.hasMoreElements()) {
			names.add(iter.nextElement().toString());
		}
		iter = localProperties.propertyNames();
		names = new ArrayList<>();
		while (iter.hasMoreElements()) {
			names.add(iter.nextElement().toString());
		}
		Collections.sort(names);

		StringBuilder sb = new StringBuilder();
		sb.append("config summary: \n");
		int i = 1;
		for (String key : names) {
			String value = get(key, (String)null);
			sb.append(String.format("%3d. %-40s = %s\n", i, key, value));
			i++;
		}
		logger.info(sb.toString());
	}
}
