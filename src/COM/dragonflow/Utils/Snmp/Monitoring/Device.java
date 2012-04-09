/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.Snmp.Monitoring;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.RealTimeReportingData;

// Referenced classes of package COM.dragonflow.Utils.Snmp.Monitoring:
// Metric

public class Device
{

    java.util.Vector metrics;
    private String identifier;
    private String displayName;
    private COM.dragonflow.Utils.Snmp.SNMPSession session;
    private java.util.Hashtable propertyNametoMetricLabel;
    private java.util.Hashtable RTPropertyNametoMetricLabel;
    private java.util.Hashtable RTPropertyNametoGraphLabel;
    private String initialRTPropertyValue;
    private long realTimeDataWindow;
    private int numRequests;
    static final boolean $assertionsDisabled; /* synthetic field */

    public Device(String s, String s1, java.util.Vector vector)
    {
        initialRTPropertyValue = "-1";
        numRequests = -1;
        identifier = s;
        displayName = s1;
        metrics = vector;
        propertyNametoMetricLabel = new Hashtable();
        RTPropertyNametoMetricLabel = new Hashtable();
        RTPropertyNametoGraphLabel = new Hashtable();
    }

    public void setRealTimeDataWindow(long l)
    {
        realTimeDataWindow = l;
    }

    public void setSession(COM.dragonflow.Utils.Snmp.SNMPSession snmpsession)
    {
        session = snmpsession;
        int i = metrics.size();
        for(int j = 0; j < i; j++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(j);
            metric.setSession(snmpsession);
        }

    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public boolean refreshMetrics(StringBuffer stringbuffer)
    {
        stringbuffer.setLength(0);
        for(int i = 0; i < metrics.size(); i++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(i);
            metric.refresh(stringbuffer);
            if(stringbuffer.length() > 0)
            {
                return false;
            }
        }

        return true;
    }

    public int populateRegularProperties(StringProperty astringproperty[], COM.dragonflow.SiteView.Monitor monitor)
    {
        int i = 0;
        int j = metrics.size();
        for(int k = 0; k < j && i < astringproperty.length; k++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(k);
            String as[] = metric.getValues();
            for(int l = 0; l < as.length && i < astringproperty.length; l++)
            {
                monitor.setProperty(astringproperty[i], as[l]);
                propertyNametoMetricLabel.put(astringproperty[i].getName(), metric.getLabel(l));
                i++;
            }

        }

        return i;
    }

    public java.util.Vector getRTDataFromProperty(String s)
    {
        return (java.util.Vector)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(s);
    }

    public int populateRTProperties(StringProperty astringproperty[], COM.dragonflow.SiteView.Monitor monitor, StringProperty stringproperty)
    {
        int i = 0;
        int j = metrics.size();
        java.util.HashMap hashmap = (java.util.HashMap)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(monitor.getProperty(stringproperty));
        if(hashmap == null)
        {
            hashmap = new HashMap();
        }
        java.util.ArrayList arraylist = new ArrayList();
        for(int k = 0; k < j && i < astringproperty.length; k++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(k);
            if(!metric.isRealTime())
            {
                continue;
            }
            String as[] = metric.getValues();
            arraylist.clear();
            for(int l = 0; l < as.length && i < astringproperty.length; l++)
            {
                if(metric.isOnSameGraph())
                {
                    arraylist.add(astringproperty[i].getName());
                }
                java.util.Vector vector;
                if(isNewRTProperty(monitor.getProperty(astringproperty[i])))
                {
                    vector = new Vector();
                } else
                {
                    vector = (java.util.Vector)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(monitor.getProperty(astringproperty[i]));
                    truncateDataOutsideWindow(vector);
                }
                COM.dragonflow.SiteView.RealTimeReportingData realtimereportingdata = new RealTimeReportingData(metric.getTime(), as[l]);
                vector.add(realtimereportingdata);
                monitor.setProperty(astringproperty[i], COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(vector, false));
                String s = astringproperty[i].getName();
                RTPropertyNametoMetricLabel.put(s, metric.getLabel(l));
                RTPropertyNametoGraphLabel.put(s, metric.getName());
                i++;
            }

            String as1[] = new String[arraylist.size()];
            arraylist.toArray(as1);
            for(int i1 = 0; i1 < as1.length; i1++)
            {
                hashmap.put(as1[i1], as1);
            }

        }

        monitor.setProperty(stringproperty, COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(hashmap, false));
        return i;
    }

    public StringProperty[] getPropertiesOnSameGraph(COM.dragonflow.SiteView.Monitor monitor, StringProperty stringproperty, StringProperty stringproperty1)
    {
        String s = monitor.getProperty(stringproperty1);
        java.util.HashMap hashmap;
        String as[];
        if((hashmap = (java.util.HashMap)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(s)) != null && (as = (String[])hashmap.get(stringproperty.getName())) != null)
        {
            StringProperty astringproperty[] = new StringProperty[as.length];
            for(int i = 0; i < as.length; i++)
            {
                StringProperty stringproperty2 = monitor.getPropertyObject(as[i]);
                if(stringproperty2 == null)
                {
                    COM.dragonflow.Log.LogManager.log("Error", "Device.getPropertiesOnSameGraph could not find property: " + as[i]);
                    return (new StringProperty[] {
                        stringproperty
                    });
                }
                astringproperty[i] = stringproperty2;
            }

            return astringproperty;
        } else
        {
            return (new StringProperty[] {
                stringproperty
            });
        }
    }

    private void truncateDataOutsideWindow(java.util.Vector vector)
    {
        if(vector == null)
        {
            return;
        }
        long l = (new Date()).getTime() / 1000L;
        java.util.ListIterator listiterator = vector.listIterator();
        do
        {
            if(!listiterator.hasNext())
            {
                break;
            }
            COM.dragonflow.SiteView.RealTimeReportingData realtimereportingdata = (COM.dragonflow.SiteView.RealTimeReportingData)listiterator.next();
            if(l - realtimereportingdata.getTime() <= realTimeDataWindow)
            {
                break;
            }
            listiterator.remove();
        } while(true);
    }

    private boolean isNewRTProperty(String s)
    {
        return s.equals(initialRTPropertyValue);
    }

    public void setInitialRTPropertyValue(String s)
    {
        initialRTPropertyValue = "-1";
    }

    public void updateRegularPropertyNameToLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        monitor.setProperty(stringproperty, COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(propertyNametoMetricLabel, false));
    }

