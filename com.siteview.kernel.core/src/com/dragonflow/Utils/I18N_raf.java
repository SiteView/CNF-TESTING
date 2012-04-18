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

public final class I18N_raf {

    byte buffer[];

    int buf_end;

    int buf_pos;

    private final int BUF_SIZE = 32768;

    java.io.RandomAccessFile file;

    public I18N_raf(String s, long l) throws java.io.IOException {
        buf_end = 0;
        buf_pos = 0;
        file = null;
        file = new RandomAccessFile(s, "r");
        file.seek(l);
        buffer = new byte[32768];
    }

    public final void close() throws java.io.IOException {
        if (file != null) {
            file.close();
        }
        file = null;
    }

    public final byte read() throws java.io.IOException {
        if (buf_pos >= buf_end && fillBuffer() < 0) {
            return -1;
        }
        if (buf_end == 0) {
            return -1;
        } else {
            return buffer[buf_pos ++];
        }
    }

    private final int fillBuffer() throws java.io.IOException {
        int i = file.read(buffer, 0, 32768);
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
            s = new String(buffer, buf_pos, k);
            buf_pos = i + 1;
        } else {
            byte abyte0[] = new byte[4096];
            byte byte0 = -1;
            int l = 0;
            do {
                byte byte1;
                if (l >= 4096 || (byte1 = read()) == -1) {
                    break;
                }
                abyte0[l] = byte1;
                if (byte1 == 10) {
                    if (l > 0) {
                        s = new String(abyte0, 0, l);
                    }
                    break;
                }
                l ++;
            } while (true);
        }
        return s;
    }
}
