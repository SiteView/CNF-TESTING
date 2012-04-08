/*
 * 
 * Created on 2005-2-15 10:48:31
 *
 * SiteViewObject.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SiteViewObject</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Page.servicePage;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Properties.Visitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// MonitorGroup, Monitor, AtomicMonitor, Platform,
// SiteViewGroup, Machine

public abstract class SiteViewObject extends PropertiedObject {

    public SiteViewObject() {
        owner = null;
        elements = null;
        temporaryOwner = null;
        propErrorStatusMap = new HashMap();
        propGoodStatusMap = new HashMap();
        propWarningStatusMap = new HashMap();
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        return new Vector();
    }

    public static SiteViewObject createObject(HashMap hashmap)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return createObject(hashmap, false, cDefaultPackages);
    }

    public static SiteViewObject createTestObject(HashMap hashmap)
            throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return createObject(hashmap, true, cDefaultPackages);
    }

    public static SiteViewObject createObject(HashMap hashmap, boolean flag,
            String as[]) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        if (as != null) {
            String s = (String) hashmap.get("_class");
            Class class1 = null;
            int i = 0;
            do {
                if (class1 != null)
                    break;
                try {
                    String s1 = as[i] + s;
                    if (s1.equals("COM.dragonflow.StandardMonitor.DHCPMonitor"))
                        try {
                            Class.forName("edu.bucknell.net.JDHCP.DHCPSocket");
                        } catch (ClassNotFoundException classnotfoundexception1) {
                            throw classnotfoundexception1;
                        }
                    class1 = Class.forName(s1);
                    break;
                } catch (ClassNotFoundException classnotfoundexception) {
                    if (++i >= as.length)
                        throw classnotfoundexception;
                }
            } while (true);
            if (!loadableClass(class1) && !flag) {
                throw new ClassNotFoundException(
                        "Class "
                                + s
                                + " was marked \"not loadable\". "
                                + "This monitor type not supported on this platform and is ignored.");
            } else {
                SiteViewObject siteviewobject = (SiteViewObject) class1
                        .newInstance();
                siteviewobject.readFromHashMap(hashmap);
                siteviewobject.initialize(hashmap);
                return siteviewobject;
            }
        } else {
            return null;
        }
    }

    protected static void addClassElement(String s,
            SiteViewObject siteviewobject) {
        Array array = (Array) getClassPropertyByObject(s, "elements");
        if (array == null) {
            array = new Array();
            setClassPropertyByObject(s, "elements", array);
        }
        array.add(siteviewobject);
    }

    public static boolean loadableClass(Class class1) {
        Object obj = getClassPropertyByObject(class1.getName(), "loadable");
        if (obj != null) {
            return (obj instanceof String) && ((String) obj).equals("true");
        }
        else {
            return true;
        }
    }

    public static boolean addableClass(Class class1) {
        Object obj = getClassPropertyByObject(class1.getName(), "addable");
        if (obj != null) {
            return (obj instanceof String) && ((String) obj).equals("true");
        }
        else {
            return true;
        }
    }

    boolean doAutomaticSubstitutionResolve() {
        return temporaryOwner != null;
    }

    public void setTemporaryOwner(SiteViewObject siteviewobject) {
        temporaryOwner = siteviewobject;
    }

    public SiteViewObject resolveObjectReference(String s) {
        SiteViewObject siteviewobject = null;
        if (siteviewobject == null && s.equals("group"))
            siteviewobject = owner;
        if (siteviewobject == null && temporaryOwner != null)
            siteviewobject = temporaryOwner.resolveObjectReference(s);
        if (siteviewobject == null && owner != null)
            siteviewobject = owner.resolveObjectReference(s);
        return siteviewobject;
    }

    public String getValue(String s) {
        int i = s.indexOf(".");
        SiteViewObject siteviewobject = null;
        if (i >= 0) {
            String s1 = s.substring(0, i);
            s = s.substring(i + 1);
            siteviewobject = resolveObjectReference(s1);
        }
        if (siteviewobject == null)
            siteviewobject = this;
        return siteviewobject.getProperty(s);
    }

    public SiteViewObject getOwner() {
        return owner;
    }

    public SiteViewObject getSiteSeerOwner() {
        Object obj = owner;
        if (Platform.isSiteSeerServer()
                && owner == SiteViewGroup.currentSiteView()
                && (this instanceof MonitorGroup)) {
            String s = getProperty(MonitorGroup.pAccountName);
            if (s.length() > 0) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                obj = siteviewgroup.getGroup(s);
            }
        }
        return ((SiteViewObject) (obj));
    }

    public void setOwner(SiteViewObject siteviewobject) {
        owner = siteviewobject;
    }

    void readFromHashMap(HashMap hashmap) {
        setValuesTable(hashmap);
    }

    void mergeFromHashMap(HashMap hashmap) {
        setValuesTable(hashmap, true);
    }

    public void initialize(HashMap hashmap) {
    }

    Enumeration getElements() {
        if (elements == null)
            return cEmptyArray.elements();
        else
            return elements.elements();
    }

    public Array getRawElements() {
        return elements;
    }

    private void copyElementsOfClass(Array array, Array array1, String s) {
        try {
            Class class1 = Class.forName(s);
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                Object object = enumeration.nextElement();
                for (Class class2 = object.getClass(); class2 != null; class2 = class2
                        .getSuperclass()) {
                    if (class2 == class1) {
                        array1.add(object);
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException classnotfoundexception) {
            System.err.println("Could not copyElementsOfClass() : " + s);
        }
    }

    protected void getElementsOfClass(Array array, String s, boolean flag) {
        if (elements != null)
            copyElementsOfClass(elements, array, s);
        if (flag && owner != null)
            owner.getElementsOfClass(array, s, flag);
    }

    protected Array getElementsOfClass(String s, boolean flag) {
        return getElementsOfClass(s, flag, true);
    }

    public Array getElementsOfClass(String s, boolean flag, boolean flag1) {
        Array array = new Array();
        getElementsOfClass(array, s, flag);
        if (flag && flag1) {
            Array array1 = (Array) getClassProperty("elements");
            if (array1 != null)
                copyElementsOfClass(array1, array, s);
        }
        return array;
    }

    public SiteViewObject getElementByID(String s) {
        for (Enumeration enumeration = getElements(); enumeration
                .hasMoreElements();) {
            SiteViewObject siteviewobject = (SiteViewObject) enumeration
                    .nextElement();
            String s1 = I18N
                    .toDefaultEncoding(siteviewobject.getProperty(pID));
            if (s.equals(s1) && (siteviewobject instanceof Monitor))
                return siteviewobject;
        }

        return null;
    }

    public SiteViewObject getElement(String s) {
        I18N.test(s, 0);
        StringTokenizer stringtokenizer = new StringTokenizer(s, ID_SEPARATOR);
        String as[] = new String[stringtokenizer.countTokens()];
        int i = 0;
        while (stringtokenizer.hasMoreTokens())
            as[i++] = stringtokenizer.nextToken();
        return getElement(as, 0);
    }

    SiteViewObject getElement(String as[], int i) {
        SiteViewObject siteviewobject = getElementByID(as[i]);
        if (i == as.length - 1)
            return siteviewobject;
        if (siteviewobject != null)
            return siteviewobject.getElement(as, ++i);
        else
            return null;
    }

    public String getGroupPathID() {
        String s = "/";
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Object obj = this;
        do {
            if (obj == null)
                break;
            String s1 = I18N.toDefaultEncoding(((SiteViewObject) (obj))
                    .getProperty(pID));
            s = "/" + s1 + s;
            String s2 = I18N.toDefaultEncoding(((SiteViewObject) (obj))
                    .getProperty("_parent"));
            if (s2.length() == 0)
                break;
            obj = siteviewgroup.getGroup(s2);
        } while (true);
        return s;
    }

    public String getFullID() {
        if (owner != null) {
            String s = owner.getFullID();
            if (!s.endsWith(":"))
                s = s + ID_SEPARATOR;
            return s + getProperty(pID);
        } else {
            return getProperty(pID);
        }
    }

    String getNextID() {
        int i = 0;
        if (hasValue(pNextID))
            i = getPropertyAsInteger(pNextID);
        String s = String.valueOf(++i);
        setProperty(pNextID, s);
        return s;
    }

    public void removeAllElements() {
        elements = null;
    }

    public void addElement(SiteViewObject siteviewobject) {
        if (elements == null)
            elements = new Array();
        elements.add(siteviewobject);
        siteviewobject.setOwner(this);
    }

    public void removeElement(SiteViewObject siteviewobject) {
        if (elements != null)
            elements.remove(siteviewobject);
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public int acceptVisitor(Visitor visitor) {
        int i = super.acceptVisitor(visitor);
        if (elements != null && i == 0) {
            Enumeration enumeration = elements.elements();
            /*while (enumeration.hasMoreElements()) {
                SiteViewObject siteviewobject = (SiteViewObject) enumeration
                        .nextElement();
                i = siteviewobject.acceptVisitor(visitor);
                if (i == 1) {
                    break;
                }
            } */
			do
            {
                if(!enumeration.hasMoreElements())
                    break;
                SiteViewObject siteviewobject = (SiteViewObject)enumeration.nextElement();
                i = siteviewobject.acceptVisitor(visitor);
            } while(i != 1);
        }
        if (i == 2) {
            i = 0;
        }
        return i;
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pFullID)
            return getFullID();
        if (stringproperty == pOwnerID)
            if (owner != null)
                return owner.getProperty(pID);
            else
                return "";
        if (stringproperty == pGroupID)
            if (owner != null)
                return owner.getProperty(pGroupID);
            else
                return "";
        String s = super.getProperty(stringproperty);
        if (doAutomaticSubstitutionResolve()
                && TextUtils.isSubstituteExpression(s))
            s = TextUtils.substitute(s, this);
        return s;
    }

    public Enumeration getMultipleValues(StringProperty stringproperty) {
        Enumeration enumeration = super.getMultipleValues(stringproperty);
        if (doAutomaticSubstitutionResolve()) {
            Array array = new Array();
            String s;
            for (; enumeration.hasMoreElements(); array.add(s)) {
                s = (String) enumeration.nextElement();
                if (TextUtils.isSubstituteExpression(s))
                    s = TextUtils.substitute(s, this);
            }

            enumeration = array.elements();
        }
        return enumeration;
    }

    public String getSetting(String s) {
        if (Platform.isSiteSeerServer())
            return getSetting(s, true);
        else
            return getSetting(s, false);
    }

    public String getSetting(String s, boolean flag) {
        SiteViewObject siteviewobject = flag ? getSiteSeerOwner()
                : getOwner();
        String s1 = getProperty(s);
        if ((s1 == null || s1.length() == 0) && siteviewobject != null
                && siteviewobject != this)
            s1 = siteviewobject.getSetting(s);
        return s1;
    }

    public String getTreeSetting(String s) {
        String s1 = getProperty(s);
        if ((s1 == null || s1.length() == 0) && owner != null) {
            SiteViewObject siteviewobject = null;
            String s2 = I18N.toDefaultEncoding(getProperty("_parent"));
            if (s2.length() > 0)
                siteviewobject = owner.getElement(s2);
            if (siteviewobject == null) {
                siteviewobject = owner;
                setProperty("_parent", "");
            }
            return siteviewobject.getTreeSetting(s);
        } else {
            return s1;
        }
    }

    public String getSetting(StringProperty stringproperty) {
        return getSetting(stringproperty.getName());
    }

    public long getSettingAsLong(String s) {
        long l;
        try {
            String string_25_ = getSetting(s);
            l = Long.parseLong(string_25_);
        } catch (NumberFormatException numberformatexception) {
            return 0L;
        }
        return l;
    }

    public int getSettingAsLong(String s, int i) {
        int j = i;
        String s1 = getSetting(s);
        if (!s1.equals(""))
            try {
                j = Integer.valueOf(s1).intValue();
            } catch (NumberFormatException numberformatexception) {
            }
        return j;
    }

    public long getSettingAsLong(StringProperty stringproperty) {
        return getSettingAsLong(stringproperty.getName());
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        return s;
    }

    public boolean isPropertyExcluded(StringProperty stringproperty,
            HTTPRequest httprequest) {
        return false;
    }

    void appendProperty(StringBuffer stringbuffer,
            StringProperty stringproperty, boolean flag) {
        String s;
        if (stringproperty.isPassword)
            s = TextUtils.filledString('*', stringproperty.valueString(
                    getProperty(stringproperty)).length());
        else
            s = stringproperty.valueString(getProperty(stringproperty));
        if (s.length() > 0 && !s.equals("n/a")) {
            if (stringbuffer.length() > 0)
                stringbuffer.append(Platform.FILE_NEWLINE);
            stringbuffer
                    .append(stringproperty.getLabel() + ": " + enc(s, flag));
        }
    }

    private String encProp(StringProperty stringproperty, boolean flag) {
        return enc(getProperty(stringproperty), flag);
    }

    private String enc(String s, boolean flag) {
        return flag ? I18N.toDefaultEncoding(s) : s;
    }

    public String createFromTemplate(String s) {
        return createFromTemplate(s, true);
    }

    public String createFromTemplate(String s, boolean flag) {
        //TODO need review
        I18N.test(s, 0);
        int i = 0;
        StringBuffer stringbuffer = new StringBuffer();
        char c = '<';
        char c1 = '>';
        int j = s.indexOf(TAG_STYLE_TAG);
        if (j >= 0) {
            c = s.charAt(j + TAG_STYLE_TAG.length());
            c1 = s.charAt(j + TAG_STYLE_TAG.length() + 1);
            String s1 = TAG_STYLE_TAG + c + c1 + "]";
            s = TextUtils.replaceString(s, s1, "");
        }
        label0: do {
            int k = s.indexOf(c, i);
            if (k == -1)
                break;
            int l = s.indexOf(c1, k + 1);
            if (l == -1)
                break;
            stringbuffer.append(s.substring(i, k));
            i = l + 1;
            String s2 = s.substring(k + 1, l);
            int i1 = 0;
            int j1 = s2.lastIndexOf(":");
            if (j1 >= 0) {
                String s3 = s2.substring(j1 + 1);
                if (TextUtils.onlyChars(s3, "0123456789")) {
                    i1 = TextUtils.toInt(s3);
                    s2 = s2.substring(0, j1);
                }
            }
            if (s2.equalsIgnoreCase("name")) {
                addToBuffer(stringbuffer, encProp(Monitor.pName, flag), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("state")) {
                addToBuffer(stringbuffer, encProp(Monitor.pStateString, flag),
                        i1);
                continue;
            }
            if (s2.equalsIgnoreCase("processtext")) {
                String s4 = getProperty("_machine");
                if (s4.length() > 0)
                    servicePage.printProcessStats(5, null, stringbuffer, s4,
                            true);
                continue;
            }
            if (s2.equalsIgnoreCase("groupdescription")
                    || s2.equalsIgnoreCase("firstgroupdescription")
                    || s2.equalsIgnoreCase("fullgroupdescription")) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                Object obj = null;
                Monitor monitor1 = null;
                if (siteviewgroup == null)
                    continue;
                monitor1 = (Monitor) siteviewgroup.getElement(encProp(
                        pGroupID, flag));
                do {
                    if (monitor1 == null)
                        continue label0;
                    Enumeration enumeration5 = monitor1
                            .getMultipleValues(Monitor.pDescription);
                    if (enumeration5.hasMoreElements()) {
                        for (; enumeration5.hasMoreElements(); addToBuffer(
                                stringbuffer, enc(enumeration5.nextElement()
                                        + "\n", flag), i1))
                            ;
                        if (s2.equalsIgnoreCase("firstgroupdescription"))
                            continue label0;
                    }
                    if (s2.equalsIgnoreCase("groupdescription"))
                        continue label0;
                    String s8 = enc(monitor1.getProperty("_parent"), flag);
                    if (s8 == null || s8.length() == 0)
                        continue label0;
                    monitor1 = (Monitor) siteviewgroup.getElement(s8);
                } while (true);
            }
            if (s2.equalsIgnoreCase("fullgroupid")) {
                addToBuffer(stringbuffer, enc(CGI.getGroupFullName(encProp(
                        pOwnerID, flag)), flag), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("group")) {
                SiteViewGroup siteviewgroup1 = SiteViewGroup
                        .currentSiteView();
                Monitor monitor = null;
                if (siteviewgroup1 != null)
                    monitor = (Monitor) siteviewgroup1.getElement(encProp(
                            pGroupID, flag));
                if (monitor != null)
                    addToBuffer(stringbuffer, enc(monitor
                            .getProperty(Monitor.pName), flag), i1);
                else
                    addToBuffer(stringbuffer, encProp(pOwnerID, flag), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("siteviewurl")) {
                String s5 = getSetting("_adminURL");
                String s9 = getSetting("_account");
                if (s9.length() != 0)
                    s5 = s5 + "/?account=" + s9;
                addToBuffer(stringbuffer, s5, i1);
                continue;
            }
            if (s2.equalsIgnoreCase("siteviewuserurl")) {
                String s6 = getSetting("_adminURL");
                String s10 = getSetting("_account");
                if (s10.length() == 0)
                    s6 = s6 + "/userhtml/SiteView.html";
                else
                    s6 = s6 + "/accounts/" + s10 + "/userhtml/SiteView.html";
                addToBuffer(stringbuffer, s6, i1);
                continue;
            }
            if (s2.equalsIgnoreCase("alertHelpURL")) {
                SiteViewGroup siteviewgroup2 = SiteViewGroup
                        .currentSiteView();
                String s11 = (String) getClassProperty("class");
                String s14 = "/docs/AlertHelp" + s11 + ".htm";
                if (!(new File(Platform.getRoot() + s14)).exists())
                    s14 = "/docs/AlertHelp.htm";
                addToBuffer(stringbuffer, siteviewgroup2.mainURL() + s14, i1);
                continue;
            }
            if (s2.equalsIgnoreCase("currentTime")) {
                addToBuffer(stringbuffer, TextUtils.prettyDate(Platform
                        .timeMillis()
                        + getSettingAsLong("_timeOffset") * 1000L), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("time")) {
                addToBuffer(stringbuffer, TextUtils.prettyDate(TextUtils
                        .toLong(getProperty(AtomicMonitor.pMonitorDoneTime))
                        + getSettingAsLong("_timeOffset") * 1000L), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("time-time")) {
                addToBuffer(
                        stringbuffer,
                        TextUtils
                                .prettyDateTime(new Date(
                                        TextUtils
                                                .toLong(getProperty(AtomicMonitor.pMonitorDoneTime))
                                                + getSettingAsLong("_timeOffset")
                                                * 1000L)), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("time-date")) {
                addToBuffer(
                        stringbuffer,
                        TextUtils
                                .prettyDateDate(new Date(
                                        TextUtils
                                                .toLong(getProperty(AtomicMonitor.pMonitorDoneTime))
                                                + getSettingAsLong("_timeOffset")
                                                * 1000L)), i1);
                continue;
            }
            if (s2.equalsIgnoreCase("mainParameters")) {
                Enumeration enumeration = getParameterObjects();
                while (enumeration.hasMoreElements()) {
                    StringProperty stringproperty = (StringProperty) enumeration
                            .nextElement();
                    appendProperty(stringbuffer, stringproperty, flag);
                }
                continue;
            }
            if (s2.equalsIgnoreCase("mainStateProperties")) {
                int k1 = 0;
                while (true) {
                    StringProperty stringproperty1 = getStatePropertyObject(k1);
                    if (stringproperty1 == null)
                        continue label0;
                    appendProperty(stringbuffer, stringproperty1, flag);
                    k1++;
                } 
            }
            if (s2.equalsIgnoreCase("secondaryStateProperties")) {
                Enumeration enumeration1 = getProperties().elements();
                do {
                    StringProperty stringproperty2;
                    do {
                        if (!enumeration1.hasMoreElements())
                            continue label0;
                        stringproperty2 = (StringProperty) enumeration1
                                .nextElement();
                    } while (!stringproperty2.isStateProperty
                            || stringproperty2.getOrder() != 0);
                    appendProperty(stringbuffer, stringproperty2, flag);
                } while (true);
            }
            if (s2.equalsIgnoreCase("errorOnly")) {
                Enumeration enumeration2 = getProperties().elements();
                do {
                    StringProperty stringproperty3;
                    String s16;
                    do {
                        do {
                            Boolean boolean1;
                            do {
                                do {
                                    if (!enumeration2.hasMoreElements())
                                        continue label0;
                                    stringproperty3 = (StringProperty) enumeration2
                                            .nextElement();
                                } while (!stringproperty3.isStateProperty);
                                String s15 = stringproperty3.getName();
                                boolean1 = (Boolean) propErrorStatusMap
                                        .get(s15);
                            } while (boolean1 == null
                                    || !boolean1.booleanValue());
                            s16 = stringproperty3
                                    .valueString(getProperty(stringproperty3));
                        } while (s16.length() <= 0);
                        if (stringbuffer.length() > 0)
                            stringbuffer.append(Platform.FILE_NEWLINE);
                    } while (!(this instanceof AtomicMonitor));
                    AtomicMonitor atomicmonitor = (AtomicMonitor) this;
                    String s18 = atomicmonitor.GetPropertyLabel(
                            stringproperty3, false);
                    if (s18 == null)
                        s18 = stringproperty3.getLabel();
                    stringbuffer.append(s18 + ": " + s16);
                } while (true);
            }
            if (s2.equalsIgnoreCase("secondaryParameters")) {
                Enumeration enumeration3 = getProperties().elements();
                do {
                    StringProperty stringproperty4;
                    do {
                        if (!enumeration3.hasMoreElements())
                            continue label0;
                        stringproperty4 = (StringProperty) enumeration3
                                .nextElement();
                    } while (!stringproperty4.isParameter
                            || stringproperty4.getOrder() != 0);
                    appendProperty(stringbuffer, stringproperty4, flag);
                } while (true);
            }
            if (s2.equalsIgnoreCase("all")) {
                Array array = getProperties();
                Enumeration enumeration4 = array.elements();
                while (enumeration4.hasMoreElements()) {
                    StringProperty stringproperty5 = (StringProperty) enumeration4
                            .nextElement();
                    appendProperty(stringbuffer, stringproperty5, flag);
                }
                continue;
            }
            if (s2.equalsIgnoreCase("remoteMachineName")) {
                String s7 = getProperty("_machine");
                if (s7.length() > 0) {
                    String s12 = Machine.getMachineName(s7);
                    if (s12.startsWith("\\\\"))
                        s12 = s12.substring(2, s12.length());
                    addToBuffer(stringbuffer, s12, i1);
                }
                continue;
            }
            if (TextUtils.startsWithIgnoreCase(s2, "group.")) {
                SiteViewGroup siteviewgroup3 = SiteViewGroup
                        .currentSiteView();
                Object obj1 = null;
                if (siteviewgroup3 == null)
                    continue;
                String as[] = TextUtils.split(s2, ".");
                Monitor monitor2 = (Monitor) siteviewgroup3
                        .getElement(encProp(pGroupID, flag));
                int l1 = 1;
                do {
                    if (l1 >= as.length || monitor2 == null)
                        continue label0;
                    String s17 = as[l1];
                    if (s17.equalsIgnoreCase("parent")) {
                        String s13 = enc(monitor2.getProperty("_parent"), flag);
                        if (s13 == null || s13.length() == 0) {
                            addToBuffer(stringbuffer, "***no parent***", i1);
                            continue label0;
                        }
                        monitor2 = (Monitor) siteviewgroup3.getElement(s13);
                    } else {
                        addToBuffer(stringbuffer, enc(
                                monitor2.getProperty(s17), flag), i1);
                        continue label0;
                    }
                    l1++;
                } while (true);
            }
            if (hasProperty(s2))
                addToBuffer(stringbuffer, enc(getProperty(s2), flag), i1);
            else if (getSetting(s2).length() > 0)
                addToBuffer(stringbuffer, enc(getSetting(s2), flag), i1);
            else if (TextUtils.isSubstituteExpression(s2))
                addToBuffer(stringbuffer, enc(TextUtils.substitute(s2), flag),
                        i1);
            else if (getSetting("_ignoreMissingTemplateProperties").length() <= 0)
                addToBuffer(stringbuffer,
                        "*** property not found (" + s2 + ")", i1);
        } while (true);
        stringbuffer.append(s.substring(i));
        return stringbuffer.toString();
    }

    void addToBuffer(StringBuffer stringbuffer, String s, int i) {
        if (i > 0 && s.length() > i)
            s = s.substring(0, i);
        stringbuffer.append(s);
    }

    protected String createFromTemplateFile(String s) throws IOException {
        String s1 = "";
        if (getSetting("_ignoreTemplateCache").length() <= 0) {
            if (templateCache == null)
                templateCache = new HashMap();
            s1 = (String) templateCache.get(s);
            if (s1 == null) {
                s1 = FileUtils.readCharFile(s).toString();
                templateCache.put(s, s1);
            }
        } else {
            s1 = FileUtils.readCharFile(s).toString();
        }
        return createFromTemplate(s1);
    }

    public String getScheduleSettings(String s) {
        return getAdditionalSettings("Schedule", s);
    }

    public String getPagerSettings(String s) {
        return getAdditionalSettings("Pager", s);
    }

    public String getMailSettings(String s) {
        return getAdditionalSettings("Mail", s);
    }

    public String getSNMPSettings(String s) {
        return getAdditionalSettings("SNMP", s);
    }

    public Enumeration getMultipleSettings(String s) {
        Enumeration enumeration = getMultipleValues(s);
        if (enumeration.hasMoreElements())
            return enumeration;
        SiteViewObject siteviewobject = getSiteSeerOwner();
        if (siteviewobject != null)
            return siteviewobject.getMultipleSettings(s);
        else
            return (new Vector()).elements();
    }

    public String getAdditionalSettings(String s, String s1) {
        String s2 = "_id=" + s1;
        Enumeration enumeration = getMultipleValues("_additional" + s);
        if (enumeration.hasMoreElements())
            while (enumeration.hasMoreElements()) {
                String s3 = (String) enumeration.nextElement();
                int i = s3.indexOf(s2);
                if (i >= 0) {
                    int j = s3.indexOf(" ", i);
                    String s4 = s3.substring(i + 4, j);
                    if (s1.compareTo(s4) == 0)
                        return s3;
                }
            }
        SiteViewObject siteviewobject = getSiteSeerOwner();
        if (siteviewobject != null && tryOwnerForSettings())
            return siteviewobject.getAdditionalSettings(s, s1);
        else
            return "";
    }

    public boolean tryOwnerForSettings() {
        return true;
    }

    protected String getTemplateConfigFilePath() {
        for (SiteViewObject siteviewobject = this; siteviewobject != null; siteviewobject = siteviewobject
                .getOwner())
            ;
        return super.getTemplateConfigFilePath();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    public String matchExpects() {
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = false;
        Enumeration enumeration = valuesTable.keys();
        while (!enumeration.hasMoreElements()) {
            String s = enumeration.nextElement().toString();
            if (!s.startsWith("_expect-"))
                continue;
            flag = true;
            String s1 = s;
            s = s.substring("_expect-".length());
            String s2 = null;
            try {
                s2 = getProperty(s1);
            } catch (Exception exception) {
                stringbuffer.append("No such property: " + s1 + "\n");
                TextUtils.debugPrint("***ERROR!\"" + exception + "\" \"" + s1
                        + "\" not found!");
                continue;
            }
            String s3 = matchValue(s2, getProperty(s));
            if (s3.length() > 0)
                stringbuffer.append(s + ": " + s3 + "\n");
        } 
        
        if (!flag) {
            stringbuffer.append(" no expect conditions in group.\n");
        }
        return stringbuffer.toString();
    }

    String matchValue(String s, String s1) {
        float f = (0.0F / 0.0F);
        float f1 = (0.0F / 0.0F);
        String s2 = "";
        if (s.startsWith("[")) {
            int i = s.indexOf("]");
            if (i >= 1) {
                String s3 = s.substring(1, i);
                s = s.substring(i + 1);
                String as[] = TextUtils.split(s3, "-");
                if (as.length == 2) {
                    f1 = TextUtils.toFloat(as[0]);
                    f = TextUtils.toFloat(as[1]);
                }
            }
        }
        Array array = new Array();
        if (TextUtils.matchExpression(s1, s, array, new StringBuffer()) == Monitor.kURLok) {
            if (!Float.isNaN(f1) && !Float.isNaN(f)) {
                float f2;
                if (array.size() >= 1)
                    f2 = TextUtils.toFloat((String) array.at(0));
                else
                    f2 = TextUtils.toFloat(s1);
                if (f2 < f1 || f2 > f)
                    s2 = "expected in range [" + f1 + "-" + f + "] got " + s1;
            }
        } else {
            s2 = "expected \"" + s + "\" got \"" + s1 + "\"";
        }
        return s2;
    }

    public static StringProperty pID;

    public static StringProperty pFullID;

    public static StringProperty pOwnerID;

    static StringProperty pNextID;

    static StringProperty pVersion;

    public static StringProperty pGroupID;

    public static String ID_SEPARATOR = "/";

    static Array cEmptyArray = new Array();

    static String cDefaultPackages[] = { "COM.dragonflow.StandardMonitor.",
            "COM.dragonflow.StandardAction.", "COM.dragonflow.SiteView.",
            "CustomMonitor.", "CustomAction." };

    SiteViewObject owner;

    Array elements;

    SiteViewObject temporaryOwner;

    static String TAG_STYLE_TAG = "[Tag-Style:";

    public HashMap propErrorStatusMap;

    public HashMap propGoodStatusMap;

    public HashMap propWarningStatusMap;

    private static HashMap templateCache = null;

    public static final String EXPECT_PREFIX = "_expect-";

    static {
        pID = new StringProperty("_id", "1");
        pFullID = new StringProperty("fullID");
        pOwnerID = new StringProperty("ownerID");
        pNextID = new StringProperty("_nextID", "0");
        pGroupID = new StringProperty("groupID", "");
        pVersion = new StringProperty("_version", Platform.getVersion(),
                "version");
        StringProperty astringproperty[] = { pID, pFullID, pOwnerID, pNextID,
                pVersion, pGroupID };
        addProperties("COM.dragonflow.SiteView.SiteViewObject",
                astringproperty);
    }
}