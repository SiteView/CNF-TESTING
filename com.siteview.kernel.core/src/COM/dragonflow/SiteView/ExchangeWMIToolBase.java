/*
 * 
 * Created on 2005-2-16 15:10:17
 *
 * ExchangeWMIToolBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ExchangeWMIToolBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.HashMap;
import java.util.Vector;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.WMIUtils;
//import COM.dragonflow.WMIMonitor.xdr.property_pair;
//import COM.dragonflow.WMIMonitor.xdr.property_pair_seq;
//import COM.dragonflow.WMIMonitor.xdr.wmi_connect;
//import COM.dragonflow.WMIMonitor.xdr.wmi_instance_result;
//import COM.dragonflow.WMIMonitor.xdr.wmi_instance_seq;
//import COM.dragonflow.WMIMonitor.xdr.wmi_run_query;

// Referenced classes of package COM.dragonflow.SiteView:
// ExchangeToolBase, IServerPropMonitor

public class ExchangeWMIToolBase extends ExchangeToolBase implements
        IServerPropMonitor {

    protected static ServerProperty pHost;

    protected static StringProperty pUsername;

    protected static StringProperty pPassword;

    protected static StringProperty pTimeout;

    private static final int DEFAULT_TIMEOUT_SECONDS = 60;

    public ExchangeWMIToolBase() {
    }

    protected Vector executeQuery(String s) {
//        wmi_instance_seq wmi_instance_seq1 = new wmi_instance_seq();
        StringBuffer stringbuffer = new StringBuffer();
        int i = getPropertyAsInteger(pTimeout);
        if (i == 0) {
            i = 60;
        }
//        if (!WMIUtils.sendWmiRequest(5, new wmi_run_query(getConnectionInfo(),
//                s), wmi_instance_seq1, stringbuffer, i)) {
//            setProperty(pStatus, "error");
//            setProperty(pStateString, "Query failed: " + stringbuffer);
//            LogManager.log("Error", "ExchangeWMIToolBase: " + getFullID()
//                    + " failed, output:\n" + stringbuffer);
//            return null;
//        }
       Vector vector = new Vector();
//        for (int j = 0; j < wmi_instance_seq1.size(); j++) {
//            wmi_instance_result wmi_instance_result1 = wmi_instance_seq1.get(j);
//            HashMap hashmap = new HashMap();
//            vector.add(hashmap);
//            property_pair_seq property_pair_seq1 = wmi_instance_result1
//                    .get_properties();
//            for (int k = 0; k < property_pair_seq1.size(); k++) {
//                property_pair property_pair1 = property_pair_seq1.get(k);
//                hashmap.put(property_pair1.get_prop_key(), property_pair1
//                        .get_prop_value());
//            }
//
//        }

        return vector;
    }

    public String getHostname() {
        return getProperty(pHost);
    }

//    protected wmi_connect getConnectionInfo() {
//        return new wmi_connect(getProperty(pHost)
//                + "\\root\\MicrosoftExchangeV2", getProperty(pUsername),
//                getProperty(pPassword));
//    }

    public StringProperty getServerProperty() {
        return pHost;
    }

    public boolean remoteCommandLineAllowed() {
        return false;
    }

    static {
        String s = (COM.dragonflow.SiteView.ExchangeWMIToolBase.class)
                .getName();
        pHost = new ServerProperty("_server");
        pHost.setDisplayText("Server", "");
        pHost.setParameterOptions(true, 1, false);
        pUsername = new StringProperty("_username");
        pUsername.setDisplayText("Username",
                "For accessing WMI statistics on host");
        pUsername.setParameterOptions(true, 2, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password",
                "For accessing WMI statistics on host");
        pPassword.setParameterOptions(true, 3, false);
        pPassword.isPassword = true;
        pTimeout = new StringProperty("_timeout", "60");
        pTimeout.setDisplayText("Timeout",
                "timeout in seconds for monitor to update");
        pTimeout.setParameterOptions(true, 5, true);
        StringProperty astringproperty[] = { pHost, pUsername, pPassword,
                pTimeout };
        addProperties(s, astringproperty);
    }
}
