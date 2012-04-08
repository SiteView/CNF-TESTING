/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class treeControl extends COM.dragonflow.Page.CGI {

    public boolean parentSelected;

    public boolean treeCommand;

    public String ctrlName;

    public static final String USETREE = "_useTreeCtrl";

    private COM.dragonflow.HTTP.HTTPRequest request;

    private boolean single;

    private boolean masterSelected;

    private boolean noneSelected;

    private boolean hiddenSelection;

    private boolean rememberSelects;

    private boolean forgetState;

    private boolean isOpen;

    private COM.dragonflow.Page.CGI cgi;

    private COM.dragonflow.SiteView.SiteViewGroup siteview;

    private static final String NONE = "_none";

    private static final String STATE = "state";

    private static final String SELECTED = "selected";

    private static final String TREECLOSE = "treeclose";

    private static final String TREEOPEN = "treeopen";

    private static final String TREECLEAR = "treeclear";

    private static final String TREESELECT = "treeSeleected";

    private static final int UNSELECTED = 0;

    private static final int ISSELECTED = 1;

    private static final int SELECTGREY = 2;

    private jgl.HashMap state;

    private jgl.HashMap selected;

    private jgl.Array selectList;

    public treeControl(COM.dragonflow.HTTP.HTTPRequest httprequest,
            String s, boolean flag, jgl.Array array, boolean flag1) {
        parentSelected = false;
        treeCommand = false;
        ctrlName = null;
        request = null;
        single = false;
        masterSelected = false;
        noneSelected = false;
        hiddenSelection = false;
        rememberSelects = false;
        forgetState = false;
        isOpen = false;
        cgi = null;
        siteview = null;
        state = new HashMap();
        selected = new HashMap();
        selectList = null;
        selectList = new Array();
        forgetState = flag1;
        for (int i = 0; array != null && i < array.size(); i++) {
            String s1 = (String) array.at(i);
            String as[] = COM.dragonflow.Utils.TextUtils
                    .split(s1, " ");
            selectList.add(as[0]);
        }

        rememberSelects = true;
        single = flag;
        setTree(httprequest, s);
    }

    public treeControl(COM.dragonflow.HTTP.HTTPRequest httprequest,
            String s, boolean flag) {
        parentSelected = false;
        treeCommand = false;
        ctrlName = null;
        request = null;
        single = false;
        masterSelected = false;
        noneSelected = false;
        hiddenSelection = false;
        rememberSelects = false;
        forgetState = false;
        isOpen = false;
        cgi = null;
        siteview = null;
        state = new HashMap();
        selected = new HashMap();
        selectList = null;
        single = flag;
        rememberSelects = true;
        setTree(httprequest, s);
    }

    public treeControl(COM.dragonflow.HTTP.HTTPRequest httprequest,
            String s, boolean flag, boolean flag1) {
        parentSelected = false;
        treeCommand = false;
        ctrlName = null;
        request = null;
        single = false;
        masterSelected = false;
        noneSelected = false;
        hiddenSelection = false;
        rememberSelects = false;
        forgetState = false;
        isOpen = false;
        cgi = null;
        siteview = null;
        state = new HashMap();
        selected = new HashMap();
        selectList = null;
        single = flag;
        rememberSelects = flag1;
        setTree(httprequest, s);
    }

    public void setTree(COM.dragonflow.HTTP.HTTPRequest httprequest,
            String s) {
        parentSelected = false;
        request = httprequest;
        if (s != null) {
            ctrlName = new String(s);
        } else {
            ctrlName = new String("targets");
        }
        siteview = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        getTreeState();
    }

    protected void finalize() throws java.lang.Throwable {
        saveTreeState();
    }

    public static boolean useTree() {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        return siteviewgroup.getProperty("_useTreeCtrl").length() > 0;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param httprequest
     * @return
     */
    public static boolean notHandled(COM.dragonflow.HTTP.HTTPRequest httprequest) {
            if (COM.dragonflow.Page.treeControl.useTree()) {
            Enumeration enumeration = httprequest.variables.keys();
            String s;
            while (enumeration.hasMoreElements()) {
                s = (String) enumeration.nextElement();
            if (s.indexOf("treeopen") >= 0 || s.indexOf("treeclose") >= 0
                    || s.indexOf("treeSeleected") >= 0
                    || s.indexOf("treeclear") >= 0) {
            return false;
            }
            }
            }
        return true;
    }

    public boolean process(String s, String s1,
            String s2, jgl.Array array, jgl.Array array1,
            jgl.Array array2, int i, COM.dragonflow.Page.CGI cgi1,
            StringBuffer stringbuffer) throws java.lang.Exception {
        StringBuffer stringbuffer1 = new StringBuffer();
        cgi = cgi1;
        jgl.HashMap hashmap = new HashMap();
        for (int j = 0; array != null && j < array.size(); j++) {
            hashmap.put(array.at(j), "true");
        }

        jgl.HashMap hashmap1 = array1 != null ? new HashMap() : null;
        for (int k = 0; array1 != null && k < array1.size(); k++) {
            hashmap1.put(array1.at(k), "true");
        }

        jgl.HashMap hashmap2 = array2 != null ? new HashMap() : null;
        for (int l = 0; array2 != null && l < array2.size(); l++) {
            hashmap2.put(array2.at(l), "true");
        }

        hiddenSelection = true;
        if (single) {
            Enumeration enumeration = hashmap.keys();
            if (enumeration.hasMoreElements()) {
                String s3 = (String) enumeration
                        .nextElement();
                if (s3.equals("_none")) {
                    hiddenSelection = false;
                } else {
                    String as[] = COM.dragonflow.Utils.TextUtils
                            .split(s3, " ");
                    s3 = as[0];
                    COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor) siteview
                            .getElement(s3);
                    if (state.get("treeopen" + s3) != null
                            || request.getValue("treeopen" + s3 + ".x")
                                    .length() > 0) {
                        hiddenSelection = false;
                    } else {
                        while (monitor != null
                                && monitor.getProperty("_parent").length() > 0) {
                            String s5 = monitor
                                    .getProperty("_parent");
                            COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor) siteview
                                    .getElement(s5);
                            hiddenSelection = true;
                            Enumeration enumeration4 = monitor1
                                    .getMonitors();
                            while (enumeration4.hasMoreElements()) {
                                COM.dragonflow.SiteView.Monitor monitor2 = (COM.dragonflow.SiteView.Monitor) enumeration4
                                        .nextElement();
                                if (!(monitor2 instanceof COM.dragonflow.SiteView.SubGroup)) {
                                    continue;
                                }
                                String s8 = monitor2
                                        .getProperty(COM.dragonflow.SiteView.SubGroup.pGroup);
                                if (!s8.equals(s3)) {
                                    continue;
                                }
                                hiddenSelection = false;
                                break;
                            } 
                            
                            monitor = monitor1;
                            s3 = s5;
                        }
                        if (!hiddenSelection
                                && state.get("treeopen" + s3) != null
                                || request.getValue("treeopen" + s3 + ".x")
                                        .length() > 0) {
                            hiddenSelection = false;
                        } else {
                            hiddenSelection = true;
                        }
                    }
                }
            }
        }
        StringBuffer stringbuffer2 = new StringBuffer();
        if ((i & 4) != 0) {
            if (hashmap.get("_none") != null) {
                hashmap.put("_none", "used");
            }
            createLine(null, noneSelected(hashmap, i) ? 1 : 0, "_none", "None",
                    stringbuffer1, 0, true);
        }
        if ((i & 0x10) != 0) {
            createLine(null, 0, "", "-- SiteView Panel --", stringbuffer1, 0,
                    true);
        }
        if ((i & 0x40) != 0) {
            createLine(null, masterSelected(hashmap, i) ? 1 : 0, "_master",
                    "Select All Groups", stringbuffer1, 0, true);
        }
        if (selectList == null) {
            selectList = new Array();
        }
        for (Enumeration enumeration1 = hashmap.keys(); enumeration1
                .hasMoreElements();) {
            String s4 = (String) enumeration1.nextElement();
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s4,
                    " ");
            if (as1.length > 1) {
                if ((i & 0x20) != 0) {
                    selectList.add(as1[1] + "/" + as1[0]);
                } else {
                    selectList.add(as1[0] + "/" + as1[1]);
                }
            } else {
                selectList.add(as1[0]);
            }
        }

        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(
                true);
        jgl.Array array3 = cgi.getGroupNameList(hashmapordered, hashmap1,
                hashmap2, true);
        for (Enumeration enumeration2 = array3.elements(); enumeration2
                .hasMoreElements();) {
            int i1 = 0;
            parentSelected = false;
            String s6 = (String) enumeration2.nextElement();
            Enumeration enumeration5 = hashmapordered.values(s6);
            while (enumeration5.hasMoreElements()) {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) enumeration5
                        .nextElement();
                String s9 = monitorgroup
                        .getProperty(COM.dragonflow.SiteView.Monitor.pID);
                boolean flag1 = false;
                if (hashmap.get(s9) != null) {
                    flag1 = true;
                    hashmap.put(s9, "used");
                }
                int j1 = selectType(
                        s9,
                        flag1,
                        COM.dragonflow.HTTP.HTTPRequest.encodeString(s9),
                        monitorgroup
                                .getProperty(COM.dragonflow.SiteView.SiteViewGroup.pGroupID),
                        hashmap, i);
                createLine(monitorgroup, j1, s9, s6, stringbuffer1, i1,
                        (i & 2) != 0);
                if (isOpen) {
                    parentSelected = j1 == 1;
                    doNextGroup(monitorgroup, i, s9, hashmap, hashmap1,
                            stringbuffer1, s6, ++i1);
                }
            }
        }

        if (hashmap.get("_master") == null) {
            Enumeration enumeration3 = hashmap.keys();
            while (enumeration3.hasMoreElements()) {
                String s7 = (String) enumeration3
                        .nextElement();
                if (hashmap.get(s7).equals("true")) {
                    stringbuffer2.append("<input type=hidden name=" + ctrlName
                            + " value="
                            + COM.dragonflow.HTTP.HTTPRequest.encodeString(s7)
                            + ">\n");
                }
            } 
        }
        stringbuffer.append("<TR><TD ALIGN=RIGHT VALIGN=TOP><B>" + s
                + "</B></TD>\n" + "<TD ALIGN=LEFT>" + stringbuffer2.toString()
                + "<TABLE>\n" + stringbuffer1.toString()
                + "\n<tr><td><font size=\"-1\">" + s2 + "</font></td></tr>"
                + "</TABLE>\n</TD><TD><I>" + s1 + "</I></TD></TR>");
        boolean flag = treeCommand;
        saveTreeState();
        return !flag;
    }

    private void doNextGroup(COM.dragonflow.SiteView.MonitorGroup monitorgroup,
            int i, String s, jgl.HashMap hashmap,
            jgl.HashMap hashmap1, StringBuffer stringbuffer,
            String s1, int j) {
        Enumeration enumeration = monitorgroup.getMonitors();
        int k = j;
        boolean flag = parentSelected;
        while (enumeration.hasMoreElements()) {
            java.lang.Object obj = (COM.dragonflow.SiteView.Monitor) enumeration
                    .nextElement();
            boolean flag1 = false;
            boolean flag2 = obj instanceof COM.dragonflow.SiteView.SubGroup;
            if ((i & 1) != 0 || flag2) {
                boolean flag3 = (i & 2) != 0 || !flag2;
                j = k;
                parentSelected = flag;
                String s3 = ((COM.dragonflow.SiteView.Monitor) (obj))
                        .getProperty(COM.dragonflow.SiteView.Monitor.pID);
                String s2;
                if ((i & 0x20) != 0) {
                    s2 = s3 + " " + s;
                } else {
                    s2 = s + " " + s3;
                }
                if (hashmap1 == null || hashmap1.get(s2) == null) {
                    if (hashmap.get(s2) != null) {
                        flag1 = true;
                        hashmap.put(s2, "used");
                    }
                    String s5 = ((COM.dragonflow.SiteView.Monitor) (obj))
                            .getProperty(COM.dragonflow.SiteView.Monitor.pName);
                    int l = selectType(
                            s2,
                            flag1,
                            COM.dragonflow.HTTP.HTTPRequest.encodeString(s2),
                            flag2 ? ((COM.dragonflow.SiteView.Monitor) (obj))
                                    .getProperty(COM.dragonflow.SiteView.SubGroup.pGroup)
                                    : "", hashmap, i);
                    createLine(((COM.dragonflow.SiteView.Monitor) (obj)), l,
                            s2, s5, stringbuffer, j, flag3);
                    if (isOpen && flag2) {
                        if (l == 1) {
                            parentSelected = true;
                        }
                        String s4 = ((COM.dragonflow.SiteView.Monitor) (obj))
                                .getProperty(COM.dragonflow.SiteView.SubGroup.pGroup);
                        obj = (COM.dragonflow.SiteView.MonitorGroup) siteview
                                .getElement(s4);
                        String s6 = ((COM.dragonflow.SiteView.Monitor) (obj))
                                .getProperty(COM.dragonflow.SiteView.Monitor.pName);
                        String s7 = ((i & 8) != 0 ? "" : s1 + ": ")
                                + s6;
                        doNextGroup((COM.dragonflow.SiteView.MonitorGroup) obj,
                                i, s4, hashmap, hashmap1, stringbuffer, s7, ++j);
                    }
                }
            }
        } 
    }

    private boolean noneSelected(jgl.HashMap hashmap, int i) {
        boolean flag = false;
        if (selected.get("_none") != null) {
            noneSelected = true;
        }
        if (hashmap.get("_none") != null || hashmap.size() == 0) {
            noneSelected = true;
        }
        if (request.getValue("treeclear_none").length() > 0
                || request.getValue("treeclear_none.x").length() > 0) {
            selected.put("_none", "selected");
            noneSelected = true;
            flag = true;
            hashmap.clear();
            hashmap.put("_none", "used");
        }
        if (noneSelected) {
            if (flag)
                ;
        }
        return noneSelected;
    }

    private boolean masterSelected(jgl.HashMap hashmap, int i) {
        boolean flag = false;
        if (selected.get("_master") != null) {
            masterSelected = true;
        }
        if (hashmap.get("_master") != null) {
            masterSelected = true;
        }
        if (request.getValue("treeclear_master").length() > 0
                || request.getValue("treeclear_master.x").length() > 0) {
            selected.put("_master", "selected");
            masterSelected = true;
            flag = true;
        } else if (request.getValue("treeSeleected_master").length() > 0
                || request.getValue("treeSeleected_master.x").length() > 0) {
            selected.remove("_master");
            masterSelected = false;
            flag = true;
        }
        if (masterSelected && !flag
                && changeSelection("_master", "_master", hashmap, i) == 0) {
            masterSelected = false;
        }
        return masterSelected;
    }

    private int selectType(String s, boolean flag,
            String s1, String s2, jgl.HashMap hashmap, int i) {
        boolean flag1 = false;
        boolean flag4 = false;
        isOpen = false;
        if (s2.length() > 0) {
            if (COM.dragonflow.Utils.TextUtils.getValue(state, "treeopen" + s)
                    .length() > 0) {
                isOpen = true;
            }
            if (request.getValue("treeclose" + s1).length() > 0
                    || request.getValue("treeclose" + s1 + ".x").length() > 0) {
                treeCommand = true;
                isOpen = false;
                state.remove("treeopen" + s);
                boolean flag2 = true;
            }
            if (request.getValue("treeopen" + s1).length() > 0
                    || request.getValue("treeopen" + s1 + ".x").length() > 0) {
                treeCommand = true;
                isOpen = true;
                state.put("treeopen" + s, "true");
                boolean flag3 = true;
            }
        }
        int j = flag ? 1 : 0;
        if (parentSelected || masterSelected) {
            j = 1;
        }
        if (selected.get(s) != null) {
            j = 1;
        }
        if (request.getValue("treeclear" + s1).length() > 0
                || request.getValue("treeclear" + s1 + ".x").length() > 0) {
            j = 1;
            selected.put(s, "selected");
            flag4 = true;
            if (s2.length() > 0 && isOpen) {
                Enumeration enumeration = hashmap.keys();
                while (enumeration.hasMoreElements()) {
                    String s3 = (String) enumeration
                            .nextElement();
                    if (COM.dragonflow.Page.treeControl.isRelated(s3, s2)) {
                        hashmap.remove(s3);
                    }
                } 
            }
        } else if (request.getValue("treeSeleected" + s1).length() > 0
                || request.getValue("treeSeleected" + s1 + ".x").length() > 0) {
            j = 0;
            selected.remove(s);
            flag4 = true;
        }
        if (s2.length() == 0) {
            return j;
        }
        if (!isOpen && j == 0 && !noneSelected && !flag4) {
            for (int k = 0; selectList != null && k < selectList.size(); k++) {
                if (COM.dragonflow.Page.treeControl.isRelated(
                        (String) selectList.at(k), s2)) {
                    return 2;
                }
            }

        } else if (isOpen && j == 1 && !flag4) {
            j = changeSelection(s2, s, hashmap, i);
        }
        return j;
    }

    private int changeSelection(String s, String s1,
            jgl.HashMap hashmap, int i) {
        int j = 1;
        Enumeration enumeration = request.variables.keys();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (!s2.startsWith("treeSeleected") || s2.indexOf(".y") >= 0) {
                continue;
            }
            String s3 = s2.substring("treeSeleected".length());
            if (s3.indexOf(".x") >= 0) {
                s3 = s3.substring(0, s3.lastIndexOf("."));
            }
            s3 = COM.dragonflow.Utils.TextUtils.replaceChar(s3, '+', "/");
            if (!s.equals("_master")
                    && !COM.dragonflow.Page.treeControl.isRelated(s3, s)) {
                continue;
            }
            j = 0;
            selected.remove(s1);
            selectNodes(s, s3, hashmap, i);
            break;
        } 
        return j;
    }

    private void selectNodes(String s, String s1,
            jgl.HashMap hashmap, int i) {
        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor) siteview
                .getElement(s1);
        boolean flag = true;
        if (s.equals("_master")) {
            COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(
                    true);
            jgl.Array array = cgi.getGroupNameList(hashmapordered, null, null,
                    true);
            for (Enumeration enumeration = array.elements(); enumeration
                    .hasMoreElements();) {
                String s2 = (String) enumeration
                        .nextElement();
                Enumeration enumeration1 = hashmapordered.values(s2);
                while (enumeration1.hasMoreElements()) {
                    COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) enumeration1
                            .nextElement();
                    String s3 = monitorgroup
                            .getProperty(COM.dragonflow.SiteView.SiteViewGroup.pGroupID);
                    doNextSelect(s3, monitorgroup, monitor, hashmap, i, flag);
                }
            }

        } else {
            COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor) siteview
                    .getElement(s);
            if (!(monitor1 instanceof COM.dragonflow.SiteView.MonitorGroup)
                    && !(monitor1 instanceof COM.dragonflow.SiteView.SubGroup)) {
                return;
            }
            doNextSelect(s, monitor1, monitor, hashmap, i, flag);
        }
    }

    private boolean doNextSelect(String s,
            COM.dragonflow.SiteView.Monitor monitor,
            COM.dragonflow.SiteView.Monitor monitor1, jgl.HashMap hashmap,
            int i, boolean flag) {
        Enumeration enumeration = monitor.getMonitors();
        String s1 = "";
        while (enumeration.hasMoreElements()) {
            java.lang.Object obj = (COM.dragonflow.SiteView.Monitor) enumeration
                    .nextElement();
            String s3 = ((COM.dragonflow.SiteView.Monitor) (obj))
                    .getProperty(COM.dragonflow.SiteView.Monitor.pID);
            if (obj == monitor1) {
                flag = false;
            } else {
                String s2;
                if ((i & 0x20) != 0) {
                    s2 = s3 + " " + s;
                } else {
                    s2 = s + " " + s3;
                }
                if ((obj instanceof COM.dragonflow.SiteView.MonitorGroup)
                        || (obj instanceof COM.dragonflow.SiteView.SubGroup)) {
                    if (!flag) {
                        hashmap.put(s2, "true");
                    } else {
                        if (hashmap.get(s2) != null) {
                            hashmap.remove(s2);
                        }
                        if (obj instanceof COM.dragonflow.SiteView.SubGroup) {
                            String s4 = ((COM.dragonflow.SiteView.Monitor) (obj))
                                    .getProperty(COM.dragonflow.SiteView.SubGroup.pGroup);
                            obj = (COM.dragonflow.SiteView.MonitorGroup) siteview
                                    .getElement(s4);
                        }
                        String s5 = ((COM.dragonflow.SiteView.Monitor) (obj))
                                .getProperty(COM.dragonflow.SiteView.Monitor.pID);
                        flag = doNextSelect(s5,
                                ((COM.dragonflow.SiteView.Monitor) (obj)),
                                monitor1, hashmap, i, flag);
                        if (flag) {
                            hashmap.put(s2, "true");
                        }
                    }
                } else {
                    hashmap.put(s2, "true");
                }
            }
        } 
        return flag;
    }

    public void createLine(COM.dragonflow.SiteView.Monitor monitor, int i,
            String s, String s1,
            StringBuffer stringbuffer, int j, boolean flag) {
        String s2 = COM.dragonflow.HTTP.HTTPRequest.encodeString(s);
        String s3 = "";
        if (monitor != null
                && ((monitor instanceof COM.dragonflow.SiteView.MonitorGroup) || (monitor instanceof COM.dragonflow.SiteView.SubGroup))) {
            if (monitor instanceof COM.dragonflow.SiteView.SubGroup) {
                s3 = monitor
                        .getProperty(COM.dragonflow.SiteView.SubGroup.pGroup);
            } else {
                s3 = monitor
                        .getProperty(COM.dragonflow.SiteView.SiteViewGroup.pGroupID);
            }
        }
        boolean flag1 = i > 0;
        String s4 = null;
        String s5 = flag1 ? "treeSeleected" : "treeclear";
        if (i == 1) {
            s4 = single ? "radioChecked.jpg" : "checkboxChecked.jpg";
        } else if (i == 2) {
            s4 = single ? "radioGrey.jpg" : "checkboxGrey.jpg";
        } else {
            s4 = single ? "radio.jpg" : "checkbox.jpg";
        }
        boolean flag2 = s3.length() > 0 || s.equals("_master") || single
                && i == 0 && hiddenSelection || parentSelected;
        stringbuffer.append("<tr><td>");
        stringbuffer.append(getIndentHTML(j));
        if (s3.length() > 0) {
            stringbuffer.append(printOpenClose(s2));
            if (flag) {
                stringbuffer.append("<input type=image name=" + s5 + s2
                        + " src=/SiteView/htdocs/artwork/" + s4 + " alt=\""
                        + s5 + "\" border=0>\n");
            } else if (i == 2) {
                stringbuffer.append("<img src=/SiteView/htdocs/artwork/" + s4
                        + " border=0>\n");
            }
            String s6 = COM.dragonflow.SiteView.Platform.getURLPath(
                    "htdocs", request.getAccount())
                    + "/Detail";
            stringbuffer.append("<B><A HREF=" + s6 + s3 + ".html>" + s1
                    + "</b>");
        } else if (s.equals("_master")) {
            stringbuffer.append("<input type=image name=" + s5 + s2
                    + " src=/SiteView/htdocs/artwork/" + s4 + " alt=\"" + s5
                    + "\" border=0>\n");
            stringbuffer.append("<B>" + s1 + "</b>");
            if (masterSelected) {
                stringbuffer.append("<input type=hidden name=" + ctrlName
                        + " value=" + s2 + ">\n");
            }
        } else {
            if (single && !flag2) {
                stringbuffer.append("<input type=radio name=" + ctrlName
                        + " value=\"" + s2 + "\" " + (flag1 ? "checked" : "")
                        + ">");
            } else if (flag2) {
                stringbuffer.append("<input type=image name=" + s5 + s2
                        + " src=/SiteView/htdocs/artwork/" + s4 + " alt=\""
                        + s5 + "\" border=0>\n");
            } else {
                stringbuffer.append("<input type=checkbox name=" + ctrlName
                        + " value=\"" + s2 + "\" " + (flag1 ? "checked" : "")
                        + ">");
            }
            stringbuffer.append(s1);
        }
        if (i == 1 && flag2 && !parentSelected && s3.length() > 0
                && !masterSelected) {
            stringbuffer.append("<input type=hidden name=" + ctrlName
                    + " value=" + s2 + ">\n");
        }
        stringbuffer.append("</td></tr>");
    }

    public void saveTreeState() {
        String s = request.getAccount();
        String s1 = request.getValue("page");
        StringBuffer stringbuffer = new StringBuffer();
        jgl.Array array = getFrames(stringbuffer);
        boolean flag = false;
        boolean flag1 = false;
        for (int i = 0; i < array.size() && (!flag || !flag1); i++) {
            jgl.HashMap hashmap = (jgl.HashMap) array.at(i);
            if (!COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_user")
                    .equals(s)
                    || !COM.dragonflow.Utils.TextUtils
                            .getValue(hashmap, "_page").equals(s1)) {
                continue;
            }
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_type")
                    .equals("state")) {
                flag = true;
                array.put(i, state);
                continue;
            }
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_type")
                    .equals("selected")) {
                flag1 = true;
                array.put(i, selected);
            }
        }

        if (!flag) {
            array.add(state);
        }
        if (!flag1) {
            array.add(selected);
        }
        try {
            COM.dragonflow.Properties.FrameFile.writeToFile(stringbuffer
                    .toString(), array);
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void printBody() throws java.lang.Exception {
    }

    private String printOpenClose(String s) {
        String s1 = isOpen ? "treeclose" : "treeopen";
        String s2 = isOpen ? "Minus.gif" : "Plus.gif";
        return "<input type=image name=" + s1 + s
                + " src=/SiteView/htdocs/artwork/" + s2 + " alt=\"" + s1
                + "\" border=0>";
    }

    private String getIndentHTML(int i) {
        int j = i * 44;
        if (j == 0) {
            j = 1;
        }
        return "<img src=/SiteView/htdocs/artwork/empty1111.gif height=11 width="
                + j + " border=0>";
    }

    private jgl.Array getFrames(StringBuffer stringbuffer) {
        String s1 = request.getAccount();
        String s;
        if (COM.dragonflow.SiteView.Platform.isStandardAccount(s1)) {
            s = COM.dragonflow.SiteView.Platform.getDirectoryPath("groups", s1);
        } else {
            s = COM.dragonflow.SiteView.Platform.getRoot()
                    + java.io.File.separator + "accounts"
                    + java.io.File.separator + s1;
        }
        String s2 = s + java.io.File.separator + "treeCtrl.dyn";
        jgl.Array array = null;
        try {
            array = COM.dragonflow.Properties.FrameFile.readFromFile(s2);
        } catch (java.lang.Exception exception) {
            array = new Array();
        }
        stringbuffer.append(s2);
        return array;
    }

    private void getTreeState() {
        String s = request.getAccount();
        String s1 = request.getValue("page");
        if (rememberSelects
                && COM.dragonflow.Page.treeControl.notHandled(request)) {
            rememberSelects = false;
        }
        state.clear();
        selected.clear();
        jgl.Array array = getFrames(new StringBuffer());
        for (int i = 0; i < array.size(); i++) {
            jgl.HashMap hashmap = (jgl.HashMap) array.at(i);
            if (!COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_user")
                    .equals(s)
                    || !COM.dragonflow.Utils.TextUtils
                            .getValue(hashmap, "_page").equals(s1)) {
                continue;
            }
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_type")
                    .equals("state")
                    && !forgetState) {
                String s2;
                for (Enumeration enumeration = hashmap.keys(); enumeration
                        .hasMoreElements(); state.add(s2, hashmap.get(s2))) {
                    s2 = (String) enumeration.nextElement();
                }

                continue;
            }
            if (!COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_type")
                    .equals("selected")
                    || !COM.dragonflow.Utils.TextUtils
                            .getValue(hashmap, "_page").equals(s1)
                    || !rememberSelects) {
                continue;
            }
            String s3;
            for (Enumeration enumeration1 = hashmap.keys(); enumeration1
                    .hasMoreElements(); selected.add(s3, hashmap.get(s3))) {
                s3 = (String) enumeration1.nextElement();
            }

        }

        if (state.isEmpty()) {
            state.add("_type", "state");
            state.add("_user", s);
            state.add("_page", s1);
        }
        if (selected.isEmpty()) {
            selected.add("_type", "selected");
            selected.add("_user", s);
            selected.add("_page", s1);
        }
    }
}
