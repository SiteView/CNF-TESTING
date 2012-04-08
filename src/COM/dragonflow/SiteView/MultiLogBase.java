/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * MultiLogBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>MultiLogBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jgl.Array;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.Braf;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.LogFileFilter;
import COM.dragonflow.Utils.SerializerUtils;
import COM.oroinc.text.perl.Perl5Util;

// Referenced classes of package COM.dragonflow.SiteView:
// ServerMonitor, Platform, Machine

public abstract class MultiLogBase extends ServerMonitor {

    protected static StringProperty pCurrentLogFileList;

    protected static NumericProperty pMatchCount;

    protected static final String currentLogFileListName = "currentLogFileList";

    protected static final String matchCountName = "matchCount";

    private Perl5Util perl;

    protected HashMap currentLogFileList;

    public MultiLogBase() {
        perl = new Perl5Util();
        currentLogFileList = new HashMap();
    }

    public abstract String getLogPath();

    public abstract String getFileNameMatchCriteria();

    public abstract String getElementMatchCriteria();

    public abstract boolean getSearchFromStart();

    public abstract int matchLine(String s, String s1);

    public boolean update() {
        int i = 0;
        String as[] = new String[0];
        long l = System.currentTimeMillis();
        String s = getProperty(pMachineName);
        long l1 = System.currentTimeMillis();
        as = getLogFiles(s, getLogPath(), getFileNameMatchCriteria());
        long l2 = System.currentTimeMillis();
        System.out.println("getting log files took: " + (l2 - l1) + " msecs");
        if (as != null) {
            currentLogFileList = (HashMap) SerializerUtils
                    .decodeJavaObjectFromString(getProperty(pCurrentLogFileList));
            if (currentLogFileList == null) {
                currentLogFileList = new HashMap();
            }
            for (int j = 0; j < as.length; j++) {
                i += countLineMatches(s, getFilePath(as[j], s));
            }

        } else {
            LogManager
                    .log("Error",
                            "MultiLogBase::update() Error retrieving files to be monitored.");
            setProperty(pStateString, "Unable to retrieve log files.");
            setProperty(pMatchCount, 0);
            setProperty(pCategory, ERROR_CATEGORY);
            setProperty(pNoData, "n/a");
            return false;
        }
        HashMap hashmap = new HashMap();
        for (int k = 0; k < as.length; k++) {
            String s1 = as[k];
            Object obj = currentLogFileList.get(s1);
            if (obj != null) {
                hashmap.put(s1, obj);
            } else {
                LogManager.log("Error",
                        "Multi Log Monitor did not store last read posistion for file "
                                + s1);
            }
        }

        currentLogFileList = hashmap;
        long l3 = System.currentTimeMillis();
        setProperty(pStateString, getStatusString(i, as, l3 - l));
        setProperty(pMatchCount, i);
        setProperty(pCategory, GOOD_CATEGORY);
        System.out.println("matches: " + i);
        setProperty(pCurrentLogFileList, SerializerUtils
                .encodeObject(currentLogFileList));
        return true;
    }

    protected String getStatusString(int i, String as[], long l) {
        return i + " matches found in " + as.length
                + " files, operation took: " + l + " msecs";
    }

    private String getFileNameFromPath(String s, String s1) {
        char c = File.separatorChar;
        if (s1.length() > 0) {
            if (s1.startsWith("\\\\")) {
                c = '\\';
            } else {
                c = '/';
            }
        }
        int i = s.lastIndexOf(c);
        return s.substring(i + 1);
    }

    private String getFilePath(String s, String s1) {
        String s2 = getLogPath();
        if (s1.length() == 0) {
            s2 = s2 + File.separator;
        } else if (s1.startsWith("\\\\")) {
            s2 = s2 + "\\";
        } else {
            s2 = s2 + "/";
        }
        return s2 + s;
    }

