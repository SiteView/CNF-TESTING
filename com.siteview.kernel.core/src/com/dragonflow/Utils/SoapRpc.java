 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package com.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import COM.datachannel.xml.om.Document;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// RawXmlWriter, XSLUtils, TextUtils

public class SoapRpc
{

    String methodName;
    static final int REQUESTRESPONSE = 2;
    static final int ONEWAY = 1;
    int transmission;
    public static boolean debug = false;
    static final String types[] = {
        "String", "Integer", "Boolean", "Double", "Date", "Base64", "Struct", "Array", "Nil"
    };
    public static final String WSDL_XMLNS = "http://schemas.xmlsoap.org/wsdl/";
    public static final String SOAPENV_XMLNS = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAPENC_XMLNS = "http://schemas.xmlsoap.org/soap/encoding/";
    private static final String SOAP_CONTENT_TYPE = "text/xml; charset=\"utf-8\"";
    public static String DEF_EL = "definitions";
    public static String SERVICE_EL = "service";
    public static String SOAPADDRS_EL = "soap:address";
    public static String SOAPOPER_EL = "soap:operation";
    public static String OPERATION_EL = "operation";
    public static String BINDING_EL = "binding";
    public static String PART_EL = "part";
    public static String MESSAGE_EL = "message";
    public static String PORT_EL = "port";
    public static String PORTTYPE_EL = "portType";
    public static String INPUT_EL = "input";
    public static String OUTPUT_EL = "output";
    public static String NAME_ATTR = "name";
    public static String ROOTNS_ATTR = "targetNamespace";
    public static String SOAPACTION_ATTR = "soapAction";
    public static String LOCATION_ATTR = "location";
    public static String WSDLNS_ATTR = "xmlns";
    public static String METHODNS_ATTR = "namespace";
    public static String PARAMETERORDER_ATTR = "parameterOrder";
    public static String MESSAGE_ATTR = "message";
    public static String TYPE_ATTR = "type";
    public static String SOAPBODY_EL = "SOAP-ENV:Body";
    public static String SOAPENV_EL = "SOAP-ENV:Envelope";
    public static String SOAPFAULT_EL = "SOAP-ENV:Fault";
    StringBuffer xmlContent;
    public static final int exec_status = 0;
    public static final int exec_response = 1;
    public static final int exec_duration = 2;
    public static final int exec_fault = 3;
    private final int iRequest = 0;
    private final int iResponseHeaders = 1;
    private final int iResponseBody = 2;
    private final int ntlmResponseSize = 3;

    public SoapRpc()
    {
        transmission = 0;
        xmlContent = new StringBuffer();
    }

    public static String stripEncoding(String s)
    {
        String s1 = s.substring(s.indexOf("?>"));
        return "<?xml version=\"1.0\"  " + s1;
    }

