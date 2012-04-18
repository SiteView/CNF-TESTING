/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLContentMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLContentMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.Utils.HTTPUtils;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.TextUtils;
import com.dragonflow.Utils.URLContext;
import com.dragonflow.Utils.URLResults;

// Referenced classes of package com.dragonflow.StandardMonitor:
//            URLMonitor

public class URLContentMonitor extends URLMonitor
{

    static StringProperty pValueLabels;
    static StringProperty pMatchValue2;
    static StringProperty pMatchValue3;
    static StringProperty pMatchValue4;
    static StringProperty pMatchValue5;
    static StringProperty pMatchValue6;
    static StringProperty pMatchValue7;
    static StringProperty pMatchValue8;
    static StringProperty pMatchValue9;
    static StringProperty pMatchValue10;
    HashMap labelsCache;
    public static String CRLF = "\r\n";
    public static int DEFAULT_TIMEOUT = 60000;
    static final String TARGET_TAGS[] = {
        "INPUT", "IMG", "BASE", "BODY"
    };
    public static int DEFAULT_MAX_REDIRECTS = 10;

    public URLContentMonitor()
    {
        labelsCache = null;
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    synchronized HashMap getLabels()
    {
        if(labelsCache == null)
        {
            labelsCache = new HashMap();
            if(getProperty(pValueLabels).length() > 0)
            {
                String as[] = TextUtils.split(getProperty(pValueLabels), ",");
                labelsCache.add("content match", as[0].trim());
                if(as.length >= 2)
                {
                    labelsCache.add("matchValue2", as[1].trim());
                }
                if(as.length >= 3)
                {
                    labelsCache.add("matchValue3", as[2].trim());
                }
                if(as.length >= 4)
                {
                    labelsCache.add("matchValue4", as[3].trim());
                }
                if(as.length >= 5)
                {
                    labelsCache.add("matchValue5", as[4].trim());
                }
                if(as.length >= 6)
                {
                    labelsCache.add("matchValue6", as[5].trim());
                }
                if(as.length >= 7)
                {
                    labelsCache.add("matchValue7", as[6].trim());
                }
                if(as.length >= 8)
                {
                    labelsCache.add("matchValue8", as[7].trim());
                }
                if(as.length >= 9)
                {
                    labelsCache.add("matchValue9", as[8].trim());
                }
                if(as.length >= 10)
                {
                    labelsCache.add("matchValue10", as[9].trim());
                }
            } else
            {
                labelsCache.add("matchValue", "content match");
                labelsCache.add("matchValue2", "second content match");
                labelsCache.add("matchValue3", "third content match");
                labelsCache.add("matchValue4", "fourth content match");
                labelsCache.add("matchValue5", "fifth content match");
                labelsCache.add("matchValue6", "sixth content match");
                labelsCache.add("matchValue7", "seventh content match");
                labelsCache.add("matchValue8", "eigth content match");
                labelsCache.add("matchValue9", "ninth content match");
                labelsCache.add("matchValue10", "tenth content match");
            }
            Array array = getProperties();
            Enumeration enumeration = (Enumeration) array.iterator();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                if(stringproperty.isThreshold() && stringproperty.getName().indexOf("Value") == -1)
                {
                    labelsCache.add(stringproperty.getLabel(), stringproperty.getLabel());
                }
            } while(true);
        }
        return labelsCache;
    }

    public String getPropertyName(StringProperty stringproperty)
    {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if(s1.length() == 0)
        {
            s1 = s;
        }
        return s1;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag)
    {
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if(s1.length() != 0)
        {
            return s1;
        }
        if(flag)
        {
            return "";
        } else
        {
            return s;
        }
    }

    protected boolean update()
    {
        getResults_URLAndImagesAndFrames(getProperty(pProxy), getProperty(pProxyPassword), getProperty(pProxyUserName));
        return true;
    }

