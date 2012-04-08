/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NTCounterMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NTCounterMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.JdbcLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.NTCounterBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.PerfChartFile;
import COM.dragonflow.Utils.PerfCounter;
import COM.dragonflow.Utils.TextUtils;

public class NTCounterMonitor extends ServerMonitor
{

    static StringProperty pObject;
    static StringProperty pCounter;
    static StringProperty pInstance;
    static StringProperty pUnits;
    static StringProperty pScale;
    static ScalarProperty pPMCFile;
    public static StringProperty pValues[];
    public static StringProperty pMeasurements[];
    public static StringProperty pLastMeasurements[];
    public static StringProperty pLastBaseMeasurements[];
    public static StringProperty pLastMeasurementTime;
    public static StringProperty pLastMeasurementTicks;
    public static StringProperty pLastMeasurementWasNA;
    public static StringProperty pMonitorRunCount;
    public static StringProperty pPercentDeviation[];
    public static StringProperty pRollingBaseValues[];
    public static StringProperty pErrCnt;
    static final String pmcFileDirName = "templates.perfmon";
    static final String pmcFileExtension = ".PMC";
    static final String pmwFileExtension = ".PMW";
    static final String htmFileExtension = ".HTM";
    static final String userObjectString = "(Custom Object)";
    static final String defaultFile = "System.pmc";
    static final String notAvailable = "n/a";
    static int nMaxCounters;
    private Map mIDMap;
    boolean hasSubscription;
    boolean showDebug;
    static HashMap allSettings = new HashMap();
    static Map modifiedDates = new java.util.HashMap();
    Array countersCache;
    HashMap labelsCache;
    static String perf_data_create = "CREATE TABLE [dbo].[PERF_DATA] ([SERVERID] [int] NOT NULL ,[POLL_TIME] [datetime] NOT NULL ,[RESID] [int] NOT NULL ,[RESVAL] [real] NOT NULL ) ON [PRIMARY] ";
    static String perf_data_create2 = "CREATE TABLE [dbo].[RESOURCE] ( [RESID] [int] IDENTITY (3545, 1) NOT NULL , [RESTYPE] [varchar] (120) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [RESSUBTYPE] [varchar] (120) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [RESINSTANCE] [varchar] (120) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ) ON [PRIMARY]";
    static String perf_data_define = "CREATE PROCEDURE [dbo].[sp_insert_perf_data] @intServerID int, @datPollTime datetime, @vchResType varchar(120), @vchResSubType varchar(120), @vchResInstance nvarchar(120), @rlResValue real AS BEGIN DECLARE @id INT SET @id = (SELECT RESID FROM Resource WHERE RESTYPE = @vchResType and RESSUBTYPE = @vchResSubType and RESINSTANCE = @vchResInstance) IF @id IS NULL BEGIN   INSERT INTO Resource (RESTYPE, RESSUBTYPE, RESINSTANCE) VALUES (@vchResType, @vchResSubType, @vchResInstance)   SET @id = (SELECT RESID FROM Resource WHERE RESTYPE = @vchResType and RESSUBTYPE = @vchResSubType and RESINSTANCE = @vchResInstance) END INSERT INTO perf_data VALUES (@intServerid, @datPollTime, @id, @rlResValue) END";
    static String perf_data_insert = "call dbo.sp_insert_perf_data(?,?,?,?,?,?)";
    static HashMap noData = new HashMap();

    public NTCounterMonitor()
    {
        mIDMap = null;
        hasSubscription = false;
        showDebug = false;
        countersCache = null;
        labelsCache = null;
    }

    protected void startMonitor()
    {
        if(getSetting("_perfDebug").length() > 0)
        {
            showDebug = true;
        }
        if(LUtils.isSubscriptionLicense())
        {
            hasSubscription = true;
        }
        super.startMonitor();
    }

