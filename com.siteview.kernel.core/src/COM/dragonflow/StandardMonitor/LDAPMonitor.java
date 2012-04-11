/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * LDAPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>LDAPMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.net.URLEncoder;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;
import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor

public class LDAPMonitor extends AtomicMonitor
{

    static StringProperty pURLProvider;
    static StringProperty pSecurityPrincipal;
    static StringProperty pSecurityCredential;
    static StringProperty pMatchString;
    static StringProperty pMatchValue;
    static StringProperty pLdapQuery;
    static StringProperty pLdapFilter;
    static StringProperty pStatus;
    static StringProperty pRoundTripTime;

    public LDAPMonitor()
    {
    }

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(pURLProvider));
    }

    protected boolean update()
    {
        String s = getProperty(pURLProvider);
        String s1 = getProperty(pSecurityPrincipal);
        String s2 = getProperty(pSecurityCredential);
        String s3 = getProperty(pMatchString);
        String s4 = getProperty(pLdapQuery);
        String s5 = getProperty(pLdapFilter);
        long l = 200L;
        String as[] = authenticate(s, s1, s2, s4, s5);
        String s7 = TextUtils.floatToString(TextUtils.toFloat(as[2]) / 1000F, 2) + " sec";
        String s6;
        if(as[0].length() > 0)
        {
            s6 = as[0];
        } else
        {
            s6 = as[1] + " " + s7;
        }
        l = TextUtils.toLong(as[3]);
        if(s3.length() != 0)
        {
            Array array = new Array();
            l = TextUtils.matchExpression(as[1], s3, array, new StringBuffer());
            if(l != (long)Monitor.kURLok)
            {
                String s8 = URLMonitor.getHTMLEncoding(as[1]);
                l = TextUtils.matchExpression(as[1], I18N.UnicodeToString(s3, s8), array, new StringBuffer());
                s6 = "content match error, " + s6;
            } else
            if(array.size() == 0)
            {
                s6 = "matched: " + s6;
            } else
            {
                setProperty(pMatchValue, array.at(0));
                s6 = "matched " + array.at(0) + " " + s6;
            }
        }
        int i = getSettingAsLong("_ldapMaxSummary", 100);
        if(s6.length() > i)
        {
            s6 = s6.substring(0, i) + " ...";
        }
        if(stillActive())
        {
            synchronized(this)
            {
                if(as[0].length() > 0)
                {
                    setProperty(pStatus, l);
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pStateString, getProperty(pStatus) + " " + s6);
                    setProperty(pNoData, "n/a");
                } else
                {
                    setProperty(pRoundTripTime, as[2]);
                    setProperty(pStatus, l);
                    setProperty(pStateString, s6);
                }
            }
        }
        return true;
    }

    public static String[] authenticate(String s, String s1, String s2, String s3, String s4)
    {
        String s5 = "";
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        long l = Platform.timeMillis();
        s5 = getLdapAttr(s, s1, s2, stringbuffer1, stringbuffer, s3, s4);
        if(stringbuffer.length() > 0)
        {
            LogManager.log("Error", "not able to authenticate user ");
        }
        long l1 = Platform.timeMillis() - l;
        String as[] = new String[4];
        as[0] = stringbuffer.toString();
        as[1] = s5;
        as[2] = String.valueOf(l1);
        as[3] = stringbuffer1.toString();
        return as;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        return array;
    }

    public static String getLdapAttr(String s, String s1, String s2, StringBuffer stringbuffer, StringBuffer stringbuffer1, String s3, String s4)
    {
        if(s2.equals("none"))
        {
            s2 = "";
        }
        Hashtable hashtable = new Hashtable(11);
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        if(s2.length() > 0)
        {
            hashtable.put("java.naming.security.authentication", "simple");
        } else
        {
            hashtable.put("java.naming.security.authentication", "none");
        }
        if(s1.length() > 0)
        {
            hashtable.put("java.naming.security.principal", s1);
        }
        hashtable.put("java.naming.security.credentials", s2);
        Object obj = null;
        String s5 = "";
        String s6 = "[LDAP-ANY]";
        String s7 = "[LDAP-3]";
        String s8 = "[LDAP-SSL]";
        if(s.startsWith(s8))
        {
            hashtable.put("java.naming.security.protocol", "ssl");
            s = s.substring(s8.length());
        } else
        {
            hashtable.put("java.naming.ldap.factory.socket", "COM.dragonflow.Utils.SimpleSocketFactory");
        }
        hashtable.put("com.sun.jndi.ldap.trace.ber", System.err);
        if(s3.startsWith(s7))
        {
            hashtable.put("java.naming.ldap.version", "3");
            s3 = s3.substring(s7.length());
        } else
        if(s3.startsWith(s6))
        {
            s3 = s3.substring(s6.length());
        } else
        {
            hashtable.put("java.naming.ldap.version", "2");
        }
        try
        {
            InitialDirContext initialdircontext;
            if(s3.length() > 0 && s4.length() > 0)
            {
                hashtable.put("java.naming.provider.url", s);
                initialdircontext = new InitialDirContext(hashtable);
                SearchControls searchcontrols = new SearchControls();
                searchcontrols.setReturningObjFlag(true);
                searchcontrols.setSearchScope(2);
                NamingEnumeration namingenumeration = initialdircontext.search(s3, s4, searchcontrols);
                s5 = getSearchEnumeration(namingenumeration, stringbuffer1);
            } else
            if(s3.length() > 0)
            {
                hashtable.put("java.naming.provider.url", s + "/" + s3);
                initialdircontext = new InitialDirContext(hashtable);
                javax.naming.directory.Attributes attributes = initialdircontext.getAttributes(s + "/" + s3);
                if(attributes != null)
                {
                    s5 = attributes.toString();
                }
            } else
            {
                hashtable.put("java.naming.provider.url", s + "/" + s1);
                initialdircontext = new InitialDirContext(hashtable);
                javax.naming.directory.Attributes attributes1 = initialdircontext.getAttributes(s + "/" + s1);
                if(attributes1 != null)
                {
                    s5 = attributes1.toString();
                }
            }
            initialdircontext.close();
        }
        catch(AuthenticationException authenticationexception)
        {
            stringbuffer.append("-65");
            return stringbuffer1.append("not able to authenticate this user " + authenticationexception.toString(true)).toString();
        }
        catch(CommunicationException communicationexception)
        {
            stringbuffer.append("-997");
            return stringbuffer1.append("not able to reach server " + communicationexception.toString(true)).toString();
        }
        catch(NameNotFoundException namenotfoundexception)
        {
            stringbuffer.append("-97");
            return stringbuffer1.append("Not able to find this user. " + namenotfoundexception.toString(true)).toString();
        }
        catch(InvalidSearchFilterException invalidsearchfilterexception)
        {
            stringbuffer.append("-96");
            return stringbuffer1.append("Invalid Filter. " + invalidsearchfilterexception.toString(true)).toString();
        }
        catch(InvalidSearchControlsException invalidsearchcontrolsexception)
        {
            stringbuffer.append("-95");
            return stringbuffer1.append("Invalid Search Control " + invalidsearchcontrolsexception.toString(true)).toString();
        }
        catch(NamingException namingexception)
        {
            stringbuffer.append("-98");
            return stringbuffer1.append("Not able to authenticate this user. " + namingexception.toString(true)).toString();
        }
        catch(Exception exception)
        {
            stringbuffer.append("-99");
            return stringbuffer1.append("General error. " + exception.getMessage()).toString();
        }
        if(s5.length() > 0 || stringbuffer1.length() <= 0)
        {
            stringbuffer.append("200");
            return s5;
        } else
        {
            stringbuffer.append("-99");
            return stringbuffer1.toString();
        }
    }

    public static String getSearchEnumeration(NamingEnumeration namingenumeration, StringBuffer stringbuffer)
    {
        String s = "";
        try
        {
            while(namingenumeration.hasMore()) 
            {
                TextUtils.debugPrint(" helloe");
                SearchResult searchresult = (SearchResult)namingenumeration.next();
                s = s + searchresult.getName() + " ";
                s = s + searchresult.getAttributes();
            }
        }
        catch(NamingException namingexception)
        {
            stringbuffer.append(" Search Result Error " + namingexception.getMessage());
            s = "";
        }
        return s;
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=LDAP&securityPrincipal=";
        s = s + URLEncoder.encode(getProperty(pSecurityPrincipal));
        s = s + "&securityCredential=";
        s = s + URLEncoder.encode(TextUtils.obscure(getProperty(pSecurityCredential)));
        s = s + "&urlProvider=";
        s = s + URLEncoder.encode(getProperty(pURLProvider));
        return s;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pURLProvider)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static 
    {
        pURLProvider = new StringProperty("_urlprovider");
        pURLProvider.setDisplayText("LDAP service provider", "the LDAP server to connect to (example: ldap://ldap.this-company.com:389");
        pURLProvider.setParameterOptions(true, 2, false);
        pSecurityPrincipal = new StringProperty("_securityprincipal");
        pSecurityPrincipal.setDisplayText("Security Principal", "the LDAP query to perform (example: uid=testuser,ou=TEST,o=this-company.com)");
        pSecurityPrincipal.setParameterOptions(true, 4, false);
        pSecurityCredential = new StringProperty("_password");
        pSecurityCredential.setDisplayText("Security Credentials", "the password used for authentication. If left blank it defaults to Anonymous.");
        pSecurityCredential.setParameterOptions(true, 5, false);
        pSecurityCredential.isPassword = true;
        pMatchString = new StringProperty("_matchstring");
        pMatchString.setDisplayText("Content Match", "optional, match against query result, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>.");
        pMatchString.setParameterOptions(true, 1, true);
        pLdapQuery = new StringProperty("_ldapquery");
        pLdapQuery.setDisplayText("Object Query", "Be able to look at a separate object than the user dn object. This field has to be set if using a filter.");
        pLdapQuery.setParameterOptions(true, 2, true);
        pLdapFilter = new StringProperty("_ldapfilter");
        pLdapFilter.setDisplayText("LDAP Filter", "To be able to perform a search with filter");
        pLdapFilter.setParameterOptions(true, 3, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(2);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        StringProperty astringproperty[] = {
            pURLProvider, pSecurityPrincipal, pSecurityCredential, pMatchValue, pMatchString, pRoundTripTime, pStatus, pLdapQuery, pLdapFilter
        };
        addProperties("COM.dragonflow.StandardMonitor.LDAPMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.LDAPMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.LDAPMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "description", "Tests an LDAP server by sending a password authentication request.");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "help", "LDAPMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "title", "LDAP");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "class", "LDAPMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "toolName", "LDAP Authentication");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "toolDescription", "Test an LDAP server by authenticating a user.");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "topazName", "LDAP Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "topazType", "Web Application Server");
        try
        {
            Class.forName("javax.naming.ldap.LdapContext");
            setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "loadable", "true");
        }
        catch(Throwable throwable)
        {
            setClassProperty("COM.dragonflow.StandardMonitor.LDAPMonitor", "loadable", "false");
        }
    }
}
