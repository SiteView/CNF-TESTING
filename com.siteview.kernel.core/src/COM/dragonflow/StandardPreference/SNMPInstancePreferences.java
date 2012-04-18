/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardPreference;

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
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardAction.SNMPTrap;

public class SNMPInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    private String settingName;

    public static COM.dragonflow.Properties.StringProperty pName;

    public static COM.dragonflow.Properties.StringProperty pDisabled;

    public static COM.dragonflow.Properties.StringProperty pSnmpHost;

    public static COM.dragonflow.Properties.StringProperty pSnmpObjectID;

    public static COM.dragonflow.Properties.StringProperty pSnmpObjectIDOther;

    public static COM.dragonflow.Properties.StringProperty pSnmpCommunity;

    public static COM.dragonflow.Properties.StringProperty pSnmpGeneric;

    public static COM.dragonflow.Properties.StringProperty pSnmpSpecific;

    public static COM.dragonflow.Properties.StringProperty pSnmpTrapVersion;

    public SNMPInstancePreferences() {
        settingName = "_additionalSNMP";
    }

    public java.util.Vector test(String s) throws java.lang.Exception {
        java.util.Vector vector = new Vector();
        if (s.length() == 0) {
            s = "Test Message";
        }
        COM.dragonflow.StandardAction.SNMPTrap snmptrap = new SNMPTrap();
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
        String s1 = snmptrap.sendTrap(s);
        if (s1.length() == 0) {
            vector.add("The test trap was sent successfully. (" + snmptrap + ")");
        } else {
            s = "The SNMP Trap could not be sent. The error was: " + s1;
            throw new Exception(s);
        }
        return vector;
    }

    public boolean hasMultipleValues() {
        return true;
    }

    public String getSettingName() {
        return settingName;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
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
        } else if (stringproperty == pName && (s == null || s.length() == 0)) {
            s = (String) hashmap.get(pSnmpHost.getName());
        }
        return s;
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest) throws COM.dragonflow.SiteViewException.SiteViewException {
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
        pName = new StringProperty("_name");
        pName.setDisplayText("Setting Name", "Enter the name of the SNMP settings used to specify SNMP settings when adding alerts.");
        pName.setParameterOptions(true, 1, false);
        pDisabled = new BooleanProperty("_disabled", "");
        pDisabled.setDisplayText("Disabled", "Disable the SNMP settings to prevent alert traps from being sent via that SNMP setting.");
        pDisabled.setParameterOptions(true, 2, false);
        pSnmpHost = new StringProperty("_snmpHost");
        pSnmpHost.setDisplayText("Send to Host", "Enter the host name or IP address of the machine that will receive this trap - for example snmp.this-company.com or 206.168.191.20. This is the machine running the SNMP console.");
        pSnmpHost.setParameterOptions(true, 3, false);
        pSnmpObjectID = new ScalarProperty("_snmpObjectID");
        pSnmpObjectID.setDisplayText("SNMP Object ID", "Enter the SNMP object that is sending the trap. For example .1.3.6.1.2.1.1 is the \"system\" object from MIB-II (RFC 1213).");
        pSnmpObjectID.setParameterOptions(true, 4, false);
        pSnmpObjectIDOther = new StringProperty("_snmpObjectIDOther");
        pSnmpObjectIDOther.setDisplayText("Other", "");
        pSnmpObjectIDOther.setParameterOptions(true, 5, false);
        pSnmpCommunity = new StringProperty("_snmpCommunity");
        pSnmpCommunity.setDisplayText("SNMP Community", "Enter the SNMP community name used for this trap - usually this is \"public\".");
        pSnmpCommunity.setParameterOptions(true, 6, false);
        pSnmpGeneric = new ScalarProperty("_snmpGeneric");
        pSnmpGeneric.setDisplayText("Trap ID: Generic", "Enter the Generic trap type. If the generic trap type is \"enterprise specific\", then fill in the specific trap type which is a number.");
        pSnmpGeneric.setParameterOptions(true, 7, false);
        pSnmpSpecific = new StringProperty("_snmpSpecific");
        pSnmpSpecific.setDisplayText("Trap ID: Specific", "");
        pSnmpSpecific.setParameterOptions(true, 8, false);
        pSnmpTrapVersion = new StringProperty("_snmpTrapVersion");
        pSnmpTrapVersion.setDisplayText("SNMP Trap version", "This describes what version the SNMP trap will be formatted in when sending to your SNMP host.");
        pSnmpTrapVersion.setParameterOptions(true, 9, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pName, pDisabled, pSnmpHost, pSnmpObjectID, pSnmpObjectIDOther, pSnmpCommunity, pSnmpGeneric, pSnmpSpecific, pSnmpTrapVersion };
        COM.dragonflow.StandardPreference.SNMPInstancePreferences.addProperties("COM.dragonflow.StandardPreference.SNMPInstancePreferences", astringproperty);
    }
}
