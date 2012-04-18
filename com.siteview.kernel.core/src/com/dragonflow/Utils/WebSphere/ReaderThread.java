/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;

public class ReaderThread extends java.lang.Thread {

    java.io.BufferedReader input;

    String logType;

    String logPrefix;

    StringBuffer buffer;

    public ReaderThread(java.io.BufferedReader bufferedreader, String s, String s1) {
        logType = "Error";
        logPrefix = "";
        buffer = null;
        input = bufferedreader;
        if (s != null) {
            logType = s;
        }
        if (s1 != null) {
            logPrefix = s1;
        }
    }

    public ReaderThread(java.io.BufferedReader bufferedreader, StringBuffer stringbuffer) {
        logType = "Error";
        logPrefix = "";
        buffer = null;
        input = bufferedreader;
        buffer = stringbuffer;
    }

    public void run() {
        String s;
        try {
            while ((s = input.readLine()) != null) {
                if (buffer != null) {
                    buffer.append(s).append("\r\n");
                } else {
                    com.dragonflow.Log.LogManager.log(logType, logPrefix + s);
                }
            }
        } catch (java.io.IOException ioexception) {
            com.dragonflow.Log.LogManager.log("Error", "ReaderThread caught IOException: " + ioexception);
        }
    }
}
