/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SunOneMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SunOneMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.io.StringReader;
import java.util.HashMap;
import jgl.Array;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SunOneMonitor extends BrowsableURLContentBase
{

    public static StringProperty pPostData;
    public static StringProperty pURL;
    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
    private final String excludeList = ",thread,profile,profile-bucket,";
    private final String precisionList = ",load1MinuteAverage,load5MinuteAverage,load15MinuteAverage,percentIdle,percentUser,percentKernel,fractionSystemMemoryUsage,";
    private static String makeUniqueList = ",cpu-info#cpu,";
    public static final String process_unique_string = "process#pid,";
    private final String derivedList[] = {
        "/process/% Cache table utilization = /process/cache-bucket/countEntries / /process/cache-bucket/maxEntries", "/process/% Cache heap utilization = /process/cache-bucket/sizeHeapCache / /process/cache-bucket/maxHeapCacheSize", "/process/% File cache hits = /process/cache-bucket/countHits % /process/cache-bucket/countMisses", "/process/% Idle threads = /process/thread-pool-bucket/countThreadsIdle / /process/thread-pool-bucket/countThreads", "/process/% DNS cache utilization = /process/dns-bucket/countCacheEntries / /process/dns-bucket/maxCacheEntries", "/process/% DNS cache hits = /process/dns-bucket/countCacheHits % /process/dns-bucket/countCacheMisses", "/process/% DNS cache misses = /process/dns-bucket/countCacheMisses % /process/dns-bucket/countCacheHits", "/process/% Cache memory utilization = /process/cache-bucket/sizeMmapCache / /process/cache-bucket/maxMmapCacheSize", "/process/% File info cache hits = /process/cache-bucket/countInfoHits % /process/cache-bucket/countInfoMisses", "/process/% File content cache hits = /process/cache-bucket/countContentHits % /process/cache-bucket/countContentMisses"
    };
    private final int iPlanetMaxPrecision = 6;
    private final int derivedCountersPrecision = 2;
    private SunOneFormulaEngine formulaEngine;

    public SunOneMonitor()
    {
        formulaEngine = null;
        formulaEngine = new SunOneFormulaEngine(derivedList);
    }

    protected String createXMLFromTemplate(String s)
    {
        String s1 = "";
        try
        {
            XMLReader xmlreader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            SunOneXMLHandler sunonexmlhandler = new SunOneXMLHandler();
            sunonexmlhandler.setExcludeList(",thread,profile,profile-bucket,");
            sunonexmlhandler.setDerivedList(derivedList);
            sunonexmlhandler.setFormulaEngine(formulaEngine);
            xmlreader.setFeature("http://xml.org/sax/features/validation", false);
            xmlreader.setContentHandler(sunonexmlhandler);
            xmlreader.setErrorHandler(sunonexmlhandler);
            int i = s.indexOf("<stats");
            String s2 = s.substring(i);
            StringReader stringreader = new StringReader(s2);
            xmlreader.parse(new InputSource(stringreader));
            s1 = sunonexmlhandler.getXML();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return s1;
    }

    protected void parseForCounterValues(String s, String as[])
    {
        Object obj = null;
        for(int i = 0; i < as.length; i++)
        {
            as[i] = "";
        }

        HashMap hashmap = new HashMap();
        for(int j = 0; j < nMaxCounters; j++)
        {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
            if(s1.length() == 0)
            {
                break;
            }
            String s2 = null;
            if(isCounterInMakeUniqueList(s1))
            {
                s2 = extractUniqueCounterName(s1);
                if(isDerivedCounter(s1))
                {
                    formulaEngine.registerSelectedDerivedCounter(s2);
                }
            } else
            {
                s2 = extractAdequateCounterName(s1, false);
                if(isDerivedCounter(s1))
                {
                    formulaEngine.registerSelectedDerivedCounter(s2);
                }
            }
            hashmap.put(s2, new Integer(j));
        }

        try
        {
            XMLReader xmlreader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            SunOneXMLHandlerUpdate sunonexmlhandlerupdate = new SunOneXMLHandlerUpdate();
            sunonexmlhandlerupdate.setExcludeList(",thread,profile,profile-bucket,");
            sunonexmlhandlerupdate.setFormulaEngine(formulaEngine);
            xmlreader.setFeature("http://xml.org/sax/features/validation", false);
            xmlreader.setContentHandler(sunonexmlhandlerupdate);
            xmlreader.setErrorHandler(sunonexmlhandlerupdate);
            int k = s.indexOf("<stats");
            String s3 = s.substring(k);
            StringReader stringreader = new StringReader(s3);
            sunonexmlhandlerupdate.setSelectedCounters(hashmap);
            sunonexmlhandlerupdate.setCounterValuesArray(as);
            xmlreader.parse(new InputSource(stringreader));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        formulaEngine.calculateDerivedCounterValues(hashmap, as);
    }

    protected void setPrecision()
    {
        Object obj = null;
        int i = 0;
        for(int j = 0; j < nMaxCounters; j++)
        {
            String s = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
            if(s.length() == 0)
            {
                break;
            }
            int k = s.lastIndexOf('/');
            String s1 = s.substring(k + 1);
            int l = ",load1MinuteAverage,load5MinuteAverage,load15MinuteAverage,percentIdle,percentUser,percentKernel,fractionSystemMemoryUsage,".indexOf("," + s1 + ",");
            if(l >= 0)
            {
                i = 6;
            } else
            if(s.indexOf("Derived Counters") >= 0)
            {
                i = 2;
            }
            ((NumericProperty)getPropertyObject(PROPERTY_NAME_COUNTER_VALUE + (j + 1))).defaultPrecision = i;
        }

    }

    public Array getConnectionProperties()
    {
        Array array = super.getConnectionProperties();
        array.insert(0, pURL);
        array.insert(1, pPostData);
        return array;
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    protected String getStatString()
    {
        return "stats";
    }

    public static boolean isDerivedCounter(String s)
    {
        return s.indexOf("Derived Counters") >= 0;
    }

    public static boolean isCounterInMakeUniqueList(String s)
    {
        if(makeUniqueList == null)
        {
            return false;
        }
        String as[] = TextUtils.split(s, "/");
        if(as.length == 0)
        {
            return false;
        }
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            int j = s1.indexOf('@');
            if(j <= 0)
            {
                continue;
            }
            s1 = s1.substring(0, j);
            if(isElementInMakeUniqueList(s1))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isElementInMakeUniqueList(String s)
    {
        return makeUniqueList != null && makeUniqueList.indexOf("," + s + "#") >= 0;
    }

    public static String getAttrForUniqueCounter(String s)
    {
        String s1 = null;
        String as[] = TextUtils.split(makeUniqueList, ",");
        int i = 0;
        do
        {
            if(i >= as.length)
            {
                break;
            }
            String s2 = as[i];
            if(s2.indexOf(s + "#") >= 0)
            {
                s1 = s2.substring(s2.indexOf('#') + 1);
                break;
            }
            i++;
        } while(true);
        return s1;
    }

    private static String extractUniqueCounterName(String s)
    {
        int i = s.lastIndexOf('@');
        if(i <= 0)
        {
            return extractAdequateCounterName(s, false);
        }
        int j = s.lastIndexOf('/', i);
        if(j < 0)
        {
            j = 0;
        }
        return s.substring(j);
    }

    public static String extractElementFromQualifiedCounterName(String s)
    {
        int i = s.lastIndexOf('@');
        if(i <= 0)
        {
            return s;
        }
        int j = s.lastIndexOf('/', i);
        if(j < 0)
        {
            j = 0;
        }
        return s.substring(j + 1, i - 1);
    }

    public static String extractQualifedElementFromQualifiedCounterName(String s)
    {
        String s1 = extractUniqueCounterName(s);
        int i = s1.indexOf("/", 1);
        int j = 0;
        if(s1.charAt(0) == '/')
        {
            j = 1;
        }
        return s1.substring(j, i);
    }

    public static String extractAdequateCounterName(String s, boolean flag)
    {
        String s1 = null;
        int i = s.lastIndexOf('/');
        int j = s.lastIndexOf('/', i - 1);
        if(j < 0)
        {
            s1 = s.substring(0);
            if(flag)
            {
                s1 = "/" + s1;
            }
        } else
        {
            s1 = s.substring(j);
            if(!flag)
            {
                s1 = s1.substring(1);
            }
        }
        return s1;
    }

    public static String getMakeUniqueList()
    {
        return makeUniqueList;
    }

    public static void setMakeUniqueList(String s)
    {
        makeUniqueList = s;
    }

    protected String getBrowseTreeTimeoutParameterName()
    {
        return "_sunOneMonitorGetBrowseTreeTimeout";
    }

    static 
    {
        Array array = new Array();
        pURL = new StringProperty("_server");
        pURL.setDisplayText("Stats-XML URL", "URL of the stats-xml report (example: http://server:port/stats-xml/nesstats.xml)");
        pURL.setParameterOptions(false, true, 1, false);
        array.add(pURL);
        pPostData = new StringProperty("_postData");
        pPostData.setDisplayText("POST Data", "optional name=value entries to send with the POST request, separate each entry with a comma.");
        pPostData.setParameterOptions(false, true, 2, false);
        array.add(pPostData);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty[i] = (StringProperty)array.at(i);
        }

        String s = (COM.dragonflow.StandardMonitor.SunOneMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors SunONE web servers using the statistics XML page");
        setClassProperty(s, "help", "SunOneMon.htm");
        setClassProperty(s, "title", "SunONE WebServer");
        setClassProperty(s, "class", "SunOneMonitor");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "SunONE");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "topazType", "Web Application Server");
        setClassProperty(s, "loadable", "true");
        String s1 = System.getProperty("SunOneMonitor.debug", "false");
        debug = s1.equalsIgnoreCase("true");
    }
}
