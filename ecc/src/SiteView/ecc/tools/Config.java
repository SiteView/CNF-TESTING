package SiteView.ecc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * @author zhongping.wang
 * <p>
 * ��ȡ�����ļ�properties�ļ�
 * </p>
 */
public class Config {
	public static Properties prop = null;

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public static String getReturnStr(String filePath, String parm) {
		try {
			FileInputStream fis = new FileInputStream(new File(filePath));
			prop = new Properties();
			prop.load(fis);
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop.getProperty(parm);
	}
}
