/*
 * 
 * Created on 2005-2-16 17:42:56
 *
 * URLDecodeInputStream.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>URLDecodeInputStream</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class URLDecodeInputStream extends FilterInputStream {

    public URLDecodeInputStream(InputStream inputstream) {
        super(inputstream);
    }

    public int read() throws IOException {
        // TODO need review
        int i = in.read();
        if (i == -1)
            return i;
        switch (i) {
        case 43:
            return 32;
        case 37: {
            StringBuffer stringbuffer = new StringBuffer(2);
            stringbuffer.append((char) in.read());
            stringbuffer.append((char) in.read());
            int j;
            try {
                j = Integer.parseInt(stringbuffer.toString(), 16);
            } catch (NumberFormatException numberformatexception) {
                throw new IllegalStateException();
            }
            return j;
        }
        default:
            return i;
        }
    }

    public int read(byte abyte0[], int i, int j) throws IOException {
        int k;
        for (k = 0; k < j; k++) {
            int l = read();
            if (l == -1) {
                return k != 0 ? k : -1;
            }
            abyte0[i++] = (byte) l;
        }

        return k;
    }
}
