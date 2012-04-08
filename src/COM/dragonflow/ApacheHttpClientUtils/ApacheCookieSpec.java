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

import java.util.Date;

public class ApacheCookieSpec extends org.apache.commons.httpclient.cookie.CookieSpecBase {

    public ApacheCookieSpec() {
    }

    public boolean match(String s, int i, String s1, boolean flag, org.apache.commons.httpclient.Cookie cookie) {
        LOG.trace("enter CookieSpecBase.match(String, int, String, boolean, Cookie");
        if (s == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (s.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (i < 0) {
            throw new IllegalArgumentException("Invalid port: " + i);
        }
        if (s1 == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (s1.trim().equals("")) {
            s1 = "/";
        }
        s = s.toLowerCase();
        if (cookie.getDomain() == null) {
            LOG.warn("Invalid cookie state: domain not specified");
            return false;
        }
        if (cookie.getPath() == null) {
            LOG.warn("Invalid cookie state: path not specified");
            return false;
        } else {
            return (cookie.getExpiryDate() == null || cookie.getExpiryDate().after(new Date())) && COM.dragonflow.ApacheHttpClientUtils.ApacheCookieSpec.domainMatch(s, cookie.getDomain()) && pathMatch(s1, cookie.getPath()) && (!cookie.getSecure() || flag);
        }
    }

    private static boolean domainMatch(String s, String s1) {
        boolean flag = s.equals(s1) || s1.startsWith(".") && s.endsWith(s1);
        return flag;
    }

    private boolean pathMatch(String s, String s1) {
        return s.startsWith(s1);
    }
}