    synchronized Array getCounters()
    {
        if(countersCache == null)
        {
            String s = getProperty(pObject);
            if(s.length() > 0)
            {
                countersCache = new Array();
                PerfCounter perfcounter = new PerfCounter();
                perfcounter.object = getProperty(pObject);
                perfcounter.counterName = getProperty(pCounter);
                perfcounter.instance = getProperty(pInstance);
                countersCache.add(perfcounter);
            } else
            {
                synchronized(allSettings)
                {
                    String s1 = Platform.getRoot() + File.separator + "templates.perfmon" + File.separator + getProperty(pPMCFile);
                    countersCache = (Array)allSettings.get(s1);
                    Long long1 = (Long)modifiedDates.get(s1);
                    File file = new File(s1);
                    long l = 0L;
                    if(file.exists())
                    {
                        l = file.lastModified();
                    }
                    if(countersCache == null || long1 == null || long1.longValue() != l)
                    {
                        try
                        {
                            countersCache = PerfChartFile.GetSettings(s1);
                            allSettings.put(s1, countersCache);
                            modifiedDates.put(s1, new Long(l));
                        }
                        catch(Exception exception)
                        {
                            countersCache = new Array();
                            LogManager.log("Error", "error reading Perf Chart file, " + s1 + ", " + exception);
                        }
                    }
                }
            }
        }
        return countersCache;
    }

    int getActiveCounters()
    {
        Array array = getCounters();
        if(array == null)
        {
            return 0;
        }
        int i = array.size();
        if(i > nMaxCounters)
        {
            i = nMaxCounters;
        }
        return i;
    }

    synchronized HashMap getLabels()
    {
        if(labelsCache == null)
        {
            labelsCache = new HashMap();
            if(getProperty(pObject).length() > 0)
            {
                labelsCache.add("Counter 1 Value", getProperty(pCounter));
                labelsCache.add("Counter 1 Percent Deviation", getProperty(pCounter) + " percent Deviation");
            } else
            {
                Array array = getCounters();
                for(int i = 0; i < array.size(); i++)
                {
                    PerfCounter perfcounter = (PerfCounter)array.at(i);
                    labelsCache.add("Counter " + (i + 1) + " Value", perfcounter.object + " : " + perfcounter.counterName + ":" + perfcounter.instance);
                    labelsCache.add("Counter " + (i + 1) + " Percent Deviation", perfcounter.object + " : " + perfcounter.counterName + ":" + perfcounter.instance + " Percent Deviation");
                }

                labelsCache.add(pErrCnt.getName(), "counters in error");
            }
        }
        return labelsCache;
    }

    public boolean remoteCommandLineAllowed()
    {
        return false;
    }

    long wrapAroundDifference(long l, long l1)
    {
        if(l >= l1 && l1 >= 0L)
        {
            return l - l1;
        }
        long l2 = -1L;
        l2 >>= 1;
        if(l1 > 0L)
        {
            for(long l3 = l1 >> 2; l2 > l3; l2 >>= 1) { }
            l2 <<= 1;
            l2 |= 1L;
        }
        long l4 = (l ^ l1) & 1L;
        if((l1 & 1L) != 0L)
        {
            l4 = -l4;
        }
        l1 >>= 1;
        l >>= 1;
        long l5;
        if(l >= l1)
        {
            l5 = l - l1;
        } else
        {
            l5 = l + (l2 - l1) + 1L;
        }
        l5 <<= 2;
        l5 += l4;
        return l5;
    }

    public static String padWithZeros(int i, int j)
    {
        String s = "" + i;
        int k = s.length();
        for(int l = 0; l < j - k; l++)
        {
            s = "0" + s;
        }

        return s;
    }

    String jdbcDateFormat(Date date)
    {
        return (date.getYear() + 1900) + "-" + padWithZeros(date.getMonth() + 1, 2) + "-" + padWithZeros(date.getDate(), 2) + " " + padWithZeros(date.getHours(), 2) + ":" + padWithZeros(date.getMinutes(), 2) + ":" + padWithZeros(date.getSeconds(), 2);
    }

