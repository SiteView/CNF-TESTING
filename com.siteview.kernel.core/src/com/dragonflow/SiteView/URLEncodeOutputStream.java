/*
 * 
 * Created on 2005-2-16 17:42:24
 *
 * URLEncodeOutputStream.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>URLEncodeOutputStream</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

public class URLEncodeOutputStream extends FilterOutputStream {

    static BitSet dontNeedEncoding;

    static final int caseDiff = 32;

    public URLEncodeOutputStream(OutputStream outputstream) {
        super(outputstream);
    }

    public void write(int i) throws IOException {
        _encode((char) (i & 0xff));
    }

    private void _encode(char c) throws IOException {
        StringBuffer stringbuffer = new StringBuffer(3);
        if (dontNeedEncoding.get(c)) {
            if (c == ' ') {
                c = '+';
            }
            out.write(c);
        } else {
            stringbuffer.append('%');
            char c1 = Character.forDigit(c >> 4 & 0xf, 16);
            if (Character.isLetter(c1)) {
                c1 -= ' ';
            }
            stringbuffer.append(c1);
            c1 = Character.forDigit(c & 0xf, 16);
            if (Character.isLetter(c1)) {
                c1 -= ' ';
            }
            stringbuffer.append(c1);
            out.write(stringbuffer.toString().getBytes());
        }
    }

    static {
        dontNeedEncoding = new BitSet(256);
        for (int i = 97; i <= 122; i++) {
            dontNeedEncoding.set(i);
        }

        for (int j = 65; j <= 90; j++) {
            dontNeedEncoding.set(j);
        }

        for (int k = 48; k <= 57; k++) {
            dontNeedEncoding.set(k);
        }

        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
    }
}
