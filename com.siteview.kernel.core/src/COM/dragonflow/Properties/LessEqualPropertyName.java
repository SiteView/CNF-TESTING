/*
 * 
 * Created on 2005-2-28 7:02:15
 *
 * LessEqualPropertyName.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>LessEqualPropertyName</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.BinaryPredicate;

// Referenced classes of package COM.dragonflow.Properties:
// StringProperty

public class LessEqualPropertyName implements BinaryPredicate {

    public LessEqualPropertyName() {
    }

    public boolean execute(Object obj, Object obj1) {
        String s;
        if (obj instanceof StringProperty) {
            s = ((StringProperty) obj).getName();
        } else {
            s = (String) obj;
        }
        String s1;
        if (obj1 instanceof StringProperty) {
            s1 = ((StringProperty) obj1).getName();
        } else {
            s1 = (String) obj1;
        }
        return s1.toLowerCase().compareTo(s.toLowerCase()) > 0;
    }
}