// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:25
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MeasurementConfigFilePathPropertyHandler.java

package COM.dragonflow.ems.xmlMonitor.elementHandlers;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.ems.Shared.EmsMeasurementConfigFileProperty;

// Referenced classes of package COM.dragonflow.ems.xmlMonitor.elementHandlers:
//            ConfigFilePathPropertyHandler, PropertyHandlerDispatcher

public class MeasurementConfigFilePathPropertyHandler extends ConfigFilePathPropertyHandler
{

    public MeasurementConfigFilePathPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        super(dispatcher);
    }

    protected StringProperty getProperty()
    {
        StringProperty p = new EmsMeasurementConfigFileProperty(dispatcher.getFolderName());
        return p;
    }
}