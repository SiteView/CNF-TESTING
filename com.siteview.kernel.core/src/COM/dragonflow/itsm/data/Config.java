package COM.dragonflow.itsm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;


/**
 * <p>
 * ��ȡ�����ļ�properties�ļ�
 * </p>
 */
public class Config {
//	public static final String ACTIONPATH = "itsm_siteview9.2.properties";  
	public static final Properties prop = new Properties();  
	/**
	 * ��ȡ����
	 * @return
	 */
	public static String getReturnStr(String filename,String parm){
		try {
			String path = Config.class.getClassLoader().getResource("").toURI().getPath();
			FileInputStream fis = new FileInputStream(new File(path + filename));
			prop.load(fis); 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop.getProperty(parm);
	}
}