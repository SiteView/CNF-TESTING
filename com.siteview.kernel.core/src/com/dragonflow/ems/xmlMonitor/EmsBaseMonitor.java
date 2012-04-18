// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:16:03
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EmsBaseMonitor.java

package com.dragonflow.ems.xmlMonitor;


public abstract class EmsBaseMonitor
{

    public EmsBaseMonitor()
    {
    }

    public static final String DB_URL = "_pDbUrl";
    public static final String DRIVER_NAME = "_pDbDriverName";
    public static final String START_TIME = "_pStartTime";
    public static final String DYN_START_TIME = "pStartTime";
    public static final String PS_COLLECTED = "pCollected";
    public static final String DB_USER_NAME = "_pDbUserName";
    public static final String DB_PASSWORD = "_pDbPassword";
    public static final String DB_SELECT = "_pDbSelect";
    public static final String DB_WHERE = "_pDbWhere";
    public static final String DB_FROM = "_pDbFrom";
    public static final String MONITOR_STATUS = "pMStatus";
    public static String ALERT_TYPE = "Alert";
    public static String MEASUREMENT_TYPE = "Measurement";
    public static final String OLD_PS_COLLECTED = "pCollected";
    public static final String OLD_START_TIME = "_resetEmum";
    public static final String OLD_DYN_START_TIME = "lastEmum";
    public static final String OLD_DB_URL = "_database";
    public static final String OLD_DB_DRIVER_NAME = "_driver";
    public static final String OLD_DB_USER_NAME = "_user";
    public static final String OLD_DB_PASSWORD = "_password";
    public static final String OLD_DB_SELECT = "_select";
    public static final String OLD_DB_WHERE = "_where";
    public static final String OLD_DB_FROM = "_from";
    public static final String OLD_DB_ORDER_BY = "_orderBy";
    public static final String OLD_DB_ENUM_FIELD_TYPE = "_fieldType";
    public static final String OLD_DB_ENUM_NAME = "_emsTablePrimaryKeys";

}