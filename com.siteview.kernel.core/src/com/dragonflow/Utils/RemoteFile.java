/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

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

// Referenced classes of package com.dragonflow.Utils:
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
        if (com.dragonflow.SiteView.Machine.isNTSSH(s)) {
            return;
        }
        com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getMachine(s);
        if (machine != null) {
            com.dragonflow.Utils.RemoteCommandLine remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            os = com.dragonflow.SiteView.Machine.getOS(s);
            if (com.dragonflow.SiteView.Platform.isWindows(os)) {
                if (path.indexOf(":\\") >= 0) {
                    fullPath = path;
                }
            } else if (com.dragonflow.SiteView.Platform.isUnix(os) && path.startsWith("/")) {
                fullPath = path;
            }
            com.dragonflow.SiteView.Machine _tmp = machine;
            if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
                com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX setting fullpath. path: " + path + " fullPath: " + fullPath);
            }
            if (fullPath.length() == 0) {
                String s2 = com.dragonflow.SiteView.Platform.currentDirectoryCommand(s);
                Array array = remotecommandline.exec(s2, machine);
                if (remotecommandline.exitValue == 0 && array.size() > 0) {
                    pwd = (String) array.get(0);
                    pwd = pwd.trim();
                    findStartOfUnixPwdResultDirectoryPath(machine);
                    com.dragonflow.SiteView.Machine _tmp1 = machine;
                    if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
                        com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " from remote line 1: " + (String) array.get(0));
                    }
                    if (pwd.length() == 0) {
                        pwd = (String) array.get(1);
                        pwd = pwd.trim();
                        findStartOfUnixPwdResultDirectoryPath(machine);
                        com.dragonflow.SiteView.Machine _tmp2 = machine;
                        if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
                            com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " from remote line 2: " + (String) array.get(1));
                        }
                    }
                }
                if (!pwd.endsWith(com.dragonflow.SiteView.Platform.pathSeparator(os))) {
                    pwd += com.dragonflow.SiteView.Platform.pathSeparator(os);
                }
                fullPath = pwd + path;
                com.dragonflow.SiteView.Machine _tmp3 = machine;
                if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
                    com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX pwd plus path.  pwd: " + pwd + " path: " + path + " fullPath: " + fullPath);
                }
            } else {
                com.dragonflow.SiteView.Machine _tmp4 = machine;
                if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
                    com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX FINAL fullpath. path: " + path + " fullPath: " + fullPath);
                }
            }
        } else {
            com.dragonflow.Log.LogManager.log("Error", "Could find machine for " + s + " - looking for remote file " + path);
        }
    }

    private void findStartOfUnixPwdResultDirectoryPath(com.dragonflow.SiteView.Machine machine) {
        int i = pwd.indexOf(" /");
        com.dragonflow.SiteView.Machine _tmp = machine;
        if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
            com.dragonflow.Log.LogManager.log("RunMonitor", "RemoteFile() after UNIX call to pwd on remote. pwd: " + pwd + " indexPathStart: " + i);
        }
        if (com.dragonflow.SiteView.Platform.isUnix(os) && i > 0 && i < pwd.length()) {
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
        return com.dragonflow.SiteView.Platform.pathSeparator(os);
    }

    public Array listFiles() {
        String s = com.dragonflow.SiteView.Platform.dirCommand(machineID, com.dragonflow.SiteView.Platform.DIR_FILES);
        s = s + " " + fullPath;
        String s1 = machineID;
        if (s1.startsWith("\\\\")) {
            s1 = s1.substring(2);
        }
        Object obj = null;
        Array array = new Array();
        if (com.dragonflow.SiteView.Machine.isNTSSH(s1)) {
            s = "directory.bat";
            com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getNTMachine(s1);
            com.dragonflow.Utils.SSHCommandLine sshcommandline = new SSHCommandLine();
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
            com.dragonflow.SiteView.Machine machine1 = com.dragonflow.SiteView.Machine.getMachine(machineID);
            if (machine1 != null) {
                com.dragonflow.Utils.RemoteCommandLine remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine1);
                if (com.dragonflow.SiteView.Platform.isUnix(com.dragonflow.SiteView.Machine.getOS(machineID))) {
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
