// FrontEnd Plus GUI for JAD
// DeCompiled : WebLogic6xMonitor.class
package COM.dragonflow.StandardMonitor;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.ThreadPool;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import jgl.Array;
import jgl.HashMap;

public final class WebLogic6xMonitorTest
{
    static NumericProperty pPort;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static StringProperty pWeblogicJar;
    static BooleanProperty pSecure;
    public static StringProperty pServerName;
    static StringProperty pTarget;
    static StringProperty pStatus;
    static int nMaxCounters;
    private static HashMap cache = new HashMap();
    private static ThreadPool webLogicMonitorThreadsPool = new ThreadPool("WebLogicMonitors", null);
//    private static ThreadPool webLogicMonitorThreadsPool;

	public WebLogic6xMonitorTest()
    {

	}

    public boolean updatetest()
    {
        int i = 5;
        String as[] = new String[5];
        String as1[] = new String[5];
		String as2[] = new String[5];
		as[0]="ServerRuntime:myserver/ExecuteQueueRuntime:weblogic.admin.HTTP/PendingRequestCurrentCount";
		as1[0]="cxy1:Location=myserver,Name=weblogic.admin.HTTP,ServerRuntime=myserver,Type=ExecuteQueueRuntime/PendingRequestCurrentCount";
		as[1]="ServerRuntime:myserver/ExecuteQueueRuntime:weblogic.admin.HTTP/ExecuteThreadCurrentIdleCount";
		as1[1]="cxy1:Location=myserver,Name=weblogic.admin.HTTP,ServerRuntime=myserver,Type=ExecuteQueueRuntime/ExecuteThreadCurrentIdleCount";
		as[2]="ServerRuntime:myserver/ExecuteQueueRuntime:weblogic.admin.HTTP/ExecuteThreadTotalCount";
		as1[2]="cxy1:Location=myserver,Name=weblogic.admin.HTTP,ServerRuntime=myserver,Type=ExecuteQueueRuntime/ExecuteThreadTotalCount";
		as[3]="ServerRuntime:myserver/ExecuteQueueRuntime:weblogic.admin.HTTP/PendingRequestOldestTime";
		as1[3]="cxy1:Location=myserver,Name=weblogic.admin.HTTP,ServerRuntime=myserver,Type=ExecuteQueueRuntime/PendingRequestOldestTime";
		as[4]="ServerRuntime:myserver/ExecuteQueueRuntime:weblogic.admin.HTTP/ServicedRequestTotalCount";
		as1[4]="cxy1:Location=myserver,Name=weblogic.admin.HTTP,ServerRuntime=myserver,Type=ExecuteQueueRuntime/ServicedRequestTotalCount";

/*
		for(int j = 0; j < i; j++)
        {
            as[j] = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
            as1[j] = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
	        LogManager.log("RunMonitor", "monitorImplUpdate as[j]:" + as[j]);
			LogManager.log("RunMonitor", "monitorImplUpdate as1[j]:" + as1[j]);
        }
*/
System.out.println("1 ");
        
        String s = monitorImplUpdate(as, as1, as2);
System.out.println("2 ");
        LogManager.log("RunMonitor", "monitorImplUpdate End");

System.out.println("3 ");
        if(stillActive())
            synchronized(this)
            {
//               if(s.equals("OK"))
                {
                    String s1 = "";
                    boolean flag = false;
                    int l = 0;
                    for(int i1 = 0; i1 < i; i1++)
                    {
                        String s3 = as2[i1];
						System.out.println("PropVlaue:"  + s3);
                    }

                }
            }
System.out.println("4 ");
        return true;
    }

    private String monitorImplUpdate(String as[], String as1[], String as2[])
    {
       try
		{
		return (String)callWL6Method("update", new Class[] {
            String[].class, String[].class, String[].class
        }, new Object[] {
            as, as1, as2
        });
	   }
	   catch(Exception exception)
		{
//        Exception exception;
  //      exception;
        LogManager.log("Error", "WebLogic6x Update failed: " + exception.toString());
        return "Update failed. See log for details.";
		}
    }

