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
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI, reportPage

public class setupPage extends COM.dragonflow.Page.CGI
{

    public setupPage()
    {
    }

    void doLicense()
    {
        printBodyHeader("SiteView Lite License");
        outputStream.println("<CENTER><H2>SiteView Lite License</H2></CENTER><P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><CENTER><input type=hidden name=name value=\"" + request.getValue("name") + "\">\n" + "<input type=hidden name=email value=\"" + request.getValue("email") + "\">\n" + "<input type=hidden name=page value=\"setup\">" + "<input type=hidden name=account value=\"administrator\">\n" + "<input type=hidden name=operation value=\"setup\">" + "<input type=hidden name=licenseAccepted value=\"yes\">" + "<input type=submit value=\"Accept\"> SiteView Lite license" + "</CENTER></FORM>");
        outputStream.println("<PRE>");
        try
        {
            outputStream.println(COM.dragonflow.Utils.FileUtils.readFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "license.html"));
        }
        catch(java.io.IOException ioexception)
        {
            outputStream.println(ioexception);
        }
        outputStream.println("</PRE>");
        printFooter(outputStream);
    }

    void doInitialSetup()
    {
        outputStream.println("<H1 align=\"center\">SiteView Setup</H1>\n<hr>");
        outputStream.println("<br><p><b>Please wait a few seconds while basic SiteView settings are being made...</b><p>\n");
        outputStream.flush();
        if(request.getValue("operation").indexOf("Examples") != -1)
        {
            outputStream.println("<b>Creating default monitors and groups ...</b><p>");
            outputStream.flush();
            jgl.Array array = new Array();
            jgl.Array array1 = new Array();
            jgl.HashMap hashmap = new HashMap();
            jgl.HashMap hashmap1 = new HashMap();
            int i = 0;
            char c = '\u0258';
            array.add("Examples");
            hashmap.put("_name", "Examples");
            hashmap.put("_description", "<hr><b>A group of example SiteView monitors organized into subgroups. Click a subgroup  name in the table below to view the contents of that group. Use the <u>Delete</u> link below  the table to delete this group or any of the  subgroups to recover the SiteView  monitor license points used by these monitors.</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "Heartbeat monitors subgroup");
            hashmap1.put("_group", "Heartbeats");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "Application monitors subgroup");
            hashmap1.put("_group", "Applications");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "System monitors subgroup");
            hashmap1.put("_group", "Systems");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "Network monitors subgroup");
            hashmap1.put("_group", "Network");
            array1.add(hashmap1);
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Examples", "mg");
            array.add("Heartbeats");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "Heartbeat monitors subgroup");
            hashmap.put("_parent", "Examples");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_description", "<hr><b>A group of key indicators of basic system function. Other groups or monitors dependent on this group will be disabled if the monitors in this group reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            if(COM.dragonflow.SiteView.Platform.isWindows())
            {
                hashmap1 = new HashMap();
                hashmap1.put("_id", "" + i++);
                hashmap1.put("_class", "ServiceMonitor");
                hashmap1.put("_name", "Server service");
                hashmap1.put("_service", "Server");
                hashmap1.put("_frequency", "" + (int)c);
                hashmap1.put("_monitorDescription", "<br>Example Service Monitor watching a service or process upon which system operation is dependent");
                array1.add(hashmap1);
                hashmap1 = new HashMap();
                hashmap1.put("_id", "" + i++);
                hashmap1.put("_class", "ServiceMonitor");
                hashmap1.put("_name", "java process");
                hashmap1.put("_service", "(using Process Name)");
                hashmap1.put("_process", "java");
                hashmap1.put("_frequency", "" + (int)c);
                hashmap1.put("_monitorDescription", "<br>Example Service Monitor watching a service or process upon which system operation is dependent");
                array1.add(hashmap1);
            } else
            if(!COM.dragonflow.SiteView.Platform.isWindows())
            {
                String as[] = {
                    "init", "inetd", "ttymon"
                };
                jgl.Array array2 = COM.dragonflow.SiteView.Platform.processList();
                int k = 0;
                do
                {
                    if(k >= as.length)
                    {
                        break;
                    }
                    Enumeration enumeration2 = array2.elements();
                    do
                    {
                        if(!enumeration2.hasMoreElements())
                        {
                            break;
                        }
                        String s4 = (String)enumeration2.nextElement();
                        if(s4.indexOf(as[k]) == -1)
                        {
                            continue;
                        }
                        hashmap1 = new HashMap();
                        hashmap1.put("_id", "" + i++);
                        hashmap1.put("_class", "ServiceMonitor");
                        hashmap1.put("_name", as[k] + " process");
                        hashmap1.put("_service", s4);
                        hashmap1.put("_frequency", "" + (int)c);
                        hashmap1.put("_monitorDescription", "<br>Example Service Monitor watching a process upon which system operation is dependent");
                        array1.add(hashmap1);
                        break;
                    } while(true);
                    k++;
                } while(true);
            } else
            {
                java.lang.System.err.println("failed to find 'HEARTBEAT' process to monitor");
            }
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Heartbeats", "mg");
            array.add("Applications");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "Application monitors subgroup");
            hashmap.put("_parent", "Examples");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_dependsOn", "Heartbeats 1");
            hashmap.put("_description", "<hr><b>A group of example monitors watching web servers and applications. Use monitors like these to watch system performance statistics and key system operation.  This group is dependent on the first monitor in the Heartbeats group and will be disabled  if that monitor reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "URLMonitor");
            hashmap1.put("_name", "URL: demo.siteview.com");
            hashmap1.put("_url", "http://demo.siteview.com/");
            hashmap1.put("_frequency", "43200");
            hashmap1.put("_dependsOn", "Heartbeats 1");
            hashmap1.put("_dependsCondition", "good");
            hashmap1.put("_monitorDescription", "<br>Example URL Monitor on demo.siteview.com.   Edit this monitor to include a URL at your web site");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "URLSequenceMonitor");
            hashmap1.put("_referenceType1", "url");
            hashmap1.put("_referenceType2", "form");
            hashmap1.put("_referenceType3", "link");
            hashmap1.put("_referenceType4", "link");
            hashmap1.put("_referenceType5", "link");
            hashmap1.put("_referenceType6", "link");
            hashmap1.put("_reference1", "http://www.google.com/");
            hashmap1.put("_reference2", "Google Search");
            hashmap1.put("_reference3", "<b>SiteView</b>\256 User Guide - Table of Contents");
            hashmap1.put("_reference4", "Key Features in SiteView");
            hashmap1.put("_reference5", "SNMP Trap Alert");
            hashmap1.put("_reference6", "regular expression");
            hashmap1.put("_frequency", "302400");
            hashmap1.put("_dependsOn", "Applications 1");
            hashmap1.put("_dependsCondition", "good");
            hashmap1.put("_name", "URL Sequence: Google Search on SiteView monitoring");
            hashmap1.put("_timeout", "60");
            hashmap1.put("_content4", "/[Aa]gentless/");
            hashmap1.put("_class", "URLSequenceMonitor");
            hashmap1.put("_postData2", "q=siteview url snmp monitoring web");
            hashmap1.put("_monitorDescription", "<br>Example URL Sequence monitor starting with a Google  search on SiteView monitoring following to the SiteView User Guide online. Will indicate  if there is a problem with a particular step of a web transaction.  ");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "URLContentMonitor");
            hashmap1.put("_name", "URL Content: demo.siteview.com");
            hashmap1.put("_url", "http://demo.siteview.com/");
            hashmap1.put("_frequency", "43200");
            hashmap1.put("_dependsOn", "Applications 1");
            hashmap1.put("_dependsCondition", "good");
            hashmap1.put("_content", "/([a-zA-Z\\s\\w]{1,20})/s");
            hashmap1.put("_timeout", "60");
            hashmap1.put("_monitorDescription", "<br>Example URL Content Monitor on demo.siteview.com.   This monitor includes an example regular expression matching the text on the web page. Use parentheses in the regular expression to have the matched content  displayed on in the status message.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "ApacheMonitor");
            hashmap1.put("_name", "Apache server stats.");
            hashmap1.put("_url", "http://apacheserver:80/server-status?auto");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_counters", "CPULoad,ReqPerSec,BytesPerSec,BusyWorkers,IdleWorkers");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_monitorDescription", "<br>Example Apache Server Monitor watching web server statistics. Edit this monitor to include the address of an actual Apache server and uncheck the Disable option to run the monitor");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "NetscapeMonitor");
            hashmap1.put("_name", "iPlanet 6.0 server stats.");
            hashmap1.put("_url", "_url=http://iplanetserver:port/https-iplanetserver/bin/instance-app/virtualServerStats.jsp?pollInterval=15&vsname=All");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_apacheCounters", "Total Number Of Requests,4xx,Other,200,404");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_monitorDescription", "<br>Example iPlanet Server Monitor watching iPlanet 6.0 web server statistics. Edit this monitor to include the admin server address of an actual iPlanet 6.0 server, the admin user name and password,  and uncheck the Disable option to run the monitor");
            array1.add(hashmap1);
            if(COM.dragonflow.SiteView.Platform.isWindows())
            {
                String as1[] = {
                    "IIS Admin", "World Wide Web", "Netscape", "FTP", "HTTP"
                };
                jgl.Array array3 = COM.dragonflow.SiteView.Platform.processList();
                for(Enumeration enumeration1 = array3.elements(); enumeration1.hasMoreElements();)
                {
                    String s2 = (String)enumeration1.nextElement();
                    int k1 = 0;
                    while(k1 < as1.length) 
                    {
                        if(s2.indexOf(as1[k1]) != -1)
                        {
                            hashmap1 = new HashMap();
                            hashmap1.put("_id", "" + i++);
                            hashmap1.put("_class", "ServiceMonitor");
                            hashmap1.put("_name", s2 + " service");
                            hashmap1.put("_service", s2);
                            hashmap1.put("_frequency", "" + (int)c);
                            hashmap1.put("_monitorDescription", "<br>Example Service Monitor watching an network service or process  upon which system operation is dependent");
                            array1.add(hashmap1);
                        }
                        k1++;
                    }
                }

            } else
            {
                String as2[] = {
                    "http", "ftp", "sendmail"
                };
                jgl.Array array4 = COM.dragonflow.SiteView.Platform.processList();
label0:
                for(int l = 0; l < as2.length; l++)
                {
                    Enumeration enumeration3 = array4.elements();
                    String s5;
                    do
                    {
                        if(!enumeration3.hasMoreElements())
                        {
                            continue label0;
                        }
                        s5 = (String)enumeration3.nextElement();
                    } while(s5.indexOf(as2[l]) == -1);
                    hashmap1 = new HashMap();
                    hashmap1.put("_id", "" + i++);
                    hashmap1.put("_class", "ServiceMonitor");
                    hashmap1.put("_name", as2[l] + " process");
                    hashmap1.put("_service", s5);
                    hashmap1.put("_frequency", "" + (int)c);
                    hashmap1.put("_monitorDescription", "<br>Example Service Monitor watching a process upon  which system operation is dependent");
                    array1.add(hashmap1);
                }

            }
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "Database Subgroup");
            hashmap1.put("_group", "Database_Subgroup");
            array1.add(hashmap1);
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Applications", "mg");
            array.add("Systems");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "System monitors subgroup");
            hashmap.put("_parent", "Examples");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_dependsOn", "Heartbeats 1");
            hashmap.put("_description", "<hr><b>A group of example monitors watching server hardware resources. Use Server monitors to track utilization levels of hardware resources to identify possible performance problems due to over-loaded systems.  This group is dependent on the first monitor in the Heartbeats group and will be disabled  if that monitor reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "CPUMonitor");
            hashmap1.put("_cpu", "0");
            hashmap1.put("_name", "CPU monitor for local machine");
            hashmap1.put("_frequency", "" + (int)c);
            hashmap1.put("_monitorDescription", "<br>An example CPU Utilization Monitor for the CPU on the local machine.  (May not show data for multi-processor machines)");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "MemoryMonitor");
            hashmap1.put("_cpu", "0");
            hashmap1.put("_name", "Memory");
            hashmap1.put("_frequency", "" + (int)c);
            array1.add(hashmap1);
            try
            {
                java.util.Vector vector = COM.dragonflow.SiteView.Platform.getDisks("");
                for(int j = 0; j < vector.size(); j += 2)
                {
                    String s1 = (String)vector.elementAt(j);
                    String s3 = "Disk space on " + vector.elementAt(j + 1);
                    hashmap1 = new HashMap();
                    hashmap1.put("_id", "" + i++);
                    hashmap1.put("_class", "DiskSpaceMonitor");
                    hashmap1.put("_disk", s1);
                    hashmap1.put("_name", s3);
                    hashmap1.put("_frequency", "" + (int)c);
                    array1.add(hashmap1);
                }

            }
            catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
            {
                java.lang.System.err.println("Unable to retrieve disks: " + siteviewexception.getMessage());
            }
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "LogMonitor");
            hashmap1.put("_name", "Log File on error.log");
            hashmap1.put("_logFile", "" + COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "logs" + java.io.File.separator + "error.log");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_match", "/not.found/");
            hashmap1.put("_monitorDescription", "<br>An example Log File Monitor watching SiteView error.log for instances of \"not found\" indicating a SiteView file system error");
            array1.add(hashmap1);
            if(COM.dragonflow.SiteView.Platform.isWindows())
            {
                hashmap1 = new HashMap();
                hashmap1.put("_id", "" + i++);
                hashmap1.put("_class", "ScriptMonitor");
                hashmap1.put("_name", "Example Script Monitor matching net view");
                hashmap1.put("_script", "example_netview.bat");
                hashmap1.put("_frequency", "43200");
                hashmap1.put("_remotescript", "none");
                hashmap1.put("_expression", "/[A-Z]{3,16}/");
                hashmap1.put("_monitorDescription", "<br>An example Script Monitor with a content  match on the results of a NET VIEW command. You can use Script Monitors  to perform system automation tasks with SiteView  recording the results of the script run.");
                array1.add(hashmap1);
            } else
            {
                hashmap1 = new HashMap();
                hashmap1.put("_id", "" + i++);
                hashmap1.put("_class", "ScriptMonitor");
                hashmap1.put("_name", "Example Script Monitor matching number of users");
                hashmap1.put("_script", "example_who.sh");
                hashmap1.put("_frequency", "3600");
                hashmap1.put("_remotescript", "none");
                hashmap1.put("_expression", "/([\\d]{1,6})/");
                hashmap1.put("_monitorDescription", "<br>An example Script Monitor with a content  match on the number of current users (results of 'who' and 'wc-l' commands). You can use Script Monitors  to perform system automation tasks with SiteView  recording the results of the script run.");
                array1.add(hashmap1);
            }
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Systems", "mg");
            array.add("Network");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "Network monitors subgroup");
            hashmap.put("_parent", "Examples");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_dependsOn", "Heartbeats 1");
            hashmap.put("_description", "<hr><b>A group of example monitors watching network resources and connectivity. This group is dependent on the first monitor in the Heartbeats group and will be disabled  if that monitor reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "PingMonitor");
            hashmap1.put("_name", "Ping: dragonflow.com");
            hashmap1.put("_hostname", "dragonflow.com");
            hashmap1.put("_frequency", "43200");
            hashmap1.put("_monitorDescription", "<br>An example Ping Monitor watching basic network connectivity.");
            array1.add(hashmap1);
            for(Enumeration enumeration = COM.dragonflow.SiteView.Platform.getDNSAddresses().elements(); enumeration.hasMoreElements(); array1.add(hashmap1))
            {
                String s = (String)enumeration.nextElement();
                hashmap1 = new HashMap();
                hashmap1.put("_id", "" + i++);
                hashmap1.put("_class", "DNSMonitor");
                hashmap1.put("_server", s);
                hashmap1.put("_hostname", "www." + COM.dragonflow.SiteView.Platform.exampleDomain);
                hashmap1.put("_name", "DNS: " + s);
                hashmap1.put("_frequency", "" + (int)c);
                hashmap1.put("_monitorDescription", "<br>An example DNS Monitor watching network domain name resolution function.");
            }

            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SubGroup");
            hashmap1.put("_name", "SNMP Subgroup");
            hashmap1.put("_group", "SNMP_Subgroup");
            array1.add(hashmap1);
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Network", "mg");
            array.add("SNMP_Subgroup");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "SNMP Subgroup");
            hashmap.put("_parent", "Network");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_dependsOn", "Network 1");
            hashmap.put("_description", "<hr><b>A group of example SNMP and Formula Composite monitors  watching system resources and connectivity. SiteView can send SNMP traps to management consoles  for errors not normally detected by SNMP agents. SiteView can do individual SNMP GET  requests or act as a SNMP listener for multiple SNMP agents.  This group is dependent on the first monitor in the Network group (Ping Monitor) and will be disabled  if that monitor reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SNMPMonitor");
            hashmap1.put("_snmpversion", "V1");
            hashmap1.put("_oidIndex", "0");
            hashmap1.put("_oid", ".1.3.6.1.2.1.1.1");
            hashmap1.put("_timeout", "5");
            hashmap1.put("_community", "public");
            hashmap1.put("_name", "SNMP get:system.sysDescr");
            hashmap1.put("_hostname", "localhost");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_measurementDesc", "System Description");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_monitorDescription", "<br>An example SNMP Monitor reading the system description  on the host indicated. Edit this monitor to include a valid host address that has an SNMP agent answering on port 161.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SNMPMonitor");
            hashmap1.put("_snmpversion", "V1");
            hashmap1.put("_oidIndex", "0");
            hashmap1.put("_oid", ".1.3.6.1.2.1.4.9");
            hashmap1.put("_timeout", "5");
            hashmap1.put("_community", "public");
            hashmap1.put("_name", "SNMP get:ipInDelivers");
            hashmap1.put("_hostname", "localhost");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_measurementDesc", "IP \"In\" Delivered");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_monitorDescription", "<br>An example SNMP Monitor watching IP deliveries on the host indicated. Edit this monitor to include a valid host address that has an SNMP agent answering on port 161.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SNMPMonitor");
            hashmap1.put("_snmpversion", "V1");
            hashmap1.put("_oidIndex", "0");
            hashmap1.put("_oid", ".1.3.6.1.2.1.4.3");
            hashmap1.put("_timeout", "5");
            hashmap1.put("_community", "public");
            hashmap1.put("_name", "SNMP get:ipInReceives");
            hashmap1.put("_hostname", "localhost");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_measurementDesc", "IP \"In\" Received");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_monitorDescription", "<br>An example SNMP Monitor watching TCP connection fails.  Edit this monitor to include a  valid host address that has an SNMP agent answering on port 161.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "BandwidthMonitor");
            hashmap1.put("_delay", "0");
            hashmap1.put("_timeout", "5");
            hashmap1.put("_classifier", "result <= .98\terror");
            hashmap1.put("_classifier", "result <= .90\twarning");
            hashmap1.put("_name", "IP delivers vs. IP Receives on localhost");
            hashmap1.put("_opertaion", "Divide12");
            hashmap1.put("_item", "SNMP_Subgroup 2");
            hashmap1.put("_item", "SNMP_Subgroup 3");
            hashmap1.put("_measurementDesc", "IP \"In\" Received");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_disabled", "checked");
            hashmap1.put("_monitorDescription", "<br>An example Formula Composite Monitor comparing  IP Delivers versus IP Receives on the machine specified in the two monitors above.   Enable this monitor after the other two have been enabled.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "SNMPTrapMonitor");
            hashmap1.put("_name", "SNMP Trap listener");
            hashmap1.put("_hostname", "SNMP.Host.address");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_alerting", "once");
            hashmap1.put("_match", "/.*/");
            hashmap1.put("_monitorDescription", "<br>An example SNMP Trap Monitor that scans a list of SNMP traps sent to this machine. Requires that SNMP agents be configured to send traps to this host address.");
            array1.add(hashmap1);
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "SNMP_Subgroup", "mg");
            array.add("Database_Subgroup");
            array1 = new Array();
            hashmap = new HashMap();
            hashmap.put("_name", "Database Subgroup");
            hashmap.put("_parent", "Applications");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_dependsOn", "Heartbeats 2");
            hashmap.put("_description", "<hr><b>A group of example monitors for checking back-end databases.  These monitors are currently disabled and require additional configuration to set up  database drivers and connections.  This group is dependent on the second monitor in the Heartbeats group and will be disabled  if that monitor reports an error</b><hr>");
            array1.add(hashmap);
            i = 1;
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "DatabaseMonitor");
            hashmap1.put("_driver", "org.gjt.mm.mysql.Driver");
            hashmap1.put("_connectTimeout", "60");
            hashmap1.put("_database", "jdbc:mysql://databaseserver:3306/databasename");
            hashmap1.put("_user", "dbadmin");
            hashmap1.put("_password", "");
            hashmap1.put("_disabled", "on");
            hashmap1.put("_name", "Database Monitor on mySQL");
            hashmap1.put("_query", "select * from dbtable");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_queryTimeout", "60");
            hashmap1.put("_monitorDescription", "<br>An example Database Monitor setup for mySQL database. Requires  third party driver libraries. <ol><li>Download the mySQL drivers from: http://www.worldserver.com/mm.mysql/.  <li>Uncompress the downloaded files and locate mysql.jar </li>Place the mysql.jar in to the SiteView\\java\\lib\\ext directory <li>Stop and restart the SiteView service. <li>Complete the applicable database URL, user, password, and query string.</ol>");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "DatabaseMonitor");
            hashmap1.put("_driver", "com.inet.tds.TdsDriver");
            hashmap1.put("_connectTimeout", "60");
            hashmap1.put("_database", "jdbc:inetdae:serveraddress:1433?database=dbname");
            hashmap1.put("_user", "dbadmin");
            hashmap1.put("_password", "");
            hashmap1.put("_disabled", "on");
            hashmap1.put("_name", "Database Monitor on MS SQL 2000");
            hashmap1.put("_query", "select * from dbtable");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_queryTimeout", "60");
            hashmap1.put("_monitorDescription", "<br>An example Database Monitor setup for Microsoft SQL database.  Uses driver libraries included with SiteView.<br> Complete the applicable database URL, user, password, and query string to enable.");
            array1.add(hashmap1);
            hashmap1 = new HashMap();
            hashmap1.put("_id", "" + i++);
            hashmap1.put("_class", "DatabaseMonitor");
            hashmap1.put("_driver", "oracle.jdbc.driver.OracleDriver");
            hashmap1.put("_connectTimeout", "60");
            hashmap1.put("_database", "jdbc:oracle:thin:@serveraddress:1521:ORCL");
            hashmap1.put("_user", "dbadmin");
            hashmap1.put("_password", "");
            hashmap1.put("_disabled", "on");
            hashmap1.put("_name", "Database Monitor on Oracle 8");
            hashmap1.put("_query", "select * from dbtable");
            hashmap1.put("_frequency", "3600");
            hashmap1.put("_queryTimeout", "60");
            hashmap1.put("_monitorDescription", "<br>An example Database Monitor setup for Oracle 8 database. Requires  third party driver libraries. <ol><li>Download the Oracle Thin JDBC drivers from http://technet.oracle.com/software/download.htm <li>Copy the downloaded driver package into the SiteView\\java\\lib\\ext subdirectory. <li>Stop and restart the SiteView service. <li>Complete the applicable database URL, user, password, and query string.</ol>");
            array1.add(hashmap1);
            hashmap.put("_nextID", "" + i);
            writeFrameFile(array1, "Database_Subgroup", "mg");
            outputStream.println("<p>Adding default daily and weekly reports...<p>");
            outputStream.flush();
            jgl.Array array5 = new Array();
            int i1 = 1;
            for(int j1 = 0; j1 < array.size(); j1++)
            {
                array5.add(createDailyFrame(i1++, (String)array.at(j1)));
                array5.add(createWeeklyFrame(i1++, (String)array.at(j1)));
            }

            writeFrameFile(array5, "history", "config");
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            siteviewgroup.setProperty("_nextReportID", String.valueOf(java.lang.Math.max(i1, 10)));
            siteviewgroup.setProperty("_browser", request.getUserAgent());
            siteviewgroup.saveSettings();
            updateStatusPage(array);
            COM.dragonflow.Page.reportPage.createReportsIndexFiles(request);
        }
        outputStream.println("<p>Will automatically open to the main SiteView panel within next 10 seconds...<p>");
        outputStream.flush();
        outputStream.println("</BODY></HTML>\n");
    }

    public void printBody()
    {
        jgl.Array array = new Array();
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        try
        {
            jgl.Array array1 = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes" + java.io.File.separator + "setup.config");
            jgl.HashMap hashmap = (jgl.HashMap)array1.front();
            if(request.isPost())
            {
                if(request.getValue("licenseAccepted").length() > 0)
                {
                    if(request.getValue("operation").indexOf("addLic") != -1)
                    {
                        String s = updateMCSettings();
                        printRefreshHeader("", "/SiteView/cgi/go.exe/SiteView?page=" + request.getValue("page") + "&account=" + request.getAccount() + "&operation=startoptions&mcstatus=" + s, 2);
                        outputStream.println("\n <IMG SRC=/SiteView/htdocs/artwork/SS_1stXsetup_v2.gif border=\"0\" alt=\"Starting SiteView the First Time\"\n>");
                        outputStream.println("<p>Updating SiteView settings ... <br><br></p><p>Will automatically refresh within 10 seconds...</p><br><br>");
                        printFooter(outputStream);
                        outputStream.flush();
                    } else
                    if(request.getValue("operation").indexOf("Skip") != -1)
                    {
                        printRefreshHeader("", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 2);
                        outputStream.println("\n <IMG SRC=/SiteView/htdocs/artwork/SS_1stXsetup_v2.gif border=\"0\" alt=\"Starting SiteView the First Time\"\n>");
                        outputStream.println("<p>Updating settings ... </p><p>Will start SiteView within 10 seconds...");
                        printFooter(outputStream);
                        outputStream.flush();
                    } else
                    if(request.getValue("operation").indexOf("Copy") != -1 || request.getValue("operation").indexOf("Setupwzd") != -1)
                    {
                        printRefreshHeader("", "/SiteView/cgi/go.exe/SiteView?page=" + request.getValue("page") + "&account=" + request.getAccount() + "&operation=2", 2);
                    } else
                    {
                        printRefreshHeader("", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html", 2);
                        outputStream.println("\n <IMG SRC=/SiteView/htdocs/artwork/SS_1stXsetup_v2.gif border=\"0\" alt=\"Starting SiteView the First Time\"\n>");
                        doInitialSetup();
                        printFooter(outputStream);
                        outputStream.flush();
                    }
                } else
                {
                    doLicense();
                }
            } else
            {
                String s1 = (String)hashmap.get("_productName");
                if(s1.length() > 0)
                {
                    s1 = (String)hashmap.get("_productName");
                } else
                {
                    s1 = "SiteView";
                }
                printBodyHeader("Starting " + s1 + " the First Time");
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                if(request.getValue("operation").indexOf("startoptions") == -1)
                {
                    outputStream.println("\n <IMG SRC=/SiteView/htdocs/artwork/SS_1stXsetup_v2.gif border=\"0\" alt=\"Starting SiteView the First Time\"\n>");
                    outputStream.println("\n<h3>Welcome to " + s1 + "</h3>\n" + "<p>Before we get started with monitoring your web systems, use this form to enter " + " a contact name, e-mail address, and  mail server that " + " SiteView can use for sending e-mail alerts to the SiteView administrator in your organization." + " If you have received license key(s) for this SiteView installation, enter the " + " license key information in the fields provided.</p> ");
                    outputStream.println("\n<p><b>Note:</b>\n Entering data in these fields is not required for the free SiteView evaluation. Licensing is required  for continuing use of the product, to access certain monitor types, or use some setup options. You can enter license keys later on  the General Preferences page.</p> <p>Click <b>Continue</b> to update the SiteView settings and go to the next screen of options on how to quickly get started with SiteView.</p> ");
                    outputStream.println("<FORM NAME=\"dataForm\" ACTION=\"/SiteView/cgi/go.exe/SiteView\" method=\"POST\"><TABLE border=0 cellspacing=3>\n<TR><TD align=\"left\" valign=\"middle\"><b>SiteView Administrator E-mail</b></TD><TD><TD ALIGN=LEFT><input type=text name=\"autoEmail\" size=50 value=\"" + siteviewgroup.getSetting("_autoEmail") + "\"> for receiving SiteView E-mail Alerts</TD></TR>" + "\n<TR><TD align=\"left\" valign=\"middle\"><b>E-mail Server</b></TD>" + "<TD><TD ALIGN=LEFT><input type=text name=\"mailServer\" size=50 value=\"" + siteviewgroup.getSetting("_mailServer") + "\"> for receiving SiteView E-mail Alerts</TD></TR>" + "\n<TR><TD align=\"left\" valign=\"middle\"><b>SiteView License Key</b></TD>" + "<TD><TD ALIGN=LEFT><input type=text name=\"scopeLicense\" size=50 value=\"" + siteviewgroup.getSetting("_license") + "\"> required after 10-day free evaluation</TD></TR>" + "\n<TR><TD align=\"left\" valign=\"middle\"><b>Optional Monitor License</b></TD>" + "<TD><TD ALIGN=LEFT><input type=text name=\"scopeForXLicense\" size=50 value=\"" + siteviewgroup.getSetting("_licenseForX") + "\"> required for extra monitor types</TD></TR>" + "</TABLE>\n");
                    outputStream.println("\n<input type=\"hidden\" name=\"page\" value=\"setup\"><input type=\"hidden\" name=\"account\" value=\"administrator\"><input type=\"hidden\" name=\"licenseAccepted\" value=\"yes\"><input type=\"hidden\" name=\"operation\" value=\"addLicense\"><input type=\"submit\" value=\"Continue\"> with the SiteView setup examples</FORM><p><br><br><br></p>");
                } else
                {
                    COM.dragonflow.SiteView.SiteViewGroup _tmp = siteviewgroup;
                    COM.dragonflow.SiteView.SiteViewGroup.SignalReload();
                    StringBuffer stringbuffer = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + (String)hashmap.get("_startupPage") + ".txt");
                    outputStream.println("\n" + stringbuffer.toString() + "\n");
                    for(Enumeration enumeration = hashmap.values("_showWizardLink"); enumeration.hasMoreElements();)
                    {
                        String s2 = (String)enumeration.nextElement();
                        String s3 = "COM.dragonflow.StandardMonitor." + s2 + "Monitor";
                        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + s2 + "Link.txt");
                        try
                        {
                            COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)java.lang.Class.forName(s3).newInstance();
                            if(COM.dragonflow.Utils.LUtils.isValidSSforXLicense(atomicmonitor) && file.exists())
                            {
                                StringBuffer stringbuffer1 = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + s2 + "Link.txt");
                                outputStream.println("\n" + stringbuffer1.toString() + "\n");
                            } else
                            if(atomicmonitor.getClassProperty("loadable").equals("true") && file.exists())
                            {
                                StringBuffer stringbuffer2 = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + s2 + "Link.txt");
                                outputStream.println("\n" + stringbuffer2.toString() + "\n");
                            } else
                            {
                                outputStream.println("\n");
                            }
                        }
                        catch(java.lang.Exception exception)
                        {
                            if(file.exists())
                            {
                                StringBuffer stringbuffer3 = COM.dragonflow.Utils.FileUtils.readCharFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.view" + java.io.File.separator + s2 + "Link.txt");
                                outputStream.println("\n" + stringbuffer3.toString() + "\n");
                            } else
                            {
                                java.lang.System.err.println(exception + " unable to find wizard link file. ");
                            }
                        }
                    }

                }
                printFooter(outputStream);
            }
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.out.print("Unable to find setup.config");
        }
    }

    void writeFrameFile(jgl.Array array, String s, String s1)
    {
        try
        {
            outputStream.println("<p>Adding '" + s + "' file ...");
            outputStream.flush();
            COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + s + "." + s1, array);
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.err.println(exception + "An error occurred when trying to write the file: " + s);
        }
    }

    private String updateMCSettings()
    {
        String s = "license not updated";
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if(request.getValue("name").length() > 0)
        {
            siteviewgroup.setProperty("_adminName", request.getValue("name"));
        }
        if(request.getValue("autoEmail").length() > 0)
        {
            siteviewgroup.setProperty("_autoEmail", request.getValue("autoEmail"));
        }
        if(request.getValue("mailServer").length() > 0)
        {
            siteviewgroup.setProperty("_mailServer", request.getValue("mailServer"));
        }
        String s1 = siteviewgroup.getSetting("_license");
        String s2 = request.getValue("scopeLicense").trim();
        if(!s2.equals(s1) && COM.dragonflow.Utils.LUtils.isValidLicense(s2))
        {
            siteviewgroup.setProperty("_license", s2);
            s = "license updated";
        }
        String s3 = siteviewgroup.getSetting("_licenseForX");
        String s4 = request.getValue("scopeForXLicense").trim();
        if(!s3.equals(s4) && COM.dragonflow.Utils.LUtils.isValidLicense(s4))
        {
            siteviewgroup.setProperty("_licenseForX", s4);
        }
        String s5 = siteviewgroup.getSetting("_autoEmail");
        if(s5 != null && s5.length() != 0)
        {
            String s6 = siteviewgroup.getSetting("_nextConditionID");
            if(s6 == null || s6.equals(""))
            {
                s6 = "1";
            }
            String s7 = "category eq 'error' and errorCount == 1\tmailto " + s5 + " Default" + "\t" + s6;
            siteviewgroup.setProperty("_alertCondition", s7);
            String s8 = COM.dragonflow.Utils.TextUtils.increment(s6);
            siteviewgroup.setProperty("_nextConditionID", s8);
        }
        siteviewgroup.saveSettings();
        return s;
    }

    void updateStatusPage(jgl.Array array)
    {
        outputStream.println("<p>Updating status pages and config file ...<p>");
        outputStream.flush();
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        try
        {
            siteviewgroup.startingUp = true;
            siteviewgroup.saveSettings();
            for(int i = 0; i < array.size(); i++)
            {
                COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages((String)array.at(i));
            }

            siteviewgroup.startingUp = false;
        }
        catch(java.lang.Exception exception) { }
    }

    jgl.HashMap createHistoryFrame(int i, String s)
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("startHour", "now");
        hashmapordered.add("startDay", "today");
        hashmapordered.add("relative", "-1");
        hashmapordered.add("precision", "default");
        hashmapordered.add("monitors", s);
        hashmapordered.add("id", String.valueOf(i));
        hashmapordered.add("basic", "checked");
        return hashmapordered;
    }

    jgl.HashMap createDailyFrame(int i, String s)
    {
        jgl.HashMap hashmap = createHistoryFrame(i, s);
        hashmap.add("window", String.valueOf(0x15180));
        hashmap.add("schedule", "weekday\tM,T,W,R,F,S,U\t01:00");
        return hashmap;
    }

    jgl.HashMap createWeeklyFrame(int i, String s)
    {
        jgl.HashMap hashmap = createHistoryFrame(i, s);
        hashmap.add("window", String.valueOf(0x93a80));
        hashmap.add("schedule", "weekday\tU\t02:00");
        return hashmap;
    }

    public static void main(String args[])
        throws java.lang.Exception
    {
        (new setupPage()).handleRequest();
    }
}
