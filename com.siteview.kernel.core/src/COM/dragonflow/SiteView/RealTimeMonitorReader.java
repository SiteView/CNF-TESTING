/*
 * 
 * Created on 2005-2-16 16:24:38
 *
 * RealTimeMonitorReader.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>RealTimeMonitorReader</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Date;

import jgl.HashMap;

// Referenced classes of package COM.dragonflow.SiteView:
// RealTimeReportingMonitor, SampleCollector, RealTimeReportingData

public class RealTimeMonitorReader {

    PrintWriter progressStream;

    HashMap monitorMap;

    public RealTimeMonitorReader(PrintWriter printwriter) {
        progressStream = null;
        progressStream = printwriter;
    }

    public RealTimeMonitorReader() {
        progressStream = null;
        progressStream = null;
    }

    public void process(SampleCollector asamplecollector[], Date date,
            Date date1, int i) {
        long l = date.getTime() / 1000L;
        long l1 = date1.getTime() / 1000L;
        for (int j = 0; j < asamplecollector.length; j++) {
            RealTimeReportingMonitor realtimereportingmonitor = (RealTimeReportingMonitor) asamplecollector[j]
                    .getMonitor();
            RealTimeReportingData arealtimereportingdata[] = realtimereportingmonitor
                    .getRealTimeData(asamplecollector[j].getProperty());
            if (arealtimereportingdata == null
                    || arealtimereportingdata.length <= 0) {
                continue;
            }
            long l2 = arealtimereportingdata[0].getTime();
            if (l2 < l) {
                l = l2;
                date.setTime(l * 1000L);
            }
            long l3 = arealtimereportingdata[arealtimereportingdata.length - 1]
                    .getTime();
            if (l3 > l1) {
                l1 = l3;
                date1.setTime(l1 * 1000L);
            }
            asamplecollector[j].createBuckets(l2, date1.getTime() / 1000L, i);
            for (int k = 0; k < arealtimereportingdata.length; k++) {
                asamplecollector[j].add(arealtimereportingdata[k].getTime(),
                        arealtimereportingdata[k].getValue(),
                        arealtimereportingdata[k].getCategory(),
                        arealtimereportingdata[k].getErrorType(),
                        arealtimereportingdata[k].isBestCaseCalc());
            }

        }

    }
}