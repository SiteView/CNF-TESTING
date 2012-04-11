// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-2-22 15:16:04
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StaticInitializeSaxHandler.java

package COM.dragonflow.ems.xmlMonitor;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.ems.xmlMonitor.elementHandlers.PropertyHandlerDispatcher;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StaticInitializeSaxHandler extends DefaultHandler
{

    public StaticInitializeSaxHandler()
    {
        props = new Vector();
        propertyHandler = new PropertyHandlerDispatcher();
    }

    public StringProperty[] getProps()
    {
        return (StringProperty[])props.toArray(dummy);
    }

    public void startElement(String arg0, String name, String arg2, Attributes attrs)
        throws SAXException
    {
        if(name.compareTo(MONITOR) == 0)
            handleMonitor(attrs);
        else
        if(name.endsWith(PROPERTY))
            propertyHandler.handleElement(name, props, attrs);
    }

    private void handleMonitor(Attributes attrs)
    {
        String folderName = attrs.getValue(EMS_FOLDER_NAME);
        propertyHandler.setEmsFolderName(folderName);
    }

    private static StringProperty dummy[] = new StringProperty[0];
    private static String MONITOR = "Monitor";
    private static String PROPERTY = "Property";
    private static String EMS_FOLDER_NAME = "emsFolderName";
    private Vector props;
    private PropertyHandlerDispatcher propertyHandler;

}