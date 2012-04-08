/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebServiceMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebServiceMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Properties.XMLStringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SoapRpc;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// URLMonitor

public class WebServiceMonitor extends AtomicMonitor {

    static StringProperty pWSDLurl;

    static StringProperty pMethodName;

    static StringProperty pServerURL;

    static StringProperty pMatchString;

    static StringProperty pMatchValue;

    static StringProperty pMethodNS;

    static StringProperty pActionURI;

    static StringProperty pContentType;

    static StringProperty pSchema;

    static StringProperty pUserName;

    static StringProperty pPassword;

    static StringProperty pProxyUserName;

    static StringProperty pProxyPassword;

    static StringProperty pProxy;

    static StringProperty pNTLMDomain;

    static StringProperty pUseDotNetSOAP;

    static StringProperty pUserAgent;

    static StringProperty pArgNames;

    static StringProperty pStatus;

    static StringProperty pRoundTripTime;

    public static final String WEBSERVICE_ARG_DELIMITER = "\n";

    public WebServiceMonitor() {
    }

    public String getHostname() {
        return HTTPUtils.hostFromURL(getProperty(pServerURL));
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        String s = getProperty(pWSDLurl);
        String s1 = getProperty(pMethodNS);
        String s2 = getProperty(pActionURI);
        String s3 = getProperty(pServerURL);
        String s4 = getProperty(pMethodName);
        String s5 = getProperty(pMatchString);
        String s6 = getProperty(pArgNames);
        String s7 = getProperty(pContentType);
        String s8 = getProperty(pSchema);
        String s9 = getProperty(pUserName);
        String s10 = getProperty(pPassword);
        String s11 = getProperty(pProxy);
        String s12 = getProperty(pProxyPassword);
        String s13 = getProperty(pProxyUserName);
        String s14 = getProperty(pNTLMDomain);
        String s15 = getProperty(pUseDotNetSOAP);
        String s16 = getSetting(pUserAgent);
        boolean flag = false;
        int i = 0;
        long l = 0L;
        String s17 = "";
        String s18 = "";
        String s19 = "";
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        boolean flag1 = s15.length() > 0;
        Object[] aobj = rpcCall(s, s4, s6, s7, s8, s1, s2, s16, s3, stringbuffer, stringbuffer1, s9, s10, s11, s13, s12, s14, flag1);
        if (aobj != null) {
            i = ((Long) aobj[0]).intValue();
            s18 = (String) aobj[1];
            l = ((Long) aobj[2]).longValue();
            s19 = (String) aobj[3];
        }
        if (aobj == null && stringbuffer.length() <= 0) {
            s17 = "no data";
        } else if (stringbuffer.length() > 0) {
            s17 = stringbuffer.toString();
        } else if (s19.length() > 0) {
            s17 = s19;
            i = URLMonitor.kSOAPFaultError;
        } else if (i != 200 && i != 301 && i != 302) {
            s17 = URLMonitor.lookupStatus(i);
        } else {
            s17 = TextUtils.floatToString((float) l / 1000F, 2) + " sec";
            flag = true;
        }
        if (s5.length() != 0 && aobj != null && flag) {
            Array array = new Array();
            i = TextUtils.matchExpressionForWebServiceMonitor(s18, s5, array, new StringBuffer());
            if (i != Monitor.kURLok) {
                String s20 = URLMonitor.getHTMLEncoding(s18);
                i = TextUtils.matchExpression(s18, I18N.UnicodeToString(s5, s20), array, new StringBuffer());
            }
            if (i != kURLok) {
                s17 = "content match error ";
            } else {
                s17 = "matched " + s17;
                if (array.size() > 0) {
                    setProperty(pMatchValue, array.at(0));
                }
            }
        }
        if (stillActive()) {
            synchronized (this) {
                setProperty(pRoundTripTime, TextUtils.floatToString((float) l / 1000F, 2));
                if (aobj == null) {
                    setProperty(pStatus, "n/a");
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pNoData, "n/a");
                    setProperty(pStateString, s17);
                    return true;
                }
                setProperty(pStatus, i);
                setProperty(pStateString, s17);
            }
        }

