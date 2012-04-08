/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * J2EEAttachObserver.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>J2EEAttachObserver</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.StandardMonitor.J2EERealtimeMonitor;
//import COM.dragonflow.TopazIntegration.TopazManager;
import COM.dragonflow.Utils.TextUtils;

//import com.dragonflow.infra.jmdrv.AttachObserver;
//import com.dragonflow.infra.jmdrv.AttachedSession;
//import com.dragonflow.infra.jmdrv.Jmdrv;
//import com.dragonflow.infra.jmdrv.JmdrvException;
//import com.dragonflow.infra.jmdrv.Publisher;

// Referenced classes of package COM.dragonflow.SiteView:
// TopazConfigurator, Platform, MasterConfig

public class J2EEAttachObserver extends Thread //implements AttachObserver 
{

//    private Jmdrv mJMDRVInstance;
//
//    private AttachedSession session;
//
//    private Publisher mPublisher;
//
    private boolean mShutdown;

    private LinkedList mQueue;

    private static final int MAX_QUEUE_SIZE = 10;

    private static final String TOPAZ_BUS_PORT = "63436";

    private static int chunkSize;

    private static final String statusStrings[] = { "STATUS_UNKNOWN", "STATUS_DOWN", "STATUS_FAILED", "STATUS_KILLED",
            "STATUS_UNKNOWN", "STATUS_UNKNOWN", "STATUS_UNKNOWN", "STATUS_ATTACHED", "STATUS_DETACHED",
            "STATUS_PS_POST_GUARANTEED", "STATUS_STUBBORN_STOPED" };

    public static final long STATUS_LAST = 1024L;

    private static final String logFile;

    private static final String JMDRV_LABEL = "PROJECT_TOPAZ";

    private static final String MDRV_PUBLISH_SUBJECT = "online_ss_data";

    private String mTopazUrl;

    private String mTimeDiffServer;

    public boolean mRealtimeBusSeparate;

    public long mTimeDiff;

    private long mStatus;

    private J2EERealtimeMonitor owner;

    public J2EEAttachObserver(J2EERealtimeMonitor j2eerealtimemonitor) {
        mShutdown = false;
        mQueue = new LinkedList();
        mTimeDiffServer = null;
        mRealtimeBusSeparate = false;
        mTimeDiff = 0L;
        mStatus = 0L;
        owner = j2eerealtimemonitor;
//        try {
//            jgl.HashMap hashmap = TopazConfigurator.getTopazConfig();
//            mTopazUrl = TextUtils.getValue(hashmap, "_topazAdminServerAddress");
//            if (mTopazUrl == null || mTopazUrl.length() < 1) {
//                URL url = new URL(TopazManager.getInstance().getTopazServerSettings().getAdminServerUrl());
//                mTopazUrl = url.getHost();
//            } else {
//                mRealtimeBusSeparate = true;
//                mTimeDiffServer = TextUtils.getValue(hashmap, "_topazRealtimeGraphServer");
//                if (mTimeDiffServer == null || mTimeDiffServer.length() < 1) {
//                    mTimeDiffServer = mTopazUrl;
//                }
//            }
//        } catch (MalformedURLException malformedurlexception) {
//            mTopazUrl = null;
//        } catch (Exception exception) {
//            TextUtils.debugPrint("Unable to allocate stream in cache");
//        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public void run() {
        try {
//            mJMDRVInstance = new Jmdrv(COM.dragonflow.MdrvMain.MdrvMain.Settings.MAIN_JMDRV_CMDLINE, 0);
//            if (session == null) {
//                session = mJMDRVInstance.attachRemote(this, "-lnch_db_url http://" + mTopazUrl + "/topaz/"
//                        + " -lnch_remote_connection_port " + "63436", "PROJECT_TOPAZ", "131072", null, null, null,
//                                                      36936);
//            }
//            mPublisher = mJMDRVInstance.createPublisher("online_ss_data", 3);
            int i = 0;
            long l = System.currentTimeMillis();
            while (true) {
                if (i == 0 && mRealtimeBusSeparate) {
                    try {
                        URL url = new URL("http://" + mTimeDiffServer + "/topaz/j2ee/meter/ScopeServlet?getTime=");
                        URLConnection urlconnection = url.openConnection();
                        DataInputStream datainputstream = new DataInputStream(urlconnection.getInputStream());
                        long l1 = datainputstream.readLong();
                        mTimeDiff = l1 - System.currentTimeMillis();
                        datainputstream.close();
                    } catch (Exception exception1) {
                        System.err.println("Can't sync realtime time diff with topaz " + exception1);
                    }
                } else {
//                    mTimeDiff = TopazManager.getInstance().getTopazServerSettings().getTimeDiff() * 1000L;
                }
                if (++i > 240) {
                    i = 0;
                }
//                mJMDRVInstance.doWait(2500L, 0);
                publishQueue();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        mStatus = 256L;
    }

    public void onStatus(long l) {
        mStatus = l;
    }

    public void send(List list) {
        synchronized (mQueue) {
            if (mQueue.size() >= 10) {
                mQueue.removeFirst();
            }
            mQueue.addLast(list);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    private void publishQueue() {
        synchronized (this.mQueue) {
            while (!mQueue.isEmpty()) {
                List list = (List) mQueue.getFirst();
                try {
                    ArrayList arraylist = new ArrayList();
                    int i = list.size();
                    for (int j = 0; j < i; j += chunkSize) {
                        int k = j + chunkSize;
                        if (k > i) {
                            k = i;
                        }
                        byte bs[] = encode(new ArrayList(list.subList(j, k)));
//                        mPublisher.publish(bs, 0);
                    }

                    mQueue.removeFirst();
                }
//				catch (JmdrvException e) 
//				{
//                    e.printStackTrace();
//                    LogManager.log("error", "Failed to publish J2EE Realtime results to " + mTopazUrl);
//                    mStatus = 4L;
//                    mQueue.removeFirst();
//                }
				catch (Exception e) {
                    mQueue.removeFirst();
                }
            }
        }
    }

    public void shutDown() {
        mShutdown = true;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param list
     * @return
     */
    private static byte[] encode(List list) {
        try {
            ByteArrayOutputStream bytearrayoutputstream;
            bytearrayoutputstream = new ByteArrayOutputStream();
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);
            objectoutputstream.writeObject(list);
            return bytearrayoutputstream.toByteArray();
        } catch (IOException e) {
            TextUtils.debugPrint("Problem in writing to stream");
            e.printStackTrace();
            return null;
        }
    }

    public long getStatus() {
        return mStatus;
    }

    public static String getStatusString(long l) {
        StringBuffer stringbuffer = new StringBuffer();
        String s = "";
        int i = 0;
        for (long l1 = 1L; i <= statusStrings.length; l1 <<= 1) {
            if ((l & l1) != 0L) {
                stringbuffer.append(s + statusStrings[i]);
                s = "|";
            }
            i++;
        }

        return stringbuffer.toString();
    }

    static {
        logFile = Platform.getRoot() + File.separator + "logs" + File.separator + "j2ee_mdrv.log";
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        chunkSize = TextUtils.toInt(TextUtils.getValue(hashmap, "_J2EERealtimeMonitorChunkSize"));
        if (chunkSize <= 0) {
            chunkSize = 500;
        }
    }
}
