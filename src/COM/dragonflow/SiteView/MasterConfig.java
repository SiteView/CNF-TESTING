/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MasterConfig.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MasterConfig</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Utils.FileUtils;
import SiteViewMain.SiteViewSupport;

// Referenced classes of package COM.dragonflow.SiteView:
// Platform

public class MasterConfig {

    private static boolean shutdownDueToError = false;

    private static HashMap masterConfig = null;

    private static boolean masterConfigShouldExist = false;

    private static boolean retryMasterConfigReads;

    private static final String MASTER_CONFIG;

    public MasterConfig() {
    }

    private static HashMap loadMasterConfig() {
        if (shutdownDueToError) {
            return null;
        }
        HashMap hashmap = new HashMap();
        Array array = null;
        try {
            array = FrameFile.readFromFile(Platform.getRoot() + File.separator
                    + "groups" + File.separator + "master.config");
            hashmap = (HashMap) array.front();
            if (!masterConfigShouldExist) {
                masterConfigShouldExist = true;
            }
        } catch (Exception exception) {
            if (masterConfigShouldExist) {
                if (retryMasterConfigReads) {
                    System.gc();
                    for (int i = 0; i < 10
                            && (array == null || array.size() == 0); i++) {
                        Platform.sleep(1000L);
                        try {
                            array = FrameFile.readFromFile(MASTER_CONFIG);
                            hashmap = (HashMap) array.front();
                        } catch (Exception exception1) {
                        }
                    }

                }
                if (array == null || array.size() == 0) {
                    shutdownDueToError = true;
                    System.out.println("Cannot read Master.config");
                    LogManager.log("Error", Platform.productName
                            + " shutting down..Cannot read Master.config");
                    SiteViewSupport.ShutdownProcess();
                    return null;
                }
            }
        }
        return hashmap;
    }

    public static HashMap getMasterConfig() {
        if (shutdownDueToError) {
            return null;
        }
        if (masterConfig == null) {
            synchronized (FileUtils.getFileLock(MASTER_CONFIG)) {
                if (masterConfig == null) {
                    masterConfig = loadMasterConfig();
                }
            }
        }
        return masterConfig;
    }

    public static void clearConfigCache() {
        masterConfig = null;
    }

    public static void saveMasterConfig(HashMap hashmap) {
        if (shutdownDueToError) {
            return;
        }
        masterConfig = hashmap;
        try {
            String s = Platform.getRoot() + "/groups/master.config";
            Array array = new Array();
            array.add(hashmap);
            FrameFile.writeToFile(s, array, "_", true, true);
            Platform.chmod(s, "rw");
            if (!masterConfigShouldExist) {
                masterConfigShouldExist = true;
            }
        } catch (Exception exception) {
            LogManager.log("Error", "saving master.config: " + exception);
        }
    }

    static {
        MASTER_CONFIG = Platform.getRoot() + File.separator + "groups"
                + File.separator + "master.config";
        String s = System.getProperty("retryMasterConfigReads");
        if (s != null && s.equals("true")) {
            retryMasterConfigReads = true;
        }
    }
}
