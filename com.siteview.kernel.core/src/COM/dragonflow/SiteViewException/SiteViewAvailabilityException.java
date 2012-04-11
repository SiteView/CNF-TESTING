/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.SiteViewException;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.SiteViewException:
// SiteViewException, SiteViewError

public class SiteViewAvailabilityException extends COM.dragonflow.SiteViewException.SiteViewException
{

    static final boolean $assertionsDisabled; /* synthetic field */

    public SiteViewAvailabilityException(long l)
    {
        super(l);
        siteViewError.setType(1);
    }

    public SiteViewAvailabilityException(long l, String as[])
    {
        super(l, as);
        siteViewError.setType(1);
    }

    public SiteViewAvailabilityException(long l, String as[], long l1, String s)
    {
        super(l, as);
        siteViewError.setType(1);
        siteViewError.setApplicationErrorCode(l1);
        siteViewError.setApplicationErrorMessage(s);
    }

    public SiteViewAvailabilityException(long l, long l1, String s)
    {
        super(l);
        siteViewError.setType(1);
        siteViewError.setApplicationErrorCode(l1);
        siteViewError.setApplicationErrorMessage(s);
    }

    public SiteViewAvailabilityException(COM.dragonflow.SiteViewException.SiteViewError siteviewerror)
    {
        super(siteviewerror);
        if(!$assertionsDisabled && siteviewerror.getType() != 1)
        {
            throw new AssertionError();
        } else
        {
            return;
        }
    }

    public long getApplicationErrorCode()
    {
        return siteViewError.getApplicationErrorCode();
    }

    public String getApplicationErrorMessage()
    {
        return siteViewError.getApplicationErrorMessage();
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.SiteViewException.SiteViewAvailabilityException.class).desiredAssertionStatus();
    }
}
