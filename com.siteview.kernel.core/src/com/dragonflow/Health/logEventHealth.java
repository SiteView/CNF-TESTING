/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Hashtable;
import java.util.Vector;

public class logEventHealth {

    private static java.util.Vector searchStrings = new Vector();

    private static java.util.Hashtable matches = new Hashtable();

    public logEventHealth() {
    }

    public static void reset() {
        matches.clear();
    }

    public static java.util.Vector getMatches(String s) {
        return (java.util.Vector) matches.get(s);
    }

    public static void addSearchString(String s) {
        if (!searchStrings.contains(s)) {
            searchStrings.add(s);
        }
    }

    public static void log(String s) {
        java.util.Iterator iterator = searchStrings.iterator();
        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(s1);
            java.util.regex.Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                java.util.Vector vector = (java.util.Vector) matches.get(s1);
                if (vector != null) {
                    vector.add(s);
                } else {
                    java.util.Vector vector1 = new Vector();
                    vector1.add(s);
                    matches.put(s1, vector1);
                }
            }
        } 
    }

    public String toString() {
        return "logEvent Health - watches various log files for strings, usually error.log";
    }

}
