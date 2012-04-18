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

public final class GreaterFileModified implements BinaryPredicate {

    private boolean fromOldToNew;

    public GreaterFileModified(boolean flag) {
        fromOldToNew = true;
        fromOldToNew = flag;
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        if (fromOldToNew) {
            return ((java.io.File) obj).lastModified() <= ((java.io.File) obj1).lastModified();
        } else {
            return ((java.io.File) obj).lastModified() > ((java.io.File) obj1).lastModified();
        }
    }
}
