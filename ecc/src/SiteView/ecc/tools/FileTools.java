package SiteView.ecc.tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import SiteView.ecc.Activator;
import SiteView.ecc.views.EccTreeControl;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

import system.Collections.ICollection;
import system.Data.DataException;
import system.Xml.XmlElement;

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
	public static ICollection getBussCollection(String key,String value,String s){
		ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s, QueryInfoToGet.All);
		//XmlElement[] xmls = new XmlElement[map.size()];
		XmlElement xml=null;
//		int i=0;
//		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
//		while (iterator.hasNext()) {
//			String key = iterator.next().toString();
//			key = key.substring(0, key.indexOf("="));
//			String value=map.get(key);
//			xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
//					Operators.Equals,value);
//			xmls[i++] = xml;
//		}
//		query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(xmls));
		xml=query.get_CriteriaBuilder().FieldAndValueExpression(key,Operators.Equals,value);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = siteviewApi.get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		return iCollenction;
	}
	//ip地址与主机名 互相转换
	public static String getHostName(String ip){
		InetAddress	a;
		String s="";
		try {
			if(ip.startsWith("\\")){
				a=InetAddress.getByName(ip.substring(ip.indexOf("\\")+2));
				s= a.getHostAddress();
			}else{
				a=InetAddress.getByName(ip);
				s= a.getHostName();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static void main(String[]args){
		BusinessObject bo=EccTreeControl.CreateBo("RecId","5275F89B5ECB45B0859D7B1CA60D72F5", "Ecc.PingMonitor");
		System.out.println(bo);
	}
}
