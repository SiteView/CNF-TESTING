/*
 * 
 * Created on 2005-2-28 7:03:42
 *
 * PerfmonProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>PerfmonProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.PerfmonMonitorBase;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.Utils.HtmlUtil;
import COM.dragonflow.Utils.JscriptUtil;
import COM.dragonflow.Utils.TextUtils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import jgl.Array;

//Referenced classes of package COM.dragonflow.Properties:
//         StringProperty

public class PerfmonProperty extends StringProperty
{

 static final String PERFMON_OBJECT_PROP_LABEL = "Monitored Object";
 static final String PERFMON_OBJECT_PROP_DESC = "Select the object you wish to monitor";
 static final String PERFMON_VALUES_CAPTION = "Selected Measurements";
 static final String HIDDEN_INPUTS_PREFIX = "perfmonId";
 static final String MIN_TD_WIDTH = "100px";

 public PerfmonProperty(String s)
 {
     super(s);
 }

 public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, jgl.HashMap hashmap, boolean flag)
 {
     PerfmonMonitorBase perfmonmonitorbase = (PerfmonMonitorBase)siteviewobject;
     perfmonmonitorbase.setReturnUrl(httprequest);
     HashMap hashmap1 = perfmonmonitorbase.getRequiredRequestCreationProps();
     hashmap1.put("perfmonPageOp", "");
     printwriter.println(HtmlUtil.createHiddenInputs(hashmap1));
     Array array = perfmonmonitorbase.getPropertiesToPassBetweenPages(httprequest);
     String as[] = new String[hashmap1.size() + array.size()];
     Set set = hashmap1.keySet();
     Iterator iterator = set.iterator();
     for(int i = 0; iterator.hasNext(); i++)
     {
         as[i] = (String)iterator.next();
     }

     for(int j = 0; j < array.size(); j++)
     {
         as[j + hashmap1.size()] = ((StringProperty)array.at(j)).getName();
     }

     String s = "getFieldsAsUrlParams";
     String s1 = JscriptUtil.createFieldsAsUrlParamsFunc(s, as);
     printwriter.println(s1);
     String s2 = cgi.getPageLink("perfmon", "");
     String s3 = "gotoSelectionsPage";
     printwriter.println("<SCRIPT>   function " + s3 + "(op){\n" + "       var colOpObj = document.getElementsByName(\"" + "perfmonPageOp" + "\");\n" + "       for (i=0; i<colOpObj.length; i++){\n" + "           if (colOpObj[i].tagName.toLowerCase() == \"input\"){\n" + "               colOpObj[i].value=op;\n" + "           }\n" + "       }\n" + "       location.href = \"" + s2 + "&" + "\" + getFieldsAsUrlParams();\n" + "   }\n" + "</SCRIPT>\n");
     String s4 = printMsmtsInTable(perfmonmonitorbase.getPerfmonMeasurements());
     printwriter.println("<TR>\n   <TD ALIGN=RIGHT>Selected Measurements</TD>\n   <TD>\n       <TABLE border=1 cellspacing=0><TR><TD><TABLE>\n           <TR>\n               <TD>\n" + s4 + "               </TD>\n" + "           </TR>\n" + "               <TD ALIGN=LEFT>\n" + "                   <A href='javascript:void(0)' onclick=\"gotoSelectionsPage('" + "selectMsmts" + "');return false;\">Select Measurements\n" + "                   </A>\n" + "               </TD>\n" + "           </TR>\n" + "       </TABLE></TD></TR></TABLE>\n" + "   </TD>\n" + "</TR>\n");
 }

 public static String printMsmtsInTable(StringProperty astringproperty[])
 {
     String s = "<TABLE border=1 cellspacing=0>";
     if(astringproperty.length == 0)
     {
         s = s + "<TR><TD ALIGN=LEFT>None selected</TD></TR>";
     } else
     {
         for(int i = 0; i < astringproperty.length; i++)
         {
             s = s + "<TR><TD ALIGN=LEFT title=\"" + TextUtils.escapeHTML(astringproperty[i].getDescription()) + "\">" + TextUtils.escapeHTML(PerfmonMonitorBase.getMsmtLabel(astringproperty[i])) + "</TD></TR>";
         }

     }
     s = s + "</TABLE></TD></TR>";
     return s;
 }
}