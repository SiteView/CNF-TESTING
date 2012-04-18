/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

import com.recursionsw.jgl.BinaryPredicate;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Utils:
// TextUtils
public final class GreaterBrowsableCounter implements BinaryPredicate {

    public GreaterBrowsableCounter() {
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        String s = ((com.dragonflow.Properties.StringProperty) obj).getName();
        if (s.startsWith(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID)) {
            String s1 = ((com.dragonflow.Properties.StringProperty) obj1).getName();
            if (s1.startsWith(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID)) {
                int i = com.dragonflow.Utils.TextUtils.toInt(s.substring(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID.length()));
                int j = com.dragonflow.Utils.TextUtils.toInt(s1.substring(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_ID.length()));
                return j > i;
            }
        }
        return false;
    }
}
