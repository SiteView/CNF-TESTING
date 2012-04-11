/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * UnixCounterMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>UnixCounterMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.JdbcLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.*;

import java.io.File;
import java.util.*;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            NTCounterMonitor

public class UnixCounterMonitor extends ServerMonitor
{

    static StringProperty pObject;
    static StringProperty pCounter;
    static StringProperty pInstance;
    static StringProperty pUnits;
    static StringProperty pScale;
    static ScalarProperty pPMCFile;
    static StringProperty pValues[];
    static StringProperty pMeasurements[];
    static StringProperty pLastMeasurements[];
    static StringProperty pLastBaseMeasurements[];
    static StringProperty pLastMeasurementTime;
    static StringProperty pLastMeasurementTicks;
    static final String pmcFileDirName = "templates.perfmon";
    static final String pmcFileExtension = ".PMC";
    static final String pmwFileExtension = ".PMW";
    static final String htmFileExtension = ".HTM";
    static final String userObjectString = "(Custom Object)";
    static final String defaultFile = "System.pmc";
    static final String notAvailable = "n/a";
    static int nMaxCounters;
    boolean hasSubscription;
    static boolean showDebug = false;
    static HashMap allSettings = new HashMap();
    Array countersCache;
    HashMap labelsCache;
    static HashMap noData = new HashMap();

