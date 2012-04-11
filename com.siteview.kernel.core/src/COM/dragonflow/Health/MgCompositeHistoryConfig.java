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
// MgCompositeGroupParts, MgCompositeMasterConfig

public class MgCompositeHistoryConfig extends COM.dragonflow.Health.MgCompositeGroupParts {

    java.util.Vector CompositeParts;

    java.util.Vector FileHashMaps;

    COM.dragonflow.Health.MgCompositeMasterConfig MgCompMasterConfig;

    MgCompositeHistoryConfig(java.util.Vector vector, COM.dragonflow.Health.MgCompositeMasterConfig mgcompositemasterconfig) {
        CompositeParts = new Vector();
        MgCompMasterConfig = mgcompositemasterconfig;
        FileHashMaps = vector;
        fileTitle = "File";
        fileExtension = ".config";
        COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates = (COM.dragonflow.Properties.HashMapDuplicates) FileHashMaps.firstElement();
        java.lang.Object obj;
        for (java.util.Iterator iterator = hashmapduplicates.keySet().iterator(); iterator.hasNext(); java.lang.System.out.println(" key: " + obj + " value: " + hashmapduplicates.get(obj))) {
            obj = iterator.next();
        }

    }

    public String getFileName() {
        return FileName;
    }

    public void createHTML() {
    }

    public void printSelf(int i) {
        java.lang.System.out.println("Frames for history.config: " + FileName);
        for (java.util.Iterator iterator = FileHashMaps.iterator(); iterator.hasNext(); java.lang.System.out.println("#")) {
            COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates = (COM.dragonflow.Properties.HashMapDuplicates) iterator.next();
            java.lang.Object obj;
            for (java.util.Iterator iterator1 = hashmapduplicates.keySet().iterator(); iterator1.hasNext(); java.lang.System.out.println(" key: " + obj + " value: " + hashmapduplicates.get(obj))) {
                obj = iterator1.next();
            }

        }

    }

    public boolean errorCheck() {
        boolean flag = true;
        checkNextID();
        if (messages.size() > 0) {
            flag = false;
        }
        return flag;
    }

    private void checkNextID() {
        java.util.Iterator iterator = FileHashMaps.iterator();
        COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates = (COM.dragonflow.Properties.HashMapDuplicates) iterator.next();
        java.lang.Integer integer = null;
        if (MgCompMasterConfig != null) {
            integer = MgCompMasterConfig.getNextReportID();
        }
        java.util.HashMap hashmap = new HashMap();
        Object obj = null;
        while (iterator.hasNext()) {
            COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates1 = (COM.dragonflow.Properties.HashMapDuplicates) iterator.next();
            java.lang.Object obj1 = check_TagEqualsValue("id", hashmapduplicates1.get("id"), false, false);
            java.lang.Integer integer1 = null;
            checkCountingNumber("_id", obj1);
            checkForDuplicatesAndGTNextID("id", integer1, hashmap, "_nextReportID(master.config)", integer);
            java.lang.Object obj2 = check_TagEqualsValue("monitors", hashmapduplicates1.get("monitors"), true, true);
            if (fileNames.get(obj2) == null && !monitorNames.contains(obj2)) {
                String s = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "', did not find monitor or group that it reports on: '" + obj2 + "'. May have been deleted.";
                messages.add(s);
                java.lang.System.out.println(s);
            }
        } 
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
        for (java.util.Iterator iterator1 = CompositeParts.iterator(); iterator1.hasNext(); ((COM.dragonflow.Health.MgCompositeGroupParts) iterator1.next()).reportToErrorLog()) {
        }
        return true;
    }

    public boolean add(COM.dragonflow.Health.MgCompositeGroupParts mgcompositegroupparts) {
        return true;
    }

    public boolean findSelfUpLine(COM.dragonflow.Health.MgCompositeGroupParts mgcompositegroupparts, String s) {
        if (!mgcompositegroupparts.FileName.equals(s)) {
            if (mgcompositegroupparts.parentRef == null) {
                return false;
            }
            return findSelfUpLine(mgcompositegroupparts.parentRef, s);
        } else {
            return true;
        }
    }

    public boolean addMonitorNames() {
        return true;
    }
}
