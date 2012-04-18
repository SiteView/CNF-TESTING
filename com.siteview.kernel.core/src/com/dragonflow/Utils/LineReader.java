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

import java.util.HashMap;

import com.recursionsw.jgl.Array;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils

public class LineReader {

    int lineNumber;

    int startLine;

    int endLine;

    int headerLine;

    String columnNames[];

    String columnLabels[];

    int columnStartIndex[];

    int columnEndIndex[];

    String matchLine;

    String skipLineMatch;

    String startMatch;

    String endMatch;

    String reverseColumns;

    static boolean debug = false;

    private java.io.PrintWriter traceStream;

    private java.lang.Boolean traceDetail;

    boolean start;

    Array lines;

    String currentLine;

    boolean shouldSkipLine;

    boolean reading;

    static final String COLUMN_NAME_KEY = "ColumnName";

    private static java.util.Map streamMap = java.util.Collections.synchronizedMap(new HashMap());

    private static java.util.Map detailMap = java.util.Collections.synchronizedMap(new HashMap());

    public LineReader(Array array) {
        lineNumber = 0;
        startLine = 0;
        endLine = 0x7fffffff;
        headerLine = 1;
        columnNames = null;
        columnLabels = null;
        columnStartIndex = null;
        columnEndIndex = null;
        matchLine = "";
        skipLineMatch = "";
        startMatch = "";
        endMatch = "";
        reverseColumns = "";
        traceStream = null;
        traceDetail = null;
        start = true;
        lines = null;
        currentLine = "";
        shouldSkipLine = false;
        reading = true;
        lines = array;
    }

    public LineReader(Array array, com.dragonflow.SiteView.OSAdapter osadapter, String s) {
        this(array);
        startLine = osadapter.getCommandSettingAsInteger(s, "startLine", startLine);
        endLine = osadapter.getCommandSettingAsInteger(s, "endLine", endLine);
        headerLine = osadapter.getCommandSettingAsInteger(s, "headerLine", headerLine);
        startMatch = osadapter.getCommandSetting(s, "startMatch");
        endMatch = osadapter.getCommandSetting(s, "endMatch");
        matchLine = osadapter.getCommandSetting(s, "matchLine");
        skipLineMatch = osadapter.getCommandSetting(s, "skipLine");
        reverseColumns = osadapter.getCommandSetting(s, "reverseColumns");
        if (osadapter.getCommandSetting(s, "reverseLines").length() > 0) {
            printDebugDetail("Reversing lines");
            Array array1 = new Array();
            for (int i = array.size() - 1; i >= 0; i --) {
                array1.add(array.get(i));
            }

            lines = array1;
        }
        start = startMatch.length() == 0;
        Array array2 = osadapter.getMatchedCommandSettings(s, "ColumnName");
        if (array2.size() > 0) {
            columnNames = new String[array2.size()];
            columnLabels = new String[array2.size()];
            columnStartIndex = new int[array2.size()];
            columnEndIndex = new int[array2.size()];
            for (int j = 0; j < array2.size(); j ++) {
                String s1 = (String) array2.get(j);
                columnLabels[j] = osadapter.getCommandSetting(s, s1);
                s1 = s1.substring(0, s1.length() - "ColumnName".length());
                columnNames[j] = s1;
                columnStartIndex[j] = 0;
                columnEndIndex[j] = -1;
            }

        }
    }

    public boolean processLine() {
        if (lineNumber >= lines.size()) {
            reading = false;
        }
        if (reading) {
            shouldSkipLine = false;
            currentLine = (String) lines.get(lineNumber);
            lineNumber ++;
            if (columnNames != null && lineNumber == headerLine) {
                parseHeader();
            }
            if (lineNumber == 1) {
                printDebugDetail("");
            }
            printDebugDetail("line " + com.dragonflow.Utils.TextUtils.numberToString(lineNumber) + ": " + currentLine);
            if (lineNumber < startLine) {
                shouldSkipLine = true;
            }
            if (lineNumber > endLine) {
                printDebugDetail("endLine (" + endLine + ") reached");
                reading = false;
            }
            if (endMatch.length() > 0 && com.dragonflow.Utils.TextUtils.match(currentLine, endMatch)) {
                printDebugDetail("endMatch matched: " + endMatch);
                reading = false;
            }
            if (!start && startMatch.length() > 0) {
                start = com.dragonflow.Utils.TextUtils.match(currentLine, startMatch);
                if (start) {
                    printDebugDetail("starting startLine: " + startMatch);
                }
            }
            if (matchLine.length() > 0) {
                if (!com.dragonflow.Utils.TextUtils.match(currentLine, matchLine)) {
                    shouldSkipLine = true;
                } else {
                    printDebugDetail("matchLine matched: " + matchLine);
                }
            }
            if (skipLineMatch.length() > 0 && com.dragonflow.Utils.TextUtils.match(currentLine, skipLineMatch)) {
                printDebugDetail("skipping skipLine: " + skipLineMatch);
                shouldSkipLine = true;
            }
            if (!start) {
                shouldSkipLine = true;
            }
        }
        return reading;
    }

