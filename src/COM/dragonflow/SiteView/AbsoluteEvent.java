/*
 * 
 * Created on 2005-2-15 10:34:14
 *
 * AbsoluteEvent.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>AbsoluteEvent</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ScheduleEvent, AbsoluteSchedule, Action

class AbsoluteEvent extends ScheduleEvent {

    /**
     * CAUTION: Decompiled by hand
     * 
     * @param action
     * @param s
     * @throws Exception
     */
    AbsoluteEvent(Action action, String s) throws Exception {
        super(action);
        scheduleItems = new Array();
        String as[] = TextUtils.split(s, ",");
        if (as.length == 0)
            throw new Exception("Invalid Absolute Schedule: " + s);
        label0: for (int i = 0; i < as.length; i++) {
            String s1 = as[i].trim();
            if (Character.isDigit(s1.charAt(0))) {
                int j = TextUtils.toInt(s1.substring(0, 2));
                String s2 = s1.substring(2);
                String as1[] = TextUtils.split(s2, "/");
                int l = 0;
                while (l < as1.length) {
                    String s4 = as1[l];
                    String as3[] = TextUtils.split(s4, ":");
                    AbsoluteSchedule absoluteschedule;
                    if (as3.length == 2) {
                        absoluteschedule = new AbsoluteSchedule(j, -1,
                                TextUtils.toInt(as3[0]), TextUtils
                                        .toInt(as3[1]));
                    }
                    else {
                        absoluteschedule = new AbsoluteSchedule(j, -1,
                                TextUtils.toInt(as3[0]), 0);
                    }
                    scheduleItems.add(absoluteschedule);
                    l++;
                } 
            }
            else {
                int k = TextUtils.dayLetterToNumber(s1.substring(0, 1));
                String s3 = s1.substring(1);
                String as2[] = TextUtils.split(s3, "/");
                int i1 = 0;
                while (i1 < as2.length) {
                    String s5 = as2[i1];
                    String as4[] = TextUtils.split(s5, ":");
                    AbsoluteSchedule absoluteschedule1;
                    if (as4.length == 2) {
                        absoluteschedule1 = new AbsoluteSchedule(-1, k, TextUtils
                                .toInt(as4[0]), TextUtils.toInt(as4[1]));
                    }
                    else {
                        absoluteschedule1 = new AbsoluteSchedule(-1, k, TextUtils
                                .toInt(as4[0]), 0);
                    }
                    scheduleItems.add(absoluteschedule1);
                    i1++;
                }
            }
        }

    }

    /**
     * CAUTION: Decompiled by hand
     */
    long calculateNextTime(long l) {
        Date date = new Date(l);
        AbsoluteSchedule absoluteschedule = null;
        Enumeration enumeration = scheduleItems.elements();
        while (enumeration.hasMoreElements()) {
            AbsoluteSchedule absoluteschedule1 = (AbsoluteSchedule) enumeration
                    .nextElement();
            if (!absoluteschedule1.isGreater(date)) {
                continue;
            }
            absoluteschedule = absoluteschedule1;
            break;
        }
        byte byte0 = 0;
        int i = 0;
        if (absoluteschedule == null) {
            absoluteschedule = (AbsoluteSchedule) scheduleItems.front();
            if (absoluteschedule.monthDay == -1) {
                byte0 = 7;
            }
            else {
                i = 1;
            }
        }
        int j = absoluteschedule.monthDay;
        if (j == -1) {
            j = date.getDate() + (absoluteschedule.day - date.getDay()) + byte0;
        }
        int k = date.getMonth() + i;
        Date date1 = new Date(date.getYear(), k, j, absoluteschedule.hour,
                absoluteschedule.minute);
        time = date1.getTime();
        return time;
    }

    public static void main(String args[]) throws Exception {
        weeklyTest();
        monthlyTest();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @throws Exception
     */
    static void weeklyTest() throws Exception {
        String s = "U1:00/5:00,M2,T15:00,W15:00,R15:00,F15:00,S16:00/19:00";
        System.out.println("Weekly schedules are:\n");
        System.out.println(s + "\n");
        AbsoluteEvent absoluteevent = new AbsoluteEvent(null, s);
        Enumeration enumeration = absoluteevent.scheduleItems.elements();
        while (enumeration.hasMoreElements()) {
            System.out.println("item: " + enumeration.nextElement());
        }

        Date adate[] = { new Date(99, 4, 30, 0, 0), new Date(99, 4, 30, 3, 0),
                new Date(99, 4, 30, 10, 0), new Date(100, 3, 25, 11, 0),
                new Date(98, 3, 25, 15, 0), new Date(98, 3, 25, 16, 0),
                new Date(98, 3, 25, 16, 0, 1), new Date(98, 3, 25, 17, 0),
                new Date(98, 3, 25, 23, 0), new Date(97, 10, 18, 23, 0),
                new Date(96, 11, 1, 23, 59), new Date(98, 11, 31, 23, 59),
                new Date(98, 0, 1, 0, 0) };
        try {
            for (int i = 0; i < adate.length; i++) {
                System.out.print("Trying \"" + adate[i] + "\"\n");
                System.out.print("   result: \"");
                long l = absoluteevent.calculateNextTime(adate[i].getTime());
                Date date = new Date(l);
                if (adate[i].getTime() > date.getTime()) {
                    throw new Exception("scheduled event before request");
                }
                System.out.print(date + "\"\n");
            }

        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @throws Exception
     */
    static void monthlyTest() throws Exception {
        String s = "011:00/5:00,082,1215:00,1515:00,2615:00,2715:00,3016:00/19:00";
        System.out.println("Monthly schedules are:\n");
        System.out.println(s + "\n");
        AbsoluteEvent absoluteevent = new AbsoluteEvent(null, s);
        Enumeration enumeration = absoluteevent.scheduleItems.elements();
        while ( enumeration.hasMoreElements()) {
            System.out.println("item: " + enumeration.nextElement());
        }
        Date adate[] = { new Date(99, 4, 30, 0, 0), new Date(99, 4, 30, 3, 0),
                new Date(99, 4, 30, 10, 0), new Date(100, 10, 30, 11, 0),
                new Date(100, 10, 30, 15, 0), new Date(100, 10, 30, 16, 0),
                new Date(100, 10, 30, 16, 0, 1), new Date(100, 10, 30, 17, 0),
                new Date(100, 10, 30, 23, 0), new Date(97, 10, 18, 23, 0),
                new Date(96, 11, 1, 23, 59), new Date(98, 11, 31, 23, 59),
                new Date(98, 0, 1, 0, 0), new Date(100, 1, 28, 0, 0) };
        try {
            for (int i = 0; i < adate.length; i++) {
                System.out.print("Trying \"" + adate[i] + "\"\n");
                System.out.print("   result: \"");
                long l = absoluteevent.calculateNextTime(adate[i].getTime());
                Date date = new Date(l);
                if (adate[i].getTime() > date.getTime()) {
                    throw new Exception("scheduled event before request");
                }
                System.out.print(date + "\"\n");
            }

        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
    }

    public Array scheduleItems;
}
