/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Api;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Api:
// SSBaseReturnValues
public class SSHealthStats extends com.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String status;

    private String type;

    private java.lang.Object measurement1;

    private java.lang.Object measurement2;

    public static final String CURRENT_MONITORS_PER_MINUTE = "CURRENT_MONITORS_PER_MINUTE";

    public static final String CURRENT_MONITORS_RUNNING = "CURRENT_MONITORS_RUNNING";

    public static final String CURRENT_MONITORS_WAITING = "CURRENT_MONITORS_WAITING";

    public static final String MAXIMUM_MONITORS_PER_MINUTE = "MAXIMUM_MONITORS_PER_MINUTE";

    public static final String MAXIMUM_MONITORS_RUNNING = "MAXIMUM_MONITORS_RUNNING";

    public static final String MAXIMUM_MONITORS_WAITING = "MAXIMUM_MONITORS_WAITING";

    public static final String LOG_MONITOR_STATS = "LOG_MONITOR_STATS";

    public static final String SERVER_LOAD_STATS = "SERVER_LOAD_STATS";

    public static final String RUNNING_MONITOR_STATS = "RUNNING_MONITOR_STATS";

    public SSHealthStats(String s, String s1, String s2, java.lang.Object obj, java.lang.Object obj1) {
        name = s;
        status = s1;
        measurement1 = obj;
        measurement2 = obj1;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = null;
        try {
            class1 = java.lang.Class.forName("java.lang.Object");
            if (type.equals("CURRENT_MONITORS_PER_MINUTE")) {
                class1 = java.lang.Class.forName("java.lang.Float");
            } else if (type.equals("CURRENT_MONITORS_RUNNING")) {
                class1 = java.lang.Class.forName("java.lang.Integer");
            } else if (type.equals("CURRENT_MONITORS_WAITING")) {
                class1 = java.lang.Class.forName("java.lang.Integer");
            }
            if (type.equals("MAXIMUM_MONITORS_PER_MINUTE")) {
                class1 = java.lang.Class.forName("java.lang.Float");
            } else if (type.equals("MAXIMUM_MONITORS_RUNNING")) {
                class1 = java.lang.Class.forName("java.lang.Float");
            } else if (type.equals("MAXIMUM_MONITORS_WAITING")) {
                class1 = java.lang.Class.forName("java.lang.Float");
            } else if (type.equals("LOG_MONITOR_STATS")) {
                class1 = java.lang.Class.forName("java.lang.Integer");
            } else if (type.equals("SERVER_LOAD_STATS")) {
                class1 = java.lang.Class.forName("String");
            } else if (type.equals("RUNNING_MONITOR_STATS")) {
                class1 = java.lang.Class.forName("String");
            }
        } catch (java.lang.ClassNotFoundException classnotfoundexception) {
            class1 = null;
        }
        return class1;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public java.lang.Object getMeasurement1() {
        return measurement1;
    }

    public java.lang.Object getMeasurement2() {
        return measurement2;
    }
}