    void logPerfValue(PerfCounter perfcounter, String s, float f)
    {
        if(getProperty("_databaseLogging").length() == 0)
        {
            return;
        }
        if(!hasSubscription)
        {
            LogManager.log("Error", "needs SiteReliance license");
            return;
        }
        if(JdbcLogger.logger == null)
        {
            LogManager.log("Error", "database logging not configured");
            return;
        }
        if(Float.isNaN(f))
        {
            String s1 = perfcounter.object + "/" + perfcounter.counterName + "/" + perfcounter.instance + ", " + getParent().getProperty(pName) + ", " + getProperty(pName);
            if(noData.get(s1) == null)
            {
                noData.put(s1, s1);
                LogManager.log("RunMonitor", "n/a perf data, " + s1);
            }
            return;
        }
        String s2 = getProperty(pMonitorDescription);
        String s3 = "-1";
        Array array = Platform.split(' ', s2);
        if(array.size() >= 1)
        {
            Array array1 = Platform.split('=', (String)array.at(0));
            if(array1.size() == 2)
            {
                s3 = (String)array1.at(1);
            }
        }
        Array array2 = new Array();
        array2.add(s3);
        array2.add(s);
        array2.add(perfcounter.object);
        array2.add(perfcounter.counterName);
        array2.add(perfcounter.instance);
        array2.add("" + f);
        JdbcLogger.logger.logCustom(this, array2, "perf_data", perf_data_create, perf_data_create2, perf_data_define, perf_data_insert);
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=perfCounter";
        return s;
    }

