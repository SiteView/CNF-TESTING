/*
 * 
 * Created on 2005-2-28 7:08:57
 *
 * treeProperty.java
 *
 * History:
 *
 */
package com.dragonflow.Properties;

/**
 * Comment for <code>treeProperty</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Page.CGI;
import com.dragonflow.Page.treeControl;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.SiteViewGroup;
import com.dragonflow.SiteView.SiteViewObject;
import com.dragonflow.SiteView.SubGroup;

//Referenced classes of package com.dragonflow.Properties:
//         ScalarProperty

public class treeProperty extends ScalarProperty
{

 public treeProperty(String s)
 {
     this(s, "");
 }

 public treeProperty(String s, boolean flag)
 {
     this(s, "", flag);
 }

 public treeProperty(String s, String s1)
 {
     this(s, s1, false);
 }

 public treeProperty(String s, String s1, boolean flag)
 {
     super(s, s1);
     allowDisable = flag;
 }

 public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject, HTTPRequest httprequest, HashMap hashmap, boolean flag)
 {
     if(!treeControl.useTree())
     {
         super.printProperty(cgi, printwriter, siteviewobject, httprequest, hashmap, flag);
         return;
     }
     if(!flag && (siteviewobject instanceof AtomicMonitor))
     {
         flag = !((AtomicMonitor)siteviewobject).isEditableProperty(this, httprequest);
     }
     if(allowDisable && flag)
     {
         super.printProperty(cgi, printwriter, siteviewobject, httprequest, hashmap, flag);
         return;
     }
     Object obj = hashmap.get(this);
     String s = "";
     if(obj != null)
     {
         s = (String)obj;
     }
     Array array = new Array();
     if(httprequest.getValue(getName()).length() > 0)
     {
         array.add(HTTPRequest.decodeString(httprequest.getValue(getName())));
     } else
     {
         Enumeration enumeration = siteviewobject.getMultipleValues(this);
         if(enumeration.hasMoreElements())
         {
             array.add(enumeration.nextElement());
         }
     }
     if(array.size() == 0)
     {
         array.add("_none");
     }
     StringBuffer stringbuffer = new StringBuffer();
     byte byte0 = 5;
     Array array1 = new Array();
     SiteViewObject siteviewobject1 = ((Monitor)siteviewobject).getParent();
     String s1 = "";
     if(siteviewobject1 instanceof SubGroup)
     {
         s1 = siteviewobject1.getProperty(SubGroup.pGroup);
     } else
     {
         s1 = siteviewobject1.getProperty(SiteViewGroup.pGroupID);
     }
     array1.add(s1 + " " + siteviewobject.getProperty(Monitor.pID));
     try
     {
         treeControl treecontrol = new treeControl(httprequest, getName(), true, array, !httprequest.isPost());
         treecontrol.process(getLabel(), s, getDescription(), array, array1, null, byte0, cgi, stringbuffer);
     }
     catch(Exception exception)
     {
         exception.printStackTrace();
     }
     printwriter.println("<a name=treeContrtol>");
     printwriter.print("<TR>" + stringbuffer + "</TR>");
     if(!treeControl.notHandled(httprequest))
     {
         printwriter.print("<script language=\"JavaScript\">;document");
     }
 }
}