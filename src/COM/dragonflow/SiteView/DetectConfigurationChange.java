/*
 * 
 * Created on 2005-2-15 12:50:01
 *
 * DetectConfigurationChange.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>DetectConfigurationChange</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import jgl.Array;
import jgl.HashMap;
import jgl.HashMapIterator;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Utils.FileUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, SiteViewGroup, TopazConfigurator

public class DetectConfigurationChange extends Action {

    private HashMap fileNameMap;

    private static boolean configHasChanged = false;

    private static DetectConfigurationChange ourInstance;

    public DetectConfigurationChange() {
        fileNameMap = new HashMap();
    }

    public static synchronized DetectConfigurationChange getInstance() {
        if (ourInstance == null) {
            ourInstance = new DetectConfigurationChange();
        }
        ourInstance.initialize();
        return ourInstance;
    }

    public void initialize() {
        runType = 1;
    }

    public void setConfigChangeFlag() {
        configHasChanged = true;
    }

    private void clearConfigChangeFlag() {
        configHasChanged = false;
    }

    public boolean isConfigChanged() {
        return configHasChanged;
    }

    public synchronized boolean execute() {
        try {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            Array array = new Array();
            Array array1 = new Array();
            Array array2 = new Array();
            Array array3 = siteviewgroup.getGroupFiles();
            Enumeration enumeration = array3.elements();
            File file = null;
            File file1 = null;
            File file2 = null;
            File file3 = null;
            HashSet hashset = new HashSet();
            for (Enumeration enumeration1 = fileNameMap.keys(); enumeration1
                    .hasMoreElements(); hashset.add(enumeration1.nextElement())) {
            }
            String s;
            for (; enumeration.hasMoreElements(); hashset.remove(s)) {
                File file4 = (File) enumeration.nextElement();
                s = file4.getAbsolutePath();
                Long long1 = (Long) fileNameMap.get(s);
                if (long1 == null) {
                    if (file4.getName().equals("history.config")) {
                        file = file4;
                    } else if (file4.getName().equals("dynamic.config")) {
                        file2 = file4;
                    } else {
                        array.add(file4);
                    }
                } else if (long1.longValue() != file4.lastModified()) {
                    if (file4.getName().equals("history.config")) {
                        file1 = file4;
                    } else if (file4.getName().equals("dynamic.config")) {
                        file3 = file4;
                    } else {
                        array1.add(file4);
                        if (file4.getAbsolutePath().endsWith(".mg")) {
//                            TopazConfigurator.checkModified(file4);
                        }
                    }
                }
                long1 = new Long(file4.lastModified());
                fileNameMap.put(s, long1);
            }

            if (file2 != null) {
                array.add(file2);
            }
            if (file3 != null) {
                array1.add(file3);
            }
            if (file != null) {
                array.add(file);
            }
            if (file1 != null) {
                array1.add(file1);
            }
            String s1;
            for (Iterator iterator = hashset.iterator(); iterator.hasNext(); LogManager
                    .log("Debug", "group deleted: " + s1)) {
                s1 = (String) iterator.next();
                fileNameMap.remove(s1);
                File file5 = new File(s1);
                array2.add(file5);
                if (file5.getAbsolutePath().endsWith(".mg")) {
//                    TopazConfigurator.checkDeleted(file5);
                }
            }

            HashMap hashmap = new HashMap();
            siteviewgroup.adjustGroups(array, array1, array2, hashmap);
            for (HashMapIterator hashmapiterator = hashmap.begin(); !hashmapiterator
                    .atEnd(); hashmapiterator.advance()) {
                fileNameMap.put(hashmapiterator.key(), hashmapiterator.value());
            }

        } catch (Exception exception) {
            LogManager
                    .log("Error", "Error loading configuration: " + exception);
            LogManager.log("Error", "  detail: "
                    + FileUtils.stackTraceText(exception));
        }
        clearConfigChangeFlag();
        return true;
    }

    public String toString() {
        return "detect configuration change";
    }

    public synchronized void resetFileTimeStamp(String s) {
        String s1 = (new File(s)).getAbsolutePath();
        Long long1 = (Long) fileNameMap.get(s1);
        if (long1 != null) {
            fileNameMap.remove(s1);
            fileNameMap.put(s1, new Long(0L));
        }
    }

}
