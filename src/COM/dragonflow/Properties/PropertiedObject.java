/*
 * 
 * Created on 2005-2-15 11:15:55
 *
 * PropertiedObject.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>PropertiedObject</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import jgl.HashMapIterator;
import jgl.Sorting;

import org.w3c.dom.Document;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Portal;
import COM.dragonflow.Utils.SerializerUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Properties:
// PropertyTable, HashMapOrdered, StringProperty, GreaterEqualOrder,
// FrameFile, XMLProperty, Visitor

public class PropertiedObject {

    protected static void addProperties(String s, StringProperty astringproperty[]) {
        Class class1;
        try {
            class1 = Class.forName(s);
        } catch (ClassNotFoundException classnotfoundexception) {
            System.out.println("Class " + s + " not found in AddProperties");
            return;
        }
        Class class2 = class1.getSuperclass();
        PropertyTable propertytable = (PropertyTable) cPropertyMap.get(class2.getName());
        PropertyTable propertytable1 = new PropertyTable(propertytable);
        for (int i = 0; i < astringproperty.length; i++) {
            propertytable1.add(astringproperty[i].name, astringproperty[i]);
        }

        cPropertyMap.put(s, propertytable1);
        cClassValues.put(s, new HashMap());
    }

    public static Object getPropertyMapByObject(String s, String s1) {
        HashMap hashmap = (HashMap) cPropertyMap.get(s);
        return hashmap.get(s1);
    }

    public static void addPropertyToPropertyMap(String s, StringProperty stringproperty) {
        PropertyTable propertytable = (PropertyTable) cPropertyMap.get(s);
        if (propertytable == null) {
            return;
        } else {
            propertytable.add(stringproperty.name, stringproperty);
            return;
        }
    }

    public static Object getClassPropertyByObject(String s, String s1) {
        HashMap hashmap = (HashMap) cClassValues.get(s);
        return hashmap.get(s1);
    }

    public static void setClassPropertyByObject(String s, String s1, Object obj) {
        HashMap hashmap = (HashMap) cClassValues.get(s);
        hashmap.put(s1, obj);
    }

    public static void setClassProperty(String s, String s1, String s2) {
        setClassPropertyByObject(s, s1, s2);
    }

    public PropertiedObject() {
        currentStatus = "";
        properties = null;
        valuesTable = new HashMapOrdered(true);
        templateTable = null;
        templateTableLastUpdate = 0L;
        templateID = null;
        properties = (PropertyTable) cPropertyMap.get(getClass().getName());
    }

    public void print() {
        printValuesTable();
    }

    public void printValuesTable() {
        if (valuesTable != null) {
            for (HashMapIterator hashmapiterator = valuesTable.begin(); !hashmapiterator.atEnd(); hashmapiterator
                    .advance()) {
                System.out.println(hashmapiterator.key() + "=" + hashmapiterator.value());
            }

        } else {
            System.out.println("No values");
        }
    }

    public HashMap getValuesTable() {
        return valuesTable;
    }

    public void setValuesTable(HashMap hashmap) {
        setValuesTable(hashmap, false);
    }

    public void preprocessValuesTable(HashMap hashmap) {
    }

    public void setValuesTable(HashMap hashmap, boolean flag) {
        if (!flag) {
            valuesTable = new HashMapOrdered(true);
        }
        preprocessValuesTable(hashmap);
        initializeValuesTable(hashmap, valuesTable, false);
    }

    public void resetTemplateCache() {
        templateCache = null;
        templateCacheLastUpdate = Platform.timeMillis();
    }

    public void initializeTemplate(String s) {
        templateID = s;
        if (templateCacheLastUpdate == 0L) {
            templateCacheLastUpdate = 1L;
        }
    }

    public boolean templateConfigurationExists() {
        return templateCache != null && templateCache.size() > 0;
    }

    public static Array getTemplateConfigFileList() {
        Array array = new Array();
        String s = Platform.getRoot() + "/groups/" + TEMPLATES_FILE;
        File file = new File(s);
        if (file.exists()) {
            array.add(s);
        }
        if (Platform.isPortal()) {
            Portal.addTemplateConfigFiles(array);
        }
        return array;
    }

    public HashMap getTemplateTable() {
        if (templateID != null && templateTableLastUpdate < templateCacheLastUpdate) {
            templateTable = null;
            if (templateID.length() > 0) {
                if (templateCache == null) {
                    templateCache = new HashMap();
                    String s = "";
                    Array array = getTemplateConfigFileList();
                    for (int i = 0; i < array.size(); i++)
                        try {
                            s = (String) array.at(i);
                            HashMap hashmap2 = new HashMap();
                            Array array1 = FrameFile.readFromFile(s);
                            templateCacheLastUpdate = Platform.timeMillis();
                            for (int j = 1; j < array1.size(); j++) {
                                HashMap hashmap3 = (HashMap) array1.at(j);
                                hashmap2.put(TextUtils.getValue(hashmap3, "__id"), hashmap3);
                            }

                            resolveTemplateReferences(hashmap2);
                            templateCache.add(s, hashmap2);
                        } catch (IOException ioexception) {
                            LogManager.log("Error", "Could not read template file " + s);
                        }

                }
                if (templateCache != null) {
                    String s1 = getTemplateConfigFilePath();
                    HashMap hashmap = null;
                    hashmap = (HashMap) templateCache.get(s1);
                    if (hashmap != null) {
                        HashMap hashmap1 = (HashMap) hashmap.get(templateID);
                        if (hashmap1 != null) {
                            templateTable = new HashMapOrdered(true);
                            initializeValuesTable(hashmap1, templateTable, true);
                        }
                    }
                }
            }
            templateTableLastUpdate = Platform.timeMillis();
        }
        return templateTable;
    }

    static void resolveTemplateReferences(HashMap hashmap) {
        if (hashmap != null) {
            HashMap hashmap1;
            for (Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); resolveTemplateReferences(
                                                                                                                    hashmap,
                                                                                                                    hashmap1,
                                                                                                                    0,
                                                                                                                    hashmap
                                                                                                                            .size()))
                hashmap1 = (HashMap) hashmap.get(enumeration.nextElement());

        }
    }

    protected String getTemplateConfigFilePath() {
        return Platform.getRoot() + "/groups/" + TEMPLATES_FILE;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param hashmap1
     * @param i
     * @param j
     */
    static void resolveTemplateReferences(HashMap hashmap, HashMap hashmap1, int i, int j) {
        if (i > j) {
            LogManager.log("Error", "Loop detected in templates.config");
            return;
        }
        for (Enumeration enumeration = hashmap1.values("__template"); enumeration.hasMoreElements();) {
            String s = (String) enumeration.nextElement();
            HashMap hashmap2 = (HashMap) hashmap.get(s);
            if (hashmap2 != null) {
                resolveTemplateReferences(hashmap, hashmap2, i + 1, j);
                Enumeration enumeration1 = hashmap2.keys();
                while (enumeration1.hasMoreElements()) {
                    String s1 = (String) enumeration1.nextElement();
                    if (hashmap1.get(s1) == null)
                        hashmap1.put(s1, hashmap2.get(s1));
                }
            }
        }

        hashmap1.remove("__template");
    }

    public boolean propertyInTemplate(StringProperty stringproperty) {
        if (getTemplateTable() != null) {
            return templateTable.get(stringproperty) != null;
        }
        else {
            return false;
        }
    }

    public boolean propertyInTemplate(String s) {
        if (getTemplateTable() != null) {
            StringProperty stringproperty = getPropertyObject(s);
            if (stringproperty != null) {
                return propertyInTemplate(stringproperty);
            }
            else {
                return templateTable.get(s) != null;
            }
        } else {
            return false;
        }
    }

    public static Vector getTemplateList(String s) {
        Vector vector = new Vector();
        vector.addElement("");
        vector.addElement("none");
        String s1 = Platform.getRoot() + "/groups/" + TEMPLATES_FILE;
        if (s.length() > 0) {
            s = Portal.cleanPortalServerID(s);
            s1 = Portal.getPortalSiteViewRootPath(s) + "/groups/" + TEMPLATES_FILE;
        }
        try {
            Array array = FrameFile.readFromFile(s1);
            for (int i = 1; i < array.size(); i++) {
                HashMap hashmap = (HashMap) array.at(i);
                vector.addElement(TextUtils.getValue(hashmap, "__id"));
                vector.addElement(TextUtils.getValue(hashmap, "__name"));
            }

        } catch (IOException e) {
            /* empty */
        }
        return vector;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hashmap
     * @param hashmap1
     * @param flag
     */
    public void initializeValuesTable(HashMap hashmap, HashMap hashmap1, boolean flag) {
        for (HashMapIterator hashmapiterator = hashmap.begin(); !hashmapiterator.atEnd(); hashmapiterator.advance()) {
            String s = (String) hashmapiterator.key();
            Object obj = hashmapiterator.value();
            StringProperty stringproperty = getPropertyObject(s);
            if (stringproperty != null) {
                if (!stringproperty.onPlatform(getPlatform())) {
                    continue;
                }
                if (flag) {
                    hashmap1.remove(stringproperty);
                }
                if (obj instanceof Array) {
                    for (Enumeration enumeration = ((Array) obj).elements(); enumeration.hasMoreElements(); hashmap1
                            .add(stringproperty, enumeration.nextElement()))
                        ;
                } else {
                    hashmap1.add(stringproperty, obj);
                }
                continue;
            }
            if (flag) {
                hashmap1.remove(s);
            }

            if (obj instanceof Array) {
                Enumeration enumeration1 = ((Array) obj).elements();
                while (enumeration1.hasMoreElements()) {
                    Object obj1 = enumeration1.nextElement();
                    if ((obj1 instanceof String)) {
                        hashmap1.add(s, obj1);
                    }
                }
            } else if (obj instanceof String) {
                hashmap1.add(s, obj);
            }
        }

    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringproperty
     * @return
     */
    private HashMap findTable(StringProperty stringproperty) {
        if (getTemplateTable() != null && templateTable.get(stringproperty) != null) {
            return templateTable;
        }
        if (stringproperty != null && valuesTable != null && valuesTable.get(stringproperty) != null) {
            return valuesTable;
        } else {
            return null;
        }
    }

    private HashMap findTable(String s) {
        if (getTemplateTable() != null && templateTable.get(s) != null) {
            return templateTable;
        }
        if (valuesTable != null && valuesTable.get(s) != null) {
            return valuesTable;
        } else {
            return null;
        }
    }

    public String getProperty(StringProperty stringproperty) throws NullPointerException {
        String s = null;
        HashMap hashmap = findTable(stringproperty);
        if (hashmap != null) {
            Object obj = hashmap.get(stringproperty);
            if (obj != null && (obj instanceof Array)) {
                s = "";
                Array array = (Array) obj;
                for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements();) {
                    if (s.length() != 0) {
                        s = s + stringproperty.multiLineDelimiter + " ";
                    }
                    s = s + enumeration.nextElement();
                }

            } else {
                s = (String) obj;
            }
        }
        if (s == null && stringproperty != null) {
            s = stringproperty.defaultValue;
        }
        return s;
    }

    public Array getProperties(StringProperty stringproperty) throws NullPointerException {
        jgl.Array array = new Array();
        jgl.HashMap hashmap = findTable(stringproperty);
        if (hashmap != null) {
            Object obj = hashmap.get(stringproperty);
            if (obj != null && (obj instanceof Array)) {
                array = (Array) obj;
            } else if (obj != null) {
                array.add(obj);
            }
        }
        return array;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     * @throws NullPointerException
     */
    public Array getProperties(String s) throws NullPointerException {
        StringProperty stringproperty = getPropertyObject(s);
        if (stringproperty != null) {
            return getProperties(stringproperty);
        }
        jgl.Array array = new Array();
        Object obj = null;
        jgl.HashMap hashmap = findTable(s);
        if (hashmap != null) {
            obj = hashmap.get(s);
        }

        if (obj == null) {
            return array;
        }

        if (obj instanceof String) {
            array.add(obj);
        } else if (obj instanceof Array) {
            array = (Array) obj;
        }
        return array;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     * @throws NullPointerException
     */
    public String getProperty(String s) throws NullPointerException {
        StringProperty stringproperty = getPropertyObject(s);
        if (stringproperty != null) {
            return getProperty(stringproperty);
        }

        Object obj = null;
        String s1 = null;
        jgl.HashMap hashmap = findTable(s);
        if (hashmap != null) {
            obj = hashmap.get(s);
        }

        if (obj == null) {
            s1 = "";
        } else if (obj instanceof String) {
            s1 = (String) obj;
        } else if (obj instanceof Array) {
            s1 = (String) ((Array) obj).at(0);
        } else {
            s1 = "";
        }
        return s1;
    }

    public Document getPropertyAsDocument(XMLProperty xmlproperty) {
        String s = getProperty(xmlproperty);
        return XMLProperty.parseString(s);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringproperty
     * @return
     */
    public long getPropertyAsLong(StringProperty stringproperty) {
        long l;
        try {
            String string = getProperty(stringproperty);
            l = Long.parseLong(string);
        } catch (NumberFormatException numberformatexception) {
            return 0L;
        }
        return l;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringproperty
     * @return
     */
    public int getPropertyAsInteger(StringProperty stringproperty) {
        int i;
        try {
            String string = getProperty(stringproperty);
            i = Integer.parseInt(string);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
        return i;
    }

    public boolean getPropertyAsBoolean(StringProperty stringproperty) {
        String s = getProperty(stringproperty);
        return s != null && s.length() != 0 && !s.equalsIgnoreCase("false");
    }

    public synchronized Enumeration getMultipleValues(StringProperty stringproperty) {
        jgl.HashMap hashmap = findTable(stringproperty);
        if (hashmap != null) {
            return hashmap.values(stringproperty);
        } else {
            return cEmptyArray.elements();
        }
    }

    public synchronized Enumeration getMultipleValues(String s) {
        if (valuesTable != null) {
            StringProperty stringproperty = getPropertyObject(s);
            if (stringproperty != null) {
                return getMultipleValues(stringproperty);
            }
            HashMap hashmap = findTable(s);
            if (hashmap != null) {
                return hashmap.values(s);
            }
        }
        return cEmptyArray.elements();
    }

    public boolean hasMultipleValues(StringProperty stringproperty) {
        HashMap hashmap = findTable(stringproperty);
        if (hashmap == null) {
            return false;
        } else {
            return hashmap.count(stringproperty) > 1;
        }
    }

    public boolean hasValue(StringProperty stringproperty) {
        HashMap hashmap = findTable(stringproperty);
        if (hashmap == null) {
            return false;
        } else {
            return hashmap.count(stringproperty) > 0;
        }
    }

    public boolean hasValue(String s) {
        StringProperty stringproperty = getPropertyObject(s);
        if (stringproperty != null) {
            return hasValue(stringproperty);
        }
        HashMap hashmap = findTable(s);
        if (hashmap == null) {
            return false;
        } else {
            return hashmap.count(s) > 0;
        }
    }

    public void setProperty(StringProperty stringproperty, String s) throws NullPointerException {
        valuesTable.put(stringproperty, s);
    }

    public void setProperty(StringProperty stringproperty, int i) {
        setProperty(stringproperty, String.valueOf(i));
    }

    public void setProperty(StringProperty stringproperty, long l) {
        setProperty(stringproperty, String.valueOf(l));
    }

    public void setProperty(StringProperty stringproperty, float f) {
        setProperty(stringproperty, String.valueOf(f));
    }

    public void setProperty(StringProperty stringproperty, Object obj) {
        if (obj != null)
            setProperty(stringproperty, obj.toString());
        else
            setProperty(stringproperty, "null");
    }

    public void setPropertyWithObject(StringProperty stringproperty, Serializable serializable) throws IOException {
        if (serializable != null) {
            String s = SerializerUtils.encodeObjectBase64(serializable, false);
            if (s != null)
                setProperty(stringproperty, s);
        } else {
            throw new IOException("Error encoding object: " + serializable);
        }
    }

    public void setProperty(XMLProperty xmlproperty, Document document) {
        String s = XMLProperty.generateXMLString(document);
        setProperty(((StringProperty) (xmlproperty)), s);
    }

    public Object getPropertyAsObject(StringProperty stringproperty) {
        String s = getProperty(stringproperty);
        if (s.length() > 0) {
            return SerializerUtils.decodeJavaObjectFromStringBase64(s);
        } else {
            return null;
        }
    }

    public void unsetProperty(StringProperty stringproperty) {
        if (valuesTable != null) {
            valuesTable.remove(stringproperty);
        }
    }

    public void unsetProperty(String s) {
        if (valuesTable != null) {
            StringProperty stringproperty = getPropertyObject(s);
            if (stringproperty != null)
                valuesTable.remove(stringproperty);
            else
                valuesTable.remove(s);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     */
    public void unsetPropertiesWithPrefix(String s) {
        LinkedList linkedlist = new LinkedList();
        if (valuesTable != null) {
            Enumeration enumeration = valuesTable.keys();
            while (enumeration.hasMoreElements()) {
                Object obj = enumeration.nextElement();
                if (obj instanceof StringProperty) {
                    obj = ((StringProperty) obj).getName();
                }
                String s2 = (String) obj;
                if (s2.startsWith(s)) {
                    linkedlist.add(s2);
                }
            }
        }
        for (int i = 0; i < linkedlist.size(); i++) {
            String s1 = (String) linkedlist.get(i);
            unsetProperty(s1);
        }

    }

    public void addProperty(StringProperty stringproperty, String s) throws NullPointerException {
        if (valuesTable == null) {
            setProperty(stringproperty, s);
        } else {
            valuesTable.add(stringproperty, s);
        }
    }

    public void incrementProperty(StringProperty stringproperty) {
        setProperty(stringproperty, TextUtils.increment(getProperty(stringproperty)));
    }

    public void incrementProperty(String s) {
        setProperty(s, TextUtils.increment(getProperty(s)));
    }

    public void setProperty(String s, String s1) throws NullPointerException {
        StringProperty stringproperty = getPropertyObject(s);
        if (stringproperty != null) {
            setProperty(stringproperty, s1);
        } else {
            valuesTable.put(s, s1);
        }
    }

    public void addProperty(String s, String s1) throws NullPointerException {
        StringProperty stringproperty = getPropertyObject(s);
        if (stringproperty != null) {
            addProperty(stringproperty, s1);
        } else {
            valuesTable.add(s, s1);
        }
    }

    public StringProperty getPropertyObject(String s) {
        StringProperty stringproperty = null;
        if (properties != null) {
            stringproperty = (StringProperty) properties.get(s, getPlatform());
        }
        return stringproperty;
    }

    public boolean hasProperty(String s) {
        return getPropertyObject(s) != null;
    }

    public int getPlatform() {
        return Platform.getLocalPlatform();
    }

    public Array getCurrentPropertyNames() {
        if (valuesTable != null) {
            Enumeration enumeration = valuesTable.keys();
            String s = "";
            Array array = new Array();
            while (enumeration.hasMoreElements()) {
                String s1 = enumeration.nextElement().toString();
                if (!s1.equals(s))
                    array.add(s1);
                s = s1;
            }
            return array;
        } else {
            return new Array();
        }
    }

    public Array getProperties() {
        return properties.getProperties(getPlatform());
    }

    public Array getImmediateProperties() {
        return properties.getImmediateProperties(getPlatform());
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param i
     * @return
     */
    public StringProperty getStatePropertyObject(int i) {
        Array array = getProperties();
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.isStateProperty && stringproperty.getOrder() == i) {
                return stringproperty;
            }
        }

        return null;
    }

    public Enumeration getStatePropertyObjects() {
        return getStatePropertyObjects(true);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param flag
     * @return
     */
    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = getProperties();
        Array array1 = new Array();
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.isStateProperty && stringproperty.getOrder() > 0
                    && (flag || stringproperty.isPrimaryStateProperty)) {
                array1.add(stringproperty);
            }
        }
        Sorting.sort(array1, new GreaterEqualOrder());
        return array1.elements();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    public Enumeration getParameterObjects() {
        Array array = getProperties();
        Array array1 = new Array();
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty) enumeration.nextElement();
            if (stringproperty.isParameter && stringproperty.getOrder() > 0) {
                array1.add(stringproperty);
            }
        }
        Sorting.sort(array1, new GreaterEqualOrder());
        return array1.elements();
    }

    public HashMap getClassProperties() {
        return getClassPropertyTable();
    }

    private HashMap getClassPropertyTable() {
        Class class1 = getClass();
        String s = class1.getName();
        return (HashMap) cClassValues.get(s);
    }

    public Object getClassProperty(String s) {
        jgl.HashMap hashmap = getClassPropertyTable();
        if (hashmap == null) {
            return null;
        }
        else {
            return hashmap.get(s);
        }
    }

    public String getClassPropertyString(String s) {
        Object obj = getClassProperty(s);
        if (obj != null) {
            return obj.toString();
        }
        else {
            return "";
        }
    }

    public String getPropertyName(StringProperty stringproperty) {
        return stringproperty.getName();
    }

    public int acceptVisitor(Visitor visitor) {
        return visitor.visit(this);
    }

    public Array getLogProperties() {
        return new Array();
    }

    public String currentStatus;

    static jgl.HashMap cPropertyMap = new jgl.HashMap();

    static jgl.HashMap cClassValues = new jgl.HashMap();

    static jgl.Array cEmptyArray = new jgl.Array();

    protected static final boolean EDITABLE = true;

    protected static final boolean NOT_EDITABLE = false;

    protected static final boolean CONFIGURABLE = true;

    protected static final boolean NOT_CONFIGURABLE = false;

    protected static final boolean ADVANCED = true;

    protected static final boolean BASIC = false;

    protected static final boolean VERIFIER = true;

    protected static final boolean NO_VERIFIER = false;

    PropertyTable properties;

    public jgl.HashMap valuesTable;

    public jgl.HashMap templateTable;

    private long templateTableLastUpdate;

    public static String TEMPLATES_FILE = "templates.config";

    private static long templateCacheLastUpdate = 0L;

    private static jgl.HashMap templateCache = null;

    public String templateID;

}