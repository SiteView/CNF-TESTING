/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MultiLogMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MultiLogMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.MultiLogBase;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.TextUtils;
import COM.oroinc.text.perl.Perl5Util;
import jgl.HashMap;

public class MultiLogMonitor extends MultiLogBase
{

    private static StringProperty pLogPath;
    private static StringProperty pElementMatchCriteria;
    private static StringProperty pFileNameMatchCriteria;
    private static BooleanProperty pSearchFromStart;
    private static final String logPathName = "_logPath";
    private static final String elementMatchCriteriaName = "_matchCriteria";
    private static final String fileNameMatchCriteriaName = "_fileNameMatchCriteria";
    private static final String searchFromStartName = "_searchFromStart";
    private static final String logPathDisplayText = "<p>the directory containing the log files to be monitored.<br>for Unix remotes, use the 'Choose Server' link. For SSH remotes you <b>must</b> use the java library option and ssh1<br>for NT remotes, specify a UNC path to the file.  For example, \\\\machinename\\sharename\\filename.log</p>";
    private static final String fileNameMatchCriteriaDisplayText = "<p>the match expression to use when searching for files within the log directory<br>for Unix remotes, you must use a Unix style regex supported by grep<br>for NT, local or remote, use a <a href=/SiteView/docs/regexp.htm>regular expression</a></p>";
    private static final String searchFromStartDisplayText = "always search from the beginning of the file (useful for testing)";
    private static final String elementMatchCriteriaDisplayText = "<p>the <a href=/SiteView/docs/regexp.htm>regular expression</a> used to qualify a matching line.<br>";
    private String elemMatchExp;
    private Perl5Util perl;

    public MultiLogMonitor()
    {
        elemMatchExp = getProperty(pElementMatchCriteria);
        perl = new Perl5Util();
    }

    public int matchLine(String s, String s1)
    {
        if(elemMatchExp.length() == 0)
        {
            elemMatchExp = getProperty(pElementMatchCriteria);
        }
        return !perl.match(elemMatchExp, s1) ? 0 : 1;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pFileNameMatchCriteria)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, "missing file match expression");
                return s;
            }
        } else
        if(stringproperty == pLogPath)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, "missing log directory");
                return s;
            }
            if(!fileExists(s))
            {
                hashmap.put(stringproperty, "log directory not found");
                return s;
            }
        } else
        if(stringproperty == pElementMatchCriteria)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, "missing search expression");
                return s;
            }
            if(!TextUtils.isRegularExpression(s))
            {
                hashmap.put(stringproperty, "error parsing search expression(s)");
                return s;
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getLogPath()
    {
        return getProperty(pLogPath);
    }

    public String getFileNameMatchCriteria()
    {
        return getProperty(pFileNameMatchCriteria);
    }

    public String getElementMatchCriteria()
    {
        return getProperty(pElementMatchCriteria);
    }

    public boolean getSearchFromStart()
    {
        return getPropertyAsBoolean(pSearchFromStart);
    }

    static 
    {
        pLogPath = new StringProperty("_logPath");
        pLogPath.setDisplayText("Log File Directory", "<p>the directory containing the log files to be monitored.<br>for Unix remotes, use the 'Choose Server' link. For SSH remotes you <b>must</b> use the java library option and ssh1<br>for NT remotes, specify a UNC path to the file.  For example, \\\\machinename\\sharename\\filename.log</p>");
        pLogPath.setParameterOptions(true, 1, false);
        pFileNameMatchCriteria = new StringProperty("_fileNameMatchCriteria");
        pFileNameMatchCriteria.setDisplayText("File Name Match", "<p>the match expression to use when searching for files within the log directory<br>for Unix remotes, you must use a Unix style regex supported by grep<br>for NT, local or remote, use a <a href=/SiteView/docs/regexp.htm>regular expression</a></p>");
        pFileNameMatchCriteria.setParameterOptions(true, 2, false);
        pElementMatchCriteria = new StringProperty("_matchCriteria");
        pElementMatchCriteria.setDisplayText("Content Match", "<p>the <a href=/SiteView/docs/regexp.htm>regular expression</a> used to qualify a matching line.<br>");
        pElementMatchCriteria.setParameterOptions(true, 3, false);
        pSearchFromStart = new BooleanProperty("_searchFromStart");
        pSearchFromStart.setDisplayText("Search From Start", "always search from the beginning of the file (useful for testing)");
        pSearchFromStart.setParameterOptions(true, 4, false);
        StringProperty astringproperty[] = {
            pLogPath, pFileNameMatchCriteria, pElementMatchCriteria, pSearchFromStart
        };
        String s = (COM.dragonflow.StandardMonitor.MultiLogMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("matchCount > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("matchCount == 'n/a'\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Scans log files for specific log entries.");
        setClassProperty(s, "help", "MultiLogMon.htm");
        setClassProperty(s, "title", "Multi Log");
        setClassProperty(s, "class", "MultiLogMonitor");
        setClassProperty(s, "target", "_multiLogFile");
        setClassProperty(s, "topazName", "Multi Log");
        setClassProperty(s, "topazType", "System Resources");
        setClassProperty(s, "classType", "advanced");
        setClassProperty(s, "loadable", "false");
    }
}