    protected String updateMatchValuesAndProperty(String s, String s1, String s2)
    {
        String s3 = "";
        unsetProperty(getLocationProperty(pMatchValue, s3));
        unsetProperty(getLocationProperty(pMatchValue2, s3));
        unsetProperty(getLocationProperty(pMatchValue3, s3));
        unsetProperty(getLocationProperty(pMatchValue4, s3));
        unsetProperty(getLocationProperty(pMatchValue5, s3));
        unsetProperty(getLocationProperty(pMatchValue6, s3));
        unsetProperty(getLocationProperty(pMatchValue7, s3));
        unsetProperty(getLocationProperty(pMatchValue8, s3));
        unsetProperty(getLocationProperty(pMatchValue9, s3));
        unsetProperty(getLocationProperty(pMatchValue10, s3));
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        int i = TextUtils.matchExpression(s1, s, array, stringbuffer);
        if(i != Monitor.kURLok)
        {
            String s4 = URLMonitor.getHTMLEncoding(s1);
            int j = TextUtils.matchExpression(s1, I18N.UnicodeToString(s, s4), array, stringbuffer);
        }
        if(array.size() > 0)
        {
            setProperty(getLocationProperty(pMatchValue, s3), array.get(0));
        }
        if(array.size() > 1)
        {
            setProperty(getLocationProperty(pMatchValue2, s3), array.get(1));
        }
        if(array.size() > 2)
        {
            setProperty(getLocationProperty(pMatchValue3, s3), array.get(2));
        }
        if(array.size() > 3)
        {
            setProperty(getLocationProperty(pMatchValue4, s3), array.get(3));
        }
        if(array.size() > 4)
        {
            setProperty(getLocationProperty(pMatchValue5, s3), array.get(4));
        }
        if(array.size() > 5)
        {
            setProperty(getLocationProperty(pMatchValue6, s3), array.get(5));
        }
        if(array.size() > 6)
        {
            setProperty(getLocationProperty(pMatchValue7, s3), array.get(6));
        }
        if(array.size() > 7)
        {
            setProperty(getLocationProperty(pMatchValue8, s3), array.get(7));
        }
        if(array.size() > 8)
        {
            setProperty(getLocationProperty(pMatchValue9, s3), array.get(8));
        }
        if(array.size() > 9)
        {
            setProperty(getLocationProperty(pMatchValue10, s3), array.get(9));
        }
        String s5 = stringbuffer.toString();
        return s5;
    }

    int getMatchCount()
    {
        String s = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            if(s.charAt(j) == '(')
            {
                i++;
            }
        }

