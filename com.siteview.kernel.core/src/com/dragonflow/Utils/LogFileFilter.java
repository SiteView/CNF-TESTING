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

import COM.oroinc.text.perl.Perl5Util;

public class LogFileFilter implements java.io.FilenameFilter {

    protected String pattern;

    protected COM.oroinc.text.perl.Perl5Util perl;

    public LogFileFilter(String s) {
        pattern = s;
        perl = new Perl5Util();
    }

    public boolean accept(java.io.File file, String s) {
        boolean flag = false;
        flag = perl.match(pattern, s);
        return flag;
    }
}
