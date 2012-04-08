/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * Oracle9iMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>Oracle9iMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.io.PrintStream;
import java.net.URLEncoder;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor, URLContentMonitor

public class Oracle9iMonitor extends URLContentBase
{

    static StringProperty pURL;
    static StringProperty pCounters;
    static StringProperty pTimeout;
    static StringProperty pErrorContent;
    static StringProperty pProxy;
    static StringProperty pUserName;
    static StringProperty pChallengeResponse;
    static StringProperty pPassword;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static StringProperty pStatus;
    static String returnURL;
    public static String templateFile;
    private static String defaultCountersFromTemplate;
    private static String allCounters[] = new String[0];
    public static int DEFAULT_TIMEOUT = 60000;

    public Oracle9iMonitor()
    {
    }

    public String getReturnURL()
    {
        return HTTPRequest.encodeString(returnURL);
    }

    public StringProperty getCountersProperty()
    {
        return pCounters;
    }

    public void setCountersContent(String s)
    {
        setProperty(pCounters, s);
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    public String getTemplateFile()
    {
        return templateFile;
    }

    protected boolean update()
    {
        String s = "";
        String s1 = getProperty(pURL);
        String s2 = getSetting("_oracleRegExp");
        StringBuffer stringbuffer = new StringBuffer();
        if(s2.length() <= 0)
        {
            s2 = buildRegExp(stringbuffer);
        }
        String s3 = getProperty(pProxy);
        String s4 = getProperty(pProxyPassword);
        String s5 = getProperty(pProxyUserName);
        String s6 = getProperty(pUserName);
        if(getProperty(pChallengeResponse).length() > 0)
        {
            s6 = URLMonitor.NT_CHALLENGE_RESPONSE_TAG + s6;
        }
        String s7 = getProperty(pPassword);
        int i = getPropertyAsInteger(pTimeout) * 1000;
        if(i == 0)
        {
            i = DEFAULT_TIMEOUT;
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        String s8 = "";
        progressString += "Retrieving URL " + getProperty(pURL) + "\n";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("_urlContentMatchMax", 51200);
        long al[] = URLContentMonitor.checkURL(s1, "", "", s3, s5, s4, null, s6, s7, "", stringbuffer1, l, "", 0, i, new StringBuffer());
        long l1 = al[0];
        for(int j = 0; j < pValues.length; j++)
        {
            unsetProperty(pValues[j]);
        }

        if(TextUtils.isValueExpression(s2))
        {
            String s9 = stringbuffer1.toString();
            StringBuffer stringbuffer2 = new StringBuffer();
            Array array = new Array();
            int k;
            if(stringbuffer.length() == 0 || (k = TextUtils.matchExpression(s9, stringbuffer.toString())) == Monitor.kURLok)
            {
                k = TextUtils.matchExpression(s9, s2, array, stringbuffer2);
                if(k != Monitor.kURLok)
                {
                    String s11 = URLMonitor.getHTMLEncoding(s9);
                    k = TextUtils.matchExpression(s9, I18N.UnicodeToString(s2, s11), array, stringbuffer2);
                }
            }
            if(k == Monitor.kURLok)
            {
                s8 = updateMatchValues(s, s2, false, array);
            } else
            {
                updateErrorValues(s, s2);
                l1 = k;
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                if(l1 == 200L && s8.length() > 0)
                {
                    setProperty(pStateString, s8);
                    setProperty(pStatus, String.valueOf(l1));
                } else
                {
                    String s10 = "";
                    if(l1 == 200L)
                    {
                        s10 = "invalid match expression ";
                        setProperty(pStatus, " no match ");
                    } else
                    {
                        s10 = lookupStatus(l1) + s8;
                        setProperty(pStatus, lookupStatus(l1));
                        setProperty(pNoData, "n/a");
                    }
                    setProperty(getLocationProperty(pStateString, s), s10);
                    setProperty(getLocationProperty(pMeasurement, s), String.valueOf(0));
                }
            }
        }
        return true;
    }

    public String updateMatchValues(String s, String s1, boolean flag, Array array)
    {
        String s2 = "";
        String as[] = buildThresholdsArray();
        for(int i = 0; i < nMaxCounters; i++)
        {
            if(array.size() <= i || nMaxCounters <= i || as.length <= i)
            {
                continue;
            }
            if(s2.length() > 0)
            {
                s2 = s2 + ", ";
            }
            if(getProperty("value" + i).length() > 0)
            {
                setProperty(getLocationProperty(getPropertyObject("value" + i), s), array.at(i));
            }
            s2 = s2 + as[i] + " = " + array.at(i);
        }

        if(flag)
        {
            int j = getSettingAsLong("_urlContentMatchDisplayMax", 150);
            if(s2.length() > j)
            {
                s2 = s2.substring(0, j) + "...";
            }
        }
        return s2;
    }

    public String getCountersContent()
    {
        return getProperty(pCounters);
    }

    public String buildRegExp(StringBuffer stringbuffer)
    {
        StringBuffer stringbuffer1 = new StringBuffer("/");
        stringbuffer.append("/");
        boolean flag = false;
        int i = allCounters.length;
        boolean aflag[] = new boolean[i];
        stringbuffer1 = stringbuffer1.append("Up\\/Down Time.*?");
        boolean flag1 = true;
        for(int j = 0; j < i; j++)
        {
            if(allCounters[j].startsWith("Apology") && flag1)
            {
                flag1 = false;
                stringbuffer1 = stringbuffer1.append("Network Error.*?");
            }
            if(allCounters[j].matches(".*Up\\/Down Time.*"))
            {
                if(isCounterSelected(allCounters[j]))
                {
                    String s = "(UP|DOWN).*?";
                    stringbuffer1 = stringbuffer1.append(s);
                    stringbuffer.append(s);
                } else
                {
                    String s1 = "[UD][PO]W?N?.*?";
                    stringbuffer1 = stringbuffer1.append(s1);
                    stringbuffer.append(s1);
                }
                continue;
            }
            if(isCounterSelected(allCounters[j]))
            {
                stringbuffer1 = stringbuffer1.append(">\\s*([-0-9]\\d*\\.?\\d*).*?");
            } else
            {
                stringbuffer1 = stringbuffer1.append(">\\s*[-0-9]\\d*\\.?\\d*.*?");
            }
        }

        stringbuffer1 = stringbuffer1.append("/s");
        stringbuffer.append("/s");
        if(flag)
        {
            System.out.println(stringbuffer1);
        }
        return stringbuffer1.toString();
    }

    public String buildRegExp()
    {
        return buildRegExp(new StringBuffer());
    }

    private boolean isCounterSelected(String s)
    {
        String as[] = TextUtils.split(getCountersContent(), ",");
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].equals(s))
            {
                return true;
            }
        }

