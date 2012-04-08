/*
 * 
 * Created on 2005-2-28 7:11:47
 *
 * XMLStringProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>XMLStringProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

//Referenced classes of package COM.dragonflow.Properties:
//         StringProperty

public class XMLStringProperty extends StringProperty
{

 public XMLStringProperty(String s)
 {
     super(s);
 }

 public XMLStringProperty(String s, String s1)
 {
     super(s, s1);
 }

 public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, HashMap hashmap, boolean flag)
 {
     Object obj = hashmap.get(this);
     String s = "";
     if(encoding == null || encoding.length() == 0)
     {
         encoding = I18N.getDefaultEncoding();
     }
     if(obj != null)
     {
         s = (String)obj;
     }
     printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>" + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><TEXTAREA name=" + getName() + " rows=4 cols=50>");
     Enumeration enumeration = siteviewobject.getMultipleValues(this);
     if(enumeration.hasMoreElements())
     {
         for(; enumeration.hasMoreElements(); printwriter.println(TextUtils.encodeArgs((String)enumeration.nextElement()))) { }
     } else
     {
         String as[] = TextUtils.split(getDefault(), multiLineDelimiter);
         for(int i = 0; i < as.length; i++)
         {
             printwriter.println(as[i]);
         }

     }
     printwriter.println("</TEXTAREA></TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription() + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>");
 }
}