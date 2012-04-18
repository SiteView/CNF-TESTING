/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * HistorySummaryEntry.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>HistorySummaryEntry</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
class HistorySummaryEntry {

    String monitorName;

    String category;

    String average;

    String maximum;

    HistorySummaryEntry() {
        monitorName = "";
        category = "";
        average = "";
        maximum = "";
    }

    HistorySummaryEntry(String s, String s1, String s2, String s3) {
        monitorName = "";
        category = "";
        average = "";
        maximum = "";
        monitorName = s;
        category = s1;
        average = s2;
        maximum = s3;
    }
}
