/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * IObjectWithUniqueId.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>IObjectWithUniqueId</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject
public interface IObjectWithUniqueId {

    public abstract void setUniqueInternalId(int i);

    public abstract int getUniqueInternalId();

    public abstract SiteViewObject getParentGroup();

    public abstract String getName();
}
