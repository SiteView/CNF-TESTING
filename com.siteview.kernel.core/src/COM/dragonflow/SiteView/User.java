/*
 * 
 * Created on 2005-2-16 17:40:06
 *
 * User.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>User</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardMonitor.LDAPMonitor;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, Platform, MasterConfig, MonitorGroup,
// SiteViewGroup

public class User extends SiteViewObject {

    public static StringProperty pLogin;

    public static StringProperty pPassword;

    public static StringProperty pLdap;

    public static StringProperty pSecurityPrincipal;

    public static StringProperty pRealName;

    public static StringProperty pEditPermissions;

    public static StringProperty pAccount;

    public static StringProperty pEmail;

    public static final String USERS_FILENAME = "users.config";

    public static HashMap accountTable = new HashMapOrdered(true);

    public static HashMap litePermissions;

    private static java.util.HashMap ldapAuthenticationCache = new java.util.HashMap();

    private static final String ldapCacheExpirationInSecsStr = "_ldapCacheExpirationInSecs";

    private static final String ldapAuthenticationCaching = "_ldapAuthenticationCaching";

    private static long ldapCacheExpirationInSecs;

    private static boolean doCaching = false;

    static Array usersCache = null;

    HashMap permissions;

    MonitorGroup accountGroup;

    public User() {
        permissions = null;
        accountGroup = null;
    }

    static void registerUsers(MonitorGroup monitorgroup, String s, Enumeration enumeration, HashMap hashmap) {
        String s1;
        for (; enumeration.hasMoreElements(); registerUser(monitorgroup, s, TextUtils.stringToHashMap(s1), hashmap)) {
            s1 = (String) enumeration.nextElement();
        }

    }

    static void registerUser(String s, HashMap hashmap, HashMap hashmap1) {
        registerUser(null, s, hashmap, hashmap1);
    }

    static void registerUser(MonitorGroup monitorgroup, String s, HashMap hashmap, HashMap hashmap1) {
        if (s.length() > 0) {
            User user = new User();
            user.readFromHashMap(hashmap);
            user.initialize(hashmap);
            user.permissions = hashmap1;
            user.accountGroup = monitorgroup;
            user.setProperty(pAccount, s);
            accountTable.add(s, user);
        }
    }

    public static HashMap createAdministratorLogin(HashMap hashmap) {
        HashMap hashmap1 = new HashMap();
        hashmap1.put("_id", "administrator");
        hashmap1.put("_login", TextUtils.getValue(hashmap, "_adminUsername"));
        hashmap1.put("_password", TextUtils.getValue(hashmap, "_adminPassword"));
        hashmap1.put("_realName", Platform.productName + " Administrator");
        hashmap1.put("_edit", "true");
        hashmap1.put("_useGlobalPermissions", "true");
        return hashmap1;
    }

    public static String checkPassword(HashMap hashmap, String s, String s1) {
        String s2 = "";
        if (!s.equals(s1)) {
            s2 = addError(s2, "passwords entered are different");
        }
        String s3 = TextUtils.getValue(hashmap, "_adminMinimumLength");
        if (s3.length() > 0) {
            int i = TextUtils.toInt(s3);
            if (i > 0 && s.length() < i) {
                s2 = addError(s2, "password must be at least " + i + " characters");
            }
        }
        if (TextUtils.getValue(hashmap, "_adminRequireAlpha").length() > 0 && !TextUtils.hasChars(s, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
            s2 = addError(s2, "password must contain at least one letter");
        }
        if (TextUtils.getValue(hashmap, "_adminRequireNumber").length() > 0 && !TextUtils.hasChars(s, "0123456789")) {
            s2 = addError(s2, "password must contain at least one number");
        }
        if (TextUtils.getValue(hashmap, "_adminRequirePunctuation").length() > 0 && !TextUtils.hasChars(s, ",./<>?;':\"[]\\{}|=-+_)(*&^%$#@!`~")) {
            s2 = addError(s2, "password must contain at least one punctuation character");
        }
        return s2;
    }

    static String addError(String s, String s1) {
        if (s.length() > 0) {
            s = s + ", ";
        }
        s = s + s1;
        return s;
    }

    public static HashMap createUserLogin(HashMap hashmap) {
        HashMap hashmap1 = new HashMap();
        hashmap1.put("_id", "user");
        hashmap1.put("_login", TextUtils.getValue(hashmap, "_userUsername"));
        hashmap1.put("_password", TextUtils.getValue(hashmap, "_userPassword"));
        hashmap1.put("_realName", Platform.productName + " User");
        if (TextUtils.getValue(hashmap, "_userEnabled").length() == 0) {
            hashmap1.put("_disabled", "true");
        }
        if (TextUtils.getValue(hashmap, "_userDisableQuickReports").length() == 0) {
            hashmap1.put("_reportAdhoc", "true");
            hashmap1.put("_monitorRecent", "true");
            hashmap1.put("_alertRecentReport", "true");
            hashmap1.put("_alertAdhocReport", "true");
        }
        if (TextUtils.getValue(hashmap, "_userDisableReportIndexToolbar").length() == 0) {
            hashmap1.put("_reportIndexToolbar", "true");
        }
        if (TextUtils.getValue(hashmap, "_userEnableRefresh").length() > 0) {
            hashmap1.put("_monitorRefresh", "true");
        }
        if (TextUtils.getValue(hashmap, "_userHideMoreColumn").length() == 0) {
            hashmap1.put("_monitorTools", "true");
        }
        hashmap1.put("_preferenceTest", "true");
        hashmap1.put("_progress", "true");
        hashmap1.put("_browse", "true");
        hashmap1.put("_tools", "true");
        hashmap1.put("_topazConfigChangesReport", "true");
        return hashmap1;
    }

    public static HashMap findUser(Array array, String s) {
        for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements();) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            if (s.equals(TextUtils.getValue(hashmap, "_id"))) {
                return hashmap;
            }
        }

        return null;
    }

    static void clearUsersCache() {
        usersCache = null;
    }

    public static Array readUsers() {
        if (usersCache == null) {
            usersCache = new Array();
            try {
                usersCache = FrameFile.readFromFile(Platform.getRoot() + "/groups/" + "users.config");
            } catch (Exception exception) {
            }
            HashMap hashmap = MasterConfig.getMasterConfig();
            initializeUsersList(usersCache, hashmap);
        }
        return usersCache;
    }

    public static void initializeUsersList(Array array, HashMap hashmap) {
        if (array.size() == 0) {
            HashMap hashmap1 = new HashMap();
            hashmap1.put("_nextID", "1");
            array.add(hashmap1);
        }
        if (findUser(array, "administrator") == null) {
            array.add(createAdministratorLogin(hashmap));
        }
        if (!Platform.isPortal() && findUser(array, "user") == null) {
            array.add(createUserLogin(hashmap));
        }
    }

    public static void writeUsers(Array array) throws IOException {
        FrameFile.writeToFile(Platform.getRoot() + "/groups/" + "users.config", array);
        unloadUsers();
        loadUsers();
    }

    public static void loadUsers() {
        LogManager.log("RunMonitor", "Loading users.config");
        Array array = readUsers();
        Enumeration enumeration = array.elements();
        if (enumeration.hasMoreElements()) {
            enumeration.nextElement();
        }
        while (enumeration.hasMoreElements()) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            String s = TextUtils.getValue(hashmap, "_useGlobalPermissions");
            if (s.length() > 0) {
                registerUser(TextUtils.getValue(hashmap, "_id"), hashmap, MasterConfig.getMasterConfig());
            } else {
                registerUser(TextUtils.getValue(hashmap, "_id"), hashmap, hashmap);
            }
        }
    }

    public static void unloadUsers() {
        LogManager.log("Debug", "unloading users.config");
        Array array = readUsers();
        Enumeration enumeration = array.elements();
        if (enumeration.hasMoreElements()) {
            enumeration.nextElement();
        }
        HashMap hashmap;
        for (; enumeration.hasMoreElements(); unregisterUsers(TextUtils.getValue(hashmap, "_id"))) {
            hashmap = (HashMap) enumeration.nextElement();
        }

        clearUsersCache();
    }

    static void unregisterUsers(String s) {
        accountTable.remove(s);
    }

    public static Array findUsersForLogin(String s, String s1) {
        Array array = new Array();
        for (Enumeration enumeration = accountTable.keys(); enumeration.hasMoreElements();) {
            String s2 = (String) enumeration.nextElement();
            Enumeration enumeration1 = accountTable.values(s2);
            while (enumeration1.hasMoreElements()) {
                User user = (User) enumeration1.nextElement();
                if (user.getProperty(pLogin).equalsIgnoreCase(s) && user.getProperty(pPassword).equals(s1)) {
                    array.add(user);
                } else if (user.getProperty(pLogin).equalsIgnoreCase(s) && user.getProperty(pLdap).length() > 0 && user.getProperty(pSecurityPrincipal).length() > 0) {
                    array.add(user);
                }
            }
        }

        return array;
    }

    public boolean authenticateUsingLdap(HTTPRequest httprequest) {
        if (httprequest.getPassword().length() <= 0) {
            return false;
        }
        if (doCaching) {
            return authenticateUsingCachedLdap(httprequest);
        } else {
            return authenticateUsingNonCachedLdap(httprequest);
        }
    }

    public boolean authenticateUsingNonCachedLdap(HTTPRequest httprequest) {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        LDAPMonitor.getLdapAttr(getProperty(pLdap), getProperty(pSecurityPrincipal), httprequest.getPassword(), stringbuffer, stringbuffer1, "", "");
        if (stringbuffer1.length() > 0) {
            LogManager.log("Error", "User::Error authenticating user with LDAP server: " + stringbuffer1.toString());
            return false;
        } else {
            return true;
        }
    }

    public boolean authenticateUsingCachedLdap(HTTPRequest httprequest) {
        String s = genLdapAuthenticationCacheKey(httprequest);
        Long long1 = null;
        synchronized (ldapAuthenticationCache) {
            long1 = (Long) ldapAuthenticationCache.get(s);
        }
        boolean flag = false;
        if (long1 != null && !authExpired(long1)) {
            flag = true;
        } else if (long1 == null || authExpired(long1)) {
            flag = cacheLdapAuthenticationResult(httprequest);
        }
        return flag;
    }

    private boolean cacheLdapAuthenticationResult(HTTPRequest httprequest) {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        LDAPMonitor.getLdapAttr(getProperty(pLdap), getProperty(pSecurityPrincipal), httprequest.getPassword(), stringbuffer, stringbuffer1, "", "");
        String s = genLdapAuthenticationCacheKey(httprequest);
        if (stringbuffer1.length() > 0) {
            LogManager.log("Error", "User::Error authenticating user with LDAP server: " + stringbuffer1.toString());
            return false;
        }
        synchronized (ldapAuthenticationCache) {
            ldapAuthenticationCache.put(s, new Long(System.currentTimeMillis()));
        }
        return true;
    }

    private boolean authExpired(Long long1) {
        if (long1 == null) {
            return true;
        }
        return long1.longValue() < System.currentTimeMillis() - ldapCacheExpirationInSecs * 1000L;
    }

    private String genLdapAuthenticationCacheKey(HTTPRequest httprequest) {
        return getProperty(pLdap).toString() + "." + getProperty(pSecurityPrincipal).toString() + "." + httprequest.getPassword();
    }

    public boolean authenticate(HTTPRequest httprequest) {
        if (getProperty(pLogin).equalsIgnoreCase(httprequest.getUsername())) {
            if (getProperty(pLdap).length() > 0 && getProperty(pSecurityPrincipal).length() > 0) {
                return authenticateUsingLdap(httprequest);
            }
            if (getProperty(pPassword).equals(httprequest.getPassword())) {
                if (accountGroup != null) {
                    String s = accountGroup.getSetting("_initialLoginEmail");
                    if (s.length() > 0) {
                        String s1 = accountGroup.getProperty("lastLogin");
                        if (s1.length() == 0) {
                            String s2 = TextUtils.dateToString();
                            accountGroup.setProperty("lastLogin", s2);
                            String s3 = "";
                            String s4 = accountGroup.getProperty("_contactEmail");
                            String s5 = accountGroup.getProperty("_id");
                            String s6 = "Initial account login: " + s5 + ", " + s4;
                            HashMap hashmap = new HashMap(MasterConfig.getMasterConfig());
                            hashmap.put("_fromAddress", Platform.supportEmail);
                            hashmap.put("_hideServerInSubject", "true");
                            String s7 = "Initial account login\n\nuser=" + getProperty(pLogin) + "\n" + "email=" + s4 + "\n" + "account=" + s5 + "\n" + "date=" + Platform.makeDate();
                            MailUtils.mail(hashmap, s, s6, s7, s3, null, false);
                            LogManager.log("RunMonitor", "Initial login for " + s5);
                        }
                        s1 = TextUtils.dateToString();
                        accountGroup.setProperty("lastLogin", s1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static Enumeration getUsersForAccount(String s) {
        if (accountTable.size() == 0) {
            getUserForAccount(s);
        }
        return accountTable.values(s);
    }

    public static User getFirstUserForAccount(String s) {
        Enumeration enumeration = getUsersForAccount(s);
        if (enumeration.hasMoreElements()) {
            return (User) enumeration.nextElement();
        } else {
            return null;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static User getUserForAccount(String s) {
        if (accountTable.size() > 0) {
            return getFirstUserForAccount(s);
        }
        if (Platform.isStandardAccount(s)) {
            loadUsers();
            return getFirstUserForAccount(s);
        }
        User user;
        try {
            Array array = CGI.ReadGroupFrames(s, null);
            if (array == null || array.size() <= 0) {
                return null;
            }
            jgl.HashMap hashmap = (jgl.HashMap) array.at(0);
            registerUsers(null, s, hashmap.values("_user"), hashmap);
            user = getFirstUserForAccount(s);
        } catch (Exception exception) {
            System.out.println("Could not read group file for " + s + " : " + exception);
            return null;
        }
        return user;
    }

    public String getPermission(String s) {
        if (permissions == null) {
            return "";
        }
        String s1 = (String) permissions.get(s);
        if (s1 == null) {
            s1 = "";
        }
        return s1;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public Vector getPermissions(String s) {
        Vector vector = new Vector();
        if (permissions != null) {
            Enumeration enumeration = permissions.values(s);
            String as[] = new String[2];
            while (enumeration.hasMoreElements()) {
                String s1 = (String) enumeration.nextElement();
                int i = TextUtils.splitChar(s1, ',', as);
                if (i == 2) {
                    vector.addElement(as[0]);
                    vector.addElement(as[1]);
                    System.out.println(as[0]);
                    System.out.println(as[1]);
                }
            }
        }
        return vector;
    }

    public String getPermission(String s, String s1) {
        if (permissions == null) {
            return "";
        }
        for (Enumeration enumeration = permissions.values(s); enumeration.hasMoreElements();) {
            String s2 = (String) enumeration.nextElement();
            String as[] = TextUtils.split(s2, ",");
            if (as.length == 2 && as[0].equals(s1)) {
                return as[1];
            }
        }

        return "";
    }

    static {
        litePermissions = null;
        ldapCacheExpirationInSecs = -1L;
        pLogin = new StringProperty("_login", "");
        pPassword = new StringProperty("_password", "");
        pRealName = new StringProperty("_realName", "");
        pEditPermissions = new StringProperty("_edit", "");
        pAccount = new StringProperty("_account", "administrator");
        pEmail = new StringProperty("_email", "");
        pLdap = new StringProperty("_ldapserver", "");
        pSecurityPrincipal = new StringProperty("_securityprincipal", "");
        StringProperty astringproperty[] = { pLogin, pPassword, pRealName, pEditPermissions, pAccount, pEmail, pLdap, pSecurityPrincipal };
        addProperties("COM.dragonflow.SiteView.User", astringproperty);
        litePermissions = new HashMapOrdered(true);
        litePermissions.add("_maximumMonitors", "5");
        litePermissions.add("_minimumFrequency", "900");
        litePermissions.add("_maximumReportsCount", "2");
        litePermissions.add("_maximumGroups", "2");
        litePermissions.add("_maximumAlerts", "4");
        litePermissions.add("_actionType", "Mailto,1");
        litePermissions.add("_actionType", "Page,1");
        litePermissions.add("_actionType", "SNMPTrap,1");
        litePermissions.add("_actionType", "Run,1");
        litePermissions.add("_actionType", "Page._pager,1");
        ldapCacheExpirationInSecs = TextUtils.readLong(SiteViewGroup.currentSiteView().getProperty("_ldapCacheExpirationInSecs"), 0);
        if (ldapCacheExpirationInSecs == -1L) {
            ldapCacheExpirationInSecs = 3600L;
        }
        System.out.println("***** Setting ldap cache exp to: " + ldapCacheExpirationInSecs);
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        if (siteviewgroup.getProperty("_ldapAuthenticationCaching").length() > 0) {
            doCaching = true;
        }
    }
}
