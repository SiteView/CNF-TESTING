/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StatefulMonitor;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Timer;

import jgl.Array;

import org.xml.sax.InputSource;

//import com.dragonflow.topaz.j2ee.comm.HashedSample;
//import com.dragonflow.topaz.j2ee.configuration.SiteViewConfigurator;

// Referenced classes of package COM.dragonflow.StatefulMonitor:
// StatefulConnection, StatefulConnectionUser

public class J2EEConnection extends COM.dragonflow.StatefulMonitor.StatefulConnection
{
    private static class TreeNode
    {

        private String cls;
        private String name;
        private boolean hasCounters;
        private java.util.Map keys;
        private java.util.Map children;

        TreeNode addChild(TreeNode treenode)
        {
            children.put(treenode.cls + ": " + treenode.name, treenode);
            return treenode;
        }

        void addNode(String as[])
            throws java.lang.Exception
        {
            TreeNode treenode = this;
            for(int i = 0; i < as.length; i++)
            {
                TreeNode treenode1 = treenode.getChild(as[i]);
                if(treenode1 == null)
                {
                    treenode1 = treenode.addChild(new TreeNode(keys, as[i]));
                }
                treenode = treenode1;
            }

            treenode.addCounters();
        }

        public void addCounters()
        {
            hasCounters = true;
        }

        public TreeNode getChild(String s)
        {
            return (TreeNode)children.get(s);
        }

        public void toXML(StringBuffer stringbuffer)
        {
            toXML(stringbuffer, 0);
        }

        private void toXML(StringBuffer stringbuffer, int i)
        {
            StringBuffer stringbuffer1 = new StringBuffer();
            for(int j = 0; j < i; j++)
            {
                stringbuffer1.append("  ");
            }

            if(cls == null)
            {
                stringbuffer.append(stringbuffer1 + "<" + keys.get("root") + ">\n");
            } else
            {
                String s = (String)keys.get(cls);
                if(s == null)
                {
                    s = cls;
                }
                stringbuffer.append(stringbuffer1 + "<" + keys.get("object") + " " + keys.get("class") + "=\"" + s + "\" " + keys.get("name") + "=\"" + escape(name) + "\">\n");
                if(hasCounters)
                {
                    stringbuffer.append(stringbuffer1 + "  <" + keys.get("counter") + " " + keys.get("name") + "=\"HPS\" " + keys.get("enabled") + "=\"1\"/>\n");
                    stringbuffer.append(stringbuffer1 + "  <" + keys.get("counter") + " " + keys.get("name") + "=\"ART\" " + keys.get("enabled") + "=\"1\"/>\n");
                }
            }
            TreeNode treenode;
            for(java.util.Iterator iterator = children.values().iterator(); iterator.hasNext(); treenode.toXML(stringbuffer, i + 1))
            {
                treenode = (TreeNode)iterator.next();
            }

            if(cls == null)
            {
                stringbuffer.append(stringbuffer1 + "</" + keys.get("root") + ">");
            } else
            {
                stringbuffer.append(stringbuffer1 + "</" + keys.get("object") + ">\n");
            }
        }

        private String escape(String s)
        {
            String s1 = "";
            java.text.StringCharacterIterator stringcharacteriterator = new StringCharacterIterator(s);
            for(char c = stringcharacteriterator.first(); c != '\uFFFF'; c = stringcharacteriterator.next())
            {
                switch(c)
                {
                case 60: // '<'
                    s1 = s1 + "&lt;";
                    break;

                case 62: // '>'
                    s1 = s1 + "&gt;";
                    break;

                case 38: // '&'
                    s1 = s1 + "&amp;";
                    break;

                case 39: // '\''
                    s1 = s1 + "&apos;";
                    break;

                case 34: // '"'
                    s1 = s1 + "&quot;";
                    break;

                case 10: // '\n'
                case 13: // '\r'
                    s1 = s1 + " ";
                    break;

                default:
                    s1 = s1 + c;
                    break;
                }
            }

            return s1;
        }

