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
public class SSStringReturnValue extends COM.dragonflow.Api.SSBaseReturnValues {

    private String value;

    public SSStringReturnValue(String s) {
        value = s;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = null;
        try {
            class1 = java.lang.Class.forName("String");
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
        return class1.getName();
    }

    public String getValue() {
        return value;
    }
}
