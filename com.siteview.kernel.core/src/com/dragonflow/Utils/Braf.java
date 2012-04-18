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
import java.io.IOException;
import java.io.RandomAccessFile;

// Referenced classes of package com.dragonflow.Utils:
// I18N

public final class Braf {

    byte buffer[];

    int buf_end;

    int buf_pos;

    int BUF_SIZE;

    java.io.RandomAccessFile file;

    int lineLength;

    public Braf(String s, long l) throws java.io.IOException {
        buf_end = 0;
        buf_pos = 0;
        BUF_SIZE = 32768;
        file = null;
        lineLength = 0;
        file = new RandomAccessFile(s, "r");
        file.seek(l);
        buffer = new byte[BUF_SIZE];
    }

    public final void close() throws java.io.IOException {
        if (file != null) {
            file.close();
        }
        file = null;
    }

    public final int read() throws java.io.IOException {
        if (buf_pos >= buf_end && fillBuffer() < 0) {
            return -1;
        }
        if (buf_end == 0) {
            return -1;
        } else {
            return buffer[buf_pos ++];
        }
    }

    public final long lastLineLength() throws java.io.IOException {
        return (long) lineLength;
    }

    private final int fillBuffer() throws java.io.IOException {
        int i = file.read(buffer, 0, BUF_SIZE);
        if (i >= 0) {
            buf_end = i;
            buf_pos = 0;
        }
        return i;
    }

    public final String readLine() throws java.io.IOException {
        return readLine(false);
    }

    public final String readLine(boolean flag) throws java.io.IOException {
        String s = readWholeLine();
        if (s != null) {
            lineLength = s.length();
        }
        if (!flag && s != null) {
            int i = s.length();
            do {
                if (i <= 0) {
                    break;
                }
                char c = s.charAt(i - 1);
                if (c != '\n' && c != '\r') {
                    break;
                }
                s = s.substring(0, -- i);
            } while (true);
        }
        return s;
    }

    public final String readWholeLine() throws java.io.IOException {
        String s = null;
        if (buf_end - buf_pos <= 0) {
            try {
                if (fillBuffer() < 0) {
                    throw new IOException("error in filling buffer!");
                }
            } catch (java.io.IOException ioexception) {
                return null;
            }
        }
        int i = -1;
        int j = buf_pos;
        do {
            if (j >= buf_end) {
                break;
            }
            if (buffer[j] == 10) {
                i = j;
                break;
            }
            j ++;
        } while (true);
        if (i > buf_pos) {
            int k = (i - buf_pos) + 1;
            s = new String(buffer, buf_pos, k, com.dragonflow.Utils.I18N.nullEncoding());
            buf_pos = i + 1;
        } else {
            StringBuffer stringbuffer = new StringBuffer(4096);
            byte byte0 = -1;
            int i1 = 0;
            do {
                int l;
                if (i1 >= 4096 || (l = read()) == -1) {
                    break;
                }
                stringbuffer.append((char) (l & 0xff));
                if (l == 10) {
                    if (stringbuffer.length() > 0) {
                        s = com.dragonflow.Utils.I18N.StringToUnicode(stringbuffer.toString(), com.dragonflow.Utils.I18N.nullEncoding());
                    }
                    break;
                }
                i1 ++;
            } while (true);
        }
        return s;
    }
}
