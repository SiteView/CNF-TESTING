/*
 * 
 * Created on 2005-2-16 16:53:15
 *
 * SiteViewMail.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>SiteViewMail</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.servicePage;
import com.dragonflow.Utils.FileUtils;
import com.dragonflow.Utils.SMTP;

// Referenced classes of package com.dragonflow.SiteView:
// SiteViewGroup, Platform

public class SiteViewMail {

    public SiteViewMail() {
    }

    public static void statusMail(SiteViewGroup siteviewgroup, String s) {
        String s1 = siteviewgroup.getSetting("_autoEmail");
        if (s1.length() == 0) {
            return;
        }
        String s2 = Platform.productName + " " + s;
        String s3 = s2 + "\n\n";
        int i = siteviewgroup.getMonitorCount();
        int j = siteviewgroup.getDisabledMonitorCount();
        int k = siteviewgroup.getGroupCount();
        s3 = s3 + "  " + i + " active monitors";
        if (j > 0) {
            s3 = s3 + " and " + j + " disabled monitors";
        }
        s3 = s3 + " in " + k + " groups\n";
        s3 = s3 + "\n  " + siteviewgroup.mainURL();
        s3 = s3 + "\n  " + Platform.productName + " " + Platform.getVersion()
                + "\n";
        String s4 = siteviewgroup.simpleMail(s1, s2, s3);
        if (s4.length() != 0) {
            LogManager.log("Error", "STATUS MAIL ERROR: " + s4);
        }
    }

    public static void portChangedMail(SiteViewGroup siteviewgroup, String s,
            int i, int j) {
        String s1 = siteviewgroup.getSetting("_autoEmail");
        if (s1.length() == 0) {
            return;
        }
        String s2 = Platform.productName + " Server Port Changed\n\n";
        String s3 = "";
        if (s.length() == 0) {
            s2 = s2 + "The server port for " + Platform.productName
                    + " has changed back to " + j + ".\n";
        } else {
            StringBuffer stringbuffer = new StringBuffer();
            servicePage.printProcessStats(5, stringbuffer);
            s2 = s2 + "The server port for " + Platform.productName
                    + " has changed to " + j + "\n" + "\n" + "The old port ("
                    + i + ") could not be opened.  The error\n"
                    + "message was: " + s + "\n";
            s3 = "\nRunning Processes\n\n" + stringbuffer.toString();
        }
        s2 = s2 + "\n" + urlText(siteviewgroup) + "\n" + s3;
        siteviewgroup.simpleMail(s1, Platform.productName
                + " Server Port Changed", s2);
    }

    public static void portErrorMail(SiteViewGroup siteviewgroup, String s,
            int i, int j) {
        String s1 = siteviewgroup.getSetting("_autoEmail");
        if (s1.length() == 0) {
            return;
        } else {
            String s2 = Platform.productName
                    + " Server Port Error\n\n"
                    + "The server port for "
                    + Platform.productName
                    + " could not be opened.\n"
                    + "\n"
                    + "The port ("
                    + i
                    + ") could not be opened.  The error\n"
                    + "message was: "
                    + s
                    + "\n"
                    + "\n"
                    + "The "
                    + Platform.productName
                    + " service is running but you will not be able\n"
                    + "to connect to it using your browser.\n"
                    + "\n"
                    + "To fix this problem, change the _httpPort parameter in groups/master.config\n"
                    + "to an available port and restart the "
                    + Platform.productName + " service.\n" + "\n";
            siteviewgroup.simpleMail(s1, Platform.productName
                    + " Server Port Error", s2);
            return;
        }
    }

    public static String urlText(SiteViewGroup siteviewgroup) {
        String s = siteviewgroup.mainURL();
        return "You can use your browser and connect to the "
                + Platform.productName + " main panel\n" + "using the URL:\n"
                + "   " + s + "\n";
    }

    public static void firstTimeMail(SiteViewGroup siteviewgroup, String s,
            String s1) {
        String s2 = siteviewgroup.getSetting("_autoEmail");
        if (s2.length() != 0) {
            installMail(siteviewgroup, s2);
        }
    }

    public static void installMail(SiteViewGroup siteviewgroup, String s) {
        String s1;
        if (Platform.isWindows()) {
            s1 = "If you need to start or stop the " + Platform.productName
                    + " service, use the\n" + "Services Control Panel.\n";
        } else {
            s1 = "If you need to start or stop the "
                    + Platform.productName
                    + " process, use the\n"
                    + Platform.productName
                    + "/start and "
                    + Platform.productName
                    + "/stop scripts.\n"
                    + "\n"
                    + "Don't forget to add "
                    + Platform.productName
                    + "/start to one of your\n"
                    + "init.d scripts so it will start whenever your server starts.\n";
        }
        String s2 = Platform.productName + " Installation Complete";
        String s3 = Platform.productName
                + " Installation has been completed.\n" + "\n" + "The "
                + Platform.productName
                + " service has been successfully installed and started\n"
                + "for the first time.\n" + "\n" + urlText(siteviewgroup)
                + "\n" + s1 + "\n";
        siteviewgroup.simpleMail(s, s2, s3);
    }

    public static void main(String args[]) {
        // TODO need review
        int i = 0;
        String string = "";
        String string_18_ = Platform.supportEmail;
        String string_19_ = "Test";
        String string_20_ = "";
        String string_21_ = "";
        String string_22_ = "";
        String string_23_ = "mail." + Platform.exampleDomain;
        String string_24_ = "206.168.191.45";
        String string_25_ = "";
        for (/**/; i < args.length; i++) {
            if (args[i].equals("-f"))
                string_18_ = args[++i];
            else if (args[i].equals("-t"))
                string = args[++i];
            else if (args[i].equals("-s"))
                string_19_ = args[++i];
            else if (args[i].equals("-m"))
                string_20_ = args[++i];
            else if (args[i].equals("-l"))
                string_21_ = args[++i];
            else if (args[i].equals("-mx"))
                string_23_ = args[++i];
        }
        try {
            System.out.println("Subject: " + string_19_);
            System.out.println("From: " + string_18_);
            Vector vector = new Vector();
            if (string.length() > 0)
                vector.addElement(string);
            if (string_21_.length() > 0) {
                System.out.println("Mail List File: " + string_21_);
                FileInputStream fileinputstream = null;
                try {
                    fileinputstream = new FileInputStream(string_21_);
                    BufferedReader bufferedreader = FileUtils
                            .MakeInputReader(fileinputstream);
                    String string_26_;
                    while ((string_26_ = bufferedreader.readLine()) != null) {
                        string_26_ = string_26_.trim();
                        if (string_26_.length() > 0
                                && !string_26_.startsWith("#"))
                            vector.addElement(string_26_);
                    }
                } catch (IOException e) {
                    if (fileinputstream != null)
                        fileinputstream.close();
                    throw e;
                }
                if (fileinputstream != null)
                    fileinputstream.close();
            }
            if (string_20_.length() > 0) {
                System.out.println("Message File: " + string_20_);
                FileInputStream fileinputstream = null;
                StringBuffer stringbuffer = new StringBuffer();
                try {
                    fileinputstream = new FileInputStream(string_20_);
                    BufferedReader bufferedreader = FileUtils
                            .MakeInputReader(fileinputstream);
                    String string_27_;
                    while ((string_27_ = bufferedreader.readLine()) != null) {
                        stringbuffer.append(string_27_);
                        stringbuffer.append('\n');
                    }
                } catch (IOException e) {
                    if (fileinputstream != null)
                        fileinputstream.close();
                    throw e;
                }
                if (fileinputstream != null)
                    fileinputstream.close();
                string_22_ = stringbuffer.toString();
            }
            int i_28_ = string_24_.indexOf(":");
            if (i_28_ >= 0)
                string_24_ = string_24_.substring(0, i_28_);
            if (vector.size() == 0) {
                System.out.println("No recipients");
                System.exit(0);
            }
            if (string_18_.length() == 0) {
                System.out.println("No from");
                System.exit(0);
            }
            if (string_22_.length() == 0) {
                System.out.println("No message");
                System.exit(0);
            }
            int i_29_ = 0;
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                String string_30_ = (String) enumeration.nextElement();
                SMTP smtp = new SMTP(string_23_, SMTP.defaultTimeout, null);
                System.out.println("Sending message " + ++i_29_ + " of "
                        + vector.size() + " to " + string_30_);
                smtp.send(string_18_, string_30_, string_25_, string_19_,
                        string_22_, null);
                smtp.close();
            }
        } catch (Exception exception) {
            System.out.println("PROBLEM EMAILING: " + exception.toString());
        }
    }
}
