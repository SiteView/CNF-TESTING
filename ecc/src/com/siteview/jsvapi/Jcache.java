package com.siteview.jsvapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

public class Jcache
{
	private GeneralCacheAdministrator	admin	= null;
	/*

	public static void main(String[] args)
	{
		Jcache jc = new Jcache();
		
		String temp = new String("temp");
		while (temp.compareToIgnoreCase("q") != 0)
		{
			long start = System.currentTimeMillis();
			try
			{
				for (Integer i = 1; i <= 1; ++i)
					jc.test1(i.toString(), false);
				
			} catch (Exception e)
			{
				System.err.println("\n\n");
				e.printStackTrace();
			}
			logger.info("\n run: " + (float) (System.currentTimeMillis() - start) / 1000 + " s");
			logger.info("press enter key to continue, q + enter to quit.");
			Scanner in = new Scanner(System.in);
			try
			{
				temp = in.nextLine();
			} catch (Exception e)
			{
				System.exit(1);
			}
		}
	}
	*/
	public Jcache()
	{
		InputStream is = null;
		try
		{
			Properties prop = new Properties();
			is = getClass().getResourceAsStream("/oscache.properties");
			prop.load(is);
			admin = new GeneralCacheAdministrator(prop);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}finally{
			if (is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/*
	public void test1(String temp, boolean show)
	{
		Map<String, Map<String, String>> fmap = new HashMap<String, Map<String, String>>();
		Map<String, String> ndata = new HashMap<String, String>();
		StringBuilder estr = new StringBuilder();
		
		boolean ret = GetUnivData_Cache(fmap, ndata, estr);

		logger.info("\n\n");
		if (show)
			Jsvapi.getInstance().DisplayUtilMapInMap(fmap);
		
		logger.info("GetUnivData:" + ret);
		logger.info("estr:" + estr);
		logger.info("get " + fmap.size() + " node");
		logger.info("get " + fmap.get("property").size() + " key");
	}
	
	public static Map<String, Map<String, String>> test51(String temp, boolean show)
	{
		Jsvapi svapi = Jsvapi.getInstance();
		Map<String, Map<String, String>> fmap = new HashMap<String, Map<String, String>>();
		Map<String, String> ndata = new HashMap<String, String>();
		StringBuilder estr = new StringBuilder();
		
		ndata.put("dowhat", "LoadResource");
		boolean ret = svapi.GetUnivData(fmap, ndata, estr);
		if (!ret)
			return null;
		return fmap;
	}
	public void test52(String temp, boolean show)
	{
		String myKey = new String("LoadResource");
		int myRefreshPeriod = 10;
		Map<String, Map<String, String>> fmap = null;
		
		try
		{
			fmap = (Map<String, Map<String, String>>) admin.getFromCache(myKey, myRefreshPeriod);
		} catch (NeedsRefreshException nre)
		{
			try
			{
				fmap = test51(temp, show);
				admin.putInCache(myKey, fmap);
			} catch (Exception ex)
			{
				fmap = (Map<String, Map<String, String>>) nre.getCacheContent();
				admin.cancelUpdate(myKey);
			}
		}
		
		logger.info("\n\n");
		if (show)
			Jsvapi.getInstance().DisplayUtilMapInMap(fmap);
		
		logger.info("get " + fmap.size() + " node");
		logger.info("get " + fmap.get("property").size() + " key");
	}
	private boolean GetUnivData_Cache(Map<String, Map<String, String>> fmap, Map<String, String> inwhat, StringBuilder estr)
	{
		String myKey = new String("LoadResource");
		Map<String, Map<String, String>> tfmap = new HashMap<String, Map<String, String>>();
		
		try
		{
			tfmap = (Map<String, Map<String, String>>) admin.getFromCache(myKey, 10);
		} catch (NeedsRefreshException nre)
		{
			try
			{
				tfmap = test51(" ", false);
				admin.putInCache(myKey, tfmap);
			} catch (Exception ex)
			{
				tfmap = (Map<String, Map<String, String>>) nre.getCacheContent();
				admin.cancelUpdate(myKey);
			}
		}
	
		fmap.clear();
		fmap.putAll(tfmap);
		return true;
	}
	*/
	
}
