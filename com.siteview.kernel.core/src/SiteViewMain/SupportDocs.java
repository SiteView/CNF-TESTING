// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:20
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Enumeration;

import jgl.Array;
import jgl.BinaryPredicate;
import jgl.HashMap;
import jgl.Sorting;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;

import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package SiteViewMain:
//            SupportNoteUtils

public class SupportDocs
    implements BinaryPredicate
{

    public SupportDocs()
    {
    }

    public static void main(String args[])
    {
        String s = "";
        String s1 = "";
        String s2 = "F:/www/htdocs/support/";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        boolean flag = false;
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equals("-s"))
            {
                sourceDirectoryName = args[++i];
                continue;
            }
            if(args[i].equals("-d"))
            {
                s = args[++i];
                continue;
            }
            if(args[i].equals("-i"))
            {
                s1 = args[++i];
                continue;
            }
            if(args[i].equals("-r"))
            {
                recentDays = TextUtils.toLong(args[++i]);
                continue;
            }
            if(args[i].equals("-bugVersion"))
            {
                bugVersion = args[++i];
                continue;
            }
            if(args[i].equals("-patchVersion"))
            {
                patchVersion = args[++i];
                continue;
            }
            if(args[i].equals("-search"))
            {
                s3 = args[++i];
                s3 = s3.replace('^', '"');
                flag = true;
                continue;
            }
            if(args[i].equals("-product"))
            {
                s4 = args[++i];
                continue;
            }
            if(args[i].equals("-sd"))
            {
                s2 = args[++i];
                continue;
            }
            if(args[i].equals("-ip"))
                s5 = args[++i];
        }

        if(flag)
        {
            if(s3.length() > 0)
                searchDocs(s2, s3, s4, s5);
            else
                System.out.println("No search words entered");
        } else
        if(s.length() > 0)
            try
            {
                Array array = SupportNoteUtils.readNotes(sourceDirectoryName);
                IndexWriter indexwriter = new IndexWriter(s + "index", new StandardAnalyzer(), true);
                indexwriter.mergeFactor = 20;
                for(int j = 0; j < array.size(); j++)
                {
                    HashMap hashmap = (HashMap)array.at(j);
                    indexNote(indexwriter, hashmap);
                }

                indexwriter.optimize();
                indexwriter.close();
                Array array1 = null;
                if(debug)
                    System.out.println("Generating support pages");
                array1 = FrameFile.readFromFile(sourceDirectoryName + "topics.txt");
                notePublicTemplate = FileUtils.readFile(sourceDirectoryName + "notePublicTemplate.txt").toString();
                noteInternalTemplate = FileUtils.readFile(sourceDirectoryName + "noteInternalTemplate.txt").toString();
                noteRedirectTemplate = FileUtils.readFile(sourceDirectoryName + "noteRedirectTemplate.txt").toString();
                publicTemplate = FileUtils.readFile(sourceDirectoryName + "publicTemplate.txt").toString();
                if(array != null && array1 != null)
                    writeSupportDocs(s, s1, array1, array);
            }
            catch(Exception exception)
            {
                System.out.println("Exception generating notes: " + exception);
                exception.printStackTrace();
            }
    }

    public static void indexNote(IndexWriter indexwriter, HashMap hashmap)
        throws IOException
    {
        Document document = new Document();
        document.add(Field.Text("path", TextUtils.getValue(hashmap, "filename")));
        document.add(Field.Text("title", TextUtils.getValue(hashmap, "title")));
        document.add(Field.Text("body", TextUtils.getValue(hashmap, "body") + TextUtils.getValue(hashmap, "title")));
        document.add(Field.Text("topics", TextUtils.getValue(hashmap, "topics")));
        document.add(Field.Text("product", TextUtils.getValue(hashmap, "product")));
        document.add(Field.Text("releases", TextUtils.getValue(hashmap, "releases")));
        document.add(Field.Keyword("updated", TextUtils.getValue(hashmap, "updated")));
        indexwriter.addDocument(document);
    }

    static void writeSupportDocs(String s, String s1, Array array, Array array1)
    {
        System.out.println("Sorting notes...");
        HashMap hashmap = new HashMap();
        for(int i = 0; i < array.size(); i++)
        {
            HashMap hashmap1 = (HashMap)array.at(i);
            String s2 = TOPICS_DIRECTORY + "topic" + TextUtils.getValue(hashmap1, "id") + ".htm";
            hashmap1.put("filename", s2);
            System.out.println("Processing topic " + TextUtils.getValue(hashmap1, "title"));
            String s3 = TextUtils.getValue(hashmap1, "match");
            Array array2 = new Array();
            for(int j1 = 0; j1 < array1.size(); j1++)
            {
                HashMap hashmap7 = (HashMap)array1.at(j1);
                boolean flag1 = false;
                Enumeration enumeration = hashmap7.values("topic");
                do
                {
                    if(!enumeration.hasMoreElements())
                        break;
                    String s6 = (String)enumeration.nextElement();
                    if(!TextUtils.match(s6, s3))
                        continue;
                    flag1 = true;
                    break;
                } while(true);
                if(!flag1)
                    continue;
                array2.add(hashmap7);
                Array array4 = (Array)hashmap7.get("topicList");
                if(array4 == null)
                {
                    array4 = new Array();
                    hashmap7.put("topicList", array4);
                }
                array4.add(hashmap1);
            }

            System.out.println("" + array2.size() + " notes for topic");
            Sorting.sort(array2, new SupportDocs());
            hashmap1.put("notes", array2);
            String s5 = TextUtils.getValue(hashmap1, "parent");
            if(s5.length() > 0)
            {
                HashMap hashmap8 = (HashMap)hashmap.get(s5);
                if(hashmap8 == null)
                    continue;
                Array array3 = (Array)hashmap8.get("topicList");
                if(array3 == null)
                {
                    array3 = new Array();
                    hashmap8.put("topicList", array3);
                }
                array3.add(hashmap1);
                hashmap1.put("parentTopic", hashmap8);
            } else
            {
                hashmap.put(TextUtils.getValue(hashmap1, "id"), hashmap1);
            }
        }

        boolean flag = false;
        System.out.println("Looking for note inconsistencies...");
        for(int j = 0; j < array1.size(); j++)
        {
            HashMap hashmap3 = (HashMap)array1.at(j);
            if(hashmap3.get("topicList") == null)
            {
                flag = true;
                System.out.println("Orphan note: " + TextUtils.getValue(hashmap3, "id") + ": " + TextUtils.getValue(hashmap3, "title"));
            }
            if(hashmap3.get("product") == null)
            {
                flag = true;
                System.out.println("No product for note: " + TextUtils.getValue(hashmap3, "id") + ": " + TextUtils.getValue(hashmap3, "title"));
            }
        }

        System.out.println("Looking for duplicate notes...");
        HashMap hashmap2 = new HashMap();
        for(int k = 0; k < array1.size(); k++)
        {
            HashMap hashmap4 = (HashMap)array1.at(k);
            String s4 = TextUtils.getValue(hashmap4, "idnum");
            HashMap hashmap6 = (HashMap)hashmap2.get(s4);
            if(hashmap6 != null)
            {
                System.out.println("Duplicate Notes for idnum " + s4);
                System.out.println("Note 1: " + hashmap6.get("title"));
                System.out.println("Note 2: " + hashmap4.get("title"));
                flag = true;
            } else
            {
                hashmap2.put(s4, hashmap4);
            }
        }

        if(flag)
        {
            System.out.println("*** Problem In Notes - Indexes Not Generated ***");
            System.exit(0);
        }
        System.out.println("Writing note files...");
        for(int l = 0; l < array1.size(); l++)
        {
            HashMap hashmap5 = (HashMap)array1.at(l);
            SupportNoteUtils.writeNoteFile(s, hashmap5, notePublicTemplate);
            if(TextUtils.getValue(hashmap5, "id").length() > 0)
                SupportNoteUtils.writeIDNoteFile(s, hashmap5, noteRedirectTemplate);
            if(s1.length() > 0)
                SupportNoteUtils.writeNoteFile(s1, (HashMap)array1.at(l), noteInternalTemplate);
        }

        System.out.println("Writing topic files...");
        for(int i1 = 0; i1 < array.size(); i1++)
            writeTopicFile(s, (HashMap)array.at(i1));

        System.out.println("Writing index files...");
        writeIndexFiles(s, array, "", "");
        System.out.println("Writing all file...");
        writeAllFile(s, array, "", "");
        System.out.println("Writing hot index files...");
        writeIndexFiles(s, array, "", hotWeight);
        System.out.println("Writing hot all file...");
        writeAllFile(s, array, "", hotWeight);
        System.out.println("Writing daily index files...");
        writeIndexFiles(s, array, "Day", "");
        System.out.println("Writing daily all file...");
        writeAllFile(s, array, "Day", "");
        System.out.println("Writing weekly index files...");
        writeIndexFiles(s, array, "Week", "");
        System.out.println("Writing weekly all file...");
        writeAllFile(s, array, "Week", "");
        System.out.println("Writing monthly index files...");
        writeIndexFiles(s, array, "Month", "");
        System.out.println("Writing monthly all file...");
        writeAllFile(s, array, "Month", "");
        writePatchIndexFile(s, array);
        writeBugIndexFile(s, array);
    }

    static void writeIndexFiles(String s, Array array, String s1, String s2)
    {
        writeIndexFile(s, array, s1, s2);
        writeSiteViewIndexFile(s, array, s1, s2);
        writeSiteSeerIndexFile(s, array, s1, s2);
        writeSiteViewNTIndexFile(s, array, s1, s2);
        writeSiteViewUnixIndexFile(s, array, s1, s2);
    }

    static String writeTopicFile(String s, HashMap hashmap)
    {
        Array array = (Array)hashmap.get("notes");
        Array array1 = (Array)hashmap.get("topicList");
        if(array == null && array1 == null)
        {
            System.out.println("No notes or subtopics for topic " + hashmap.get("title"));
            return "";
        }
        StringBuffer stringbuffer = new StringBuffer();
        if(array1 != null)
        {
            stringbuffer.append("Subtopics: <BR>\n");
            for(int i = 0; i < array1.size(); i++)
            {
                HashMap hashmap1 = (HashMap)array1.at(i);
                stringbuffer.append("<BR><A HREF=../" + TextUtils.getValue(hashmap1, "filename") + ">" + TextUtils.getValue(hashmap1, "title") + "</A>\n");
            }

            stringbuffer.append("<HR>\n");
        }
        stringbuffer.append("<P>Notes on this topic: <P>\n");
        for(int j = 0; j < array.size(); j++)
        {
            HashMap hashmap2 = (HashMap)array.at(j);
            stringbuffer.append("<P><A HREF=../" + TextUtils.getValue(hashmap2, "filename") + ">" + TextUtils.getValue(hashmap2, "title") + "</A>\n");
        }

        hashmap.put("body", stringbuffer.toString());
        return SupportNoteUtils.writeFile(s + TextUtils.getValue(hashmap, "filename"), topicKeys, hashmap, publicTemplate);
    }

    static String getFullTopicLink(HashMap hashmap)
    {
        HashMap hashmap1 = (HashMap)hashmap.get("parentTopic");
        String s = "<A HREF=../" + TextUtils.getValue(hashmap, "filename") + ">" + TextUtils.getValue(hashmap, "title") + "</A>";
        if(hashmap1 != null)
            return getFullTopicLink(hashmap1) + " : " + s;
        else
            return s;
    }

    static void writeAllFile(String s, Array array, String s1, String s2)
    {
        writeIndexFile(s, array, "Notes (full version)", "all.htm", true, (String[][])null, s1, s2);
    }

    static void writeIndexFile(String s, Array array, String s1, String s2)
    {
        writeIndexFile(s, array, "Notes", "index.htm", false, (String[][])null, s1, s2);
    }

    static void writeSiteViewIndexFile(String s, Array array, String s1, String s2)
    {
        String as[][] = {
            {
                "product", "SiteView"
            }
        };
        writeIndexFile(s, array, "SiteView Notes", "SiteViewIndex.htm", false, as, s1, s2);
    }

    static void writeSiteViewNTIndexFile(String s, Array array, String s1, String s2)
    {
        String as[][] = {
            {
                "product", "SiteView"
            }, {
                "product", "/Unix/c"
            }
        };
        writeIndexFile(s, array, "SiteView NT Notes", "SiteViewNTIndex.htm", false, as, s1, s2);
    }

    static void writeSiteViewUnixIndexFile(String s, Array array, String s1, String s2)
    {
        String as[][] = {
            {
                "product", "SiteView"
            }, {
                "product", "/NT/c"
            }
        };
        writeIndexFile(s, array, "SiteView Unix Notes", "SiteViewUnixIndex.htm", false, as, s1, s2);
    }

    static void writeSiteSeerIndexFile(String s, Array array, String s1, String s2)
    {
        String as[][] = {
            {
                "product", "SiteSeer"
            }
        };
        writeIndexFile(s, array, "SiteSeer Notes", "SiteSeerIndex.htm", false, as, s1, s2);
    }

    static void writeBugIndexFile(String s, Array array)
    {
        String as[][] = {
            {
                "releases", bugVersion
            }, {
                "title", "/Bug/i"
            }
        };
        writeIndexFile(s, array, "SiteView Known Bugs for Release " + bugVersion, "BugIndex.htm", false, as, "", "");
    }

    static void writePatchIndexFile(String s, Array array)
    {
        String as[][] = {
            {
                "releases", patchVersion
            }, {
                "title", "/Patch/i"
            }
        };
        writeIndexFile(s, array, "SiteView Patches for Release " + patchVersion, "PatchIndex.htm", false, as, "", "");
    }

    static void writeIndexFile(String s, Array array, String s1, String s2, boolean flag, String as[][], String s3, String s4)
    {
        HashMap hashmap = new HashMap();
        String s5 = "";
        String s6 = "";
        if(s3.length() > 0)
        {
            long l = 31L;
            if(s3.equalsIgnoreCase("Week"))
                l = 7L;
            else
            if(s3.equalsIgnoreCase("Day"))
                l = 1L;
            else
            if(s3.equalsIgnoreCase("Month"))
                l = 30L;
            long l2 = (new Date()).getTime();
            Date date = new Date(l2 - l * (long)TextUtils.DAY_SECONDS * 1000L);
            s5 = TextUtils.prettyDateDate(date);
            s2 = s3 + s2;
            s1 = s1 + " New or Revised in Last " + s3;
            s6 = "(since " + s5 + ")";
        }
        if(s4.length() > 0)
        {
            s2 = "hot" + s2;
            s1 = "Popular " + s1;
        }
        String s7 = MAIL_DIRECTORY + s2;
        s7 = TextUtils.replaceString(s7, ".htm", ".txt");
        s2 = INDEXES_DIRECTORY + s2;
        hashmap.put("title", s1);
        long l1 = 0L;
        if(s5.length() > 0)
            l1 = (long)TextUtils.stringToDateSeconds(s5) * 1000L;
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        StringBuffer stringbuffer2 = new StringBuffer();
        HashMap hashmap1 = new HashMap();
        stringbuffer.append("<H2>" + s1 + "</H2>");
        if(s6.length() > 0)
            stringbuffer.append(s6);
        stringbuffer.append("<HR><P>\n");
        for(int i = 0; i < array.size(); i++)
        {
            boolean flag1 = false;
            HashMap hashmap2 = (HashMap)array.at(i);
            Array array1 = (Array)hashmap2.get("notes");
            StringBuffer stringbuffer4 = new StringBuffer();
            if(array1 == null)
                continue;
            for(int j = 0; j < array1.size(); j++)
            {
                HashMap hashmap3 = (HashMap)array1.at(j);
                boolean flag2 = true;
                if(as != null)
                {
                    int k = 0;
                    do
                    {
                        if(k >= as.length)
                            break;
                        String s12 = as[k][0];
                        if(!TextUtils.match(TextUtils.getValue(hashmap3, s12), as[k][1]))
                        {
                            flag2 = false;
                            break;
                        }
                        k++;
                    } while(true);
                }
                if(l1 > 0L)
                {
                    String s8 = TextUtils.getValue(hashmap3, "created");
                    if(s8.length() > 0)
                    {
                        long l3 = (long)TextUtils.stringToDateSeconds(s8) * 1000L;
                        if(l3 >= l1)
                            flag1 = true;
                    }
                    long l4 = (long)TextUtils.stringToDateSeconds(TextUtils.getValue(hashmap3, "updated")) * 1000L;
                    if(l4 < l1)
                        flag2 = false;
                }
                if(s4.length() > 0)
                {
                    String s9 = TextUtils.getValue(hashmap3, "weight");
                    if(!TextUtils.match(s9, s4))
                        flag2 = false;
                }
                if(!flag2)
                    continue;
                stringbuffer4.append("<P>");
                if(flag)
                    stringbuffer4.append("<DL><DT>");
                stringbuffer4.append("<A HREF=../" + TextUtils.getValue(hashmap3, "filename") + ">" + TextUtils.getValue(hashmap3, "title") + "</A>\n");
                if(hashmap1.get(TextUtils.getValue(hashmap3, "filename")) == null)
                {
                    String s10 = "  " + TextUtils.getValue(hashmap3, "title") + Platform.FILE_NEWLINE + "     http://www.dragonflow.com/support/" + TextUtils.getValue(hashmap3, "filename") + Platform.FILE_NEWLINE + Platform.FILE_NEWLINE;
                    if(flag1)
                        stringbuffer1.append(s10);
                    else
                        stringbuffer2.append(s10);
                    hashmap1.put(TextUtils.getValue(hashmap3, "filename"), hashmap3);
                }
                if(!flag)
                    continue;
                stringbuffer4.append("</DT><DD>" + TextUtils.getValue(hashmap3, "body") + "</DD></DL><P>\n");
                if(hashmap1.get(TextUtils.getValue(hashmap3, "filename")) != null)
                    continue;
                String s11 = "  " + TextUtils.getValue(hashmap3, "body") + Platform.FILE_NEWLINE + TextUtils.filledString('-', 70) + Platform.FILE_NEWLINE;
                if(flag1)
                    stringbuffer1.append(s11);
                else
                    stringbuffer2.append(s11);
            }

            if(stringbuffer4.length() > 0)
                stringbuffer.append("<DL><DT><B>" + getFullTopicLink(hashmap2) + "</B></DT>" + "<DD>" + stringbuffer4 + "</DD></DL><P>\n");
        }

        hashmap.put("body", stringbuffer.toString());
        SupportNoteUtils.writeFile(s + s2, indexKeys, hashmap, publicTemplate);
        try
        {
            StringBuffer stringbuffer3 = new StringBuffer();
            if(stringbuffer1.length() > 0)
                stringbuffer3.append("New Support Notes" + Platform.FILE_NEWLINE + stringbuffer1);
            if(stringbuffer2.length() > 0)
                stringbuffer3.append("Revised Support Notes: " + Platform.FILE_NEWLINE + stringbuffer2);
            if(stringbuffer3.length() > 0)
                FileUtils.writeFile(TextUtils.replaceString(sourceDirectoryName, "/notes", "") + "/" + s7, stringbuffer3.toString());
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not write mail file " + s7 + "Exception: " + ioexception);
        }
    }

    static int compareVersions(String s, String s1)
    {
        return getVersionInt(s) - getVersionInt(s1);
    }

    static int getVersionInt(String s)
    {
        int i = 0;
        int j = s.indexOf(".");
        if(j != -1)
        {
            i += TextUtils.toInt(s.substring(0, j)) * 100;
            int k = s.indexOf("c");
            if(k == -1)
                k = s.indexOf("b");
            if(k == -1)
                k = s.indexOf("a");
            if(k != -1)
            {
                i += TextUtils.toInt(s.substring(j + 1, k)) * 10;
                i += TextUtils.toInt(s.substring(k + 1));
            } else
            {
                System.out.println("Bad version number: " + s);
            }
        }
        return i;
    }

    static void searchDocs(String s, String s1, String s2, String s3)
    {
        String s4 = s2;
        if(s4.length() == 0)
            s4 = "all products";
        else
        if(s4.equals("NT"))
            s4 = "SiteView NT";
        else
        if(s4.equals("Unix"))
            s4 = "SiteView Unix";
        else
        if(s4.indexOf("SiteReliance") >= 0 || s4.indexOf("CentraScope") >= 0)
        {
            s4 = "CentraScope";
            s2 = "";
        }
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<FORM METHOD=POST ACTION=support-search.pl>\n<INPUT TYPE=TEXT name=search size=25 value=\"" + s1 + "\">\n" + "<input type=submit name=submit value=\"Search\">\n" + "<input type=hidden name=product value=\"" + s2 + "\">\n" + "<BR>\n" + "<small>searching in " + s4 + "</small>\n" + "<P>\n");
        if(s2.length() > 0)
        {
            if(!s1.startsWith("+"))
                s1 = "+" + s1;
            s1 = "+product:" + s2 + " " + s1;
        }
        Hits hits = null;
        try
        {
            IndexSearcher indexsearcher = new IndexSearcher(s + "index");
            StandardAnalyzer standardanalyzer = new StandardAnalyzer();
			org.apache.lucene.search.Query query = QueryParser.parse(s1, "body", standardanalyzer);
            hits = indexsearcher.search(query);
            if(hits.length() > 0)
            {
                stringbuffer.append("" + hits.length() + " total matching\tnotes<P>\n");
                stringbuffer.append("<DL>\n");
                for(int i = 0; i < hits.length(); i++)
                {
                    stringbuffer.append("<DT><B><A HREF=../" + hits.doc(i).get("path") + ">" + hits.doc(i).get("title") + "</A></B>\n");
                    stringbuffer.append("<DD><FONT SIZE=-1>" + getSummary(hits.doc(i).get("body")) + "</FONT>\n");
                }

                stringbuffer.append("</DL>\n");
            } else
            {
                stringbuffer.append("no notes matched the search\n");
            }
            indexsearcher.close();
            String s7 = FileUtils.readFile(s + "publicTemplate.txt").toString();
            s7 = TextUtils.replaceString(s7, "%title%", "Support Search Results");
            s7 = TextUtils.replaceString(s7, "%body%", stringbuffer.toString());
            System.out.println(s7);
        }
        catch(Exception exception)
        {
            System.out.println("Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        String s5 = s1.replace(',', '|') + "," + s2.replace(',', '|');
        Date date = new Date();
        String s6 = "D:\\SupportSearch_" + TextUtils.numberToString(date.getYear() + 1900) + "_" + TextUtils.numberToString(date.getMonth() + 1) + ".log";
        String s8 = TextUtils.dateToString(date) + "," + s3 + "," + hits.length() + "," + s5;
        appendToFile(s6, s8);
        if(hits != null && hits.length() == 0)
            appendToFile("D:\\NoHitSearches.log", s8);
    }

    static void appendToFile(String s, String s1)
    {
        RandomAccessFile randomaccessfile = null;
        try
        {
            randomaccessfile = new RandomAccessFile(s, "rw");
            randomaccessfile.seek(randomaccessfile.length());
            randomaccessfile.writeBytes(s1 + "\r\n");
        }
        catch(IOException ioexception)
        {
            System.err.println("Could not open log file " + randomaccessfile);
        }
        finally
        {
            try
            {
                if(randomaccessfile != null)
                    randomaccessfile.close();
            }
            catch(IOException ioexception1) { }
        }
    }

    static String getSummary(String s)
    {
        String s1 = s;
        if(s1.length() > MAX_SUMMARY_LENGTH)
            s1 = s1.substring(0, MAX_SUMMARY_LENGTH) + "...";
        int i;
        while((i = s1.indexOf("<")) >= 0) 
        {
            int j = s1.indexOf(">", i);
            if(j == -1)
                s1 = s1.substring(0, i);
            else
                s1 = s1.substring(0, i) + s1.substring(j + 1);
        }
        return s1;
    }

    HashMap getWeightMap()
    {
        if(internalWeightMap == null)
        {
            internalWeightMap = new HashMap();
            internalWeightMap.put("common", new Integer(5));
            internalWeightMap.put("frequent", new Integer(4));
            internalWeightMap.put("normal", new Integer(3));
            internalWeightMap.put("infrequent", new Integer(2));
            internalWeightMap.put("rare", new Integer(1));
        }
        return internalWeightMap;
    }

    public boolean execute(Object obj, Object obj1)
    {
        HashMap hashmap = getWeightMap();
        Integer integer = (Integer)hashmap.get(TextUtils.getValue((HashMap)obj, "weight"));
        Integer integer1 = (Integer)hashmap.get(TextUtils.getValue((HashMap)obj1, "weight"));
        if(integer != null && integer1 != null)
            return integer.intValue() > integer1.intValue();
        else
            return false;
    }

    static boolean debug = false;
    static long recentDays = 30L;
    static String hotWeight = "common";
    static String bugVersion = "nobugversion";
    static String patchVersion = "nopatchversion";
    public static String TOPICS_DIRECTORY = "topics/";
    public static String INDEXES_DIRECTORY = "indexes/";
    public static String MAIL_DIRECTORY = "mail/";
    static String notePublicTemplate = "TEMPLATE NOT FOUND";
    static String noteInternalTemplate = "TEMPLATE NOT FOUND";
    static String publicTemplate = "TEMPLATE NOT FOUND";
    static String noteRedirectTemplate = "TEMPLATE NOTE FOUND";
    static String topicKeys[] = {
        "title", "body", "filename"
    };
    static String indexKeys[] = {
        "title", "body", "filename"
    };
    static String sourceDirectoryName = "F:/supportsite/notes/";
    static int MAX_SUMMARY_LENGTH = 120;
    static HashMap internalWeightMap = null;

}