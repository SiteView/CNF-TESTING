/*
 * 
 * Created on 2005-2-16 17:31:07
 *
 * WebSphereMonitorJMX.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>WebSphereMonitorJMX</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import COM.dragonflow.Utils.WebSphere.ConnectionException;
import COM.dragonflow.Utils.WebSphere.WSUtils;
import COM.dragonflow.Utils.WebSphere.WebSphereCounter;
import COM.dragonflow.Utils.WebSphereJMXInterface;

// Referenced classes of package COM.dragonflow.SiteView:
// WebSphereMonitorImpl, WebSphereJMXObject

public class WebSphereMonitorJMX extends WebSphereMonitorImpl {

    WebSphereJMXInterface management;

    Object adminClient;

    Class adminClientClass;

    Hashtable addedMBeans;

    Object statConfigs[];

    HashMap configMap;

    Object perfOName;

    Object serverOName;

    Set objectNameSet;

    HashSet filters;

    private static Properties props;

    static String propsFilename;

    private static final String WEBSPHERE_MBEANS = "WebSphere:*";

    public WebSphereMonitorJMX(String s, String s1, String s2, String s3) throws ConnectionException {
        super(s, s1, s2, s3);
        management = null;
        adminClient = null;
        adminClientClass = null;
        addedMBeans = null;
        statConfigs = null;
        configMap = new HashMap();
        perfOName = null;
        serverOName = null;
        filters = new HashSet();
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX() constructor.");
        }
        connect();
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX() constructor.");
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public void connect() throws ConnectionException {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.connect().");
        }
        try {
            int i = 1;
            while (true) {
                String s = props.getProperty("jmxFilter" + i);
                if (s == null) {
                    break;
                }
                filters.add(s);
                i ++;
            }
            Properties properties = new Properties();
            properties.setProperty("type", props.getProperty("jmxProtocol"));
            properties.setProperty("host", host);
            properties.setProperty("port", port);
            if (username == null && password == null) {
                properties.setProperty("securityEnabled", "false");
            } else {
                properties.setProperty("securityEnabled", "true");
            }
            if (username != null) {
                properties.setProperty("username", username);
            }
            if (password != null) {
                properties.setProperty("password", password);
            }
            Class class1 = Thread.currentThread().getContextClassLoader().loadClass("com.ibm.websphere.management.AdminClientFactory");
            Method method = class1.getMethod("createAdminClient", new Class[] { java.util.Properties.class });
            if (debug) {
                System.err.println("WebSphereMonitorJMX.connect() creating adminClient with clientProps=" + properties + ".");
            }
            adminClient = method.invoke(null, new Object[] { properties });
            if (debug) {
                System.err.println("WebSphereMonitorJMX.connect() created adminClient=" + adminClient + ".");
            }
            adminClientClass = adminClient.getClass();
            management = new WebSphereJMXInterface(adminClientClass);
            Object obj = management.objectNameCtr.newInstance(new Object[] { "WebSphere:*" });
            if (debug) {
                System.err.println("WebSphereMonitorJMX.connect() invoking AdminClient.queryNames() with ObjectName on=WebSphere:*.");
            }
            objectNameSet = (Set) management.queryNamesMethod.invoke(adminClient, new Object[] { obj, null });
            findONames();
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.connect().");
            }
        } catch (Exception exception) {
            error("Exception in WebSphereMonitorJMX.connect(): " + exception.toString(), exception);
            throw new ConnectionException(exception);
        } finally {
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.connect().");
            }
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public boolean getCounterList(StringBuffer stringbuffer) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.getCounterList().");
        }

        try {
            Object aobj[];
            addedMBeans = new Hashtable();
            WebSphereJMXObject webspherejmxobject = new WebSphereJMXObject();
            HashMap hashmap = new HashMap();
            aobj = getStatConfigs(perfOName);
            if (aobj == null) {

                stringbuffer.append("Failed to get StatConfigs from perfOName=" + perfOName + ".  Possible cause is that the WebSphere client jars in the selected WebSphere directory do not match the version of the WebSphere server to be monitored.");
                if (debug) {
                    System.err.println("Leaving WebSphereMonitorJMX.getCounterList().");
                }
                return false;
            }

            for (int i = 0; i < aobj.length; i ++) {
                String s = (String) management.getShortNameMethod.invoke(aobj[i], null);
                configMap.put(s, aobj[i]);
                Object aobj1[] = (Object[]) management.listAllDataMethod.invoke(aobj[i], null);
                if (aobj1.length <= 0) {
                    continue;
                }
                for (int j = 0; j < aobj1.length; j ++) {
                    String s1 = (String) management.getDICommentMethod.invoke(aobj1[j], null);
                    String s2 = (String) management.getDINameMethod.invoke(aobj1[j], null);
                    hashmap.put(s2, s1);
                }

            }

            Object obj = collectStats();
            WebSphereJMXObject webspherejmxobject1 = new WebSphereJMXObject(perfOName, adminClient, management, filters, obj, hashmap, "was:");
            addPerfMBeanObject(webspherejmxobject1, webspherejmxobject, perfOName);
            webspherejmxobject1.setParent(webspherejmxobject);
            if (objectNameSet != null) {
                Object obj1 = null;
                Iterator iterator = objectNameSet.iterator();
                while (iterator.hasNext()) {
                    Object obj2 = iterator.next();
                    if (!addedMBeans.containsKey(obj2)) {
                        WebSphereJMXObject webspherejmxobject2 = new WebSphereJMXObject(obj2, adminClient, management, filters, configMap, perfOName);
                        addObject(webspherejmxobject2, webspherejmxobject, perfOName);
                    }
                }
            }
            webspherejmxobject.purge();
            CharArrayWriter chararraywriter = new CharArrayWriter();
            webspherejmxobject.printXML(new PrintWriter(chararraywriter), 0);
            stringbuffer.append(chararraywriter.toString());
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.getCounterList().");
            }
            return true;
        } catch (Exception e) {
            stringbuffer.append("Error collecting counters: " + e);
            error("Error collecting counters: " + e, e);
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.getCounterList().");
            }
            return false;
        }

        // if(debug) {
        // System.err.println("Leaving WebSphereMonitorJMX.getCounterList().");
        // }
    }

    private WebSphereJMXObject findFiller(String s, String s1, WebSphereJMXObject webspherejmxobject, Object obj) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.findFiller() with name=" + s + " type=" + s1 + "parent=" + webspherejmxobject + " and perfOName=" + obj + ".");
        }
        WebSphereJMXObject webspherejmxobject1 = (WebSphereJMXObject) addedMBeans.get(s);
        if (webspherejmxobject1 == null) {
            if (debug) {
                System.err.println("WebSphereMonitorJMX.findFiller() did not find name=" + s + " in addMBeans Map, creating new WebSphereJMXObject.");
            }
            webspherejmxobject1 = new WebSphereJMXObject(management.objectNameCtr.newInstance(new Object[] { "WebSphere:name=" + s + ",type=" + s1 }), webspherejmxobject, adminClient, management, filters, configMap, obj);
            addedMBeans.put(s, webspherejmxobject1);
        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.findFiller() with retval=" + webspherejmxobject1 + ".");
        }
        return webspherejmxobject1;
    }

    private void addObject(WebSphereJMXObject webspherejmxobject, WebSphereJMXObject webspherejmxobject1, Object obj) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.addObject() with obj=" + webspherejmxobject + " root=" + webspherejmxobject1 + "perfOName=" + obj + ".");
        }
        WebSphereJMXObject webspherejmxobject2 = null;
        try {
            WebSphereJMXObject webspherejmxobject3 = findFiller(webspherejmxobject.getCell(), "cell", webspherejmxobject1, obj);
            WebSphereJMXObject webspherejmxobject4 = findFiller(webspherejmxobject.getNode(), "node", webspherejmxobject3, obj);
            WebSphereJMXObject webspherejmxobject5 = findFiller(webspherejmxobject.getProcess(), "server", webspherejmxobject4, obj);
            WebSphereJMXObject webspherejmxobject6 = findFiller("Additional Metrics", "additional", webspherejmxobject5, obj);
            String s = webspherejmxobject.getType();
            if (s != null) {
                WebSphereJMXObject webspherejmxobject7 = findFiller(s, s, webspherejmxobject6, obj);
                if (webspherejmxobject7 != null) {
                    webspherejmxobject2 = webspherejmxobject7;
                    if (debug) {
                        System.err.println("WebSphereMonitorJMX.addObject() found parentObject=" + webspherejmxobject2 + ".");
                    }
                }
            }
            if (webspherejmxobject2 == null) {
                webspherejmxobject2 = webspherejmxobject6;
                if (debug) {
                    System.err.println("WebSphereMonitorJMX.addObject() found parentObject=" + webspherejmxobject2 + ".");
                }
            }
        } catch (Exception exception) {
            if (debug) {
                System.err.println("Exception in WebSphereMonitorJMX.addObject(): " + exception + ", setting parentObject to root=" + webspherejmxobject1 + ".");
            }
            webspherejmxobject2 = webspherejmxobject1;
        }
        webspherejmxobject.setParent(webspherejmxobject2);
        if (debug) {
            System.err.println("WebSphereMonitorJMX.addObject() invoking addSubObject on parentObject.");
        }
        webspherejmxobject2.addSubObject(webspherejmxobject);
        if (debug) {
            System.err.println("WebSphereMonitorJMX.addObject() adding ObjectName=" + webspherejmxobject.getObjectName() + " to addedMBeans.");
        }
        addedMBeans.put(webspherejmxobject.getObjectName(), webspherejmxobject);
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.addObject().");
        }
    }

    private void addPerfMBeanObject(WebSphereJMXObject webspherejmxobject, WebSphereJMXObject webspherejmxobject1, Object obj) {
        if (webspherejmxobject != null && webspherejmxobject.children != null) {
            Object obj1 = null;
            Object obj2 = null;
            WebSphereJMXObject webspherejmxobject4 = null;
            try {
                WebSphereJMXObject webspherejmxobject2 = findFiller(webspherejmxobject.getCell(), "cell", webspherejmxobject1, obj);
                WebSphereJMXObject webspherejmxobject3 = findFiller(webspherejmxobject.getNode(), "node", webspherejmxobject2, obj);
                webspherejmxobject4 = findFiller(webspherejmxobject.getProcess(), "server", webspherejmxobject3, obj);
            } catch (Exception exception) {
                error("Exception in WebSphereMonitorJMX.addPerfMBeanObject(): " + exception, exception);
                return;
            }
            WebSphereJMXObject webspherejmxobject5;
            for (Iterator iterator = webspherejmxobject.children.iterator(); iterator.hasNext(); webspherejmxobject4.addSubObject(webspherejmxobject5)) {
                webspherejmxobject5 = (WebSphereJMXObject) iterator.next();
                webspherejmxobject5.setParent(webspherejmxobject4);
            }

            addedMBeans.put(webspherejmxobject.getObjectName(), webspherejmxobject);
        }
    }

    protected String getStatData(Object obj) throws IllegalAccessException, InvocationTargetException {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.getStatData() with counterStat=" + obj + ".");
        }
        String s = null;
        String s1 = obj.getClass().getName();
        if ("com.ibm.websphere.pmi.stat.BoundaryStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getUpperBoundMethod.invoke(obj, null));
        } else if ("com.ibm.websphere.pmi.stat.BoundedRangeStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getCurrentMethod.invoke(obj, null));
        } else if ("com.ibm.websphere.pmi.stat.CountStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getCountMethod.invoke(obj, null));
        } else if ("com.ibm.websphere.pmi.stat.DoubleStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getDoubleMethod.invoke(obj, null));
        } else if ("com.ibm.websphere.pmi.stat.RangeStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getRSCurrentMethod.invoke(obj, null));
        } else if ("com.ibm.websphere.pmi.stat.TimeStatisticImpl".equals(s1)) {
            s = String.valueOf(management.getTotalMethod.invoke(obj, null));
        } else {
            s = "n/a";
        }
        if (s == null) {
            s = "n/a";
        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.getStatData() with retval=" + s + ".");
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param obj
     * @return
     */
    protected String getIBMWebSphereValue(String s, Object obj) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJXM.getIBMWebSphereValue2() with counter=" + s + ".");
        }
        String s1 = "n/a";
        s = WebSphereCounter.denormalize(s);
        String s2 = s.substring(12);
        Object obj1 = obj;
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(s2, "/");
            int i = stringtokenizer.countTokens();
            for (int j = 0; j < i; j ++) {
                String s3 = stringtokenizer.nextToken();
                if (s3 != null && s3.length() > 0) {
                    Object obj2 = management.getStatsMethod.invoke(obj1, new Object[] { s3 });
                    if (obj2 != null) {
                        obj1 = obj2;
                    } else {
                        Object obj3 = management.getStatisticMethod.invoke(obj1, new Object[] { s3 });
                        if (obj3 != null) {
                            s1 = getStatData(obj3);
                        }
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            error("Exception in WebSphereMonitorJMX.getIBMWebSphereValue(): " + exception.getMessage(), exception);
            s1 = "Error: Failed to get value for " + s + ". Exception was: " + exception.toString();
        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.getIBMWebSphereValue() with retval=" + s1 + ".");
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public WebSphereCounter[] getCounterValues(WebSphereCounter awebspherecounter[]) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.getCounterValues() with counters=" + awebspherecounter);
        }
        Object obj = null;
        for (int i = 0; i < awebspherecounter.length; i ++) {
            String s = null;
            String s1 = WebSphereCounter.denormalize(awebspherecounter[i].getName());
            if (s1.indexOf("was:/server/") == 0) {
                if (obj == null) {
                    obj = collectStats();
                }
                awebspherecounter[i].setValue(getIBMWebSphereValue(s1, obj));

            } else {
                s = awebspherecounter[i].getObjectNameFromID();
                s1 = awebspherecounter[i].getAttributeNameFromID();
                try {
                    Object obj1 = management.objectNameCtr.newInstance(new Object[] { s });
                    Object obj2 = management.getMBeanInfoMethod.invoke(adminClient, new Object[] { obj1 });
                    Object aobj[] = (Object[]) management.getAttributesMethod.invoke(obj2, null);
                    boolean flag = false;
                    for (int j = 0; j < aobj.length; j ++) {
                        if (((Boolean) management.isReadableMethod.invoke(aobj[j], null)).booleanValue()) {
                            String s3 = (String) management.getNameMethod.invoke(aobj[j], null);
                            if (s1.equals(s3)) {
                                flag = true;
                                break;
                            }
                        }
                    }

                    if (!flag) {
                        Object obj3 = null;
                        try {
                            obj3 = management.getAttributeMethod.invoke(adminClient, new Object[] { obj1, "stats" });
                        } catch (InvocationTargetException invocationtargetexception) {
                            if (debug) {
                                System.err.println("Exception occurred in WebSphereMonitorJMX.getCounterValues(): " + invocationtargetexception);
                            }
                        }

                        if (obj3 == null) {
                            Object aobj1[] = getStatConfigs(perfOName);
                            String s4 = (String) management.getKeyPropertyMethod.invoke(obj1, new Object[] { "type" });
                            for (int k = 0; k < aobj1.length; k ++) {
                                String s5 = (String) management.getMBeanTypeMethod.invoke(aobj1[k], null);
                                if (s5.equals(s4)) {
                                    management.setConfigMethod.invoke(obj3, new Object[] { aobj1[k] });
                                    Object obj4 = management.getStatisticMethod.invoke(obj3, new Object[] { s1 });
                                    if (obj4 != null) {
                                        awebspherecounter[i].setValue(getStatData(obj4));
                                    } else {
                                        awebspherecounter[i].setValue("n/a");
                                    }
                                }
                            }
                        }
                    } else {
                        String s2 = String.valueOf(management.getAttributeMethod.invoke(adminClient, new Object[] { obj1, s1 }));
                        if (s2 == null) {
                            awebspherecounter[i].setValue("n/a");
                        } else {
                            awebspherecounter[i].setValue(s2);
                        }
                    }
                } catch (Exception exception) {
                    awebspherecounter[i].setValue("Error: Failed to get value for " + awebspherecounter[i] + ". Exception: " + exception.toString());
                    error("Exception in WebSphereMonitorJMX.getCounterValues: " + exception, exception);
                }
            }
        }

        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.getCounterValues() and returning counters=" + awebspherecounter + ".");
        }
        return awebspherecounter;
    }

    protected void findONames() throws ConnectionException {
        String s = findPerfMBean();
        findServerMBean(s);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     * @throws ConnectionException
     */
    protected String findPerfMBean() throws ConnectionException {
        String s = "";
        try {
            Object obj = null;
            Iterator iterator = objectNameSet.iterator();
            String s1;
            while (iterator.hasNext()) {
                obj = iterator.next();
                s1 = (String) management.getKeyPropertyMethod.invoke(obj, new Object[] { "type" });
                if (s1 != null && s1.equalsIgnoreCase("perf")) {
                    perfOName = obj;
                    s = (String) management.getKeyPropertyMethod.invoke(obj, new Object[] { "process" });
                    return s;
                }
            }
        } catch (Exception e) {
            error("Exception in WebSphereMonitorJMX.findPerfMBean(): " + e, e);
        }

        if (perfOName == null) {
            throw new ConnectionException("Could not find PerfMBean on WebSphere Application Server.  Is the Performance Monitoring Service enabled?");
        } else {
            return s;
        }

    }

    protected void findServerMBean(String s) throws ConnectionException {
        try {
            Object obj = null;
            Iterator iterator = objectNameSet.iterator();
            String s2;
            String s1;
            while (iterator.hasNext()) {
                obj = iterator.next();
                s1 = (String) management.getKeyPropertyMethod.invoke(obj, new Object[] { "type" });
                if (s1 != null && s1.equalsIgnoreCase("server")) {
                    s2 = (String) management.getKeyPropertyMethod.invoke(obj, new Object[] { "name" });
                    if (s2 != null && s.equals(s2)) {
                        serverOName = obj;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in WebSphereMonitorJMX.findServerMBean(): " + e);
        }
        if (serverOName == null) {
            throw new ConnectionException("Could not find ServerMBean for server " + s + " to match PerfMBean on WebSphere Application Server.  ");
        } else {
            return;
        }
    }

    protected Object[] getStatConfigs(Object obj) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.getStatConfigs() with perfOName=" + obj + ".");
        }
        if (statConfigs != null) {
            return statConfigs;
        }
        try {
            if (obj != null) {
                if (debug) {
                    System.err.println("WebSphereMonitorJMX.getStatConfigs() perfOName was null, invoking the AdminClient.getConfigs() method.");
                }
                statConfigs = (Object[]) management.invokeMethod.invoke(adminClient, new Object[] { obj, "getConfigs", null, null });
            }
        } catch (Exception exception) {
            error("WebSphereMonitorJMX.getStatConfigs(): " + exception, exception);
        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.getStatConfigs() with statConfigs=" + statConfigs + ".");
        }
        return statConfigs;
    }

    protected void bindConfigs(Object obj, Object aobj[]) throws IllegalAccessException, InvocationTargetException {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.bindConfigs() with stats=" + obj + " and configs=" + aobj + ".");
        }
        HashMap hashmap = new HashMap();
        for (int i = 0; i < aobj.length; i ++) {
            String s = (String) management.getShortNameMethod.invoke(aobj[i], null);
            hashmap.put(s, aobj[i]);
        }

        Object aobj1[] = (Object[]) management.getSubStatsMethod.invoke(obj, null);
        if (aobj1 != null) {
            for (int j = 0; j < aobj1.length; j ++) {
                String s1 = (String) management.getStatsNameMethod.invoke(aobj1[j], null);
                management.setConfigMethod.invoke(aobj1[j], new Object[] { hashmap.get(s1) });
            }

        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorJMX.bindConfigs().");
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    protected Object collectStats() {
        if (debug) {
            System.err.println("Entering WebSphereMonitorJMX.collectStats().");
        }
        try {
            Object obj;
            Object aobj[] = { serverOName, new Boolean(true) };
            String as[] = { "javax.management.ObjectName", "java.lang.Boolean" };
            obj = management.invokeMethod.invoke(adminClient, new Object[] { perfOName, "getStatsObject", aobj, as });
            Object aobj1[] = (Object[]) management.invokeMethod.invoke(adminClient, new Object[] { perfOName, "getConfigs", null, null });
            bindConfigs(obj, aobj1);
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.collectStats() and returning mbeanStats=" + obj + ".");
            }
            return obj;
        } catch (Exception e) {
            error("Exception in WebSphereMonitorJMX.collectStats(): " + e, e);
            if (debug) {
                System.err.println("Leaving WebSphereMonitorJMX.collectStats() and returning null.");
            }
            return null;
        }
    }

    static {
        props = new Properties();
        propsFilename = WSUtils.getRoot() + File.separator + "templates.applications" + File.separator + "websphere.props";
        try {
            props.load(new FileInputStream(propsFilename));
        } catch (Exception exception) {
            System.err.println("Cannot open Websphere properties file: " + propsFilename);
        }
    }
}
