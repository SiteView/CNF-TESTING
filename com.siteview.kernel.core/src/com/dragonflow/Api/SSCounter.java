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
public class SSCounter extends com.dragonflow.Api.SSBaseReturnValues {

    private String name;

    private String id;

    public SSCounter(String s, String s1) {
        name = s;
        id = s1;
    }

    public String toString() {
        return "(" + name + ", " + id + ")";
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
}
