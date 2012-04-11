/*
 * 
 * Created on 2005-2-16 15:04:31
 *
 * ExchangeMsgTrafficBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ExchangeMsgTrafficBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.Pair;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ExchangeToolBase, MasterConfig

public abstract class ExchangeMsgTrafficBase extends ExchangeToolBase {
    static class MECompare implements Comparator {

        public int compare(Object obj, Object obj1) {
            java.util.Map.Entry entry = (java.util.Map.Entry) obj;
            java.util.Map.Entry entry1 = (java.util.Map.Entry) obj1;
            int i = ((Integer) entry.getValue()).intValue();
            int j = ((Integer) entry1.getValue()).intValue();
            if (i < j) {
                return 1;
            }
            return i <= j ? 0 : -1;
        }

        MECompare() {
        }
    }

    protected static StringProperty pLogDir;

    protected static StringProperty pInterval;

    protected static StringProperty pMsgSize;

    protected static StringProperty pRecipients;

    protected static StringProperty pTopSendingDomainsN;

    protected static StringProperty pTopOutgoingUsersN;

    private static final int COST_IN_LICENSE_POINTS = 5;

    protected static final String DEFAULT_INBOUND_ENTRY_TYPE = "1012";

    protected static final String DEFAULT_OUTBOUND_ENTRY_TYPE = "1010";

    private static final String IN_ET_KEY = "_exchangeInboundTypes";

    private static final String OUT_ET_KEY = "_exchangeOutboundTypes";

    private static Set inboundEntryTypes;

    private static Set outboundEntryTypes;

    public ExchangeMsgTrafficBase() {
    }

    public String getHostname() {
        StringTokenizer stringtokenizer = new StringTokenizer(getProperty(pLogDir), File.separator);
        if (stringtokenizer.hasMoreTokens()) {
            return stringtokenizer.nextToken();
        } else {
            return "";
        }
    }

    protected boolean isInboundType(String s) {
        return inboundEntryTypes.contains(s);
    }

    protected boolean isOutboundType(String s) {
        return outboundEntryTypes.contains(s);
    }

    protected List tokenizeMessage(String s) {
        StringTokenizer stringtokenizer = new StringTokenizer(s, "\t\n", true);
        LinkedList linkedlist = new LinkedList();
        for (; stringtokenizer.hasMoreTokens(); linkedlist.add(stringtokenizer.nextToken())) {
        }
        ListIterator listiterator = linkedlist.listIterator();
        boolean flag = false;
        while (listiterator.hasNext()) {
            String s1 = (String) listiterator.next();
            if (s1.equals("\t")) {
                if (flag) {
                    listiterator.set("");
                } else {
                    listiterator.remove();
                }
                flag = true;
            } else {
                flag = false;
            }
        }
        return linkedlist;
    }

    protected Set getLogFilesToProcess(int i) {
        String s = getProperty(pLogDir);
        if (!s.endsWith(File.separator)) {
            s = s + File.separator;
        }
        File file = new File(s);
        if (!file.exists()) {
            return null;
        }
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
        HashSet hashset = new HashSet();
        Date date = new Date();
        GregorianCalendar gregoriancalendar = new GregorianCalendar();
        gregoriancalendar.setTime(date);
        GregorianCalendar gregoriancalendar1 = new GregorianCalendar();
        gregoriancalendar1.setTime(date);
        gregoriancalendar1.add(6, 2);
        gregoriancalendar.add(12, -i);
        gregoriancalendar.add(6, -1);
        for (; gregoriancalendar.getTime().compareTo(gregoriancalendar1.getTime()) < 0; gregoriancalendar.add(6, 1)) {
            File file1 = new File(s + simpledateformat.format(gregoriancalendar.getTime()) + ".log");
            if (file1.exists()) {
                hashset.add(file1);
            }
        }

        return hashset;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        try {
            Vector vector = getExchangeMessages();
            if (vector == null) {

                LogManager.log("Error", "Could not connect to the Exchange server");
                setProperty(pStateString, "Could not connect to the Exchange server");
                setProperty(pStatus, "Error");
                return false;
            }

            Document document = createNewDocument("Exchange Message Statistics");
            Map map = findOutboundMessages(vector);
            Map map1 = findInboundMessages(vector);
            showTopNSendingDomains(map1, document);
            showTopNOutgoingUsers(map, document);
            showSentMsgsLargerThanN(map, document);
            showSentMsgsNRecipients(map, document);
            saveDocument(document);
            setProperty(pStateString, "OK");
            setProperty(pStatus, "OK");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            setProperty(pNoData, "n/a");
            setProperty(pStatus, "error");
            setProperty(pStateString, e.getMessage());
            return false;
        }
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pInterval || stringproperty == pMsgSize || stringproperty == pRecipients) {
            int i = 0;
            try {
                i = Integer.parseInt(s);
            } catch (Exception exception) {
            }
            int j = 1;
            if (stringproperty == pRecipients) {
                j = 0;
            }
            if (i < j) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " must be an integer greater than or equal to " + j);
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    private void showTopNSendingDomains(Map map, Document document) {
        int i = getPropertyAsInteger(pTopSendingDomainsN);
        Vector vector = getTopNSendingDomains(map, i);
        addListContent(vector, "Top " + i + " Sending Domains", document);
    }

    private void showTopNOutgoingUsers(Map map, Document document) {
        int i = getPropertyAsInteger(pTopOutgoingUsersN);
        Vector vector = getTopNOutgoingUsers(map, i);
        addListContent(vector, "Top " + i + " Outgoing Users", document);
    }

    private void showSentMsgsLargerThanN(Map map, Document document) {
        int i = getPropertyAsInteger(pMsgSize);
        Vector vector = getSentMsgsLargerThanN(map, i);
        addListContent(vector, "Sent Messages Larger Than " + i + " Bytes", document);
    }

    private void showSentMsgsNRecipients(Map map, Document document) {
        int i = getPropertyAsInteger(pRecipients);
        Vector vector = getSentMsgsNRecipients(map, i);
        addListContent(vector, "Messages Sent to More Than " + i + " Recipients", document);
    }

    protected abstract Vector getExchangeMessages();

    private Vector getTopNSendingDomains(Map map, int i) {
        Vector vector = new Vector();
        if (i > 0) {
            java.util.Map.Entry aentry[] = findCollapsedCounts(map, "SendingDomain");
            for (int j = 0; j < i && j < aentry.length; j ++) {
                Vector vector1 = new Vector();
                vector.addElement(vector1);
                vector1.addElement(new Pair("Domain", aentry[j].getKey()));
                vector1.addElement(new Pair("Messages", aentry[j].getValue().toString()));
            }

        }
        return vector;
    }

    private Vector getTopNOutgoingUsers(Map map, int i) {
        Vector vector = new Vector();
        if (i > 0) {
            java.util.Map.Entry aentry[] = findCollapsedCounts(map, "SenderAddress");
            for (int j = 0; j < i && j < aentry.length; j ++) {
                Vector vector1 = new Vector();
                vector.addElement(vector1);
                vector1.addElement(new Pair("Sender", aentry[j].getKey()));
                vector1.addElement(new Pair("Messages", aentry[j].getValue().toString()));
            }

        }
        return vector;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param map
     * @param i
     * @return
     */
    private Vector getSentMsgsLargerThanN(Map map, int i) {
        int j = 0;
        Iterator iter = map.values().iterator();
        while (iter.hasNext()) {
            Map map1 = (Map) iter.next();
            String s = (String) map1.get("Size");
            try {
                int k = Integer.parseInt(s);
                if (k > i) {
                    j ++;
                }
            } catch (Exception exception) {
            }
        }

        Vector obj = new Vector();
        Vector vector = new Vector();
        obj.addElement(vector);
        vector.addElement(new Pair("Count", "" + j));
        return obj;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param map
     * @param i
     * @return
     */
    private Vector getSentMsgsNRecipients(Map map, int i) {
        int j = 0;
        Iterator iter = map.values().iterator();
        while (iter.hasNext()) {
            Map map1 = (Map) iter.next();
            String s = (String) map1.get("RecipientCount");
            try {
                int k = Integer.parseInt(s);
                if (k > i) {
                    j ++;
                }
            } catch (Exception exception) {
            }
        }

        Vector vec = new Vector();
        Vector vector = new Vector();
        vec.addElement(vector);
        vector.addElement(new Pair("Count", "" + j));
        return vec;
    }

    private Map findOutboundMessages(Vector vector) {
        HashMap hashmap = new HashMap();
        for (int i = 0; i < vector.size(); i ++) {
            HashMap hashmap1 = (HashMap) vector.elementAt(i);
            String s = (String) hashmap1.get("EntryType");
            String s1 = (String) hashmap1.get("MessageID");
            if (isOutboundType(s) && !hashmap.containsKey(s1)) {
                hashmap.put(s1, hashmap1);
            }
        }

        return hashmap;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param vector
     * @return
     */
    private Map findInboundMessages(Vector vector) {
        HashMap hashmap = new HashMap();
        for (int i = 0; i < vector.size(); i ++) {
            HashMap hashmap1 = (HashMap) vector.elementAt(i);
            String s = (String) hashmap1.get("EntryType");
            String s2 = (String) hashmap1.get("MessageID");
            if (isInboundType(s) && !hashmap.containsKey(s2)) {
                hashmap.put(s2, hashmap1);
            }
        }

        Iterator iterator = hashmap.values().iterator();
        while (iterator.hasNext()) {
            Map map = (Map) iterator.next();
            String s1 = (String) map.get("SenderAddress");
            int j = s1.indexOf("@");
            if (j != -1) {
                String s3 = s1.substring(j + 1);
                map.put("SendingDomain", s3);
            }
        }
        return hashmap;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param map
     * @param s
     * @return
     */
    private java.util.Map.Entry[] findCollapsedCounts(Map map, String s) {
        HashMap hashmap = new HashMap();
        Iterator iterator = map.values().iterator();
        while (iterator.hasNext()) {
            Map map1 = (Map) iterator.next();
            String s1 = (String) map1.get(s);
            if (s1 != null) {
                Integer integer = (Integer) hashmap.get(s1);
                int i = 1;
                if (integer != null) {
                    i += integer.intValue();
                }
                hashmap.put(s1, new Integer(i));
            }
        }

        Object aobj[] = hashmap.entrySet().toArray();
        Arrays.sort(aobj, new MECompare());
        java.util.Map.Entry aentry[] = new java.util.Map.Entry[aobj.length];
        System.arraycopy(((Object) (aobj)), 0, aentry, 0, aobj.length);
        return aentry;
    }

    public int getCostInLicensePoints() {
        return 5;
    }

    static {
        inboundEntryTypes = null;
        outboundEntryTypes = null;
        String s = (COM.dragonflow.SiteView.ExchangeMsgTrafficBase.class).getName();
        pLogDir = new StringProperty("_logdir");
        pLogDir.setDisplayText("Log directory", "Enter the UNC path of the message tracking log file directory (example \\\\servername\\tracking.log)");
        pLogDir.setParameterOptions(true, 1, false);
        pInterval = new StringProperty("_qinterval");
        pInterval.setDisplayText("Query interval", "How many minutes back to query for messages");
        pInterval.setParameterOptions(true, 4, false);
        pMsgSize = new StringProperty("_msgsize");
        pMsgSize.setDisplayText("Message size limit", "For \"Sent messages larger than N bytes\" statistic");
        pMsgSize.setParameterOptions(true, 5, false);
        pRecipients = new StringProperty("_recips");
        pRecipients.setDisplayText("Recipient limit", "For \"Sent messages with more than N recipients\" statistic");
        pRecipients.setParameterOptions(true, 6, false);
        pTopSendingDomainsN = new StringProperty("_topSendingDomainsN");
        pTopSendingDomainsN.setDisplayText("Number of domains", "For \"Top N sending domains\" statistic");
        pTopSendingDomainsN.setParameterOptions(true, 7, false);
        pTopOutgoingUsersN = new StringProperty("_topOutgoingUsersN");
        pTopOutgoingUsersN.setDisplayText("Number of outgoing users", "For \"Top N outgoing users\" statistic");
        pTopOutgoingUsersN.setParameterOptions(true, 8, false);
        StringProperty astringproperty[] = { pLogDir, pInterval, pMsgSize, pRecipients, pTopSendingDomainsN, pTopOutgoingUsersN };
        addProperties(s, astringproperty);
        inboundEntryTypes = new HashSet();
        outboundEntryTypes = new HashSet();
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        String s1 = (String) hashmap.get("_exchangeInboundTypes");
        if (s1 == null || s1.trim().equals("")) {
            s1 = "1012";
        }
        String s2 = (String) hashmap.get("_exchangeOutboundTypes");
        if (s2 == null || s2.trim().equals("")) {
            s2 = "1010";
        }
        String as[] = TextUtils.split(s1, ", ");
        inboundEntryTypes.addAll(Arrays.asList(as));
        as = TextUtils.split(s2, ", ");
        outboundEntryTypes.addAll(Arrays.asList(as));
    }
}