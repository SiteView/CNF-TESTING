/*
 * 
 * Created on 2005-2-16 16:25:02
 *
 * RealTimeReportingData.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>RealTimeReportingData</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.Serializable;

// Referenced classes of package com.dragonflow.SiteView:
// Monitor

public class RealTimeReportingData implements Serializable {

    private long time;

    private String value;

    private String category;

    private String errorType;

    private boolean bestCaseCalc;

    public RealTimeReportingData(long l, String s) {
        category = Monitor.GOOD_CATEGORY;
        errorType = Monitor.NON_FAILURE;
        bestCaseCalc = false;
        time = l;
        value = s;
    }

    public boolean isBestCaseCalc() {
        return bestCaseCalc;
    }

    public String getCategory() {
        return category;
    }

    public String getErrorType() {
        return errorType;
    }

    public long getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }
}