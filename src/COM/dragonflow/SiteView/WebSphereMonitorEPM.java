/*
 * 
 * Created on 2005-2-16 17:34:42
 *
 * WebSphereMonitorEPM.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>WebSphereMonitorEPM</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.ejb.EJBObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.Security.CredentialType;
import org.omg.Security.InvalidCredentialType;
import org.omg.Security.OpaqueHolder;
import org.omg.SecurityLevel2.CredentialsHolder;
import org.omg.SecurityLevel2.InvalidCredential;
import org.omg.SecurityLevel2.LoginFailed;

import COM.dragonflow.Utils.WebSphere.ConnectionException;
import COM.dragonflow.Utils.WebSphere.WebSphereCounter;
import COM.dragonflow.Utils.WebSphere.WebSphereObject;

import com.ibm.IExtendedSecurity.Current;
import com.ibm.ejs.oa.EJSORB;
import com.ibm.ejs.perf.client.PerfClientData;
import com.ibm.ejs.perf.client.PerfClientDataGroupImpl;
import com.ibm.ejs.perf.client.PerfDouble;
import com.ibm.ejs.perf.client.PerfGroup;
import com.ibm.ejs.perf.client.PerfInt;
import com.ibm.ejs.perf.client.PerfLoad;
import com.ibm.ejs.perf.client.PerfLong;
import com.ibm.ejs.perf.client.PerfStatInfo;
import com.ibm.ejs.perf.client.PerfValue;
import com.ibm.ejs.perf.value.PerfValueLoad;
import com.ibm.ejs.perf.value.PerfValueStat;
import com.ibm.ejs.sm.beans.EpmService;
import com.ibm.ejs.sm.beans.EpmServiceHome;

public final class WebSphereMonitorEPM {

    private static final String usage = "Usage: WebSphereMonitor [-username username] [-passwd passwd] [-port port] [-dir dir] hostname [counters...]";

    private InitialContext initialContext;

    private String repositoryHome;

    private com.ibm.ejs.perf.client.PerfEpmClient perfClient;

    private HashMap map;

    private String counters[];

    private String host;

    private String password;

    private String realm;

    private String username;

    private String port;

    private String dir;

    public WebSphereMonitorEPM(String s, String s1, String s2, String s3, String s4, String s5, String as[]) {
        map = new HashMap();
        host = s;
        port = s1;
        username = s2;
        password = s3;
        realm = s4;
        dir = s5;
        counters = as;
    }

    public void connect() throws ConnectionException {
        try {
            Properties properties = new Properties();
            if (System.getProperty("java.naming.factory.initial") != null) {
                properties.put("java.naming.factory.initial", System.getProperty("java.naming.factory.initial"));
            } else {
                properties.put("java.naming.factory.initial", "com.ibm.ejs.ns.jndi.CNInitialContextFactory");
            }
            properties.put("java.naming.provider.url", "iiop://" + host + ":" + port);
            initialContext = new InitialContext(properties);
            repositoryHome = com.ibm.ejs.sm.beans.Attributes.qualifyRepositoryHomeName(host, "");
            setCredentials();
            EpmServiceHome epmservicehome = (EpmServiceHome) lookup("EpmServiceHome");
            EpmService epmservice = epmservicehome.create();
            perfClient = new com.ibm.ejs.perf.client.PerfEpmClient(epmservice);
        } catch (Exception exception) {
            throw new ConnectionException(exception);
        }
    }

    private int getNumCounters() {
        if (counters == null) {
            return 0;
        } else {
            return counters.length;
        }
    }

    boolean setCredentials() {
        try {
            if (username != null && !username.equals("")) {
                com.ibm.CORBA.iiop.ORB orb = EJSORB.getORBInstance();
                Current current = (Current) orb.resolve_initial_references("SecurityCurrent");
                org.omg.SecurityLevel2.Credentials credentials = current.login_helper().request_login(username, realm, password, new CredentialsHolder(), new OpaqueHolder());
                current.set_credentials(CredentialType.SecInvocationCredentials, credentials);
            }
        } catch (InvalidName invalidname) {
            System.err.println("Invalid Name: " + invalidname);
            return false;
        } catch (LoginFailed loginfailed) {
            System.err.println("Login Failed: " + loginfailed);
            return false;
        } catch (InvalidCredentialType invalidcredentialtype) {
            System.err.println("Invalid Credential Type Failed: " + invalidcredentialtype);
            return false;
        } catch (InvalidCredential invalidcredential) {
            System.err.println("Invalid Credential: " + invalidcredential);
            return false;
        }
        return true;
    }

    Object lookup(String s) {
        Object object;
        try {
            object = (PortableRemoteObject.narrow(initialContext.lookup(repositoryHome + s), Class.forName("com.ibm.ejs.sm.beans." + s)));
        } catch (NamingException namingexception) {
            System.err.println("WebSphereMonitorEPM.lookup() NamingException: " + namingexception.getExplanation() + ". Resolved portion of name was: " + namingexception.getResolvedName() + ". Unresolved portion of name was: "
                    + namingexception.getRemainingName());
            namingexception.printStackTrace(System.err);
            System.err.println("WebSphereMonitorEPM.lookup() NamingException root cause: " + namingexception.getRootCause());
            namingexception.getRootCause().printStackTrace(System.err);
            return null;
        } catch (ClassNotFoundException classnotfoundexception) {
            System.err.println("ClassNotFound Exception: " + classnotfoundexception);
            return null;
        }
        return object;
    }

    public boolean getCounterList(StringBuffer stringbuffer) {
        WebSphereObject websphereobject = new WebSphereObject();
        com.ibm.ejs.sm.beans.NodeHome nodehome = (com.ibm.ejs.sm.beans.NodeHome) lookup("NodeHome");
        boolean bool;
        try {
            Enumeration enumeration = nodehome.findAll(false);
            while (enumeration.hasMoreElements()) {
                EJBObject ejbobject = (EJBObject) enumeration.nextElement();
                Long var_long = (Long) ejbobject.getPrimaryKey();
                com.ibm.ejs.sm.beans.Node node = nodehome.findByPrimaryKey(var_long);
                WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, node.getFullName().getLeafElement().getName(), node.getFullName().getLeafElement().getName(), "");
                addServers(websphereobject1, node);
            }
            websphereobject.toXML(stringbuffer, 0);
            bool = true;
        } catch (Exception exception) {
            System.err.println("Exception while getting counter list: " + exception);
            stringbuffer.append("Error collecting counters: " + exception);
            return false;
        }
        return bool;
    }

    private void addServers(WebSphereObject websphereobject, com.ibm.ejs.sm.beans.Node node) throws Exception {
        com.ibm.ejs.sm.beans.TypeHome typehome = (com.ibm.ejs.sm.beans.TypeHome) lookup("TypeHome");
        com.ibm.ejs.sm.beans.Type type = typehome.findByImplClass("com.ibm.ejs.sm.beans.EJBServerBean", true);
        com.ibm.ejs.sm.beans.EJBServerHome ejbserverhome = (com.ibm.ejs.sm.beans.EJBServerHome) lookup("EJBServerHome");
        for (Enumeration enumeration = node.listContainedObjects(type); enumeration.hasMoreElements();) {
            EJBObject ejbobject = (EJBObject) enumeration.nextElement();
            Long long1 = (Long) ejbobject.getPrimaryKey();
            try {
                com.ibm.ejs.sm.beans.EJBServer ejbserver = ejbserverhome.findByPrimaryKey(long1);
                WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, ejbserver.getFullName().getLeafElement().getName(), ejbserver.getFullName().getLeafElement().getName(), "");
                addGroups(websphereobject1, ejbserver);
            } catch (Exception exception) {
            }
        }

    }

    void addGroups(WebSphereObject websphereobject, com.ibm.ejs.sm.beans.Server server) throws Exception {
        PerfGroup perfgroup = (PerfGroup) perfClient.query(server, "AllEpmRoots");
        PerfValue aperfvalue[] = perfgroup.listAll();
        for (int i = 0; i < aperfvalue.length; i ++) {
            WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(aperfvalue[i].getName(), 46), aperfvalue[i].getName(), "");
            addMembers(server, websphereobject1, aperfvalue[i].getName());
        }

    }

    void addMembers(com.ibm.ejs.sm.beans.Server server, WebSphereObject websphereobject, String s) throws Exception {
        PerfGroup perfgroup = (PerfGroup) perfClient.query(server, s);
        PerfGroup perfgroup1 = (PerfGroup) perfClient.get(server, s);
        PerfClientDataGroupImpl perfclientdatagroupimpl = new PerfClientDataGroupImpl(perfgroup);
        PerfClientDataGroupImpl perfclientdatagroupimpl1 = new PerfClientDataGroupImpl(perfgroup1);
        PerfValue aperfvalue[] = perfclientdatagroupimpl.listAll();
        PerfValue aperfvalue1[] = perfclientdatagroupimpl1.listAll();
        for (int i = 0; i < aperfvalue.length; i ++) {
            if (aperfvalue[i] instanceof PerfGroup) {
                if (!s.equals(aperfvalue[i].getName())) {
                    WebSphereObject websphereobject1 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(aperfvalue[i].getName(), 46), aperfvalue[i].getName(), "");
                    addMembers(server, websphereobject1, aperfvalue[i].getName());
                }
                continue;
            }
            if (!(aperfvalue[i] instanceof PerfClientData)) {
                continue;
            }
            PerfClientData perfclientdata = (PerfClientData) aperfvalue[i];
            PerfClientData perfclientdata1 = (PerfClientData) aperfvalue1[i];
            PerfValue perfvalue = perfclientdata1.getCurrentValue();
            if (perfvalue instanceof PerfValueStat) {
                WebSphereObject websphereobject2 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(perfclientdata.getName(), 46), perfclientdata.getName(), perfclientdata.getDescription());
                websphereobject2.addCounter("Mean", "mean*", "Mean of the sample set");
                websphereobject2.addCounter("Count", "count*", "Element count");
                websphereobject2.addCounter("SumSquares", "sumSquares*", "Sum of squares of the elements");
                websphereobject2.addCounter("Variance", "variance*", "Variance");
                websphereobject2.addCounter("StandardDeviation", "standardDeviation*", "Standard Deviation");
                continue;
            }
            if (perfvalue instanceof PerfValueLoad) {
                WebSphereObject websphereobject3 = new WebSphereObject(websphereobject, WebSphereCounter.getLeafName(perfclientdata.getName(), 46), perfclientdata.getName(), perfclientdata.getDescription());
                websphereobject3.addCounter("Mean", "mean*", "Time-weighted average value");
                websphereobject3.addCounter("CurrentLevel", "getCurrentLevel*", "Last data point");
            } else {
                websphereobject.addCounter(WebSphereCounter.getLeafName(perfclientdata.getName(), 46), perfclientdata.getName(), perfclientdata.getDescription());
            }
        }

    }

    protected String[] getCounterValues() {
        String as[] = new String[counters.length];
        for (int i = 0; i < as.length; i ++) {
            try {
                StringBuffer stringbuffer = new StringBuffer();
                StringBuffer stringbuffer1 = new StringBuffer();
                StringBuffer stringbuffer2 = new StringBuffer();
                StringBuffer stringbuffer3 = new StringBuffer();
                StringBuffer stringbuffer4 = new StringBuffer();
                parseID(WebSphereCounter.denormalize(counters[i]), stringbuffer, stringbuffer1, stringbuffer2, stringbuffer3, stringbuffer4);
                com.ibm.ejs.sm.beans.Node node = getNode(stringbuffer.toString());
                com.ibm.ejs.sm.beans.Server server = getServer(node, stringbuffer1.toString());
                PerfGroup perfgroup = getGroup(server, stringbuffer2.toString());
                PerfValue perfvalue = getCounter(perfgroup, stringbuffer3.toString());
                if (perfvalue instanceof PerfStatInfo) {
                    PerfStatInfo perfstatinfo = (PerfStatInfo) perfvalue;
                    com.ibm.ejs.perf.client.StatInfo statinfo = perfstatinfo.valueStatInfo();
                    as[i] = String.valueOf(statinfo.getClass().getMethod(stringbuffer4.toString(), null).invoke(statinfo, null).toString());
                    continue;
                }
                if (perfvalue instanceof PerfLoad) {
                    PerfLoad perfload = (PerfLoad) perfvalue;
                    as[i] = String.valueOf(perfload.getClass().getMethod(stringbuffer4.toString(), null).invoke(perfload, null).toString());
                } else {
                    as[i] = perfValueToString(perfvalue);
                }
                continue;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            as[i] = "Error";
        }

        return as;
    }

    private void parseID(String s, StringBuffer stringbuffer, StringBuffer stringbuffer1, StringBuffer stringbuffer2, StringBuffer stringbuffer3, StringBuffer stringbuffer4) {
        StringTokenizer stringtokenizer = new StringTokenizer(s, "/");
        String as[] = new String[stringtokenizer.countTokens()];
        for (int i = 0; stringtokenizer.hasMoreElements(); i ++) {
            as[i] = stringtokenizer.nextToken();
        }

        int j = as.length - 1;
        if (as[j].endsWith("*")) {
            stringbuffer4.append(as[j].substring(0, as[j].length() - 1));
            j --;
        }
        stringbuffer3.append(as[j]);
        stringbuffer2.append(as[j - 1]);
        stringbuffer1.append(as[1]);
        stringbuffer.append(as[0]);
    }

    private com.ibm.ejs.sm.beans.Node getNode(String s) throws Exception {
        com.ibm.ejs.sm.beans.Node node = (com.ibm.ejs.sm.beans.Node) map.get(s);
        if (node == null) {
            com.ibm.ejs.sm.beans.NodeHome nodehome = (com.ibm.ejs.sm.beans.NodeHome) lookup("NodeHome");
            node = nodehome.findByName(s, false);
            if (node != null) {
                map.put(s, node);
            }
        }
        return node;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param node
     * @param s
     * @return
     * @throws Exception
     */
    private com.ibm.ejs.sm.beans.Server getServer(com.ibm.ejs.sm.beans.Node node, String s) throws Exception {
        Object obj = (com.ibm.ejs.sm.beans.Server) map.get(s);
        if (obj == null) {
            com.ibm.ejs.sm.beans.TypeHome typehome = (com.ibm.ejs.sm.beans.TypeHome) lookup("TypeHome");
            com.ibm.ejs.sm.beans.Type type = typehome.findByImplClass("com.ibm.ejs.sm.beans.EJBServerBean", true);
            com.ibm.ejs.sm.beans.EJBServerHome ejbserverhome = (com.ibm.ejs.sm.beans.EJBServerHome) lookup("EJBServerHome");
            Enumeration enumeration = node.listContainedObjects(type);
            while (enumeration.hasMoreElements()) {
                EJBObject ejbobject = (EJBObject) enumeration.nextElement();
                Long long1 = (Long) ejbobject.getPrimaryKey();
                try {
                    com.ibm.ejs.sm.beans.EJBServer ejbserver = ejbserverhome.findByPrimaryKey(long1);
                    if (!ejbserver.getFullName().getLeafElement().getName().equals(s)) {
                        continue;
                    }
                    obj = ejbserver;
                    break;
                } catch (Exception exception) {
                }
            }

            if (obj == null) {
                throw new Exception("Server " + s + " not found.");
            }
            map.put(s, obj);
        }
        return ((com.ibm.ejs.sm.beans.Server) (obj));
    }

    private PerfGroup getGroup(com.ibm.ejs.sm.beans.Server server, String s) throws Exception {
        String s1 = server.getFullName().toString() + s;
        PerfGroup perfgroup = (PerfGroup) map.get(s1);
        if (perfgroup == null) {
            perfgroup = (PerfGroup) perfClient.get(server, s);
            if (perfgroup != null) {
                map.put(s1, perfgroup);
            }
        }
        return perfgroup;
    }

    private PerfValue getCounter(PerfGroup perfgroup, String s) {
        PerfClientDataGroupImpl perfclientdatagroupimpl = new PerfClientDataGroupImpl(perfgroup);
        PerfValue aperfvalue[] = perfclientdatagroupimpl.listAll();
        for (int i = 0; i < aperfvalue.length; i ++) {
            if (!(aperfvalue[i] instanceof PerfClientData)) {
                continue;
            }
            PerfClientData perfclientdata = (PerfClientData) aperfvalue[i];
            String s1 = perfclientdata.getName();
            if (s1.equals(s)) {
                return perfclientdata.getCurrentValue();
            }
        }

        return null;
    }

    public static String perfValueToString(PerfValue perfvalue) {
        if (perfvalue == null) {
            return "n/a";
        }
        double d;
        if (perfvalue instanceof PerfDouble) {
            d = ((PerfDouble) perfvalue).valueDouble();
        } else if (perfvalue instanceof PerfInt) {
            d = ((PerfInt) perfvalue).valueInt();
        } else if (perfvalue instanceof PerfLong) {
            d = ((PerfLong) perfvalue).valueLong();
        } else {
            d = Double.valueOf(perfvalue.getValue().toString()).doubleValue();
        }
        return String.valueOf(d);
    }

    static WebSphereMonitorEPM createMonitorFromArgs(String as[]) throws IllegalArgumentException {
        if (as.length < 1) {
            throw new IllegalArgumentException("Usage: WebSphereMonitor [-username username] [-passwd passwd] [-port port] [-dir dir] hostname [counters...]");
        }
        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "900";
        String s4 = "";
        String s5 = "";
        int i;
        for (i = 0; i < as.length && as[i].startsWith("-"); i ++) {
            if (as[i].equals("-username")) {
                s = as[++ i];
            } else if (as[i].equals("-password")) {
                s1 = as[++ i];
            } else if (as[i].equals("-realm")) {
                s2 = as[++ i];
            } else if (as[i].equals("-port")) {
                s3 = as[++ i];
            } else if (as[i].equals("-dir")) {
                s4 = as[++ i];
            } else {
                throw new IllegalArgumentException(as[i] + " : " + "Usage: WebSphereMonitor [-username username] [-passwd passwd] [-port port] [-dir dir] hostname [counters...]");
            }
            if (i == as.length) {
                throw new IllegalArgumentException("Missing hostname : Usage: WebSphereMonitor [-username username] [-passwd passwd] [-port port] [-dir dir] hostname [counters...]");
            }
        }

        s5 = as[i ++];
        String as1[] = null;
        int j = as.length - i;
        if (j > 0) {
            as1 = new String[j];
            for (int k = 0; k < j; k ++) {
                as1[k] = as[i + k];
            }

        }
        return new WebSphereMonitorEPM(s5, s3, s, s1, s2, s4, as1);
    }

    public static void main(String args[]) {
        WebSphereMonitorEPM webspheremonitorepm = null;
        try {
            webspheremonitorepm = createMonitorFromArgs(args);
        } catch (IllegalArgumentException illegalargumentexception) {
            System.err.println("WebSphereMonitorEPM encountered IllegalArgumentException: " + illegalargumentexception);
            illegalargumentexception.printStackTrace();
            System.exit(1);
        }
        try {
            webspheremonitorepm.connect();
            if (webspheremonitorepm.counters == null) {
                StringBuffer stringbuffer = new StringBuffer();
                boolean flag = webspheremonitorepm.getCounterList(stringbuffer);
                System.out.print(stringbuffer);
                System.exit(flag ? 0 : 1);
            } else {
                String args1[] = webspheremonitorepm.getCounterValues();
                for (int i = 0; i < webspheremonitorepm.getNumCounters(); i ++) {
                    String s;
                    for (StringTokenizer stringtokenizer = new StringTokenizer(args1[i], "\r\n"); stringtokenizer.hasMoreTokens(); System.out.println(" " + s)) {
                        s = stringtokenizer.nextToken();
                    }

                    System.out.println(">");
                }

                System.out.println();
                System.exit(0);
            }
        } catch (ConnectionException connectionexception) {
            System.out.println("Failed to connect to WebSphere server " + webspheremonitorepm.host + ":" + webspheremonitorepm.port + ". Please check if the server is running and if the server and port settings are correct.");
            System.err.println("WebSphereMonitorEPM.main() encountered a ConnectionException: " + connectionexception);
            connectionexception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("WebSphere Monitor encountered an exception: " + exception);
            System.err.println("WebSphereMonitorEPM.main() encountered an exception: " + exception);
            exception.printStackTrace();
        }
        System.exit(1);
    }
}