    public void updateRTPropertyNameToLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        monitor.setProperty(stringproperty, COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(RTPropertyNametoMetricLabel, false));
    }

    public void updateRTPropertyNameToGraphLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        monitor.setProperty(stringproperty, COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(RTPropertyNametoGraphLabel, false));
    }

    public void setRegularyPropertyNameToLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        if(monitor.getProperty(stringproperty).length() > 0)
        {
            propertyNametoMetricLabel = (java.util.Hashtable)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(monitor.getProperty(stringproperty));
        }
    }

    public void setRTPropertyNameToLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        if(monitor.getProperty(stringproperty).length() > 0)
        {
            RTPropertyNametoMetricLabel = (java.util.Hashtable)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(monitor.getProperty(stringproperty));
        }
    }

    public void setRTPropertyNameToGraphLabelMap(StringProperty stringproperty, COM.dragonflow.SiteView.Monitor monitor)
    {
        if(monitor.getProperty(stringproperty).length() > 0)
        {
            RTPropertyNametoGraphLabel = (java.util.Hashtable)COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromStringBase64(monitor.getProperty(stringproperty));
        }
    }

    public String getLabelFromPropertyName(String s)
    {
        String s1 = (String)propertyNametoMetricLabel.get(s);
        if(s1 == null)
        {
            s1 = (String)RTPropertyNametoMetricLabel.get(s);
        }
        if(s1 == null)
        {
            s1 = "";
        }
        return s1;
    }

    public String getRTGraphLabel(String s)
    {
        String s1 = (String)RTPropertyNametoGraphLabel.get(s);
        if(s1 == null)
        {
            s1 = "";
        }
        return s1;
    }

    public int getNumRequests()
    {
        if(numRequests > 0)
        {
            return numRequests;
        }
        numRequests = 0;
        for(int i = 0; i < metrics.size(); i++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(i);
            numRequests += metric.getNumRequests();
        }

        return numRequests;
    }

    public int populateNamesAndIDs(StringProperty astringproperty[], StringProperty astringproperty1[], COM.dragonflow.SiteView.Monitor monitor, StringBuffer stringbuffer)
    {
        if(!$assertionsDisabled && astringproperty.length != astringproperty1.length)
        {
            throw new AssertionError();
        }
        stringbuffer.setLength(0);
        int i = 0;
        for(int j = 0; j < metrics.size() && i < astringproperty1.length; j++)
        {
            COM.dragonflow.Utils.Snmp.Monitoring.Metric metric = (COM.dragonflow.Utils.Snmp.Monitoring.Metric)metrics.get(j);
            String as[] = metric.getInstanceOIDs(stringbuffer);
            String as1[] = metric.getInstanceNames(stringbuffer);
            if(stringbuffer.length() > 0)
            {
                return i;
            }
            for(int k = 0; k < as.length && i < astringproperty1.length; k++)
            {
                monitor.setProperty(astringproperty1[i], as[k]);
                if(as1.length == as.length)
                {
                    monitor.setProperty(astringproperty[i], as1[k]);
                } else
                {
                    monitor.setProperty(astringproperty[i], as1[0]);
                }
                i++;
            }

        }

        return i;
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.Utils.Snmp.Monitoring.Device.class).desiredAssertionStatus();
    }
}
