/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils.Snmp.Monitoring;

import com.recursionsw.jgl.HashMap;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.PercentProperty;
import com.dragonflow.Properties.RateProperty;
import com.dragonflow.Properties.StringProperty;

// Referenced classes of package com.dragonflow.Utils.Snmp.Monitoring:
// IFTable

public class NetInterface
{

    private String name;
    private String desc;
    private String alias;
    private String physAddress;
    private int row;
    private boolean mapRowToIfTableIndex;
    private long inOctets;
    private long lastInOctets;
    private long outOctets;
    private long lastOutOctets;
    private long speed;
    private long status;
    private long inUnicastPackets;
    private long lastInUnicastPackets;
    private long outUnicastPackets;
    private long lastOutUnicastPackets;
    private long inDiscards;
    private long lastInDiscards;
    private long outDiscards;
    private long lastOutDiscards;
    private long inErrors;
    private long lastInErrors;
    private long outErrors;
    private long lastOutErrors;
    private long outQLen;
    private long time;
    private long lastTime;
    private String duplex;
    private int indexMethod;
    public static final String HALF_DUPLEX = "half-duplex";
    public static final String FULL_DUPLEX = "full-duplex";
    public static final int INDEX_BY_DESC = 0;
    public static final int INDEX_BY_PHYS_ADDR = 1;
    public static final int INDEX_BY_INDEX = 2;
    public static final int INDEX_BY_NAME = 3;
    public static final int RATE_PROPERTY = 1;
    public static final int NUMERIC_PROPERTY = 2;
    public static final int PERCENT_PROPERTY = 3;
    public static final int STRING_PROPERTY = 4;
    public static final String IN_OCTETS_PROP_NAME = "ifInOctets";
    public static final int IN_OCTETS_PROP_TYPE = 2;
    public static final String TOTAL_IN_OCTETS_PROP_NAME = "totalIfInOctets";
    public static final int TOTAL_IN_OCTETS_PROP_TYPE = 2;
    public static final String OUT_OCTETS_PROP_NAME = "ifOutOctets";
    public static final int OUT_OCTETS_PROP_TYPE = 2;
    public static final String TOTAL_OUT_OCTETS_PROP_NAME = "totalIfOutOctets";
    public static final int TOTAL_OUT_OCTETS_PROP_TYPE = 2;
    public static final String SPEED_PROP_NAME = "ifSpeed";
    public static final int SPEED_PROP_TYPE = 1;
    public static final String IN_UCAST_PKT_PROP_NAME = "ifInUcastPkts";
    public static final int IN_UCAST_PKT_PROP_TYPE = 2;
    public static final String TOTAL_IN_UCAST_PKT_PROP_NAME = "totalIfInUcastPkts";
    public static final int TOTAL_IN_UCAST_PKT_PROP_TYPE = 2;
    public static final String OUT_UCAST_PKT_PROP_NAME = "ifOutUcastPkts";
    public static final int OUT_UCAST_PKT_PROP_TYPE = 2;
    public static final String TOTAL_OUT_UCAST_PKT_PROP_NAME = "totalIfOutUcastPkts";
    public static final int TOTAL_OUT_UCAST_PKT_PROP_TYPE = 2;
    public static final String IN_DISCARDS_PROP_NAME = "ifInDiscards";
    public static final int IN_DISCARDS_PROP_TYPE = 2;
    public static final String TOTAL_IN_DISCARDS_PROP_NAME = "totalIfInDiscards";
    public static final int TOTAL_IN_DISCARDS_PROP_TYPE = 2;
    public static final String OUT_DISCARDS_PROP_NAME = "ifOutDiscards";
    public static final int OUT_DISCARDS_PROP_TYPE = 2;
    public static final String TOTAL_OUT_DISCARDS_PROP_NAME = "totalIfOutDiscards";
    public static final int TOTAL_OUT_DISCARDS_PROP_TYPE = 2;
    public static final String IN_ERRORS_PROP_NAME = "ifInErrors";
    public static final int IN_ERRORS_PROP_TYPE = 2;
    public static final String TOTAL_IN_ERRORS_PROP_NAME = "totalIfInErrors";
    public static final int TOTAL_IN_ERRORS_PROP_TYPE = 2;
    public static final String OUT_ERRORS_PROP_NAME = "ifOutErrors";
    public static final int OUT_ERRORS_PROP_TYPE = 2;
    public static final String TOTAL_OUT_ERRORS_PROP_NAME = "totalIfOutErrors";
    public static final int TOTAL_OUT_ERRORS_PROP_TYPE = 2;
    public static final String OUT_Q_LEN_PROP_NAME = "ifOutQLen";
    public static final int OUT_Q_LEN_PROP_TYPE = 2;
    public static final String MEASUREMENT_TIME_PROP_NAME = "measureTime";
    public static final int MEASUREMENT_TIME_PROP_TYPE = 2;
    public static final String PERCENT_BANDWIDTH_USED_PROP_NAME = "percentBWUsed";
    public static final int PERCENT_BANDWIDTH_USED_PROP_TYPE = 3;
    private static final String METRIC_NAMES[] = {
        "ifInOctets", "totalIfInOctets", "ifOutOctets", "totalIfOutOctets", "ifSpeed", "ifInUcastPkts", "totalIfInUcastPkts", "ifOutUcastPkts", "totalIfOutUcastPkts", "ifInDiscards", 
        "totalIfInDiscards", "ifOutDiscards", "totalIfOutDiscards", "ifInErrors", "totalIfInErrors", "ifOutErrors", "totalIfOutErrors", "ifOutQLen", "measureTime", "percentBWUsed"
    };
    private static final int METRIC_TYPES[] = {
        2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 
        2, 2, 2, 2, 2, 2, 2, 2, 2, 3
    };
    private static String RT_METRIC_NAMES[] = {
        "ifInOctets", "ifOutOctets", "percentBWUsed"
    };
    private static int RT_METRIC_TYPES[] = {
        2, 2, 3
    };
    private static HashMap namesToLabels;
    private static HashMap sameGraphPropertiesMap = new HashMap();

