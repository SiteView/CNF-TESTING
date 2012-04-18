/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StatefulMonitor;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.HashSet;

// Referenced classes of package com.dragonflow.StatefulMonitor:
// StatefulConnectionUser

public abstract class StatefulConnection {

    java.lang.Object connID;

    java.util.HashSet usersIDs;

    String error;

    boolean isInError;

    public java.lang.Object getConnID() {
        return connID;
    }

    public abstract void disconnect();

    public boolean isInError() {
        return isInError;
    }

    public String getError() {
        return error;
    }

    public void setError(String s) {
        isInError = true;
        error = s;
    }

    public StatefulConnection(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        connID = null;
        usersIDs = new HashSet();
        error = null;
        isInError = false;
        connID = statefulconnectionuser.getConnID(getClass());
    }

    /**
     * 
     * 
     * @return
     */
    public int getUsersNum() {
        synchronized (usersIDs) {
            return usersIDs.size();
        }
    }

    /**
     * 
     * 
     * @param statefulconnectionuser
     * @return
     */
    public boolean isUsedBy(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        synchronized (usersIDs) {
            return usersIDs.contains(statefulconnectionuser.getUniqueID());
        }
    }

    public void addUserRef(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        synchronized (usersIDs) {
            usersIDs.add(statefulconnectionuser.getUniqueID());
        }
    }

    public void removeUserRef(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser) {
        synchronized (usersIDs) {
            usersIDs.remove(statefulconnectionuser.getUniqueID());
            if (usersIDs.isEmpty()) {
                disconnect();
            }
        }
    }

    public boolean canBeUsedBy(com.dragonflow.StatefulMonitor.StatefulConnectionUser statefulconnectionuser, boolean flag) {
        if (flag && isInError()) {
            return false;
        } else {
            return connID.equals(statefulconnectionuser.getConnID(getClass()));
        }
    }
}
