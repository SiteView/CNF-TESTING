/*
 * 
 * Created on 2005-2-28 6:57:39
 *
 * FrequencyProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>FrequencyProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.SiteViewObject;
import java.io.PrintWriter;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.Properties:
//         NumericProperty

public class FrequencyProperty extends NumericProperty
{

 static final int daySeconds = 0x15180;
 static final int hourSeconds = 3600;
 static final int minuteSeconds = 60;

 public FrequencyProperty(String s, String s1)
 {
     super(s, s1);
 }

 public static int toSeconds(int i, String s)
 {
     if(s.equals("days"))
     {
         return i * 0x15180;
     }
     if(s.equals("hours"))
     {
         return i * 3600;
     }
     if(s.equals("minutes"))
     {
         return i * 60;
     } else
     {
         return i;
     }
 }

 public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, HashMap hashmap, boolean flag)
 {
     Object obj = hashmap.get(this);
     String s = "";
     if(obj != null)
     {
         s = (String)obj;
     }
     int i = siteviewobject.getPropertyAsInteger(this);
     String s1 = "";
     String s2 = "";
     String s3 = "";
     String s4 = "";
     if(i == 0)
     {
         s3 = "SELECTED";
     } else
     if(i % 0x15180 == 0)
     {
         i /= 0x15180;
         s1 = "SELECTED";
     } else
     if(i % 3600 == 0)
     {
         i /= 3600;
         s2 = "SELECTED";
     } else
     if(i % 60 == 0)
     {
         i /= 60;
         s3 = "SELECTED";
     } else
     {
         s4 = "SELECTED";
     }
     String s5 = "";
     if(i != 0)
     {
         s5 = "" + i;
     }
     printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>" + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><input type=text name=" + getName() + " size=5 maxlength=4 value=" + s5 + ">" + "<select name=" + getName() + "Units size=1>" + "<OPTION " + s4 + ">seconds<OPTION " + s3 + ">minutes<OPTION " + s2 + ">hours<OPTION " + s1 + ">days" + "</SELECT></TD></TR>" + "<TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>");
 }
}