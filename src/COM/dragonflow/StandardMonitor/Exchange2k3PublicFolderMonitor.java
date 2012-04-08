/*
 * 
 * Created on 2005-3-7 1:10:14
 *
 * Exchange2k3PublicFolderMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>Exchange2k3PublicFolderMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.ExchangeWMIToolBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.Pair;
import COM.dragonflow.Utils.WMIUtils;

public class Exchange2k3PublicFolderMonitor extends ExchangeWMIToolBase {

    static DateFormat mDisplayDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    static StringProperty pNotAccessedN;

    private static final int COST_IN_LICENSE_POINTS = 5;

    public Exchange2k3PublicFolderMonitor() {
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    protected boolean update() {
        try {
            String s = "SELECT * FROM Exchange_PublicFolder";
            Vector vector = executeQuery(s);
            if (vector == null) {
                return false;
            }
            for (int i = 0; i < vector.size(); i ++) {
                Map map = (Map) vector.elementAt(i);
                Date date = WMIUtils.makeJavaDateFromWMIDate((String) map.get("CreationTime"));
                if (date != null) {
                    map.put("JavaCreationTime", date);
                }
                date = WMIUtils.makeJavaDateFromWMIDate((String) map.get("LastAccessTime"));
                if (date != null) {
                    map.put("JavaLastAccessTime", date);
                }
                date = WMIUtils.makeJavaDateFromWMIDate((String) map.get("LastModificationTime"));
                if (date != null) {
                    map.put("JavaLastModificationTime", date);
                }
            }

            Document document = createNewDocument("Exchange Public Folder Statistics");
            showAllFolderStats(vector, document);
            showEmptyFolders(vector, document);
            showNotAccessedInNDays(vector, document);
            saveDocument(document);
            setProperty(pStateString, "OK");
            setProperty(pStatus, "OK");
            return true;
        } catch (Exception e) {
            setProperty(pNoData, "n/a");
            setProperty(pStatus, "error");
            setProperty(pStateString, e.getMessage());
            return false;
        }
    }

    private void showAllFolderStats(Vector vector, Document document) {
        Vector vector1 = new Vector();
        for (int i = 0; i < vector.size(); i ++) {
            Vector vector2 = new Vector();
            vector1.addElement(vector2);
            Map map = (Map) vector.elementAt(i);
            String s = (String) map.get("Name");
            vector2.addElement(new Pair("Name", s));
            Date date = (Date) map.get("JavaLastAccessTime");
            if (date != null) {
                synchronized (mDisplayDateFormat) {
                    vector2.addElement(new Pair("Last Access", mDisplayDateFormat.format(date)));
                }
            } else {
                vector2.addElement(new Pair("Last Access", "none"));
            }
            date = (Date) map.get("JavaCreationTime");
            if (date != null) {
                synchronized (mDisplayDateFormat) {
                    vector2.addElement(new Pair("Creation time", mDisplayDateFormat.format(date)));
                }
            } else {
                vector2.addElement(new Pair("Creation time", "none"));
            }
            vector2.addElement(new Pair("Size (in bytes)", map.get("NormalMessageSize")));
        }

        addListContent(vector1, "Public Folder Attributes", document);
    }

    private void showEmptyFolders(Vector vector, Document document) {
        Vector vector1 = new Vector();
        for (int i = 0; i < vector.size(); i ++) {
            Map map = (Map) vector.elementAt(i);
            String s = (String) map.get("MessageCount");
            if (s.equals("0")) {
                Vector vector2 = new Vector();
                vector1.addElement(vector2);
                vector2.addElement(new Pair("Name", map.get("Name")));
            }
        }

        addListContent(vector1, "Empty Folders", document);
    }

    private void showNotAccessedInNDays(Vector vector, Document document) {
        Vector vector1 = new Vector();
        int i = getPropertyAsInteger(pNotAccessedN);
        if (i > 0) {
            GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTime(new Date());
            gregoriancalendar.add(6, -i);
            Date date = gregoriancalendar.getTime();
            for (int j = 0; j < vector.size(); j ++) {
                Map map = (Map) vector.elementAt(j);
                Date date1 = (Date) map.get("JavaLastAccessTime");
                if (date1 == null) {
                    date1 = (Date) map.get("JavaCreationTime");
                }
                if (date1 == null || date1.getTime() >= date.getTime()) {
                    continue;
                }
                Vector vector2 = new Vector();
                vector1.addElement(vector2);
                vector2.addElement(new Pair("Name", map.get("Name")));
                synchronized (mDisplayDateFormat) {
                    vector2.addElement(new Pair("Last Access/Creation", mDisplayDateFormat.format(date1)));
                }
            }

            addListContent(vector1, "Folders Not Accessed in " + i + " Days", document);
        }
    }

    public int getCostInLicensePoints() {
        return 5;
    }

    static {
        String s = (COM.dragonflow.StandardMonitor.Exchange2k3PublicFolderMonitor.class).getName();
        pNotAccessedN = new StringProperty("_notaccessedN");
        pNotAccessedN.setDisplayText("Days since access", "For \"Folders not accessed in N days\"");
        pNotAccessedN.setParameterOptions(true, 5, false);
        StringProperty astringproperty[] = { pNotAccessedN };
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("status != OK\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood", true));
        setClassProperty(s, "description", "Monitors Exchange 2003 Public Folders.");
        setClassProperty(s, "title", "Exchange 2003 Public Folder");
        setClassProperty(s, "class", "Exchange2k3PublicFolderMonitor");
        setClassProperty(s, "help", "Exch2k3PublicFolderMon.htm");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Exchange 2003 Public Folder");
        setClassProperty(s, "classType", "application");
        if (!Platform.isWindows() || !LUtils.isValidSSforXLicense(new Exchange2k3PublicFolderMonitor())) {
            setClassProperty(s, "loadable", "false");
        }
        setClassProperty(s, "addable", "false");
    }
}
