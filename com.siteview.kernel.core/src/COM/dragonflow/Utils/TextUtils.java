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

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import COM.dragonflow.Properties.HashMapOrdered;
import COM.oroinc.text.perl.Perl5Util;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;
import com.recursionsw.jgl.predicates.LessString;

// Referenced classes of package COM.dragonflow.Utils:
// StringBinaryPredicate, StringHashMapBinaryPredicate, ParameterParser,
// FileUtils,
// LocaleUtils, XSLUtils

public class TextUtils
{

    static final String CURRENT_STATE_START = "<!--CURRENTSTATE";
    static final String CURRENT_STATE_END = "ENDCURRENTSTATE-->";
    private static String pk = "SiteViewDragonflowSoftware";
    private static String pkpre = "(0x)";
    private static String indent = "";
    private static boolean decValuesInitialized = false;
    private static boolean useDecFormat = false;
    private static java.text.DecimalFormat DecFormat = null;
    private static java.text.DecimalFormat DecFormatFixed = null;
    private static String dateToStringFormat = "HH:mm:ss MM/dd/yyyy";
    private static String fileDateFormatPattern = "HH_mm-MM_dd_yyyy";
    public static final String dayLetters[] = {
        "U", "M", "T", "W", "R", "F", "S"
    };
    public static int MINUTE_SECONDS;
    public static int HOUR_SECONDS;
    public static int DAY_SECONDS;
    public static final int MONTH_DAYS[] = {
        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 
        30, 31
    };
    private static final String PRETTY_DATE_PATTERN_SETTING = "_prettyDateCustomPattern";
    private static boolean customPrettyDatePatternInitialized = false;
    private static java.lang.Object prettyDateSync = new Object();
    private static java.text.SimpleDateFormat prettyDateCustomFormatter = null;
    static String dayFileFormatWithTimePattern = "yyyy_MM_dd-HH_mm";
    static String dayFileFormatPattern = "yyyy_MM_dd";
    public static final String ALPHABETIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMERIC = "0123456789";
    public static final String PUNCTUATION = ",./<>?;':\"[]\\{}|=-+_)(*&^%$#@!`~";
    public static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String FILE_CHARS = ".-_";
    public static final String HOSTNAME_CHARS = ".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String WHITESPACE_CHARS = " \n\t";
    static String OFFSET_MINUTES = "$offsetMinutes=";
    static String GET_PROPERTY = "$value-";
    static String GET_PRIVATE = "$private-";
    public static String regExModifiers;
    public static String extRegExModifiers;
    public static String allRegExModifiers;
    static final boolean $assertionsDisabled; /* synthetic field */

    public TextUtils()
    {
    }

    private static synchronized void initDecValues()
    {
        if(decValuesInitialized)
        {
            return;
        }
        decValuesInitialized = true;
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        Enumeration enumeration = (Enumeration) hashmap.values("_noScientificNotation");
        String s = "";
        while (enumeration.hasMoreElements())
            {
            s = (String)enumeration.nextElement();
            if(s == null || s.length() <= 1)
            {
                continue;
            }
            useDecFormat = true;
            break;
        } 
        
        try
        {
            DecFormat = new DecimalFormat(s);
            DecFormatFixed = new DecimalFormat("#.#####");
        }
        catch(java.lang.NullPointerException nullpointerexception)
        {
            useDecFormat = false;
            DecFormat = null;
            java.lang.System.out.println("_noScientificNotation(master.config) = Should never have a null format for DecimalFormat here: '" + nullpointerexception.toString() + "'");
            nullpointerexception.printStackTrace();
        }
        catch(java.lang.IllegalArgumentException illegalargumentexception)
        {
            useDecFormat = false;
            DecFormat = null;
            java.lang.System.out.println("_noScientificNotation(master.config) = invalid DecimalFormat format: '" + illegalargumentexception.toString() + "'");
            illegalargumentexception.printStackTrace();
        }
    }

    public static boolean getuseDecFormat()
    {
        return TextUtils.isUseDecFormat();
    }

    public static void hackQuote(String s)
        throws java.lang.Exception
    {
        java.io.BufferedReader bufferedreader = FileUtils.MakeInputReader(new FileInputStream(s));
        while (true)
        {
            String s1 = bufferedreader.readLine();
            if(s1 != null)
            {
                s1 = TextUtils.replaceChar(s1, '"', "\\\"");
                java.lang.System.out.println("+\"" + s1 + "\\n\"");
            } else
            {
                return;
            }
        }
    }

    public static void main(String args[])
        throws java.lang.Exception
    {
        if(args.length > 0)
        {
            if(args[0].equals("-version"))
            {
                java.util.Date date = new Date(java.lang.System.currentTimeMillis());
                String s = TextUtils.prettyDate(date) + " Build: " + date.getTime() + "\n";
                java.lang.System.out.println("Creating date.txt with: " + s);
                FileUtils.writeFile("date.txt", s);
            } else
            if(args[0].equals("-delay"))
            {
                COM.dragonflow.SiteView.Platform.sleep(java.lang.Long.parseLong(args[1]));
            } else
            if(args.length > 1)
            {
                if(args[0].equals("-s"))
                {
                    java.lang.System.out.println(TextUtils.obscure(args[1]));
                } else
                if(args[0].equals("-d"))
                {
                    java.lang.System.out.println(TextUtils.enlighten(args[1]));
                } else
                {
                    java.lang.System.out.println("bad option");
                }
            }
            java.lang.System.exit(0);
        }
        try
        {
            java.lang.Class.forName("TextUtils");
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println("Exception: " + exception);
        }
        java.lang.System.exit(0);
        float f = 3.402823E+038F;
        for(int i = 0; i < 200; i++)
        {
            java.lang.System.out.println(i + ":   " + f + ", " + TextUtils.floatToString(f, 2));
            java.lang.System.out.println(i + ": " + -f + ", " + TextUtils.floatToString(-f, 2));
            f /= 10F;
        }

        f = 2.147484E+009F;
        for(int j = 0; j < 200; j++)
        {
            java.lang.System.out.println(j + ":   " + f + ", " + TextUtils.floatToString(f, 2));
            java.lang.System.out.println(j + ": " + -f + ", " + TextUtils.floatToString(-f, 2));
            f /= 10F;
        }

    }

    public static int dayLetterToNumber(String s)
    {
        if(s.length() > 0)
        {
            switch(s.toUpperCase().charAt(0))
            {
            case 85: // 'U'
                return 0;

            case 77: // 'M'
                return 1;

            case 84: // 'T'
                return 2;

            case 87: // 'W'
                return 3;

            case 82: // 'R'
                return 4;

            case 70: // 'F'
                return 5;

            case 83: // 'S'
                return 6;
            }
        }
        return -1;
    }

    public static String obscure(String s)
    {
        if(s.length() == 0)
        {
            return s;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            int j = s.charAt(i);
            j += pk.charAt(i % pk.length());
            int k = j / 16;
            int l = j - k * 16;
            stringbuffer.append((char)(65 + k));
            stringbuffer.append((char)(65 + l));
        }

        return pkpre + stringbuffer.toString();
    }

    public static String enlighten(String s)
    {
        if(!s.startsWith(pkpre))
        {
            return s;
        }
        s = s.substring(pkpre.length());
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length() / 2;
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            int l = s.charAt(j++) - 65;
            int i1 = s.charAt(j++) - 65;
            int j1 = l * 16 + i1;
            j1 -= pk.charAt(k % pk.length());
            stringbuffer.append((char)j1);
        }

