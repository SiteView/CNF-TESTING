/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SilverStreamMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SilverStreamMonitor</code>
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

public class SilverStreamMonitor extends URLContentBase
{

    static StringProperty pURL;
    static StringProperty pCounters;
    static StringProperty pTimeout;
    static StringProperty pProxy;
    static StringProperty pUserName;
    static StringProperty pChallengeResponse;
    static StringProperty pPassword;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static StringProperty pStatus;
    static String returnURL;
    public static String templateFile;
    public static int DEFAULT_TIMEOUT = 60000;

    public SilverStreamMonitor()
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
        String s2 = getSetting("_silverstreamRegExp");
        if(s2.length() <= 0)
        {
            s2 = buildRegExp();
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
        StringBuffer stringbuffer = new StringBuffer();
        String s8 = "";
        progressString += "Retrieving URL " + getProperty(pURL) + "\n";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("urlContentMatchMax", 51200);
        long al[] = URLContentMonitor.checkURL(s1, s2, "", s3, s5, s4, null, s6, s7, "", stringbuffer, l, "", 0, i, new StringBuffer());
        long l1 = al[0];
        for(int j = 0; j < pValues.length; j++)
        {
            unsetProperty(pValues[j]);
        }

        if(TextUtils.isValueExpression(s2))
        {
            if(isContentMatchStatus(l1))
            {
                s8 = updateMatchValues(s, s2, stringbuffer.toString(), false);
            } else
            {
                updateErrorValues(s, s2);
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
                    String s9 = "";
                    if(l1 == 200L)
                    {
                        s9 = "invalid match expression";
                        setProperty(pStatus, " no match ");
                    } else
                    {
                        s9 = lookupStatus(l1) + s8;
                        setProperty(pStatus, lookupStatus(l1));
                        setProperty(pNoData, "n/a");
                    }
                    setProperty(getLocationProperty(pStateString, s), s9);
                    setProperty(getLocationProperty(pMeasurement, s), String.valueOf(0));
                }
            }
        }
        return true;
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

    public String getTemplateFile()
    {
        return templateFile;
    }

    public String getCountersContent()
    {
        return getProperty(pCounters);
    }

    public String buildRegExp()
    {
        String s = new String("/");
        String as[] = TextUtils.split(getCountersContent(), ",");
        boolean flag = false;
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].equals("hits") || as[i].equals("bytes"))
            {
                s = s.concat(as[i] + ":(\\d*).*?");
                continue;
            }
            if(as[i].startsWith("Request processing times"))
            {
                s = s.concat("Request processing times:.*?");
                if(as[i].endsWith("(min)"))
                {
                    s = s.concat("(\\d+).*?");
                    i++;
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(i >= as.length)
                {
                    break;
                }
                if(as[i].endsWith("(avg)"))
                {
                    s = s.concat("(\\d+).*?");
                    i++;
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(i >= as.length)
                {
                    break;
                }
                if(as[i].endsWith("(max)"))
                {
                    s = s.concat("(\\d+).*?");
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                continue;
            }
            if(as[i].startsWith("Thread counts"))
            {
                s = s.concat("Thread counts:.*?");
                if(as[i].endsWith("(free)"))
                {
                    s = s.concat("(\\d+).*?");
                    i++;
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(i >= as.length)
                {
                    break;
                }
                if(as[i].endsWith("(idle)"))
                {
                    s = s.concat("(\\d+).*?");
                    i++;
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(i >= as.length)
                {
                    break;
                }
                if(as[i].endsWith("(total)"))
                {
                    s = s.concat("(\\d+).*?");
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                continue;
            }
            if(as[i].startsWith("Memory status"))
            {
                s = s.concat("Memory status:.*?");
                if(as[i].endsWith("(Free memory)"))
                {
                    s = s.concat("(\\d+).*?");
                    if(++i >= as.length)
                    {
                        break;
                    }
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(as[i].endsWith("(Total memory)"))
                {
                    s = s.concat("(\\d+).*?");
                    if(++i >= as.length)
                    {
                        break;
                    }
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                if(as[i].endsWith("(GC Count)"))
                {
                    s = s.concat("(\\d+).*?");
                } else
                {
                    s = s.concat("\\d+.*?");
                }
                continue;
            }
            if(as[i].startsWith("Current load"))
            {
                s = s.concat(as[i] + ".*?:.*?(\\d)");
                continue;
            }
            if(!as[i].startsWith("Session/License status"))
            {
                continue;
            }
            s = s.concat("Session/License status:.*?");
            if(as[i].endsWith("(Idle sessions)"))
            {
                s = s.concat("(\\d+).*?");
                if(++i >= as.length)
                {
                    break;
                }
            } else
            {
                s = s.concat("\\d+.*?");
            }
            if(as[i].endsWith("(Total sessions)"))
            {
                s = s.concat("(\\d+).*?");
                if(++i >= as.length)
                {
                    break;
                }
            } else
            {
                s = s.concat("\\d+.*?");
            }
            if(as[i].endsWith("(Used licenses)"))
            {
                s = s.concat("(\\d+).*?");
                if(++i >= as.length)
                {
                    break;
                }
            } else
            {
                s = s.concat("\\d+.*?");
            }
            if(as[i].endsWith("(Total licenses)"))
            {
                s = s.concat("(\\d+).*?");
            } else
            {
                s = s.concat("\\d+.*?");
            }
        }

        s = s.concat("/s");
        if(flag)
        {
            System.out.println(s);
        }
        return s;
    }

    public String getUnitTestURL()
    {
        return "http://199.203.78.57:8000/Silverstream/statistics";
    }

    public String getTestAuthUser()
    {
        return "SilverAdmin";
    }

    public String getTestAuthPass()
    {
        return "tr33-t0p";
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
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=SilverStreamMonitor";
        templateFile = "counters.silverstream";
        pCounters = new StringProperty("_apacheCounters", getTemplateContent(getTemplatePath(), templateFile, false), "SilverStream Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=URLContent\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the server administration URL (example: http://server:port/SilverStream/Statistics)");
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
        addProperties("COM.dragonflow.StandardMonitor.SilverStreamMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.SilverStreamMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.SilverStreamMonitor", Rule.stringToClassifier("status != 200\terror"));
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "description", "Monitors SilverStream Server performance statistics.");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "help", "SilverStreamMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "title", "SilverStream Server");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "class", "SilverStreamMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "topazName", "SilverStream");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.SilverStreamMonitor", "applicationType", "URLContent");
    }
}
