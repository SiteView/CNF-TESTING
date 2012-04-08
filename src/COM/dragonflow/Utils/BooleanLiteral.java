/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

// Referenced classes of package COM.dragonflow.Utils:
// Literal, ParseException
public class BooleanLiteral extends COM.dragonflow.Utils.Literal {

    boolean value;

    BooleanLiteral(String s) throws COM.dragonflow.Utils.ParseException {
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

    java.lang.Object interpret(COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.SiteView.Rule rule) {
        return new Boolean(value);
    }

    public String toString(COM.dragonflow.SiteView.SiteViewObject siteviewobject) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
