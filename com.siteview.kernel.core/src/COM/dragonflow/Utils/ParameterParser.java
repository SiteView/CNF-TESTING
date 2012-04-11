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

import java.util.LinkedList;
import java.util.StringTokenizer;

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils

public class ParameterParser
{

    private String _paramString;
    private java.util.List _paramsList;
    private java.util.List _holdList;
    private int _shellToEmulate;
    private boolean _insideQuotedString;
    private static final String BACKSLASH_LITERAL_MARKER = "SITEVIEW_BACKSLASH_LITERAL";
    private static final String QUOTE_LITERAL_MARKER = "SITEVIEW_QUOTE_LITERAL";
    static final String UNTERMINATED_STRING_ERROR = "ERROR: UNTERMINATED QUOTED STRING";
    public static final int BASH_SHELL = 0;
    public static final int WINDOWS_SHELL = 1;

    public ParameterParser(String s)
    {
        _paramString = s;
        _shellToEmulate = 0;
    }

    public ParameterParser(String s, int i)
        throws java.lang.IllegalArgumentException
    {
        _paramString = s;
        if(i < 0 || i > 1)
        {
            throw new IllegalArgumentException("Unknown shell");
        } else
        {
            _shellToEmulate = i;
            return;
        }
    }

    public java.util.List doParse()
    {
        preProcessEscapedCharacters();
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(_paramString, " \\\"", true);
        _paramsList = new LinkedList();
        _holdList = new LinkedList();
        _insideQuotedString = false;
        while(stringtokenizer.hasMoreTokens()) 
        {
            String s = stringtokenizer.nextToken();
            if(s.equals("\""))
            {
                if(_insideQuotedString)
                {
                    addHoldListToParamsList();
                }
                _insideQuotedString = !_insideQuotedString;
            } else
            {
                processToken(s);
            }
        }
        if(_insideQuotedString)
        {
            _paramsList.add("ERROR: UNTERMINATED QUOTED STRING");
        }
        return _paramsList;
    }

    private void preProcessEscapedCharacters()
    {
        _paramString = COM.dragonflow.Utils.TextUtils.replaceString(_paramString, "\\\\", "SITEVIEW_BACKSLASH_LITERALSITEVIEW_BACKSLASH_LITERAL");
        _paramString = COM.dragonflow.Utils.TextUtils.replaceString(_paramString, "\\\"", "SITEVIEW_BACKSLASH_LITERALSITEVIEW_QUOTE_LITERAL");
        if(_shellToEmulate == 1)
        {
            _paramString = COM.dragonflow.Utils.TextUtils.replaceString(_paramString, "\\", "SITEVIEW_BACKSLASH_LITERAL");
        } else
        {
            _paramString = COM.dragonflow.Utils.TextUtils.replaceString(_paramString, "\\", "");
        }
    }

    private void processToken(String s)
    {
        s = COM.dragonflow.Utils.TextUtils.replaceString(s, "SITEVIEW_BACKSLASH_LITERAL", "\\");
        s = COM.dragonflow.Utils.TextUtils.replaceString(s, "SITEVIEW_QUOTE_LITERAL", "\"");
        if(_insideQuotedString)
        {
            _holdList.add(s);
        } else
        if(!s.equals(" "))
        {
            _paramsList.add(s);
        }
    }

    private void addHoldListToParamsList()
    {
        String s = "";
        for(int i = 0; i < _holdList.size(); i++)
        {
            s = s + (String)_holdList.get(i);
        }

        _paramsList.add(s);
        _holdList.clear();
    }

    public static String arrayCmdToStringCmd(String as[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(" ");
            }
            boolean flag = !as[i].startsWith("\"");
            if(flag)
            {
                stringbuffer.append("\"");
            }
            stringbuffer.append(as[i]);
            if(flag)
            {
                stringbuffer.append("\"");
            }
        }

        return stringbuffer.toString();
    }
}
