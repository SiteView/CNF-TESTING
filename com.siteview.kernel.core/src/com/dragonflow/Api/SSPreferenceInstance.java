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

public class SSPreferenceInstance extends com.dragonflow.Api.SSBaseReturnValues
{

    private String settingName;
    private com.dragonflow.Api.SSInstanceProperty instanceProperties[];

    public SSPreferenceInstance(String s, com.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        settingName = s;
        instanceProperties = assinstanceproperty;
    }

    public java.lang.Object getReturnValueType()
    {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getSettingName()
    {
        return settingName;
    }

    public com.dragonflow.Api.SSInstanceProperty[] getInstanceProperties()
    {
        return instanceProperties;
    }
}
