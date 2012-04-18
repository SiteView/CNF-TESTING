/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Resource;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Resource:
// Resource

public class SiteViewResource extends com.dragonflow.Resource.Resource
{

    public static final String RESOURCE_NAME = "siteview";

    public SiteViewResource()
    {
    }

    public static String getString(String s)
    {
        return com.dragonflow.Resource.Resource.getString("siteview", s);
    }

    public static String getFormattedString(String s, java.lang.Object aobj[])
    {
        return com.dragonflow.Resource.Resource.getFormattedString("siteview", s, aobj);
    }
}
