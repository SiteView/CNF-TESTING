/*
 * 
 * Created on 2005-2-16 16:49:58
 *
 * ServerReadThread.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ServerReadThread</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewGroup, ServerControlThread, MasterConfig, Server,
// Platform

class ServerReadThread extends Thread {

    long pause;

    boolean done;

    int index;

    ServerControlThread controlThread;

    public ServerReadThread(ServerControlThread servercontrolthread, long l,
            int i) {
        super((Runnable) null);
        done = false;
        index = 0;
        controlThread = null;
        controlThread = servercontrolthread;
        pause = l;
        index = i;
    }

    public void run() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        while (!done) {
            HashMap hashmap = controlThread.nextServer();
            if (hashmap != null) {
                StringBuffer stringbuffer = new StringBuffer();
                long l = Server.fetchServerState(
                        MasterConfig.getMasterConfig(), hashmap, stringbuffer);
                hashmap.put("_status", "" + l);
                hashmap.put("_contents", stringbuffer.toString());
                Platform.sleep(pause);
            } else {
                synchronized (controlThread) {
                    try {
                        controlThread.wait();
                    } catch (Exception exception) {
                        System.out
                                .println("Exception waiting for Server Control Thread: "
                                        + exception);
                    }
                }
            }
        }
    }
}
