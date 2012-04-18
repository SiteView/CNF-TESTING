/*
 * 
 * Created on 2005-2-28 7:05:03
 *
 * PropertyTable.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>PropertyTable</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Properties:
// StringProperty

public class PropertyTable extends HashMap implements java.io.Serializable {

    private PropertyTable parent;

    public PropertyTable(PropertyTable propertytable) {
        super(true);
        parent = propertytable;
    }

    public Object get(Object obj) {
        Object obj1 = super.get(obj);
        if (obj1 == null && parent != null) {
            return parent.get(obj);
        } else {
            return obj1;
        }
    }

    public Object get(Object obj, int i) {
        int j = count(obj);
        if (j == 0 && parent != null) {
            return parent.get(obj);
        }
        if (j == 1) {
            return super.get(obj);
        }
        for (Enumeration enumeration = (Enumeration) values(obj); enumeration.hasMoreElements();) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.onPlatform(i)) {
                return stringproperty;
            }
        }

        return null;
    }

    public Array getProperties(int i) {
        Array array = new Array();
        getProperties(array, i);
        return array;
    }

    /**
     * 
     * 
     * @param array
     * @param i
     */
    public void getProperties(Array array, int i) {
        if (parent != null) {
            parent.getProperties(array, i);
        }
        Enumeration enumeration = elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.onPlatform(i)) {
                array.add(stringproperty);
            }
        }
    }

    /**
     * 
     * 
     * @param i
     * @return
     */
    public Array getImmediateProperties(int i) {
        Array array = new Array();
        Enumeration enumeration = elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.onPlatform(i)) {
                array.add(stringproperty);
            }
        } 
        return array;
    }
}