        return i;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Array array = new Array();
        array.add(pRoundTripTime);
        int i = getMatchCount();
        if(i >= 1)
        {
            array.add(pMatchValue);
        }
        if(i >= 2)
        {
            array.add(pMatchValue2);
        }
        if(i >= 3)
        {
            array.add(pMatchValue3);
        }
        if(i >= 4)
        {
            array.add(pMatchValue4);
        }
        if(i >= 5)
        {
            array.add(pMatchValue5);
        }
        if(i >= 6)
        {
            array.add(pMatchValue6);
        }
        if(i >= 7)
        {
            array.add(pMatchValue7);
        }
        if(i >= 8)
        {
            array.add(pMatchValue8);
        }
        if(i >= 9)
        {
            array.add(pMatchValue9);
        }
        if(i >= 10)
        {
            array.add(pMatchValue10);
        }
        return (Enumeration) array.iterator();
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.remove(pMatchValue);
        array.remove(pOverallStatus);
        array.remove(pTotalErrors);
        array.add(pMatchValue);
        array.add(pMatchValue2);
        array.add(pMatchValue3);
        array.add(pMatchValue4);
        array.add(pMatchValue5);
        array.add(pMatchValue6);
        array.add(pMatchValue7);
        array.add(pMatchValue8);
        array.add(pMatchValue9);
        array.add(pMatchValue10);
        return array;
    }

    public static long[] checkURL(String s, String s1, String s2, String s3, String s4, String s5, Array array, String s6, 
            String s7, String s8, StringBuffer stringbuffer, long l, String s9, int i, 
            int j, StringBuffer stringbuffer1)
    {
        s8.length();
        URLContext urlcontext = new URLContext(null);
        if(i > 0)
        {
            urlcontext.getMonitor().setProperty("_errorOnRedirect", "true");
        }
        urlcontext.setEncodePostData(null);
        String s10 = "";
        StringBuffer stringbuffer2 = null;
        HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s6, s7, s10, null, s3, s4, s5, null, 3, 0, 0);
        URLResults urlresults = URLMonitor.checkURL(httprequestsettings, urlcontext, s1, s2, array, stringbuffer, l, s9, j, stringbuffer1, stringbuffer2);
        return urlresults.getResultsAsArray();
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pContentMatch && s.length() == 0)
        {
            hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
        } else
        {
            String s1 = URLMonitor.verifyUrlMonitorProperty(stringproperty, s, hashmap);
            if(s1 != null)
            {
                s = s1;
            } else
            {
                s = super.verify(stringproperty, s, httprequest, hashmap);
            }
        }
        return s;
    }

    static 
    {
        pValueLabels = new StringProperty("_valueLabels", "");
        pValueLabels.setDisplayText("Match Value Labels", "Labels for the values matched on the content output, separated by a \",\"");
        pValueLabels.setParameterOptions(true, 25, true);
        pMatchValue2 = new NumericProperty("matchValue2");
        pMatchValue2.setStateOptions(1);
        pMatchValue3 = new NumericProperty("matchValue3");
        pMatchValue3.setStateOptions(1);
        pMatchValue4 = new NumericProperty("matchValue4");
        pMatchValue4.setStateOptions(1);
        pMatchValue5 = new NumericProperty("matchValue5");
        pMatchValue5.setStateOptions(1);
        pMatchValue6 = new NumericProperty("matchValue6");
        pMatchValue6.setStateOptions(1);
        pMatchValue7 = new NumericProperty("matchValue7");
        pMatchValue7.setStateOptions(1);
        pMatchValue8 = new NumericProperty("matchValue8");
        pMatchValue8.setStateOptions(1);
        pMatchValue9 = new NumericProperty("matchValue9");
        pMatchValue9.setStateOptions(1);
        pMatchValue10 = new NumericProperty("matchValue10");
        pMatchValue10.setStateOptions(1);
        StringProperty astringproperty[] = {
            pValueLabels, pMatchValue2, pMatchValue3, pMatchValue4, pMatchValue5, pMatchValue6, pMatchValue7, pMatchValue8, pMatchValue9, pMatchValue10
        };
        addProperties("com.dragonflow.StandardMonitor.URLContentMonitor", astringproperty);
        addClassElement("com.dragonflow.StandardMonitor.URLContentMonitor", Rule.stringToClassifier("status == 200\tgood\tstatus == 2xx"));
        addClassElement("com.dragonflow.StandardMonitor.URLContentMonitor", Rule.stringToClassifier("status != 200\terror\tstatus != 2xx"));
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "description", "Retrieves a web page and saves multiple matching values from the content.");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "help", "URLContentMon.htm");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "title", "URL Content");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "class", "URLContentMonitor");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "classType", "advanced");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "target", "_url");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "toolName", "Get URL Content");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "toolDescription", "Requests a URL from a server and display the page received.");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "classType", "advanced");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "topazName", "URL Content");
        setClassProperty("com.dragonflow.StandardMonitor.URLContentMonitor", "topazType", "Web Application Server");
        String s = System.getProperty("URLContentMonitor.debug");
        if(s != null)
        {
            debugURL = TextUtils.toInt(s);
            System.out.println("debugURL=" + debugURL);
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        millisecondPrecision = TextUtils.toInt(TextUtils.getValue(hashmap, "_defaultMillisecondPrecision"));
        if(millisecondPrecision == 0)
        {
            millisecondPrecision = 2;
        }
    }
}
