/*
 * 
 * Created on 2005-2-16 16:25:42
 *
 * RealTimeReportingMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>RealTimeReportingMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import COM.dragonflow.Properties.StringProperty;

// Referenced classes of package COM.dragonflow.SiteView:
// RealTimeReportingData

public interface RealTimeReportingMonitor {

    public abstract RealTimeReportingData[] getRealTimeData(
            StringProperty stringproperty);

    public abstract Enumeration getRealTimeProperties();

    public abstract long lastRunTime();

    public abstract String getRTGraphLabel(StringProperty stringproperty);

    public abstract StringProperty[] getPropertiesOnSameGraph(
            StringProperty stringproperty);

    public abstract long getRTVerticalMax(StringProperty stringproperty);
}
