/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * PDHMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>PDHMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
//import COM.dragonflow.MdrvMain.MdrvMain;
import COM.dragonflow.PDHMonitor.xdr.*;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.PDH.PDHRawCounterCache;
import COM.dragonflow.Utils.PDH.PDHSupport;
import COM.dragonflow.Utils.ArgsPackagerUtil;
import COM.dragonflow.Utils.TextUtils;

import java.io.IOException;
import jgl.Array;

public class PDHMonitor extends PerfmonMonitorBase
    implements IServerPropMonitor
{

    static ServerProperty pPdhServer;
    static StringProperty pPreviousCounters;

    public PDHMonitor()
    {
    }

    public boolean getAvailableCounters(String s, Object aobj[], StringBuffer stringbuffer)
    {
//        pdh_object_info pdh_object_info1 = new pdh_object_info();
//        String s1 = getProperty(pPdhServer);
//        COM.dragonflow.PDHMonitor.xdr.pdh_connect pdh_connect = PDHSupport.getXdrHost(s1);
//        if(!PDHSupport.sendPdhRequest(2, new pdh_get_object_info(pdh_connect, s), pdh_object_info1, stringbuffer))
//        {
//            return false;
//        }
//        pdh_counter_info_seq pdh_counter_info_seq1 = pdh_object_info1.get_counters();
//        StringProperty astringproperty[] = new StringProperty[pdh_counter_info_seq1.size()];
//        for(int i = 0; i < pdh_counter_info_seq1.size(); i++)
//        {
//            StringProperty stringproperty = new StringProperty(pdh_counter_info_seq1.get(i).get_counter());
//            stringproperty.setDisplayText(pdh_counter_info_seq1.get(i).get_counter(), pdh_counter_info_seq1.get(i).get_description());
//            astringproperty[i] = stringproperty;
//        }

//        aobj[0] = astringproperty;
        return true;
    }

    public boolean getAvailableInstances(String s, Object aobj[], StringBuffer stringbuffer)
    {
//        pdh_object_info pdh_object_info1 = new pdh_object_info();
//        String s1 = getProperty(pPdhServer);
//        COM.dragonflow.PDHMonitor.xdr.pdh_connect pdh_connect = PDHSupport.getXdrHost(s1);
//        if(!PDHSupport.sendPdhRequest(2, new pdh_get_object_info(pdh_connect, s), pdh_object_info1, stringbuffer))
//        {
//            return false;
//        }
//        pdh_string_seq pdh_string_seq1 = pdh_object_info1.get_instances();
//        StringProperty astringproperty[] = new StringProperty[pdh_string_seq1.size()];
//        for(int i = 0; i < pdh_string_seq1.size(); i++)
//        {
//            astringproperty[i] = new StringProperty(pdh_string_seq1.get(i).get_strVal(), "", pdh_string_seq1.get(i).get_strVal());
//        }
//
//        aobj[0] = astringproperty;
        return true;
    }

    public boolean getAvailableObjects(Object aobj[], StringBuffer stringbuffer)
    {
//        pdh_object_list pdh_object_list1 = new pdh_object_list();
//        String s = getProperty(pPdhServer);
//        COM.dragonflow.PDHMonitor.xdr.pdh_connect pdh_connect = PDHSupport.getXdrHost(s);
//        if(!PDHSupport.sendPdhRequest(1, new pdh_get_objects(pdh_connect), pdh_object_list1, stringbuffer))
//        {
//            return false;
//        }
//        StringProperty astringproperty[] = new StringProperty[pdh_object_list1.get_objects_list().size()];
//        for(int i = 0; i < pdh_object_list1.get_objects_list().size(); i++)
//        {
//            astringproperty[i] = new StringProperty(pdh_object_list1.get_objects_list().get(i).get_strVal(), "", pdh_object_list1.get_objects_list().get(i).get_strVal());
//        }
//
//        aobj[0] = astringproperty;
        return true;
    }

    public String getHostname()
    {
        return getProperty(pPdhServer);
    }

    public StringProperty getServerProperty()
    {
        return pPdhServer;
    }

    public boolean remoteCommandLineAllowed()
    {
        return false;
    }

    protected void startMonitor()
    {
        ServerMonitor.addRemote(this);
        super.startMonitor();
    }

    protected void stopMonitor()
    {
        ServerMonitor.removeRemote(this);
        super.stopMonitor();
    }

    public Array getPropertiesToPassBetweenPages(HTTPRequest httprequest)
    {
        Array array = super.getPropertiesToPassBetweenPages(httprequest);
        array.add(pPdhServer);
        return array;
    }

    protected boolean update()
    {
        PDHRawCounterCache pdhrawcountercache = (PDHRawCounterCache)getPropertyAsObject(pPreviousCounters);
        boolean flag = true;
        boolean flag1 = true;
        if(getFullID().equals("1"))
        {
            flag1 = false;
        }
        StringProperty astringproperty[] = getPerfmonMeasurements();
        String s = getProperty(pPdhServer);
        if(pdhrawcountercache == null)
        {
            flag = false;
        } else
        {
            String as[] = new String[astringproperty.length];
            for(int i = 0; i < astringproperty.length; i++)
            {
                as[i] = astringproperty[i].getLabel();
            }

            flag = pdhrawcountercache.checkValid(s, as);
        }
        setProperty(pCountersInError, astringproperty.length);
//        pdh_measurement_seq pdh_measurement_seq1 = new pdh_measurement_seq();
//        for(int j = 0; astringproperty != null && j < astringproperty.length; j++)
//        {
//            astringproperty[j].setValue("n/a");
//            String as1[] = ArgsPackagerUtil.unpackageArgsToStrArray(astringproperty[j].getLabel());
//            pdh_raw_counter pdh_raw_counter1 = null;
//            if(flag)
//            {
//                pdh_raw_counter1 = pdhrawcountercache.get(j);
//            }
//            if(pdh_raw_counter1 == null)
//            {
//                pdh_raw_counter1 = new pdh_raw_counter(1, "", "", "", 0);
//            }
//            pdh_measurement_seq1.add(new pdh_measurement(as1[0], as1[1], as1[2], 0.0D, false, pdh_raw_counter1));
//        }
//
//        pdh_measurement_seq pdh_measurement_seq2 = new pdh_measurement_seq();
//        COM.dragonflow.PDHMonitor.xdr.pdh_connect pdh_connect = PDHSupport.getXdrHost(s);
//        StringBuffer stringbuffer = new StringBuffer();
//        char c = '\u03E8';
//        if(!PDHSupport.sendPdhRequest(3, new pdh_run_measurement(getFullID(), flag1, flag, c, pdh_connect, pdh_measurement_seq1), pdh_measurement_seq2, stringbuffer))
//        {
//            setProperty(pStateString, stringbuffer.toString());
//            setProperty(pNoData, "n/a");
//            setProperty(pPreviousCounters, "");
//            return true;
//        }
//        if(flag1)
//        {
//            pdhrawcountercache = new PDHRawCounterCache(s);
//        }
//        int k = 0;
//        for(int l = 0; l < pdh_measurement_seq2.size(); l++)
//        {
//            StringProperty stringproperty = astringproperty[l];
//            stringproperty.setValue("n/a");
//            if(pdh_measurement_seq2.get(l).get_resultStatus())
//            {
//                double d = pdh_measurement_seq2.get(l).get_resultValue();
//                stringproperty.setValue(TextUtils.floatToString((float)d, 2));
//            } else
//            {
//                k++;
//            }
//            if(flag1)
//            {
//                pdh_raw_counter pdh_raw_counter2 = pdh_measurement_seq2.get(l).get_rawCounter();
//                pdhrawcountercache.put(stringproperty.getLabel(), pdh_raw_counter2);
//            }
//        }
//
//        updateValues();
//        setProperty(pCountersInError, k);
//        if(flag1)
//        {
//            try
//            {
//                setPropertyWithObject(pPreviousCounters, pdhrawcountercache);
//            }
//            catch(IOException ioexception)
//            {
//                LogManager.logException(ioexception);
//            }
//        }
//        currentStatus = "set stateString...";
//        StringBuffer stringbuffer1 = new StringBuffer();
//        for(int i1 = 0; astringproperty != null && i1 < astringproperty.length; i1++)
//        {
//            stringbuffer1.append(getMsmtLabel(astringproperty[i1]) + "=" + astringproperty[i1].getValue());
//            if(i1 < astringproperty.length - 1)
//            {
//                stringbuffer1.append(", ");
//            }
//        }
//
//        stringbuffer1.append(stringbuffer);
//        setProperty(pStateString, stringbuffer1.toString());
        return true;
    }

    public boolean isMultiThreshold()
    {
        return true;
    }

    static 
    {
        pPdhServer = null;
        pPreviousCounters = null;
        String s = (COM.dragonflow.StandardMonitor.PDHMonitor.class).getName();
        pPdhServer = new ServerProperty("_pdhMachine", "");
        pPdhServer.setParameterOptions(true, 1, false);
        pPdhServer.setDisplayText("Server", "the server to monitor");
        pPreviousCounters = new StringProperty("previousCounters");
        addProperties(s, new StringProperty[] {
            pPdhServer, pPreviousCounters
        });
        addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors performance data of Windows servers using the Performance Data Helper (PDH) library");
        setClassProperty(s, "title", "Windows Resources");
        setClassProperty(s, "help", "PDHMonitor.htm");
        setClassProperty(s, "class", "PDHMonitor");
        setClassProperty(s, "classType", "server");
//        if(!Platform.isWindows() || !COM.dragonflow.MdrvMain.MdrvMain.Settings.shouldLoadMdrv())
//        {
//            setClassProperty(s, "loadable", "false");
//        }
    }
}