        public TreeNode(java.util.Map map)
        {
            cls = null;
            name = null;
            hasCounters = false;
            children = new HashMap();
            keys = map;
        }

        public TreeNode(java.util.Map map, String s)
            throws java.lang.Exception
        {
            this(map);
            int i = s.indexOf(": ");
            if(i < 0)
            {
                throw new Exception("Invalid node name, ': ' expected: " + s);
            } else
            {
                cls = s.substring(0, i);
                name = s.substring(i + 2);
                return;
            }
        }
    }

    public class Monitor
    {

        private java.util.Set counters;
        private int interval;
//        private com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator aggregator;
        private boolean hasResults;
        private String lastError;

        public String getLastError()
        {
            return lastError;
        }

        public void setLastError(String s)
        {
            lastError = s;
        }

        public boolean getHasResults()
        {
            return hasResults;
        }

        public void setHasResults(boolean flag)
        {
            hasResults = flag;
        }

        public java.util.Set getCounters()
        {
            return counters;
        }

        public void setCounters(java.util.Set set)
        {
            counters = set;
        }

        public int getInterval()
        {
            return interval;
        }

        public void setInterval(int i)
        {
            interval = i;
        }

//        public com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator getAggregator()
//        {
//            return aggregator;
//        }
//
//        public void setAggregator(com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator siteviewaggregator)
//        {
//            aggregator = siteviewaggregator;
//        }
//
//        public Monitor(java.util.Set set, int i, com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator siteviewaggregator)
//        {
//            super();
//            hasResults = false;
//            lastError = null;
//            counters = set;
//            interval = i;
//            aggregator = siteviewaggregator;
//        }
    }

    private class SampleData
    {

        public java.util.Map measurment;
        public java.util.Map values;

        public SampleData(java.util.Map map, java.util.Map map1)
        {
            super();
            measurment = map;
            values = map1;
        }
    }

    private class GetMetricDataTask extends java.util.TimerTask
    {

        private J2EEConnection con;

        public void run()
        {
            con.getMetricData(true);
        }

        public GetMetricDataTask(J2EEConnection j2eeconnection1)
        {
            super();
            con = j2eeconnection1;
        }
    }


    private static final String GET_METRIC_LIST_URL = "/?id=10&type=0";
    private static final String GET_METRIC_DATA_URL = "/?id=11&type=0";
    private static final String POST_METRIC_LIST_URL = "/?id=12&type=0";
    private static final String GET_METRIC_LIST_XSL = "J2EEGetList.xsl";
    private static final String GET_METRIC_LIST_SHORT_XSL = "J2EEGetListShort.xsl";
    private static final String GET_METRIC_DATA_XSL = "J2EEGetData.xsl";
    private static final String POST_METRIC_LIST_XSL = "J2EEPostList.xsl";
    private static final String MONITOR_TYPE = "J2EEMonitor";
    private static final String AGGREGATOR_ID = "SiteViewAggregator";
    private String lastError;
    private java.util.Map xmlKeys;
    java.util.Map activeMonitors;
    java.lang.Integer minInterval;
    java.lang.Integer maxInterval;
    java.util.Map counterUnion;
    private java.util.Timer getMetricDataTimer;
    static javax.xml.parsers.DocumentBuilder documentBuilder = null;
//    public static com.dragonflow.topaz.j2ee.configuration.Configurator aggregationConfigurator;
    private static final java.util.Map xmlLongKeys;
    private static final java.util.Map xmlShortKeys;
    private static final java.util.Map xmlToLong;
    private static final String XML_KEY_ROOT = "root";
    private static final String XML_KEY_OBJECT = "object";
    private static final String XML_KEY_CLASS = "class";
    private static final String XML_KEY_NAME = "name";
    private static final String XML_KEY_COUNTER = "counter";
    private static final String XML_KEY_ENABLED = "enabled";
    private static final String XML_KEY_VAL = "val";
    private static final String XML_KEY_GROUP = "group";
    private static final String XML_KEY_METHOD = "Method";
    private static final String XML_KEY_SQL = "SQL";

