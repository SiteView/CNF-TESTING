/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Utils:
// SMTP, TextUtils, LUtils, FileUtils
public class MailUtils {

    public MailUtils() {
    }

    public static void sendEmailWarning(String s, String s1) {
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_autoEmail");
        if (s2.length() != 0 && s.length() > 0 && s1.length() > 0) {
            com.dragonflow.Utils.MailUtils.mail(hashmap, s2, s, s1, "", null, false);
        }
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2) {
        return com.dragonflow.Utils.MailUtils.mail(hashmap, s, s1, s2, "");
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2, String s3) {
        return com.dragonflow.Utils.MailUtils.mail(hashmap, s, s1, s2, s3, null, true);
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2, String s3, Array array) {
        return com.dragonflow.Utils.MailUtils.mail(hashmap, s, s1, s2, s3, array, true);
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2, String s3, Array array, boolean flag) {
        return com.dragonflow.Utils.MailUtils.mail(hashmap, s, s1, s2, s3, array, flag, "");
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2, String s3, Array array, boolean flag, String s4) {
        return com.dragonflow.Utils.MailUtils.mail(hashmap, s, s1, s2, s3, array, flag, s4, null);
    }

    public static String mail(HashMap hashmap, String s, String s1, String s2, String s3, Array array, boolean flag, String s4, String s5) {
        String s6 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailServer");
        if (s6.length() == 0) {
            return "missing mail server";
        }
        String s8 = com.dragonflow.Utils.MailUtils.mail1(hashmap, s6, s, s1, s2, s3, array, flag, s4, s5);
        if (s8.length() != 0) {
            String s7 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailServerBackup");
            if (s7.length() > 0) {
                com.dragonflow.Log.LogManager.log("Error", "Using backup mail server (" + s8 + ") for " + s1 + " To: " + s);
                s8 = com.dragonflow.Utils.MailUtils.mail1(hashmap, s7, s, s1, s2, s3, array, flag, s4, s5);
            }
        }
        return s8;
    }

