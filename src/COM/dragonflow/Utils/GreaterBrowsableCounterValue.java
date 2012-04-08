/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

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
public final class GreaterBrowsableCounterValue implements jgl.BinaryPredicate {

    public GreaterBrowsableCounterValue() {
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        String s = ((COM.dragonflow.Properties.StringProperty) obj).getName();
        if (s.startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
            String s1 = ((COM.dragonflow.Properties.StringProperty) obj1).getName();
            if (s1.startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
                int i = COM.dragonflow.Utils.TextUtils.toInt(s.substring(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE.length()));
                int j = COM.dragonflow.Utils.TextUtils.toInt(s1.substring(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE.length()));
                return j > i;
            }
        }
//		else if ((COM.dragonflow.Properties.StringProperty) obj == COM.dragonflow.Astra.AstraMonitorBase.pTotalTime
//                && ((COM.dragonflow.Properties.StringProperty) obj1).getName().startsWith(COM.dragonflow.SiteView.BrowsableBase.PROPERTY_NAME_COUNTER_VALUE)) {
//            return true;
//        }
        return false;
    }
}
