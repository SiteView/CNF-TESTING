 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package COM.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */


// Referenced classes of package COM.dragonflow.Utils:
// RawXmlWriter, StringPropertyUtil

public class HtmlUtil
{

    public HtmlUtil()
    {
    }

    public static String createHiddenInputs(java.util.HashMap hashmap)
    {
        StringBuffer stringbuffer = new StringBuffer();
        java.util.Set set = hashmap.entrySet();
        java.util.Map.Entry entry;
        for(java.util.Iterator iterator = set.iterator(); iterator.hasNext(); stringbuffer.append("<INPUT type=hidden name='" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement((String)entry.getKey()) + "' id='" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement((String)entry.getKey()) + "' value='" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement((String)entry.getValue()) + "'>\n"))
        {
            entry = (java.util.Map.Entry)iterator.next();
        }

        return stringbuffer.toString();
    }

    public static String createHiddenInputs(COM.dragonflow.Properties.StringProperty astringproperty[], String s, String s1, String s2, String s3)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<INPUT type=hidden id='" + s + s3 + "' value='" + astringproperty.length + "'>\n");
        for(int i = 0; i < astringproperty.length; i++)
        {
            String s4 = COM.dragonflow.Utils.RawXmlWriter.enCodeElement(astringproperty[i].getLabel());
            stringbuffer.append("<INPUT type=hidden id='" + s + s1 + i + "' value='" + s4 + "'>\n");
            String s5 = COM.dragonflow.Utils.RawXmlWriter.enCodeElement(astringproperty[i].getDescription());
            stringbuffer.append("<INPUT type=hidden id='" + s + s2 + i + "' value='" + s5 + "'>\n");
        }

        return stringbuffer.toString();
    }

    public static String createCheckboxList(COM.dragonflow.Properties.StringProperty astringproperty[], String s)
    {
        COM.dragonflow.Properties.StringProperty astringproperty1[] = (COM.dragonflow.Properties.StringProperty[])astringproperty.clone();
        COM.dragonflow.Utils.StringPropertyUtil.sortPropsArray(astringproperty1);
        StringBuffer stringbuffer = new StringBuffer("<SCRIPT LANGUAGE='Javascript' SRC='/SiteView/htdocs/js/utils.js'></SCRIPT>\n<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>\n   <TR>\n       <TD width=100%><DIV style='width:100%; height:88px; OVERFLOW: auto'>\n           <TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>\n");
        for(int i = 0; i < astringproperty1.length; i++)
        {
            stringbuffer.append("               <TR>\n                   <TD>\n                       <INPUT type='checkbox' id='" + s + i + "' value=\"" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement(astringproperty1[i].getLabel()) + "\" title=\"" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement(astringproperty1[i].getDescription()) + "\">" + COM.dragonflow.Utils.RawXmlWriter.enCodeElement(astringproperty1[i].getLabel()) + "</INPUT>\n" + "                   </TD>\n" + "               </TR>\n");
        }

        stringbuffer.append("           </TABLE>\n       </DIV></TD>\n       <TD vAlign=top>\n           <TABLE width=1 cellSpacing=0 cellPadding=0 border=0 align=top>\n               <TR><TD>\n                   <INPUT type=button title='Select all counters' value='Select All' onclick=\"checkCheckboxes('" + s + "', true)\">\n" + "               </TD></TR>\n" + "               <TR><TD>\n" + "                   <INPUT type=button value='Clear All' title='Clear all counter selections' onclick=\"checkCheckboxes('" + s + "', false)\" style='WIDTH: 100%'>\n" + "               </TD></TR>\n" + "           </TABLE>\n" + "       </TD>\n" + "   </TR>\n" + "</TABLE>\n");
        return stringbuffer.toString();
    }
}
