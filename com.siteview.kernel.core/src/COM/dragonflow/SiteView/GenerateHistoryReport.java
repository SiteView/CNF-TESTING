/*
 * 
 * Created on 2005-2-16 15:12:31
 *
 * GenerateHistoryReport.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>GenerateHistoryReport</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, SiteViewGroup, HistoryReport

public class GenerateHistoryReport extends Action implements Runnable {

    HistoryReport report;

    static boolean bDisableReports = false;

    static HashMap rtConfig = null;

    public GenerateHistoryReport(HistoryReport historyreport) {
        report = null;
        report = historyreport;
        runType = 1;
    }

    public boolean execute() {
        if (rtConfig == null) {
            rtConfig = SiteViewGroup.loadRTConfig();
            bDisableReports = TextUtils.getValue(rtConfig, "_disableReports")
                    .equalsIgnoreCase("true");
        }
        if (bDisableReports) {
            return true;
        } else {
            run();
            return true;
        }
    }

    public void run() {
        if (!report.isDisabled()) {
            String s = report.getProperty(HistoryReport.pOwnerID) + "-"
                    + report.getProperty(HistoryReport.pQueryID);
            LogManager.log("RunMonitor", "Report starting: " + s);
            try {
                report.createFromQuery("");
            } catch (SiteViewException siteviewexception) {
                if (siteviewexception instanceof SiteViewParameterException) {
                    SiteViewParameterException siteviewparameterexception = (SiteViewParameterException) siteviewexception;
                    Map map = siteviewparameterexception
                            .getErrorParameterMap();
                    if (map.size() > 0) {
                        Set set = map.keySet();
                        String s2;
                        for (Iterator iterator = set.iterator(); iterator
                                .hasNext(); LogManager.log("Error",
                                "Report error: " + s + ", " + s2)) {
                            String s1 = (String) iterator.next();
                            s2 = (String) map.get(s1);
                            LogManager.log("RunMonitor", "Report error: " + s
                                    + ", " + s2);
                        }

                    }
                } else {
                    LogManager.log("RunMonitor", "Report error: " + s + ", "
                            + siteviewexception.getFormattedErrorArgs()[0]);
                    LogManager.log("Error", "Report error: " + s + ", "
                            + siteviewexception.getFormattedErrorArgs()[0]);
                }
            } catch (Throwable throwable) {
                LogManager.log("RunMonitor", "Report error: " + s + ", "
                        + throwable);
                LogManager
                        .log("Error", "Report error: " + s + ", " + throwable);
            }
            LogManager.log("RunMonitor", "Reported completed: " + s);
        }
    }

    public String toString() {
        return "generate management report "
                + report.getProperty(HistoryReport.pOwnerID) + " - "
                + report.getProperty(HistoryReport.pQueryID);
    }

}