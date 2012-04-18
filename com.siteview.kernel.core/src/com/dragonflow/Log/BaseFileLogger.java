/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Log;

import com.recursionsw.jgl.HashMap;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Log:
// Logger, LogManager
public class BaseFileLogger extends com.dragonflow.Log.Logger {

    protected StringBuffer buffer;

    long maxBufferSize;

    protected static long getLongSetting(HashMap hashmap, String s, long l) {
        long l1 = l;
        String s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, s);
        if (s1 != null) {
            try {
                l1 = java.lang.Long.parseLong(s1);
            } catch (java.lang.NumberFormatException numberformatexception) {
                java.lang.System.err.println("Failed to parse " + s1);
            }
        }
        return l1;
    }

    public BaseFileLogger(long l, int i) {
        super(l);
        maxBufferSize = 0L;
        initBuffer(i);
    }

    protected void initBuffer(int i) {
        buffer = new StringBuffer(i + 100);
        maxBufferSize = i;
    }

    public void flush() {
        synchronized (this) {
            if (buffer.length() > 0) {
                buffers.add(buffer);
                buffer = new StringBuffer();
            }
        }
        super.flush();
    }

    protected void addToFileBuffer(StringBuffer stringbuffer) {
        boolean flag = false;
        synchronized (this) {
            buffer.append(stringbuffer);
            if ((long) buffer.length() > maxBufferSize) {
                buffers.add(buffer);
                buffer = new StringBuffer();
                flag = true;
            }
        }
        if (flag) {
            com.dragonflow.Log.LogManager.triggerLogging(this);
        }
    }
}
