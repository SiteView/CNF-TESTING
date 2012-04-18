// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:26
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   HiddenPropertyHandler.java

package com.dragonflow.ems.xmlMonitor.elementHandlers;

import com.dragonflow.Properties.StringProperty;
import java.util.Vector;
import org.xml.sax.Attributes;

// Referenced classes of package com.dragonflow.ems.xmlMonitor.elementHandlers:
//            StringPropertyHandler, PropertyHandlerDispatcher

public class HiddenPropertyHandler extends StringPropertyHandler
{

    public HiddenPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        super(dispatcher);
    }

    public void handleProperty(Attributes attrs, Vector props)
    {
        boolean isState = getBoolAttribute(attrs, "isState");
        int counter = dispatcher.getCounter(false);
        StringProperty p = getProperty(attrs.getValue("name"), handleDefaultValue(attrs.getValue("value")));
        p.setParameterOptions(false, counter, false);
        p.setIsStateProperty(isState);
        props.add(p);
    }

    private static final String IS_STATE = "isState";
}