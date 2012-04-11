/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MailMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MailMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.supportPage;
import COM.dragonflow.Properties.FileProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Base64Decoder;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.Imap;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.pop3;
import COM.dragonflow.Utils.popStatus;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// URLMonitor

public class MailMonitor extends AtomicMonitor {

    static long internalMails = 0L;

    static long internalMailBytes = 0L;

    static long internalMailDuration = 0L;

    static long internalMailErrors = 0L;

    static StringProperty pSMTPServer;

    static StringProperty pPopServer;

    static StringProperty pPopAccount;

    static StringProperty pPopPassword;

    static StringProperty pPopUser;

    static StringProperty pCheckTimeout;

    static StringProperty pCheckEvery;

    static StringProperty pContentMatch;

    static StringProperty pReceiveOnly;

    static StringProperty pUseImap;

    static FileProperty pAttachment;

    static StringProperty pSMTPUser;

    static StringProperty pSMTPPassword;

    static StringProperty pRoundTripTime;

    static StringProperty pSMTPTime;

    static StringProperty pReceiveTime;

    static StringProperty pStatus;

    static StringProperty pMatchValue;

    static HashMap popLocks = new HashMap();

    public MailMonitor() {
    }

    public String getHostname() {
        return getProperty(pPopServer);
    }