    public J2EEConnection(StatefulConnectionUser statefulconnectionuser)
    {
        super(statefulconnectionuser);
        activeMonitors = java.util.Collections.synchronizedMap(new HashMap());
        minInterval = new Integer(0x7fffffff);
        maxInterval = new Integer(0x80000000);
        counterUnion = new HashMap();
        getMetricDataTimer = null;
    }

    public void disconnect()
    {
    }

    public boolean connect()
    {
        return true;
    }

    public boolean isInError()
    {
        return false;
    }

    public String getMetricList(StringBuffer stringbuffer)
    {
        String s;
        try
        {
            s = doRequest("/?id=10&type=0", true, null);
        }
        catch(java.lang.Exception exception)
        {
            stringbuffer.append(exception.getMessage());
            return "";
        }
        int i = s.indexOf("<browse_data>");
        if(i > 0)
        {
            s = s.substring(i);
        }
        return s;
    }

    protected String doRequest(String s, boolean flag, String s1)
        throws java.lang.Exception
    {
        if(s == null)
        {
            throw new Exception("Paramater uri cannot be null");
        }
        COM.dragonflow.Utils.SocketSession socketsession = COM.dragonflow.Utils.SocketSession.getSession(null);
        StringBuffer stringbuffer = new StringBuffer(10000);
        String s2 = "http://" + getConnID() + s;
        jgl.Array array = null;
        if(s1 != null)
        {
            array = new Array();
            array.add("Custom-Content: " + s1);
        }
        long al[] = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s2, null, null, null, null, null, array, null, null, null, stringbuffer, 0x989680L, null, 0, 60000, null);
        String s3 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
        if(al[0] == 200L)
        {
            s3 = s3.trim();
            if(s3.startsWith("<") && flag)
            {
                String s5 = s3.substring(0, 50);
                String s4;
                if(s3.indexOf("<P>") >= 0)
                {
                    xmlKeys = xmlShortKeys;
                    s4 = "J2EEGetListShort.xsl";
                } else
                {
                    xmlKeys = xmlLongKeys;
                    s4 = "J2EEGetList.xsl";
                }
                return J2EEConnection.transform(s3, "templates.applications/" + s4);
            } else
            {
                return s3;
            }
        } else
        {
            throw new Exception(COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(al[0]));
        }
    }

    private static String transform(String s, String s1)
        throws java.lang.Exception
    {
        if(s1 == null)
        {
            return s;
        }
        s1 = COM.dragonflow.SiteView.Platform.getRoot() + "/" + s1;
        java.io.CharArrayWriter chararraywriter = new CharArrayWriter();
        try
        {
            StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readFile(s1);
            COM.dragonflow.Utils.XSLUtils.convert(s, stringbuffer.toString(), new PrintWriter(chararraywriter));
        }
        catch(java.io.IOException ioexception)
        {
            ioexception.printStackTrace();
            throw new Exception("Failed to read XSL file: " + s1);
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            throw new Exception("Exception: " + exception);
        }
        return chararraywriter.toString();
    }

    public void addMonitor(COM.dragonflow.SiteView.J2EEMonitor j2eemonitor)
    {
        String s = (String)j2eemonitor.getUniqueID();
        if(activeMonitors.containsKey(s))
        {
            return;
        }
//        Monitor monitor = new Monitor(j2eemonitor.getCounters(), j2eemonitor.getInterval(), com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator.getInstance(1, j2eemonitor.getInterval(), s));
//        synchronized(monitor)
//        {
//            activeMonitors.put(s, monitor);
//            calcCounterUnion();
//            calcInterval();
//        }
        getMetricData(true);
    }

    public void updateMonitor(COM.dragonflow.SiteView.J2EEMonitor j2eemonitor)
    {
        String s = (String)j2eemonitor.getUniqueID();
        Monitor monitor = (Monitor)activeMonitors.get(s);
        if(monitor == null)
        {
            return;
        }
        java.util.Set set = j2eemonitor.getCounters();
        int i = j2eemonitor.getInterval();
        boolean flag = false;
        synchronized(monitor)
        {
            if(!set.equals(monitor.getCounters()))
            {
                monitor.setCounters(set);
                calcCounterUnion();
                flag = true;
            }
            if(i != monitor.getInterval())
            {
                monitor.setInterval(i);
                calcInterval();
                flag = true;
            }
        }
        if(flag)
        {
            getMetricData(true);
        }
    }

    private void removeAggregator(String s)
    {
        Monitor monitor = (Monitor)activeMonitors.get(s);
//        com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator siteviewaggregator = monitor.getAggregator();
//        synchronized(siteviewaggregator)
//        {
//            com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator.removeInstance(s);
//        }
    }

    private synchronized void calcInterval()
    {
        int i = 0x7fffffff;
        int j = 0x80000000;
        synchronized(activeMonitors)
        {
            java.util.Iterator iterator = activeMonitors.values().iterator();
            while (iterator.hasNext()) {
                Monitor monitor = (Monitor)iterator.next();
                int k = monitor.getInterval();
                if(k < i)
                {
                    i = k;
                }
                if(k > j)
                {
                    j = k;
                }
            } 
        }
        if(i != minInterval.intValue())
        {
            synchronized(minInterval)
            {
                minInterval = new Integer(i);
                if(getMetricDataTimer != null)
                {
                    getMetricDataTimer.cancel();
                }
                if(activeMonitors.size() != 0)
                {
                    getMetricDataTimer = new Timer(true);
                    getMetricDataTimer.scheduleAtFixedRate(new GetMetricDataTask(this), (i * 1000) / 4, (i * 1000) / 2);
                }
            }
        }
        if(j != maxInterval.intValue())
        {
            synchronized(maxInterval)
            {
                maxInterval = new Integer(j);
            }
        }
    }

    private synchronized void calcCounterUnion()
    {
        java.util.HashSet hashset = new HashSet();
        synchronized(activeMonitors)
        {
            Monitor monitor;
            for(java.util.Iterator iterator = activeMonitors.values().iterator(); iterator.hasNext(); hashset.addAll(monitor.getCounters()))
            {
                monitor = (Monitor)iterator.next();
            }

        }
        synchronized(counterUnion)
        {
            if(!hashset.equals(counterUnion))
            {
                counterUnion.clear();
                String s;
                for(java.util.Iterator iterator1 = hashset.iterator(); iterator1.hasNext(); counterUnion.put(s, J2EEConnection.parseName(s)))
                {
                    s = (String)iterator1.next();
                }

                postMetricList();
            }
        }
    }

    public void removeUserRef(StatefulConnectionUser statefulconnectionuser)
    {
//        com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator.removeInstance((String)statefulconnectionuser.getUniqueID());
        activeMonitors.remove(statefulconnectionuser.getUniqueID());
        calcCounterUnion();
        calcInterval();
        super.removeUserRef(statefulconnectionuser);
    }

    public void getMetricData(boolean flag)
    {
        String s;
        try
        {
            s = doRequest("/?id=11&type=0", false, null);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("error", "J2EEConnection: Failed to retrieve data from target " + getConnID() + ": " + exception.getMessage());
            lastError = "Failed to retrieve data from server: " + exception.getMessage();
            return;
        }
        if(s.indexOf("Results are null. No measurements were selected") >= 0)
        {
            if(flag)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "J2EEConnection: No measuements were selected for target " + getConnID() + ". Reposting metric list.");
                postMetricList();
                getMetricData(false);
            } else
            {
                COM.dragonflow.Log.LogManager.log("error", "J2EEConnection: No measuements were selected for target " + getConnID() + ". Reposting metric list did not solve problem. Aborting...");
                lastError = "Internal error, see log for details";
            }
        } else
        {
            try
            {
                handleMetricData(s);
                lastError = null;
            }
            catch(java.lang.Exception exception1)
            {
                exception1.printStackTrace();
                COM.dragonflow.Log.LogManager.log("error", "J2EEConnection: Error while parsing metric data from " + getConnID() + ": " + exception1.getMessage());
                lastError = "Failed to parse metric data: " + exception1.getMessage();
            }
        }
    }

    private void handleMetricData(String s)
        throws java.lang.Exception
    {
        long l = java.lang.System.currentTimeMillis() / 1000L;
        org.w3c.dom.Document document = documentBuilder.parse(new InputSource(new StringReader(s)));
        org.w3c.dom.NodeList nodelist = document.getElementsByTagName((String)xmlKeys.get("counter"));
        int i = nodelist.getLength();
        java.util.HashMap hashmap = new HashMap();
        for(int j = 0; j < i; j++)
        {
            org.w3c.dom.Element element = (org.w3c.dom.Element)nodelist.item(j);
            if(element.getAttribute((String)xmlKeys.get("name")).equals("HPS"))
            {
                continue;
            }
            org.w3c.dom.Element element1 = (org.w3c.dom.Element)element.getParentNode();
            java.util.Map map = getValuesFromObject(element1);
            java.util.HashMap hashmap1 = new HashMap();
            jgl.Array array = new Array();
            for(org.w3c.dom.Element element2 = element1; element2.getTagName().equals((String)xmlKeys.get("object")); element2 = (org.w3c.dom.Element)element2.getParentNode())
            {
                String s4 = element2.getAttribute((String)xmlKeys.get("class"));
                String s5 = (String)xmlToLong.get(s4);
                if(s5 == null)
                {
                    s5 = s4;
                }
                String s6 = element2.getAttribute((String)xmlKeys.get("name"));
                hashmap1.put(s5, s6);
                array.add(s5 + ": " + s6);
            }

            String s2 = J2EEConnection.getNameFromArray(array);
            hashmap.put(s2, new SampleData(hashmap1, map));
        }

        for(java.util.Iterator iterator = activeMonitors.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String s1 = (String)entry.getKey();
            Monitor monitor = (Monitor)entry.getValue();
            java.util.Set set = monitor.getCounters();
            java.util.LinkedList linkedlist = new LinkedList();
            String s3 = String.valueOf(l);
            boolean flag = false;
            for(java.util.Iterator iterator1 = set.iterator(); iterator1.hasNext();)
            {
                String s7 = (String)iterator1.next();
                SampleData sampledata = (SampleData)hashmap.get(s7);
                if(sampledata == null)
                {
                    java.lang.System.out.println("J2EEConnection " + getConnID() + "/" + s1 + ": Counter missing - " + s7);
                    flag = true;
                } else
                {
//                    com.dragonflow.topaz.j2ee.comm.HashedSample hashedsample = new HashedSample(l, "J2EEMonitor");
                    java.util.Map.Entry entry1;
//                    for(java.util.Iterator iterator2 = sampledata.values.entrySet().iterator(); iterator2.hasNext(); hashedsample.addValue((String)entry1.getKey(), entry1.getValue()))
//                    {
//                        entry1 = (java.util.Map.Entry)iterator2.next();
//                    }

//                    hashedsample.addValue("TimeStamp", new Double(l));
//                    hashedsample.setMeasurementValue("Monitor", "J2EEMonitor");
//                    hashedsample.setMeasurementValue("TimeStamp", s3);
//                    java.util.Map.Entry entry2;
//                    for(java.util.Iterator iterator3 = sampledata.measurment.entrySet().iterator(); iterator3.hasNext(); hashedsample.setMeasurementValue((String)entry2.getKey(), (String)entry2.getValue()))
//                    {
//                        entry2 = (java.util.Map.Entry)iterator3.next();
//                    }
//
//                    linkedlist.add(hashedsample);
                }
            }

            if(flag)
            {
                COM.dragonflow.Log.LogManager.log("error", "Monitor: " + s1 + " missing counter");
                monitor.setLastError("Probe is out of sync. This is probably due to a second client (such as SiteView or LoadRunner) running against the same probe. To resolve this, shut down the unwanted client and repost metrics from the Tools link on this row.");
            } else
            {
                monitor.setLastError(null);
            }
//            com.dragonflow.topaz.j2ee.configuration.CheckSampleChunkResult checksamplechunkresult = aggregationConfigurator.checkSampleChunk(linkedlist);
//            java.util.ArrayList arraylist = checksamplechunkresult.getResultListByAggregatorId("SiteViewAggregator");
//            synchronized(monitor)
//            {
//                if(arraylist != null)
//                {
//                    monitor.getAggregator().addChunk(arraylist);
//                    monitor.setHasResults(true);
//                }
//            }
        }

    }

