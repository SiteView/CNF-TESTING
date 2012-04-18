/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package com.dragonflow.Health:
// MgCompositeGroupParts

public class MgCompositeMasterConfig extends com.dragonflow.Health.MgCompositeGroupParts {

    com.dragonflow.Properties.HashMapDuplicates myHm;

    java.lang.Integer nextReportIDInt;

    MgCompositeMasterConfig(com.dragonflow.Properties.HashMapDuplicates hashmapduplicates) {
        myHm = hashmapduplicates;
        fileTitle = "File";
        fileExtension = ".config";
    }

    public void createHTML() {
    }

    public java.lang.Integer getNextReportID() {
        return nextReportIDInt;
    }

    public void printSelf(int i) {
        java.lang.System.out.println("FILE: master.config: ");
        java.lang.Object obj;
        for (java.util.Iterator iterator = myHm.keySet().iterator(); iterator.hasNext(); java.lang.System.out.println("Level: " + i + " key: " + obj + " value: " + myHm.get(obj))) {
            for (int j = 0; j < i; j ++) {
                java.lang.System.out.print('\t');
            }

            obj = iterator.next();
        }

    }

    public boolean errorCheck() {
        boolean flag = true;
        java.lang.Object obj = check_TagEqualsValue("_nextConditionID", myHm.get("_nextConditionID"), false, false);
        java.lang.Object obj1 = check_TagEqualsValue("_nextNTRemoteID", myHm.get("_nextNTRemoteID"), false, false);
        java.lang.Integer integer = checkCountingNumber("_nextNTRemoteID", obj1);
        java.lang.Object obj2 = check_TagEqualsValue("_nextRemoteID", myHm.get("_nextRemoteID"), false, false);
        java.lang.Integer integer1 = checkCountingNumber("_nextRemoteID", obj2);
        java.lang.Object obj3 = check_TagEqualsValue("_nextReportID", myHm.get("_nextReportID"), false, false);
        nextReportIDInt = checkCountingNumber("_nextReportID", obj3);
        checkAlerts(myHm);
        java.lang.Object obj4 = check_TagEqualsValue("_remoteMachine", myHm.get("_remoteMachine"), true, true);
        if (obj4 != null) {
            java.util.Vector vector = new Vector();
            if (obj4 instanceof String) {
                vector.add(obj4);
            } else if (obj4 instanceof java.util.Vector) {
                vector = (java.util.Vector) obj4;
            }
            java.util.Iterator iterator = vector.iterator();
            java.util.HashMap hashmap = new HashMap();
            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                java.util.StringTokenizer stringtokenizer = new StringTokenizer(s);
                while (stringtokenizer.hasMoreTokens()) {
                    String s2 = stringtokenizer.nextToken();
                    int i = s2.indexOf("_id=");
                    if (i == 0) {
                        String s4 = s2.substring(4);
                        java.lang.Integer integer2 = checkCountingNumber("_id=", s4);
                        checkForDuplicatesAndGTNextID("_id=", integer2, hashmap, "_nextRemoteID", integer1);
                        if (integer2 != null) {
                            remoteMachines.add(integer2);
                        }
                    }
                }
            }
            java.lang.Object obj9 = check_TagEqualsValue("_remoteNTMachine", myHm.get("_remoteNTMachine"), true, true);
            if (obj9 != null) {
                vector.clear();
                if (obj4 instanceof String) {
                    vector.add(obj4);
                } else if (obj4 instanceof java.util.Vector) {
                    vector = (java.util.Vector) obj4;
                }
                java.util.Iterator iterator1 = vector.iterator();
                Object obj6 = null;
                Object obj7 = null;
                Object obj8 = null;
                hashmap.clear();
                while (iterator1.hasNext()) {
                    String s1 = (String) iterator1.next();
                    java.util.StringTokenizer stringtokenizer1 = new StringTokenizer(s1);
                    while (stringtokenizer1.hasMoreTokens()) {
                        String s3 = stringtokenizer1.nextToken();
                        int j = s3.indexOf("_id=");
                        if (j == 0) {
                            String s5 = s3.substring(4);
                            java.lang.Integer integer3 = checkCountingNumber("_id=", s5);
                            checkForDuplicatesAndGTNextID("_id=", integer3, hashmap, "_nextRemoteID", integer1);
                            if (integer3 != null) {
                                remoteNTMachines.add(integer3);
                            }
                        }
                    }
                }
            }
        }
        java.lang.Object obj5 = check_TagEqualsValue("_remoteNTMachine", myHm.get("_remoteNTMachine"), true, true);
        if (messages.size() > 0) {
            flag = false;
        }
        return flag;
    }

    public boolean reportToErrorLog() {
        java.util.Iterator iterator = messages.iterator();
        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            if (s.startsWith("ERROR,")) {
                int i = s.indexOf(',');
                String s1 = s.substring(i + 1);
                java.lang.System.out.println("error.log MgHealth: " + s1);
                com.dragonflow.Log.LogManager.log("Error", "MgHealth: " + s1);
            }
        } 
        return true;
    }

    public boolean addMonitorNames() {
        return true;
    }
}
