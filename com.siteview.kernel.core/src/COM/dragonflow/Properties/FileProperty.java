/*
 * 
 * Created on 2005-2-28 6:55:23
 *
 * FileProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>FileProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

import java.io.File;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.Properties:
//         StringProperty

public class FileProperty extends StringProperty
{

 private boolean browse;

 public FileProperty(String s, String s1)
 {
     super(s, s1);
     browse = false;
 }

 public FileProperty(String s, String s1, boolean flag)
 {
     super(s, s1);
     browse = false;
     browse = flag;
 }

 public void fieldLabels(String s, String s1)
 {
     if(Platform.isWindows())
     {
         setDisplayText(s, s1 + "<br> Optionally, use a <a href=/SiteView/docs/regexp.htm#date_vars>regular expression</a>" + " to insert date and time variables <br>(e.g s/C:\\firstdir\\$shortYear$$0month$$0day$/)");
     } else
     {
         setDisplayText(s, s1 + "<br> Optionally, use a <a href=/SiteView/docs/regexp.htm#date_vars>regular expression</a>" + " to insert date and time variables <br>(e.g s|/firstdir/$shortYear$$0month$$0day$|)");
     }
 }

 public String getName(Monitor monitor)
 {
     String s = new String(monitor.getProperty(this));
     if(s != null && TextUtils.isSubstituteExpression(s))
     {
         s = TextUtils.substitute(s);
     }
     return s;
 }

 public File getValue(Monitor monitor)
 {
     String s = I18N.toDefaultEncoding(getName(monitor));
     File file = null;
     if(s != null)
     {
         file = new File(s);
     }
     return file;
 }

 public File verify(String s, HashMap hashmap)
 {
     return verify(s, hashmap, true, true);
 }

 public File verify(String s, HashMap hashmap, boolean flag)
 {
     return verify(s, hashmap, flag, true);
 }

 public File verify(String s, HashMap hashmap, boolean flag, boolean flag1)
 {
     File file = null;
     String s1 = I18N.toDefaultEncoding(s);
     if(TextUtils.isSubstituteExpression(s1.trim()))
     {
         flag = false;
     }
     if(s1.trim().length() == 0 && flag1)
     {
         hashmap.put(this, getLabel() + " missing");
     } else
     if(s1.trim().length() > 0 && flag)
     {
         file = new File(s1);
         if(!file.exists())
         {
             hashmap.put(this, getLabel() + " not found");
         }
     }
     return file;
 }

 public boolean isBrowse()
 {
     return browse;
 }
}