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

import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.recursionsw.jgl.Array;
import COM.datachannel.xml.om.Document;

// Referenced classes of package com.dragonflow.Utils:
// SoapRpc, TextUtils, FileUtils

public class XSLUtils
{

    public static final boolean SHOW_FOOTER = true;
    public static final boolean NO_FOOTER = false;

    public XSLUtils()
    {
    }

    public static int matchXML(String s, String s1, Array array, StringBuffer stringbuffer)
    {
        int i = com.dragonflow.SiteView.Monitor.kURLContentMatchError;
        try
        {
            Array array1 = com.dragonflow.SiteView.Platform.split(',', s1);
            COM.datachannel.xml.om.Document document = new Document();
            int j = 0;
            String s2 = (String)array1.get(j);
            String s4 = "<?xml";
            String s5 = null;
            String s6 = "xml.endtag=";
            if(s2.startsWith("xml.endtag="))
            {
                j++;
                s5 = s2.substring(s6.length());
            }
            int k = s.indexOf(s4);
            if(k == -1)
            {
                i = com.dragonflow.SiteView.Monitor.kXMLFormatError;
            } else
            {
                s = s.substring(k);
            }
            if(s5 != null)
            {
                int l = s.indexOf(s5);
                if(l == -1)
                {
                    i = com.dragonflow.SiteView.Monitor.kXMLFormatError;
                } else
                {
                    s = s.substring(0, l + s5.length());
                }
            }
            if(i != com.dragonflow.SiteView.Monitor.kXMLFormatError)
            {
                s = com.dragonflow.Utils.SoapRpc.stripEncoding(s);
                document.loadXML(s);
                if(s1.length() != 0)
                {
                    do
                    {
                        if(j >= array1.size())
                        {
                            break;
                        }
                        String s3 = (String)array1.get(j++);
                        s3 = s3.trim();
                        s3 = s3.substring(4);
                        StringBuffer stringbuffer1 = new StringBuffer();
                        i = com.dragonflow.Utils.XSLUtils.findMatches(document, s3, stringbuffer1, stringbuffer);
                        array.add(stringbuffer1.toString());
                    } while(i == com.dragonflow.SiteView.Monitor.kURLok);
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            com.dragonflow.Log.LogManager.log("RunMonitor", "XML format error, " + exception);
            stringbuffer.append(exception.getMessage());
            i = com.dragonflow.SiteView.Monitor.kXMLFormatError;
        }
        return i;
    }

    public static void findElements(org.w3c.dom.Node node, String s, Array array)
    {
        String s1 = s;
        String s2 = "";
        int i = s.indexOf('.');
        if(i != -1)
        {
            s1 = s.substring(0, i);
            s2 = s.substring(i + 1);
        }
        String s3 = node.getNodeName();
        if(s3 != null)
        {
            if(!s1.equals(s3.toString()))
            {
                return;
            }
            if(s2.length() == 0)
            {
                array.add(node);
            } else
            {
                org.w3c.dom.NodeList nodelist = node.getChildNodes();
                for(int j = 0; j < nodelist.getLength(); j++)
                {
                    org.w3c.dom.Node node1 = nodelist.item(j);
                    com.dragonflow.Utils.XSLUtils.findElements(node1, s2, array);
                }

            }
        }
    }

    static int findMatches(COM.datachannel.xml.om.Document document, String s, StringBuffer stringbuffer, StringBuffer stringbuffer1)
    {
        s = s.trim();
        int i = com.dragonflow.SiteView.Monitor.kURLok;
        String s1 = s;
        String s2 = null;
        int j = s1.indexOf('=');
        if(j != -1)
        {
            s2 = s1.substring(j + 1);
            s1 = s1.substring(0, j);
        }
        int k = s1.indexOf(".[");
        String s3 = null;
        if(k != -1)
        {
            s3 = s1.substring(k + 2, s1.length() - 1);
            s1 = s1.substring(0, k);
        }
        int l = s1.lastIndexOf("]");
        int i1 = 0;
        String s4 = "";
        String s6 = "";
        if(l != -1)
        {
            String s8 = s1.substring(0, l);
            int j1 = s8.indexOf("[");
            if(j1 < l)
            {
                String s9 = s1.substring(j1 + 1, l);
                int l1 = s9.indexOf("->");
                if(l1 >= 0)
                {
                    String s10 = s8.substring(0, j1);
                    Array array1 = new Array();
                    String s5 = s9.substring(0, l1);
                    String s7 = s9.substring(l1 + 2);
                    com.dragonflow.Utils.XSLUtils.findElements(document.getDocumentElement(), s10, array1);
                    Enumeration enumeration1 =  (Enumeration) array1.iterator();
                    i1 = 1;
//                    while (enumeration.hasMoreElements()) {
//                        COM.datachannel.xml.om.Element element1 = (COM.datachannel.xml.om.Element)enumeration1.nextElement();
//                        if(element1.getAttribute(s5).equals(s7))
//                        {
//                            break;
//                        }
//                        i1++;
//                    } 
                } else
                {
                    i1 = com.dragonflow.Utils.TextUtils.toInt(s9);
                }
                s8 = s8.substring(0, j1);
                String s11 = s1.substring(l + 1);
                s1 = s8 + s11;
            }
        }
        Array array = new Array();
        com.dragonflow.Utils.XSLUtils.findElements(document.getDocumentElement(), s1, array);
        Enumeration enumeration = (Enumeration) array.iterator();
        int k1 = 0;
        int i2 = 1;
        while (enumeration.hasMoreElements()) {
            String s12 = "";
            COM.datachannel.xml.om.Element element = (COM.datachannel.xml.om.Element)enumeration.nextElement();
            if(i1 > 0)
            {
                if(i2++ != i1)
                {
                    continue;
                }
                s12 = s3 != null ? element.getAttribute(s3) : element.getText();
            } else
            {
                s12 = s3 != null ? element.getAttribute(s3) : element.getText();
            }
            if(stringbuffer.length() != 0)
            {
                stringbuffer.append(", ");
            }
            stringbuffer.append(s12);
            if(s2 != null && s2.equals(s12))
            {
                k1++;
            }
        } 
        
        String s13 = "";
        if(array.size() == 0)
        {
            s13 = "missing " + s;
            i = com.dragonflow.SiteView.Monitor.kXMLElementNotFoundError;
        } else
        if(s2 != null)
        {
            if(k1 == 0)
            {
                i = com.dragonflow.SiteView.Monitor.kXMLElementMatchError;
                s13 = "expected " + s + ", found " + stringbuffer;
            }
        } else
        {
            s13 = "matched " + stringbuffer;
        }
        if(stringbuffer1.length() != 0)
        {
            stringbuffer1.append(", ");
        }
        stringbuffer1.append(s13);
        return i;
    }

    public static void convert(String s, String s1, String s2)
        throws java.io.IOException
    {
        String s3 = com.dragonflow.Utils.FileUtils.readFile(s).toString();
        String s4 = com.dragonflow.Utils.FileUtils.readFile(s1).toString();
        java.io.FileOutputStream fileoutputstream = new FileOutputStream(s2);
        java.io.PrintWriter printwriter = com.dragonflow.Utils.FileUtils.MakeOutputWriter(fileoutputstream);
        com.dragonflow.Utils.XSLUtils.convert(s3, s4, printwriter, null, false);
        printwriter.close();
        fileoutputstream.close();
    }

    public static void convert(String s, String s1, java.io.PrintWriter printwriter)
    {
        com.dragonflow.Utils.XSLUtils.convert(s, s1, printwriter, null, false);
    }

    private static String addLineNumbers(String s)
    {
        String as[] = com.dragonflow.Utils.TextUtils.split(s, "\n");
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            com.dragonflow.Utils.TextUtils.appendStringRightJustify(stringbuffer, "" + (i + 1), 5);
            stringbuffer.append("  ");
            stringbuffer.append(as[i]);
            stringbuffer.append("\n");
        }

        return stringbuffer.toString();
    }

    private static String convertDataChannel(String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        COM.datachannel.xml.om.Document document = new Document();
        try
        {
            document = new Document();
            document.loadXML(s);
        }
        catch(java.lang.Exception exception)
        {
            stringbuffer.append("Error Reading XML");
            stringbuffer1.append(exception.getMessage());
            stringbuffer2.append(com.dragonflow.Utils.XSLUtils.addLineNumbers(s));
        }
        COM.datachannel.xml.om.Document document1 = new Document();
        try
        {
            document1 = new Document();
            document1.loadXML(s1);
        }
        catch(java.lang.Exception exception1)
        {
            stringbuffer.append("Error Reading XSL Template");
            stringbuffer1.append(exception1.getMessage());
            stringbuffer2.append(com.dragonflow.Utils.XSLUtils.addLineNumbers(s));
        }
        String s2 = "";
        if(stringbuffer1.length() == 0)
        {
            try
            {
                s2 = document.transformNode((COM.datachannel.xml.om.IXMLDOMNode)document1.getDocumentElement());
            }
            catch(java.lang.Exception exception2)
            {
                stringbuffer.append("Error Reading transforming XML");
                stringbuffer1.append(exception2.getMessage());
                if(stringbuffer1.length() == 0)
                {
                    stringbuffer1.append("" + exception2);
                }
            }
        }
        return s2;
    }

    private static String convertSun(String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1, StringBuffer stringbuffer2)
    {
        javax.xml.transform.Transformer transformer = null;
        try
        {
            javax.xml.transform.TransformerFactory transformerfactory = javax.xml.transform.TransformerFactory.newInstance();
            transformer = transformerfactory.newTransformer(new StreamSource(new StringReader(s1)));
        }
        catch(java.lang.Exception exception)
        {
            stringbuffer.append("Error Reading XSL Template");
            stringbuffer1.append(exception.getMessage());
            stringbuffer2.append(com.dragonflow.Utils.XSLUtils.addLineNumbers(s));
        }
        String s2 = "";
        if(stringbuffer1.length() == 0)
        {
            try
            {
                java.io.StringWriter stringwriter = new StringWriter();
                transformer.transform(new StreamSource(new StringReader(s)), new StreamResult(stringwriter));
                s2 = stringwriter.toString();
            }
            catch(java.lang.Exception exception1)
            {
                stringbuffer.append("Error Reading transforming XML");
                stringbuffer1.append(exception1.getMessage());
                if(stringbuffer1.length() == 0)
                {
                    stringbuffer1.append("" + exception1);
                }
            }
        }
        return s2;
    }

    public static String getAttrValue(org.w3c.dom.Node node, String s, String s1, StringBuffer stringbuffer)
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

    public static void convert(String s, String s1, java.io.PrintWriter printwriter, com.dragonflow.HTTP.HTTPRequest httprequest, boolean flag)
    {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        String s2;
        if(s1.indexOf("xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"") != -1)
        {
            s2 = com.dragonflow.Utils.XSLUtils.convertSun(s, s1, stringbuffer, stringbuffer1, stringbuffer2);
        } else
        {
            s2 = com.dragonflow.Utils.XSLUtils.convertDataChannel(s, s1, stringbuffer, stringbuffer1, stringbuffer2);
        }
        String s3 = stringbuffer.toString();
        String s4 = stringbuffer1.toString();
        String s5 = stringbuffer2.toString();
        if(s4.length() > 0)
        {
            s2 = "<HTML><HEAD>" + com.dragonflow.Page.CGI.nocacheHeader + com.dragonflow.SiteView.Platform.charSetTag + "<TITLE>Error Generating Document</TITLE></HEAD>\n" + "<BODY BGCOLOR=#FFFFFF><H2>" + s3 + "</H2><P>\n" + s4 + "<HR>\n" + "<PRE>\n" + com.dragonflow.Utils.TextUtils.escapeHTML(s5) + "</PRE>\n" + "</BODY></HTML>\n";
        }
        String s6 = "";
        if(flag)
        {
            int i = s2.lastIndexOf("</BODY>");
            if(i == -1)
            {
                i = s2.lastIndexOf("</body>");
            }
            if(i >= 0)
            {
                s2 = s2.substring(0, i);
                s6 = s2.substring(i);
            }
        }
        if(httprequest != null)
        {
            s2 = com.dragonflow.Utils.TextUtils.replaceString(s2, "account=administrator", "account=" + httprequest.getAccount());
            s2 = com.dragonflow.Utils.TextUtils.replaceString(s2, "name=\"account\" value=\"administrator\"", "name=\"account\" value=\"" + httprequest.getAccount() + "\"");
        }
        printwriter.print(s2);
        if(flag)
        {
            com.dragonflow.Page.CGI.printFooter(printwriter, httprequest);
        }
        printwriter.print(s6);
        printwriter.flush();
    }

    public static void main(String args[])
    {
        if(args.length == 1)
        {
            COM.datachannel.xml.om.Document document = new Document();
            try
            {
                String s = com.dragonflow.Utils.FileUtils.readFile(args[0]).toString();
                COM.datachannel.xml.om.Document document1 = new Document();
                document1.loadXML(s);
            }
            catch(java.lang.Exception exception1)
            {
                java.lang.System.out.println("Exception: " + exception1.getMessage());
            }
        } else
        if(args.length != 3)
        {
            java.lang.System.out.println("Usage: XSLUtils xmlFile xslURL outputURL");
            java.lang.System.exit(0);
        } else
        {
            try
            {
                com.dragonflow.Utils.XSLUtils.convert(args[0], args[1], args[2]);
            }
            catch(java.lang.Exception exception)
            {
                java.lang.System.out.println("Exception: " + exception);
                exception.printStackTrace();
            }
        }
    }
}
