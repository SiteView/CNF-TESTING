/*
 * 
 * Created on 2005-2-15 10:36:06
 *
 * AbsoluteSchedule.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>AbsoluteSchedule</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Date;

import com.dragonflow.Log.LogManager;

class AbsoluteSchedule {

    AbsoluteSchedule(int i, int j, int k, int l) {
        monthDay = -1;
        day = 0;
        hour = 0;
        minute = 0;
        if (i != -1 && (i > 31 || i < 0)) {
            LogManager.log("Error", "invalid monthDay in schedule: " + i);
            i = 0;
        }
        if (j != -1 && (j > 6 || j < 0)) {
            LogManager.log("Error", "invalid day in schedule: " + j);
            j = 0;
        }
        if (k > 24 || k < 0) {
            LogManager.log("Error", "invalid hours in schedule: " + k);
            k = 0;
        }
        if (l > 60 || l < 0) {
            LogManager.log("Error", "invalid minutes in schedule: " + l);
            l = 0;
        }
        monthDay = i;
        day = j;
        hour = k;
        minute = l;
    }

    public String toString() {
        return "AbsoluteSchedule: monthDay=" + monthDay + ", day=" + day
                + ", hour=" + hour + ", minute=" + minute;
    }

    boolean isGreater(Date date) {
        if (monthDay != -1) {
            int i = date.getDate();
            if (monthDay > i) {
                int l = date.getMonth();
                Date date1 = new Date(date.getYear(), date.getMonth(), monthDay);
                return date1.getMonth() == l;
            }
            if (monthDay < i)
                return false;
        } else {
            int j = date.getDay();
            if (day > j)
                return true;
            if (day < j)
                return false;
        }
        int k = date.getHours();
        if (hour > k)
            return true;
        if (hour < k)
            return false;
        int i1 = date.getMinutes();
        return minute > i1;
    }

    public int monthDay;

    public int day;

    public int hour;

    public int minute;
}