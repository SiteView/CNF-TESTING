//FrontEnd Plus GUI for JAD
//DeCompiled : go.class
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.SocketStream;

public class go
{

 public go()
 {
 }

 public static void loadShellProperties()
     throws IOException
 {
     Properties properties = new Properties(System.getProperties());
     Process process = CommandLine.execSync("cmd.exe /c set");
     BufferedReader bufferedreader = FileUtils.MakeInputReader(process.getInputStream());
     for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
     {
         int i = s.indexOf('=');
         if(i < 0)
         {
             System.out.println("Error: SET output line '" + s + "'");
         } else
         {
             String s1 = s.substring(0, i);
             String s2 = s.substring(i + 1);
             properties.put(s1, s2);
         }
     }

     System.setProperties(properties);
 }

 static HTTPRequest getRequest(String as[])
     throws Exception
 {
     BufferedReader bufferedreader = FileUtils.MakeInputReader(System.in);
     HTTPRequest httprequest;
     if(as.length != 0)
         httprequest = new HTTPRequest(as);
     else
         httprequest = new HTTPRequest(bufferedreader);
     return httprequest;
 }

 public static void main(String args[])
     throws IOException
 {
     PrintStream printstream = System.out;
     PrintWriter printwriter = FileUtils.MakeOutputWriter(printstream);
     printwriter.println("Content-type: text/html\n\n<html>");
     printwriter.flush();
     HTTPRequest httprequest = null;
     try
     {
         if(Platform.isWindows())
             loadShellProperties();
         httprequest = getRequest(args);
         SiteViewGroup.setupUser(httprequest);
         String s = httprequest.getValue("page");
         if(s == null || s.length() == 0)
             s = "index";
         if(s.equals("perfex"))
         {
             printwriter.println("<PRE>");
             Array array = (new CommandLine()).exec(Platform.getRoot() + "/tools/perfex " + httprequest.getValue("option"));
             for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); printwriter.println((String)enumeration.nextElement()));
             printwriter.println("</PRE>");
         } else
         if(s.equals("SendModem"))
         {
             printwriter.println("<PRE>");
             Array array1 = (new CommandLine()).exec(Platform.getRoot() + "/tools/SendModem " + httprequest.getValue("option"));
             for(Enumeration enumeration1 = array1.elements(); enumeration1.hasMoreElements(); printwriter.println((String)enumeration1.nextElement()));
             printwriter.println("</PRE>");
         } else
         if(s.equals("Platform"))
         {
             printwriter.println("<PRE>");
             Platform.main(new String[0]);
             printwriter.println("</PRE>");
         } else
         if(s.equals("dialup"))
         {
             printwriter.println("<PRE>");
             Array array2 = (new CommandLine()).exec(Platform.getRoot() + "/tools/dialup " + httprequest.getValue("option"));
             for(Enumeration enumeration2 = array2.elements(); enumeration2.hasMoreElements(); printwriter.println((String)enumeration2.nextElement()));
             printwriter.println("</PRE>");
         } else
         {
             HashMap hashmap = MasterConfig.getMasterConfig();
             String s3 = (String)hashmap.get("_httpPort");
             if((s3 == null || s3.length() <= 0) && Platform.isUnix())
                 SocketStream.initialize(hashmap);
             Class class1 = Class.forName("COM.dragonflow.Page." + s + "Page");
             CGI cgi = (CGI)class1.newInstance();
             cgi.initialize(httprequest, printwriter);
             cgi.printBody();
         }
         printwriter.println("</HTML>");
     }
     catch(ClassNotFoundException classnotfoundexception)
     {
         String s1 = "";
         if(httprequest != null)
             s1 = httprequest.getURL();
         HTTPRequest.printErrorMessage(httprequest, 404, s1, classnotfoundexception, printwriter);
     }
     catch(Exception exception)
     {
         String s2 = "";
         if(httprequest != null)
             s2 = httprequest.getURL();
         HTTPRequest.printErrorMessage(httprequest, 999, s2, exception, printwriter);
     }
     printwriter.flush();
     System.exit(0);
 }
}