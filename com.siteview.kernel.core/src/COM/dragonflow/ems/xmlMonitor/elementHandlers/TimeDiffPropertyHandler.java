// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TimeDiffPropertyHandler.java

package COM.dragonflow.ems.xmlMonitor.elementHandlers;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.ems.Shared.EmsTimeDiffProperty;
import java.util.Vector;
import org.xml.sax.Attributes;

// Referenced classes of package COM.dragonflow.ems.xmlMonitor.elementHandlers:
//            PropertyHandler, PropertyHandlerDispatcher

public class TimeDiffPropertyHandler
    implements PropertyHandler
{

    public TimeDiffPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public void handleProperty(Attributes attrs, Vector props)
    {
        StringProperty p = new EmsTimeDiffProperty();
        int counter = dispatcher.getCounter(true);
        p.setParameterOptions(true, counter, true);
        props.add(p);
    }

    private PropertyHandlerDispatcher dispatcher;
}