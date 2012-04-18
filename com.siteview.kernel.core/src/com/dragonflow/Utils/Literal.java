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
// Expression, StringLiteral, NumericLiteral, PropertyNameLiteral,
// ParseException
public abstract class Literal extends com.dragonflow.Utils.Expression {

    public Literal() {
    }

    /**
     * 
     * 
     * @param s
     * @return
     * @throws com.dragonflow.Utils.ParseException
     */
    static com.dragonflow.Utils.Expression parse(String s) throws com.dragonflow.Utils.ParseException {
        if (s.startsWith("'") && s.endsWith("'")) {
            return new StringLiteral(s.substring(1, s.length() - 1));
        }

        try {
            java.lang.Float float1;
            boolean flag = true;
            for (int i = 0; i < s.length(); i ++) {
                char c = s.charAt(i);
                if (c != '.' && !java.lang.Character.isDigit(c)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                float1 = java.lang.Float.valueOf(s);
                return new NumericLiteral(float1.floatValue());
            }

            return new PropertyNameLiteral(s);
        } catch (NumberFormatException e) {
            return new PropertyNameLiteral(s);
        } catch (Exception e) {
            throw new ParseException("Not a number: " + s);
        }
    }
}
