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

public class RawReader extends java.io.InputStreamReader {

    private java.io.InputStream in;

    public RawReader(java.io.InputStream inputstream) {
        super(inputstream);
        in = null;
        in = inputstream;
    }

    public RawReader(java.io.InputStream inputstream, String s) throws java.io.UnsupportedEncodingException {
        super(inputstream, s);
        in = null;
    }

    public int read() throws java.io.IOException {
        return in.read() & 0xff;
    }

    public synchronized int read(char ac[], int i, int j) throws java.io.IOException {
        byte abyte0[] = new byte[ac.length];
        boolean flag = false;
        int l = 0;
        do {
            l = in.read(abyte0, i, j);
        } while (l == 0);
        for (int k = 0; k < l; k ++) {
            ac[k] = (char) (abyte0[k] & 0xff);
        }

        return l;
    }
}
