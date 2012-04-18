/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.StandardAction.SNMPTrap;

public class SNMPDefaultPreferences extends com.dragonflow.SiteView.Preferences {

    public static com.dragonflow.Properties.StringProperty pSnmpHost;

    public static com.dragonflow.Properties.StringProperty pSnmpObjectID;

    public static com.dragonflow.Properties.StringProperty pSnmpObjectIDOther;

    public static com.dragonflow.Properties.StringProperty pSnmpCommunity;

    public static com.dragonflow.Properties.StringProperty pSnmpGeneric;

    public static com.dragonflow.Properties.StringProperty pSnmpSpecific;

    public static com.dragonflow.Properties.StringProperty pSnmpTrapVersion;

    public SNMPDefaultPreferences() {
    }

    public java.util.Vector test() throws java.lang.Exception {
        java.util.Vector vector = new Vector();
        String s = "Test Message";
        com.dragonflow.StandardAction.SNMPTrap snmptrap = new SNMPTrap();
        if (getProperty("_snmpHost").length() > 0) {
            snmptrap.setProperty("_snmpHost", getProperty("_snmpHost"));
            snmptrap.setProperty("_snmpCommunity", getProperty("_snmpCommunity"));
            snmptrap.setProperty("_snmpGeneric", getProperty("_snmpGeneric"));
            snmptrap.setProperty("_snmpSpecific", getProperty("_snmpSpecific"));
            snmptrap.setProperty("_snmpObjectID", getProperty("_snmpObjectID"));
        }
        Array array = new Array();
        array.add(s);
        snmptrap.setArgs(array);
        String s2 = snmptrap.sendTrap(s);
        if (s2.length() == 0) {
            vector.add("The test trap was sent successfully. (" + snmptrap + ")");
        } else {
            String s1 = "The SNMP Trap could not be sent. The error was: " + s2;
            throw new Exception(s1);
        }
        return vector;
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty == pSnmpObjectID && s.equals("0")) {
            String s1 = (String) hashmap.get(pSnmpObjectIDOther.getName());
            if (s1 == null || s1.equals("")) {
                hashmap1.put(pSnmpObjectIDOther, "Enter a value for the SNMP Object ID.");
            }
        } else if (stringproperty == pSnmpGeneric && s.equals("6")) {
            String s2 = (String) hashmap.get(pSnmpSpecific.getName());
            if (s2 == null || s2.equals("")) {
                hashmap1.put(pSnmpSpecific, "Enter a value for the Trap ID.");
            }
        } else if (stringproperty == pSnmpSpecific && s.equals("0")) {
            s = "";
        }
        return s;
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest) throws com.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        if (scalarproperty == pSnmpObjectID) {
            vector.addElement(".1.3.6.1.4.1.11.2.17.1");
            vector.addElement("HP Open View Event");
            vector.addElement(".1.3.6.1.2.1.1");
            vector.addElement("System - MIB-II");
            vector.addElement(".1.3.6.1.4.1.311.1.1.3.1.2");
            vector.addElement("Microsoft - Vendor MIB");
            vector.addElement(".1.3.6.1.2.1.25.1");
            vector.addElement("System - Host Resources MIB");
            vector.addElement("0");
            vector.addElement("other...");
        } else if (scalarproperty == pSnmpGeneric) {
            vector.addElement("0");
            vector.addElement("cold start");
            vector.addElement("1");
            vector.addElement("warm start");
            vector.addElement("2");
            vector.addElement("link down");
            vector.addElement("3");
            vector.addElement("link up");
            vector.addElement("6");
            vector.addElement("enterprise specific");
        } else if (scalarproperty == pSnmpTrapVersion) {
            vector.addElement("V1");
            vector.addElement("V1");
            vector.addElement("V2");
            vector.addElement("V2c");
        }
        return vector;
    }

    static {
        pSnmpHost = new StringProperty("_snmpHost");
        pSnmpHost.setDisplayText("Send to Host", "Enter the host name or IP address of the machine that will receive this trap - for example snmp.this-company.com or 206.168.191.20. This is the machine running the SNMP console.");
        pSnmpHost.setParameterOptions(true, 1, false);
        pSnmpObjectID = new ScalarProperty("_snmpObjectID");
        pSnmpObjectID.setDisplayText("SNMP Object ID", "Enter the SNMP object that is sending the trap. For example .1.3.6.1.2.1.1 is the \"system\" object from MIB-II (RFC 1213).");
        pSnmpObjectID.setParameterOptions(true, 2, false);
        pSnmpObjectIDOther = new StringProperty("_snmpObjectIDOther");
        pSnmpObjectIDOther.setDisplayText("Other", "");
        pSnmpObjectIDOther.setParameterOptions(true, 3, false);
        pSnmpCommunity = new StringProperty("_snmpCommunity");
        pSnmpCommunity.setDisplayText("SNMP Community", "Enter the SNMP community name used for this trap - usually this is \"public\".");
        pSnmpCommunity.setParameterOptions(true, 4, false);
        pSnmpGeneric = new ScalarProperty("_snmpGeneric");
        pSnmpGeneric.setDisplayText("Trap ID: Generic", "Enter the Generic trap type. If the generic trap type is \"enterprise specific\", then fill in the specific trap type which is a number.");
        pSnmpGeneric.setParameterOptions(true, 5, false);
        pSnmpSpecific = new StringProperty("_snmpSpecific");
        pSnmpSpecific.setDisplayText("Trap ID: Specific", "");
        pSnmpSpecific.setParameterOptions(true, 6, false);
        pSnmpTrapVersion = new StringProperty("_snmpTrapVersion");
        pSnmpTrapVersion.setDisplayText("SNMP Trap version", "This describes what version the SNMP trap will be formatted in when sending to your SNMP host.");
        pSnmpTrapVersion.setParameterOptions(true, 7, false);
        com.dragonflow.Properties.StringProperty astringproperty[] = { pSnmpHost, pSnmpObjectID, pSnmpObjectIDOther, pSnmpCommunity, pSnmpGeneric, pSnmpSpecific, pSnmpTrapVersion };
        com.dragonflow.StandardPreference.SNMPDefaultPreferences.addProperties("com.dragonflow.StandardPreference.SNMPDefaultPreferences", astringproperty);
    }
}
