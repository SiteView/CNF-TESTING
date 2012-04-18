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
import java.io.RandomAccessFile;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class demoAppPage extends COM.dragonflow.Page.CGI
{

    public static HashMap shoppingBasket = new HashMap();

    public demoAppPage()
    {
    }

    public void integratePages(String s, String s1)
    {
        COM.dragonflow.Utils.TextUtils.debugPrint("TEMPLATENAME=" + s);
        COM.dragonflow.Utils.TextUtils.debugPrint("PARAMS=" + s1);
        StringBuffer stringbuffer = new StringBuffer();
        HashMap hashmap = getMasterConfig();
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_appServer");
        String s3 = request.getValue("session");
        if(s3.length() == 0)
        {
            long l = 0x186a0L + (long)(java.lang.Math.random() * 100000D);
            s3 = "" + l;
        }
        String s4 = s2 + "/SiteView/cgi/go.exe/SiteView?page=demoApp&session=" + s3 + "&" + s1;
        COM.dragonflow.Utils.TextUtils.debugPrint("URL=" + s4);
        long al[] = COM.dragonflow.StandardMonitor.URLMonitor.sendHTTPRequest(s4, "", "", "", "", "", null, stringbuffer, 0x186a0L, "", 60000);
        String s5 = "";
        if(al[0] != (long)COM.dragonflow.StandardMonitor.URLMonitor.kURLok)
        {
            s5 = "Error accessing application server " + al[0];
        } else
        {
            s5 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
        }
        outputStream.println("<HTML><HEAD><TITLE>Computer Overstocks</TITLE></HEAD><BODY bgcolor=#ffffff><table border=0 width=100%><tr><td height=185 width=17%><img src=/SiteView/htdocs/sidepaneling.jpg width=138 height=416></td><td height=185 width=83% valign=top><table border=0 width=100%><tr><td width=21%><img src=/SiteView/htdocs/logonotype.gif width=78 height=104></td><td width=79% valign=middle><h1>Fresh Overstocks</h1></td></tr></table><p>&nbsp;</p><p><H2>" + s + "</H2>" + "<P>" + s5 + "</p>" + "</td>" + "</tr>" + "</table>" + "</BODY>" + "</HTML>");
    }

    public void printCatalogContents()
        throws java.lang.Exception
    {
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/demoCatalog.config");
        String s = "/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=item&session=" + request.getValue("session") + "&item=";
        outputStream.println("<TABLE BORDER=1 cellspacing=0>");
        for(int i = 0; i < array.size(); i++)
        {
            HashMap hashmap = (HashMap)array.get(i);
            outputStream.println("<TR><TD><A HREF=" + s + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "id") + "><B>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name") + "</B></A></TD>" + "<TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "description") + "</TD>" + "<TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "price") + "</TD></TR>");
        }

        outputStream.println("</TABLE>");
    }

    public void printCatalogPage()
    {
        integratePages("Catalog of Items", "operation=catalogContents");
    }

    public void printItemContents()
        throws java.lang.Exception
    {
        String s = request.getValue("item");
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/demoCatalog.config");
        String s1 = request.getValue("session");
        String s2 = "/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=shoppingBasket&session=" + s1 + "&add=";
        HashMap hashmap = null;
        int i = 0;
        do
        {
            if(i >= array.size())
            {
                break;
            }
            HashMap hashmap1 = (HashMap)array.get(i);
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "id").equals(s))
            {
                hashmap = hashmap1;
                break;
            }
            i++;
        } while(true);
        if(hashmap == null)
        {
            outputStream.println("<TABLE><TR><TD>Item not found in DB: " + s);
        } else
        {
            outputStream.println("<H2>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "description") + "</H2>");
            outputStream.println("<P>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "longDescription"));
            outputStream.println("<P><B>In Stock : </B>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "inStock"));
            outputStream.println("<P><A HREF=" + s2 + s + ">Add to Basket</A>");
            outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=catalog&session=" + request.getValue("session") + ">Return to Catalog</A>");
        }
    }

    public void printItemPage()
    {
        integratePages("Item Description", "operation=itemContents&item=" + request.getValue("item"));
    }

    public void printShoppingBasketPage()
    {
        integratePages("Shopping Basket", "operation=shoppingBasketContents&add=" + request.getValue("add"));
    }

    public void printShoppingBasketContents()
        throws java.lang.Exception
    {
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/demoCatalog.config");
        String s = request.getValue("session");
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=item&session=" + s + "&item=";
        HashMap hashmap = (HashMap)shoppingBasket.get(s);
        if(hashmap == null)
        {
            hashmap = new HashMap();
            shoppingBasket.add(s, hashmap);
        }
        String s2 = request.getValue("add");
        if(s2.length() > 0)
        {
            HashMap hashmap1 = findFrame(array, "id", s2);
            if(hashmap1 != null)
            {
                hashmap.add(s2, hashmap1);
            }
        }
        Enumeration enumeration = (Enumeration) hashmap.keys();
        if(!enumeration.hasMoreElements())
        {
            outputStream.println("<H3>No items in in-basket</H3>,");
        } else
        {
            outputStream.println("<TABLE><TR><TH>Item</TH><TH>Description</TH></TR>");
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s3 = (String)enumeration.nextElement();
                HashMap hashmap2 = findFrame(array, "id", s3);
                if(hashmap2 != null)
                {
                    outputStream.println("<TR><TD><A HREF=" + s1 + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "id") + ">" + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "name") + "</A>" + "</TD><TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "description") + "</TD></TR>");
                }
            } while(true);
            outputStream.println("</TABLE>");
            outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=checkout&session=" + s + ">Check Out</A>");
            outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=catalog&session=" + request.getValue("session") + ">Return to Catalog</A>");
        }
    }

    public void printCheckoutPage()
    {
        integratePages("Check Out", "operation=checkoutContents&add=" + request.getValue("add"));
    }

    public void printCheckoutContents()
        throws java.lang.Exception
    {
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/demoCatalog.config");
        String s = request.getValue("session");
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=item&session=" + s + "&item=";
        HashMap hashmap = (HashMap)shoppingBasket.get(s);
        if(hashmap == null)
        {
            hashmap = new HashMap();
            shoppingBasket.add(s, hashmap);
        }
        Enumeration enumeration = (Enumeration) hashmap.keys();
        if(!enumeration.hasMoreElements())
        {
            outputStream.println("<H3>No items in in-basket</H3>,");
        } else
        {
            outputStream.println("<TABLE><TR><TH>Item</TH><TH>Description</TH></TR>");
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s2 = (String)enumeration.nextElement();
                HashMap hashmap1 = findFrame(array, "id", s2);
                if(hashmap1 != null)
                {
                    outputStream.println("<TR><TD><A HREF=" + s1 + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "id") + ">" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name") + "</A>" + "</TD><TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "description") + "</TD></TR>");
                }
            } while(true);
            outputStream.println("</TABLE>");
            outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView METHOD=POST>Name: <INPUT TYPE=TEXT NAME=name SIZE=30><BR>\nAddress: <INPUT TYPE=TEXT NAME=address SIZE=30><BR>\nCity: <INPUT TYPE=TEXT NAME=city SIZE=10><BR>\nState: <INPUT TYPE=TEXT NAME=state SIZE=5><BR>\nE-mail: <INPUT TYPE=TEXT NAME=email SIZE=30><BR>\n<INPUT TYPE=HIDDEN NAME=page VALUE=demoApp>\n<INPUT TYPE=HIDDEN NAME=operation VALUE=order>\n<INPUT TYPE=HIDDEN NAME=session VALUE=" + s + ">\n" + "<INPUT TYPE=SUBMIT VALUE=\"Place Order\">\n" + "</FORM>");
        }
    }

    void log(String s)
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/logs/demo.log");
        java.io.RandomAccessFile randomaccessfile = null;
        try
        {
            randomaccessfile = new RandomAccessFile(file, "rw");
            randomaccessfile.seek(randomaccessfile.length());
            String s1 = "";
            s1 = s1 + s + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
            randomaccessfile.writeBytes(s1);
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.err.println("Could not open log file " + file);
        }
        finally
        {
            try
            {
                if(randomaccessfile != null)
                {
                    randomaccessfile.close();
                }
            }
            catch(java.io.IOException ioexception1) { }
        }
    }

    public void printOrderPage()
    {
        integratePages("Place Order", "operation=orderContents&email=" + request.getValue("email"));
    }

    public void printOrderContents()
        throws java.lang.Exception
    {
        String s = request.getValue("session");
        HashMap hashmap = (HashMap)shoppingBasket.get(s);
        if(hashmap == null)
        {
            hashmap = new HashMap();
            shoppingBasket.add(s, hashmap);
        }
        Enumeration enumeration = (Enumeration) hashmap.keys();
        if(!enumeration.hasMoreElements())
        {
            outputStream.println("<H3>No items in in-basket</H3>,");
        } else
        {
            long l = 10000L + (long)(java.lang.Math.random() * 10000D);
            String s1 = "" + l;
            String s2 = "Your order number was " + s1;
            outputStream.println("<H2>Order Processed</H2><P><PRE>" + s2 + "</PRE>");
            COM.dragonflow.Utils.MailUtils.mail(getMasterConfig(), request.getValue("email"), "Widget Order", s2);
            log(s + "," + s1);
            shoppingBasket.remove(s);
            outputStream.println("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=catalog&session=" + s + ">Return to Catalog</A>");
        }
    }

    HashMap findFrame(Array array, String s, String s1)
    {
        for(int i = 0; i < array.size(); i++)
        {
            HashMap hashmap = (HashMap)array.get(i);
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, s).equals(s1))
            {
                return hashmap;
            }
        }

        return null;
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        COM.dragonflow.Utils.TextUtils.debugPrint("OPERATION=" + s);
        if(s.equals("catalog"))
        {
            printCatalogPage();
        } else
        if(s.equals("item"))
        {
            printItemPage();
        } else
        if(s.equals("catalogContents"))
        {
            printCatalogContents();
        } else
        if(s.equals("itemContents"))
        {
            printItemContents();
        } else
        if(s.equals("shoppingBasket"))
        {
            printShoppingBasketPage();
        } else
        if(s.equals("shoppingBasketContents"))
        {
            printShoppingBasketContents();
        } else
        if(s.equals("checkout"))
        {
            printCheckoutPage();
        } else
        if(s.equals("checkoutContents"))
        {
            printCheckoutContents();
        } else
        if(s.equals("order"))
        {
            printOrderPage();
        } else
        if(s.equals("orderContents"))
        {
            printOrderContents();
        } else
        {
            COM.dragonflow.Page.demoAppPage.printError(outputStream, "Operation unknown", "unknown operation for catalog", "/SiteView/cgi/go.exe/SiteView?page=demoApp&operation=catalog&session=" + request.getValue("session"));
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.demoAppPage demoapppage = new demoAppPage();
        if(args.length > 0)
        {
            demoapppage.args = args;
        }
        demoapppage.handleRequest();
    }

}
