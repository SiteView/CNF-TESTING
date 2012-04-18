/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

import com.recursionsw.jgl.BinaryPredicate;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils
public final class GreaterBrowsableCounter implements BinaryPredicate {

    public GreaterBrowsableCounter() {
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        String s = ((COM.dragonflow.Properties.StringProperty) obj).getName();
        if (s.startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID)) {
            String s1 = ((COM.dragonflow.Properties.StringProperty) obj1).getName();
            if (s1.startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID)) {
                int i = COM.dragonflow.Utils.TextUtils.toInt(s.substring(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID.length()));
                int j = COM.dragonflow.Utils.TextUtils.toInt(s1.substring(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID.length()));
                return j > i;
            }
        }
        return false;
    }
}
