/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.ApacheHttpClientUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */


// Referenced classes of package COM.dragonflow.ApacheHttpClientUtils:
// CommonMethodImpl, ICommonMethod

public class ApachePostMethod extends org.apache.commons.httpclient.methods.PostMethod
    implements COM.dragonflow.ApacheHttpClientUtils.ICommonMethod
{

    COM.dragonflow.ApacheHttpClientUtils.CommonMethodImpl myMethodImpl;
    private boolean encodePostData;
    private String requestString;

    public ApachePostMethod(String s)
    {
        myMethodImpl = null;
        encodePostData = true;
        requestString = null;
        myMethodImpl = new CommonMethodImpl(this);
        COM.dragonflow.ApacheHttpClientUtils.CommonMethodImpl _tmp = myMethodImpl;
        COM.dragonflow.ApacheHttpClientUtils.CommonMethodImpl.initMethod(this, s);
    }

    public long getResponseDuration()
    {
        return myMethodImpl.getResponseDuration();
    }

    public long getDownloadDuration()
    {
        return myMethodImpl.getDownloadDuration();
    }

    public long getConnectDuration()
    {
        return myMethodImpl.getConnectDuration();
    }

    public void setConnectStartTime(long l)
    {
        myMethodImpl.setConnectStartTime(l);
    }

    public int execute(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection)
        throws org.apache.commons.httpclient.HttpException, org.apache.commons.httpclient.HttpRecoverableException, java.io.IOException
    {
        myMethodImpl.executeTimingInit(httpstate, httpconnection);
        return super.execute(httpstate, httpconnection);
    }

    public String getRequestString(boolean flag)
    {
        return myMethodImpl.getRequestString(flag);
    }

    public long getDNSDuration()
    {
        return myMethodImpl.getDNSDuration();
    }

    public long getDaysUntilCertExpires()
    {
        return myMethodImpl.getDaysUntilCertExpires();
    }

    protected void readStatusLine(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection)
        throws java.io.IOException, org.apache.commons.httpclient.HttpRecoverableException, org.apache.commons.httpclient.HttpException
    {
        super.readStatusLine(httpstate, httpconnection);
        myMethodImpl.readStatusLine(httpstate, httpconnection);
    }

    public byte[] getResponseBody()
    {
        byte abyte0[] = super.getResponseBody();
        myMethodImpl.computeDownloadDuration();
        return abyte0;
    }

    public boolean getEncodePostData()
    {
        return encodePostData;
    }

    public void setEncodePostData(boolean flag)
    {
        encodePostData = flag;
    }

    public void setRequestBody(String s)
    {
        requestString = s;
        super.setRequestBody(s);
    }

    protected byte[] generateRequestBody()
    {
        if(encodePostData)
        {
            return super.generateRequestBody();
        }
        if(getParameters().length > 0)
        {
            String s = COM.dragonflow.ApacheHttpClientUtils.ApachePostMethod.formUrlNotEncoded(getParameters());
            return s.getBytes();
        }
        if(requestString != null && requestString.length() > 0)
        {
            return requestString.getBytes();
        } else
        {
            return null;
        }
    }

    private static String formUrlNotEncoded(org.apache.commons.httpclient.NameValuePair anamevaluepair[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < anamevaluepair.length; i++)
        {
            if(anamevaluepair[i].getName() == null)
            {
                continue;
            }
            if(i > 0)
            {
                stringbuffer.append("&");
            }
            String s = anamevaluepair[i].getName();
            stringbuffer.append(s);
            stringbuffer.append("=");
            if(anamevaluepair[i].getValue() != null)
            {
                String s1 = anamevaluepair[i].getValue();
                stringbuffer.append(s1);
            }
        }

        return stringbuffer.toString();
    }

    public int getResponseContentLength()
    {
        return super.getResponseContentLength();
    }

    protected void addCookieRequestHeader(org.apache.commons.httpclient.HttpState httpstate, org.apache.commons.httpclient.HttpConnection httpconnection)
        throws java.io.IOException, org.apache.commons.httpclient.HttpException
    {
        myMethodImpl.addCookieRequestHeader(httpstate, httpconnection, getRequestHeaderGroup());
    }
}
