// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:15:45
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EmsTimeDiffProperty.java

package COM.dragonflow.ems.Shared;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.FrequencyProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.Utils.TextUtils;
import jgl.HashMap;

public class EmsTimeDiffProperty extends FrequencyProperty
{

    public EmsTimeDiffProperty()
    {
        this("", "");
    }

    public EmsTimeDiffProperty(String sName, String sValue)
    {
        super(sName != null && sName.length() != 0 ? sName : "_emsTimeDiff", sValue);
        setDisplayText("EMS Time Difference", "Time difference between the EMS and the SiteView machines.");
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors)
    {
        if(property == this)
        {
            String sUnits = request.getValue(property.getName() + "Units");
            int iSeconds = FrequencyProperty.toSeconds(TextUtils.toInt(value), sUnits);
            return "" + iSeconds;
        } else
        {
            return "0";
        }
    }

    public long getEmsTimeDiff(AtomicMonitor monitor)
    {
        return monitor == null ? 0L : FrequencyProperty.toSeconds(monitor.getPropertyAsInteger(this), this + "Units");
    }

    public static final String UNITS = "Units";
    public static final String PN_EMS_TIME_DIFF = "_emsTimeDiff";
    protected static final String EMS_TIME_DIFF_TEXT = "EMS Time Difference";
    protected static final String EMS_TIME_DIFF_DESCRIPTION = "Time difference between the EMS and the SiteView machines.";
}