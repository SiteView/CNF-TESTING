// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.Utils.TextUtils;

import java.io.PrintStream;

public class Scramble
{

    public Scramble()
    {
    }

    public static void main(String args[])
    {
        if(args.length < 1)
        {
            System.out.println("Usage: Scramble cleartext");
            System.exit(0);
        }
        System.out.println(TextUtils.obscure(args[0]));
    }
}