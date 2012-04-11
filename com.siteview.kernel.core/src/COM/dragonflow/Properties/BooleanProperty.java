/*
 * 
 * Created on 2005-2-28 6:50:32
 *
 * BooleanProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>BooleanProperty</code>
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
//         StringProperty

public class BooleanProperty extends StringProperty
{

 public BooleanProperty(String s)
 {
     super(s, "");
 }

 public BooleanProperty(String s, String s1)
 {
     super(s, s1);
 }

 public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, HashMap hashmap, boolean flag)
 {
     Object obj = hashmap.get(this);
     String s = "";
     if(obj != null)
     {
         s = (String)obj;
     }
     String s1 = siteviewobject.getProperty(this);
     String s2 = "checked";
     if(s1 == null || s1.trim().length() == 0)
     {
         s2 = "";
     }
     printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\"></TD><TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><input type=checkbox name=" + getName() + " size=50 " + s2 + ">" + getLabel() + "</TD></TR>" + "<TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>");
 }
}