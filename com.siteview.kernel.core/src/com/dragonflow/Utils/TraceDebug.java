/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class TraceDebug {

    public static int TRACELEVEL = 0;

    protected static java.io.PrintStream stream = null;

    protected static int callDepth = 0;

    public TraceDebug() {
    }

    public static void initStream(java.io.PrintStream printstream) {
        stream = printstream;
    }

    public static void traceEntry(String s) {
        if (TRACELEVEL == 0) {
            return;
        }
        if (TRACELEVEL == 2) {
            callDepth ++;
        }
        com.dragonflow.Utils.TraceDebug.printEntering(s);
    }

    public static void traceExit(String s) {
        if (TRACELEVEL == 0) {
            return;
        }
        com.dragonflow.Utils.TraceDebug.printExiting(s);
        if (TRACELEVEL == 2) {
            callDepth --;
        }
    }

    private static void printEntering(String s) {
        com.dragonflow.Utils.TraceDebug.printIndent();
        stream.println("--> " + s);
    }

    private static void printExiting(String s) {
        com.dragonflow.Utils.TraceDebug.printIndent();
        stream.println("<-- " + s);
    }

    private static void printIndent() {
        for (int i = 0; i < callDepth; i ++) {
            stream.print("  ");
        }

    }

}
