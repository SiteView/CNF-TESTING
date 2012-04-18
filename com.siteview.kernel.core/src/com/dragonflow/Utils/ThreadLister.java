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

class ThreadLister {

    ThreadLister() {
    }

    private static void print_thread_info(java.io.PrintWriter printwriter, java.lang.Thread thread, String s) {
        if (thread == null) {
            return;
        } else {
            printwriter.println(s + "Thread: " + thread.getName() + "  Priority: " + thread.getPriority() + (thread.isDaemon() ? " Daemon" : "") + (thread.isAlive() ? "" : " Not Alive"));
            return;
        }
    }

    private static void list_group(java.io.PrintWriter printwriter, java.lang.ThreadGroup threadgroup, String s) {
        if (threadgroup == null) {
            return;
        }
        int i = threadgroup.activeCount();
        int j = threadgroup.activeGroupCount();
        java.lang.Thread athread[] = new java.lang.Thread[i];
        java.lang.ThreadGroup athreadgroup[] = new java.lang.ThreadGroup[j];
        threadgroup.enumerate(athread, false);
        threadgroup.enumerate(athreadgroup, false);
        printwriter.println(s + "Thread Group: " + threadgroup.getName() + "  Max Priority: " + threadgroup.getMaxPriority() + (threadgroup.isDaemon() ? " Daemon" : ""));
        for (int k = 0; k < i; k ++) {
            com.dragonflow.Utils.ThreadLister.print_thread_info(printwriter, athread[k], s + "    ");
        }

        for (int l = 0; l < j; l ++) {
            com.dragonflow.Utils.ThreadLister.list_group(printwriter, athreadgroup[l], s + "    ");
        }

    }

    public static void listAllThreads(java.io.PrintWriter printwriter) {
        printwriter.println("-------Threads------\n");
        java.lang.ThreadGroup threadgroup = java.lang.Thread.currentThread().getThreadGroup();
        java.lang.ThreadGroup threadgroup1 = threadgroup;
        for (java.lang.ThreadGroup threadgroup2 = threadgroup1.getParent(); threadgroup2 != null; threadgroup2 = threadgroup2.getParent()) {
            threadgroup1 = threadgroup2;
        }

        com.dragonflow.Utils.ThreadLister.list_group(printwriter, threadgroup1, "");
    }
}
