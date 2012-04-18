// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:27
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ConfigFilePathPropertyHandler.java

package com.dragonflow.ems.xmlMonitor.elementHandlers;

import com.dragonflow.Properties.StringProperty;
import com.dragonflow.ems.Shared.EmsConfigFileProperty;
import java.util.Vector;
import org.xml.sax.Attributes;

// Referenced classes of package com.dragonflow.ems.xmlMonitor.elementHandlers:
//            PropertyHandler, PropertyHandlerDispatcher

public class ConfigFilePathPropertyHandler
    implements PropertyHandler
{

    public ConfigFilePathPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    protected StringProperty getProperty()
    {
        StringProperty p = new EmsConfigFileProperty(dispatcher.getFolderName());
        return p;
    }

    public void handleProperty(Attributes attrs, Vector props)
    {
        StringProperty p = getProperty();
        int counter = dispatcher.getCounter(true);
        p.setParameterOptions(true, counter, true);
        props.add(p);
    }

    protected PropertyHandlerDispatcher dispatcher;
}