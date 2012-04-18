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
// Literal
public class PropertyNameLiteral extends com.dragonflow.Utils.Literal {

    String propertySpec;

    PropertyNameLiteral(String s) {
        propertySpec = s;
    }

    java.lang.Object interpret(com.dragonflow.SiteView.SiteViewObject siteviewobject, com.dragonflow.SiteView.Rule rule) {
        String s = propertySpec;
        int i = s.indexOf(".");
        if (i >= 0) {
            String s1 = s.substring(0, i);
            s = s.substring(i + 1);
            com.dragonflow.SiteView.SiteViewObject siteviewobject1 = siteviewobject.resolveObjectReference(s1);
            if (siteviewobject1 != null) {
                siteviewobject = siteviewobject1;
            }
        }
        if (siteviewobject.hasProperty(s)) {
            String s2 = siteviewobject.getProperty(s);
            return s2;
        }
        String s3 = siteviewobject.getProperty(s);
        if (s3.length() > 0) {
            return s3;
        }
        if (rule != null) {
            String s4 = siteviewobject.getProperty(s + rule.getFullID());
            if (s4.length() > 0) {
                return s4;
            }
        }
        return s;
    }

    public String getPropertyName() {
        return propertySpec;
    }

    public String toString(com.dragonflow.SiteView.SiteViewObject siteviewobject) {
        String s = siteviewobject.getProperty(propertySpec);
        if (s == "") {
            return propertySpec;
        } else {
            return propertySpec + "(value: " + s + ")";
        }
    }
}
