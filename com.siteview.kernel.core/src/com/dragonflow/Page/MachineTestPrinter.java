/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.io.BufferedReader;

class MachineTestPrinter extends java.lang.Thread {

    private String message;

    private java.io.PrintWriter out;

    private java.io.PipedReader reader;

    MachineTestPrinter(java.io.PrintWriter printwriter,
            java.io.PipedReader pipedreader) {
        message = "";
        out = printwriter;
        reader = pipedreader;
    }

    public void run() {
        try {
            printTestResults();
        } catch (java.io.IOException ioexception) {
            message = "test failed";
        }
    }

    private void printTestResults() throws java.io.IOException {
        boolean flag = false;
        boolean flag1 = false;
        java.io.BufferedReader bufferedreader = new BufferedReader(reader);
        String s;
        for (; (s = bufferedreader.readLine()) != null; out.flush()) {
            if (s.length() > 0
                    && com.dragonflow.Utils.TextUtils.onlyChars(s, "-")) {
                out.println("<HR>");
                flag = true;
                continue;
            }
            if (flag && s.length() > 0) {
                s = "<B>" + s + "</B>";
                flag = false;
            }
            out.println(s);
            if (s.toString().indexOf("failed") != -1
                    || s.toString().indexOf("-99") != -1
                    || s.toString().indexOf("no data") != -1) {
                flag1 = true;
            }
        }

        bufferedreader.close();
        if (flag1) {
            message = message.concat(" some commands failed");
        }
    }

    String getMessage() {
        return message;
    }
}
