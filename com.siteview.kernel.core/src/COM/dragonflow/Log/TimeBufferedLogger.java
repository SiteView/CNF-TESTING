/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Date;

// Referenced classes of package COM.dragonflow.Log:
// Logger

class TimeBufferedLogger {

    protected COM.dragonflow.Log.Logger logger;

    protected java.util.Date expiration;

    protected long bufferDuration;

    static int uniqueID = 0;

    static java.lang.Object uniqueIDLock = new Object();

    private int id;

    public TimeBufferedLogger(COM.dragonflow.Log.Logger logger1) {
        this(logger1, false);
    }

    public TimeBufferedLogger(COM.dragonflow.Log.Logger logger1, boolean flag) {
        logger = logger1;
        synchronized (uniqueIDLock) {
            id = ++ uniqueID;
        }
        long l = logger1.getBufferDuration();
        if (l > 0L && l < 1000L) {
            l = 1000L;
        }
        bufferDuration = l;
        if (flag) {
            expiration = new Date();
        } else {
            updateExpiration();
        }
    }

    public COM.dragonflow.Log.Logger getLogger() {
        return logger;
    }

    public java.util.Date getExpiration() {
        return expiration;
    }

    public long getBufferDuration() {
        return bufferDuration;
    }

    public void updateExpiration() {
        expiration = new Date((new Date()).getTime() + bufferDuration);
    }

    public void forceImmediate() {
        expiration = new Date();
    }

    public void log() {
        logger.flush();
    }

    public int getUniqueID() {
        return id;
    }

}
