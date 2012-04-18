/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.PDH;

import java.util.Vector;

//import com.dragonflow.PDHMonitor.xdr.pdh_raw_counter;

public class PDHRawCounterCache
 implements java.io.Serializable
{
 static class RawCounter
     implements java.io.Serializable
 {

     public String label;
     public int cstatus;
     public String timeStamp;
     public String firstValue;
     public String secondValue;
     public int multiCount;

//     public com.dragonflow.PDHMonitor.xdr.pdh_raw_counter get_pdh_raw_counter()
//     {
//         com.dragonflow.PDHMonitor.xdr.pdh_raw_counter pdh_raw_counter1 = new pdh_raw_counter(cstatus, timeStamp, firstValue, secondValue, multiCount);
//         return pdh_raw_counter1;
//     }

//     public RawCounter(String s, com.dragonflow.PDHMonitor.xdr.pdh_raw_counter pdh_raw_counter1)
//     {
//         label = s;
//         cstatus = pdh_raw_counter1.get_cstatus();
//         timeStamp = pdh_raw_counter1.get_timeStamp();
//         firstValue = pdh_raw_counter1.get_firstValue();
//         secondValue = pdh_raw_counter1.get_secondValue();
//         multiCount = pdh_raw_counter1.get_multiCount();
//     }
 }


 private String pdhServer;
 private java.util.Vector counters;

 public PDHRawCounterCache(String s)
 {
     counters = new Vector();
     pdhServer = s;
 }

// public com.dragonflow.PDHMonitor.xdr.pdh_raw_counter get(int i)
// {
//     RawCounter rawcounter = (RawCounter)counters.get(i);
//     com.dragonflow.PDHMonitor.xdr.pdh_raw_counter pdh_raw_counter = rawcounter.get_pdh_raw_counter();
//     return pdh_raw_counter;
// }

// public void put(String s, com.dragonflow.PDHMonitor.xdr.pdh_raw_counter pdh_raw_counter)
// {
//     counters.addElement(new RawCounter(s, pdh_raw_counter));
// }

 public boolean checkValid(String s, String as[])
 {
     boolean flag = true;
     if(!s.equals(pdhServer))
     {
         flag = false;
     } else
     if(as.length != counters.size())
     {
         flag = false;
     } else
     {
         int i = 0;
         do
         {
             if(i >= as.length)
             {
                 break;
             }
             String s1 = ((RawCounter)counters.elementAt(i)).label;
             if(!as[i].equals(s1))
             {
                 flag = false;
                 break;
             }
             i++;
         } while(true);
     }
     return flag;
 }
}