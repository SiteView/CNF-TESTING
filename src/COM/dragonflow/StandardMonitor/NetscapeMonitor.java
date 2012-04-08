/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NetscapeMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NetscapeMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.PrintStream;
import java.net.URLEncoder;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor, URLContentMonitor

public class NetscapeMonitor extends URLContentBase
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
    static String server = "";
    public static int DEFAULT_TIMEOUT = 30000;

    public NetscapeMonitor()
    {
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    protected boolean update()
    {
        String s = "";
        String s1 = getProperty(pURL);
        String s2 = getProperty(pCounters);
        String s3 = getSetting("_netscapeRegExp");
        String s4 = "";
        boolean flag = false;
        String as[] = {
            "Other", "Of", "404", "Max", "Cache", "Keep", "Thread"
        };
        String as1[] = {
            "Other", "Max", "Of", "transfer", "requ", "xx"
        };
        String as2[] = {
            "transfer", "requ", "xxx", "Thread"
        };
        String as3[] = {
            "sitemon", ".perf", "Stats"
        };
        StringBuffer stringbuffer = new StringBuffer();
        if(s1.indexOf(as3[0]) != -1)
        {
            if(anyStringsFoundIn(s2, as, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains 'sitemon', which is for Netscape version 4.x, but one of the counters you selected contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as) + " for this Netscape version. ";
            } else
            if(anyStringsFoundIn(s3, as, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains 'sitemon', which is for Netscape version 4.x, but _netscapeRegExp in the master.config contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as) + " for this Netscape version. ";
            } else
            {
                server = "sitemon";
                flag = true;
            }
        } else
        if(s1.indexOf(as3[1]) != -1)
        {
            if(anyStringsFoundIn(s2, as1, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains '.perf', which is for Netscape version 4.x perf dump, but one of the counters you selected contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as1) + " for this Netscape version. ";
            } else
            if(anyStringsFoundIn(s3, as1, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains '.perf', which is for Netscape version 4.x perf dump, but _netscapeRegExp in the master.config contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as1) + " for this Netscape version. ";
            } else
            {
                server = "perf";
                flag = true;
            }
        } else
        if(s1.indexOf(as3[2]) != -1)
        {
            if(anyStringsFoundIn(s2, as2, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains 'Stats', which is for Netscape version 6.0 stats, but one of the counters you selected contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as2) + " for this Netscape version. ";
            } else
            if(anyStringsFoundIn(s3, as2, stringbuffer))
            {
                flag = false;
                s4 = "Your URL: contains 'Stats', which is for Netscape version 6.0 stats, but _netscapeRegExp in the master.config contain this counter string: '" + stringbuffer.toString() + "' counters cannot contain any of these: " + getString(as2) + " for this Netscape version. ";
            } else
            {
                server = "Stats";
                flag = true;
            }
        } else
        {
            flag = false;
            s4 = "Your URL: must contain one of these strings: " + getString(as3);
        }
        if(s2.length() <= 0)
        {
            flag = false;
            s4 = "Counters are empty, select counters and update monitor";
        }
        if(s3.length() <= 0 && flag)
        {
            s3 = buildRegExp();
        }
        String s5 = getProperty(pProxy);
        String s6 = getProperty(pProxyPassword);
        String s7 = getProperty(pProxyUserName);
        String s8 = getProperty(pUserName);
        if(getProperty(pChallengeResponse).length() > 0)
        {
            s8 = URLMonitor.NT_CHALLENGE_RESPONSE_TAG + s8;
        }
        String s9 = getProperty(pPassword);
        int i = getPropertyAsInteger(pTimeout) * 1000;
        if(i == 0)
        {
            i = DEFAULT_TIMEOUT;
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        String s10 = "";
        progressString += "Retrieving URL " + getProperty(pURL) + "\n";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("urlContentMatchMax", 51200);
        long al[] = URLContentMonitor.checkURL(s1, s3, "", s5, s7, s6, null, s8, s9, "", stringbuffer1, l, "", 0, i, new StringBuffer());
        long l1 = al[0];
        String s11 = "";
        if(l1 == (long)Monitor.kURLTimeoutError && stringbuffer1.toString().indexOf("THIS_STRING_NEVER_HAPPENS") >= 0)
        {
            l1 = Monitor.kURLok;
        }
        for(int j = 0; j < pValues.length; j++)
        {
            unsetProperty(pValues[j]);
        }

        if(TextUtils.isValueExpression(s3))
        {
            if(flag && isContentMatchStatus(l1))
            {
                s10 = updateMatchValues(s, s3, stringbuffer1.toString(), false);
            } else
            {
                updateErrorValues(s, s3);
            }
        }
        if(stillActive())
        {
            synchronized(this)
            {
                if(l1 == 200L && s10.length() > 0 && flag)
                {
                    setProperty(pStateString, s10);
                    setProperty(pStatus, String.valueOf(l1));
                } else
                {
                    if(l1 == 200L && flag)
                    {
                        String s12 = "DID NOT FIND VALUES";
                        if(s10.length() > 0)
                        {
                            s12 = s10;
                        }
                        s11 = s11 + "Invalid content match expression with regular expression: '" + s3 + " " + s12 + " from this URL: '" + s1 + "'. The regular expression must contain parenthesis and match something in the URL page.";
                        setProperty(pStatus, " no match ");
                    } else
                    if(!flag)
                    {
                        s11 = "Server and counter mismatch " + s4;
                        setProperty(pStatus, " no data ");
                    } else
                    {
                        String s13 = "DID NOT MATCH";
                        if(s10.length() > 0)
                        {
                            s13 = s10;
                        }
                        s11 = lookupStatus(l1) + ": with regular expression: '" + s3 + " " + s13 + " from this URL: '" + s1 + "'";
                        setProperty(pStatus, String.valueOf(l1));
                        setProperty(pNoData, "n/a");
                    }
                    setProperty(getLocationProperty(pStateString, s), s11);
                    setProperty(getLocationProperty(pMeasurement, s), String.valueOf(0));
                }
            }
        }
        return true;
    }

    private String getString(String as[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            stringbuffer.append("'" + as[i] + "' ");
        }

        return stringbuffer.toString();
    }

    private boolean anyStringsFoundIn(String s, String as[], StringBuffer stringbuffer)
    {
        for(int i = 0; i < as.length; i++)
        {
            if(s.indexOf(as[i]) != -1)
            {
                stringbuffer.append(as[i]);
                return true;
            }
        }

        return false;
    }

    public String getTemplateFile()
    {
        return templateFile;
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

    public String getCountersContent()
    {
        return getProperty(pCounters);
    }

    public String buildRegExp()
    {
        String s = new String();
        String as[] = TextUtils.split(getCountersContent(), ",");
        boolean flag = false;
        if(server.indexOf("perf") == -1)
        {
            s = "/.?";
            for(int i = 0; i < as.length; i++)
            {
                if(as[i].indexOf("transfer") != -1 || as[i].indexOf("Total requ") != -1 || as[i].indexOf("of Process") != -1)
                {
                    s = s.concat(".*?<TD>[\\w\\W]{0,64}" + as[i] + ":<\\/TD><TD>(\\d*)<\\/TD>");
                    continue;
                }
                if(as[i].indexOf("Number Of") != -1 || as[i].indexOf("Max") != -1 || as[i].indexOf("Alive Time") != -1 && as[i].indexOf("outs") == -1)
                {
                    s = s.concat(".*?<TD>[\\w\\W]{0,64}" + as[i] + ":.*?<\\/TD><TD>(\\d*)\\W?<\\/TD>");
                } else
                {
                    s = s.concat(".*<TD[\\w\\W]{20,96}" + as[i] + "[\\w\\W]{5,17}<\\/TD>[\\w\\W]{96,128}center[\\w\\W]{48,64}3\">(\\d*)[\\w\\W]{0,8}<\\/TD>");
                }
            }

            s = s.concat("/s");
        } else
        if(server.indexOf("perf") != -1)
        {
            s = "/.?";
            for(int j = 0; j < as.length; j++)
            {
                s = s.concat("[\\w\\W]*?" + as[j] + "[\\s]{2,80}([\\/\\w\\d\\:]{0,80})");
            }

            s = s.concat("/");
        } else
        {
            s = s.concat("/[\\w\\W]{1,80}/s");
        }
        if(flag)
        {
            System.out.println("Netscape RegExp= " + s);
        }
        return s;
    }

    public String getUnitTestURL()
    {
        return "http://cnudelman:8888/https-CNUDELMAN.dragonflow.com/bin/sitemon?doit";
    }

    public String getTestAuthUser()
    {
        return "admin";
    }

    public String getTestAuthPass()
    {
        return "christie";
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

    static 
    {
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=NetscapeMonitor";
        templateFile = "counters.iplanet";
        pCounters = new StringProperty("_apacheCounters", getTemplateContent(getTemplatePath(), templateFile, false), "Netscape Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=URLContent\">choose counters</A>");
        pCounters.setParameterOptions(true, 1, false);
        pCounters.isMultiLine = true;
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the server administration URL (example: http://server:adminport/https-SERVERINSTANCE/bin/sitemon?doit:)");
        pURL.setParameterOptions(true, 2, false);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization");
        pUserName.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
        pPassword.setParameterOptions(true, 4, false);
        pPassword.isPassword = true;
        pTimeout = new NumericProperty("_timeout", "20", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 1, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 2, true);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 3, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 4, true);
        pProxyPassword.isPassword = true;
        pChallengeResponse = new BooleanProperty("_challengeResponse", "");
        pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
        pChallengeResponse.setParameterOptions(true, 5, true);
        pStatus = new StringProperty("status");
        StringProperty astringproperty[] = {
            pURL, pCounters, pTimeout, pProxy, pProxyUserName, pProxyPassword, pChallengeResponse, pUserName, pPassword, pStatus
        };
        addProperties("COM.dragonflow.StandardMonitor.NetscapeMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.NetscapeMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.NetscapeMonitor", Rule.stringToClassifier("status != 200\terror"));
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "description", "Monitors iPlanet 4.1, 6.0 and Netscape 3.x server statistics (formerly the Netscape Server Monitor).");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "help", "iPlanetServerMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "title", "iPlanet Server");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "class", "NetscapeMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "topazName", "Netscape");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "topazType", "Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.NetscapeMonitor", "applicationType", "URLContent");
    }
}
