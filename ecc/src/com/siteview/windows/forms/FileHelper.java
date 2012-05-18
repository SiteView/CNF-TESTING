package com.siteview.windows.forms;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

public class FileHelper {
	/**
	 * 获取文件路径
	 * 
	 * @return filepath
	 */
	public static String getFilePath(String pluginId, String filePath) {
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}
		URL fullPathString = BundleUtility.find(bundle, filePath);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(filePath);
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return fullPathString.toString();
	}

}
