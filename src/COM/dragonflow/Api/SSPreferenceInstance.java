/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Api;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Api:
// SSBaseReturnValues, SSInstanceProperty

public class SSPreferenceInstance extends COM.dragonflow.Api.SSBaseReturnValues
{

    private String settingName;
    private COM.dragonflow.Api.SSInstanceProperty instanceProperties[];

    public SSPreferenceInstance(String s, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
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

    public COM.dragonflow.Api.SSInstanceProperty[] getInstanceProperties()
    {
        return instanceProperties;
    }
}
