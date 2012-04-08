/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * LinkMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>LinkMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.*;

import java.io.File;
import java.util.Vector;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor

public class LinkMonitor extends AtomicMonitor
{

    static StringProperty pURL;
    static StringProperty pTimeout;
    static StringProperty pProxy;
    static StringProperty pUserName;
    static StringProperty pChallengeResponse;
    static StringProperty pPassword;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static StringProperty pPostData;
    static StringProperty pPause;
    static StringProperty pMaxLinks;
    static StringProperty pMaxSearchDepth;
    static StringProperty pReport;
    static StringProperty pExternalLinks;
    static StringProperty pLinkErrors;
    static StringProperty pTotalPages;
    static StringProperty pTotalGraphics;
    static StringProperty pAverage;
    static StringProperty pBrokenLinks;
    String brokenLinks;

    public LinkMonitor()
    {
        brokenLinks = "";
    }

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pBrokenLinks)
        {
            return brokenLinks;
        }
        if(stringproperty == pReport)
        {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            return siteviewgroup.mainURL() + reportURL();
        } else
        {
            return super.getProperty(stringproperty);
        }
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    public String getTestURL()
    {
        return "/SiteView" + reportURL();
    }

    public String getTestTitle()
    {
        return "Summary";
    }

    public String reportURL()
    {
        return "/cgi/go.exe/SiteView?page=link&file=" + getSummaryFilename();
    }

    String getSummaryFilename()
    {
        return "LinkReport_" + getProperty(pOwnerID) + getProperty(pID) + ".log";
    }

    protected boolean update()
    {
        boolean flag = true;
        String s = Platform.getRoot() + File.separator + "logs" + File.separator + getSummaryFilename();
        Array array = TextUtils.enumToArray(getMultipleValues(pPostData));
        String s1 = getProperty(pUserName);
        if(getProperty(pChallengeResponse).length() > 0)
        {
            s1 = URLMonitor.NT_CHALLENGE_RESPONSE_TAG + s1;
        }
        LinkFinder linkfinder = new LinkFinder(getProperty(pURL), getProperty(pProxy), getProperty(pProxyUserName), getProperty(pProxyPassword), array, s1, getProperty(pPassword), getPropertyAsInteger(pTimeout) * 1000, getPropertyAsInteger(pPause), getPropertyAsInteger(pMaxLinks), getPropertyAsInteger(pMaxSearchDepth), s, this);
        boolean flag1 = getPropertyAsBoolean(pExternalLinks);
        linkfinder.search(flag1);
        UpdateProperties("", linkfinder.totalBroken, linkfinder.totalPages, linkfinder.totalGraphics, linkfinder.totalTime, 0, linkfinder.brokenSummary, linkfinder.brokenLinks);
        return flag;
    }

    public void UpdateProperties(String s, int i, int j, int k, long l, int i1, 
            String s1, String s2)
    {
        brokenLinks = s2;
        if(stillActive())
        {
            synchronized(this)
            {
                long l1 = 0L;
                if(j - i != 0)
                {
                    l1 = l / (long)(j - i);
                }
                setProperty(pLinkErrors, i);
                setProperty(pTotalPages, j);
                setProperty(pTotalGraphics, k);
                setProperty(pAverage, l1);
                String s3 = "";
                if(i == 0)
                {
                    s3 = "" + j + " links, " + k + " graphics, average " + l1 + " ms";
                } else
                {
                    s3 = "" + i + " errors, " + j + " links, " + k + " graphics, average " + l1 + " ms, " + s1;
                }
                if(s.length() != 0)
                {
                    s3 = "IN PROGRESS, " + s + ", " + s3;
                }
                setProperty(pStateString, s3);
                if(i == j)
                {
                    setProperty(pNoData, "n/a");
                }
            }
        }
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pLinkErrors);
        array.add(pTotalPages);
        array.add(pTotalGraphics);
        array.add(pAverage);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        String s1 = URLMonitor.verifyUrlMonitorProperty(stringproperty, s, hashmap);
        if(s1 != null)
        {
            s = s1;
        } else
        {
            s = super.verify(stringproperty, s, httprequest, hashmap);
        }
        return s;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pMaxSearchDepth)
        {
            Vector vector = new Vector();
            vector.addElement("1");
            vector.addElement("main page links");
            vector.addElement("3");
            vector.addElement("3");
            vector.addElement("5");
            vector.addElement("5");
            vector.addElement("10");
            vector.addElement("10");
            vector.addElement("100");
            vector.addElement("no limit");
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    static 
    {
        pURL = new StringProperty("_url");
        pURL.setDisplayText("URL", "the URL to start link checking from (example: http://demo." + Platform.exampleDomain + ")");
        pURL.setParameterOptions(true, 1, false);
        pPause = new NumericProperty("_pause", "250", "milliseconds");
        pPause.setDisplayText("Pause", "delay between checks, in milliseconds");
        pPause.setParameterOptions(true, 1, true);
        pMaxLinks = new NumericProperty("_maxLinks", "800", "links");
        pMaxLinks.setDisplayText("Maximum Links", "the maximum number of links to check");
        pMaxLinks.setParameterOptions(true, 2, true);
        pMaxSearchDepth = new ScalarProperty("_maxSearchDepth", "100");
        pMaxSearchDepth.setDisplayText("Maximum Hops", "the maximum \"hops\" from the start URL to check");
        pMaxSearchDepth.setParameterOptions(true, 3, true);
        ((ScalarProperty)pMaxSearchDepth).allowOther = true;
        pReport = new StringProperty("_report");
        pReport.setDisplayText("Summary Report", "URL to summary report listing all the links checked");
        pReport.setParameterOptions(false, 4, true);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 5, true);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, 6, true);
        pChallengeResponse = new BooleanProperty("_challengeResponse", "");
        pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
        pChallengeResponse.setParameterOptions(true, 7, true);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization");
        pUserName.setParameterOptions(true, 8, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
        pPassword.setParameterOptions(true, 9, true);
        pPassword.isPassword = true;
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 10, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 11, true);
        pProxyPassword.isPassword = true;
        pPostData = new StringProperty("_postData", "", "POST Variables");
        pPostData.setDisplayText("POST Data", "optional name=value variables, one per line, to send with a POST request to the first URL checked");
        pPostData.setParameterOptions(true, 12, true);
        pPostData.isMultiLine = true;
        pExternalLinks = new BooleanProperty("_externalLinks", "");
        pExternalLinks.setDisplayText("Search External Links", "when selected, search all links on each page, not just links with the original base URL.");
        pExternalLinks.setParameterOptions(true, 5, false);
        pLinkErrors = new NumericProperty("linkErrors", "0", "errors");
        pLinkErrors.setLabel("link errors");
        pLinkErrors.setStateOptions(1);
        pTotalPages = new NumericProperty("links", "0", "links");
        pTotalPages.setLabel("total links");
        pTotalPages.setStateOptions(2);
        pAverage = new NumericProperty("average", "0", "milliseconds");
        pAverage.setLabel("average");
        pAverage.setStateOptions(2);
        pTotalGraphics = new NumericProperty("graphics", "0", "links");
        pTotalGraphics.setLabel("total graphics");
        pTotalGraphics.setStateOptions(2);
        pBrokenLinks = new StringProperty("brokenLinks");
        StringProperty astringproperty[] = {
            pURL, pTimeout, pPause, pMaxLinks, pMaxSearchDepth, pProxy, pProxyUserName, pProxyPassword, pChallengeResponse, pUserName, 
            pPassword, pPostData, pReport, pExternalLinks, pLinkErrors, pTotalPages, pAverage, pTotalGraphics, pBrokenLinks
        };
        addProperties("COM.dragonflow.StandardMonitor.LinkMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.LinkMonitor", Rule.stringToClassifier("linkErrors == 0\tgood", true));
        addClassElement("COM.dragonflow.StandardMonitor.LinkMonitor", Rule.stringToClassifier("linkErrors > 0\terror", true));
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "description", "Check all the links on a web site for problems.");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "help", "LinkMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "title", "Link Check");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "class", "LinkMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "target", "_url");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "defaultFrequency", "86400");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "toolPageDisable", "true");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "topazName", "Link Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.LinkMonitor", "topazType", "Web Application Server");
    }
}
