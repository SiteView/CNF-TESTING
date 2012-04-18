// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:27
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PasswordPropertyHandler.java

package com.dragonflow.ems.xmlMonitor.elementHandlers;

import com.dragonflow.Properties.StringProperty;

// Referenced classes of package com.dragonflow.ems.xmlMonitor.elementHandlers:
//            StringPropertyHandler, PropertyHandlerDispatcher

public class PasswordPropertyHandler extends StringPropertyHandler
{

    public PasswordPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        super(dispatcher);
    }

    protected StringProperty getProperty(String name, String defaultVal)
    {
        StringProperty p = super.getProperty(name, defaultVal);
        p.isPassword = true;
        return p;
    }
}