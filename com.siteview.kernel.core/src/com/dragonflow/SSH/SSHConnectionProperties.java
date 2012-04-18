/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.SSH;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.SSH:
// SSHClientBase
public class SSHConnectionProperties extends java.util.Properties {

    public static final String USERNAME = "username";

    public static final String HOSTNAME = "server";

    public static final String PORT = "port";

    public static final String AUTH_METHOD = "auth-method";

    public static final String KEY_FILE = "private-key";

    public static final String PASSPHRASE = "passphrase";

    public static final String PASSWORD = "password";

    public static final String PASSWORD_AUTH = "password";

    public static final String KEY_AUTH = "publickey";

    public static final String CIPHER = "cipher";

    public SSHConnectionProperties(com.dragonflow.SiteView.Machine machine) {
        String s = machine.getProperty(com.dragonflow.SiteView.Machine.pPassword);
        String s1 = machine.getProperty(com.dragonflow.SiteView.Machine.pHost);
        String s2 = machine.getProperty(com.dragonflow.SiteView.Machine.pLogin);
        String s3 = machine.getProperty(com.dragonflow.SiteView.Machine.pSshKeyFile);
        String s4 = machine.getProperty(com.dragonflow.SiteView.Machine.pSshAuthMethod);
        int i = machine.getPropertyAsInteger(com.dragonflow.SiteView.Machine.pSSHPort);
        setUsername(s2);
        setPassword(s);
        setPort(i);
        setTarget(s1);
        if (s4.equals("password")) {
            setAuthenticationMethod("password");
        } else {
            setAuthenticationMethod("publickey");
            setKeyFile(s3);
        }
        setCipher("blowfish");
    }

    public void setCipher(String s) {
        setProperty("cipher", s);
    }

    public String getCipher() {
        return getProperty("cipher");
    }

    public String getUsername() {
        return getProperty("username");
    }

    public void setUsername(String s) {
        setProperty("username", s);
    }

    public String getPassword() {
        return getProperty("password");
    }

    public void setPassword(String s) {
        setProperty("password", s);
    }

    public String getTarget() {
        return getProperty("server");
    }

    public void setTarget(String s) {
        int i = s.indexOf(" -P ");
        if (i != -1) {
            try {
                setPort(java.lang.Integer.parseInt(s.substring(i + 4)));
            } catch (java.lang.Exception exception) {
            }
            s = s.substring(0, i);
        }
        setProperty("server", s);
    }

    /**
     * 
     * 
     * @return
     */
    public int getPort() {
        try {
            return java.lang.Integer.parseInt(getProperty("port"));
        } catch (java.lang.Exception exception) {
            return 22;
        }
    }

    public void setPort(int i) {
        setProperty("port", i + "");
    }

    public boolean isPasswordAuthentication() {
        return getProperty("auth-method").equals("password");
    }

    public boolean isKeyAuthentication() {
        return getProperty("auth-method").equals("publickey");
    }

    public void setAuthenticationMethod(String s) {
        setProperty("auth-method", s);
    }

    public String getAuthenticationMethod() {
        return getProperty("auth-method");
    }

    public String getKeyFile() {
        return getProperty("private-key");
    }

    public void setKeyFile(String s) {
        if (s.length() == 0) {
            s = com.dragonflow.SSH.SSHClientBase.DEFAULT_KEY_FILE;
        }
        setProperty("private-key", s);
    }
}
