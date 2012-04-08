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

import java.util.Enumeration;

import COM.dragonflow.XmlApi.XmlApiRequestXML;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class xmlApiPage extends COM.dragonflow.Page.CGI {

    public xmlApiPage() {
    }

    public void printBody() {
        String s = request.getValue("_login");
        String s1 = request.getValue("_password");
        String s2 = request.getValue("account");
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig
                .getMasterConfig();
        String s3 = "";
        String s6 = null;
        if (COM.dragonflow.Utils.I18N.isI18N) {
            try {
                s6 = COM.dragonflow.Utils.I18N.toNullEncoding(new String(request
                        .getValue("xml").getBytes("Cp437"), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
            }
            if (s6 == null) {
                s6 = request.getValue("xml");
            }
        } else {
            s6 = request.getValue("xml");
        }
        java.lang.Boolean boolean1 = new Boolean(request
                .getValue("licenseExpired"));
        java.lang.System.out.println("APIRequest: \r\n" + s6);
        COM.dragonflow.XmlApi.XmlApiRequestXML xmlapirequestxml = new XmlApiRequestXML(
                s6);
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject = (COM.dragonflow.XmlApi.XmlApiObject) xmlapirequestxml
                .getAPIRequest();
        Enumeration enumeration = xmlapiobject.elements();
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject1 = (COM.dragonflow.XmlApi.XmlApiObject) enumeration
                .nextElement();
        String s7 = xmlapiobject1.getName();
        if (!boolean1.booleanValue() || s7.equals("updateLicense")) {
            if (s2.length() == 0) {
                s2 = "administrator";
            }
            COM.dragonflow.SiteView.User user = COM.dragonflow.SiteView.User
                    .getUserForAccount(s2);
            if (user != null) {
                if (user.getProperty("_login").equalsIgnoreCase(s)
                        && user.getProperty("_password").equals(s1)) {
                    if (user.getProperty("_disabled").length() > 0) {
                        if (COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                                "_loginDisabledMessage").length() > 0) {
                            s3 = COM.dragonflow.Utils.TextUtils.getValue(
                                    hashmap, "_loginDisabledMessage");
                        }
                        if (s3.length() == 0) {
                            s3 = "This account has been disabled.";
                        }
                        xmlapiobject.setProperty("errortype", "operational",
                                false);
                        xmlapiobject
                                .setProperty(
                                        "errorcode",
                                        (new Long(
                                                COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DISABLED))
                                                .toString(), false);
                        xmlapiobject.setProperty("error", s3, false);
                    } else {
                        xmlapirequestxml.doRequests();
                    }
                } else {
                    if (COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                            "_loginIncorrectMessage").length() > 0) {
                        s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap,
                                "_loginIncorrectMessage");
                    }
                    if (s3.length() == 0) {
                        s3 = "Login incorrect for account: " + s2;
                    }
                    xmlapiobject.setProperty("errortype", "operational", false);
                    xmlapiobject
                            .setProperty(
                                    "errorcode",
                                    (new Long(
                                            COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_LOGIN_INCORRECT))
                                            .toString(), false);
                    xmlapiobject.setProperty("error", s3, false);
                }
            } else {
                String s4 = COM.dragonflow.Resource.SiteViewResource
                        .getFormattedString(
                                (new Long(
                                        COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DOES_NOT_EXIST))
                                        .toString(), new String[0]);
                xmlapiobject.setProperty("errortype", "operational", false);
                xmlapiobject
                        .setProperty(
                                "errorcode",
                                (new Long(
                                        COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DOES_NOT_EXIST))
                                        .toString(), false);
                xmlapiobject.setProperty("error", s4, false);
            }
        } else {
            String s5 = COM.dragonflow.Resource.SiteViewResource
                    .getFormattedString(
                            (new Long(
                                    COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_LICENSE_EXPIRED))
                                    .toString(), new String[0]);
            xmlapiobject.setProperty("errortype", "operational", false);
            xmlapiobject
                    .setProperty(
                            "errorcode",
                            (new Long(
                                    COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_LICENSE_EXPIRED))
                                    .toString(), false);
            xmlapiobject.setProperty("error", s5, false);
        }
        xmlapirequestxml.setAPIRequest(xmlapiobject);
        String s8 = (String) xmlapirequestxml
                .getResponse(false);
        java.lang.System.out.println("APIResponse: \r\n" + s8);
        outputStream.println(s8);
    }
}
