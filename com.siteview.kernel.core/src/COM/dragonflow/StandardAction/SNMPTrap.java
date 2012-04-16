/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;

import com.netaphor.snmp.CommunityTarget;
import com.netaphor.snmp.Counter;
import com.netaphor.snmp.Gauge;
import com.netaphor.snmp.GenericAddress;
import com.netaphor.snmp.Integer32;
import com.netaphor.snmp.IpAddress;
import com.netaphor.snmp.OID;
import com.netaphor.snmp.OctetString;
import com.netaphor.snmp.Opaque;
import com.netaphor.snmp.PDU;
import com.netaphor.snmp.SecureTarget;
import com.netaphor.snmp.Session;
import com.netaphor.snmp.TimeTicks;
import com.netaphor.snmp.TrapPDU;
import com.netaphor.snmp.UsmUser;
import com.netaphor.snmp.VariableBinding;

// Referenced classes of package COM.dragonflow.StandardAction:
// SNMPVariableBinding

public class SNMPTrap extends COM.dragonflow.SiteView.Action
{

    public static COM.dragonflow.Properties.StringProperty pMessage;
    public static COM.dragonflow.Properties.StringProperty pTemplate;
    public static COM.dragonflow.Properties.StringProperty pSNMPSetting;
    public static COM.dragonflow.Properties.StringProperty pHost;
    public static COM.dragonflow.Properties.StringProperty pObjectID;
    public static COM.dragonflow.Properties.StringProperty pCommunity;
    public static COM.dragonflow.Properties.StringProperty pGeneric;
    public static COM.dragonflow.Properties.StringProperty pSpecific;
    public static COM.dragonflow.Properties.StringProperty pSNMPVersion;
    static String SYSTEM_OID = "1.3.6.1.2.1.";
    public static String OIDS[][] = {
        {
            "HP Open View Event", ".1.3.6.1.4.1.11.2.17.1"
        }, {
            "System - MIB-II", ".1.3.6.1.2.1.1"
        }, {
            "Microsoft - Vendor MIB", ".1.3.6.1.4.1.311.1.1.3.1.2"
        }, {
            "System - Host Resources MIB", ".1.3.6.1.2.1.25.1"
        }
    };
    public static String TRAPIDS[][] = {
        {
            "cold start", "0"
        }, {
            "warm start", "1"
        }, {
            "link down", "2"
        }, {
            "link up", "3"
        }, {
            "enterprise specific", "6"
        }
    };
    public static String VERSION[][] = {
        {
            "V1", "V1"
        }, {
            "V2c", "V2"
        }
    };
    private static java.net.InetAddress localInetAddress = null;

    public void initializeFromArguments(jgl.Array array, jgl.HashMap hashmap)
    {
        if(array.size() > 0)
        {
            setProperty(pMessage, ((String)array.at(0)).replace('^', ' '));
        }
        if(array.size() > 1)
        {
            setProperty(pTemplate, array.at(1));
        } else
        {
            setProperty(pTemplate, "Default");
        }
        unsetProperty(pSNMPSetting);
        for(Enumeration enumeration = hashmap.values("_id"); enumeration.hasMoreElements(); addProperty(pSNMPSetting, (String)enumeration.nextElement())) { }
        Enumeration enumeration1 = hashmap.keys();
        while (enumeration1.hasMoreElements()) {
            String s = (String)enumeration1.nextElement();
            if(s.startsWith("_snmp"))
            {
                setProperty(s, (String)hashmap.get(s));
            }
        } 
    }

