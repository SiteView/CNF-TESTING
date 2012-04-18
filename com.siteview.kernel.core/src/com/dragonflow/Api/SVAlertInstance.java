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
// SSBaseReturnValues, SSInstanceProperty

public class SVAlertInstance extends com.dragonflow.Api.SSBaseReturnValues
{

    private String alertId;
    private String monitorId;
    private String groupId;
    private com.dragonflow.Api.SSInstanceProperty instanceProperties[];

    public SVAlertInstance(String s, String s1, String s2, com.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        alertId = s;
        monitorId = s1;
        groupId = s2;
        instanceProperties = assinstanceproperty;
    }

    public java.lang.Object getReturnValueType()
    {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getAlertId()
    {
        return alertId;
    }

    public String getMonitorId()
    {
        return monitorId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public com.dragonflow.Api.SSInstanceProperty[] getInstanceProperties()
    {
        return instanceProperties;
    }
}
