/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SiebelMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SiebelMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.RawXmlWriter;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

import java.io.File;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLSequenceMonitor, URLMonitor

public class SiebelMonitor extends BrowsableBase
{

    static StringProperty pURL;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static StringProperty pProxy;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static BooleanProperty pFormBasedAuthentication;
    static StringProperty pAuthFormName;
    static StringProperty pAuthUsernameFormField;
    static StringProperty pAuthPasswordFormField;
    static StringProperty pAuthFormButton;
    static String template_path;
    static String template_name = "counters.siebel";
    static int nMaxCounters;
    String findStartCapital;
    String findEndCapital;
    String findStart;
    String findEnd;

    public SiebelMonitor()
    {
        findStartCapital = "<TD>";
        findEndCapital = "<BR></TD>";
        findStart = "<td>";
        findEnd = "<br></td>";
    }

    public Array getConnectionProperties()
    {
        Array array = new Array();
        array.add(pURL);
        array.add(pPassword);
        array.add(pUsername);
        array.add(pProxy);
        array.add(pProxyUserName);
        array.add(pProxyPassword);
        array.add(pFormBasedAuthentication);
        array.add(pAuthFormName);
        array.add(pAuthUsernameFormField);
        array.add(pAuthPasswordFormField);
        array.add(pAuthFormButton);
        return array;
    }

    protected long[] doFormBasedAuth(StringBuffer stringbuffer, String s, String s1, String s2, String s3, String s4)
    {
        HashMap hashmap = new HashMap();
        hashmap.add("_location", "");
        hashmap.add("_encoding", I18N.getDefaultEncoding());
        hashmap.add("_referenceType1", "url");
        hashmap.add("_referenceType2", "form");
        hashmap.add("_reference1", getProperty(pURL));
        hashmap.add("_timeout", "60");
        String s5 = getProperty(pAuthFormName);
        String s6 = getProperty(pAuthFormButton);
        StringBuffer stringbuffer1 = new StringBuffer("");
        if(s6 != null && s6.length() > 0)
        {
            stringbuffer1 = stringbuffer1.append(s6);
        }
        if(s5 != null && s5.length() > 0)
        {
            stringbuffer1 = stringbuffer1.append(s5);
        }
        hashmap.add("_reference2", stringbuffer1.toString());
        hashmap.add("_postData2", getProperty(pAuthUsernameFormField) + "=" + s + "\n" + getProperty(pAuthPasswordFormField) + "=" + s1);
        StringBuffer stringbuffer2 = new StringBuffer();
        StringBuffer stringbuffer3 = new StringBuffer();
        StringBuffer stringbuffer4 = new StringBuffer();
        long al[] = URLSequenceMonitor.checkURLSequence(hashmap, stringbuffer2, null, stringbuffer3, stringbuffer, stringbuffer4, null, true);
        return al;
    }

