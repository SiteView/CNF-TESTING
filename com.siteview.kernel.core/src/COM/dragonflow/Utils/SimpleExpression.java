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
// Expression, BooleanLiteral, ParseException, InterpreterException,
// Literal, TextUtils

public class SimpleExpression extends COM.dragonflow.Utils.Expression
{

    COM.dragonflow.Utils.Expression left;
    COM.dragonflow.Utils.Expression right;
    boolean notEqual;
    boolean equal;
    boolean greaterThan;
    boolean lessThan;
    boolean contains;
    boolean notContains;
    boolean multipleOf;

    public COM.dragonflow.Utils.Expression getLeft()
    {
        return left;
    }

    public COM.dragonflow.Utils.Expression getRight()
    {
        return right;
    }

    static COM.dragonflow.Utils.Expression parse(String as[], int i, int j)
        throws COM.dragonflow.Utils.ParseException
    {
        if(j - i == 2)
        {
            COM.dragonflow.Utils.Expression expression = COM.dragonflow.Utils.Literal.parse(as[i]);
            String s = as[i + 1];
            COM.dragonflow.Utils.Expression expression1 = COM.dragonflow.Utils.Literal.parse(as[i + 2]);
            return new SimpleExpression(expression, s, expression1);
        }
        if(i == j)
        {
            return new BooleanLiteral(as[i]);
        } else
        {
            throw new ParseException("Improper expression: \"" + COM.dragonflow.Utils.SimpleExpression.getTokenString(as, i, j) + "\"");
        }
    }

    SimpleExpression(COM.dragonflow.Utils.Expression expression, String s, COM.dragonflow.Utils.Expression expression1)
    {
        notEqual = false;
        equal = false;
        greaterThan = false;
        lessThan = false;
        contains = false;
        notContains = false;
        multipleOf = false;
        left = expression;
        right = expression1;
        if(s.indexOf('>') >= 0)
        {
            greaterThan = true;
        }
        if(s.indexOf('<') >= 0)
        {
            lessThan = true;
        }
        if(s.indexOf('=') >= 0)
        {
            equal = true;
        }
        if(s.indexOf('!') >= 0)
        {
            notEqual = true;
        }
        if(s.equals("eq"))
        {
            equal = true;
        }
        if(s.equals("ne"))
        {
            notEqual = true;
        }
        if(s.equals("contains"))
        {
            contains = true;
        }
        if(s.equals("doesNotContain"))
        {
            notContains = true;
        }
        if(s.equals("multipleOf"))
        {
            multipleOf = true;
        }
        if(greaterThan && lessThan)
        {
            notEqual = true;
        }
    }

    /**
     * 
     */
    java.lang.Object interpret(COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.SiteView.Rule rule)
        throws COM.dragonflow.Utils.InterpreterException
    {
        java.lang.Object obj = left.interpret(siteviewobject, rule);
        java.lang.Object obj1 = right.interpret(siteviewobject, rule);
        if((obj instanceof java.lang.Number) || (obj1 instanceof java.lang.Number))
        {
            java.lang.Object obj2;
            if(obj instanceof String)
            {
                if(obj.equals("n/a"))
                {
                    return new Boolean(false);
                }
                try
                {
                    obj2 = java.lang.Float.valueOf((String)obj);
                }
                catch(java.lang.Exception exception)
                {
                    obj2 = new Float(0.0F);
                }
            } else
            {
                obj2 = (java.lang.Number)obj;
            }
            java.lang.Object obj3;
            if(obj1 instanceof String)
            {
                if(obj1.equals("n/a"))
                {
                    return new Boolean(false);
                }
                try
                {
                    obj3 = java.lang.Float.valueOf((String)obj1);
                }
                catch(java.lang.Exception exception1)
                {
                    obj3 = new Float(0.0F);
                }
            } else
            {
                obj3 = (java.lang.Number)obj1;
            }
            float f = ((java.lang.Number) (obj2)).floatValue();
            float f1 = ((java.lang.Number) (obj3)).floatValue();
            if(notEqual)
            {
                return new Boolean(f != f1);
            }
            if(greaterThan && equal)
            {
                return new Boolean(f >= f1);
            }
            if(lessThan && equal)
            {
                return new Boolean(f <= f1);
            }
            if(greaterThan)
            {
                return new Boolean(f > f1);
            }
            if(lessThan)
            {
                return new Boolean(f < f1);
            }
            if(equal)
            {
                return new Boolean(f == f1);
            }
            if(multipleOf)
            {
                long l = (long)f;
                long l1 = (long)(f1 * 1000F + 0.1F);
                long l2 = l1 / 1000L;
                long l3 = l1 % 1000L;
                boolean flag1 = false;
                if(l2 > 0L)
                {
                    flag1 = l % l2 == l3;
                }
                return new Boolean(flag1);
            } else
            {
                return new Boolean(false);
            }
        }
        boolean flag;
        if(contains || notContains)
        {
            flag = COM.dragonflow.Utils.TextUtils.match((String)obj, (String)obj1);
            if(notContains)
            {
                return new Boolean(!flag);
            }
        } else
        if(equal || notEqual)
        {
            flag = ((String)obj).equalsIgnoreCase((String)obj1);
            if(notEqual)
            {
                return new Boolean(!flag);
            }
        } else
        {
            throw new InterpreterException("Unknown string comparison: " + obj + "," + obj1);
        }
        return new Boolean(flag);
    }

    public String toString(COM.dragonflow.SiteView.SiteViewObject siteviewobject)
    {
        String s = left.toString(siteviewobject);
        String s1 = right.toString(siteviewobject);
        String s2 = "";
        if(notEqual)
        {
            s2 = " != ";
        } else
        if(greaterThan && equal)
        {
            s2 = " >= ";
        } else
        if(lessThan && equal)
        {
            s2 = " <= ";
        } else
        if(greaterThan)
        {
            s2 = " > ";
        } else
        if(lessThan)
        {
            s2 = " < ";
        } else
        if(equal)
        {
            s2 = " == ";
        } else
        if(multipleOf)
        {
            s2 = " multipleOf ";
        } else
        if(contains)
        {
            s2 = " contains ";
        } else
        if(notContains)
        {
            s2 = " doesNotContain ";
        } else
        {
            s2 = " UNKNOWN_COMPARE_OPERATOR ";
        }
        return s + s2 + s1;
    }
}
