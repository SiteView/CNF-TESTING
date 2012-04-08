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
// SiteViewError

public abstract class SiteViewException extends java.lang.Exception
{

    String argsForFormattedError[];
    protected final COM.dragonflow.SiteViewException.SiteViewError siteViewError;

    public SiteViewException(long l)
    {
        super(COM.dragonflow.Resource.SiteViewResource.getString((new Long(l)).toString()));
        siteViewError = new SiteViewError(-1, l);
    }

    public SiteViewException(long l, String as[])
    {
        super(COM.dragonflow.Resource.SiteViewResource.getFormattedString((new Long(l)).toString(), as));
        siteViewError = new SiteViewError(-1, l, as);
    }

    public SiteViewException(COM.dragonflow.SiteViewException.SiteViewError siteviewerror)
    {
        super(COM.dragonflow.Resource.SiteViewResource.getFormattedString((new Long(siteviewerror.getErrorCode())).toString(), siteviewerror.getArgsForFormattedError()));
        siteViewError = siteviewerror;
    }

    public COM.dragonflow.SiteViewException.SiteViewError getSiteViewError()
    {
        return siteViewError;
    }

    public long getErrorCode()
    {
        return siteViewError.getErrorCode();
    }

    public String[] getFormattedErrorArgs()
    {
        return siteViewError.getArgsForFormattedError();
    }
}
