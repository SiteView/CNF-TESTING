/*
 * 
 * Created on 2005-2-16 16:44:12
 *
 * ScheduleManager.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ScheduleManager</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import COM.dragonflow.StandardAction.UpdateMonitor;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, MonitorGroup, Monitor, SubGroup,
// SiteViewGroup, Scheduler, Platform

public class ScheduleManager {
    class ScheduleObject {

        private String scheduleCache;

        private List associatedMonitorIds;

        public String getScheduleCache() {
            return scheduleCache;
        }

        public void setScheduleCache(String s) {
            scheduleCache = s;
        }

        public List getAssociatedMonitors() {
            return associatedMonitorIds;
        }

        public void addToAssociatedMonitors(String s) {
            if (associatedMonitorIds == null) {
                associatedMonitorIds = new ArrayList();
            }
            if (!associatedMonitorIds.contains(s)) {
                associatedMonitorIds.add(s);
            }
        }

        public void deleteFromAssociatedMonitors(String s) {
            if (associatedMonitorIds == null) {
                return;
            }
            if (associatedMonitorIds.contains(s)) {
                associatedMonitorIds.remove(s);
            }
            if (associatedMonitorIds.size() == 0) {
                associatedMonitorIds = null;
                scheduleCache = null;
            }
        }

        public ScheduleObject(String s, ArrayList arraylist) {
            super();
            scheduleCache = null;
            associatedMonitorIds = null;
            associatedMonitorIds = arraylist;
            scheduleCache = s;
        }
    }

    private static ScheduleManager _scheduleManagerSingleton;

    private static HashMap scheduleMonitorMap = new HashMap();

    static final boolean $assertionsDisabled; /* synthetic field */

    public static ScheduleManager getInstance() {
        if (_scheduleManagerSingleton == null) {
            synchronized (scheduleMonitorMap) {
                if (_scheduleManagerSingleton == null) {
                    _scheduleManagerSingleton = new ScheduleManager();
                }
            }
        }
        return _scheduleManagerSingleton;
    }

    private ScheduleManager() {
        initScheduleMonitorMap();
    }

    public String getSchedule(Monitor monitor) {
        String s = "";
        String s2 = getScheduleIdFromMonitor(monitor);
        ScheduleObject scheduleobject = getScheduleObjectFromMap(s2);
        if (scheduleobject == null) {
            addMonitorToScheduleObject((AtomicMonitor) monitor);
            scheduleobject = getScheduleObjectFromMap(s2);
        }
        if (!$assertionsDisabled && scheduleobject == null) {
            throw new AssertionError();
        } else {
            String s1 = scheduleobject.getScheduleCache();
            return s1;
        }
    }

    public void updateSchedule(String s, String s1) {
        ScheduleObject scheduleobject = getScheduleObjectFromMap(s);
        if (scheduleobject != null) {
            scheduleobject.setScheduleCache(s1);
            rescheduleMonitorsForSchedule(s);
        }
    }

    public void addMonitorToScheduleObject(AtomicMonitor atomicmonitor) {
        synchronized (scheduleMonitorMap) {
            String s = getScheduleIdFromMonitor(atomicmonitor);
            ScheduleObject scheduleobject = getScheduleObjectFromMap(s);
            if (scheduleobject != null) {
                scheduleobject.addToAssociatedMonitors(atomicmonitor
                        .getFullID());
                if (scheduleobject.getScheduleCache() == null) {
                    scheduleobject
                            .setScheduleCache(getScheduleFromMonitor(atomicmonitor));
                }
            } else {
                ArrayList arraylist = new ArrayList();
                arraylist.add(atomicmonitor.getFullID());
                scheduleobject = new ScheduleObject(
                        getScheduleFromMonitor(atomicmonitor), arraylist);
            }
            putScheduleObjectInMap(s, scheduleobject);
        }
    }

    public void updateMonitorInScheduleObject(String s,
            AtomicMonitor atomicmonitor) {
        synchronized (scheduleMonitorMap) {
            String s1 = getScheduleIdFromMonitor(atomicmonitor);
            if (!s.equals(s1)) {
                deleteMonitorFromScheduleObject(s, atomicmonitor.getFullID());
                addMonitorToScheduleObject(atomicmonitor);
                SiteViewGroup.currentSiteView().monitorScheduler
                        .unschedule(atomicmonitor.action);
            }
        }
    }

    public void deleteMonitorFromScheduleObject(String s, String s1) {
        synchronized (scheduleMonitorMap) {
            ScheduleObject scheduleobject = getScheduleObjectFromMap(s);
            if (!$assertionsDisabled && scheduleobject == null) {
                throw new AssertionError();
            }
            scheduleobject.deleteFromAssociatedMonitors(s1);
            putScheduleObjectInMap(s, scheduleobject);
        }
    }

    public String getScheduleIdFromMonitor(Monitor monitor) {
        String s = "";
        s = monitor.getProperty(AtomicMonitor.pSchedule);
        int i = s.indexOf("_id=");
        if (i != -1) {
            s = s.substring(i + 4);
        }
        return s;
    }

    public String getScheduleFromMonitor(Monitor monitor) {
        String s = monitor.getProperty(AtomicMonitor.pSchedule);
        int i = s.indexOf("_id=");
        if (i != -1) {
            String s1 = s.substring(i + 4);
            s = monitor.getScheduleSettings(s1);
            jgl.HashMap hashmap = TextUtils.stringToHashMap(s);
            s = TextUtils.getValue(hashmap, "_schedule");
        }
        return s;
    }

    public boolean hasMonitorReferences(String s) {
        boolean flag = false;
        ScheduleObject scheduleobject = getScheduleObjectFromMap(s);
        if (scheduleobject != null) {
            List list = scheduleobject.getAssociatedMonitors();
            if (list != null && list.size() > 0) {
                flag = true;
            }
        }
        return flag;
    }

    private void initScheduleMonitorMap() {
        for (Enumeration enumeration = SiteViewGroup.currentSiteView()
                .getMonitors(); enumeration.hasMoreElements();) {
            MonitorGroup monitorgroup = (MonitorGroup) enumeration
                    .nextElement();
            Enumeration enumeration1 = monitorgroup.getMonitors();
            while (enumeration1.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration1.nextElement();
                if (!(monitor instanceof SubGroup)) {
                    addMonitorToScheduleObject((AtomicMonitor) monitor);
                }
            }
        }

    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private ScheduleObject getScheduleObjectFromMap(String s) {
        synchronized (ScheduleManager.scheduleMonitorMap) {
            return (ScheduleObject) scheduleMonitorMap.get(s);
        }
    }

    private void putScheduleObjectInMap(String s, ScheduleObject scheduleobject) {
        synchronized (scheduleMonitorMap) {
            scheduleMonitorMap.put(s, scheduleobject);
        }
    }

    private void rescheduleMonitorsForSchedule(String s) {
        ScheduleObject scheduleobject = getScheduleObjectFromMap(s);
        if (scheduleobject != null) {
            List list = scheduleobject.getAssociatedMonitors();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String s1 = (String) list.get(i);
                    SiteViewGroup siteviewgroup = SiteViewGroup
                            .currentSiteView();
                    AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewgroup
                            .getElement(s1);
                    SiteViewGroup.currentSiteView().monitorScheduler
                            .unschedule(atomicmonitor.action);
                    rescheduleMonitor(atomicmonitor, scheduleobject
                            .getScheduleCache(), 0L);
                }

            }
        }
    }

    private void rescheduleMonitor(AtomicMonitor atomicmonitor, String s, long l) {
        UpdateMonitor updatemonitor = new UpdateMonitor(atomicmonitor);
        if (l == 0L) {
            l = Platform.timeMillis();
        }
        if (s.startsWith("*")) {
            SiteViewGroup.currentSiteView().monitorScheduler
                    .scheduleAbsolutePeriodicAction(updatemonitor, s, l);
        } else {
            long l1 = atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency) * 1000L;
            if (l1 != 0L) {
                SiteViewGroup.currentSiteView().monitorScheduler
                        .scheduleRepeatedPeriodicAction(updatemonitor, l1, l);
            }
        }
    }

    static {
        $assertionsDisabled = !(COM.dragonflow.SiteView.ScheduleManager.class)
                .desiredAssertionStatus();
    }
}
