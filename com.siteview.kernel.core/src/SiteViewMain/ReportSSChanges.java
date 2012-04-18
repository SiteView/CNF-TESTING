// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:16
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.util.Enumeration;
import java.util.GregorianCalendar;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.Utils.CommandLine;
import com.dragonflow.Utils.MailUtils;
import com.dragonflow.Utils.TextUtils;

public class ReportSSChanges
{

    public ReportSSChanges()
    {
    }

    public static void printUsage()
    {
        System.out.println("Usage: ReportSSChanges ");
        System.out.println("Gets a report from SS.exe History and emails what changes occurred that day");
        System.exit(0);
    }

    public static void main(String args[])
    {
        if(args.length == 0)
        {
            System.out.println("Usage: \ncd SiteView\\classes\n..\\java\\bin\\java -cp . ReportSSChanges ssDirectory ssProject toMailAddress\nWhere:\nssDirectory   - Directory where ss.exe is (ie on turtle it is \\vgdev\\install\\srcsafe\\\nssProject     - Project to start getting history for  (ie latest is $/SiteView)\ntoMailAddress - Address of who to mail this to (ie default is siteviewsoftware@merc-int.com)\n");
            return;
        }
        GregorianCalendar gregoriancalendar = new GregorianCalendar();
        System.out.println("Executing: " + (gregoriancalendar.get(2) + 1) + "/" + gregoriancalendar.get(5) + "/" + gregoriancalendar.get(1));
        gregoriancalendar.add(5, -2);
        String s = "" + (gregoriancalendar.get(2) + 1) + "/" + (gregoriancalendar.get(5) >= 10 ? "" : "0") + gregoriancalendar.get(5) + "/0" + (gregoriancalendar.get(1) - 2000);
        System.out.println("Test against: " + s);
        String s1 = "\\vgdev\\install\\srcsafe\\";
        if(args.length > 0)
            s1 = new String(args[0]);
        String s2 = "$/SiteView";
        if(args.length > 1)
            s2 = new String(args[1]);
        String s3 = s1 + "ss.exe history " + s2 + " -r";
        System.out.println("Command String = " + s3);
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s3);
        int i = commandline.getExitValue();
        System.out.println("ExitValue = " + i);
        Enumeration enumeration = (Enumeration) array.iterator();
        String s4 = null;
        for(; enumeration.hasMoreElements(); System.out.print("\n"))
        {
            String s5 = (String)enumeration.nextElement();
            s4 = s4 + s5 + "\n";
            System.out.print(s5);
            if(s5.indexOf("Date:") < 0)
                continue;
            int j = s5.indexOf("/");
            String s7 = s5.substring(j - 2).trim();
            int k = TextUtils.readIntegerSafe(s7, 0);
            int l = TextUtils.readIntegerSafe(s7, s7.indexOf("/") + 1);
            s7 = s7.substring(s7.indexOf("/") + 1);
            int i1 = TextUtils.readIntegerSafe(s7, s7.indexOf("/") + 1) + 2000;
            GregorianCalendar gregoriancalendar1 = new GregorianCalendar(i1, k - 1, l);
            String s9 = "" + (gregoriancalendar1.get(2) + 1) + "/" + (gregoriancalendar1.get(5) >= 10 ? "" : "0") + gregoriancalendar1.get(5) + "/" + gregoriancalendar1.get(1);
            System.out.print(" *Date*: " + s9 + "\n");
            if(gregoriancalendar1.before(gregoriancalendar))
                break;
        }

        String s6 = new String("siteviewsoftware@merc-int.com");
        if(args.length > 2)
            s6 = new String(args[2]);
        HashMap hashmap = new HashMap();
        hashmap.put("_mailServer", "mail.siteview.com");
        String s8 = MailUtils.mail(hashmap, s6, "SourceSafe Changes today for project " + s2, s4, "", null, false, "", null);
        System.out.println("Return from mail: " + s8);
    }
}