    protected int countLineMatches(String s, String s1) {
        int i = 0;
        long l = 0L;
        Long long1 = (Long) currentLogFileList.get(getFileNameFromPath(s1, s));
        if (!getSearchFromStart() && long1 != null) {
            l = long1.longValue();
        }
        if (s.length() == 0 || s.startsWith("\\\\")) {
            try {
                Braf braf = new Braf(s1, l);
                for (String s2 = null; (s2 = braf.readLine()) != null;) {
                    l += s2.length() + 1;
                    i += matchLine(s1, s2);
                }

                braf.close();
                braf = null;
            } catch (IOException ioexception) {
                LogManager.log("Error",
                        "MultiLogBase:: Failure reading file from NT machine: "
                                + ioexception.getMessage());
            }
        } else if (l <= getUnixRemoteLogFileSize(s1)) {
            Array array = readUnixRemoteFile(s1, l);
            for (Enumeration enumeration = array.elements(); enumeration
                    .hasMoreElements();) {
                String s3 = (String) enumeration.nextElement();
                l += s3.length() + 1;
                i += matchLine(s1, s3);
            }

        }
        currentLogFileList.put(getFileNameFromPath(s1, s), new Long(l));
        return i;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pMatchCount);
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        array.add(pMatchCount);
        return array.elements();
    }

    protected boolean fileExists(String s) {
        String s1 = getProperty(pMachineName);
        if (s1.length() == 0 || s1.startsWith("\\\\")) {
            return (new File(s)).exists();
        } else {
            return getUnixFileExists(s);
        }
    }

    protected String[] getLogFiles(String s, String s1, String s2) {
        if (s.length() == 0 || s.startsWith("\\\\")) {
            return getNTLogFiles(s1, s2);
        } else {
            return getUnixLogFiles(s, s1, s2);
        }
    }

    protected int getUnixMatchCount() {
        String s = getProperty(pMachineName);
        String s1 = "cat `find " + getLogPath() + "| grep "
                + stripLeadingAndTrailingSlash(getFileNameMatchCriteria())
                + "` | grep -c "
                + stripLeadingAndTrailingSlash(getElementMatchCriteria());
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        if (array != null && commandline.exitValue != 1) {
            return (new Long(((String) array.at(0)).trim())).intValue();
        } else {
            LogManager
                    .log("Error",
                            "MultiLogBase:: Failure getting match count from Unix remote.");
            return 0;
        }
    }

    protected boolean getUnixFileExists(String s) {
        String s1 = getProperty(pMachineName);
        CommandLine commandline = new CommandLine();
        String s2 = "/usr/bin/test -r " + s + "; echo $?";
        Array array = commandline.exec(s2, s1, Platform.getLock(s1));
        return array != null && array.size() > 0
                && ((String) array.at(0)).equals("0");
    }

    protected String[] getNTLogFiles(String s, String s1) {
        LogFileFilter logfilefilter = new LogFileFilter(s1);
        File file = new File(s);
        String as[] = new String[1];
        if (file.exists()) {
            if ((as = file.list(logfilefilter)) == null) {
                return null;
            }
            for (int i = 0; i < as.length; i++) {
                as[i] = as[i];
            }

        } else {
            LogManager.log("Error", "MultiLogBase::Unable to scan directory: "
                    + getLogPath() + " , directory does not exist");
        }
        return as;
    }

    public List getFileListing(File file) throws FileNotFoundException {
        String s = getFileNameMatchCriteria();
        ArrayList arraylist = new ArrayList();
        File afile[] = file.listFiles();
        List list = Arrays.asList(afile);
        Iterator iterator = list.iterator();
        Object obj = null;
        do {
            if (!iterator.hasNext()) {
                break;
            }
            File file1 = (File) iterator.next();
            if (!file1.isFile()) {
                arraylist.addAll(getFileListing(file1));
            } else if (perl.match(s, file1.getName())) {
                arraylist.add(file1);
            }
        } while (true);
        Collections.sort(arraylist);
        return arraylist;
    }

    protected String[] getUnixLogFiles(String s, String s1, String s2) {
        CommandLine commandline = new CommandLine();
        String s3 = stripLeadingAndTrailingSlash(s2);
        String s4 = "find " + s1 + "| grep '" + s3 + "'";
        Array array = new Array();
        String as[] = new String[1];
        array = commandline.exec(s4, s, Platform.getLock(s));
        if (commandline.exitValue != 1) {
            as = new String[array.size()];
            for (int i = 0; i < as.length; i++) {
                as[i] = getFileNameFromPath((String) array.at(i), s);
            }

        } else {
            LogManager.log("Error", "MultiLogBase::Unable to scan directory: "
                    + getLogPath() + " , directory does not exist??");
        }
        return as;
    }

    protected String stripLeadingAndTrailingSlash(String s) {
        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        if (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    protected long getUnixRemoteLogFileSize(String s) {
        String s1 = getProperty(pMachineName);
        String s2 = "cat " + s + " | wc -c";
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s2, s1, Platform.getLock(s1));
        if (array != null && commandline.exitValue != 1) {
            return (new Long(((String) array.at(0)).trim())).longValue();
        } else {
            LogManager.log("Error",
                    "MultiLogBase:: Failure getting file size from Unix remote: "
                            + s);
            return 0L;
        }
    }

    protected Array readLinesWithRaf(String s, long l) {
        Array array = new Array();
        try {
            RandomAccessFile randomaccessfile = new RandomAccessFile(s, "r");
            for (String s1 = null; (s1 = randomaccessfile.readLine()) != null;) {
                array.add(s1);
            }

            randomaccessfile.close();
        } catch (IOException ioexception) {
            LogManager.log("Error",
                    "MultiLogBase:: Failure reading file from NT machine: "
                            + ioexception.getMessage());
        }
        return array;
    }

    protected Array readUnixRemoteFile(String s, long l) {
        String s1 = getProperty(pMachineName);
        jgl.HashMap hashmap = new jgl.HashMap();
        hashmap.put("file", s);
        hashmap.put("bytes", "" + l);
        String s2 = Machine.getCommandString("tail", s1, hashmap);
        s2 = s2 + "| grep "
                + stripLeadingAndTrailingSlash(getElementMatchCriteria());
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s2, s1, Platform.getLock(s1));
        if (array != null && commandline.exitValue != 1) {
            return array;
        } else {
            LogManager.log("Error",
                    "MultiLogBase:: Failure reading file from unix remote: "
                            + s);
            return new Array();
        }
    }

    static {
        pMatchCount = new NumericProperty("matchCount");
        pMatchCount.setIsThreshold(true);
        pCurrentLogFileList = new StringProperty("currentLogFileList");
        StringProperty astringproperty[] = { pMatchCount, pCurrentLogFileList };
        String s = (COM.dragonflow.SiteView.MultiLogBase.class).getName();
        addProperties(s, astringproperty);
    }
}
