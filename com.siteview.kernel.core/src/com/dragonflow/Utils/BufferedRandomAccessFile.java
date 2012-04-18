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

public class BufferedRandomAccessFile extends java.io.RandomAccessFile {

    byte buff[];

    int buf_end;

    int buf_pos;

    long real_pos;

    int BUF_SIZE;

    public BufferedRandomAccessFile(java.io.File file, String s) throws java.io.IOException {
        super(file, s);
        buf_end = 0;
        buf_pos = 0;
        real_pos = 0L;
        BUF_SIZE = 32768;
        init();
    }

    public BufferedRandomAccessFile(java.io.File file, String s, int i) throws java.io.IOException {
        super(file, s);
        buf_end = 0;
        buf_pos = 0;
        real_pos = 0L;
        BUF_SIZE = 32768;
        BUF_SIZE = i;
        init();
    }

    public BufferedRandomAccessFile(String s, String s1) throws java.io.IOException {
        super(s, s1);
        buf_end = 0;
        buf_pos = 0;
        real_pos = 0L;
        BUF_SIZE = 32768;
        init();
    }

    public BufferedRandomAccessFile(String s, String s1, int i) throws java.io.IOException {
        super(s, s1);
        buf_end = 0;
        buf_pos = 0;
        real_pos = 0L;
        BUF_SIZE = 32768;
        BUF_SIZE = i;
        init();
    }

    private void init() throws java.io.IOException {
        invalidate();
        buff = new byte[BUF_SIZE];
    }

    public final int read() throws java.io.IOException {
        if (buf_pos >= buf_end && fillBuffer() < 0) {
            return -1;
        }
        if (buf_end == 0) {
            return -1;
        } else {
            return buff[buf_pos ++];
        }
    }

    private int fillBuffer() throws java.io.IOException {
        int i = super.read(buff, 0, BUF_SIZE);
        if (i >= 0) {
            real_pos += i;
            buf_end = i;
            buf_pos = 0;
        }
        return i;
    }

    private void invalidate() throws java.io.IOException {
        buf_end = 0;
        buf_pos = 0;
        real_pos = super.getFilePointer();
    }

    public int read(byte abyte0[], int i, int j) throws java.io.IOException {
        int k = buf_end - buf_pos;
        if (j <= k) {
            java.lang.System.arraycopy(buff, buf_pos, abyte0, i, j);
            buf_pos += j;
            return j;
        }
        for (int l = 0; l < j; l ++) {
            int i1 = read();
            if (i1 != -1) {
                abyte0[i + l] = (byte) i1;
            } else {
                return l;
            }
        }

        return j;
    }

    public long getFilePointer() throws java.io.IOException {
        long l = real_pos;
        return (l - (long) buf_end) + (long) buf_pos;
    }

    public void seek(long l) throws java.io.IOException {
        int i = (int) (real_pos - l);
        if (i >= 0 && i <= buf_end) {
            buf_pos = buf_end - i;
        } else {
            super.seek(l);
            invalidate();
        }
    }

    public final String getNextLine() throws java.io.IOException {
        String s = null;
        if (buf_end - buf_pos <= 0 && fillBuffer() < 0) {
            return null;
        }
        int i = -1;
        int j = buf_pos;
        do {
            if (j >= buf_end) {
                break;
            }
            if (buff[j] == 10) {
                i = j;
                break;
            }
            j ++;
        } while (true);
        if (i < 0) {
            StringBuffer stringbuffer = new StringBuffer(256);
            int k;
            while ((k = read()) != -1 && k != 10) {
                stringbuffer.append((char) k);
            }
            if (k == -1 && stringbuffer.length() == 0) {
                return null;
            }
            int l = stringbuffer.length();
            if (stringbuffer.charAt(l - 1) == '\r') {
                return stringbuffer.substring(0, l - 1);
            } else {
                return stringbuffer.toString();
            }
        }
        if (i > 0 && i > buf_pos && buff[i - 1] == 13) {
            s = new String(buff, buf_pos, i - buf_pos - 1);
        } else {
            s = new String(buff, buf_pos, i - buf_pos);
        }
        buf_pos = i + 1;
        return s;
    }
}
