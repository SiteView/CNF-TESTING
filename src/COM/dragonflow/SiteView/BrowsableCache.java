/*
 * 
 * Created on 2005-2-15 11:41:35
 *
 * BrowsableCache.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableCache</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.net.URLDecoder;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.datachannel.xml.om.Document;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.BrowsableProperty;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableMonitor, Platform, AtomicMonitor

public class BrowsableCache {

    public BrowsableCache() {
    }

    public static synchronized String getUniqueID() {
        long l = Platform.timeMillis();
        String s;
        for (s = "" + l; getCache(s, false, false) != null; s = "" + l)
            l++;

        getCache(s, true, false);
        return s;
    }

    public static synchronized HashMap getCache(String s, boolean flag,
            boolean flag1) {
        HashMap hashmap = null;
        for (int i = 0; i < browseCache.size(); i++) {
            HashMap hashmap1 = (HashMap) browseCache.at(i);
            String s1 = (String) hashmap1.get("id");
            if (!s1.equals(s)) {
                try {
                    long l = (new Long(s1)).longValue();
                    if (Platform.timeMillis() - l > 0x36ee80L) {
                        browseCache.remove(i);
                        i--;
                    }
                } catch (Exception exception) {
                }
                continue;
            }
            if (flag1) {
                browseCache.remove(i);
                i--;
                deleteCacheFile(s);
            } else {
                hashmap = hashmap1;
            }
        }

        if (hashmap == null) {
            hashmap = getCacheFromFile(s);
            if (hashmap != null)
                browseCache.add(hashmap);
        }
        if (hashmap == null && flag) {
            hashmap = new HashMap();
            hashmap.add("id", s);
            hashmap.add("xml", new Document());
            hashmap.add("tree", new HashMap());
            hashmap.add("selectNames", new HashMap());
            hashmap.add("selectIDs", new HashMap());
            hashmap.add("permanentSelectNames", new HashMap());
            hashmap.add("permanentSelectIDs", new HashMap());
            hashmap.add("cParms", new HashMap());
            hashmap.add("mProp", new HashMap());
            browseCache.add(hashmap);
        }
        return hashmap;
    }

    public static void clearSelections(String s) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null) {
            HashMap hashmap1 = (HashMap) hashmap.get("selectNames");
            hashmap1.clear();
            hashmap1 = (HashMap) hashmap.get("selectIDs");
            hashmap1.clear();
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @param flag1
     * @return
     */
    public static Array getSelections(String s, boolean flag, boolean flag1) {
        Array array = new Array();
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null) {
            HashMap hashmap1;
            HashMap hashmap2;
            if (flag1) {
                hashmap1 = (HashMap) hashmap.get("permanentSelectNames");
                hashmap2 = (HashMap) hashmap.get("permanentSelectIDs");
            } else {
                hashmap1 = (HashMap) hashmap.get("selectNames");
                hashmap2 = (HashMap) hashmap.get("selectIDs");
            }
            if (flag) {
                Enumeration enumeration = hashmap1.keys();
                while (enumeration.hasMoreElements()) {
                    hashmap1.put(enumeration.nextElement(), "");
                }
            }
            array.add(hashmap1);
            array.add(hashmap2);
            return array;
        } else {
            return new Array();
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param browsablemonitor
     * @param s
     * @param flag
     * @param s1
     * @param httprequest
     * @param flag1
     */
    public static void mergeSelections(BrowsableMonitor browsablemonitor,
            String s, boolean flag, String s1, HTTPRequest httprequest,
            boolean flag1) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null) {
            HashMap hashmap1 = (HashMap) hashmap.get("selectNames");
            HashMap hashmap2 = (HashMap) hashmap.get("selectIDs");
            if (flag) {
                hashmap1.clear();
                hashmap2.clear();
            }
            String s2 = "";
            String s5 = "";
            HashMap hashmap3 = new HashMap();
            String s3;
            String s7;
            for (Enumeration enumeration = httprequest
                    .getValues(BrowsableProperty.BROWSE); enumeration
                    .hasMoreElements(); hashmap2.put(s3, s7)) {
                s3 = (String) enumeration.nextElement();
                if (s3.indexOf(":object") > 0)
                    s3 = s3.substring(0, s3.indexOf(":objec"));
                Object obj = httprequest.variables.get("SELECTED" + s3 + "ID");
                s7 = "";
                if (obj instanceof Array)
                    s7 = (String) ((Array) obj).at(0);
                else
                    s7 = (String) obj;
                if (s7 == null)
                    s7 = "";
                if (s7.length() > 0
                        && browsablemonitor.manageBrowsableSelectionsByID()) {
                    String s8 = findNameFromID(hashmap2, s7, browsablemonitor);
                    if (s8 != null)
                        s3 = s8;
                }
                hashmap3.put(s3, s1);
                hashmap1.put(s3, s1);
            }

            Enumeration enumeration1 = hashmap1.keys();
            while (enumeration1.hasMoreElements()) {
                String s4 = (String) enumeration1.nextElement();
                String s6 = (String) hashmap1.get(s4);
                if (s6.equals("visible") && hashmap3.get(s4) == null) {
                    hashmap1.remove(s4);
                    hashmap2.remove(s4);
                }
            } 
            
            if (flag1) {
                HashMap hashmap4 = (HashMap) hashmap
                        .get("permanentSelectNames");
                HashMap hashmap5 = (HashMap) hashmap.get("permanentSelectIDs");
                String s9;
                String s10;
                for (Enumeration enumeration2 = hashmap1.keys(); enumeration2
                        .hasMoreElements(); hashmap4.put(s9, s10)) {
                    s9 = (String) enumeration2.nextElement();
                    s10 = (String) hashmap1.get(s9);
                }

                String s11;
                String s12;
                for (Enumeration enumeration3 = hashmap2.keys(); enumeration3
                        .hasMoreElements(); hashmap5.put(s11, s12)) {
                    s11 = (String) enumeration3.nextElement();
                    s12 = (String) hashmap2.get(s11);
                }

            }
        }
    }

    private static String findNameFromID(HashMap hashmap, String s,
            BrowsableMonitor browsablemonitor) {
        s = URLDecoder.decode(s);
        for (Enumeration enumeration = hashmap.keys(); enumeration
                .hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            String s2 = URLDecoder.decode((String) hashmap.get(s1));
            if (browsablemonitor.areBrowseIDsEqual(s2, s))
                return s1;
        }

        return null;
    }

    public static void updateCache(String s) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null) {
            HashMap hashmap1 = (HashMap) hashmap.get("permanentSelectIDs");
            HashMap hashmap2 = (HashMap) hashmap.get("selectNames");
            HashMap hashmap3 = (HashMap) hashmap.get("selectIDs");
            hashmap2.clear();
            hashmap3.clear();
            String s1;
            String s2;
            for (Enumeration enumeration = hashmap1.keys(); enumeration
                    .hasMoreElements(); hashmap3.put(s1, s2)) {
                s1 = (String) enumeration.nextElement();
                s2 = (String) hashmap1.get(s1);
                hashmap2.put(s1, "");
            }

        }
    }

    public static void clearState(String s) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null) {
            HashMap hashmap1 = (HashMap) hashmap.get("tree");
            hashmap1.clear();
        }
    }

    public static HashMap getCurrentState(String s) {
        HashMap hashmap = new HashMap();
        HashMap hashmap1 = getCache(s, false, false);
        if (hashmap1 != null)
            hashmap = (HashMap) hashmap1.get("tree");
        return hashmap;
    }

    public static void saveCurrentState(String s, HashMap hashmap) {
        HashMap hashmap1 = getCache(s, false, false);
        if (hashmap1 != null)
            hashmap1.put("tree", hashmap);
    }

    static HashMap getCacheFromFile(String s) {
        HashMap hashmap = null;
        Object obj = null;
        String s1 = Platform.getRoot() + File.separator
                + "templates.applications" + File.separator + s + ".cache";
        try {
            Array array = FrameFile.readFromFile(s1);
            hashmap = new HashMap();
            hashmap.add("id", s);
            hashmap.add("xml", new Document());
            hashmap.add("tree", array.at(0));
            hashmap.add("selectNames", array.at(1));
            hashmap.add("selectIDs", array.at(2));
            hashmap.add("permanentSelectNames", array.at(1));
            hashmap.add("permanentSelectIDs", array.at(2));
            hashmap.add("cParms", array.at(3));
            hashmap.add("mProp", array.at(4));
        } catch (Exception exception) {
        }
        return hashmap;
    }

    public static void saveCache(String s) {
        for (int i = 0; i < browseCache.size(); i++) {
            HashMap hashmap = (HashMap) browseCache.at(i);
            String s1 = (String) hashmap.get("id");
            if (!s1.equals(s))
                continue;
            String s2 = Platform.getRoot() + File.separator
                    + "templates.applications" + File.separator + s + ".cache";
            try {
                Array array = new Array();
                array.add(hashmap.get("tree"));
                array.add(hashmap.get("selectNames"));
                array.add(hashmap.get("selectIDs"));
                array.add(hashmap.get("cParms"));
                array.add(hashmap.get("mProp"));
                FrameFile.writeToFile(s2, array);
            } catch (Exception exception) {
                LogManager.log("error",
                        "browsablePage.saveCache unable to write " + s2 + ", "
                                + exception.getMessage());
            }
        }

    }

    static void deleteCacheFile(String s) {
        String s1 = Platform.getRoot() + File.separator
                + "templates.applications" + File.separator + s + ".cache";
        try {
            FileUtils.delete(s1);
        } catch (Exception exception) {
            LogManager.log("error",
                    "browsablePage.deleteCacheFile unable to delete " + s1
                            + ", " + exception.getMessage());
        }
        deleteOldCacheFiles();
    }

    static void deleteOldCacheFiles() {
        String s = Platform.getRoot() + File.separator
                + "templates.applications";
        try {
            File file = new File(s);
            if (file.isDirectory()) {
                String as[] = file.list();
                for (int i = 0; i < as.length; i++)
                    if (as[i].endsWith(".cache.bak")
                            || as[i].endsWith(".cache")) {
                        File file1 = new File(s + File.separator + as[i]);
                        if (as[i].endsWith(".cache.bak")
                                || Platform.timeMillis() - file1.lastModified() > 0x5265c00L)
                            file1.delete();
                    }

            }
        } catch (Exception exception) {
        }
    }

    public static Document getXml(String s) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null)
            return (Document) hashmap.get("xml");
        else
            return new Document();
    }

    public static void saveXml(String s, Document document) {
        HashMap hashmap = getCache(s, false, false);
        if (hashmap != null)
            hashmap.put("xml", document);
    }

    public static String getXmlFile(String s) {
        String s1 = "";
        String s2 = Platform.getRoot() + File.separator
                + "templates.applications" + File.separator + s;
        try {
            s1 = FileUtils.readFile(s2).toString().trim();
        } catch (Exception exception) {
            s1 = "";
        }
        return s1;
    }

    public static void saveXmlFile(String s, String s1) {
        String s2 = Platform.getRoot() + File.separator
                + "templates.applications" + File.separator + s;
        try {
            FileUtils.writeFile(s2, s1);
        } catch (Exception exception) {
            LogManager.log("error",
                    "browsablePage.saveXmlFile unable to write " + s + ", "
                            + exception.getMessage());
        }
    }

    public static void deleteXmlFile(String s) {
        String s1 = Platform.getRoot() + File.separator
                + "templates.applications" + File.separator + s;
        try {
            FileUtils.delete(s1);
        } catch (Exception exception) {
            LogManager.log("error",
                    "browsablePage.deleteXmlFile unable to delete " + s + ", "
                            + exception.getMessage());
        }
    }

    public static String getXmlFileName(AtomicMonitor atomicmonitor) {
        String s = (String) atomicmonitor.getClassProperty("class");
        if (s.endsWith("Monitor"))
            s = s.substring(0, s.length() - 7);
        Array array = ((BrowsableMonitor) atomicmonitor)
                .getConnectionProperties();
        for (int i = 0; i < array.size(); i++) {
            String s1 = atomicmonitor
                    .getProperty(((StringProperty) array.at(i)).getName());
            if (!((StringProperty) array.at(i)).isPassword
                    && TextUtils
                            .keepChars(s1,
                                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._$")
                            .equals(s1)) {
                s = s + "_" + s1;
                continue;
            }
            String s2 = TextUtils.obscure(s1);
            if (s2.length() > 4)
                s = s + "_" + s2.substring(4);
        }

        if (s.length() > 210)
            s = s.substring(0, 210);
        s = s + ".xml";
        return s;
    }

    public static final String KEY_ID = "id";

    public static final String KEY_XML = "xml";

    public static final String KEY_TREE = "tree";

    public static final String KEY_SELECT_NAMES = "selectNames";

    public static final String KEY_SELECT_IDS = "selectIDs";

    public static final String KEY_PERM_SELECT_NAMES = "permanentSelectNames";

    public static final String KEY_PERM_SELECT_IDS = "permanentSelectIDs";

    public static final String KEY_CPARMS = "cParms";

    public static final String KEY_MPROP = "mProp";

    static final String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._$";

    static final int MAX_FILENAME_LENGTH = 210;

    private static Array browseCache = new Array();

    static {
        deleteOldCacheFiles();
    }
}
