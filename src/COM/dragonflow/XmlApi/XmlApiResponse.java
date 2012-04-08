/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.XmlApi;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */


public class XmlApiResponse extends jgl.HashMap
{

    public static final int OPERATIONAL_ERROR = 0;
    public static final int AVAILABILITY_ERROR = 1;
    public static final int PARAMETER_ERROR = 2;
    public static final String OPERATIONAL_STR = "operational";
    public static final String AVAILABILITY_STR = "availability";
    public static final String PARAMETER_STR = "parameter";
    public static final String NEW_RESPONSE_KEY = "newresponse";
    public static final String ERROR_CODE_KEY = "errorcode";
    public static final String VECTOR_KEY = "vector";
    public static final String ERROR_PARAMETERS_KEY = "errorparameters";
    public static final String ERROR_ARGS_KEY = "errorargs";
    public static final String ERROR_KEY = "error";
    public static final String ERROR_TYPE_KEY = "errortype";
    public static final String ERROR_APP_CODE_KEY = "applicationerrorcode";
    public static final String ERROR_APP_MSG_KEY = "applicationerrormessage";

    public XmlApiResponse()
    {
    }

    public void setError(String s)
    {
        if(s != null)
        {
            put("error", s);
        }
    }

    public String getError()
    {
        return (String)get("error");
    }

    public void setErrorCode(String s)
    {
        if(s != null)
        {
            put("errorcode", s);
        }
    }

    public String getErrorCode()
    {
        return (String)get("errorcode");
    }

    public void setReturnVector(java.util.Vector vector)
    {
        if(vector != null)
        {
            put("vector", vector);
        }
    }

    public void setParameterErrors(java.util.Map map)
    {
        if(map != null)
        {
            put("errorparameters", map);
        }
    }

    public java.util.Map getParameterErrors()
    {
        return (java.util.Map)get("errorparameters");
    }

    public void setErrorArguments(String as[])
    {
        if(as != null)
        {
            put("errorargs", as);
        }
    }

    public String[] getErrorArguments()
    {
        return (String[])get("errorargs");
    }

    public void setApplicationErrorCode(String s)
    {
        if(s != null)
        {
            put("applicationerrorcode", s);
        }
    }

    public String getApplicationErrorCode()
    {
        return (String)get("applicationerrorcode");
    }

    public void setApplicationErrorMessage(String s)
    {
        if(s != null)
        {
            put("applicationerrormessage", s);
        }
    }

    public String getApplicationErrorMessage()
    {
        return (String)get("applicationerrormessage");
    }

    public void setErrorType(int i)
    {
        if(i == 0)
        {
            put("errortype", "operational");
        } else
        if(i == 1)
        {
            put("errortype", "availability");
        } else
        if(i == 2)
        {
            put("errortype", "parameter");
        }
    }

    public String getErrorType()
    {
        return (String)get("errortype");
    }

    public boolean hasErrors()
    {
        boolean flag = false;
        String s = (String)get("errortype");
        if(s != null && s.length() > 0)
        {
            flag = true;
        }
        return flag;
    }

    public boolean isParameterError()
    {
        boolean flag = false;
        String s = (String)get("errortype");
        if(s != null && s.length() > 0 && s.equals("parameter"))
        {
            flag = true;
        }
        return flag;
    }

    public boolean isOperationalError()
    {
        boolean flag = false;
        String s = (String)get("errortype");
        if(s != null && s.length() > 0 && s.equals("operational"))
        {
            flag = true;
        }
        return flag;
    }

    public boolean isAvailabilityError()
    {
        boolean flag = false;
        String s = (String)get("errortype");
        if(s != null && s.length() > 0 && s.equals("availability"))
        {
            flag = true;
        }
        return flag;
    }

    public java.util.Vector getReturnVector()
    {
        return (java.util.Vector)get("vector");
    }

    public void setErrorResponse(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
    {
        setError(siteviewexception.getMessage());
        setErrorCode((new Long(siteviewexception.getErrorCode())).toString());
        setErrorArguments(siteviewexception.getFormattedErrorArgs());
        if(siteviewexception instanceof COM.dragonflow.SiteViewException.SiteViewParameterException)
        {
            setErrorType(2);
            setParameterErrors(((COM.dragonflow.SiteViewException.SiteViewParameterException)siteviewexception).getErrorParameterMap());
        } else
        if(siteviewexception instanceof COM.dragonflow.SiteViewException.SiteViewOperationalException)
        {
            setErrorType(0);
            setApplicationErrorCode((new Long(((COM.dragonflow.SiteViewException.SiteViewOperationalException)siteviewexception).getApplicationErrorCode())).toString());
            setApplicationErrorMessage(((COM.dragonflow.SiteViewException.SiteViewOperationalException)siteviewexception).getApplicationErrorMessage());
        } else
        if(siteviewexception instanceof COM.dragonflow.SiteViewException.SiteViewAvailabilityException)
        {
            setErrorType(1);
            setApplicationErrorCode((new Long(((COM.dragonflow.SiteViewException.SiteViewAvailabilityException)siteviewexception).getApplicationErrorCode())).toString());
            setApplicationErrorMessage(((COM.dragonflow.SiteViewException.SiteViewAvailabilityException)siteviewexception).getApplicationErrorMessage());
        }
    }
}
