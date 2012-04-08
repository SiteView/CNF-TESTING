/*
 * 
 * Created on 2005-3-7 1:09:46
 *
 * Exchange2k3MsgTrafficMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>Exchange2k3MsgTrafficMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.ExchangeMsgTrafficBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.LUtils;

public class Exchange2k3MsgTrafficMonitor extends ExchangeMsgTrafficBase {

    public Exchange2k3MsgTrafficMonitor() {
    }

    public void setProperty(StringProperty stringproperty, String s) throws NullPointerException {
        if (stringproperty == pLogDir && s.startsWith("\\\\") && s.indexOf("\\", 2) == -1) {
            String s1 = s.substring(2);
            StringTokenizer stringtokenizer = new StringTokenizer(s1, ".");
            String s2 = stringtokenizer.nextToken();
            s = "\\\\" + s1 + "\\" + s2 + ".log";
        }
        super.setProperty(stringproperty, s);
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected Vector getExchangeMessages() {
        try {
            Vector vector = new Vector();
            int i = getPropertyAsInteger(pInterval);
            Set set = getLogFilesToProcess(i);
            if (set == null) {
                return null;
            }

            Date date = new Date();
            GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTime(date);
            gregoriancalendar.add(12, -i);
            Date date1 = gregoriancalendar.getTime();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-M-d h:m:s z");
            SimpleDateFormat simpledateformat1 = new SimpleDateFormat("yyyyMMddHHmmss'.000000+000'");
            BufferedReader bufferedreader;
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                File file = (File) iterator.next();
                bufferedreader = new BufferedReader(new FileReader(file));
                Object obj = null;
                HashMap hashmap;
                Iterator iterator1;
                String s1;
                String s;
                while ((s = bufferedreader.readLine()) != null) {

                    if (s.trim().equals("") || s.startsWith("#")) {
                        continue;
                    }
                    List list = tokenizeMessage(s);
                    int j = list.size();
                    if (j < 21) {
                        continue;
                    }
                    hashmap = new HashMap();
                    iterator1 = list.iterator();
                    s1 = iterator1.next() + " " + iterator1.next();
                    try {
                        Object obj1 = simpledateformat.parse(s1);
                        if (((Date) (obj1)).compareTo(date1) < 0) {
                            continue;
                        }
                        hashmap.put("TimeLogged", simpledateformat1.format(((Date) (obj1))));
                    } catch (ParseException parseexception) {
                        LogManager.log("Error", "Exchange2k3MsgTrafficMonitor: " + getFullID() + ": " + parseexception.toString());
                        continue; /* Loop/switch isn't completed */
                    }
                    iterator1.next();
                    iterator1.next();
                    iterator1.next();
                    iterator1.next();
                    iterator1.next();
                    hashmap.put("RecipientAddress", iterator1.next());
                    String s2 = (String) iterator1.next();
                    if (isInboundType(s2) || isOutboundType(s2)) {
                        hashmap.put("EntryType", s2);
                        hashmap.put("MessageID", iterator1.next());
                        iterator1.next();
                        iterator1.next();
                        hashmap.put("Size", iterator1.next());
                        hashmap.put("RecipientCount", iterator1.next());
                        iterator1.next();
                        iterator1.next();
                        iterator1.next();
                        iterator1.next();
                        iterator1.next();
                        hashmap.put("SenderAddress", iterator1.next());
                        vector.add(hashmap);
                    }
                }
            }
            return vector;
            
        } catch (Exception exception) {
            setProperty(pNoData, "n/a");
            setProperty(pStatus, "error");
            setProperty(pStateString, exception.getMessage());
            LogManager.log("Error", "Exchange2k3MsgTrafficMonitor: " + getFullID() + " failed: " + exception.getMessage());
            return null;
        }
    }

    static {
        String s = (COM.dragonflow.StandardMonitor.Exchange2k3MsgTrafficMonitor.class).getName();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("status != OK\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood", true));
        setClassProperty(s, "description", "Monitors Exchange Performance Counters.");
        setClassProperty(s, "title", "Exchange 2000/2003 Message Traffic");
        setClassProperty(s, "class", "Exchange2k3MsgTrafficMonitor");
        setClassProperty(s, "help", "Exch2k3MsgTrafficMon.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Exchange 2000/2003 Message Traffic");
        setClassProperty(s, "classType", "application");
        if (!Platform.isWindows() || !LUtils.isValidSSforXLicense(new Exchange2k3MsgTrafficMonitor())) {
            setClassProperty(s, "loadable", "false");
        }
        setClassProperty(s, "addable", "false");
    }
}
