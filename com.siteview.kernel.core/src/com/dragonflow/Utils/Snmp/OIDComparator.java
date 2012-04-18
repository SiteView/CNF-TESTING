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

public class OIDComparator implements java.util.Comparator {

    public OIDComparator() {
    }

    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        com.netaphor.snmp.OID oid = (com.netaphor.snmp.OID) obj;
        com.netaphor.snmp.OID oid1 = (com.netaphor.snmp.OID) obj1;
        return oid.compare(oid1);
    }
}
