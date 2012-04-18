/*
 * 
 * Created on 2005-2-16 16:27:04
 *
 * Rule.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>Rule</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import com.dragonflow.Log.LogManager;
import com.dragonflow.Properties.HashMapOrdered;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.Utils.Expression;
import com.dragonflow.Utils.I18N;
import com.dragonflow.Utils.TextUtils;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.SiteView:
// SiteViewObject, Monitor, Platform, AtomicMonitor,
// Action, MasterConfig

public class Rule extends SiteViewObject {

    public static StringProperty pExpression;
    public static StringProperty pAction;
    public static StringProperty pDisplayThis;
    public static final int kClassifierRuleGroup = 1;
    static final int kActionRuleGroup = 2;
    static String cRulePackages[] = { "com.dragonflow.SiteView.", "com.dragonflow.StandardAction.", "CustomAction." };
    private static HashMap cCurrentActions = new HashMap();
    static HashMap setting;
    private static int topazAlertsEnabled = 0;
    int ruleGroup;
    boolean stopOnMatch;
    public boolean isDefaultRule;
    Expression cachedExpression;
    public Array includeFilter;
    public Array excludeFilter;

    public Rule() {
        ruleGroup = 2;
        stopOnMatch = false;
        isDefaultRule = false;
        cachedExpression = null;
        includeFilter = null;
        excludeFilter = null;
    }

    public static String getActionID(Rule rule, Monitor monitor) {
        return rule.getFullID() + " -- " + monitor.getFullID();
    }

    public String getFullID() {
        if (owner != null) {
            return owner.getFullID() + ID_SEPARATOR + getProperty("_alertID");
        } else {
            return getProperty("_alertID");
        }
    }

    public static Rule createRule(HashMap hashmap) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return (Rule) createObject(hashmap, false, cRulePackages);
    }

    public static Rule stringToClassifier(String s) {
        return stringToClassifier(s, false);
    }

    public static Rule stringToClassifier(String s, boolean flag) {
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.add("_class", "Rule");
        String as[] = TextUtils.split(s, "\t");
        if (as.length < 2) {
            System.out.println("Classifier had no tabs (probably converted to spaces): " + s);
            return null;
        }
        hashmapordered.add("_expression", as[0]);
        hashmapordered.add("_action", "SetProperty category " + as[1]);
        if (as.length > 2) {
            hashmapordered.add("_displayThis", as[2]);
        }
        Rule rule = null;
        try {
            rule = createRule(hashmapordered);
            rule.setRuleGroup(1);
            rule.isDefaultRule = flag;
        } catch (Exception exception) {
            System.out.println("Could not create classifier from string: " + s);
        }
        return rule;
    }

    public static Rule stringToAction(String s) {
        HashMapOrdered hmo;
        String[] as;
        hmo = new HashMapOrdered(true);
        hmo.add("_class", "Rule");
        as = TextUtils.split(s, "\t");
        if (as.length < 2) {
            System.out.println("Action had no tabs (probably converted to spaces): " + s);
            return null;
        }
        hmo.add("_expression", as[0]);
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(as[1]);
        stringbuffer.setCharAt(0, Character.toUpperCase(stringbuffer.charAt(0)));
        hmo.add("_action", stringbuffer.toString());
        if (as.length >= 3) {
            hmo.add("_alertID", as[2]);
        }

        Rule rule = null;
        try {
            rule = (Rule) SiteViewObject.createObject(hmo);
            if (rule != null) {
                rule.setRuleGroup(2);
                if (as.length > 3) {
                    String s1 = as[3];
                    if (!s1.startsWith("_UIContext=") && s1.length() != 0) {
                        rule.includeFilter = new Array();
                        Array array = Platform.split(',', s1);
                        String s2;
                        for (Enumeration en = (Enumeration) array.iterator(); en.hasMoreElements(); rule.includeFilter.add(s2)) {
                            s2 = (String) en.nextElement();
                            s2 = '/' + s2.replace(' ', '/') + '/';
                        }
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Could not create action from string: " + s);
        }
        return rule;
    }

    int getRuleGroup() {
        return ruleGroup;
    }

    void setRuleGroup(int i) {
        switch (i) {
        case 1: // '\001'
            stopOnMatch = true;
            break;

        case 2: // '\002'
            stopOnMatch = false;
            break;
        }
        ruleGroup = i;
    }

    boolean getStopOnMatch() {
        return stopOnMatch;
    }

    public boolean match(SiteViewObject sso) {
        if (this.excludeFilter != null || this.includeFilter != null) {
            String s = sso.getOwner().getGroupPathID() + I18N.toDefaultEncoding(sso.getProperty(pID)) + "/";
            if (this.excludeFilter != null) {
                boolean exclude = false;
                for (Enumeration en = (Enumeration) excludeFilter.iterator(); en.hasMoreElements();) {
                    String s2 = (String) en.nextElement();
                    if (s.indexOf(s2) != -1) {
                        exclude = true;
                        if (AtomicMonitor.alertDebug) {
                            LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId
                                    + " Monitor excluded because: " + s2);
                        }
                        return false;
                    }
                }

                if (exclude) {
                    if (AtomicMonitor.alertDebug) {
                        LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId
                                + " excluded because excludeFilter was true: " + excludeFilter.toString());
                    }
                    return false;
                }
            }

            if (this.includeFilter != null) {
                boolean include = false;
                Enumeration ifEnum = (Enumeration) this.includeFilter.iterator();
                while (ifEnum.hasMoreElements()) {
                    String s3 = (String) ifEnum.nextElement();
                    if (s.indexOf(s3) == -1) {
                        continue;
                    }
                    include = true;
                    break;
                }

                if (!include) {
                    if (AtomicMonitor.alertDebug) {
                        LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId
                                + " excluded because INCLUDEFilter was false: " + includeFilter.toString());
                    }
                    return false;
                }
            }
        }

        String exp = getProperty(Rule.pExpression);
        Boolean result = new Boolean(false);
        try {
            if (AtomicMonitor.alertDebug) {
                LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId + " Rule: " + exp);
            }
            if (this.cachedExpression == null) {
                this.cachedExpression = Expression.parseString(exp);
            }
            result = (Boolean) this.cachedExpression.interpretExpression(sso, this);
            return result.booleanValue();
        } catch (Exception e) {
            LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId + " Match Exception: " + e);
            System.out.println("Match Exception: " + e);
        } finally {
            if (AtomicMonitor.alertDebug) {
                LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId + " "
                        + cachedExpression.toString());
                LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId
                        + " ALERT/GOOD/WARNING/ERROR VALUES: " + cachedExpression.toString(sso));
                LogManager.log("RunMonitor", "alertDebug: " + AtomicMonitor.alertDebugId + " Rule value: " + result);
            }
        }
        return false;
    }

    public void testAction(Monitor monitor) {
    }

    public void doAction(Monitor monitor) {
        if (doAction(monitor, null) && topazAlertsEnabled > 0) {
//            TopazServerSettings topazserversettings = TopazManager.getInstance().getTopazServerSettings();
//            if (TopazManager.getInstance().isAMServerIntegrationActived()) {
//                Array array = Platform.split(' ', getProperty(pAction));
//                String s = (String) array.popFront();
//                if (!s.startsWith("Set")) {
//                    HashMap hashmap = new HashMap(1);
//                    hashmap.put("action", s);
//                    AMMessage ammessage = new AMMessage(2);
//                    ammessage.mDate = new Date();
//                    int i = TopazManager.getInstance().getTopazServerSettings().getTopazIntegrationVersion();
//                    if (i >= 3) {
//                        ammessage.mTopazEvent = new SiteViewEvent(monitor, hashmap, false);
//                    } else {
//                        ammessage.mTopazEvent = new TopazAlert(monitor, hashmap);
//                    }
//                    Publisher.publish(ammessage);
//                }
//            }
        }
    }

    public boolean doAction(Monitor monitor, PrintWriter printwriter) {
        Action action = null;
        if (action == null) {
            action = Action.createAction(getProperty(pAction));
            if (action != null) {
                action.setMonitor(monitor);
                action.setRule(this);
                action.setOwner(this);
                action.setTraceStream(printwriter);
                if (getStopOnMatch())
                    ;
            }
        }
        if (action != null) {
            monitor.currentStatus = "sending alert...";
            action.trigger();
        } else {
            System.out.println("Could not trigger action " + getProperty(pAction));
        }
        return true;
    }

    public void undoAction(Monitor monitor) {
    }

    public static void main(String args[]) {
        Rule rule = new Rule();
        Rule rule1 = new Rule();
        String s = "10";
        String s1 = "5";
        if (args.length > 0) {
            s = args[0];
        }
        if (args.length > 1) {
            s1 = args[1];
        }
        rule1.setProperty("val", s);
        rule.setProperty(pExpression, "val multipleOf " + s1);
        boolean flag = rule.match(rule1);
        System.out.println("matched=" + flag);
    }

    static {
        pExpression = new StringProperty("_expression", "");
        pAction = new StringProperty("_action", "");
        pDisplayThis = new StringProperty("_displayThis", "");
        StringProperty astringproperty[] = { pExpression, pAction, pDisplayThis };
        addProperties("com.dragonflow.SiteView.Rule", astringproperty);
        setting = MasterConfig.getMasterConfig();
        if (setting != null) {
            topazAlertsEnabled = TextUtils.toInt(TextUtils.getValue(setting, "_topazAlertsEnabled"));
        }
    }
}