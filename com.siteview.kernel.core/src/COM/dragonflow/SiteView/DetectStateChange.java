/*
 * 
 * Created on 2005-2-15 12:52:57
 *
 * DetectStateChange.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>DetectStateChange</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Utils.I18N;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, MonitorGroup, SiteViewGroup, Monitor

public class DetectStateChange extends Action {

    long lastStateChange;

    public DetectStateChange() {
        lastStateChange = -1L;
        runType = 1;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public boolean execute() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getPropertyAsLong(Monitor.pLastUpdate);
        if (l > lastStateChange) {
            Array array = siteviewgroup.getElementsOfClass(
                    "COM.dragonflow.SiteView.MonitorGroup", false);
            HashMap hashmap = new HashMap();
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                 MonitorGroup monitorgroup = (MonitorGroup) enumeration
                        .nextElement();
                if (monitorgroup.getPropertyAsLong(MonitorGroup.pLastSaved) < monitorgroup
                        .getPropertyAsLong(Monitor.pLastUpdate)) {
                    propagateChange(monitorgroup, hashmap);
                }
            } 
            
            MonitorGroup monitorgroup1;
            for (Enumeration enumeration1 = hashmap.keys(); enumeration1
                    .hasMoreElements(); monitorgroup1.writeAllHTML()) {
                String s = (String) enumeration1.nextElement();
                monitorgroup1 = (MonitorGroup) hashmap.get(s);
                monitorgroup1.saveDynamic();
            }

            SiteViewGroup.currentSiteView().writeAllHTML();
            lastStateChange = l;
        }
        return true;
    }

    void propagateChange(MonitorGroup monitorgroup, HashMap hashmap) {
        hashmap.put(monitorgroup.getFullID(), monitorgroup);
        String s = monitorgroup.getProperty(MonitorGroup.pParent);
        if (s.length() > 0) {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            MonitorGroup monitorgroup1 = (MonitorGroup) siteviewgroup
                    .getElement(I18N.toDefaultEncoding(s));
            if (monitorgroup1 != null) {
                propagateChange(monitorgroup1, hashmap);
            }
        }
    }

    public String toString() {
        return "detect state change";
    }
}