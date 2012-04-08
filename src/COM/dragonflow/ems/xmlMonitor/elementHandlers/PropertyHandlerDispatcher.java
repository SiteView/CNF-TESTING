// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 13:55:28
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PropertyHandlerDispatcher.java

package COM.dragonflow.ems.xmlMonitor.elementHandlers;

import COM.dragonflow.SiteView.Platform;
import java.io.File;
import java.util.*;
import org.xml.sax.Attributes;

// Referenced classes of package COM.dragonflow.ems.xmlMonitor.elementHandlers:
//            StringPropertyHandler, HiddenPropertyHandler, IntPropertyHandler, ConfigFilePathPropertyHandler, 
//            MeasurementConfigFilePathPropertyHandler, PasswordPropertyHandler, TimeDiffPropertyHandler, PropertyHandler

public class PropertyHandlerDispatcher
{

    public PropertyHandlerDispatcher()
    {
        basicCounter = 0;
        advancedCounter = 25;
        handlers = new HashMap();
        handlers.put("StringProperty", new StringPropertyHandler(this));
        handlers.put("HiddenStringProperty", new HiddenPropertyHandler(this));
        handlers.put("IntProperty", new IntPropertyHandler(this));
        handlers.put("ConfigFilePathProperty", new ConfigFilePathPropertyHandler(this));
        handlers.put("MeasurementConfigFilePathProperty", new MeasurementConfigFilePathPropertyHandler(this));
        handlers.put("PasswordProperty", new PasswordPropertyHandler(this));
        handlers.put("TimeDiffProperty", new TimeDiffPropertyHandler(this));
    }

    public void handleElement(String elementName, Vector props, Attributes attrs)
    {
        getHandler(elementName).handleProperty(attrs, props);
    }

    public void setEmsFolderName(String name)
    {
        emsFolderName = name;
        emsFolderPath = ROOT + File.separator + "ems" + File.separator + emsFolderName + File.separator;
    }

    private PropertyHandler getHandler(String type)
    {
        PropertyHandler handler = (PropertyHandler)handlers.get(type);
        return handler;
    }

    int getCounter(boolean advanced)
    {
        if(advanced)
        {
            advancedCounter++;
            return advancedCounter;
        } else
        {
            basicCounter++;
            return basicCounter;
        }
    }

    String getRootFolder()
    {
        return emsFolderPath;
    }

    public String getFolderName()
    {
        return emsFolderName;
    }

    Map handlers;
    private int basicCounter;
    private int advancedCounter;
    private static final String STRING_PROPERTY = "StringProperty";
    private static final String CONFIG_PROPERTY = "ConfigFilePathProperty";
    private static final String MEASUREMENT_CONFIG_PROPERTY = "MeasurementConfigFilePathProperty";
    private static final String TIME_DIFF_PROPERTY = "TimeDiffProperty";
    private static final String HIDDEN_STRING_PROPERTY = "HiddenStringProperty";
    private static final String PASSWORD_STRING_PROPERTY = "PasswordProperty";
    private static final String INT_PROPERTY = "IntProperty";
    private String monitorName;
    private String emsFolderName;
    private String emsFolderPath;
    public static String ROOT = Platform.getRoot();

}