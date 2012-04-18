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

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Enumeration;

import com.sun.net.ssl.internal.ssl.Provider;

public class SSLSocketStream {

    static javax.net.ServerSocketFactory sslServerSocketFactory = null;

    static javax.net.SocketFactory sslSocketFactory = null;

    static java.security.SecureRandom randomGenerator = null;

    public static String certificateDescription = "";

    public SSLSocketStream() {
    }

    public static javax.net.ServerSocketFactory getServerSocketFactory() {
        return sslServerSocketFactory;
    }

    public static javax.net.SocketFactory getSocketFactory() {
        return sslSocketFactory;
    }

    public static java.net.Socket createSSLFactorySocket(javax.net.ssl.SSLSocketFactory sslsocketfactory, java.net.Socket socket, String s, int i, boolean flag) throws java.io.IOException {
        javax.net.ssl.SSLSocket sslsocket = (javax.net.ssl.SSLSocket) sslsocketfactory.createSocket(socket, s, i, flag);
        return sslsocket;
    }

    public static String initialize(String s, String s1, String s2) {
        String s3 = "";
        try {
            java.lang.System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new Provider());
            com.sun.net.ssl.SSLContext sslcontext = com.sun.net.ssl.SSLContext.getInstance("SSL");
            randomGenerator = new SecureRandom();
            byte abyte0[] = new byte[20];
            for (int i = 0; i < 20; i ++) {
                abyte0[i] = (byte) (int) (java.lang.Math.random() * 256D - 128D);
            }

            randomGenerator.setSeed(abyte0);
            if ((new File(s + ".pfx")).exists()) {
                s = s + ".pfx";
            }
            com.sun.net.ssl.KeyManager akeymanager[] = null;
            try {
                if ((new File(s)).exists()) {
                    java.security.KeyStore keystore;
                    if (s.endsWith(".pfx")) {
                        keystore = java.security.KeyStore.getInstance("PKCS12");
                    } else {
                        keystore = java.security.KeyStore.getInstance("JKS");
                    }
                    com.sun.net.ssl.KeyManagerFactory keymanagerfactory = com.sun.net.ssl.KeyManagerFactory.getInstance("SunX509");
                    java.io.FileInputStream fileinputstream = new FileInputStream(s);
                    char ac[] = s1.toCharArray();
                    char ac1[] = s2.toCharArray();
                    keystore.load(fileinputstream, ac);
                    Enumeration enumeration = keystore.aliases();
                    while (enumeration.hasMoreElements()) {
                        String s4 = (String) enumeration.nextElement();
                        certificateDescription = certificateDescription + " (" + s4;
                        java.security.cert.Certificate acertificate[] = keystore.getCertificateChain(s4);
                        if (acertificate != null) {
                            int j = 0;
                            while (j < acertificate.length) {
                                java.security.cert.X509Certificate x509certificate = (java.security.cert.X509Certificate) acertificate[j];
                                certificateDescription = certificateDescription + " (cert " + x509certificate.getSubjectDN() + ", " + x509certificate.getSigAlgName() + ")";
                                j ++;
                            }
                        }
                    }
                    s3 = s3 + "certs: " + certificateDescription + "\n";
                    keymanagerfactory.init(keystore, ac1);
                    akeymanager = keymanagerfactory.getKeyManagers();
                }
            } catch (java.lang.Exception exception) {
                exception.printStackTrace();
                s3 = s3 + exception.toString();
            }
            sslcontext.init(akeymanager, null, randomGenerator);
            if (akeymanager != null) {
                sslServerSocketFactory = sslcontext.getServerSocketFactory();
            }
            sslSocketFactory = sslcontext.getSocketFactory();
        } catch (java.lang.Throwable throwable) {
            throwable.printStackTrace();
            s3 = s3 + throwable.toString();
        }
        return s3;
    }

    public static void invalidateSSLSession(java.net.Socket socket) {
        if (socket instanceof javax.net.ssl.SSLSocket) {
            javax.net.ssl.SSLSession sslsession = ((javax.net.ssl.SSLSocket) socket).getSession();
            sslsession.invalidate();
        }
    }

    public static javax.net.SocketFactory getSSLFactory(String s, String s1, StringBuffer stringbuffer) {
        javax.net.ssl.SSLSocketFactory sslsocketfactory = null;
        try {
            if (!(new File(s)).exists()) {
                throw new Exception("certificate not found");
            }
            java.security.KeyStore keystore;
            if (s.endsWith(".pfx")) {
                keystore = java.security.KeyStore.getInstance("PKCS12");
            } else {
                keystore = java.security.KeyStore.getInstance("JKS");
            }
            java.io.FileInputStream fileinputstream = new FileInputStream(s);
            char ac[] = s1.toCharArray();
            char ac1[] = s1.toCharArray();
            keystore.load(fileinputstream, ac);
            Enumeration enumeration = keystore.aliases();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                certificateDescription = certificateDescription + " (" + s2;
                java.security.cert.Certificate acertificate[] = keystore.getCertificateChain(s2);
                if (acertificate != null) {
                    int i = 0;
                    while (i < acertificate.length) {
                        java.security.cert.X509Certificate x509certificate = (java.security.cert.X509Certificate) acertificate[i];
                        certificateDescription = certificateDescription + " (cert " + x509certificate.getSubjectDN() + ", " + x509certificate.getSigAlgName() + ")";
                        i ++;
                    }
                }
            } 
            stringbuffer.append("certs: " + certificateDescription + "\n");
            com.sun.net.ssl.KeyManagerFactory keymanagerfactory = com.sun.net.ssl.KeyManagerFactory.getInstance("SunX509");
            keymanagerfactory.init(keystore, ac1);
            com.sun.net.ssl.KeyManager akeymanager[] = keymanagerfactory.getKeyManagers();
            com.sun.net.ssl.SSLContext sslcontext = com.sun.net.ssl.SSLContext.getInstance("SSL");
            sslcontext.init(akeymanager, null, randomGenerator);
            sslsocketfactory = sslcontext.getSocketFactory();
        } catch (java.lang.Throwable throwable) {
            throwable.printStackTrace();
            stringbuffer.append("error: " + throwable.toString());
        }
        return sslsocketfactory;
    }

}
