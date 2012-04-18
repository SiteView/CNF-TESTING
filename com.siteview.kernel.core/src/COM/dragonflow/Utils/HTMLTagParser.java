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

import java.util.Enumeration;
import java.util.Vector;

import COM.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.BinaryPredicate;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils

public class HTMLTagParser
    implements BinaryPredicate
{

    static HashMap closeMap;
    public static String POST_EQUALS_TAG = "\\eq.";
    private boolean reading;
    private boolean endOfTag;
    private String source;
    private int readIndex;
    private String lastAttribute;
    private String lastValue;
    private char currentChar;
    public Array tags;
    boolean ignoreComments;
    public boolean ignoreScripts;
    public boolean ignoreNoscripts;
    public boolean recordIndexes;
    private HashMap currentTag;
    private int currentOrder;
    private HashMap tagMap;
    private HashMap openTagStartIndexes;
    private HashMap openTagObjectLists;
    private HashMap openTagFrames;
    public String terminatorTag;
    static final String TEST_INPUT = "<HTML><HEAD><TITLE>Test Page</TITLE></HEAD><BODY FGCOLOR=#FFFFFF>\n<H3>Heading</H3>\n<SCRIPT LANGUAGE=\"JavaScript\">\\nimtag = \"<P><input type=radio name=radioscript value=radios checked>\";\n</SCRIPT>\n<P><A HREF=\"http://global.link.com\">Fully-qualified link with quotes</A>\n<P><a href=/partial/path.htm>Partial link with no quotes</A>\n<form method=POST action=\"http://post.form.com\">\n<P><input type=text value=\"text value\" name=textname size=40>\n<P><INPUT TYPE=PASSWORD value=\"password value\" name=passwordname size=40>\n<P><textarea col2=40 row=3 name=textareaname>This is a long bunch of text in a text area</textarea>\n<P><input type=checkbox name=\"checkname\" value=checkvalue checked>\n<P><input type=checkbox name=\"checkname2\" value=checkvalue2>\n<P><input type=image src=http://www.colorado.gov/images/bannerBtnHm_r1_c1.gif >\n<P><input type=radio name=radio1 value=radio11>\n<P><input type=radio name=radio1 value=radio12 checked>\n<P><input type=radio name=radio1 value=radio13>\n<P><input type=radio name=radio2 value=radio21>\n<P><input type=radio name=radio2 value=radio22>\n<P><input type=radio name=radio2 value=radio23>\n<P><select multiple name=selectname>\n<option selected value=\"one\">One\n<OPTION value=two>Two</option>\n<option value=three>Three things</option>\n<option value=\"four\">Four\n<option value=five selected>Five\n</select><P><select name=selectname1>\n<option>One\n<OPTION>Two\n<option>Three things\n<option>Four\n<option selected>Five\n</select><!--onelinecomment-->\n<P><input type=submit name=button value=\"Click Button\">\n<P><input type=submit name=image value=\"Click Image\">\n<input type=reset><!-- <A href=in/comment.gif>In a Comment</A> --><P><A href=last/link.htm>Last Link</a>\n</body></html>";

    public Array getVariables(HashMap hashmap, String s, String s1)
    {
        Array array = (Array)hashmap.get("contentObjects");
        if(array != null)
        {
            return getVariables(array, s, s1);
        } else
        {
            return new Array();
        }
    }

    public Array getFormInputs(HashMap hashmap)
    {
        Array array = (Array)hashmap.get("contentObjects");
        if(array != null)
        {
            return getFormInputs(array, null, null);
        } else
        {
            return new Array();
        }
    }

    public Array getFormInputs(Array array)
    {
        return getFormInputs(array, null, null);
    }

    public String FindVar(Array array, String s)
    {
        s = s + "=";
        for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();)
        {
            String s1 = (String)enumeration.nextElement();
            if(s1.startsWith(s))
            {
                return s1;
            }
        }

        return null;
    }

    public HashMap FindVariable(Array array, String s)
    {
        s = s + "=";
        for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();)
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_value");
            if(s1.startsWith(s))
            {
                return hashmap;
            }
        }

        return null;
    }

    void AddValue(Array array, HashMap hashmap, String s, String s1)
    {
        if(s.indexOf("=") >= 0)
        {
            s = COM.dragonflow.Utils.TextUtils.replaceString(s, "=", POST_EQUALS_TAG);
        }
        hashmap.put("_value", s + "=" + s1);
        array.add(hashmap);
    }

    public Array getVariables(Array array, String s, String s1)
    {
        Array array1 = getFormInputs(array, s, s1);
        Array array2 = new Array();
        HashMap hashmap;
        for(Enumeration enumeration =  (Enumeration) array1.iterator(); enumeration.hasMoreElements(); array2.add(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_value")))
        {
            hashmap = (HashMap)enumeration.nextElement();
        }

        return array2;
    }

    private Array getFormInputs(Array array, String s, String s1)
    {
        Array array1 = new Array();
        HashMap hashmap = new HashMap();
        Enumeration enumeration = findTags(array, "input");
        while (enumeration.hasMoreElements()) {
            HashMap hashmap1 = (HashMap)enumeration.nextElement();
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "type").toLowerCase();
            if(s2.length() == 0)
            {
                s2 = "text";
            }
            if(s2.equals("text") || s2.equals("hidden") || s2.equals("password"))
            {
                AddValue(array1, hashmap1, COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name"), COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value"));
            } else
            if(s2.equals("radio"))
            {
                String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                if(hashmap.get(s4) == null)
                {
                    hashmap.put(s4, hashmap1);
                }
                if(hashmap1.get("checked") != null)
                {
                    AddValue(array1, hashmap1, COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name"), COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value"));
                }
            } else
            if(s2.equals("checkbox"))
            {
                if(hashmap1.get("checked") != null)
                {
                    AddValue(array1, hashmap1, COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name"), COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value"));
                }
            } else
            if(s2.equals("submit"))
            {
                if(hashmap1.get("name") != null)
                {
                    if(s == null)
                    {
                        AddValue(array1, hashmap1, COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name"), COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value"));
                    } else
                    if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name").equals(s) && COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value").equals(s1))
                    {
                        AddValue(array1, hashmap1, s, s1);
                    }
                }
            } else
            if(s2.equals("image"))
            {
                HashMap hashmap2 = new HashMap(hashmap1);
                if(hashmap1.get("name") != null)
                {
                    if(s == null)
                    {
                        AddValue(array1, hashmap1, COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name") + ".x", "1");
                        AddValue(array1, hashmap2, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "name") + ".y", "1");
                    } else
                    if(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name").equals(s))
                    {
                        AddValue(array1, hashmap1, s + ".x", "1");
                        AddValue(array1, hashmap2, COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "name") + ".y", "1");
                    }
                }
            }
        } 
        Enumeration enumeration1 = (Enumeration) hashmap.keys();
        while (enumeration1.hasMoreElements()) {
            String s3 = (String)enumeration1.nextElement();
            HashMap hashmap3 = (HashMap)hashmap.get(s3);
            if(FindVariable(array1, s3) == null)
            {
                AddValue(array1, hashmap3, s3, COM.dragonflow.Utils.TextUtils.getValue(hashmap3, "value"));
            }
        }
        HashMap hashmap4;
        for(Enumeration enumeration2 = findTags(array, "textarea"); enumeration2.hasMoreElements(); AddValue(array1, hashmap4, COM.dragonflow.Utils.TextUtils.getValue(hashmap4, "name"), COM.dragonflow.Utils.TextUtils.getValue(hashmap4, "contents")))
        {
            hashmap4 = (HashMap)enumeration2.nextElement();
        }

        Enumeration enumeration3 = findTags(array, "select");
        while (enumeration3.hasMoreElements()) {
            HashMap hashmap5 = (HashMap)enumeration3.nextElement();
            HashMap hashmap6 = null;
            String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap5, "name");
            boolean flag = hashmap5.get("multiple") != null;
            boolean flag1 = false;
            Enumeration enumeration4 = findTags(hashmap5, "option");
            while (enumeration4.hasMoreElements()) {
                HashMap hashmap7 = (HashMap)enumeration4.nextElement();
                if(hashmap7.get("selected") != null)
                {
                    flag1 = true;
                    if(flag)
                    {
                        String s7 = "value";
                        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap7, s7).length() == 0)
                        {
                            s7 = "contents";
                        }
                        AddValue(array1, hashmap7, s5, COM.dragonflow.Utils.TextUtils.getValue(hashmap7, s7));
                    } else
                    {
                        hashmap6 = hashmap7;
                    }
                } else
                if(hashmap6 == null)
                {
                    hashmap6 = hashmap7;
                }
            } 
            if(flag)
            {
                if(!flag1)
                {
                    AddValue(array1, hashmap6, s5, "");
                }
            } else
            if(hashmap6 != null)
            {
                String s6 = "value";
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap6, s6).length() == 0)
                {
                    s6 = "contents";
                }
                AddValue(array1, hashmap6, s5, COM.dragonflow.Utils.TextUtils.getValue(hashmap6, s6));
            }
        } 
        Sorting.sort(array1, this);
        return array1;
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1)
    {
        HashMap hashmap = (HashMap)obj;
        HashMap hashmap1 = (HashMap)obj1;
        return COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_order")) < COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_order"));
    }

    public Enumeration findTags(HashMap hashmap, String s)
    {
        Array array = (Array)hashmap.get("contentObjects");
        if(array != null)
        {
            return findTags(array, s);
        } else
        {
            return (Enumeration) (new Array()).iterator();
        }
    }

    public Enumeration findTags(HashMap hashmap, String as[])
    {
        Array array = (Array)hashmap.get("contentObjects");
        if(array != null)
        {
            return findTags(array, as);
        } else
        {
        	return (Enumeration) (new Array()).iterator();
        }
    }

    public Enumeration findTags(String s)
    {
        return findTags(tags, s);
    }

    public Enumeration findTags(Array array, String s)
    {
        s = s.toLowerCase();
        Array array1 = new Array();
        for(int i = 0; i < array.size(); i++)
        {
            HashMap hashmap = (HashMap)array.get(i);
            if(!s.equals(hashmap.get("tag")))
            {
                continue;
            }
            if(COM.dragonflow.StandardMonitor.URLMonitor.debugURL != 0)
            {
                String s1 = (String)hashmap.get("src");
                if(s1 != null)
                {
                    java.lang.System.out.println("HTMLTagParser.java - found a src tag: " + hashmap.toString());
                }
            }
            array1.add(hashmap);
        }

        return  (Enumeration) array1.iterator();
    }

    public Enumeration findTags(Array array, String as[])
    {
        int i = as.length;
        String as1[] = new String[i];
        for(int j = 0; j < i; j++)
        {
            as1[j] = as[j].toLowerCase();
        }

        Array array1 = new Array();
        for(int l = 0; l < array.size(); l++)
        {
            HashMap hashmap = (HashMap)array.get(l);
            String s = (String)hashmap.get("tag");
            for(int k = 0; k < i; k++)
            {
                if(as1[k].equals(s))
                {
                    array1.add(hashmap);
                }
            }

        }

        return  (Enumeration) array1.iterator();
    }

    public HTMLTagParser(String s, String as[])
    {
        this(s, as, true);
    }

    public HTMLTagParser(String s, String as[], boolean flag)
    {
        this(s, as, flag, "");
    }

    public HTMLTagParser(String s, String as[], boolean flag, String s1)
    {
        reading = true;
        endOfTag = false;
        source = "";
        readIndex = 0;
        lastAttribute = null;
        lastValue = null;
        currentChar = ' ';
        tags = new Array();
        ignoreComments = true;
        ignoreScripts = true;
        ignoreNoscripts = true;
        recordIndexes = false;
        currentTag = null;
        currentOrder = 0;
        tagMap = new HashMap();
        openTagStartIndexes = new HashMap();
        openTagObjectLists = new HashMap();
        openTagFrames = new HashMap();
        terminatorTag = "";
        terminatorTag = new String(s1.toLowerCase());
        source = new String(s);
        ignoreComments = flag;
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].startsWith("/"))
            {
                tagMap.add(new String(as[i].substring(1).toLowerCase()), "");
            }
            tagMap.add(new String(as[i].toLowerCase()), "");
        }

    }

    public String getSource()
    {
        return new String(source);
    }

    public Array process()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stripSpace();
        if(ignoreScripts)
        {
            stripScript("<script", "</script>");
        }
        if(ignoreNoscripts)
        {
            stripScript("<noscript", "</noscript>");
        }
        for(char c = currentChar; reading; c = readChar())
        {
            if(c != '<')
            {
                continue;
            }
            StringBuffer stringbuffer1 = new StringBuffer(150);
            for(c = readChar(); reading && !java.lang.Character.isWhitespace(c) && c != '>'; c = readChar())
            {
                stringbuffer1.append(c);
            }

            if(COM.dragonflow.StandardMonitor.URLMonitor.debugURL != 0)
            {
                java.lang.System.out.println("HTMLTagParser - tag found: " + stringbuffer1.toString());
            }
            if(stringbuffer1.length() <= 0)
            {
                continue;
            }
            String s = stringbuffer1.toString().toLowerCase();
            if(s.startsWith("!--"))
            {
                if(c != '>' || !s.endsWith("--"))
                {
                    readComment();
                }
                continue;
            }
            if(tagMap.get(s) == null)
            {
                continue;
            }
            if(s.startsWith("/"))
            {
                autoCloseTags(s);
                if(terminatorTag.length() > 0 && terminatorTag.equals(s))
                {
                    reading = false;
                }
                continue;
            }
            closeTag(s);
            currentTag = new HashMapOrdered(true);
            currentTag.put("tag", s);
            currentTag.put("_order", "" + currentOrder++);
            if(recordIndexes)
            {
                currentTag.put("_startTagIndex", "" + (readIndex - (s.length() + 2)));
            }
            tags.add(currentTag);
            addToOpenTags(currentTag);
            readAttributes();
            if(recordIndexes)
            {
                currentTag.put("_endTagIndex", "" + readIndex);
            }
            if(tagMap.get("/" + s) != null)
            {
                openTagStartIndexes.put("/" + s, new Integer(readIndex));
                openTagFrames.put("/" + s, currentTag);
            }
        }

        do
        {
            Enumeration enumeration = (Enumeration) openTagFrames.keys();
            if(enumeration.hasMoreElements())
            {
                String s1 = (String)enumeration.nextElement();
                autoCloseTags(s1);
            } else
            {
                return tags;
            }
        } while(true);
    }

    void addToOpenTags(HashMap hashmap)
    {
        String s;
        Array array;
        for(Enumeration enumeration = (Enumeration) openTagFrames.keys(); enumeration.hasMoreElements(); openTagObjectLists.put(s, array))
        {
            s = (String)enumeration.nextElement();
            array = (Array)openTagObjectLists.get(s);
            if(array == null)
            {
                array = new Array();
            }
            array.add(hashmap);
        }

    }

    void autoCloseTags(String s)
    {
        closeTag(s);
        for(Enumeration enumeration = (Enumeration) closeMap.values(s); enumeration.hasMoreElements(); closeTag((String)enumeration.nextElement())) { }
    }

    void closeTag(String s)
    {
        String s1 = s;
        if(!s.startsWith("/"))
        {
            s1 = "/" + s;
        }
        HashMap hashmap = (HashMap)openTagFrames.get(s1);
        if(hashmap != null)
        {
            int i = ((java.lang.Integer)openTagStartIndexes.get(s1)).intValue();
            int j = readIndex;
            j -= s.length() + 2;
            String s2 = "";
            if(j > i)
            {
                s2 = source.substring(i, j);
            }
            hashmap.put("contents", s2);
            if(recordIndexes)
            {
                hashmap.put("_startContentIndex", "" + i);
                hashmap.put("_endContentIndex", "" + j);
                hashmap.put("_endTagIndex", "" + readIndex);
            }
            Array array = (Array)openTagObjectLists.get(s1);
            if(array != null)
            {
                hashmap.put("contentObjects", array);
                openTagObjectLists.remove(s1);
            }
            openTagFrames.remove(s1);
            openTagStartIndexes.remove(s1);
        }
    }

    void readComment()
    {
        if(!ignoreComments)
        {
            return;
        }
        int i = 32;
        char c = ' ';
        for(char c1 = readChar(); reading && (i != 45 || c != 45 || c1 != '>'); c1 = readChar())
        {
            i = c;
            c = c1;
        }

    }

    void readAttributes()
    {
        endOfTag = currentChar == '>';
        do
        {
            if(endOfTag || !reading)
            {
                break;
            }
            parseAttribute();
            if(lastAttribute.length() > 0)
            {
                lastValue = COM.dragonflow.Utils.TextUtils.replaceString(lastValue, "&quot;", "\"");
                String s = lastAttribute.toLowerCase();
                if(currentTag.get(s) == null)
                {
                    currentTag.put(s, lastValue);
                }
            }
        } while(true);
    }

    private char readChar()
    {
        if(readIndex >= source.length())
        {
            reading = false;
            currentChar = '\uFFFF';
        } else
        {
            currentChar = source.charAt(readIndex++);
        }
        return currentChar;
    }

    void stripSpace()
    {
        for(; reading && java.lang.Character.isWhitespace(currentChar); readChar()) { }
    }

    void stripScript(String s, String s1)
    {
        int i1 = s1.length();
        String s2 = new String(source);
        s2 = s2.toLowerCase();
        java.util.Vector vector = new Vector();
        vector.addElement(new Integer(0));
        int j1 = 0;
        do
        {
            int i = s2.indexOf(s, j1);
            if(i < 0)
            {
                break;
            }
            j1 = i + 1;
            int k = s2.indexOf(s1, j1);
            if(k < 0)
            {
                break;
            }
            if(i > k)
            {
                java.lang.System.out.println("startIdx > endIdx" + i + " " + k);
                break;
            }
            j1 = k + i1;
            vector.addElement(new Integer(i));
            vector.addElement(new Integer(k + i1));
        } while(true);
        String s3 = "";
        for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
        {
            int j = ((java.lang.Integer)enumeration.nextElement()).intValue();
            if(enumeration.hasMoreElements())
            {
                int l = ((java.lang.Integer)enumeration.nextElement()).intValue();
                s3 = s3 + source.substring(j, l);
            } else
            {
                s3 = s3 + source.substring(j);
            }
        }

        source = s3;
    }

    String parseQuotedString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        char c = currentChar;
        readChar();
        do
        {
            if(!reading)
            {
                break;
            }
            if(currentChar == c)
            {
                readChar();
                break;
            }
            stringbuffer.append(currentChar);
            readChar();
        } while(true);
        return stringbuffer.toString();
    }

    String parseToken()
    {
        return parseToken("");
    }

    String parseToken(String s)
    {
        StringBuffer stringbuffer = null;
        do
        {
            if(!reading)
            {
                break;
            }
            if(stringbuffer == null)
            {
                if(currentChar == '"' || currentChar == '\'')
                {
                    return parseQuotedString();
                }
                stringbuffer = new StringBuffer();
            }
            if(java.lang.Character.isWhitespace(currentChar) || s.indexOf(currentChar) >= 0)
            {
                break;
            }
            if(currentChar == '>')
            {
                endOfTag = true;
                break;
            }
            stringbuffer.append(currentChar);
            readChar();
        } while(true);
        if(stringbuffer == null)
        {
            stringbuffer = new StringBuffer();
        }
        return stringbuffer.toString();
    }

    void parseAttribute()
    {
        lastAttribute = "";
        lastValue = "";
        stripSpace();
        lastAttribute = parseToken("=");
        stripSpace();
        if(currentChar == '=')
        {
            readChar();
            stripSpace();
            lastValue = parseToken();
        }
    }

    static 
    {
        closeMap = new HashMapOrdered(true);
        closeMap.add("/select", "/option");
        closeMap.add("/table", "/td");
        closeMap.add("/table", "/tr");
    }
}
