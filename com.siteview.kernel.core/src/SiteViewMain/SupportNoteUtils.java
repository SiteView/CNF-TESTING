// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:21
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

public class SupportNoteUtils
{

    public SupportNoteUtils()
    {
    }

    public static Array readNotes(String s)
    {
        Array array = new Array();
        File file = new File(s);
        if(!file.exists())
        {
            System.out.println("Source Directory " + s + " does not exist");
            return null;
        }
        String as[] = file.list();
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            String s2 = "";
            int j = s1.lastIndexOf(".");
            if(j >= 0)
            {
                s2 = s1.substring(j + 1);
                s1 = s1.substring(0, j);
            }
            if(s2.startsWith("htm"))
            {
                HashMap hashmap = readNote(new File(file, as[i]));
                array.add(hashmap);
            }
        }

        return array;
    }

    public static HashMap readNote(File file)
    {
        FileInputStream fileinputstream = null;
        BufferedReader bufferedreader = null;
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        boolean flag = false;
        try
        {
            fileinputstream = new FileInputStream(file);
            bufferedreader = FileUtils.MakeInputReader(fileinputstream);
            do
            {
                String s;
                if((s = bufferedreader.readLine()) == null)
                    break;
                if(s.startsWith(NOTE_COMMENT_START))
                {
                    String s3 = "";
                    String s5 = "";
                    int j = s.lastIndexOf(NOTE_COMMENT_END);
                    if(j >= 0)
                        s3 = s.substring(NOTE_COMMENT_START.length(), j);
                    String s7 = s3;
                    int k = s3.lastIndexOf("=");
                    if(k >= 0)
                    {
                        s5 = s3.substring(k + 1);
                        s7 = s3.substring(0, k);
                    }
                    if(s7.equals("start"))
                        flag = true;
                    else
                    if(s7.equals("end"))
                        flag = false;
                    else
                        hashmapordered.add(s7, s5);
                } else
                if(flag)
                    hashmapordered.put("body", TextUtils.getValue(hashmapordered, "body") + s + "\n");
            } while(true);
        }
        catch(Exception exception)
        {
            System.out.println("Exception reading " + file + ": " + exception);
            exception.printStackTrace();
        }
        finally
        {
            try
            {
                if(bufferedreader != null)
                    bufferedreader.close();
                if(fileinputstream != null)
                    fileinputstream.close();
            }
            catch(Exception exception2) { }
        }
        if(TextUtils.getValue(hashmapordered, "updated").length() == 0)
            hashmapordered.put("updated", TextUtils.prettyDateDate(TextUtils.prettyDate(file.lastModified())));
        if(TextUtils.getValue(hashmapordered, "weight").length() == 0)
            hashmapordered.put("weight", "normal");
        if(TextUtils.getValue(hashmapordered, "filename").length() == 0)
        {
            String s1 = NOTES_DIRECTORY + "note" + TextUtils.getValue(hashmapordered, "idnum") + ".htm";
            hashmapordered.put("filename", s1);
        }
        if(TextUtils.getValue(hashmapordered, "idfilename").length() == 0)
        {
            String s2 = NOTES_DIRECTORY + "note" + TextUtils.getValue(hashmapordered, "id") + ".htm";
            hashmapordered.put("idfilename", s2);
        }
        if(TextUtils.getValue(hashmapordered, "topics").length() > 0)
        {
            String as[] = TextUtils.split(TextUtils.getValue(hashmapordered, "topics"), ",");
            for(int i = 0; i < as.length; i++)
                hashmapordered.add("topic", as[i]);

        } else
        {
            Enumeration enumeration = hashmapordered.values("topic");
            String s4;
            String s6;
            for(s4 = ""; enumeration.hasMoreElements(); s4 = s4 + s6)
            {
                s6 = (String)enumeration.nextElement();
                if(s4.length() > 0)
                    s4 = s4 + ",";
            }

            hashmapordered.put("topics", s4);
        }
        if(TextUtils.getValue(hashmapordered, "releases").length() == 0)
            hashmapordered.put("releases", "");
        if(TextUtils.getValue(hashmapordered, "author").length() == 0)
            hashmapordered.put("author", "");
        return hashmapordered;
    }

    public static String writeFile(String s, String as[], HashMap hashmap, String s1)
    {
        FileOutputStream fileoutputstream = null;
        PrintWriter printwriter = null;
        String s2 = "";
        try
        {
            fileoutputstream = new FileOutputStream(s);
            printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            String as1[] = new String[as.length * 2];
            for(int i = 0; i < as.length; i++)
            {
                as1[i] = "%" + as[i] + "%";
                as1[i + as.length] = TextUtils.getValue(hashmap, as[i]);
            }

            String s3 = TextUtils.replaceString(s1, as1);
            printwriter.print(s3);
        }
        catch(Exception exception)
        {
            s2 = exception.getMessage();
            System.out.println("Could not write " + hashmap.get("title") + ": " + exception);
        }
        finally
        {
            try
            {
                if(printwriter != null)
                    printwriter.close();
                if(fileoutputstream != null)
                    fileoutputstream.close();
            }
            catch(Exception exception2) { }
        }
        return s2;
    }

    public static String writeNoteFile(String s, HashMap hashmap, String s1)
    {
        return writeFile(s + TextUtils.getValue(hashmap, "filename"), noteKeys, hashmap, s1);
    }

    public static String writeIDNoteFile(String s, HashMap hashmap, String s1)
    {
        return writeFile(s + TextUtils.getValue(hashmap, "idfilename"), noteKeys, hashmap, s1);
    }

    public static void main(String args[])
        throws Exception
    {
        Array array = readNotes("C:/supportsite/notes/");
        String s = FileUtils.readFile("C:/supportsite/notes/noteInternalTemplate.txt").toString();
        for(int i = 0; i < array.size(); i++)
        {
            HashMap hashmap = (HashMap)array.at(i);
            writeNoteFile("C:/supportsite/", hashmap, s);
        }

    }

    static String NOTE_COMMENT_START = "<!--note-";
    static String NOTE_COMMENT_END = "-->";
    public static String NOTES_DIRECTORY = "notes/";
    public static String noteKeys[] = {
        "title", "id", "idnum", "body", "topics", "filename", "product", "updated", "created", "weight", 
        "releases", "author"
    };

}