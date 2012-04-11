/*
 * 
 * Created on 2005-2-16 16:24:14
 *
 * ProgressLogger.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ProgressLogger</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;

import jgl.Array;
import COM.dragonflow.Log.Logger;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, Monitor, Platform

public class ProgressLogger extends Logger {

    private static final int MAX_HISTORY = 20;

    public static ProgressLogger cProgressLogger = null;

    public Array history;

    public long lastProgressTime;

    public ProgressLogger() {
        history = new Array();
        lastProgressTime = Platform.timeMillis();
        if (cProgressLogger == null) {
            cProgressLogger = this;
        }
    }

    public synchronized void log(String s, Date date, String s1) {
        String s2 = TextUtils.prettyDate(date) + "\t" + s1;
        if (history.size() >= 20) {
            history.popBack();
        }
        history.pushFront(s2);
        lastProgressTime = Platform.timeMillis();
    }

    public void log(String s, Date date, PropertiedObject propertiedobject) {
        String s1 = propertiedobject.getProperty(Monitor.pCategory);
        if (!s1.equals("good")) {
            s1 = s1.toUpperCase();
        }
        String s2 = propertiedobject.getProperty(SiteViewObject.pOwnerID);
        Monitor monitor = (Monitor) ((SiteViewObject) propertiedobject)
                .getOwner();
        if (monitor != null) {
            s2 = monitor.getProperty(Monitor.pName);
        }
        String s3 = s2 + "\t" + propertiedobject.getProperty(Monitor.pName)
                + "\t" + s1 + ", "
                + propertiedobject.getProperty(Monitor.pStateString);
        log(s, date, s3);
    }

}
