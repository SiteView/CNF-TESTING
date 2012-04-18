/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * URLSequenceMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>URLSequenceMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTMLTagParser;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.TraceDebug;
import COM.dragonflow.Utils.URLInfo;
import COM.dragonflow.Utils.URLResults;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLLoader, URLMonitor

public class URLSequenceMonitor extends AtomicMonitor
{

    public static final int DEFAULT_MILLISECOND_PRECISION = 2;
    static final int STEP_PROPERTIES = 12;
    static StringProperty pReferenceType[];
    public static StringProperty pReference[];
    static StringProperty pContentMatch[];
    static StringProperty pErrorContent[];
    static StringProperty pPostData[];
    static StringProperty pUserName[];
    static StringProperty pPassword[];
    static StringProperty pDomain[];
    static StringProperty pWhenToAuthenticate[];
    static StringProperty pStepName[];
    static StringProperty pStepDelay[];
    static StringProperty pEncoding[];
    static StringProperty pEncodePostData[];
    static StringProperty pMeasureDetails;
    static StringProperty pProxy;
    static StringProperty pFrames;
    static StringProperty pImages;
    static StringProperty pTimeout;
    static StringProperty pTimeoutPerStep;
    static StringProperty pProxyUserName;
    static StringProperty pProxyPassword;
    static StringProperty pResumeStep;
    static BooleanProperty pResumeRemainingSteps;
    static StringProperty pDaysUntilCertExpiration;
    static StringProperty pHTTPVersion10;
    static StringProperty pRetries;
    static StringProperty pAcceptAllUntrustedCerts;
    static StringProperty pAcceptInvalidCerts;
    static StringProperty pRoundTripTime;
    public static StringProperty pStepRoundTripTime[];
    public static StringProperty pStepDNSTime[];
    public static StringProperty pStepResponseTime[];
    public static StringProperty pStepDownloadTime[];
    public static StringProperty pStepConnectTime[];
    static StringProperty pStatus;
    static StringProperty pStatusText;
    static StringProperty pURL;
    static StringProperty pURLHeader;
    static StringProperty pMatchValue;
    static StringProperty pTotalErrors;
    static StringProperty pErrorStepName;
    static int millisecondPrecision;
    public static int numberOfSteps;
    public static int minimumNumberOfSteps;
    public static long contentMax = 0x7d000L;
    public static final int ELEMENTS_FOR_TOTAL = 7;
    public static final int URL_RESULT_INDEX = 0;
    public static final int TOTAL_DURATION_INDEX = 1;
    public static final int TOTAL_BYTES_INDEX = 2;
    public static final int TOTAL_FRAMES_INDEX = 3;
    public static final int TOTAL_FRAME_ERRORS_INDEX = 4;
    public static final int TOTAL_IMAGES_INDEX = 5;
    public static final int TOTAL_IMAGE_ERRORS_INDEX = 6;
    public static final int ELEMENTS_PER_STEP = 6;
    public static final int DURATION_STEP_OFFSET = 0;
    public static final int DNSTIME_STEP_OFFSET = 1;
    public static final int CONNECT_TIME_STEP_OFFSET = 2;
    public static final int RESPONSE_TIME_STEP_OFFSET = 3;
    public static final int DOWNLOAD_TIME_STEP_OFFSET = 4;
    public static final int HTML_TRUNCATED_IF_NONZERO_STEP_OFFSET = 5;
    public static String refererStartToken = "Referer: ";
    public static String refererEndToken = "endReferer";
    private static TraceDebug traceit = new TraceDebug();
    public String stepURLs[];
    public String stepStatus[];
    static final String TARGET_TAGS[] = {
        "/A", "INPUT", "/FORM", "/OPTION", "/SELECT", "/TEXTAREA", "FRAME", "BASE", "AREA", "META", 
        "IFRAME"
    };
    static final String JAVASCRIPT = "[javascript]";
    static String replaceToken = "Replace: ";

    public URLSequenceMonitor()
    {
    }

