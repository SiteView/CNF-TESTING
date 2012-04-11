/*
 * 
 * Created on 2005-2-15 12:31:26
 *
 * CompareSlot.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>CompareSlot</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.BinaryPredicate;
import jgl.HashMap;
import COM.dragonflow.Utils.TextUtils;

public class CompareSlot implements BinaryPredicate {

    public static boolean DIRECTION_GREATER = true;

    public static boolean DIRECTION_LESS = false;

    public static boolean STRING_COMPARE = false;

    public static boolean NUMERIC_COMPARE = true;

    String slotName;

    boolean greater;

    boolean numericCompare;

    public CompareSlot(String s) {
        slotName = null;
        greater = true;
        numericCompare = false;
        slotName = s;
    }

    public CompareSlot(String s, boolean flag) {
        this(s);
        greater = flag;
    }

    public CompareSlot(String s, boolean flag, boolean flag1) {
        this(s, flag);
        numericCompare = flag1;
    }

    public boolean execute(Object obj, Object obj1) {
        String s = TextUtils.getValue((HashMap) obj, slotName);
        String s1 = TextUtils.getValue((HashMap) obj1, slotName);
        if (numericCompare) {
            if (greater) {
                return TextUtils.toFloat(s) > TextUtils.toFloat(s1);
            } else {
                return TextUtils.toFloat(s) < TextUtils.toFloat(s1);
            }
        }
        if (greater) {
            return s.compareTo(s1) > 0;
        } else {
            return s.compareTo(s1) < 0;
        }
    }

}