    public NetInterface(String s, String s1, int i, String s2, int j, boolean flag)
    {
        mapRowToIfTableIndex = false;
        s2 = "half-duplex".equals(s2) ? "half-duplex" : "full-duplex";
        initialize(s, s, s, s1, i, s2, j, flag);
    }

    public NetInterface(String s, String s1, String s2, String s3, int i, boolean flag)
    {
        mapRowToIfTableIndex = false;
        initialize(s, s1, s2, s3, i, "half-duplex", 2, flag);
    }

    private void initialize(String s, String s1, String s2, String s3, int i, String s4, int j, 
            boolean flag)
    {
        name = s;
        desc = s1;
        alias = s2;
        physAddress = s3;
        row = i;
        duplex = s4;
        indexMethod = j;
        mapRowToIfTableIndex = flag;
    }

    public void setDuplex(String s)
    {
        if(s.equals("half-duplex"))
        {
            duplex = "half-duplex";
        } else
        {
            duplex = "full-duplex";
        }
    }

    public void setMetrics(com.dragonflow.Utils.Snmp.Monitoring.IFTable iftable)
    {
        if(indexMethod == 3)
        {
            if((row = iftable.getRowFromName(name)) < 0)
            {
                com.dragonflow.Log.LogManager.log("Error", "NetInterface could not determine row in ifTable when indexing by name (ifName).");
                return;
            }
        } else
        if(indexMethod == 0)
        {
            if((row = iftable.getRowFromDescription(desc)) < 0)
            {
                com.dragonflow.Log.LogManager.log("Error", "NetInterface could not determine row in ifTable when indexing by description (ifDescr).");
                return;
            }
        } else
        if(indexMethod == 1)
        {
            if((row = iftable.getRowFromPhysicalAddress(physAddress)) < 0)
            {
                com.dragonflow.Log.LogManager.log("Error", "NetInterface could not determine row in ifTable when indexing by physical address (ifPhysAddr).");
                return;
            }
        } else
        if(indexMethod == 2 && mapRowToIfTableIndex)
        {
            row = iftable.translateArrayPositionToRowNum(row);
            mapRowToIfTableIndex = false;
        }
        inOctets = iftable.getInOctets(row);
        outOctets = iftable.getOutOctets(row);
        speed = iftable.getSpeed(row);
        status = iftable.getOperStatus(row);
        inUnicastPackets = iftable.getInUCastPackets(row);
        outUnicastPackets = iftable.getOutUCastPackets(row);
        inDiscards = iftable.getInDiscards(row);
        outDiscards = iftable.getOutDiscards(row);
        inErrors = iftable.getInErrors(row);
        outErrors = iftable.getOutErrors(row);
        outQLen = iftable.getOutQLen(row);
        time = iftable.getTime();
    }

