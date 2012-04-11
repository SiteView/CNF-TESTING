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
// SSBaseReturnValues
public class SSMonitorInstanceInfo extends COM.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String id;

    private String groupId;

    private String type;

    public SSMonitorInstanceInfo(String s, String s1, String s2, String s3) {
        name = s;
        id = s1;
        groupId = s2;
        type = s3;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }
}
