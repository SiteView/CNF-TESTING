/*
 * 
 * Created on 2005-2-20 4:26:21
 *
 * OSAdapter.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>OSAdapter</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SSH.SSHManager;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.LineReader;
import COM.dragonflow.Utils.TelnetCommandLine;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, MasterConfig, Machine, SiteViewGroup,
// Platform, Monitor

public class OSAdapter {

    static HashMap adapters = new HashMap();

    static String ADAPTER_DIRECTORY;

    HashMap commands;

    HashMap osProperties;

    public static Array getOSIDs() {
        return TextUtils.enumToArray(adapters.keys());
    }

    public static Array getOSs(Array array) {
        Array array1 = getOSIDs();
        array1 = TextUtils.sortStrings(array1, true);
        for (int i = 0; i < array1.size(); i ++) {
            OSAdapter osadapter = getOSAdapter((String) array1.at(i));
            array.add(osadapter.getOSID());
            array.add(osadapter.getOSName());
        }

        return array;
    }

    public static OSAdapter getOSAdapter(String s) {
        return (OSAdapter) adapters.get(s);
    }

    OSAdapter(String s) {
        commands = new HashMap();
        osProperties = new HashMap();
        try {
            Array array = FrameFile.readFromFile(s);
            for (int i = 0; i < array.size(); i ++) {
                HashMap hashmap = (HashMap) array.at(i);
                if (i == 0) {
                    osProperties = hashmap;
                    continue;
                }
                String s1 = TextUtils.getValue(hashmap, "id");
                if (s1.length() > 0) {
                    commands.put(s1, hashmap);
                }
            }

            adapters.add(getOSID(), this);
        } catch (Exception exception) {
            LogManager.log("Error", "Could not read " + getOSID() + " platform file from " + s + ": " + exception);
        }
    }

    public String getOSName() {
        return TextUtils.getValue(osProperties, "name");
    }

    public String getOSID() {
        return TextUtils.getValue(osProperties, "id");
    }

    HashMap getCommand(String s) {
        return (HashMap) commands.get(s);
    }

    public String getCommandString(String s, HashMap hashmap) {
        String s1 = "";
        HashMap hashmap1 = getCommand(s);
        if (hashmap1 != null) {
            s1 = TextUtils.getValue(hashmap1, "command");
            if (hashmap != null) {
                s1 = replaceVariables(s1, hashmap);
            }
        }
        return s1;
    }

    public String getCommandSetting(String s, String s1) {
        HashMap hashmap = getCommand(s);
        String s2 = "";
        if (hashmap != null) {
            s2 = TextUtils.getValue(hashmap, s1);
        }
        return s2;
    }

    public String getCommandSetting(String s, String s1, String s2) {
        String s3 = getCommandSetting(s, s1);
        if (s3.length() == 0) {
            return s2;
        } else {
            return s3;
        }
    }

    public int getCommandSettingAsInteger(String s, String s1) {
        return TextUtils.toInt(getCommandSetting(s, s1));
    }

    public int getCommandSettingAsInteger(String s, String s1, int i) {
        String s2 = getCommandSetting(s, s1);
        if (s2.length() == 0) {
            return i;
        } else {
            return TextUtils.toInt(s2);
        }
    }

    public long getCommandSettingAsLong(String s, String s1) {
        return TextUtils.toLong(getCommandSetting(s, s1));
    }

    public long getCommandSettingAsLong(String s, String s1, long l) {
        String s2 = getCommandSetting(s, s1);
        if (s2.length() == 0) {
            return l;
        } else {
            return TextUtils.toLong(s2);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @return
     */
    public Array getMatchedCommandSettings(String s, String s1) {
        Array array = new Array();
        HashMap hashmap = getCommand(s);
        if (hashmap != null) {
            Enumeration enumeration = hashmap.keys();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                if (TextUtils.match(s2, s1)) {
                    array.add(s2);
                }
            }
        }
        return array;
    }

    String replaceVariables(String s, HashMap hashmap) {
        int i = 0;
        StringBuffer stringbuffer = new StringBuffer();
        while (true) {
            int j = s.indexOf('<', i);
            if (j == -1) {
                break;
            }
            int k = s.indexOf('>', j + 1);
            if (k == -1) {
                break;
            }
            stringbuffer.append(s.substring(i, j));
            i = k + 1;
            String s1 = s.substring(j + 1, k);
            stringbuffer.append(TextUtils.getValue(hashmap, s1));
        }
        stringbuffer.append(s.substring(i));
        return stringbuffer.toString();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        HashMap hashmap = MasterConfig.getMasterConfig();
        Machine.registerMachines(hashmap.values("_remoteMachine"));
        if (args.length < 1) {
            System.out.println("Usage: OSAdapter machine [detail] [CPUMonitor] [DiskSpaceMonitor] [MemoryMonitor] [ProcessMonitor]");
            System.exit(0);
        }
        String s = args[0];
        PrintWriter printwriter = FileUtils.MakeOutputWriter(System.out);
        Machine machine;
        if (s.startsWith(Machine.REMOTE_PREFIX)) {
            machine = Machine.getMachine(s);
        } else {
            machine = Machine.getMachineByName(s);
        }
        if (machine == null) {
            printwriter.println(s + " not found in defined Unix Remote Servers");
            System.exit(0);
        }
        HashMap hashmap1 = null;
        boolean flag = false;
        boolean flag2 = false;
        for (int i = 1; i < args.length; i ++) {
            if (args[i].equals("detail")) {
                LineReader.setTraceStream(printwriter);
                flag2 = true;
                continue;
            }
            if (args[i].endsWith("Monitor")) {
                if (hashmap1 == null) {
                    hashmap1 = new HashMap();
                }
                hashmap1.put(args[i], "yes");
                LineReader.setTraceStream(printwriter);
                flag2 = true;
                continue;
            }
            if (args[i].equals("test")) {
                flag = true;
            }
        }

        // machine; // TODO need review
        int j = Machine.stringToOS(machine.getProperty(Machine.pOS));
        if (flag && j == 8) {
            printwriter.println("Cannot test adapted platforms - only the original HP, Solaris and SGI platforms");
            boolean flag1 = false;
        }
        try {
            runTests(machine, hashmap1, printwriter, flag2);
        } catch (Exception e) {
            printwriter.println("Exception: " + e);
        } finally {
            printwriter.flush();
            LineReader.resetTraceStream();
            SSHManager.getInstance().closeAll();
            TelnetCommandLine.closeAll();
            System.exit(0);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param machine
     * @param hashmap
     * @param writer
     * @param flag
     * @throws Exception
     */
    public static void runTests(Machine machine, HashMap hashmap, Writer writer, boolean flag) throws Exception {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String string = Machine.REMOTE_PREFIX + machine.getProperty(Machine.pID);
        PrintWriter printwriter = new PrintWriter(writer);
        if (flag) {
            LineReader.setTraceStream(printwriter);
        }
        
        try {
            if (hashmap == null || TextUtils.getValue(hashmap, "CPUMonitor").length() > 0) {
                testCPU(printwriter, string, siteviewgroup, flag);
            }
            if (hashmap == null || (TextUtils.getValue(hashmap, "DiskSpaceMonitor").length() > 0)) {
                testDiskFull(printwriter, string, siteviewgroup, flag);
            }
            if (hashmap == null || TextUtils.getValue(hashmap, "MemoryMonitor").length() > 0) {
                testMemory(printwriter, string, siteviewgroup, flag);
            }
            if (hashmap == null || TextUtils.getValue(hashmap, "ServiceMonitor").length() > 0) {
                testService(printwriter, string, siteviewgroup, flag);
            }
        } catch (Exception e) {
            if (flag) {
                LineReader.resetTraceStream();
            }
            throw e;
        }
        if (flag) {
            LineReader.resetTraceStream();
        }
        printwriter.println("\n");
        printwriter.println("---------------------------------------------------");
        printwriter.println("All Tests Complete");
    }

    static void testCPU(PrintWriter printwriter, String s, SiteViewGroup siteviewgroup, boolean flag) throws Exception {
        printwriter.println("---------------------------------------------------");
        printwriter.println("Testing CPU Monitor");
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("_class", "CPUMonitor");
        hashmapordered.put("_machine", s);
        AtomicMonitor atomicmonitor = (AtomicMonitor) SiteViewGroup.createTestObject(hashmapordered);
        atomicmonitor.setOwner(siteviewgroup);
        runMonitor(printwriter, atomicmonitor, flag);
        printwriter.println("CPU Monitor Test complete.");
    }

    static void testDiskFull(PrintWriter printwriter, String s, SiteViewGroup siteviewgroup, boolean flag) throws Exception {
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("_class", "DiskSpaceMonitor");
        hashmapordered.put("_machine", s);
        printwriter.println("---------------------------------------------------");
        printwriter.println("Testing Disk Space Monitor");
        printwriter.println("\nReading list of disks to find a disk to monitor\n");
        Vector vector = Platform.getDisks(s);
        if (vector.size() > 0) {
            printwriter.println("\nWill check disk " + vector.elementAt(0));
            hashmapordered.put("_disk", "" + vector.elementAt(0));
            AtomicMonitor atomicmonitor = (AtomicMonitor) SiteViewGroup.createTestObject(hashmapordered);
            atomicmonitor.setOwner(siteviewgroup);
            runMonitor(printwriter, atomicmonitor, flag);
            printwriter.println("Disk Space Monitor Test complete.");
        }
    }

    static void testMemory(PrintWriter printwriter, String s, SiteViewGroup siteviewgroup, boolean flag) throws Exception {
        printwriter.println("---------------------------------------------------");
        printwriter.println("Testing Memory Monitor");
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("_class", "MemoryMonitor");
        hashmapordered.put("_machine", s);
        AtomicMonitor atomicmonitor = (AtomicMonitor) SiteViewGroup.createTestObject(hashmapordered);
        atomicmonitor.setOwner(siteviewgroup);
        runMonitor(printwriter, atomicmonitor, flag);
        printwriter.println("\nChecking memory again (to get page faults)");
        runMonitor(printwriter, atomicmonitor, flag);
        printwriter.println("Memory Monitor Test complete.");
    }

    static void testService(PrintWriter printwriter, String s, SiteViewGroup siteviewgroup, boolean flag) throws Exception {
        printwriter.println("---------------------------------------------------");
        printwriter.println("Testing Service Monitor");
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("_class", "ServiceMonitor");
        hashmapordered.put("_machine", s);
        printwriter.println("\nReading list of processes to find a process to monitor\n");
        Array array = Platform.getProcesses(s);
        if (array.size() > 0) {
            String s1 = "";
            int i = 0;
            do {
                if (i >= array.size()) {
                    break;
                }
                String s2 = (String) array.at(i);
                if (s2.indexOf("syslog") >= 0) {
                    s1 = s2;
                    break;
                }
                i ++;
            } while (true);
            if (s1.length() == 0) {
                s1 = (String) array.at(0);
            }
            printwriter.println("Will check Process " + s1);
            hashmapordered.put("_service", s1);
            AtomicMonitor atomicmonitor = (AtomicMonitor) SiteViewGroup.createTestObject(hashmapordered);
            atomicmonitor.setOwner(siteviewgroup);
            runMonitor(printwriter, atomicmonitor, flag);
            int j = s1.indexOf(' ');
            if (j != -1) {
                s1 = s1.substring(0, j);
            }
            int k = s1.lastIndexOf('/');
            if (k != -1) {
                s1 = s1.substring(k + 1);
            }
            printwriter.println("\nChecking process " + s1 + " again (to read memory size of process)");
            hashmapordered = new HashMapOrdered(true);
            hashmapordered.put("_class", "ServiceMonitor");
            hashmapordered.put("_machine", s);
            hashmapordered.put("_checkMemory", "true");
            hashmapordered.put("_service", s1);
            atomicmonitor = (AtomicMonitor) SiteViewGroup.createTestObject(hashmapordered);
            atomicmonitor.setOwner(siteviewgroup);
            runMonitor(printwriter, atomicmonitor, flag);
            printwriter.println("Service Monitor Test complete.");
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param printwriter
     * @param atomicmonitor
     * @param flag
     */
    static void runMonitor(PrintWriter printwriter, AtomicMonitor atomicmonitor, boolean flag) {
        try {
            atomicmonitor.testUpdate();
            if (flag) {
                printwriter.println("\n---------- result ----------");
                Array array = atomicmonitor.getLogProperties();
                int i = 0;
                for (/**/; i < array.size(); i ++) {
                    StringProperty stringproperty = (StringProperty) array.at(i);
                    if (i < 5) {
                        StringProperty stringproperty_30_ = stringproperty;
                        if (atomicmonitor != null) {
                            /* empty */
                        }
                        if (stringproperty_30_ != AtomicMonitor.pCategory) {
                            StringProperty stringproperty_31_ = stringproperty;
                            if (atomicmonitor != null) {
                                /* empty */
                            }
                            if (stringproperty_31_ != AtomicMonitor.pStateString)
                                continue;
                        }
                    }
                    String string = stringproperty.getLabel();
                    StringProperty stringproperty_32_ = stringproperty;
                    if (atomicmonitor != null) {
                        /* empty */ //TODO need review
                    }
                    if (stringproperty_32_ == AtomicMonitor.pStateString)
                        string = "status";
                    printwriter.println(string + ": " + (stringproperty.printString(atomicmonitor.getProperty(stringproperty))));
                }
            } else {
                AtomicMonitor atomicmonitor_33_ = atomicmonitor;
                if (atomicmonitor != null) {
                    /* empty */
                }
                String string = atomicmonitor_33_.getProperty(AtomicMonitor.pCategory);
                if (string.equals(Monitor.GOOD_CATEGORY))
                    string = "OK";
                if (string.equals(Monitor.NODATA_CATEGORY))
                    string = "no data";
                PrintWriter printwriter_34_ = printwriter;
                StringBuffer stringbuffer = new StringBuffer().append(string).append(", ");
                AtomicMonitor atomicmonitor_35_ = atomicmonitor;
                if (atomicmonitor != null) {
                    /* empty */
                }
                printwriter_34_.println(stringbuffer.append(atomicmonitor_35_.getProperty(AtomicMonitor.pStateString)).toString());
            }
        } catch (Exception exception) {
            printwriter.println("Exception: " + exception);
        }
    }

    static {
        ADAPTER_DIRECTORY = Platform.getRoot() + File.separator + "templates.os" + File.separator;
        try {
            File file = new File(ADAPTER_DIRECTORY);
            if (file.exists() && file.isDirectory()) {
                String as[] = file.list();
                if (as != null) {
                    for (int i = 0; i < as.length; i ++) {
                        String s = ADAPTER_DIRECTORY + as[i];
                        new OSAdapter(s);
                    }

                }
            }
        } catch (Exception exception) {
            TextUtils.debugPrint("Exception reading adapters: " + exception);
        }
    }
}