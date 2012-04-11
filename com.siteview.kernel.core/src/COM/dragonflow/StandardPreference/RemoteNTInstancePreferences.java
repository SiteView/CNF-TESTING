/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.CounterLock;

public class RemoteNTInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    private String settingName;

    public static COM.dragonflow.Properties.StringProperty pID;

    public static COM.dragonflow.Properties.StringProperty pHost;

    public static COM.dragonflow.Properties.StringProperty pLogin;

    public static COM.dragonflow.Properties.StringProperty pPassword;

    public static COM.dragonflow.Properties.StringProperty pName;

    public static COM.dragonflow.Properties.StringProperty pTrace;

    public static COM.dragonflow.Properties.StringProperty pMethod;

    public static COM.dragonflow.Properties.StringProperty pOs;

    public static COM.dragonflow.Properties.StringProperty pSshClient;

    public static COM.dragonflow.Properties.StringProperty pDisableCache;

    public static COM.dragonflow.Properties.StringProperty pSshConnectionsLimit;

    public static COM.dragonflow.Properties.StringProperty pSshAuthMethod;

    public static COM.dragonflow.Properties.StringProperty pKeyFile;

    public static COM.dragonflow.Properties.StringProperty pVersion2;

    public static COM.dragonflow.Properties.StringProperty pSshCommand;

    public static COM.dragonflow.Properties.StringProperty pSSHPort;

    public static COM.dragonflow.Properties.StringProperty pStatus;

    public RemoteNTInstancePreferences() {
        settingName = "_remoteNTMachine";
    }

    public java.util.Vector test(String s) throws java.lang.Exception {
        java.util.Vector vector = new Vector();
        int i = 0;
        boolean flag = false;
        boolean flag1 = (new Boolean(getProperty(pTrace))).booleanValue();
        String s1 = getProperty(pHost);
        jgl.Array array = new Array();
        i = COM.dragonflow.SiteView.Platform.CheckPermissions(s1, COM.dragonflow.SiteView.MasterConfig.getMasterConfig(), array);
        String s2 = "Connection successful";
        if (s1 != null) {
            String s3;
            for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); vector.add(s3)) {
                s3 = (String) enumeration.nextElement();
            }

            if (i == 0 && flag1) {
                jgl.Array array1 = new Array();
                i = COM.dragonflow.SiteView.Platform.readProcessList(array1, s1, new CounterLock(1), false);
                String s4;
                for (Enumeration enumeration1 = array1.elements(); enumeration1.hasMoreElements(); vector.add(s4)) {
                    s4 = (String) enumeration1.nextElement();
                }

            }
            if (i != 0) {
                flag = true;
                if (i == 5) {
                    s2 = "The user login or password is invalid.";
                } else if (i == 53) {
                    s2 = "remote server not found";
                } else if (i == 1723) {
                    s2 = "remote server not accepting performance monitoring connections";
                } else if (i == 5) {
                    s2 = "access permissions error";
                } else {
                    s2 = "unknown error (" + i + ")";
                }
                s2 = "SiteView was unable to connect to the remote server. Reason: " + s2;
            }
        } else {
            s2 = "Could not get machine information for " + s1;
            flag = true;
        }
        jgl.Array array2 = getFrames();
        jgl.HashMap hashmap = findMachine(array2, getProperty(pID));
        hashmap.put("_status", s2);
        try {
            saveMachines(array2, "_remoteNTMachine");
        } catch (java.lang.Exception exception) {
            s2 = "There was a problem updating the server status." + exception.toString();
            flag = true;
        }
        if (flag) {
            throw new Exception(s2);
        } else {
            return vector;
        }
    }

    public boolean hasMultipleValues() {
        return true;
    }

    public String getSettingName() {
        return settingName;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty.getName().equals("_name") && (s == null || s.length() == 0)) {
            s = (String) hashmap.get("_host");
            if (!s.startsWith("\\\\")) {
                s = "\\\\" + s;
            }
        } else if (stringproperty.getName().equals("_host") && s != null && s.length() > 0) {
            boolean flag = hashmap.get("_method").equals("ssh");
            boolean flag1 = s.startsWith("\\\\");
            if (flag) {
                if (flag1) {
                    s = s.substring(2);
                }
            } else if (!flag1) {
                s = "\\\\" + s;
            }
        } else if (stringproperty.getName().equals("_login") && s != null && s.length() > 0) {
            String s1 = (String) hashmap.get("_method");
            if (s.indexOf("\\") == -1 && s1 != null && !s1.equals("ssh")) {
                String s2 = (String) hashmap.get("_host");
                if (s2.startsWith("\\\\")) {
                    s2 = s2.substring(2);
                }
                s = s2 + "\\" + s;
            }
        }
        return s;
    }

    public String[] addPreferences(java.util.HashMap hashmap) throws COM.dragonflow.SiteViewException.SiteViewException {
        hashmap.put("_os", "NT");
        return super.addPreferences(hashmap);
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest, COM.dragonflow.Page.CGI cgi) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        if (scalarproperty == pOs) {
            jgl.Array array = COM.dragonflow.SiteView.Machine.getAllowedOSs();
            for (int i = 0; i < array.size(); i ++) {
                if (((String) array.at(i)).length() > 0) {
                    vector.addElement(array.at(i));
                }
            }

        } else if (scalarproperty == pMethod) {
            jgl.Array array1 = COM.dragonflow.SiteView.Machine.getNTAllowedMethods();
            for (int j = 0; j < array1.size(); j ++) {
                if (((String) array1.at(j)).length() > 0) {
                    vector.addElement(array1.at(j));
                }
            }

        } else if (scalarproperty == pSshAuthMethod) {
            jgl.Array array2 = COM.dragonflow.SiteView.Machine.getAllowedSshAuthMethods();
            for (int k = 0; k < array2.size(); k ++) {
                if (((String) array2.at(k)).length() > 0) {
                    vector.addElement(array2.at(k));
                }
            }

        } else if (scalarproperty == pSshClient) {
            jgl.Array array3 = COM.dragonflow.SiteView.Machine.getAllowedSshConnectionMethods();
            for (int l = 0; l < array3.size(); l ++) {
                if (((String) array3.at(l)).length() > 0) {
                    vector.addElement(array3.at(l));
                }
            }

        }
        return vector;
    }

    private void saveMachines(jgl.Array array, String s) {
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        hashmap.remove(s);
        for (int i = 0; i < array.size(); i ++) {
            jgl.HashMap hashmap1 = (jgl.HashMap) array.at(i);
            hashmap.add(s, COM.dragonflow.Utils.TextUtils.hashMapToString(hashmap1));
        }

        COM.dragonflow.SiteView.MasterConfig.saveMasterConfig(hashmap);
    }

    public String[] updatePreferences(java.util.HashMap hashmap, String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        COM.dragonflow.SSH.SSHManager.getInstance().deleteRemote(s1 + "NT");
        return super.updatePreferences(hashmap, s, s1);
    }

    private jgl.HashMap findMachine(jgl.Array array, String s) {
        Enumeration enumeration = array.elements();
        jgl.HashMap hashmap = new HashMap();
        while (enumeration.hasMoreElements()) {
            hashmap = (jgl.HashMap) enumeration.nextElement();
            if (s.equals(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_id"))) {
                return hashmap;
            }
        }
        return hashmap;
    }

    private jgl.Array getFrames() {
        jgl.Array array = new Array();
        try {
            array = readMachines(getRemoteName());
        } catch (java.lang.Exception exception) {
        }
        return array;
    }

    private jgl.Array readMachines(String s) {
        jgl.Array array = new Array();
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s1;
        for (Enumeration enumeration = hashmap.values(s); enumeration.hasMoreElements(); array.add(COM.dragonflow.Utils.TextUtils.stringToHashMap(s1))) {
            s1 = (String) enumeration.nextElement();
            if (s1.indexOf("_id") >= 0) {
                continue;
            }
            String s2 = "_nextRemoteID";
            if (s.equals("_remoteNTMachine")) {
                s2 = "_nextRemoteNTID";
            }
            String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, s2);
            if (s3.length() == 0) {
                s3 = "10";
            }
            s1 = s1 + " _id=" + s3;
            hashmap.put(s2, COM.dragonflow.Utils.TextUtils.increment(s3));
            COM.dragonflow.SiteView.MasterConfig.saveMasterConfig(hashmap);
        }

        return array;
    }

    private String getRemoteName() {
        return "_remoteNTMachine";
    }

    static {
        pID = new StringProperty("_id");
        pHost = new StringProperty("_host");
        pHost.setDisplayText("NT Server Address", "Enter the UNC style name (Ex: \\FOO) or the IP address.");
        pHost.setParameterOptions(true, 1, false);
        pMethod = new ScalarProperty("_method");
        pMethod.setDisplayText("Connection Method", "Select the method used to connect to the remote server. Requires that applicable services be enabled on the remote machine.");
        pMethod.setParameterOptions(true, 2, false);
        pLogin = new StringProperty("_login");
        pLogin.setDisplayText("Login", "Enter the login for the remote server.");
        pLogin.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "Enter the password for logging in to the remote server.");
        pPassword.setParameterOptions(true, 4, false);
        pPassword.isPassword = true;
        pName = new StringProperty("_name");
        pName.setDisplayText("Title", "Optional, enter the title for the remote server, the default title is the server address.");
        pName.setParameterOptions(true, 5, false);
        pTrace = new BooleanProperty("_trace");
        pTrace.setDisplayText("Trace", "Check this to trace messages to and from the remote NT server in the RunMonitor.log file.");
        pTrace.setParameterOptions(true, 6, false);
        pSshClient = new ScalarProperty("_sshClient");
        pSshClient.setDisplayText("SSH Connection Method", "Select the method to use to connect to the remote server.");
        pSshClient.setParameterOptions(true, 20, false);
        pDisableCache = new BooleanProperty("_disableCache");
        pDisableCache.setDisplayText("Disable Connection Caching", "Check this box to disable caching of SSH connections ");
        pDisableCache.setParameterOptions(true, 21, false);
        pSshConnectionsLimit = new StringProperty("_sshConnectionsLimit");
        pSshConnectionsLimit.setDisplayText("Connection Limit", "Enter the maximum number of connections allowed for this remote.");
        pSshConnectionsLimit.setParameterOptions(true, 22, false);
        pSshAuthMethod = new ScalarProperty("_sshAuthMethod");
        pSshAuthMethod.setDisplayText("SSH Authentication Method", "Select the method to use to authenticate to the remote server (SSH connections only)");
        pSshAuthMethod.setParameterOptions(true, 23, false);
        pKeyFile = new StringProperty("_keyFile");
        pKeyFile.setDisplayText("Key File for SSH connections", "Enter the path to the file containing the private key for this SSH connection. Only valid if authentication method is \"Key File\".");
        pKeyFile.setParameterOptions(true, 24, false);
        pVersion2 = new BooleanProperty("_version2");
        pVersion2.setDisplayText("SSH Version 2 Only", "Check this box to force SSH to only use SSH protocol version 2. ( This SSH option is only supported using the internal java libraries connection method)");
        pVersion2.setParameterOptions(true, 25, false);
        pSshCommand = new StringProperty("_sshCommand");
        pSshCommand.setDisplayText("Custom Commandline",
                "Enter the command for execution of external ssh client. For substituion with above options use $host$, $user$ and $password$ respectivly. This setting is for only for connections using an external process.");
        pSshCommand.setParameterOptions(true, 26, false);
        pSSHPort = new NumericProperty("_sshPort", "22");
        pSSHPort.setDisplayText("SSH Port Number", "Enter the port that the SSH service is running on.");
        pSSHPort.setParameterOptions(true, 27, true);
        pStatus = new StringProperty("_status");
        pStatus.setStateOptions(1);
        pStatus.setIsParameter(false);
        pOs = new StringProperty("_os");
        pOs.setParameterOptions(true, false, 28, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pID, pHost, pMethod, pOs, pLogin, pPassword, pName, pTrace, pSshClient, pDisableCache, pSshConnectionsLimit, pSshAuthMethod, pKeyFile, pVersion2, pSshCommand, pSSHPort, pStatus };
        COM.dragonflow.StandardPreference.RemoteNTInstancePreferences.addProperties("COM.dragonflow.StandardPreference.RemoteNTInstancePreferences", astringproperty);
    }
}
