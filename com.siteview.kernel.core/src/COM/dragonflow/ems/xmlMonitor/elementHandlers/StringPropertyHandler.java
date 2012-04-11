// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:29
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StringPropertyHandler.java

package COM.dragonflow.ems.xmlMonitor.elementHandlers;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.ems.xmlMonitor.StaticInitializer;
import java.util.Vector;
import org.xml.sax.Attributes;

// Referenced classes of package COM.dragonflow.ems.xmlMonitor.elementHandlers:
//            PropertyHandler, PropertyHandlerDispatcher

public class StringPropertyHandler
    implements PropertyHandler
{

    public StringPropertyHandler(PropertyHandlerDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public void handleProperty(Attributes attrs, Vector props)
    {
        boolean advanced = getBoolAttribute(attrs, "isAdvanced");
        StringProperty p = initProperty(attrs.getValue("name"), handleDefaultValue(attrs.getValue("value")), attrs.getValue("label"), attrs.getValue("description"), advanced, getBoolAttribute(attrs, "isEditable"), getStateOptions(attrs.getValue("stateOptions")));
        props.add(p);
    }

    private int getStateOptions(String value)
    {
        int val = -1;
        if(value != null)
            val = Integer.parseInt(value);
        return val;
    }

    protected String handleDefaultValue(String defaultValue)
    {
        return defaultValue;
    }

    protected StringProperty getProperty(String name, String defaultVal)
    {
        StringProperty p;
        if(defaultVal != null && defaultVal.trim().length() > 0)
            p = new StringProperty(name, defaultVal);
        else
            p = new StringProperty(name);
        return p;
    }

    protected boolean getBoolAttribute(Attributes attrs, String name)
    {
        String val = attrs.getValue(name);
        return val != null && val.compareTo("true") == 0;
    }

    protected StringProperty initProperty(String name, String defaultValue, String label, String description, boolean advanced, boolean isEditable, int stateOptions)
    {
        int counter = dispatcher.getCounter(advanced);
        StringProperty property = getProperty(name, defaultValue);
        property.setDisplayText(StaticInitializer.getString(label), StaticInitializer.getString(description));
        if(stateOptions > 0)
            property.setStateOptions(stateOptions);
        property.setParameterOptions(isEditable, counter, advanced);
        return property;
    }

    protected PropertyHandlerDispatcher dispatcher;
}