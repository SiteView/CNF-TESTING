/*
 * 
 * Created on 2005-2-28 6:51:24
 *
 * BrowsableProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>BrowsableProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.BrowsableCache;
import COM.dragonflow.SiteView.BrowsableMonitor;
import COM.dragonflow.SiteView.SiteViewObject;

// Referenced classes of package COM.dragonflow.Properties:
// StringProperty

public class BrowsableProperty extends StringProperty {

    public static String BROWSE = "browse";

    boolean disabled;

    public BrowsableProperty(String s) {
        this(s, "");
    }

    public BrowsableProperty(String s, String s1) {
        super(s, s1);
        disabled = false;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param node
     * @return
     */
    Array getNodeIdNames(Node node) {
        Array array = new Array();
        String s = "id";
        String s1 = ((Element) node).getAttribute(s);
        if (s1 == null || s1.length() == 0) {
            s = "name";
            s1 = ((Element) node).getAttribute(s);
        }
        if (s1 == null) {
            return array;
        }
        array.add(s1);
        Node node1 = node.getParentNode();
        while (node1 != null) {
            String s2 = ((Element) node1).getAttribute(s);
            if (s2 == null || s2.length() <= 0) {
                break;
            }
            array.add(s2);
            node1 = node1.getParentNode();
        } 
        return array;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param node
     * @return
     */
    Array getNodeDisplayNames(Node node) {
        Array array = new Array();
        String s = ((Element) node).getAttribute("name");
        if (s == null) {
            return array;
        }
        array.add(s);
        Node node1 = node.getParentNode();
        while (node1 != null) {
            String s1 = ((Element) node1).getAttribute("name");
            if (s1 == null || s1.length() <= 0) {
                break;
            }
            array.add(s1);
            node1 = node1.getParentNode();
        } 
        return array;
    }

    public void buildNodeList(Array array, Node node) {
        if (node == null || !node.hasChildNodes()) {
            return;
        }
        NodeList nodelist = node.getChildNodes();
        int i = nodelist.getLength();
        for (int j = 0; j < i; j++) {
            Node node1 = nodelist.item(j);
            array.add(node1);
            buildNodeList(array, node1);
        }

    }

    public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject,
            HTTPRequest httprequest, HashMap hashmap, boolean flag) {
        if (disabled) {
            return;
        }
        if (siteviewobject instanceof BrowsableMonitor) {
            Object obj = hashmap.get(this);
            String s = "";
            if (obj != null) {
                s = (String) obj;
            }
            AtomicMonitor atomicmonitor = (AtomicMonitor) siteviewobject;
            boolean flag1 = false;
            String s1 = httprequest.getValue("uniqueID");
            if (s1.length() > 0) {
                flag1 = true;
            } else {
                s1 = BrowsableCache.getUniqueID();
            }
            StringBuffer stringbuffer = printCounters(atomicmonitor, s1, flag1);
            boolean flag2 = true;
            boolean flag3 = true;
            if (atomicmonitor instanceof BrowsableBase) {
                flag3 = ((BrowsableBase) atomicmonitor).isServerBased();
            }
            String s2 = httprequest.rawURL;
            String s3 = cgi.getPageLink("browsable", "") + "&returnURL=" + URLEncoder.encode(s2);
            if (flag3) {
                printServer(atomicmonitor, s1, printwriter, flag2, s3, flag1);
            }
            printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>"
                    + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\">");
            printwriter.println("<TABLE border=1 cellspacing=0>");
            printwriter.println(stringbuffer.toString());
            printwriter.println("</TABLE></TD><TD>");
            printwriter.println("</TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription()
                    + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>" + "<TR><TD></TD><TD>");
            if (flag2) {
                if (flag3) {
                    printwriter.println("<a href=" + s3 + "&uniqueID=" + s1 + "&" + "countersPage"
                            + "=no>choose counters</a>");
                } else {
                    String s4 = "";
                    Array array = atomicmonitor.getPropertiesToPassBetweenPages(httprequest);
                    for (int i = 0; i < array.size(); i++) {
                        StringProperty stringproperty = (StringProperty) array.at(i);
                        s4 = s4 + " + getPropValues('" + stringproperty.getName() + "')";
                    }

                    String s5 = "<script> function getPropValues(prop) {\nvalues = \"\";\nfor (i=0; i < document.getElementsByName(prop).length; i++)\nif (document.getElementsByName(prop).item(i).type == 'checkbox') {\nif (document.getElementsByName(prop).item(i).checked)\n values += \"&\" + prop + \"=true\"; }\nelse values += \"&\" + prop + \"=\" + document.getElementsByName(prop).item(i).value;\nreturn values;}\nfunction createHref() {\nlocation.href=\""
                            + s3
                            + "&uniqueID="
                            + s1
                            + "&"
                            + "countersPage"
                            + "="
                            + "yes"
                            + "&submit=Browse\""
                            + s4
                            + ";} </script>";
                    printwriter.println(s5 + "<a href=\"javascript:createHref();\">choose counters</a>");
                }
            }
            printwriter.println("</TD></TR><TR><TD>&nbsp;</TD></TR>");
            BrowsableCache.saveCache(s1);
        } else {
            printwriter.println("BrowsableProperty not valid with this monitor.");
        }
    }

    public void setDisabled() {
        disabled = true;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param atomicmonitor
     * @param s
     * @param flag
     * @return
     */
    private StringBuffer printCounters(AtomicMonitor atomicmonitor, String s, boolean flag) {
        StringBuffer stringbuffer = new StringBuffer();
        if (flag) {
            stringbuffer.append("<input type=hidden name=\"uniqueID\" value=\"" + s + "\">\n");
            HashMap hashmap = BrowsableCache.getCache(s, true, false);
            HashMap hashmap2 = (HashMap) hashmap.get("selectNames");
            HashMap hashmap4 = (HashMap) hashmap.get("selectIDs");
            HashMap hashmap6 = (HashMap) hashmap.get("permanentSelectNames");
            HashMap hashmap8 = (HashMap) hashmap.get("permanentSelectIDs");
            HashMap hashmap10 = (HashMap) hashmap6.clone();
            Enumeration enumeration = hashmap10.keys();
            if (enumeration.hasMoreElements()) {
                int i = 1;
                while (enumeration.hasMoreElements() || i > atomicmonitor.getMaxCounters()) {
                    String s3 = (String) enumeration.nextElement();
                    if (s3.endsWith("object")) {
                        String s5 = s3.substring(0, s3.indexOf("object"));
                        COM.datachannel.xml.om.Document document = BrowsableCache.getXml(s);
                        if (document.getDocumentElement() != null) {
                            Array array = new Array();
                            NodeList nodelist = document.getDocumentElement().getChildNodes();
                            int k = nodelist.getLength();
                            Node node = null;
                            for (int i1 = 0; i1 < k; i1++) {
                                array.add(nodelist.item(i1));
                                buildNodeList(array, nodelist.item(i1));
                            }

                            for (int j1 = 0; j1 < array.size(); j1++) {
                                Node node1 = (Node) array.at(j1);
                                if (node1 == null || !node1.getNodeName().equals("object")) {
                                    continue;
                                }
                                String s7 = ((BrowsableMonitor) atomicmonitor)
                                        .setBrowseName(getNodeDisplayNames(node1));
                                if (!s7.equals(URLDecoder.decode(s5))) {
                                    continue;
                                }
                                node = node1;
                                break;
                            }

                            if (node != null) {
                                NodeList nodelist1 = node.getChildNodes();
                                int l = nodelist1.getLength();
                                array.clear();
                                for (int k1 = 0; k1 < l; k1++) {
                                    array.add(nodelist1.item(k1));
                                    buildNodeList(array, nodelist1.item(k1));
                                }

                                for (int l1 = 0; l1 < array.size(); l1++) {
                                    Node node2 = (Node) array.at(l1);
                                    if (!node2.getNodeName().equals("counter")) {
                                        continue;
                                    }
                                    String s8 = ((BrowsableMonitor) atomicmonitor).setBrowseID(getNodeIdNames(node2));
                                    String s9 = ((BrowsableMonitor) atomicmonitor)
                                            .setBrowseName(getNodeDisplayNames(node2));
                                    if (hashmap10.get(URLEncoder.encode(s9)) == null) {
                                        stringbuffer.append("<TR><TD><input type=checkbox name=\"" + BROWSE
                                                + URLEncoder.encode(s9) + "\" value=\"" + URLEncoder.encode(s9) + "\" "
                                                + "CHECKED" + ">" + s9 + "</TD></TR>");
                                        i++;
                                        hashmap2.put(URLEncoder.encode(s9), "visible");
                                        hashmap4.put(URLEncoder.encode(s9), URLEncoder.encode(s8));
                                        hashmap6.put(URLEncoder.encode(s9), "visible");
                                        hashmap8.put(URLEncoder.encode(s9), URLEncoder.encode(s8));
                                    }
                                }

                                hashmap2.remove(s3);
                            } else {
                                stringbuffer.append("<TR><TD>no selections</TD></TR>");
                            }
                        }
                    } else {
                        stringbuffer.append("<TR><TD><input type=checkbox name=\"" + BROWSE + s3 + "\" value=\"" + s3
                                + "\" " + "CHECKED" + ">" + URLDecoder.decode(s3) + "</TD></TR>");
                        i++;
                    }
                }
            } else {
                stringbuffer.append("<TR><TD>no selections</TD></TR>");
            }
        } else {
            stringbuffer.append("<input type=hidden name=\"uniqueID\" value=\"" + s + "\">\n");
            HashMap hashmap1 = BrowsableCache.getCache(s, true, false);
            HashMap hashmap3 = (HashMap) hashmap1.get("selectNames");
            HashMap hashmap5 = (HashMap) hashmap1.get("selectIDs");
            HashMap hashmap7 = (HashMap) hashmap1.get("permanentSelectNames");
            HashMap hashmap9 = (HashMap) hashmap1.get("permanentSelectIDs");
            String s1 = ((BrowsableMonitor) atomicmonitor).getBrowseName();
            String s2 = ((BrowsableMonitor) atomicmonitor).getBrowseID();
            int j = 1;
            while (true) {
                String s4 = atomicmonitor.getProperty(s1 + j);
                String s6 = atomicmonitor.getProperty(s2 + j);
                if (s4.length() == 0 || s6.length() == 0) {
                    break;
                }
                hashmap3.put(URLEncoder.encode(s4), "");
                hashmap5.put(URLEncoder.encode(s4), URLEncoder.encode(s6));
                hashmap7.put(URLEncoder.encode(s4), "");
                hashmap9.put(URLEncoder.encode(s4), URLEncoder.encode(s6));
                stringbuffer.append("<TR><TD><input type=checkbox name=\"" + BROWSE + URLEncoder.encode(s4)
                        + "\" value=\"" + URLEncoder.encode(s4) + "\" " + "CHECKED" + ">" + s4 + "</TD></TR>");
                j++;
            }
            if (j == 1) {
                stringbuffer.append("<TR><TD>no selections</TD></TR>");
            }
        }
        return stringbuffer;
    }

    private void printServer(AtomicMonitor atomicmonitor, String s, PrintWriter printwriter, boolean flag, String s1,
            boolean flag1) {
        Array array = ((BrowsableMonitor) atomicmonitor).getConnectionProperties();
        HashMap hashmap = BrowsableCache.getCache(s, false, false);
        HashMap hashmap1 = (HashMap) hashmap.get("cParms");
        String s2 = "";
        String s3 = "";
        if (array.size() > 0) {
            s2 = ((StringProperty) array.at(0)).getName();
            s3 = (String) hashmap1.get(s2);
        }
        if (s3 == null || s3.length() == 0) {
            s3 = atomicmonitor.getHostname();
        }
        if (s3.length() == 0) {
            s3 = "none selected";
        }
        StringProperty stringproperty = atomicmonitor.getPropertyObject(s2);
        String s4 = "";
        if (stringproperty != null) {
            s4 = stringproperty.getDescription();
        }
        printwriter
                .println("<TR><TD ALIGN=RIGHT>Server</TD><TD><TABLE><TR><TD ALIGN=LEFT><TABLE border=1 cellspacing=0><TR><TD>"
                        + s3 + "</TD></TR></TABLE>");
        printwriter.println("<TD>");
        if (flag) {
            printwriter.println("<a href=" + s1 + "&uniqueID=" + s + ">choose server</a>");
        }
        printwriter.println("</TD></TR><TR><TD><FONT SIZE=-1>" + s4 + "</FONT></TABLE></TD></TR><TR><TD></TD></TR>");
        for (int i = 0; i < array.size(); i++) {
            String s5 = ((StringProperty) array.at(i)).getName();
            String s6 = (String) hashmap1.get(s5);
            if (s6 == null) {
                s6 = "";
            }
            if (s6.length() == 0 && !flag1) {
                s6 = atomicmonitor.getProperty(s5);
            }
            printwriter.println("<input type=hidden name=\"" + s5 + "\" value=\"" + s6 + "\">\n");
            hashmap1.put(s5, s6);
        }

    }

}
