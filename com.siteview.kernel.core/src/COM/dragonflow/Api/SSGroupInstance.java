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

public class SSGroupInstance extends COM.dragonflow.Api.SSBaseReturnValues
{

    private String groupId;
    private COM.dragonflow.Api.SSInstanceProperty instanceProperties[];

    public SSGroupInstance(String s, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        groupId = s;
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

    public COM.dragonflow.Api.SSInstanceProperty[] getInstanceProperties()
    {
        return instanceProperties;
    }
}
