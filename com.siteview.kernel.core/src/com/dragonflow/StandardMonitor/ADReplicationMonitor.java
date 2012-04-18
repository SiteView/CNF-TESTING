/*
 * 
 * Created on 2005-3-5 14:17:00
 *
 * ADReplicationMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>ADReplicationMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.Utils.LUtils;
import com.dragonflow.Utils.TextUtils;
import com.dragonflow.Utils.LDAP.LDAPSession;

public class ADReplicationMonitor extends AtomicMonitor {

    static StringProperty pDomainControllerName;

    static StringProperty pReplicatingDomainControllers;

    static StringProperty pSecurityPrincipal;

    static StringProperty pSecurityCredential;

    static NumericProperty pMaxRepTime;

    static NumericProperty pPoliingInterval;

    static StringProperty pDirectoryRootPath;

    static StringProperty pLatencyObjectContainerPath;

    static StringProperty pStatus;

    static NumericProperty replicationTime[];

    static final String statusOK = "ok";

    static final String statusTimeout = "timeout";

    static final String statusNoConnection = "no connection";

    static String dateFormat;

    static DateFormat df;

    static int maxNumberOfReplicatingDC;

    private static final String TimeTag = "Description";

    private static String REPLICATION_TIME;

    private LDAPSession session;

    private boolean trace;

    private boolean debug;

    private String securityPrincipalCache;

    private static String localIP = null;

    private String timeOfUpdate;

    static final boolean $assertionsDisabled; /* synthetic field */

    public ADReplicationMonitor() {
        securityPrincipalCache = null;
    }

    public String getHostname() {
        return getProperty(pDomainControllerName);
    }

    protected boolean update() {
        String s = getProperty(pDomainControllerName);
        String s1 = s;
        s1 = getUrlProviderFromDCName(s1);
        int i = getSettingAsLong("_adLDAPTimeout", 5000);
        String s2 = getSecurityPrincipal(i);
        String s3 = getProperty(pSecurityCredential);
        trace = getSetting("_ldapTrace").length() > 0;
        debug = getSetting("_adDebug").length() > 0;
        if (session == null) {
            try {
                session = new LDAPSession(s2, s3, s1, trace, i);
            } catch (NamingException namingexception) {
                String s4 = "ADReplication Monitor(" + s + "):  Unable to initiate session.  " + namingexception.getMessage();
                LogManager.log("Error", s4);
                setProperty(pStateString, s4);
                setProperty(pStatus, "no connection");
                return false;
            }
        }
        try {
            createContainers();
        } catch (NamingException namingexception1) {
            String s5 = "ADReplication Monitor(" + s + "):  Unable to create objects in tree.  " + namingexception1.getMessage();
            LogManager.log("Error", s5);
            setProperty(pStateString, s5);
            setProperty(pStatus, "no connection");
            if (session != null) {
                session.close();
            }
            session = null;
            return false;
        }
        try {
            updateTimeStamp(getReplicationContainerContext());
        } catch (NamingException namingexception2) {
            String s6 = "ADReplication Monitor(" + s + "): error.  Unable to trigger replication " + namingexception2.getMessage();
            LogManager.log("Error", s6);
            setProperty(pStateString, s6);
            setProperty(pStatus, "no connection");
            session.close();
            session = null;
            return false;
        }
        String as[] = getProperty(pReplicatingDomainControllers).split(",");
        Vector vector = new Vector();
        Hashtable hashtable = new Hashtable(as.length);
        for (int j = 0; j < as.length && j < maxNumberOfReplicatingDC; j ++) {
            vector.addElement(as[j]);
            setProperty(replicationTime[j], 0x7fffffff);
            hashtable.put(as[j], replicationTime[j]);
        }

        int k = vector.size();
        long al[] = new long[1];
        StringBuffer stringbuffer = new StringBuffer();
        long l = getPropertyAsLong(pPoliingInterval) * 1000L;
        long l1 = getPropertyAsLong(pMaxRepTime) * 1000L;
        String s7 = "ok";
        boolean flag;
        do {
            flag = false;
            try {
                Thread.currentThread();
                Thread.sleep(l);
            } catch (InterruptedException interruptedexception) {
            }
            l1 -= l;
            Vector vector1 = new Vector(vector);
            for (int j1 = 0; j1 < vector1.size();j1 ++) {
                String s9 = (String) vector1.elementAt(j1);
                try {
                    LDAPSession ldapsession = new LDAPSession(s2, s3, getUrlProviderFromDCName(s9), trace, i);
                    boolean flag1 = calculateLatency(ldapsession, al);
                    long l3 = al[0];
                    if (flag1 && l3 >= 0L && l3 < (long) getPropertyAsInteger(pMaxRepTime)) {
                        setProperty((StringProperty) hashtable.get(s9), l3);
                        vector.remove(s9);
                    } else {
                        if (l3 == 0x7fffffffL) {
                            LogManager.log("Error", "ADReplication Monitor(" + s + "): failed to find replicated data. Error occurred for " + s9);
                        } else if (debug || l3 < 0L) {
                            LogManager.log("Error", "ADReplication Monitor(" + s + "): failed to find replicated data. Replication time for last run for " + s9 + "=" + l3 + (l3 >= 0L ? "" : "Clock may not be synched"));
                        }
                        flag = true;
                    }
                    ldapsession.close();
                    continue;
                } catch (NamingException namingexception3) {
                    s7 = "no connection";
                    setProperty(replicationTime[j1], "n/a");
                    vector.remove(s);
                    LogManager.log("Error", "ADReplication Monitor(" + s + "): error. Unable to create session for replicating dc " + s9 + ". " + namingexception3.getMessage());
                    //j1 ++;dingbing.xu
                }
            }

        } while (flag && l1 > 0L);
        for (int i1 = 0; i1 < k; i1 ++) {
            if (stringbuffer.length() != 0) {
                stringbuffer.append(",");
            }
            String s8 = as[i1];
            long l2 = getPropertyAsInteger(replicationTime[i1]);
            if (l2 == 0x7fffffffL) {
                s7 = "timeout";
                setProperty(replicationTime[i1], "n/a");
                stringbuffer.append("Replication Time: " + s8 + "=n/a (timeout)");
                continue;
            }
            if (getProperty(replicationTime[i1]).equals("n/a")) {
                stringbuffer.append("Replication Time: " + s8 + "=n/a (no connection)");
            } else {
                stringbuffer.append("Replication Time: " + s8 + "=" + l2 + "/sec");
            }
        }

        setProperty(pStatus, s7);
        setProperty(pStateString, stringbuffer);
        setProperty(pNoData, "n/a");
        return true;
    }

    private String getSecurityPrincipal(int i) {
        String s = getProperty(pSecurityPrincipal);
        if (securityPrincipalCache == null || securityPrincipalCache.indexOf(s) == -1) {
            try {
                LDAPSession ldapsession = new LDAPSession("", "", getUrlProviderFromDCName(getProperty(pDomainControllerName)), trace, i);
                if (ldapsession != null) {
                    String s1 = ldapsession.getFromRootCache("rootDomainNamingContext");
                    ldapsession.close();
                    if (s1 != null && s != null) {
                        int j = s1.length();
                        int k = s.length();
                        Object obj = null;
                        if (k >= j) {
                            String s2 = s.substring(k - j);
                            if (!s2.equalsIgnoreCase(s1)) {
                                s = "CN=" + s + ",CN=Users," + s1;
                            }
                        } else {
                            s = "CN=" + s + ",CN=Users," + s1;
                        }
                    }
                }
            } catch (NamingException namingexception) {
                LogManager.log("Error", "AD Replication Monitor: Unable to get root context to compute Security Principal." + namingexception.getMessage());
            }
        }
        securityPrincipalCache = s;
        if (!$assertionsDisabled && securityPrincipalCache == null) {
            throw new AssertionError("Security Principal should never be null at this point.");
        } else {
            return securityPrincipalCache;
        }
    }

    private String getUrlProviderFromDCName(String s) {
        return getUrlProviderFromDCName(s, true);
    }

    private String getUrlProviderFromDCName(String s, boolean flag) {
        char c = '\u0185';
        char c1 = '\u0CC4';
        if (s.indexOf(":") == -1) {
            s = "ldap://" + s + ":" + (int) (flag ? c : c1);
        } else {
            s = "ldap://" + s;
        }
        return s;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        for (int i = 0; i < replicationTime.length && getProperty(replicationTime[i]).length() != 0; i ++) {
            array.add(replicationTime[i]);
        }

        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        String as[] = getProperty(pReplicatingDomainControllers).split(",");
        if (as.length != 0 && as[0].length() != 0) {
            for (int i = 0; i < as.length; i ++) {
                array.add(replicationTime[i]);
            }

        }
        return (Enumeration) array.iterator();
    }

    public String getTopazCounterDescription(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty);
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = stringproperty.toString();
        if (s.indexOf(REPLICATION_TIME) >= 0) {
            String s1 = s.substring(REPLICATION_TIME.length());
            String as[] = getProperty(pReplicatingDomainControllers).split(",");
            int i = 0;
            try {
                i = Integer.parseInt(s1);
            } catch (Exception exception) {
            }
            if (as.length > i && as[i].length() > 0) {
                return "Replication time for " + as[i] + " ";
            }
        }
        return super.GetPropertyLabel(stringproperty, flag);
    }

    public String getTestURL() {
        String s = "/SiteView/cgi/go.exe/SiteView?page=LDAP&securityPrincipal=";
        s = s + URLEncoder.encode(getSecurityPrincipal(2000));
        s = s + "&securityCredential=";
        s = s + URLEncoder.encode(TextUtils.obscure(getProperty(pSecurityCredential)));
        s = s + "&urlProvider=";
        s = s + URLEncoder.encode(getUrlProviderFromDCName(getProperty(pDomainControllerName)));
        s = s + "&ldapQuery=";
        s = s + getReplicationContainerContext();
        return s;
    }

    public int getCostInLicensePoints() {
        return getProperty(pReplicatingDomainControllers).split(",").length;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pSecurityPrincipal) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pSecurityCredential) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pDomainControllerName) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pReplicatingDomainControllers) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            if (s.matches("\\s+")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + "Cannot have whitespace in entry.");
            }
            return s;
        }
        if (stringproperty == pFrequency) {
            s = super.verify(stringproperty, s, httprequest, hashmap);
            int i = 0;
            try {
                i = Integer.parseInt(s);
            } catch (Exception exception) {
            }
            int k = getPropertyAsInteger(pMaxRepTime);
            if (k >= i) {
                hashmap.put(stringproperty, "Frequency " + i + " seconds must be greater than timeout " + k + "seoncds.");
            }
            return s;
        }
        if (stringproperty == pPoliingInterval) {
            int j = 0;
            try {
                j = Integer.parseInt(s);
            } catch (NumberFormatException numberformatexception) {
            }
            int l = getPropertyAsInteger(pMaxRepTime);
            if (getPropertyAsInteger(pPoliingInterval) > l) {
                hashmap.put(stringproperty, "Polling interval " + j + "seconds must be greater than timeout " + l + "seoncds.");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    protected void stopMonitor() {
        if (session != null) {
            session.close();
        }
        session = null;
        super.stopMonitor();
    }

    private void createContainers() throws NamingException {
        String s = getProperty(pLatencyObjectContainerPath);
        String s1 = getProperty(pDirectoryRootPath);
        if (s1.length() > 0) {
            s = s + "," + s1;
        }
        if (!session.contextExists(s)) {
            BasicAttributes basicattributes = new BasicAttributes();
            String s3 = "CN=Container," + session.getFromRootCache("schemaNamingContext");
            basicattributes.put(new BasicAttribute("objectCategory", s3));
            basicattributes.put(new BasicAttribute("objectClass", "container"));
            String s4 = s;
            int i = s4.indexOf(",");
            if (i != -1) {
                s4 = s4.substring(0, i);
            }
            int j = s4.indexOf("=");
            basicattributes.put(new BasicAttribute(s4.substring(0, j), s4.substring(j + 1)));
            session.createSubcontext(s, basicattributes);
        }
        String s2 = getReplicationContainerContext();
        if (!session.contextExists(s2)) {
            BasicAttributes basicattributes1 = new BasicAttributes();
            String s5 = "CN=Container," + session.getFromRootCache("schemaNamingContext");
            basicattributes1.put(new BasicAttribute("objectCategory", s5));
            basicattributes1.put(new BasicAttribute("objectClass", "container"));
            basicattributes1.put(new BasicAttribute("cn", getUniqueName()));
            session.createSubcontext(s2, basicattributes1);
        }
    }

    /**
     * CAUTION: Deompiled by hand.
     * 
     * @return
     */
    private String getReplicationContainerContext() {
        String s;
        s = "cn=" + getUniqueName() + "," + getProperty(pLatencyObjectContainerPath);
        String s1 = getProperty(pDirectoryRootPath);
        if (s1.length() <= 0) {
            s = s + "," + s1;
        } else if (session == null) {
            return null;
        } else {
            try {
                s = s + "," + session.getDefaultContext();
            } catch (NamingException e) {
                LogManager.log("Error", "AD Replication Montior unable to get default context. " + e.getMessage());
            }
        }
        return s;
    }

    private String getUniqueName() {
        if (localIP == null) {
            try {
                localIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                localIP = "127.0.0.1";
                LogManager.log("Error", "ADRreplication monitor unable to get local IP, using 127.0.0.1");
            }
        }
        return getProperty(pDomainControllerName) + ":" + localIP + ":" + getProperty(pUniqueInternalId);
    }

    private void updateTimeStamp(String s) throws NamingException {
        timeOfUpdate = session.getFromRoot("currentTime");
        session.setAttribute(s, "Description", timeOfUpdate);
    }

    /**
     * 
     * 
     * @param ldapsession
     * @param al
     * @return
     */
    private boolean calculateLatency(LDAPSession ldapsession, long al[]) {
        al[0] = 0x7fffffffL;
        try {
            String s = getReplicationContainerContext();
            if (s == null) {
                return false;
            }
            String s1;
            Date date;
            s1 = ldapsession.getValue(s, "description");
            String s2 = ldapsession.getValue(s, "whenChanged");
            String s3 = timeOfUpdate;
            date = df.parse(s2);
            Date date1 = df.parse(s3);
            if (s1 != null && timeOfUpdate.equals(s1)) {
                al[0] = (date.getTime() - date1.getTime()) / 1000L;
                return true;
            }
            if (s1 != null && s1.length() > 0) {
                Date date2 = df.parse(s1);
                al[0] = (date.getTime() - date2.getTime()) / 1000L;
                return false;
            }
        } catch (Exception exception) {
            LogManager.log("Error", "AD Replication Monitor unable to calculate latency for " + ldapsession.getUrl() + ". " + exception.getMessage());
        }
        return false;
    }

    static {
        $assertionsDisabled = !(com.dragonflow.StandardMonitor.ADReplicationMonitor.class).desiredAssertionStatus();
        dateFormat = "yyyyMMddHHmmss'.0Z'";
        maxNumberOfReplicatingDC = 10;
        REPLICATION_TIME = "replicationTime";
        ArrayList arraylist = new ArrayList();
        HashMap hashmap = MasterConfig.getMasterConfig();
        try {
            int i = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxNumReplicatingDC"));
            if (i > 0) {
                maxNumberOfReplicatingDC = i;
            }
        } catch (Exception exception) {
        }
        replicationTime = new NumericProperty[maxNumberOfReplicatingDC];
        df = new SimpleDateFormat(dateFormat);
        int j = 1;
        int k = 1;
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(j ++);
        arraylist.add(pStatus);
        pDomainControllerName = new StringProperty("_domainController");
        pDomainControllerName.setDisplayText("Domain Controller", "The fully qualified name of the Domain Controller that is being replicated");
        pDomainControllerName.setParameterOptions(true, j ++, false);
        arraylist.add(pDomainControllerName);
        pReplicatingDomainControllers = new StringProperty("_replicatingDomainControllers");
        pReplicatingDomainControllers.setDisplayText("Replicating Domain Controllers", "A comma seperated list of domain controllers that replicate this DC's data.");
        pReplicatingDomainControllers.setParameterOptions(true, j ++, false);
        arraylist.add(pReplicatingDomainControllers);
        pSecurityPrincipal = new StringProperty("_securityprincipal");
        pSecurityPrincipal.setDisplayText("Username", "Enter in Usernane or full Security Principal Context of a Domain Admin.  The default context is taken from the root context. ( ex. CN=UserName,CN=USERS,DC=your-company,DC=com ) ");
        pSecurityPrincipal.setParameterOptions(true, j ++, false);
        arraylist.add(pSecurityPrincipal);
        pSecurityCredential = new StringProperty("_password");
        pSecurityCredential.setDisplayText("Password", "The password used for authentication. ");
        pSecurityCredential.setParameterOptions(true, j ++, false);
        pSecurityCredential.isPassword = true;
        arraylist.add(pSecurityCredential);
        pPoliingInterval = new NumericProperty("_pollingInterval", "10", "seconds");
        pPoliingInterval.setDisplayText("Polling Interval", "Interval to poll, in seconds, for verification of replication.");
        pPoliingInterval.setParameterOptions(true, k ++, true);
        arraylist.add(pPoliingInterval);
        pMaxRepTime = new NumericProperty("_maxTime", "500", "seconds");
        pMaxRepTime.setDisplayText("Maximum Replication Time", "The maximum amount of time, in seconds, to poll for verification of replication.");
        pMaxRepTime.setParameterOptions(true, k ++, true);
        arraylist.add(pMaxRepTime);
        pDirectoryRootPath = new StringProperty("_ldapquery");
        pDirectoryRootPath.setDisplayText("Path to Directory", "This is the path to the part of the Directory that is to be monitored. ( default is the default context of the AD server ) ");
        pDirectoryRootPath.setParameterOptions(true, k ++, true);
        arraylist.add(pDirectoryRootPath);
        pLatencyObjectContainerPath = new StringProperty("_containerPath", "cn=SiteViewReplication");
        pLatencyObjectContainerPath.setDisplayText("Container Path", "The Path to the container where SiteView replication objects will be stored.");
        pLatencyObjectContainerPath.setParameterOptions(false, k ++, true);
        arraylist.add(pLatencyObjectContainerPath);
        for (int l = 0; l < maxNumberOfReplicatingDC; l ++) {
            replicationTime[l] = new NumericProperty(REPLICATION_TIME + l, "", "seconds");
            replicationTime[l].setLabel("replication time for dc " + (l + 1) + " ");
            replicationTime[l].setStateOptions(j ++);
            replicationTime[l].setIsThreshold(true);
            arraylist.add(replicationTime[l]);
        }

        StringProperty astringproperty[] = new StringProperty[arraylist.size()];
        for (int i1 = 0; i1 < arraylist.size(); i1 ++) {
            astringproperty[i1] = (StringProperty) arraylist.get(i1);
        }

        String s = (com.dragonflow.StandardMonitor.ADReplicationMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("status != ok\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Tests replication time between Domain Controllers");
        setClassProperty(s, "help", "ADReplicationMonitor.htm");
        setClassProperty(s, "title", "Active Directory Replication Monitor");
        setClassProperty(s, "class", "ADReplicationMonitor");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "toolName", "LDAP Authentication");
        setClassProperty(s, "toolDescription", "Test an LDAP server by authenticating a user.");
        setClassProperty(s, "topazName", "Active Directory Replication Monitor");
        setClassProperty(s, "topazType", "Web Application Server");
        try {
            Class.forName("javax.naming.ldap.LdapContext");
            setClassProperty(s, "loadable", "true");
        } catch (Throwable throwable) {
            setClassProperty(s, "loadable", "false");
        }
        if (!LUtils.isValidSSforXLicense(new ADReplicationMonitor())) {
            setClassProperty(s, "loadable", "false");
        }
        setClassProperty(s, "addable", "false");
    }
}