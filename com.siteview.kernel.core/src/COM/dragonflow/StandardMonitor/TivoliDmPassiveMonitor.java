/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * TivoliDmPassiveMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>TivoliDmPassiveMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BrowsableProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.BrowsableMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.TextUtils;

public class TivoliDmPassiveMonitor extends AtomicMonitor
    implements BrowsableMonitor//, AbstractPassiveMonitor
{

    private boolean started_;
    private String tecServerName_;
    private int tecServerPort_;
    private boolean showUnits_;
    private boolean showTimestamp_;
//    private LogHelper logger_;
//    private AbstractDataStore store_;
//    private TecServerProperties tec_;
    StringBuffer browseDataStr_;
    StringBuffer statusStr_;
//    private TecDmEventSubscriber subscriber_;
    private int selectedCounters_;
//    private DoubledVector availServers_;
    private Vector events_;
    private final Array connProperties_ = new Array();
    private static String VALUE_PREFIX;
    private static final String NAME_PROPERTY = "_browseName";
    private static final String ID_PROPERTY = "_browseNameid";
    private static final String NO_SERVERS = "There're no available servers yet";
    private static BrowsableProperty browseCounters_;
    private static ScalarProperty server_;
    private static StringProperty state_;
    private static StringProperty countersInError_;
    private static StringProperty tecServer_;
    private static StringProperty connInitialized_;
    private static StringProperty property_[];
    private static int nMaxCounters;
    private static final int LOW_COUNTER_NAMES_BOUND = 6;
    private static final int HIGH_COUNTER_NAMES_BOUND;
    private static final int LOW_COUNTER_IDS_BOUND;
    private static final int HIGH_COUNTER_IDS_BOUND;
    private static final int LOW_COUNTER_VALUES_BOUND;
    private static final int HIGH_COUNTER_VALUES_BOUND;
    private static final String COMPONENT_NAME = "Monitor";
    private static DecimalFormat valueFormatter = new DecimalFormat("#.##");

    public TivoliDmPassiveMonitor()
    {
        started_ = false;
        tecServerName_ = null;
        tecServerPort_ = 0;
        showUnits_ = false;
        showTimestamp_ = false;
//        logger_ = null;
//        store_ = null;
//        tec_ = null;
        browseDataStr_ = new StringBuffer();
        statusStr_ = new StringBuffer();
//        subscriber_ = null;
        selectedCounters_ = 0;
//        availServers_ = new DoubledVector();
        events_ = new Vector(nMaxCounters);
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(MasterConfig.getMasterConfig(), "_DispatcherMaxCounters"));
        if(nMaxCounters <= 0)
        {
            nMaxCounters = 30;
        }
//        store_ = TecDmEventStore.getInstance();
//        subscriber_ = new TecDmEventSubscriber();
//        connProperties_.add(server_);
//        if(null == store_)
//        {
//            throw new IllegalStateException("Unable to obtain Tivoli event data store object");
//        } else
//        {
//            Config cfg = Config.getInstance();
//            logger_ = LogPolicyFactory.getInstance("Tivoli").getLogger("Monitor");
//            String showUnits = cfg.getProperty("show_units_in_status");
//            String showTimestamp = cfg.getProperty("show_timestamp_in_status");
//            showUnits_ = "1".equals(showUnits) || "true".equals(showUnits) || "TRUE".equals(showUnits);
//            showTimestamp_ = "1".equals(showTimestamp) || "true".equals(showTimestamp) || "TRUE".equals(showTimestamp);
//            return;
//        }
    }

    protected void stopMonitor()
    {
//        logger_.debug("==> TivoliDmPassiveMonitor::stopMonitor");
//        subscriber_.stop();
//        try
//        {
//            if(null != tec_)
//            {
//                logger_.debug(" stop ems source");
//                TecDmEventStore.release(tec_);
//            }
//        }
//        catch(IOException e)
//        {
//            logger_.error(e);
//        }
//        finally
//        {
//            tec_ = null;
//        }
//        super.stopMonitor();
//        selectedCounters_ = 0;
//        logger_.debug("<== TivoliDmPassiveMonitor::stopMonitor");
    }

    private StringBuffer initiateTecConnections(StringBuffer sb)
    {
//        logger_.debug("==> initiateTecConnections ");
//        sb = null != sb ? sb : new StringBuffer();
//        if(TecDmEventStore.serversCnt() > 0)
//        {
//            parseTecServerInput();
//            try
//            {
//                if(null != tecServerName_ && !"".equals(tecServerName_))
//                {
//                    logger_.debug("initiate Tec Connection for server " + tecServerName_ + ':' + tecServerPort_);
//                    tec_ = TecDmEventStore.initTecConnection(tecServerName_, tecServerPort_);
//                    setProperty(connInitialized_, 1);
//                    sb.append("Connection request to TEC server ").append(tecServerName_).append(" successfully posted. ");
//                } else
//                {
//                    tec_ = null;
//                    setProperty(connInitialized_, 0);
//                    sb.append("No connection to initialize. ");
//                }
//                subscriber_.start(getProperty(server_), new MonitorPropertyArrayEnum(this, property_, LOW_COUNTER_IDS_BOUND, LOW_COUNTER_IDS_BOUND + selectedCounters_));
//            }
//            catch(SocketTimeoutException ex)
//            {
//                sb.append("Unable to initialize connection to TEC server. Portmaper on host ").append(tecServerName_).append(" doesn't respond. ");
//                logger_.error(new String(sb), ex);
//                setProperty(connInitialized_, -1);
//            }
//            catch(Throwable ex)
//            {
//                sb.append("Unable to initialize connection to TEC server ").append(tecServerName_).append(", because ").append(ex.getMessage()).append(". ");
//                logger_.error(new String(sb), ex);
//                setProperty(connInitialized_, -1);
//            }
//        } else
//        {
//            setProperty(connInitialized_, -1);
//            sb.append("There are no active TEC event listeners. Check the error.log file for TEC event listener creation errors and change TEC event listener configuration in the ems/Tivoli/tivoli_dm.config file.");
//        }
//        logger_.debug("<== initiateTecConnections");
        return sb;
    }

    private void parseTecServerInput()
    {
        String tecServer = getProperty(tecServer_);
        if(null != tecServer && !"".equals(tecServer))
        {
            int colonIdx = tecServer.indexOf(':');
            String tecPortStr = -1 == colonIdx ? null : tecServer.substring(colonIdx + 1);
            tecServerName_ = -1 == colonIdx ? tecServer : tecServer.substring(0, colonIdx);
            tecServerPort_ = null == tecPortStr || "".equals(tecPortStr) ? -1 : TextUtils.toInt(tecPortStr);
            tecServerPort_ = tecServerPort_ > 0 ? tecServerPort_ : -1;
        }
    }

    protected void startMonitor()
    {
//        logger_.debug("==> startMonitor");
        start();
        super.startMonitor();
//        logger_.debug("<== startMonitor");
    }

    private void start()
    {
        if(!started_)
        {
            setProperty(connInitialized_, -1);
            selectedCounters_ = selectedCounters();
            started_ = true;
        }
    }

    public Array getConnectionProperties()
    {
        return connProperties_;
    }

    public String getBrowseData(StringBuffer errors)
    {
        String serverName = getProperty(server_);
        browseDataStr_.delete(0, browseDataStr_.length());
        if(null != serverName || "".equals(serverName))
        {
            events_.clear();
//            int cnt = store_.getHostEvents(serverName, events_);
//            logger_.debug("There're " + cnt + "(" + events_.size() + ") counters for server " + serverName);
//            subscriber_.build(browseDataStr_, events_, cnt);
//            if(cnt <= 0)
//            {
//                errors.append("There're no available counters for the selected server.");
//            }
        } else
        {
            errors.append("Illegal monitor state: server name is not selected");
        }
//        logger_.debug("***************************************");
//        logger_.debug(browseDataStr_.toString());
//        logger_.debug("***************************************");
        return browseDataStr_.toString();
    }

    public String GetPropertyLabel(StringProperty p, boolean forcePretty)
    {
        if(p.getName().startsWith(VALUE_PREFIX))
        {
            int index = TextUtils.toInt(p.getName().substring(VALUE_PREFIX.length()));
            return getProperty("_browseName" + index);
        } else
        {
            return p.printString();
        }
    }

    public String getPropertyName(StringProperty p)
    {
        if(p.getName().startsWith(VALUE_PREFIX))
        {
            int index = TextUtils.toInt(p.getName().substring(VALUE_PREFIX.length()));
            return getProperty("_browseName" + index);
        } else
        {
            return super.getPropertyName(p);
        }
    }

    public int getMaxCounters()
    {
        return nMaxCounters;
    }

    public String getBrowseName()
    {
        return "_browseName";
    }

    public String getBrowseID()
    {
        return "_browseNameid";
    }

