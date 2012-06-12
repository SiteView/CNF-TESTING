package SiteView.ecc.tools;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import SiteView.ecc.Activator;

import system.Data.DataException;

/**
 * �ļ�������
 * 
 * @author Administrator
 * 
 */
public class FileTools {
	public static final String PLUGIN_ID = "ecc";

	// ��ȡ�ļ�·��
	public static String getRealPath(String fileName) {
		URL urlentry;
		String strEntry;
		try {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			if (bundle == null)
				throw new DataException("�����ļ���·��", new NullPointerException());
			// get path URL
			urlentry = bundle.getEntry(fileName);
			if (urlentry == null)
				throw new DataException("�����ļ���·��", new NullPointerException());
			strEntry = FileLocator.toFileURL(urlentry).getPath();
			strEntry=strEntry.substring(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DataException("�����ļ���·��", e);
		}
		return strEntry;
	}

	// ��ȡ���·��
	public static String getPluginPath() {
		return Activator.getDefault().getStateLocation().makeAbsolute()
				.toFile().getAbsolutePath();
	}
}
