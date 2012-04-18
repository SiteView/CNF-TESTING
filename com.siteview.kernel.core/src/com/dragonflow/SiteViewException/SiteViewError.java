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


public class SiteViewError
{

    public static final int SS_ERR_TYPE_UNKNOWN = -1;
    public static final int SS_ERR_TYPE_AVAILABILITY = 1;
    public static final int SS_ERR_TYPE_OPERATIONAL = 2;
    public static final int SS_ERR_TYPE_PARAMETER = 3;
    private long applicationErrorCode;
    private String applicationErrorMessage;
    private long errorCode;
    private String argsForFormattedError[];
    private int type;

    public SiteViewError(int i, long l)
    {
        type = -1;
        type = i;
        errorCode = l;
        argsForFormattedError = new String[0];
        applicationErrorCode = com.dragonflow.Resource.SiteViewErrorCodes.ERR_NOT_AVAILABLE;
        applicationErrorMessage = "";
    }

    public SiteViewError(int i, long l, String as[])
    {
        type = -1;
        type = i;
        errorCode = l;
        argsForFormattedError = as;
        applicationErrorCode = com.dragonflow.Resource.SiteViewErrorCodes.ERR_NOT_AVAILABLE;
        applicationErrorMessage = "";
    }

    public SiteViewError(int i, long l, String as[], long l1, String s)
    {
        type = -1;
        type = i;
        errorCode = l;
        argsForFormattedError = as;
        applicationErrorCode = l1;
        applicationErrorMessage = s;
    }

    public SiteViewError(int i, long l, long l1, String s)
    {
        type = -1;
        type = i;
        errorCode = l;
        applicationErrorCode = l1;
        applicationErrorMessage = s;
    }

    public long getApplicationErrorCode()
    {
        return applicationErrorCode;
    }

    public String getApplicationErrorMessage()
    {
        return applicationErrorMessage;
    }

    public long getErrorCode()
    {
        return errorCode;
    }

    public void setApplicationErrorCode(long l)
    {
        applicationErrorCode = l;
    }

    public void setApplicationErrorMessage(String s)
    {
        applicationErrorMessage = s;
    }

    public boolean isOfType(int i)
    {
        return type == i;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int i)
    {
        type = i;
    }

    public String[] getArgsForFormattedError()
    {
        return argsForFormattedError;
    }

    public void setArgsForFormattedError(String as[])
    {
        argsForFormattedError = as;
    }
}
