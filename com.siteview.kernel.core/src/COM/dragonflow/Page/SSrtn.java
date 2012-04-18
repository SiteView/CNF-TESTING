/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import com.recursionsw.jgl.HashMap;

class SSrtn {

    int m_status;

    String m_message;

    HashMap m_errorlist;

    public static int ERR_Failure;

    public static int ERR_Okay;

    public static int ERR_Exception;

    public static int ERR_InvalidOpr;

    public static int ERR_MissingParam;

    public static int ERR_InvalidParam;

    public static int ERR_NoTemplatesFound;

    public static int ERR_NoTemplateFile;

    public static int ERR_MonNotAvailable;

    public static int ERR_MonTooManyClass;

    public static int ERR_MonTooManyLicense;

    public static int ERR_MonTooManyAims;

    public static int ERR_MonTooManyFAndI;

    public static int ERR_MonTooManyGroup;

    public static int ERR_badTemplateData;

    static int SS_LAST;

    static String m_errortbl[];

    SSrtn(int i) {
        m_status = i;
        m_message = null;
        m_errorlist = null;
    }

    SSrtn() {
        m_status = ERR_Okay;
        m_message = null;
        m_errorlist = null;
    }

    public boolean isOkay() {
        return m_status == 1;
    }

    public boolean isError() {
        return m_status != 1;
    }

    public int isStatus() {
        return m_status;
    }

    public String errormsg() {
        if (m_status >= 0 && m_status < SS_LAST) {
            return m_errortbl[m_status];
        } else {
            return "No error message found, error code out of range";
        }
    }

    public void setError(int i, String s) {
        m_status = i;
        m_message = s;
    }

    public void setMessage(String s) {
        m_message = s;
    }

    public String message() {
        if (m_message == null) {
            return "";
        } else {
            return m_message;
        }
    }

    public void setErrorList(HashMap hashmap) {
        m_errorlist = hashmap;
    }

    public boolean isErrorList() {
        return m_errorlist != null && m_errorlist.size() > 0;
    }

    public HashMap getErrorList() {
        return m_errorlist;
    }

    static {
        ERR_Failure = 0;
        ERR_Okay = 1;
        ERR_Exception = 2;
        ERR_InvalidOpr = 3;
        ERR_MissingParam = 4;
        ERR_InvalidParam = 5;
        ERR_NoTemplatesFound = 6;
        ERR_NoTemplateFile = 7;
        ERR_MonNotAvailable = 8;
        ERR_MonTooManyClass = 9;
        ERR_MonTooManyLicense = 10;
        ERR_MonTooManyAims = 11;
        ERR_MonTooManyFAndI = 12;
        ERR_MonTooManyGroup = 13;
        ERR_badTemplateData = 14;
        SS_LAST = 15;
        m_errortbl = new String[SS_LAST];
        m_errortbl[ERR_Failure] = "General Failure";
        m_errortbl[ERR_Okay] = "Okay";
        m_errortbl[ERR_Exception] = "Exception Caught";
        m_errortbl[ERR_InvalidOpr] = "Missing or Invalid operation specified";
        m_errortbl[ERR_MissingParam] = "Missing an expected parameter";
        m_errortbl[ERR_InvalidParam] = "Given parameter is invalid";
        m_errortbl[ERR_NoTemplatesFound] = "No Monitor Set templates found";
        m_errortbl[ERR_NoTemplateFile] = "No Monitor Set templates selected";
        m_errortbl[ERR_MonNotAvailable] = "Can't create monitor, monitor not available";
        m_errortbl[ERR_MonTooManyClass] = "Can't create monitor, limit reached for monitors of this type";
        m_errortbl[ERR_MonTooManyLicense] = "Can't create monitor, limit reached for all monitors";
        m_errortbl[ERR_MonTooManyAims] = "Can't create monitor, limit reached for aims monitors";
        m_errortbl[ERR_MonTooManyFAndI] = "Can't create monitor, limit reached for frame and image monitors";
        m_errortbl[ERR_MonTooManyGroup] = "Can't create monitor, limit reached for monitors in this group";
        m_errortbl[ERR_badTemplateData] = "Invalid or missing data found in template file";
    }
}
