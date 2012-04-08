/*
 * 
 * Created on 2005-2-15 12:30:19
 *
 * CompareProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>CompareProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.BinaryPredicate;
import COM.dragonflow.Properties.StringProperty;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject

public class CompareProperty implements BinaryPredicate {

    public static boolean DIRECTION_GREATER = true;

    public static boolean DIRECTION_LESS = false;

    StringProperty property;

    String propertyName;

    boolean greater;

    public CompareProperty(StringProperty stringproperty) {
        property = null;
        propertyName = "";
        greater = true;
        property = stringproperty;
    }

    public CompareProperty(StringProperty stringproperty, boolean flag) {
        this(stringproperty);
        greater = flag;
    }

    public CompareProperty(String s) {
        property = null;
        propertyName = "";
        greater = true;
        propertyName = s;
    }

    public CompareProperty(String s, boolean flag) {
        this(s);
        greater = flag;
    }

    public boolean execute(Object obj, Object obj1) {
        String s;
        String s1;
        if (property == null) {
            s = ((SiteViewObject) obj).getProperty(propertyName);
            s1 = ((SiteViewObject) obj1).getProperty(propertyName);
        } else {
            s = ((SiteViewObject) obj).getProperty(property);
            s1 = ((SiteViewObject) obj1).getProperty(property);
        }
        if (greater) {
            return s.compareTo(s1) > 0;
        } else {
            return s.compareTo(s1) < 0;
        }
    }

}
