/*
 * 
 * Created on 2005-2-16 15:11:06
 *
 * FindGroupWithFileVisitor.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>FindGroupWithFileVisitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;

import com.dragonflow.Properties.FindObjectsOfClassVisitor;
import com.dragonflow.Properties.PropertiedObject;

// Referenced classes of package com.dragonflow.SiteView:
// MonitorGroup

public class FindGroupWithFileVisitor extends FindObjectsOfClassVisitor {

    private File groupFile;

    public FindGroupWithFileVisitor(File file) {
        super("com.dragonflow.SiteView.MonitorGroup");
        setStopAfterFirst(true);
        groupFile = file;
    }

    protected boolean test(PropertiedObject propertiedobject) {
        if (super.test(propertiedobject)) {
            MonitorGroup monitorgroup = (MonitorGroup) propertiedobject;
            return groupFile.equals(monitorgroup.getFile());
        } else {
            return false;
        }
    }
}