//    static void dump(com.dragonflow.topaz.j2ee.aggregator.Node node, String s)
//    {
//        java.lang.System.out.println(s);
//        java.util.Set set = node.getSubNodeNames();
//        com.dragonflow.topaz.j2ee.aggregator.Node node1;
//        String s2;
//        for(java.util.Iterator iterator = set.iterator(); iterator.hasNext(); J2EEConnection.dump(node1, s2))
//        {
//            String s1 = (String)iterator.next();
//            node1 = node.getSubNode(s1);
//            if(s == null)
//            {
//                s2 = s1;
//            } else
//            {
//                s2 = s + "/" + s1;
//            }
//        }
//
//    }

    private java.util.Map getValuesFromObject(org.w3c.dom.Element element)
    {
        java.util.HashMap hashmap = new HashMap();
        org.w3c.dom.NodeList nodelist = element.getChildNodes();
        int i = nodelist.getLength();
        for(int j = 0; j < i; j++)
        {
            org.w3c.dom.Node node = nodelist.item(j);
            if(node.getNodeType() != 1)
            {
                continue;
            }
            org.w3c.dom.Element element1 = (org.w3c.dom.Element)node;
            if(!element1.getTagName().equals((String)xmlKeys.get("counter")))
            {
                continue;
            }
            String s = element1.getAttribute((String)xmlKeys.get("val"));
            if(s.length() == 0)
            {
                s = "0.0";
            }
            hashmap.put(element1.getAttribute((String)xmlKeys.get("name")), new Double(s));
        }

        return hashmap;
    }

    public static String[] parseName(String s)
    {
        java.util.ArrayList arraylist = new ArrayList();
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == '\\')
            {
                if(++i < s.length())
                {
                    stringbuffer.append(s.charAt(i));
                }
                continue;
            }
            if(c == '/')
            {
                arraylist.add(stringbuffer.toString());
                stringbuffer = new StringBuffer();
            } else
            {
                stringbuffer.append(c);
            }
        }

        arraylist.add(stringbuffer.toString());
        String as[] = new String[arraylist.size()];
        arraylist.toArray(as);
        return as;
    }

    public void postMetricList()
    {
        try
        {
            doRequest("/?id=10&type=0", true, null);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("error", "J2EEConnection: Failed to post metric list target " + getConnID() + ": " + exception.getMessage());
            return;
        }
        TreeNode treenode = new TreeNode(xmlKeys);
        for(java.util.Iterator iterator = counterUnion.values().iterator(); iterator.hasNext();)
        {
            String as[] = (String[])iterator.next();
            try
            {
                treenode.addNode(as);
            }
            catch(java.lang.Exception exception1)
            {
                exception1.printStackTrace();
            }
        }

        StringBuffer stringbuffer = new StringBuffer();
        treenode.toXML(stringbuffer);
        StringBuffer stringbuffer1 = new StringBuffer();
        try
        {
            doRequest("/?id=12&type=0", false, stringbuffer.toString());
        }
        catch(java.lang.Exception exception2)
        {
            COM.dragonflow.Log.LogManager.log("error", "J2EEConnection: Failed to post metric list target " + getConnID() + ": " + exception2.getMessage());
        }
    }

    public static String getNameFromArray(jgl.Array array)
    {
        String s = "";
        int i = array.size();
        int j;
        if(array.at(0).equals("All SQL"))
        {
            j = 1;
        } else
        {
            j = 0;
        }
        for(int k = i; k > j; k--)
        {
            if(k < array.size())
            {
                s = s + '/';
            }
            s = s + J2EEConnection.escapeString((String)array.at(k - 1));
        }

        return s;
    }

    public static String escapeString(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == '/' || c == '\\')
            {
                stringbuffer.append('\\');
            }
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