    void parseHeader() {
        printDebugDetail("Finding column names...");
        for (int i = 0; i < columnNames.length; i ++) {
            int k = currentLine.indexOf(columnLabels[i]);
            boolean flag = true;
            if (k < 0) {
                continue;
            }
            int l = k;
            int i1 = (k + columnLabels[i].length()) - 1;
            if (flag) {
                
                for (int j1 = l - 1; j1 >= 0; j1 --) {
                    char c = currentLine.charAt(j1);
                    if (!java.lang.Character.isWhitespace(c)) {
                        break;
                    }
                    l = j1;
                } 
            }
            columnStartIndex[i] = l;
            for (int k1 = i1 + 1; k1 < currentLine.length(); k1 ++) {
                char c1 = currentLine.charAt(k1);
                if (!java.lang.Character.isWhitespace(c1)) {
                    columnEndIndex[i] = i1 + 1;
                    break;
                }               
            }
        }

        for (int j = 0; j < columnNames.length; j ++) {
            printDebugDetail("Column \"" + columnNames[j] + "\" read from column " + columnStartIndex[j] + " to " + columnEndIndex[j]);
        }

    }

    public String readColumnByName(String s) {
        int i = -1;
        int j = -1;
        for (int k = 0; k < columnNames.length; k ++) {
            if (columnNames[k].equals(s)) {
                i = columnStartIndex[k];
                j = columnEndIndex[k];
            }
        }

        String s1 = "";
        if (i >= 0) {
            if (j < 0 || j > currentLine.length()) {
                j = currentLine.length();
            }
            if (i < currentLine.length()) {
                s1 = currentLine.substring(i, j).trim();
            }
        }
        printDebugDetail("reading column \"" + s + "\": " + s1);
        return s1;
    }

    public String readColumn(int i) {
        return readColumn(i, false);
    }

    public String readColumn(int i, boolean flag) {
        String as[] = com.dragonflow.Utils.TextUtils.split(currentLine);
        if (flag) {
            i = (as.length - i) + 1;
        }
        if (as.length >= i && i > 0) {
            return as[i - 1];
        }
        if (i == 999 && as.length > 0) {
            return as[as.length - 1];
        } else {
            return "";
        }
    }

    public String readColumn(int i, String s) {
        boolean flag = false;
        if (reverseColumns.length() > 0) {
            String as[] = reverseColumns.split(",");
            int j = 0;
            do {
                if (j >= as.length) {
                    break;
                }
                if (s.equals(as[j])) {
                    flag = true;
                    break;
                }
                j ++;
            } while (true);
        }
        String s1 = readColumn(i, flag);
        printDetailTraceLine("reading " + s + " (" + i + "): " + s1);
        return s1;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public boolean isDone() {
        return !reading;
    }

    public boolean skipLine() {
        return shouldSkipLine;
    }

    private void printDebugDetail(String s) {
        if (debug) {
            com.dragonflow.Utils.TextUtils.debugPrint(s);
        }
        printDetailTraceLine(s);
    }

    private void printDetailTraceLine(String s) {
        if (isTraceDetail()) {
            printTraceLine(s);
        }
    }

    private void printTraceLine(String s) {
        if (getTraceStream() != null) {
            getTraceStream().println(s);
        }
    }

    private java.io.PrintWriter getTraceStream() {
        if (traceStream == null) {
            traceStream = (java.io.PrintWriter) streamMap.get(java.lang.Thread.currentThread());
        }
        return traceStream;
    }

    public static void setTraceStream(java.io.PrintWriter printwriter) {
        java.lang.Thread thread = java.lang.Thread.currentThread();
        streamMap.put(thread, printwriter);
        com.dragonflow.Utils.LineReader.setTraceDetail(true);
    }

    public static void resetTraceStream() {
        streamMap.remove(java.lang.Thread.currentThread());
        com.dragonflow.Utils.LineReader.setTraceDetail(false);
    }

    private boolean isTraceDetail() {
        if (traceDetail == null) {
            java.lang.Boolean boolean1 = (java.lang.Boolean) detailMap.get(java.lang.Thread.currentThread());
            if (boolean1 != null) {
                traceDetail = boolean1;
            } else {
                traceDetail = new Boolean(false);
            }
        }
        return traceDetail.booleanValue();
    }

    private static void setTraceDetail(boolean flag) {
        java.lang.Thread thread = java.lang.Thread.currentThread();
        detailMap.put(thread, new Boolean(flag));
    }

}