    public String getTestURL() {
        String s = URLEncoder.encode(getProperty(pPopAccount));
        String s1 = URLEncoder.encode(TextUtils.obscure(getProperty(pPopPassword)));
        String s2 = URLEncoder.encode(getProperty(pPopServer));
        String s3 = URLEncoder.encode(getProperty(pPopUser));
        String s4 = URLEncoder.encode(getProperty(pSMTPServer));
        String s5 = getProperty(pUseImap);
        String s6 = getProperty(pReceiveOnly);
        String s7 = HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pContentMatch)));
        String s8 = "/SiteView/cgi/go.exe/SiteView?page=mail&to=" + s3 + "&smtpServer=" + s4 + "&popServer=" + s2 + "&popAccount=" + s + "&popPassword=" + s1 + "&receiveOnly=" + s6 + "&useImap=" + s5 + "&contentMatch=" + s7;
        return s8;
    }

    public static Object getPopLock(String s) {
        Object obj = popLocks.get(s);
        if (obj == null) {
            obj = new Object();
            popLocks.put(s, obj);
        }
        return obj;
    }

    protected boolean update() {
        String s = getProperty(pPopAccount);
        String s1 = getProperty(pPopPassword);
        String s2 = getProperty(pPopServer);
        String s3 = getProperty(pPopUser);
        String s4 = getProperty(pSMTPServer);
        String s5 = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
        String s6 = pAttachment.getName(this);
        String s7 = getProperty(pSMTPUser);
        String s8 = getProperty(pSMTPPassword);
        String s9 = "";
        boolean flag = getProperty(pReceiveOnly).equals("receive");
        boolean flag1 = getProperty(pReceiveOnly).equals("send");
        boolean flag2 = getProperty(pUseImap).length() > 0;
        String s10 = "from " + getProperty(pFullID) + "/" + getProperty(pSample);
        Object aobj[] = roundTripMail(this, null, s3, s4, s2, s, s1, s10, getPropertyAsInteger(pCheckEvery), getPropertyAsInteger(pCheckTimeout), false, flag, flag1, flag2, s5, s6, s7, s8);
        int i = ((Long) aobj[0]).intValue();
        String s11 = (String) aobj[1];
        String s12 = (String) aobj[2];
        String s13 = (String) aobj[3];
        int j = ((Long) aobj[4]).intValue();
        int k = ((Long) aobj[5]).intValue();
        internalMails ++;
        internalMailBytes += 4000L;
        internalMailDuration += i;
        URLMonitor.internalURLs ++;
        URLMonitor.internalURLBytes += 4000L;
        URLMonitor.internalURLDuration += i;
        if (stillActive()) {
            synchronized (this) {
                if (s11.length() > 0 || s12.length() > 0) {
                    internalMailErrors ++;
                    URLMonitor.internalURLErrors ++;
                    if (s11.length() > 0) {
                        setProperty(pStatus, "send failed: " + s11);
                    } else {
                        setProperty(pStatus, "retrieve failed: " + s12);
                    }
                    setProperty(pStateString, getProperty(pStatus));
                    setProperty(pRoundTripTime, "n/a");
                    setProperty(pNoData, "n/a");
                } else {
                    Array array = new Array();
                    int l = TextUtils.matchExpression(s13, s5, array, new StringBuffer());
                    if (l == Monitor.kURLok && array.size() > 0) {
                        s9 = (String) array.at(0);
                    }
                    setProperty(pStatus, "ok");
                    setProperty(pRoundTripTime, i);
                    setProperty(pMatchValue, s9);
                    if (!flag && !flag1) {
                        setProperty(pSMTPTime, j);
                        setProperty(pReceiveTime, k);
                    } else {
                        unsetProperty(pSMTPTime);
                        unsetProperty(pReceiveTime);
                    }
                    String s14;
                    if (flag) {
                        s14 = "successfully retrieved in ";
                        s14 = s14 + pRoundTripTime.valueString(getProperty(pRoundTripTime));
                    } else if (flag1) {
                        s14 = "successfully sent in ";
                        s14 = s14 + pRoundTripTime.valueString(getProperty(pRoundTripTime));
                    } else {
                        s14 = "successfully looped in ";
                        s14 = s14 + pRoundTripTime.valueString(getProperty(pRoundTripTime));
                        s14 = s14 + ", send time ";
                        s14 = s14 + pSMTPTime.valueString(getProperty(pSMTPTime));
                        s14 = s14 + ", receive time ";
                        s14 = s14 + pReceiveTime.valueString(getProperty(pReceiveTime));
                    }
                    if (!s9.equals("")) {
                        s14 = s14 + ", matched " + s9;
                    }
                    setProperty(pStateString, I18N.StringToUnicode(s14, I18N.nullEncoding()));
                }
            }
        }
        return true;
    }

    static void updateProgress(AtomicMonitor atomicmonitor, PrintWriter printwriter, String s) {
        if (atomicmonitor != null) {
            atomicmonitor.progressString += s + "\n";
        }
        if (printwriter != null) {
            printwriter.println(s + "\n");
            printwriter.flush();
        }
    }

    public static Object[] roundTripMail(MailMonitor mailmonitor, PrintWriter printwriter, String s, String s1, String s2, String s3, String s4, String s5, int i, int j, boolean flag, boolean flag1, boolean flag2, boolean flag3, String s6, String s7,
            String s8, String s9) {
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s10 = "";
        String s11 = "SiteView Mail Monitor test message";
        Date date = Platform.makeDate();
        String s12 = TextUtils.prettyDate(date);
        StringBuffer stringbuffer = new StringBuffer("");
        String s13 = "";
        if (s7.length() > 0) {
            String s14 = "----SiteView " + Platform.timeMillis();
            s11 = s11 + " [attachments]" + s14;
            stringbuffer.append("This is a multi-part message in MIME format.\n");
            stringbuffer.append("\n");
            stringbuffer.append("--" + s14 + "\n");
            stringbuffer.append("Content-Type: text/plain; charset=us-ascii\n");
            stringbuffer.append("Content-Transfer-Encoding: 7bit\n");
            stringbuffer.append("\n");
            stringbuffer.append("SiteView Mail Monitor test message sent at " + s12 + "\n" + s5);
            stringbuffer.append("\n");
            String s15 = "";
            String s16 = "";
            int k = s7.lastIndexOf(File.separator);
            if (k < 0) {
                s15 = "";
                s16 = s7;
            } else {
                s15 = s7.substring(0, k + 1);
                s16 = s7.substring(k + 1);
            }
            stringbuffer.append(supportPage.mimeAttachFile(s16, s15, s14, -1L));
            stringbuffer.append("\n--" + s14 + "--\n");
        } else {
            stringbuffer.append("SiteView Mail Monitor test message sent at " + s12 + "\n" + s5);
        }
        long l = date.getTime();
        boolean flag4 = TextUtils.getValue(hashmap, "_disablePOP3TopCommand").length() > 0;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;
        if (flag1) {
            updateProgress(mailmonitor, printwriter, "Only receiving");
        } else {
            if (flag2) {
                updateProgress(mailmonitor, printwriter, "Only sending");
            }
            s6 = I18N.UnicodeToString(s5, I18N.nullEncoding());
            HashMap hashmap1 = new HashMap();
            hashmap1.put("_mailServer", s1);
            hashmap1.put("_mailServerBackup", "");
            hashmap1.put("_webserverAddress", TextUtils.getValue(hashmap, "_webserverAddress"));
            hashmap1.put("_fromAddress", TextUtils.getValue(hashmap, "_fromAddress"));
            hashmap1.put("_license", TextUtils.getValue(hashmap, "_license"));
            hashmap1.put("_smtpUser", s8);
            hashmap1.put("_smtpPassword", s9);
            boolean flag5 = true;
            if (s7.length() > 0) {
                hashmap1.put("_hideServerInSubject", "true");
                flag5 = false;
            }
            if (s.length() == 0) {
                s = s3 + "@" + s2;
            }
            updateProgress(mailmonitor, printwriter, "Sending message to " + s + " at mail server " + s1);
            Array array = new Array();
            String s18 = "";
            s10 = MailUtils.mail(hashmap1, s, s11, stringbuffer.toString(), "", array, flag5, s18, null);
            long l4 = Platform.timeMillis();
            l1 = l4 - l;
            if (flag) {
                for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); updateProgress(mailmonitor, printwriter, (String) enumeration.nextElement())) {
                }
            }
        }
        long l5 = Platform.timeMillis();
        String s17 = "";
        String s19 = "";
        boolean flag6 = false;
        long l6 = i * 1000;
        long l7 = j * 1000;
        if (s10.length() == 0 && !flag2) {
            if (!flag1) {
                updateProgress(mailmonitor, printwriter, "Send completed");
            }
            if (!flag3) {
                updateProgress(mailmonitor, printwriter, "Retrieving message from POP account " + s3 + " at POP server " + s2);
                pop3 pop3_1;
                if (flag) {
                    pop3_1 = new pop3(s2, s3, s4, mailmonitor, printwriter);
                } else {
                    pop3_1 = new pop3(s2, s3, s4, null, null);
                }
                do {
                    if (flag6) {
                        break;
                    }
                    long l9 = l7 - (Platform.timeMillis() - l5);
                    if (l9 <= 0L) {
                        s17 = "timeout";
                        break;
                    }
                    synchronized (getPopLock(s2 + ":" + s3)) {
                        String s20 = new String(s6);
                        updateProgress(mailmonitor, printwriter, "Checking mailbox...");
                        String as1[] = readMessagesPop(s2, s3, l9, s11, s20, pop3_1, flag4, mailmonitor, printwriter);
                        flag6 = as1[0] != null;
                        s17 = as1[1];
                        s19 = as1[2];
                    }
                    if (flag6) {
                        break;
                    }
                    if (s17.length() > 0) {
                        updateProgress(mailmonitor, printwriter, "Error retrieving message: " + s17);
                    } else {
                        updateProgress(mailmonitor, printwriter, "Message not found");
                    }
                    Platform.sleep(Math.min(l6, l9));
                } while (true);
            } else {
                updateProgress(mailmonitor, printwriter, "Retrieving message from IMAP account " + s3 + " at IMAP server " + s2);
                Imap imap;
                if (flag) {
                    imap = new Imap(s2, s3, s4, mailmonitor, printwriter);
                } else {
                    imap = new Imap(s2, s3, s4, null, null);
                }
                while (!flag6) {
                    long l10 = l7 - (Platform.timeMillis() - l5);
                    if (l10 <= 0L) {
                        s17 = "timeout";
                        break;
                    }
                    synchronized (getPopLock(s2 + ":" + s3)) {
                        updateProgress(mailmonitor, printwriter, "Checking mailbox...");
                        String as[] = readMessagesImap(s2, s3, l10, s11, s6, imap, mailmonitor, printwriter);
                        flag6 = as[0] != null;
                        s17 = as[1];
                        s19 = as[2];
                    }
                    if (flag6) {
                        break;
                    }
                    if (s17.length() > 0) {
                        updateProgress(mailmonitor, printwriter, "Error retrieving message: " + s17);
                    } else {
                        updateProgress(mailmonitor, printwriter, "Message not found");
                    }
                    Platform.sleep(Math.min(l6, l10));
                }
            }
        }
        long l8 = Platform.timeMillis();
        l2 = l8 - l5;
        Object aobj[] = new Object[6];
        aobj[0] = new Long(l8 - l);
        aobj[1] = s10;
        aobj[2] = s17;
        aobj[3] = s19;
        aobj[4] = new Long(l1);
        aobj[5] = new Long(l2);
        return aobj;
    }

    static String[] readMessagesImap(String s, String s1, long l, String s2, String s3, Imap imap, AtomicMonitor atomicmonitor, PrintWriter printwriter) {
        String s4 = "";
        boolean flag = false;
        String s5 = "";
        try {
            long l1 = Platform.timeMillis();
            if (l <= 0L) {
                s4 = "timeout";
            } else {
                popStatus popstatus = imap.connect();
                if (!popstatus.OK()) {
                    s4 = "could not connect to imap server " + s;
                } else {
                    imap.socketTimeout = (int) l;
                    popStatus popstatus1 = imap.login();
                    if (!popstatus1.OK()) {
                        s4 = "could not login to IMAP server " + s + " as user " + s1;
                    } else {
                        popStatus popstatus2 = imap.select("INBOX");
                        if (!popstatus2.OK()) {
                            s4 = "could not select mailbox from IMAP server " + s;
                        } else {
                            String as1[] = popstatus2.Responses();
                            int j = 0;
                            int i = -1;
                            do {
                                if (as1[j].length() <= 0) {
                                    break;
                                }
                                if (as1[j].indexOf("EXIS") != -1) {
                                    i = getIntFromString(as1[j]);
                                    break;
                                }
                                j ++;
                            } while (true);
                            if (i == -1 || i == 0) {
                                s4 = "IMAP server " + s + " has an empty inbox.";
                            } else {
                                for (int k = i; k > 0; k --) {
                                    long l2 = Platform.timeMillis();
                                    l -= l2 - l1;
                                    l1 = l2;
                                    if (l < 0L) {
                                        s4 = "timeout";
                                        break;
                                    }
                                    imap.socketTimeout = (int) l;
                                    String s6 = "FETCH " + k + " FLAGS";
                                    Vector vector = new Vector();
                                    boolean flag1 = false;
                                    popStatus popstatus3 = imap.roundTrip(s6, vector);
                                    if (popstatus3.OK()) {
                                        int i1 = vector.size();
                                        int j1 = 0;
                                        do {
                                            if (j1 >= i1) {
                                                break;
                                            }
                                            String s7 = (String) vector.elementAt(j1);
                                            if (s7.indexOf("\\Seen") >= 0) {
                                                flag1 = true;
                                                break;
                                            }
                                            j1 ++;
                                        } while (true);
                                    }
                                    popstatus3 = imap.fetch(k);
                                    String as2[] = popstatus3.Responses();
                                    if (!popstatus3.OK()) {
                                        continue;
                                    }
                                    if (sameMessageSimple(s3, as2)) {
                                        flag = true;
                                        s5 = as2[0];
                                        updateProgress(atomicmonitor, printwriter, "Retrieved message");
                                        popstatus3 = imap.dele(k);
                                        if (!popstatus3.OK()) {
                                            s4 = "could not tag message: " + i;
                                            break;
                                        }
                                        popstatus3 = imap.expunge();
                                        if (!popstatus3.OK()) {
                                            s4 = "could not delete message: " + i;
                                        }
                                        break;
                                    }
                                    if (flag1) {
                                        continue;
                                    }
                                    s6 = "STORE " + k + " -FLAGS (\\Seen)";
                                    popstatus3 = imap.roundTrip(s6, vector);
                                    if (popstatus3.OK()) {
                                        continue;
                                    }
                                    s4 = "could not mark message as unread: " + k;
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            s4 = "-Err could not receive message, " + exception;
        } finally {
            try {
                imap.quit();
            } catch (Exception exception2) {
                s4 = "-Err could not logout: " + exception2;
            }
            imap.close();
        }
        String as[] = new String[3];
        if (flag) {
            as[0] = "gotit";
            as[2] = s5;
        }
        as[1] = s4;
        return as;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param l
     * @param s2
     * @param s3
     * @param pop3_1
     * @param flag
     * @param atomicmonitor
     * @param printwriter
     * @return
     */
    static String[] readMessagesPop(String s, String s1, long l, String s2, String s3, pop3 pop3_1, boolean flag, AtomicMonitor atomicmonitor, PrintWriter printwriter) {
        String s4 = "";
        String s5 = "";
        boolean flag1 = false;

        try {
            long l1 = Platform.timeMillis();
            if (l <= 0L) {
                s4 = "timeout";
            } else {
                popStatus popstatus = pop3_1.connect();
                if (!popstatus.OK()) {
                    s4 = "could not connect to POP3 server " + s;
                } else {
                    pop3_1.socketTimeout = (int) l;
                    popStatus popstatus1 = pop3_1.login();
                    if (!popstatus1.OK()) {
                        s4 = "could not login to POP3 server " + s + " as user " + s1;
                    } else {
                        popStatus popstatus2 = pop3_1.list();
                        if (!popstatus2.OK()) {
                            s4 = "could not retrieve messages from POP3 server " + s;
                        } else {
                            String as1[] = popstatus2.Responses();
                            for (int i = as1.length - 1; i >= 0; i --) {
                                if (as1[i].charAt(0) == '.') {
                                    continue;
                                }
                                int j = TextUtils.readInteger(as1[i], 0);
                                if (j <= 0) {
                                    continue;
                                }
                                long l2 = Platform.timeMillis();
                                l -= l2 - l1;
                                l1 = l2;
                                if (l < 0L) {
                                    s4 = "timeout";
                                    break;
                                }
                                pop3_1.socketTimeout = (int) l;
                                popstatus2._OK = false;
                                if (!flag) {
                                    popstatus2 = pop3_1.top(j, 50);
                                }
                                if (!popstatus2.OK()) {
                                    popstatus2 = pop3_1.retr(j);
                                }
                                if (!popstatus2.OK()) {
                                    continue;
                                }
                                String as2[] = popstatus2.Responses();
                                StringBuffer stringbuffer = new StringBuffer();
                                for (int k = 0; k < as2.length; k ++) {
                                    if (k < as2.length - 1 && as2[k].endsWith("=") && as2[k + 1].startsWith("=")) {
                                        as2[k] = as2[k].substring(0, as2[k].length() - 1);
                                    }
                                    stringbuffer.append(as2[k]);
                                }

                                String s6 = stringbuffer.toString();
                                String s7 = parseSubject(s6);
                                String s8 = getMimeEncoding(s6);
                                s6 = decode(s6, s8);
                                String s9 = s8.length() <= 0 ? new String(s3) : I18N.StringToUnicode(s3, s8);
                                if (!sameMessage(s9, s7 + s6)) {
                                    continue;
                                }
                                flag1 = true;
                                String as3[] = popstatus2.Responses();
                                for (int i1 = 0; i1 < as3.length; i1 ++) {
                                    s5 = s5 + as3[i1] + "\n";
                                }

                                updateProgress(atomicmonitor, printwriter, "Retrieved message");
                                popstatus2 = pop3_1.dele(j);
                                if (!popstatus2.OK()) {
                                    s4 = "could not delete message: " + j;
                                }
                                break;
                            }

                        }
                    }
                }
            }
            try {
                pop3_1.quit();
            } catch (Exception exception) {
                /* empty */
            } finally {
                pop3_1.close();
            }
        } catch (Exception exception1) {
            s4 = "could not receive message, " + exception1;
            try {
                pop3_1.quit();
            } catch (Exception exception2) {
                /* empty */
            } finally {
                pop3_1.close();
            }
        }

        try {
            pop3_1.quit();
        } catch (Exception exception4) {
            /* empty */
        } finally {
            pop3_1.close();
        }

        String as[] = new String[3];
        if (flag1) {
            as[0] = "gotit";
        }
        as[1] = s4;
        as[2] = s5;
        return as;
    }

    /**
     * CAUTION: Decompled by hand.
     * 
     * @param s
     * @return
     */
    static String getMimeEncoding(String s) {
        String s1 = s;
        String s2 = "";
        int i;
        while (true) {
            if (s1.indexOf("_NextPart_") >= 0) {
                i = s1.indexOf("_NextPart_");
                int j = s1.indexOf("quoted-printable");
                if (j > 0) {
                    if (s1.substring(i + "_NextPart_".length(), j).indexOf("_NextPart_") >= 0) {
                        s1 = s1.substring(i + "_NextPart_".length());
                        if (s1.indexOf("charset=") >= 0) {
                            String s3 = s1.substring(s1.indexOf("charset=") + "charset=".length());
                            int k = s3.indexOf("\"") + 1;
                            int l = s3.substring(k).indexOf("\"");
                            if (k >= 0 && l >= 0 && l > k) {
                                s2 = s3.substring(k, k + l);
                            }
                            s = s1.substring(i);
                        }
                    } else {
                        s = s1.substring(i);
                        break;
                    }
                }
            }
        }
        return s2;
    }

    static String decode(String s, String s1) {
        String s2 = "";
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i ++) {
            char c = s.charAt(i);
            if (c == '=') {
                if (i < s.length() - 3) {
                    char c1 = Character.toUpperCase(s.charAt(i + 1));
                    char c2 = Character.toUpperCase(s.charAt(i + 2));
                    if ((c1 >= '0' && c1 <= '9' || c1 >= 'A' && c1 <= 'F') && (c2 >= '0' && c2 <= '9' || c2 >= 'A' && c2 <= 'F')) {
                        int l = Character.digit(c1, 16) * 16;
                        int i1 = Character.digit(c2, 16);
                        stringbuffer.append((char) (l + i1));
                    } else {
                        stringbuffer.append(s.charAt(i));
                        stringbuffer.append(s.charAt(i + 1));
                        stringbuffer.append(s.charAt(i + 2));
                    }
                    i += 2;
                } else {
                    stringbuffer.append(c);
                }
            } else {
                stringbuffer.append(c);
            }
        }

        try {
            byte abyte0[] = new byte[stringbuffer.length()];
            for (int j = 0; j < stringbuffer.length(); j ++) {
                abyte0[j] = (byte) stringbuffer.charAt(j);
            }

            String s3 = s1 == null || s1.length() <= 0 ? new String(abyte0) : new String(abyte0, s1);
            abyte0 = s3.getBytes();
            StringBuffer stringbuffer1 = new StringBuffer();
            for (int k = 0; k < abyte0.length; k ++) {
                stringbuffer1.append((char) (abyte0[k] & 0xff));
            }

            s2 = stringbuffer1.toString();
        } catch (Exception exception) {
            System.out.println("!!!!!!!!!MailMonitor.decode: bad charset !!!!!!!!!!");
        }
        return s2;
    }

    private static String parseSubject(String s) {
        String s1 = "";
        boolean flag = false;
        if (s.indexOf("Subject: ") < 0) {
            return s1;
        }
        String s2 = s.substring(s.indexOf("Subject: ") + "Subject: ".length());
        if (s2.indexOf("=?") < 0) {
            if (s2.indexOf(":") >= 0) {
                return s2.substring(0, s2.indexOf(":"));
            } else {
                return s2;
            }
        }
        s2 = s2.substring(0, s2.indexOf("?=") + 3);
        if (flag) {
            System.out.println("MimeMessage: parsing Subject: " + s2);
        }
        int i = s2.indexOf("=?") + 2;
        int j = s2.indexOf("?", i);
        String s3 = s2.substring(i, j);
        if (flag) {
            System.out.println("MimeMessage: got encoding of: " + s3);
        }
        i = j + 1;
        j = s2.indexOf("?", i);
        String s4 = s2.substring(i, j);
        if (flag) {
            System.out.println("MimeMessage: got packing of: " + s4);
        }
        i = j + 1;
        j = s2.indexOf("?=", i);
        String s5 = s2.substring(i, j);
        if (flag) {
            System.out.println("MimeMessage: got text of: " + s5);
        }
        if (s4.toUpperCase().compareTo("B") == 0) {
            Base64Decoder base64decoder = new Base64Decoder(s5);
            try {
                s5 = base64decoder.processString();
                if (flag) {
                    I18N.dmp("MimeMessage: decoding text: ", s5);
                }
                if (flag) {
                    System.out.println("MimeMessage: decoding subject: " + s2);
                }
                char ac[] = s5.toCharArray();
                byte abyte0[] = new byte[ac.length];
                for (int k = 0; k < ac.length; k ++) {
                    abyte0[k] = (byte) (ac[k] & 0xff);
                }

                s5 = new String(abyte0, s3);
                byte abyte1[] = s5.getBytes();
                StringBuffer stringbuffer = new StringBuffer();
                for (int l = 0; l < abyte1.length; l ++) {
                    stringbuffer.append((char) (abyte1[l] & 0xff));
                }

                s5 = stringbuffer.toString();
                if (flag) {
                    I18N.dmp("MimeMessage Subject decoded to: ", s5);
                }
            } catch (Exception exception) {
                System.out.println("MailMonitor/parseSubject() Caught exception: " + exception);
            }
            s1 = s5;
        }
        return s1;
    }

    static boolean sameMessageSimple(String s, String as[]) {
        for (int j = 0; j < as.length; j ++) {
            Array array = new Array();
            int i = TextUtils.matchExpression(as[j], s, array, new StringBuffer());
            if (i == Monitor.kURLok) {
                return true;
            }
        }

        return false;
    }

    static boolean sameMessage(String s, String s1) {
        Array array = new Array();
        array = new Array();
        int i;
        if (s1.indexOf(s) >= 0) {
            i = Monitor.kURLok;
        } else {
            i = TextUtils.matchExpression(s1, s, array, new StringBuffer());
        }
        return i == Monitor.kURLok;
    }

    static int getIntFromString(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i ++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                stringbuffer.append(c);
            }
        }

        return TextUtils.readInteger(stringbuffer.toString(), 0);
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        array.add(pRoundTripTime);
        if (!getProperty(pReceiveOnly).equals("true")) {
            array.add(pSMTPTime);
            array.add(pReceiveTime);
        }
        if (TextUtils.isNumber(getProperty(pMatchValue)) && getProperty(pMatchValue).length() > 0) {
            array.add(pMatchValue);
        }
        return array.elements();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pRoundTripTime);
        array.add(pStatus);
        array.add(pMatchValue);
        array.add(pSMTPTime);
        array.add(pReceiveTime);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pSMTPServer) {
            if (s.length() == 0 && getProperty(pReceiveOnly).equals("receive")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if (stringproperty == pPopServer) {
            if (s.length() == 0 && !getProperty(pReceiveOnly).equals("send")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if (stringproperty == pPopAccount) {
            if (s.length() == 0 && !getProperty(pReceiveOnly).equals("send")) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == pPopUser) {
            if (s.length() > 0 && !getProperty(pReceiveOnly).equals("send") && (TextUtils.hasSpaces(s) || s.indexOf("@") == -1)) {
                hashmap.put(stringproperty, "user account must be of the form username@hostname");
            }
            return s;
        }
        if (stringproperty == pCheckTimeout) {
            if (TextUtils.digits(s) != s.length()) {
                hashmap.put(stringproperty, "time out must be a number");
            } else if (TextUtils.toInt(s) < 30) {
                hashmap.put(stringproperty, "time out must be greater than 30 seconds");
            }
            return s;
        }
        if (stringproperty == pCheckEvery) {
            if (TextUtils.digits(s) != s.length()) {
                hashmap.put(stringproperty, "must be a number");
            } else if (TextUtils.toInt(s) < 5) {
                hashmap.put(stringproperty, "cannot check more frequently than every 5 seconds");
            }
            return s;
        }
        if (stringproperty == pContentMatch) {
            if (getProperty(pReceiveOnly).equals("")) {
                if (s.length() > 0) {
                    hashmap.put(stringproperty, "This field needs to be clear during send & receive");
                }
            } else if (getProperty(pReceiveOnly).equals("receeve") && s.length() == 0) {
                hashmap.put(stringproperty, "Need to match on a value when only receiving");
            }
            return s;
        }
        if (stringproperty == pAttachment) {
            pAttachment.verify(s, hashmap, true, false);
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        if (scalarproperty == pReceiveOnly) {
            Vector vector = new Vector();
            vector.addElement("");
            vector.addElement("Send & Receive");
            vector.addElement("receive");
            vector.addElement("Receive Only");
            vector.addElement("send");
            vector.addElement("Send Only");
            return vector;
        }
        if (scalarproperty == pUseImap) {
            Vector vector1 = new Vector();
            vector1.addElement("");
            vector1.addElement("POP3");
            vector1.addElement("true");
            vector1.addElement("IMAP4");
            return vector1;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    static {
        pReceiveOnly = new ScalarProperty("_receiveOnly", "");
        pReceiveOnly.setDisplayText("Action", "the action the monitor will take with the e-mail message. For example, send and receive or receive only.");
        pReceiveOnly.setParameterOptions(true, 1, false);
        ((ScalarProperty) pReceiveOnly).allowOther = false;
        pSMTPServer = new StringProperty("_smtpServer", "");
        pSMTPServer.setDisplayText("Sending Mail Server (SMTP)", "hostname of mail server where test message is sent - for example, mail." + Platform.exampleDomain + "");
        pSMTPServer.setParameterOptions(true, 2, false);
        pPopUser = new StringProperty("_popUser", "");
        pPopUser.setDisplayText("Send To Address", "address where test message is sent to - for example, " + Platform.supportEmail);
        pPopUser.setParameterOptions(true, 3, false);
        pUseImap = new ScalarProperty("_useIMAP", "");
        pUseImap.setDisplayText("Receiving Protocol", "the protocol used to receive the message");
        pUseImap.setParameterOptions(true, 4, false);
        ((ScalarProperty) pUseImap).allowOther = false;
        pPopServer = new StringProperty("_popServer", "");
        pPopServer.setDisplayText("Receiving Mail Server ", "the mail server where the test message is received - for example, mail." + Platform.exampleDomain + "");
        pPopServer.setParameterOptions(true, 5, false);
        pPopAccount = new StringProperty("_popAccount", "");
        pPopAccount.setDisplayText("Receiving Mail Server User Name", "the account name for the receiving mailbox - for example, support");
        pPopAccount.setParameterOptions(true, 6, false);
        pPopPassword = new StringProperty("_popPassword", "");
        pPopPassword.setDisplayText("Receiving Mail Server Password", "the password for the receiving mailbox");
        pPopPassword.setParameterOptions(true, 7, false);
        pPopPassword.isPassword = true;
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Receive Content Match", "for Receive Only, the text to match in the contents of the received message. (example: Subject: MySubject)");
        pContentMatch.setParameterOptions(true, 8, true);
        pCheckTimeout = new NumericProperty("_checkTimeout", "300", "seconds");
        pCheckTimeout.setDisplayText("Timeout", "amount of time (in seconds) to keep checking for the message to be received");
        pCheckTimeout.setParameterOptions(true, 9, true);
        pCheckEvery = new NumericProperty("_checkEvery", "10", "seconds");
        pCheckEvery.setDisplayText("POP Check Delay", "delay (in seconds) between each login to the receiving server, while waiting for the message to arrive");
        pCheckEvery.setParameterOptions(true, 10, true);
        pAttachment = new FileProperty("_attachment", "");
        pAttachment.fieldLabels("Attachment", "Enter the full path name of a file to attach to the email");
        pAttachment.setParameterOptions(true, 11, true);
        pSMTPUser = new StringProperty("_smtpUser", "");
        pSMTPUser.setDisplayText("SMTP User", "User name for smtp-auth ");
        pSMTPUser.setParameterOptions(true, 12, true);
        pSMTPPassword = new StringProperty("_smtpPassword", "");
        pSMTPPassword.setDisplayText("SMTP Password", "the password for smtp-auth server");
        pSMTPPassword.setParameterOptions(true, 13, true);
        pSMTPPassword.isPassword = true;
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pSMTPTime = new NumericProperty("smtpTime", "0", "milliseconds");
        pSMTPTime.setLabel("Send time");
        pSMTPTime.setStateOptions(2);
        pReceiveTime = new NumericProperty("receiveTime", "0", "milliseconds");
        pReceiveTime.setLabel("Receive time");
        pReceiveTime.setStateOptions(3);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        pMatchValue = new StringProperty("matchValue");
        pMatchValue.setLabel("content match");
        pMatchValue.setIsThreshold(true);
        StringProperty astringproperty[] = { pSMTPServer, pPopServer, pPopAccount, pPopUser, pPopPassword, pCheckTimeout, pCheckEvery, pContentMatch, pReceiveOnly, pUseImap, pRoundTripTime, pStatus, pMatchValue, pAttachment, pSMTPTime, pReceiveTime,
                pSMTPPassword, pSMTPUser };
        addProperties("COM.dragonflow.StandardMonitor.MailMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.MailMonitor", Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.MailMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "description", "Tests mail by sending a mail message, and retrieving that message.");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "help", "MailMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "title", "Mail");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "class", "MailMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "target", "_smtpServer");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "toolName", "Mail Round Trip Test");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "toolDescription", "Test a mail server by sending and retrieving a test message.");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "topazName", "E-mail Monitor");
        setClassProperty("COM.dragonflow.StandardMonitor.MailMonitor", "topazType", "Mail server");
    }
}