//    public com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator getAggregatorAndSwap(COM.dragonflow.SiteView.J2EEMonitor j2eemonitor)
//    {
//        String s = (String)j2eemonitor.getUniqueID();
//        Monitor monitor = (Monitor)activeMonitors.get(s);
//        if(!monitor.getHasResults())
//        {
//            getMetricData(true);
//        }
//        com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator siteviewaggregator;
//        synchronized(monitor)
//        {
//            siteviewaggregator = monitor.getAggregator();
//            com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator.removeInstance(s);
//            monitor.setAggregator(com.dragonflow.topaz.j2ee.aggregator.SiteViewAggregator.getInstance(1, monitor.getInterval(), s));
//            monitor.setHasResults(false);
//        }
//        return siteviewaggregator;
//    }

    public String getLastError(COM.dragonflow.SiteView.J2EEMonitor j2eemonitor)
    {
        String s = (String)j2eemonitor.getUniqueID();
        Monitor monitor = (Monitor)activeMonitors.get(s);
        String s1 = monitor.getLastError();
        if(s1 == null)
        {
            s1 = lastError;
        }
        return s1;
    }

    public static String getGroupFromCounterName(String s)
    {
        int i = s.indexOf("group: ");
        if(i < 0)
        {
            return null;
        }
        int j = s.indexOf('/', i);
        if(j < 0)
        {
            return null;
        } else
        {
            String s1 = s.substring(i + 7, j);
            return s.substring(6, i - 1) + " " + s1;
        }
    }

    static 
    {
        xmlLongKeys = new HashMap();
        xmlShortKeys = new HashMap();
        xmlToLong = new HashMap();
        xmlLongKeys.put("root", "PerformanceMonitor");
        xmlShortKeys.put("root", "P");
        xmlLongKeys.put("object", "object");
        xmlShortKeys.put("object", "o");
        xmlLongKeys.put("class", "class");
        xmlShortKeys.put("class", "c");
        xmlLongKeys.put("name", "name");
        xmlShortKeys.put("name", "n");
        xmlLongKeys.put("counter", "counter");
        xmlShortKeys.put("counter", "c");
        xmlLongKeys.put("enabled", "enabled");
        xmlShortKeys.put("enabled", "e");
        xmlLongKeys.put("val", "val");
        xmlShortKeys.put("val", "v");
        xmlLongKeys.put("group", "group");
        xmlShortKeys.put("group", "g");
        xmlLongKeys.put("Method", "Method");
        xmlShortKeys.put("Method", "M");
        xmlLongKeys.put("SQL", "SQL");
        xmlShortKeys.put("SQL", "S");
        xmlToLong.put("group", "group");
        xmlToLong.put("g", "group");
        xmlToLong.put("Method", "Method");
        xmlToLong.put("M", "Method");
        xmlToLong.put("class", "class");
        xmlToLong.put("c", "class");
        xmlToLong.put("SQL", "SQL");
        xmlToLong.put("S", "SQL");
        try
        {
            documentBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            aggregationConfigurator = new SiteViewConfigurator(COM.dragonflow.SiteView.Platform.getRoot() + "/" + "templates.applications/J2EEAggregation.xml");
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
