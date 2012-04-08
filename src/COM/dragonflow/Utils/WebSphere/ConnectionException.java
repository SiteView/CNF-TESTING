/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.WebSphere;

public class ConnectionException extends java.lang.Exception {

    java.lang.Exception e;

    public ConnectionException(java.lang.Exception exception) {
        super("Inner exception is: " + exception);
        e = exception;
    }

    public ConnectionException(String s) {
        super(s);
    }

    public java.lang.Exception getInnerException() {
        return e;
    }
}
