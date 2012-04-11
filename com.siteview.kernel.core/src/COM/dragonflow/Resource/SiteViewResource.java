/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Resource;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Resource:
// Resource

public class SiteViewResource extends COM.dragonflow.Resource.Resource
{

    public static final String RESOURCE_NAME = "siteview";

    public SiteViewResource()
    {
    }

    public static String getString(String s)
    {
        return COM.dragonflow.Resource.Resource.getString("siteview", s);
    }

    public static String getFormattedString(String s, java.lang.Object aobj[])
    {
        return COM.dragonflow.Resource.Resource.getFormattedString("siteview", s, aobj);
    }
}