        return true;
    }

    public static Object[] rpcCall(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, StringBuffer stringbuffer, StringBuffer stringbuffer1, String s9, String s10, String s11, String s12, String s13,
            String s14, boolean flag) {
        Vector vector;
        String as[];
        Object aobj[];
        vector = new Vector();
        as = getParameters(s2, vector);
        aobj = null;
        SoapRpc soaprpc = new SoapRpc();
        if (stringbuffer.length() > 0) {
            return aobj;
        }
        try {
            aobj = soaprpc.execute(s1, vector, s4, as, s, s3, s5, s6, s7, s8, s9, s10, s11, s12, s13, stringbuffer1, s14, flag);
        } catch (Exception exception) {
            exception.printStackTrace();
            stringbuffer.append(exception.toString());
        }
        return aobj;
    }

    public String getTestURL() {
        String s = "/SiteView/cgi/go.exe/SiteView?page=serviceWizard&operation=Tool&class=WebServiceMonitor";
        s = s + "&group=" + HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID))) + "&id=" + I18N.toDefaultEncoding(getProperty("_id"));
        return s;
    }

    public String getAddPage() {
        return "serviceWizard";
    }

    private static String[] getParameters(String s, Vector vector) {
        String as[] = splitArguments(s);
        String as1[] = new String[128];
        for (int i = 0; i < as.length; i ++) {
            as1[i] = TextUtils.readStringFromStart(as[i], "=");
            int j = as[i].indexOf('=');
            vector.addElement(as[i].substring(j + 1));
        }

        return as1;
    }

    private static String[] splitArguments(String s) {
        String s1 = null;
        if (s.indexOf("\n") >= 0) {
            s1 = "\n";
        } else {
            s1 = "\n";
        }
        return TextUtils.split(s, s1);
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        return array;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pSchema) {
            Vector vector = new Vector();
            vector.addElement("SOAP");
            vector.addElement("SOAP");
            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        URL url;
        if (stringproperty == pProxy && s.length() > 0) {
            try {
                url = new URL(s);
            } catch (Exception exception) {
                hashmap.put(stringproperty, "Proxy URL is invalid.");
                return s;
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    static {
        String s = (COM.dragonflow.StandardMonitor.WebServiceMonitor.class).getName();
        String s1 = "com.sun.crypto.provider.SunJCE";
        try {
            Provider provider = (Provider) Class.forName(s1).newInstance();
            Security.addProvider(provider);
        } catch (Exception exception) {
            exception.printStackTrace();
            LogManager.log("Error", exception.getMessage() + " (Cannot support Cryptographic Authentication)");
        }
        pWSDLurl = new StringProperty("_wsdlurl", "");
        pWSDLurl.setDisplayText("URL of the WSDL", "the url of the Web Service Description file");
        pWSDLurl.setParameterOptions(true, 2, false);
        pMethodName = new StringProperty("_methodname");
        pMethodName.setDisplayText("Method Name", "the method name in the server for the call");
        pMethodName.setParameterOptions(true, 4, false);
        pServerURL = new StringProperty("_serverurl");
        pServerURL.setDisplayText("Server URL", "The URL of the web service to be monitored");
        pServerURL.setParameterOptions(true, 5, false);
        pArgNames = new XMLStringProperty("_argnames");
        pArgNames.setDisplayText("Name of arguments", "The name of aguments in the same order as above");
        pArgNames.isMultiLine = true;
        pArgNames.multiLineDelimiter = "\n";
        pArgNames.setParameterOptions(true, 6, false);
        pMatchString = new StringProperty("_matchstring");
        pMatchString.setDisplayText("Content Match", "optional, match against query result, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
        pMatchString.setParameterOptions(true, 1, true);
        pContentType = new StringProperty("_contenttype");
        pContentType.setDisplayText("HTTP Content-Type ", "The HTTP request header content type (optional)");
        pContentType.setParameterOptions(true, 2, true);
        pUserAgent = new StringProperty("_URLUserAgent");
        pUserAgent.setDisplayText("HTTP User-Agent ", "The HTTP User-Agent for the SOAP request (optional)");
        pUserAgent.setParameterOptions(true, 3, true);
        pUseDotNetSOAP = new BooleanProperty("_useDotNetSoap");
        pUseDotNetSOAP.setDisplayText("Use .NET SOAP", "Check this box if web service is based on MS .NET");
        pUseDotNetSOAP.setParameterOptions(true, 4, true);
        pSchema = new ScalarProperty("_schema", "");
        pSchema.setDisplayText("Request's schema", "What is the schema SOAP or XML");
        pSchema.setParameterOptions(true, 5, true);
        pMethodNS = new StringProperty("_methodns");
        pMethodNS.setDisplayText("Method Name Space", "Method name space.");
        pMethodNS.setParameterOptions(true, 6, true);
        pActionURI = new StringProperty("_actionuri");
        pActionURI.setDisplayText("SOAP ACTION", "The SOAP ACTION url in the request header.");
        pActionURI.setParameterOptions(true, 7, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pNTLMDomain = new StringProperty("_ntlmDomain");
        pNTLMDomain.setDisplayText("NTLM Domain", "Domain name (required only if using NTLM Authorization)");
        pNTLMDomain.setParameterOptions(true, 9, true);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("Authorization User Name", "optional user name if the Web Service requires authorization");
        pUserName.setParameterOptions(true, 10, true);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Authorization Password", "optional password if the Web Service requires authorization");
        pPassword.setParameterOptions(true, 11, true);
        pPassword.isPassword = true;
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional proxy server URL to use (example: http://proxy." + Platform.exampleDomain + ":80) - Note: protocol must be specified");
        pProxy.setParameterOptions(true, 12, true);
        pProxyUserName = new StringProperty("_proxyuser");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, 13, true);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, 14, true);
        pProxyPassword.isPassword = true;
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(2);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        pMatchValue.setStateOptions(3);
        StringProperty astringproperty[] = { pWSDLurl, pMethodName, pServerURL, pMatchValue, pMatchString, pContentType, pUserAgent, pMethodNS, pActionURI, pSchema, pUseDotNetSOAP, pRoundTripTime, pNTLMDomain, pUserName, pPassword, pProxy,
                pProxyUserName, pProxyPassword, pStatus, pArgNames };
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("status != '200'\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors a Web Service by sending a SOAP Request to the Web Service.");
        setClassProperty(s, "help", "WebServiceMonitor.htm");
        setClassProperty(s, "title", "Web Service");
        setClassProperty(s, "target", "_serverurl");
        setClassProperty(s, "class", "WebServiceMonitor");
        setClassProperty(s, "toolName", "Web Service Monitor");
        setClassProperty(s, "toolDescription", "Test availabitliy of a SOAP Web Service.");
        setClassProperty(s, "loadable", "true");
        setClassProperty(s, "topazName", "Web Service");
        setClassProperty(s, "topazType", "Web Application Server");
    }
}
