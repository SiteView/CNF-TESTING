/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.ApacheHttpClientUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.security.NoSuchAlgorithmException;

public class UntrustedCertTrustManager
    implements javax.net.ssl.X509TrustManager
{

    private javax.net.ssl.X509TrustManager standardTrustManager;
    private boolean acceptAllUntrusted;
    private boolean acceptInvalid;
    private static final org.apache.commons.logging.Log LOG;

    public UntrustedCertTrustManager(java.security.KeyStore keystore, boolean flag, boolean flag1)
        throws java.security.NoSuchAlgorithmException, java.security.KeyStoreException
    {
        standardTrustManager = null;
        acceptAllUntrusted = false;
        acceptInvalid = false;
        acceptAllUntrusted = flag;
        acceptInvalid = flag1;
        javax.net.ssl.TrustManagerFactory trustmanagerfactory = javax.net.ssl.TrustManagerFactory.getInstance("SunX509");
        trustmanagerfactory.init(keystore);
        javax.net.ssl.TrustManager atrustmanager[] = trustmanagerfactory.getTrustManagers();
        if(atrustmanager.length == 0)
        {
            throw new NoSuchAlgorithmException("SunX509 trust manager not supported");
        } else
        {
            standardTrustManager = (javax.net.ssl.X509TrustManager)atrustmanager[0];
            return;
        }
    }

    public void checkClientTrusted(java.security.cert.X509Certificate ax509certificate[], String s)
        throws java.security.cert.CertificateException
    {
        standardTrustManager.checkClientTrusted(ax509certificate, s);
    }

    public void checkServerTrusted(java.security.cert.X509Certificate ax509certificate[], String s)
        throws java.security.cert.CertificateException
    {
        if(ax509certificate != null && LOG.isDebugEnabled())
        {
            LOG.debug("Server certificate chain:");
            for(int i = 0; i < ax509certificate.length; i++)
            {
                LOG.debug("X509Certificate[" + i + "]=" + ax509certificate[i]);
            }

        }
        if(!acceptAllUntrusted)
        {
            standardTrustManager.checkServerTrusted(ax509certificate, s);
        }
        if(!acceptInvalid && ax509certificate != null)
        {
            for(int j = 0; j < ax509certificate.length; j++)
            {
                java.security.cert.X509Certificate x509certificate = ax509certificate[j];
                x509certificate.checkValidity();
            }

        }
    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers()
    {
        return standardTrustManager.getAcceptedIssuers();
    }

    static 
    {
        LOG = org.apache.commons.logging.LogFactory.getLog(com.dragonflow.ApacheHttpClientUtils.UntrustedCertTrustManager.class);
    }
}
