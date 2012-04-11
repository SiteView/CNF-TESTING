/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * IErrorCodeProvider.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>IErrorCodeProvider</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import COM.dragonflow.SiteViewException.SiteViewError;

public interface IErrorCodeProvider {

    public abstract boolean collectionErrorOccurred();

    public abstract SiteViewError getCollectionError();

    public abstract long[] getSupportedErrorConditions();
}
