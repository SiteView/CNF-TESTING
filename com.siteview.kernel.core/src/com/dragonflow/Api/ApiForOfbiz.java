package com.dragonflow.Api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;


public class ApiForOfbiz
{
	static public ArrayList<HashMap<String, String>> getMonitorsData()
	{		
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try
		{
			com.dragonflow.Api.APIMonitor apim = new APIMonitor();
			Collection collection = apim.getAllMonitors();
			Vector vector = (Vector) collection;

			Monitor monitor;
			int index = 0;
			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				monitor = (Monitor) iterator.next();
				
				HashMap<String, String> ndata = new HashMap<String, String>();
				ndata.put(new String("Name"), monitor.getProperty(AtomicMonitor.pName));
				ndata.put(new String("GroupID"), monitor.getProperty(AtomicMonitor.pOwnerID));
				ndata.put(new String("MonitorID"), monitor.getProperty(AtomicMonitor.pID));
				ndata.put(new String("Type"), monitor.getProperty(AtomicMonitor.pClass));
				list.add(ndata);
			}
		} catch (java.lang.Exception e)
		{
			System.out.println(e);
		}
		return list;
	}
	
	static public String getStr()
	{
		return new String("value test");
	}
	
	static public String getMonitorStr(int index)
	{
		String rstr= new String("");
		ArrayList<HashMap<String, String>> list= getMonitorsData();
		if(index>=list.size())
			return rstr;
		
		HashMap<String, String> ndata= list.get(index);
		rstr= "&nbsp;-- " + (index+1) + "th monitor --<BR>";
		for (String key : ndata.keySet())
			rstr+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + key + "= \"" + ndata.get(key) + "\"<BR>";
		return rstr;
	}
	
	static public void DisplayAllMonitor()
	{
		ArrayList<HashMap<String, String>> list= getMonitorsData();
		System.out.println("\n\n ------------- Display All Monitor: " + list.size() + "------------------");
		int index=0;

		for(HashMap<String, String> ndata: list)
		{
			System.out.println(" -- " + ++index + "th monitor --");
			for (String key : ndata.keySet())
				System.out.println("        " + key + "= \"" + ndata.get(key) + "\"");
		}
		System.out.println(" ------------- Display All Monitor ------------------\n\n");
	}
}
