 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package COM.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */
import java.util.Date;
import java.util.Enumeration;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Utils:
// ParseException, Expression, InterpreterException, TextUtils,
// LUtils, SocketSession, FileUtils

public class SiteViewXMLQuery
{

    HashMap categoryFilter;
    HashMap typeFilter;
    HashMap groupFilter;
    String nameFilter;
    String statusFilter;
    Array expressionFilter;
    Array expressionStringFilter;
    HashMap propertyFilter;
    HashMap propertySetFilter;
    HashMap serverFilter;
    int level;
    boolean localRequest;
    boolean includeRemoteServers;
    String serverTitle;
    String linkBase;
    boolean addLinks;
    boolean addAlerts;
    boolean includeBlankProperties;
    boolean includeEmptyGroups;
    boolean includeEmptyServers;
    public static String TRUE_STRING = "true";
    public static String XML_HEADER = "<?xml version=\"1.0\"?>";
    Array servers;
    java.io.PrintWriter outputStream;
    StringBuffer buffer;
    COM.dragonflow.HTTP.HTTPRequest request;
    HashMap config;
    static final int INDENT_STRING_COUNT = 25;
    static String INDENT_STRING;
    static String indentStrings[];

    public SiteViewXMLQuery(HashMap hashmap, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        categoryFilter = null;
        typeFilter = null;
        groupFilter = null;
        nameFilter = "";
        statusFilter = "";
        expressionFilter = null;
        expressionStringFilter = null;
        propertyFilter = null;
        propertySetFilter = null;
        serverFilter = null;
        level = 0;
        localRequest = true;
        includeRemoteServers = false;
        serverTitle = "local";
        linkBase = "";
        addLinks = false;
        addAlerts = false;
        includeBlankProperties = false;
        includeEmptyGroups = false;
        includeEmptyServers = false;
        servers = null;
        outputStream = null;
        buffer = null;
        request = null;
        config = null;
        if(httprequest == null)
        {
            request = new HTTPRequest();
            request.setUser("administrator");
        } else
        {
            request = httprequest;
        }
        config = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "remote").length() > 0)
        {
            localRequest = false;
            serverTitle = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "serverTitle");
            linkBase = "http://" + COM.dragonflow.Utils.TextUtils.getValue(config, "_webserverAddress");
            String s = COM.dragonflow.Utils.TextUtils.getValue(config, "_httpActivePort");
            if(s.length() != 0 && !s.equals("80"))
            {
                linkBase += ":" + s;
            }
        }
        categoryFilter = setupQueryMap(hashmap, "category");
        typeFilter = setupQueryMap(hashmap, "type");
        groupFilter = setupQueryMap(hashmap, "group");
        nameFilter = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name");
        statusFilter = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "status");
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "exp").length() > 0)
        {
            expressionFilter = new Array();
            expressionStringFilter = new Array();
            for(Enumeration enumeration = (Enumeration) hashmap.values("exp"); enumeration.hasMoreElements();)
            {
                String s1 = (String)enumeration.nextElement();
                Object obj = null;
                try
                {
                    COM.dragonflow.Utils.Expression expression = COM.dragonflow.Utils.Expression.parseString(s1);
                    expressionFilter.add(expression);
                    expressionStringFilter.add(s1);
                }
                catch(COM.dragonflow.Utils.ParseException parseexception)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Parse error in XML Query expression: " + s1);
                }
            }

        }
        propertyFilter = setupQueryMap(hashmap, "property");
        propertySetFilter = setupQueryMap(hashmap, "propertySet");
        if(propertySetFilter != null)
        {
            if(propertySetFilter.get("links") != null)
            {
                addLinks = true;
                propertySetFilter.remove("links");
            }
            if(propertySetFilter.get("alerts") != null)
            {
                addAlerts = true;
                propertySetFilter.remove("alerts");
            }
            if(propertySetFilter.get("parameters") != null)
            {
                propertySetFilter.put("mainParameters", TRUE_STRING);
                propertySetFilter.put("secondaryParameters", TRUE_STRING);
            }
            if(propertySetFilter.get("state") != null)
            {
                propertySetFilter.put("mainState", TRUE_STRING);
                propertySetFilter.put("secondaryState", TRUE_STRING);
            }
            if(propertySetFilter.size() == 0)
            {
                propertySetFilter = null;
            }
        }
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "includeBlankProperties").length() > 0)
        {
            includeBlankProperties = true;
        }
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "includeEmptyGroups").length() > 0)
        {
            includeEmptyGroups = true;
        }
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "includeEmptyServers").length() > 0)
        {
            includeEmptyServers = true;
        }
        serverFilter = setupQueryMap(hashmap, "server");
        if(serverFilter != null)
        {
            includeRemoteServers = true;
            if(COM.dragonflow.Utils.TextUtils.getValue(serverFilter, "all").length() > 0)
            {
                serverFilter.remove("all");
            }
            if(serverFilter.size() == 0)
            {
                serverFilter = null;
            }
        }
        servers = getServers(hashmap);
    }

    public SiteViewXMLQuery(HashMap hashmap, java.io.PrintWriter printwriter, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        this(hashmap, httprequest);
        outputStream = printwriter;
    }

    public SiteViewXMLQuery(HashMap hashmap, StringBuffer stringbuffer, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        this(hashmap, httprequest);
        buffer = stringbuffer;
    }

    public Array getServers(HashMap hashmap)
    {
        Array array = new Array();
        Array array1 = new Array();
        if(includeRemoteServers)
        {
            String s = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/" + "multi.config";
            try
            {
                array1 = COM.dragonflow.Properties.FrameFile.readFromFile(s);
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Could not read multi.config from " + s + ": " + exception.getMessage());
            }
        }
        if(array1.size() == 0)
        {
            HashMap hashmap1 = new HashMap();
            hashmap1.put("server", "_local");
            array1.add(hashmap1);
        }
        Enumeration enumeration =  (Enumeration) array1.iterator();
        while (enumeration.hasMoreElements()) {
            HashMap hashmap2 = (HashMap)enumeration.nextElement();
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "server");
            if(s1.equals("_local") && COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "title").length() == 0)
            {
                hashmap2.put("title", COM.dragonflow.SiteView.Server.LocalServerName());
            }
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "title").length() == 0)
            {
                hashmap2.put("title", hashmap2.get("server"));
            }
            if(serverFilter == null || serverFilter.get(s1) != null)
            {
                array.add(hashmap2);
            }
        } 
        return array;
    }

    private String getIndentString()
    {
        if(level < 25)
        {
            return indentStrings[level];
        }
        String s = "";
        for(int i = 0; i < level; i++)
        {
            s = s + INDENT_STRING;
        }

        return s;
    }

    private void println(String s)
    {
        String s1 = getIndentString();
        if(outputStream != null)
        {
            outputStream.print(s1);
            outputStream.println(s);
        }
        if(buffer != null)
        {
            buffer.append(s1);
            buffer.append(s);
            buffer.append('\n');
        }
    }

    public HashMap setupQueryMap(HashMap hashmap, String s)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = null;
        String s1;
        for(Enumeration enumeration = (Enumeration) hashmap.values(s); enumeration.hasMoreElements(); hashmapordered.put(s1, TRUE_STRING))
        {
            s1 = (String)enumeration.nextElement();
            if(hashmapordered == null)
            {
                hashmapordered = new HashMapOrdered(true);
            }
        }

        return hashmapordered;
    }

    public void printXML()
    {
        println(XML_HEADER);
        if(localRequest)
        {
            println("<enterprise>");
        }
        level++;
        for(Enumeration enumeration = (Enumeration) servers.iterator(); enumeration.hasMoreElements();)
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            if(!COM.dragonflow.Utils.TextUtils.getValue(hashmap, "server").equals("_local"))
            {
                printRemoteServer(hashmap);
            } else
            {
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                println("<siteview>");
                level++;
                println("<server>" + siteviewgroup.getSetting("_webserverAddress") + "</server>");
                if(serverTitle.length() > 0)
                {
                    println("<serverTitle>" + serverTitle + "</serverTitle>");
                }
                if(addLinks)
                {
                    println("<serverLink>" + siteviewgroup.mainURL() + "?account=" + request.getAccount() + "</serverLink>");
                }
                Array array = siteviewgroup.getGroupIDs();
                array = sortGroupsForTree(array);
                for(int i = 0; i < array.size(); i++)
                {
                    String s = (String)array.get(i);
                    COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s);
                    if(monitorgroup != null)
                    {
                        printGroup(monitorgroup);
                    }
                }

                if(addAlerts)
                {
                    Array array1 = siteviewgroup.getElementsOfClass("COM.dragonflow.SiteView.Rule", false, false);
                    COM.dragonflow.SiteView.Rule rule;
                    for(Enumeration enumeration1 =  (Enumeration) array1.iterator(); enumeration1.hasMoreElements(); printAlert(rule))
                    {
                        rule = (COM.dragonflow.SiteView.Rule)enumeration1.nextElement();
                    }

                }
                level--;
                println("</siteview>");
            }
        }

        level--;
        if(localRequest)
        {
            println("</enterprise>");
        }
    }

    boolean groupAllowed(String s)
    {
        return groupFilter == null || groupFilter.get(s) != null;
    }

    String getIndentHTML(int i)
    {
        String s = "";
        for(int j = 1; j <= i; j++)
        {
            s = s + "&nbsp;&nbsp;";
        }

        return s;
    }

    void printGroupProperties(COM.dragonflow.SiteView.MonitorGroup monitorgroup)
    {
        println("<group>");
        level++;
        printXML(monitorgroup);
        if(addLinks)
        {
            println("<detailLink>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(linkBase + COM.dragonflow.SiteView.Monitor.getGroupDetailLink(request, monitorgroup)) + "</detailLink>");
            println("<categoryArt>" + COM.dragonflow.SiteView.MonitorGroup.getCategoryArt(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArt>");
            println("<categoryArtSmall>" + COM.dragonflow.SiteView.MonitorGroup.getSmallCategoryArt(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArtSmall>");
            println("<categoryArtTiny>" + COM.dragonflow.SiteView.MonitorGroup.getTinyCategoryArt(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArtTiny>");
            println("<gaugeArt>" + COM.dragonflow.SiteView.Monitor.getGaugeHTML(request, monitorgroup) + "</gaugeArt>");
        }
        println("<groupIndent>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(getIndentHTML(COM.dragonflow.Utils.TextUtils.toInt(monitorgroup.getProperty("groupLevel")))) + "</groupIndent>");
    }

    void printGroup(COM.dragonflow.SiteView.MonitorGroup monitorgroup)
    {
        if(groupAllowed(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID)))
        {
            boolean flag = false;
            if(includeEmptyGroups)
            {
                printGroupProperties(monitorgroup);
                flag = true;
            }
            if(addAlerts)
            {
                Array array = monitorgroup.getElementsOfClass("COM.dragonflow.SiteView.Rule", false, false);
                COM.dragonflow.SiteView.Rule rule;
                for(Enumeration enumeration1 = (Enumeration) array.iterator(); enumeration1.hasMoreElements(); printAlert(rule))
                {
                    rule = (COM.dragonflow.SiteView.Rule)enumeration1.nextElement();
                    if(!flag)
                    {
                        printGroupProperties(monitorgroup);
                        flag = true;
                    }
                }

            }
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)enumeration.nextElement();
                if(!filterMonitor(monitor))
                {
                    if(!flag)
                    {
                        printGroupProperties(monitorgroup);
                        flag = true;
                    }
                    printMonitor(monitor);
                }
            } 
            if(flag)
            {
                level--;
                println("</group>");
            }
        }
    }

    boolean filterMonitor(COM.dragonflow.SiteView.Monitor monitor)
    {
        if(monitor instanceof COM.dragonflow.SiteView.SubGroup)
        {
            return true;
        }
        if(categoryFilter != null && categoryFilter.get(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) == null)
        {
            return true;
        }
        if(nameFilter.length() > 0 && !COM.dragonflow.Utils.TextUtils.match(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName), nameFilter))
        {
            return true;
        }
        if(monitor instanceof COM.dragonflow.SiteView.AtomicMonitor)
        {
            if(typeFilter != null && typeFilter.get(monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass)) == null)
            {
                return true;
            }
            if(statusFilter.length() > 0 && !COM.dragonflow.Utils.TextUtils.match(monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pStateString), statusFilter))
            {
                return true;
            }
        }
        if(expressionFilter != null)
        {
            boolean flag = false;
            for(int i = 0; i < expressionFilter.size(); i++)
            {
                COM.dragonflow.Utils.Expression expression = (COM.dragonflow.Utils.Expression)expressionFilter.get(i);
                try
                {
                    java.lang.Boolean boolean1 = (java.lang.Boolean)expression.interpretExpression(monitor, null);
                    if(boolean1.booleanValue())
                    {
                        flag = true;
                    }
                }
                catch(COM.dragonflow.Utils.InterpreterException interpreterexception)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Interpreter exception in XML Query: " + interpreterexception.getMessage());
                }
            }

            if(!flag)
            {
                return true;
            }
        }
        return false;
    }

    void printMonitor(COM.dragonflow.SiteView.Monitor monitor)
    {
        println("<monitor>");
        level++;
        printXML(monitor);
        if(addLinks)
        {
            println("<categoryArt>" + COM.dragonflow.SiteView.MonitorGroup.getCategoryArt(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArt>");
            println("<categoryArtSmall>" + COM.dragonflow.SiteView.MonitorGroup.getSmallCategoryArt(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArtSmall>");
            println("<categoryArtTiny>" + COM.dragonflow.SiteView.MonitorGroup.getTinyCategoryArt(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pCategory)) + "</categoryArtTiny>");
            println("<gaugeArt>" + COM.dragonflow.SiteView.Monitor.getGaugeHTML(request, monitor) + "</gaugeArt>");
            String s = monitor.getTableMoreLink(request);
            if(includeBlankProperties || s.length() > 0)
            {
                println("<toolsLink>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(linkBase + s) + "</toolsLink>");
            }
            println("<recentLink>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(linkBase + monitor.getTableRecentLink(request)) + "</recentLink>");
        }
        if(addAlerts)
        {
            Array array = monitor.getElementsOfClass("COM.dragonflow.SiteView.Rule", false, false);
            COM.dragonflow.SiteView.Rule rule;
            for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); printAlert(rule))
            {
                rule = (COM.dragonflow.SiteView.Rule)enumeration.nextElement();
            }

        }
        level--;
        println("</monitor>");
    }

    void printRemoteServer(HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "server");
        String s1 = "http://";
        if(s.indexOf("://") != -1)
        {
            s1 = "";
        }
        String s2 = s1 + s + "/SiteView/cgi/go.exe/SiteView";
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "account");
        String s4 = "";
        String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "proxy");
        String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "proxyusername");
        String s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "proxypassword");
        String s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "username");
        String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "password");
        String s10 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "timeout");
        int i = 60000;
        if(s10.length() != 0)
        {
            i = COM.dragonflow.Utils.TextUtils.toInt(s10) * 1000;
        }
        if(s8.length() == 0)
        {
            s8 = COM.dragonflow.Utils.TextUtils.getValue(config, "_adminUsername");
            s9 = COM.dragonflow.Utils.TextUtils.getValue(config, "_adminPassword");
        }
        long l = -1L;
        if(COM.dragonflow.Utils.TextUtils.getValue(config, "_overviewMaxData").length() > 0)
        {
            l = COM.dragonflow.Utils.TextUtils.toLong(COM.dragonflow.Utils.TextUtils.getValue(config, "_overviewMaxData"));
        }
        if(l < 1L)
        {
            l = 0x7a120L;
        }
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        array.add("page=xml");
        array.add("account=" + s3);
        array.add("license=" + COM.dragonflow.Utils.LUtils.getLicenseKey(config));
        if(nameFilter.length() > 0)
        {
            array.add("name=" + nameFilter);
        }
        if(statusFilter.length() > 0)
        {
            array.add("status=" + statusFilter);
        }
        addMapToPostData(groupFilter, "group", array);
        addMapToPostData(categoryFilter, "category", array);
        addMapToPostData(typeFilter, "type", array);
        addMapToPostData(propertyFilter, "property", array);
        addMapToPostData(propertySetFilter, "propertySet", array);
        if(addLinks)
        {
            array.add("propertySet=links");
        }
        if(addAlerts)
        {
            array.add("propertySet=alerts");
        }
        if(expressionFilter != null)
        {
            for(Enumeration enumeration = (Enumeration) expressionStringFilter.iterator(); enumeration.hasMoreElements(); array.add(enumeration.nextElement())) { }
        }
        array.add("remote=true");
        array.add("serverTitle=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "title"));
        COM.dragonflow.Utils.SocketSession socketsession = COM.dragonflow.Utils.SocketSession.getSession(null);
        long al[] = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s2, s4, "", s5, s6, s7, array, s8, s9, "", stringbuffer, l, "", 0, i, null);
        socketsession.close();
        if(al[0] == 200L)
        {
            String s11 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
            int j = s11.indexOf(XML_HEADER);
            if(j >= 0)
            {
                s11 = s11.substring(j + XML_HEADER.length());
            }
            println(s11);
        } else
        {
            println("<siteview>");
            println("<error>" + COM.dragonflow.SiteView.Monitor.lookupStatus(al[0]) + "</error>");
            println("</siteview>");
        }
    }

    void addMapToPostData(HashMap hashmap, String s, Array array)
    {
        if(hashmap != null)
        {
            String s1;
            for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements(); array.add(s + "=" + s1))
            {
                s1 = (String)enumeration.nextElement();
            }

        }
    }

    boolean allowProperty(COM.dragonflow.Properties.StringProperty stringproperty)
    {
        boolean flag = true;
        if(propertyFilter != null || propertySetFilter != null)
        {
            flag = false;
            if(propertyFilter != null && propertyFilter.get(stringproperty.getName()) != null)
            {
                flag = true;
            }
            if(!flag && propertySetFilter != null)
            {
                if(stringproperty.isParameter)
                {
                    if(stringproperty.getOrder() > 0)
                    {
                        flag = propertySetFilter.get("mainParameters") != null;
                    } else
                    {
                        flag = propertySetFilter.get("secondaryParameters") != null;
                    }
                } else
                if(stringproperty.isStateProperty)
                {
                    if(stringproperty.getOrder() > 0)
                    {
                        flag = propertySetFilter.get("mainState") != null;
                    } else
                    {
                        flag = propertySetFilter.get("secondaryState") != null;
                    }
                }
            }
        }
        return flag;
    }

    public static String xmlEncode(String s)
    {
        String s1 = "&";
        String s2 = "&amp;";
        String s3 = s;
        for(int i = -1; (i = s3.indexOf(s1, i + 1)) != -1;)/*dingbing.xu no change!*/
        {
            s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());
        }

        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, ">", "&gt;");
        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, "<", "&lt;");
        return s3;
    }

    void printXML(COM.dragonflow.SiteView.SiteViewObject siteviewobject)
    {
        Array array = siteviewobject.getProperties();
        Enumeration enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(allowProperty(stringproperty))
            {
                String s = stringproperty.getName();
                s = s.replace('/', '-');
                String s1 = "";
                if(stringproperty == COM.dragonflow.SiteView.AtomicMonitor.pLastUpdate)
                {
                    if(siteviewobject.getPropertyAsLong(COM.dragonflow.SiteView.AtomicMonitor.pLastUpdate) > 0L)
                    {
                        long l = COM.dragonflow.Utils.TextUtils.toLong(request.getUserSetting("_timeOffset")) * 1000L;
                        s1 = COM.dragonflow.Utils.TextUtils.prettyDate(new Date(siteviewobject.getPropertyAsLong(COM.dragonflow.SiteView.AtomicMonitor.pLastUpdate) + l));
                    }
                } else
                {
                    s1 = stringproperty.valueString(siteviewobject.getProperty(stringproperty));
                }
                s1 = s1.replace('<', '*');
                s1 = s1.replace('>', '*');
                if(s1.length() != 0 || includeBlankProperties)
                {
                    println("<" + s + ">" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(s1) + "</" + s + ">");
                }
            }
        } 
    }

    COM.dragonflow.SiteView.Action createAction(COM.dragonflow.SiteView.Rule rule)
    {
        String s = rule.getProperty(COM.dragonflow.SiteView.Rule.pAction);
        COM.dragonflow.SiteView.Action action = COM.dragonflow.SiteView.Action.createAction(s);
        action.setOwner(rule);
        int i = s.indexOf(" ");
        String as[];
        if(i >= 0)
        {
            as = COM.dragonflow.Utils.TextUtils.split(s.substring(i + 1, s.length()));
        } else
        {
            as = new String[0];
        }
        Array array = new Array();
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        for(int j = 0; j < as.length; j++)
        {
            int k = as[j].indexOf("=");
            if(k == -1)
            {
                array.add(as[j]);
            } else
            {
                hashmapordered.add(as[j].substring(0, k), as[j].substring(k + 1));
            }
        }

        action.initializeFromArguments(array, hashmapordered);
        return action;
    }

    void printAlert(COM.dragonflow.SiteView.Rule rule)
    {
        println("<alert>");
        level++;
        println("<rule>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(rule.getProperty(COM.dragonflow.SiteView.Rule.pExpression)) + "</rule>");
        COM.dragonflow.SiteView.Action action = createAction(rule);
        println("<description>" + COM.dragonflow.Utils.SiteViewXMLQuery.xmlEncode(action.getActionDescription()) + "</description>");
        println("<type>" + action.getClassProperty("name") + "</type>");
        printXML(action);
        level--;
        println("</alert>");
    }

    Array sortGroupsForTree(Array array)
    {
        Array array1 = new Array();
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        int i = 0;
        for(int j = 0; j < array.size(); j++)
        {
            String s = (String)array.get(j);
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s);
            if(monitorgroup != null && monitorgroup.getProperty(COM.dragonflow.SiteView.MonitorGroup.pParent).length() == 0)
            {
                array1.add(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID));
                monitorgroup.setProperty("groupLevel", "0");
                findSubgroups(monitorgroup, array1, i + 1);
            }
        }

        return array1;
    }

    void findSubgroups(COM.dragonflow.SiteView.MonitorGroup monitorgroup, Array array, int i)
    {
        if(groupAllowed(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID)))
        {
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)enumeration.nextElement();
                if(monitor instanceof COM.dragonflow.SiteView.SubGroup)
                {
                    String s = monitor.getProperty(COM.dragonflow.SiteView.SubGroup.pGroup);
                    COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                    COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s);
                    if(monitorgroup1 != null)
                    {
                        array.add(s);
                        monitorgroup1.setProperty("groupLevel", "" + i);
                        findSubgroups(monitorgroup1, array, i + 1);
                    }
                }
            }
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("propertySet", "mainParameters");
        hashmapordered.add("list", "tree");
        java.io.PrintWriter printwriter = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(java.lang.System.out);
        COM.dragonflow.Utils.SiteViewXMLQuery siteviewxmlquery = new SiteViewXMLQuery(hashmapordered, printwriter, null);
        siteviewxmlquery.printXML();
        printwriter.flush();
    }

    static 
    {
        INDENT_STRING = "  ";
        indentStrings = new String[25];
        String s = "";
        for(int i = 0; i < 25; i++)
        {
            indentStrings[i] = s;
            s = s + INDENT_STRING;
        }

    }
}
