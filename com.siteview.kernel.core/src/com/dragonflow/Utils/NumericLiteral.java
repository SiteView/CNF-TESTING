/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package com.dragonflow.Utils:
// Literal, InterpreterException, TextUtils
public class NumericLiteral extends com.dragonflow.Utils.Literal {

    float value;

    NumericLiteral(float f) {
        value = f;
    }

    java.lang.Object interpret(com.dragonflow.SiteView.SiteViewObject siteviewobject, com.dragonflow.SiteView.Rule rule) throws com.dragonflow.Utils.InterpreterException {
        return new Float(value);
    }

    public String toString(com.dragonflow.SiteView.SiteViewObject siteviewobject) {
        return com.dragonflow.Utils.TextUtils.floatToString(value, 0);
    }
}