        return false;
    }

    public String getUnitTestURL()
    {
        return "http://scheduler.dragonflow.com/SS_tests/oracle_stats.htmhttp://sunqa1:4000/webcacheadmin?SCREEN_ID=CGA.Site.Stats&ACTION=Show";
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pURL)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if(stringproperty == pCounters)
        {
            if(s.equals("No Counters available for this machine"))
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getTestURL()
    {
        String s = getProperty(pURL);
        if(TextUtils.isSubstituteExpression(s))
        {
            s = TextUtils.substitute(s);
        }
        String s1 = HTTPRequest.encodeString(s);
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=get&host=" + s1;
        String s3 = I18N.toDefaultEncoding(getProperty(pID));
        String s4 = HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID)));
        if(s4.length() > 0)
        {
            s2 = s2 + "&group=" + s4;
        }
        if(s3.length() > 0)
        {
            s2 = s2 + "&id=" + s3;
        }
        return s2;
    }

    static 
    {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=Oracle9iMonitor";
        templateFile = "counters.oracle";
        defaultCountersFromTemplate = "";
        pCounters = new StringProperty("_apacheCounters", getTemplateContent(getTemplatePath(), templateFile, true), "Oracle9i Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=URLContent\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        defaultCountersFromTemplate = pCounters.getDefault();
        allCounters = TextUtils.split(defaultCountersFromTemplate, ",");
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the server administration URL (example: http://server:port/webcacheadmin?SCREEN_ID=CGA.Site.Stats&ACTION=Show)");
        pURL.setParameterOptions(true, 2, false);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 1, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 2, true);
        pStatus = new StringProperty("status");
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization");
        pUserName.setParameterOptions(true, 3, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
        pPassword.setParameterOptions(true, 4, true);
        pPassword.isPassword = true;
        pChallengeResponse = new BooleanProperty("_challengeResponse", "");
        pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
        pChallengeResponse.setParameterOptions(true, 5, true);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 6, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 7, true);
        pProxyPassword.isPassword = true;
        StringProperty astringproperty[] = {
            pURL, pCounters, pTimeout, pProxy, pProxyUserName, pProxyPassword, pChallengeResponse, pUserName, pPassword, pStatus
        };
        addProperties("COM.dragonflow.StandardMonitor.Oracle9iMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.Oracle9iMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.Oracle9iMonitor", Rule.stringToClassifier("status != 200\terror"));
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "description", "Monitors Oracle9i Application Server performance statistics.");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "help", "Oracle9iServerMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "title", "Oracle9i Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "class", "Oracle9iMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "topazName", "Oracle9iAS HTTP Server");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "topazType", "Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.Oracle9iMonitor", "applicationType", "URLContent");
    }
}
