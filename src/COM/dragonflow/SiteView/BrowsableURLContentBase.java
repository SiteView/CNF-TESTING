/*
 * 
 * Created on 2005-2-15 12:26:16
 *
 * BrowsableURLContentBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableURLContentBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, MasterConfig, SiteViewGroup, Platform

public abstract class BrowsableURLContentBase extends BrowsableBase {

    public static StringProperty pProxy;

    public static StringProperty pProxyUserName;

    public static StringProperty pProxyPassword;

    public static StringProperty pTimeout;

    public static int nMaxCounters;

    private static int defaultGetBrowseTreeTimeout = 120;

    protected static boolean debug = false;

    private String url;

    private Array postData;

    private String username;

    private String password;

    private String proxy;

    private String proxyUser;

    private String proxyPassword;

    public BrowsableURLContentBase() {
    }

    protected boolean update() {
        loadConnectionProperties();
        int i = getPropertyAsInteger(pTimeout);
        StringBuffer stringbuffer = new StringBuffer();
        if (i <= 0) {
            i = 60;
        }
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, url, null, null, proxy,
                proxyUser, proxyPassword, postData, username, password, null,
                stringbuffer, 0x7fffffffffffffffL, null, 0, i * 1000, null);
        StringBuffer stringbuffer1 = new StringBuffer();
        int j;
        for (j = 0; j < nMaxCounters
                && getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1)).length() != 0; j++) {
        }
        if (al[0] != 200L && al[0] != -996L) {
            stringbuffer1.append(al[0] + "\n Explanation: "
                    + URLMonitor.lookupStatus(al[0]));
        }
        String s = stringbuffer.toString();
        String as[] = new String[j];
        if (stringbuffer1.length() <= 0) {
            parseForCounterValues(s, as);
        }
        int k = 0;
        if (stillActive()) {
            synchronized (this) {
                String s1 = "";
                if (stringbuffer1.length() <= 0) {
                    for (int l = 0; l < as.length; l++) {
                        String s2 = getProperty(PROPERTY_NAME_COUNTER_NAME
                                + (l + 1));
                        if (as[l].equals("")) {
                            setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1),
                                    "n/a");
                            s1 = s1 + s2 + " = " + "<not found>";
                            if (l != as.length - 1) {
                                s1 = s1 + ", ";
                            }
                            k++;
                            continue;
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1),
                                TextUtils.removeChars(as[l], ","));
                        s1 = s1 + s2 + " = " + as[l];
                        if (l != as.length - 1) {
                            s1 = s1 + ", ";
                        }
                    }

                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            k);
                } else {
                    for (int i1 = 0; i1 < nMaxCounters; i1++) {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1),
                                "n/a");
                    }

                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            nMaxCounters);
                    setProperty(pNoData, "n/a");
                    s1 = stringbuffer1.toString();
                }
                setPrecision();
                setProperty(pStateString, s1);
            }
        }
        return true;
    }

    private void loadConnectionProperties() {
        Array array = getConnectionProperties();
        url = getProperty((StringProperty) array.at(0));
        postData = getPostData((StringProperty) array.at(1));
        username = getProperty((StringProperty) array.at(3));
        password = getProperty((StringProperty) array.at(4));
        proxy = getProperty(pProxy);
        proxyUser = getProperty(pProxyUserName);
        proxyPassword = getProperty(pProxyPassword);
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pProxy);
        array.add(pProxyUserName);
        array.add(pProxyPassword);
        return array;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        loadConnectionProperties();
        SocketSession socketsession = SocketSession.getSession(null);
        StringBuffer stringbuffer1 = new StringBuffer();
        printDebug("URLContentBase: Getbrowsetree using timeout: "
                + getBrowseTreeTimeout() + " sec.");
        long al[] = URLMonitor.checkURL(socketsession, url, null, null, proxy,
                proxyUser, proxyPassword, postData, username, password, null,
                stringbuffer1, 0x7fffffffffffffffL, null, 0,
                getBrowseTreeTimeout() * 1000, null);
        if (al[0] != 200L && al[0] != -996L) {
            stringbuffer.append(al[0] + "\n Explanation: "
                    + URLMonitor.lookupStatus(al[0]));
            return "";
        }
        String s = stringbuffer1.toString();
        if (s.indexOf(getStatString()) < 0) {
            stringbuffer.append("File does not contain system statistics.");
            return "";
        } else {
            String s1 = createXMLFromTemplate(s);
            return s1;
        }
    }

    protected abstract void parseForCounterValues(String s, String as[]);

    protected abstract String createXMLFromTemplate(String s);

    protected abstract String getStatString();

    protected abstract void setPrecision();

    protected abstract String getBrowseTreeTimeoutParameterName();

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap
                .put("_browsableContentMaxCounters", (new Integer(i))
                        .toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public int getBrowseTreeTimeout() {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        int i = siteviewgroup.getSettingAsLong(
                getBrowseTreeTimeoutParameterName(),
                defaultGetBrowseTreeTimeout);
        if (i <= 0) {
            i = defaultGetBrowseTreeTimeout;
        }
        return i;
    }

    protected void printDebug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    private Array getPostData(StringProperty stringproperty) {
        Array array = new Array();
        String as[] = TextUtils.split(getProperty(stringproperty), ",");
        for (int i = 0; i < as.length; i++) {
            array.add(as[i]);
        }

        return array;
    }

    static {
        nMaxCounters = 120;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_browsableContentMaxCounters"));
        if (nMaxCounters <= 0 || nMaxCounters < 120) {
            nMaxCounters = 120;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(
                nMaxCounters, true);
        Array array = new Array();
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy",
                "optional proxy server to use including port (example: proxy."
                        + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(false, 8, false);
        array.add(pProxy);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName
                .setDisplayText("Proxy Server User Name",
                        "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(false, 9, false);
        array.add(pProxyUserName);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password",
                "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(false, 10, false);
        pProxyPassword.isPassword = true;
        array.add(pProxyPassword);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout",
                "the time out, in seconds, to wait for the response");
        pTimeout.setParameterOptions(true, 11, true);
        array.add(pTimeout);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i++) {
            astringproperty1[i] = (StringProperty) array.at(i);
        }

        String s = (COM.dragonflow.SiteView.BrowsableURLContentBase.class)
                .getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length
                + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0,
                astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2,
                astringproperty1.length, astringproperty.length);
        addProperties(s, astringproperty2);
    }
}