    protected boolean update()
    {
        String s = getProperty(pMachineName);
        int i = TextUtils.toInt(getProperty(pMonitorRunCount));
        long lLastMeasureTime = TextUtils.toLong(getProperty(pLastMeasurementTime));
        long lLastMeasureTick = TextUtils.toLong(getProperty(pLastMeasurementTicks));
        boolean flag = TextUtils.toInt(getProperty(pLastMeasurementWasNA)) == 1;
        long lMeasureTime = 0L;
        long l3 = 1L;
        long l4 = 0L;
        Array array = getCounters();
        int j = 0;
        if(array.size() == 0)
        {
            setProperty(pLastMeasurementTime, 0);
            for(int k = 0; k < nMaxCounters; k++)
            {
                setProperty(pValues[k], "n/a");
                setProperty(pMeasurements[k], 0);
                setProperty(pLastMeasurements[k], 0);
            }

            setProperty(pNoData, "n/a");
            setProperty(pStateString, "Perf Chart file error");
            return false;
        }
        if(array.size() > nMaxCounters)
        {
            String s1 = "_NTCounterMonitorMaxCounters in master.config needs to be at least " + array.size() + " for the file \"" + getProperty(pPMCFile) + "\"";
            setProperty(pNoData, "n/a");
            setProperty(pStateString, s1);
            return false;
        }
        if(mIDMap == null)
        {
            mIDMap = NTCounterBase.getIDCacheForCounters(s, array);
        }
        long lastMeasurement[] = new long[nMaxCounters];
        long measurement[] = new long[nMaxCounters];
        long al2[] = new long[nMaxCounters];
        String s2 = jdbcDateFormat(Platform.makeDate());
        Enumeration enumeration = null;
        long lDefaMeasureTime = 0L;
        long l6 = 0L;
        float f = 0.0F;
        String s3 = "";
        boolean flag1 = true;
        Object obj = null;
        Array array2 = null;
        if(monitorDebugLevel == 3)
        {
            array2 = new Array();
        }
label0:
        for(int i1 = 0; flag1 && i1 < 2; i1++)
        {
            if(i1 != 0)
            {
                Platform.sleep(4000L);
            }
            StringBuffer stringbuffer = new StringBuffer();
            Array array1 = NTCounterBase.getPerfData(s, array, stringbuffer, showDebug, this, array2, mIDMap);
            s3 = stringbuffer.toString();
            enumeration = array1.elements();
            if(enumeration.hasMoreElements())
            {
            	lMeasureTime = TextUtils.toLong((String)enumeration.nextElement());
            }
            if(enumeration.hasMoreElements())
            {
                l4 = TextUtils.toLong((String)enumeration.nextElement());
            }
            if(enumeration.hasMoreElements())
            {
                l3 = TextUtils.toLong((String)enumeration.nextElement());
            }
            lDefaMeasureTime = lMeasureTime - lLastMeasureTime;
            l6 = l4 - lLastMeasureTick;
            f = (float)l6 / (float)l3;
            flag1 = lDefaMeasureTime <= 0L || l6 <= 0L || lLastMeasureTime <= 0L || flag || lLastMeasureTick <= 0L;
            if(i1 > 0)
            {
                break;
            }
            if(!flag1)
            {
                for(int k1 = 0; k1 < nMaxCounters; k1++)
                {
                	lastMeasurement[k1] = TextUtils.toLong(getProperty(pLastMeasurements[k1]));
                    al2[k1] = TextUtils.toLong(getProperty(pLastBaseMeasurements[k1]));
                }

                continue;
            }
            lLastMeasureTime = lMeasureTime;
            lLastMeasureTick = l4;
            flag = false;
            int i2 = 0;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    continue label0;
                }
                String s4 = (String)enumeration.nextElement();
                if(s4.equals("n/a"))
                {
                    enumeration.nextElement();
                    enumeration.nextElement();
                    lastMeasurement[i2] = 0L;
                    al2[i2++] = 0L;
                } else
                {
                	lastMeasurement[i2] = TextUtils.toLong((String)enumeration.nextElement());
                    al2[i2++] = TextUtils.toLong((String)enumeration.nextElement());
                }
            } while(true);
        }

        int j1 = 0;
        byte byte0 = 2;
        float fScale = 1.0F;
        if(!getProperty(pScale).equals("1"))
        {
        	fScale = StringProperty.toFloat(getProperty(pScale));
            if(!Float.isNaN(fScale) && fScale > 0.0F && fScale < 1.0F)
            {
                byte0 = 2;
            }
        }
        long l7 = getSettingAsLong("_NTCounterSummaryMax", 3);
        while(enumeration.hasMoreElements()) 
        {
            PerfCounter perfcounter = (PerfCounter)array.at(j1);
            String s5 = (String)enumeration.nextElement();
            float fResult = (0.0F / 0.0F);
            long l8 = 0L;
            boolean flag2 = false;
            boolean flag3 = false;
            if(s5.equals("n/a"))
            {
                enumeration.nextElement();
                enumeration.nextElement();
            } else
            {
            	measurement[j1] = TextUtils.toLong((String)enumeration.nextElement());
                long Cal = measurement[j1] - lastMeasurement[j1];
                byte0 = 2;
                l8 = TextUtils.toLong((String)enumeration.nextElement());
                long lDefaBase = l8 - al2[j1];
                if(!flag1)
                {
                    if(j1 > 0 && (long)j1 < l7)
                    {
                        s3 = s3 + ", ";
                    }
                    if(measurement[j1] >= 0L)
                    {
                        if(s5.equals("PERF_COUNTER_COUNTER"))
                        {
                            if(Cal >= 0L)
                            {
                                if(lastMeasurement[j1] == 0L)
                                {
                                	fResult = 0.0F;
                                } else
                                {
                                	fResult = (float)Cal / f;
                                }
                                flag3 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_TIMER"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / (float)l6;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_TIMER_INV"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = 1.0F - (float)Cal / f;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_BULK_COUNT"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / f;
                                flag3 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_RAWCOUNT") || s5.equals("PERF_COUNTER_LARGE_RAWCOUNT") || s5.equals("PERF_COUNTER_RAWCOUNT_HEX") || s5.equals("PERF_COUNTER_LARGE_RAWCOUNT_HEX"))
                        {
                        	fResult = measurement[j1];
                            byte0 = 0;
                        } else
                        if(s5.equals("PERF_ELAPSED_TIME"))
                        {
                            long l11 = measurement[j1];
                            if(l11 > lMeasureTime || l11 < 0L)
                            {
                                l11 >>>= 32;
                            }
                            fResult = (lMeasureTime - l11) / 0x989680L;
                            byte0 = 0;
                        } else
                        if(s5.equals("PERF_RAW_FRACTION"))
                        {
                            if(measurement[j1] == 0L)
                            {
                            	fResult = 0.0F;
                            } else
                            {
                            	fResult = (float)measurement[j1] / (float)l8;
                            }
                            flag2 = true;
                        } else
                        if(s5.equals("PERF_SAMPLE_FRACTION"))
                        {
                            flag2 = true;
                            if(Cal <= 0L || lDefaBase <= 0L)
                            {
                            	fResult = 0.0F;
                            } else
                            {
                            	fResult = (float)Cal / (float)lDefaBase;
                            }
                        } else
                        if(s5.equals("PERF_SAMPLE_COUNTER"))
                        {
                            if(Cal >= 0L && lDefaBase > 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaBase;
                            }
                        } else
                        if(s5.equals("PERF_AVERAGE_TIME"))
                        {
                        	fResult = (float)measurement[j1] / (float)l8;
                        } else
                        if(s5.equals("PERF_AVERAGE_TIMER"))
                        {
                            if(Cal >= 0L && lDefaBase > 0L)
                            {
                            	fResult = (float)Cal / (float)l3 / (float)lDefaBase;
                            } else
                            {
                            	fResult = 0.0F;
                            }
                        } else
                        if(s5.equals("PERF_AVERAGE_BULK"))
                        {
                            if(Cal >= 0L && lDefaBase > 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaBase;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_TIMER"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaMeasureTime;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_TIMER_INV"))
                        {
                            if(Cal >= 0L)
                            {
                                float f3 = (float)Cal / (float)lDefaMeasureTime;
                                fResult = 1.0F - f3;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_MULTI_TIMER"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / f / (float)l8;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_MULTI_TIMER_INV"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = 1.0F - (float)Cal / f / (float)l8;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_MULTI_TIMER"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaMeasureTime / (float)l8;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_MULTI_TIMER_INV"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = 1.0F - (float)Cal / (float)lDefaMeasureTime / (float)l8;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_QUEUELEN_TYPE"))
                        {
                        	fResult = ((float)lastMeasurement[j1] + (float)(lMeasureTime * measurement[j1])) / (float)lDefaMeasureTime;
                        } else
                        if(s5.equals("PERF_COUNTER_LARGE_QUEUELEN_TYPE"))
                        {
                        	fResult = ((float)lastMeasurement[j1] + (float)(lMeasureTime * measurement[j1])) / (float)lDefaMeasureTime;
                        } else
                        if(s5.equals("PERF_PRECISION_100NSEC_TIMER"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaBase;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_PRECISION_100NSEC_QUEUELEN"))
                        {
                            if(Cal >= 0L)
                            {
                            	fResult = (float)Cal / (float)lDefaMeasureTime;
                            }
                        } else
                        if(s5.equals(""))
                        {
                            MonitorGroup monitorgroup = (MonitorGroup)getOwner();
                            LogManager.log("Error", "NTCounterMonitor: no data for counter \"" + perfcounter.object + "/" + perfcounter.instance + "/" + perfcounter.counterName + "\" on host " + s + ", monitor " + getFullID() + " in group " + monitorgroup.getFullID());
                        } else
                        {
                            LogManager.log("Error", "NTCounterMonitor: could not find the counter type for: " + s5);
                        }
                        if(flag2)
                        {
                            byte0 = 2;
                            fResult *= 100F;
                        }
                    }
                }
                if(showDebug)
                {
                    LogManager.log("RunMonitor", "Counter Monitor, type=" + s5 + ", value=" + fResult + ", delta=" + Cal + ", deltaBase=" + lDefaBase + ", deltaTime=" + lDefaMeasureTime);
                }
            }
            if(stillActive())
            {
                synchronized(this)
                {
                    logPerfValue(perfcounter, s2, fResult);
                    if(Float.isNaN(fResult))
                    {
                        setProperty(pLastMeasurementTime, 0);
                        setProperty(pLastMeasurementWasNA, 1);
                        setProperty(pValues[j1], "n/a");
                        j++;
                        setProperty(pMeasurements[j1], 0);
                        setProperty(pLastMeasurements[j1], 0);
                        if((long)j1 < l7)
                        {
                            s3 = s3 + perfcounter.counterName + " " + "n/a";
                        }
                        if(monitorDebugLevel == 3 && array2 != null)
                        {
                            StringBuffer stringbuffer1 = new StringBuffer();
                            for(int j2 = 0; j2 < array2.size(); j2++)
                            {
                                stringbuffer1.append(array2.at(j2) + "\n");
                            }

                            LogManager.log("Error", "NTCounterMonitor: " + getFullID() + " failed, output:\n" + stringbuffer1);
                        }
                    } else
                    {
                    	fResult *= fScale;
                        setProperty(pValues[j1], TextUtils.floatToString(fResult, byte0));
                        setProperty(pLastMeasurements[j1], measurement[j1]);
                        setProperty(pLastBaseMeasurements[j1], l8);
                        setProperty(pMeasurements[j1], getMeasurement(pValues[j1], 10L));
                        setProperty(pLastMeasurementWasNA, 0);
                        if(i > 0)
                        {
                            setRollingBaseProperties(fResult, pRollingBaseValues[j1], pPercentDeviation[j1], i, byte0);
                        }
                        if((long)j1 < l7)
                        {
                            s3 = s3 + perfcounter.counterName + ":" + perfcounter.instance + " = " + TextUtils.floatToString(fResult, byte0);
                            if(getProperty(pUnits).length() > 0)
                            {
                                s3 = s3 + " " + getProperty(pUnits);
                            }
                            if(flag2)
                            {
                                s3 = s3 + "%";
                            }
                            if(flag3)
                            {
                                s3 = s3 + "/sec";
                            }
                        }
                    }
                }
            }
            if((long)j1 == l7)
            {
                s3 = s3 + "...";
            }
            j1++;
        }
        if(flag1)
        {
            setProperty(pNoData, "n/a");
        }
        setProperty(pErrCnt, "" + j);
        setProperty(pLastMeasurementTime, lMeasureTime);
        setProperty(pLastMeasurementTicks, l4);
        setProperty(pStateString, s3);
        return true;
    }

    void setRollingBaseProperties(float f, StringProperty stringproperty, StringProperty stringproperty1, int i, int j)
    {
        String s = getProperty(stringproperty);
        Array array = new Array();
        if(s != null && s.length() > 0)
        {
            array = TextUtils.splitArray(s, "\t");
        }
        float f1 = 0.0F;
        int k = 0;
        for(int l = 0; l < array.size(); l++)
        {
            float f3 = TextUtils.toFloat((String)array.at(l));
            if(f3 != 0.0F)
            {
                f1 += f3;
                k++;
            }
        }

        float f2 = 0.0F;
        if(k > 0)
        {
            f2 = f1 / (float)k;
        }
        if(array.size() >= i)
        {
            array.popFront();
        }
        array.add(TextUtils.floatToString(f, j));
        s = "";
        for(int i1 = 0; i1 < array.size(); i1++)
        {
            s = s + (String)array.at(i1);
            s = s + "\t";
        }

        unsetProperty(stringproperty);
        setProperty(stringproperty, s);
        if(f2 == 0.0F)
        {
            f2 = f;
        }
        float f4 = (Math.abs(f2 - f) / f2) * 100F;
        if(array.size() < i)
        {
            return;
        } else
        {
            setProperty(stringproperty1, TextUtils.floatToString(f4, 3));
            return;
        }
    }

    public String getPropertyName(StringProperty stringproperty)
    {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if(s1.length() == 0)
        {
            s1 = s;
        }
        return s1;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag)
    {
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if(s1.length() != 0)
        {
            return s1;
        }
        if(flag)
        {
            return "";
        } else
        {
            return s;
        }
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        int i = getActiveCounters();
        for(int j = 0; j < i; j++)
        {
            array.add(pValues[j]);
            array.add(pPercentDeviation[j]);
            array.add(pMeasurements[j]);
            array.add(pLastMeasurements[j]);
        }

        array.add(pErrCnt);
        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Array array = new Array();
        int i = getActiveCounters();
        for(int j = 0; j < i; j++)
        {
            array.add(pValues[j]);
        }

        array.add(pErrCnt);
        return array.elements();
    }

    public String defaultTitle()
    {
        String s = getProperty(pObject);
        String s1;
        if(s.length() != 0)
        {
            s1 = getProperty(pObject) + " : " + getProperty(pCounter);
            if(getProperty(pInstance).length() > 0)
            {
                s1 = s1 + " : " + getProperty(pInstance);
            }
        } else
        {
            String s2 = getProperty(pPMCFile);
            s1 = "NT Performance Counter";
            if(s2 != null && s2.length() > 0)
            {
                s1 = s1 + ": " + s2;
            }
        }
        return s1;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pObject)
        {
            if(s.length() > 0)
            {
                setProperty(pPMCFile, "(Custom Object)");
            } else
            if(getProperty(pPMCFile).equals("(Custom Object)"))
            {
                setProperty(pPMCFile, "none");
            }
        } else
        if(stringproperty == pScale)
        {
            return s;
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pScale)
        {
            Vector vector = new Vector();
            vector.addElement("10000000");
            vector.addElement("10000000");
            vector.addElement("1000000");
            vector.addElement("1000000");
            vector.addElement("100000");
            vector.addElement("100000");
            vector.addElement("10000");
            vector.addElement("10000");
            vector.addElement("1000");
            vector.addElement("1000");
            vector.addElement("100");
            vector.addElement("100");
            vector.addElement("10");
            vector.addElement("10");
            vector.addElement("1");
            vector.addElement("1");
            vector.addElement("0.1");
            vector.addElement(".1");
            vector.addElement("0.01");
            vector.addElement(".01");
            vector.addElement("0.001");
            vector.addElement(".001");
            vector.addElement("0.0001");
            vector.addElement(".0001");
            vector.addElement("0.00001");
            vector.addElement(".00001");
            vector.addElement("0.000001");
            vector.addElement(".000001");
            vector.addElement("0.0000001");
            vector.addElement(".0000001");
            vector.addElement("0.00000001");
            vector.addElement(".00000001");
            vector.addElement(String.valueOf(0.0009765625F));
            vector.addElement("kilobytes");
            vector.addElement(String.valueOf(9.536743E-007F));
            vector.addElement("megabytes");
            return vector;
        }
        if(scalarproperty == pPMCFile)
        {
            File file = new File(Platform.getRoot() + File.separator + "templates.perfmon" + File.separator);
            String as[] = file.list();
            Vector vector1 = new Vector();
            for(int i = 0; i < as.length; i++)
            {
                String s = as[i].toUpperCase();
                if(s.endsWith(".PMC") || s.endsWith(".PMW") || s.endsWith(".HTM"))
                {
                    vector1.addElement(as[i]);
                    vector1.addElement(as[i]);
                }
            }

            vector1.addElement("(Custom Object)");
            vector1.addElement("(Custom Object)");
            return vector1;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String getTopazCounterLabel(StringProperty stringproperty)
    {
        return getPropertyName(stringproperty);
    }

    static 
    {
        nMaxCounters = 10;
        Array array = new Array();
        HashMap hashmap = MasterConfig.getMasterConfig();
        int i = TextUtils.toInt(TextUtils.getValue(hashmap, "_NTCounterMonitorMaxCounters"));
        if(i > nMaxCounters)
        {
            nMaxCounters = i;
        }
        pPMCFile = new ScalarProperty("_pmcfile", "System.pmc");
        pPMCFile.setDisplayText("PerfMon Chart File", "a Performance Monitor setting file that specifies the counters (from templates.perfmon directory)");
        pPMCFile.setParameterOptions(true, 1, false);
        pObject = new StringProperty("_object");
        pObject.setDisplayText("Object", "this is same as the name of the object in the NT Performance Monitor under \"Add to Chart\"");
        pObject.setParameterOptions(true, 2, true);
        pCounter = new StringProperty("_counter");
        pCounter.setDisplayText("Counter", "this is same as the name of the counter in the NT Performance Monitor under \"Add to Chart\"");
        pCounter.setParameterOptions(true, 3, true);
        pInstance = new StringProperty("_instance");
        pInstance.setDisplayText("Instance", "this is same as the instance in the NT Performance Monitor under \"Add to Chart\". Leaving this blank will choose the first instance in the list, if there are multiple instances.");
        pInstance.setParameterOptions(true, 4, true);
        pUnits = new StringProperty("_units");
        pUnits.setDisplayText("Units", "optional units string to append when displaying the value of this counter.");
        pUnits.setParameterOptions(true, 5, true);
        pScale = new ScalarProperty("_scale", "1");
        pScale.setDisplayText("Scale", "optional scale multiplier for this counter - for example, you may want to scale a disk usage counter by megabytes to make the output more readable.");
        pScale.setParameterOptions(true, 6, true);
        ((ScalarProperty)pScale).allowOther = true;
        pMonitorRunCount = new NumericProperty("_monitorRunCount", "0");
        pMonitorRunCount.setDisplayText("Baseline Interval", "The number of monitor runs to be averaged for a <a href=\"/SiteView/docs/RollingBaseline.htm\" target=HELP>Rolling Baseline</a>.");
        pMonitorRunCount.setParameterOptions(true, 7, true);
        pValues = new StringProperty[nMaxCounters];
        pPercentDeviation = new StringProperty[nMaxCounters];
        pRollingBaseValues = new StringProperty[nMaxCounters];
        pMeasurements = new StringProperty[nMaxCounters];
        pLastMeasurements = new StringProperty[nMaxCounters];
        pLastBaseMeasurements = new StringProperty[nMaxCounters];
        pErrCnt = new NumericProperty(BrowsableBase.PROPERTY_NAME_COUNTERS_IN_ERROR, "0");
        pErrCnt.setIsThreshold(true);
        for(int j = 0; j < nMaxCounters; j++)
        {
            pValues[j] = new NumericProperty("value" + j);
            pValues[j].setDisplayText("Counter " + (j + 1) + " Value", "The NT Performance Counter");
            pValues[j].setStateOptions(j + 1);
            array.add(pValues[j]);
            pPercentDeviation[j] = new NumericProperty("percentdeviation" + j);
            pPercentDeviation[j].setDisplayText("Counter " + (j + 1) + " Percent Deviation", "The NT Performance Counter Percent Deviation");
            pPercentDeviation[j].setStateOptions(j + 1 + nMaxCounters);
            array.add(pPercentDeviation[j]);
            pRollingBaseValues[j] = new NumericProperty("rollingbasevalue" + j);
            pMeasurements[j] = new NumericProperty("measurement" + j);
            array.add(pMeasurements[j]);
            pLastMeasurements[j] = new NumericProperty("lastMeasurement" + j);
            array.add(pLastMeasurements[j]);
            pLastBaseMeasurements[j] = new NumericProperty("lastBaseMeasurement" + j);
            array.add(pLastBaseMeasurements[j]);
        }

        pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
        pLastMeasurementTicks = new NumericProperty("lastMeasurementTicks");
        pLastMeasurementWasNA = new NumericProperty("lastMeasurementWasNotAvailable");
        array.add(pPMCFile);
        array.add(pObject);
        array.add(pCounter);
        array.add(pUnits);
        array.add(pScale);
        array.add(pMonitorRunCount);
        array.add(pInstance);
        array.add(pLastMeasurementTime);
        array.add(pLastMeasurementTicks);
        array.add(pLastMeasurementWasNA);
        array.add(pErrCnt);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int k = 0; k < array.size(); k++)
        {
            astringproperty[k] = (StringProperty)array.at(k);
        }

        addProperties("COM.dragonflow.StandardMonitor.NTCounterMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.NTCounterMonitor", Rule.stringToClassifier(BrowsableBase.PROPERTY_NAME_COUNTERS_IN_ERROR + " > 0\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.NTCounterMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "description", "Retrieves the value of Windows NT Performance Counters.");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "help", "NTCtrMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "title", "Windows Performance Counter");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "class", "NTCounterMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "target", "_counter");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "toolName", "Performance Counters");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "toolDescription", "Provides an interface to perfex to list performance counters.");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "topazName", "NT Counter");
        setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "topazType", "System Resources");
        if(!Platform.isWindows() && TextUtils.getValue(hashmap, "_allowUnixToNT").length() <= 0)
        {
            setClassProperty("COM.dragonflow.StandardMonitor.NTCounterMonitor", "loadable", "false");
        }
    }
}
