// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:13
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.io.File;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.Properties.FrameFile;
import com.dragonflow.Utils.TextUtils;

public class CopyGroups
{

    public CopyGroups()
    {
    }

    public static void printUsage()
    {
        System.out.println("Usage: CopyGroups sourceDirectory destDirectory [[-d setting] [-r setting value] [-a setting value] [-s setting value]...]");
        System.out.println("       -d   delete this setting");
        System.out.println("       -r   replace the value of this setting if it exists");
        System.out.println("       -s   set this setting to the value");
        System.out.println("       -a   add this value this setting");
        System.exit(0);
    }

    public static void main(String args[])
    {
        if(args.length >= 2);
        String s = args[0];
        String s1 = args[1];
        int i = 2;
        do
        {
            if(i >= args.length)
                break;
            if(args[i].equals("-d"))
            {
                if(i + 1 >= args.length)
                {
                    System.out.println("-d requires a setting argument");
                    printUsage();
                } else
                {
                    i += 2;
                }
            } else
            if(args[i].startsWith("-r") || args[i].equals("-a") || args[i].equals("-s"))
                if(i + 1 >= args.length)
                {
                    System.out.println(args[i] + " requires a setting argument");
                    printUsage();
                } else
                {
                    i += 3;
                }
        } while(true);
        try
        {
            File file = new File(s);
            String args1[] = file.list();
            int j = 0;
            do
            {
                if(j >= args1.length)
                    break;
                if(args1[j].endsWith(".mg"))
                {
                    System.out.println("Working on file " + args1[j]);
                    File file1 = new File(file, args1[j]);
                    Array array = FrameFile.readFromFile(file1.getAbsolutePath());
                    Enumeration enumeration = (Enumeration) array.iterator();
                    int k = 0;
                    while(enumeration.hasMoreElements()) 
                    {
                        k++;
                        HashMap hashmap = (HashMap)enumeration.nextElement();
                        int l = 2;
                        String s2 = "Unknown frame";
                        if(k == 1)
                            s2 = "Group frame " + hashmap.get("_name");
                        else
                        if(hashmap.get("_id") != null)
                            s2 = "Monitor frame for " + hashmap.get("_name");
                        else
                        if(hashmap.get("id") != null)
                            s2 = "Report frame for ID " + hashmap.get("id");
                        System.out.println("  " + s2);
                        while(l < args.length) 
                            if(args[l].equals("-d"))
                            {
                                String s3 = args[++l];
                                if(hashmap.get(s3) != null)
                                {
                                    System.out.println("    deleting " + s3 + " - value was " + hashmap.get(s3));
                                    hashmap.remove(s3);
                                }
                                l++;
                            } else
                            if(args[l].equals("-a"))
                            {
                                String s4 = args[++l];
                                String s7 = args[++l];
                                System.out.println("    adding " + s4 + " with value " + s7);
                                hashmap.add(s4, s7);
                                l++;
                            } else
                            if(args[l].startsWith("-r"))
                            {
                                String s5 = args[l];
                                String s8 = args[++l];
                                String s10 = args[++l];
                                if(hashmap.get(s8) != null)
                                {
                                    boolean flag = true;
                                    if(s5.equals("-rmax"))
                                    {
                                        long l1 = TextUtils.toLong((String)hashmap.get(s8));
                                        long l2 = TextUtils.toLong(s10);
                                        if(l1 > l2)
                                            flag = false;
                                    }
                                    if(flag)
                                    {
                                        System.out.println("    replacing " + s8 + " (old value was " + hashmap.get(s8) + ") with value " + s10);
                                        hashmap.put(s8, s10);
                                    }
                                }
                                l++;
                            } else
                            if(args[l].equals("-s"))
                            {
                                String s6 = args[++l];
                                String s9 = args[++l];
                                System.out.println("    setting " + s6 + " to value " + s9);
                                hashmap.put(s6, s9);
                                l++;
                            }
                    }
                    System.out.println();
                    FrameFile.writeToFile(s1 + "/" + args1[j], array);
                }
                j++;
            } while(true);
        }
        catch(Exception exception)
        {
            System.out.println("Exception: " + exception);
        }
    }
}