/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class SSstreamReader {

    byte m_buffer[];

    java.io.InputStream m_socketStream;

    int m_reqid;

    boolean m_debug;

    public SSstreamReader(java.io.InputStream inputstream, int i, boolean flag) {
        m_debug = false;
        m_socketStream = inputstream;
        m_reqid = i;
        m_debug = flag;
        m_buffer = new byte[4096];
    }

    public String readLine() throws java.lang.Exception {
        int j = 0;
        int i;
        do {
            try {
                i = m_socketStream.read();
            } catch (java.lang.Exception exception) {
                if (m_debug) {
                    java.lang.System.out.println(m_reqid + "-SSstreamReader caught exception reading: " + exception);
                }
                throw exception;
            }
            if (i == -1) {
                break;
            }
            m_buffer[j ++] = (byte) i;
        } while (i != 10);
        if (j == 0) {
            return null;
        }
        String s = null;
        try {
            s = new String(m_buffer, 0, j, "UTF-8");
        } catch (java.lang.Exception exception1) {
            java.lang.System.out.println(m_reqid + "-SSstreamReader.readLine caught exception utf-8 decoding: " + exception1);
            s = null;
        }
        if (s != null) {
            return s;
        }
        try {
            s = new String(m_buffer, 0, j, "8859_1");
        } catch (java.lang.Exception exception2) {
            java.lang.System.out.println(m_reqid + "-SSstreamReader.readLine caught exception 8859_1 decoding: " + exception2);
            s = null;
        }
        return s;
    }

    public String read(int i) throws java.lang.Exception {
        byte abyte0[] = new byte[i + 2];
        int j = 0;
        int k = i;
        do {
            if (k <= 0) {
                break;
            }
            int l = -1;
            try {
                l = m_socketStream.read(abyte0, j, k);
            } catch (java.lang.Exception exception) {
                java.lang.System.out.println(m_reqid + "-SSstreamReader.read caught exception reading: " + exception);
                throw exception;
            }
            if (l == -1) {
                break;
            }
            j += l;
            k -= l;
        } while (true);
        String s = null;
        try {
            s = new String(abyte0, 0, i, "UTF-8");
        } catch (java.lang.Exception exception1) {
            java.lang.System.out.println(m_reqid + "-SSstreamReader.read caught exception utf-8 decoding: " + exception1);
            s = null;
        }
        if (s != null) {
            return s;
        }
        try {
            s = new String(abyte0, 0, i, "8859_1");
        } catch (java.lang.Exception exception2) {
            java.lang.System.out.println(m_reqid + "-SSstreamReader.read caught exception 8859_1 decoding: " + exception2);
            s = null;
        }
        return s;
    }
}