    public static void allocateStepProperties(int i)
    {
        int j = 0;
        if(pReferenceType != null)
        {
            j = numberOfSteps;
        }
        StringProperty astringproperty[] = new StringProperty[i];
        if(pReferenceType != null)
        {
            System.arraycopy(pReferenceType, 0, astringproperty, 0, pReferenceType.length);
        }
        StringProperty astringproperty1[] = new StringProperty[i];
        if(pReference != null)
        {
            System.arraycopy(pReference, 0, astringproperty1, 0, pReference.length);
        }
        StringProperty astringproperty2[] = new StringProperty[i];
        if(pEncoding != null)
        {
            System.arraycopy(pEncoding, 0, astringproperty2, 0, pEncoding.length);
        }
        StringProperty astringproperty3[] = new StringProperty[i];
        if(pEncodePostData != null)
        {
            System.arraycopy(pEncodePostData, 0, astringproperty3, 0, pEncodePostData.length);
        }
        StringProperty astringproperty4[] = new StringProperty[i];
        if(pPostData != null)
        {
            System.arraycopy(pPostData, 0, astringproperty4, 0, pPostData.length);
        }
        StringProperty astringproperty5[] = new StringProperty[i];
        if(pContentMatch != null)
        {
            System.arraycopy(pContentMatch, 0, astringproperty5, 0, pContentMatch.length);
        }
        StringProperty astringproperty6[] = new StringProperty[i];
        if(pErrorContent != null)
        {
            System.arraycopy(pErrorContent, 0, astringproperty6, 0, pErrorContent.length);
        }
        StringProperty astringproperty7[] = new StringProperty[i];
        if(pUserName != null)
        {
            System.arraycopy(pUserName, 0, astringproperty7, 0, pUserName.length);
        }
        StringProperty astringproperty8[] = new StringProperty[i];
        if(pPassword != null)
        {
            System.arraycopy(pPassword, 0, astringproperty8, 0, pPassword.length);
        }
        StringProperty astringproperty9[] = new StringProperty[i];
        if(pDomain != null)
        {
            System.arraycopy(pDomain, 0, astringproperty9, 0, pDomain.length);
        }
        StringProperty astringproperty10[] = new StringProperty[i];
        if(pWhenToAuthenticate != null)
        {
            System.arraycopy(pWhenToAuthenticate, 0, astringproperty10, 0, pWhenToAuthenticate.length);
        }
        StringProperty astringproperty11[] = new StringProperty[i];
        if(pStepDelay != null)
        {
            System.arraycopy(pStepDelay, 0, astringproperty11, 0, pStepDelay.length);
        }
        StringProperty astringproperty12[] = new StringProperty[i];
        if(pStepName != null)
        {
            System.arraycopy(pStepName, 0, astringproperty12, 0, pStepName.length);
        }
        StringProperty astringproperty13[] = new StringProperty[i];
        if(pStepRoundTripTime != null)
        {
            System.arraycopy(pStepRoundTripTime, 0, astringproperty13, 0, pStepRoundTripTime.length);
        }
        StringProperty astringproperty14[] = new StringProperty[i];
        if(pStepDNSTime != null)
        {
            System.arraycopy(pStepDNSTime, 0, astringproperty14, 0, pStepDNSTime.length);
        }
        StringProperty astringproperty15[] = new StringProperty[i];
        if(pStepConnectTime != null)
        {
            System.arraycopy(pStepConnectTime, 0, astringproperty15, 0, pStepConnectTime.length);
        }
        StringProperty astringproperty16[] = new StringProperty[i];
        if(pStepResponseTime != null)
        {
            System.arraycopy(pStepResponseTime, 0, astringproperty16, 0, pStepResponseTime.length);
        }
        StringProperty astringproperty17[] = new StringProperty[i];
        if(pStepDownloadTime != null)
        {
            System.arraycopy(pStepDownloadTime, 0, astringproperty17, 0, pStepDownloadTime.length);
        }
        int k = 0;
        for(int l = j; l < i; l++)
        {
            int i1 = l + 1;
            int j1 = 1;
            ScalarProperty scalarproperty = new ScalarProperty("_referenceType" + i1);
            scalarproperty.setDisplayText("Step " + i1 + " Type", "the type of item being referred to in the reference (the next field) - this is used to determine which parts of the HTML will be scanned for text matches.");
            scalarproperty.setParameterOptions(true, l * 12 + j1++, false);
            scalarproperty.isVariablePropertyCountDependent = true;
            astringproperty[l] = scalarproperty;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty[l]);
            }
            StringProperty stringproperty = new StringProperty("_reference" + i1);
            stringproperty.setDisplayText("Step " + i1 + " Reference", "the URL, link, or submit button to be followed at this step (examples: a URL of http://demo." + Platform.exampleDomain + ", a link of Next, or a button name of Submit)");
            stringproperty.setParameterOptions(true, l * 12 + j1++, false);
            stringproperty.isVariablePropertyCountKey = true;
            stringproperty.minVariablePropertyCount = minimumNumberOfSteps;
            stringproperty.maxVariablePropertyCount = numberOfSteps;
            astringproperty1[l] = stringproperty;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty1[l]);
            }
            StringProperty stringproperty1 = new StringProperty("_encoding" + i1);
            stringproperty1.setDisplayText("Step " + i1 + " Encoding Character Set:", "Enter code page (ie Cp1252 or Shift_JIS or EUC-JP)");
            stringproperty1.setParameterOptions(true, l * 12 + j1++, false);
            stringproperty1.isVariablePropertyCountDependent = true;
            astringproperty2[l] = stringproperty1;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty2[l]);
            }
            StringProperty stringproperty2 = new StringProperty("_postData" + i1, "", "POST Variables");
            stringproperty2.setDisplayText("Step " + i1 + " POST Data", "optional, enter name=value variables, one per line, to send with a POST request for this step");
            stringproperty2.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty2.isMultiLine = true;
            stringproperty2.isVariablePropertyCountDependent = true;
            astringproperty4[l] = stringproperty2;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty4[l]);
            }
            StringProperty stringproperty3 = new StringProperty("_content" + i1);
            stringproperty3.setDisplayText("Step " + i1 + " Match Content", "optional, match against content for this step, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
            stringproperty3.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty3.isVariablePropertyCountDependent = true;
            astringproperty5[l] = stringproperty3;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty5[l]);
            }
            StringProperty stringproperty4 = new StringProperty("_errorContent" + i1);
            stringproperty4.setDisplayText("Step " + i1 + " Error If Match", "optional, generate an error if the content of the URL for this step contains this text");
            stringproperty4.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty4.isVariablePropertyCountDependent = true;
            astringproperty6[l] = stringproperty4;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty6[l]);
            }
            StringProperty stringproperty5 = new StringProperty("_username" + i1);
            stringproperty5.setDisplayText("Step " + i1 + " User Name", "optional, user name if the URL for this step requires authorization              See the <a href=\"/SiteView/docs/URLMon.htm#authorization\" TARGET=Help>               documentation</a> for more information.                           <p></p>");
            stringproperty5.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty5.isVariablePropertyCountDependent = true;
            astringproperty7[l] = stringproperty5;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty7[l]);
            }
            StringProperty stringproperty6 = new StringProperty("_password" + i1);
            stringproperty6.setDisplayText("Step " + i1 + " Password", "optional, password if the URL for this step requires authorization");
            stringproperty6.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty6.isPassword = true;
            stringproperty6.isVariablePropertyCountDependent = true;
            astringproperty8[l] = stringproperty6;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty8[l]);
            }
            StringProperty stringproperty7 = new StringProperty("_domain" + i1);
            stringproperty7.setDisplayText("Step " + i1 + " Authorization NTLM Domain", "optional domain if the URL requires for NTLM authorization");
            stringproperty7.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty7.isVariablePropertyCountDependent = true;
            astringproperty9[l] = stringproperty7;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty9[l]);
            }
            ScalarProperty scalarproperty1 = new ScalarProperty("_whenToAuthenticate" + i1);
            scalarproperty1.setDisplayText("Step " + i1 + "Preemptive Authorization", "              See the <a href=\"/SiteView/docs/URLMon.htm#authorization\" TARGET=Help>               documentation</a> for more information.                           <p></p>");
            scalarproperty1.setParameterOptions(true, l * 12 + j1++, true);
            scalarproperty1.isVariablePropertyCountDependent = true;
            astringproperty10[l] = scalarproperty1;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty10[l]);
            }
            StringProperty stringproperty8 = new StringProperty("_stepDelay" + i1);
            stringproperty8.setDisplayText("Step " + i1 + " Delay", "optional, number of seconds to wait before performing the next step");
            stringproperty8.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty8.isVariablePropertyCountDependent = true;
            astringproperty11[l] = stringproperty8;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty11[l]);
            }
            StringProperty stringproperty9 = new StringProperty("_stepName" + i1);
            stringproperty9.setDisplayText("Step " + i1 + " Title", "optional, title for this step used in alerts and reports");
            stringproperty9.setParameterOptions(true, l * 12 + j1++, true);
            stringproperty9.isVariablePropertyCountDependent = true;
            astringproperty12[l] = stringproperty9;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty12[l]);
            }
            ScalarProperty scalarproperty2 = new ScalarProperty("_URLDropDownEncodePostData" + i1);
            scalarproperty2.setDisplayText("Step " + i1 + " When to Encode Post Data", "By default if Content-Type: urlencoded found, then encode, otherwise force according to the selected option.");
            scalarproperty2.setParameterOptions(true, l * 12 + j1++, true);
            scalarproperty2.isVariablePropertyCountDependent = true;
            astringproperty3[l] = scalarproperty2;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty3[l]);
            }
            NumericProperty numericproperty = new NumericProperty("stepRoundTripTime" + i1, "", "milliseconds");
            numericproperty.setLabel("step " + i1 + " round trip time");
            numericproperty.setStateOptions(k++);
            numericproperty.similarProperty = pRoundTripTime;
            astringproperty13[l] = numericproperty;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty13[l]);
            }
            NumericProperty numericproperty1 = new NumericProperty("stepDnsTime" + i1, "", "milliseconds");
            numericproperty1.setLabel("step " + i1 + " dns time");
            numericproperty1.setStateOptions(k++);
            numericproperty1.similarProperty = pRoundTripTime;
            astringproperty14[l] = numericproperty1;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty14[l]);
            }
            NumericProperty numericproperty2 = new NumericProperty("stepConnectTime" + i1, "", "milliseconds");
            numericproperty2.setLabel("step " + i1 + " connect time");
            numericproperty2.setStateOptions(k++);
            numericproperty2.similarProperty = pRoundTripTime;
            astringproperty15[l] = numericproperty2;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty15[l]);
            }
            NumericProperty numericproperty3 = new NumericProperty("stepResponseTime" + i1, "", "milliseconds");
            numericproperty3.setLabel("step " + i1 + " response time");
            numericproperty3.setStateOptions(k++);
            numericproperty3.similarProperty = pRoundTripTime;
            astringproperty16[l] = numericproperty3;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty16[l]);
            }
            NumericProperty numericproperty4 = new NumericProperty("stepDownloadTime" + i1, "", "milliseconds");
            numericproperty4.setLabel("step " + i1 + " download time");
            numericproperty4.setStateOptions(k++);
            numericproperty4.similarProperty = pRoundTripTime;
            astringproperty17[l] = numericproperty4;
            if(j >= numberOfSteps)
            {
                PropertiedObject.addPropertyToPropertyMap((COM.dragonflow.StandardMonitor.URLSequenceMonitor.class).getName(), astringproperty17[l]);
            }
        }

        numberOfSteps = i;
        pReferenceType = astringproperty;
        pReference = astringproperty1;
        pContentMatch = astringproperty5;
        pErrorContent = astringproperty6;
        pPostData = astringproperty4;
        pUserName = astringproperty7;
        pPassword = astringproperty8;
        pDomain = astringproperty9;
        pWhenToAuthenticate = astringproperty10;
        pStepDelay = astringproperty11;
        pStepName = astringproperty12;
        pEncoding = astringproperty2;
        pEncodePostData = astringproperty3;
        pStepRoundTripTime = astringproperty13;
        pStepDNSTime = astringproperty14;
        pStepConnectTime = astringproperty15;
        pStepResponseTime = astringproperty16;
        pStepDownloadTime = astringproperty17;
    }

    protected boolean update()
    {
        update1("", "");
        return true;
    }

    protected long[] update1(String s, String s1)
    {
        stepURLs = new String[numberOfSteps];
        stepStatus = new String[numberOfSteps];
        String s2 = getProperty(pProxy);
        String s3 = getProperty(pProxyPassword);
        String s4 = getProperty(pProxyUserName);
        boolean flag = getPropertyAsBoolean(pTimeoutPerStep);
        long l = Platform.timeMillis();
        int i = getNumberOfSteps();
        String as[] = new String[i + 1];
        String as1[] = new String[i + 1];
        String as2[] = new String[i + 1];
        String as3[] = new String[i + 1];
        Enumeration aenumeration[] = new Enumeration[i + 1];
        String as4[] = new String[i + 1];
        String as5[] = new String[i + 1];
        String as6[] = new String[i + 1];
        String as7[] = new String[i + 1];
        String as8[] = new String[i + 1];
        String as9[] = new String[i + 1];
        String as10[] = new String[i + 1];
        String as11[] = new String[i + 1];
        as1[0] = null;
        as[0] = null;
        as2[0] = null;
        as3[0] = null;
        aenumeration[0] = null;
        as4[0] = null;
        as5[0] = null;
        as6[0] = null;
        as7[0] = null;
        as8[0] = null;
        as9[0] = null;
        as10[0] = I18N.getDefaultEncoding();
        as11[0] = null;
        for(int j = 1; j <= i; j++)
        {
            as[j] = getProperty(pReferenceType[j - 1]);
            as1[j] = getProperty(pReference[j - 1]);
            as10[j] = getProperty(pEncoding[j - 1]);
            as2[j] = I18N.UnicodeToString(getProperty(pContentMatch[j - 1]), I18N.nullEncoding());
            as3[j] = I18N.UnicodeToString(getProperty(pErrorContent[j - 1]), I18N.nullEncoding());
            aenumeration[j] = getMultipleValues(pPostData[j - 1]);
            if(!aenumeration[j].hasMoreElements())
            {
                aenumeration[j] = null;
            }
            as4[j] = getProperty(pUserName[j - 1]);
            as5[j] = getProperty(pPassword[j - 1]);
            as6[j] = getProperty(pDomain[j - 1]);
            as7[j] = getProperty(pWhenToAuthenticate[j - 1]);
            as8[j] = getProperty(pStepDelay[j - 1]);
            as9[j] = getProperty(pStepName[j - 1]);
            as11[j] = getProperty(pEncodePostData[j - 1]);
        }

        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        StringBuffer stringbuffer3 = new StringBuffer();
        String s5 = "";
        Array array = Platform.split(',', s2);
        setProperty(pErrorStepName, "");
        long al[] = null;
        if(array.size() <= 1)
        {
            al = checkURLSequence(s, as, as1, as10, as2, as3, s2, s4, s3, aenumeration, as4, as5, as6, as7, as8, as9, as11, getProperty(pTimeout), flag, stringbuffer2, this, null, null, stepURLs, stepStatus, stringbuffer, stringbuffer1, stringbuffer3, false);
        } else
        {
            Enumeration enumeration = (Enumeration) array.iterator();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                stringbuffer = new StringBuffer();
                stringbuffer1 = new StringBuffer();
                stringbuffer2 = new StringBuffer();
                s5 = (String)enumeration.nextElement();
                al = checkURLSequence(s, as, as1, as10, as2, as3, s5, s4, s3, aenumeration, as4, as5, as6, as7, as8, as9, as11, getProperty(pTimeout), flag, stringbuffer2, this, null, null, stepURLs, stepStatus, stringbuffer, stringbuffer1, stringbuffer3, false);
            } while(URLMonitor.shouldTryNextProxy(al[0]));
        }
        long l1 = al[0];
        long l2 = al[1];
        if(l2 == 0L)
        {
            l2 = Platform.timeMillis() - l;
        }
        long l3 = al[2];
        long l4 = al[3];
        long l5 = al[4];
        long l6 = al[5];
        long l7 = al[6];
        if(stillActive())
        {
            synchronized(this)
            {
                setProperty(pTotalErrors, l5 + l7);
                setProperty(pStatusText, lookupStatus(l1));
                setProperty(getLocationProperty(pStatus, s1), l1);
                for(int k = 0; k < numberOfSteps; k++)
                {
                    int i1 = k * 6 + 7;
                    if(al[i1] >= 0L)
                    {
                        setProperty(pStepRoundTripTime[k], al[i1]);
                        setProperty(pStepDNSTime[k], al[i1 + 1]);
                        setProperty(pStepConnectTime[k], al[i1 + 2]);
                        setProperty(pStepResponseTime[k], al[i1 + 3]);
                        setProperty(pStepDownloadTime[k], al[i1 + 4]);
                    } else
                    {
                        setProperty(pStepRoundTripTime[k], "n/a");
                        setProperty(pStepDNSTime[k], "n/a");
                        setProperty(pStepConnectTime[k], "n/a");
                        setProperty(pStepResponseTime[k], "n/a");
                        setProperty(pStepDownloadTime[k], "n/a");
                    }
                    if(stepStatus[k] != null)
                    {
                        stringbuffer2.append(stepStatus[k]);
                    }
                }

                setProperty(pURL, stringbuffer.toString());
                if(l1 == 200L)
                {
                    String s6 = TextUtils.floatToString((float)l2 / 1000F, millisecondPrecision) + " sec";
                    String s7 = "";
                    String s8 = "";
                    if(getPropertyAsBoolean(pFrames))
                    {
                        if(l5 > 0L)
                        {
                            s8 = ", " + l5 + " of " + l4 + " frames in error";
                        } else
                        if(l4 == 0L)
                        {
                            s8 = ", no frames";
                        } else
                        if(l4 == 1L)
                        {
                            s8 = ", 1 frame";
                        } else
                        {
                            s8 = ", " + l4 + " frames";
                        }
                    }
                    if(getPropertyAsBoolean(pImages))
                    {
                        if(l7 > 0L)
                        {
                            s7 = ", " + l7 + " of " + l6 + " images in error";
                        } else
                        if(l6 == 0L)
                        {
                            s7 = ", no images";
                        } else
                        if(l6 == 1L)
                        {
                            s7 = ", 1 image";
                        } else
                        {
                            s7 = ", " + l6 + " images";
                        }
                    }
                    String s10 = s6 + ", " + i + " steps, " + TextUtils.bytesToString(l3) + " total" + s8 + s7;
                    if(stringbuffer2.length() != 0)
                    {
                        s10 = s10 + ", " + stringbuffer2;
                    }
                    if(s5.length() != 0)
                    {
                        s10 = s10 + ", using proxy " + s5;
                    }
                    setProperty(pStateString, I18N.StringToUnicode(s10, I18N.nullEncoding()));
                    setProperty(getLocationProperty(pRoundTripTime, s1), String.valueOf(l2));
                    setProperty(getLocationProperty(pStateString, s1), I18N.StringToUnicode(s10, I18N.nullEncoding()));
                    setProperty(pMeasurement, getMeasurement(pRoundTripTime, 4000 * i));
                    setProperty(getLocationProperty(pURLHeader, s1), "");
                } else
                {
                    long l8 = getSettingAsLong("_urlHeaderLinesToSave", 15);
                    setProperty(getLocationProperty(pURLHeader, s1), URLMonitor.parseHeader(stringbuffer1.toString(), l8));
                    String s9 = stringbuffer2.toString();
                    if(s5.length() != 0)
                    {
                        s9 = s9 + ", using proxy " + s5;
                    }
                    setProperty(pStateString, I18N.StringToUnicode(s9, I18N.nullEncoding()));
                    setProperty(pMeasurement, String.valueOf(0));
                    setProperty(getLocationProperty(pRoundTripTime, s1), "n/a");
                    setProperty(pNoData, "n/a");
                    setProperty(getLocationProperty(pStateString, s1), I18N.StringToUnicode(s9, I18N.nullEncoding()));
                }
            }
        }
        long al1[] = new long[8];
        al1[0] = l1;
        al1[1] = l2;
        al1[2] = l3;
        al1[3] = i;
        al1[4] = l6;
        al1[5] = l7;
        al1[6] = l4;
        al1[7] = l5;
        return al1;
    }

    public String getProperty(StringProperty stringproperty)
        throws NullPointerException
    {
        if(stringproperty == pDiagnosticText)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            }
            String s = getProperty(pURL);
            String s3 = Platform.hostFromURL(s);
            if(s3 == null)
            {
                return "URL format error: " + s;
            }
            String s5 = "";
            int i = getPropertyAsInteger(pStatus);
            long l = getSettingAsLong("_urlHeaderLinesToSave", 15);
            if(l != 0L && isContentError(i))
            {
                String s6 = getProperty(pURLHeader);
                s6 = s6.replace('^', '\n');
                s5 = "Response from server:\n" + s6 + "\n";
            }
            s5 = s5 + diagnostic(s3, i);
            return s5;
        }
        if(stringproperty == pDiagnosticTraceRoute)
        {
            if(getProperty(pCategory).equals("good"))
            {
                return "";
            }
            String s1 = getProperty(pURL);
            String s4 = Platform.hostFromURL(s1);
            if(s4 == null)
            {
                return "URL format error: " + s1;
            } else
            {
                return diagnosticTraceRoute(s4);
            }
        } else
        {
            String s2 = super.getProperty(stringproperty);
            return s2;
        }
    }

    /**
     * 
     */
    public String getHostname()
    {
        String s = HTTPUtils.hostFromURL(getProperty(pReference[0]));
        if(getSetting("_urlLookupHost").length() > 0)
        {
            try {
        InetAddress inetaddress = InetAddress.getByName(s);
        String s1 = inetaddress.getHostAddress();
        InetAddress inetaddress1 = InetAddress.getByName(s1);
        return inetaddress1.getHostName();
            }
        catch (Exception e) {
        LogManager.log("Error", "Failed to perform reverse lookup " + e);
        return s;
        }
        }
        return s;
    }

    public String getAddPage()
    {
        return "tranWizard";
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=tranWizard&operation=Tool&class=URLSequenceMonitor";
        s = s + "&group=" + HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID))) + "&id=" + I18N.toDefaultEncoding(getProperty("_id"));
        return s;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        int i = -1;
        int j = -1;
        int k = -1;
        int l = 0;
        do
        {
            if(l >= numberOfSteps)
            {
                break;
            }
            if(stringproperty == pReference[l])
            {
                i = l;
                break;
            }
            if(stringproperty == pContentMatch[l])
            {
                j = l;
                break;
            }
            if(stringproperty == pErrorContent[l])
            {
                k = l;
                break;
            }
            l++;
        } while(true);
        if(i >= 0)
        {
            if(getProperty(pReferenceType[i]).equals("url"))
            {
                if(i == 0)
                {
                    s = URLMonitor.verifyUrlMonitorProperty(stringproperty, s, hashmap);
                }
            } else
            {
                String s1 = TextUtils.legalMatchString(s);
                if(s1.length() > 0)
                {
                    hashmap.put(stringproperty, s1);
                }
            }
        } else
        if(stringproperty == pProxy)
        {
            s = URLMonitor.verifyUrlMonitorProperty(stringproperty, s, hashmap);
        } else
        if(j >= 0 || k >= 0)
        {
            s = URLMonitor.verifyUrlMonitorProperty(stringproperty, s, hashmap);
        } else
        {
            s = super.verify(stringproperty, s, httprequest, hashmap);
        }
        return s;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        int i = -1;
        int j = -1;
        int k = -1;
        int l = 0;
        do
        {
            if(l >= numberOfSteps)
            {
                break;
            }
            if(scalarproperty == pReferenceType[l])
            {
                i = l;
                break;
            }
            if(scalarproperty == pEncodePostData[l])
            {
                j = l;
                break;
            }
            if(scalarproperty == pWhenToAuthenticate[l])
            {
                k = l;
                break;
            }
            l++;
        } while(true);
        if(scalarproperty == pResumeStep)
        {
            Vector vector = new Vector();
            vector.addElement("0");
            vector.addElement("None");
            int i1;
            for(i1 = 1; i1 <= numberOfSteps && getProperty(pReference[i1 - 1]).length() != 0; i1++) { }
            for(int j1 = 1; j1 <= numberOfSteps && (httprequest.getValue("_reference" + j1).length() != 0 || j1 < i1); j1++)
            {
                String s = getProperty(pReferenceType[j1 - 1]);
                if(s.length() == 0)
                {
                    s = httprequest.getValue("_referenceType" + j1);
                }
                if(!s.equals("url"))
                {
                    continue;
                }
                vector.addElement("" + j1);
                String s1 = getProperty(pStepName[j1 - 1]);
                if(s1.length() > 0)
                {
                    vector.addElement("Step " + j1 + ": " + s1);
                } else
                {
                    vector.addElement("Step " + j1);
                }
            }

            return vector;
        }
        if(j >= 0)
        {
            Vector vector1 = new Vector();
            vector1.addElement(URLMonitor.urlencodedDropDown[0]);
            vector1.addElement(URLMonitor.urlencodedDropDown[1]);
            vector1.addElement(URLMonitor.urlencodedDropDown[2]);
            vector1.addElement(URLMonitor.urlencodedDropDown[3]);
            vector1.addElement(URLMonitor.urlencodedDropDown[4]);
            vector1.addElement(URLMonitor.urlencodedDropDown[5]);
            return vector1;
        }
        if(k >= 0)
        {
            Vector vector2 = new Vector();
            vector2.addElement(URLMonitor.authOn401DropDown[0]);
            vector2.addElement(URLMonitor.authOn401DropDown[1]);
            vector2.addElement(URLMonitor.authOn401DropDown[2]);
            vector2.addElement(URLMonitor.authOn401DropDown[3]);
            vector2.addElement(URLMonitor.authOn401DropDown[4]);
            vector2.addElement(URLMonitor.authOn401DropDown[5]);
            return vector2;
        }
        if(i >= 0)
        {
            Vector vector3 = new Vector();
            vector3.addElement("url");
            vector3.addElement("URL");
            if(i > 0)
            {
                vector3.addElement("link");
                vector3.addElement("Link - match the contents of a link");
                vector3.addElement("form");
                vector3.addElement("Form - match the displayed name of a Submit button");
                vector3.addElement("frame");
                vector3.addElement("Frame - match the name of a frame");
                vector3.addElement("refresh");
                vector3.addElement("Refresh - follow a META Refresh tag if it matches");
            }
            return vector3;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        array.add(pStatusText);
        for(int i = 1; i <= numberOfSteps && getProperty(pReference[i - 1]).length() != 0; i++)
        {
            array.add(pStepRoundTripTime[i - 1]);
            if(getPropertyAsBoolean(pMeasureDetails))
            {
                array.add(pStepDNSTime[i - 1]);
                array.add(pStepConnectTime[i - 1]);
                array.add(pStepResponseTime[i - 1]);
                array.add(pStepDownloadTime[i - 1]);
            }
        }

        return array;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Array array = new Array();
        array.add(pRoundTripTime);
        if(flag)
        {
            int i = getNumberOfSteps();
            for(int j = 1; j <= i; j++)
            {
                array.add(pStepRoundTripTime[j - 1]);
                if(getPropertyAsBoolean(pMeasureDetails))
                {
                    array.add(pStepDNSTime[j - 1]);
                    array.add(pStepConnectTime[j - 1]);
                    array.add(pStepResponseTime[j - 1]);
                    array.add(pStepDownloadTime[j - 1]);
                }
            }

        }
        return (Enumeration) array.iterator();
    }

    public static long[] checkURLSequence(HashMap hashmap, StringBuffer stringbuffer, PrintWriter printwriter, StringBuffer stringbuffer1, StringBuffer stringbuffer2, StringBuffer stringbuffer3, URLSequenceMonitor urlsequencemonitor)
    {
        return checkURLSequence(hashmap, stringbuffer, printwriter, stringbuffer1, stringbuffer2, stringbuffer3, urlsequencemonitor, false);
    }

    public static long[] checkURLSequence(HashMap hashmap, StringBuffer stringbuffer, PrintWriter printwriter, StringBuffer stringbuffer1, StringBuffer stringbuffer2, StringBuffer stringbuffer3, URLSequenceMonitor urlsequencemonitor, boolean flag)
    {
        int i;
        for(i = 0; TextUtils.getValue(hashmap, "_reference" + (i + 1)).length() > 0; i++) { }
        String s = "";
        Enumeration enumeration = (Enumeration) hashmap.values("_location");
        if(enumeration.hasMoreElements())
        {
            s = (String)enumeration.nextElement();
        }
        String s1 = TextUtils.getValue(HTTPUtils.locationMap, s);
        if(s1.length() == 0)
        {
            if(s.length() > 0)
            {
                LogManager.log("Error", "Could not find URL location info for \"" + s + "\"");
                s = "";
            }
        } else
        {
            s = HTTPUtils.getLocationURL(s1);
        }
        StringBuffer stringbuffer4 = new StringBuffer();
        String as[] = new String[i + 1];
        String as1[] = new String[i + 1];
        String as2[] = new String[i + 1];
        String as3[] = new String[i + 1];
        String as4[] = new String[i + 1];
        Enumeration aenumeration[] = new Enumeration[i + 1];
        String as5[] = new String[i + 1];
        String as6[] = new String[i + 1];
        String as7[] = new String[i + 1];
        String as8[] = new String[i + 1];
        String as9[] = new String[i + 1];
        String as10[] = new String[i + 1];
        String as11[] = new String[i + 1];
        as1[0] = null;
        as[0] = null;
        as3[0] = null;
        as4[0] = null;
        aenumeration[0] = null;
        as5[0] = null;
        as6[0] = null;
        as7[0] = null;
        as8[0] = null;
        as9[0] = null;
        as2[0] = I18N.getDefaultEncoding();
        as11[0] = null;
        for(int j = 1; j <= i; j++)
        {
            as[j] = TextUtils.getValue(hashmap, pReferenceType[j - 1].getName());
            as1[j] = TextUtils.getValue(hashmap, pReference[j - 1].getName());
            as2[j] = TextUtils.getValue(hashmap, pEncoding[j - 1].getName());
            as3[j] = TextUtils.getValue(hashmap, pContentMatch[j - 1].getName());
            as4[j] = TextUtils.getValue(hashmap, pErrorContent[j - 1].getName());
            if(hashmap.count(pPostData[j - 1].getName()) == 1)
            {
                String as12[] = TextUtils.split(TextUtils.getValue(hashmap, pPostData[j - 1].getName()), "\n");
                Array array1 = new Array();
                for(int k = 0; k < as12.length; k++)
                {
                    if(as12[k].length() > 0)
                    {
                        array1.add(as12[k].trim());
                    }
                }

                if(array1.size() > 0)
                {
                    aenumeration[j] =  (Enumeration) array1.iterator();
                } else
                {
                    aenumeration[j] = null;
                }
            } else
            if(hashmap.count(pPostData[j - 1].getName()) == 0)
            {
                aenumeration[j] = null;
            } else
            {
                aenumeration[j] = (Enumeration) hashmap.values(pPostData[j - 1].getName());
            }
            as5[j] = TextUtils.getValue(hashmap, pUserName[j - 1].getName());
            if(TextUtils.getValue(hashmap, "_challengeResponse").length() > 0)
            {
                as5[j] = URLMonitor.NT_CHALLENGE_RESPONSE_TAG + as5[j];
            }
            as6[j] = TextUtils.getValue(hashmap, pPassword[j - 1].getName());
            as7[j] = TextUtils.getValue(hashmap, pDomain[j - 1].getName());
            as8[j] = TextUtils.getValue(hashmap, pWhenToAuthenticate[j - 1].getName());
            as9[j] = TextUtils.getValue(hashmap, pStepDelay[j - 1].getName());
            as10[j] = TextUtils.getValue(hashmap, pStepName[j - 1].getName());
            as11[j] = TextUtils.getValue(hashmap, pEncodePostData[j - 1].getName());
        }

        String s2 = TextUtils.getValue(hashmap, pProxy.getName());
        Array array = Platform.split(',', s2);
        boolean flag1 = TextUtils.getValue(hashmap, pTimeoutPerStep.getName()).length() > 0;
        long al[] = null;
        if(array.size() <= 1)
        {
            al = checkURLSequence(s, as, as1, as2, as3, as4, s2, TextUtils.getValue(hashmap, pProxyUserName.getName()), TextUtils.getValue(hashmap, pProxyPassword.getName()), aenumeration, as5, as6, as7, as8, as9, as10, as11, TextUtils.getValue(hashmap, pTimeout.getName()), flag1, stringbuffer, urlsequencemonitor, printwriter, stringbuffer1, null, null, stringbuffer4, stringbuffer2, stringbuffer3, flag);
        } else
        {
            Enumeration enumeration1 = (Enumeration) array.iterator();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                String s3 = (String)enumeration1.nextElement();
                stringbuffer.setLength(0);
                if(printwriter != null)
                {
                    printwriter.println("Using proxy: " + s3);
                }
                al = checkURLSequence(s, as, as1, as2, as3, as4, s3, TextUtils.getValue(hashmap, pProxyUserName.getName()), TextUtils.getValue(hashmap, pProxyPassword.getName()), aenumeration, as5, as6, as7, as8, as9, as10, as11, TextUtils.getValue(hashmap, pTimeout.getName()), flag1, stringbuffer, urlsequencemonitor, printwriter, stringbuffer1, null, null, stringbuffer4, stringbuffer2, stringbuffer3, flag);
                stringbuffer.append(", using proxy " + s3);
            } while(URLMonitor.shouldTryNextProxy(al[0]));
        }
        return al;
    }

    static String gv(String as[], int i)
    {
        String s = as[i];
        if(s.length() == 0 && as[0] != null)
        {
            s = as[0];
        }
        return s;
    }

    public static Array checkForm(String s, HashMap hashmap, HTMLTagParser htmltagparser, int i, boolean flag)
    {
        Enumeration enumeration = htmltagparser.findTags(hashmap, "input");
        String s1 = null;
        String s2 = "";
        boolean flag1 = false;
        int j = 0;
        if(i == -1 && s.length() == 0)
        {
            s1 = "";
        } else
        {
            HashMap hashmap1 = null;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                HashMap hashmap2 = (HashMap)enumeration.nextElement();
                String s4 = TextUtils.getValue(hashmap2, "type").toLowerCase();
                if(s4.equals("submit"))
                {
                    j++;
                    if(i != -1)
                    {
                        if(i != j)
                        {
                            continue;
                        }
                        hashmap1 = hashmap2;
                        break;
                    }
                    String s5 = TextUtils.getValue(hashmap2, "value");
                    if(s5.length() == 0)
                    {
                        s5 = "Submit";
                    }
                    if(TextUtils.match(s5, s))
                    {
                        hashmap1 = hashmap2;
                        break;
                    }
                    if(!TextUtils.match(TextUtils.getValue(hashmap2, "name"), s))
                    {
                        continue;
                    }
                    hashmap1 = hashmap2;
                    break;
                }
                if(!s4.equals("image"))
                {
                    continue;
                }
                j++;
                if(i != -1)
                {
                    if(i != j)
                    {
                        continue;
                    }
                    hashmap1 = hashmap2;
                    flag1 = true;
                    break;
                }
                if(TextUtils.match(TextUtils.getValue(hashmap2, "name"), s))
                {
                    hashmap1 = hashmap2;
                    flag1 = true;
                    break;
                }
                if(TextUtils.match(TextUtils.getValue(hashmap2, "alt"), s))
                {
                    hashmap1 = hashmap2;
                    flag1 = true;
                    break;
                }
                if(TextUtils.match(TextUtils.getValue(hashmap2, "value"), s))
                {
                    hashmap1 = hashmap2;
                    flag1 = true;
                    break;
                }
                if(!TextUtils.match(TextUtils.getValue(hashmap2, "src"), s))
                {
                    continue;
                }
                hashmap1 = hashmap2;
                flag1 = true;
                break;
            } while(true);
            if(hashmap1 != null)
            {
                s1 = TextUtils.getValue(hashmap1, "name");
                s2 = TextUtils.getValue(hashmap1, "value");
            }
        }
        Array array = null;
        if(s1 == null && flag)
        {
            s1 = s;
            s2 = s;
        }
        if(s1 != null)
        {
            array = htmltagparser.getVariables(hashmap, s1, s2);
            if(flag1)
            {
                String s3 = "x";
                if(s1.length() != 0)
                {
                    s3 = s1 + "." + s3;
                }
                if(htmltagparser.FindVar(array, s3) == null)
                {
                    array.add(s3 + "=1");
                }
                s3 = "y";
                if(s1.length() != 0)
                {
                    s3 = s1 + "." + s3;
                }
                if(htmltagparser.FindVar(array, s3) == null)
                {
                    array.add(s3 + "=1");
                }
            }
        }
        return array;
    }

    static String getStepName(int i, String as[])
    {
        String s = as[i] + " (step " + i + ")";
        if(s == null || s.length() == 0)
        {
            s = "step " + i;
        }
        return s;
    }

    static Array mergeVariable(Array array, String s)
    {
        Array array1 = new Array();
        Enumeration enumeration = (Enumeration) array.iterator();
        if(s.startsWith(replaceToken))
        {
            String s1 = s.substring(replaceToken.length());
            String s3 = "/";
            if(s1.endsWith("/"))
            {
                s3 = "/";
            } else
            if(s1.endsWith("|"))
            {
                s3 = "|";
            }
            if(s1.startsWith(s3) && s1.endsWith(s3))
            {
                s1 = s1.substring(1, s1.length() - 1);
                int j = s1.indexOf(s3);
                if(j == -1)
                {
                    LogManager.log("Error", "missing middle delimiter in replace expression, " + s);
                } else
                {
                    String s4 = s1.substring(0, j);
                    String s5 = s1.substring(j + 1);
                    String s9;
                    for(; enumeration.hasMoreElements(); array1.add(s9))
                    {
                        String s7 = (String)enumeration.nextElement();
                        LogManager.log("RunMonitor", "form variable replace check, old=" + s7 + ", match=" + s4);
                        s9 = s7;
                        int l = s7.indexOf(s4);
                        if(l != -1)
                        {
                            s9 = s7.substring(0, l) + s5 + s7.substring(l + s4.length());
                            LogManager.log("RunMonitor", "form variable replaced, old=" + s7 + ", new=" + s9);
                        }
                    }

                }
            } else
            {
                LogManager.log("Error", "missing start or end delimiter in replace expression, " + s);
            }
        } else
        {
            String s2 = TextUtils.readStringFromStart(s, "=");
            int i = -1;
            int k = 0;
            if(s2.startsWith("[") && s2.endsWith("]"))
            {
                i = TextUtils.toInt(s2.substring(1, s2.length() - 1));
            }
            boolean flag = false;
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                k++;
                String s6 = (String)enumeration.nextElement();
                String s8 = TextUtils.readStringFromStart(s6, "=");
                boolean flag1 = s8.equals(s2);
                if(i == k)
                {
                    flag1 = true;
                    int i1 = s.indexOf("=");
                    String s11 = "";
                    if(i1 >= 0)
                    {
                        s11 = s.substring(i1 + 1);
                    }
                    s = s8 + "=" + s11;
                    flag = false;
                }
                if(s2.endsWith("*"))
                {
                    String s10 = s2.substring(0, s2.length() - 1);
                    if(s8.startsWith(s10))
                    {
                        flag1 = true;
                        int j1 = s.indexOf("=");
                        String s12 = "";
                        if(j1 >= 0)
                        {
                            s12 = s.substring(j1 + 1);
                        }
                        s = s8 + "=" + s12;
                        flag = false;
                    }
                }
                if(!flag1)
                {
                    array1.add(s6);
                } else
                if(!flag)
                {
                    array1.add(s);
                    flag = true;
                }
            } while(true);
            if(!flag)
            {
                array1.add(s);
            }
        }
        return array1;
    }

    static long[] checkURLSequence(String s, String as[], String as1[], String as2[], String as3[], String as4[], String s1, String s2, 
            String s3, Enumeration aenumeration[], String as5[], String as6[], String as7[], String as8[], String as9[], 
            String as10[], String as11[], String s4, boolean flag, StringBuffer stringbuffer, URLSequenceMonitor urlsequencemonitor, PrintWriter printwriter, 
            StringBuffer stringbuffer1, String as12[], String as13[], StringBuffer stringbuffer2, StringBuffer stringbuffer3, StringBuffer stringbuffer4, boolean flag1)
    {
        String s5 = as1[1];
        if(TextUtils.isSubstituteExpression(s5))
        {
            s5 = TextUtils.substitute(s5);
        }
        String s6 = "";
        long l = 0L;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        long l4 = 0L;
        long al[] = new long[7 + numberOfSteps * 6];
        for(int i = 0; i < al.length; i++)
        {
            al[i] = -1L;
        }

        SocketSession socketsession = SocketSession.getSession(urlsequencemonitor);
        boolean flag2 = socketsession.context.getSetting("_URLSequenceMonitorIgnoreHTMLComments").length() > 0;
        if(urlsequencemonitor != null && urlsequencemonitor.getProperty("_ignoreHTMLComments").length() > 0)
        {
            flag2 = true;
        }
        Enumeration enumeration = aenumeration[1];
        HashMap hashmap = new HashMap();
        URLLoader urlloader = null;
        String s7 = "";
        Array array = new Array();
        Array array1 = new Array();
        long l5 = 0L;
        long l6 = 0L;
        long l7 = Monitor.kURLok;
        long l8 = TextUtils.toLong(s4) * 1000L;
        if(l8 == 0L)
        {
            l8 = URLMonitor.DEFAULT_TIMEOUT;
        }
        boolean flag3 = socketsession.context.getSetting("_urlSequenceMonitorLogError").length() > 0;
        if(flag3 && stringbuffer1 == null)
        {
            stringbuffer1 = new StringBuffer();
        }
        long l9 = Monitor.kURLok;
        String s8 = "";
        boolean flag4 = false;
        if(urlsequencemonitor != null)
        {
            s8 = urlsequencemonitor.getProperty(pResumeStep);
            flag4 = urlsequencemonitor.getPropertyAsBoolean(pResumeRemainingSteps);
        }
        int j = -1;
        if(s8.length() > 0)
        {
            j = TextUtils.toInt(s8);
            if(j >= as.length)
            {
                j = -1;
            }
        }
        String s9 = "";
        StringBuffer stringbuffer5 = null;
        for(int k = 1; k < as.length; k++)
        {
            boolean flag5;
            int j2;
label0:
            {
label1:
                {
                    URLInfo urlinfo;
                    int k3;
                    Array array7;
                    String s23;
                    HTMLTagParser htmltagparser;
label2:
                    {
                        String s21;
                        String s22;
label3:
                        {
                            String s20;
label4:
                            {
                                if(urlsequencemonitor != null && !urlsequencemonitor.stillActive())
                                {
                                    return al;
                                }
                                for(; array1.size() < k; array1.add(new Array())) { }
                                long l10 = Platform.timeMillis();
                                String s11 = socketsession.context.getSetting("_urlOtherHeader");
                                flag5 = false;
                                if(flag)
                                {
                                    l8 = TextUtils.toLong(s4) * 1000L;
                                    if(l8 == 0L)
                                    {
                                        l8 = URLMonitor.DEFAULT_TIMEOUT;
                                    }
                                }
                                boolean flag6 = flag2;
                                if(s5.indexOf("{$") != -1)
                                {
                                    s5 = TextUtils.replaceMatchValues(s5, array, array1);
                                }
                                if(enumeration != null)
                                {
                                    Array array3 = new Array();
                                    String s12;
                                    for(; enumeration.hasMoreElements(); array3.add(s12))
                                    {
                                        s12 = (String)enumeration.nextElement();
                                        if(s12.indexOf("{$") != -1)
                                        {
                                            s12 = TextUtils.replaceMatchValues(s12, array, array1);
                                        }
                                    }

                                    enumeration = (Enumeration) array3.iterator();
                                }
                                if(urlsequencemonitor != null)
                                {
                                    urlsequencemonitor.progressString += "Step " + k + ": checking URL " + s5 + "\n";
                                }
                                if(printwriter != null)
                                {
                                    printwriter.println("Step " + k + ": checking URL " + s5);
                                }
                                Array array4 = TextUtils.enumToArray(enumeration);
                                boolean flag7 = true;
                                if(array4 != null)
                                {
                                    Enumeration enumeration1 = (Enumeration) array4.iterator();
                                    int i2 = 0;
                                    do
                                    {
                                        if(!enumeration1.hasMoreElements())
                                        {
                                            break;
                                        }
                                        String s15 = (String)enumeration1.nextElement();
                                        if(s15.startsWith("Custom-Header: Referer:"))
                                        {
                                            flag7 = false;
                                            if(s15.indexOf("[none]") != -1)
                                            {
                                                array4.remove(i2);
                                            }
                                            break;
                                        }
                                        i2++;
                                    } while(true);
                                }
                                if(flag7 && s6.length() > 0)
                                {
                                    if(s11.length() > 0)
                                    {
                                        s11 = s11 + URLMonitor.CRLF;
                                    }
                                    s11 = s11 + refererStartToken + s6 + "&" + refererEndToken;
                                }
                                if(as12 != null)
                                {
                                    as12[k - 1] = s5;
                                }
                                if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
                                {
                                    LogManager.log("RunMonitor", "Step " + k + "\nURL=" + s5 + "\nMATCH=" + gv(as3, k));
                                }
                                stringbuffer2.setLength(0);
                                stringbuffer2.append(s5);
                                stringbuffer3.setLength(0);
                                stringbuffer4.setLength(0);
                                String s13 = gv(as2, k);
                                String s14 = gv(as3, k);
                                String s16 = gv(as4, k);
                                j2 = TextUtils.toInt(gv(as9, k));
                                int k2 = urlsequencemonitor.getPropertyAsInteger(pRetries) >= 11 ? 10 : urlsequencemonitor.getPropertyAsInteger(pRetries);
                                long l11 = TextUtils.toLong(urlsequencemonitor.getSetting("_urlContentMatchMax"));
                                if(l11 > 1L)
                                {
                                    contentMax = l11;
                                }
                                socketsession.setEncodePostData(gv(as11, k));
                                socketsession.setDomain(gv(as7, k));
                                socketsession.setAuthenticationWhenRequested(gv(as8, k));
                                URLResults urlresults = URLMonitor.checkURLSeq(socketsession, s5, s13, s14, s16, s1, s2, s3, array4, gv(as5, k), gv(as6, k), s, stringbuffer3, contentMax, s11, k2, (int)l8, stringbuffer2, stringbuffer4);
                                l7 = urlresults.getStatus();
                                if(as13 != null)
                                {
                                    as13[k - 1] = urlresults.getErrorMessage();
                                }
                                long l12 = urlresults.getTotalDuration();
                                long l13 = urlresults.getTotalBytes();
                                long l14 = urlresults.getDnsTime();
                                long l15 = urlresults.getConnectTime();
                                long l16 = urlresults.getResponseTime();
                                long l17 = urlresults.getDownloadTime();
                                long l18 = urlresults.getHtmlTruncatedIfNonZero();
                                if(l12 == 0L)
                                {
                                    l12 = Platform.timeMillis() - l10;
                                }
                                if(l7 == (long)kURLTimeoutError)
                                {
                                    l12 = 0L;
                                }
                                int i3 = (k - 1) * 6 + 7;
                                l6 += l13;
                                l5 += l12;
                                l8 -= l12;
                                al[i3] = l12;
                                al[i3 + 1] = l14;
                                al[i3 + 2] = l15;
                                al[i3 + 3] = l16;
                                al[i3 + 4] = l17;
                                al[i3 + 5] = l18;
                                if(urlsequencemonitor != null)
                                {
                                    urlsequencemonitor.progressString += lookupStatus(l7) + " ";
                                    if(l7 == (long)kURLok)
                                    {
                                        urlsequencemonitor.progressString += "in " + TextUtils.floatToString((float)l12 / 1000F, millisecondPrecision) + " sec";
                                    }
                                    urlsequencemonitor.progressString += "\n";
                                }
                                if(printwriter != null)
                                {
                                    printwriter.print(lookupStatus(l7) + " ");
                                    if(l7 == (long)kURLok)
                                    {
                                        printwriter.print("in " + TextUtils.floatToString((float)l12 / 1000F, millisecondPrecision) + " sec");
                                    }
                                    printwriter.println("");
                                }
                                if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
                                {
                                    LogManager.log("RunMonitor", "Step " + k + "\nSTATUS=" + l7 + "\nDURATION=" + l12);
                                }
                                if(stringbuffer1 != null)
                                {
                                    appendContentBuffer(s5, stringbuffer2.toString(), stringbuffer1, stringbuffer4, k, k != as.length - 1, as2);
                                }
                                array = new Array();
                                boolean flag8 = socketsession.context.getSetting("_URLSequenceMonitorIgnoreMatchSummary").length() > 0;
                                String s17 = I18N.UnicodeToString(stringbuffer3.toString(), I18N.nullEncoding());
                                if(TextUtils.isValueExpression(s14))
                                {
                                    array = updateMatchValue(socketsession.context, flag8 ? new StringBuffer("") : stringbuffer, s14, s17, "Content Matched: ");
                                }
                                if(TextUtils.isValueExpression(s16))
                                {
                                    StringBuffer stringbuffer6 = new StringBuffer("");
                                    Array array5 = new Array();
                                    array5 = updateMatchValue(socketsession.context, stringbuffer6, s16, s17, "Error Matched: ");
                                    if(array5.size() > 0)
                                    {
                                        array = array5;
                                    }
                                    if(stringbuffer.length() > 0)
                                    {
                                        stringbuffer.append(", " + stringbuffer6);
                                    } else
                                    {
                                        stringbuffer.append(stringbuffer6);
                                    }
                                }
                                Enumeration enumeration2 = (Enumeration) array.iterator();
                                Array array6 = (Array)array1.get(k - 1);
                                String s19;
                                for(; enumeration2.hasMoreElements(); array6.add(s19))
                                {
                                    s19 = (String)enumeration2.nextElement();
                                }

                                urlinfo = new URLInfo(stringbuffer2.toString());
                                if(l7 == (long)kURLok && urlsequencemonitor != null && (urlsequencemonitor.getPropertyAsBoolean(pImages) || urlsequencemonitor.getPropertyAsBoolean(pFrames)))
                                {
                                    int j3 = urlsequencemonitor.getSettingAsLong("_urlLoadThreads", 1);
                                    URLLoader urlloader1 = new URLLoader(urlsequencemonitor, socketsession, hashmap, j3, urlsequencemonitor.getPropertyAsBoolean(pFrames), urlsequencemonitor.getPropertyAsBoolean(pImages), stringbuffer2.toString(), gv(as5, k), gv(as6, k), s11, s, contentMax, stringbuffer3, (int)l8, s1, s2, s3, printwriter);
                                    urlloader1.waitForCompletion();
                                    urlloader = urlloader1;
                                    String s18 = I18N.StringToUnicode(stringbuffer3.toString(), I18N.nullEncoding());
                                    int i4 = s18.indexOf("sendmail");
                                    if(i4 >= 0)
                                    {
                                        I18N.dmp("Match: ", s14);
                                        I18N.dmp("Content: ", s18.substring(i4, i4 + 40));
                                    }
                                    long al2[] = urlloader1.getResults(s14, s16, new StringBuffer(), new StringBuffer());
                                    long l19 = al2[1] - l;
                                    l = al2[1];
                                    l5 += l19;
                                    l8 -= l19;
                                    al[i3] += l19;
                                    long l20 = al2[5] - l1;
                                    long l21 = al2[6] - l2;
                                    long l22 = al2[7] - l3;
                                    long l23 = al2[8] - l4;
                                    l1 = al2[5];
                                    l2 = al2[6];
                                    l3 = al2[7];
                                    l4 = al2[8];
                                    al[i3 + 1] += l20;
                                    al[i3 + 2] += l21;
                                    al[i3 + 3] += l22;
                                    al[i3 + 4] += l23;
                                }
                                k3 = l7 == (long)kURLok || k >= j ? k + 1 : j;
                                boolean flag9 = k < j;
                                boolean flag10 = l9 != (long)kURLok;
                                if((l7 != (long)kURLok || flag10 && !flag4) && (l7 == (long)kURLok || !flag9))
                                {
                                    break label1;
                                }
                                if(k >= as.length - 1)
                                {
                                    break label0;
                                }
                                s20 = stringbuffer3.toString();
                                array7 = null;
                                s21 = as1[k3];
                                s22 = as[k3];
                                s23 = "POST";
                                if(socketsession.refererURL != null && socketsession.refererURL.length() > 0 && l7 == (long)kURLok)
                                {
                                    s6 = socketsession.refererURL;
                                } else
                                {
                                    s6 = s5;
                                }
                                socketsession.refererURL = null;
                                s5 = "";
                                socketsession.updateCookies(s20, s6);
                                if(urlsequencemonitor != null && urlsequencemonitor.getProperty("_ignoreHTMLComments" + k3).length() > 0)
                                {
                                    flag6 = true;
                                }
                                htmltagparser = new HTMLTagParser(s20, TARGET_TAGS, flag6);
                                if(s21.startsWith("[javascript]"))
                                {
                                    s21 = s21.substring("[javascript]".length());
                                    htmltagparser.ignoreScripts = false;
                                } else
                                {
                                    htmltagparser.ignoreScripts = socketsession.context.getSetting("_urlHTMLInJavaScript").length() == 0;
                                    htmltagparser.ignoreNoscripts = socketsession.context.getSetting("_urlHTMLInJavaScript").length() == 0;
                                }
                                htmltagparser.process();
                                if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
                                {
                                    LogManager.log("RunMonitor", "MATCH REFERENCE=" + s21);
                                    LogManager.log("RunMonitor", "NEXT REFERENCE TYPE=" + s22);
                                    for(int j4 = 0; j4 < htmltagparser.tags.size(); j4++)
                                    {
                                        LogManager.log("RunMonitor", "TAG=" + htmltagparser.tags.get(j4));
                                    }

                                }
                                if(!s22.equals("link"))
                                {
                                    break label3;
                                }
                                s21 = s21.trim();
                                Enumeration enumeration3 = htmltagparser.findTags("a");
                                do
                                {
                                    if(!enumeration3.hasMoreElements())
                                    {
                                        break;
                                    }
                                    HashMap hashmap1 = (HashMap)enumeration3.nextElement();
                                    String s25 = TextUtils.getValue(hashmap1, "href");
                                    String s28 = TextUtils.getValue(hashmap1, "contents");
                                    if(!TextUtils.match(s28, s21))
                                    {
                                        continue;
                                    }
                                    s5 = s25;
                                    break;
                                } while(true);
                                if(s5.length() != 0)
                                {
                                    break label4;
                                }
                                Enumeration enumeration7 = htmltagparser.findTags("area");
                                String s29;
                                do
                                {
                                    if(!enumeration7.hasMoreElements())
                                    {
                                        break label4;
                                    }
                                    HashMap hashmap4 = (HashMap)enumeration7.nextElement();
                                    s29 = TextUtils.getValue(hashmap4, "href");
                                } while(!TextUtils.match(s29, s21));
                                s5 = s29;
                            }
label5:
                            {
                                if(s5.length() == 0 && TextUtils.isValueExpression(s21))
                                {
                                    Array array8 = new Array();
                                    int j5 = TextUtils.matchExpression(s20, s21, array8, new StringBuffer());
                                    if(j5 != Monitor.kURLok)
                                    {
                                        String s30 = URLMonitor.getHTMLEncoding(s20);
                                        int k5 = TextUtils.matchExpression(s20, I18N.UnicodeToString(s21, s30));
                                    }
                                    if(array8.size() > 0)
                                    {
                                        for(Enumeration enumeration11 = (Enumeration) array8.iterator(); enumeration11.hasMoreElements();)
                                        {
                                            String s32 = (String)enumeration11.nextElement();
                                            s5 = s5 + s32;
                                        }

                                    }
                                }
                                if(s5.length() != 0)
                                {
                                    break label5;
                                }
                                Enumeration enumeration4 = htmltagparser.findTags("a");
                                String s26;
                                do
                                {
                                    if(!enumeration4.hasMoreElements())
                                    {
                                        break label5;
                                    }
                                    HashMap hashmap2 = (HashMap)enumeration4.nextElement();
                                    s26 = TextUtils.getValue(hashmap2, "href");
                                } while(!TextUtils.match(s26, s21));
                                s5 = s26;
                            }
                            if(s5.length() == 0)
                            {
                                stringbuffer.append("missing link \"" + s21 + "\" on " + getStepName(k, as10) + " (" + s6 + ")");
                                flag5 = true;
                                if(k < j)
                                {
                                    s5 = as1[j];
                                    k3 = j;
                                    s22 = as[j];
                                    l7 = kURLContentElementMissing;
                                }
                            } else
                            if(printwriter != null)
                            {
                                printwriter.println("Found link " + s21);
                            }
                            s5 = TextUtils.unescapeHTML(s5);
                            break label2;
                        }
label6:
                        {
label7:
                            {
label8:
                                {
                                    if(s22.equals("form"))
                                    {
                                        int k4 = -1;
                                        int i5 = -1;
                                        int i6 = s21.lastIndexOf("]");
                                        int j6 = s21.lastIndexOf("[");
                                        if(j6 != -1 && i6 != -1 && j6 < i6)
                                        {
                                            k4 = TextUtils.toInt(s21.substring(j6 + 1, i6));
                                            s21 = s21.substring(0, j6);
                                            i6 = s21.lastIndexOf("]");
                                            j6 = s21.lastIndexOf("[");
                                            if(j6 != -1 && i6 != -1 && j6 < i6)
                                            {
                                                i5 = TextUtils.toInt(s21.substring(j6 + 1, i6));
                                                s21 = s21.substring(0, j6);
                                            }
                                        }
                                        int i7 = 0;
                                        Enumeration enumeration13 = htmltagparser.findTags("form");
                                        do
                                        {
                                            if(!enumeration13.hasMoreElements())
                                            {
                                                break;
                                            }
                                            i7++;
                                            HashMap hashmap9 = (HashMap)enumeration13.nextElement();
                                            if(k4 != -1 && i7 != k4)
                                            {
                                                continue;
                                            }
                                            String s35 = TextUtils.getValue(hashmap9, "action");
                                            if(s35.length() == 0)
                                            {
                                                s35 = s6;
                                            }
                                            if(aenumeration[k3] != null)
                                            {
                                                Enumeration enumeration14 = aenumeration[k3];
                                                Array array10 = new Array();
                                                do
                                                {
                                                    if(!enumeration14.hasMoreElements())
                                                    {
                                                        break;
                                                    }
                                                    String s39 = (String)enumeration14.nextElement();
                                                    array10.add(s39);
                                                    if(URLMonitor.getHeaderType(s39) == URLMonitor.ACTION_HEADER_TYPE)
                                                    {
                                                        s35 = s39.substring(URLMonitor.ACTION_HEADER.length());
                                                    }
                                                } while(true);
                                                aenumeration[k3] = (Enumeration) array10.iterator();
                                            }
                                            array7 = checkForm(s21, hashmap9, htmltagparser, i5, flag1);
                                            if(array7 == null)
                                            {
                                                continue;
                                            }
                                            s5 = s35;
                                            s23 = TextUtils.getValue(hashmap9, "method");
                                            if(s23.length() == 0)
                                            {
                                                s23 = "GET";
                                            }
                                            break;
                                        } while(true);
                                        if(s5.length() == 0)
                                        {
                                            String s33 = "";
                                            if(i5 != -1)
                                            {
                                                s33 = "[" + i5 + "]";
                                            } else
                                            {
                                                s33 = "\"" + s21 + "\"";
                                            }
                                            String s36 = "";
                                            if(k4 != -1)
                                            {
                                                s36 = " [" + k4 + "]";
                                            }
                                            stringbuffer.append("missing form" + s36 + " button " + s33 + " on " + getStepName(k, as10) + " (" + s6 + ")");
                                            flag5 = true;
                                            if(k < j)
                                            {
                                                s5 = as1[j];
                                                k3 = j;
                                                s22 = as[j];
                                                l7 = kURLContentElementMissing;
                                            }
                                        } else
                                        if(printwriter != null)
                                        {
                                            printwriter.println("Found submit button " + s21);
                                        }
                                        break label2;
                                    }
                                    if(!s22.equals("frame"))
                                    {
                                        break label6;
                                    }
                                    if(TextUtils.match("_parent", s21))
                                    {
                                        s5 = s7;
                                        s7 = null;
                                        break label7;
                                    }
                                    Enumeration enumeration5 = htmltagparser.findTags("frame");
                                    Enumeration enumeration8 = htmltagparser.findTags("iframe");
                                    do
                                    {
                                        if(!enumeration5.hasMoreElements())
                                        {
                                            break;
                                        }
                                        HashMap hashmap5 = (HashMap)enumeration5.nextElement();
                                        if(!TextUtils.match(TextUtils.getValue(hashmap5, "name"), s21))
                                        {
                                            continue;
                                        }
                                        s5 = TextUtils.getValue(hashmap5, "src");
                                        s7 = stringbuffer2.toString();
                                        break;
                                    } while(true);
                                    HashMap hashmap6;
                                    if(s5.length() == 0)
                                    {
                                        do
                                        {
                                            if(!enumeration8.hasMoreElements())
                                            {
                                                break;
                                            }
                                            hashmap6 = (HashMap)enumeration8.nextElement();
                                            if(!TextUtils.match(TextUtils.getValue(hashmap6, "name"), s21))
                                            {
                                                continue;
                                            }
                                            s5 = TextUtils.getValue(hashmap6, "src");
                                            s7 = stringbuffer2.toString();
                                            break;
                                        } while(true);
                                    }
                                    if(s5.length() != 0)
                                    {
                                        break label8;
                                    }
                                    enumeration5 = htmltagparser.findTags("frame");
                                    do
                                    {
                                        if(!enumeration5.hasMoreElements())
                                        {
                                            break label8;
                                        }
                                        hashmap6 = (HashMap)enumeration5.nextElement();
                                    } while(!TextUtils.match(TextUtils.getValue(hashmap6, "src"), s21));
                                    s5 = TextUtils.getValue(hashmap6, "src");
                                    s7 = stringbuffer2.toString();
                                }
                                if(s5.length() != 0)
                                {
                                    break label7;
                                }
                                Enumeration enumeration9 = htmltagparser.findTags("iframe");
                                HashMap hashmap7;
                                do
                                {
                                    if(!enumeration9.hasMoreElements())
                                    {
                                        break label7;
                                    }
                                    hashmap7 = (HashMap)enumeration9.nextElement();
                                } while(!TextUtils.match(TextUtils.getValue(hashmap7, "src"), s21));
                                s5 = TextUtils.getValue(hashmap7, "src");
                                s7 = stringbuffer2.toString();
                            }
                            if(s5.length() == 0)
                            {
                                stringbuffer.append("missing frame \"" + s21 + "\" on " + getStepName(k, as10) + " (" + s6 + ")");
                                flag5 = true;
                                if(k < j)
                                {
                                    s5 = as1[j];
                                    k3 = j;
                                    s22 = as[j];
                                    l7 = kURLContentElementMissing;
                                }
                            } else
                            if(printwriter != null)
                            {
                                printwriter.println("Found frame " + s21);
                            }
                            s5 = TextUtils.unescapeHTML(s5);
                            break label2;
                        }
                        if(s22.equals("refresh"))
                        {
                            Enumeration enumeration6 = htmltagparser.findTags("meta");
label9:
                            do
                            {
                                String s27;
                                int k6;
                                do
                                {
                                    HashMap hashmap3;
                                    do
                                    {
                                        if(!enumeration6.hasMoreElements())
                                        {
                                            break label9;
                                        }
                                        hashmap3 = (HashMap)enumeration6.nextElement();
                                    } while(!TextUtils.getValue(hashmap3, "http-equiv").equalsIgnoreCase("refresh"));
                                    s27 = TextUtils.getValue(hashmap3, "content");
                                    k6 = s27.indexOf("=");
                                } while(k6 < 0);
                                s5 = s27.substring(k6 + 1).trim();
                            } while(!TextUtils.match(s5, s21));
                            if(s5.length() == 0 && k < j)
                            {
                                s5 = as1[j];
                                k3 = j;
                                s22 = as[j];
                                l7 = kURLContentElementMissing;
                            }
                        } else
                        if(s22.equals("url"))
                        {
                            s5 = s21;
                            if(TextUtils.isSubstituteExpression(s5))
                            {
                                s5 = TextUtils.substitute(s5);
                            }
                        }
                    }
                    boolean flag11 = false;
                    if(s5.length() == 0)
                    {
                        l7 = kURLContentElementMissing;
                    } else
                    {
                        s9 = s5;
                        String s24 = "";
                        Enumeration enumeration10 = htmltagparser.findTags("base");
                        if(enumeration10.hasMoreElements())
                        {
                            HashMap hashmap8 = (HashMap)enumeration10.nextElement();
                            s24 = TextUtils.getValue(hashmap8, "href");
                        }
                        if(s5.indexOf("{$") != -1)
                        {
                            s5 = TextUtils.replaceMatchValues(s5, array, array1);
                        }
                        s5 = URLMonitor.resolveURL(s5.trim(), urlinfo, s24);
                        s9 = s5;
                        enumeration = aenumeration[k3];
                        if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
                        {
                            LogManager.log("RunMonitor", "FORM VARIABLES=" + array7);
                            LogManager.log("RunMonitor", "BASE=" + s24);
                            LogManager.log("RunMonitor", "NEWURL=" + s5);
                        }
                        if(array7 != null)
                        {
                            if(enumeration != null)
                            {
                                String s31;
                                for(; enumeration.hasMoreElements(); array7 = mergeVariable(array7, s31))
                                {
                                    s31 = (String)enumeration.nextElement();
                                    if(s31.indexOf("{$") != -1)
                                    {
                                        s31 = TextUtils.replaceMatchValues(s31, array, array1);
                                    }
                                    if(!TextUtils.isSubstituteExpression(s31))
                                    {
                                        continue;
                                    }
                                    if(!flag11)
                                    {
                                        flag11 = TextUtils.isPrivateSubstituteExpression(s31);
                                    }
                                    s31 = TextUtils.substitute(s31);
                                }

                            }
                            if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
                            {
                                LogManager.log("RunMonitor", "FORM VARIABLES LIST=" + array7);
                            }
                            if(s23.equalsIgnoreCase("get"))
                            {
                                Array array9 = new Array();
                                Enumeration enumeration12 = (Enumeration) array7.iterator();
                                byte byte0 = -1;
                                String s34 = s5.indexOf("?") != -1 ? "&" : "?";
                                while(enumeration12.hasMoreElements()) 
                                {
                                    String s37 = (String)enumeration12.nextElement();
                                    int j7 = URLMonitor.getHeaderType(s37);
                                    if(j7 > 0)
                                    {
                                        array9.add(s37);
                                    } else
                                    {
                                        if(j7 == URLMonitor.CUSTOM_CONTENT_TYPE)
                                        {
                                            s37 = s37.substring(URLMonitor.CUSTOM_CONTENT.length());
                                        }
                                        String s38 = s37;
                                        int k7 = s37.indexOf("=");
                                        if(k7 != -1)
                                        {
                                            s38 = s37.substring(0, k7);
                                        }
                                        try
                                        {
                                            s5 = s5 + s34 + URLEncoder.encode(I18N.UnicodeToString(s38, I18N.nullEncoding()), gv(as2, k3));
                                        }
                                        catch(UnsupportedEncodingException unsupportedencodingexception)
                                        {
                                            System.out.println("Error - encoding: " + as2[k3]);
                                            unsupportedencodingexception.printStackTrace();
                                        }
                                        if(k7 != -1)
                                        {
                                            String s40 = s37.substring(k7 + 1);
                                            try
                                            {
                                                s40 = I18N.StringToUnicode(I18N.UnicodeToString(s40, I18N.nullEncoding()), gv(as2, k3));
                                                s5 = s5 + "=" + URLEncoder.encode(s40, gv(as2, k3));
                                            }
                                            catch(UnsupportedEncodingException unsupportedencodingexception1)
                                            {
                                                System.out.println("Error - encoding: " + gv(as2, k3));
                                                s5 = s5 + "=" + I18N.UnicodeToString(s40, I18N.nullEncoding());
                                            }
                                        }
                                        s34 = "&";
                                    }
                                }
                                enumeration = (Enumeration) array9.iterator();
                            } else
                            {
                                enumeration = (Enumeration) array7.iterator();
                            }
                        }
                    }
                    if(!flag11)
                    {
                        s9 = "";
                    } else
                    {
                        s9 = s9 + ".... (contains private variables that aren't not shown)";
                    }
                    break label0;
                }
                s6 = s5;
                if(s9.length() > 0)
                {
                    s6 = s9;
                }
            }
            if(l7 != (long)kURLok && (l9 == (long)kURLok || k != j) && !flag5)
            {
                if(stringbuffer.length() != 0)
                {
                    stringbuffer.append(", ");
                }
                stringbuffer.append(Monitor.lookupStatus(l7) + " on " + getStepName(k, as10) + ", " + s6);
            }
            if(l9 != (long)kURLok && k >= j && !flag4)
            {
                l7 = l9;
                stringbuffer3.setLength(0);
                stringbuffer3.append(stringbuffer5);
                break;
            }
            if(l7 != (long)kURLok && k < j)
            {
                k = j - 1;
                l9 = l7;
                stringbuffer5 = new StringBuffer(stringbuffer3.toString());
            } else
            if(l7 != (long)kURLok)
            {
                if(urlsequencemonitor != null)
                {
                    urlsequencemonitor.setProperty(pErrorStepName, getStepName(k, as10));
                }
                break;
            }
            if(j2 != 0)
            {
                Platform.sleep(j2 * 1000);
            }
        }

        if(l9 != (long)kURLok && l9 != l7)
        {
            l7 = l9;
        }
        socketsession.close();
        if(flag3 && l7 != (long)kURLok && stringbuffer1 != null)
        {
            LogManager.log(socketsession.context.getSetting(Monitor.pErrorLogName), "Error=" + l7 + ", Transaction Sequence=\n" + stringbuffer1.toString());
        }
        if((URLMonitor.debugURL & URLMonitor.kDebugTransaction) != 0)
        {
            LogManager.log("RunMonitor", "DONE WITH SEQUENCE");
        }
        if(urlloader != null)
        {
            long al1[] = urlloader.getResults("", "", new StringBuffer(), new StringBuffer());
            l6 += al1[2];
            al[3] = al1[10];
            al[4] = al1[11];
            al[5] = al1[13];
            al[6] = al1[14];
        }
        if(urlsequencemonitor != null)
        {
            urlsequencemonitor.unsetPropertiesWithPrefix("matchValue");
            String s10 = null;
            int i1 = 1;
            for(int j1 = 0; j1 < array1.size(); j1++)
            {
                Array array2 = (Array)array1.get(j1);
                for(int k1 = 0; k1 < array2.size(); k1++)
                {
                    s10 = array2.get(k1).toString();
                    urlsequencemonitor.setProperty("matchValue" + i1++, s10);
                }

            }

            if(s10 != null)
            {
                urlsequencemonitor.setProperty(pMatchValue, s10);
            }
        }
        al[0] = l7;
        al[1] = l5;
        al[2] = l6;
        return al;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag)
    {
        String s = stringproperty.printString();
        if(s.startsWith("step"))
        {
            int i = 1;
            do
            {
                if(i > numberOfSteps)
                {
                    break;
                }
                if(pStepRoundTripTime[i - 1] == stringproperty)
                {
                    String s1 = getProperty(pStepName[i - 1]);
                    if(s1.length() > 0)
                    {
                        String s2 = "step " + i;
                        s = s1 + s.substring(s2.length());
                    }
                    break;
                }
                i++;
            } while(true);
        }
        return s;
    }

    static Array updateMatchValue(SiteViewObject siteviewobject, StringBuffer stringbuffer, String s, String s1)
    {
        return updateMatchValue(siteviewobject, stringbuffer, s, s1, "matched ");
    }

    static Array updateMatchValue(SiteViewObject siteviewobject, StringBuffer stringbuffer, String s, String s1, String s2)
    {
        Array array = new Array();
        StringBuffer stringbuffer1 = new StringBuffer();
        I18N.dmp("Match: ", s);
        int i = TextUtils.matchExpression(s1, s, array, stringbuffer1, s2);
        if(i != Monitor.kURLok)
        {
            String s3 = URLMonitor.getHTMLEncoding(s1);
            int j = TextUtils.matchExpression(s1, I18N.UnicodeToString(s, s3), array, stringbuffer1, s2);
        }
        if(stringbuffer1.length() != 0)
        {
            if(stringbuffer.length() != 0)
            {
                stringbuffer.append(", ");
            }
            String s4 = stringbuffer1.toString();
            stringbuffer.append(s4);
        }
        return array;
    }

    public static void appendContentBuffer(String s, String s1, StringBuffer stringbuffer, StringBuffer stringbuffer1, int i, boolean flag, String as[])
    {
        String s2 = "";
        String s3 = URLMonitor.getHTTPContent(stringbuffer1.toString());
        if(s3.length() > 0)
        {
            String as1[] = {
                "/TITLE"
            };
            HTMLTagParser htmltagparser = new HTMLTagParser(s3, as1);
            htmltagparser.process();
            Enumeration enumeration = htmltagparser.findTags("title");
            if(enumeration.hasMoreElements())
            {
                HashMap hashmap = (HashMap)enumeration.nextElement();
                s2 = TextUtils.getValue(hashmap, "contents");
            }
        }
        stringbuffer.append("<HR><B><A NAME=step" + i + ">Step " + i + "</A></B><H3>");
        if(s2.length() > 0)
        {
            stringbuffer.append(s2);
            stringbuffer.append(" : ");
        }
        stringbuffer.append(s);
        stringbuffer.append("</H3>");
        if(flag)
        {
            stringbuffer.append("<A HREF=#step" + (i + 1) + ">Next Step</A>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        if(i > 1)
        {
            stringbuffer.append("<A HREF=#step" + (i - 1) + ">Previous Step</A>");
        }
        stringbuffer.append("<HR><P><PRE>");
        stringbuffer.append(s1 + "\n\n");
        String s4 = stringbuffer1.toString();
        stringbuffer.append(I18N.escapeString(s4, gv(as, i)));
        stringbuffer.append("</PRE>\n");
    }

    public static void main(String args[])
        throws IOException
    {
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        int i = 0;
        String s = "";
        String s2 = "";
        String s4 = "";
        for(; i < args.length; i++)
        {
            int j = args[i].indexOf("=");
            if(j != -1)
            {
                hashmapordered.add(args[i].substring(0, j), args[i].substring(j + 1));
                continue;
            }
            if(args[i].equals("-x"))
            {
                String s1 = args[++i];
                i++;
                continue;
            }
            if(args[i].equals("-xu"))
            {
                String s3 = args[++i];
                i++;
                continue;
            }
            if(args[i].equals("-xp"))
            {
                String s5 = args[++i];
                i++;
            }
        }

        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        StringBuffer stringbuffer3 = new StringBuffer();
        PrintWriter printwriter = FileUtils.MakeOutputWriter(System.out);
        long al[] = checkURLSequence(hashmapordered, stringbuffer, printwriter, stringbuffer1, stringbuffer3, stringbuffer2, null);
        printwriter.flush();
        System.err.println("STATUS=" + lookupStatus((int)al[0]));
        if(stringbuffer.length() > 0)
        {
            System.err.println("ERROR\n-------------\n");
            System.out.print(stringbuffer);
            System.err.println("\n-------------");
        }
        System.err.println("SEQUENCE BUFFER---------------------------\n");
        System.out.println(stringbuffer1);
        System.err.println("\n-------------------------------------");
    }

    public int getNumberOfSteps()
    {
        int i = 0;
        for(int j = 1; j <= numberOfSteps && getProperty(pReference[j - 1]).length() != 0; j++)
        {
            i++;
        }

        return i;
    }

    public int getCostInLicensePoints()
    {
        return getNumberOfSteps() * 1;
    }

    static 
    {
        millisecondPrecision = 2;
        numberOfSteps = 20;
        minimumNumberOfSteps = 4;
        HashMap hashmap = MasterConfig.getMasterConfig();
        try
        {
            int i = TextUtils.toInt(TextUtils.getValue(hashmap, "_URLSequenceMonitorSteps"));
            if(i > numberOfSteps)
            {
                numberOfSteps = i;
            }
            i = TextUtils.toInt(TextUtils.getValue(hashmap, "_URLSequenceMonitorMinSteps"));
            if(i > minimumNumberOfSteps)
            {
                minimumNumberOfSteps = i;
            }
            long l = TextUtils.toLong(TextUtils.getValue(hashmap, "_urlContentMatchMax"));
            if(l > 1L)
            {
                contentMax = l;
            }
            if(TextUtils.getValue(hashmap, "_URLSequenceMonitorDebug").length() > 0)
            {
                URLMonitor.debugURL |= URLMonitor.kDebugTransaction;
            }
        }
        catch(Exception exception) { }
        allocateStepProperties(numberOfSteps);
        Array array = new Array();
        for(int j = 1; j <= numberOfSteps; j++)
        {
            array.add(pReferenceType[j - 1]);
            array.add(pReference[j - 1]);
            array.add(pEncoding[j - 1]);
            array.add(pPostData[j - 1]);
            array.add(pContentMatch[j - 1]);
            array.add(pErrorContent[j - 1]);
            array.add(pUserName[j - 1]);
            array.add(pPassword[j - 1]);
            array.add(pDomain[j - 1]);
            array.add(pWhenToAuthenticate[j - 1]);
            array.add(pStepDelay[j - 1]);
            array.add(pStepName[j - 1]);
            array.add(pEncodePostData[j - 1]);
            array.add(pStepRoundTripTime[j - 1]);
            array.add(pStepDNSTime[j - 1]);
            array.add(pStepConnectTime[j - 1]);
            array.add(pStepResponseTime[j - 1]);
            array.add(pStepDownloadTime[j - 1]);
        }

        int k = numberOfSteps * 12;
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for entire sequence to complete");
        pTimeout.setParameterOptions(true, k + 9, true);
        array.add(pTimeout);
        pTimeoutPerStep = new BooleanProperty("_timeoutPerStep", "");
        pTimeoutPerStep.setDisplayText("Timeout is Per Step", "when selected, the timeout specified above is for each step, rather than the overall time");
        pTimeoutPerStep.setParameterOptions(true, k + 10, true);
        array.add(pTimeoutPerStep);
        pProxy = new StringProperty("_proxy");
        pProxy.setDisplayText("HTTP Proxy", "optional list of proxy servers to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
        pProxy.setParameterOptions(true, k + 11, true);
        array.add(pProxy);
        pImages = new BooleanProperty("_getImages", "");
        pImages.setDisplayText("Retrieve Images", "when selected, all of the graphics externally referenced in the page will also be retrieved and calculated in the total response time");
        pImages.setParameterOptions(true, k + 12, true);
        array.add(pImages);
        pFrames = new BooleanProperty("_getFrames", "");
        pFrames.setDisplayText("Retrieve Frames", "when selected, all of the URLs referenced by frames in a frameset will be retrieved and calculated in the total response time");
        pFrames.setParameterOptions(true, k + 13, true);
        array.add(pFrames);
        pProxyUserName = new StringProperty("_proxyusername");
        pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
        pProxyUserName.setParameterOptions(true, k + 14, true);
        array.add(pProxyUserName);
        pProxyPassword = new StringProperty("_proxypassword");
        pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
        pProxyPassword.setParameterOptions(true, k + 15, true);
        pProxyPassword.isPassword = true;
        array.add(pProxyPassword);
        pResumeStep = new ScalarProperty("_resumeStep", "");
        pResumeStep.setDisplayText("Resume at step, if error", "If a step returns an error, you can specify where to resume the sequence.");
        pResumeStep.setParameterOptions(true, k + 17, true);
        array.add(pResumeStep);
        pResumeRemainingSteps = new BooleanProperty("_resumeRemainingSteps", "");
        pResumeRemainingSteps.setDisplayText("Execute resume step and remaining steps", "If resume step is executed, this selection tells the monitor to execute that step and continue executing to the end.");
        pResumeRemainingSteps.setParameterOptions(true, k + 18, true);
        array.add(pResumeRemainingSteps);
        pMeasureDetails = new BooleanProperty("_measureDetails", "");
        pMeasureDetails.setDisplayText("Show Detailed Measurements", "when selected, detailed measurement times are displayed for DNS lookup, connecting, server response, and downloading.");
        pMeasureDetails.setParameterOptions(true, k + 19, true);
        array.add(pMeasureDetails);
        pHTTPVersion10 = new BooleanProperty("_HTTPVersion10", "");
        pHTTPVersion10.setDisplayText("HTTP Version", "when unselected, use HTTP Version 1.1 in the request header; when selected, use 1.0");
        pHTTPVersion10.setParameterOptions(true, k + 20, true);
        array.add(pHTTPVersion10);
        pRetries = new NumericProperty("_retries", "0", "Retries");
        pRetries.setDisplayText("Retries", "The number of times (0-10) to retry the request on recoverable errors, if monitor times out retries are cut short.");
        pRetries.setParameterOptions(true, k + 21, true);
        array.add(pRetries);
        pAcceptAllUntrustedCerts = new BooleanProperty("_sslAcceptAllUntrustedCerts", "");
        pAcceptAllUntrustedCerts.setDisplayText("Accept Untrusted Certs for HTTPS", "Accept certificates that are untrusted in the cert chain.");
        pAcceptAllUntrustedCerts.setParameterOptions(true, k + 22, true);
        array.add(pAcceptAllUntrustedCerts);
        pAcceptInvalidCerts = new BooleanProperty("_sslAcceptInvalidCerts", "");
        pAcceptInvalidCerts.setDisplayText("Accept Invalid Certs for HTTPS", "Accept certificates even if todays date in not in the date ranges in the cert chain.");
        pAcceptInvalidCerts.setParameterOptions(true, k + 23, true);
        array.add(pAcceptInvalidCerts);
        int i1 = 1;
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(i1++);
        array.add(pRoundTripTime);
        pStepRoundTripTime = new StringProperty[numberOfSteps];
        pStepDNSTime = new StringProperty[numberOfSteps];
        pStepConnectTime = new StringProperty[numberOfSteps];
        pStepResponseTime = new StringProperty[numberOfSteps];
        pStepDownloadTime = new StringProperty[numberOfSteps];
        for(int j1 = 1; j1 <= numberOfSteps; j1++)
        {
            pStepRoundTripTime[j1 - 1] = new NumericProperty("stepRoundTripTime" + j1, "", "milliseconds");
            pStepRoundTripTime[j1 - 1].setLabel("step " + j1 + " round trip time");
            pStepRoundTripTime[j1 - 1].setStateOptions(i1++);
            pStepRoundTripTime[j1 - 1].similarProperty = pRoundTripTime;
            array.add(pStepRoundTripTime[j1 - 1]);
            pStepDNSTime[j1 - 1] = new NumericProperty("stepDnsTime" + j1, "", "milliseconds");
            pStepDNSTime[j1 - 1].setLabel("step " + j1 + " dns time");
            pStepDNSTime[j1 - 1].setStateOptions(i1++);
            pStepDNSTime[j1 - 1].similarProperty = pRoundTripTime;
            array.add(pStepDNSTime[j1 - 1]);
            pStepConnectTime[j1 - 1] = new NumericProperty("stepConnectTime" + j1, "", "milliseconds");
            pStepConnectTime[j1 - 1].setLabel("step " + j1 + " connect time");
            pStepConnectTime[j1 - 1].setStateOptions(i1++);
            pStepConnectTime[j1 - 1].similarProperty = pRoundTripTime;
            array.add(pStepConnectTime[j1 - 1]);
            pStepResponseTime[j1 - 1] = new NumericProperty("stepResponseTime" + j1, "", "milliseconds");
            pStepResponseTime[j1 - 1].setLabel("step " + j1 + " response time");
            pStepResponseTime[j1 - 1].setStateOptions(i1++);
            pStepResponseTime[j1 - 1].similarProperty = pRoundTripTime;
            array.add(pStepResponseTime[j1 - 1]);
            pStepDownloadTime[j1 - 1] = new NumericProperty("stepDownloadTime" + j1, "", "milliseconds");
            pStepDownloadTime[j1 - 1].setLabel("step " + j1 + " download time");
            pStepDownloadTime[j1 - 1].setStateOptions(i1++);
            pStepDownloadTime[j1 - 1].similarProperty = pRoundTripTime;
            array.add(pStepDownloadTime[j1 - 1]);
        }

        pTotalErrors = new NumericProperty("totalErrors", "0", "errors");
        pTotalErrors.setLabel("total errors");
        pTotalErrors.setStateOptions(i1++);
        array.add(pTotalErrors);
        pDaysUntilCertExpiration = new NumericProperty("daysUntilCertExpires");
        pDaysUntilCertExpiration.setLabel("Certificate Expiration Days Remaining.");
        pDaysUntilCertExpiration.setIsThreshold(true);
        array.add(pDaysUntilCertExpiration);
        pURL = new StringProperty("url");
        array.add(pURL);
        pURLHeader = new StringProperty("urlHeader");
        array.add(pURLHeader);
        pStatusText = new StringProperty("statusText");
        array.add(pStatusText);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        array.add(pStatus);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        array.add(pMatchValue);
        pErrorStepName = new StringProperty("errorStepName");
        array.add(pErrorStepName);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int k1 = 0; k1 < array.size(); k1++)
        {
            astringproperty[k1] = (StringProperty)array.get(k1);
        }

        addProperties("COM.dragonflow.StandardMonitor.URLSequenceMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.URLSequenceMonitor", Rule.stringToClassifier("status == 200\tgood\tstatus == 2xx"));
        addClassElement("COM.dragonflow.StandardMonitor.URLSequenceMonitor", Rule.stringToClassifier("status == -994\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.URLSequenceMonitor", Rule.stringToClassifier("status != 200\terror\tstatus != 2xx"));
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "description", "(Formerly named: <i>URL Transaction Monitor</i>)<br>Verifies that a sequence can be completed by checking a series of linked web pages (HTTP and HTTPS) and forms.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "help", "URLSeqMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "title", "URL Sequence");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "class", "URLSequenceMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "target", "_reference1");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "topazName", "URL Sequence Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "toolName", "Check URL Sequence");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "toolDescription", "Retrieve a sequence of URL's.");
        setClassProperty("COM.dragonflow.StandardMonitor.URLSequenceMonitor", "loadable", "true");
        millisecondPrecision = TextUtils.toInt(TextUtils.getValue(hashmap, "_defaultMillisecondPrecision"));
        if(millisecondPrecision == 0)
        {
            millisecondPrecision = 2;
        }
    }
}
