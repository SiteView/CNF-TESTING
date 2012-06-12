package SiteView.ecc.tools;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import SiteView.ecc.Activator;

import system.Data.DataException;

/**
 * 文件操作类
 * 
 * @author Administrator
 * 
 */
public class FileTools {
	public static final String PLUGIN_ID = "ecc";

	// 获取文件路径
	public static String getRealPath(String fileName) {
		URL urlentry;
		String strEntry;
		try {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			if (bundle == null)
				throw new DataException("请检查文件的路径", new NullPointerException());
			// get path URL
			urlentry = bundle.getEntry(fileName);
			if (urlentry == null)
				throw new DataException("请检查文件的路径", new NullPointerException());
			strEntry = FileLocator.toFileURL(urlentry).getPath();
			strEntry=strEntry.substring(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DataException("请检查文件的路径", e);
		}
		return strEntry;
	}

	// 获取插件路径
	public static String getPluginPath() {
		return Activator.getDefault().getStateLocation().makeAbsolute()
				.toFile().getAbsolutePath();
	}
}
