package COM.dragonflow.itsm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;


/**
 * <p>
 * 读取配置文件properties文件
 * </p>
 */
public class Config {
	public static final String ACTIONPATH = "itsm_siteview9.2.properties";  
	public static final Properties prop = new Properties();  
	/**
	 * 获取参数
	 * @return
	 */
	public static String getReturnStr(String parm){
		try {
			String path = Config.class.getClassLoader().getResource("").toURI().getPath();
			FileInputStream fis = new FileInputStream(new File(path + ACTIONPATH));
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