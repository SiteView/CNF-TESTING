package COM.dragonflow.Api;

import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.IServerPropMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.NTCounterBase;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.StandardMonitor.SNMPCPUMonitor;

public class ApiRmiServer extends java.rmi.server.UnicastRemoteObject implements
		APIInterfaces {
	String hostname;
	Registry registry;
	APIGroup apigroup;
	APIMonitor apimonitor;
	static String[] NTCounterGroups = { "SQLServerMonitor",
			"WindowsMediaMonitor", "ADPerformanceMonitor", "ASPMonitor",
			"ColdFusionMonitor", "IISServerMonitor", "RealMonitor" };
	static String[] DispatcherMonitorCounterGroups = { "OracleDBMonitor",
			"PatrolMonitor", "TuxedoMonitor" };
	static String[] MultiContentBaseCounterGroups = {
			"HealthUnixServerMonitor", "LogEventHealthMonitor",
			"MediaPlayerMonitorBase", "MonitorLoadMonitor" };
	static String[] MediaPlayerMonitorBaseCounterGroups = {
			"RealMediaPlayerMonitor", "WindowsMediaPlayerMonitor" };
	static String[] SNMPBaseCounterGroups = { "DynamoMonitor",
			"CheckPointMonitor", "WebLogic5xMonitor" };
	static String[] BrowsableSNMPBaseCounterGroups = { "BrowsableSNMPMonitor",
			"CiscoMonitor", "F5Monitor", "IPlanetAppServerMonitor",
			"IPlanetWSMonitor", "NetworkBandwidthMonitor", "VMWareMonitor" };
	static String[] ServerMonitorCounterGroups = { "AssetMonitor",
			"CPUMonitor", "DiskSpaceMonitor", "MemoryMonitor",
			"NTCounterMonitor", "NTEventLogMonitor", "ScriptMonitor",
			"ServiceMonitor", "UnixCounterMonitor", "WebServerMonitor" };
	static String[] BrowsableExeBaseCounterGroups = { "DB2Monitor",
			"SAPMonitor" };
	static String[] BrowsableBaseCounterGroups = { "BrowsableNTCounterMonitor",
			"BrowsableWMIMonitor", "DatabaseCounterMonitor", "IPMIMonitor",
			"OracleJDBCMonitor", "SiebelMonitor", "WebLogic6xMonitor",
			"WebSphereMonitor" };
	static String[] InterFaceCounterGroups = { "InterfaceMonitor" };

	public ApiRmiServer() throws RemoteException {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			// byte[] ipAddr = addr.getAddress();

			// Get hostname
			hostname = addr.getHostName();
		} catch (Exception e) {
			System.out.println("can't get inet address.");
		}
		int port = 3232;
		System.out.println("RMI server start at this address=" + hostname
				+ ",port=" + port);
		try {
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("kernelApiRmiServer", this);

			apigroup = new APIGroup();
			apimonitor = new APIMonitor();
		} catch (RemoteException e) {
			System.out.println("remote exception" + e);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public static boolean isHave(String[] strs, String s) {
		/*
		 * 此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
		 */
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].indexOf(s) != -1) {// 循环查找字符串数组中的每个字符串中是否包含所有查找的内容
				return true;// 查找到了就返回真，不在继续查询
			}
		}
		return false;// 没找到返回false
	}

	public List<Map<String, Object>> getAllGroupInstances()
			throws SiteViewException {
		List<MonitorGroup> mgs = apigroup.getAllGroupInstances();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (MonitorGroup mg : mgs) {
			SSInstanceProperty assinstanceproperty1[] = apigroup
					.getInstanceProperties(mg.getProperty("_id"),
							APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
			Map<String, Object> nodedata = new HashMap<String, Object>();
			for (int k = 0; k < assinstanceproperty1.length; k++) {
				nodedata.put(assinstanceproperty1[k].getName(),
						assinstanceproperty1[k].getValue());
			}

			nodedata.put("_id", mg.getFullID());
			list.add(nodedata);
		}
		return list;
	}

	public List<Map<String, Object>> getTopLevelGroupInstances()
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			APIGroup apimg = new APIGroup();
			ArrayList<MonitorGroup> mgs = apigroup.getTopLevelGroupInstances();

			for (MonitorGroup mg : mgs) {
				SSInstanceProperty assinstanceproperty1[] = apigroup
						.getInstanceProperties(mg.getProperty("_id"),
								APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
				Map<String, Object> nodedata = new HashMap<String, Object>();
				for (int k = 0; k < assinstanceproperty1.length; k++) {
					nodedata.put(assinstanceproperty1[k].getName(),
							assinstanceproperty1[k].getValue());
				}
				nodedata.put("_id", mg.getFullID());
				list.add(nodedata);
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;

	}

	public List<Map<String, Object>> getChildGroupInstances(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apigroup.getChildGroupInstances(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					MonitorGroup mg = (MonitorGroup) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apigroup
							.getInstanceProperties(mg.getProperty("_id"),
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						nodedata.put(assinstanceproperty1[k].getName(),
								assinstanceproperty1[k].getValue());
					}
					nodedata.put("_id", mg.getFullID());
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public void deleteGroup(String groupID) throws RemoteException,
			SiteViewException {
		apigroup.delete(groupID);
	}

	public void deleteMonitor(String monitorID, String groupID)
			throws RemoteException, SiteViewException {
		apimonitor.delete(monitorID, groupID);
	}

	public List<Map<String, Object>> getMonitorsForGroup(String groupID)
			throws RemoteException, SiteViewException {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			Collection mgs = apimonitor.getMonitorsForGroup(groupID);

			if (mgs.size() > 0) {
				for (Iterator iter = mgs.iterator(); iter.hasNext();) {
					AtomicMonitor monitor = (AtomicMonitor) iter.next();
					SSInstanceProperty assinstanceproperty1[] = apimonitor
							.getInstanceProperties(monitor.getProperty("_id"),
									groupID,
									APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
					Map<String, Object> nodedata = new HashMap<String, Object>();
					for (int k = 0; k < assinstanceproperty1.length; k++) {
						nodedata.put(assinstanceproperty1[k].getName(),
								assinstanceproperty1[k].getValue());
					}
					nodedata.put("_id", monitor.getFullID());
					list.add(nodedata);
				}
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public int getNumOfMonitorsForGroup(String groupID) throws RemoteException,
			SiteViewException {
		return apigroup.getNumOfMonitorsForGroup(groupID);
	}

	static public ArrayList<HashMap<String, String>> getMonitorsData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			APIMonitor apim = new APIMonitor();
			Collection collection = apim.getAllMonitors();
			Vector vector = (Vector) collection;

			Monitor monitor;
			int index = 0;
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				monitor = (Monitor) iterator.next();

				HashMap<String, String> ndata = new HashMap<String, String>();
				ndata.put(new String("Name"),
						monitor.getProperty(AtomicMonitor.pName));
				ndata.put(new String("GroupID"),
						monitor.getProperty(AtomicMonitor.pOwnerID));
				ndata.put(new String("MonitorID"),
						monitor.getProperty(AtomicMonitor.pID));
				ndata.put(new String("Type"),
						monitor.getProperty(AtomicMonitor.pClass));
				list.add(ndata);
			}
		} catch (java.lang.Exception e) {
			System.out.println(e);
		}
		return list;
	}

	public boolean trylogin(String strUser, String strPwd)
			throws RemoteException, SiteViewException {
		jgl.Array array = COM.dragonflow.SiteView.User.findUsersForLogin(
				strUser, strPwd);
		if (array.size() > 0)
			return true;
		else
			return false;
	}

	public void createMonitor(String monitorType, String groupid,
			List<Map<String, String>> paramlist) throws RemoteException,
			SiteViewException {
		// TODO Auto-generated method stub
		int size = getPropertyCount(paramlist);
		SSInstanceProperty[] assinstanceproperty = new SSInstanceProperty[size];
		int j = 0;

		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
			for (Entry<String, String> entry : map.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();
				assinstanceproperty[j] = new SSInstanceProperty(k, v);
				j++;
			}
		}
		COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apimonitor
				.create(monitorType, groupid, assinstanceproperty);
	}

	private int getPropertyCount(List<Map<String, String>> paramlist) {
		int total = 0;
		for (int i = 0; i < paramlist.size(); i++) {
			Map<String, String> map = paramlist.get(i);
			// assinstanceproperty = new SSInstanceProperty[map.size()];
			total += map.size();
		}

		return total;
	}

	/**
	 * Get monitor counters parms:hostname,monitorType return String
	 * monitorcounters
	 */
	public String getMonitorCounters(Map parmsmap) throws RemoteException,
			SiteViewException {
		// TODO Auto-generated method stub
		boolean flag = true;
		String monitorcounters = "";
		String xmldata = "";
		StringBuffer stringbuffer = new StringBuffer("");
		String cachexmlname = "";
		if (isHave(InterFaceCounterGroups, parmsmap.get("monitortype")
				.toString())) {// InterFace Monitor
			SNMPCPUMonitor snmp = new SNMPCPUMonitor();
			snmp.createSnmp(parmsmap.get("serverInterface").toString(), Integer
					.parseInt(parmsmap.get("PortInterface").toString()),
					parmsmap.get("InterfaceSNMPPublic").toString(), Integer
							.parseInt(parmsmap.get("TimeOutInterface")
									.toString()), 800);
			List<String> interfaceportcounterlist = snmp
					.getTree("1.3.6.1.2.1.2.2.1.1");
			List<String> interfacedesccounterlist = snmp
					.getTree("1.3.6.1.2.1.2.2.1.2");
			for(String interfacedesc : interfacedesccounterlist){
				monitorcounters += interfacedesc+",";
			}
		} else {
			AtomicMonitor atomicmonitor = null;
			try {
				atomicmonitor = AtomicMonitor.MonitorCreate(parmsmap.get(
						"monitortype").toString());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isHave(NTCounterGroups, parmsmap.get("monitortype").toString())) {// NTCounterGroups
				atomicmonitor.setProperty(((IServerPropMonitor) atomicmonitor)
						.getServerProperty(), hostname);
				monitorcounters = ((NTCounterBase) atomicmonitor)
						.getCountersContent();
				if (monitorcounters.length() == 0) {
					jgl.Array array1 = ((NTCounterBase) atomicmonitor)
							.getAvailableCounters();
					if (array1.size() > 0) {
						monitorcounters = ((NTCounterBase) atomicmonitor)
								.getDefaultCounters();
					} else {
						flag = false;
					}
				}
			} else if (isHave(BrowsableBaseCounterGroups,
					parmsmap.get("monitortype").toString())) {// BrowsableBaseCounterGroups
				if (parmsmap.get("monitortype").toString()
						.equals("OracleJDBCMonitor")) {
					atomicmonitor.setProperty("_driver",
							parmsmap.get("oracleuserdriver").toString());
					atomicmonitor.setProperty("_connectTimeout",
							parmsmap.get("connectiontimeout").toString());
					atomicmonitor.setProperty("_queryTimeout",
							parmsmap.get("querytimeout").toString());
					atomicmonitor.setProperty("_server",
							parmsmap.get("oracleuserurl").toString());
					atomicmonitor.setProperty("_user",
							parmsmap.get("oracleusername").toString());
					atomicmonitor.setProperty("_password",
							parmsmap.get("oracleuserpwd").toString());
					boolean flag1 = ((COM.dragonflow.SiteView.BrowsableMonitor) atomicmonitor)
							.isUsingCountersCache();// Check if use cache
					if (flag1) {
						cachexmlname = COM.dragonflow.SiteView.BrowsableCache
								.getXmlFileName(atomicmonitor);// get Cache xml
																// name
						xmldata = COM.dragonflow.SiteView.BrowsableCache
								.getXmlFile(cachexmlname);// Get xml data
						monitorcounters = analyticXml();
					}
					if (xmldata.length() == 0) {
						xmldata = ((COM.dragonflow.SiteView.BrowsableMonitor) atomicmonitor)
								.getBrowseData(stringbuffer).trim();
						if (stringbuffer.length() == 0 && flag1) {
							COM.dragonflow.SiteView.BrowsableCache.saveXmlFile(
									cachexmlname, xmldata);
						}
					}
				}

			}
		}
		if (monitorcounters.length() == 0) {
			if (flag) {
				monitorcounters = "No Counters selected";
			} else {
				monitorcounters = "No Counters available for this machine";
			}
		}
		return monitorcounters;
	}

	// 解析返回的XML格式的数据成String
	public static String analyticXml() {
		String xmldata = "";
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = db.parse(new File("d:\\counter.xml"));// 把文件解析成DOCUMENT类型
			Element root = document.getDocumentElement(); // 得到Document的根
			// System.out.println("根节点标记名：" + root.getTagName());
			NodeList nodeList = root.getElementsByTagName("object");
			String objstr = "";
			String counterstr = "";
			String counterString = "";
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node fatherNode = nodeList.item(i);
				// System.out.println("父节点为:" + fatherNode.getNodeName());
				// 把父节点的属性拿出来
				NamedNodeMap attributes = fatherNode.getAttributes();
				for (int j = 0; j < attributes.getLength(); j++) {
					Node attribute = attributes.item(j);
					// System.out.println("object的属性名为:" +
					// attribute.getNodeName()
					// + " 相对应的属性值为:" + attribute.getNodeValue());
					objstr = attribute.getNodeValue();
					if (fatherNode instanceof Element) {
						NodeList childNodes = ((Element) fatherNode)
								.getElementsByTagName("counter");
						for (int k = 0; k < childNodes.getLength(); k++) {
							Node childNode = childNodes.item(k);
							NamedNodeMap sonattributes = childNode
									.getAttributes();
							for (int h = 0; h < sonattributes.getLength(); h++) {
								Node sonnode = sonattributes.item(h);
								// System.out.println("Counter的属性值为:"
								// + sonnode.getNodeValue());
								counterstr = sonnode.getNodeValue();
								counterString = objstr + counterstr;
								// System.out.println(counterString);
							}
							xmldata = xmldata + counterString + ",";
						}

					}

				}

			}
			System.out.println(xmldata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmldata;

	}

	public static void showElem(NodeList nl) {
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);// 得到父节点
			System.out.println("NodeName:" + n.getNodeName());
			if (n.hasChildNodes()) {
				NamedNodeMap attributes = n.getAttributes();
				for (int j = 0; j < attributes.getLength(); j++) {
					Node attribute = attributes.item(j);
					// 得到属性名
					String attributeName = attribute.getNodeName();
					System.out.println("属性名:" + attributeName);
					// 得到属性值
					String attributeValue = attribute.getNodeValue();
					System.out.println("属性值:" + attributeValue);
				}
				NodeList childList = n.getChildNodes();
				for (int x = 0; x < childList.getLength(); x++) {
					Node childNode = childList.item(x);
					// 得到子节点的名字
					String childNodeName = childNode.getNodeName();
					System.out.println("子节点名:" + childNodeName);
					// 得到子节点的值
					String childNodeValue = childNode.getNodeValue();
					System.out.println("子节点值:" + childNodeValue);
				}
			}

			// showElem(n.getChildNodes());// 递归
		}
	}

	public static void main(String[] args) {
		analyticXml();
	}
}
