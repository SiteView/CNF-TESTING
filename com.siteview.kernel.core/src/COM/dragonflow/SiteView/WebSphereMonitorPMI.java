/*
 * 
 * Created on 2005-2-16 17:29:54
 *
 * WebSphereMonitorPMI.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>WebSphereMonitorPMI</code>
 * 
 * XXX looks like AOP weaved in.
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.rmi.RemoteException;
import java.util.Vector;

import org.omg.SecurityLevel2.LoginFailed;

import COM.dragonflow.Utils.WebSphere.ConnectionException;
import COM.dragonflow.Utils.WebSphere.WebSphereCounter;
import COM.dragonflow.Utils.WebSphere.WebSphereObject;

import com.ibm.websphere.pmi.client.CpdCollection;
import com.ibm.websphere.pmi.client.CpdData;
import com.ibm.websphere.pmi.client.CpdLoad;
import com.ibm.websphere.pmi.client.CpdStat;
import com.ibm.websphere.pmi.client.CpdValue;
import com.ibm.websphere.pmi.client.PerfDescriptor;
import com.ibm.websphere.pmi.client.PmiClient;
import com.ibm.ws.security.util.LoginHelper;

// Referenced classes of package COM.dragonflow.SiteView:
// WebSphereMonitorImpl

public final class WebSphereMonitorPMI extends WebSphereMonitorImpl {

    private PmiClient pmi;

    public WebSphereMonitorPMI(String s, String s1, String s2, String s3) throws ConnectionException {
        super(s, s1, s2, s3);
        connect();
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public void connect() throws ConnectionException {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.connect().");
        }
        try {
            if (username != null && username.length() > 0) {
                if (debug) {
                    System.err.println("WebSphereMonitorPMI.connect() instantiating a new LoginHelper().");
                }
                LoginHelper loginhelper = new LoginHelper();
                if (debug) {
                    System.err.println("WebSphereMonitorPMI.connect() invoking  LoginHelper.login().");
                }
                loginhelper.login(username, password);
            }
            if (debug) {
                System.err.println("WebSphereMonitorPMI.connect() creating new PmiClient for host=" + host + " and port=" + port + ".");
            }
            pmi = new PmiClient(host, port);
            if (debug) {
                System.err.println("Leaving WebSphereMonitorPMI.connect().");
            }
        } catch (RemoteException remoteexception) {
            error("WebSphereMonitorPMI.connect() encountered RemoteException while connecting to WebSphere server: " + remoteexception.toString(), remoteexception);
            throw new ConnectionException(remoteexception);
        } catch (LoginFailed loginfailed) {
            error("WebSphereMonitorPMI.connect() encountered LoginFailed exception while connecting to WebSphere server: " + loginfailed.toString(), loginfailed);
            throw new ConnectionException(loginfailed);
        } catch (Exception exception) {
            error("WebSphereMonitorPMI encountered an exception while connecting to WebSphere server: " + exception.toString(), exception);
            throw new ConnectionException(exception);
        } finally {
            if (debug) {
                System.err.println("Leaving WebSphereMonitorPMI.connect().");
            }
        }
    }

    public boolean getCounterList(StringBuffer stringbuffer) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.getCounterList() with StringBuffer xml=" + stringbuffer + ".");
        }

        try {
            try {
                WebSphereObject websphereobject = new WebSphereObject();
                if (debug) {
                    System.err.println("WebSphereMonitorPMI.getCounterList() invoking PmiClient.createRootCollection().");
                }
                PmiClient.createRootCollection();
                if (debug) {
                    System.err.println("WebSphereMonitorPMI.getCounterList() getting list of all nodes in domain with PmiClient.listNodes().");
                }
                PerfDescriptor[] aperfdescriptor = pmi.listNodes();
                if (aperfdescriptor == null) {

                    error("WebSphereMonitorPMI.getCounterList() found no nodes while getting a list of counters.");
                    stringbuffer.append("No nodes could be listed from the given WebSphere AS admin server. Please verify connection properties.");
                    boolean b = false;
                    if (debug) {
                        System.err.println("Leaving WebSphereMonitorPMI.getCounterList().");
                    }
                    return b;
                }
                for (int i = 0; i < aperfdescriptor.length; i ++) {
                    String s = aperfdescriptor[i].getName();
                    if (debug) {
                        System.err.println("WebSphereMonitorPMI.getCounterList() getting counters for node: " + s + ".");
                    }
                    WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, s, s, "");
                    addServers(websphereobject1, aperfdescriptor[i]);
                }

                websphereobject.toXML(stringbuffer, 0);
                boolean b = true;
                if (debug) {
                    System.err.println("Leaving WebSphereMonitorPMI.getCounterList().");
                }
                return b;
            } catch (Exception e) {

                boolean flag;
                error("WebSphereMonitorPMI.getCounterList() encountered an exception while getting list of counters: " + e, e);
                stringbuffer.append("Error collecting counters: " + e);
                flag = false;
                if (debug) {
                    System.err.println("Leaving WebSphereMonitorPMI.getCounterList().");
                }
                return flag;
            }
        } finally {
            if (debug) {
                System.err.println("Leaving WebSphereMonitorPMI.getCounterList().");
            }
        }

    }

    void addServers(WebSphereObject websphereobject, PerfDescriptor perfdescriptor) throws Exception {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.addServers() with nodeObj=" + websphereobject + " and node=" + perfdescriptor + ".");
        }
        PerfDescriptor aperfdescriptor[] = pmi.listServers(perfdescriptor);
        for (int i = 0; i < aperfdescriptor.length; i ++) {
            WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, aperfdescriptor[i].getName(), aperfdescriptor[i].getName(), "");
            addMembers(websphereobject1, aperfdescriptor[i]);
        }

        if (debug) {
            System.err.println("Leaving WebSphereMonitorPMI.addServers().");
        }
    }

    void addMembers(WebSphereObject websphereobject, PerfDescriptor perfdescriptor) throws Exception {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.addMembers() with parentObj=" + websphereobject + " and parent=" + perfdescriptor + ".");
        }
        if (debug) {
            System.err.println("WebSphereMonitorPMI.addMembers() listing all PMI members under parent " + perfdescriptor + ".");
        }
        PerfDescriptor aperfdescriptor[] = pmi.listMembers(perfdescriptor);
        if (aperfdescriptor != null) {
            for (int i = 0; i < aperfdescriptor.length; i ++) {
                if (aperfdescriptor[i].getType() == 18) {
                    continue;
                }
                if (debug) {
                    System.err.println("WebSphereMonitorPMI.addMembers() recursively adding members for member " + aperfdescriptor[i].getName() + ".");
                }
                WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, aperfdescriptor[i].getName(), aperfdescriptor[i].getName(), "");
                addMembers(websphereobject1, aperfdescriptor[i]);
            }

        }
        CpdCollection cpdcollection = pmi.get(perfdescriptor, false);
        if (cpdcollection != null) {
            CpdData acpddata[] = cpdcollection.dataMembers();
            for (int j = 0; j < acpddata.length; j ++) {
                CpdValue cpdvalue = acpddata[j].getValue();
                if (cpdvalue instanceof CpdStat) {
                    WebSphereObject websphereobject2 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(acpddata[j].getPmiDataInfo().getName(), 46), acpddata[j].getPmiDataInfo().getName(), acpddata[j].getPmiDataInfo().getComment());
                    websphereobject2.addCounter("Mean", "mean*", "Mean of the sample set");
                    websphereobject2.addCounter("Count", "count*", "Element count");
                    websphereobject2.addCounter("SumSquares", "sumSquares*", "Sum of squares of the elements");
                    websphereobject2.addCounter("Variance", "variance*", "Variance");
                    websphereobject2.addCounter("StandardDeviation", "standardDeviation*", "Standard Deviation");
                    continue;
                }
                if (cpdvalue instanceof CpdLoad) {
                    WebSphereObject websphereobject3 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(acpddata[j].getPmiDataInfo().getName(), 46), acpddata[j].getPmiDataInfo().getName(), acpddata[j].getPmiDataInfo().getComment());
                    websphereobject3.addCounter("Mean", "mean*", "Time-weighted average value");
                    websphereobject3.addCounter("CurrentLevel", "getCurrentLevel*", "Last data point");
                    websphereobject3.addCounter("Weight", "getWeight*", "Measured time period");
                } else {
                    websphereobject.addCounter(WebSphereCounter.getLeafName(acpddata[j].getPmiDataInfo().getName(), 46), acpddata[j].getPmiDataInfo().getName(), acpddata[j].getPmiDataInfo().getComment());
                }
            }

        }
        if (debug) {
            System.err.println("Leaving WebSphereMonitorPMI.addMembers().");
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public WebSphereCounter[] getCounterValues(WebSphereCounter awebspherecounter[]) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.getCounterValues() with counters=" + awebspherecounter + ".");
        }
        for (int i = 0; i < awebspherecounter.length; i ++) {
            String s2 = null;
            int j = awebspherecounter[i].getName().lastIndexOf('/');
            int k = awebspherecounter[i].getName().length();
            if (awebspherecounter[i].getName().endsWith("*")) {
                s2 = awebspherecounter[i].getName().substring(j + 1, k - 1);
                k = j;
                j = awebspherecounter[i].getName().lastIndexOf('/', j - 1);
            }
            String s1 = WebSphereCounter.denormalize(awebspherecounter[i].getName().substring(j + 1, k));
            String s = WebSphereCounter.denormalize(awebspherecounter[i].getName().substring(0, j));
            if (debug) {
                System.err.println("WebSphereMonitorPMI.getCounterValues() getting value for counter=" + s1 + ".");
            }
            String as[] = splitObject(s);
            PerfDescriptor perfdescriptor = PmiClient.createPerfDescriptor(as);
            awebspherecounter[i].setValue("n/a");
            try {
                CpdCollection cpdcollection = pmi.get(perfdescriptor, false);
                CpdData acpddata[] = cpdcollection.dataMembers();
                for (int l = 0; l < acpddata.length; l ++) {
                    if (acpddata[l].getPmiDataInfo().getName().equals(s1)) {
                        CpdValue cpdvalue = acpddata[l].getValue();
                        if ((cpdvalue instanceof CpdStat) || (cpdvalue instanceof CpdLoad)) {
                            awebspherecounter[i].setValue(String.valueOf(cpdvalue.getClass().getMethod(s2, null).invoke(cpdvalue, null)));
                        } else {
                            awebspherecounter[i].setValue(String.valueOf(cpdvalue.getValue()));
                        }
                    }
                }
            } catch (Exception exception) {
                error("Exception occurred while getting list of counters from WebSphere server: " + exception, exception);
            }
        }

        if (debug) {
            System.err.println("Leaving WebSphereMonitorPMI.getCounterValues() and returning conters=" + awebspherecounter + ".");
        }
        return awebspherecounter;
    }

    private String[] splitObject(String s) {
        if (debug) {
            System.err.println("Entering WebSphereMonitorPMI.splitObject() with object=" + s + ".");
        }
        Vector vector = new Vector();
        int i = s.length();
        StringBuffer stringbuffer = new StringBuffer();
        for (int j = 0; j < i; j ++) {
            switch (s.charAt(j)) {
            case 92: // '\\'
                j ++;
                stringbuffer.append(s.charAt(j));
                break;

            case 47: // '/'
                vector.add(stringbuffer.toString());
                stringbuffer = new StringBuffer();
                break;

            default:
                stringbuffer.append(s.charAt(j));
                break;
            }
        }

        vector.add(stringbuffer.toString());
        String as[] = new String[vector.size()];
        for (int k = 0; k < as.length; k ++) {
            as[k] = (String) vector.elementAt(k);
        }

        if (debug) {
            System.err.println("Leaving WebSphereMonitorPMI.splitObject() with ret=" + as + ".");
        }
        return as;
    }
}
