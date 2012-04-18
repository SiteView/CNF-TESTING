/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;

public class WebSphereConnectionProperties implements java.io.Serializable {

    public static final int VERSION_3_5 = 0;

    public static final int VERSION_4_X = 1;

    public static final int VERSION_5_X = 2;

    public static final String VERSION_3_5_STR = "3.5x";

    public static final String VERSION_4_X_STR = "4.x";

    public static final String VERSION_5_X_STR = "5.x";

    String serverName;

    String username;

    String password;

    String clientProps;

    int port;

    int version;

    String hashID;

    public WebSphereConnectionProperties(String s, String s1, String s2, int i, String s3) {
        serverName = s;
        username = s1;
        password = s2;
        port = i;
        detectVersion(s3);
        StringBuffer stringbuffer = (new StringBuffer(s)).append(s1).append(s2);
        stringbuffer.append(i).append(s3);
        hashID = stringbuffer.toString();
    }

    private void detectVersion(String s) {
        if (s.equals("4.x")) {
            version = 1;
        } else {
            version = 2;
        }
    }

    public String getHashID() {
        return hashID;
    }

    public String getServerName() {
        return serverName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public int getVersion() {
        return version;
    }

    public String getAPI() {
        if (version == 1) {
            return "PMI";
        } else {
            return "JMX";
        }
    }
}