    private Object callWL6Method(String s, Class aclass[], Object aobj[])
        throws Exception
    {

        Object obj1;
        String s1 = "127.0.0.1";//= getProperty(pServerName);
        String s2 = "7001"; //= getProperty(pPort);
        String s4 = "weblogic";//= getProperty(pUsername);
        String s5 = "weblogic";//= getProperty(pPassword);
        boolean flag = false;//= getPropertyAsBoolean(pSecure);
        String s6 = "E:\\bea\\weblogic81\\server\\lib\\weblogic.jar";//= getProperty(pWeblogicJar);
        String s7;
        if(flag)
            s7 = "https";
        else
            s7 = "http";
        String s8 = s7 + s1 + s2 + s4 + s5 + s6;
        LogManager.log("RunMonitor", "Trace s8:" + s8);
        NonDeferringClassLoader nondeferringclassloader = null;
        synchronized(this)
        {
            nondeferringclassloader = (NonDeferringClassLoader)cache.get(s8);
            if(nondeferringclassloader == null)
            {
                if(s6 != null && !s6.equals(""))
                {
                    String s9 = null;
                    if(s6.charAt(0) == '/')
                        s9 = s6.substring(1);
                    else
                        s9 = s6;
                    String s10 = "file:///" + Platform.getRoot() + File.separator + "classes" + File.separator;
                    URLClassLoader urlclassloader = new URLClassLoader(new URL[] {
                        new URL(s10)
                    });
					LogManager.log("RunMonitor", "Trace s9:" + s9);
                    nondeferringclassloader = new NonDeferringClassLoader(new URL[] {
                        new URL("file:///" + s9)
                    }, urlclassloader);
                } else
                {
                    URL aurl[] = {
                        new URL(s7 + "://" + s1 + "/classes/")
                    };
                    nondeferringclassloader = new NonDeferringClassLoader(aurl);
                }
                cache.put(s8, nondeferringclassloader);
            }
        }
        LogManager.log("RunMonitor", "Trace s6:" + s6);
        LogManager.log("RunMonitor", "Trace s7:" + s7);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        if(classloader != nondeferringclassloader)
            Thread.currentThread().setContextClassLoader(nondeferringclassloader);
        LogManager.log("RunMonitor", "WebLogic6x: Connected");
        LogManager.log("RunMonitor", "Detected WebLogic Version: " + nondeferringclassloader.loadClass("weblogic.version").getMethod("getReleaseBuildVersion", null).invoke(null, null));
        Class class1 = nondeferringclassloader.loadClass("COM.dragonflow.SiteView.Weblogic6xMonitorImpl");

        LogManager.log("RunMonitor", "Trace s1:" + s1);
        LogManager.log("RunMonitor", "Trace s2:" + s2);
        LogManager.log("RunMonitor", "Trace s4:" + s4);
        LogManager.log("RunMonitor", "Trace s5:" + s5);
		Object obj = class1.getConstructors()[0].newInstance(new Object[] {
            s1 + ":" + s2, s4, s5, new Boolean(flag)
        });
        LogManager.log("RunMonitor", "Trace s:" + s);
        LogManager.log("RunMonitor", "Trace aclass:" + aclass);
        Method method = class1.getMethod(s, aclass);
		LogManager.log("RunMonitor", "Trace method:" + method.toString());
		LogManager.log("RunMonitor", "Trace obj:" + obj.toString());
		LogManager.log("RunMonitor", "Trace aobj:" + aobj.toString());
        obj1 = method.invoke(obj, aobj);
		LogManager.log("RunMonitor", "Trace obj1:" + obj1.toString());
        Thread.currentThread().setContextClassLoader(classloader);
        return obj1;
//        Throwable throwable;
  //      throwable;
//        LogManager.log("Error", throwable.toString());
     //   String s3 = throwable.getClass().getName();
  //      if(s3.equals("weblogic.common.internal.VersioningError"))
         //   throw new Exception("Server may be running external jar files. This is a known WebLogic issue and has a few workarounds:\n1. Place the jar file after the weblogic.jar entry in the classpath\n2. Instead of a jar file, keep the external classes in a directory structure and include the root directory instead of the jar file in classpath\n");
    //    else
      //      throw new Exception("Unhandled exception thrown: " + s3 + " See log for details");
    }





    public int getMaxCounters()
    {
        return nMaxCounters;
    }

    public void setMaxCounters(int i)
    {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_WebLogic6xMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

//    public COM.dragonflow.Utils.ThreadPool.SingleThread getThread()
    {
//        COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = webLogicMonitorThreadsPool.getThread();
//        singlethread.setNameIfNeeded(getProperty(pName) + "(" + getProperty(pGroupID) + "/" + getProperty(pID) + ") ");
//        singlethread.setPriorityIfNeeded(7);
  //      thread = singlethread;
//        return thread;
    }

    public boolean releaseOnExit()
    {
        if(Thread.currentThread() instanceof COM.dragonflow.Utils.ThreadPool.SingleThread)
        {
            COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = (COM.dragonflow.Utils.ThreadPool.SingleThread)Thread.currentThread();
            if(singlethread.getPoolName().equals(webLogicMonitorThreadsPool.getPoolName()))
                return singlethread.getCustomProperty();
        }
        return false;
    }

    public boolean stillActive()
    {
        if(Thread.currentThread() instanceof COM.dragonflow.Utils.ThreadPool.SingleThread)
        {
            COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = (COM.dragonflow.Utils.ThreadPool.SingleThread)Thread.currentThread();
            if(singlethread.getPoolName().equals(webLogicMonitorThreadsPool.getPoolName()))
                return singlethread.isSingleThreadAlive();
        }
        return true;
    }

	 public static void main(String[] argv) throws Exception 
	{
        WebLogic6xMonitorTest weblogic6xmonitor;
     
        weblogic6xmonitor = new WebLogic6xMonitorTest();
/*
  //      weblogic6xmonitor.setProperty(pServerName, "127.0.0.1");
    //    weblogic6xmonitor.setProperty(pPort, "7001");
      //  weblogic6xmonitor.setProperty(pUsername, "weblogic");
        //weblogic6xmonitor.setProperty(pPassword, "weblogic");
        weblogic6xmonitor.setProperty(pWeblogicJar, "E:\bea\weblogic81\server\lib\weblogic.jar");*/
		weblogic6xmonitor.updatetest();
/*        StringBuffer stringbuffer;
		String strOutput;
		stringbuffer = new StringBuffer();
        try{
		 strOutput = (String)weblogic6xmonitor.callWL6Method("getBrowseData", new Class[] {
            StringBuffer.class
        }, new Object[] {
            stringbuffer
        });
		}
		catch(Exception exception)
		{
			//Exception exception;
        //exception;
       // LogManager.log("Error", "Static getAvailableApplicationsXML failed: errorStr");
        return ;//stringbuffer.toString();
		}*/
		//LogManager.log("Weblogic", strOutput);
		//System.out.println(strOutput);
		System.out.println("WebLogic6xMonitorTest");
	}
}