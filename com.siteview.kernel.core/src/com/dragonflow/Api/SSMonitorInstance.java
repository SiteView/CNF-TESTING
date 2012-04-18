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

public class SSMonitorInstance extends com.dragonflow.Api.SSBaseReturnValues
{

    private String groupId;
    private String monitorId;
    private com.dragonflow.Api.SSInstanceProperty instanceProperties[];

    public SSMonitorInstance(String s, String s1, com.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        groupId = s;
        monitorId = s1;
        instanceProperties = assinstanceproperty;
    }

    public java.lang.Object getReturnValueType()
    {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getMonitorId()
    {
        return monitorId;
    }

    public com.dragonflow.Api.SSInstanceProperty[] getInstanceProperties()
    {
        return instanceProperties;
    }
}
