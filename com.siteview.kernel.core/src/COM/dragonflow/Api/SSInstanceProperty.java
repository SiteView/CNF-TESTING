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
public class SSInstanceProperty extends COM.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String label;

    private java.lang.Object value;

    public SSInstanceProperty(String name, java.lang.Object value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return "(" + name + ", " + label + ", " + (String) value + ")";
    }

    public SSInstanceProperty(String name, String label, java.lang.Object value) {
        this.name = name;
        this.label = label;
        this.value = value;
    }

    public java.lang.Object getReturnValueType() {
        java.lang.Class class1 = getClass();
        return class1.getName();
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public java.lang.Object getValue() {
        return value;
    }
}
