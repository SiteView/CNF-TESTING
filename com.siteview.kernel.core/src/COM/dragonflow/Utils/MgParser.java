 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package COM.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import COM.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils

public class MgParser
{

    public final int URL_POINTS = 1;
    final String CLASS_KEY = "_class";
    String groupsPath;
    final String urlSequenceMonitor = "URLSequenceMonitor";
    final String urlListMonitor = "URLListMonitor";
    final String apacheMonitor = "ApacheMonitor";
    final String aribaMonitor = "AribaMonitor";
    final String aspMonitor = "ASPMonitor";
    final String checkpointMonitor = "CheckPointMonitor";
    final String coldfusionMonitor = "ColdFusionMonitor";
    final String iisserverMonitor = "IISServerMonitor";
    final String netscapeMonitor = "NetscapeMonitor";
    final String realMonitor = "RealMonitor";
    final String silverstreamMonitor = "SilverStreamMonitor";
    final String windowsmediaMonitor = "WindowsMediaMonitor";
    final String ebusinessChainMonitor = "EBusinessTransactionMonitor";
    final String compositeMonitor = "CompositeMonitor";

    public MgParser(String s)
    {
        groupsPath = "";
        java.io.File file = new File(s);
        if(file.exists() && file.isDirectory())
        {
            groupsPath = s;
        } else
        {
            java.lang.System.out.println(s + "unreadable");
        }
    }

