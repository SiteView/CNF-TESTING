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

public class SplitOutputStream extends java.io.FilterOutputStream {

    java.io.OutputStream sinkStream;

    public SplitOutputStream(java.io.OutputStream outputstream, java.io.OutputStream outputstream1) {
        super(outputstream);
        sinkStream = outputstream1;
    }

    public void write(int i) throws java.io.IOException {
        out.write(i);
        sinkStream.write(i);
    }

    public void write(byte abyte0[], int i, int j) throws java.io.IOException {
        for (int k = 0; k < j; k ++) {
            out.write(abyte0[i + k]);
            sinkStream.write(abyte0[i + k]);
        }

    }

    public void flush() throws java.io.IOException {
        out.flush();
        sinkStream.flush();
    }

    public void close() throws java.io.IOException {
        try {
            flush();
        } catch (java.io.IOException ioexception) {
        }
        out.close();
        sinkStream.close();
    }
}
