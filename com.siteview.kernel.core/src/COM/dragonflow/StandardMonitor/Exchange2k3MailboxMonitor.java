/*
 * 
 * Created on 2005-3-7 1:08:41
 *
 * Exchange2k3MailboxMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>Exchange2k3MailboxMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.w3c.dom.Document;

public class Exchange2k3MailboxMonitor extends ExchangeWMIToolBase
{
 class MBSizeCompare
     implements Comparator
 {

     public int compare(Object obj, Object obj1)
     {
         Map map = (Map)obj;
         Map map1 = (Map)obj1;
         String s = (String)map.get("Size");
         String s1 = (String)map1.get("Size");
         int i = 0;
         int j = 0;
         try
         {
             i = Integer.parseInt(s);
         }
         catch(Exception exception) { }
         try
         {
             j = Integer.parseInt(s1);
         }
         catch(Exception exception1) { }
         if(i < j)
         {
             return 1;
         }
         return i <= j ? 0 : -1;
     }

     MBSizeCompare()
     {
         super();
     }
 }


 static DateFormat mDisplayDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
 static StringProperty pNotAccessedN;
 static StringProperty pTopSizeN;
 private static String EXCH_ISSUE_WARNING = "2";
 private static String EXCH_PROHIBIT_SEND = "4";
 private static final int COST_IN_LICENSE_POINTS = 3;

 public Exchange2k3MailboxMonitor()
 {
 }

 /**
  * CAUTION: Decompiled by hand.
  */
 protected boolean update()
 {
     try {
     String s = "SELECT * FROM Exchange_Mailbox";
     Vector vector = executeQuery(s);
     if(vector == null)
     {
         return false;
     }
     for(int i = 0; i < vector.size(); i++)
     {
         Map map = (Map)vector.elementAt(i);
         Date date = WMIUtils.makeJavaDateFromWMIDate((String)map.get("LastLogonTime"));
         if(date != null)
         {
             map.put("JavaLastLogonTime", date);
         }
         date = WMIUtils.makeJavaDateFromWMIDate((String)map.get("LastLogoffTime"));
         if(date != null)
         {
             map.put("JavaLastLogoffTime", date);
         }
     }

     Document document = createNewDocument("Exchange Mailbox Statistics");
     showNotAccessedInNDays(vector, document);
     showOverLimitMailboxes(vector, document);
     showTopNMailboxesInSize(vector, document);
     saveDocument(document);
     setProperty(pStateString, "OK");
     setProperty(pStatus, "OK");
     return true;
     }
     catch (Exception e) {
     setProperty(pNoData, "n/a");
     setProperty(pStatus, "error");
     setProperty(pStateString, e.getMessage());
     return false;
     }
 }

 private void showTopNMailboxesInSize(Vector vector, Document document)
 {
     int i = getPropertyAsInteger(pTopSizeN);
     if(i > 0)
     {
         Object aobj[] = vector.toArray();
         Arrays.sort(aobj, new MBSizeCompare());
         Vector vector1 = new Vector();
         for(int j = 0; j < i && j < aobj.length; j++)
         {
             Vector vector2 = new Vector();
             vector1.addElement(vector2);
             Map map = (Map)aobj[j];
             vector2.addElement(new Pair("Mailbox", map.get("MailboxDisplayName")));
             vector2.addElement(new Pair("Size", map.get("Size")));
         }

         addListContent(vector1, "Top " + i + " Mailboxes in Size", document);
     }
 }

 private void showOverLimitMailboxes(Vector vector, Document document)
 {
     Vector vector1 = new Vector();
     for(int i = 0; i < vector.size(); i++)
     {
         Map map = (Map)vector.elementAt(i);
         String s = (String)map.get("StorageLimitInfo");
         if(s.equals(EXCH_ISSUE_WARNING) || s.equals(EXCH_PROHIBIT_SEND))
         {
             Vector vector2 = new Vector();
             vector1.addElement(vector2);
             vector2.addElement(new Pair("Mailbox", map.get("MailboxDisplayName")));
         }
     }

     addListContent(vector1, "Over Limit Mailboxes", document);
 }

 private void showNotAccessedInNDays(Vector vector, Document document)
 {
     int i = getPropertyAsInteger(pNotAccessedN);
     if(i > 0)
     {
         Vector vector1 = new Vector();
         GregorianCalendar gregoriancalendar = new GregorianCalendar();
         gregoriancalendar.setTime(new Date());
         gregoriancalendar.add(6, -i);
         Date date = gregoriancalendar.getTime();
         for(int j = 0; j < vector.size(); j++)
         {
             Map map = (Map)vector.elementAt(j);
             Date date1 = (Date)map.get("JavaLastLogoffTime");
             if(date1 == null || date1.getTime() >= date.getTime())
             {
                 continue;
             }
             Vector vector2 = new Vector();
             vector1.addElement(vector2);
             vector2.addElement(new Pair("Mailbox", map.get("MailboxDisplayName")));
             synchronized(mDisplayDateFormat)
             {
                 vector2.addElement(new Pair("Last Logoff Time", mDisplayDateFormat.format(date1)));
             }
         }

         addListContent(vector1, "Mailboxes Not Accessed in " + i + " Days", document);
     }
 }

 public int getCostInLicensePoints()
 {
     return 3;
 }

 static 
 {
     String s = (COM.dragonflow.StandardMonitor.Exchange2k3MailboxMonitor.class).getName();
     pNotAccessedN = new StringProperty("_notaccessedN");
     pNotAccessedN.setDisplayText("Days since access", "For \"Mailboxes not accessed in N days\"");
     pNotAccessedN.setParameterOptions(true, 5, false);
     pTopSizeN = new StringProperty("_topsizeN");
     pTopSizeN.setDisplayText("N largest mailboxes", "For \"Top N mailboxes in size\"");
     pTopSizeN.setParameterOptions(true, 6, false);
     StringProperty astringproperty[] = {
         pNotAccessedN, pTopSizeN
     };
     addProperties(s, astringproperty);
     addClassElement(s, Rule.stringToClassifier("status != OK\terror"));
     addClassElement(s, Rule.stringToClassifier("always\tgood", true));
     setClassProperty(s, "description", "Monitors Exchange 2003 Mailboxes.");
     setClassProperty(s, "title", "Exchange 2003 Mailbox");
     setClassProperty(s, "class", "Exchange2k3MailboxMonitor");
     setClassProperty(s, "help", "Exch2k3MailboxMon.htm");
     setClassProperty(s, "target", "_server");
     setClassProperty(s, "topazName", "Exchange 2003 Mailbox");
     setClassProperty(s, "classType", "application");
     if(!Platform.isWindows() || !LUtils.isValidSSforXLicense(new Exchange2k3MailboxMonitor()))
     {
         setClassProperty(s, "loadable", "false");
     }
     setClassProperty(s, "addable", "false");
 }
}