    private String getFaultString(org.w3c.dom.Node node)
    {
        String s = "";
        Array array = new Array();
        try
        {
            com.dragonflow.Utils.XSLUtils.findElements(node, SOAPENV_EL + "." + SOAPBODY_EL + "." + SOAPFAULT_EL, array);
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                org.w3c.dom.Node node1 = (org.w3c.dom.Node)enumeration.nextElement();
                if(node1.getNodeName().equals(SOAPFAULT_EL))
                {
                    org.w3c.dom.NodeList nodelist = node1.getChildNodes();
                    int i = 0;
                    while(i < nodelist.getLength()) 
                    {
                        s = s + nodelist.item(i).getNodeName();
                        s = s + ": " + nodelist.item(i).getFirstChild().getNodeValue() + " ";
                        i++;
                    }
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
        return s;
    }

    private String getAttrValue(org.w3c.dom.Node node, String s, String s1, StringBuffer stringbuffer)
    {
        Array array = new Array();
        String s2 = null;
        try
        {
            if(s.length() > 0)
            {
                com.dragonflow.Utils.XSLUtils.findElements(node, s, array);
                Enumeration enumeration = (Enumeration) array.iterator();
                org.w3c.dom.Node node1 = (org.w3c.dom.Node)enumeration.nextElement();
                s2 = ((COM.datachannel.xml.om.Element)node1).getAttribute(s1);
            } else
            {
                s2 = ((COM.datachannel.xml.om.Element)node).getAttribute(s1);
            }
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            stringbuffer.append(" Error getting the attribute. Check wsdl file ");
            stringbuffer.append(exception.toString() + " " + s);
        }
        return s2;
    }

    public java.lang.Object[] execute(String s, java.util.Vector vector, String s1, String as[], String s2, String s3, String s4, 
            String s5, String s6, String s7, String s8, String s9, String s10, String s11, 
            String s12, StringBuffer stringbuffer, String s13, boolean flag)
        throws java.io.IOException
    {
        StringBuffer stringbuffer1 = new StringBuffer();
        String s14 = "";
        String s16 = "\"" + s5 + "\"";
        String s15;
        long al[];
        try
        {
            StringBuffer stringbuffer2 = new StringBuffer();
            com.dragonflow.Utils.RawXmlWriter rawxmlwriter = new RawXmlWriter(stringbuffer2);
            rawxmlwriter.writeSOAPHeader(new Array());
            writeRequest(rawxmlwriter, s, vector, as, s4, flag);
            com.dragonflow.Utils.SoapRpc.status("SOAP req: " + stringbuffer2.toString());
            String as1[] = new String[3];
            al = httpPost(s7, s8, s9, s10, s11, s12, s13, s16, s6, stringbuffer2.toString(), as1);
            stringbuffer1.append(as1[2]);
            stringbuffer.append("(REQUEST)\n\n" + com.dragonflow.Utils.TextUtils.encodeArgs(as1[0]) + "\n\n");
            stringbuffer.append("(RESPONSE)\n\n" + as1[1] + "\n" + com.dragonflow.Utils.TextUtils.encodeArgs(as1[2]));
            s15 = stringbuffer1.toString();
            com.dragonflow.Utils.SoapRpc.status("SOAP resp: " + s15);
            boolean flag1 = true;
            if(al[0] < 200L || al[0] > 299L || al[0] == 204L)
            {
                flag1 = false;
            } else
            if(s15.indexOf("<?xml version=") < 0)
            {
                flag1 = false;
            }
            if(flag1)
            {
                try
                {
                    COM.datachannel.xml.om.Document document = new Document();
                    document.loadXML(com.dragonflow.Utils.SoapRpc.stripEncoding(s15));
                    s14 = getFaultString(document.getDocumentElement());
                }
                catch(java.lang.Exception exception1)
                {
                    exception1.printStackTrace();
                    al[0] = com.dragonflow.StandardMonitor.URLMonitor.kXMLFormatError;
                    com.dragonflow.Log.LogManager.log("Error", "SoapRpc.execute: " + exception1.getMessage());
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            throw new IOException(exception.getMessage());
        }
        java.lang.Object aobj[] = new java.lang.Object[4];
        aobj[0] = new Long(al[0]);
        aobj[1] = s15;
        aobj[2] = new Long(al[1]);
        aobj[3] = s14;
        return aobj;
    }

    /**
     * 
     * 
     * @param s
     * @param stringbuffer
     * @param stringbuffer1
     * @return
     */
    public static int getWSDLContent(String s, StringBuffer stringbuffer, StringBuffer stringbuffer1)
    {
        org.apache.commons.httpclient.HttpClient httpclient;
        org.apache.commons.httpclient.methods.GetMethod getmethod;
        char c = '\310';
        httpclient = new HttpClient();
        getmethod = new GetMethod(s);
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_URLUserAgent");
        
        int i;
        try {
        if(s1.length() > 0)
        {
            getmethod.setRequestHeader("User-Agent", s1);
        }
        i = httpclient.executeMethod(getmethod);
        com.dragonflow.Utils.SoapRpc.status("HTTP Status: " + i);
        stringbuffer.append(getmethod.getResponseBodyAsString());
        getmethod.releaseConnection();
        }
        catch (RuntimeException e) {
            getmethod.releaseConnection();
            throw e;
        }
        catch (Exception exception) {
        i = 500;
        stringbuffer1.append(exception.toString());
        com.dragonflow.Log.LogManager.log("Error", "SoapRpc.getWSDLContent: " + exception.getMessage());
        exception.printStackTrace();
        getmethod.releaseConnection();
        }
        return i;
    }

    private void writeRequest(com.dragonflow.Utils.RawXmlWriter rawxmlwriter, String s, java.util.Vector vector, String as[], String s1, boolean flag)
    {
        String s2 = "ns1:" + s;
        try
        {
            rawxmlwriter.startElement(SOAPBODY_EL);
            if(s1.length() > 0)
            {
                if(flag)
                {
                    rawxmlwriter.startElement(s + " xmlns=\"" + s1 + "\"");
                } else
                {
                    rawxmlwriter.startElement(s2 + " xmlns:ns1=\"" + s1 + "\"");
                }
            } else
            {
                rawxmlwriter.startElement(s);
            }
            int i = vector.size();
            for(int j = 0; j < i; j++)
            {
                String s5 = ((String)vector.elementAt(j)).trim();
                int k = as[j].indexOf('(');
                if(k > 0)
                {
                    String s3 = as[j].substring(0, k).trim();
                    int l = as[j].indexOf(')');
                    String s4 = as[j].substring(k + 1, l);
                    if(s5.length() <= 0)
                    {
                        rawxmlwriter.emptyElement(s3);
                    } else
                    {
                        rawxmlwriter.startElement(s3 + " xsi:type=\"xsd:" + s4 + "\"");
                        rawxmlwriter.write(s5);
                        rawxmlwriter.endElement(s3);
                    }
                } else
                {
                    rawxmlwriter.write(s5);
                }
            }

            if(s1.length() > 0 && !flag)
            {
                rawxmlwriter.endElement(s2);
            } else
            {
                rawxmlwriter.endElement(s);
            }
            rawxmlwriter.endElement(SOAPBODY_EL);
            rawxmlwriter.endElement(SOAPENV_EL);
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            com.dragonflow.Log.LogManager.log("Error", "SoapRpc.writeRequest exception: " + exception.getMessage());
        }
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     * @param s6
     * @param s7
     * @param s8
     * @param s9
     * @param as
     * @return
     * @throws java.lang.Exception
     */
    public long[] httpPost(String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            String s7, String s8, String s9, String as[])
        throws java.lang.Exception
    {
        long al[];
        org.apache.commons.httpclient.HttpClient httpclient;
        org.apache.commons.httpclient.methods.PostMethod postmethod;
        al = new long[2];
        com.dragonflow.Utils.SoapRpc.status("Doing Web Service HTTP Post auth with credentials: " + s1 + "/" + s2 + "/" + s6);
        if(s6 == null)
        {
            s6 = "";
        }
        httpclient = new HttpClient();
        httpclient.getState().setAuthenticationPreemptive(true);
        if(s1 == null || s1.length() == 0)
        {
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_defaultAuthUsername");
            if(s1.length() > 0)
            {
                if(s1.indexOf("\\") != -1)
                {
                    String as1[] = (new String(s1)).split("\\\\");
                    if(as1.length == 2)
                    {
                        s6 = as1[0];
                        s1 = as1[1];
                    }
                }
                s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_defaultAuthPassword");
                httpclient.getState().setAuthenticationPreemptive(false);
            }
        }
        if(s1 != null && s1.length() > 0)
        {
            String s10;
            try
            {
                java.net.InetAddress inetaddress = java.net.InetAddress.getLocalHost();
                s10 = inetaddress.getHostName();
            }
            catch(java.net.UnknownHostException unknownhostexception)
            {
                s10 = "";
                com.dragonflow.Log.LogManager.log("Error", "SoapRPC.httpPost is unable to get local host");
                unknownhostexception.printStackTrace();
            }
            org.apache.commons.httpclient.NTCredentials ntcredentials = new NTCredentials(s1, s2, s10, s6);
            httpclient.getState().setCredentials(null, null, ntcredentials);
            com.dragonflow.Utils.SoapRpc.status("Username found and credentials being set");
        }
        java.net.URL url = null;
        if(s3.length() > 0)
        {
            try
            {
                url = new URL(s3);
            }
            catch(java.lang.Exception exception)
            {
                com.dragonflow.Log.LogManager.log("Error", "Invalid proxy server specified: " + s3);
            }
        }
        if(url != null)
        {
            int i = url.getPort();
            org.apache.commons.httpclient.HostConfiguration hostconfiguration = httpclient.getHostConfiguration();
            hostconfiguration.setProxy(url.getHost(), i >= 0 ? i : 80);
            org.apache.commons.httpclient.UsernamePasswordCredentials usernamepasswordcredentials = new UsernamePasswordCredentials(s4, s5);
            httpclient.getState().setProxyCredentials(null, null, usernamepasswordcredentials);
            com.dragonflow.Utils.SoapRpc.status("Proxying at: " + url.toString() + " / " + s4 + " / " + s5);
        }
        postmethod = new PostMethod(s);
        postmethod.setDoAuthentication(true);
        postmethod.setRequestHeader("Content-type", "text/xml; charset=\"utf-8\"");
        postmethod.setRequestHeader("SOAPAction", s7);
        if(s8.length() > 0)
        {
            postmethod.setRequestHeader("User-Agent", s8);
        }
        postmethod.setRequestBody(s9);
        
        try {
        long l = java.lang.System.currentTimeMillis();
        int j = httpclient.executeMethod(postmethod);
        long l1 = java.lang.System.currentTimeMillis();
        as[0] = getRequestString(postmethod);
        as[1] = getResponseHeaders(postmethod);
        as[2] = postmethod.getResponseBodyAsString();
        al[0] = j;
        al[1] = l1 - l;
        postmethod.releaseConnection();
        }
        catch (RuntimeException exception1) {
        postmethod.releaseConnection();
        throw exception1;
        }
        com.dragonflow.Utils.SoapRpc.status("SOAP Request: " + as[0]);
        com.dragonflow.Utils.SoapRpc.status("SOAP Response: " + as[2]);
        return al;
    }

    private String getRequestString(org.apache.commons.httpclient.methods.PostMethod postmethod)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s = "HTTP/" + (postmethod.isHttp11() ? "1.1" : "1.0");
        stringbuffer.append(postmethod.getName() + " " + postmethod.getPath() + " " + s + "\n");
        org.apache.commons.httpclient.Header aheader[] = postmethod.getRequestHeaders();
        for(int i = 0; i < aheader.length; i++)
        {
            stringbuffer.append(aheader[i].toString());
        }

        stringbuffer.append("\n");
        try
        {
            stringbuffer.append(postmethod.getRequestBodyAsString());
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            com.dragonflow.Log.LogManager.log("Error", exception.toString());
        }
        return stringbuffer.toString();
    }

    private String getResponseHeaders(org.apache.commons.httpclient.methods.PostMethod postmethod)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(postmethod.getStatusLine().toString() + "\n");
        org.apache.commons.httpclient.Header aheader[] = postmethod.getResponseHeaders();
        for(int i = 0; i < aheader.length; i++)
        {
            stringbuffer.append(aheader[i].toString());
        }

        return stringbuffer.toString();
    }

    private static void status(String s)
    {
        if(debug)
        {
            java.lang.System.out.println(s);
        }
    }

    static 
    {
        String s = java.lang.System.getProperty("SoapRpc.debug", "false");
        if(s.equalsIgnoreCase("true"))
        {
            debug = true;
        }
    }
}
