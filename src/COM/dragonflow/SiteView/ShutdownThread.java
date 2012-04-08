/*
 * 
 * Created on 2005-2-16 16:50:27
 *
 * ShutdownThread.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ShutdownThread</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewGroup
public class ShutdownThread extends Thread {

    private String shutdownMessage;

    private int monitorStopTimeout;

    public ShutdownThread(int i, String s) {
        monitorStopTimeout = i;
        shutdownMessage = s;
    }

    public void run() {
        try {
            SiteViewGroup.currentSiteView().stopSiteViewNoTimeout(
                    monitorStopTimeout, shutdownMessage);
        } catch (Exception exception) {
            System.out.println("SiteView shutdown: stop failed "
                    + exception.getMessage());
            exception.printStackTrace();
        }
        synchronized (this) {
            notify();
        }
    }
}
