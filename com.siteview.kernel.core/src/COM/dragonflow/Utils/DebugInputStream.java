/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

class DebugInputStream extends java.io.InputStream {

    java.io.InputStream is;

    DebugInputStream(java.io.InputStream inputstream) {
        is = null;
        is = inputstream;
    }

    public int read() throws java.io.IOException {
        int i = is.read();
        java.lang.System.out.print((char) i);
        return i;
    }
}
