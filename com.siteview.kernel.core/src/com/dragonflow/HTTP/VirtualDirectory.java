/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class VirtualDirectory {

    String virtualPath;

    String documentPath;

    boolean cgiDirectory;

    VirtualDirectory(String s, String s1) {
        cgiDirectory = false;
        virtualPath = s;
        documentPath = s1;
    }

    VirtualDirectory(String s, String s1, boolean flag) {
        this(s, s1);
        cgiDirectory = flag;
    }

    String getVirtualPath() {
        return virtualPath;
    }

    String getDocumentPath() {
        return documentPath;
    }

    boolean isCGIDirectory() {
        return cgiDirectory;
    }

    String getFullDocumentPath(String s) {
        if (s.startsWith(virtualPath)) {
            return documentPath + s.substring(virtualPath.length());
        } else {
            return null;
        }
    }

    public String toString() {
        return "VirtualDirectory\n  virtual=" + virtualPath + "\n  document=" + documentPath + "\n  isCGI=" + cgiDirectory;
    }
}
