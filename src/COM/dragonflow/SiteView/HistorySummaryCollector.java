/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * HistorySummaryCollector.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>HistorySummaryCollector</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.SiteView:
// HistorySummary, HistorySummaryEntry

class HistorySummaryCollector {

    HashMap historySummaries;

    Array uniqueMonitorIDs;

    Array uniqueMonitorNames;

    String account;

    String queryID;

    HistorySummaryCollector(String s, String s1) {
        historySummaries = new HashMap();
        uniqueMonitorIDs = new Array();
        uniqueMonitorNames = new Array();
        account = s;
        queryID = s1;
    }

    void add(Date date) {
        historySummaries.add(date, new HistorySummary(account, queryID, date,
                uniqueMonitorIDs, uniqueMonitorNames));
    }

    Enumeration getMonitorNames() {
        return uniqueMonitorNames.elements();
    }

    Enumeration getMonitorIDs() {
        return uniqueMonitorIDs.elements();
    }

    HistorySummaryEntry getEntry(Date date, String s) {
        HistorySummary historysummary = (HistorySummary) historySummaries
                .get(date);
        HistorySummaryEntry historysummaryentry = null;
        if (historysummary != null) {
            historysummaryentry = historysummary.getHistorySummaryEntry(s);
        }
        if (historysummaryentry == null) {
            historysummaryentry = new HistorySummaryEntry();
        }
        return historysummaryentry;
    }

    String getCategory(Date date, String s) {
        return getEntry(date, s).category;
    }

    String getAverage(Date date, String s) {
        return getEntry(date, s).average;
    }

    String getMaximum(Date date, String s) {
        return getEntry(date, s).maximum;
    }

    String getStartDate(Date date) {
        HistorySummary historysummary = (HistorySummary) historySummaries
                .get(date);
        if (historysummary != null) {
            return historysummary.startDate;
        } else {
            return "";
        }
    }

    String getStartTime(Date date) {
        HistorySummary historysummary = (HistorySummary) historySummaries
                .get(date);
        if (historysummary != null) {
            return historysummary.startTime;
        } else {
            return "";
        }
    }

    String getEndDate(Date date) {
        HistorySummary historysummary = (HistorySummary) historySummaries
                .get(date);
        if (historysummary != null) {
            return historysummary.endDate;
        } else {
            return "";
        }
    }

    String getEndTime(Date date) {
        HistorySummary historysummary = (HistorySummary) historySummaries
                .get(date);
        if (historysummary != null) {
            return historysummary.endTime;
        } else {
            return "";
        }
    }
}
