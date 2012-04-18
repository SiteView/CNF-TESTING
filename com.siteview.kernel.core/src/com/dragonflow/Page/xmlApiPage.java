/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.util.Enumeration;

import com.recursionsw.jgl.HashMap;

import com.dragonflow.Resource.SiteViewErrorCodes;
import com.dragonflow.Resource.SiteViewResource;
import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.User;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.TextUtils;
import com.dragonflow.XmlApi.XmlApiObject;
import com.dragonflow.XmlApi.XmlApiRequestXML;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class xmlApiPage extends com.dragonflow.Page.CGI {

    public xmlApiPage() {
    }

    public void printBody() {
        String login = request.getValue("_login");
        String password = request.getValue("_password");
        String account = request.getValue("account");
        HashMap hashmap = MasterConfig
                .getMasterConfig();
        String s3 = "";
        String requestXml = null;
        if (I18N.isI18N) {
            try {
                requestXml = I18N.toNullEncoding(new String(request
                        .getValue("xml").getBytes("Cp437"), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException unsupportedencodingexception) {
            }
            if (requestXml == null) {
                requestXml = request.getValue("xml");
            }
        } else {
            requestXml = request.getValue("xml");
        }
        java.lang.Boolean licenseExpired = new Boolean(request
                .getValue("licenseExpired"));
        java.lang.System.out.println("APIRequest: \r\n" + requestXml);
        XmlApiRequestXML xmlapirequestxml = new XmlApiRequestXML(requestXml);
        XmlApiObject xmlapirequest = (XmlApiObject) xmlapirequestxml.getAPIRequest();
        Enumeration enumeration = (Enumeration) xmlapirequest.iterator();
        XmlApiObject xmlapiobject1 = (XmlApiObject) enumeration.nextElement();
        String s7 = xmlapiobject1.getName();
        if (!licenseExpired.booleanValue() || s7.equals("updateLicense")) {
            if (account.length() == 0) {
                account = "administrator";
            }
            User user = User.getUserForAccount(account);
            if (user != null) {
                if (user.getProperty("_login").equalsIgnoreCase(login)
                        && user.getProperty("_password").equals(password)) {
                    if (user.getProperty("_disabled").length() > 0) {
                        if (TextUtils.getValue(hashmap,
                                "_loginDisabledMessage").length() > 0) {
                            s3 = TextUtils.getValue(
                                    hashmap, "_loginDisabledMessage");
                        }
                        if (s3.length() == 0) {
                            s3 = "This account has been disabled.";
                        }
                        xmlapirequest.setProperty("errortype", "operational",
                                false);
                        xmlapirequest
                                .setProperty(
                                        "errorcode",
                                        (new Long(
                                                SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DISABLED))
                                                .toString(), false);
                        xmlapirequest.setProperty("error", s3, false);
                    } else {
                        xmlapirequestxml.doRequests();
                    }
                } else {
                    if (TextUtils.getValue(hashmap,
                            "_loginIncorrectMessage").length() > 0) {
                        s3 = TextUtils.getValue(hashmap,
                                "_loginIncorrectMessage");
                    }
                    if (s3.length() == 0) {
                        s3 = "Login incorrect for account: " + account;
                    }
                    xmlapirequest.setProperty("errortype", "operational", false);
                    xmlapirequest
                            .setProperty(
                                    "errorcode",
                                    (new Long(
                                            SiteViewErrorCodes.ERR_OP_SS_LOGIN_INCORRECT))
                                            .toString(), false);
                    xmlapirequest.setProperty("error", s3, false);
                }
            } else {
                String s4 = SiteViewResource
                        .getFormattedString(
                                (new Long(
                                        SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DOES_NOT_EXIST))
                                        .toString(), new String[0]);
                xmlapirequest.setProperty("errortype", "operational", false);
                xmlapirequest
                        .setProperty(
                                "errorcode",
                                (new Long(
                                        SiteViewErrorCodes.ERR_OP_SS_ACCOUNT_DOES_NOT_EXIST))
                                        .toString(), false);
                xmlapirequest.setProperty("error", s4, false);
            }
        } else {
            String s5 = SiteViewResource
                    .getFormattedString(
                            (new Long(
                                    SiteViewErrorCodes.ERR_OP_SS_LICENSE_EXPIRED))
                                    .toString(), new String[0]);
            xmlapirequest.setProperty("errortype", "operational", false);
            xmlapirequest
                    .setProperty(
                            "errorcode",
                            (new Long(
                                    SiteViewErrorCodes.ERR_OP_SS_LICENSE_EXPIRED))
                                    .toString(), false);
            xmlapirequest.setProperty("error", s5, false);
        }
        xmlapirequestxml.setAPIRequest(xmlapirequest);
        String response = (String) xmlapirequestxml.getResponse(false);
        java.lang.System.out.println("APIResponse: \r\n" + response);
        outputStream.println(response);
    }
}
