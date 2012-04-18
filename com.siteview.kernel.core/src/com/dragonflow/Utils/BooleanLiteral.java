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
// Literal, ParseException
public class BooleanLiteral extends com.dragonflow.Utils.Literal {

    boolean value;

    BooleanLiteral(String s) throws com.dragonflow.Utils.ParseException {
        if (s.equalsIgnoreCase("always")) {
            value = true;
        } else if (s.equalsIgnoreCase("disabled")) {
            value = false;
        } else if (s.equalsIgnoreCase("true")) {
            value = true;
        } else if (s.equalsIgnoreCase("false")) {
            value = false;
        } else {
            throw new ParseException("Not a boolean: " + s);
        }
    }

    java.lang.Object interpret(com.dragonflow.SiteView.SiteViewObject siteviewobject, com.dragonflow.SiteView.Rule rule) {
        return new Boolean(value);
    }

    public String toString(com.dragonflow.SiteView.SiteViewObject siteviewobject) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
