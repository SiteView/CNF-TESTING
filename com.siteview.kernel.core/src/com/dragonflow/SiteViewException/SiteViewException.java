/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.SiteViewException;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */


// Referenced classes of package com.dragonflow.SiteViewException:
// SiteViewError

public abstract class SiteViewException extends java.lang.Exception
{

    String argsForFormattedError[];
    protected final com.dragonflow.SiteViewException.SiteViewError siteViewError;

    public SiteViewException(long l)
    {
        super(com.dragonflow.Resource.SiteViewResource.getString((new Long(l)).toString()));
        siteViewError = new SiteViewError(-1, l);
    }

    public SiteViewException(long l, String as[])
    {
        super(com.dragonflow.Resource.SiteViewResource.getFormattedString((new Long(l)).toString(), as));
        siteViewError = new SiteViewError(-1, l, as);
    }

    public SiteViewException(com.dragonflow.SiteViewException.SiteViewError siteviewerror)
    {
        super(com.dragonflow.Resource.SiteViewResource.getFormattedString((new Long(siteviewerror.getErrorCode())).toString(), siteviewerror.getArgsForFormattedError()));
        siteViewError = siteviewerror;
    }

    public com.dragonflow.SiteViewException.SiteViewError getSiteViewError()
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