    public String getActionString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("SNMPTrap");
        String s = getProperty(pMessage);
        String s1 = getProperty(pTemplate);
        if(s1.length() > 0 && s.length() == 0)
        {
            s = " ";
        }
        if(s.length() > 0)
        {
            stringbuffer.append(" ");
            stringbuffer.append(s.replace(' ', '^'));
            if(s1.length() > 0)
            {
                stringbuffer.append(" ");
                stringbuffer.append(s1);
            }
        }
        if(getProperty(pHost).length() > 0)
        {
            appendProperty(stringbuffer, pHost);
            appendProperty(stringbuffer, pObjectID);
            appendProperty(stringbuffer, pCommunity);
            appendProperty(stringbuffer, pGeneric);
            appendProperty(stringbuffer, pSpecific);
        } else
        {
            for(Enumeration enumeration = getMultipleValues(pSNMPSetting); enumeration.hasMoreElements(); stringbuffer.append((String)enumeration.nextElement()))
            {
                stringbuffer.append(" _id=");
            }

        }
        return stringbuffer.toString();
    }

    public String getActionDescription()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String)getClassProperty("label"));
        String s = getProperty(pMessage);
        if(s.length() > 0 && !s.equals(" "))
        {
            stringbuffer.append(" \"" + getProperty(pMessage) + "\"");
        }
        String s1 = getProperty(pTemplate);
        if(s1.length() > 0 && !s1.equals("Default"))
        {
            stringbuffer.append(" " + s1);
        }
        if(getProperty(pHost).length() == 0)
        {
            String s2 = "_name=";
            Enumeration enumeration = getMultipleValues(pSNMPSetting);
            boolean flag = true;
            boolean flag1 = false;
            while (enumeration.hasMoreElements()) {
                String s3 = (String)enumeration.nextElement();
                String s4 = "";
                if(!s3.equals("default"))
                {
                    String s5 = getOwner().getSNMPSettings(s3);
                    if(s5.length() > 0)
                    {
                        int i = s5.indexOf(s2);
                        if(i >= 0)
                        {
                            int j = s5.indexOf(" ", i);
                            if(j == -1)
                            {
                                j = s5.length();
                            }
                            s4 = s5.substring(i + s2.length(), j);
                            s4 = COM.dragonflow.Utils.TextUtils.storedValueToValue(s4);
                        }
                    }
                    if(s4.length() > 0)
                    {
                        if(!flag)
                        {
                            stringbuffer.append(", ");
                        } else
                        {
                            stringbuffer.append(" to ");
                            if(flag1)
                            {
                                stringbuffer.append("Default, ");
                            }
                            flag = false;
                        }
                        stringbuffer.append(s4);
                    }
                } else
                {
                    flag1 = true;
                }
            } 
        }
        return stringbuffer.toString();
    }

    public boolean defaultsAreSet(jgl.HashMap hashmap)
    {
        boolean flag = true;
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_snmpHost");
        if(s.length() == 0)
        {
            flag = false;
        }
        return flag;
    }

    public boolean showOptionalProperties()
    {
        return getProperty(pHost).length() > 0;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap)
    {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest, COM.dragonflow.Page.CGI cgi)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        if(scalarproperty == pTemplate)
        {
            java.util.Vector vector = COM.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.snmp", httprequest);
            java.util.Vector vector4 = new Vector();
            for(int l = 0; l < vector.size(); l++)
            {
                vector4.addElement(vector.elementAt(l));
                vector4.addElement(vector.elementAt(l));
            }

            return vector4;
        }
        if(scalarproperty == pSNMPSetting)
        {
            Enumeration enumeration = null;
            if(httprequest.isStandardAccount())
            {
                jgl.HashMap hashmap = cgi.getMasterConfig();
                enumeration = hashmap.values("_additionalSNMP");
            } else
            {
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                COM.dragonflow.SiteView.SiteViewObject siteviewobject = siteviewgroup.getElement(httprequest.getAccount());
                enumeration = siteviewobject.getMultipleValues("_additionalSNMP");
            }
            java.util.Vector vector5 = new Vector();
            vector5.addElement("default");
            vector5.addElement("Default");
            jgl.HashMap hashmap1;
            for(; enumeration.hasMoreElements(); vector5.addElement(hashmap1.get("_name")))
            {
                hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap((String)enumeration.nextElement());
                vector5.addElement(hashmap1.get("_id"));
            }

            return vector5;
        }
        if(scalarproperty == pObjectID)
        {
            java.util.Vector vector1 = new Vector();
            for(int i = 0; i < OIDS.length; i++)
            {
                vector1.addElement(OIDS[i][1]);
                vector1.addElement(OIDS[i][0]);
            }

            return vector1;
        }
        if(scalarproperty == pSNMPVersion)
        {
            java.util.Vector vector2 = new Vector();
            for(int j = 0; j < VERSION.length; j++)
            {
                vector2.addElement(VERSION[j][1]);
                vector2.addElement(VERSION[j][0]);
            }

            return vector2;
        }
        if(scalarproperty == pGeneric)
        {
            java.util.Vector vector3 = new Vector();
            for(int k = 0; k < TRAPIDS.length; k++)
            {
                vector3.addElement(TRAPIDS[k][1]);
                vector3.addElement(TRAPIDS[k][0]);
            }

            return vector3;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public SNMPTrap()
    {
        snmpClass = getClass();
        runType = 2;
    }

    public boolean execute()
    {
        boolean flag = false;
        if(hasValue("_id"))
        {
            jgl.Array array = new Array();
            Enumeration enumeration = getMultipleValues("_id");
            while (enumeration.hasMoreElements()) {
                String s = (String)enumeration.nextElement();
                boolean flag1 = true;
                boolean flag2 = false;
                String s1 = "default";
                if(s.equals("default"))
                {
                    jgl.Array array1 = getCurrentPropertyNames();
                    for(int i = 0; i < array1.size(); i++)
                    {
                        String s4 = (String)array1.at(i);
                        if(s4.startsWith("_snmp"))
                        {
                            unsetProperty(s4);
                        }
                    }

                } else
                {
                    String s2 = getOwner().getSNMPSettings(s);
                    if(s2.length() > 0)
                    {
                        jgl.HashMap hashmap = COM.dragonflow.Utils.TextUtils.stringToHashMap(s2);
                        s1 = (String)hashmap.get("_name");
                        Enumeration enumeration1 = hashmap.keys();
                        while (enumeration1.hasMoreElements()) {
                            String s5 = (String)enumeration1.nextElement();
                            if(s5.startsWith("_snmp"))
                            {
                                setProperty(s5, (String)hashmap.get(s5));
                            }
                        } 
                        flag2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_disabled").length() > 0;
                    } else
                    {
                        flag1 = false;
                    }
                }
                if(flag1)
                {
                    if(!flag2)
                    {
                        messageBuffer = new StringBuffer();
                        boolean flag3 = execute1(s1);
                        String s3 = messageBuffer.toString();
                        if(!flag3)
                        {
                            array.add(s);
                            COM.dragonflow.Log.LogManager.log(getSetting(COM.dragonflow.SiteView.Monitor.pErrorLogName), s3 + ", " + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName) + " " + monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pOwnerID) + ", \"" + s1 + "\" SNMP settings");
                        }
                        COM.dragonflow.Log.LogManager.log("Progress", monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pOwnerID) + "\t" + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName) + "\t" + s3);
                    }
                } else
                {
                    COM.dragonflow.Log.LogManager.log(getSetting(COM.dragonflow.SiteView.Monitor.pErrorLogName), "Missing SNMP setting id " + s + ", " + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName) + " " + monitor.getProperty(COM.dragonflow.SiteView.SiteViewObject.pOwnerID));
                }
            }
            messageBuffer.setLength(0);
            flag = array.size() == 0;
        } else
        {
            flag = execute1("");
        }
        return flag;
    }

    public boolean execute1(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = "Default";
        if(args.length > 1)
        {
            s1 = args[1];
        }
        String s2 = createMessage(stringbuffer, "templates.snmp", s1);
        String s3 = monitor.createFromTemplate(getMessage()) + stringbuffer.toString();
        String s4 = "";
        String s5 = "";
        String as[] = parseOutKeyIfExists("[Agent Host:", s3, s4);
        s3 = as[0];
        s4 = as[1];
        as = parseOutKeyIfExists("[Specific:", s3, s5);
        s3 = as[0];
        s5 = as[1];
        if(s2.length() == 0)
        {
            s2 = sendTrap(s3, s4, s5);
        }
        boolean flag = s2.length() == 0;
        String s6 = "Snmp trap sent";
        if(!flag)
        {
            s6 = "SNMP TRAP NOT SENT";
        }
        if(s.length() != 0 && !s.equals("default"))
        {
            s6 = s6 + ", " + s;
        }
        messageBuffer.append(s6 + ", " + s3);
        logAlert(baseAlertLogEntry(s6, s3, flag) + " alert-result: " + s2 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-trap: " + this + COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        return flag;
    }

    public String getSetting(String s)
    {
        String s1 = super.getSetting(s);
        if(s1.length() == 0)
        {
            if(s.equals("_snmpPort"))
            {
                s1 = "162";
            } else
            if(s.equals("_snmpVariable"))
            {
                s1 = "1.0";
            } else
            if(s.equals("_specificTrap"))
            {
                s1 = "0";
            }
        }
        return s1;
    }

    String getMessage()
    {
        String s = "";
        if(args.length > 0)
        {
            s = args[0];
        }
        s = s.replace('$', '\t');
        s = s.replace('^', ' ');
        if(s.equals(" "))
        {
            s = "";
        }
        return s;
    }

    public String sendTrap(String s)
    {
        return sendTrap(s, "", "");
    }

    public String sendTrap(String s, String s1, String s2)
    {
        boolean flag = false;
        String s3 = "";
        try
        {
            String s4 = getSetting("_snmpHost");
            String s5 = getSetting("_snmpPort");
            String s6 = getSetting("_snmpCommunity");
            String s7 = getSetting("_snmpObjectID");
            String s8 = getSetting("_snmpGeneric");
            if(s2.length() == 0)
            {
                s2 = getSetting("_snmpSpecific");
            }
            String s9 = getSetting("_snmpTrapVersion");
            String s10 = getSetting("_snmpPrefix");
            String s11 = getSetting("_snmpV3Username");
            String s12 = getSetting("_snmpV3Password");
            String s13 = "";
            if(s9.length() <= 0)
            {
                s9 = "V1";
            }
            if(s1.length() == 0)
            {
                String s14 = getSetting("_webserverAddress");
                if(COM.dragonflow.Utils.TextUtils.onlyChars(s14, "0123456789."))
                {
                    s1 = s14;
                }
            }
            jgl.Array array = new Array();
            if(s.trim().length() > 0)
            {
                String s15 = getSetting("_snmpVariable");
                String s16 = getSetting("_snmpVariableType");
                if(s16.length() == 0)
                {
                    s16 = "STRING";
                }
                jgl.Array array1 = new Array();
                array1 = COM.dragonflow.SiteView.Platform.split('\n', s);
                Enumeration enumeration = array1.elements();
                while (enumeration.hasMoreElements()) {
                    String s17 = (String)enumeration.nextElement();
                    String as1[] = parseOutKeyIfExists("[Command:", s17, s13);
                    s17 = as1[0];
                    if(as1[1].equals("SET"))
                    {
                        s13 = as1[1];
                    } else
                    {
                        String as2[] = parseOutKeyIfExists("[OID:", s17, s15);
                        s17 = as2[0];
                        s15 = as2[1];
                        as2 = parseOutKeyIfExists("[Type:", s17, s16);
                        s17 = as2[0];
                        s16 = as2[1];
                        s17 = COM.dragonflow.Utils.TextUtils.removeChars(s17, "\r");
                        array.add(new SNMPVariableBinding(s15, s16, s17));
                        int j = s15.lastIndexOf(".");
                        if(j != -1)
                        {
                            String s18 = s15.substring(0, j + 1);
                            String s19 = s15.substring(j + 1);
                            s15 = s18 + COM.dragonflow.Utils.TextUtils.increment(s19);
                        }
                    }
                }
            }
            String as[] = COM.dragonflow.Utils.TextUtils.split(s4, ",");
            for(int i = 0; i < as.length; i++)
            {
                s3 = COM.dragonflow.StandardAction.SNMPTrap.SendSNMPTrap(as[i].trim(), s5, s6, s7, s8, s2, s1, array, s9, s13, s10, s11, s12);
                if(i == 0 && s3.length() == 0)
                {
                    flag = true;
                } else
                {
                    COM.dragonflow.Log.LogManager.log("Error", "Error sending secondary SNMP Trap to " + as[i] + ": " + s3);
                }
            }

        }
        catch(java.lang.Exception exception)
        {
            s3 = "internal error (" + exception + ")";
        }
        if(!flag)
        {
            s3 = "SNMP TRAP ERROR: " + s3;
        }
        return s3;
    }

    public String[] parseOutKeyIfExists(String s, String s1, String s2)
    {
        String as[] = new String[2];
        as[0] = s1;
        as[1] = s2;
        int i = s1.indexOf(s);
        if(i != -1)
        {
            int j = s1.indexOf("]", i);
            if(j != -1)
            {
                as[1] = s1.substring(i + s.length(), j);
                as[1] = as[1].trim();
                as[0] = s1.substring(0, i) + s1.substring(j + 1, s1.length());
                as[0] = as[0].trim();
            }
        }
        return as;
    }

    private static java.net.InetAddress getLocalAddress()
    {
        if(localInetAddress == null)
        {
            try
            {
                localInetAddress = java.net.InetAddress.getLocalHost();
            }
            catch(java.net.UnknownHostException unknownhostexception) { }
        }
        return localInetAddress;
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     * @param s6
     * @param array
     * @param s7
     * @param s8
     * @param s9
     * @param s10
     * @param s11
     * @return
     */
    private static String SendSNMPTrap(String s, String s1, String s2, String s3, String s4, String s5, String s6, jgl.Array array, 
            String s7, String s8, String s9, String s10, String s11)
    {
        String s12;
        com.netaphor.snmp.Session session;
        java.lang.Object obj;
        java.lang.Object obj1;
        com.netaphor.snmp.VariableBinding avariablebinding[];
        if(s3.length() == 0)
        {
            return "missing SNMP Object ID";
        }
        
        s12 = "";
        if(s.startsWith("\\"))
        {
            s = s.substring(2);
        }
        java.net.InetAddress inetaddress = null;
        if(s6.length() > 0)
        {
            try
            {
                inetaddress = java.net.InetAddress.getByName(s6);
            }
            catch(java.net.UnknownHostException unknownhostexception)
            {
                return "could not find address of agent host: " + s6;
            }
        }
        if(inetaddress == null)
        {
            inetaddress = COM.dragonflow.StandardAction.SNMPTrap.getLocalAddress();
        }
        if(inetaddress == null)
        {
            return "could not find address of local host";
        }
        
        session = new Session();
        try {
        com.netaphor.snmp.IpAddress ipaddress = new IpAddress(inetaddress);
        com.netaphor.snmp.GenericAddress genericaddress = new GenericAddress(ipaddress);
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        com.netaphor.snmp.GenericAddress genericaddress1 = new GenericAddress(s + ":" + s1);
        if(s3.startsWith("."))
        {
            s3 = s3.substring(1);
        }
        com.netaphor.snmp.OID oid = null;
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_snmpTrapNoSystemNotify").length() > 0)
        {
            oid = new OID(s3);
        } else
        if(s9.length() > 0)
        {
            if(s9.startsWith("."))
            {
                s9 = s9.substring(1);
            }
            if(!s9.endsWith("."))
            {
                s9 = s9 + ".";
            }
            oid = new OID(s9 + s3);
        } else
        {
            oid = new OID(SYSTEM_OID + s3);
        }
        obj = null;
        long l = COM.dragonflow.SiteView.Platform.timeMillis();
        l /= 1000L;
        com.netaphor.snmp.TimeTicks timeticks = new TimeTicks(l);
        if(s7.equals("V1"))
        {
            COM.dragonflow.Utils.TextUtils.debugPrint(" in V1");
            obj = new TrapPDU();
            ((com.netaphor.snmp.TrapPDU)obj).setGenericTrapType(new Integer32(COM.dragonflow.Utils.TextUtils.toInt(s4)));
            ((com.netaphor.snmp.TrapPDU)obj).setSpecificTrapType(new Integer32(COM.dragonflow.Utils.TextUtils.toInt(s5)));
            ((com.netaphor.snmp.TrapPDU)obj).setEnterprise(oid);
            ((com.netaphor.snmp.TrapPDU)obj).setTimestamp(timeticks);
            if(genericaddress != null)
            {
                ((com.netaphor.snmp.TrapPDU)obj).setAgentAddress(genericaddress);
            }
        } else
        {
            obj = new PDU(timeticks, oid);
        }
        obj1 = null;
        if(s7.equals("V3"))
        {
            com.netaphor.snmp.SecureTarget securetarget = null;
            com.netaphor.snmp.UsmUser usmuser = null;
            byte abyte0[] = {
                1, 5, 6, 7
            };
            session.setLocalEngine(abyte0, 5, 1000);
            usmuser = new UsmUser(s10, s11, com.netaphor.snmp.Session.MD5AUTHENTICATION, null, null);
            securetarget = new SecureTarget(genericaddress1, abyte0, usmuser);
            obj1 = securetarget;
        } else
        {
            com.netaphor.snmp.CommunityTarget communitytarget = new CommunityTarget(genericaddress1);
            communitytarget.setCommunity(s2);
            obj1 = communitytarget;
        }
        if(s7.equals("V1"))
        {
            ((com.netaphor.snmp.Target) (obj1)).setVersion(0);
        } else
        if(s7.equals("V2"))
        {
            ((com.netaphor.snmp.Target) (obj1)).setVersion(1);
        } else
        if(s7.equals("V3"))
        {
            ((com.netaphor.snmp.Target) (obj1)).setVersion(3);
        }
        ((com.netaphor.snmp.Target) (obj1)).setRetries(2);
        ((com.netaphor.snmp.Target) (obj1)).setTimeout(500L);
        Enumeration enumeration = array.elements();
        avariablebinding = new com.netaphor.snmp.VariableBinding[array.size()];
        int i = 0;
        while(enumeration.hasMoreElements()) 
        {
            COM.dragonflow.StandardAction.SNMPVariableBinding snmpvariablebinding = (COM.dragonflow.StandardAction.SNMPVariableBinding)enumeration.nextElement();
            String s13 = snmpvariablebinding.objectID;
            if(s13.startsWith("."))
            {
                s13 = s13.substring(1);
            }
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_snmpTrapNoSystem").length() <= 0)
            {
                if(s9.length() > 0)
                {
                    if(s9.startsWith("."))
                    {
                        s9 = s9.substring(1);
                    }
                    if(!s9.endsWith("."))
                    {
                        s9 = s9 + ".";
                    }
                    s13 = s9 + s13;
                } else
                {
                    s13 = SYSTEM_OID + s13;
                }
            }
            com.netaphor.snmp.OID oid1 = new OID(s13);
            if(oid1.getValue() == null)
            {
                java.lang.System.err.println("Invalid OID argument: " + oid1);
            } else
            {
                avariablebinding[i++] = COM.dragonflow.StandardAction.SNMPTrap.addVarBind(((com.netaphor.snmp.PDU) (obj)), oid1, snmpvariablebinding.type, snmpvariablebinding.value);
            }
        }
        ((com.netaphor.snmp.PDU) (obj)).setVariableBindingList(avariablebinding);
        if(s8.length() > 0)
        {
            session.set(((com.netaphor.snmp.PDU) (obj)), ((com.netaphor.snmp.Target) (obj1)));
        } else
        {
            session.sendTrap(((com.netaphor.snmp.PDU) (obj)), ((com.netaphor.snmp.Target) (obj1)));
        }
        session.close();
        }
        catch (RuntimeException exception1) {
            session.close();
            throw exception1;
        }
        catch (java.lang.Exception exception) {
        s12 = "SNMP Trap Error Sending PDU : " + exception.getMessage();
        session.close();
        }

        return s12;
    }

    static com.netaphor.snmp.VariableBinding addVarBind(com.netaphor.snmp.PDU pdu, com.netaphor.snmp.OID oid, String s, String s1)
    {
        com.netaphor.snmp.VariableBinding variablebinding = new VariableBinding();
        variablebinding.setOid(oid);
        java.lang.Object obj = null;
        if(s.equals("STRING"))
        {
            obj = new OctetString(s1);
        } else
        if(s.equals("INTEGER"))
        {
            obj = new Integer32(COM.dragonflow.Utils.TextUtils.toInt(s1));
        } else
        if(s.equals("GAUGE"))
        {
            obj = new Gauge(COM.dragonflow.Utils.TextUtils.toLong(s1));
        } else
        if(s.equals("TIMETICKS"))
        {
            obj = new TimeTicks(COM.dragonflow.Utils.TextUtils.toLong(s1));
        } else
        if(s.equals("OPAQUE"))
        {
            obj = new Opaque(s1);
        } else
        if(s.equals("IPADDRESS"))
        {
            obj = new IpAddress(s1);
        } else
        if(s.equals("COUNTER"))
        {
            obj = new Counter(COM.dragonflow.Utils.TextUtils.toLong(s1));
        } else
        if(s.equals("OID"))
        {
            obj = new OID(s1);
        } else
        {
            obj = new OctetString("invalid type");
            java.lang.System.err.println("Sending SNMP Trap Invalid variable type: " + s + " Valid types are: STRING, INTEGER, GAUGE, TIMETICKS, OPAQUE, IPADDRESS, COUNTER, OID");
            java.lang.System.out.println("Sending SNMP Trap Invalid variable type: " + s + " Valid types are: STRING, INTEGER, GAUGE, TIMETICKS, OPAQUE, IPADDRESS, COUNTER, OID");
            COM.dragonflow.Log.LogManager.log("Error", "Sending SNMP Trap. Look in templates.snmp directory for [Type: ]. Invalid variable type: " + s + " Valid types are: STRING, INTEGER, GAUGE, TIMETICKS, OPAQUE, IPADDRESS, COUNTER, OID");
        }
        variablebinding.setVariable(((com.netaphor.snmp.Variable) (obj)));
        return variablebinding;
    }

    public static void main(String args[])
    {
        java.lang.System.out.println("SNMPTrap address message oid trap specific community\n");
        int i = 0;
        boolean flag = false;
        if(args.length > 0 && args[0].equals("-once"))
        {
            i = 1;
            flag = true;
        }
        String s = "206.168.191.19";
        if(args.length > i)
        {
            s = args[i];
        }
        String s1 = "this is a test";
        if(args.length > i + 1)
        {
            s1 = args[i + 1];
        }
        String s2 = ".1.3.6.1.4.1.11.2.17.1";
        if(args.length > i + 2)
        {
            s2 = args[i + 2];
        }
        String s3 = "6";
        if(args.length > i + 3)
        {
            s3 = args[i + 3];
        }
        String s4 = "2";
        if(args.length > i + 4)
        {
            s4 = args[i + 4];
        }
        String s5 = "public";
        if(args.length > i + 5)
        {
            s5 = args[i + 5];
        }
        String s6 = "";
        if(args.length > i + 6)
        {
            s6 = args[i + 6];
        }
        jgl.Array array = new Array();
        com.netaphor.snmp.VariableBinding variablebinding = new VariableBinding();
        com.netaphor.snmp.OID oid = new OID(s2);
        variablebinding.setOid(oid);
        com.netaphor.snmp.OctetString octetstring = new OctetString(s1);
        variablebinding.setVariable(octetstring);
        array.add(variablebinding);
        while(true)
        {
            long l = java.lang.System.currentTimeMillis();
            java.lang.System.out.println("sending trap...");
            java.lang.System.out.println(" to: " + s);
            java.lang.System.out.println(" message: " + s1);
            java.lang.System.out.println(" oid: " + s2);
            java.lang.System.out.println(" type: " + s3);
            java.lang.System.out.println(" specific: " + s4);
            java.lang.System.out.println(" community: " + s5);
            COM.dragonflow.StandardAction.SNMPTrap.SendSNMPTrap(s, "162", s5, s2, s3, s4, s6, array, "V1", "", "", "", "");
            long l1 = java.lang.System.currentTimeMillis();
            java.lang.System.out.println("trap sent, " + (l1 - l) + " milliseconds");
            if(flag)
            {
                java.lang.System.exit(0);
            }
            COM.dragonflow.SiteView.Platform.sleep(2000L);
        }
    }

    static 
    {
        pMessage = new StringProperty("_message", "", "");
        pMessage.setDisplayText("Message", "optional prefix to SNMP trap message");
        pMessage.setParameterOptions(true, 1, false);
        pTemplate = new ScalarProperty("_template", "Default");
        pTemplate.setDisplayText("Template", "the template used to create the SNMP message.  Each line in the template will be sent as a separate SNMP variable.");
        pTemplate.setParameterOptions(true, 2, false);
        pSNMPSetting = new ScalarProperty("_snmpSetting", "");
        pSNMPSetting.setDisplayText("To", "SNMP settings used to send this trap.");
        pSNMPSetting.setParameterOptions(true, 3, false);
        ((COM.dragonflow.Properties.ScalarProperty)pSNMPSetting).multiple = true;
        pHost = new StringProperty("_snmpHost", "");
        pHost.setDisplayText("Host", "Enter the host name or IP address of the machine that will receive this trap - for example snmp." + COM.dragonflow.SiteView.Platform.exampleDomain + " or 206.168.191.20. This is the machine running the SNMP console.");
        pHost.setParameterOptions(true, 4, true);
        pHost.isOptional = true;
        pObjectID = new ScalarProperty("_snmpObjectID", "");
        pObjectID.setDisplayText("Object", "The SNMP object that is sending the trap. For example .1.3.6.1.2.1.1 is the &quot;system&quot; object from MIB-II (RFC 1213).");
        pObjectID.setParameterOptions(true, 5, true);
        pObjectID.isOptional = true;
        pCommunity = new StringProperty("_snmpCommunity", "");
        pCommunity.setDisplayText("Host", "The SNMP community name used for this trap - usually this is &quot;public&quot;.");
        pCommunity.setParameterOptions(true, 6, true);
        pCommunity.isOptional = true;
        pGeneric = new ScalarProperty("_snmpGeneric", "");
        pGeneric.setDisplayText("Generic Trap", "Generic trap type.  If the generic trap type is &quot;enterprise specific&quot;, then fill in the specific trap type, which is a number");
        pGeneric.setParameterOptions(true, 7, true);
        pGeneric.isOptional = true;
        pSpecific = new StringProperty("_snmpSpecific", "");
        pSpecific.setDisplayText("Specific Trap", "Specific trap type.");
        pSpecific.setParameterOptions(true, 8, true);
        pSpecific.isOptional = true;
        pSNMPVersion = new ScalarProperty("_snmpTrapVersion", "V1");
        pSNMPVersion.setDisplayText("SNMP Trap Version", "The version of the SNMP Trap being sent");
        pSNMPVersion.setParameterOptions(true, 9, true);
        pSNMPVersion.isOptional = true;
        COM.dragonflow.Properties.StringProperty astringproperty[] = {
            pMessage, pTemplate, pSNMPSetting, pHost, pObjectID, pCommunity, pGeneric, pSpecific, pSNMPVersion
        };
        COM.dragonflow.StandardAction.SNMPTrap.addProperties("COM.dragonflow.StandardAction.SNMPTrap", astringproperty);
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "description", "Sends an SNMP trap.");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "help", "AlertSNMPTrap.htm");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "title", "Send SNMP Trap");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "name", "SNMP Trap");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "label", "Send SNMP Trap");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "class", "SNMPTrap");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "prefs", "snmpPrefs");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "accountPrefs", "snmpPrefs");
        COM.dragonflow.StandardAction.SNMPTrap.setClassProperty("COM.dragonflow.StandardAction.SNMPTrap", "loadable", "true");
    }
}