    public static void main(String args[])
    {
        COM.dragonflow.Utils.MgParser mgparser;
        if(args.length == 1)
        {
            mgparser = new MgParser(args[0]);
        } else
        {
            java.lang.System.out.println("Usage: MgParser </path/to/groups/directory>");
            return;
        }
        java.lang.System.out.println();
        java.lang.Object aobj[] = new java.lang.Object[3];
        aobj[0] = aobj[1] = aobj[2] = new Integer(0);
        aobj = mgparser.getUsageStatistics(true, true, true);
        java.lang.System.out.println("points used: " + aobj[0]);
        java.lang.System.out.println("monitors used: " + aobj[1]);
        java.lang.System.out.println("monitor types used: ");
        HashMap hashmap = (HashMap)aobj[2];
        for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements(); java.lang.System.out.println())
        {
            String s = (String)enumeration.nextElement();
            java.lang.System.out.print("   " + s + ": " + COM.dragonflow.Utils.TextUtils.getValue(hashmap, s) + " monitor");
            if((new Integer((String)hashmap.get(s))).intValue() > 1)
            {
                java.lang.System.out.print("s");
            }
            java.lang.System.out.print(".");
        }

    }

    private java.util.Vector _getMgFiles()
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/groups/");
        java.util.Vector vector = new Vector();
        int i = 0;
        if(file.isDirectory())
        {
            String as[] = file.list();
            for(int j = 0; j < as.length; j++)
            {
                if(as[j].endsWith(".mg"))
                {
                    vector.addElement(as[j]);
                    i++;
                }
            }

        } else
        {
            java.lang.System.out.println(groupsPath + " is not a directory");
            return null;
        }
        return vector;
    }

    public java.lang.Object[] getUsageStatistics(boolean flag, boolean flag1, boolean flag2)
    {
        java.lang.Object aobj[] = new java.lang.Object[5];
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        HashMap hashmap = new HashMap();
        aobj[2] = new Integer(-1);
        java.util.Vector vector;
        if((vector = _getMgFiles()) == null)
        {
            return null;
        }

        for(int i1 = 0; i1 < vector.size(); i1++)
        {
            Array array;
            try
            {
                array = COM.dragonflow.Utils.MgParser.readFromFile(groupsPath + java.io.File.separatorChar + vector.elementAt(i1));
            }
            catch(java.io.IOException ioexception)
            {
                java.lang.System.out.println("readFromFile " + vector.elementAt(i1) + " failed");
                return null;
            }
            ArrayIterator arrayiterator = new ArrayIterator(array, 0);
            HashMap hashmap1 = new HashMap();
            while (arrayiterator.hasMoreElements())
                {
                HashMap hashmap2 = (HashMap)arrayiterator.nextElement();
                String s = "";
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_class");
                if(!s.equals("") && s.indexOf("Monitor") != -1)
                {
                    if(flag)
                    {
                        i += _countPointsInUse(hashmap2);
                    }
                    if(flag1)
                    {
                        j++;
                    }
                    if(flag2)
                    {
                        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, s);
                        if(s1.equals(""))
                        {
                            hashmap.add(s, "1");
                        } else
                        {
                            int j1 = (new Integer(s1)).intValue() + 1;
                            hashmap.put(s, String.valueOf(j1));
                        }
                        if(s.equals("URLSequenceMonitor"))
                        {
                            k += _countURLSequenceMonitor(hashmap2);
                        } else
                        if(s.equals("URLListMonitor"))
                        {
                            l += _countURLListMonitor(hashmap2);
                        }
                    }
                }
            }
        }

        aobj[0] = new Integer(i);
        aobj[1] = new Integer(j);
        aobj[2] = hashmap;
        aobj[3] = new Integer(k);
        aobj[4] = new Integer(l);
        return aobj;
    }

    private int _countPointsInUse(HashMap hashmap)
    {
        int i = 0;
        HashMap hashmap1 = _defineAppPointUsage();
        java.util.Vector vector = _findSpecialMonitors();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_class");
        if(vector.contains(s))
        {
            int j = _countSpecialMonitor(hashmap, hashmap1, s);
            i += j;
        } else
        {
            i++;
        }
        return i;
    }

    private java.util.Vector _findSpecialMonitors()
    {
        java.util.Vector vector = new Vector();
        getClass();
        vector.addElement("URLSequenceMonitor");
        getClass();
        vector.addElement("URLListMonitor");
        getClass();
        vector.addElement("ApacheMonitor");
        getClass();
        vector.addElement("AribaMonitor");
        getClass();
        vector.addElement("ASPMonitor");
        getClass();
        vector.addElement("CheckPointMonitor");
        getClass();
        vector.addElement("ColdFusionMonitor");
        getClass();
        vector.addElement("IISServerMonitor");
        getClass();
        vector.addElement("NetscapeMonitor");
        getClass();
        vector.addElement("RealMonitor");
        getClass();
        vector.addElement("SilverStreamMonitor");
        getClass();
        vector.addElement("WindowsMediaMonitor");
        getClass();
        vector.addElement("EBusinessTransactionMonitor");
        getClass();
        vector.addElement("CompositeMonitor");
        return vector;
    }

    private int _countSpecialMonitor(HashMap hashmap, HashMap hashmap1, String s)
    {
        if(s.equals("URLListMonitor"))
        {
            return _countURLListMonitor(hashmap);
        }
        if(s.equals("URLSequenceMonitor"))
        {
            return _countURLSequenceMonitor(hashmap);
        }
        if(s.equals("EBusinessTransactionMonitor"))
        {
            return 0;
        }
        if(s.equals("CompositeMonitor"))
        {
            return 0;
        } else
        {
            return _countAppMonitor(s, hashmap1);
        }
    }

    private int _countURLListMonitor(HashMap hashmap)
    {
        int i = 0;
        try
        {
            StringBuffer stringbuffer = COM.dragonflow.Utils.MgParser.readFile(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_filename").toString());
            for(java.util.StringTokenizer stringtokenizer = new StringTokenizer(stringbuffer.toString()); stringtokenizer.hasMoreElements(); stringtokenizer.nextElement())
            {
                i++;
            }

        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.out.println("Couldn't read URL List file " + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_filename"));
        }
        return i;
    }

    private int _countURLSequenceMonitor(HashMap hashmap)
    {
        Enumeration enumeration = (Enumeration) hashmap.keys();
        String s = "";
        int i = 0;
        while (enumeration.hasMoreElements()) {
            String s1 = enumeration.nextElement().toString();
            if(s1.indexOf("_reference") > -1 && s1.indexOf("_referenceType") == -1 && !COM.dragonflow.Utils.TextUtils.getValue(hashmap, s1).toString().equals(""))
            {
                i++;
            }
        }
        return i;
    }

    private int _countAppMonitor(String s, HashMap hashmap)
    {
        java.lang.Integer integer = new Integer(COM.dragonflow.Utils.TextUtils.getValue(hashmap, s).toString());
        return integer.intValue();
    }

    private HashMap _defineAppPointUsage()
    {
        HashMap hashmap = new HashMap();
        hashmap.add("ApacheMonitor", "5");
        hashmap.add("AribaMonitor", "10");
        hashmap.add("ASPMonitor", "10");
        hashmap.add("CheckPointMonitor", "8");
        hashmap.add("ColdFusionMonitor", "6");
        hashmap.add("IISServerMonitor", "10");
        hashmap.add("NetscapeMonitor", "6");
        hashmap.add("RealMonitor", "5");
        hashmap.add("SilverStreamMonitor", "10");
        hashmap.add("WindowsMediaMonitor", "10");
        return hashmap;
    }

    private boolean _checkValidClass(String s)
    {
        return true;
    }

    public static Array readFromFile(String s)
        throws java.io.IOException
    {
        StringBuffer stringbuffer = COM.dragonflow.Utils.MgParser.readFile(s);
        String s1 = stringbuffer.toString();
        s1 = COM.dragonflow.Utils.MgParser.replaceChar(s1, '\r', "");
        Array array = COM.dragonflow.Utils.MgParser.split('\n', s1);
        return COM.dragonflow.Utils.MgParser.readFrames((Enumeration) array.iterator());
    }

    private static Array readFrames(Enumeration enumeration)
        throws java.io.IOException
    {
        Array array = new Array();
        do
        {
            HashMap hashmap = COM.dragonflow.Utils.MgParser.readFrame(enumeration, "#");
            if(hashmap != null)
            {
                array.add(hashmap);
            } else
            {
                return array;
            }
        } while(true);
    }

    private static HashMap readFrame(Enumeration enumeration, String s)
        throws java.io.IOException
    {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = null;
        while (enumeration.hasMoreElements()) {
            String s1 = (String)enumeration.nextElement();
            if(s1 == null)
            {
                break;
            }
            if(!s1.startsWith("//"))
            {
                if(hashmapordered == null)
                {
                    hashmapordered = new HashMapOrdered(true);
                }
                if(s1.startsWith(s))
                {
                    return hashmapordered;
                }
                int i = s1.indexOf('=');
                if(i > 0)
                {
                    String s2 = s1.substring(0, i);
                    String s3 = s1.substring(i + 1);
                    hashmapordered.add(s2, s3);
                }
            }
        } 
        return hashmapordered;
    }

    private static String replaceChar(String s, char c, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            if(c == s.charAt(i))
            {
                stringbuffer.append(s1);
            } else
            {
                stringbuffer.append(s.charAt(i));
            }
        }

        return stringbuffer.toString();
    }

    private static StringBuffer readFile(String s)
        throws java.io.IOException
    {
        java.io.FileInputStream fileinputstream = null;
        StringBuffer stringbuffer = new StringBuffer();
        fileinputstream = new FileInputStream(s);
        int i = 0;
        byte abyte0[] = new byte[32768];
        while((i = fileinputstream.read(abyte0)) != -1) 
        {
            stringbuffer.append(new String(abyte0, 0, i));
        }
        fileinputstream.close();
        return stringbuffer;
    }

    private static Array split(char c, String s)
    {
        Array array = new Array();
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            if(s.charAt(j) == c)
            {
                String s2 = s.substring(i, j);
                array.add(s2);
                i = j + 1;
            }
        }

        if(i != s.length())
        {
            String s1 = s.substring(i, s.length());
            array.add(s1);
        }
        return array;
    }
}
