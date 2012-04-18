/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils.Snmp;

import com.netaphor.snmp.OID;

public class SNMPVariableBinding {

    private com.netaphor.snmp.OID oid;

    private String value;

    private int syntax;

    protected SNMPVariableBinding(com.netaphor.snmp.OID oid1, int i, String s) {
        value = "";
        syntax = 5;
        oid = oid1;
        syntax = i;
        value = s;
    }

    public SNMPVariableBinding(String s) {
        value = "";
        syntax = 5;
        oid = new OID(s);
    }

    public String getOIDAsString() {
        return oid.toString();
    }

    protected com.netaphor.snmp.OID getOID() {
        return oid;
    }

    protected void setOID(com.netaphor.snmp.OID oid1) {
        oid = oid1;
    }

    public String getValue() {
        return value;
    }

    public long getValueAsLong() {
        long l = 0L;
        try {
            l = java.lang.Long.parseLong(value);
        } catch (java.lang.NumberFormatException numberformatexception) {
            com.dragonflow.Log.LogManager.log("Error", "SNMPVariableBinding could not convert String value: " + value + " to long.");
        }
        return l;
    }

    protected void setValue(String s) {
        value = s;
    }

    public int getSyntax() {
        return syntax;
    }

    protected void setSyntax(int i) {
        syntax = i;
    }

    public int getSubIDAt(int i) {
        if (i < 0) {
            i = 0;
        } else if (i >= oid.length()) {
            i = oid.length() - 1;
        }
        return oid.getAt(i);
    }

    public int getLastSubID() {
        int i = oid.length() - 1;
        return oid.getAt(i);
    }
}
