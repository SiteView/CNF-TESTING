// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.*;

public class makeGroups
{

    public makeGroups()
    {
    }

    public static void main(String args[])
        throws IOException
    {
        int i = 1;
        int j = 10;
        String s = "../groups/";
        if(args.length > 0)
            i = TextUtils.toInt(args[0]);
        if(args.length > 1)
            j = TextUtils.toInt(args[1]);
        if(args.length > 2)
            s = args[2];
        int k = 1;
        for(int l = 0; l < i; l++)
        {
            PrintWriter printwriter = FileUtils.MakeOutputWriter(new FileOutputStream(s + "test" + l + ".mg"));
            printwriter.println("_name=config");
            printwriter.println("_nextID=100000");
            for(int i1 = 0; i1 < j; i1++)
            {
                printwriter.println("#");
                printwriter.println("_id=" + i1);
                printwriter.println("_name=monitor" + i1);
                printwriter.println("_frequency=1200");
                printwriter.println("_class=URLMonitor");
                printwriter.println("_getImages=on");
                if(k > 10)
                    k = 1;
                printwriter.println("_url=http://207.221.189.2/freshtest/page" + k++ + ".htm");
            }

            printwriter.close();
        }

    }
}