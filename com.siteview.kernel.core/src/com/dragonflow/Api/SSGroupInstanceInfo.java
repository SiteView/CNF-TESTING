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
public class SSGroupInstanceInfo extends com.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String groupId;

    private String parentGroupId;

    public SSGroupInstanceInfo(String s, String s1, String s2) {
        name = s;
        groupId = s1;
        parentGroupId = s2;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }
}
