/*
 * 
 * Created on 2005-3-7 1:10:42
 *
 * Exchange55MsgTrafficMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>Exchange55MsgTrafficMonitor</code>
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
import java.util.Vector;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.ExchangeMsgTrafficBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.LUtils;

public class Exchange55MsgTrafficMonitor extends ExchangeMsgTrafficBase {

    public Exchange55MsgTrafficMonitor() {
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
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy.M.d h:m:s z");
            SimpleDateFormat simpledateformat1 = new SimpleDateFormat("yyyyMMddHHmmss'.000000+000'");
            BufferedReader bufferedreader;
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                File file = (File) iterator.next();
                bufferedreader = new BufferedReader(new FileReader(file));
                Object obj = null;
                Iterator iterator1;
                HashMap hashmap;
                String s2;
                String s3;
                String s;

                while ((s = bufferedreader.readLine()) != null) {
                    if (s.trim().equals("")) {
                        continue; /* Loop/switch isn't completed */
                    }
                    List list = tokenizeMessage(s);
                    int j = list.size();
                    if (j < 13) {
                        continue; /* Loop/switch isn't completed */
                    }
                    iterator1 = list.iterator();
                    hashmap = new HashMap();
                    hashmap.put("MessageID", iterator1.next());
                    s2 = (String) iterator1.next();
                    hashmap.put("EntryType", s2);
                    s3 = (String) iterator1.next();
                    s3 = s3 + " GMT";
                    try {
                        Object obj1 = simpledateformat.parse(s3);
                        if (((Date) (obj1)).compareTo(date1) < 0) {
                            continue;
                        }
                        hashmap.put("TimeLogged", simpledateformat1.format(((Date) (obj1))));
                    } catch (ParseException parseexception) {
                        LogManager.log("Error", "Exchange55MsgTrafficMonitor: " + getFullID() + ": " + parseexception.toString());
                        continue;
                    }

                    iterator1.next();
                    hashmap.put("PartnerServer", iterator1.next());
                    iterator1.next();
                    hashmap.put("SenderAddress", iterator1.next());
                    hashmap.put("Priority", iterator1.next());
                    hashmap.put("Size", iterator1.next());
                    hashmap.put("DeliveryTime", iterator1.next());
                    hashmap.put("Cost", iterator1.next());
                    iterator1.next();
                    s2 = (String) iterator1.next();
                    hashmap.put("RecipientCount", s2);
                    int k = 0;
                    try {
                        k = Integer.parseInt(s2);
                    } catch (Exception exception1) {
                        /* empty */
                    }
                    StringBuffer stringbuffer = new StringBuffer();
                    for (int l = 0; l < k; l ++) {
                        String s4 = bufferedreader.readLine();
                        if (l > 0) {
                            stringbuffer.append(", ");
                        }
                        stringbuffer.append(s4);
                    }

                    hashmap.put("RecipientAddress", stringbuffer.toString());
                    if (isInboundType(s2) || isOutboundType(s2)) {
                        vector.add(hashmap);
                    }
                    String s1;
                    while (true) {
                        s1 = bufferedreader.readLine();
                        if (s1 != null && !s1.trim().equals("")) {
                            break;
                        }
                    }
                }
            }
            return vector;
        } catch (Exception exception) {
            setProperty(pNoData, "n/a");
            setProperty(pStatus, "error");
            setProperty(pStateString, exception.toString());
            LogManager.log("Error", "Exchange55MsgTrafficMonitor: " + getFullID() + " failed: " + exception.toString());
            return null;
        }
    }

    static {
        String s = (COM.dragonflow.StandardMonitor.Exchange55MsgTrafficMonitor.class).getName();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("status != OK\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood", true));
        setClassProperty(s, "description", "Monitors Exchange 5.5 Message Traffic.");
        setClassProperty(s, "title", "Exchange 5.5 Message Traffic");
        setClassProperty(s, "class", "Exchange55MsgTrafficMonitor");
        setClassProperty(s, "help", "Exch55MsgTrafficMon.htm");
        setClassProperty(s, "target", "_logdir");
        setClassProperty(s, "topazName", "Exchange 5.5 Message Traffic");
        setClassProperty(s, "classType", "application");
        if (!Platform.isWindows() || !LUtils.isValidSSforXLicense(new Exchange55MsgTrafficMonitor())) {
            setClassProperty(s, "loadable", "false");
        }
        setClassProperty(s, "addable", "false");
    }
}
