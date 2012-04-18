// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:16
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.dragonflow.Properties.HashMapOrdered;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.SiteView.SiteViewGroup;

public class MonitorRunner
{

    public MonitorRunner()
    {
    }

    public static void die()
    {
        System.err.println("usage: java MonitorRunner monitorClass [-c count] [-f frequency] [arg=val ...]");
        System.exit(0);
    }

    public static void main(String args[])
    {
        if(args.length < 1)
            die();
        String s = args[0];
        int i = 1;
        int j = 30;
        try
        {
            HashMapOrdered hashmapordered = new HashMapOrdered(true);
            hashmapordered.put("_class", s);
            for(int k = 1; k < args.length; k++)
            {
                if(args[k].equals("-c"))
                {
                    if(++k < args.length)
                    {
                        try
                        {
                            i = Integer.parseInt(args[k]);
                            continue;
                        }
                        catch(NumberFormatException numberformatexception)
                        {
                            System.err.println("-c must be a number");
                        }
                        die();
                    } else
                    {
                        System.err.println("-c missing count");
                        die();
                    }
                    continue;
                }
                if(args[k].equals("-f"))
                {
                    if(++k < args.length)
                    {
                        try
                        {
                            j = Integer.parseInt(args[k]);
                            continue;
                        }
                        catch(NumberFormatException numberformatexception1)
                        {
                            System.err.println("-f must be a number");
                        }
                        die();
                    } else
                    {
                        System.err.println("-f missing frequency");
                        die();
                    }
                    continue;
                }
                int l = args[k].indexOf("=");
                if(l >= 0)
                {
                    String s1 = args[k].substring(0, l);
                    String s2 = args[k].substring(l + 1);
                    hashmapordered.add(s1, s2);
                } else
                {
                    die();
                }
            }

            AtomicMonitor atomicmonitor = (AtomicMonitor)SiteViewGroup.createTestObject(hashmapordered);
            atomicmonitor.setProperty(AtomicMonitor.pFrequency, j);
            if(atomicmonitor == null)
            {
                System.err.println("Could not find monitor class: " + args[0]);
                System.exit(0);
            }
            for(int i1 = 1; i1 <= i; i1++)
            {
                if(i1 > 1)
                    Platform.sleep(atomicmonitor.getPropertyAsInteger(AtomicMonitor.pFrequency) * 1000);
                long l1 = System.currentTimeMillis();
                boolean flag = atomicmonitor.testUpdate();
                long l2 = System.currentTimeMillis();
                if(i > 1)
                    System.out.println("\n\n======= run # " + i1 + "=======\n");
                String s3 = flag ? "succeeded" : "failed";
                System.out.println(s + " " + s3 + " in " + (l2 - l1) / 1000L + " seconds\n");
                Array array = atomicmonitor.getProperties();
                System.out.println("-----  Parameters  ------");
                Enumeration enumeration = (Enumeration) array.iterator();
                do
                {
                    if(!enumeration.hasMoreElements())
                        break;
                    StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                    if(stringproperty.isParameter)
                        System.out.println(stringproperty.getName() + "=" + atomicmonitor.getProperty(stringproperty));
                } while(true);
                System.out.println("\n-----  State Properties  ------");
                enumeration = (Enumeration) array.iterator();
                do
                {
                    if(!enumeration.hasMoreElements())
                        break;
                    StringProperty stringproperty1 = (StringProperty)enumeration.nextElement();
                    if(stringproperty1.isStateProperty)
                        System.out.println(stringproperty1.getName() + "=" + atomicmonitor.getProperty(stringproperty1));
                } while(true);
            }

        }
        catch(InstantiationException instantiationexception)
        {
            System.err.println("Could not instantiate monitor");
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            System.err.println("Illegal access exception");
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            System.err.println("Could not find monitor of class: " + s);
        }
        System.exit(0);
    }

    private static final String USAGE = "usage: java MonitorRunner monitorClass [-c count] [-f frequency] [arg=val ...]";
}