    public void setLastMetrics(com.dragonflow.Properties.StringProperty astringproperty[], com.dragonflow.SiteView.Monitor monitor)
    {
        for(int i = 0; i < astringproperty.length; i++)
        {
            String s = astringproperty[i].getName();
            if(s.startsWith("totalIfInOctets"))
            {
                lastInOctets = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfOutOctets"))
            {
                lastOutOctets = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfInUcastPkts"))
            {
                lastInUnicastPackets = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfOutUcastPkts"))
            {
                lastOutUnicastPackets = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfInDiscards"))
            {
                lastInDiscards = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfOutDiscards"))
            {
                lastOutDiscards = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfInErrors"))
            {
                lastInErrors = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("totalIfOutErrors"))
            {
                lastOutErrors = monitor.getPropertyAsLong(astringproperty[i]);
                continue;
            }
            if(s.startsWith("measureTime"))
            {
                lastTime = monitor.getPropertyAsLong(astringproperty[i]);
            }
        }

    }

    public String getName()
    {
        return name;
    }

    public String getPhysAddress()
    {
        return physAddress;
    }

    public double getPercentTotalBandwidthUtil()
    {
        long l = time - lastTime;
        if(lastOutOctets == 0L || lastInOctets == 0L || l == 0L)
        {
            return 0.0D;
        }
        double d = 0.0D;
        long l1;
        if(outOctets < lastOutOctets)
        {
            l1 = outOctets + (0xffffffffL - lastOutOctets);
        } else
        {
            l1 = outOctets - lastOutOctets;
        }
        long l2;
        if(inOctets < lastInOctets)
        {
            l2 = inOctets + (0xffffffffL - lastInOctets);
        } else
        {
            l2 = inOctets - lastInOctets;
        }
        long l3;
        if(duplex.equals("half-duplex"))
        {
            l3 = (l1 + l2) * 8L;
        } else
        {
            l3 = java.lang.Math.max(l1, l2) * 8L;
        }
        d = (100D * ((double)l3 / (double)l)) / (double)speed;
        return d;
    }

    public long getBytesIn()
    {
        return getCounterRate(lastInOctets, inOctets);
    }

    public long getBytesOut()
    {
        return getCounterRate(lastOutOctets, outOctets);
    }

    public long getDiscardsIn()
    {
        return getCounterRate(lastInDiscards, inDiscards);
    }

    public long getDiscardsOut()
    {
        return getCounterRate(lastOutDiscards, outDiscards);
    }

    public long getErrorsIn()
    {
        return getCounterRate(lastInErrors, inErrors);
    }

    public long getErrorsOut()
    {
        return getCounterRate(lastOutErrors, outErrors);
    }

    public long getUnicastPacketsIn()
    {
        return getCounterRate(lastInUnicastPackets, inUnicastPackets);
    }

    public long getUnicastPacketsOut()
    {
        return getCounterRate(lastOutUnicastPackets, outUnicastPackets);
    }

    public long getOutQLen()
    {
        return outQLen;
    }

    private long getCounterDifference(long l, long l1)
    {
        if(l == 0L)
        {
            return 0L;
        }
        if(l1 < l)
        {
            return (long)((double)l1 + (4294967295D - (double)l));
        } else
        {
            return l1 - l;
        }
    }

    private long getCounterRate(long l, long l1)
    {
        long l2 = time - lastTime;
        if(l2 <= 0L)
        {
            return 0L;
        }
        if(l == 0L)
        {
            return 0L;
        }
        long l3;
        if(l1 < l)
        {
            l3 = (long)((double)l1 + (4294967295D - (double)l));
        } else
        {
            l3 = l1 - l;
        }
        return l3 / l2;
    }

    public long getStatus()
    {
        return status;
    }

    public int getRow()
    {
        return row;
    }

    public long getTime()
    {
        return time;
    }

    public String getMetricByName(String s)
    {
        if(s.startsWith("totalIfInOctets"))
        {
            return java.lang.Long.toString(inOctets);
        }
        if(s.startsWith("totalIfOutOctets"))
        {
            return java.lang.Long.toString(outOctets);
        }
        if(s.startsWith("ifSpeed"))
        {
            return java.lang.Long.toString(speed);
        }
        if(s.startsWith("totalIfInUcastPkts"))
        {
            return java.lang.Long.toString(inUnicastPackets);
        }
        if(s.startsWith("totalIfOutUcastPkts"))
        {
            return java.lang.Long.toString(outUnicastPackets);
        }
        if(s.startsWith("totalIfInDiscards"))
        {
            return java.lang.Long.toString(inDiscards);
        }
        if(s.startsWith("totalIfOutDiscards"))
        {
            return java.lang.Long.toString(outDiscards);
        }
        if(s.startsWith("totalIfInErrors"))
        {
            return java.lang.Long.toString(inErrors);
        }
        if(s.startsWith("totalIfOutErrors"))
        {
            return java.lang.Long.toString(outErrors);
        }
        if(s.startsWith("measureTime"))
        {
            return java.lang.Long.toString(time);
        }
        if(s.startsWith("ifInOctets"))
        {
            return java.lang.Long.toString(getBytesIn());
        }
        if(s.startsWith("ifOutOctets"))
        {
            return java.lang.Long.toString(getBytesOut());
        }
        if(s.startsWith("ifInDiscards"))
        {
            return java.lang.Long.toString(getDiscardsIn());
        }
        if(s.startsWith("ifOutDiscards"))
        {
            return java.lang.Long.toString(getDiscardsOut());
        }
        if(s.startsWith("ifInErrors"))
        {
            return java.lang.Long.toString(getErrorsIn());
        }
        if(s.startsWith("ifOutErrors"))
        {
            return java.lang.Long.toString(getErrorsOut());
        }
        if(s.startsWith("ifInUcastPkts"))
        {
            return java.lang.Long.toString(getUnicastPacketsIn());
        }
        if(s.startsWith("ifOutUcastPkts"))
        {
            return java.lang.Long.toString(getUnicastPacketsOut());
        }
        if(s.startsWith("ifOutQLen"))
        {
            return java.lang.Long.toString(getOutQLen());
        }
        if(s.startsWith("percentBWUsed"))
        {
            return java.lang.Integer.toString((int)getPercentTotalBandwidthUtil());
        } else
        {
            return "";
        }
    }

    public String getPropertyLabel(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(name);
        stringbuffer.append(": ");
        String s1 = (String)namesToLabels.get(s);
        if(s1 != null)
        {
            stringbuffer.append(s1);
        } else
        {
            stringbuffer.append(s);
        }
        return stringbuffer.toString();
    }

    public static com.dragonflow.Properties.StringProperty[] createRegularProperties(String s, java.util.Vector vector)
    {
        StringBuffer stringbuffer = new StringBuffer();
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[METRIC_NAMES.length];
        for(int i = 0; i < METRIC_NAMES.length; i++)
        {
            stringbuffer.setLength(0);
            stringbuffer.append(METRIC_NAMES[i]).append(s).append("_").append(i);
            if(METRIC_TYPES[i] == 1)
            {
                astringproperty[i] = new RateProperty(stringbuffer.toString());
            } else
            if(METRIC_TYPES[i] == 2)
            {
                astringproperty[i] = new NumericProperty(stringbuffer.toString());
            } else
            if(METRIC_TYPES[i] == 3)
            {
                astringproperty[i] = new PercentProperty(stringbuffer.toString());
            } else
            if(METRIC_TYPES[i] == 4)
            {
                astringproperty[i] = new StringProperty(stringbuffer.toString());
            }
            astringproperty[i].isStateProperty = false;
            vector.add(astringproperty[i]);
        }

        return astringproperty;
    }

    public static com.dragonflow.Properties.StringProperty[] createRealTimeProperties(String s, java.util.Vector vector, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer();
        com.dragonflow.Properties.StringProperty astringproperty[] = new com.dragonflow.Properties.StringProperty[RT_METRIC_NAMES.length];
        com.dragonflow.Properties.StringProperty stringproperty = null;
        com.dragonflow.Properties.StringProperty stringproperty1 = null;
        for(int i = 0; i < RT_METRIC_NAMES.length; i++)
        {
            stringbuffer.setLength(0);
            stringbuffer.append(RT_METRIC_NAMES[i]).append(s).append("_").append(i).append("_rt");
            if(RT_METRIC_TYPES[i] == 1)
            {
                astringproperty[i] = new RateProperty(stringbuffer.toString(), s1);
            } else
            if(RT_METRIC_TYPES[i] == 2)
            {
                astringproperty[i] = new NumericProperty(stringbuffer.toString(), s1);
            } else
            if(RT_METRIC_TYPES[i] == 3)
            {
                astringproperty[i] = new PercentProperty(stringbuffer.toString(), s1);
            } else
            if(RT_METRIC_TYPES[i] == 4)
            {
                astringproperty[i] = new StringProperty(stringbuffer.toString(), s1);
            }
            if(astringproperty[i].getName().startsWith("ifInOctets"))
            {
                stringproperty = astringproperty[i];
            } else
            if(astringproperty[i].getName().startsWith("ifOutOctets"))
            {
                stringproperty1 = astringproperty[i];
            }
            astringproperty[i].isStateProperty = false;
            vector.add(astringproperty[i]);
        }

        if(stringproperty1 != null && stringproperty != null)
        {
            sameGraphPropertiesMap.put(stringproperty, new com.dragonflow.Properties.StringProperty[] {
                stringproperty, stringproperty1
            });
            sameGraphPropertiesMap.put(stringproperty1, new com.dragonflow.Properties.StringProperty[] {
                stringproperty, stringproperty1
            });
        }
        return astringproperty;
    }

    public static com.dragonflow.Properties.StringProperty[] getPropertiesOnSameGraph(com.dragonflow.Properties.StringProperty stringproperty)
    {
        com.dragonflow.Properties.StringProperty astringproperty[] = (com.dragonflow.Properties.StringProperty[])sameGraphPropertiesMap.get(stringproperty);
        if(astringproperty != null)
        {
            return astringproperty;
        } else
        {
            return (new com.dragonflow.Properties.StringProperty[] {
                stringproperty
            });
        }
    }

    public boolean isStateProperty(String s)
    {
        return s.startsWith("ifInOctets") || s.startsWith("ifOutOctets") || s.startsWith("ifInUcastPkts") && inUnicastPackets != -1L || s.startsWith("ifOutUcastPkts") && outUnicastPackets != -1L || s.startsWith("ifInDiscards") && inDiscards != -1L || s.startsWith("ifOutDiscards") && outDiscards != -1L || s.startsWith("ifInErrors") && inErrors != -1L || s.startsWith("ifOutErrors") && outErrors != -1L || s.startsWith("ifOutQLen") && outQLen != -1L || s.startsWith("percentBWUsed");
    }

    public String getAlias()
    {
        return alias;
    }

    public String getDescription()
    {
        return desc;
    }

    static 
    {
        namesToLabels = new HashMap();
        namesToLabels.put("ifInOctets", "Bytes In");
        namesToLabels.put("totalIfInOctets", "Total Bytes In");
        namesToLabels.put("ifOutOctets", "Bytes Out");
        namesToLabels.put("totalIfOutOctets", "Total Bytes Out");
        namesToLabels.put("ifSpeed", "Interface Speed");
        namesToLabels.put("ifInUcastPkts", "Unicast Packets In");
        namesToLabels.put("totalIfInUcastPkts", "Total Unicast Packets In");
        namesToLabels.put("ifOutUcastPkts", "Unicast Packets Out");
        namesToLabels.put("totalIfOutUcastPkts", "Total Unicast Packets Out");
        namesToLabels.put("ifInDiscards", "Incoming Discarded Packets");
        namesToLabels.put("totalIfInDiscards", "Total Incoming Discarded Packets");
        namesToLabels.put("ifOutDiscards", "Outgoing Discarded Packets");
        namesToLabels.put("totalIfOutDiscards", "Total Outgoing Discarded Packets");
        namesToLabels.put("ifInErrors", "Incoming Packets in Error");
        namesToLabels.put("totalIfInErrors", "Total Incoming Packets in Error");
        namesToLabels.put("ifOutErrors", "Outgoing Packets in Error");
        namesToLabels.put("totalIfOutErrors", "Total Outgoing Packets in Error");
        namesToLabels.put("ifOutQLen", "Out Queue Length (Pkts)");
        namesToLabels.put("measureTime", "Time of Measurement");
        namesToLabels.put("percentBWUsed", "% Bandwidth Utilized");
    }
}