    protected boolean update()
    {
        String s = getProperty(pURL);
        String s1 = getProperty(pPassword);
        String s2 = getProperty(pUsername);
        String s3 = getProperty(pProxy);
        String s4 = getProperty(pProxyUserName);
        String s5 = getProperty(pProxyPassword);
        StringBuffer stringbuffer = new StringBuffer();
        long al[];
        if(getProperty(pFormBasedAuthentication) != null && (new Boolean(getProperty(pFormBasedAuthentication))).booleanValue())
        {
            al = doFormBasedAuth(stringbuffer, s2, s1, s3, s4, s5);
        } else
        {
            SocketSession socketsession = SocketSession.getSession(null);
            al = URLMonitor.checkURL(socketsession, s, null, null, s3, s4, s5, new Array(), s2, s1, null, stringbuffer, 0x7fffffffffffffffL, null, 0, 15000, null);
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        int i;
        for(i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i++) { }
        if(al[0] != 200L && al[0] != -996L)
        {
            stringbuffer1.append(al[0] + "\n Explanation: " + URLMonitor.lookupStatus(al[0]));
        }
        String s6 = stringbuffer.toString();
        String as[] = new String[i];
        String as1[] = new String[i];
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            String s7 = getProperty(PROPERTY_NAME_COUNTER_ID + (k + 1));
            s7 = s7.substring(s7.indexOf(" ", 0) + 1);
            StringBuffer stringbuffer2 = new StringBuffer();
            Array array = new Array();
            int j1 = TextUtils.matchExpression(s6, s7, array, stringbuffer2);
            if(j1 != Monitor.kURLok)
            {
                String s10 = URLMonitor.getHTMLEncoding(s6);
                j1 = TextUtils.matchExpression(s6, I18N.UnicodeToString(s7, s10), array, stringbuffer2);
            }
            if(j1 != 200)
            {
                as1[k] = "content match error " + stringbuffer2;
                as[k] = "";
            }
            if(array.size() > 0)
            {
                as[k] = (String)array.at(0);
                as1[k] = "";
            }
        }

        if(stillActive())
        {
            synchronized(this)
            {
                String s8 = "";
                if(stringbuffer1.length() <= 0)
                {
                    for(int l = 0; l < as.length; l++)
                    {
                        String s9 = getProperty(PROPERTY_NAME_COUNTER_NAME + (l + 1));
                        if(as[l] != null && as[l].equals("") || as1[l].length() > 0)
                        {
                            setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), "n/a");
                            s8 = s8 + s9 + " = " + as1[l];
                            if(l < as.length - 1)
                            {
                                s8 = s8 + ", ";
                            }
                            j++;
                            continue;
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), TextUtils.removeChars(as[l], ","));
                        s8 = s8 + s9 + " = " + as[l];
                        if(l < as.length - 1)
                        {
                            s8 = s8 + ", ";
                        }
                    }

                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), j);
                } else
                {
                    for(int i1 = 0; i1 < nMaxCounters; i1++)
                    {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), "n/a");
                    }

                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                    setProperty(pNoData, "n/a");
                    s8 = stringbuffer1.toString();
                }
                setProperty(pStateString, s8);
            }
        }
        return true;
    }

    public String getBrowseData(StringBuffer stringbuffer)
    {
        String s = getProperty(pURL);
        String s1 = getProperty(pPassword);
        String s2 = getProperty(pUsername);
        String s3 = getProperty(pProxy);
        String s4 = getProperty(pProxyUserName);
        String s5 = getProperty(pProxyPassword);
        StringBuffer stringbuffer1 = new StringBuffer();
        long al[];
        if(getProperty(pFormBasedAuthentication).length() > 0)
        {
            al = doFormBasedAuth(stringbuffer1, s2, s1, s3, s4, s5);
        } else
        {
            SocketSession socketsession = SocketSession.getSession(null);
            al = URLMonitor.checkURL(socketsession, s, null, null, s3, s4, s5, new Array(), s2, s1, null, stringbuffer1, 0x7fffffffffffffffL, null, 0, 15000, null);
        }
        if(al[0] != 200L && al[0] != -996L)
        {
            stringbuffer.append(al[0] + "\n Explanation: " + URLMonitor.lookupStatus(al[0]));
            return "";
        }
        String s6 = stringbuffer1.toString();
        if(s6.indexOf("System Stat") < 0)
        {
            stringbuffer.append("File does not contain system statistics.");
            return "";
        } else
        {
            String s7 = createXMLFromTemplate(s6);
            return s7;
        }
    }

    private String createXMLFromTemplate(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = getProperty("_siebelTemplateFile");
        if(s1.length() <= 0)
        {
            s1 = template_name;
        }
        try
        {
            stringbuffer = FileUtils.readFile(template_path + s1);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        StringBuffer stringbuffer1 = new StringBuffer();
        RawXmlWriter rawxmlwriter = new RawXmlWriter(stringbuffer1);
        rawxmlwriter.startElement("browse_data");
        String as[] = TextUtils.split(stringbuffer.toString(), URLMonitor.CRLF);
        String s2 = "";
        Array array = new Array();
        String s3 = "";
        String s4 = "";
        for(int i = 0; i < as.length; i++)
        {
            String s5 = as[i];
            String as1[] = TextUtils.split(s5, "\t");
            if(as1[0].indexOf("topobject") >= 0 && as1[3].startsWith("static"))
            {
                s2 = as1[1];
                rawxmlwriter.startElement("object name=\"" + RawXmlWriter.enCodeElement(s2) + "\" ");
                continue;
            }
            if(s2.length() > 0 && as1[0].startsWith("startobject") && as1[3].startsWith("static"))
            {
                rawxmlwriter.startElement("object name=\"" + RawXmlWriter.enCodeElement(as1[1]) + "\" ");
                continue;
            }
            if(s2.length() > 0 && as1[0].startsWith("counter") && as1[3].startsWith("static"))
            {
                rawxmlwriter.emptyElement("counter name=\"" + RawXmlWriter.enCodeElement(as1[1]) + "\" id=\"" + RawXmlWriter.enCodeElement(as1[4]) + "\" ");
                continue;
            }
            if(s2.length() > 0 && as1[0].startsWith("endobject") && as1[3].startsWith("static"))
            {
                rawxmlwriter.endElement("object");
                continue;
            }
            if(as1[0].indexOf("topobject") >= 0 && as1[3].startsWith("dynamic"))
            {
                rawxmlwriter.endElement("object");
                s2 = "";
                s3 = as1[4];
                rawxmlwriter.startElement("object name=\"" + RawXmlWriter.enCodeElement(as1[1]) + "\" ");
                s4 = as1[1];
                continue;
            }
            if(as1[3].startsWith("dynamic"))
            {
                array.add(s5);
            }
        }

        int j = s.indexOf(s4);
        int k = s.indexOf(s3);
        String s6 = s.substring(j, k);
        int l = s6.indexOf(findStart);
        if(l < 0)
        {
            findStart = findStartCapital;
            findEnd = findEndCapital;
        }
        String s7 = s6.substring(s6.indexOf(findStart) + findStart.length());
        s7 = s7.substring(0, s7.indexOf(findEnd));
        buildApplicationXML(s6, rawxmlwriter, s7, array);
        rawxmlwriter.endElement("object");
        rawxmlwriter.endElement("browse_data");
        try
        {
            FileUtils.writeFile(Platform.getRoot() + File.separator + "templates.applications" + File.separator + "Siebel.xml", stringbuffer1.toString());
        }
        catch(Exception exception1)
        {
            exception1.printStackTrace();
        }
        return stringbuffer1.toString();
    }

    void buildApplicationXML(String s, RawXmlWriter rawxmlwriter, String s1, Array array)
    {
        int i = s.indexOf(s1);
        if(i < 0)
        {
            return;
        }
        boolean flag = false;
        String s2 = new String(s);
        String s3 = s2.substring(s2.indexOf(s1, 0));
        if(s3.indexOf(findStart) < 0)
        {
            flag = true;
        }
        int j = s3.indexOf("<", 0);
        String s4 = s3.substring(0, j);
        for(int k = 0; k < array.size(); k++)
        {
            String s6 = (String)array.at(k);
            String as[] = TextUtils.split(s6, "\t");
            if(as[0].startsWith("startobject") && as[3].startsWith("dynamic"))
            {
                rawxmlwriter.startElement("object name=\"" + RawXmlWriter.enCodeElement(TextUtils.replaceString(as[1], "APPLICATIONNAME", s4)) + "\" ");
                continue;
            }
            if(as[0].startsWith("counter") && as[3].startsWith("dynamic"))
            {
                int l = s4.indexOf("/");
                String s7 = "";
                if(l >= 0)
                {
                    s7 = TextUtils.replaceString(s4, "/", "\\/");
                } else
                {
                    s7 = s4;
                }
                rawxmlwriter.emptyElement("counter name=\"" + RawXmlWriter.enCodeElement(as[1]) + "\" id=\"" + RawXmlWriter.enCodeElement(TextUtils.replaceString(as[4], "APPLICATIONNAME", s7)) + "\" ");
                continue;
            }
            if(as[0].startsWith("endobject") && as[3].startsWith("dynamic"))
            {
                rawxmlwriter.endElement("object");
            }
        }

        String s5 = s3.substring(j);
        s1 = s5.substring(s5.indexOf(findStart) + findStart.length());
        if(flag)
        {
            return;
        } else
        {
            s1 = s1.substring(0, s1.indexOf(findEnd));
            buildApplicationXML(s5, rawxmlwriter, s1, array);
            return;
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE))
        {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if(s1.length() <= 0)
            {
                String s2 = getProperty(PROPERTY_NAME_COUNTER_NAME + 1);
                if(s2.length() <= 0)
                {
                    hashmap.put(stringproperty, "No counters selected");
                }
            }
            return s;
        }
        if(stringproperty == pAuthFormName)
        {
            int i = -1;
            int j = s.lastIndexOf("]");
            int k = s.lastIndexOf("[");
            if(k != -1 && j != -1 && k < j)
            {
                i = TextUtils.toInt(s.substring(k + 1, j));
            }
            if(i < 0)
            {
                hashmap.put(stringproperty, "Invalid form identifier");
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURL));
    }

    public int getMaxCounters()
    {
        return nMaxCounters;
    }

    public void setMaxCounters(int i)
    {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_browsableContentMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public java.util.HashMap getTopazMeasurements()
    {
        java.util.HashMap hashmap = super.getTopazMeasurements();
        hashmap.remove(getTopazCounterLabel(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR)));
        return hashmap;
    }

    protected boolean supportTopazIndividualCounterQuality()
    {
        return true;
    }

    static 
    {
        template_path = Platform.getRoot() + File.separator + "templates.applications" + File.separator;
        nMaxCounters = 30;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_browsableContentMaxCounters"));
        if(nMaxCounters <= 0)
        {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        Array array = new Array();
        pURL = new StringProperty("_server");
        pURL.setDisplayText("Application URL", "URL of the web plug-in server stats page for the application.\n For example, http://siebelsrv/service/_stats.swe ");
        pURL.setParameterOptions(false, true, 4, false);
        array.add(pURL);
        pUsername = new StringProperty("_username", "");
        pUsername.setDisplayText("Username", "The username to access the web server stats page");
        pUsername.setParameterOptions(false, true, 6, false);
        array.add(pUsername);
        pPassword = new StringProperty("_password", "");
        pPassword.setDisplayText("Password", "The password for the web server stats page");
        pPassword.setParameterOptions(false, true, 7, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(false, true, 8, false);
        array.add(pProxy);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(false, true, 9, false);
        array.add(pProxyUserName);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(false, true, 10, false);
        pProxyPassword.isPassword = true;
        array.add(pProxyPassword);
        pFormBasedAuthentication = new BooleanProperty("_formBasedAuthenticationRequired");
        pFormBasedAuthentication.setDisplayText("HTML Form-based Authentication Required", "enable HTML form-based authentication");
        pFormBasedAuthentication.setParameterOptions(false, true, 11, false);
        array.add(pFormBasedAuthentication);
        pAuthFormName = new StringProperty("_authFormName", "[1]");
        pAuthFormName.setDisplayText("Authorization Form Name", "optional identifier of the form (the number of the form) if the server requires HTML form-based authentication");
        pAuthFormName.setParameterOptions(false, true, 12, false);
        array.add(pAuthFormName);
        pAuthUsernameFormField = new StringProperty("_authUsernameFormField");
        pAuthUsernameFormField.setDisplayText("Authorization Username Form Field", "optional username form field if the server requires HTML form-based authentication");
        pAuthUsernameFormField.setParameterOptions(false, true, 13, false);
        array.add(pAuthUsernameFormField);
        pAuthFormButton = new StringProperty("_authFormButton", "[1]");
        pAuthFormButton.setDisplayText("Authorization Form Button", "optional name or number identifier of the button to submit on the authorization form if the server requires HTML form-based authentication");
        pAuthFormButton.setParameterOptions(false, true, 14, false);
        array.add(pAuthFormButton);
        pAuthPasswordFormField = new StringProperty("_authPasswordFormField");
        pAuthPasswordFormField.setDisplayText("Authorization Password Form Field", "optional password form field if the server requires HTML form-based authentication");
        pAuthPasswordFormField.setParameterOptions(false, true, 15, false);
        array.add(pAuthPasswordFormField);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty1[i] = (StringProperty)array.at(i);
        }

        String s = (COM.dragonflow.StandardMonitor.SiebelMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        addProperties(s, astringproperty2);
        addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors Siebel application statistics through the Siebel web server");
        setClassProperty(s, "help", "SiebelWebServerMon.htm");
        setClassProperty(s, "title", "Siebel Web Server");
        setClassProperty(s, "class", "SiebelMonitor");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Siebel Web Server");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "topazType", "Web Application Server");
        if(LUtils.isValidSSforXLicense(new SiebelMonitor()))
        {
            setClassProperty(s, "loadable", "true");
        } else
        {
            setClassProperty(s, "loadable", "false");
        }
    }
}