    public UnixCounterMonitor()
    {
        hasSubscription = false;
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

    public int getMaxCounters()
    {
        return nMaxCounters;
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
                String s1 = Platform.getRoot() + File.separator + "templates.perfmon" + File.separator + getProperty(pPMCFile);
                countersCache = (Array)allSettings.get(s1);
                if(countersCache == null)
                {
                    try
                    {
                        countersCache = PerfChartFile.GetSettings(s1);
                        allSettings.put(s1, countersCache);
                    }
                    catch(Exception exception)
                    {
                        countersCache = new Array();
                        LogManager.log("Error", "error reading Perf Chart file, " + s1 + ", " + exception);
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
            } else
            {
                Array array = getCounters();
                for(int i = 0; i < array.size(); i++)
                {
                    PerfCounter perfcounter = (PerfCounter)array.at(i);
                    labelsCache.add("Counter " + (i + 1) + " Value", perfcounter.counterName);
                }

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
        if(showDebug)
        {
            LogManager.log("RunMonitor", "server=" + s3 + ", data=" + s + ", object=" + perfcounter.object + ", counter=" + perfcounter.counterName + ", instance=" + perfcounter.instance + ", value=" + f);
        }
        Array array2 = new Array();
        array2.add(s3);
        array2.add(s);
        array2.add(perfcounter.object);
        array2.add(perfcounter.counterName);
        array2.add(perfcounter.instance);
        array2.add("" + f);
        JdbcLogger.logger.logCustom(this, array2, "perf_data", NTCounterMonitor.perf_data_create, NTCounterMonitor.perf_data_create2, NTCounterMonitor.perf_data_define, NTCounterMonitor.perf_data_insert);
    }

    protected boolean update()
    {
        String s = getProperty(pMachineName);
        long l = TextUtils.toLong(getProperty(pLastMeasurementTime));
        long l1 = TextUtils.toLong(getProperty(pLastMeasurementTicks));
        long l2 = 0L;
        long l3 = 1L;
        long l4 = 0L;
        Array array = getCounters();
        if(array.size() == 0)
        {
            setProperty(pLastMeasurementTime, 0);
            for(int i = 0; i < nMaxCounters; i++)
            {
                setProperty(pValues[i], "n/a");
                setProperty(pMeasurements[i], 0);
                setProperty(pLastMeasurements[i], 0);
            }

            setProperty(pStateString, "Perf Chart file error");
            return false;
        }
        if(array.size() > nMaxCounters)
        {
            String s1 = "_UnixCounterMonitorMaxCounters in master.config needs to be at least " + array.size() + " for the file \"" + getProperty(pPMCFile) + "\"";
            setProperty(pStateString, s1);
            return false;
        }
        long al[] = new long[nMaxCounters];
        long al1[] = new long[nMaxCounters];
        long al2[] = new long[nMaxCounters];
        String s2 = jdbcDateFormat(Platform.makeDate());
        Enumeration enumeration = null;
        long l5 = 0L;
        long l6 = 0L;
        float f = 0.0F;
        String s3 = "";
        boolean flag = true;
        for(int j = 0; flag && j < 2; j++)
        {
            StringBuffer stringbuffer = new StringBuffer();
            Array array1 = getUnixData(s, array, stringbuffer);
            s3 = stringbuffer.toString();
            enumeration = array1.elements();
            if(enumeration.hasMoreElements())
            {
                l2 = TextUtils.toLong((String)enumeration.nextElement());
            }
            if(enumeration.hasMoreElements())
            {
                l4 = TextUtils.toLong((String)enumeration.nextElement());
            }
            if(enumeration.hasMoreElements())
            {
                l3 = TextUtils.toLong((String)enumeration.nextElement());
            }
            l5 = l2 - l;
            l6 = l4 - l1;
            f = (float)l6 / (float)l3;
            flag = l5 <= 0L || l6 <= 0L || l <= 0L || l1 <= 0L;
            if(j > 0)
            {
                break;
            }
            if(!flag)
            {
                for(int i1 = 0; i1 < nMaxCounters; i1++)
                {
                    al[i1] = TextUtils.toLong(getProperty(pLastMeasurements[i1]));
                    al2[i1] = TextUtils.toLong(getProperty(pLastBaseMeasurements[i1]));
                }

                continue;
            }
            l = l2;
            l1 = l4;
            int j1 = 0;
            while(enumeration.hasMoreElements()) 
            {
                String s4 = (String)enumeration.nextElement();
                if(s4.equals("n/a"))
                {
                    enumeration.nextElement();
                    enumeration.nextElement();
                    al[j1] = 0L;
                    al2[j1++] = 0L;
                } else
                {
                    al[j1] = TextUtils.toLong((String)enumeration.nextElement());
                    al2[j1++] = TextUtils.toLong((String)enumeration.nextElement());
                }
            }
            Platform.sleep(2000L);
        }

        int k = 0;
        byte byte0 = 2;
        float f1 = 1.0F;
        if(!getProperty(pScale).equals("1"))
        {
            f1 = StringProperty.toFloat(getProperty(pScale));
            if(!Float.isNaN(f1) && f1 > 0.0F && f1 < 1.0F)
            {
                byte0 = 2;
            }
        }
        long l7 = getSettingAsLong("_NTCounterSummaryMax", 3);
        while(enumeration.hasMoreElements()) 
        {
            PerfCounter perfcounter = (PerfCounter)array.at(k);
            String s5 = (String)enumeration.nextElement();
            float f2 = (0.0F / 0.0F);
            long l8 = 0L;
            boolean flag1 = false;
            boolean flag2 = false;
            if(s5.equals("n/a"))
            {
                enumeration.nextElement();
                enumeration.nextElement();
            } else
            {
                al1[k] = TextUtils.toLong((String)enumeration.nextElement());
                long l9 = al1[k] - al[k];
                byte0 = 2;
                l8 = TextUtils.toLong((String)enumeration.nextElement());
                long l10 = l8 - al2[k];
                if(!flag)
                {
                    if(k > 0 && (long)k < l7)
                    {
                        s3 = s3 + ", ";
                    }
                    if(al1[k] >= 0L)
                    {
                        if(s5.equals("PERF_COUNTER_COUNTER"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / f;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_TIMER"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / (float)l6;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_TIMER_INV"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = 1.0F - (float)l9 / f;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_BULK_COUNT"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / f;
                                flag2 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_RAWCOUNT") || s5.equals("PERF_COUNTER_LARGE_RAWCOUNT") || s5.equals("PERF_COUNTER_RAWCOUNT_HEX") || s5.equals("PERF_COUNTER_LARGE_RAWCOUNT_HEX"))
                        {
                            f2 = al1[k];
                            byte0 = 0;
                        } else
                        if(s5.equals("PERF_ELAPSED_TIME"))
                        {
                            long l11 = al1[k];
                            if(l11 > l4 || l11 < 0L)
                            {
                                l11 >>>= 32;
                            }
                            f2 = (l4 - l11) / l3;
                            byte0 = 0;
                        } else
                        if(s5.equals("PERF_RAW_FRACTION"))
                        {
                            if(al1[k] == 0L)
                            {
                                f2 = 0.0F;
                            } else
                            {
                                f2 = (float)al1[k] / (float)l8;
                            }
                            flag1 = true;
                        } else
                        if(s5.equals("PERF_SAMPLE_FRACTION"))
                        {
                            if(l9 >= 0L && l10 > 0L)
                            {
                                f2 = (float)l9 / (float)l10;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_SAMPLE_COUNTER"))
                        {
                            if(l9 >= 0L && l10 > 0L)
                            {
                                f2 = (float)l9 / (float)l10;
                            }
                        } else
                        if(s5.equals("PERF_AVERAGE_TIME"))
                        {
                            f2 = (float)al1[k] / (float)l8;
                        } else
                        if(s5.equals("PERF_AVERAGE_BULK"))
                        {
                            if(l9 >= 0L && l10 > 0L)
                            {
                                f2 = (float)l9 / (float)l10;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_TIMER"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / (float)l5;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_TIMER_INV"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = 1.0F - (float)l9 / (float)l5;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_MULTI_TIMER"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / f / (float)l8;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_MULTI_TIMER_INV"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = 1.0F - (float)l9 / f / (float)l8;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_MULTI_TIMER"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = (float)l9 / (float)l5 / (float)l8;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_100NSEC_MULTI_TIMER_INV"))
                        {
                            if(l9 >= 0L)
                            {
                                f2 = 1.0F - (float)l9 / (float)l5 / (float)l8;
                                flag1 = true;
                            }
                        } else
                        if(s5.equals("PERF_COUNTER_QUEUELEN_TYPE"))
                        {
                            f2 = ((float)al[k] + (float)(l2 * al1[k])) / (float)l5;
                        } else
                        if(s5.equals("PERF_COUNTER_LARGE_QUEUELEN_TYPE"))
                        {
                            f2 = ((float)al[k] + (float)(l2 * al1[k])) / (float)l5;
                        } else
                        if(s5.equals("PERF_PRECISION_100NSEC_TIMER") && l9 >= 0L)
                        {
                            f2 = (float)l9 / (float)l10;
                            flag1 = true;
                        }
                        if(flag1)
                        {
                            byte0 = 2;
                            f2 *= 100F;
                        }
                    }
                }
            }
            if(stillActive())
            {
                synchronized(this)
                {
                    logPerfValue(perfcounter, s2, f2);
                    if(Float.isNaN(f2))
                    {
                        setProperty(pLastMeasurementTime, 0);
                        setProperty(pValues[k], "n/a");
                        setProperty(pMeasurements[k], 0);
                        setProperty(pLastMeasurements[k], 0);
                        if((long)k < l7)
                        {
                            s3 = s3 + perfcounter.counterName + " " + "n/a";
                        }
                    } else
                    {
                        f2 *= f1;
                        setProperty(pValues[k], TextUtils.floatToString(f2, byte0));
                        setProperty(pLastMeasurements[k], al1[k]);
                        setProperty(pLastBaseMeasurements[k], l8);
                        setProperty(pMeasurements[k], getMeasurement(pValues[k], 10L));
                        if((long)k < l7)
                        {
                            s3 = s3 + perfcounter.counterName + " = " + TextUtils.floatToString(f2, byte0);
                            if(getProperty(pUnits).length() > 0)
                            {
                                s3 = s3 + " " + getProperty(pUnits);
                            }
                            if(flag1)
                            {
                                s3 = s3 + "%";
                            }
                            if(flag2)
                            {
                                s3 = s3 + "/sec";
                            }
                        }
                    }
                }
            }
            if((long)k == l7)
            {
                s3 = s3 + "...";
            }
            k++;
        }
        setProperty(pLastMeasurementTime, l2);
        setProperty(pLastMeasurementTicks, l4);
        setProperty(pStateString, s3);
        return true;
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

    static int addUnixVMStat(String s, HashMap hashmap, StringBuffer stringbuffer)
    {
        String s1 = Machine.getCommandString("vmstat", s);
        if(s1.length() == 0)
        {
            String s2 = "error, reading vm command for " + s;
            stringbuffer.append(s2);
            LogManager.log("Error", s2);
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if(osadapter == null)
        {
            String s3 = "error, reading vm settings for " + s;
            stringbuffer.append(s3);
            LogManager.log("Error", s3);
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if(commandline.getExitValue() != 0)
        {
            return commandline.getExitValue();
        }
        int i = osadapter.getCommandSettingAsInteger("vmstat", "memoryFree");
        int j = osadapter.getCommandSettingAsInteger("vmstat", "cpuUser");
        int k = osadapter.getCommandSettingAsInteger("vmstat", "cpuSystem");
        LineReader linereader = new LineReader(array, osadapter, "vmstat");
        do
        {
            if(!linereader.processLine())
            {
                break;
            }
            if(linereader.skipLine())
            {
                continue;
            }
            String s4 = linereader.readColumn(i, "memoryFree");
            if(s4.length() > 0)
            {
                long l = TextUtils.readLong(s4, 0) * 1024L;
                hashmap.put("Available Bytes", "" + l);
                if(showDebug)
                {
                    LogManager.log("RunMonitor", "Available Bytes=" + l);
                }
            }
            long l1 = 0L;
            s4 = linereader.readColumn(j, "cpuUser");
            if(s4.length() > 0)
            {
                l1 = TextUtils.readLong(s4, 0);
                hashmap.put("% Total User Time", "" + l1);
                if(showDebug)
                {
                    LogManager.log("RunMonitor", "% Total User Time=" + l1);
                }
            }
            s4 = linereader.readColumn(k, "cpuSystem");
            if(s4.length() > 0)
            {
                long l2 = TextUtils.readLong(s4, 0) + l1;
                hashmap.put("% Total Processor Time", "" + l2);
                if(showDebug)
                {
                    LogManager.log("RunMonitor", "% Total Processor Time=" + l2);
                }
            }
            break;
        } while(true);
        return 0;
    }

    static void appendOutput(String s, String s1, int i, Array array)
    {
        if(showDebug)
        {
            LogManager.log("RunMonitor", "asset command=" + s1);
            LogManager.log("RunMonitor", " result=" + i);
            LogManager.log("RunMonitor", " machine=" + s);
            for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); LogManager.log("RunMonitor", " output=" + enumeration.nextElement())) { }
        }
    }

    static int addUnixDrives(String s, HashMap hashmap, StringBuffer stringbuffer)
    {
        String s1 = Machine.getCommandString("disks", s);
        if(s1.length() == 0)
        {
            stringbuffer.append("error, reading disk command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if(osadapter == null)
        {
            stringbuffer.append("error, reading disk settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if(commandline.getExitValue() != 0)
        {
            return commandline.getExitValue();
        }
        Enumeration enumeration = array.elements();
        int i = osadapter.getCommandSettingAsInteger("disks", "total");
        int j = osadapter.getCommandSettingAsInteger("disks", "free");
        int k = osadapter.getCommandSettingAsInteger("disks", "mount");
        long l = 0L;
        long l1 = 0L;
        LineReader linereader = new LineReader(array, osadapter, "disks");
        do
        {
            if(!linereader.processLine())
            {
                break;
            }
            if(!linereader.skipLine())
            {
                String s2 = linereader.getCurrentLine();
                int i1 = i;
                int j1 = j;
                int k1 = k;
                if(s2.startsWith("/dev"))
                {
                    String s3 = linereader.readColumn(j1, "free");
                    long l2 = 0L;
                    long l3 = 0L;
                    if(s3.length() == 0 && enumeration.hasMoreElements())
                    {
                        linereader.processLine();
                        i1--;
                        j1--;
                        k1--;
                        s3 = linereader.readColumn(j1, "free");
                    }
                    if(s3.length() > 0)
                    {
                        l2 = TextUtils.readLong(s3, 0);
                        l += l2;
                        if(showDebug)
                        {
                            LogManager.log("RunMonitor", "Disk Free K=" + l);
                        }
                    }
                    s3 = linereader.readColumn(i1, "total");
                    if(s3.length() > 0)
                    {
                        l3 = TextUtils.readLong(s3, 0);
                        l1 += l3;
                        if(showDebug)
                        {
                            LogManager.log("RunMonitor", "Disk Total K=" + l1);
                        }
                    }
                    String s4 = linereader.readColumn(k1, "mount");
                    if(l3 != 0L)
                    {
                        hashmap.put(getKey("% Free Space", s4), "" + (100L * l2) / l3);
                    }
                    hashmap.put(getKey("Free Megabytes", s4), "" + l2 / 1024L);
                }
            }
        } while(true);
        if(l1 != 0L)
        {
            hashmap.put("% Free Space", "" + (100L * l) / l1);
        }
        hashmap.put("Free Megabytes", "" + l / 1024L);
        return 0;
    }

    static int addUnixMemory(String s, HashMap hashmap, StringBuffer stringbuffer)
    {
        long al[] = Platform.getMemoryFull(s, 0L, 0L);
        long l = al[0];
        if(l == -1L)
        {
            return -1;
        } else
        {
            hashmap.put("% Committed Bytes In Use", "" + l);
            return 0;
        }
    }

    static int addUnixPaging(String s, HashMap hashmap, StringBuffer stringbuffer)
    {
        String s1 = Machine.getCommandString("paging", s);
        if(s1.length() == 0)
        {
            stringbuffer.append("error, reading paging command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if(osadapter == null)
        {
            stringbuffer.append("error, reading paging settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        long l = osadapter.getCommandSettingAsInteger("paging", "blockSize");
        if(l == 0L)
        {
            stringbuffer.append("error, reading paging blocksize for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        int i = osadapter.getCommandSettingAsInteger("paging", "blocks");
        int j = osadapter.getCommandSettingAsInteger("paging", "free");
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if(commandline.getExitValue() != 0)
        {
            return commandline.getExitValue();
        }
        long l1 = 0L;
        long l2 = 0L;
        LineReader linereader = new LineReader(array, osadapter, "paging");
        do
        {
            if(!linereader.processLine())
            {
                break;
            }
            if(!linereader.skipLine())
            {
                String s2 = linereader.readColumn(j, "free");
                if(s2.length() > 0)
                {
                    l1 += TextUtils.readLong(s2, 0);
                    if(showDebug)
                    {
                        LogManager.log("RunMonitor", "Paging Free blocks=" + l1);
                    }
                }
                s2 = linereader.readColumn(i, "blocks");
                if(s2.length() > 0)
                {
                    l2 += TextUtils.readLong(s2, 0);
                    if(showDebug)
                    {
                        LogManager.log("RunMonitor", "Paging Total blocks=" + l2);
                    }
                }
            }
        } while(true);
        if(l2 != 0L && l != 0L)
        {
            hashmap.put("% Usage", "" + (100L - (100L * l1 * l) / (l2 * l)));
        } else
        {
            hashmap.put("% Usage", "0");
        }
        if(showDebug)
        {
            LogManager.log("RunMonitor", "% Usage=" + hashmap.get("% Usage"));
        }
        return 0;
    }

    static int addUnixNetworks(String s, HashMap hashmap, StringBuffer stringbuffer)
    {
        String s1 = Machine.getCommandString("network", s);
        if(s1.length() == 0)
        {
            stringbuffer.append("error, reading network command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if(osadapter == null)
        {
            stringbuffer.append("error, reading network settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if(commandline.getExitValue() != 0)
        {
            return commandline.getExitValue();
        }
        String s2 = osadapter.getCommandSetting("network", "bytesSentMatch");
        String s3 = osadapter.getCommandSetting("network", "bytesReceivedMatch");
        String s4 = osadapter.getCommandSetting("network", "packetsSentMatch");
        String s5 = osadapter.getCommandSetting("network", "packetsReceivedMatch");
        LineReader linereader = new LineReader(array, osadapter, "network");
        do
        {
            if(!linereader.processLine())
            {
                break;
            }
            if(!linereader.skipLine())
            {
                String s6 = linereader.getCurrentLine();
                addNetValue(s6, s2, hashmap, "Bytes Sent/sec");
                addNetValue(s6, s3, hashmap, "Bytes Received/sec");
                addNetValue(s6, s4, hashmap, "Packets Sent/sec");
                addNetValue(s6, s5, hashmap, "Packets Received/sec");
            }
        } while(true);
        return 0;
    }

    static void addNetValue(String s, String s1, HashMap hashmap, String s2)
    {
        int i = s.indexOf(s1);
        if(i != -1)
        {
            String s3 = s.substring(i + s1.length());
            s3 = s3.trim();
            if(s3.startsWith("="))
            {
                s3 = s3.substring(1);
                long l = TextUtils.readLong(s3, 0);
                hashmap.put(s2, "*" + l);
                if(showDebug)
                {
                    LogManager.log("RunMonitor", s2 + "=" + l);
                }
            }
        }
    }

    static String getKey(String s, String s1)
    {
        String s2 = s;
        if(s1.length() > 0 && s1.toLowerCase().indexOf("total") == -1)
        {
            s2 = s2 + "(" + s1 + ")";
        }
        return s2;
    }

    public static Array getUnixData(String s, Array array, StringBuffer stringbuffer)
    {
        long l = Platform.timeMillis();
        String s1 = "" + l;
        String s2 = "1000";
        String s3 = "" + l;
        int i = array.size() <= nMaxCounters ? array.size() : nMaxCounters;
        boolean aflag[] = new boolean[i];
        String as[] = new String[i];
        String as1[] = new String[i];
        String as2[] = new String[i];
        for(int j = 0; j < i; j++)
        {
            aflag[j] = false;
        }

        HashMap hashmap = new HashMap();
        int k = addUnixVMStat(s, hashmap, stringbuffer);
        if(k == 0)
        {
            k = addUnixDrives(s, hashmap, stringbuffer);
        }
        if(k == 0)
        {
            k = addUnixMemory(s, hashmap, stringbuffer);
        }
        if(k == 0)
        {
            k = addUnixNetworks(s, hashmap, stringbuffer);
        }
        if(k == 0)
        {
            k = addUnixPaging(s, hashmap, stringbuffer);
        }
        if(k != 0)
        {
            stringbuffer.append(lookupStatus(k) + ", ");
            LogManager.log("Error", "Counter Monitor error: " + stringbuffer + s);
        } else
        {
            for(int i1 = 0; i1 < i; i1++)
            {
                PerfCounter perfcounter = (PerfCounter)array.at(i1);
                String s4 = (String)hashmap.get(getKey(perfcounter.counterName, perfcounter.instance));
                if(s4 == null)
                {
                    continue;
                }
                aflag[i1] = true;
                String s6 = "PERF_COUNTER_RAWCOUNT";
                if(s4.startsWith("*"))
                {
                    s4 = s4.substring(1);
                    s6 = "PERF_COUNTER_COUNTER";
                }
                as[i1] = s6;
                as1[i1] = s4;
                as2[i1] = "0";
            }

        }
        Array array1 = new Array();
        array1.add(s1);
        array1.add(s3);
        array1.add(s2);
        for(int j1 = 0; j1 < i; j1++)
        {
            if(aflag[j1])
            {
                array1.add(as[j1]);
                array1.add(as1[j1]);
                array1.add(as2[j1]);
            } else
            {
                String s5 = "";
                array1.add(s5);
                array1.add(s5);
                array1.add(s5);
            }
        }

        return array1;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        int i = getActiveCounters();
        for(int j = 0; j < i; j++)
        {
            array.add(pValues[j]);
            array.add(pMeasurements[j]);
            array.add(pLastMeasurements[j]);
        }

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

    static 
    {
        nMaxCounters = 0;
        Array array = new Array();
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_UnixCounterMonitorMaxCounters"));
        if(nMaxCounters == 0)
        {
            nMaxCounters = 8;
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
        pValues = new StringProperty[nMaxCounters];
        pMeasurements = new StringProperty[nMaxCounters];
        pLastMeasurements = new StringProperty[nMaxCounters];
        pLastBaseMeasurements = new StringProperty[nMaxCounters];
        for(int i = 0; i < nMaxCounters; i++)
        {
            pValues[i] = new NumericProperty("value" + i);
            pValues[i].setDisplayText("Counter " + (i + 1) + " Value", "The NT Performance Counter");
            pValues[i].setStateOptions(i + 1);
            array.add(pValues[i]);
            pMeasurements[i] = new NumericProperty("measurement" + i);
            array.add(pMeasurements[i]);
            pLastMeasurements[i] = new NumericProperty("lastMeasurement" + i);
            array.add(pLastMeasurements[i]);
            pLastBaseMeasurements[i] = new NumericProperty("lastBaseMeasurement" + i);
            array.add(pLastBaseMeasurements[i]);
        }

        pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
        pLastMeasurementTicks = new NumericProperty("lastMeasurementTicks");
        array.add(pPMCFile);
        array.add(pObject);
        array.add(pCounter);
        array.add(pUnits);
        array.add(pScale);
        array.add(pInstance);
        array.add(pLastMeasurementTime);
        array.add(pLastMeasurementTicks);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int j = 0; j < array.size(); j++)
        {
            astringproperty[j] = (StringProperty)array.at(j);
        }

        addProperties("COM.dragonflow.StandardMonitor.UnixCounterMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.UnixCounterMonitor", Rule.stringToClassifier("value0 == n/a\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.UnixCounterMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "description", "Retrieves the value of an arbitrary Windows NT Performance Counter.");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "help", "UnixCtrMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "title", "Unix Performance Counter");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "class", "UnixCounterMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "target", "_counter");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "topazName", "Unix Counter");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "topazType", "System Resources");
        setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "classType", "advanced");
        if((new File(Platform.getRoot() + File.separator + "assets")).exists())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "loadable", "true");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.UnixCounterMonitor", "loadable", "false");
        }
    }
}
