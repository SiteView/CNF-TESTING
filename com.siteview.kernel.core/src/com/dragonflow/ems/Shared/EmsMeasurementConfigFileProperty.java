// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:15:44
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   EmsMeasurementConfigFileProperty.java

package com.dragonflow.ems.Shared;


// Referenced classes of package com.dragonflow.ems.Shared:
//            EmsConfigFileProperty

public class EmsMeasurementConfigFileProperty extends EmsConfigFileProperty
{

    public EmsMeasurementConfigFileProperty(String sMonitorName)
    {
        super(sMonitorName, "");
    }

    protected String getDefaultValue()
    {
        return "measurement.config";
    }

    public static String getMeasurementConfigFilePath(String emsName, String val)
    {
        if(val != null && !"".equals(val))
            return val;
        else
            return EmsConfigFileProperty.createConfigFileName(emsName, "measurement.config");
    }
}