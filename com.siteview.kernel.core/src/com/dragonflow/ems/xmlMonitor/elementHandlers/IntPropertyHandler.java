// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:26
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IntPropertyHandler.java

package com.dragonflow.ems.xmlMonitor.elementHandlers;

import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.StringProperty;

// Referenced classes of package com.dragonflow.ems.xmlMonitor.elementHandlers:
//            StringPropertyHandler, PropertyHandlerDispatcher

public class IntPropertyHandler extends StringPropertyHandler
{

    public IntPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        super(dispatcher);
    }

    protected StringProperty getProperty(String name, String defaultVal)
    {
        StringProperty p;
        if(defaultVal != null && defaultVal.trim().length() > 0)
            p = new NumericProperty(name, defaultVal);
        else
            p = new NumericProperty(name);
        return p;
    }
}