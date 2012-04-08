/*
 * 
 * Created on 2005-2-15 11:53:39
 *
 * BrowsableMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.Array;

public interface BrowsableMonitor {

    public abstract Array getConnectionProperties();

    public abstract String getBrowseData(StringBuffer stringbuffer);

    public abstract int getMaxCounters();

    public abstract void setMaxCounters(int i);

    public abstract String getBrowseName();

    public abstract String getBrowseID();

    public abstract String setBrowseName(Array array);

    public abstract String setBrowseID(Array array);

    public abstract boolean isUsingCountersCache();

    public abstract boolean isServerBased();

    public abstract boolean manageBrowsableSelectionsByID();

    public abstract boolean areBrowseIDsEqual(String s, String s1);
}