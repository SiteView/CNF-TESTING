 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package COM.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.monitorUtils;

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils, I18N, LocaleUtils

public class LUtils
{
    static class HeartbeatSessionTask extends java.util.TimerTask
        implements java.lang.Runnable
    {

        public void run()
        {
            try
            {
                if(java.lang.System.currentTimeMillis() > sessionLastHeartbeat + sessionTimeout)
                {
                    COM.dragonflow.Log.LogManager.log("error", "Session expired after " + sessionTimeout + " milliseconds, no heartbeat recieved.");
                    COM.dragonflow.Log.LogManager.log("error", "Shutting down SiteView.");
                    hb.cancel();
                    sessionShuttingDownOfSS = true;
                    SiteViewMain.SiteViewSupport.ShutdownProcess();
                }
            }
            catch(java.lang.Exception exception)
            {
                exception.printStackTrace();
            }
        }

        HeartbeatSessionTask()
        {
        }
    }


    public static final int kInvalid = -1;
    public static final int kEvaluation = 0;
    public static final int kExtension = 1;
    public static final int kSubscription = 2;
    public static final int kPermanent = 3;
    public static final int kHaExtension = 4;
    public static final int kHaSubscription = 5;
    public static final int kHaPermanent = 6;
    public static final int kTopazWatchdog = 7;
    public static final int kPersonalEdition = 8;
    private static final String strTagExtension = "EX";
    private static final String strTagSubscription = "SR";
    private static final String strTagPermanent = "PM";
    private static final String strTagHaExtension = "HX";
    private static final String strTagHaSubscription = "HS";
    private static final String strTagHaPermanent = "HA";
    private static final String strTagPersonalEdition = "PE";
    private static final String strKeyInvalid = "Invalid";
    private static final String strKeyEvaluation = "Evaluation";
    private static final String strKeyExtension = "Extension";
    private static final String strKeySubscription = "Subscription";
    private static final String strKeyPermanent = "SiteView";
    private static final String strKeyHaExtension = "HA Extension";
    private static final String strKeyHaSubscription = "HA Subscription";
    private static final String strKeyHaPermanent = "HA SiteView";
    private static final String strKeyTopazWatchdog = "Topaz Watchdog";
    private static final String strKeyPersonalEdition = "Personal Edition";
    private static final int nDaysTextWarningExt = 90;
    private static final int nDaysMailWarningExt = 7;
    private static final int nDaysShutdownExt = 0;
    private static final int nDaysTextWarningSub = 14;
    private static final int nDaysMailWarningSub = 14;
    private static final int nDaysShutdownSub = 0;
    private static final int nDaysMailWarningEval = -13;
    private static final int nDaysShutdownEval = -20;
    public static final int nDefaultPoints = 0;
    public static final int nPointRounding = 25;
    private static final int nEvaluationPoints = 1000;
    private static final int nPermanentDays = 10000;
    private static final int nMaxValidPoints = 0x186a0;
    private static final int nIndexLicenseTypeStart = 0;
    private static final int nIndexLicenseTypeEnd = 1;
    private static final int nIndexExpireTimeStart = 2;
    private static final int nIndexExpireTimeEnd = 9;
    private static final int nIndexNumPointsStart = 10;
    private static final int nIndexNumPointsEnd = 15;
    private static final int nIndexDelimiter = 16;
    private static final int nIndexMonitorTypeStart = 17;
    private static final int nIndexMonitorTypeEnd = 18;
    private static final int nIndexChecksumStart = 19;
    private static final int nIndexChecksumEnd = 22;
    private static final int nIndexLast = 22;
    public static final int nMonTypeInvalid = 0;
    public static final int nMonTypeApache = 1;
    public static final int nMonTypeAriba = 2;
    public static final int nMonTypeASP = 3;
    public static final int nMonTypeAsset = 4;
    public static final int nMonTypeCheckPoint = 5;
    public static final int nMonTypeCheckPointOPSEC = 6;
    public static final int nMonTypeCisco = 7;
    public static final int nMonTypeColdFusion = 8;
    public static final int nMonTypeComposite = 9;
    public static final int nMonTypeCPU = 10;
    public static final int nMonTypeDatabase = 11;
    public static final int nMonTypeDHCP = 12;
    public static final int nMonTypeDirectory = 13;
    public static final int nMonTypeDiskSpace = 14;
    public static final int nMonTypeDNS = 15;
    public static final int nMonTypeDynamo = 16;
    public static final int nMonTypeEBusinessTransaction = 17;
    public static final int nMonTypeF5 = 18;
    public static final int nMonTypeFile = 19;
    public static final int nMonTypeFTP = 20;
    public static final int nMonTypeIISServer = 21;
    public static final int nMonTypeLDAP = 22;
    public static final int nMonTypeLink = 23;
    public static final int nMonTypeLog = 24;
    public static final int nMonTypeMail = 25;
    public static final int nMonTypeMemory = 26;
    public static final int nMonTypeNetscape = 27;
    public static final int nMonTypeNetwork = 28;
    public static final int nMonTypeNews = 29;
    public static final int nMonTypeNTCounter = 30;
    public static final int nMonTypeNTDialup = 31;
    public static final int nMonTypeNTEventLog = 32;
    public static final int nMonTypeOracle9i = 33;
    public static final int nMonTypePing = 34;
    public static final int nMonTypePingRemote = 35;
    public static final int nMonTypePort = 36;
    public static final int nMonTypeRadius = 37;
    public static final int nMonTypeReal = 38;
    public static final int nMonTypeRTSP = 39;
    public static final int nMonTypeScript = 40;
    public static final int nMonTypeService = 41;
    public static final int nMonTypeSilverStream = 42;
    public static final int nMonTypeSimulation = 43;
    public static final int nMonTypeSiteSeer = 44;
    public static final int nMonTypeSiteSeer2 = 45;
    public static final int nMonTypeSNMP = 46;
    public static final int nMonTypeSNMPTrap = 47;
    public static final int nMonTypeSQLServer = 48;
    public static final int nMonTypeSybase = 49;
    public static final int nMonTypeTelnet = 50;
    public static final int nMonTypeUnixCounter = 51;
    public static final int nMonTypeURL = 52;
    public static final int nMonTypeURLContent = 53;
    public static final int nMonTypeURLList = 54;
    public static final int nMonTypeURLRemote = 55;
    public static final int nMonTypeURLRemoteSequence = 56;
    public static final int nMonTypeURLSequence = 57;
    public static final int nMonTypeWebLogic5x = 58;
    public static final int nMonTypeWebServer = 59;
    public static final int nMonTypeWebService = 60;
    public static final int nMonTypeWebSphere = 61;
    public static final int nMonTypeWindowsMedia = 62;
    public static final int nMonTypeBV55 = 63;
    public static final int nMonTypeSiebel = 64;
    public static final int nMonTypeQT = 65;
    public static final int nMonTypeALT = 66;
    public static final int nMonTypePortRemote = 67;
    public static final int nMonTypeWebLogic6x = 68;
    public static final int nMonTypeSAPPotrtal = 69;
    public static final int nMonTypeCCMSSAP = 70;
    public static final int nMonTypeEJB = 71;
    public static final int nMonTypeJ2EE = 72;
    public static final int nMonTypeComPlus = 73;
    public static final int nMonTypeEms = 74;
    public static final int nMonTypeMQStatus = 75;
    public static final int nMonTypeSunOne = 76;
    public static final int nMonTypeExchange = 77;
    public static final int nMonTypeActiveDirectory = 78;
    public static final int nSolutionTypeWebLogic = 79;
    public static final int nSolutionTypeWebSphere = 80;
    public static final int nSolutionTypeOracle = 81;
    public static final int nSolutionTypeSiebel = 64;
    public static final int nMonTypeAll = 99;
    static jgl.HashMap monNameToType = new HashMap();
    static jgl.HashMap siteSeerMonNameToType = new HashMap();
    static long sessionTimeout = 0L;
    static boolean sessionActive = false;
    static String sessionLicense = "";
    static String sessionLicenseForX = "";
    static long sessionLastHeartbeat = 0L;
    static boolean sessionShuttingDownOfSS = false;
    static HeartbeatSessionTask hb = null;
    private static final String UNKNOWN_CLASS = "unknown";
    public static final String WEBSPHERE_SOLUTION = "WebSphereSolution";
    public static final String WEBLOGIC_SOLUTION = "WebLogicSolution";
    public static final String ORACLE_SOLUTION = "OracleSolution";
    public static final String SIEBEL_SOLUTION = "SiebelSolution";
    private static long lLastCurrentMinutes = 0L;

