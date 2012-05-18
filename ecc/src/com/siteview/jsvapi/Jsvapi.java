package com.siteview.jsvapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

import siteview.windows.forms.ImageHelper;

import SiteView.ecc.Activator;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.siteview.windows.forms.FileHelper;

/**
 * swig 封装的 svapi , 部分模板数据缓存在 java 端
 */
public class Jsvapi {
	private final static Logger logger = Logger.getLogger(Jsvapi.class);
	private static GeneralCacheAdministrator admin;
	static {
		try {
			System.loadLibrary("swigsvapi");
		} catch (Exception e) {
			System.err
					.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help."
							+ e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Jsvapi getInstance(String webDir) {
		if (admin == null) {
			logger.info("执行Jsvapi初 始化......;webDir=" + webDir);
			Jsvapi jsvapi = new Jsvapi();
			webDir = new File(jsvapi.getClass().getResource("/../..").getFile())
					.getAbsolutePath();
			logger.info("webDir=" + webDir);
			// jsvapi.prepareSvdbIniFile(webDir);
			InputStream is = null;
			try {
				Properties prop = new Properties();
				is = jsvapi.getClass().getResourceAsStream(
						"/oscache.properties");
				prop.load(is);
				Jsvapi.admin = new GeneralCacheAdministrator(prop);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			jsvapi.setServerAddr(webDir);
			jsvapi.test2();
			return jsvapi;
		}
		return new Jsvapi();
	}
	static void getdll(){
		try
		{
			System.loadLibrary("swigsvapi");
		} catch (Exception e)
		{
			System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help." + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Jsvapi getInstance() {
		return getInstance(FileHelper.getFilePath(Activator.PLUGIN_ID, "files/svapi.ini"));
	}

	/**
	 * 本函数的功能：读取 .dll 所在目录下的 svapi.ini ,设置与 svdb 通讯的强制地址。
	 */
	public String getConfigFile() {
		return configFile;
	}

	private String configFile = null;

	public void setServerAddr(String path) {
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		Map<String, String> ndata = new FastMap<String, String>();
		StringBuilder estr = new StringBuilder();

		ndata.put("dowhat", "SetSvdbAddrByFile");
		String sep = System.getProperty("file.separator");
		configFile = path + sep + "svapi.ini";
		ndata.put("filename", configFile);
		boolean ret = GetUnivData(fmap, ndata, estr);

		// DisplayUtilMapInMap(fmap);
		logger.info("SetSvdbAddrByFile:" + ret);
		logger.info("estr:" + estr);
	}

	/**
	 * 本函数的功能：获取与 svdb 通讯的强制地址。
	 */
	public void test2() {
		Jsvapi svapi = new Jsvapi();
		Map<String, Map<String, String>> fmap = new FastMap<String, Map<String, String>>();
		Map<String, String> ndata = new FastMap<String, String>();
		StringBuilder estr = new StringBuilder();

		ndata.put("dowhat", "GetSvdbAddr");
		boolean ret = svapi.GetUnivData(fmap, ndata, estr);

		DisplayUtilMapInMap(fmap);
		logger.info("GetUnivData:" + ret);
		logger.info("estr:" + estr);
	}

	/**
	 * swig 封装的 svapi , 此构造函数装载 svapi.dll 等到 jvm 中,并为模板数据缓存初始化
	 */
	public Jsvapi() {

	}

	/**
	 * 此函数对应于 scasvapi.h(即scasvapi.dll) 中的 c++ 函数 GetUnivData
	 * 
	 * @fmap 从 svdb 服务器请求获得的数据
	 * @inwhat 传入的请求
	 * @estr 返回的错误信息 便于调试
	 * 
	 */
	public boolean GetUnivData(Map<String, Map<String, String>> fmap,
			Map<String, String> inwhat, StringBuilder estr) {
		boolean gocache = false;
		try {
			String dowhat = inwhat.get("dowhat");
			if (dowhat.contains("LoadResource")
					|| dowhat.contains("MonitorTemplet")
					|| dowhat.contains("EntityTemplet")
					|| dowhat.contains("EntityGroup"))
				gocache = true;
		} catch (Exception e) {
		}
		if (gocache)
			return GetUnivData_Cache(fmap, inwhat, estr);

		StringMap smap = UtilMapToStringMap(inwhat);
		ForestMap fsmap = new ForestMap();
		MyString mestr = new MyString();

		boolean ret = SwigSvapi.swig_GetUnivData(fsmap, smap, mestr);

		ForestMapToUtilMapInMap(fmap, fsmap);
		estr.delete(0, estr.length());
		estr.append(mestr.getStr());
		return ret;
	}

	/**
	 * 此函数对应于 scasvapi.h(即scasvapi.dll) 中的 c++ 函数 SubmitUnivData
	 * 
	 * @fmap 要提交给 svdb 服务器的数据
	 * @inwhat 传入的请求
	 * @estr 返回的错误信息 便于调试
	 * 
	 */
	public boolean SubmitUnivData(Map<String, Map<String, String>> fmap,
			Map<String, String> inwhat, StringBuilder estr) {
		StringMap smap = UtilMapToStringMap(inwhat);
		ForestMap fsmap = UtilMapInMapToForestMap(fmap);
		MyString mestr = new MyString();

		boolean ret = SwigSvapi.swig_SubmitUnivData(fsmap, smap, mestr);

		ForestMapToUtilMapInMap(fmap, fsmap);
		estr.delete(0, estr.length());
		estr.append(mestr.getStr());
		return ret;
	}

	/**
	 * 此函数对应于 scasvapi.h(即scasvapi.dll) 中的 c++ 函数 GetForestData
	 * 
	 * @fmap 从 svdb 服务器请求获得的树数据
	 * @inwhat 传入的请求
	 * @estr 返回的错误信息 便于调试
	 * 
	 */
	public boolean GetForestData(List<Map<String, String>> vmap,
			Map<String, String> inwhat, StringBuilder estr) {
		StringMap smap = UtilMapToStringMap(inwhat);
		ForestVector fvec = new ForestVector();
		MyString mestr = new MyString();

		boolean ret = SwigSvapi.swig_GetForestData(fvec, smap, mestr);

		ForestArrayListToUtilMapInArrayList(vmap, fvec);
		estr.delete(0, estr.length());
		estr.append(mestr.getStr());
		return ret;
	}

	/**
	 * 显示 java.util.HashMap 的内容
	 */
	public void DisplayUtilMap(Map<String, String> ndata) {
		if (ndata == null)
			return;

		for (String key : ndata.keySet())
			logger.info("        " + key + "= \"" + ndata.get(key) + "\"");
	}

	/**
	 * 显示 java.util.HashMap in HashMap 的内容
	 */
	public void DisplayUtilMapInMap(Map<String, Map<String, String>> fmap) {
		if (fmap == null)
			return;

		logger.info("   -- Display UtilMapInMap begin (" + fmap.size()
				+ " node)" + " -- ");
		for (String key : fmap.keySet()) {
			Map<String, String> ndata = fmap.get(key);
			logger.info("     ---- " + key + " (" + ndata.size() + " key)"
					+ " ----");
			DisplayUtilMap(ndata);
		}
		logger.info("   -- Display UtilMapInMap end (" + fmap.size() + " node)"
				+ " -- ");
	}

	/**
	 * 显示 java.util.HashMap in List 的内容
	 */
	public void DisplayUtilMapInArrayList(List<Map<String, String>> fmap) {
		if (fmap == null)
			return;

		logger.info("   -- Display UtilMapInArrayList begin (" + fmap.size()
				+ " node)" + " -- ");
		for (int key = 0; key < fmap.size(); ++key) {
			Map<String, String> ndata = fmap.get(key);
			logger.info("     ---- No. " + (key + 1) + " (" + ndata.size()
					+ " key)" + " ----");
			DisplayUtilMap(ndata);
		}
		logger.info("   -- Display UtilMapInArrayList end (" + fmap.size()
				+ " node)" + " -- ");
	}

	private boolean GetUnivData_Cache(Map<String, Map<String, String>> fmap,
			Map<String, String> inwhat, StringBuilder estr) {
		String myKey = inwhat.toString();
		Map<String, Map<String, String>> tfmap = new FastMap<String, Map<String, String>>();

		boolean ret = true;
		try {
			tfmap = (Map<String, Map<String, String>>) admin.getFromCache(
					myKey, 10);
		} catch (NeedsRefreshException nre) {
			try {
				try {
					StringMap smap = UtilMapToStringMap(inwhat);
					ForestMap fsmap = new ForestMap();
					MyString mestr = new MyString();

					ret = SwigSvapi.swig_GetUnivData(fsmap, smap, mestr);

					ForestMapToUtilMapInMap(tfmap, fsmap);
					estr.delete(0, estr.length());
					estr.append(mestr.getStr());
				} catch (Exception e) {
					ret = false;
					estr.append(e.toString() + ";  ");
				}

				if (ret)
					admin.putInCache(myKey, tfmap);
				else
					admin.cancelUpdate(myKey);

			} catch (Exception ex) {
				tfmap = (Map<String, Map<String, String>>) nre
						.getCacheContent();
				admin.cancelUpdate(myKey);
			}
		}

		fmap.clear();
		if (!tfmap.isEmpty())
			fmap.putAll(tfmap);
		return ret;
	}

	/**
	 * 将 java.util.HashMap ，转换为 swig 的 StringMap
	 */
	private StringMap UtilMapToStringMap(Map<String, String> ndata) {
		StringMap smap = new StringMap();
		if (ndata == null)
			return smap;

		for (String key : ndata.keySet())
			smap.set(key, ndata.get(key));
		return smap;
	}

	/**
	 * 将 java.util.HashMap in HashMap，转换为 swig 的 ForestMap
	 */
	private ForestMap UtilMapInMapToForestMap(
			Map<String, Map<String, String>> fmap) {
		ForestMap fsmap = new ForestMap();
		if (fmap == null)
			return fsmap;

		for (String key : fmap.keySet())
			fsmap.set(key, UtilMapToStringMap(fmap.get(key)));
		return fsmap;
	}

	/**
	 * 将 swig 的 StringMap ，转换为 java.util.HashMap
	 */
	private Map<String, String> StringMapToUtilMap(StringMap smap) {
		Map<String, String> ndata = new FastMap<String, String>();
		if (smap == null)
			return ndata;

		MyBool mb = new MyBool();
		MyString nextkey = new MyString();
		while (SwigSvapi.swig_SNextKey(smap, nextkey, mb)) {
			String key = nextkey.getStr();
			ndata.put(key, smap.get(key));
			// Thread.currentThread().yield();
		}
		return ndata;
	}

	/**
	 * 将 swig 的 ForestMap ，转换为 java.util.HashMap in HashMap
	 */
	private void ForestMapToUtilMapInMap(Map<String, Map<String, String>> fmap,
			ForestMap fsmap) {
		fmap.clear();
		if (fsmap == null)
			return;

		MyBool mb = new MyBool();
		MyString nextkey = new MyString();
		while (SwigSvapi.swig_FNextKey(fsmap, nextkey, mb)) {
			String key = nextkey.getStr();
			fmap.put(key, StringMapToUtilMap(fsmap.get(key)));
			// Thread.currentThread().yield();
		}
		return;
	}

	/**
	 * 将 swig 的 ForestArrayList ，转换为 java.util.HashMap in List
	 */
	private void ForestArrayListToUtilMapInArrayList(
			List<Map<String, String>> fmap, ForestVector fvec) {
		fmap.clear();
		if (fvec == null)
			return;

		long size = fvec.size();
		for (int index = 0; index < size; ++index)
			fmap.add(StringMapToUtilMap(fvec.get(index)));

		return;
	}

	/**
	 * 显示 swig 封装的数据结构 StringMap 的内容
	 */
	private void DisplayStringMap(StringMap smap) {
		if (smap == null)
			return;

		MyBool mb = new MyBool();
		MyString nextkey = new MyString();
		while (SwigSvapi.swig_SNextKey(smap, nextkey, mb)) {
			String key = nextkey.getStr();
			logger.info("        " + key + "= \"" + smap.get(key) + "\"");
			// Thread.currentThread().yield();
		}
	}
	/**
	 * 显示 swig 封装的数据结构 ForestMap 的内容
	 */
	private void DisplayForestMap(ForestMap fsmap) {
		if (fsmap == null)
			return;

		logger.info("   -- Display ForestMap begin (" + fsmap.size() + " node)"
				+ " -- ");

		MyBool mb = new MyBool();
		MyString nextkey = new MyString();
		while (SwigSvapi.swig_FNextKey(fsmap, nextkey, mb)) {
			// Thread.currentThread().yield();
			String key = nextkey.getStr();
			StringMap smap = null;
			try {
				smap = fsmap.get(key);
			} catch (Exception e) {
				logger.info("     Display ForestMap: " + e + " \"" + key + "\"");
				continue;
			}
			logger.info("     ---- " + key + " (" + smap.size() + " key)"
					+ " ----");
			DisplayStringMap(smap);
		}
		logger.info("   -- Display ForestMap end   -- ");
	}
	public static void main(String[] args) {
		getdll();
	}
}
