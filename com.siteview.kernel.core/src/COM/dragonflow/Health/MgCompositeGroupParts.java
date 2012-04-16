/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Health;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public abstract class MgCompositeGroupParts {

    String parentName;

    String parentGroupName;

    String FileName;

    COM.dragonflow.Health.MgCompositeGroupParts parentRef;

    static java.util.HashMap fileNames = new HashMap();

    static java.util.Vector monitorNames = new Vector();

    static java.util.Vector remoteMachines = new Vector();

    static java.util.Vector remoteNTMachines = new Vector();

    java.util.Vector messages;

    String fileTitle;

    String fileExtension;

    public MgCompositeGroupParts() {
        parentName = null;
        parentGroupName = null;
        FileName = null;
        parentRef = null;
        messages = new Vector();
    }

    public abstract void createHTML();

    public abstract void printSelf(int i);

    public abstract boolean errorCheck();

    public abstract boolean reportToErrorLog();

    public abstract boolean addMonitorNames();

    public java.util.HashMap getFileNameList() {
        return fileNames;
    }

    public java.util.Vector getMonitorNameList() {
        return monitorNames;
    }

    public void setParentRef(COM.dragonflow.Health.MgCompositeGroupParts mgcompositegroupparts) {
        parentRef = mgcompositegroupparts;
    }

    public void addMessage(String s) {
        messages.add(s);
    }

    public boolean add(COM.dragonflow.Health.MgCompositeGroupParts mgcompositegroupparts) {
        return true;
    }

    public java.lang.Object check_TagEqualsValue(String s, java.lang.Object obj, boolean flag, boolean flag1) {
        if (obj == null) {
            if (flag1) {
                return null;
            } else {
                String s1 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' tag: '" + s + "' not found.";
                messages.add(s1);
                java.lang.System.out.println(s1);
                return "";
            }
        }
        if (obj instanceof String) {
            if (obj.equals("")) {
                String s2 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' tag: '" + s + "' value blank.";
                messages.add(s2);
                java.lang.System.out.println(s2);
            }
        } else if (obj instanceof java.util.Vector) {
            if (!flag) {
                String s3 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' multiple tags: '" + s + "' found. values: '" + obj.toString() + "' in header or monitor. Invalid in this situation.";
                messages.add(s3);
                java.lang.System.out.println(s3);
                return ((java.util.Vector) obj).lastElement();
            }
        } else if (obj instanceof java.util.HashMap) {
            return "";
        }
        return obj;
    }

    public void setFileName(String s) {
        if (s == null) {
            FileName = "";
        } else {
            FileName = s;
        }
        if (!FileName.equals("")) {
            fileNames.put(FileName, " ");
        }
    }

    /**
     * 
     * 
     * @param hashmapduplicates
     * @return
     */
    public boolean checkAlerts(COM.dragonflow.Properties.HashMapDuplicates hashmapduplicates) {
        java.lang.Object obj;
        obj = hashmapduplicates.get("_alertCondition");
        if (obj == null) {
            obj = new String("");
            return true;
        }
        try {
            check_TagEqualsValue("_alertCondition", obj, true, false);
            java.util.Vector vector = new Vector();
            if (obj instanceof String) {
                vector.add(obj);
            } else if (obj instanceof java.util.Vector) {
                vector = (java.util.Vector) obj;
            }
            java.lang.Object obj1 = check_TagEqualsValue("_nextConditionID", hashmapduplicates.get("_nextConditionID"), false, false);
            java.lang.Integer integer = checkCountingNumber("_nextConditionID", obj1);
            java.util.Iterator iterator = vector.iterator();
            java.util.HashMap hashmap = new HashMap();
            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();
                java.util.StringTokenizer stringtokenizer = new StringTokenizer(s1, "\t");
                if (stringtokenizer.hasMoreTokens()) {
                    String s2 = stringtokenizer.nextToken();
                    String s6 = "^(disabled and )?category eq '(error|warning|good)'(\\sand\\s\\S+\\s\\S+\\s(\\S|'.*')+)*$";
                    if (COM.dragonflow.Utils.TextUtils.matchExpression(s2, "/" + s6 + "/") != 200) {
                        String s9 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' alert condition: '" + s2 + "' does not match this pattern: " + s6;
                        messages.add(s9);
                        java.lang.System.out.println(s9);
                    }
                }
                if (stringtokenizer.hasMoreTokens()) {
                    String s3 = stringtokenizer.nextToken();
                    String s7 = "^(mailto|page|run|SNMPTrap|Sound|DatabaseAlert|disable|NTLogEvent|Post)\\s.+$";
                    if (COM.dragonflow.Utils.TextUtils.matchExpression(s3, "/" + s7 + "/") != 200) {
                        String s10 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' alert action: '" + s3 + "' does not match this pattern: " + s7;
                        messages.add(s10);
                        java.lang.System.out.println(s10);
                    }
                }
                if (stringtokenizer.hasMoreTokens()) {
                    String s4 = stringtokenizer.nextToken();
                    String s8 = s4;
                    java.lang.Integer integer1 = checkCountingNumber("alert id", s8);
                    if (integer1 != null) {
                        int i = integer1.intValue();
                        String s13 = null;
                        s13 = (String) hashmap.put(integer1, "");
                        if (s13 != null) {
                            String s14 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' alert id: '" + integer1 + "' found multiple alert id tags with this same number in this frame( lines between #s).";
                            messages.add(s14);
                            java.lang.System.out.println(s14);
                        }
                        if (integer != null) {
                            int j = integer.intValue();
                            if (j < i) {
                                String s15 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' _alertCondition alert id: '" + integer1 + "' greater than _nextConditionID: '" + integer + "' for this frame( lines between #s)";
                                messages.add(s15);
                                java.lang.System.out.println(s15);
                            }
                        }
                    }
                }
                if (stringtokenizer.hasMoreTokens()) {
                    String s5 = stringtokenizer.nextToken();
                    java.util.StringTokenizer stringtokenizer1 = new StringTokenizer(s5, ",");
                    while (stringtokenizer1.hasMoreTokens()) {
                        String s11 = stringtokenizer1.nextToken();
                        if (fileNames.get(s11) == null && !monitorNames.contains(s11)) {
                            String s12 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' alert monitor(Name Format: 'mgName ID#') or group name: '" + s11 + "' not found.";
                            messages.add(s12);
                            java.lang.System.out.println(s12);
                        }
                    }
                }
            }
        } catch (java.lang.Exception exception) {
            String s = "Logger error:  Exception: " + exception.getMessage();
            java.lang.System.out.println(s);
            COM.dragonflow.Log.LogManager.log(s);
        }
        return true;
    }

    protected java.lang.Integer checkCountingNumber(String s, java.lang.Object obj) {
        java.lang.Integer integer = null;
        if (obj instanceof String) {
            String s1 = (String) obj;
            if (COM.dragonflow.Utils.TextUtils.matchExpression(s1, "/^[1-9][0-9]{0,7}$/") == 200) {
                integer = new Integer(s1);
            } else {
                String s2 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' " + s + ": '" + s1 + "' is not a counting number like 1,2,3... ";
                messages.add(s2);
                java.lang.System.out.println(s2);
            }
        }
        return integer;
    }

    protected void checkForDuplicatesAndGTNextID(String s, java.lang.Integer integer, java.util.HashMap hashmap, String s1, java.lang.Integer integer1) {
        if (integer != null) {
            int i = integer.intValue();
            String s2 = (String) hashmap.put(integer, "");
            if (s2 != null) {
                String s3 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' " + s + ": '" + integer + "' found multiple id tags with this same number in this file. May indicate # missing between monitor frames.";
                messages.add(s3);
                java.lang.System.out.println(s3);
            }
            if (integer1 != null) {
                int j = integer1.intValue();
                if (j < i) {
                    String s4 = "ERROR, " + fileTitle + ": '" + FileName + fileExtension + "' " + s + ": '" + integer + "' greater than " + s1 + ": '" + integer1 + "'";
                    messages.add(s4);
                    java.lang.System.out.println(s4);
                }
            }
        }
    }

}
