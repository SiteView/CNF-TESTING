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
public class SSAlertInstanceInfo extends com.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String alertFullId;

    private String alertId;

    private String uniqueAlertId;

    private String groupId;

    private String monitorId;

    private String type;

    public SSAlertInstanceInfo(String s, String s1, String s2, String s3, String s4, String s5) {
        name = s;
        alertFullId = s1;
        alertId = s2;
        groupId = s3;
        monitorId = s4;
        type = s5;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getName() {
        return name;
    }

    public String getAlertFullId() {
        return alertFullId;
    }

    public String getAlertId() {
        return alertId;
    }

    public String getUniqueAlertId() {
        return uniqueAlertId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public String getType() {
        return type;
    }
}
