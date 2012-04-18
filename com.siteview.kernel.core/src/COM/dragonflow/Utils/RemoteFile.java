/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;

import com.recursionsw.jgl.Array;

// Referenced classes of package COM.dragonflow.Utils:
// SSHCommandLine, RemoteCommandLine

public class RemoteFile {

    String machineID;

    String pwd;

    String path;

    String fullPath;

    boolean directory;

    int os;

    public RemoteFile(String s, String s1) {
        machineID = "";
        pwd = "";
        path = "";
        fullPath = "";
        directory = false;
        path = s1;
        machineID = s;
        if (COM.dragonflow.SiteView.Machine.isNTSSH(s)) {
            return;
        }
        COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getMachine(s);
        if (machine != null) {
            COM.dragonflow.Utils.RemoteCommandLine remotecommandline = COM.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            os = COM.dragonflow.SiteView.Machine.getOS(s);
            if (COM.dragonflow.SiteView.Platform.isWindows(os)) {
                if (path.indexOf(":\\") >= 0) {
                    fullPath = path;
                }
            } else if (COM.dragonflow.SiteView.Platform.isUnix(os) && path.startsWith("/")) {
                fullPath = path;
            }
            COM.dragonflow.SiteView.Machine _tmp = machine;
            if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX setting fullpath. path: " + path + " fullPath: " + fullPath);
            }
            if (fullPath.length() == 0) {
                String s2 = COM.dragonflow.SiteView.Platform.currentDirectoryCommand(s);
                Array array = remotecommandline.exec(s2, machine);
                if (remotecommandline.exitValue == 0 && array.size() > 0) {
                    pwd = (String) array.get(0);
                    pwd = pwd.trim();
                    findStartOfUnixPwdResultDirectoryPath(machine);
                    COM.dragonflow.SiteView.Machine _tmp1 = machine;
                    if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
                        COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " from remote line 1: " + (String) array.get(0));
                    }
                    if (pwd.length() == 0) {
                        pwd = (String) array.get(1);
                        pwd = pwd.trim();
                        findStartOfUnixPwdResultDirectoryPath(machine);
                        COM.dragonflow.SiteView.Machine _tmp2 = machine;
                        if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
                            COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " from remote line 2: " + (String) array.get(1));
                        }
                    }
                }
                if (!pwd.endsWith(COM.dragonflow.SiteView.Platform.pathSeparator(os))) {
                    pwd += COM.dragonflow.SiteView.Platform.pathSeparator(os);
                }
                fullPath = pwd + path;
                COM.dragonflow.SiteView.Machine _tmp3 = machine;
                if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX pwd plus path.  pwd: " + pwd + " path: " + path + " fullPath: " + fullPath);
                }
            } else {
                COM.dragonflow.SiteView.Machine _tmp4 = machine;
                if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX FINAL fullpath. path: " + path + " fullPath: " + fullPath);
                }
            }
        } else {
            COM.dragonflow.Log.LogManager.log("Error", "Could find machine for " + s + " - looking for remote file " + path);
        }
    }

    private void findStartOfUnixPwdResultDirectoryPath(COM.dragonflow.SiteView.Machine machine) {
        int i = pwd.indexOf(" /");
        COM.dragonflow.SiteView.Machine _tmp = machine;
        if (machine.getPropertyAsBoolean(COM.dragonflow.SiteView.Machine.pTrace)) {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " indexPathStart: " + i);
        }
        if (COM.dragonflow.SiteView.Platform.isUnix(os) && i > 0 && i < pwd.length()) {
            pwd = pwd.substring(i + 1);
        }
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getPwd() {
        return pwd;
    }

    public String pathSeparator() {
        return COM.dragonflow.SiteView.Platform.pathSeparator(os);
    }

    public Array listFiles() {
        String s = COM.dragonflow.SiteView.Platform.dirCommand(machineID, COM.dragonflow.SiteView.Platform.DIR_FILES);
        s = s + " " + fullPath;
        String s1 = machineID;
        if (s1.startsWith("\\\\")) {
            s1 = s1.substring(2);
        }
        Object obj = null;
        Array array = new Array();
        if (COM.dragonflow.SiteView.Machine.isNTSSH(s1)) {
            s = "directory.bat";
            COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getNTMachine(s1);
            COM.dragonflow.Utils.SSHCommandLine sshcommandline = new SSHCommandLine();
            Array array1 = sshcommandline.exec(s, machine, false);
            Enumeration enumeration =  (Enumeration) array1.iterator();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                s2 = s2.trim();
                if (!s2.endsWith("/")) {
                    array.add(s2);
                }
            }
        } else {
            COM.dragonflow.SiteView.Machine machine1 = COM.dragonflow.SiteView.Machine.getMachine(machineID);
            if (machine1 != null) {
                COM.dragonflow.Utils.RemoteCommandLine remotecommandline = COM.dragonflow.SiteView.Machine.getRemoteCommandLine(machine1);
                if (COM.dragonflow.SiteView.Platform.isUnix(COM.dragonflow.SiteView.Machine.getOS(machineID))) {
                    s = s + " | cat";
                }
                Array array2 = remotecommandline.exec(s, machine1);
                Enumeration enumeration1 =  (Enumeration) array2.iterator();
                while (enumeration1.hasMoreElements()) {
                    String s3 = (String) enumeration1.nextElement();
                    s3 = s3.trim();
                    if (s3.endsWith("*")) {
                        s3 = s3.substring(0, s3.length() - 1);
                    }
                    if (!s3.endsWith("/")) {
                        array.add(s3);
                    }
                } 
            }
        }
        return array;
    }
}
