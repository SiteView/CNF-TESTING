/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * HistorySummary.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>HistorySummary</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Date;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// HistorySummaryEntry, HistoryReport

class HistorySummary {

    HashMap itemMap;

    String startDate;

    String startTime;

    String endDate;

    String endTime;

    HistorySummary(String s, String s1, Date date, Array array, Array array1) {
        itemMap = new HashMap();
        startDate = "";
        startTime = "";
        endDate = "";
        endTime = "";
        File file = new File(HistoryReport.dateToReportPathBase(s, s1, date)
                + ".html");
        String s2 = "";
        String s3 = "";
        if (file.exists()) {
            String as[][] = { { "<!--OVERVIEW", "ENDOVERVIEW-->" },
                    { "<!--TIMEPERIOD", "ENDTIMEPERIOD-->" } };
            String as3[] = FileUtils.getTextChunks(file, as, 0);
            s3 = as3[0];
            s2 = as3[1];
        } else {
            LogManager.log("Error", "Could not find history file: "
                    + file.getAbsolutePath());
        }
        if (s3.length() > 0) {
            String as1[] = TextUtils.split(s3, "\t\n");
            for (int i = 0; i + 4 < as1.length; i += 5) {
                itemMap.add(as1[i], new HistorySummaryEntry(as1[i + 1],
                        as1[i + 2], as1[i + 3], as1[i + 4]));
                boolean flag = false;
                for (int j = 0; j < array.size(); j++) {
                    if (as1[i].equals(array.at(j))) {
                        flag = true;
                        array1.put(j, as1[i + 1]);
                    }
                }

                if (!flag) {
                    array.add(as1[i]);
                    array1.add(as1[i + 1]);
                }
            }

        }
        if (s2.length() > 0) {
            String as2[] = TextUtils.split(s2, "\t");
            if (as2.length >= 4) {
                startDate = as2[0];
                startTime = as2[1];
                endDate = as2[2];
                endTime = as2[3];
            }
        } else if (s3.length() > 0) {
            LogManager.log("Error", "Error reading time information from: "
                    + file.getAbsolutePath());
        }
    }

    HistorySummaryEntry getHistorySummaryEntry(String s) {
        return (HistorySummaryEntry) itemMap.get(s);
    }
}