    static String mail1(HashMap hashmap, String s, String s1, String s2, String s3, String s4, Array array, boolean flag, String s5, String s6) {
        String s7 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_fromAddress");
        String s8 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_smtpUser");
        String s9 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_smtpPassword");
        s1 = com.dragonflow.Utils.TextUtils.toEmailList(s1);
        if (s7.length() == 0) {
            String as[] = com.dragonflow.Utils.TextUtils.split(s1, ",");
            if (as.length > 0) {
                s7 = as[0];
            }
        }
        String s10 = com.dragonflow.Utils.LUtils.getLicenseSummary(hashmap, true);
        if (s10.length() == 0) {
            s10 = com.dragonflow.SiteView.Platform.productName + " from " + com.dragonflow.SiteView.Platform.companyName;
        }
        String s11 = "\n\n-----------------------------------------------------\n" + s10 + "\n" + "\n" + com.dragonflow.SiteView.Platform.salesFooter + "-----------------------------------------------------\n\n";
        if (s7.indexOf("siteseer@") != -1) {
            s11 = "\n\n-----------------------------------------------------\nSiteSeer from " + com.dragonflow.SiteView.Platform.companyName + "\n" + "\n" + com.dragonflow.SiteView.Platform.salesFooter
                    + "-----------------------------------------------------\n\n";
            if (s5.length() > 0) {
                com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteviewgroup.getElement(s5);
                if (monitorgroup != null) {
                    String s14 = monitorgroup.getProperty("_customer");
                    if (s14.indexOf("trial") != -1) {
                        s11 = "\n\n-----------------------------------------------------\nSiteSeer from " + com.dragonflow.SiteView.Platform.companyName + "\n" + "\n" + "$expired$\n" + "\n" + "Call " + com.dragonflow.SiteView.Platform.salesPhone
                                + " or e-mail " + com.dragonflow.SiteView.Platform.salesEmail + "\n" + "to subscribe to the SiteSeer service.\n" + "\n" + com.dragonflow.SiteView.Platform.salesFooter
                                + "-----------------------------------------------------\n\n";
                    }
                    String s16 = monitorgroup.getProperty("_partner");
                    if (s16.length() > 0) {
                        com.dragonflow.SiteView.MonitorGroup monitorgroup1 = (com.dragonflow.SiteView.MonitorGroup) siteviewgroup.getElement(s16);
                        if (monitorgroup1 != null) {
                            String s18 = monitorgroup1.getProperty("_partnerMailFooter");
                            if (s14.indexOf("trial") != -1) {
                                s18 = monitorgroup1.getProperty("_partnerMailTrialFooter");
                            }
                            if (s18.length() != 0) {
                                s11 = s18.replace('^', '\n');
                            }
                            String s20 = monitorgroup1.getProperty("_partnerFromAddress");
                            if (s20.length() != 0) {
                                s7 = s20;
                            }
                        }
                    }
                    if (s14.indexOf("trial") != -1) {
                        String s17 = monitorgroup.getProperty("_startDate");
                        java.util.Date date = com.dragonflow.Utils.TextUtils.stringToDate(s17);
                        java.util.Date date1 = com.dragonflow.SiteView.Platform.makeDate();
                        long l1 = 0x15180L;
                        long l2 = (date1.getTime() - date.getTime()) / (l1 * 1000L);
                        String s21 = "   Your SiteSeer Trial Period Has Expired.\n\n";
                        if (l2 < 10L) {
                            String s22 = String.valueOf(10L - l2);
                            s21 = "   Your SiteSeer Trial Period Expires In " + s22 + " Days.";
                        }
                        s11 = com.dragonflow.Utils.TextUtils.replaceString(s11, "$expired$", s21);
                    }
                }
            }
        }
        String s12 = "";
        if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_hideServerInSubject").length() == 0) {
            s12 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_webserverAddress");
            int i = s12.indexOf(":");
            if (i >= 0) {
                s12 = s12.substring(0, i);
            }
        }
        if (s12.length() != 0) {
            s2 = s2 + " (" + s12 + ")";
        }
        if (flag) {
            s3 = s3 + s11;
        }
        String s13 = "";
        try {
            String s15 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailSubjectMax");
            int j = com.dragonflow.Utils.TextUtils.toInt(s15);
            if (j == 0) {
                j = 160;
            }
            if (s2.length() > j) {
                s2 = s2.substring(0, j) + "...";
            }
            int k = com.dragonflow.Utils.SMTP.defaultTimeout;
            String s19 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailTimeout");
            if (s19.length() > 0) {
                int l = com.dragonflow.Utils.TextUtils.toInt(s19) * 1000;
                if (l > com.dragonflow.Utils.SMTP.defaultTimeout) {
                    k = l;
                }
            }
            com.dragonflow.Utils.SMTP smtp = new SMTP(s, k, array);
            if (s8.length() > 0) {
                smtp.sendSecure(s7, s1, s4, s2, s3, s6, s8, s9);
            } else {
                smtp.send(s7, s1, s4, s2, s3, s6);
            }
            smtp.close();
        } catch (java.net.UnknownHostException unknownhostexception) {
            s13 = com.dragonflow.SiteView.Monitor.lookupStatus(com.dragonflow.SiteView.Monitor.kURLBadHostNameError);
        } catch (java.net.SocketException socketexception) {
            if (com.dragonflow.SiteView.Platform.noRoute(socketexception)) {
                s13 = com.dragonflow.SiteView.Monitor.lookupStatus(com.dragonflow.SiteView.Monitor.kURLNoRouteToHostError);
            } else {
                s13 = com.dragonflow.SiteView.Monitor.lookupStatus(com.dragonflow.SiteView.Monitor.kURLNoConnectionError);
            }
        } catch (java.io.InterruptedIOException interruptedioexception) {
            s13 = com.dragonflow.SiteView.Monitor.lookupStatus(com.dragonflow.SiteView.Monitor.kURLTimeoutError);
        } catch (java.lang.Exception exception) {
            s13 = exception.toString();
            if (s13.indexOf("NullPointerException") >= 0) {
                s13 = s13 + "\n" + com.dragonflow.Utils.FileUtils.stackTraceText(exception);
            }
        }
        return s13;
    }
}
