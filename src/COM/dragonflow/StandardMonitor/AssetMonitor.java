/*
 * 
 * Created on 2005-3-5 14:19:48
 *
 * AssetMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>AssetMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
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
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.OSAdapter;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.LineReader;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// NTCounterMonitor

public class AssetMonitor extends ServerMonitor {

    static String drive_asset_create = "CREATE TABLE [dbo].[DRIVE_ASSET] ([SERVERID] [int] NULL ,[DRIVE_NAME] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[TYPE] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[SERIAL_NUMBER] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[LOGICAL_SIZE_IN_MEG] [int] NULL ,[VOLUME] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[LAST_UPDATE] [datetime] NULL) ON [PRIMARY]";

    static String drive_asset_create2 = "";

    static String drive_asset_create3 = "";

    static String drive_asset_define = "";

    static String drive_asset_insert = "INSERT INTO DRIVE_ASSET VALUES(?,?,?,?,?,?,?)";

    static String nic_asset_create = "CREATE TABLE [dbo].[NIC_ASSET] (\t[SERVERID] [int] NOT NULL ,\t[nic_name] [varchar] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,\t[host_name] [varchar] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,\t[ip_address] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,\t[last_update] [datetime] NULL) ON [PRIMARY]";

    static String nic_asset_create2 = "";

    static String nic_asset_create3 = "";

    static String nic_asset_define = "";

    static String nic_asset_insert = "INSERT INTO NIC_ASSET VALUES(?,?,?,?,?)";

    static String server_asset_create = "CREATE TABLE [dbo].[SERVER_ASSET] ([SERVERID] [int] NOT NULL ,[num_cpu] [smallint] NULL ,[cpu_type] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[cpu_name] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[cpu_vendor] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[cpu_speed] [int] NULL ,[tot_ram] [int] NULL ,[bios_date] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[os] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[os_version] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[service_pack] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[controller_type] [varchar] (60) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[last_update] [datetime] NULL) ON [PRIMARY]";

    static String server_asset_create2 = "";

    static String server_asset_create3 = "";

    static String server_asset_define = "";

    static String server_asset_insert = "INSERT INTO SERVER_ASSET VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

    static String service_asset_create = "CREATE TABLE [dbo].[SERVICE_ASSET] ([SERVERID] [int] NOT NULL ,[SERVICEID] [int] NOT NULL ,[STATE] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[LAST_UPDATE] [datetime] NULL ) ON [PRIMARY]";

    static String service_asset_create2 = "CREATE TABLE [dbo].[Service_Lookup] ([ServiceID] [int] IDENTITY (1, 1) NOT NULL ,[Service_Name] [char] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,[Hosted_Software_ID] [int] NULL ) ON [PRIMARY]";

    static String service_asset_create3 = "";

    static String service_asset_define = "CREATE PROCEDURE [dbo].[sp_insert_service] @serverid int, @servicename varchar(255), @state varchar(255), @lastupdate datetime AS BEGIN DECLARE @id INT SET @id = (SELECT ServiceID FROM service_lookup WHERE Service_Name = @servicename) IF @id IS NULL BEGIN   INSERT INTO service_lookup (Service_Name) VALUES (@servicename)   SET @id = (SELECT ServiceID FROM service_lookup where Service_Name = @servicename) END INSERT INTO service_asset VALUES (@serverid, @id, @state, @lastupdate) END";

    static String service_asset_insert = "call dbo.sp_insert_service(?,?,?,?)";

    static String software_asset_create = "CREATE TABLE [dbo].[SOFTWARE_ASSET] ([SERVERID] [int] NULL ,[HOSTED_SOFTWARE_ID] [int] NULL ,[LAST_UPDATE] [datetime] NULL ) ON [PRIMARY] ";

    static String software_asset_create2 = "CREATE TABLE [dbo].[Hosted_Software] ( [Hosted_Software_ID] [int] IDENTITY (1, 1) NOT NULL , [Vendor] [char] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Product_Name] [char] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Platform] [char] (30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Supported] [int] NULL , [Version] [char] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Service_Pack] [char] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Title] [char] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL  ) ON [PRIMARY]";

    static String software_asset_create3 = "";

    static String software_asset_define = "CREATE PROCEDURE [dbo].[sp_insert_software] @serverid int, @softwarename varchar(255), @lastupdate datetime AS BEGIN DECLARE  @id INT SET @id = (SELECT Hosted_Software_ID FROM Hosted_Software WHERE Product_Name = @softwarename) IF NOT @id IS NULL BEGIN INSERT INTO SOFTWARE_ASSET VALUES (@serverid, @id, @lastupdate) END END";

    static String software_asset_insert = "call sp_insert_software (?,?,?)";

    static String unix_drive_asset_create = "CREATE TABLE [dbo].[UNIX_DRIVE_ASSET] ( [SERVERID] [int] NULL , [MOUNT_POINT] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [PHYSICAL_NAME] [varchar] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [LOGICAL_SIZE_IN_KB] [bigint] NULL , [LAST_UPDATE] [datetime] NULL ) ON [PRIMARY]";

    static String unix_drive_asset_create2 = "";

    static String unix_drive_asset_create3 = "";

    static String unix_drive_asset_define = "";

    static String unix_drive_asset_insert = "INSERT INTO UNIX_DRIVE_ASSET VALUES(?,?,?,?,?)";

    static String unix_nic_asset_create = "CREATE TABLE [dbo].[UNIX_NIC] ([SERVERID] [int] NULL , [INTERFACE] [varchar] (20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [INSTANCE] [int] NULL , [IP_ADDRESS] [varchar] (20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [HOSTNAME] [varchar] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [LAST_UPDATE] [datetime] NULL ) ON [PRIMARY]";

    static String unix_nic_asset_create2 = "";

    static String unix_nic_asset_create3 = "";

    static String unix_nic_asset_define = "";

    static String unix_nic_asset_insert = "INSERT INTO UNIX_NIC VALUES(?,?,?,?,?,?)";

    static String unix_server_asset_create = "CREATE TABLE [dbo].[UNIX_SERVER_ASSET] ( [SERVERID] [int] NOT NULL , [num_cpu] [smallint] NULL , [cpu_type] [varchar] (30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [cpu_speed] [varchar] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [tot_ram] [varchar] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [obp] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [os] [varchar] (10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [os_version] [varchar] (10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [os_release] [varchar] (30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [class] [varchar] (10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [platform] [varchar] (40) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [build_version] [varchar] (20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [mac_address] [varchar] (30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [last_update] [datetime] NULL ) ON [PRIMARY]";

    static String unix_server_asset_create2 = "";

    static String unix_server_asset_create3 = "";

    static String unix_server_asset_define = "";

    static String unix_server_asset_insert = "INSERT INTO UNIX_SERVER_ASSET VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    static String unix_procs_create = "CREATE TABLE [dbo].[UNIX_PROCS] ( [SERVERID] [int] NOT NULL , [PROCESS] [varchar] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [UNAME] [varchar] (20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [INSTANCES] [int] NULL , [LAST_UPDATE] [datetime] NOT NULL ) ON [PRIMARY]";

    static String unix_procs_create2 = "";

    static String unix_procs_create3 = "";

    static String unix_procs_define = "";

    static String unix_procs_insert = "insert into UNIX_PROCS values(?,?,?,?,?)";

    static String unix_patch_create = "CREATE TABLE [dbo].[UNIX_PATCH] ( [SERVERID] [int] NOT NULL , [PATCHNUM] [char] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL , [PATCHREVNUM] [char] (4) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [LAST_UPDATE] [datetime] NOT NULL ) ON [PRIMARY]";

    static String unix_patch_create2 = "";

    static String unix_patch_create3 = "";

    static String unix_patch_define = "";

    static String unix_patch_insert = "insert into UNIX_PATCH values(?,?,?,?)";

    static String unix_package_create = "CREATE TABLE [dbo].[UNIX_SOFTWARE] ( [SERVERID] [int] NULL ,[HOSTED_SOFTWARE_ID] [int] NULL ,[LAST_UPDATE] [datetime] NULL ) ON [PRIMARY] ";

    static String unix_package_create2 = "CREATE TABLE [dbo].[UNIX_PKG] ( [SERVERID] [int] NOT NULL , [PKGNAME] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [PKGVERS] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [PKGDESC] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [HOSTED_SOFTWARE_ID] [int] NULL , [LAST_UPDATE] [datetime] NULL ) ON [PRIMARY]";

    static String unix_package_create3 = "CREATE TABLE [dbo].[Hosted_Software] ( [Hosted_Software_ID] [int] IDENTITY (1, 1) NOT NULL , [Vendor] [char] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Product_Name] [char] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Platform] [char] (30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Supported] [int] NULL , [Version] [char] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Service_Pack] [char] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL , [Title] [char] (100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL  ) ON [PRIMARY]";

    static String unix_package_define = "CREATE PROCEDURE [dbo].[sp_insert_unix_software] @serverid int, @softwarename varchar(255), @softwarevers varchar(255), @softwaredesc varchar(255), @lastupdate datetime AS BEGIN DECLARE  @id INT SET @id = (SELECT Hosted_Software_ID FROM Hosted_Software WHERE Product_Name = @softwarename) IF NOT @id IS NULL  INSERT INTO UNIX_SOFTWARE VALUES (@serverid, @id, @lastupdate)  ELSE  INSERT INTO UNIX_PKG VALUES (@serverid, @softwarename, @softwarevers, @softwaredesc, @id, @lastupdate)END";

    static String unix_package_insert = "call sp_insert_unix_software(?,?,?,?,?)";

    static StringProperty pAsset;

    static StringProperty pExpression;

    static StringProperty pMaxMeasurement;

    static StringProperty pRoundTripTime;

    static StringProperty pStatus;

    static StringProperty pRecords;

    boolean hasSubscription;

    boolean showDebug;

    public AssetMonitor() {
        hasSubscription = false;
        showDebug = false;
    }

    static void man(HashMap hashmap, String s, String s1, String s2, String s3, String s4, String s5) {
        System.out.println("");
        String s6 = "_logJdbcCreate" + s;
        String s7 = TextUtils.getValue(hashmap, s6);
        if (s7.length() == 0) {
            s7 = s1;
        }
        System.out.println(s6 + "=" + s7);
        System.out.println("");
        s6 = "_logJdbcCreate2" + s;
        s7 = TextUtils.getValue(hashmap, s6);
        if (s7.length() == 0) {
            s7 = s2;
        }
        System.out.println(s6 + "=" + s7);
        System.out.println("");
        s6 = "_logJdbcCreate3" + s;
        s7 = TextUtils.getValue(hashmap, s6);
        if (s7.length() == 0) {
            s7 = s3;
        }
        System.out.println(s6 + "=" + s7);
        System.out.println("");
        s6 = "_logJdbcDefine" + s;
        s7 = TextUtils.getValue(hashmap, s6);
        if (s7.length() == 0) {
            s7 = s4;
        }
        System.out.println(s6 + "=" + s7);
        System.out.println("");
        s6 = "_logJdbcInsert" + s;
        s7 = TextUtils.getValue(hashmap, s6);
        if (s7.length() == 0) {
            s7 = s5;
        }
        System.out.println(s6 + "=" + s7);
        System.out.println("");
        System.out.println("");
    }

    public static void main(String args[]) {
        int i = 0;
        try {
            HashMap hashmap = MasterConfig.getMasterConfig();
            if (args.length == 1 && args[0].equals("-print")) {
                System.out.println("Database Definitions for SiteView Asset Management, " + Platform.getVersion());
                System.out.println("");
                man(hashmap, "server_asset", server_asset_create, server_asset_create2, server_asset_create3, server_asset_define, server_asset_insert);
                man(hashmap, "service_asset", service_asset_create, service_asset_create2, service_asset_create3, service_asset_define, service_asset_insert);
                man(hashmap, "service_asset", software_asset_create, software_asset_create2, software_asset_create3, software_asset_define, software_asset_insert);
                man(hashmap, "drive_asset", drive_asset_create, drive_asset_create2, drive_asset_create3, drive_asset_define, drive_asset_insert);
                man(hashmap, "nic_asset", nic_asset_create, nic_asset_create2, nic_asset_create3, nic_asset_define, nic_asset_insert);
                man(hashmap, "unix_server_asset", unix_server_asset_create, unix_server_asset_create2, unix_server_asset_create3, unix_server_asset_define, unix_server_asset_insert);
                man(hashmap, "unix_procs", unix_procs_create, unix_procs_create2, unix_procs_create3, unix_procs_define, unix_procs_insert);
                man(hashmap, "unix_patch", unix_patch_create, unix_patch_create2, unix_patch_create3, unix_patch_define, unix_patch_insert);
                man(hashmap, "unix_package", unix_package_create, unix_package_create2, unix_package_create3, unix_package_define, unix_package_insert);
                man(hashmap, "unix_drive_asset", unix_drive_asset_create, unix_drive_asset_create2, unix_drive_asset_create3, unix_drive_asset_define, unix_drive_asset_insert);
                man(hashmap, "unix_nic_asset", unix_nic_asset_create, unix_nic_asset_create2, unix_nic_asset_create3, unix_nic_asset_define, unix_nic_asset_insert);
                man(hashmap, "perf_data", NTCounterMonitor.perf_data_create, NTCounterMonitor.perf_data_create2, "", NTCounterMonitor.perf_data_define, NTCounterMonitor.perf_data_insert);
                System.exit(0);
            }
            JdbcLogger jdbclogger = new JdbcLogger(hashmap);
            if (TextUtils.getValue(hashmap, "_logJdbcURLSiteViewLog").length() == 0) {
                throw new Exception("_logJdbcURLSiteViewLog is not defined in master.config");
            }
            if (TextUtils.getValue(hashmap, "_logJdbcUserSiteViewLog").length() == 0) {
                throw new Exception("_logJdbcUserSiteViewLog is not defined in master.config");
            }
            if (TextUtils.getValue(hashmap, "_logJdbcPasswordSiteViewLog").length() == 0) {
                throw new Exception("_logJdbcPasswordSiteViewLog is not defined in master.config");
            }
            JdbcLogger.logger.getCustomStatement("server_asset", server_asset_create, "", "", server_asset_define, server_asset_insert);
            JdbcLogger.logger.getCustomStatement("service_asset", service_asset_create, service_asset_create2, "", service_asset_define, service_asset_insert);
            JdbcLogger.logger.getCustomStatement("software_asset", software_asset_create, software_asset_create2, "", software_asset_define, software_asset_insert);
            JdbcLogger.logger.getCustomStatement("drive_asset", drive_asset_create, "", "", drive_asset_define, drive_asset_insert);
            JdbcLogger.logger.getCustomStatement("nic_asset", nic_asset_create, "", "", nic_asset_define, nic_asset_insert);
            JdbcLogger.logger.getCustomStatement("unix_server_asset", unix_server_asset_create, "", "", unix_server_asset_define, unix_server_asset_insert);
            JdbcLogger.logger.getCustomStatement("unix_procs", unix_procs_create, "", "", unix_procs_define, unix_procs_insert);
            JdbcLogger.logger.getCustomStatement("unix_patch", unix_patch_create, "", "", unix_patch_define, unix_patch_insert);
            JdbcLogger.logger.getCustomStatement("unix_package", unix_package_create, unix_package_create2, unix_package_create3, unix_package_define, unix_package_insert);
            JdbcLogger.logger.getCustomStatement("unix_drive_asset", unix_drive_asset_create, "", "", unix_drive_asset_define, unix_drive_asset_insert);
            JdbcLogger.logger.getCustomStatement("unix_nic_asset", unix_nic_asset_create, "", "", unix_nic_asset_define, unix_nic_asset_insert);
            JdbcLogger.logger.getCustomStatement("perf_data", NTCounterMonitor.perf_data_create, NTCounterMonitor.perf_data_create2, "", NTCounterMonitor.perf_data_define, NTCounterMonitor.perf_data_insert);
        } catch (Exception exception) {
            System.out.println("*** ERROR, " + exception.getMessage());
            i = -1;
        }
        System.out.flush();
        System.exit(i);
    }

    protected void startMonitor() {
        if (getSetting("_assetDebug").length() > 0) {
            showDebug = true;
        }
        if (LUtils.isSubscriptionLicense()) {
            hasSubscription = true;
        }
        super.startMonitor();
    }

    Array splitRecord(Object obj) {
        if (obj instanceof Array) {
            return (Array) obj;
        } else {
            String s = (String) obj;
            return Platform.split(',', s);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @param stringbuffer
     * @return
     */
    int logAssets(Array array, StringBuffer stringbuffer) {
        String s = getProperty(pAsset);
        if (!hasSubscription) {
            stringbuffer.append("error, " + s + " records, needs SiteReliance license");
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        if (JdbcLogger.logger == null) {
            stringbuffer.append("error, " + s + " records, database logging not configured");
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        String s1 = getProperty(pMonitorDescription);
        String s2 = "-1";
        Array array1 = Platform.split(' ', s1);
        if (array1.size() >= 1) {
            Array array2 = Platform.split('=', (String) array1.at(0));
            if (array2.size() == 2) {
                s2 = (String) array2.at(1);
            }
        }
        Array array3 = new Array();
        Enumeration enumeration = array.elements();
        boolean flag = false;
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof Array) {
                array3.add(obj);
                continue;
            }
            if (flag) {
                String s3 = (String) obj;
                if (s3.startsWith("ENDOUTPUT")) {
                    break;
                }
                array3.add(obj);
            } else {
                String s4 = (String) obj;
                if (s4.startsWith("STARTOUTPUT")) {
                    flag = true;
                }
            }
        } 
        
        if (!flag) {
            stringbuffer.append("no data");
            return -1;
        }
        stringbuffer.insert(0, array3.size() + " " + s + " records");
        if (showDebug) {
            LogManager.log("RunMonitor", "logging " + s + ", " + stringbuffer);
        }
        if (s.equals("server")) {
            Enumeration enumeration1 = array3.elements();
            Array array4 = new Array();
            String s5;
            for (; enumeration1.hasMoreElements(); array4.add(s5)) {
                s5 = (String) enumeration1.nextElement();
                int i = s5.indexOf(':');
                if (i != -1) {
                    s5 = s5.substring(i + 1).trim();
                }
            }

            array4.pushFront(s2);
            array4.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            JdbcLogger.logger.logCustom(this, array4, "server_asset", server_asset_create, server_asset_define, server_asset_insert);
        } else if (s.equals("service")) {
            Array array6;
            for (Enumeration enumeration2 = array3.elements(); enumeration2.hasMoreElements(); JdbcLogger.logger.logCustom(this, array6, "service_asset", service_asset_create, service_asset_create2, service_asset_define, service_asset_insert)) {
                Object obj1 = enumeration2.nextElement();
                array6 = splitRecord(obj1);
                array6.pushFront(s2);
                array6.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("software")) {
            Array array7;
            for (Enumeration enumeration3 = array3.elements(); enumeration3.hasMoreElements(); JdbcLogger.logger.logCustom(this, array7, "software_asset", software_asset_create, software_asset_create2, software_asset_define, software_asset_insert)) {
                Object obj2 = enumeration3.nextElement();
                array7 = splitRecord(obj2);
                array7.pushFront(s2);
                array7.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("drive")) {
            Array array8;
            for (Enumeration enumeration4 = array3.elements(); enumeration4.hasMoreElements(); JdbcLogger.logger.logCustom(this, array8, "drive_asset", drive_asset_create, drive_asset_define, drive_asset_insert)) {
                Object obj3 = enumeration4.nextElement();
                array8 = splitRecord(obj3);
                array8.pushFront(s2);
                array8.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("nic")) {
            Array array9;
            for (Enumeration enumeration5 = array3.elements(); enumeration5.hasMoreElements(); JdbcLogger.logger.logCustom(this, array9, "nic_asset", nic_asset_create, nic_asset_define, nic_asset_insert)) {
                Object obj4 = enumeration5.nextElement();
                array9 = splitRecord(obj4);
                array9.pushFront(s2);
                array9.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("unix_server")) {
            Enumeration enumeration6 = array3.elements();
            Array array5 = new Array();
            String s6;
            for (; enumeration6.hasMoreElements(); array5.add(s6)) {
                s6 = (String) enumeration6.nextElement();
                int j = s6.indexOf(':');
                if (j != -1) {
                    s6 = s6.substring(j + 1).trim();
                }
            }

            array5.pushFront(s2);
            array5.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            JdbcLogger.logger.logCustom(this, array5, "unix_server_asset", unix_server_asset_create, unix_server_asset_define, unix_server_asset_insert);
        } else if (s.equals("unix_procs")) {
            Array array10;
            for (Enumeration enumeration7 = array3.elements(); enumeration7.hasMoreElements(); JdbcLogger.logger.logCustom(this, array10, "unix_procs", unix_procs_create, unix_procs_define, unix_procs_insert)) {
                Object obj5 = enumeration7.nextElement();
                array10 = splitRecord(obj5);
                array10.pushFront(s2);
                array10.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("unix_patch")) {
            Array array11;
            for (Enumeration enumeration8 = array3.elements(); enumeration8.hasMoreElements(); JdbcLogger.logger.logCustom(this, array11, "unix_patch", unix_patch_create, unix_patch_define, unix_patch_insert)) {
                Object obj6 = enumeration8.nextElement();
                array11 = splitRecord(obj6);
                array11.pushFront(s2);
                array11.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("unix_package")) {
            Array array12;
            for (Enumeration enumeration9 = array3.elements(); enumeration9.hasMoreElements(); JdbcLogger.logger.logCustom(this, array12, "unix_package", unix_package_create, unix_package_create2, unix_package_create3, unix_package_define,
                    unix_package_insert)) {
                Object obj7 = enumeration9.nextElement();
                array12 = splitRecord(obj7);
                array12.pushFront(s2);
                array12.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("unix_drive")) {
            Array array13;
            for (Enumeration enumeration10 = array3.elements(); enumeration10.hasMoreElements(); JdbcLogger.logger.logCustom(this, array13, "unix_drive_asset", unix_drive_asset_create, unix_drive_asset_define, unix_drive_asset_insert)) {
                Object obj8 = enumeration10.nextElement();
                array13 = splitRecord(obj8);
                array13.pushFront(s2);
                array13.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else if (s.equals("unix_nic")) {
            Array array14;
            for (Enumeration enumeration11 = array3.elements(); enumeration11.hasMoreElements(); JdbcLogger.logger.logCustom(this, array14, "unix_nic_asset", unix_nic_asset_create, unix_nic_asset_define, unix_nic_asset_insert)) {
                Object obj9 = enumeration11.nextElement();
                array14 = splitRecord(obj9);
                array14.pushFront(s2);
                array14.add(JdbcLogger.jdbcDateFormat(Platform.makeDate()));
            }

        } else {
            stringbuffer.append("unknown asset type, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        return 0;
    }

    String addParameters() {
        String s = getProperty("_parameters");
        if (s.length() > 0) {
            s = " " + s;
        }
        return s;
    }

    void appendOutput(String s, String s1, int i, Array array) {
        if (showDebug) {
            LogManager.log("RunMonitor", "asset command=" + s1);
            LogManager.log("RunMonitor", " result=" + i);
            LogManager.log("RunMonitor", " machine=" + s);
            for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); LogManager.log("RunMonitor", " output=" + enumeration.nextElement())) {
            }
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        long l = Platform.timeMillis();
        int i = -1;
        long l1 = getPropertyAsLong(pMaxMeasurement);
        Array array = new Array();
        StringBuffer stringbuffer = new StringBuffer();
        try {
            String s = getProperty(pMachineName);
            String s1 = getProperty(pAsset);
            if (Platform.isNTRemote(s)) {
                s = s.substring(2);
                String s2 = Platform.getRoot() + "/assets/" + s1 + ".vbs";
                File file = new File(s2);
                String s4 = "cscript " + file.getAbsolutePath() + " " + s;
                if (showDebug) {
                    LogManager.log("RunMonitor", "Asset command: " + s4);
                }
                CommandLine commandline = new CommandLine();
                array = commandline.exec(s4, "", Platform.getLock(s));
                i = commandline.getExitValue();
                if (i == 70) {
                    i = 401;
                } else if (i == 462) {
                    i = kURLNoConnectionError;
                }
                appendOutput(s, s4, i, array);
            } else {
                if (s1.equals("unix_drive")) {
                    i = getDisks(s, array, stringbuffer);
                } else if (s1.equals("unix_procs")) {
                    i = getProcs(s, array, stringbuffer);
                } else if (s1.equals("unix_server")) {
                    i = getServer(s, array, stringbuffer);
                } else if (s1.equals("unix_nic")) {
                    i = getNics(s, array, stringbuffer);
                } else if (s1.equals("unix_package")) {
                    i = getPackages(s, array, stringbuffer);
                } else if (s1.equals("unix_patch")) {
                    i = getPatches(s, array, stringbuffer);
                }
                if (array.size() > 0 || s1.equals("unix_software") || s1.equals("unix_patch")) {
                    array.pushFront("STARTOUTPUT");
                    array.add("ENDOUTPUT");
                }
            }
            if (i == 0) {
                i = logAssets(array, stringbuffer);
            }
        } catch (Exception exception) {
            LogManager.log("RunMonitor", "Asset monitor error: " + exception);
        }
        long l2 = Platform.timeMillis() - l;
        if (stillActive()) {
            synchronized (this) {
                setProperty(pStatus, i);
                setProperty(pRecords, array.size());
                setProperty(pRoundTripTime, l2);
                setProperty(pMeasurement, getMeasurement(pRoundTripTime, l1));
                String s3 = stringbuffer.toString();
                if (i != 0) {
                    String s5 = "";
                    if (i == 429) {
                        s5 = "WMI not installed";
                    } else {
                        s5 = lookupStatus(i);
                    }
                    if (s3.length() > 0) {
                        s3 = s5 + ", " + s3;
                    } else {
                        s3 = s5;
                    }
                }
                setProperty(pStateString, s3);
            }
        }
        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @return
     */
    public int getDisks(String s, Array array, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("disks", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading disk command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading disk settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        Enumeration enumeration = array1.elements();
        int i = osadapter.getCommandSettingAsInteger("disks", "total");
        int j = osadapter.getCommandSettingAsInteger("disks", "name");
        int k = osadapter.getCommandSettingAsInteger("disks", "mount");
        LineReader linereader = new LineReader(array1, osadapter, "disks");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s2 = linereader.getCurrentLine();
                if (s2.startsWith("/dev")) {
                    long l = 0L;
                    String s4 = linereader.readColumn(j, "name");
                    int i1 = k;
                    int j1 = i;
                    String s5 = linereader.readColumn(i1, "mount");
                    if (s5.length() == 0 && enumeration.hasMoreElements()) {
                        linereader.processLine();
                        String s3 = linereader.getCurrentLine();
                        i1 --;
                        j1 --;
                        s5 = linereader.readColumn(i1, "mount");
                    }
                    String s6 = linereader.readColumn(j1, "total");
                    if (s6.length() > 0) {
                        l = TextUtils.readLong(s6, 0);
                    }
                    Array array2 = new Array();
                    array2.add(s5);
                    array2.add(s4);
                    array2.add("" + l);
                    debugAsset("disk", array2);
                    array.add(array2);
                }
            }
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @return
     */
    public int getPatches(String s, Array array, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("patch", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading patch command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading patch settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        String s2 = osadapter.getCommandSetting("patch", "match");
        String s3 = osadapter.getCommandSetting("patch", "matchPrefix");
        LineReader linereader = new LineReader(array1, osadapter, "patch");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s4 = linereader.getCurrentLine();
                if (s2.length() > 0) {
                    int i = s4.indexOf(s2);
                    if (i != -1) {
                        String s6 = s4.substring(i + s2.length()).trim();
                        int j = s6.indexOf(' ');
                        if (j != -1) {
                            s6 = s6.substring(0, j);
                            int k = s6.indexOf('-');
                            if (k != -1) {
                                String s8 = s6.substring(0, k);
                                String s9 = s6.substring(k + 1);
                                Array array3 = new Array();
                                array3.add(s8);
                                array3.add(s9);
                                debugAsset("patch", array3);
                                array.add(array3);
                            }
                        }
                    }
                }
                if (s3.length() > 0) {
                    s4 = s4.trim();
                    if (s4.startsWith(s3)) {
                        String s5 = linereader.readColumn(1, "id").trim();
                        String s7 = linereader.readColumn(2, "rev").trim();
                        if (s5.length() > 0 && s7.length() > 0) {
                            Array array2 = new Array();
                            array2.add(s5);
                            array2.add(s7);
                            debugAsset("patch", array2);
                            array.add(array2);
                        }
                    }
                }
            }
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @return
     */
    public int getNics(String s, Array array, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("nicHost", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading nic host command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading nic host settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        Enumeration enumeration = array1.elements();
        int i = osadapter.getCommandSettingAsInteger("nicHost", "name");
        int j = osadapter.getCommandSettingAsInteger("nicHost", "host");
        HashMap hashmap = new HashMap();
        LineReader linereader = new LineReader(array1, osadapter, "nicHost");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s2 = linereader.readColumn(i, "name");
                if (s2.length() != 0 && s2.indexOf("Name") == -1) {
                    String s3 = linereader.readColumn(j, "host");
                    hashmap.put(s2, s3);
                }
            }
        } 
        
        s1 = Machine.getCommandString("nic", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading nic command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading nic settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        commandline = new CommandLine();
        array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        enumeration = array1.elements();
        i = osadapter.getCommandSettingAsInteger("nic", "name");
        int k = osadapter.getCommandSettingAsInteger("nic", "address");
        linereader = new LineReader(array1, osadapter, "nic");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s4 = linereader.readColumn(i, "name");
                if (s4.length() != 0 && s4.indexOf("Name") == -1) {
                    String s5 = linereader.readColumn(k, "address");
                    String s6 = TextUtils.getValue(hashmap, s4);
                    Array array2 = new Array();
                    array2.add(s4);
                    array2.add("0");
                    array2.add(s5);
                    array2.add(s6);
                    debugAsset("nic", array2);
                    array.add(array2);
                }
            }
        }

        byte byte0 = 0;
        if (array.size() == 0) {
            byte0 = -1;
        }
        return byte0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @return
     */
    public int getProcs(String s, Array array, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("proc", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading proc command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading proc settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        HashMap hashmap = new HashMap();
        String s2 = "$$$";
        LineReader linereader = new LineReader(array1, osadapter, "proc");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s3 = linereader.readColumnByName("user");
                String s4 = linereader.readColumnByName("name");
                String s6 = s4 + s2 + s3;
                TextUtils.incrementEntry(hashmap, s6);
            }
        } 
        
        Array array2;
        for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); array.add(array2)) {
            String s5 = (String) enumeration.nextElement();
            int i = s5.indexOf(s2);
            String s7 = s5.substring(0, i);
            if (s7.length() >= 100) {
                s7 = s7.substring(0, 99);
            }
            String s8 = s5.substring(i + s2.length());
            if (s8.length() >= 20) {
                s8 = s8.substring(0, 19);
            }
            String s9 = TextUtils.getValue(hashmap, s5);
            array2 = new Array();
            array2.add(s7);
            array2.add(s8);
            array2.add(s9);
            debugAsset("proc", array2);
        }

        byte byte0 = 0;
        if (array.size() == 0) {
            byte0 = -1;
        }
        return byte0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    public int addPackageExceptions(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Platform.getRoot() + File.separator + "assets" + File.separator + "software.sh";
        String s2 = "";
        if (!(new File(s1)).exists()) {
            return 0;
        }
        try {
            s2 = FileUtils.readFile(s1).toString();
        } catch (IOException ioexception) {
            stringbuffer.append("error, reading software rules, " + s1 + ", " + ioexception);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        Array array = Platform.split('\n', s2);

        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            String s3 = (String) enumeration.nextElement();
            s3 = s3.trim();
            if (s3.length() != 0 && !s3.startsWith("#")) {
                if (showDebug) {
                    LogManager.log("RunMonitor", "rule=" + s3);
                }
                Array array1 = Platform.split(',', s3);
                if (array1.size() < 3) {
                    LogManager.log("Error", "bad rule, " + s3);
                }
                String s4 = (String) array1.at(0);
                String s5 = (String) array1.at(1);
                String s6 = (String) array1.at(2);
                String s7 = "";
                if (array1.size() >= 4) {
                    s7 = (String) array1.at(3);
                }
                String s8 = "";
                if (array1.size() >= 5) {
                    s8 = (String) array1.at(4);
                }
                if (s5.equals("path")) {
                    CommandLine commandline = new CommandLine();
                    String s9 = getSetting("_softwareAssetPathCommand");
                    if (s9.length() == 0) {
                        s9 = "/bin/sh -c 'ls $path$ >/dev/null 2>/dev/null ; if [ $? -eq 0 ] ; then echo \"installed: $app$\" ; fi'";
                    }
                    s9 = TextUtils.replaceVariable(s9, "$path$", s6);
                    s9 = TextUtils.replaceVariable(s9, "$app$", s4);
                    Array array2 = commandline.exec(s9, s, Platform.getLock(s));
                    appendOutput(s, s9, commandline.getExitValue(), array2);
                    if (commandline.getExitValue() != 0) {
                        return commandline.getExitValue();
                    }

                    String s10;
                    String s11;
                    int i;
                    Enumeration enumeration1 = array2.elements();
                    while (enumeration1.hasMoreElements()) {
                        s10 = (String) enumeration1.nextElement();
                        s11 = "installed: ";
                        i = s10.indexOf(s11);
                        if (i != -1) {
                            String s12 = s10.substring(i + s11.length()).trim();
                            addPackage(hashmap, s12, s7, s8);
                        }
                    }
                } else {
                    LogManager.log("Error", "unknown rule type, " + s5 + ", " + s3);
                }
            }
        }
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @return
     */
    int getPackages(String s, Array array, StringBuffer stringbuffer) {
        HashMap hashmap = new HashMap();
        String s1 = Machine.getCommandString("package", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading package command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading package settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array1 = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array1);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }

        String s2 = osadapter.getCommandSetting("package", "nameMatch");
        String s3 = osadapter.getCommandSetting("package", "descMatch");
        String s4 = osadapter.getCommandSetting("package", "versMatch");
        int i = osadapter.getCommandSettingAsInteger("package", "nameColumn");
        int j = osadapter.getCommandSettingAsInteger("package", "versColumn");
        String s5 = "";
        String s6 = "";
        String s9 = "";
        LineReader linereader = new LineReader(array1, osadapter, "package");

        String s10;
        while (linereader.processLine()) {
            if (linereader.skipLine()) {
                continue;
            }
            s10 = linereader.getCurrentLine();
            if (s2.length() > 0) {
                int k = s10.indexOf(s2);
                if (k != -1) {
                    s5 = s10.substring(k + s2.length()).trim();
                }
                k = s10.indexOf(s3);
                if (k != -1) {
                    s9 = s10.substring(k + s3.length()).trim();
                }
                k = s10.indexOf(s4);
                if (k != -1) {
                    String s7 = s10.substring(k + s4.length()).trim();
                    addPackage(hashmap, s5, s7, s9);
                    s5 = "";
                    s7 = "";
                    s9 = "";
                }
                continue;
            }
            if (s10.startsWith("#")) {
                continue;
            }
            String s8;
            s5 = linereader.readColumn(i, "name");
            s8 = linereader.readColumn(j, "vers");
            if (s5.length() != 0 && s8.length() != 0) {
                try {
                    int l = s10.indexOf(s8) + s8.length();
                    s9 = "";
                    if (l < s10.length()) {
                        s9 = s10.substring(l).trim();
                    }
                    addPackage(hashmap, s5, s8, s9);
                } catch (Exception exception) {
                    LogManager.log("RunMonitor", "skipping package line, " + exception + ", " + s10);
                }
            }
        }

        if (hashmap.size() == 0) {
            return -1;
        }
        addPackageExceptions(s, hashmap, stringbuffer);
        Enumeration enumeration = hashmap.elements();
        while (enumeration.hasMoreElements()) {
            array.add(enumeration.nextElement());
        }
        return 0;
    }

    void addPackage(HashMap hashmap, String s, String s1, String s2) {
        Array array = new Array();
        if (s.length() >= 50) {
            s = s.substring(0, 49);
        }
        if (s1.length() >= 50) {
            s1 = s1.substring(0, 49);
        }
        if (s2.length() >= 255) {
            s2 = s2.substring(0, 254);
        }
        array.add(s);
        array.add(s1);
        array.add(s2);
        debugAsset("pack", array);
        hashmap.put(s, array);
    }

    void debugAsset(String s, Array array) {
        if (showDebug) {
            StringBuffer stringbuffer = new StringBuffer();
            for (int i = 0; i < array.size(); i ++) {
                if (i != 0) {
                    stringbuffer.append(",");
                }
                stringbuffer.append(array.at(i));
            }

            LogManager.log("RunMonitor", "asset, adding " + s + " record, " + stringbuffer);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addProcessors(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("processor", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading processor command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading processor settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        String s2 = osadapter.getCommandSetting("processor", "speedMatch");
        String s3 = osadapter.getCommandSetting("processor", "typeMatch");
        LineReader linereader = new LineReader(array, osadapter, "processor");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s4 = linereader.getCurrentLine();
                int i = s4.indexOf(s2);
                if (i != -1) {
                    String s5 = s4.substring(i + s2.length()).trim();
                    long l = TextUtils.readLong(s5, 0);
                    hashmap.put("cpu_speed", "" + l);
                    if (showDebug) {
                        LogManager.log("RunMonitor", "cpu_speed=" + l);
                    }
                }
                i = s4.indexOf(s3);
                if (i != -1) {
                    String s6 = s4.substring(i + s3.length()).trim();
                    int j = s6.indexOf(' ');
                    if (j != -1) {
                        s6 = s6.substring(0, j);
                        hashmap.put("cpu_type", s6);
                        if (showDebug) {
                            LogManager.log("RunMonitor", "cpu_type=" + s6);
                        }
                    }
                }
            }
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addSingleLine(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("version", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading version command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading version settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        } else {
            LineReader linereader = new LineReader(array, osadapter, "network");
            linereader.processLine();
            String s2 = linereader.getCurrentLine();
            Array array1 = Platform.split(' ', s2);
            hashmap.put("host_name", array1.at(1));
            hashmap.put("machine_name", array1.at(5));
            hashmap.put("os", array1.at(0));
            hashmap.put("os_release", array1.at(2));
            hashmap.put("os_name", array1.at(0));
            hashmap.put("class", array1.at(4));
            return 0;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addSingleCPU(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("processor", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading processor command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading processor settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        String s2 = osadapter.getCommandSetting("processor", "speedMatch");
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        LineReader linereader = new LineReader(array, osadapter, "processor");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s3 = linereader.getCurrentLine();
                int i = s3.indexOf(s2);
                if (i != -1) {
                    String s4 = s3.substring(i + s2.length()).trim();
                    long l = TextUtils.toInt(s4) / 10000;
                    hashmap.put("cpu_speed", "" + l);
                }
            }
        }
        
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addSingleMemory(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("physicalmemory", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading physicalmemory command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading physicalmemory settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        String s2 = osadapter.getCommandSetting("physicalmemory", "memoryMatch");
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        LineReader linereader = new LineReader(array, osadapter, "physicalmemory");
        while (linereader.processLine()) {
            String s3 = linereader.getCurrentLine();
            if (showDebug) {
                LogManager.log("RunMonitor", "memory, line=" + linereader.getCurrentLine());
            }
            if (!linereader.skipLine()) {
                int i = s3.indexOf(s2);
                if (i != -1) {
                    String s4 = s3.substring(i + s2.length()).trim();
                    long l = TextUtils.toInt(s4) / 256;
                    hashmap.put("tot_ram", "" + l);
                }
            }
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addSingleCPUCount(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("processors", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading processors command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading processors settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        String s2 = osadapter.getCommandSetting("processors", "match");
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        int i = 0;
        LineReader linereader = new LineReader(array, osadapter, "processors");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s3 = linereader.getCurrentLine();
                int j = s3.indexOf(s2);
                if (j != -1) {
                    i ++;
                }
            }
        } 
        
        hashmap.put("num_cpu", "" + i);
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addVersions(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("version", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading version command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading version settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        LineReader linereader = new LineReader(array, osadapter, "network");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s2 = linereader.getCurrentLine();
                String s3 = "Node = ";
                int i = s2.indexOf(s3);
                if (i != -1) {
                    String s4 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "machine_name=" + s4);
                    }
                    if (showDebug) {
                        LogManager.log("RunMonitor", "host_name=" + s4);
                    }
                    hashmap.put("host_name", s4);
                    hashmap.put("machine_name", s4);
                }
                s3 = "System = ";
                i = s2.indexOf(s3);
                if (i != -1) {
                    String s5 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "os=" + s5);
                    }
                    hashmap.put("os", s5);
                }
                s3 = "Release = ";
                i = s2.indexOf(s3);
                if (i != -1) {
                    String s6 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "os_version=" + s6);
                    }
                    hashmap.put("os_version", s6);
                }
                s3 = "KernelID = ";
                i = s2.indexOf(s3);
                if (i != -1) {
                    String s7 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "os_release=" + s7);
                    }
                    hashmap.put("os_release", s7);
                }
                s3 = "Machine = ";
                i = s2.indexOf(s3);
                if (i != -1) {
                    String s8 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "class=" + s8);
                    }
                    hashmap.put("class", s8);
                }
                s3 = "NumCPU = ";
                i = s2.indexOf(s3);
                if (i != -1) {
                    String s9 = s2.substring(i + s3.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "num_cpu=" + s9);
                    }
                    hashmap.put("num_cpu", s9);
                }
            }
        }
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addPlatform(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("platform", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading platform command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading platform settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        LineReader linereader = new LineReader(array, osadapter, "platform");
        while (linereader.processLine()) {
            if (linereader.skipLine()) {
                continue;
            }
            String s2 = linereader.getCurrentLine();
            String s3 = s2.trim();
            if (showDebug) {
                LogManager.log("RunMonitor", "platform=" + s3);
            }
            hashmap.put("platform", s3);
            break;
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addBoot(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("bootprom", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading bootprom command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading bootprom settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        LineReader linereader = new LineReader(array, osadapter, "network");
        while (linereader.processLine()) {
            if (linereader.skipLine()) {
                continue;
            }
            String s2 = linereader.getCurrentLine();
            String s3 = s2.trim();
            if (showDebug) {
                LogManager.log("RunMonitor", "bootprom=" + s3);
            }
            hashmap.put("obp", s3);
            break;
        } 
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addMemory(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("config", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading config command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading config settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        String s2 = osadapter.getCommandSetting("config", "memoryMatch");
        LineReader linereader = new LineReader(array, osadapter, "config");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s3 = linereader.getCurrentLine();
                int i = s3.indexOf(s2);
                if (i != -1) {
                    String s4 = s3.substring(i + s2.length()).trim();
                    long l = TextUtils.readLong(s4, 0);
                    hashmap.put("tot_ram", "" + l);
                    if (showDebug) {
                        LogManager.log("RunMonitor", "tot_ram=" + l);
                    }
                }
            }
        } 
        
        return 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param hashmap
     * @param stringbuffer
     * @return
     */
    int addAddress(String s, HashMap hashmap, StringBuffer stringbuffer) {
        String s1 = Machine.getCommandString("address", s);
        if (s1.length() == 0) {
            stringbuffer.append("error, reading address command for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        OSAdapter osadapter = Machine.getAdapter(s);
        if (osadapter == null) {
            stringbuffer.append("error, reading address settings for server, " + s);
            LogManager.log("Error", stringbuffer.toString());
            return -1;
        }
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, Platform.getLock(s));
        appendOutput(s, s1, commandline.getExitValue(), array);
        if (commandline.getExitValue() != 0) {
            return commandline.getExitValue();
        }
        String s2 = osadapter.getCommandSetting("address", "addressMatch");
        LineReader linereader = new LineReader(array, osadapter, "address");
        while (linereader.processLine()) {
            if (!linereader.skipLine()) {
                String s3 = linereader.getCurrentLine();
                int i = s3.indexOf(s2);
                if (i != -1) {
                    String s4 = s3.substring(i + s2.length()).trim();
                    if (showDebug) {
                        LogManager.log("RunMonitor", "mac_address=" + s4);
                    }
                    hashmap.put("mac_address", s4);
                }
            }
        }
        
        return 0;
    }

    public int getServer(String s, Array array, StringBuffer stringbuffer) {
        HashMap hashmap = new HashMap();
        int i = 0;
        OSAdapter osadapter = Machine.getAdapter(s);
        String s1 = osadapter.getCommandSetting("version", "singleLine");
        if (s1.length() > 0) {
            i = addSingleLine(s, hashmap, stringbuffer);
            if (i == 0) {
                i = addSingleCPUCount(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addSingleCPU(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addSingleMemory(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addAddress(s, hashmap, stringbuffer);
            }
        } else {
            i = addVersions(s, hashmap, stringbuffer);
            if (i == 0) {
                i = addPlatform(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addProcessors(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addBoot(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addMemory(s, hashmap, stringbuffer);
            }
            if (i == 0) {
                i = addAddress(s, hashmap, stringbuffer);
            }
        }
        if (i == 0) {
            array.add("num_cpu: " + TextUtils.getValue(hashmap, "num_cpu"));
            array.add("cpu_type: " + TextUtils.getValue(hashmap, "cpu_type"));
            array.add("cpu_speed: " + TextUtils.getValue(hashmap, "cpu_speed"));
            array.add("tot_ram: " + TextUtils.getValue(hashmap, "tot_ram"));
            array.add("obp: " + TextUtils.getValue(hashmap, "obp"));
            array.add("os: " + TextUtils.getValue(hashmap, "os"));
            array.add("os_version: " + TextUtils.getValue(hashmap, "os_version"));
            array.add("os_release: " + TextUtils.getValue(hashmap, "os_release"));
            array.add("class: " + TextUtils.getValue(hashmap, "class"));
            array.add("platform: " + TextUtils.getValue(hashmap, "platform"));
            array.add("build_version: " + TextUtils.getValue(hashmap, "build_version"));
            array.add("mac_address: " + TextUtils.getValue(hashmap, "mac_address"));
        }
        debugAsset("unix_server", array);
        return i;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pAsset) {
            Vector vector = new Vector();
            File file = new File(Platform.getUsedDirectoryPath("assets", httprequest.getAccount()));
            String as[] = file.list();
            for (int i = 0; i < as.length; i ++) {
                String s = as[i];
                if (!s.endsWith(".vbs")) {
                    continue;
                }
                File file1 = new File(file, as[i]);
                if (!file1.isDirectory()) {
                    vector.addElement(s.substring(0, s.length() - 4));
                    vector.addElement(s.substring(0, s.length() - 4));
                }
            }

            return vector;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pRecords);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pExpression) {
            String s1 = TextUtils.legalMatchString(s);
            if (s1.length() > 0) {
                hashmap.put(stringproperty, s1);
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    static {
        pAsset = new ScalarProperty("_asset", "");
        pAsset.setDisplayText("Asset", "the kind of asset to collect data about");
        pAsset.setParameterOptions(true, 1, false);
        pExpression = new StringProperty("_expression");
        pExpression.setDisplayText("Match Expression", "optional Perl regular expression to match against the output of the script, to extract values (example: /(\\d*) File.*([\\d,]*) bytes/).");
        pExpression.setParameterOptions(true, 2, true);
        pMaxMeasurement = new NumericProperty("_maxMeasurement", "0", "milleseconds");
        pMaxMeasurement.setDisplayText("Maximum for Measurement",
                "optional value to specify as maximum for gauge display in milleseconds (example: if the runtime of the script is 4 seconds and this value is set at 8 seconds (8000ms) than the gauge will show at 50%");
        pMaxMeasurement.setParameterOptions(true, 2, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(2);
        pRecords = new StringProperty("records");
        pRecords.setLabel("records");
        pRecords.setStateOptions(3);
        StringProperty astringproperty[] = { pAsset, pExpression, pStatus, pRecords, pRoundTripTime, pMaxMeasurement };
        addProperties("COM.dragonflow.StandardMonitor.AssetMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.AssetMonitor", Rule.stringToClassifier("status != 0\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.AssetMonitor", Rule.stringToClassifier("status == 0\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "description", "Collect asset information on a server");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "help", "AssetMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "title", "Asset");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "class", "AssetMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "target", "_asset");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "topazName", "Asset");
        setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "topazType", "System Resources");
        if ((new File(Platform.getRoot() + File.separator + "assets")).exists()) {
            setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "loadable", "true");
        } else {
            setClassProperty("COM.dragonflow.StandardMonitor.AssetMonitor", "loadable", "false");
        }
    }
}