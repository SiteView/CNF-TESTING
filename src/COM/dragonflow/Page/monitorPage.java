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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TreeMap;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.GreaterCounterByXml;

// Referenced classes of package COM.dragonflow.Page:
// CGI, treeControl, managePage

public class monitorPage extends COM.dragonflow.Page.CGI
{
    class lessMonitor
        implements jgl.BinaryPredicate
    {

        public boolean execute(java.lang.Object obj, java.lang.Object obj1)
        {
            monitorList monitorlist = (monitorList)obj;
            monitorList monitorlist1 = (monitorList)obj1;
            return monitorlist.name.toLowerCase().compareTo(monitorlist1.name.toLowerCase()) < 0;
        }

        public lessMonitor()
        {
            super();
        }
    }

    class monitorList
    {

        public String name;
        public String className;
        public String page;

        public monitorList(String s, String s1, String s2)
        {
            super();
            name = new String(s);
            className = new String(s1);
            page = new String(s2);
        }
    }


    public static final String OPERATION = "operation";
    public static final String EDIT_OPERATION = "Edit";
    static jgl.Array monitorNames = null;
    static jgl.HashMap classFilter;
    static boolean batch;
    static String showMonitors;
    static String NAME_MARKER = "$NAME$";
    static String VALUE_MARKER = "$VALUE$";

    public monitorPage()
    {
    }

    public static void burnCache()
    {
        monitorNames = null;
    }

    public static jgl.Array getMonitorClasses(boolean flag)
    {
        return COM.dragonflow.Page.monitorPage.getMonitorClasses(null, flag);
    }

    public static jgl.Array getMonitorClasses()
    {
        return COM.dragonflow.Page.monitorPage.getMonitorClasses(null, false);
    }

    public static jgl.Array getMonitorClasses(COM.dragonflow.Page.CGI cgi, boolean flag)
    {
        jgl.Array array = new Array();
        if(cgi != null && cgi.isPortalServerRequest())
        {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)cgi.getSiteView();
            if(portalsiteview != null)
            {
                classFilter = portalsiteview.getMonitorClassFilter();
            }
        }
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/classes/COM/dragonflow/StandardMonitor");
        String as[] = file.list();
        for(int i = 0; i < as.length; i++)
        {
            if(!as[i].endsWith("Monitor.class"))
            {
                continue;
            }
            int j = as[i].lastIndexOf(".class");
            String s = as[i].substring(0, j);
            if(classFilter != null && classFilter.get(s) == null)
            {
                continue;
            }
            try
            {
                java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.StandardMonitor." + s);
                if(flag)
                {
                    if(COM.dragonflow.SiteView.SiteViewObject.loadableClass(class1))
                    {
                        array.add(class1);
                    }
                    continue;
                }
                if(COM.dragonflow.SiteView.SiteViewObject.loadableClass(class1) && COM.dragonflow.SiteView.SiteViewObject.addableClass(class1))
                {
                    array.add(class1);
                }
            }
            catch(java.lang.Throwable throwable) { }
        }

        java.io.File file1 = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/classes/CustomMonitor");
        as = file1.list();
        if(as != null)
        {
            for(int k = 0; k < as.length; k++)
            {
                if(!as[k].endsWith("Monitor.class"))
                {
                    continue;
                }
                int l = as[k].lastIndexOf(".class");
                String s1 = as[k].substring(0, l);
                if(classFilter != null && classFilter.get(s1) == null)
                {
                    continue;
                }
                try
                {
                    java.lang.Class class2 = java.lang.Class.forName("CustomMonitor." + s1);
                    if(COM.dragonflow.SiteView.SiteViewObject.loadableClass(class2) && COM.dragonflow.SiteView.SiteViewObject.addableClass(class2))
                    {
                        array.add(class2);
                    }
                }
                catch(java.lang.Throwable throwable1) { }
            }

        }
        return array;
    }

    public void printProperty(COM.dragonflow.Properties.StringProperty stringproperty, java.io.PrintWriter printwriter, COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap, boolean flag)
    {
        if(!siteviewobject.isPropertyExcluded(stringproperty, httprequest) && !httprequest.getPermission("_monitorProperty", stringproperty.getName()).equals("hidden") && !siteviewobject.propertyInTemplate(stringproperty))
        {
            stringproperty.printProperty(this, printwriter, siteviewobject, httprequest, hashmap, flag);
        }
    }

    boolean shouldPrintProperty(COM.dragonflow.SiteView.SiteViewObject siteviewobject, String s)
    {
        return !siteviewobject.propertyInTemplate(s);
    }

    public COM.dragonflow.Page.CGI.menus getNavItems(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        COM.dragonflow.Page.CGI.menus menus1 = new CGI.menus();
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if(httprequest.actionAllowed("_preference"))
        {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if(httprequest.actionAllowed("_tools"))
        {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if(httprequest.actionAllowed("_progress"))
        {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        return menus1;
    }

    private String getNTAssociatedCountersContent(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s, String s1)
    {
        String s2 = "";
        StringBuffer stringbuffer = new StringBuffer();
        jgl.Array array = new Array();
        String s3 = "";
        String as[];
        if(request.hasValue("counterobjects"))
        {
            String s4 = request.getValue("counterobjects");
            as = COM.dragonflow.Utils.TextUtils.split(s4, ",");
        } else
        {
            String s5 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getCounterObjects();
            as = COM.dragonflow.Utils.TextUtils.split(s5, ",");
        }
        for(int i = 0; i < as.length; i++)
        {
            String s6 = as[i].trim();
            array.add(s6);
        }

        jgl.Array array1 = COM.dragonflow.SiteView.NTCounterBase.getPerfCounters(s1, array, stringbuffer, "");
        String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        boolean flag = true;
label0:
        for(int j = 0; j < as1.length; j++)
        {
            int k = 0;
            do
            {
                if(k >= array1.size())
                {
                    continue label0;
                }
                if(as1[j].equals(String.valueOf(k)))
                {
                    if(!flag)
                    {
                        s2 = s2 + ",";
                    } else
                    {
                        flag = false;
                    }
                    s2 = s2 + ((COM.dragonflow.Utils.PerfCounter)array1.at(k)).object + " -- ";
                    s2 = s2 + ((COM.dragonflow.Utils.PerfCounter)array1.at(k)).counterName;
                    if(((COM.dragonflow.Utils.PerfCounter)array1.at(k)).instance.length() > 0)
                    {
                        s2 = s2 + " -- " + ((COM.dragonflow.Utils.PerfCounter)array1.at(k)).instance;
                    }
                    continue label0;
                }
                k++;
            } while(true);
        }

        return s2;
    }

    private String getSNMPAssociatedCountersContent(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s)
    {
        String s1 = "";
        COM.dragonflow.SiteView.SNMPBase _tmp = (COM.dragonflow.SiteView.SNMPBase)atomicmonitor;
        COM.dragonflow.SiteView.SNMPBase _tmp1 = (COM.dragonflow.SiteView.SNMPBase)atomicmonitor;
        String s2 = COM.dragonflow.SiteView.SNMPBase.getTemplateContent(COM.dragonflow.SiteView.SNMPBase.getTemplatePath(), ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).getTemplateFile(), true);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s2, ",");
        String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        boolean flag = true;
label0:
        for(int i = 0; i < as1.length; i++)
        {
            int j = 0;
            do
            {
                if(j >= as.length)
                {
                    continue label0;
                }
                if(as1[i].equals(String.valueOf(j)))
                {
                    if(!flag)
                    {
                        s1 = s1 + ",";
                    } else
                    {
                        flag = false;
                    }
                    s1 = s1 + as[j];
                    continue label0;
                }
                j++;
            } while(true);
        }

        return s1;
    }

    private String getURLContentAssociatedCountersContent(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s)
    {
        COM.dragonflow.SiteView.URLContentBase _tmp = (COM.dragonflow.SiteView.URLContentBase)atomicmonitor;
        COM.dragonflow.SiteView.URLContentBase _tmp1 = (COM.dragonflow.SiteView.URLContentBase)atomicmonitor;
        String s1 = COM.dragonflow.SiteView.URLContentBase.getTemplateContent(COM.dragonflow.SiteView.URLContentBase.getTemplatePath(), ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).getTemplateFile(), true);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        String s2 = "";
        boolean flag = true;
label0:
        for(int i = 0; i < as1.length; i++)
        {
            int j = 0;
            do
            {
                if(j >= as.length)
                {
                    continue label0;
                }
                if(as1[i].equals(String.valueOf(j)))
                {
                    if(!flag)
                    {
                        s2 = s2 + ",";
                    } else
                    {
                        flag = false;
                    }
                    s2 = s2 + as[j];
                    continue label0;
                }
                j++;
            } while(true);
        }

        return s2;
    }

    private String getMultiContent(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s)
    {
        COM.dragonflow.SiteView.MultiContentBase _tmp = (COM.dragonflow.SiteView.MultiContentBase)atomicmonitor;
        COM.dragonflow.SiteView.MultiContentBase _tmp1 = (COM.dragonflow.SiteView.MultiContentBase)atomicmonitor;
        String s1 = COM.dragonflow.SiteView.MultiContentBase.getTemplateContent(COM.dragonflow.SiteView.MultiContentBase.getTemplatePath(), ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).getTemplateFile(), true);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        String s2 = "";
        boolean flag = true;
