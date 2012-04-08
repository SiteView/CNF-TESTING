/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.HashMap;
import java.util.Vector;

// Referenced classes of package COM.dragonflow.Health:
// MgCompositeGroupParts

public class MgCompositeMonitor extends COM.dragonflow.Health.MgCompositeGroupParts {

    COM.dragonflow.Properties.HashMapDuplicates myHm;

    static java.util.HashMap myDependsOnHm = new HashMap();

    MgCompositeMonitor(COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates) {
        myHm = hashmapduplicates;
        fileTitle = "Group";
        fileExtension = ".mg";
    }

    public void createHTML() {
    }

    public void printSelf(int i) {
        for (int j = 0; j < i; j ++) {
            java.lang.System.out.print('\t');
        }

        String s = (String) myHm.get("_name");
        if (s == null) {
            s = "";
        }
        java.lang.System.out.println("Frame: Monitor _name: " + s);
        java.lang.Object obj;
        for (java.util.Iterator iterator = myHm.keySet().iterator(); iterator.hasNext(); java.lang.System.out.println("Level: " + i + " key: " + obj + " value: " + myHm.get(obj))) {
            for (int k = 0; k < i; k ++) {
                java.lang.System.out.print('\t');
            }

            obj = iterator.next();
        }

    }

    public boolean errorCheck() {
        boolean flag = true;
        java.lang.Object obj = check_TagEqualsValue("_name", myHm.get("_name"), false, false);
        java.lang.Object obj1 = check_TagEqualsValue("_id", myHm.get("_id"), false, false);
        java.lang.Object obj2 = check_TagEqualsValue("_frequency", myHm.get("_frequency"), false, false);
        java.lang.Integer integer = checkCountingNumber("_frequency", obj2);
        java.lang.Object obj3 = check_TagEqualsValue("_class", myHm.get("_class"), false, false);
        java.lang.Object obj4 = check_TagEqualsValue("_dependsOn", myHm.get("_dependsOn"), false, true);
        if (obj4 != null) {
            java.util.Vector vector = new Vector();
            vector.add(obj4);
            if (findSelfUpLineDependsOn(vector)) {
                String s = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "', circular dependency for: '" + obj4 + "'. Here is the list of monitors that cirularly depend on each other." + vector.toString();
                messages.add(s);
                java.lang.System.out.println(s);
            }
        }
        if (obj3.equals("EBusinessTransactionMonitor") || obj3.equals("CompositeMonitor")) {
            java.lang.Object obj5 = check_TagEqualsValue("_item", myHm.get("_item"), true, false);
            java.util.Vector vector1 = new Vector();
            if (obj5 instanceof String) {
                vector1.add(obj5);
            } else {
                vector1 = (java.util.Vector) obj5;
            }
            java.util.Iterator iterator = vector1.iterator();
            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                if (fileNames.get(s1) == null && !monitorNames.contains(s1)) {
                    String s2 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "', composite or ebusiness monitor: '" + obj + "' did not find monitor(Name Format: mgName ID#) or group name: '" + s1 + "'. May have been deleted.";
                    messages.add(s2);
                    java.lang.System.out.println(s2);
                }
            } 
        }
        java.lang.Object obj6 = check_TagEqualsValue("_errorFrequency", myHm.get("_errorFrequency"), false, true);
        java.lang.Integer integer1 = new Integer(-1);
        if (obj6 != null) {
            integer1 = checkCountingNumber("_errorFrequency", obj6);
        }
        java.lang.Object obj7 = check_TagEqualsValue("_timeout", myHm.get("_timeout"), false, true);
        if (obj7 != null) {
            java.lang.Integer integer2 = checkCountingNumber("_timeout", obj7);
            if (obj6 != null && integer1.compareTo(integer2) <= 0) {
                String s3 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' _errorFrequency: '" + obj6 + "' less than _timeout: '" + obj7 + "'. May cause skipping in error.log and SiteView restarts.";
                messages.add(s3);
                java.lang.System.out.println(s3);
            }
            if (obj2 != null && integer.compareTo(integer2) <= 0) {
                String s4 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' _frequency: '" + obj2 + "' less than _timeout: '" + obj7 + "'. May cause skipping in error.log and SiteView restarts.";
                messages.add(s4);
                java.lang.System.out.println(s4);
            }
        }
        java.lang.Object obj8 = check_TagEqualsValue("_machine", myHm.get("_machine"), false, true);
        if (obj8 != null) {
            int i = ((String) obj8).indexOf(":");
            if (i > 0) {
                String s5 = ((String) obj8).substring(i + 1);
                if (!remoteMachines.contains(s5)) {
                    String s6 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "', _machine: " + obj8 + " number is not found in the master.config _remoteMachine tags in the _id value. Existing ids: " + remoteMachines;
                    messages.add(s6);
                    java.lang.System.out.println(s6);
                }
            }
        }
        java.lang.Object obj9 = check_TagEqualsValue("_dependsCondition", myHm.get("_dependsCondition"), false, true);
        checkAlerts(myHm);
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
                COM.dragonflow.Log.LogManager.log("Error", "MgHealth: " + s1);
            }
        } 
        return true;
    }

    public boolean addMonitorNames() {
        java.lang.Object obj = myHm.get("_id");
        java.lang.Object obj1 = myHm.get("_name");
        if (obj1 != null && !obj1.equals("")) {
            monitorNames.add(obj1);
        }
        if (FileName != null && !FileName.equals("") && obj != null && !obj.equals("")) {
            monitorNames.add(obj + " " + FileName);
            monitorNames.add(FileName + " " + obj);
            java.lang.Object obj2 = check_TagEqualsValue("_dependsOn", myHm.get("_dependsOn"), false, true);
            if (obj2 != null && !obj2.equals("")) {
                myDependsOnHm.put(FileName + " " + obj, obj2);
            }
        }
        return true;
    }

    private boolean findSelfUpLineDependsOn(java.util.Vector vector) {
        String s = (String) vector.lastElement();
        String s1 = (String) myDependsOnHm.get(s);
        if (s1 == null) {
            return false;
        }
        String s2 = (String) check_TagEqualsValue("_dependsOn", s1, false, false);
        if (!monitorNames.contains(s2)) {
            String s3 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "', did not find monitor that it depends on: '" + s2 + "'. May have been deleted.";
            messages.add(s3);
            java.lang.System.out.println(s3);
            return false;
        }
        boolean flag = vector.contains(s2);
        if (!flag) {
            vector.add(s2);
            return findSelfUpLineDependsOn(vector);
        } else {
            return true;
        }
    }

}