//    public String setBrowseName(Array ids)
//    {
//        return subscriber_.nameFromComponents(ids);
//    }
//
//    public String setBrowseID(Array ids)
//    {
//        return subscriber_.idFromComponents(ids);
//    }

    public String getHostname()
    {
        String rc = getProperty(server_);
        return null != rc ? rc : "Please select host name";
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
    {
        Vector rc = null;
        if(server_ == scalarproperty)
        {
//            availServers_.clear();
//            store_.availHosts(availServers_);
//            rc = availServers_;
            return rc;
        }
        Vector vValues = null;
        try
        {
            vValues = super.getScalarValues(scalarproperty, httprequest, cgi);
        }
        catch(SiteViewException e)
        {
//            logger_.error("Exception " + e);
        }
        return vValues;
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors)
    {
//        logger_.debug("==> verify " + property + "\t:\t" + value);
        String rc = value;
        if(property == server_)
        {
            rc = value;
            if(null == rc || "".equals(rc))
            {
//                logger_.debug("is about to set error");
                errors.put(property, "There're no available servers yet");
            }
        } else
        if(property == browseCounters_)
        {
            String serverName = getProperty(server_);
            int selectedCnt = selectedCounters();
//            logger_.debug("Server name " + serverName + ". There're " + selectedCnt + " selected counters");
            if(null == serverName || "".equals(serverName.trim()))
            {
                errors.put(browseCounters_, "Server is not selected");
            } else
            if(0 >= selectedCnt)
            {
                errors.put(browseCounters_, "No counters selected for server " + serverName);
            }
        } else
        {
            rc = super.verify(property, value, request, errors);
        }
//        logger_.debug("verify called with property: " + property.toString() + " and value:  '" + value + "'" + " size of errors is " + errors.size());
//        logger_.debug("<== verify ");
        return rc;
    }

    public Array getLogProperties()
    {
//        logger_.debug("==> TivoliDmPassiveMonitor::getLogProperties******************");
        Array logProperties = super.getLogProperties();
        for(int i = 0; i < selectedCounters_; i++)
        {
            logProperties.add(property_[LOW_COUNTER_VALUES_BOUND + i]);
        }

//        logger_.debug("<== TivoliDmPassiveMonitor::getLogProperties******************");
        return logProperties;
    }

    public Enumeration getStatePropertyObjects(boolean detailed)
    {
        start();
//        logger_.debug("==> TivoliDmPassiveMonitor::getStatePropertyObjects");
        Array result = new Array();
        result.add(countersInError_);
//        logger_.debug("sending " + selectedCounters_ + " counters");
        for(int i = 0; i < selectedCounters_; i++)
        {
            if(property_[LOW_COUNTER_VALUES_BOUND + i] != null)
            {
//                logger_.debug(property_[LOW_COUNTER_VALUES_BOUND + i].getName() + "=" + property_[LOW_COUNTER_VALUES_BOUND + i].getValue());
                result.add(property_[LOW_COUNTER_VALUES_BOUND + i]);
            }
        }

//        logger_.debug("<== TivoliDmPassiveMonitor::getStatePropertyObjects");
        return (Enumeration) result.iterator();
    }

    public boolean isServerBased()
    {
        return true;
    }

    protected boolean update()
    {
//        logger_.debug("==> TivoliDmPassiveMonitor::update ");
        statusStr_.delete(0, statusStr_.length());
        setProperty(countersInError_, 0);
        if(-1 == getPropertyAsInteger(connInitialized_))
        {
            initiateTecConnections(statusStr_);
        }
//        subscriber_.getValues(this);
        setProperty(Monitor.pStateString, statusStr_.toString());
//        logger_.debug(statusStr_.toString());
//        logger_.debug("<== TivoliDmPassiveMonitor::update ");
        return true;
    }

    private int selectedCounters()
    {
        boolean finished = false;
        int i = 0;
        for(i = 0; !finished && i < getMaxCounters(); i++)
        {
            String name = getProperty(property_[6 + i]);
            finished = null == name || 0 == name.length();
            if(!finished)
            {
//                logger_.debug("Selected name: " + name);
            }
        }

        if(i == 0)
        {
//            logger_.warning("no counters selected");
        }
        return i - 1;
    }

    public boolean isUsingCountersCache()
    {
        return false;
    }

    public String getTopazCounterLabel(StringProperty prop)
    {
        return GetPropertyLabel(prop, true);
    }

//    public void setValue(int idx, AbstractEvent event)
//    {
//        String value = null == event ? null : event.getValue("value");
//        String timestamp = null == event ? null : event.getValue("date");
//        String units = null == event ? null : event.getValue("units");
//        if(null != value && null != timestamp && idx < selectedCounters_ && idx >= 0)
//        {
//            String server = getProperty(server_);
//            String name = getProperty(property_[6 + idx]);
//            logger_.debug("==> build state for " + name + " " + value + " timestamp " + timestamp);
//            try
//            {
//                value = valueFormatter.format(valueFormatter.parseObject(value));
//            }
//            catch(NumberFormatException e) { }
//            catch(ParseException e) { }
//            setProperty(property_[LOW_COUNTER_VALUES_BOUND + idx], value.replace('e', 'E'));
//            if(statusStr_.length() > 0)
//            {
//                statusStr_.append(", ");
//            }
//            statusStr_.append(name).append("=").append(value);
//            if(showUnits_)
//            {
//                statusStr_.append(" ").append(units);
//            }
//            if(showTimestamp_)
//            {
//                statusStr_.append(" at ").append(timestamp);
//            }
//            if("n/a".equals(value))
//            {
//                setProperty(countersInError_, getPropertyAsLong(countersInError_) + 1L);
//            }
//        }
//    }

    public boolean manageBrowsableSelectionsByID()
    {
        return false;
    }

    public boolean areBrowseIDsEqual(String arg0, String arg1)
    {
        return false;
    }

    public void setMaxCounters(int maxCounters)
    {
        nMaxCounters = maxCounters;
        HashMap config = MasterConfig.getMasterConfig();
        config.put("_DispatcherMaxCounters", (new Integer(maxCounters)).toString());
        MasterConfig.saveMasterConfig(config);
    }

    static 
    {
        VALUE_PREFIX = "_browsableValue";
        property_ = null;
        nMaxCounters = 30;
        HIGH_COUNTER_NAMES_BOUND = (nMaxCounters + 6) - 1;
        LOW_COUNTER_IDS_BOUND = HIGH_COUNTER_NAMES_BOUND + 1;
        HIGH_COUNTER_IDS_BOUND = (LOW_COUNTER_IDS_BOUND + nMaxCounters) - 1;
        LOW_COUNTER_VALUES_BOUND = HIGH_COUNTER_IDS_BOUND + 1;
        HIGH_COUNTER_VALUES_BOUND = (LOW_COUNTER_VALUES_BOUND + nMaxCounters) - 1;
        try
        {
            browseCounters_ = new BrowsableProperty("_browse", "browseName");
            browseCounters_.setDisplayText("Counters", "Current selection of counters.");
            browseCounters_.setParameterOptions(true, 1, false);
            server_ = new ScalarProperty("_server", "");
            server_.setDisplayText("Server", "the name of the server");
            server_.setParameterOptions(false, 4, false);
            state_ = new NumericProperty("TivoliDmStateProperty", "");
            state_.setLabel("TivoliDm state property");
            state_.setStateOptions(1);
            countersInError_ = new NumericProperty("countersInError");
            countersInError_.setLabel("counters in error");
            countersInError_.setStateOptions(nMaxCounters + 1);
            countersInError_.setIsThreshold(true);
            tecServer_ = new StringProperty("_tecServer", "");
            tecServer_.setDisplayText("TEC Server", "TEC server to initiate connection with in form host:port (optional)");
            tecServer_.setParameterOptions(true, 2, true);
            connInitialized_ = new NumericProperty("tecConnStatus");
            StringProperty browsableProps[] = BrowsableBase.staticInitializer(nMaxCounters, false);
            property_ = new StringProperty[HIGH_COUNTER_VALUES_BOUND + 1];
            property_[0] = browseCounters_;
            property_[1] = server_;
            property_[2] = state_;
            property_[3] = countersInError_;
            property_[4] = tecServer_;
            property_[5] = connInitialized_;
            for(int i = 0; i < nMaxCounters; i++)
            {
                property_[6 + i] = new StringProperty("_browseName" + (i + 1));
                property_[6 + i].setDisplayText("Counter " + (i + 1) + " Name", "Topaz Counter Name");
                property_[6 + i].setParameterOptions(false, i + 4, false);
                property_[LOW_COUNTER_IDS_BOUND + i] = new StringProperty("_browseNameid" + (i + 1));
                property_[LOW_COUNTER_IDS_BOUND + i].setDisplayText("Counter " + (i + 1) + " ID", "Topaz Counter ID");
                property_[LOW_COUNTER_IDS_BOUND + i].setParameterOptions(false, nMaxCounters + i + 4, false);
                property_[LOW_COUNTER_VALUES_BOUND + i] = new NumericProperty(VALUE_PREFIX + (i + 1));
                property_[LOW_COUNTER_VALUES_BOUND + i].setDisplayText("Counter " + (i + 1) + " Value", "Topaz Counter Value");
                property_[LOW_COUNTER_VALUES_BOUND + i].setStateOptions(i + 1);
                property_[LOW_COUNTER_VALUES_BOUND + i].setIsThreshold(true);
            }

//            Config cfg = Config.getInstance();
            String fullClassName = (COM.dragonflow.StandardMonitor.TivoliDmPassiveMonitor.class).getName();
            StringProperty allProps[] = new StringProperty[property_.length + browsableProps.length];
            System.arraycopy(property_, 0, allProps, 0, property_.length);
            System.arraycopy(browsableProps, 0, allProps, property_.length, browsableProps.length);
            PropertiedObject.addProperties(fullClassName, allProps);
            PropertiedObject.addProperties(fullClassName, property_);
            SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("tecConnStatus == -1\terror"));
            SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("always\tgood"));
            PropertiedObject.setClassProperty(fullClassName, "description", "description of TivoliDmPassiveMonitor");
            PropertiedObject.setClassProperty(fullClassName, "help", "TivoliDM_Passive.htm");
            PropertiedObject.setClassProperty(fullClassName, "title", "Tivoli DM (Passive)");
            PropertiedObject.setClassProperty(fullClassName, "class", "TivoliDmPassiveMonitor");
            boolean isEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
//            PropertiedObject.setClassProperty(fullClassName, "loadable", !isEmsLicensed || !"true".equals(cfg.getProperty("enable_monitor")) ? "false" : "true");
            PropertiedObject.setClassProperty(fullClassName, "toolName", "TEC Event History");
            PropertiedObject.setClassProperty(fullClassName, "toolDescription", "Show recent TEC events received by SiteView.");
            PropertiedObject.setClassProperty(fullClassName, "classType", "beta");
            SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("countersInError == " + nMaxCounters + "\terror"));
            SiteViewObject.addClassElement(fullClassName, Rule.stringToClassifier("always\tgood"));
            PropertiedObject.setClassProperty(fullClassName, "target", "_server");
            PropertiedObject.setClassProperty(fullClassName, "topazName", "Tivoli DM (Passive)");
            PropertiedObject.setClassProperty(fullClassName, "topazType", "System Resources");
        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
        }
    }

	public String setBrowseName(Array array) {
		// TODO Auto-generated method stub
		return null;
	}

	public String setBrowseID(Array array) {
		// TODO Auto-generated method stub
		return null;
	}
}