label0:
        for(int i = 0; i < as1.length; i++)
        {
            int j = 0;
            do
            {
                if(j >= as.length)
                {
                    continue label0;
                }
                if(as1[i].equals(String.valueOf(j)))
                {
                    if(!flag)
                    {
                        s2 = s2 + ",";
                    } else
                    {
                        flag = false;
                    }
                    s2 = s2 + as[j];
                    continue label0;
                }
                j++;
            } while(true);
        }

        return s2;
    }

    private String getDynamicHealth(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s)
    {
        String s1 = ((COM.dragonflow.StandardMonitor.HealthUnixServerMonitor)atomicmonitor).getContentCounters(true);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        String s2 = "";
        boolean flag = true;
label0:
        for(int i = 0; i < as1.length; i++)
        {
            int j = 0;
            do
            {
                if(j >= as.length)
                {
                    continue label0;
                }
                if(as1[i].equals(String.valueOf(j)))
                {
                    if(!flag)
                    {
                        s2 = s2 + ",";
                    } else
                    {
                        flag = false;
                    }
                    s2 = s2 + as[j];
                    continue label0;
                }
                j++;
            } while(true);
        }

        return s2;
    }

    public String getAssociatedCountersContent(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, String s)
    {
        String s1 = "";
        String s3 = request.getValue("counters");
        if(s3.length() > 0)
        {
            String s2 = atomicmonitor.getClassPropertyString("applicationType");
            if(s2 != null && s2.length() > 0)
            {
                if(s2.equals("NTCounter"))
                {
                    s1 = getNTAssociatedCountersContent(atomicmonitor, s3, s);
                } else
                if(s2.equals("SNMP"))
                {
                    s1 = getSNMPAssociatedCountersContent(atomicmonitor, s3);
                } else
                if(s2.equals("URLContent"))
                {
                    s1 = getURLContentAssociatedCountersContent(atomicmonitor, s3);
                } else
                if(s2.equals("MultiContentBase"))
                {
                    s1 = getMultiContent(atomicmonitor, s3);
                } else
                if(s2.equals("DynamicHealth"))
                {
                    s1 = getDynamicHealth(atomicmonitor, s3);
                } else
                {
                    java.lang.System.err.println("getAssociatedCountersContent() no matching monitor class found.");
                }
            } else
            {
                java.lang.System.err.println("getAssociatedCountersContent() no monitor class found.");
            }
        }
        return s1;
    }

    void printForm(String s, String s1, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, jgl.HashMap hashmap, jgl.Array array)
        throws java.io.IOException
    {
        String s2 = "";
        String s3 = "";
        String s4 = COM.dragonflow.Utils.I18N.UnicodeToString(s1, COM.dragonflow.Utils.I18N.nullEncoding());
        s4 = COM.dragonflow.Utils.I18N.StringToUnicode(s4, "");
        String s5 = COM.dragonflow.HTTP.HTTPRequest.encodeString(s4);
        COM.dragonflow.SiteView.AtomicMonitor _tmp = atomicmonitor;
        String s6 = atomicmonitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName);
        String s7 = "";
        String s8 = (String)atomicmonitor.getClassProperty("title");
        if((atomicmonitor instanceof COM.dragonflow.SiteView.IServerPropMonitor) && (!(atomicmonitor instanceof COM.dragonflow.SiteView.ServerMonitor) || COM.dragonflow.SiteView.ServerMonitor.pMachineName.isEditable))
        {
            if(request.hasValue("server"))
            {
                String s9 = request.getValue("server");
                if(s9.equals("this server"))
                {
                    s9 = "";
                }
                atomicmonitor.setProperty(((COM.dragonflow.SiteView.IServerPropMonitor)atomicmonitor).getServerProperty(), s9);
            } else
            if(s.equals("Add"))
            {
                jgl.HashMap hashmap1 = getMasterConfig();
                String s11 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_defaultMachine");
                atomicmonitor.setProperty(((COM.dragonflow.SiteView.IServerPropMonitor)atomicmonitor).getServerProperty(), s11);
            }
        }
        String s10 = "";
        if(request.hasValue("id"))
        {
            s10 = request.getValue("id");
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("NTCounter"))
        {
            String s12 = atomicmonitor.getProperty(COM.dragonflow.SiteView.ServerMonitor.pMachineName);
            String s15 = "";
            s2 = "";
            boolean flag = true;
            if(request.hasValue("counters"))
            {
                s2 = getAssociatedCountersContent(atomicmonitor, s12);
                ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s2);
            } else
            {
                s2 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getCountersContent();
                if(s2.length() == 0)
                {
                    jgl.Array array1 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getAvailableCounters();
                    if(array1.size() > 0)
                    {
                        s2 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getDefaultCounters();
                    } else
                    {
                        flag = false;
                    }
                }
            }
            if(s2.length() == 0)
            {
                if(flag)
                {
                    s2 = "No Counters selected";
                } else
                {
                    s2 = "No Counters available for this machine";
                }
            }
            if(request.hasValue("counters"))
            {
                s3 = request.getValue("counters");
            } else
            {
                s3 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getCountersParameter(s2, s12);
            }
            if(request.hasValue("counterobjects"))
            {
                s15 = request.getValue("counterobjects");
            } else
            {
                s15 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getCounterObjects();
            }
            if(s15.length() > 0)
            {
                s15 = java.net.URLEncoder.encode(s15);
            }
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("returnURL", ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).getReturnURL());
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", s3);
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("counterobjects", s15);
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("server", s12);
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("operation", s);
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("group", s5);
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("account", request.getAccount());
            s7 = ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("id", s10);
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("SNMP"))
        {
            String s13 = atomicmonitor.getProperty(COM.dragonflow.SiteView.ServerMonitor.pMachineName);
            s2 = "";
            if(request.hasValue("counters"))
            {
                s2 = getAssociatedCountersContent(atomicmonitor, s13);
                ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s2);
            } else
            {
                s2 = ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).getCountersContent();
                if(s2.length() == 0)
                {
                    COM.dragonflow.SiteView.SNMPBase _tmp1 = (COM.dragonflow.SiteView.SNMPBase)atomicmonitor;
                    COM.dragonflow.SiteView.SNMPBase _tmp2 = (COM.dragonflow.SiteView.SNMPBase)atomicmonitor;
                    s2 = COM.dragonflow.SiteView.SNMPBase.getTemplateContent(COM.dragonflow.SiteView.SNMPBase.getTemplatePath(), ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).getTemplateFile(), false);
                }
                if(s2.length() == 0)
                {
                    s2 = "No Default SNMP values";
                }
            }
            if(request.hasValue("counters"))
            {
                s3 = request.getValue("counters");
            } else
            {
                s3 = ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).getCountersParameter(s2, s13);
            }
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("returnURL", ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).getReturnURL());
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", s3);
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("server", s13);
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("operation", s);
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("group", s5);
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("account", request.getAccount());
            s7 = ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("id", s10);
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("URLContent"))
        {
            String s14 = atomicmonitor.getProperty(COM.dragonflow.SiteView.ServerMonitor.pMachineName);
            s2 = "";
            if(request.hasValue("counters"))
            {
                s2 = getAssociatedCountersContent(atomicmonitor, s14);
                ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s2);
            } else
            {
                s2 = ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).getCountersContent();
                if(s2.length() == 0)
                {
                    COM.dragonflow.SiteView.URLContentBase _tmp3 = (COM.dragonflow.SiteView.URLContentBase)atomicmonitor;
                    COM.dragonflow.SiteView.URLContentBase _tmp4 = (COM.dragonflow.SiteView.URLContentBase)atomicmonitor;
                    s2 = COM.dragonflow.SiteView.URLContentBase.getTemplateContent(COM.dragonflow.SiteView.URLContentBase.getTemplatePath(), ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).getTemplateFile(), false);
                }
                if(s2.length() == 0)
                {
                    s2 = "No Default Counters";
                }
            }
            if(request.hasValue("counters"))
            {
                s3 = request.getValue("counters");
            } else
            {
                s3 = ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).getCountersParameter(s2, s14);
            }
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("returnURL", ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).getReturnURL());
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", s3);
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("server", s14);
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("operation", s);
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("group", s5);
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("account", request.getAccount());
            s7 = ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("id", s10);
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("MultiContentBase") || atomicmonitor.getClassPropertyString("applicationType").equals("DynamicHealth"))
        {
            s2 = "";
            if(request.hasValue("counters"))
            {
                s2 = getAssociatedCountersContent(atomicmonitor, "");
                ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s2);
            } else
            {
                if(atomicmonitor.getClassPropertyString("applicationType").equals("DynamicHealth"))
                {
                    s2 = ((COM.dragonflow.StandardMonitor.HealthUnixServerMonitor)atomicmonitor).getContentCounters(false);
                } else
                {
                    s2 = ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).getCountersContent();
                    if(s2.length() == 0)
                    {
                        COM.dragonflow.SiteView.MultiContentBase _tmp5 = (COM.dragonflow.SiteView.MultiContentBase)atomicmonitor;
                        COM.dragonflow.SiteView.MultiContentBase _tmp6 = (COM.dragonflow.SiteView.MultiContentBase)atomicmonitor;
                        s2 = COM.dragonflow.SiteView.MultiContentBase.getTemplateContent(COM.dragonflow.SiteView.MultiContentBase.getTemplatePath(), ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).getTemplateFile(), false);
                    }
                }
                if(s2.length() == 0)
                {
                    s2 = "No Default Counters";
                }
            }
            if(request.hasValue("counters"))
            {
                s3 = request.getValue("counters");
            } else
            {
                s3 = ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).getCountersParameter(s2, "");
            }
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("returnURL", ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).getReturnURL());
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", s3);
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("operation", s);
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("group", s5);
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("account", request.getAccount());
            s7 = ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("id", s10);
        }
        COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
        printButtonBar((String)atomicmonitor.getClassProperty("help"), s4, menus1);
        String s16 = (String)atomicmonitor.getClassProperty("classType");
        if(s16 != null && s16.equals("beta"))
        {
            outputStream.println("<hr> <h3 align=center>This Monitor is currently a beta feature of SiteView</h3>We would appreciate your feedback on the usefulness and function of this feature.&nbsp; <p><b>Please send your comments and/or suggestions to <a href=mailto:" + COM.dragonflow.SiteView.Platform.supportEmail + ">" + COM.dragonflow.SiteView.Platform.supportEmail + "</a>," + ". We promise a quick, personal response from one of our engineers." + "</b><hr>");
        }
        outputStream.println("<p><H2>" + s + " " + s8 + " Monitor in Group : <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s4) + ">" + COM.dragonflow.Page.CGI.getGroupName(s4) + "</a></H2>\n");
        String s17 = request.getValue("_health").length() <= 0 ? "" : "<input type=hidden name=_health value=true>\n";
        outputStream.println(getPagePOST("monitor", s) + "<input type=hidden name=group value=\"" + s1 + "\">\n" + "<input type=hidden name=rview value=\"" + request.getValue("rview") + "\">\n" + "<input type=hidden name=detail value=\"" + request.getValue("detail") + "\">\n" + "<input type=hidden name=class value=\"" + request.getValue("class") + "\">\n" + "<input type=hidden name=counters value=\"" + s3 + "\">\n" + s17 + "<input type=hidden name=id value=\"" + request.getValue("id") + "\">\n");
        if(!hashmap.isEmpty())
        {
            printErrorHeader();
        }
        outputStream.println("<TABLE><tr><td><img src=\"/SiteView/htdocs/artwork/LabelSpacer.gif\"></td><td></td><td></td></tr>");
        boolean flag1 = false;
        if((atomicmonitor instanceof COM.dragonflow.SiteView.IServerPropMonitor) && !COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().internalServerActive() && COM.dragonflow.SiteView.Platform.isNTRemote(atomicmonitor.getProperty(COM.dragonflow.SiteView.ServerMonitor.pMachineName)))
        {
            flag1 = true;
        }
        jgl.Array array2 = atomicmonitor.getProperties();
        array2 = COM.dragonflow.Properties.StringProperty.sortByOrder(array2);
        int i = calculateVariablePropertyCount(array2, atomicmonitor);
        String s18 = atomicmonitor.getProperty("_URLEncoding");
        Enumeration enumeration = array2.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(s18 != null && s18.length() > 0)
            {
                stringproperty.setEncoding(s18);
            }
            if(stringproperty.isEditable && !stringproperty.isAdvanced)
            {
                if(!stringproperty.isVariableCountProperty() || stringproperty.shouldPrintVariableCountProperty(i))
                {
                    printProperty(stringproperty, outputStream, atomicmonitor, request, hashmap, flag1);
                }
            } else
            if(stringproperty.isMultiLine && !stringproperty.isEditable)
            {
                String as[] = COM.dragonflow.Utils.TextUtils.split(s2, stringproperty.multiLineDelimiter);
                atomicmonitor.unsetProperty(stringproperty);
                outputStream.print("<TR><TD ALIGN=RIGHT>" + stringproperty.getLabel() + "</TD>" + "<TD><TABLE border=1 cellspacing=0>");
                for(int j = 0; j < as.length; j++)
                {
                    String s21 = as[j];
                    s21 = atomicmonitor.verify(stringproperty, s21, request, hashmap);
                    outputStream.print("<TR><TD ALIGN=LEFT>" + as[j] + "</TD></TR>");
                    atomicmonitor.addProperty(stringproperty, s21);
                }

                String s20 = (String)hashmap.get(stringproperty);
                if(s20 == null)
                {
                    s20 = "";
                }
                String s22 = "the current selection of counters";
                if(!s2.equals("No Counters available for this machine"))
                {
                    s22 = s7;
                }
                outputStream.println("</TABLE></TD><TD></TD><TD><I>" + s20 + "</I></TD></TR>" + "</TD><TR><TD></TD><TD><FONT SIZE=-1>" + s22 + "</FONT></TD></TR></TR>");
            }
        } while(true);
        String s19 = s;
        if(s.equals("Edit"))
        {
            s19 = "Update";
        }
        outputStream.println("</TABLE><TABLE WIDTH=100%><TR><TD><input type=submit value=" + s19 + "> " + s6 + " Monitor\n" + "</TD></TR></TABLE>" + "<p><HR><CENTER><H3>Advanced Options</H3></CENTER><TABLE>");
        enumeration = array2.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(stringproperty1.isEditable && stringproperty1.isAdvanced && (!stringproperty1.isVariableCountProperty() || stringproperty1.shouldPrintVariableCountProperty(i)))
            {
                printProperty(stringproperty1, outputStream, atomicmonitor, request, hashmap, flag1);
                if(stringproperty1 == COM.dragonflow.SiteView.Monitor.pDescription)
                {
                    printCustomProperties(atomicmonitor);
                }
            }
        } while(true);
        printOrdering(atomicmonitor, array);
        printBaselineInfo(atomicmonitor);
        printThresholds(atomicmonitor, hashmap);
        outputStream.println("</TABLE><TABLE WIDTH=100%><TR><TD><input type=submit value=" + s19 + "> " + s6 + " Monitor\n" + "</TD></TR></TABLE>" + "</FORM>");
        printFooter(outputStream);
        if(atomicmonitor.getClassPropertyString("applicationType").equals("NTCounter"))
        {
            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", "");
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("SNMP"))
        {
            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", "");
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("URLContent"))
        {
            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", "");
        }
        if(atomicmonitor.getClassPropertyString("applicationType").equals("MultiContentBase") || atomicmonitor.getClassPropertyString("applicationType").equals("DynamicHealth"))
        {
            ((COM.dragonflow.SiteView.MultiContentBase)atomicmonitor).replaceDisplayPropertiesForURL("counters", "");
        }
    }

    int saveOrdering(COM.dragonflow.SiteView.Monitor monitor, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        int i = COM.dragonflow.Utils.TextUtils.toInt(httprequest.getValue("ordering"));
        if(i <= 0)
        {
            i = 1;
        }
        return i;
    }

    void printOrdering(COM.dragonflow.SiteView.Monitor monitor, jgl.Array array)
    {
        COM.dragonflow.SiteView.Monitor _tmp = monitor;
        String s = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pID);
        int i = array.size();
        int j = array.size();
        boolean flag = false;
        for(int k = 1; k < array.size(); k++)
        {
            jgl.HashMap hashmap = (jgl.HashMap)array.at(k);
            if(!COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
            {
                continue;
            }
            if(j == array.size())
            {
                j = k;
            }
            if(s.equals(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id")))
            {
                i = k;
            }
        }

        String s1 = "";
        if(i == j)
        {
            s1 = "SELECTED";
            flag = true;
        }
        outputStream.println("<TR>\n<TD ALIGN=RIGHT><B>List Order</B></TD>\n<TD><TABLE><TR><TD ALIGN=LEFT><SELECT name=ordering size=1>\n<OPTION value=" + j + " " + s1 + ">first</OPTION>\n");
        for(int l = 1; l < array.size(); l++)
        {
            if(l == i)
            {
                continue;
            }
            jgl.HashMap hashmap1 = (jgl.HashMap)array.at(l);
            if(!COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap1))
            {
                continue;
            }
            s1 = "";
            if(!flag && l == i + 1)
            {
                s1 = "SELECTED";
                flag = true;
            }
            outputStream.println("<OPTION value=" + l + " " + s1 + "> before " + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_name") + "</OPTION>");
        }

        s1 = "";
        if(!flag || i == array.size())
        {
            s1 = "SELECTED";
        }
        outputStream.println("<OPTION value=" + array.size() + " " + s1 + ">last</OPTION>\n" + "</SELECT></TD></TR>" + "<TR><TD><FONT SIZE=-1>choose where this monitor appears in the list of monitors on the Monitor Detail page</FONT></TD></TR>" + "</TABLE></TD></TR>");
    }

    void printBaselineInfo(COM.dragonflow.SiteView.Monitor monitor)
    {
        if(monitor.hasValue(COM.dragonflow.SiteView.Monitor.pBaselineDate))
        {
            COM.dragonflow.Properties.StringProperty stringproperty = null;
            Enumeration enumeration = monitor.getStatePropertyObjects(false);
            if(enumeration.hasMoreElements())
            {
                stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            }
            String s = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pBaselineMean);
            String s1 = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pBaselineStdDev);
            if(stringproperty != null)
            {
                s = stringproperty.valueString(s, 2);
                s1 = stringproperty.valueString(s1, 2);
            } else
            {
                COM.dragonflow.SiteView.Monitor _tmp = monitor;
                s = COM.dragonflow.SiteView.Monitor.pBaselineMean.valueString(s, 2);
                COM.dragonflow.SiteView.Monitor _tmp1 = monitor;
                s1 = COM.dragonflow.SiteView.Monitor.pBaselineStdDev.valueString(s1, 2);
            }
            int i = COM.dragonflow.Utils.TextUtils.toInt(request.getUserSetting("_timeOffset"));
            COM.dragonflow.SiteView.Monitor _tmp2 = monitor;
            String s2 = COM.dragonflow.Utils.TextUtils.prettyDate(new Date(monitor.getPropertyAsLong(COM.dragonflow.SiteView.Monitor.pBaselineDate) + (long)(i * 1000)));
            outputStream.println("<TR>\n<TD ALIGN=RIGHT><B>Baseline Info</B></TD>\n<TD>Baseline taken on: " + s2 + ". Average:" + s + ". StdDev:" + s1 + "</TD></TR>\n");
        }
    }

    int calculateVariablePropertyCount(jgl.Array array, COM.dragonflow.SiteView.Monitor monitor)
    {
        int i = -1;
        int j = -1;
        int k = -1;
        int l = 0;
        Enumeration enumeration = array.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(stringproperty.isVariablePropertyCountKey)
            {
                if(i == -1)
                {
                    i = stringproperty.minVariablePropertyCount;
                }
                if(j == -1)
                {
                    j = stringproperty.maxVariablePropertyCount;
                }
                if(k == -1 && monitor.getProperty(stringproperty).length() == 0)
                {
                    k = l;
                }
                l++;
            }
        } while(true);
        int i1;
        if(k == -1)
        {
            i1 = l;
        } else
        {
            i1 = k + 2;
        }
        if(i != -1 && i1 < i)
        {
            i1 = i;
        }
        if(j != -1 && i1 > j)
        {
            i1 = j;
        }
        return i1;
    }

    public boolean isMultipleTreshold(COM.dragonflow.SiteView.Monitor monitor)
    {
        if((monitor instanceof COM.dragonflow.SiteView.AtomicMonitor) && ((COM.dragonflow.SiteView.AtomicMonitor)monitor).isMultiThreshold())
        {
            return true;
        }
        return monitor.getClassProperty("class").equals("URLSequenceMonitor");
    }

    void printThresholds(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, jgl.HashMap hashmap)
    {
        boolean flag = atomicmonitor.hasValue(COM.dragonflow.SiteView.Monitor.pBaselineDate);
        Enumeration enumeration = atomicmonitor.getProperties().elements();
        Enumeration enumeration1 = atomicmonitor.getStatePropertyObjects(false);
        boolean flag1 = false;
        boolean flag2 = false;
        byte byte0 = ((byte)(!atomicmonitor.isMultiThreshold() && !atomicmonitor.getClassProperty("class").equals("URLSequenceMonitor") ? 1 : 10));
        boolean flag3 = false;
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(!stringproperty.isThreshold())
            {
                continue;
            }
            if(!flag)
            {
                COM.dragonflow.SiteView.AtomicMonitor _tmp = atomicmonitor;
                if(stringproperty == COM.dragonflow.SiteView.AtomicMonitor.pNumStdDev)
                {
                    continue;
                }
                COM.dragonflow.SiteView.AtomicMonitor _tmp1 = atomicmonitor;
                if(stringproperty == COM.dragonflow.SiteView.AtomicMonitor.pNumPercent)
                {
                    continue;
                }
            }
            flag1 = true;
        } while(true);
        if(!flag1 && !atomicmonitor.isMultiThreshold())
        {
            return;
        }
        String as[] = {
            "error", "warning", "good"
        };
        for(int j = 0; j < as.length; j++)
        {
            if(flag3)
            {
                java.lang.System.out.println("\n***" + as[j]);
            }
            Enumeration enumeration3 = atomicmonitor.getClassifiers();
            String s = "SetProperty category " + as[j];
            String as1[] = new String[byte0];
            String s1 = null;
            String s2 = null;
            for(int k = 0; k < byte0; k++)
            {
                as1[k] = null;
            }

            int i = 0;
            do
            {
                if(!enumeration3.hasMoreElements())
                {
                    break;
                }
                COM.dragonflow.SiteView.Rule rule = (COM.dragonflow.SiteView.Rule)enumeration3.nextElement();
                if(i < byte0 && s.equals(rule.getProperty(COM.dragonflow.SiteView.Rule.pAction)))
                {
                    if(flag3)
                    {
                        java.lang.System.out.println(rule.getProperty(COM.dragonflow.SiteView.Rule.pAction) + " " + rule.getProperty(COM.dragonflow.SiteView.Rule.pExpression));
                    }
                    as1[i] = rule.getProperty(COM.dragonflow.SiteView.Rule.pExpression);
                    if(rule.getOwner() != atomicmonitor && s1 == null)
                    {
                        if(i > 0)
                        {
                            as1[i] = null;
                        }
                        if(s2 == null)
                        {
                            s2 = rule.getProperty(COM.dragonflow.SiteView.Rule.pExpression);
                        }
                        if(rule.isDefaultRule)
                        {
                            s1 = rule.getProperty(COM.dragonflow.SiteView.Rule.pExpression);
                        }
                    }
                    if(flag3)
                    {
                        java.lang.System.out.println("current: " + as1[i]);
                    }
                    if(flag3)
                    {
                        java.lang.System.out.println("default: " + s1);
                    }
                    if(flag3)
                    {
                        java.lang.System.out.println("firstDefault: " + s2);
                    }
                    i++;
                }
            } while(true);
            for(int l = 0; l < byte0; l++)
            {
                if(as1[l] == null)
                {
                    as1[l] = "none";
                }
            }

            if(flag3)
            {
                for(int i1 = 0; i1 < byte0; i1++)
                {
                    java.lang.System.out.println("current" + i1 + ": " + as1[i1]);
                }

            }
            if(s1 == null && s2 != null)
            {
                s1 = s2;
            } else
            if(s1 == null)
            {
                s1 = "none";
            }
            String s3 = "";
            boolean flag4 = false;
            if(as1[0] != null && as1[0].equals(s1))
            {
                s3 = "SELECTED";
                flag4 = true;
            }
            for(int j1 = 0; j1 < byte0; j1++)
            {
                outputStream.print("<TR>\n<TD ALIGN=RIGHT><B>");
                if(j1 == 0)
                {
                    outputStream.print(COM.dragonflow.Utils.TextUtils.toInitialUpper(as[j]) + "</B> if");
                } else
                {
                    outputStream.print("</B>&nbsp;");
                }
                outputStream.print("</TD>\n<TD>\n<TABLE ><TR>\n<TD ALIGN=LEFT><select name=" + as[j] + "-condition" + j1 + " size=1>\n");
                if(j1 != 0)
                {
                    outputStream.print("<OPTION value=none>n/a</OPTION>");
                }
                outputStream.print("<OPTION " + s3 + " value=default>" + s1 + " (default)</OPTION>\n");
                String as2[] = new String[0];
                if(as1[j1] != null)
                {
                    as2 = COM.dragonflow.Utils.TextUtils.tokenize(as1[j1]);
                }
                String s4 = "";
                if(atomicmonitor.isMultiThreshold())
                {
                    COM.dragonflow.Properties.StringProperty stringproperty1;
                    for(Enumeration enumeration2 = atomicmonitor.getStatePropertyObjects(false); enumeration2 != null && enumeration2.hasMoreElements(); outputStream.print("<OPTION " + s3 + " value=\"" + stringproperty1.getName() + "\">" + atomicmonitor.GetPropertyLabel(stringproperty1, true) + "</OPTION>"))
                    {
                        stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration2.nextElement();
                        if(!flag4 && as2.length > 2 && as2[0].equals(stringproperty1.getName()))
                        {
                            s3 = "SELECTED";
                            s4 = as2[2];
                        } else
                        {
                            s3 = "";
                        }
                    }

                } else
                {
                    Enumeration enumeration4 = atomicmonitor.getProperties().elements();
                    do
                    {
                        if(!enumeration4.hasMoreElements())
                        {
                            break;
                        }
                        COM.dragonflow.Properties.StringProperty stringproperty2 = (COM.dragonflow.Properties.StringProperty)enumeration4.nextElement();
                        s3 = "";
                        if(!flag)
                        {
                            COM.dragonflow.SiteView.AtomicMonitor _tmp2 = atomicmonitor;
                            if(stringproperty2 == COM.dragonflow.SiteView.AtomicMonitor.pNumStdDev)
                            {
                                continue;
                            }
                            COM.dragonflow.SiteView.AtomicMonitor _tmp3 = atomicmonitor;
                            if(stringproperty2 == COM.dragonflow.SiteView.AtomicMonitor.pNumPercent)
                            {
                                continue;
                            }
                        }
                        if(!flag4 && as2.length > 2 && as2[0].equals(stringproperty2.getName()))
                        {
                            s3 = "SELECTED";
                            s4 = as2[2];
                        }
                        if(stringproperty2.isThreshold())
                        {
                            String s6 = atomicmonitor.GetPropertyLabel(stringproperty2, true);
                            if(s6.length() != 0)
                            {
                                outputStream.print("<OPTION " + s3 + " value=\"" + stringproperty2.getName() + "\">" + s6 + "</OPTION>");
                            }
                        }
                    } while(true);
                }
                String s5 = "";
                if(as2.length > 1)
                {
                    s5 = as2[1];
                }
                String as3[] = {
                    ">=", "&gt;=", ">", "&gt;", "==", "==", "!=", "!=", "<", "&lt;", 
                    "<=", "&lt;=", "contains", "contains", "doesNotContain", "!contains"
                };
                outputStream.print("</select>\n<select name=" + as[j] + "-comparison" + j1 + " size=1>");
                for(int k1 = 0; k1 < as3.length; k1 += 2)
                {
                    s3 = "";
                    if(as3[k1].equals(s5))
                    {
                        s3 = "SELECTED";
                    }
                    outputStream.print("<OPTION " + s3 + " value=\"" + as3[k1] + "\">" + as3[k1 + 1] + "</OPTION>");
                }

                outputStream.print("</select>\n<input type=text name=" + as[j] + "-parameter" + j1 + " size=5 value=\"" + s4 + "\"></TD><td> Enter number or single quoted text</td></TR>\n" + "</TABLE>\n" + "</TD>\n" + "<TD><I>");
                String s7 = (String)hashmap.get(as[j] + "-parameter" + j1);
                if(s7 != null)
                {
                    outputStream.print(s7);
                }
                outputStream.print("</I></TD>\n</TR>\n");
            }

        }

    }

    void saveThresholds(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap)
    {
        atomicmonitor.unsetProperty("_classifier");
        String as[] = {
            "error", "warning", "good"
        };
        boolean flag = false;
        byte byte0;
        if(atomicmonitor.isMultiThreshold())
        {
            byte0 = 10;
        } else
        {
            byte0 = 1;
        }
        if(atomicmonitor.getClassProperty("class").equals("URLSequenceMonitor"))
        {
            byte0 = 10;
        }
        for(int i = 0; i < as.length; i++)
        {
            if(flag)
            {
                java.lang.System.out.println("i: " + i);
            }
            if(flag)
            {
                java.lang.System.out.println(as[i]);
            }
            for(int j = 0; j < byte0; j++)
            {
                if(!httprequest.hasValue(as[i] + "-condition" + j))
                {
                    continue;
                }
                String s = httprequest.getValue(as[i] + "-condition" + j);
                if(s.equals("default") || s.equals("none"))
                {
                    continue;
                }
                String s1 = httprequest.getValue(as[i] + "-comparison" + j);
                String s2 = httprequest.getValue(as[i] + "-parameter" + j);
                if(flag)
                {
                    java.lang.System.out.println(s + " " + s1 + " " + s2);
                }
                if(s2.trim().length() == 0)
                {
                    hashmap.put(as[i] + "-parameter" + j, "threshold value was missing");
                }
                try
                {
                    if(!s2.startsWith("'"))
                    {
                        java.lang.Float.valueOf(s2);
                    }
                }
                catch(java.lang.NumberFormatException numberformatexception)
                {
                    hashmap.put(as[i] + "-parameter" + j, "threshold value was not a number; may need single quotes");
                }
                String s3 = s + " " + s1 + " " + s2 + "\t" + as[i];
                if(flag)
                {
                    java.lang.System.out.println("Classifier to add: " + s3);
                }
                atomicmonitor.addProperty("_classifier", s3);
                COM.dragonflow.SiteView.Rule rule = COM.dragonflow.SiteView.Rule.stringToClassifier(s3);
                if(rule != null)
                {
                    atomicmonitor.addElement(rule);
                }
            }

        }

    }

    void printCustomProperties(COM.dragonflow.SiteView.Monitor monitor)
    {
        jgl.HashMap hashmap = getMasterConfig();
        Enumeration enumeration = hashmap.values("_monitorEditCustom");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            String as[] = COM.dragonflow.Utils.TextUtils.split(s, "|");
            String s1 = as[0];
            if(s1.length() > 0)
            {
                if(!s1.startsWith("_"))
                {
                    s1 = "_" + s1;
                }
                if(shouldPrintProperty(monitor, s1))
                {
                    String s2 = "";
                    String s3 = "";
                    String s4 = "";
                    String s5 = "<INPUT TYPE=TEXT NAME=$NAME$ SIZE=50 VALUE=\"$VALUE$\">\n";
                    if(as.length > 1)
                    {
                        s2 = as[1];
                        if(as.length > 2)
                        {
                            s3 = as[2];
                            if(as.length > 3)
                            {
                                s4 = as[3];
                                if(as.length > 4)
                                {
                                    s5 = as[4];
                                }
                            }
                        }
                    }
                    s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, NAME_MARKER, s1);
                    s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, NAME_MARKER.toLowerCase(), s1);
                    if(s4.equals("") || !monitor.getProperty(s1).equals(""))
                    {
                        s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, VALUE_MARKER, monitor.getProperty(s1));
                        s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, VALUE_MARKER.toLowerCase(), monitor.getProperty(s1));
                    } else
                    {
                        s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, VALUE_MARKER, s4);
                    }
                    outputStream.println("<TR><TD ALIGN=RIGHT>" + s2 + "</TD><TD><TABLE>\n" + "<TR><TD ALIGN=LEFT>" + s5 + "</TD></TR><TR><TD><FONT SIZE=-1>" + s3 + "</FONT>\n" + "</TD></TR></TABLE></TD><TD><I></I></TD></TR>\n");
                }
            }
        } while(true);
    }

    void saveCustomProperties(COM.dragonflow.SiteView.Monitor monitor, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        jgl.HashMap hashmap = getMasterConfig();
        Enumeration enumeration = hashmap.values("_monitorEditCustom");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            String as[] = COM.dragonflow.Utils.TextUtils.split(s, "|");
            String s1 = as[0];
            if(s1.length() > 0)
            {
                if(!s1.startsWith("_"))
                {
                    s1 = "_" + s1;
                }
                if(shouldPrintProperty(monitor, s1))
                {
                    String s2 = httprequest.getValue(s1);
                    s2 = s2.replace('\r', ' ');
                    s2 = s2.replace('\n', ' ');
                    monitor.setProperty(s1, httprequest.getValue(s1));
                }
            }
        } while(true);
    }

    void initializeTemplateTable(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        String s = request.getValue("group");
        String s1 = COM.dragonflow.Utils.I18N.UnicodeToString(s, COM.dragonflow.Utils.I18N.nullEncoding());
        s1 = COM.dragonflow.Utils.I18N.StringToUnicode(s1, "");
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSiteView();
        COM.dragonflow.SiteView.SiteViewObject siteviewobject1 = siteviewobject.getElement(COM.dragonflow.Page.monitorPage.getGroupIDRelative(s1));
        if(siteviewobject1 != null)
        {
            atomicmonitor.setOwner(siteviewobject1);
        }
        atomicmonitor.initializeTemplate();
    }

    String MonitorUpdateProperty(COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, COM.dragonflow.Properties.StringProperty stringproperty, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        return null;
    }

    void printAddForm(String s, String s1, String s2)
        throws java.lang.Exception
    {
label0:
        {
            jgl.Array array;
            COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
label1:
            {
label2:
                {
                    array = ReadGroupFrames(s2);
                    if(s.equals("Add"))
                    {
                        atomicmonitor = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(request.getValue("class"), request);
                    } else
                    {
                        atomicmonitor = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(array, request.getValue("id"), request.getPortalServer(), request);
//                        if(!COM.dragonflow.SiteView.TopazConfigurator.modMonitors.contains(atomicmonitor))
//                        {
//                            COM.dragonflow.SiteView.TopazConfigurator.modMonitors.add(atomicmonitor);
//                        }
                    }
                    initializeTemplateTable(atomicmonitor);
                    String s3 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
                    if(s3.equals("optional"))
                    {
                        printNotAvailable(atomicmonitor, s1);
                        return;
                    }
                    COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                    if(s.equals("Add"))
                    {
                        COM.dragonflow.SiteView.SiteViewObject siteviewobject = COM.dragonflow.SiteView.Portal.getSiteViewForID(s2);
                        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)siteviewobject.getElement(s2);
                        if(COM.dragonflow.Utils.TextUtils.toInt(s3) > 0)
                        {
                            int j = monitor.countMonitorsOfClass(request.getValue("class"), request.getAccount());
                            if(j >= COM.dragonflow.Utils.TextUtils.toInt(s3))
                            {
                                printLicenseExceeded(atomicmonitor, s1, COM.dragonflow.Utils.TextUtils.toInt(s3), "class");
                                return;
                            }
                        }
                        int k = request.getPermissionAsInteger("_maximumMonitors");
                        if(k > 0)
                        {
                            int i1 = monitor.countMonitors(request.getAccount());
                            if(i1 >= k)
                            {
                                printLicenseExceeded(atomicmonitor, s1, k, "group");
                                return;
                            }
                        }
                        if(COM.dragonflow.Utils.LUtils.wouldExceedLimit(atomicmonitor))
                        {
                            int j1 = COM.dragonflow.Utils.LUtils.getLicensedPoints();
                            printLicenseExceeded(atomicmonitor, s1, j1, "license");
                            return;
                        }
                        if(!COM.dragonflow.Utils.LUtils.isMonitorTypeAllowed(atomicmonitor))
                        {
                            printLicenseExceeded(atomicmonitor, s1, 0, "class");
                            return;
                        }
                    }
                    int i = request.getPermissionAsInteger("_minimumFrequency");
                    String s4 = atomicmonitor.getProperty("_encoding");
                    if(s4.length() == 0)
                    {
                        atomicmonitor.setProperty("_encoding", COM.dragonflow.Utils.I18N.nullEncoding());
                    }
                    if(s.equals("Add") && (!request.isPost() || !COM.dragonflow.Page.treeControl.notHandled(request)))
                    {
                        if(atomicmonitor.isDispatcher())
                        {
                            siteviewgroup.checkDispatcherForStart(atomicmonitor);
                        }
                        long l = atomicmonitor.getPropertyAsLong(COM.dragonflow.SiteView.AtomicMonitor.pFrequency);
                        java.lang.Object obj = atomicmonitor.getClassProperty("defaultFrequency");
                        if(obj instanceof String)
                        {
                            long l1 = COM.dragonflow.Utils.TextUtils.toInt((String)obj);
                            if(l1 > 0L)
                            {
                                l = l1;
                            }
                        }
                        if(i > 0 && l < (long)i)
                        {
                            l = i;
                        }
                        atomicmonitor.setProperty(COM.dragonflow.SiteView.AtomicMonitor.pFrequency, l);
                    }
                    if(!request.isPost())
                    {
                        break label1;
                    }
                    String s5 = "";
                    String s6 = atomicmonitor.defaultTitle();
                    jgl.HashMap hashmap = new HashMap();
                    String s7 = COM.dragonflow.SiteView.ServerMonitor.pMachineName.getName();
                    if(request.hasValue(s7))
                    {
                        COM.dragonflow.Properties.StringProperty stringproperty = atomicmonitor.getPropertyObject(s7);
                        atomicmonitor.unsetProperty(stringproperty);
                        atomicmonitor.setProperty(stringproperty, request.getValue(s7));
                    }
                    if(request.hasValue("counters"))
                    {
                        s5 = getAssociatedCountersContent(atomicmonitor, atomicmonitor.getProperty(COM.dragonflow.SiteView.ServerMonitor.pMachineName));
                        if(atomicmonitor.getClassPropertyString("applicationType").equals("NTCounter"))
                        {
                            if(s5.length() == 0)
                            {
                                s5 = "No Counters available for this machine";
                            }
                            ((COM.dragonflow.SiteView.NTCounterBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s5);
                        }
                        if(atomicmonitor.getClassPropertyString("applicationType").equals("SNMP"))
                        {
                            if(s5.length() == 0)
                            {
                                s5 = "No Counters available for this machine";
                            }
                            ((COM.dragonflow.SiteView.SNMPBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s5);
                        }
                        if(atomicmonitor.getClassPropertyString("applicationType").equals("URLContent"))
                        {
                            if(s5.length() == 0)
                            {
                                s5 = "No Counters available for this machine";
                            }
                            ((COM.dragonflow.SiteView.URLContentBase)atomicmonitor).setCountersPropertyValue(atomicmonitor, s5);
                        }
                    }
                    if(atomicmonitor instanceof COM.dragonflow.SiteView.BrowsableMonitor)
                    {
                        String s8 = request.getValue("uniqueID");
                        jgl.HashMap hashmap1 = COM.dragonflow.SiteView.BrowsableCache.getCache(s8, true, false);
                        jgl.HashMap hashmap2 = (jgl.HashMap)hashmap1.get("permanentSelectNames");
                        jgl.HashMap hashmap3 = (jgl.HashMap)hashmap1.get("permanentSelectIDs");
                        jgl.HashMap hashmap4 = (jgl.HashMap)hashmap1.get("cParms");
                        jgl.Array array2 = ((COM.dragonflow.SiteView.BrowsableMonitor)atomicmonitor).getConnectionProperties();
                        for(int i2 = 0; i2 < array2.size(); i2++)
                        {
                            COM.dragonflow.Properties.StringProperty stringproperty2 = (COM.dragonflow.Properties.StringProperty)array2.at(i2);
                            String s17 = stringproperty2.getName();
                            String s23 = (String)hashmap4.get(s17);
                            if(s23 != null || !stringproperty2.isPassword || !request.getValue(s17).equals("*********"))
                            {
                                atomicmonitor.unsetProperty(s17);
                            }
                            if(s23 != null)
                            {
                                atomicmonitor.setProperty(s17, s23);
                            }
                        }

                        int j2 = 1;
                        String s14 = ((COM.dragonflow.SiteView.BrowsableMonitor)atomicmonitor).getBrowseName();
                        String s18 = ((COM.dragonflow.SiteView.BrowsableMonitor)atomicmonitor).getBrowseID();
                        java.util.HashMap hashmap7 = new java.util.HashMap();
                        for(int k3 = 1; atomicmonitor.hasProperty(s14 + k3); k3++)
                        {
                            String s29 = atomicmonitor.getProperty(s18 + k3);
                            if(s29.length() > 0)
                            {
                                hashmap7.put(s29, new Integer(k3));
                            }
                            atomicmonitor.unsetProperty(s14 + k3);
                            atomicmonitor.unsetProperty(s18 + k3);
                        }

                        Enumeration enumeration3 = hashmap2.keys();
                        String s30 = "";
                        boolean flag = (atomicmonitor instanceof COM.dragonflow.SiteView.BrowsableBase) && ((COM.dragonflow.SiteView.BrowsableBase)atomicmonitor).isSortedCounters();
                        if(flag)
                        {
                            COM.datachannel.xml.om.Document document = COM.dragonflow.SiteView.BrowsableCache.getXml(s8);
                            if(document.getDocumentElement() == null)
                            {
                                StringBuffer stringbuffer = new StringBuffer();
                                document.loadXML(((COM.dragonflow.SiteView.BrowsableMonitor)atomicmonitor).getBrowseData(stringbuffer));
                            }
                            jgl.Array array6 = new Array();
                            for(; enumeration3.hasMoreElements(); array6.add(enumeration3.nextElement())) { }
                            jgl.Sorting.sort(array6, new GreaterCounterByXml(document));
                            for(int j4 = 0; j4 < array6.size(); j4++)
                            {
                                String s31 = (String)array6.at(j4);
                                if(setCounerProperty(s14, s31, hashmap3, atomicmonitor, j2, s18))
                                {
                                    j2++;
                                }
                            }

                        } else
                        {
                            do
                            {
                                if(!enumeration3.hasMoreElements())
                                {
                                    break;
                                }
                                String s32 = (String)enumeration3.nextElement();
                                if(setCounerProperty(s14, s32, hashmap3, atomicmonitor, j2, s18))
                                {
                                    j2++;
                                }
                            } while(true);
                        }
                        java.util.HashMap hashmap8 = new java.util.HashMap();
                        int i4 = atomicmonitor.getMaxCounters();
                        for(int k4 = 1; k4 <= i4; k4++)
                        {
                            if(!atomicmonitor.hasProperty(s18 + k4))
                            {
                                continue;
                            }
                            String s38 = atomicmonitor.getProperty(s18 + k4);
                            if(s38.length() <= 0)
                            {
                                continue;
                            }
                            java.lang.Integer integer = (java.lang.Integer)hashmap7.get(s38);
                            if(integer != null)
                            {
                                hashmap8.put(integer, new Integer(k4));
                            }
                        }

                        String as1[] = {
                            "error", "warning", "good"
                        };
                        Enumeration enumeration6 = request.getVariables();
                        java.util.HashMap hashmap9 = new java.util.HashMap();
                        String s39;
                        for(; enumeration6.hasMoreElements(); hashmap9.put(s39, request.getValue(s39)))
                        {
                            s39 = (String)enumeration6.nextElement();
                        }

                        byte byte0 = 1;
                        if(atomicmonitor.isMultiThreshold())
                        {
                            byte0 = 10;
                        }
                        for(int i5 = 0; i5 < as1.length; i5++)
                        {
                            for(int j5 = 0; j5 < byte0; j5++)
                            {
                                String s40 = as1[i5] + "-condition" + j5;
                                String s41 = (String)hashmap9.get(s40);
                                if(s41 == null || !s41.startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE))
                                {
                                    continue;
                                }
                                int k5 = java.lang.Integer.parseInt(s41.substring(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE.length()));
                                java.lang.Integer integer1 = (java.lang.Integer)hashmap8.get(new Integer(k5));
                                if(integer1 == null)
                                {
                                    request.unsetValue(s40);
                                } else
                                {
                                    String s42 = COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE + integer1.intValue();
                                    request.setValue(s40, s42);
                                }
                            }

                        }

                    }
                    jgl.Array array1 = atomicmonitor.getProperties();
                    array1 = COM.dragonflow.Properties.StringProperty.sortByOrder(array1);
                    Enumeration enumeration = array1.elements();
                    COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
                    String s9 = schedulemanager.getScheduleIdFromMonitor(atomicmonitor);
                    do
                    {
                        if(!enumeration.hasMoreElements())
                        {
                            break;
                        }
                        COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                        if(!atomicmonitor.propertyInTemplate(stringproperty1))
                        {
                            if(stringproperty1.isEditable)
                            {
                                if(stringproperty1.isMultiLine)
                                {
                                    String s10 = request.getValue(stringproperty1.getName());
                                    String as[] = COM.dragonflow.Utils.TextUtils.split(s10, "\r\n");
                                    atomicmonitor.unsetProperty(stringproperty1);
                                    int k2 = 0;
                                    while(k2 < as.length) 
                                    {
                                        String s19 = as[k2];
                                        s19 = atomicmonitor.verify(stringproperty1, s19, request, hashmap);
                                        atomicmonitor.addProperty(stringproperty1, s19);
                                        k2++;
                                    }
                                } else
                                {
                                    String s11 = request.getValue(stringproperty1.getName());
                                    String s13 = stringproperty1.getName();
                                    if((s13.equals("_getImages") || s13.equals("_getFrames")) && s11.equals("on") && request.isSiteSeerAccount())
                                    {
                                        int l2 = request.getPermissionAsInteger("_maximumFAndIMonitors");
                                        String s20 = request.getValue("operation");
                                        String s24 = request.getValue("id");
                                        String s26 = request.getValue("class");
                                        if(s26.equals("") && !s24.equals(""))
                                        {
                                            String s33 = s2 + COM.dragonflow.SiteView.SiteViewObject.ID_SEPARATOR + s24;
                                            COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor)siteviewgroup.getElement(s33);
                                            s26 = monitor1.getProperty("_class");
                                        }
                                        if(s26.startsWith("URLRemote"))
                                        {
                                            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(request.getAccount());
                                            String s36 = monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pGroupID);
                                            jgl.Array array5 = monitorgroup.getMonitorsOfClass("", s36);
                                            Enumeration enumeration5 = array5.elements();
                                            int l4 = 0;
                                            while (enumeration5.hasMoreElements())
                                                {
                                                COM.dragonflow.SiteView.Monitor monitor2 = (COM.dragonflow.SiteView.Monitor)enumeration5.nextElement();
                                                if(!s20.equals("Edit"))
                                                {
                                                    // XXX
                                                    // COM.dragonflow.StandardMonitor.URLRemoteMonitor
                                                    // does not exist.
                                                    // XXX
                                                    // COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor
                                                    // does not exist.
//                                                    if(((monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteMonitor) || (monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor)) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
//                                                    {
//                                                        l4++;
//                                                    }
                                                    continue;
                                                }
                                                if(monitor2.getProperty("id").equals(request.getValue("id")) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
                                                {
                                                    break;
                                                }
//                                                if(((monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteMonitor) || (monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor)) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
//                                                {
//                                                    l4++;
//                                                }
                                            } 
                                            if(l4 >= l2)
                                            {
                                                printTooManyFAndIMonitors(s1, l2);
                                                return;
                                            }
                                        }
                                    }
                                    if((stringproperty1 instanceof COM.dragonflow.Properties.treeProperty) && COM.dragonflow.Page.treeControl.useTree())
                                    {
                                        s11 = s11.equals("_none") ? "" : COM.dragonflow.HTTP.HTTPRequest.decodeString(s11);
                                    } else
                                    if(stringproperty1 instanceof COM.dragonflow.Properties.ScalarProperty)
                                    {
                                        s11 = ((COM.dragonflow.Properties.ScalarProperty)stringproperty1).getRawValue(request);
                                    }
                                    String s15 = s11;
                                    if(!COM.dragonflow.Utils.TextUtils.isSubstituteExpression(s11))
                                    {
                                        s15 = atomicmonitor.verify(stringproperty1, s11, request, hashmap);
                                    }
                                    if(stringproperty1.isPassword && s11.equals("*********"))
                                    {
                                        if(atomicmonitor.getProperty(stringproperty1).length() == 0 && request.getValue("hidden" + stringproperty1.getName()).length() > 0)
                                        {
                                            s15 = COM.dragonflow.Utils.TextUtils.enlighten(request.getValue("hidden" + stringproperty1.getName()));
                                        } else
                                        {
                                            s15 = atomicmonitor.getProperty(stringproperty1);
                                            if(s15.trim().length() == 0)
                                            {
                                                s15 = COM.dragonflow.Properties.StringProperty.getPrivate(request, stringproperty1.getName(), COM.dragonflow.Utils.TextUtils.obscure(stringproperty1.getName()).substring(5, COM.dragonflow.Utils.TextUtils.obscure(stringproperty1.getName()).length()), null, null);
                                            }
                                        }
                                    }
                                    if(i > 0 && (stringproperty1 instanceof COM.dragonflow.Properties.FrequencyProperty))
                                    {
                                        int i3 = COM.dragonflow.Utils.TextUtils.toInt(s15);
                                        if(i3 != 0 && i3 < i)
                                        {
                                            hashmap.put(stringproperty1, "For this account, monitors must run at intervals of " + COM.dragonflow.Utils.TextUtils.secondsToString(i) + " or more.");
                                        }
                                    }
                                    COM.dragonflow.SiteView.AtomicMonitor _tmp = atomicmonitor;
                                    if(stringproperty1 == COM.dragonflow.SiteView.AtomicMonitor.pName)
                                    {
                                        if(s15.equals(s6))
                                        {
                                            atomicmonitor.setProperty(stringproperty1, "");
                                        } else
                                        {
                                            atomicmonitor.setProperty(stringproperty1, s15);
                                        }
                                    } else
                                    if((stringproperty1 instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty1).multiple)
                                    {
                                        jgl.Array array4 = new Array();
                                        if(!COM.dragonflow.SiteView.Platform.isStandardAccount(request.getAccount()) && stringproperty1.getName().equals("_location"))
                                        {
                                            Enumeration enumeration1 = atomicmonitor.getMultipleValues(stringproperty1);
                                            String s27 = "";
                                            String s34 = "";
                                            do
                                            {
                                                if(!enumeration1.hasMoreElements())
                                                {
                                                    break;
                                                }
                                                String s28 = (String)enumeration1.nextElement();
                                                String s35 = (String)COM.dragonflow.Utils.HTTPUtils.locationMap.get(s28);
                                                if(s35 != null && !s35.equals(""))
                                                {
                                                    int l3 = COM.dragonflow.Utils.HTTPUtils.getDisplayOrder(s35);
                                                    if(l3 < 0)
                                                    {
                                                        String s37 = atomicmonitor.getSetting("_allowInvalidNode");
                                                        if(s37 == null || s37.indexOf(s28) < 0)
                                                        {
                                                            array4.add(s28);
                                                        }
                                                    }
                                                }
                                            } while(true);
                                        }
                                        Enumeration enumeration2 = request.getValues(stringproperty1.getName());
                                        atomicmonitor.unsetProperty(stringproperty1);
                                        for(; enumeration2.hasMoreElements(); atomicmonitor.addProperty(stringproperty1, (String)enumeration2.nextElement())) { }
                                        if(array4 != null && array4.size() > 0)
                                        {
                                            Enumeration enumeration4 = array4.elements();
                                            while(enumeration4.hasMoreElements()) 
                                            {
                                                atomicmonitor.addProperty(stringproperty1, (String)enumeration4.nextElement());
                                            }
                                        }
                                    } else
                                    {
                                        String s21 = atomicmonitor.getProperty(stringproperty1);
                                        atomicmonitor.setProperty(stringproperty1, s15);
                                        if(stringproperty1.getName().equals("_disabled"))
                                        {
                                            if(s21.length() <= 0 && s15.length() > 0)
                                            {
                                                siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                                            } else
                                            if(s21.length() > 0 && s15.length() <= 0)
                                            {
                                                siteviewgroup.checkDispatcherForStart(atomicmonitor);
                                            }
                                        }
                                    }
                                }
                            } else
                            if(stringproperty1.getName().equals("_counters"))
                            {
                                atomicmonitor.unsetProperty(stringproperty1);
                                String s12 = s5;
                                s12 = atomicmonitor.verify(stringproperty1, s12, request, hashmap);
                                atomicmonitor.addProperty(stringproperty1, s12);
                            }
                        }
                    } while(true);
                    if(hashmap.size() == 0)
                    {
                        atomicmonitor.verifyAll(hashmap);
                    }
                    saveThresholds(atomicmonitor, request, hashmap);
                    saveCustomProperties(atomicmonitor, request);
                    int k1 = saveOrdering(atomicmonitor, request);
                    if(hashmap.size() != 0 || !COM.dragonflow.Page.treeControl.notHandled(request))
                    {
                        if(request.isPost() && request.rawURL.indexOf("?") == -1)
                        {
                            request.rawURL += "?page=" + request.getValue("page");
                            request.rawURL += "&operation=" + request.getValue("operation");
                            request.rawURL += "&class=" + request.getValue("class");
                            request.rawURL += "&group=" + request.getValue("group");
                            request.rawURL += "&id=" + request.getValue("id");
                        }
                        if(!COM.dragonflow.Page.treeControl.notHandled(request))
                        {
                            hashmap = new HashMap();
                        }
                        printForm(s, s1, atomicmonitor, hashmap, array);
                        break label0;
                    }
                    COM.dragonflow.SiteView.AtomicMonitor _tmp1 = atomicmonitor;
                    if(atomicmonitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName).length() == 0)
                    {
                        COM.dragonflow.SiteView.AtomicMonitor _tmp2 = atomicmonitor;
                        atomicmonitor.setProperty(COM.dragonflow.SiteView.AtomicMonitor.pName, atomicmonitor.defaultTitle());
                    }
                    jgl.HashMap hashmap5 = atomicmonitor.getValuesTable();
                    jgl.Array array3 = ReadGroupFrames(s2);
                    String s16 = "";
                    if(s.equals("Add"))
                    {
                        jgl.HashMap hashmap6 = (jgl.HashMap)array3.at(0);
                        s16 = COM.dragonflow.Utils.TextUtils.getValue(hashmap6, "_nextID");
                        if(s16.length() == 0)
                        {
                            s16 = "1";
                        }
                        hashmap5.remove("_id");
                        atomicmonitor.setProperty(COM.dragonflow.SiteView.Monitor.pID, s16);
                        hashmap5.put("_class", request.getValue("class"));
                        array3.insert(k1, hashmap5);
                        String s25 = COM.dragonflow.Utils.TextUtils.increment(s16);
                        hashmap6.put("_nextID", s25);
                    } else
                    {
                        s16 = request.getValue("id");
                        int j3 = COM.dragonflow.SiteView.monitorUtils.findMonitorIndex(array3, s16);
                        java.lang.Object obj1 = array3.at(j3);
                        array3.insert(k1, hashmap5);
                        array3.remove(obj1);
                    }
                    if(!request.isPost() || !COM.dragonflow.Page.treeControl.notHandled(request))
                    {
                        break label0;
                    }
//                    if(COM.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings().isConnected() && atomicmonitor.isDispatcher())
//                    {
//                        COM.dragonflow.SiteView.DispatcherMonitor.notifyDispatcherMonitor(COM.dragonflow.SiteView.DispatcherMonitor.EDIT_MON, atomicmonitor);
//                    }
                    WriteGroupFrames(s2, array3);
                    if(s.equals("Add"))
                    {
                        schedulemanager.addMonitorToScheduleObject(atomicmonitor);
                    } else
                    {
                        schedulemanager.updateMonitorInScheduleObject(s9, atomicmonitor);
                    }
                    if(s.equals("Edit") || s.equals("Add"))
                    {
                        COM.dragonflow.SiteView.AtomicMonitor _tmp3 = atomicmonitor;
                        if(atomicmonitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pDisabled).length() == 0 && atomicmonitor.getPropertyAsLong(COM.dragonflow.SiteView.AtomicMonitor.pFrequency) != 0L && COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().internalServerActive())
                        {
                            autoFollowPortalRefresh = false;
                            if(isPortalServerRequest())
                            {
                                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
                            }
                            printRefreshPage(getPageLink("monitor", "RefreshMonitor") + "&group=" + COM.dragonflow.HTTP.HTTPRequest.encodeString(s2) + "&id=" + s16, 0);
                            break label2;
                        }
                    }
                    COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s2);
                    printReturnRefresh(COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2), 0);
                }
                if(request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request) && (atomicmonitor instanceof COM.dragonflow.SiteView.BrowsableMonitor))
                {
                    String s22 = request.getValue("uniqueID");
                    COM.dragonflow.SiteView.BrowsableCache.getCache(s22, false, true);
                }
                break label0;
            }
            printForm(s, s1, atomicmonitor, new HashMap(), array);
        }
    }

    void printNotAvailable(COM.dragonflow.SiteView.Monitor monitor, String s)
    {
        COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
        printButtonBar((String)monitor.getClassProperty("help"), s, menus1);
        String s1 = (String)monitor.getClassProperty("title");
        outputStream.print("<HR>The " + s1 + " Monitor cannot be added with a this account." + "<HR><P><A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s) + ">Return to Group</A>\n");
        printFooter(outputStream);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @param s
     * @param s1
     */
    void printMonitorTools(jgl.Array array, String s, String s1)
    {
        Enumeration enumeration;
        boolean flag;
        java.util.TreeMap treemap;
        enumeration = array.elements();
        flag = false;
        treemap = new TreeMap();

        while (enumeration.hasMoreElements())
        {
        Class class1 = (java.lang.Class)enumeration.nextElement();
        String s3 = (String)COM.dragonflow.SiteView.Monitor.getClassPropertyByObject(class1.getName(), "classType");
        if(s3 == null)
        {
            s3 = "";
        }
        if(!s3.equals(s))
        {
            continue; /* Loop/switch isn't completed */
        }
        try
        {
        COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
        atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)class1.newInstance();
        String s5 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s5.length() == 0)
        {
            s5 = request.getPermission("_monitorType", "default");
        }
        if(s5.equals("hidden"))
        {
            continue; /* Loop/switch isn't completed */
        }
        String s6 = (String)atomicmonitor.getClassProperty("toolPageDisable");
        if(s6 != null)
        {
            continue; /* Loop/switch isn't completed */
        }
        String s7;
        String s8;
        String s9;
        if(!flag)
        {
            flag = true;
            outputStream.println("<BLOCKQUOTE>" + s1 + "\n<TABLE width=90% BORDER=1 cellspacing=0 cellpadding=5>");
        }
        s7 = atomicmonitor.getTestURL();
        s8 = "";
        if(isPortalServerRequest())
        {
            s8 = "&portalserver=" + request.getPortalServer();
        }
        if(s7 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        s9 = (String)atomicmonitor.getClassProperty("toolName");
        if(s9 != null)
        {
                String s10 = (String)atomicmonitor.getClassProperty("toolDescription");
                StringBuffer stringbuffer = new StringBuffer("<TR><TD VALIGN=TOP><A HREF=");
                if(!request.getValue("AWRequest").equals("yes"))
                {
                    stringbuffer.append(s7).append("&account=").append(request.getAccount()).append(s8);
                    stringbuffer.append(">").append(s9).append("</A></TD><TD VALIGN=TOP>");
                    if(s10 != null)
                    {
                        stringbuffer.append(s10);
                    }
                    stringbuffer.append("</TD></TR>");
                    treemap.put(s9, stringbuffer.toString());
                } else
                if(s7.indexOf("DNS") >= 0 || s7.indexOf("ftp") >= 0 || s7.indexOf("ping") >= 0 || s7.indexOf("LDAP") >= 0 || s7.indexOf("news") >= 0)
                {
                    stringbuffer.append(s7).append("&account=").append(request.getAccount());
                    stringbuffer.append("&AWRequest=yes").append(s8).append(">");
                    stringbuffer.append(s9).append("</A></TD><TD VALIGN=TOP>");
                    if(s10 != null)
                    {
                        stringbuffer.append(s10);
                    }
                    stringbuffer.append("</TD></TR>");
                    treemap.put(s9, stringbuffer.toString());
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("<BR>class: " + class1.getName() + " error: " + enumeration);
        }
        }

        java.util.Map.Entry entry;
        for(java.util.Iterator iterator = treemap.entrySet().iterator(); iterator.hasNext(); outputStream.println((String)entry.getValue()))
        {
            entry = (java.util.Map.Entry)iterator.next();
        }

        if(request.getValue("AWRequest").equals("yes") && s.equals(""))
        {
            String s2 = "";
            if(isPortalServerRequest())
            {
                s2 = "&portalserver=" + request.getPortalServer();
            }
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            String s4 = siteviewgroup.getSetting("_disableWebtrace");
            if(s4 == null || s4.length() <= 0)
            {
                outputStream.print("<TR><TD VALIGN=TOP><A HREF=/SiteView/cgi/go.exe/SiteView?page=webTrace&account=" + request.getAccount() + "&AWRequest=yes" + s2 + ">" + "Web Trace" + "</A></TD><TD VALIGN=TOP>");
                outputStream.print("Trace the route of a web site.</TD></TR>");
            }
            outputStream.print("<TR><TD VALIGN=TOP><A HREF=/SiteView/cgi/go.exe/SiteView?page=trace&account=" + request.getAccount() + "&AWRequest=yes" + s2 + ">" + "TraceRoute" + "</A></TD><TD VALIGN=TOP>");
            outputStream.print("Trace the route of a web site.</TD></TR>");
        }
        if(s.equals("advanced"))
        {
            outputStream.print("<TR><TD VALIGN=TOP><A HREF=/SiteView/cgi/go.exe/SiteView?page=regularExpression&account=" + request.getAccount() + "&AWRequest=yes>" + "Regular Expression" + "</A></TD><TD VALIGN=TOP>");
            outputStream.print("Test a regular expression against your text.</TD></TR>");
        }
        if(flag)
        {
            outputStream.println("</TABLE></BLOCKQUOTE>");
        }
        return;
    }

    void printMonitorToolsForm(String s, String s1, String s2)
    {
        String s3 = request.getValue("AWRequest");
        if(!s3.equals("yes"))
        {
            COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("Tools.htm", s2, menus1);
            outputStream.println("<H2>Tools</H2>");
        }
        jgl.Array array = new Array();
        try
        {
            array = COM.dragonflow.Page.monitorPage.getMonitorClasses(this, false);
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println("*** " + exception);
        }
        printMonitorTools(array, "", "<B>Application Diagnostic Tools</B> that operate as a client application.");
        if(!s3.equals("yes"))
        {
            printMonitorTools(array, "server", "<B>Server Tools</B> show server statistics.");
            printMonitorTools(array, "network", "<B>Network Tools</B> test network connectivity and performance.");
        }
        printMonitorTools(array, "advanced", "<B>Advanced Tools</B> for complex environments that may require additional setup.");
        if(!s3.equals("yes"))
        {
            printMonitorTools(array, "beta", "<B>Beta Tools</B> new tools that are still being tested.");
            printFooter(outputStream);
        } else
        {
            outputStream.println("</TABLE></BODY></HTML>");
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param s1
     * @param s2
     */
    void printClasses(String s, jgl.Array array, String s1, String s2)
    {
        Enumeration enumeration;
        boolean flag;
        boolean flag1;
        enumeration = array.elements();
        flag = false;
        flag1 = false;

        while (enumeration.hasMoreElements())
        {
        Class class1 = (java.lang.Class)enumeration.nextElement();
        String s3 = (String)COM.dragonflow.SiteView.Monitor.getClassPropertyByObject(class1.getName(), "classType");
        if(s3 == null)
        {
            s3 = "";
        }
        if(!s3.equals(s1))
        {
            continue; 
        }
        
        try
        {
        COM.dragonflow.SiteView.AtomicMonitor atomicmonitor;
        String s4;
        atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)class1.newInstance();
        s4 = new String((String)atomicmonitor.getClassProperty("title"));
        if(s4 == null || s4.length() == 0)
        {
            continue; 
        }
        String s5;
        String s6;
        boolean flag2 = (atomicmonitor instanceof COM.dragonflow.SiteView.ServerMonitor) && !(atomicmonitor instanceof COM.dragonflow.StandardMonitor.ScriptMonitor) && !(atomicmonitor instanceof COM.dragonflow.StandardMonitor.LogMonitor);
        boolean flag3 = (atomicmonitor instanceof COM.dragonflow.SiteView.ServerMonitor) && ((COM.dragonflow.SiteView.ServerMonitor)atomicmonitor).remoteCommandLineAllowed();
        s5 = "";
        if(flag2)
        {
            s5 = s5 + "NT";
        }
        if(flag3)
        {
            if(s5.length() > 0)
            {
                s5 = s5 + ", ";
            }
            s5 = s5 + "Unix";
        }
        if(s5.length() > 0)
        {
            s5 = " <SUP><FONT SIZE=-1>remote " + s5 + "</FONT></SUP>";
        }
        s6 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s6.length() == 0)
        {
            s6 = request.getPermission("_monitorType", "default");
        }
        if(!s6.equals("hidden"))
        {
                if(!flag)
                {
                    flag = true;
                    outputStream.println("<BLOCKQUOTE>" + s2 + "<DL>");
                }
                outputStream.println("<DT><TABLE width=350><TR><TD><A HREF=" + getPageLink(atomicmonitor.getAddPage(), "Add") + "&monitor&group=" + s + "&class=" + atomicmonitor.getClassProperty("class") + ">Add " + s4 + " Monitor</A>" + s5 + "<TD align=right><A HREF=/SiteView/docs/" + atomicmonitor.getClassProperty("help") + " TARGET=Help>Help</A>" + "</TABLE><DD>" + atomicmonitor.getClassProperty("description"));
                if(s1.equals("server") && !flag1 && !DHCPLibInstalled())
                {
                    outputStream.println("<DT><TABLE width=350><TR><TD>Add DHCP Monitor<TD align=right><A HREF=/SiteView/docs/DHCPMonitor.htm TARGET=Help>Help</A></TABLE><DD>Determines whether an IP address can be obtained from a DHCP server.<BR>This monitor requires the jDHCP library.  See the <A HREF=/SiteView/docs/DHCPMonitor.htm TARGET=Help>Help</A> document for more information.</DD>");
                    flag1 = true;
                }
            }
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("<br>class: " + class1.getName() + " error: " + enumeration);
        }
        }

        if(flag)
        {
            outputStream.println("</DL></BLOCKQUOTE>");
        }
        return;
    }

    synchronized void printClasseList(String s, jgl.Array array)
    {
        int i = 0;
        int j = 0;
        outputStream.println("<h3>Alphabetical List of Monitor Types</h3><BLOCKQUOTE><TABLE border=0 cellspacing=0><tr>");
        if(monitorNames == null)
        {
            monitorNames = new Array();
            Enumeration enumeration = array.elements();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                java.lang.Class class1 = (java.lang.Class)enumeration.nextElement();
                try
                {
                    COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)class1.newInstance();
                    String s1 = (String)atomicmonitor.getClassProperty("class");
                    String s2 = (String)atomicmonitor.getClassProperty("title");
                    if(s2 != null && s2.length() > 0)
                    {
                        monitorNames.add(new monitorList(s2, s1, atomicmonitor.getAddPage()));
                    }
                }
                catch(java.lang.Exception exception) { }
            } while(true);
            jgl.Sorting.sort(monitorNames, new lessMonitor());
        }
        int ai[] = new int[monitorNames.size()];
        int k = 0;
        for(j = 0; j < monitorNames.size(); j++)
        {
            monitorList monitorlist = (monitorList)monitorNames.at(j);
            String s3 = request.getPermission("_monitorType", monitorlist.className);
            if(s3.length() == 0)
            {
                s3 = request.getPermission("_monitorType", "default");
            }
            //if(!s3.equals("hidden"))
            {
                ai[k++] = j;
            }
        }

        j = 0;
        i = 0;
        for(; j < k; j++)
        {
            int l = (i % 5) * (k / 5) + java.lang.Math.min(i % 5, k % 5);
            int i1 = i / 5;
            monitorList monitorlist1 = (monitorList)monitorNames.at(ai[l + i1]);
            if(i++ % 5 == 0)
            {
                outputStream.println("</tr><tr>");
            }
            outputStream.println("<td><font size=-1 face=Arial, sans-serif><A HREF=" + getPageLink(monitorlist1.page, "Add") + "&monitor&group=" + s + "&class=" + monitorlist1.className + ">" + monitorlist1.name + "</A>&nbsp;&nbsp;</font></td>");
        }

        outputStream.println("</tr></TABLE></BLOCKQUOTE>");
    }

    void printAddListForm(String s, String s1, String s2)
    {
        String s3 = COM.dragonflow.HTTP.HTTPRequest.encodeString(s2);
        if(request.isStandardAccount())
        {
            COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("Group.htm#addmon.htm", s3, menus1);
        } else
        {
            printButtonBar("SeerMonitors.htm", s2);
        }
        jgl.Array array = new Array();
        try
        {
            array = COM.dragonflow.Page.monitorPage.getMonitorClasses(this, true);/*ÐÞ¸ÄÎªTrue*/
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println("*** " + exception);
        }
        outputStream.println("<H2>Add Monitor to Group: <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2) + ">" + COM.dragonflow.Page.CGI.getGroupName(s2) + "</a></H2><hr>");
        printClasseList(s3, array);
        outputStream.println("<hr><p><h3>Monitor Types by Category</h3>");
        printClasses(s3, array, "", "<h3><b>Network Services Monitors</b> test applications by simulating end user actions.</h3><hr>");
        printClasses(s3, array, "server", "<h3><b>Server Monitors</b> measure attributes of the server and operating system.</h3><hr>");
        printClasses(s3, array, "application", "<h3><b>Application Monitors</b> monitor the performance of server applications.</h3><hr>");
        printClasses(s3, array, "advanced", "<h3><b>Advanced Monitors</b> handle specific needs of complex environments and may require additional setup.</h3><hr>");
        printClasses(s3, array, "beta", "<h3><b>Beta Monitors</b> are new features that are still being tested.</h3><hr>");
        outputStream.print("<P><FONT SIZE=-1>remote Unix</FONT> - indicates monitor can be used to monitor remote Unix servers<BR>\n<FONT SIZE=-1>remote NT</FONT> - indicates monitor can be used to monitor remote NT systems<P>\n");
        printFooter(outputStream);
    }

    void DeleteMonitorFromReports(String s, String s1)
    {
        jgl.Array array = null;
        try
        {
            if(!request.isStandardAccount())
            {
                array = ReadGroupFrames(request.getAccount());
            } else
            {
                String s2 = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config";
                java.io.File file = new File(s2);
                if(!file.exists())
                {
                    array = new Array();
                } else
                {
                    array = COM.dragonflow.Properties.FrameFile.readFromFile(s2);
                }
            }
        }
        catch(java.io.IOException ioexception)
        {
            array = new Array();
        }
        outputStream.println("<P>Checking reports...");
        String s3 = s1 + " " + s;
        jgl.Array array1 = new Array();
        Enumeration enumeration = array.elements();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            if(!COM.dragonflow.SiteView.Monitor.isReportFrame(hashmap))
            {
                array1.add(hashmap);
            } else
            {
                jgl.HashMap hashmap1 = COM.dragonflow.Page.managePage.copyHashMap(hashmap);
                hashmap1.remove("monitors");
                boolean flag = false;
                String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "title");
                Enumeration enumeration1 = hashmap.values("monitors");
                do
                {
                    if(!enumeration1.hasMoreElements())
                    {
                        break;
                    }
                    String s5 = (String)enumeration1.nextElement();
                    String as[] = COM.dragonflow.Utils.TextUtils.split(s5);
                    if(as.length == 1)
                    {
                        hashmap1.add("monitors", s5);
                    } else
                    if(as.length > 1)
                    {
                        if(s5.equals(s3))
                        {
                            flag = true;
                            outputStream.println("<P>Deleting monitor from report: " + s4);
                        } else
                        {
                            hashmap1.add("monitors", s5);
                        }
                    }
                } while(true);
                enumeration1 = hashmap1.values("monitors");
                if(enumeration1.hasMoreElements())
                {
                    array1.add(hashmap1);
                    if(flag)
                    {
                        java.lang.System.out.println(hashmap1.toString());
                    }
                } else
                {
                    outputStream.println("<P>No monitors in report; Deleting report: " + s4);
                }
            }
        } while(true);
        try
        {
            if(!request.isStandardAccount())
            {
                WriteGroupFrames(request.getAccount(), array1);
            } else
            {
                COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config", array1);
            }
        }
        catch(java.io.IOException ioexception1) { }
        outputStream.println("<P>Done");
    }

    void printDeleteForm(String s, String s1, String s2)
        throws java.lang.Exception
    {
        String s3 = request.getValue("id");
        if(request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request))
        {
            if(s.equals("Delete"))
            {
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                jgl.Array array2 = ReadGroupFrames(s2);
                COM.dragonflow.SiteView.AtomicMonitor atomicmonitor1 = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(array2, s3, request.getPortalServer(), request);
                COM.dragonflow.SiteView.AtomicMonitor atomicmonitor2 = (COM.dragonflow.SiteView.AtomicMonitor)siteviewgroup.getElement(s2 + COM.dragonflow.SiteView.SiteViewGroup.ID_SEPARATOR + atomicmonitor1.getFullID());
//                if(!COM.dragonflow.SiteView.TopazConfigurator.delMonitors.contains(atomicmonitor2))
//                {
//                    COM.dragonflow.SiteView.TopazConfigurator.delMonitors.add(atomicmonitor2);
//                }
                siteviewgroup.notifyMonitorDeletion(atomicmonitor2);
                COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
                schedulemanager.deleteMonitorFromScheduleObject(schedulemanager.getScheduleIdFromMonitor(atomicmonitor1), atomicmonitor2.getFullID());
            }
            try
            {
                if(isPortalServerRequest())
                {
                    printRefreshHeader("Deleting Monitor", getPageLink("portalMain", ""), 5);
                } else
                {
                    printRefreshHeader("Deleting Monitor", COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2), 5);
                }
                jgl.Array array = ReadGroupFrames(s2);
                int i = COM.dragonflow.SiteView.monitorUtils.findMonitorIndex(array, s3);
                array.remove(i);
                WriteGroupFrames(s2, array);
                if(!isPortalServerRequest())
                {
                    DeleteMonitorFromReports(s2, s3);
                }
                outputStream.println("<p><a href=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2) + ">Return to Group page</a>");
                printFooter(outputStream);
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s2);
            }
            catch(java.lang.Exception exception)
            {
                printError("There was a problem deleting the monitor.", exception.toString(), COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2));
            }
        } else
        {
            jgl.Array array1 = ReadGroupFrames(s2);
            COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(array1, s3, request.getPortalServer(), request);
            COM.dragonflow.SiteView.AtomicMonitor _tmp = atomicmonitor;
            String s4 = atomicmonitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName);
            COM.dragonflow.Page.CGI.menus menus1 = getNavItems(request);
            printButtonBar("Group.htm", "", menus1);
            outputStream.println("<h3>Delete Monitor</h3>");
            outputStream.println("<p>Are you sure you want to delete the monitor <B>" + s4 + "</B>" + " from the <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2) + ">" + COM.dragonflow.Page.CGI.getGroupName(s2) + "</A> Group?</p>");
            String s5 = request.getValue("_health").length() <= 0 ? "" : "<input type=hidden name=_health value=true>";
            outputStream.println("<p>" + getPagePOST("monitor", "Delete") + "<input type=hidden name=group value=\"" + s1 + "\">" + "<input type=hidden name=id value=\"" + s3 + "\">" + s5 + "<TABLE WIDTH=100% BORDER=0><TR>" + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit value=\"" + s + " Monitor\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s2) + ">Return to " + COM.dragonflow.Page.CGI.getGroupName(s2) + "</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    void printMonitorProgressPage(String s, String s1, String s2)
    {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        boolean flag = false;
        String s3 = s1 + COM.dragonflow.SiteView.SiteViewGroup.ID_SEPARATOR + s2;
        if(siteviewgroup != null && siteviewgroup.internalServerActive() && siteviewgroup.getSetting("_disableRefreshPage").length() == 0)
        {
            outputStream.println("<H3>" + s + " monitor</H3><P>" + "<A HREF=" + getWhereURL(COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1)) + ">" + getWhereLabel("Return to Group page") + "</A><P><HR>");
            outputStream.flush();
            if(s.equals("Updating"))
            {
                outputStream.println("Saved changes to monitor<P>");
                outputStream.flush();
            }
            if(!isPortalServerRequest())
            {
                printContentStartComment();
                COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)siteviewgroup.getElement(s3);
                if(atomicmonitor != null)
                {
                    atomicmonitor.setProperty(COM.dragonflow.SiteView.Monitor.pLastUpdate, "-1");
                    atomicmonitor.setProperty(COM.dragonflow.SiteView.Monitor.pForceRefresh, request.getValue("forceRefresh"));
                }
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s1);
                COM.dragonflow.SiteView.AtomicMonitor atomicmonitor1 = (COM.dragonflow.SiteView.AtomicMonitor)siteviewgroup.getElement(s3);
                if(atomicmonitor1 != null)
                {
                    if(atomicmonitor1.progressString.equals("Depends On Monitor Disabled\n"))
                    {
                        COM.dragonflow.SiteView.AtomicMonitor _tmp = atomicmonitor1;
                        outputStream.println("Monitor Disabled by Depends On: " + atomicmonitor1.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName) + "<P>&nbsp;&nbsp;&nbsp;");
                        outputStream.println("<HR>");
                        printFooter(outputStream);
                        printRefreshHeader("", COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1), 3);
                        outputStream.flush();
                        return;
                    }
                    if(atomicmonitor1.progressString.startsWith("disabled"))
                    {
                        COM.dragonflow.SiteView.AtomicMonitor _tmp1 = atomicmonitor1;
                        outputStream.println("Monitor Disabled by Schedule: " + atomicmonitor1.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName) + "<P>&nbsp;&nbsp;&nbsp;");
                        outputStream.println("<HR>");
                        printFooter(outputStream);
                        printRefreshHeader("", COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1), 3);
                        outputStream.flush();
                        return;
                    }
                    COM.dragonflow.SiteView.AtomicMonitor _tmp2 = atomicmonitor1;
                    outputStream.println("Asked SiteView to run monitor: " + atomicmonitor1.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName) + "<P>&nbsp;&nbsp;&nbsp;");
                    outputStream.flush();
                    long l = 1000L;
                    long l1 = COM.dragonflow.SiteView.Platform.timeMillis();
                    long l2 = l1 + (long)(siteviewgroup.getSettingAsLong("_monitorProgressTimeout", 20) * 1000);
                    flag = true;
                    int i = 0;
                    for(; atomicmonitor1.progressString.length() == 0 && COM.dragonflow.SiteView.Platform.timeMillis() <= l2; COM.dragonflow.SiteView.Platform.sleep(l))
                    {
                        if(i % 5 == 0)
                        {
                            outputStream.println("Waiting for monitor to run<br>&nbsp;&nbsp;&nbsp;");
                            outputStream.flush();
                        }
                        i++;
                    }

                    String s5 = "";
                    i = 0;
                    for(; atomicmonitor1.progressString.indexOf("\n\n") == -1 && COM.dragonflow.SiteView.Platform.timeMillis() <= l2; COM.dragonflow.SiteView.Platform.sleep(l))
                    {
                        i++;
                        if(s5.length() < atomicmonitor1.progressString.length())
                        {
                            outputStream.println(COM.dragonflow.Utils.TextUtils.replaceChar(atomicmonitor1.progressString.substring(s5.length()), '\n', "<br>&nbsp;&nbsp;&nbsp;\n"));
                            outputStream.flush();
                        } else
                        if(i > 2 && s5.length() > 0)
                        {
                            i = 0;
                            outputStream.println("Monitor running...<br>&nbsp;&nbsp;&nbsp;");
                            outputStream.flush();
                        }
                        s5 = atomicmonitor1.progressString;
                    }

                    if(s5.length() < atomicmonitor1.progressString.length())
                    {
                        outputStream.println(COM.dragonflow.Utils.TextUtils.replaceChar(atomicmonitor1.progressString.substring(s5.length()), '\n', "<br>&nbsp;&nbsp;&nbsp;\n"));
                        outputStream.flush();
                    }
                    if(COM.dragonflow.SiteView.Platform.timeMillis() > l2)
                    {
                        outputStream.println("Monitor still running.<br>&nbsp;&nbsp;&nbsp;");
                    }
                    atomicmonitor1.progressString = "";
                }
                printContentEndComment();
            } else
            {
                COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView)getSiteView();
                if(portalsiteview != null)
                {
                    String s4 = portalsiteview.getURLContentsFromRemoteSiteView("/SiteView/cgi/go.exe/SiteView?page=monitor&operation=RefreshMonitor&account=administrator&refresh=true&group=" + COM.dragonflow.Page.monitorPage.getGroupIDRelative(s1) + "&id=" + s2, "_centrascopeRefreshMatch");
                    outputStream.println(s4);
                }
            }
            outputStream.println("<HR>");
            printFooter(outputStream);
        }
        if(!flag)
        {
            printReturnRefresh(COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1), 0);
        }
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        String s1;
        if(s.equals("AddList"))
        {
            s1 = "Add Monitor";
        } else
        {
            s1 = s + " Monitor";
        }
        String s3 = request.getValue("group");
        String s4 = COM.dragonflow.Utils.I18N.UnicodeToString(s3, COM.dragonflow.Utils.I18N.nullEncoding());
        s4 = COM.dragonflow.Utils.I18N.StringToUnicode(s4, "");
        if(s.equals("RefreshMonitor"))
        {
            if(!request.actionAllowed("_monitorRefresh"))
            {
                throw new HTTPRequestException(557);
            }
            printRefreshHeader(s1, COM.dragonflow.Page.CGI.getGroupDetailURL(request, s4), 5);
        } else
        {
            String s5 = s4;
            if(request.getValue("id").length() > 0)
            {
                s5 = s5 + "/" + request.getValue("id");
            }
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = COM.dragonflow.SiteView.Portal.getSiteViewForID(s5);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject1 = siteviewobject.getElement(s5);
            if(siteviewobject1 == null)
            {
                siteviewobject1 = siteviewobject;
            }
            if(!request.isPost() && COM.dragonflow.Page.treeControl.notHandled(request))
            {
                if(!request.getValue("AWRequest").equals("yes"))
                {
                    COM.dragonflow.Page.monitorPage.printBodyHeader(outputStream, s1, "", siteviewobject1.getTreeSetting("_httpCharSet"));
                } else
                {
                    printBodyHeaderAWRequest(outputStream, s1, "", siteviewobject1.getTreeSetting("_httpCharSet"));
                }
            }
        }
        if(s.equals("AddList"))
        {
            if(!request.actionAllowed("_monitorEdit"))
            {
                throw new HTTPRequestException(557);
            }
            printAddListForm(s, s3, s4);
        } else
        if(s.equals("Tools"))
        {
            if(!request.actionAllowed("_monitorTools"))
            {
                throw new HTTPRequestException(557);
            }
            printMonitorToolsForm(s, s3, s4);
        } else
        if(s.equals("Add"))
        {
            if(!request.actionAllowed("_monitorEdit"))
            {
                throw new HTTPRequestException(557);
            }
            printAddForm(s, s3, s4);
        } else
        if(s.equals("Delete"))
        {
            if(!request.actionAllowed("_monitorEdit"))
            {
                throw new HTTPRequestException(557);
            }
            printDeleteForm(s, s3, s4);
        } else
        if(s.equals("Edit"))
        {
            if(!request.actionAllowed("_monitorEdit"))
            {
                throw new HTTPRequestException(557);
            }
            printAddForm(s, s3, s4);
        } else
        if(s.equals("RefreshMonitor"))
        {
            if(!request.actionAllowed("_monitorRefresh"))
            {
                throw new HTTPRequestException(557);
            }
            String s2 = "Updating";
            if(request.getValue("refresh").length() > 0 && !isPortalServerRequest())
            {
                COM.dragonflow.Properties.FrameFile.touchFile(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/" + s4 + ".mg");
                s2 = "Running";
            }
            printMonitorProgressPage(s2, s4, request.getValue("id"));
        } else
        {
            printError("The link was incorrect", "unknown operation", COM.dragonflow.Page.CGI.getGroupDetailURL(request, s4));
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.monitorPage monitorpage = new monitorPage();
        String s = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "docs" + java.io.File.separator;
        if(args.length > 0)
        {
            monitorpage.args = args;
            COM.dragonflow.Page.monitorPage _tmp = monitorpage;
            jgl.Array array = COM.dragonflow.Page.monitorPage.getMonitorClasses(monitorpage, false);
            if(args[0].equals("-logproperties"))
            {
                s = s + "LogColumns.htm";
                java.io.PrintStream printstream = new PrintStream(new FileOutputStream(new File(s)));
                printstream.println("<HTML><HEAD><TITLE>SiteView Log File Columns</TITLE>");
                printstream.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"siteviewUG.css\"></HEAD>");
                printstream.println("<body bgcolor=#FFFFFF link=#006600 vlink=#339966 alink=#00AA33 onload=\"window.focus()\">\n<a name=top></a><p><font size=1 face=Verdana, Arial, Helvetica, sans-serif>SiteView User's Guide</font></p>");
                printstream.println("<H1>SiteView Log File Columns</H1>\n");
                printstream.println("<p><!--onlineStart--><br clear=all><!--hr--><table id=\"nav_top\" align=\"right\"><tr>\n");
                printstream.println("<td><a href=\"Advanced.htm\"><img src=images/aback.gif border=0 vspace=0></a></td><td><a href=\"#top\"><img src=images/atop.gif border=0 vspace=0></a></td><td><a href=\"UGtoc.htm\"><img src=images/atoc.gif border=0 vspace=0></a></td><td><a href=\"regexp.htm\"><img src=images/afwd.gif border=0 vspace=0></a></td>");
                printstream.println("</tr></table><br clear=all><!--onlineEnd--><hr></p>\n");
                printstream.println("These are the contents of each column in the SiteView.log file.  The columns are tab-delimited.<P>");
                printstream.println("The first six columns are always:<BR><TABLE BORDER=1 cellspacing=0>\n<TR><TH>column</TH><TH>data in column</TH></TR>\n<TR><TD>1</TD><TD>time/date of sample</TD></TR>\n<TR><TD>2</TD><TD>category (good, error, warning, nodata)</TD></TR>\n<TR><TD>3</TD><TD>group (also called ownerID) </TD></TR>\n<TR><TD>4</TD><TD>monitor title </TD></TR>\n<TR><TD>5</TD><TD>stateString (this is the status string that shows up on the Detail Page)</TD></TR>\n<TR><TD>6</TD><TD>id:sample # (unique ID for this monitor - group + id is unique key for a monitor) sample # is unique sample # for that monitor</TD></TR>\n</TABLE><P>\n");
                for(int i = 0; i < array.size(); i++)
                {
                    java.lang.Class class1 = (java.lang.Class)array.at(i);
                    if(class1.getName().indexOf("URLRemote") != -1 || class1.getName().indexOf("PingRemote") != -1 || class1.getName().indexOf("SiteSeer") != -1)
                    {
                        continue;
                    }
                    try
                    {
                        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)class1.newInstance();
                        jgl.Array array1 = monitor.getLogProperties();
                        printstream.println("<TABLE BORDER=1 cellspacing=0><CAPTION>");
                        printstream.println("Monitor Type: " + monitor.getClassProperty("title") + "(" + monitor.getClassProperty("class") + ")");
                        printstream.println("</CAPTION><TR><TH>column</TH><TH>data in column</TH></TR>");
                        printstream.println("<TR><TD>1</TD><TD>date</TD></TR>");
                        for(int j1 = 0; j1 < array1.size(); j1++)
                        {
                            COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)array1.at(j1);
                            printstream.println("<TR><TD>" + (j1 + 2) + "</TD><TD>" + stringproperty1.getLabel() + "</TD></TR>");
                        }

                        printstream.println("</TABLE><P>");
                    }
                    catch(java.lang.Exception exception)
                    {
                        java.lang.System.out.println("Exception : " + exception);
                    }
                }

                printstream.println("</BODY></HTML>");
                java.lang.System.exit(0);
            } else
            if(args[0].equals("-dblogproperties"))
            {
                s = s + "DBProperties.htm";
                java.io.PrintStream printstream1 = new PrintStream(new FileOutputStream(new File(s)));
                printstream1.println("<HTML><HEAD><TITLE>SiteView Database Log Columns</TITLE></HEAD><BODY>");
                printstream1.println("<CENTER><H2>SiteView Database Log Columns</H2></CENTER>\n");
                printstream1.println("These are the contents of each column in the SiteScopLog table.<P>");
                printstream1.println("The first nine columns are always:<BR><TABLE BORDER=1 cellspacing=0>\n<TR><TH>column</TH><TH>data in column</TH></TR>\n<TR><TD>1</TD><TD>time/date of sample</TD></TR>\n<TR><TD>2</TD><TD>server name or IP address</TD></TR>\n<TR><TD>3</TD><TD>class</TD></TR>\n<TR><TD>4</TD><TD>sample</TD></TR>\n<TR><TD>5</TD><TD>category (good, error, warning, nodata)</TD></TR>\n<TR><TD>6</TD><TD>group </TD></TR>\n<TR><TD>7</TD><TD>monitor title </TD></TR>\n<TR><TD>8</TD><TD>stateString (this is the status string that shows up on the Detail Page)</TD></TR>\n<TR><TD>9</TD><TD>id:sample # (unique ID for this monitor - group + id is unique key for a monitor) sample # is unique sample # for that monitor</TD></TR>\n</TABLE><P>\n");
                for(int j = 0; j < array.size(); j++)
                {
                    java.lang.Class class2 = (java.lang.Class)array.at(j);
                    if(class2.getName().indexOf("URLRemote") != -1 || class2.getName().indexOf("PingRemote") != -1 || class2.getName().indexOf("SiteSeer") != -1)
                    {
                        continue;
                    }
                    try
                    {
                        COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor)class2.newInstance();
                        jgl.Array array2 = monitor1.getLogProperties();
                        printstream1.println("<TABLE BORDER=1 cellspacing=0><CAPTION>");
                        printstream1.println("Monitor Type: " + monitor1.getClassProperty("title") + "(" + monitor1.getClassProperty("class") + ")");
                        printstream1.println("</CAPTION><TR><TH>column</TH><TH>data in column</TH></TR>");
                        printstream1.println("<TR><TD>1</TD><TD>date</TD></TR>");
                        printstream1.println("<TR><TD>2</TD><TD>server</TD></TR>");
                        printstream1.println("<TR><TD>3</TD><TD>class</TD></TR>");
                        printstream1.println("<TR><TD>3</TD><TD>sample</TD></TR>");
                        for(int k1 = 0; k1 < array2.size(); k1++)
                        {
                            COM.dragonflow.Properties.StringProperty stringproperty2 = (COM.dragonflow.Properties.StringProperty)array2.at(k1);
                            printstream1.println("<TR><TD>" + (k1 + 5) + "</TD><TD>" + stringproperty2.getLabel());
                            if(k1 + 5 > 19)
                            {
                                printstream1.println("<i><font size=\"2\"> {Not enabled by default}</font></i>");
                            }
                            printstream1.println("</TD></TR>");
                        }

                        printstream1.println("</TABLE><P>");
                    }
                    catch(java.lang.Exception exception1)
                    {
                        java.lang.System.out.println("Exception : " + exception1);
                    }
                }

                printstream1.println("</BODY></HTML>");
                java.lang.System.exit(0);
            } else
            if(args[0].equals("-properties"))
            {
                s = s + "TemplateProperties.htm";
                java.io.PrintStream printstream2 = new PrintStream(new FileOutputStream(new File(s)));
                printstream2.println("<HTML><HEAD><TITLE>SiteView Template File Properties</TITLE><link rel=\"stylesheet\" type=\"text/css\" href=\"siteviewUG.css\"> </HEAD><body bgcolor=#FFFFFF link=#006600 vlink=#339966 alink=#00AA33 onload=\"window.focus()\"> <a name=top></a><p><font size=1 face=Verdana, Arial, Helvetica, sans-serif>SiteView User's Guide</font></p><h1>SiteView Template File Properties</H1><p><!--onlineStart--><br clear=all><!--hr--><table id=nav_top align=right><tr><td><a href=\"EditTemplate.htm\"><img src=images/aback.gif border=0 vspace=0></a></td><td><a href=\"#top\"><img src=images/atop.gif border=0 vspace=0></a></td><td><a href=\"UGtoc.htm\"><img src=images/atoc.gif border=0 vspace=0></a></td><td><a href=\"CustomMon.htm\"><img src=images/afwd.gif border=0 vspace=0></a></td> </tr></table><br clear=all><!--onlineEnd--><hr></p>");
                printstream2.println("<p>These are the property names that are be available for use in SiteView alert and report templates. \nYou can use these properties to customize the content of e-mail messages that are sent when a monitor \nalert is generated. These parameters are also used to <a href=ebusinesstransaction.htm#pass>pass values</a> between monitors that are part of an \n<a href=ebusinesstransaction.htm>eBusiness Transaction Monitor</a> \n<P>Properties that begin with an underscore character are monitor configuration properties. These properties are \nset within SiteView on the applicable monitor definition forms. The other properties listed are the results \nof the monitor running. Note that some of the properties, such as lastMeasurementTime, are internal properties \nand not generally interesting for alerts or reports.\n<P>The following properties are applicable to all monitor types:\n<P>\n<TABLE BORDER=1 cellspacing=0><CAPTION>General Template Properties</CAPTION>\n<TR><TH>name</TH><TH>description</TH></TR>\n<TR><TD>name</TD><TD>name of the monitor (same as _name)</TD></TR>\n<TR><TD>state</TD><TD>the status of the monitor (same as stateString)</TD></TR>\n<TR><TD>group</TD><TD>the name of the group that the monitor is in </TD></TR>\n<TR><TD>currentTime</TD><TD>the time that the alert is run</TD></TR>\n<TR><TD>time</TD><TD>the time that the monitor completed</TD></TR>\n<TR><TD>time-time</TD><TD>the time portion of the time that the monitor completed</TD></TR>\n<TR><TD>time-date</TD><TD>the date portion of the time that the monitor completed</TD></TR>\n<TR><TD>s|$year$/$month$/$day$|</TD><TD>a substitution expression - in this case year, month, day</TD></TR>\n<TR><TD>group.<I>propertyname</I></TD><TD>a given property of the group that the monitor is in</TD></TR>\n<TR><TD>group.parent.<I>propertyname</I></TD><TD>a given property of the parent group of the group that the monitor is in</TD></TR>\n<TR><TD>s|$year$/$month$/$day$|</TD><TD>a substitution expression - in this case year, month, day</TD></TR>\n<TR><TD>siteviewurl</TD><TD>the URL to the main page of SiteView for admin access</TD></TR>\n<TR><TD>siteviewuserurl</TD><TD>the URL to the main page of SiteView for user access</TD></TR>\n<TR><TD>mainParameters</TD><TD>all of the parameters that appear on the Edit form</TD></TR>\n<TR><TD>mainStateProperties</TD><TD>all of the result stats shown on the Reports</TD></TR>\n<TR><TD>secondaryParameters</TD><TD>all of the internal parameters</TD></TR>\n<TR><TD>secondaryStateProperties</TD><TD>all of the internal result stats</TD></TR>\n<TR><TD>all</TD><TD>all of the properties of the monitor</TD></TR>\n</TABLE><P>\n<P>\nThe following properties are applicable to the email templates stored in the templates.history directory:\n<P>\n<TABLE BORDER=1 cellspacing=0><CAPTION>Report Email Template Properties</CAPTION>\n<TR><TH>name</TH><TH>description</TH></TR>\n<TR><TD>reportPeriod</TD><TD>The time period for this report</TD></TR>\n<TR><TD>summary</TD><TD>Summary and measurement information</TD></TR>\n<TR><TD>basicAlertSummary</TD><TD>Basic information on what alerts have been triggered</TD></TR>\n<TR><TD>detailAlertSummary</TD><TD>More detailed information on alerts</TD></TR>\n<TR><TD>_webserverAddress</TD><TD>The IP address for the SiteView Server</TD></TR>\n<TR><TD>_httpPort</TD><TD>The port number used to access SiteView</TD></TR>\n<TR><TD>reportURL</TD><TD>The URL to the HTML version of the management report</TD></TR>\n<TR><TD>reportIndexURL</TD><TD>The URL to the index page for the management report</TD></TR>\n<TR><TD>textReportURL</TD><TD>The URL to the comma-delimited file generated by the management report</TD></TR>\n<TR><TD>xmlReportURL</TD><TD>The URL to the XML file generated by the management report</TD></TR>\n<TR><TD>userReportURL</TD><TD>The URL to the user-accessible verion of the report </TD></TR>\n<TR><TD>userReportIndexURL</TD><TD>The URL to the index page for a user-accessible report </TD></TR>\n<TR><TD>userTextReportURL</TD><TD>The URL to the comma-delimited file generated by a user-accessible report </TD></TR>\n<TR><TD>userXMLReportURL</TD><TD>The URL to the XML file generated by a user-accessible report </TD></TR>\n</TABLE><P>\n<p>Use the links below to view other template properties specific to the different monitor types:</p>\n<table width=75% border=0 cellspacing=0 cellpadding=0 align=center>\n");
                for(int k = 0; k < array.size(); k++)
                {
                    java.lang.Class class3 = (java.lang.Class)array.at(k);
                    try
                    {
                        COM.dragonflow.SiteView.Monitor monitor2 = (COM.dragonflow.SiteView.Monitor)class3.newInstance();
                        if(k % 2 == 0)
                        {
                            printstream2.println("<TR>");
                        }
                        printstream2.println("<TD><A HREF=#" + monitor2.getClassProperty("class") + ">" + monitor2.getClassProperty("title") + "</A>");
                        if(k % 2 == 1)
                        {
                            printstream2.println("</TR>");
                        }
                    }
                    catch(java.lang.Exception exception2)
                    {
                        java.lang.System.out.println("Exception : " + exception2);
                    }
                }

                printstream2.println("</TABLE><P>");
                for(int l = 0; l < array.size(); l++)
                {
                    java.lang.Class class4 = (java.lang.Class)array.at(l);
                    try
                    {
                        COM.dragonflow.SiteView.Monitor monitor3 = (COM.dragonflow.SiteView.Monitor)class4.newInstance();
                        printstream2.println("<TABLE BORDER=1 cellspacing=0><CAPTION>");
                        printstream2.println("<A NAME=" + monitor3.getClassProperty("class") + ">");
                        printstream2.println("Monitor Type: " + monitor3.getClassProperty("title"));
                        printstream2.println("</A>");
                        printstream2.println("</CAPTION><TR><TH>name</TH><TH>label</TH><TH>description</TH></TR>");
                        int i1 = 1;
                        do
                        {
                            COM.dragonflow.Properties.StringProperty stringproperty = monitor3.getStatePropertyObject(i1);
                            if(stringproperty == null)
                            {
                                break;
                            }
                            printstream2.println("<TR><TD>" + stringproperty.getName() + "</TD><TD>" + stringproperty.getLabel() + "</TD><TD></TD></TR>");
                            i1++;
                        } while(true);
                        Enumeration enumeration = monitor3.getImmediateProperties().elements();
                        do
                        {
                            if(!enumeration.hasMoreElements())
                            {
                                break;
                            }
                            COM.dragonflow.Properties.StringProperty stringproperty3 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                            if(stringproperty3.isStateProperty && stringproperty3.getOrder() == 0)
                            {
                                printstream2.println("<TR><TD>" + stringproperty3.getName() + "</TD><TD>" + stringproperty3.getLabel() + "</TD><TD></TD></TR>");
                            }
                        } while(true);
                        enumeration = monitor3.getParameterObjects();
                        do
                        {
                            if(!enumeration.hasMoreElements())
                            {
                                break;
                            }
                            COM.dragonflow.Properties.StringProperty stringproperty4 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                            printstream2.println("<TR><TD>" + stringproperty4.getName() + "</TD><TD>" + stringproperty4.getLabel() + "</TD><TD>" + stringproperty4.getDescription() + "</TD></TR>");
                            if(stringproperty4.getName().equals("_machine"))
                            {
                                printstream2.println("<TR><TD>remoteMachineName</TD><TD>Remote Server</TD><TD>use this field instead of _machine, if you need the name of the remote machine to appear in the template</TD></TR>");
                            }
                        } while(true);
                        printstream2.println("</TABLE><P>");
                    }
                    catch(java.lang.Exception exception3)
                    {
                        java.lang.System.out.println("Exception : " + exception3);
                    }
                }

                printstream2.println("</BODY></HTML>");
                java.lang.System.exit(0);
            }
        }
        monitorpage.handleRequest();
        java.lang.System.exit(0);
    }

    private boolean DHCPLibInstalled()
    {
        try
        {
            java.lang.Class.forName("COM.dragonflow.StandardMonitor.DHCPMonitor");
        }
        catch(java.lang.Throwable throwable)
        {
            return false;
        }
        return true;
    }

    private boolean setCounerProperty(String s, String s1, jgl.HashMap hashmap, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, int i, String s2)
    {
        if(request.getValue(COM.dragonflow.Properties.BrowsableProperty.BROWSE + s1).length() > 0)
        {
            String s3 = (String)hashmap.get(s1);
            atomicmonitor.setProperty(s + i, java.net.URLDecoder.decode(s1));
            if(s3 != null)
            {
                atomicmonitor.setProperty(s2 + i, java.net.URLDecoder.decode(s3));
            }
            return true;
        } else
        {
            hashmap.remove(s1);
            hashmap.remove(s1);
            return false;
        }
    }

    static 
    {
        classFilter = null;
        batch = false;
        showMonitors = "";
        String s = java.lang.System.getProperty("monitorPage.batch");
        if(s != null && s.length() > 0)
        {
            batch = true;
        }
        if(!batch)
        {
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            showMonitors = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_monitorsFilter");
            if(showMonitors.length() > 0)
            {
                classFilter = new HashMap();
                String as[] = COM.dragonflow.Utils.TextUtils.split(showMonitors, ",");
                for(int i = 0; i < as.length; i++)
                {
                    for(Enumeration enumeration = COM.dragonflow.Utils.LUtils.getMonitorsFromType(COM.dragonflow.Utils.TextUtils.toInt(as[i])); enumeration.hasMoreElements(); classFilter.put(enumeration.nextElement(), "true")) { }
                }

            }
        }
    }
}