        return stringbuffer.toString();
    }

    public static String prettyDate()
    {
        return TextUtils.prettyDate(COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static String getValue(HashMap hashmap, String s)
    {
        java.lang.Object obj = hashmap.get(s);
        if(obj == null)
        {
            return "";
        }
        if(!(obj instanceof String))
        {
            return "";
        } else
        {
            return (String)obj;
        }
    }

    public static Array getMultipleValues(HashMap hashmap, String s)
    {
        java.lang.Object obj = hashmap.get(s);
        Array array;
        if(obj instanceof Array)
        {
            array = (Array)obj;
        } else
        if(obj instanceof String)
        {
            array = new Array();
            array.add(obj);
        } else
        {
            array = new Array();
        }
        return array;
    }

    public static String getSingleValue(HashMap hashmap, String s)
    {
        Array array = TextUtils.getMultipleValues(hashmap, s);
        if(array == null)
        {
            return "";
        }
        if(array.size() <= 0)
        {
            return "";
        }
        java.lang.Object obj = array.front();
        if(!(obj instanceof String))
        {
            return "";
        } else
        {
            return (String)obj;
        }
    }

    public static void debugPrint(String s)
    {
        java.lang.System.out.println("*** " + s);
    }

    public static void debugPrintStackTrace()
    {
        java.lang.System.out.println("*** DEBUG STACK TRACE");
        (new Exception()).printStackTrace();
    }

    public static void debugPrint(String s, String s1)
    {
        if(s1 != null)
        {
            if(s1.equals("indent"))
            {
                indent = indent + "    ";
                java.lang.System.out.println(indent + "*** " + s);
            } else
            if(s1.equals("unindent") && indent.length() >= 4)
            {
                java.lang.System.out.println(indent + "*** " + s);
                indent = indent.substring(0, indent.length() - 4);
            }
        } else
        {
            java.lang.System.out.println(indent + "*** " + s);
        }
    }

    public static long timeSeconds()
    {
        java.util.Date date = new Date();
        return date.getTime() / 1000L;
    }

    public static String prettyDate(long l)
    {
        return TextUtils.prettyDate(new Date(l));
    }

    public static String prettyDateDate(java.util.Date date)
    {
        return TextUtils.prettyDatePart(date, false);
    }

    public static String prettyDateDate(String s)
    {
        return TextUtils.prettyDatePart(s, false);
    }

    public static String prettyDateTime(java.util.Date date)
    {
        return TextUtils.prettyDatePart(date, true);
    }

    public static String prettyDateTime(String s)
    {
        return TextUtils.prettyDatePart(s, true);
    }

    public static String prettyDatePart(java.util.Date date, boolean flag)
    {
        if(LocaleUtils.getTimeFormatter() != null)
        {
            if(flag)
            {
                return LocaleUtils.getTimeFormatter().format(date);
            } else
            {
                return LocaleUtils.getDateFormatter().format(date);
            }
        } else
        {
            return TextUtils.prettyDatePart(TextUtils.prettyDate(date), flag);
        }
    }

    public static String prettyDatePart(String s, boolean flag)
    {
        int i = s.lastIndexOf(" ");
        if(i >= 0)
        {
            if(flag)
            {
                return s.substring(0, i);
            } else
            {
                return s.substring(i + 1);
            }
        } else
        {
            return s;
        }
    }

    public static String prettyDate(java.util.Date date)
    {
        return TextUtils.prettyDate(date, " ");
    }

    public static String prettyDate(java.util.Date date, long l)
    {
        return TextUtils.prettyDate(date, " ", l);
    }

    public static String prettyDate(java.util.Date date, String s)
    {
        return TextUtils.prettyDate(date, s, 0L);
    }

    private static void initializeCustomFormatter()
    {
        if(!customPrettyDatePatternInitialized)
        {
            synchronized(prettyDateSync)
            {
                if(!customPrettyDatePatternInitialized)
                {
                    customPrettyDatePatternInitialized = true;
                    String s = TextUtils.getValue(COM.dragonflow.SiteView.MasterConfig.getMasterConfig(), "_prettyDateCustomPattern");
                    if(s.length() > 0)
                    {
                        try
                        {
                            prettyDateCustomFormatter = new SimpleDateFormat(s);
                        }
                        catch(java.lang.Exception exception)
                        {
                            COM.dragonflow.Log.LogManager.log("error", "Unable to create a date formatter from pattern " + s);
                            COM.dragonflow.Log.LogManager.logException(exception);
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * 
     * @param date
     * @param s
     * @param l
     * @return
     */
    public static String prettyDate(java.util.Date date, String s, long l)
    {
        java.text.DateFormat dateformat;
        TextUtils.initializeCustomFormatter();
        if(l != 0L)
        {
            date = new Date(date.getTime() + l);
        }
        dateformat = LocaleUtils.getTimeFormatter();
        if(prettyDateCustomFormatter != null)
        {
            synchronized (prettyDateCustomFormatter) {
                return prettyDateCustomFormatter.format(date);
            }
        }

        if(dateformat != null)
        {
            String s1 = dateformat.format(date) + " " + LocaleUtils.getDateFormatter().format(date);
            return s1;
        } else
        {
            java.text.SimpleDateFormat simpledateformat1 = new SimpleDateFormat("h:mm a" + s + "MM/dd/yy");
            java.text.DateFormatSymbols dateformatsymbols = simpledateformat1.getDateFormatSymbols();
            String as[] = {
                "am", "pm"
            };
            dateformatsymbols.setAmPmStrings(as);
            simpledateformat1.setDateFormatSymbols(dateformatsymbols);
            String s2 = simpledateformat1.format(date);
            return s2;
        }
    }

    public static String prettyHour(int i)
    {
        String s = "am";
        if(i == 0)
        {
            i = 12;
        } else
        if(i == 12)
        {
            s = "pm";
        } else
        if(i > 12)
        {
            s = "pm";
            i -= 12;
        }
        return "" + i + " " + s;
    }

    public static String dateToMilitaryTime(java.util.Date date)
    {
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm");
        return simpledateformat.format(date);
    }

    public static String dateToString(long l)
    {
        return TextUtils.dateToString(new Date(l));
    }

    public static String dateToString()
    {
        return TextUtils.dateToString(COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static String dateToString(java.util.Date date)
    {
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat(dateToStringFormat);
        return simpledateformat.format(date);
    }

    public static String numberToString(int i)
    {
        if(i > 9)
        {
            return String.valueOf(i);
        } else
        {
            return "0" + String.valueOf(i);
        }
    }

    public static String padWithZeros(String s, int i)
    {
        String s1 = s;
        for(int j = 0; j < i - s.length(); j++)
        {
            s1 = "0" + s1;
        }

        return s1;
    }

    public static java.util.Date stringToDate(String s)
    {
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat(dateToStringFormat);
        java.util.Date date = null;
        try
        {
            date = simpledateformat.parse(s);
        }
        catch(java.text.ParseException parseexception)
        {
            try
            {
                int i = TextUtils.readIntegerSafe(s, 0);
                int j = TextUtils.readIntegerSafe(s, 3);
                int k = TextUtils.readIntegerSafe(s, 6);
                int l = TextUtils.readIntegerSafe(s, 9) - 1;
                int i1 = TextUtils.readIntegerSafe(s, 12);
                int j1 = TextUtils.readIntegerSafe(s, 15);
                date = (new GregorianCalendar(j1, l, i1, i, j, k)).getTime();
                COM.dragonflow.Log.LogManager.log("RunMonitor", "Could not parse " + s + " into a date using default parsing");
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "TextUtils: stringToDate failed to format a date from: " + s + ", exception: " + exception.getMessage());
            }
        }
        return date;
    }

    public static String filledString(char c, int i)
    {
        StringBuffer stringbuffer = new StringBuffer(i);
        for(; i > 0; i--)
        {
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    public static String floatToString(float f, int i)
    {
        if(java.lang.Float.isNaN(f))
        {
            return "n/a";
        }
        if(TextUtils.isUseDecFormat() && TextUtils.getDecFormatFixed() != null)
        {
            return TextUtils.getDecFormatFixed().format(f);
        }
        String s = String.valueOf(f);
        int j = s.indexOf("E");
        if(j != -1)
        {
            int k = j;
            if(f < 0.0F)
            {
                if(k > 6)
                {
                    k = 6;
                }
            } else
            if(k > 5)
            {
                k = 5;
            }
            return s.substring(0, k) + "E" + s.substring(j + 1, s.length());
        }
        if(i <= 0)
        {
            return "" + java.lang.Math.round(f);
        }
        int l = s.indexOf(".");
        if(l < 0)
        {
            return s + "." + TextUtils.filledString('0', i);
        }
        int i1 = s.length() - l - 1;
        if(i1 > i)
        {
            s = s.substring(0, l + i + 1);
        } else
        if(i1 < i)
        {
            s = s + TextUtils.filledString('0', i - i1);
        }
        return s;
    }

    public static String floatToFormattedString(float f, int i)
    {
        if(java.lang.Float.isNaN(f))
        {
            return "n/a";
        }
        if(TextUtils.isUseDecFormat() && TextUtils.getDecFormat() != null)
        {
            return TextUtils.getDecFormat().format(f);
        } else
        {
            return TextUtils.floatToString(f, i);
        }
    }

    public static String dateToFileName()
    {
        return TextUtils.dateToFileName(COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static String dateToFileName(java.util.Date date)
    {
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat(fileDateFormatPattern);
        String s = simpledateformat.format(date);
        return s;
    }

    public static java.util.Date fileNameToDate(String s)
    {
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat(fileDateFormatPattern);
        java.util.Date date = null;
        try
        {
            date = simpledateformat.parse(s);
        }
        catch(java.text.ParseException parseexception)
        {
            int i = TextUtils.readIntegerSafe(s, 0);
            int j = TextUtils.readIntegerSafe(s, 3);
            int k = TextUtils.readIntegerSafe(s, 6) - 1;
            int l = TextUtils.readIntegerSafe(s, 9);
            int i1 = TextUtils.readIntegerSafe(s, 12);
            date = (new GregorianCalendar(i1, k, l, i, j)).getTime();
            COM.dragonflow.Log.LogManager.log("RunMonitor", "Could not parse " + s + " into a date using default parsing");
        }
        return date;
    }

    public static String dayToFileName()
    {
        return TextUtils.dayToFileName(COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static String dayToFileName(java.util.Date date)
    {
        return TextUtils.dayToFileName(date, false);
    }

    public static String timeToFileName(java.util.Date date)
    {
        return TextUtils.dayToFileName(date, true);
    }

    public static String dayToFileName(java.util.Date date, boolean flag)
    {
        java.text.SimpleDateFormat simpledateformat = null;
        if(flag)
        {
            simpledateformat = new SimpleDateFormat(dayFileFormatWithTimePattern);
        } else
        {
            simpledateformat = new SimpleDateFormat(dayFileFormatPattern);
        }
        return simpledateformat.format(date);
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static java.util.Date fileNameToDay(String s)
    {
        boolean flag;
        java.text.SimpleDateFormat simpledateformat;
        flag = false;
        if(s.length() >= 16 && s.charAt(10) == '-')
        {
            flag = true;
        }
        simpledateformat = null;
        try {
        if(flag)
        {
            simpledateformat = new SimpleDateFormat(dayFileFormatWithTimePattern);
        } else
        {
            simpledateformat = new SimpleDateFormat(dayFileFormatPattern);
        }
        return simpledateformat.parse(s);
        }
        catch (java.text.ParseException parseexception) {
        COM.dragonflow.Log.LogManager.log("RunMonitor", "Could not parse " + s + " into a date using default parsing");
        int i = TextUtils.readIntegerSafe(s, 0);
        int j = TextUtils.readIntegerSafe(s, 5) - 1;
        int k = TextUtils.readIntegerSafe(s, 8);
        int l = 0;
        int i1 = 0;
        if(flag)
        {
            l = TextUtils.readIntegerSafe(s, 11);
            i1 = TextUtils.readIntegerSafe(s, 14);
        }
        return (new GregorianCalendar(i, j, k, l, i1)).getTime();
        }
    }

    public static String bytesToString(long l)
    {
        if(l < 1024L)
        {
            return "" + l + " bytes";
        }
        if(l < 0x100000L)
        {
            return "" + l / 1024L + "K";
        } else
        {
            float f = (float)l / 1048576F;
            return TextUtils.floatToString(f, 2) + " MB";
        }
    }

    public static String replaceChar(String s, char c, String s1)
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

    public static String replaceString(String s, String as[])
    {
        int i = as.length / 2;
        for(int j = 0; j < i; j++)
        {
            s = TextUtils.replaceString(s, as[j], as[j + i]);
        }

        return s;
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param s2
     * @return
     */
    public static String replaceString(String s, String s1, String s2)
    {
        if(!s1.equals(s2))
        {
            int i = 0;
            while ((i = s.indexOf(s1, i)) != -1)
                {
                s = s.substring(0, i) + s2 + s.substring(i + s1.length());
                if(s2.length() > 1)
                {
                    i += s2.length();
                }
            } 
        }
        return s;
    }

    public static String toInitialUpper(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(i == 0 || java.lang.Character.isWhitespace(s.charAt(i - 1)))
            {
                stringbuffer.append(java.lang.Character.toUpperCase(c));
            } else
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    public static void appendStringRightJustify(StringBuffer stringbuffer, String s, int i)
    {
        TextUtils.appendString(stringbuffer, s, i, false, true);
    }

    public static void appendStringLeftJustify(StringBuffer stringbuffer, String s, int i)
    {
        TextUtils.appendString(stringbuffer, s, i, true, false);
    }

    public static void appendString(StringBuffer stringbuffer, String s, int i, boolean flag, boolean flag1)
    {
        if(i < 0)
        {
            stringbuffer.append(s);
        } else
        if(i < s.length())
        {
            stringbuffer.append(s.substring(0, i));
        } else
        {
            int j = i - s.length();
            if(flag1)
            {
                while(j-- > 0) 
                {
                    stringbuffer.append(' ');
                }
            }
            stringbuffer.append(s);
            if(flag)
            {
                while(j-- > 0) 
                {
                    stringbuffer.append(' ');
                }
            }
        }
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static int stringToDaySeconds(String s)
    {
        int i;
        i = s.indexOf(":");
        try {
        if(i < 0)
        {
        return java.lang.Integer.parseInt(s);
        }
        }
        catch (java.lang.NumberFormatException numberformatexception) {
        return 0;
        }
        
        int j = TextUtils.readInteger(s, 0);
        int k = 0;
        if(i + 1 < s.length())
        {
            k = TextUtils.readInteger(s, i + 1);
        }
        return j * HOUR_SECONDS + k * MINUTE_SECONDS;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static boolean isDateStringValid(String s)
    {
        try {
        if(LocaleUtils.getDateFormatter() != null)
        {
        return LocaleUtils.getDateFormatter().parse(s) != null;
        }
        }
        catch (java.lang.Exception exception) {
        return false;
        }
        
        String as[];
        as = TextUtils.split(s, "/");
        try {
        if(as.length != 3)
        {
        java.lang.Integer.parseInt(as[0]);
        java.lang.Integer.parseInt(as[1]);
        java.lang.Integer.parseInt(as[2]);
        return true;
        }
        }
        catch (java.lang.NumberFormatException numberformatexception) {
        return false;
        }
        return false;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static boolean isTimeStringValid(String s)
    {
        if(LocaleUtils.getDateFormatter() != null)
        {
            try {
        java.text.SimpleDateFormat simpledateformat;
        java.text.ParsePosition parseposition;
        if(LocaleUtils.getLocale().equals(new Locale("en", "US")) && !LocaleUtils.getLocale().equals(new Locale("en", "CA")) && !LocaleUtils.getLocale().equals(new Locale("en", "")))
        {
        simpledateformat = new SimpleDateFormat("HH:mm");
        parseposition = new ParsePosition(0);
        return simpledateformat.parse(s, parseposition) != null;
        }
        return LocaleUtils.getTimeFormatter().parse(s) != null;
            }
        catch (java.lang.Exception exception) {
        return false;
        }
        }
        
        
        String as[];
        as = TextUtils.split(s, ":");
        if(as.length == 2)
        {
            try {
        java.lang.Integer.parseInt(as[0]);
        java.lang.Integer.parseInt(as[1]);
        return true;
            }
        catch (java.lang.NumberFormatException numberformatexception) {
        return false;
        }
        }
        return false;
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @return
     */
    public static long dateStringToSeconds(String s, String s1)
    {
        String s2;
        if(LocaleUtils.getDateTimeFormatter() != null)
        {
         s2 = s + " " + s1;
        java.util.Date date;
        try {
        LocaleUtils.getDateTimeFormatter().setLenient(true);
        if(LocaleUtils.getLocale().equals(new Locale("en", "US")) || LocaleUtils.getLocale().equals(new Locale("en", "")))
        {
            java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat("MM/dd/yy HH:mm");
            java.text.ParsePosition parseposition = new ParsePosition(0);
            date = simpledateformat.parse(s2, parseposition);
        } else
        if(LocaleUtils.getLocale().equals(new Locale("en", "CA")))
        {
            java.text.SimpleDateFormat simpledateformat1 = new SimpleDateFormat("dd/MM/yy HH:mm");
            java.text.ParsePosition parseposition1 = new ParsePosition(0);
            date = simpledateformat1.parse(s2, parseposition1);
        } else
        {
            date = LocaleUtils.getDateTimeFormatter().parse(s2);
        }
        if(date == null)
        {
        COM.dragonflow.Log.LogManager.log("RunMonitor", "dateStringToSeconds invalid input of (" + s2 + ")");
        return 0L;
        }
        
        long l = date.getTime() / 1000L;
        return l;
    }
        catch (java.lang.Exception exception) {
        COM.dragonflow.Log.LogManager.log("RunMonitor", "dateStringToSeconds exception caught - input of (" + s2 + ")");
        exception.printStackTrace();
        return 0L;
        }
        }
        long l1 = TextUtils.stringToDateSeconds(s) + TextUtils.stringToDaySeconds(s1);
        return l1;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static int stringToDateSeconds(String s)
    {
        String as[];
        int i;
        as = TextUtils.split(s, "/");
        i = 0;
        if(as.length == 1)
        {
            try {
        return java.lang.Integer.parseInt(s);
            }
        catch (java.lang.NumberFormatException numberformatexception) {
            /* empty */
        }
    }
        else if(as.length == 3)
        {
            try
            {
                int j = java.lang.Integer.parseInt(as[0]) - 1;
                int k = java.lang.Integer.parseInt(as[1]);
                int l = java.lang.Integer.parseInt(as[2]);
                if(l < 96)
                {
                    l += 2000;
                } else
                if(l >= 96 && l < 100)
                {
                    l += 1900;
                }
                i = (int)((new GregorianCalendar(l, j, k)).getTimeInMillis() / 1000L);
            }
            catch(java.lang.NumberFormatException numberformatexception1) {
                /* empty */
            }
        }
        return i;
    }

    public static String secondsToString(int i)
    {
        String s = "second";
        int j = 0;
        if(i % DAY_SECONDS == 0)
        {
            s = "day";
            j = i / DAY_SECONDS;
        } else
        if(i % HOUR_SECONDS == 0)
        {
            s = "hour";
            j = i / HOUR_SECONDS;
        } else
        if(i % MINUTE_SECONDS == 0)
        {
            j = i / MINUTE_SECONDS;
            s = "minute";
        }
        return j + " " + s + (j != 1 ? "s" : "");
    }

    public static String secondsToPrettyString(int i)
    {
        int j = 0;
        int k = 0;
        int l = 0;
        if(i >= DAY_SECONDS)
        {
            j = i / DAY_SECONDS;
            i -= j * DAY_SECONDS;
        }
        if(i >= HOUR_SECONDS)
        {
            k = i / HOUR_SECONDS;
            i -= k * HOUR_SECONDS;
        }
        if(i >= MINUTE_SECONDS)
        {
            l = i / MINUTE_SECONDS;
            i -= l * MINUTE_SECONDS;
        }
        String s = "";
        if(j > 0)
        {
            if(s.length() > 0)
            {
                s = s + ", ";
            }
            s = s + "" + j + " day";
            if(j > 1)
            {
                s = s + "s";
            }
        }
        if(k > 0)
        {
            if(s.length() > 0)
            {
                s = s + ", ";
            }
            s = s + "" + k + " hour";
            if(k > 1)
            {
                s = s + "s";
            }
        }
        if(l > 0)
        {
            if(s.length() > 0)
            {
                s = s + ", ";
            }
            s = s + "" + l + " minute";
            if(l > 1)
            {
                s = s + "s";
            }
        }
        if(s.length() == 0)
        {
            s = s + "" + i + " second";
            if(i > 1)
            {
                s = s + "s";
            }
        }
        return s;
    }

    public static Array splitArray(String s, String s1)
    {
        return TextUtils.splitArrayImplementation(new StringTokenizer(s, s1));
    }

    public static Array splitArray(String s)
    {
        return TextUtils.splitArrayImplementation(new StringTokenizer(s));
    }

    private static Array splitArrayImplementation(java.util.StringTokenizer stringtokenizer)
    {
        Array array = new Array();
        for(; stringtokenizer.hasMoreTokens(); array.add(stringtokenizer.nextToken())) { }
        return array;
    }

    public static String[] split(String s, String s1)
    {
        return TextUtils.splitImplementation(new StringTokenizer(s, s1));
    }

    public static String[] split(String s)
    {
        return TextUtils.splitImplementation(new StringTokenizer(s));
    }

    private static String[] splitImplementation(java.util.StringTokenizer stringtokenizer)
    {
        int i = stringtokenizer.countTokens();
        if(i == 0)
        {
            return new String[0];
        }
        String as[] = new String[i];
        int j = 0;
        while(stringtokenizer.hasMoreTokens()) 
        {
            as[j++] = stringtokenizer.nextToken();
        }
        return as;
    }

    public static int splitChar(String s, char c, String as[])
    {
        int i = 0;
        int j = 0;
        for(int k = 0; k < s.length(); k++)
        {
            if(s.charAt(k) != c)
            {
                continue;
            }
            if(i >= as.length)
            {
                return i;
            }
            String s2 = s.substring(j, k);
            as[i++] = s2;
            j = k + 1;
        }

        if(j != s.length())
        {
            if(i >= as.length)
            {
                return i;
            }
            String s1 = s.substring(j, s.length());
            as[i++] = s1;
        }
        return i;
    }

    public static String[] tokenize(String s)
    {
        Array array = new Array();
        StringBuffer stringbuffer = new StringBuffer();
        char c = ' ';
        char c1 = ' ';
        for(int i = 0; i < s.length(); i++)
        {
            char c2 = s.charAt(i);
            if(c != ' ')
            {
                if(c2 == c && c1 != 92)
                {
                    array.add(c + stringbuffer.toString() + c);
                    stringbuffer.setLength(0);
                    c = ' ';
                } else
                {
                    stringbuffer.append(c2);
                }
            } else
            if(!java.lang.Character.isWhitespace(c2))
            {
                if(c2 == '\'' || c2 == '"')
                {
                    c = c2;
                } else
                {
                    stringbuffer.append(c2);
                }
            } else
            if(stringbuffer.length() > 0)
            {
                array.add(stringbuffer.toString());
                stringbuffer.setLength(0);
            }
            c1 = c2;
        }

        if(stringbuffer.length() > 0)
        {
            array.add(stringbuffer.toString());
        }
        String as[] = new String[array.size()];
        for(int j = 0; j < array.size(); j++)
        {
            as[j] = (String)array.get(j);
        }

        return as;
    }

    public static String removeChars(String s, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(s1.indexOf(c) == -1)
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    public static String readStringFromStart(String s, String s1)
    {
        int i = s.indexOf(s1);
        String s2 = s;
        if(i >= 0)
        {
            s2 = s.substring(0, i);
        }
        return s2;
    }

    public static String readStringFromEnd(String s, String s1)
    {
        int i = s.lastIndexOf(s1);
        String s2 = s;
        if(i >= 0)
        {
            s2 = s.substring(i + s1.length());
        }
        return s2;
    }

    public static String keepChars(String s, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(s1.indexOf(c) != -1)
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    public static String toEmailList(String s)
    {
        String as[] = TextUtils.split(s, " ,");
        StringBuffer stringbuffer = new StringBuffer(s.length());
        String s1 = "";
        for(int i = 0; i < as.length; i++)
        {
            String s2 = TextUtils.removeChars(as[i], ",");
            stringbuffer.append(s1);
            stringbuffer.append(s2);
            s1 = ",";
        }

        return stringbuffer.toString();
    }

    public static String checkPhone(String s)
    {
        if(TextUtils.digits(s) == 10)
        {
            s = "1" + s;
        }
        return s;
    }

    public static String chomp(String s)
    {
        if(s.endsWith("\n"))
        {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static int findInteger(String s, String s1, String s2)
    {
        long l = TextUtils.findLong(s, s1, s2);
        if(l <= 0x7fffffffL)
        {
            return (int)l;
        } else
        {
            return -1;
        }
    }

    public static long findLong(String s, String s1, String s2)
    {
        int i = s.indexOf(s1);
        if(i < 0)
        {
            return -1L;
        }
        for(i += s1.length(); i < s.length() && !java.lang.Character.isDigit(s.charAt(i)); i++) { }
        if(i == s.length())
        {
            return -1L;
        }
        long l = -1L;
        do
        {
            if(i >= s.length())
            {
                break;
            }
            char c = s.charAt(i);
            if(!java.lang.Character.isDigit(c))
            {
                break;
            }
            if(l > 0L)
            {
                l *= 10L;
            } else
            {
                l = 0L;
            }
            l += java.lang.Character.digit(c, 10);
            i++;
        } while(true);
        if(s2 != null && i < s.length())
        {
            int j = s.indexOf(s2, i);
            if(j < 0)
            {
                l = -1L;
            }
        }
        return l;
    }

    public static String readColumn(String s, int i)
    {
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(s);
        if(stringtokenizer.countTokens() >= i)
        {
            for(int j = 1; j <= i; j++)
            {
                String s1 = stringtokenizer.nextToken();
                if(j == i)
                {
                    return s1;
                }
            }

        }
        return "";
    }

    public static long readHex(String s)
    {
        long l = 0L;
        String s1 = "0123456789abcdef";
        s = s.toLowerCase();
        int i = 0;
        do
        {
            if(i >= s.length())
            {
                break;
            }
            char c = s.charAt(i);
            int j = s1.indexOf(c);
            if(j == -1)
            {
                break;
            }
            l = l * 16L + (long)j;
            i++;
        } while(true);
        return l;
    }

    public static int readInteger(String s, int i)
    {
        long l = TextUtils.readLong(s, i);
        if(l <= 0x7fffffffL)
        {
            return (int)l;
        } else
        {
            return -1;
        }
    }

    public static long readLong(String s, int i)
    {
        long l = -1L;
        StringBuffer stringbuffer = new StringBuffer();
        int j = s.length();
        do
        {
            if(i >= j)
            {
                break;
            }
            char c = s.charAt(i++);
            if(!java.lang.Character.isDigit(c))
            {
                break;
            }
            stringbuffer.append(c);
        } while(true);
        if(stringbuffer.length() > 0)
        {
            l = java.lang.Long.valueOf(stringbuffer.toString()).longValue();
        }
        return l;
    }

    public static int readIntegerFromEnd(String s)
    {
        int i = -1;
        int j;
        for(j = s.length() - 1; j >= 0 && java.lang.Character.isDigit(s.charAt(j)); j--) { }
        if(j >= 0 && j != s.length() - 1)
        {
            i = TextUtils.toInt(s.substring(j + 1));
        }
        return i;
    }

    public static float readFloat(String s, int i)
    {
        float f = -1F;
        StringBuffer stringbuffer = new StringBuffer();
        int j = s.length();
        do
        {
            if(i >= j)
            {
                break;
            }
            char c = s.charAt(i++);
            if(!java.lang.Character.isDigit(c) && c != '.')
            {
                break;
            }
            stringbuffer.append(c);
        } while(true);
        if(stringbuffer.length() > 0)
        {
            f = java.lang.Float.valueOf(stringbuffer.toString()).floatValue();
        }
        return f;
    }

    public static int toInt(String s)
    {
        int i = 0;
        try
        {
            i = java.lang.Integer.parseInt(s);
        }
        catch(java.lang.NumberFormatException numberformatexception) { }
        return i;
    }

    public static long toLong(String s)
    {
        long l = 0L;
        try
        {
            l = java.lang.Long.parseLong(s);
        }
        catch(java.lang.NumberFormatException numberformatexception) { }
        return l;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static double toDouble(String s)
    {
        if(s.equals("n/a"))
        {
            return (0.0D / 0.0D);
        }
        try {
        return java.lang.Double.valueOf(s).doubleValue();
        }
        catch (java.lang.NumberFormatException numberformatexception) {
        return 0.0D;
        }
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static float toFloat(String s)
    {
        if(s.equals("n/a"))
        {
            return (0.0F / 0.0F);
        }
        try {
        if(TextUtils.isUseDecFormat())
        {
            return java.lang.Float.valueOf(TextUtils.replaceString(s, ",", "")).floatValue();
        }
        return java.lang.Float.valueOf(s).floatValue();
        }
        catch (java.lang.NumberFormatException numberformatexception) {
        return 0.0F;
        }
    }

    public static String increment(String s)
    {
        long l = 0L;
        try
        {
            l = java.lang.Long.parseLong(s);
        }
        catch(java.lang.NumberFormatException numberformatexception) { }
        return String.valueOf(l + 1L);
    }

    public static void incrementEntry(HashMap hashmap, String s)
    {
        String s1 = (String)hashmap.get(s);
        if(s1 == null)
        {
            s1 = "0";
        }
        hashmap.put(s, TextUtils.increment(s1));
    }

    public static int compare(String s, String s1)
    {
        int i = s != null ? TextUtils.toInt(s) : 0;
        int j = s1 != null ? TextUtils.toInt(s1) : 0;
        return i - j;
    }

    public static int readIntegerSafe(String s, int i)
        throws java.lang.NumberFormatException
    {
        int j = TextUtils.readInteger(s, i);
        if(j == -1)
        {
            throw new NumberFormatException("Integer not found at offset " + i + " in: " + s);
        } else
        {
            return j;
        }
    }

    public static int digits(String s)
    {
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if(java.lang.Character.isDigit(c))
            {
                i++;
            }
        }

        return i;
    }

    public static int ldigits(String s)
    {
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if(java.lang.Character.isDigit(c) || c == '-')
            {
                i++;
            }
        }

        return i;
    }

    public static boolean hasLetters(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(java.lang.Character.isLetter(c))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean hasDigits(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(java.lang.Character.isDigit(c))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean hasSpaces(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == ' ')
            {
                return true;
            }
        }

        return false;
    }

    public static String stripChars(String s, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(s1.indexOf(c) >= 0)
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    public static boolean onlyChars(String s, String s1)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(s1.indexOf(c) < 0)
            {
                return false;
            }
        }

        return true;
    }

    public static boolean hasChars(String s, String s1)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(s1.indexOf(c) >= 0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isFloat(String s)
    {
        return TextUtils.onlyChars(s, "0123456789.-Ee+") && s.indexOf(".") != -1;
    }

    public static boolean isInteger(String s)
    {
        return TextUtils.onlyChars(s, "0123456789-");
    }

    public static boolean isNumber(String s)
    {
        return TextUtils.isFloat(s) || TextUtils.isInteger(s);
    }

    private static int isEscaped(StringBuffer stringbuffer, String s, int i)
    {
        if(s.substring(i).startsWith("&gt;"))
        {
            stringbuffer.append("&gt;");
            return 4;
        }
        if(s.substring(i).startsWith("&lt;"))
        {
            stringbuffer.append("&lt;");
            return 4;
        }
        if(s.substring(i).startsWith("&#") || s.substring(i).startsWith("&amp;#"))
        {
            int j = 0;
            byte byte0 = ((byte)(s.substring(i).startsWith("&#") ? 2 : 6));
            for(j = i + byte0; j < s.length() && java.lang.Character.isDigit(s.charAt(j)); j++) { }
            if(j < s.length() && s.charAt(j) == ';')
            {
                stringbuffer.append("&#");
                stringbuffer.append(s.substring(i + byte0, j + 1));
                return (j - i) + 1;
            }
        } else
        if(s.substring(i).startsWith("&amp;"))
        {
            stringbuffer.append("&amp;");
            return 5;
        }
        return 0;
    }

    private static boolean escapeSpecial(char c, StringBuffer stringbuffer)
    {
        boolean flag = true;
        if(c == '>')
        {
            stringbuffer.append("&gt;");
        } else
        if(c == '<')
        {
            stringbuffer.append("&lt;");
        } else
        if(c == '&')
        {
            stringbuffer.append("&amp;");
        } else
        {
            flag = false;
        }
        return flag;
    }

    public static void escapeChar(char c, StringBuffer stringbuffer)
    {
        if(!TextUtils.escapeSpecial(c, stringbuffer))
        {
            String s = java.lang.Integer.toString(c);
            if(s.length() == 1)
            {
                s = "00" + s;
            } else
            if(s.length() == 2)
            {
                s = "0" + s;
            }
            stringbuffer.append("&#" + s + ";");
        }
    }

    public static String escapeXML(String s, String s1)
    {
        s = s.replace('/', '-');
        StringBuffer stringbuffer = new StringBuffer(s1.length());
        stringbuffer.append("<" + s + ">");
        int i = 0;
        do
        {
            if(i >= s1.length())
            {
                break;
            }
            i += TextUtils.isEscaped(stringbuffer, s1, i);
            if(i >= s1.length())
            {
                break;
            }
            char c = s1.charAt(i++);
            if(!TextUtils.escapeSpecial(c, stringbuffer))
            {
                stringbuffer.append(c);
            }
        } while(true);
        stringbuffer.append("</" + s + ">\n");
        return stringbuffer.toString();
    }

    public static String escapeXML(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        int i = 0;
        do
        {
            if(i >= s.length())
            {
                break;
            }
            i += TextUtils.isEscaped(stringbuffer, s, i);
            if(i >= s.length())
            {
                break;
            }
            char c = s.charAt(i++);
            if(!TextUtils.escapeSpecial(c, stringbuffer))
            {
                stringbuffer.append(c);
            }
        } while(true);
        return stringbuffer.toString();
    }

    public static String escapeHTML(String s)
    {
        String s1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.:/ ";
        StringBuffer stringbuffer = new StringBuffer(s.length());
        int i = 0;
        do
        {
            if(i >= s.length())
            {
                break;
            }
            for(int j = 0; (j = TextUtils.isEscaped(stringbuffer, s, i)) > 0 && i < s.length(); i += j) { }
            if(i >= s.length())
            {
                break;
            }
            char c = s.charAt(i++);
            if(s1.indexOf(c) >= 0)
            {
                stringbuffer.append(c);
            } else
            {
                TextUtils.escapeChar(c, stringbuffer);
            }
        } while(true);
        return stringbuffer.toString();
    }

    public static boolean isDoubleByte(char c)
    {
        return c >= '\200';
    }

    public static String unescapeHTML(String s)
    {
        s = TextUtils.replaceString(s, "&amp;", "&");
        s = TextUtils.replaceString(s, "&AMP;", "&");
        return s;
    }

    public static boolean matchBytes(byte abyte0[], int i, byte abyte1[])
    {
        if(i < abyte1.length)
        {
            return false;
        }
        for(int j = 0; j < abyte1.length; j++)
        {
            if(abyte0[j] != abyte1[j])
            {
                return false;
            }
        }

        return true;
    }

    public static String toHex(int i)
    {
        String s = java.lang.Integer.toHexString(i);
        if(s.length() < 2)
        {
            s = "0" + s;
        }
        return s;
    }

    public static String bytesToString(byte abyte0[], int i)
    {
        String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+{}|:\"<>?,./;'[]\\-= `~";
        StringBuffer stringbuffer = new StringBuffer();
        for(int j = 0; j < i; j++)
        {
            int l = abyte0[j];
            if(l < 0)
            {
                l += 128;
            }
            char c = (char)l;
            if(s.indexOf(c) != -1)
            {
                stringbuffer.append(c);
            } else
            {
                stringbuffer.append('.');
            }
        }

        stringbuffer.append(" (");
        for(int k = 0; k < i; k++)
        {
            if(k != 0)
            {
                stringbuffer.append(' ');
            }
            int i1 = abyte0[k];
            if(i1 < 0)
            {
                i1 += 128;
            }
            stringbuffer.append(TextUtils.toHex(i1));
        }

        stringbuffer.append(')');
        return stringbuffer.toString();
    }

    public static byte[] stringToBytes(String s)
        throws java.lang.Exception
    {
        byte abyte0[] = new byte[s.length()];
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            if(i >= abyte0.length)
            {
                throw new Exception("buffer too small, " + i + " > " + abyte0.length);
            }
            char c = s.charAt(j);
            if(c == '\\')
            {
                if(++j >= s.length())
                {
                    throw new Exception("missing character after \\");
                }
                c = s.charAt(j);
                switch(c)
                {
                case 110: // 'n'
                    abyte0[i++] = 10;
                    break;

                case 116: // 't'
                    abyte0[i++] = 9;
                    break;

                case 114: // 'r'
                    abyte0[i++] = 13;
                    break;

                case 102: // 'f'
                    abyte0[i++] = 12;
                    break;

                case 98: // 'b'
                    abyte0[i++] = 8;
                    break;

                case 97: // 'a'
                    abyte0[i++] = 7;
                    break;

                case 101: // 'e'
                    abyte0[i++] = 27;
                    break;

                case 92: // '\\'
                    abyte0[i++] = 92;
                    break;

                case 120: // 'x'
                    abyte0[i++] = TextUtils.digitsToByte(s, j + 1, 16);
                    j += 2;
                    break;

                case 48: // '0'
                    abyte0[i++] = TextUtils.digitsToByte(s, j + 1, 8);
                    j += 2;
                    break;

                default:
                    abyte0[i++] = (byte)c;
                    break;
                }
            } else
            {
                abyte0[i++] = (byte)c;
            }
        }

        byte abyte1[] = new byte[i];
        java.lang.System.arraycopy(abyte0, 0, abyte1, 0, i);
        return abyte1;
    }

    public static byte digitsToByte(String s, int i, int j)
        throws java.lang.NumberFormatException
    {
        int k = -1;
        int l = -1;
        if(i < s.length())
        {
            k = java.lang.Character.digit(s.charAt(i), j);
        }
        if(i + 1 < s.length())
        {
            l = java.lang.Character.digit(s.charAt(i + 1), j);
        }
        if(k == -1 && l == -1)
        {
            throw new NumberFormatException();
        }
        if(l == -1)
        {
            return (byte)k;
        } else
        {
            return (byte)(k * j + l);
        }
    }

    public static String valueToStoredValue(String s)
    {
        if(s.indexOf(' ') >= 0 || s.indexOf('_') >= 0)
        {
            StringBuffer stringbuffer = new StringBuffer(s.length());
            for(int i = 0; i < s.length(); i++)
            {
                char c = s.charAt(i);
                if(c == ' ')
                {
                    stringbuffer.append('_');
                    continue;
                }
                if(c == '_')
                {
                    stringbuffer.append("\\_");
                } else
                {
                    stringbuffer.append(c);
                }
            }

            s = stringbuffer.toString();
        }
        return s;
    }

    public static String storedValueToValue(String s)
    {
        if(s.indexOf('_') >= 0)
        {
            StringBuffer stringbuffer = new StringBuffer(s.length());
            char c = ' ';
            for(int i = 0; i < s.length(); i++)
            {
                char c1 = s.charAt(i);
                if(c1 == '_')
                {
                    if(c == 92)
                    {
                        int j = stringbuffer.length() <= 0 ? 1 : stringbuffer.length();
                        stringbuffer.setLength(j - 1);
                        stringbuffer.append('_');
                    } else
                    {
                        stringbuffer.append(' ');
                    }
                } else
                {
                    stringbuffer.append(c1);
                }
                c = c1;
            }

            s = stringbuffer.toString();
        }
        return s;
    }

    public static HashMap stringToHashMap(String s)
    {
        String as[] = TextUtils.split(s);
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        for(int i = 0; i < as.length; i++)
        {
            int j = as[i].indexOf('=');
            if(j <= 0)
            {
                continue;
            }
            String s1 = as[i].substring(0, j);
            String s2 = TextUtils.storedValueToValue(as[i].substring(j + 1));
            if(s1.indexOf("assword") != -1 || s1.startsWith("_private"))
            {
                s2 = TextUtils.enlighten(s2);
            }
            hashmapordered.add(s1, s2);
        }

        return hashmapordered;
    }

    public static String hashMapToString(HashMap hashmap)
    {
        Enumeration enumeration = (Enumeration) hashmap.keys();
        StringBuffer stringbuffer = new StringBuffer();
        String s;
        for(; enumeration.hasMoreElements(); stringbuffer.append(s))
        {
            java.lang.Object obj = enumeration.nextElement();
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(" ");
            }
            stringbuffer.append(obj);
            stringbuffer.append("=");
            s = (String)hashmap.get(obj);
            String s1 = obj.toString();
            if(s1.indexOf("assword") != -1 && s1.indexOf("rompt") == -1 || s1.startsWith("_private"))
            {
                s = TextUtils.obscure(s);
            }
            s = TextUtils.valueToStoredValue(s);
        }

        return stringbuffer.toString();
    }

    public static String hashMapToString(java.util.HashMap hashmap)
    {
        java.util.Set set = hashmap.keySet();
        java.util.Iterator iterator = set.iterator();
        StringBuffer stringbuffer = new StringBuffer();
        String s;
        for(; iterator.hasNext(); stringbuffer.append(s))
        {
            java.lang.Object obj = iterator.next();
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(" ");
            }
            stringbuffer.append(obj);
            stringbuffer.append("=");
            s = (String)hashmap.get(obj);
            String s1 = obj.toString();
            if(s1.indexOf("assword") != -1 && s1.indexOf("rompt") == -1 || s1.startsWith("_private"))
            {
                s = TextUtils.obscure(s);
            }
            s = TextUtils.valueToStoredValue(s);
        }

        return stringbuffer.toString();
    }

    public static String hashMapToOrderedString(HashMap hashmap)
    {
        Enumeration enumeration = (Enumeration) hashmap.keys();
        StringBuffer stringbuffer = new StringBuffer();
        Array array = new Array();
        StringBuffer stringbuffer1;
        for(; enumeration.hasMoreElements(); array.add(stringbuffer1.toString()))
        {
            java.lang.Object obj = enumeration.nextElement();
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(" ");
            }
            stringbuffer1 = new StringBuffer();
            stringbuffer1.append(obj);
            stringbuffer1.append("=");
            String s1 = (String)hashmap.get(obj);
            s1 = TextUtils.valueToStoredValue(s1);
            String s2 = obj.toString();
            if(s2.indexOf("assword") != -1)
            {
                s1 = TextUtils.obscure(s1);
            }
            stringbuffer1.append(s1);
        }

        Sorting.sort(array, new LessString());
        enumeration = (Enumeration) array.iterator();
        while (enumeration.hasMoreElements()) {
            String s = (String)enumeration.nextElement();
            stringbuffer.append(s);
            if(enumeration.hasMoreElements())
            {
                stringbuffer.append(" ");
            }
        } 
        return stringbuffer.toString();
    }

    public static boolean isStatusLine(String s)
    {
        return s.length() >= 3 && (s.length() == 3 || s.charAt(3) == ' ') && java.lang.Character.isDigit(s.charAt(0)) && java.lang.Character.isDigit(s.charAt(1)) && java.lang.Character.isDigit(s.charAt(2));
    }

    public static boolean foundIPAddress(String s, String s1)
    {
        int i = s.indexOf(s1);
        boolean flag = false;
        if(i >= 0)
        {
            flag = true;
            if(i > 0 && java.lang.Character.isDigit(s.charAt(i - 1)))
            {
                flag = false;
            }
            int j = i + s1.length();
            if(s.length() > j && java.lang.Character.isDigit(s.charAt(j)))
            {
                flag = false;
            }
        }
        return flag;
    }

    public static String replaceVariable(String s, String s1, String s2)
    {
        do
        {
            int i = s.indexOf(s1);
            if(i != -1)
            {
                s = s.substring(0, i) + s2 + s.substring(i + s1.length());
            } else
            {
                return s;
            }
        } while(true);
    }

    public static String substitutionExpressionDelimiter(String s)
    {
        if(s.startsWith("s/") && s.endsWith("/"))
        {
            return "/";
        }
        if(s.startsWith("s|") && s.endsWith("|"))
        {
            return "|";
        } else
        {
            return "";
        }
    }

    public static boolean isSubstituteExpression(String s)
    {
        return TextUtils.substitutionExpressionDelimiter(s).length() > 0;
    }

    public static boolean isPrivateSubstituteExpression(String s)
    {
        return TextUtils.substitutionExpressionDelimiter(s).length() > 0 && s.indexOf(GET_PRIVATE) > 0;
    }

    public static String substitute(String s)
    {
        return TextUtils.substitute(s, null);
    }

    public static String substitute(String s, COM.dragonflow.SiteView.SiteViewObject siteviewobject)
    {
        String s1 = TextUtils.substitutionExpressionDelimiter(s);
        if(s1.length() > 0)
        {
            int i = s.indexOf(s1);
            int j = s.lastIndexOf(s1);
            if(i >= 0 && j >= 0)
            {
                s = TextUtils.doSubstitution(s.substring(i + 1, j), siteviewobject);
            }
        }
        return s;
    }

    public static String doSubstitution(String s)
    {
        return TextUtils.doSubstitution(s, null);
    }

    public static String doSubstitution(String s, COM.dragonflow.SiteView.SiteViewObject siteviewobject)
    {
        if(s.indexOf('$') != -1)
        {
            int i = s.indexOf(GET_PRIVATE);
            if(i >= 0)
            {
                HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
                Object obj = null;
                Object obj1 = null;
                String s1 = null;
                String s2 = "";
                do
                {
                    if(i < 0)
                    {
                        break;
                    }
                    int k = s.indexOf("$", i + 1);
                    if(k > i)
                    {
                        String s4 = s.substring(i + GET_PRIVATE.length(), k);
                        Array array = TextUtils.getMultipleValues(hashmap, "_private");
                        int j1 = 0;
                        do
                        {
                            if(j1 >= array.size())
                            {
                                break;
                            }
                            HashMap hashmap1 = TextUtils.stringToHashMap((String)array.get(j1));
                            String s3 = TextUtils.getValue(hashmap1, "_name");
                            if(s3.equals(s4))
                            {
                                s1 = TextUtils.getValue(hashmap1, "_privateValue");
                                if(s1.length() == 0)
                                {
                                    s1 = TextUtils.getValue(hashmap1, "_value");
                                }
                                s1 = s1.replace('^', ' ');
                                break;
                            }
                            j1++;
                        } while(true);
                        if(s1 == null)
                        {
                            COM.dragonflow.Log.LogManager.log("Error", "No value found for " + s + ".  Please check spelling of variable in post data.");
                            s1 = "";
                        }
                        s = s.substring(0, i) + s1 + s.substring(k + 1);
                    } else
                    {
                        COM.dragonflow.Log.LogManager.log("Error", "No second '$' found in variable name" + s + ".  Please use format s|variablename=$private-variablename$| .");
                        break;
                    }
                    i = s.indexOf(GET_PRIVATE);
                } while(true);
            }
            String as[] = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", 
                "Nov", "Dec"
            };
            String as1[] = {
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", 
                "November", "December"
            };
            String as2[] = {
                "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
            };
            String as3[] = {
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
            };
            long l = 0L;
            int i1 = s.indexOf(OFFSET_MINUTES);
            if(i1 >= 0)
            {
                int k1 = s.indexOf("$", i1 + 1);
                if(k1 > i1)
                {
                    String s5 = s.substring(i1 + OFFSET_MINUTES.length(), k1);
                    s = s.substring(0, i1) + s.substring(k1 + 1);
                    l = TextUtils.toLong(s5) * 1000L * 60L;
                }
            }
            if(siteviewobject != null)
            {
                int j = s.indexOf(GET_PROPERTY);
                do
                {
                    if(j < 0)
                    {
                        break;
                    }
                    int l1 = s.indexOf("$", j + 1);
                    if(l1 <= j)
                    {
                        break;
                    }
                    String s6 = s.substring(j + GET_PROPERTY.length(), l1);
                    String s7 = siteviewobject.getValue(s6);
                    s = s.substring(0, j) + s7 + s.substring(l1 + 1);
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "Value substitute: replacing \"" + s6 + "\" with \"" + s7 + "\" resulted in \"" + s + "\"");
                    j = s.indexOf(GET_PROPERTY);
                } while(true);
            }
            long l2 = COM.dragonflow.SiteView.Platform.timeMillis();
            java.util.Date date = new Date(l2 + l);
            java.util.GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTime(date);
            while((i1 = s.indexOf("$fullMonthName_")) >= 0) 
            {
                int i2 = s.indexOf("$", i1 + 1);
                String s20 = s.substring(i1, i2 + 1);
                i2 = s20.indexOf("_");
                String s8 = s20.substring(i2 + 1, i2 + 3);
                i2 = s20.indexOf("_", i2 + 1);
                String s12;
                if(i2 >= 0)
                {
                    s12 = s20.substring(i2 + 1, i2 + 3);
                } else
                {
                    s12 = "";
                }
                java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat("MMMM", new Locale(s8, s12));
                String s16 = simpledateformat.format(date);
                s = TextUtils.replaceVariable(s, s20, s16);
            }
            while((i1 = s.indexOf("$monthName_")) >= 0) 
            {
                int j2 = s.indexOf("$", i1 + 1);
                String s21 = s.substring(i1, j2 + 1);
                j2 = s21.indexOf("_");
                String s9 = s21.substring(j2 + 1, j2 + 3);
                j2 = s21.indexOf("_", j2 + 1);
                String s13;
                if(j2 >= 0)
                {
                    s13 = s21.substring(j2 + 1, j2 + 3);
                } else
                {
                    s13 = "";
                }
                java.text.SimpleDateFormat simpledateformat1 = new SimpleDateFormat("MMM", new Locale(s9, s13));
                String s17 = simpledateformat1.format(date);
                s = TextUtils.replaceVariable(s, s21, s17);
            }
            while((i1 = s.indexOf("$fullWeekdayName_")) >= 0) 
            {
                int k2 = s.indexOf("$", i1 + 1);
                String s22 = s.substring(i1, k2 + 1);
                k2 = s22.indexOf("_");
                String s10 = s22.substring(k2 + 1, k2 + 3);
                k2 = s22.indexOf("_", k2 + 1);
                String s14;
                if(k2 >= 0)
                {
                    s14 = s22.substring(k2 + 1, k2 + 3);
                } else
                {
                    s14 = "";
                }
                java.text.SimpleDateFormat simpledateformat2 = new SimpleDateFormat("EEEE", new Locale(s10, s14));
                String s18 = simpledateformat2.format(date);
                s = TextUtils.replaceVariable(s, s22, s18);
            }
            while((i1 = s.indexOf("$weekdayName_")) >= 0) 
            {
                int i3 = s.indexOf("$", i1 + 1);
                String s23 = s.substring(i1, i3 + 1);
                i3 = s23.indexOf("_");
                String s11 = s23.substring(i3 + 1, i3 + 3);
                i3 = s23.indexOf("_", i3 + 1);
                String s15;
                if(i3 >= 0)
                {
                    s15 = s23.substring(i3 + 1, i3 + 3);
                } else
                {
                    s15 = "";
                }
                java.text.SimpleDateFormat simpledateformat3 = new SimpleDateFormat("EEE", new Locale(s11, s15));
                String s19 = simpledateformat3.format(date);
                s = TextUtils.replaceVariable(s, s23, s19);
            }
            String s24 = "" + gregoriancalendar.get(11);
            String s25 = TextUtils.padWithZeros(s24, 2);
            String s26 = "" + gregoriancalendar.get(12);
            String s27 = TextUtils.padWithZeros(s26, 2);
            String s28 = "" + (gregoriancalendar.get(2) + 1);
            String s29 = TextUtils.padWithZeros(s28, 2);
            String s30 = "" + gregoriancalendar.get(5);
            String s31 = TextUtils.padWithZeros(s30, 2);
            int j3 = gregoriancalendar.get(7) - 1;
            String s32 = as2[j3];
            String s33 = as3[j3];
            int k3 = gregoriancalendar.get(2);
            String s34 = as[k3];
            String s35 = as1[k3];
            int l3 = gregoriancalendar.get(1);
            String s36 = TextUtils.padWithZeros("" + (l3 - 1900) % 100, 2);
            String s37 = "" + l3;
            String s38 = "" + gregoriancalendar.getTimeInMillis();
            s = TextUtils.replaceVariable(s, "$hour$", s24);
            s = TextUtils.replaceVariable(s, "$0hour$", s25);
            s = TextUtils.replaceVariable(s, "$minute$", s26);
            s = TextUtils.replaceVariable(s, "$0minute$", s27);
            s = TextUtils.replaceVariable(s, "$month$", s28);
            s = TextUtils.replaceVariable(s, "$0month$", s29);
            s = TextUtils.replaceVariable(s, "$monthName$", s34);
            s = TextUtils.replaceVariable(s, "$fullMonthName$", s35);
            s = TextUtils.replaceVariable(s, "$day$", s30);
            s = TextUtils.replaceVariable(s, "$0day$", s31);
            s = TextUtils.replaceVariable(s, "$weekdayName$", s32);
            s = TextUtils.replaceVariable(s, "$fullWeekdayName$", s33);
            s = TextUtils.replaceVariable(s, "$year$", s37);
            s = TextUtils.replaceVariable(s, "$shortYear$", s36);
            s = TextUtils.replaceVariable(s, "$ticks$", s38);
        }
        return s;
    }

    public static void rewriteMapEntry(HashMap hashmap, String s)
    {
        Array array = new Array();
        String s1;
        for(Enumeration enumeration = (Enumeration) hashmap.values(s); enumeration.hasMoreElements(); array.add(TextUtils.stringToHashMap(s1)))
        {
            s1 = (String)enumeration.nextElement();
        }

        hashmap.remove(s);
        for(int i = 0; i < array.size(); i++)
        {
            hashmap.add(s, TextUtils.hashMapToString((HashMap)array.get(i)));
        }

    }

    public static boolean match(String s, String s1)
    {
        return TextUtils.matchExpression(s, s1) == COM.dragonflow.SiteView.Monitor.kURLok;
    }

    public static int matchExpression(String s, String s1)
    {
        return TextUtils.matchExpression(s, s1, new Array(), new StringBuffer());
    }

    public static int matchExpression(String s, String s1, Array array, StringBuffer stringbuffer)
    {
        return TextUtils.matchExpression(s, s1, array, stringbuffer, "matched ");
    }

    public static int matchExpression(String s, String s1, Array array, StringBuffer stringbuffer, String s2)
    {
        Array array1 = new Array();
        int i = TextUtils.matchExpression(s, s1, array1);
        if(array1.size() >= 2)
        {
            stringbuffer.append(s2);
        }
        for(int j = 1; j < array1.size(); j++)
        {
            if(array != null)
            {
                array.add(array1.get(j));
            }
            if(j != 1)
            {
                stringbuffer.append(", ");
            }
            stringbuffer.append(array1.get(j));
        }

        return i;
    }

    public static int matchExpressionForWebServiceMonitor(String s, String s1, Array array, StringBuffer stringbuffer)
    {
        String s2 = "matched ";
        Array array1 = new Array();
        int i = TextUtils.matchExpression(s, s1, array1);
        if(array1.size() >= 1)
        {
            stringbuffer.append(s2);
        }
        for(int j = 0; j < array1.size(); j++)
        {
            if(array != null)
            {
                array.add(array1.get(j));
            }
            if(j != 0)
            {
                stringbuffer.append(", ");
            }
            stringbuffer.append(array1.get(j));
        }

        return i;
    }

    public static String stripLineTerminators(String s)
    {
        return s;
    }

    public static int matchExpression(String s, String s1, Array array)
    {
        return TextUtils.matchExpression(s, s1, array, new Perl5Util());
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param array
     * @param perl5util
     * @return
     */
    public static int matchExpression(String s, String s1, Array array, COM.oroinc.text.perl.Perl5Util perl5util)
    {
        int i;
        boolean flag;
        i = COM.dragonflow.SiteView.Monitor.kURLContentMatchError;
        s = TextUtils.stripLineTerminators(s);
        s1 = TextUtils.stripLineTerminators(s1);
        s1 = TextUtils.doSubstitution(s1);
        if(s1.startsWith("xml."))
        {
            StringBuffer stringbuffer = new StringBuffer();
            Array array1 = new Array();
            i = XSLUtils.matchXML(s, s1, array1, stringbuffer);
            array.add(stringbuffer.toString());
            for(int j = 0; j < array1.size(); j++)
            {
                array.add(array1.get(j));
            }
        } else 
        if(TextUtils.isRegularExpression(s1))
        {
        if(TextUtils.isWhiteSpaceRegularExpression(s1))
        {
            String s2 = "\t\n\f\r";
            StringBuffer stringbuffer1 = new StringBuffer("");
            boolean flag3 = false;
            for(int k = 0; k < s.length(); k++)
            {
                char c = s.charAt(k);
                if(s2.indexOf(c) != -1)
                {
                    c = ' ';
                }
                if(c != ' ' || !flag3)
                {
                    stringbuffer1.append(c);
                }
                flag3 = c == ' ';
            }

            s = stringbuffer1.toString();
        }
        flag = false;
        boolean flag2;
        boolean flag4;
        boolean flag5;

        try {
        if(perl5util == null)
        {
            perl5util = new Perl5Util();
        }
        flag2 = TextUtils.isComplementedRegularExpression(s1);
        flag4 = TextUtils.isDateRegularExpression(s1);
        flag5 = TextUtils.isAbsoluteValueRegularExpression(s1);
        if(flag2 || flag4)
        {
            s1 = TextUtils.getLegalRegularExpression(s1);
        }
        flag = perl5util.match(s1, s);
        if(flag)
        {
        array.add(perl5util.group(0));
        if(perl5util.groups() >= 2)
        {
         for (int l = 1; l < perl5util.groups(); l ++) {
        String s3 = perl5util.group(l);
        if(s3 == null) {
        s3 = "n/a";
        }
        else if(flag4) {
        long l2;
        Enumeration enumeration;
        java.util.Date date;
        long l3;
        String s4;
        java.util.Date date1;
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        l2 = TextUtils.toLong(siteviewgroup.getSetting("_timeOffset")) * 1000L;
        enumeration = siteviewgroup.getMultipleSettings("_dateCompareFormat");
        date = new Date(COM.dragonflow.SiteView.Platform.makeDate().getTime() + l2);
        l3 = -1L;
        s4 = "";
        date1 = null;
        if(enumeration.hasMoreElements()) {
        while (enumeration.hasMoreElements()) {
        s4 = (String)enumeration.nextElement();
        java.text.SimpleDateFormat simpledateformat = new SimpleDateFormat(s4);
        java.text.ParsePosition parseposition = new ParsePosition(0);
        date1 = simpledateformat.parse("" + s3, parseposition);
        if(date1 != null) {
        l3 = (date.getTime() - date1.getTime()) / 1000L;
        }
        }
        } else {
        java.text.SimpleDateFormat simpledateformat1 = new SimpleDateFormat();
        java.text.ParsePosition parseposition1 = new ParsePosition(0);
        date1 = simpledateformat1.parse("" + s3, parseposition1);
        if(date1 != null)
        {
            l3 = (date.getTime() - date1.getTime()) / 1000L;
        }
        }

        if(l3 == -1L)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "date conversion error, " + s3 + ", " + date + ", " + s4 + ", " + l2);
        } else
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "date conversion, " + l3 + " seconds old, " + s3 + ", " + date1 + ", " + date + ", " + s4 + ", " + l2);
            s3 = "" + l3;
        }
        }

        if(flag5)
        {
            try
            {
                long l1 = (new Long((String)s3)).longValue();
                if(l1 < 0L)
                {
                    s3 = "" + l1 * -1L;
                }
            }
            catch(java.lang.Exception exception1) {
                /* empty */
            }
        }
        array.add(s3);
         }
        }
        }
         

        if(flag2)
        {
            flag = !flag;
        }
        }
        catch (java.lang.Exception exception) {
        java.lang.System.out.println("Regular Expression exception: " + exception);
        }
        if(flag)
        {
            i = COM.dragonflow.SiteView.Monitor.kURLok;
        }
        }
        else {
        boolean flag1 = false;
        flag1 = s.indexOf(s1) >= 0;
        if(flag1)
        {
            i = COM.dragonflow.SiteView.Monitor.kURLok;
            array.add(s1);
        }
        }

        return i;
    }

    public static boolean isValueExpression(String s)
    {
        return s.startsWith("xml.") || TextUtils.isRegularExpression(s);
    }

    public static boolean isRegularExpression(String s)
    {
        if(s.startsWith("/"))
        {
            int i = s.lastIndexOf("/");
            if(i > 0)
            {
                String s1 = s.substring(i + 1);
                if(TextUtils.onlyChars(s1, allRegExModifiers))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isComplementedRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            if(s1.indexOf("c") >= 0)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isWhiteSpaceRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            if(s1.indexOf("w") >= 0)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isAbsoluteValueRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            if(s1.indexOf("a") >= 0)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isDateRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            if(s1.indexOf("d") >= 0)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isMultiLineRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            if(s1.indexOf("s") >= 0)
            {
                return true;
            }
        }
        return false;
    }

    public static String getLegalRegularExpression(String s)
    {
        int i = s.lastIndexOf("/");
        if(i > 0)
        {
            String s1 = s.substring(i + 1);
            s = s.substring(0, i + 1) + TextUtils.removeChars(s1, extRegExModifiers);
        }
        return s;
    }

    public static String legalMatchString(String s)
    {
        String s1 = "";
        if(s.startsWith("/"))
        {
            int i = s.lastIndexOf("/");
            if(i > 0)
            {
                String s2 = s.substring(i + 1);
                if(TextUtils.onlyChars(s2, allRegExModifiers))
                {
                    if(TextUtils.isComplementedRegularExpression(s) || TextUtils.isDateRegularExpression(s))
                    {
                        s = TextUtils.getLegalRegularExpression(s);
                    }
                    COM.oroinc.text.perl.Perl5Util perl5util = new Perl5Util();
                    try
                    {
                        perl5util.match(s, "test line for matcher");
                    }
                    catch(java.lang.Exception exception)
                    {
                        s1 = exception.getMessage();
                    }
                } else
                {
                    s1 = "only i, m, c, d, w, or s modifiers are allowed";
                }
            }
        }
        return s1;
    }

    public static String replaceMatchValues(String s, Array array, Array array1)
    {
        int i = 0;
        do
        {
            if(s.indexOf("{$$") == -1 || s.indexOf("{$$") == i || s.indexOf(".") == -1 || s.indexOf("}") == -1 || array1 == null)
            {
                break;
            }
            i = s.indexOf("{$$");
            int j = s.indexOf("{$$") + 3;
            int l = s.substring(j).indexOf(".") + j;
            int i1 = s.substring(l).indexOf("}") + l;
            if(j < l && l < i1)
            {
                int j1 = java.lang.Integer.parseInt(s.substring(j, l));
                int k1 = java.lang.Integer.parseInt(s.substring(l + 1, i1));
                if(array1.size() >= j1 && k1 > 0 && k1 < 30)
                {
                    Array array2 = (Array)array1.get(j1 - 1);
                    if(array2.size() >= k1)
                    {
                        String s3 = (String)array2.get(k1 - 1);
                        s = TextUtils.replaceString(s, s.substring(j - 3, i1 + 1), s3);
                    }
                }
            }
        } while(true);
        if(s.indexOf("{$") != -1 && array != null)
        {
            for(int k = 1; k <= array.size(); k++)
            {
                String s1 = "{$" + k + "}";
                if(s.indexOf(s1) != -1)
                {
                    String s2 = (String)array.get(k - 1);
                    s = TextUtils.replaceString(s, s1, s2);
                }
            }

        }
        return s;
    }

    public static boolean startsWithIgnoreCase(String s, String s1)
    {
        if(s.length() >= s1.length())
        {
            return s.substring(0, s1.length()).equalsIgnoreCase(s1);
        } else
        {
            return false;
        }
    }

    public static boolean endsWithIgnoreCase(String s, String s1)
    {
        if(s.length() >= s1.length())
        {
            return s.substring(s.length() - s1.length()).equalsIgnoreCase(s1);
        } else
        {
            return false;
        }
    }

    public static boolean stringBufferEndsWith(StringBuffer stringbuffer, String s)
    {
        int i = stringbuffer.length() - s.length();
        if(i < 0)
        {
            return false;
        }
        for(int j = 0; j < s.length(); j++)
        {
            if(stringbuffer.charAt(i + j) != s.charAt(j))
            {
                return false;
            }
        }

        return true;
    }

    public static String reverse(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s);
        stringbuffer.reverse();
        return stringbuffer.toString();
    }

    public static void addToBuffer(StringBuffer stringbuffer, String s)
    {
        if(stringbuffer.length() > 0)
        {
            stringbuffer.append(", ");
        }
        stringbuffer.append(s);
    }

    public static Array sortStrings(Enumeration enumeration, boolean flag)
    {
        Array array = new Array();
        for(; enumeration.hasMoreElements(); array.add(enumeration.nextElement())) { }
        return TextUtils.sortStrings(array, flag);
    }

    public static Array sortStrings(Array array, boolean flag)
    {
        Sorting.sort(array.begin(), array.end(), new StringBinaryPredicate(flag));
        return array;
    }

    public static Array sortStringHashMapArray(Array array, String as[], boolean flag)
    {
        Sorting.sort(array.begin(), array.end(), new StringHashMapBinaryPredicate(as, flag));
        return array;
    }

    public static String arrayToString(Array array)
    {
        return TextUtils.arrayToString(array, ",");
    }

    public static String arrayToString(Array array, String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < array.size(); i++)
        {
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(s);
            }
            stringbuffer.append(array.get(i));
        }

        return stringbuffer.toString();
    }

    public static Array stringToArray(String s, String s1)
    {
        Array array = new Array();
        while(s.length() > 0) 
        {
            int i = s.indexOf(s1);
            if(i == -1)
            {
                array.add(s);
                s = "";
            } else
            {
                array.add(s.substring(0, i));
                s = s.substring(i + s1.length());
            }
        }
        return array;
    }

    public static Array enumToArray(Enumeration enumeration)
    {
        Array array = null;
        if(enumeration != null && enumeration.hasMoreElements())
        {
            for(; enumeration.hasMoreElements(); array.add(enumeration.nextElement()))
            {
                if(array == null)
                {
                    array = new Array();
                }
            }

        }
        return array;
    }

    public static long getClosestNumber(String s, long l)
    {
        String as[] = TextUtils.split(s, ",");
        for(int i = 0; i < as.length; i++)
        {
            long l1 = TextUtils.toLong(as[i]);
            if(l1 > l)
            {
                return l1;
            }
        }

        return 0L;
    }

    public static String keyToDisplayString(String s, Array array)
    {
        for(int i = 0; i < array.size(); i += 2)
        {
            String s1 = (String)array.get(i);
            if(s1.equals(s))
            {
                return (String)array.get(i + 1);
            }
        }

        return "";
    }

    public static String replaceInHashMap(HashMap hashmap, String as[], HashMap hashmap1)
    {
        String s = "";
        Enumeration enumeration = (Enumeration) hashmap.keys();
        while (enumeration.hasMoreElements()) {
            java.lang.Object obj = enumeration.nextElement();
            if(hashmap1 != null)
            {
                String s1 = obj.toString();
                if(hashmap1.get(s1) != null)
                {
                    continue;
                }
            }
            java.lang.Object obj1 = hashmap.get(obj);
            if(obj1 != null)
            {
                if(obj1 instanceof String)
                {
                    String s2 = TextUtils.replaceString((String)obj1, as);
                    if(!s2.equals(obj1))
                    {
                        s = s + "&nbsp;&nbsp;replaced " + obj + ", " + obj1 + " ==> " + s2 + "<br>";
                        hashmap.put(obj, s2);
                    }
                } else
                if(obj1 instanceof Array)
                {
                    Array array = (Array)obj1;
                    int i = 0;
                    while(i < array.size()) 
                    {
                        java.lang.Object obj2 = array.get(i);
                        if(obj2 instanceof String)
                        {
                            String s3 = TextUtils.replaceString((String)obj2, as);
                            if(!s3.equals(obj2))
                            {
                                array.put(i, s3);
                                s = s + "&nbsp;&nbsp;replaced " + i + ", " + obj2 + " ==> " + s3 + "<br>";
                            }
                        }
                        i++;
                    }
                } else
                if(obj1 instanceof HashMap)
                {
                    TextUtils.replaceInHashMap((HashMap)obj1, as, null);
                }
            }
        } 
        return s;
    }

    public static String replaceInHashMapList(Array array, String as[], HashMap hashmap)
    {
        StringBuffer stringbuffer = new StringBuffer();
        HashMap hashmap1;
        for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); stringbuffer.append(TextUtils.replaceInHashMap(hashmap1, as, hashmap)))
        {
            hashmap1 = (HashMap)enumeration.nextElement();
        }

        return stringbuffer.toString();
    }

    public static HashMap findItemInHashMapArray(Array array, HashMap hashmap, boolean flag)
    {
        return TextUtils.findItemInHashMapArray(array, 0, -1, hashmap, flag);
    }

    public static HashMap findItemInHashMapArray(Array array, int i, int j, HashMap hashmap, boolean flag)
    {
        if(array == null)
        {
            return null;
        }
        if(i >= array.size())
        {
            return null;
        }
        if(j == -1)
        {
            j = array.size() - 1;
        }
        i = java.lang.Math.max(i, 0);
        if(hashmap == null)
        {
            hashmap = new HashMap();
        }

        lab: for(int k = i; k <= j; k++)
        {
            HashMap hashmap1 = (HashMap)array.get(k);
            if(hashmap1 == null)
            {
                continue;
            }
            Enumeration enumeration = (Enumeration) hashmap.keys();
            Enumeration enumeration1 = hashmap.elements();
            while(enumeration.hasMoreElements()) 
            {
                java.lang.Object obj = enumeration.nextElement();
                java.lang.Object obj1 = enumeration1.nextElement();
                java.lang.Object obj2 = hashmap1.get(obj);
                if(!flag && (obj1 instanceof String) ? obj1.equals(obj2) : ((String)obj1).equalsIgnoreCase((String)obj2))
                {
                    continue lab;
                }
            }
            return hashmap1;
        }

        return null;
    }

    public static void mergeHashMaps(HashMap hashmap, HashMap hashmap1, boolean flag, boolean flag1)
    {
        if(hashmap == null)
        {
            return;
        }
        if(hashmap1 == null)
        {
            throw new NullPointerException();
        }
        Enumeration enumeration = (Enumeration) hashmap.keys();
        while (enumeration.hasMoreElements()) {
            java.lang.Object obj = enumeration.nextElement();
            if(hashmap1.get(obj) == null && flag1 || flag)
            {
                hashmap1.put(obj, hashmap.get(obj));
            }
        } 
    }

    public static String parseNonPrintableCharacters(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(TextUtils.isPrintable(c))
            {
                stringbuffer.append(c);
            }
        }

        return new String(stringbuffer);
    }

    private static final boolean isPrintable(char c)
    {
        return c >= ' ' && c <= '\u7777';
    }

    public static String replaceParameters(String s, String s1, String as[])
    {
        if(as == null)
        {
            as = (new String[] {
                "$", "%"
            });
        }
        String s2 = s;
        if(s1 != null)
        {
            ParameterParser parameterparser = new ParameterParser(s1);
            java.util.List list = parameterparser.doParse();
            java.util.ListIterator listiterator = list.listIterator();
            for(int i = 1; listiterator.hasNext(); i++)
            {
                String s3 = (String)listiterator.next();
                for(int j = 0; j < as.length; j++)
                {
                    String s4 = as[j];
                    if(s4 != null)
                    {
                        s2 = TextUtils.replaceString(s2, s4 + String.valueOf(i), s3);
                    }
                }

            }

        }
        return s2;
    }

    public static java.util.Vector parseParentheses(String s)
        throws java.lang.Exception
    {
        if(s == null || s.length() <= 0)
        {
            return new Vector();
        }
        java.util.Vector vector = new Vector();
        java.util.Stack stack = new Stack();
        StringBuffer stringbuffer = null;
        boolean flag = false;
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if(c == '(' && !flag)
            {
                i++;
                stringbuffer = new StringBuffer();
                stringbuffer.append(c);
                stack.push(stringbuffer);
                vector.add(stringbuffer);
                continue;
            }
            if(c == ')' && !flag)
            {
                i--;
                if(stringbuffer == null || i < 0)
                {
                    throw new Exception("Got closing parenthesis with no opening parenthesis, position: " + j + " buffer: " + s.substring(0, j) + ")");
                }
                stringbuffer.append(c);
                if(stack.empty())
                {
                    if(i != 0)
                    {
                        throw new Exception("Parentheses are unbalanced, check backslashes and parentheses, position: " + j + " buffer: " + s.substring(0, j));
                    } else
                    {
                        return vector;
                    }
                }
                stack.pop();
                if(!stack.empty())
                {
                    StringBuffer stringbuffer2 = (StringBuffer)stack.pop();
                    stringbuffer2.append(stringbuffer);
                    stack.push(stringbuffer2);
                    stringbuffer = stringbuffer2;
                } else
                {
                    stringbuffer = null;
                }
                continue;
            }
            if(c == '\\')
            {
                if(flag)
                {
                    flag = false;
                } else
                {
                    flag = true;
                }
                if(stringbuffer != null)
                {
                    stringbuffer.append(c);
                }
                continue;
            }
            flag = false;
            if(stringbuffer != null)
            {
                stringbuffer.append(c);
            }
        }

        if(i > 0)
        {
            StringBuffer stringbuffer1 = new StringBuffer();
            if(!stack.empty())
            {
                stringbuffer1 = (StringBuffer)stack.pop();
            }
            throw new Exception("First parenthesis here not matched, check backslashes and parentheses. buffer: " + stringbuffer1.toString());
        }
        if(!$assertionsDisabled && i != 0)
        {
            throw new AssertionError("unmatched parentheses undetected. areWeBalanced=" + i + " regular expression: " + s);
        } else
        {
            return vector;
        }
    }

    public static void setDecFormat(java.text.DecimalFormat decimalformat)
    {
        DecFormat = decimalformat;
    }

    public static boolean isUseDecFormat()
    {
        TextUtils.initDecValues();
        return useDecFormat;
    }

    public static java.text.DecimalFormat getDecFormat()
    {
        TextUtils.initDecValues();
        return DecFormat;
    }

    public static java.text.DecimalFormat getDecFormatFixed()
    {
        TextUtils.initDecValues();
        return DecFormatFixed;
    }

    public static String convertBytesToString(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < abyte0.length; i++)
        {
            byte byte0 = abyte0[i];
            char c = (char)(byte0 & 0xff);
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    public static byte[] convertStringToBytes(String s)
    {
        byte abyte0[] = new byte[s.length()];
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            abyte0[i] = (byte)c;
        }

        return abyte0;
    }

    public static String encodeArgs(String s)
    {
        int j = 0;
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            int i = TextUtils.findNextSymbol(s, j);
            if(i >= 0)
            {
                stringbuffer.append(s.substring(j, i));
                stringbuffer.append("&amp;");
                int k = s.indexOf(';', i);
                stringbuffer.append(s.substring(i + 1, k + 1));
                j = k + 1;
            } else
            {
                stringbuffer.append(s.substring(j));
                return stringbuffer.toString();
            }
        } while(true);
    }

    private static int findNextSymbol(String s, int i)
    {
        int k = i;
        do
        {
            int j = s.indexOf('&', k);
            if(j >= 0)
            {
                int l = s.indexOf(';', j);
                if(l < 0)
                {
                    k = j + 1;
                } else
                {
                    String s1 = s.substring(j, l + 1);
                    if(s1.equals("&amp;") || s1.equals("&lt;") || s1.equals("&gt;") || s1.equals("&quot;"))
                    {
                        return j;
                    }
                    k = l + 1;
                }
            } else
            {
                return -1;
            }
        } while(true);
    }

    public static void appendAndTerminateString(String s, StringBuffer stringbuffer, long l, String s1)
    {
        if((long)stringbuffer.length() <= l)
        {
            stringbuffer.append(s + "^");
            if((long)stringbuffer.length() > l)
            {
                stringbuffer.append("^SiteView character output limited by" + s1);
            }
        }
    }

    public static boolean stringContainsSubstringFromArray(String s, String as[])
    {
        if(s == null || as == null)
        {
            return false;
        }
        for(int i = 0; i < as.length; i++)
        {
            if(as == null)
            {
                continue;
            }
            String s1 = as[i];
            if(s1 != null && s.indexOf(s1) != -1)
            {
                return true;
            }
        }

        return false;
    }

    public static String[] getSettingAsStringArray(String s)
    {
        String s1 = (String)COM.dragonflow.SiteView.MasterConfig.getMasterConfig().get(s);
        if(s1 != null && s1.length() > 0)
        {
            String as[] = TextUtils.split(s1, ",");
            if(as != null && as.length > 0)
            {
                return as;
            }
        }
        return null;
    }

    static 
    {
        $assertionsDisabled = !(TextUtils.class).desiredAssertionStatus();
        MINUTE_SECONDS = 60;
        HOUR_SECONDS = 60 * MINUTE_SECONDS;
        DAY_SECONDS = 24 * HOUR_SECONDS;
        regExModifiers = "ism";
        extRegExModifiers = "cdwa";
        allRegExModifiers = regExModifiers + extRegExModifiers;
    }
}
