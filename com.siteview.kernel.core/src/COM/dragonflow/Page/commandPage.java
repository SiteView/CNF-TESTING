/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.io.File;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class commandPage extends COM.dragonflow.Page.CGI
{

    public static String CRLF = "\r\n";

    public commandPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_command"))
        {
            throw new HTTPRequestException(557);
        }
        char c = '\310';
        String s = null;
        outputStream.println("<?xml version=\"1.0\"?>");
        outputStream.println("<SOAP-Envelope>");
        outputStream.println("<SOAP-Body>");
        try
        {
            String s1 = request.getValue("object");
            String s2 = request.getValue("method");
            String s3 = request.getValue("name");
            outputStream.println("<method>" + s2 + "</method>");
            if(s1.length() > 0)
            {
                outputStream.println("<object>" + s1 + "</object>");
            }
            if(s3.length() > 0)
            {
                outputStream.println("<name>" + s3 + "</name>");
            }
            if(s2.equals("help") || s2.length() == 0)
            {
                outputStream.println("<help>");
                outputStream.println("<object>user</object>");
                outputStream.println("<method>edit</method>");
                outputStream.println("<param>name</param>");
                outputStream.println("<param>_prop=value</param>");
                outputStream.println("<description>edit a SiteView user property</description>");
                outputStream.println("</help>");
                outputStream.println("<help>");
                outputStream.println(" <object>globals</object>");
                outputStream.println(" <method>edit</method>");
                outputStream.println("<param>_prop=value</param>");
                outputStream.println(" <description>edit a SiteView global property</description>");
                outputStream.println("</help>");
                throw new Exception("usage");
            }
            if(s1.equals("user"))
            {
                if(s2.equals("list"))
                {
                    Array array = COM.dragonflow.SiteView.User.readUsers();
                    for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); outputStream.println("</user>"))
                    {
                        HashMap hashmap3 = (HashMap)enumeration.nextElement();
                        Enumeration enumeration4 = (Enumeration) hashmap3.iterator();
                        String s10 = "";
                        outputStream.println("<user>");
                        while(enumeration4.hasMoreElements()) 
                        {
                            String s15 = (String)enumeration4.nextElement();
                            outputStream.println("<" + s15 + ">" + hashmap3.get(s15) + "</" + s15 + ">");
                            String s11 = ",";
                        }
                    }

                } else
                if(s2.equals("edit"))
                {
                    Array array1 = COM.dragonflow.SiteView.User.readUsers();
                    HashMap hashmap2 = COM.dragonflow.SiteView.User.findUser(array1, s3);
                    if(hashmap2 == null)
                    {
                        throw new Exception("unknown user: " + s3);
                    }
                    Enumeration enumeration3 = request.getVariables();
                    do
                    {
                        if(!enumeration3.hasMoreElements())
                        {
                            break;
                        }
                        String s8 = (String)enumeration3.nextElement();
                        if(s8.startsWith("_"))
                        {
                            String s12 = (String)hashmap2.get(s8);
                            String s16 = request.getValue(s8);
                            outputStream.println("<edit>");
                            outputStream.println(" <param>" + s8 + "</param>");
                            outputStream.println(" <old-value>" + s12 + "</old-value>");
                            outputStream.println(" <new-value>" + s16 + "</new-value>");
                            outputStream.println("</edit>");
                            hashmap2.put(s8, s16);
                        }
                    } while(true);
                    COM.dragonflow.SiteView.User.writeUsers(array1);
                } else
                {
                    throw new Exception("unknown method: " + s2 + ", object=user");
                }
            } else
            if(s1.equals("group"))
            {
                if(!s2.equals("add") && !s2.equals("edit"))
                {
                    throw new Exception("unknown method: " + s2 + ", object=group");
                }
                String s4 = request.getValue("group");
                Array array2;
                HashMap hashmap4;
                if(s2.equals("edit"))
                {
                    array2 = ReadGroupFrames(s4);
                    if(array2.size() > 0)
                    {
                        hashmap4 = (HashMap)array2.get(0);
                    } else
                    {
                        hashmap4 = new HashMap();
                        hashmap4.put("_nextID", "1");
                        array2.add(hashmap4);
                    }
                } else
                {
                    if((new File(COM.dragonflow.Page.commandPage.getGroupFilePath(s4, request, ".mg"))).exists())
                    {
                        throw new Exception("group exists: method=" + s2 + ", group=" + s4 + ", object=group");
                    }
                    array2 = new Array();
                    hashmap4 = new HashMap();
                    hashmap4.put("_nextID", "1");
                    array2.add(hashmap4);
                }
                outputStream.println("<" + s2 + ">");
                outputStream.println("<group>" + s4 + "</group>");
                Enumeration enumeration5 = request.getVariables();
                do
                {
                    if(!enumeration5.hasMoreElements())
                    {
                        break;
                    }
                    String s13 = (String)enumeration5.nextElement();
                    if(s13.startsWith("_"))
                    {
                        String s17 = (String)hashmap4.get(s13);
                        String s20 = request.getValue(s13);
                        outputStream.println("<edit>");
                        outputStream.println(" <param>" + s13 + "</param>");
                        outputStream.println(" <old-value>" + s17 + "</old-value>");
                        outputStream.println(" <new-value>" + s20 + "</new-value>");
                        outputStream.println("</edit>");
                        hashmap4.put(s13, s20);
                    }
                } while(true);
                outputStream.println("</" + s2 + ">");
                WriteGroupFrames(s4, array2);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s4);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
            } else
            if(s1.equals("monitor"))
            {
                if(!s2.equals("add") && !s2.equals("edit"))
                {
                    throw new Exception("unknown method: " + s2 + ", object=monitor");
                }
                String s5 = request.getValue("group");
                if(s5.length() == 0)
                {
                    throw new Exception("missing group parameter, method=" + s2 + ", object=monitor");
                }
                Array array3;
                HashMap hashmap5;
                if((new File(COM.dragonflow.Page.commandPage.getGroupFilePath(s5, request, ".mg"))).exists())
                {
                    array3 = ReadGroupFrames(s5);
                    if(array3.size() > 0)
                    {
                        hashmap5 = (HashMap)array3.get(0);
                    } else
                    {
                        hashmap5 = new HashMap();
                        hashmap5.put("_nextID", "1");
                        array3.add(hashmap5);
                    }
                } else
                {
                    array3 = new Array();
                    hashmap5 = new HashMap();
                    hashmap5.put("_nextID", "1");
                    array3.add(hashmap5);
                }
                int i = COM.dragonflow.Utils.TextUtils.toInt(request.getValue("ordering"));
                if(i <= 1)
                {
                    i = 1;
                }
                HashMap hashmap6;
                if(s2.equals("add"))
                {
                    String s18 = COM.dragonflow.Utils.TextUtils.getValue(hashmap5, "_nextID");
                    if(s18.length() == 0)
                    {
                        s18 = "1";
                    }
                    hashmap6 = new HashMap();
                    hashmap6.put("_id", s18);
                    if(request.getValue("_class").length() == 0)
                    {
                        throw new Exception("missing class parameter, method=" + s2 + ", object=monitor");
                    }
                    array3.insert(i, i, hashmap6);
                    String s21 = COM.dragonflow.Utils.TextUtils.increment(s18);
                    hashmap5.put("_nextID", s21);
                } else
                {
                    String s19 = request.getValue("id");
                    if(s19.length() == 0)
                    {
                        throw new Exception("missing id parameter, method=" + s2 + ", object=monitor");
                    }
                    int j = COM.dragonflow.Page.commandPage.findMonitorIndex(array3, s19);
                    hashmap6 = (HashMap)array3.get(j);
                    array3.remove(j);
                    array3.insert(i,i, hashmap6);
                }
                outputStream.println("<" + s2 + ">");
                outputStream.println("<group>" + s5 + "</group>");
                Enumeration enumeration6 = request.getVariables();
                do
                {
                    if(!enumeration6.hasMoreElements())
                    {
                        break;
                    }
                    String s22 = (String)enumeration6.nextElement();
                    if(s22.startsWith("_"))
                    {
                        String s23 = (String)hashmap6.get(s22);
                        String s24 = request.getValue(s22);
                        outputStream.println("<edit>");
                        outputStream.println(" <param>" + s22 + "</param>");
                        outputStream.println(" <old-value>" + s23 + "</old-value>");
                        outputStream.println(" <new-value>" + s24 + "</new-value>");
                        outputStream.println("</edit>");
                        hashmap6.put(s22, s24);
                    }
                } while(true);
                outputStream.println("</" + s2 + ">");
                WriteGroupFrames(s5, array3);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s5);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
            } else
            if(s1.equals("globals"))
            {
                if(s2.equals("list"))
                {
                    HashMap hashmap = getMasterConfig();
                    outputStream.println("<globals>");
                    String s6;
                    for(Enumeration enumeration1 = (Enumeration) hashmap.keys(); enumeration1.hasMoreElements(); outputStream.println("<" + s6 + ">" + hashmap.get(s6) + "</" + s6 + ">"))
                    {
                        s6 = (String)enumeration1.nextElement();
                    }

                    outputStream.println("</globals>");
                } else
                if(s2.equals("edit"))
                {
                    HashMap hashmap1 = getMasterConfig();
                    Enumeration enumeration2 = request.getVariables();
                    do
                    {
                        if(!enumeration2.hasMoreElements())
                        {
                            break;
                        }
                        String s7 = (String)enumeration2.nextElement();
                        if(s7.startsWith("_"))
                        {
                            String s9 = COM.dragonflow.Page.commandPage.getValue(hashmap1, s7);
                            String s14 = request.getValue(s7);
                            outputStream.println("<edit>");
                            outputStream.println(" <param>" + s7 + "</param>");
                            outputStream.println(" <old-value>" + s9 + "</old-value>");
                            outputStream.println(" <new-value>" + s14 + "</new-value>");
                            outputStream.println("</edit>");
                            hashmap1.put(s7, s14);
                        }
                    } while(true);
                    saveMasterConfig(hashmap1);
                } else
                {
                    throw new Exception("unknown method: " + s2 + ", object=globals");
                }
            } else
            {
                throw new Exception("unknown object: " + s1);
            }
        }
        catch(java.lang.Exception exception)
        {
            s = exception.getMessage();
            c = '\u01F4';
        }
        finally
        {
            if(c != '\310')
            {
                outputStream.println("<SOAP-Fault>");
                outputStream.println(" <faultcode>SOAP-ENV:Server</faultcode>");
                outputStream.println(" <faultstring>Server Error</faultstring>");
                outputStream.println(" <detail>");
                outputStream.println(" <siteview-fault>");
                outputStream.println("  <siteview-error>" + (int)c + "</siteview-error>");
                outputStream.println("  <siteview-errormessage>" + s + "</siteview-errormessage>");
                outputStream.println(" </siteview-fault>");
                outputStream.println(" </detail>");
                outputStream.println("</SOAP-Fault>");
            }
            outputStream.println("</SOAP-Body>");
            outputStream.println("</SOAP-Envelope>");
        }
    }

    public void printCGIHeader()
    {
        COM.dragonflow.HTTP.HTTPRequest _tmp = request;
        COM.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", "text/xml");
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.commandPage commandpage = new commandPage();
        if(args.length > 0)
        {
            commandpage.args = args;
        }
        commandpage.handleRequest();
    }

}