    public LUtils()
    {
    }

    private static AtomicMonitor createMonitor(String s)
    {
        AtomicMonitor atomicmonitor = null;
        try
        {
            int i = s.indexOf(' ');
            if(i >= 0)
            {
                String s1 = s.substring(0, i);
                String s2 = s.substring(i + 1);
                String s3 = Platform.getRoot() + "/groups/" + s1 + ".mg";
                jgl.Array array = COM.dragonflow.Properties.FrameFile.readFromFile(s3);
                jgl.HashMap hashmap = monitorUtils.findMonitor(array, s2);
                atomicmonitor = (AtomicMonitor)AtomicMonitor.createMonitor(hashmap, "");
            }
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "LUtils.createMonitor(), error loading monitor, " + exception);
            java.lang.System.err.println("Error loading monitor, " + exception);
            exception.printStackTrace();
        }
        return atomicmonitor;
    }

    private static String generateChecksum(String s)
    {
        String s1 = "0000";
        long l = 4L;
        long l1 = 5L;
        byte abyte0[] = s.getBytes();
        for(int i = 0; i < abyte0.length; i++)
        {
            if(i % 3 == 0)
            {
                l += abyte0[i] + 2;
                l1 *= abyte0[i] + 3;
                continue;
            }
            if(i % 3 == 1)
            {
                l *= abyte0[i] + 4;
                l1 += abyte0[i] + 5;
                continue;
            }
            if(i % 3 == 2)
            {
                l *= abyte0[i] + 6;
                l1 *= abyte0[i] + 7;
            }
        }

        long l2 = l * l1 + l;
        if(l2 < 0L)
        {
            l2 = -l2;
        }
        int j = (int)(l2 % 10000L);
        s1 = COM.dragonflow.Utils.TextUtils.numberToString(j);
        s1 = COM.dragonflow.Utils.TextUtils.padWithZeros(s1, 4);
        return s1;
    }

    private static long generateExpireTime(int i)
    {
        long l = COM.dragonflow.Utils.TextUtils.timeSeconds() / (long)COM.dragonflow.Utils.TextUtils.MINUTE_SECONDS;
        if(l <= lLastCurrentMinutes)
        {
            l = lLastCurrentMinutes + 1L;
        }
        lLastCurrentMinutes = l;
        long l1 = (long)i * 24L * 60L;
        long l2 = l + l1;
        return l2;
    }

    public static String generateLicenseKey(String s, int i, int j)
    {
        s = s.toUpperCase();
        byte byte0 = 99;
        int k = oldLicenseGetDaysRemaining(s);
        int l = oldLicenseGetMonitors(s);
        int i1 = oldLicenseGetType(s);
        int j1 = (j * l) / i;
        j1 = (j1 / 25 + 1) * 25;
        COM.dragonflow.Log.LogManager.log("RunMonitor", "generateLicenseKey(" + s + "," + i + "," + j + "): nNumPoints=" + j1);
        return generateLicenseKey(j1, i1, byte0, k);
    }

    public static String generateLicenseKey(int i)
    {
        byte byte0 = 3;
        return generateLicenseKey(i, byte0);
    }

    public static String generateLicenseKey(int i, int j)
    {
        byte byte0 = 99;
        return generateLicenseKey(i, j, byte0);
    }

    public static String generateLicenseKey(int i, int j, int k)
    {
        char c = '\u2710';
        return generateLicenseKey(i, j, k, c);
    }

    public static String generateLicenseKey(int i, int j, int k, int l)
    {
        String s2 = getLicenseTypeTag(j);
        if(s2.length() == 0)
        {
            String s = "LicenseTypeError";
            return s;
        } else
        {
            byte byte0 = 8;
            long l1 = generateExpireTime(l);
            String s3 = String.valueOf(l1);
            s3 = COM.dragonflow.Utils.TextUtils.padWithZeros(s3, byte0);
            byte0 = 6;
            String s4 = String.valueOf(i);
            s4 = COM.dragonflow.Utils.TextUtils.padWithZeros(s4, byte0);
            byte0 = 2;
            String s5 = String.valueOf(k);
            s5 = COM.dragonflow.Utils.TextUtils.padWithZeros(s5, byte0);
            byte0 = 4;
            String s1 = s2 + s3 + s4 + "-" + s5;
            String s6 = generateChecksum(s1);
            s1 = s1 + s6;
            return s1;
        }
    }

    private static String getChecksum(String s)
    {
        return s.substring(19, 23);
    }

    public static int getCostInLicensePoints(jgl.Array array, String s)
    {
        int i = 0;
        if(array != null)
        {
            for(int j = 0; j < array.size(); j++)
            {
                if(array.at(j) instanceof String)
                {
                    String s1 = (String)array.at(j);
                    AtomicMonitor atomicmonitor = createMonitor(s1);
                    if(atomicmonitor != null)
                    {
                        i += atomicmonitor.getCostInLicensePoints();
                    }
                    continue;
                }
                if(!(array.at(j) instanceof jgl.HashMap) || s == null)
                {
                    continue;
                }
                jgl.Array array1 = new Array();
                jgl.HashMap hashmap = (jgl.HashMap)array.at(j);
                String s2 = null;
                Enumeration enumeration = hashmap.keys();
                Enumeration enumeration1 = hashmap.elements();
                while (enumeration.hasMoreElements() && enumeration1.hasMoreElements())
                    {
                    String s3 = enumeration.nextElement().toString();
                    if(s3.compareTo("_id") == 0)
                    {
                        String s4 = (String)hashmap.get(s3);
                        String s5 = s + " " + s4;
                        String s6 = (String)hashmap.get("_class");
                        if(s6 != null && s6.equals("SubGroup"))
                        {
                            s2 = COM.dragonflow.Utils.I18N.toDefaultEncoding((String)hashmap.get("_group"));
                            String s7 = Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + s2 + ".mg";
                            try
                            {
                                jgl.Array array2 = COM.dragonflow.Properties.FrameFile.readFromFile(s7);
                                int k = 0;
                                while(k < array2.size()) 
                                {
                                    jgl.HashMap hashmap1 = (jgl.HashMap)array2.at(k);
                                    String s8 = (String)hashmap1.get("_id");
                                    if(s8 != null)
                                    {
                                        array1.add(hashmap1);
                                    }
                                    k++;
                                }
                            }
                            catch(java.lang.Exception exception)
                            {
                                COM.dragonflow.Log.LogManager.log("RunMonitor", "An exeception occurred trying to retrieve the subgroup: " + enumeration1);
                            }
                        } else
                        if(s6 != null)
                        {
                            array1.add(s5);
                        }
                    }
                } 
                i += getCostInLicensePoints(array1, s2);
            }

        }
        return i;
    }

    public static boolean isValidSSforXLicense(AtomicMonitor atomicmonitor)
    {
        String s = getLicenseForXKey();
        return isValidSSforXLicense(atomicmonitor, s);
    }

    public static boolean isValidSSforXLicense(AtomicMonitor atomicmonitor, String s)
    {
        if(s.length() <= 0)
        {
            return false;
        }
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        for(int i = 0; i < as.length; i++)
        {
            if(isMonitorTypeAllowed(atomicmonitor, as[i]) && getDaysRemaining(as[i]) > 0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidSSforXLicense(String s)
    {
        String s1 = getLicenseForXKey();
        if(s1.length() <= 0)
        {
            return false;
        }
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        for(int i = 0; i < as.length; i++)
        {
            if(isMonitorOrSolutionTypeAllowed(s, as[i]) && getDaysRemaining(as[i]) > 0)
            {
                return true;
            }
        }

        return false;
    }

    public static int getDaysRemaining(String s)
    {
        int i = -21;
        long l = getExpirationTime(s.trim());
        if(l > 0L)
        {
            long l1 = COM.dragonflow.Utils.TextUtils.timeSeconds() / (long)COM.dragonflow.Utils.TextUtils.MINUTE_SECONDS;
            long l2 = l - l1;
            i = (int)(l2 / 1440L);
        }
        return i;
    }

    public static String getExpirationDate(String s)
    {
        String s1 = "";
        if(isPermanentLicense(s))
        {
            s1 = "n/a";
        } else
        {
            long l = getExpirationTime(s);
            long l1 = l * 60L * 1000L;
            java.util.Date date = new Date(l1);
            s1 = COM.dragonflow.Utils.TextUtils.prettyDateDate(date);
        }
        return s1;
    }

    private static long getExpirationTime(String s)
    {
        long l = 0L;
        if(isValidLicense(s))
        {
            String s1 = s.substring(2, 10);
            l = COM.dragonflow.Utils.TextUtils.toInt(s1);
        } else
        if(getLicenseType(s) == 0)
        {
            long l1 = COM.dragonflow.Utils.TextUtils.toLong(COM.dragonflow.Utils.TextUtils.getValue(getMasterConfig(), "_installTime"));
            long l2 = 10 * COM.dragonflow.Utils.TextUtils.DAY_SECONDS;
            long l3 = COM.dragonflow.Utils.TextUtils.timeSeconds();
            if(l1 == 0L)
            {
                l = l3 / (long)COM.dragonflow.Utils.TextUtils.MINUTE_SECONDS;
            } else
            if(l1 <= l3)
            {
                l = (l1 + l2) / (long)COM.dragonflow.Utils.TextUtils.MINUTE_SECONDS;
            }
        }
        return l;
    }

    public static int getLicensedPoints()
    {
        String s = getLicenseKey();
        return getLicensedPoints(s);
    }

    public static int getLicensedPoints(String s)
    {
        int i = 0;
        if(isValidLicense(s))
        {
            String s1 = s.substring(10, 16);
            i = COM.dragonflow.Utils.TextUtils.toInt(s1);
            if(i < 0)
            {
                i = 1;
            }
        } else
        if(getLicenseType(s) == 0)
        {
            i = 1000;
        }
        return i;
    }

    public static String getLicenseErrorString(String s)
    {
        String s1 = "";
        int i = getLicenseType(s);
        String s2 = getLicenseTypeString(i);
        switch(i)
        {
        case 3: // '\003'
        case 6: // '\006'
        case 7: // '\007'
            break;

        case 0: // '\0'
        case 1: // '\001'
        case 2: // '\002'
        case 4: // '\004'
        case 5: // '\005'
            if(getDaysRemaining(s) < 0)
            {
                s1 = s2 + " license expired.";
            }
            break;

        case 8: // '\b'
        default:
            s1 = s1 + "Invalid license key.";
            break;
        }
        return s1;
    }

    public static String getLicenseExceededHTML()
    {
        return getLicenseExceededHTML(getMasterConfig());
    }

    public static String getLicenseExceededHTML(jgl.HashMap hashmap)
    {
        int i = getLicensedPoints();
        String s = "<p>This operation would exceed your limit of " + i + " points for this license.\r\n" + "<p>Upgrade your license to allow more points by <a href=" + Platform.homeURLPrefix + "/OrderOpts.htm>Ordering Online</a> or sending e-mail to <a href=mailto:" + Platform.salesEmail + ">" + Platform.salesEmail + "</a>.\r\n" + "<p>Temporary licenses may also be available." + "<br><br>" + getLicenseSummary(hashmap, true) + "\r\n";
        return s;
    }

    public static String getLicenseKey()
    {
        String session = "";
        if(sessionActive)
        {
            session = sessionLicense;
        } else
        {
        	session = getLicenseKey(getMasterConfig());
        }
        return session;
    }

    public static String getLicenseForXKey()
    {
        String s = "";
        if(sessionActive)
        {
            s = sessionLicenseForX;
        } else
        {
            s = getLicenseForXKey(getMasterConfig());
        }
        return s;
    }

    public static String getLicenseKey(jgl.HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_license");
        return s;
    }

    public static String getLicenseForXKey(jgl.HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_licenseForX");
        return s;
    }

    public static String getLicenseSummary()
    {
        String s = getLicenseKey();
        boolean flag = true;
        String s1 = Platform.productName;
        return getLicenseSummary(s, flag, s1, false);
    }

    public static String getLicenseSummary(jgl.HashMap hashmap, boolean flag)
    {
        String s = getLicenseKey(hashmap);
        String s1 = Platform.productName;
        String s2 = getLicenseForXKey(hashmap);
        boolean flag1 = false;
        if(s2.length() > 0)
        {
            flag1 = true;
        }
        return getLicenseSummary(s, flag, s1, flag1);
    }

    public static String getLicenseSummary(String s, boolean flag, String s1, boolean flag1)
    {
        String s2 = "";
        if(isValidLicense(s))
        {
            int i = getLicenseType(s);
            int k = getLicensedPoints(s);
            int i1 = getMonitorType(s);
            int j1 = getDaysRemaining(s);
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            SiteViewGroup _tmp = siteviewgroup;
            int k1 = SiteViewGroup.totalPointsUsed;
            if(k > 0 && k1 > k)
            {
                flag = true;
            }
            byte byte0 = ((byte)(i != 2 ? 90 : 14));
            if(j1 <= byte0)
            {
                flag = true;
            }
            if(i1 != 99)
            {
                flag = true;
            }
            if(flag)
            {
                String s3 = "";
                switch(i)
                {
                case 0: // '\0'
                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 8: // '\b'
                    s3 = getLicenseTypeString(i);
                    break;
                }
                if(i1 == 99)
                {
                    s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseLabel");
                    if(s3.equals("Invalid"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseInvalid");
                    } else
                    if(s3.equals("Evaluation"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseEvaluation");
                    } else
                    if(s3.equals("Extension"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseExtension");
                    } else
                    if(s3.equals("Subscription"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseSubscription");
                    } else
                    if(s3.equals("SiteView"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseSiteView");
                    } else
                    if(s3.equals("HA Extension"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseHAExtension");
                    } else
                    if(s3.equals("HA Subscription"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseHASubscription");
                    } else
                    if(s3.equals("HA SiteView"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseHASiteView");
                    } else
                    if(s3.equals("Topaz Watchdog"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseTopazWatchdog");
                    } else
                    if(s3.equals("Personal Edition"))
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseSiteViewPersonalEdition");
                    }
                }
                if(!Platform.isPortal() && i1 == 99)
                {
                    s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("Points") + " " + k;
                    s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("Used") + " " + k1;
                    if(flag1)
                    {
                        s2 = s2 + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("OptionMonitors");
                    }
                }
                if(i1 != 99)
                {
                    String s4 = getMonitorTypeString(i1);
                    s2 = s2 + " " + s4;
                }
                if(j1 < 0)
                {
                    s2 = s2 + "\n" + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("LicenseExpired") + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("DaysRemaining") + " " + j1;
                } else
                if(j1 < 90)
                {
                    s2 = s2 + "\n" + COM.dragonflow.Utils.LocaleUtils.getResourceBundle().getString("DaysRemaining") + " " + j1;
                }
            }
        } else
        if(getLicenseType(s) == 0)
        {
            int j = getDaysRemaining(s);
            int l = getLicensedPoints(s);
            s2 = "Evaluation license for " + l + " points, " + j + " days remaining";
        } else
        {
            s2 = "Invalid License Key";
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Invalid license key found: " + s);
        }
        return s2;
    }

    public static int getLicenseType()
    {
        return getLicenseType(getMasterConfig());
    }

    public static int getLicenseType(jgl.HashMap hashmap)
    {
        String s = getLicenseKey(hashmap);
        return getLicenseType(s);
    }

    public static int getLicenseType(String s)
    {
        byte byte0 = -1;
        if(s.length() > 0)
        {
            if(isValidLicense(s))
            {
                String s1 = s.substring(0, 2);
                s1 = s1.toUpperCase();
                if(s1.compareTo("EX") == 0)
                {
                    byte0 = 1;
                } else
                if(s1.compareTo("SR") == 0)
                {
                    byte0 = 2;
                } else
                if(s1.compareTo("PM") == 0)
                {
                    byte0 = 3;
                } else
                if(s1.compareTo("HX") == 0)
                {
                    byte0 = 4;
                } else
                if(s1.compareTo("HS") == 0)
                {
                    byte0 = 5;
                } else
                if(s1.compareTo("HA") == 0)
                {
                    byte0 = 6;
                } else
                if(s1.compareTo("PE") == 0)
                {
                    byte0 = 8;
                }
            }
        } else
        {
            byte0 = 0;
        }
        return byte0;
    }

    public static String getLicenseTypeString(String s)
    {
        int i = getLicenseType(s);
        return getLicenseTypeString(i);
    }

    public static String getLicenseTypeString(int i)
    {
        String s = "Invalid";
        switch(i)
        {
        case -1: 
            s = "Invalid";
            break;

        case 0: // '\0'
            s = "Evaluation";
            break;

        case 1: // '\001'
            s = "Extension";
            break;

        case 2: // '\002'
            s = "Subscription";
            break;

        case 3: // '\003'
            s = "SiteView";
            break;

        case 4: // '\004'
            s = "HA Extension";
            break;

        case 5: // '\005'
            s = "HA Subscription";
            break;

        case 6: // '\006'
            s = "HA SiteView";
            break;

        case 7: // '\007'
            s = "Topaz Watchdog";
            break;

        case 8: // '\b'
            s = "Personal Edition";
            break;
        }
        return s;
    }

    private static String getLicenseTypeTag(int i)
    {
        String s = "";
        switch(i)
        {
        case 1: // '\001'
            s = "EX";
            break;

        case 2: // '\002'
            s = "SR";
            break;

        case 3: // '\003'
            s = "PM";
            break;

        case 4: // '\004'
            s = "HX";
            break;

        case 5: // '\005'
            s = "HS";
            break;

        case 6: // '\006'
            s = "HA";
            break;

        case 8: // '\b'
            s = "PE";
            break;
        }
        return s;
    }

    private static jgl.HashMap getMasterConfig()
    {
        return MasterConfig.getMasterConfig();
    }

    public static int getMonitorType(String s)
    {
        int i = 0;
        if(isValidLicense(s))
        {
            String s1 = s.substring(17, 19);
            i = COM.dragonflow.Utils.TextUtils.toInt(s1);
        } else
        if(getLicenseType(s) == 0 || getLicenseType(s) == 7)
        {
            i = 99;
        }
        return i;
    }

    static void initializeMonNameMaps()
    {
        monNameToType.put("ApacheMonitor", new Integer(1));
        monNameToType.put("ApacheMonitor", new Integer(1));
        monNameToType.put("ASPMonitor", new Integer(3));
        monNameToType.put("AssetMonitor", new Integer(4));
        monNameToType.put("CheckPointMonitor", new Integer(5));
        monNameToType.put("CheckPointOPSECMonitor", new Integer(6));
        monNameToType.put("CiscoMonitor", new Integer(7));
        monNameToType.put("ColdFusionMonitor", new Integer(8));
        monNameToType.put("CompositeMonitor", new Integer(9));
        monNameToType.put("CPUMonitor", new Integer(10));
        monNameToType.put("DatabaseMonitor", new Integer(11));
        monNameToType.put("DHCPMonitor", new Integer(12));
        monNameToType.put("DirectoryMonitor", new Integer(13));
        monNameToType.put("DiskSpaceMonitor", new Integer(14));
        monNameToType.put("DNSMonitor", new Integer(15));
        monNameToType.put("DynamoMonitor", new Integer(16));
        monNameToType.put("EBusinessTransactionMonitor", new Integer(17));
        monNameToType.put("F5Monitor", new Integer(18));
        monNameToType.put("FileMonitor", new Integer(19));
        monNameToType.put("FTPMonitor", new Integer(20));
        monNameToType.put("LDAPMonitor", new Integer(22));
        monNameToType.put("IISServerMonitor", new Integer(21));
        monNameToType.put("LinkMonitor", new Integer(23));
        monNameToType.put("LogMonitor", new Integer(24));
        monNameToType.put("MailMonitor", new Integer(25));
        monNameToType.put("MemoryMonitor", new Integer(26));
        monNameToType.put("NetscapeMonitor", new Integer(27));
        monNameToType.put("NetworkMonitor", new Integer(28));
        monNameToType.put("NewsMonitor", new Integer(29));
        monNameToType.put("NTCounterMonitor", new Integer(30));
        monNameToType.put("NTDialupMonitor", new Integer(31));
        monNameToType.put("NTEventLogMonitor", new Integer(32));
        monNameToType.put("Oracle9iMonitor", new Integer(33));
        monNameToType.put("PingMonitor", new Integer(34));
        monNameToType.put("PortMonitor", new Integer(36));
        monNameToType.put("RadiusMonitor", new Integer(37));
        monNameToType.put("RealMonitor", new Integer(38));
        monNameToType.put("RTSPMonitor", new Integer(39));
        monNameToType.put("ScriptMonitor", new Integer(40));
        monNameToType.put("ServiceMonitor", new Integer(41));
        monNameToType.put("SilverStreamMonitor", new Integer(42));
        monNameToType.put("SiebelCmdLineMonitor", new Integer(64));
        monNameToType.put("SiebelMonitor", new Integer(64));
        monNameToType.put("SiebelLogMonitor", new Integer(64));
        monNameToType.put("QTMonitor", new Integer(65));
        monNameToType.put("ALTMonitor", new Integer(66));
        monNameToType.put("SimulationMonitor", new Integer(43));
        monNameToType.put("SNMPMonitor", new Integer(46));
        monNameToType.put("SNMPTrapMonitor", new Integer(47));
        monNameToType.put("SQLServerMonitor", new Integer(48));
        monNameToType.put("URLMonitor", new Integer(52));
        monNameToType.put("URLContentMonitor", new Integer(53));
        monNameToType.put("URLListMonitor", new Integer(54));
        monNameToType.put("URLSequenceMonitor", new Integer(57));
        monNameToType.put("WebLogic5xMonitor", new Integer(58));
        monNameToType.put("WebServerMonitor", new Integer(59));
        monNameToType.put("WebServiceMonitor", new Integer(60));
        monNameToType.put("WebSphereMonitor", new Integer(61));
        monNameToType.put("WebSphereServletMonitor", new Integer(61));
        monNameToType.put("WindowsMediaMonitor", new Integer(62));
        monNameToType.put("WebLogic6xMonitor", new Integer(68));
        monNameToType.put("SAPPortalMonitor", new Integer(69));
        monNameToType.put("CCMSSAPMonitor", new Integer(70));
        monNameToType.put("EJBMonitor", new Integer(71));
        monNameToType.put("ComPlusMonitor", new Integer(73));
        monNameToType.put("EmsDummyMonitor", new Integer(74));
        monNameToType.put("TivoliAlertMonitor", new Integer(74));
        monNameToType.put("TivoliDmPassiveMonitor", new Integer(74));
        monNameToType.put("CIMMonitor", new Integer(74));
        monNameToType.put("ClarifyMonitor", new Integer(74));
        monNameToType.put("EMSDatabaseMonitor", new Integer(74));
        monNameToType.put("EMSLogMonitor", new Integer(74));
        monNameToType.put("EMSSNMPTrapMonitor", new Integer(74));
        monNameToType.put("HPOvoMonitor", new Integer(74));
        monNameToType.put("WhatsUpMonitor", new Integer(74));
        monNameToType.put("CAMonitor", new Integer(74));
        monNameToType.put("RemedyARsMonitor", new Integer(74));
        monNameToType.put("NetCoolMonitor", new Integer(74));
        monNameToType.put("AvalonMonitor", new Integer(74));
        monNameToType.put("BMCPatrolProbeMonitor", new Integer(74));
        monNameToType.put("PatrolMonitor", new Integer(74));
        monNameToType.put("NetIQMeasurementMonitor", new Integer(74));
        monNameToType.put("ConcordSisMonitor", new Integer(74));
        monNameToType.put("HpSimSisMonitor", new Integer(74));
        monNameToType.put("MQStatusMonitor", new Integer(75));
        monNameToType.put("SunOneMonitor", new Integer(76));
        monNameToType.put("Exchange2k3MailboxMonitor", new Integer(77));
        monNameToType.put("Exchange2k3PublicFolderMonitor", new Integer(77));
        monNameToType.put("Exchange2k3MsgTrafficMonitor", new Integer(77));
        monNameToType.put("Exchange55MsgTrafficMonitor", new Integer(77));
        monNameToType.put("ADReplicationMonitor", new Integer(78));
        monNameToType.put("WebLogicSolution", new Integer(79));
        monNameToType.put("WebSphereSolution", new Integer(80));
        monNameToType.put("OracleSolution", new Integer(81));
        monNameToType.put("SiebelSolution", new Integer(64));
        siteSeerMonNameToType.put("PingRemoteMonitor", new Integer(35));
        siteSeerMonNameToType.put("SiteSeerMonitor", new Integer(44));
        siteSeerMonNameToType.put("SiteSeer2Monitor", new Integer(45));
        siteSeerMonNameToType.put("URLRemoteMonitor", new Integer(55));
        siteSeerMonNameToType.put("URLRemoteSequenceMonitor", new Integer(56));
        siteSeerMonNameToType.put("PortRemoteMonitor", new Integer(67));
    }

    public static int getMonitorTypeByName(String s)
    {
        boolean flag = Platform.isSiteSeerServer();
        int i = s.lastIndexOf(".");
        if(i != -1)
        {
            s = s.substring(i + 1);
        }
        java.lang.Integer integer = null;
        if(flag)
        {
            integer = (java.lang.Integer)siteSeerMonNameToType.get(s);
        } else
        {
            integer = (java.lang.Integer)monNameToType.get(s);
        }
        if(integer != null)
        {
            return integer.intValue();
        } else
        {
            return 0;
        }
    }

    public static int getMonitorType(AtomicMonitor atomicmonitor)
    {
        String s = atomicmonitor.getClass().toString();
        return getMonitorTypeByName(s);
    }

    public static String getMonitorTypeString(String s)
    {
        return getMonitorTypeString(getMonitorType(s));
    }

    public static String getMonitorTypeClass(int i)
    {
        String s = "unknown";
        switch(i)
        {
        case 1: // '\001'
            s = "COM.dragonflow.StandardMonitor.ApacheMonitor";
            break;

        case 2: // '\002'
            s = "COM.dragonflow.StandardMonitor.AribaMonitor";
            break;

        case 3: // '\003'
            s = "COM.dragonflow.StandardMonitor.ASPMonitor";
            break;

        case 4: // '\004'
            s = "COM.dragonflow.StandardMonitor.AssetMonitor";
            break;

        case 63: // '?'
            s = "COM.dragonflow.StandardMonitor.BV55Monitor";
            break;

        case 5: // '\005'
            s = "COM.dragonflow.StandardMonitor.CheckPointMonitor";
            break;

        case 6: // '\006'
            s = "COM.dragonflow.StandardMonitor.CheckPointOPSECMonitor";
            break;

        case 7: // '\007'
            s = "COM.dragonflow.StandardMonitor.CiscoMonitor";
            break;

        case 8: // '\b'
            s = "COM.dragonflow.StandardMonitor.ColdFusionMonitor";
            break;

        case 9: // '\t'
            s = "COM.dragonflow.StandardMonitor.CompositeMonitor";
            break;

        case 10: // '\n'
            s = "COM.dragonflow.StandardMonitor.CPUMonitor";
            break;

        case 11: // '\013'
            s = "COM.dragonflow.StandardMonitor.DatabaseMonitor";
            break;

        case 12: // '\f'
            s = "COM.dragonflow.StandardMonitor.DHCPMonitor";
            break;

        case 13: // '\r'
            s = "COM.dragonflow.StandardMonitor.DirectoryMonitor";
            break;

        case 14: // '\016'
            s = "COM.dragonflow.StandardMonitor.DiskSpaceMonitor";
            break;

        case 15: // '\017'
            s = "COM.dragonflow.StandardMonitor.DNSMonitor";
            break;

        case 16: // '\020'
            s = "COM.dragonflow.StandardMonitor.DynamoMonitor";
            break;

        case 17: // '\021'
            s = "COM.dragonflow.StandardMonitor.eBusinessTransactionMonitor";
            break;

        case 18: // '\022'
            s = "COM.dragonflow.StandardMonitor.F5Monitor";
            break;

        case 19: // '\023'
            s = "COM.dragonflow.StandardMonitor.FileMonitor";
            break;

        case 20: // '\024'
            s = "COM.dragonflow.StandardMonitor.FTPMonitor";
            break;

        case 21: // '\025'
            s = "COM.dragonflow.StandardMonitor.IISServerMonitor";
            break;

        case 22: // '\026'
            s = "COM.dragonflow.StandardMonitor.LDAPMonitor";
            break;

        case 23: // '\027'
            s = "COM.dragonflow.StandardMonitor.LinkMonitor";
            break;

        case 24: // '\030'
            s = "COM.dragonflow.StandardMonitor.LogMonitor";
            break;

        case 25: // '\031'
            s = "COM.dragonflow.StandardMonitor.MailMonitor";
            break;

        case 26: // '\032'
            s = "COM.dragonflow.StandardMonitor.MemoryMonitor";
            break;

        case 27: // '\033'
            s = "COM.dragonflow.StandardMonitor.NetscapeMonitor";
            break;

        case 28: // '\034'
            s = "COM.dragonflow.StandardMonitor.NetworkMonitor";
            break;

        case 29: // '\035'
            s = "COM.dragonflow.StandardMonitor.NewsMonitor";
            break;

        case 30: // '\036'
            s = "COM.dragonflow.StandardMonitor.NTCounterMonitor";
            break;

        case 31: // '\037'
            s = "COM.dragonflow.StandardMonitor.NTDialupMonitor";
            break;

        case 32: // ' '
            s = "COM.dragonflow.StandardMonitor.NTEventLogMonitor";
            break;

        case 33: // '!'
            s = "COM.dragonflow.StandardMonitor.Oracle9iMonitor";
            break;

        case 34: // '"'
            s = "COM.dragonflow.StandardMonitor.PingMonitor";
            break;

        case 35: // '#'
            s = "COM.dragonflow.StandardMonitor.PingRemoteMonitor";
            break;

        case 36: // '$'
            s = "COM.dragonflow.StandardMonitor.PortMonitor";
            break;

        case 37: // '%'
            s = "COM.dragonflow.StandardMonitor.RadiusMonitor";
            break;

        case 38: // '&'
            s = "COM.dragonflow.StandardMonitor.RealMonitor";
            break;

        case 39: // '\''
            s = "COM.dragonflow.StandardMonitor.RTSPMonitor";
            break;

        case 40: // '('
            s = "COM.dragonflow.StandardMonitor.ScriptMonitor";
            break;

        case 41: // ')'
            s = "COM.dragonflow.StandardMonitor.ServiceMonitor";
            break;

        case 42: // '*'
            s = "COM.dragonflow.StandardMonitor.SilverStreamMonitor";
            break;

        case 43: // '+'
            s = "COM.dragonflow.StandardMonitor.SimulationMonitor";
            break;

        case 44: // ','
            s = "COM.dragonflow.StandardMonitor.SiteSeerMonitor";
            break;

        case 45: // '-'
            s = "COM.dragonflow.StandardMonitor.SiteSeer2Monitor";
            break;

        case 46: // '.'
            s = "COM.dragonflow.StandardMonitor.SNMPMonitor";
            break;

        case 47: // '/'
            s = "COM.dragonflow.StandardMonitor.SNMPTrapMonitor";
            break;

        case 48: // '0'
            s = "COM.dragonflow.StandardMonitor.SQLServerMonitor";
            break;

        case 49: // '1'
            s = "COM.dragonflow.StandardMonitor.SybaseMonitor";
            break;

        case 64: // '@'
            s = "COM.dragonflow.StandardMonitor.SiebelMonitor,COM.dragonflow.StandardMonitor.SiebelCmdLineMonitor,COM.dragonflow.StandardMonitor.SiebelLogMonitor";
            break;

        case 65: // 'A'
            s = "COM.dragonflow.StandardMonitor.QTMonitor";
            break;

        case 66: // 'B'
            s = "COM.dragonflow.StandardMonitor.ALTMonitor";
            break;

        case 50: // '2'
            s = "COM.dragonflow.StandardMonitor.Telnet";
            break;

        case 51: // '3'
            s = "COM.dragonflow.StandardMonitor.Unix Counter";
            break;

        case 52: // '4'
            s = "COM.dragonflow.StandardMonitor.URLMonitor";
            break;

        case 53: // '5'
            s = "COM.dragonflow.StandardMonitor.URLContentMonitor";
            break;

        case 54: // '6'
            s = "COM.dragonflow.StandardMonitor.URLListMonitor";
            break;

        case 55: // '7'
            s = "COM.dragonflow.StandardMonitor.URLRemoteMonitor";
            break;

        case 56: // '8'
            s = "COM.dragonflow.StandardMonitor.URLRemotSequenceMonitor";
            break;

        case 57: // '9'
            s = "COM.dragonflow.StandardMonitor.URLSequenceMonitor";
            break;

        case 58: // ':'
            s = "COM.dragonflow.StandardMonitor.WebLogic5xMonitor";
            break;

        case 68: // 'D'
            s = "COM.dragonflow.StandardMonitor.WebLogic6xMonitor";
            break;

        case 59: // ';'
            s = "COM.dragonflow.StandardMonitor.WebServerMonitor";
            break;

        case 60: // '<'
            s = "COM.dragonflow.StandardMonitor.WebServiceMonitor";
            break;

        case 61: // '='
            s = "COM.dragonflow.StandardMonitor.WebSphereMonitor,COM.dragonflow.StandardMonitor.WebSphereServletMonitor";
            break;

        case 62: // '>'
            s = "COM.dragonflow.StandardMonitor.WindowsMediaMonitor";
            break;

        case 67: // 'C'
            s = "COM.dragonflow.StandardMonitor.PortRemoteMonitor";
            break;

        case 69: // 'E'
            s = "COM.dragonflow.StandardMonitor.SAPPotrtalMonitor";
            break;

        case 70: // 'F'
            s = "COM.dragonflow.StandardMonitor.CCMSSAPMonitor";
            break;

        case 71: // 'G'
            s = "COM.dragonflow.StandardMonitor.EJBMonitor";
            break;

        case 73: // 'I'
            s = "COM.dragonflow.StandardMonitor.ComPlusMonitor";
            break;

        case 74: // 'J'
            s = "COM.dragonflow.Utils.EmsDummyMonitor,COM.dragonflow.StandardMonitor.TivoliAlertMonitor,COM.dragonflow.StandardMonitor.TivoliDmPassiveMonitor,COM.dragonflow.StandardMonitor.CIMMonitor,COM.dragonflow.StandardMonitor.EMSDatabaseMonitor,COM.dragonflow.StandardMonitor.EMSLogMonitor,COM.dragonflow.StandardMonitor.WhatsUpMonitor,COM.dragonflow.StandardMonitor.CAMonitor,COM.dragonflow.StandardMonitor.RemedyARsMonitor,COM.dragonflow.StandardMonitor.EMSSNMPTrapMonitor,COM.dragonflow.StandardMonitor.NetCoolMonitor,COM.dragonflow.StandardMonitor.AvalonMonitor,COM.dragonflow.StandardMonitor.BMCPatrolProbeMonitor,COM.dragonflow.StandardMonitor.HPOvoMonitor,COM.dragonflow.StandardMonitor.NetIQMeasurementMonitor,COM.dragonflow.StandardMonitor.ConcordSisMonitor,COM.dragonflow.StandardMonitor.HpSimSisMonitor,COM.dragonflow.StandardMonitor.PatrolMonitor";
            break;

        case 75: // 'K'
            s = "COM.dragonflow.StandardMonitor.MQStatusMonitor";
            break;

        case 76: // 'L'
            s = "COM.dragonflow.StandardMonitor.SunOneMonitor";
            break;

        case 77: // 'M'
            s = "COM.dragonflow.StandardMonitor.Exchange2k3MailboxMonitor,COM.dragonflow.StandardMonitor.Exchange2k3PublicFolderMonitor,COM.dragonflow.StandardMonitor.Exchange2k3MsgTrafficMonitor,COM.dragonflow.StandardMonitor.Exchange55MsgTrafficMonitor";
            break;

        case 78: // 'N'
            s = "COM.dragonflow.StandardMonitor.ADReplicationMonitor";
            break;
        }
        return s;
    }

    public static String getMonitorTypeString(int i)
    {
        String s = "unknown";
        switch(i)
        {
        case 1: // '\001'
            s = "Apache";
            break;

        case 2: // '\002'
            s = "Ariba";
            break;

        case 3: // '\003'
            s = "ASP";
            break;

        case 4: // '\004'
            s = "Asset";
            break;

        case 63: // '?'
            s = "BroadVision 5.5";
            break;

        case 5: // '\005'
            s = "CheckPoint";
            break;

        case 6: // '\006'
            s = "CheckPoint OPSEC";
            break;

        case 7: // '\007'
            s = "Cisco";
            break;

        case 8: // '\b'
            s = "ColdFusion";
            break;

        case 9: // '\t'
            s = "Composite";
            break;

        case 10: // '\n'
            s = "CPU";
            break;

        case 11: // '\013'
            s = "Database";
            break;

        case 12: // '\f'
            s = "DHCP";
            break;

        case 13: // '\r'
            s = "Directory";
            break;

        case 14: // '\016'
            s = "Disk Space";
            break;

        case 15: // '\017'
            s = "DNS";
            break;

        case 16: // '\020'
            s = "Dynamo";
            break;

        case 17: // '\021'
            s = "eBusiness";
            break;

        case 18: // '\022'
            s = "F5";
            break;

        case 19: // '\023'
            s = "File";
            break;

        case 20: // '\024'
            s = "FTP";
            break;

        case 21: // '\025'
            s = "IIS Server";
            break;

        case 22: // '\026'
            s = "LDAP";
            break;

        case 23: // '\027'
            s = "Link";
            break;

        case 24: // '\030'
            s = "Log";
            break;

        case 25: // '\031'
            s = "Mail";
            break;

        case 26: // '\032'
            s = "Memory";
            break;

        case 27: // '\033'
            s = "Netscape";
            break;

        case 28: // '\034'
            s = "Network";
            break;

        case 29: // '\035'
            s = "News";
            break;

        case 30: // '\036'
            s = "NT Counter";
            break;

        case 31: // '\037'
            s = "NT Dialup";
            break;

        case 32: // ' '
            s = "NT Event Log";
            break;

        case 33: // '!'
            s = "Oracle 9i";
            break;

        case 34: // '"'
            s = "Ping";
            break;

        case 35: // '#'
            s = "Ping Remote";
            break;

        case 36: // '$'
            s = "Port";
            break;

        case 37: // '%'
            s = "Radius";
            break;

        case 38: // '&'
            s = "Real";
            break;

        case 39: // '\''
            s = "RTSP";
            break;

        case 40: // '('
            s = "Script";
            break;

        case 41: // ')'
            s = "Service";
            break;

        case 42: // '*'
            s = "SilverStream";
            break;

        case 43: // '+'
            s = "Simulation";
            break;

        case 44: // ','
            s = "SiteSeer";
            break;

        case 45: // '-'
            s = "SiteSeer2";
            break;

        case 46: // '.'
            s = "SNMP";
            break;

        case 47: // '/'
            s = "SNMP Trap";
            break;

        case 48: // '0'
            s = "SQL Server";
            break;

        case 49: // '1'
            s = "Sybase";
            break;

        case 64: // '@'
            s = "Siebel Monitors and Solutions";
            break;

        case 65: // 'A'
            s = "Quick Test";
            break;

        case 66: // 'B'
            s = "Astra Load Test";
            break;

        case 50: // '2'
            s = "Telnet";
            break;

        case 51: // '3'
            s = "Unix Counter";
            break;

        case 52: // '4'
            s = "URL";
            break;

        case 53: // '5'
            s = "URL Content";
            break;

        case 54: // '6'
            s = "URL List";
            break;

        case 55: // '7'
            s = "URL Remote";
            break;

        case 56: // '8'
            s = "URL Remote Sequence";
            break;

        case 57: // '9'
            s = "URL Sequence";
            break;

        case 58: // ':'
            s = "WebLogic 5x";
            break;

        case 68: // 'D'
            s = "WebLogic 6x";
            break;

        case 59: // ';'
            s = "Web Server";
            break;

        case 60: // '<'
            s = "Web Service";
            break;

        case 61: // '='
            s = "WebSphere";
            break;

        case 62: // '>'
            s = "Windows Media";
            break;

        case 67: // 'C'
            s = "Port Remote";
            break;

        case 69: // 'E'
            s = "SAPPortal";
            break;

        case 70: // 'F'
            s = "CCMS SAP Monitor";
            break;

        case 71: // 'G'
            s = "EJBMonitor";
            break;

        case 72: // 'H'
            s = "J2EE Monitors";
            break;

        case 73: // 'I'
            s = "Com+ Monitor";
            break;

        case 74: // 'J'
            s = "EMS Monitors";
            break;

        case 75: // 'K'
            s = "MQ Status Monitor";
            break;

        case 76: // 'L'
            s = "Sun One Monitor";
            break;

        case 77: // 'M'
            s = "Exchange Server";
            break;

        case 78: // 'N'
            s = "Active Directory";
            break;

        case 79: // 'O'
            s = "WebLogic Solution";
            break;

        case 80: // 'P'
            s = "WebSphere Solution";
            break;

        case 81: // 'Q'
            s = "Oracle Solution";
            break;
        }
        return s;
    }

    public static Enumeration getMonitorsFromType(int i)
    {
        Enumeration enumeration = monNameToType.keys(new Integer(i));
        if(!enumeration.hasMoreElements())
        {
            enumeration = siteSeerMonNameToType.keys(new Integer(i));
        }
        return enumeration;
    }

    public static int getShutdownDays(int i)
    {
        byte byte0 = 0;
        switch(i)
        {
        case 1: // '\001'
        case 4: // '\004'
            byte0 = 0;
            break;

        case 2: // '\002'
        case 5: // '\005'
            byte0 = 0;
            break;

        case 0: // '\0'
        case 8: // '\b'
        default:
            byte0 = -20;
            break;

        case 3: // '\003'
        case 6: // '\006'
        case 7: // '\007'
            break;
        }
        return byte0;
    }

    public static int getWarningDays(int i)
    {
        byte byte0 = 0;
        switch(i)
        {
        case 1: // '\001'
        case 4: // '\004'
            byte0 = 7;
            break;

        case 2: // '\002'
        case 5: // '\005'
            byte0 = 14;
            break;

        case 0: // '\0'
        case 8: // '\b'
        default:
            byte0 = -13;
            break;

        case 3: // '\003'
        case 6: // '\006'
        case 7: // '\007'
            break;
        }
        return byte0;
    }

    public static boolean isCentraScopeLicense()
    {
        String s = getLicenseKey(getMasterConfig());
        return isCentraScopeLicense(s);
    }

    public static boolean isCentraScopeLicense(String s)
    {
        boolean flag = false;
        if(Platform.isPortal() && (isSubscriptionLicense(s) || getLicenseType(s) == 0))
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isHighAvailabilityLicense()
    {
        String s = getLicenseKey(getMasterConfig());
        return isHighAvailabilityLicense(s);
    }

    public static boolean isHighAvailabilityLicense(String s)
    {
        boolean flag = false;
        int i = getLicenseType(s);
        if(i == 4 || i == 5 || i == 6)
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isMonitorTypeAllowed(AtomicMonitor atomicmonitor)
    {
        String s = getLicenseKey(getMasterConfig());
        return isMonitorTypeAllowed(atomicmonitor, s);
    }

    public static boolean isMonitorTypeAllowed(AtomicMonitor atomicmonitor, String s)
    {
        boolean flag = false;
        int i = getMonitorType(s.trim());
        if(i != 99)
        {
            int j = getMonitorType(atomicmonitor);
            if(j == i)
            {
                flag = true;
            }
        } else
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isMonitorOrSolutionTypeAllowed(String s, String s1)
    {
        boolean flag = false;
        int i = getMonitorType(s1.trim());
        if(i != 99)
        {
            int j = getMonitorTypeByName(s);
            if(j == i)
            {
                flag = true;
            }
        } else
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isPermanentLicense()
    {
        String s = getLicenseKey(getMasterConfig());
        return isPermanentLicense(s);
    }

    public static boolean isPermanentLicense(String s)
    {
        //FIXME:boolean flag = false;
    	boolean flag = true;
        int i = getLicenseType(s);
        if(i == 3 || i == 6 || i == 7)
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isSubscriptionLicense()
    {
        String s = getLicenseKey(getMasterConfig());
        return isSubscriptionLicense(s);
    }

    public static boolean isSubscriptionLicense(String s)
    {
        boolean flag = false;
        int i = getLicenseType(s);
        if(i == 2 || i == 5)
        {
            flag = true;
        }
        return flag;
    }

    public static boolean isValidLicense(String s)
    {
        boolean flag = false;
        if(s.length() == 23 && s.charAt(16) == '-')
        {
            String s1 = getChecksum(s);
            String s2 = s.substring(0, 19);
            s2 = generateChecksum(s2);
            if(s1.compareTo(s2) == 0)
            {
                flag = true;
            }
            String s3 = s.substring(10, 16);
            int i = COM.dragonflow.Utils.TextUtils.toInt(s3);
            if(i < 0)
            {
                i = 1;
            }
            if(i >= 0x186a0)
            {
                flag = false;
            }
        }
        return flag;
    }

    public static boolean setLicenseKey(String s)
    {
        jgl.HashMap hashmap = getMasterConfig();
        return setLicenseKey(s, hashmap);
    }

    public static boolean setLicenseKey(String s, jgl.HashMap hashmap)
    {
        String s1 = "_license";
        return setLicenseKey(s1, s, hashmap);
    }

    public static boolean setLicenseKey(String s, String s1, jgl.HashMap hashmap)
    {
        boolean flag = false;
        if(isValidLicense(s1))
        {
            hashmap.put(s, s1);
            flag = true;
        }
        return flag;
    }

    public static boolean wouldExceedLimit(jgl.Array array, String s)
    {
        int i = getCostInLicensePoints(array, s);
        return wouldExceedLimit(i);
    }

    public static boolean wouldExceedLimit(AtomicMonitor atomicmonitor)
    {
        int i = atomicmonitor.getCostInLicensePoints();
        return wouldExceedLimit(i);
    }

    public static boolean wouldExceedLimit(int i)
    {
        boolean flag = false;
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        SiteViewGroup _tmp = siteviewgroup;
        int j = SiteViewGroup.totalPointsUsed;
        int k = j + i;
        int l = getLicensedPoints();
        if(k > l)
        {
            flag = true;
        }
        return flag;
    }

    public static void main(String args[])
        throws java.lang.Exception
    {
    }

    private static String oldLicenseGetChecksumField(String s)
    {
        String s1 = "";
        int i = s.lastIndexOf('-') + 1;
        s1 = s.substring(i);
        return s1;
    }

    private static int oldLicenseGetDaysRemaining(String s)
    {
        int i = 10000;
        String s1 = oldLicenseGetExpirationField(s);
        if(s1.length() > 0)
        {
            int j = COM.dragonflow.Utils.TextUtils.toInt(s1.substring(2, 4));
            int k = COM.dragonflow.Utils.TextUtils.toInt(s1.substring(0, 2));
            int l = 1998 + k / 12;
            k = k % 12 - 1;
            java.util.Date date = new Date(l - 1900, k, j);
            java.util.Date date1 = new Date();
            long l1 = date.getTime() - date1.getTime();
            i = (int)(l1 / 0x5265c00L) + 1;
        }
        return i;
    }

    private static String oldLicenseGetExpirationField(String s)
    {
        String s1 = "";
        String s2 = oldLicenseGetTypeField(s);
        if(s2.compareTo("SR") == 0)
        {
            s1 = s.substring(2, 6);
        } else
        if(s2.compareTo("FWEXT") == 0 || s2.compareTo("HAEXT") == 0 || s2.compareTo("SREXT") == 0)
        {
            s1 = s.substring(5, 9);
        }
        return s1;
    }

    private static String oldLicenseGetIdField(String s)
    {
        String s1 = "";
        String s2 = oldLicenseGetTypeField(s);
        if(s2.compareTo("FW") == 0 || s2.compareTo("HA") == 0)
        {
            s1 = s.substring(2, 8);
        } else
        if(s2.compareTo("SR") == 0)
        {
            s1 = s.substring(6, 12);
        }
        return s1;
    }

    public static int oldLicenseGetMonitors(String s)
    {
        int i = 0;
        String s1 = oldLicenseGetMonitorsField(s);
        if(s1.length() > 0)
        {
            i = COM.dragonflow.Utils.TextUtils.toInt(s1);
        }
        return i;
    }

    private static String oldLicenseGetMonitorsField(String s)
    {
        String s1 = "";
        if(oldLicenseIsValid(s))
        {
            String s2 = oldLicenseGetTypeField(s);
            String s3 = oldLicenseGetExpirationField(s);
            String s4 = oldLicenseGetIdField(s);
            int i = s2.length() + s3.length() + s4.length();
            int j = i + 7;
            s1 = s.substring(i, j);
        }
        return s1;
    }

    private static int oldLicenseGetType(String s)
    {
        byte byte0 = -1;
        if(oldLicenseIsValid(s))
        {
            String s1 = oldLicenseGetTypeField(s);
            if(s1.compareTo("FWEXT") == 0)
            {
                byte0 = 1;
            } else
            if(s1.compareTo("SR") == 0 || s1.compareTo("SREXT") == 0)
            {
                byte0 = 2;
            } else
            if(s1.compareTo("FW") == 0)
            {
                byte0 = 3;
            } else
            if(s1.compareTo("HAEXT") == 0)
            {
                byte0 = 4;
            } else
            if(s1.compareTo("HA") == 0)
            {
                byte0 = 6;
            } else
            if(s1.compareTo("PE") == 0)
            {
                byte0 = 8;
            }
        }
        return byte0;
    }

    private static String oldLicenseGetTypeField(String s)
    {
        String s1 = "";
        if(s.startsWith("FWEXT") || s.startsWith("HAEXT") || s.startsWith("SREXT"))
        {
            s1 = s.substring(0, 5);
        } else
        if(s.startsWith("FW") || s.startsWith("HA") || s.startsWith("SR"))
        {
            s1 = s.substring(0, 2);
        }
        return s1;
    }

    public static boolean oldLicenseIsValid(String s)
    {
        boolean flag = false;
        if(oldLicenseGetTypeField(s).length() > 0)
        {
            int i = oldLicenseGetTypeField(s).length();
            int j = s.lastIndexOf('-');
            String s1 = s.substring(i, j);
            long l = COM.dragonflow.Utils.TextUtils.toLong(s1);
            if(s1.length() < 12)
            {
                int k = (int)l;
                k = (k * 65535) % 997;
                l = k;
            } else
            {
                l = (l * 0x1869f795fL) % 9997L;
            }
            if(l < 0L)
            {
                l = -l;
            }
            if((long)COM.dragonflow.Utils.TextUtils.toInt(oldLicenseGetChecksumField(s)) == l)
            {
                flag = true;
            }
        }
        return flag;
    }

    public static void addMonitorType(String s)
    {
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        boolean flag = false;
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i].trim();
            String s2 = getMonitorTypeClass(getMonitorType(s1));
            if(s2.equals("unknown"))
            {
                continue;
            }
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s2, ",");
            for(int j = 0; j < as1.length; j++)
            {
                String s3 = null;
                try
                {
                    s3 = (String)COM.dragonflow.Properties.PropertiedObject.getClassPropertyByObject(as1[j], "loadable");
                }
                catch(java.lang.Exception exception)
                {
                    s3 = "false";
                }
                java.lang.Class class1 = null;
                try
                {
                    class1 = java.lang.Class.forName(as1[j]);
                }
                catch(java.lang.ClassNotFoundException classnotfoundexception)
                {
                    classnotfoundexception.printStackTrace();
                }
                try
                {
                    boolean flag1 = isValidSSforXLicense((AtomicMonitor)class1.newInstance(), s1);
                    if((s3.equals("false") || COM.dragonflow.Properties.PropertiedObject.getClassPropertyByObject(as1[j], "loadable") == null) && flag1)
                    {
                        COM.dragonflow.Properties.PropertiedObject.setClassProperty(as1[j], "loadable", "true");
                        flag = true;
                    }
                }
                catch(java.lang.Exception exception1) { }
            }

        }

        if(flag)
        {
            COM.dragonflow.Page.monitorPage.burnCache();
        }
    }

    public static void sendHeartbeat()
        throws java.lang.Exception
    {
        if(sessionActive)
        {
            sessionLastHeartbeat = java.lang.System.currentTimeMillis();
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Session Heartbeat recieved: " + sessionLastHeartbeat);
        } else
        {
            throw new Exception("Heartbeat can not be sent. Session is not active!");
        }
    }

    public static void createSession(long l)
        throws java.lang.Exception
    {
        if(sessionShuttingDownOfSS)
        {
            throw new Exception("Session already exists");
        }
        if(!sessionActive)
        {
            sessionTimeout = l;
            sessionActive = true;
            sessionLastHeartbeat = java.lang.System.currentTimeMillis();
            hb = new HeartbeatSessionTask();
            java.util.Timer timer = new Timer();
            timer.scheduleAtFixedRate(hb, new Date(), l);
        } else
        {
            throw new Exception("Session already exists");
        }
    }

    public static void updateSpecialLicense(String s, boolean flag)
        throws java.lang.Exception
    {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i].trim();
            if(!isValidLicense(s1))
            {
                throw new Exception("License key \"" + s1 + "\" is not valid");
            }
        }

        if(flag)
        {
            if(!sessionActive)
            {
                throw new Exception("Session is not active");
            }
            sessionLicenseForX = s;
        } else
        {
            sessionLicenseForX = s;
            siteviewgroup.setProperty("_licenseForX", s);
            siteviewgroup.saveSettings();
        }
        (new File(Platform.expiredName())).delete();
        addMonitorType(s);
    }

    public static void updateGeneralLicense(String s, boolean flag)
        throws java.lang.Exception
    {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        if(isValidLicense(s))
        {
            if(flag)
            {
                if(!sessionActive)
                {
                    throw new Exception("Session is not active");
                }
                sessionLicense = s;
            } else
            {
                sessionLicense = s;
                siteviewgroup.setProperty("_license", s);
                siteviewgroup.saveSettings();
            }
            (new File(Platform.expiredName())).delete();
        } else
        {
            throw new Exception("License is not valid");
        }
    }

    public static boolean isSolution(int i)
    {
        return i == 77 || i == 78 || i == 79 || i == 80 || i == 81 || i == 64;
    }

    static 
    {
        initializeMonNameMaps();
    }
}
