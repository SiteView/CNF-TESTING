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
// Literal, InterpreterException
public class StringLiteral extends COM.dragonflow.Utils.Literal {

    String string;

    StringLiteral(String s) {
        string = s;
    }

    java.lang.Object interpret(COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.SiteView.Rule rule) throws COM.dragonflow.Utils.InterpreterException {
        return string;
    }

    public String toString(COM.dragonflow.SiteView.SiteViewObject siteviewobject) {
        return string;
    }
}
