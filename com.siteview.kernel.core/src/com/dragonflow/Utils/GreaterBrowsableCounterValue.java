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
public final class GreaterBrowsableCounterValue implements BinaryPredicate {

    public GreaterBrowsableCounterValue() {
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        String s = ((com.dragonflow.Properties.StringProperty) obj).getName();
        if (s.startsWith(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
            String s1 = ((com.dragonflow.Properties.StringProperty) obj1).getName();
            if (s1.startsWith(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
                int i = com.dragonflow.Utils.TextUtils.toInt(s.substring(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE.length()));
                int j = com.dragonflow.Utils.TextUtils.toInt(s1.substring(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE.length()));
                return j > i;
            }
        }
//		else if ((com.dragonflow.Properties.StringProperty) obj == com.dragonflow.Astra.AstraMonitorBase.pTotalTime
//                && ((com.dragonflow.Properties.StringProperty) obj1).getName().startsWith(com.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
//            return true;
//        }
        return false;
    }
}
