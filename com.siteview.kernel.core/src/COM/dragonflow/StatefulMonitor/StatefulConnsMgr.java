/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StatefulMonitor;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StatefulMonitor:
// StatefulConnection, StatefulConnectionUser

public class StatefulConnsMgr {

    static jgl.HashMap managers = new HashMap();

    private java.lang.Class connClass;

    jgl.Array conns;

    public StatefulConnsMgr(java.lang.Class class1) {
        connClass = null;
        conns = new Array();
        connClass = class1;
    }

    public static synchronized COM.dragonflow.StatefulMonitor.StatefulConnsMgr getManager(java.lang.Class class1) {
        COM.dragonflow.StatefulMonitor.StatefulConnsMgr statefulconnsmgr = (COM.dragonflow.StatefulMonitor.StatefulConnsMgr) managers.get(class1);
        if (statefulconnsmgr != null) {
            return statefulconnsmgr;
        } else {
            COM.dragonflow.StatefulMonitor.StatefulConnsMgr statefulconnsmgr1 = new StatefulConnsMgr(class1);
            managers.put(class1, statefulconnsmgr1);
            return statefulconnsmgr1;
        }
    }

    public synchronized COM.dragonflow.StatefulMonitor.StatefulConnection getConnection(COM.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser, StringBuffer stringbuffer) {
        COM.dragonflow.StatefulMonitor.StatefulConnection statefulconnection = null;
        int i = 0;
        do {
            if (i >= conns.size()) {
                break;
            }
            COM.dragonflow.StatefulMonitor.StatefulConnection statefulconnection1 = (COM.dragonflow.StatefulMonitor.StatefulConnection) conns.at(i);
            if (statefulconnection1.isUsedBy(statefulconnectionuser)) {
                if (!statefulconnection1.canBeUsedBy(statefulconnectionuser, false)) {
                    removeUserFromConn(i, statefulconnection1, statefulconnectionuser);
                } else {
                    statefulconnection = statefulconnection1;
                }
                break;
            }
            i ++;
        } while (true);
        if (statefulconnection == null) {
            int j = 0;
            do {
                if (j >= conns.size()) {
                    break;
                }
                COM.dragonflow.StatefulMonitor.StatefulConnection statefulconnection2 = (COM.dragonflow.StatefulMonitor.StatefulConnection) conns.at(j);
                if (statefulconnection2.canBeUsedBy(statefulconnectionuser, true)) {
                    statefulconnection = statefulconnection2;
                    break;
                }
                j ++;
            } while (true);
        }
        if (statefulconnection == null) {
            statefulconnection = statefulconnectionuser.createConnection(connClass, stringbuffer);
            if (statefulconnection != null) {
                conns.add(statefulconnection);
            }
        }
        if (statefulconnection != null) {
            statefulconnection.addUserRef(statefulconnectionuser);
        }
        return statefulconnection;
    }

    private void removeUserFromConn(int i, COM.dragonflow.StatefulMonitor.StatefulConnection statefulconnection, COM.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        statefulconnection.removeUserRef(statefulconnectionuser);
        if (statefulconnection.getUsersNum() == 0) {
            conns.remove(i);
        }
    }

    public synchronized void removeUserFromConn(COM.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        int i = conns.size();
        for (int j = 0; j < i; j ++) {
            COM.dragonflow.StatefulMonitor.StatefulConnection statefulconnection = (COM.dragonflow.StatefulMonitor.StatefulConnection) conns.at(j);
            if (statefulconnection.isUsedBy(statefulconnectionuser)) {
                removeUserFromConn(j, statefulconnection, statefulconnectionuser);
                return;
            }
        }

    }

}
