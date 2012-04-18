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
// AndExpression, OrExpression, ParseException, InterpreterException,
// TextUtils, SimpleExpression

public abstract class Expression
{

    public Expression()
    {
    }

    public static com.dragonflow.Utils.Expression parseString(String s)
        throws com.dragonflow.Utils.ParseException
    {
        String as[] = com.dragonflow.Utils.TextUtils.tokenize(s);
        return com.dragonflow.Utils.Expression.parse(as, 0, as.length - 1);
    }

    static com.dragonflow.Utils.Expression parse(String as[], int i, int j)
        throws com.dragonflow.Utils.ParseException
    {
        if(j >= i + 4 && as[i + 3].equals("and"))
        {
            com.dragonflow.Utils.Expression expression = com.dragonflow.Utils.SimpleExpression.parse(as, i, i + 2);
            com.dragonflow.Utils.Expression expression4 = com.dragonflow.Utils.Expression.parse(as, i + 4, j);
            return new AndExpression(expression, expression4);
        }
        if(j >= i + 4 && as[i + 3].equals("or"))
        {
            com.dragonflow.Utils.Expression expression1 = com.dragonflow.Utils.SimpleExpression.parse(as, i, i + 2);
            com.dragonflow.Utils.Expression expression5 = com.dragonflow.Utils.Expression.parse(as, i + 4, j);
            return new OrExpression(expression1, expression5);
        }
        if(j >= i + 2 && as[i + 1].equals("and"))
        {
            com.dragonflow.Utils.Expression expression2 = com.dragonflow.Utils.SimpleExpression.parse(as, i, i);
            com.dragonflow.Utils.Expression expression6 = com.dragonflow.Utils.Expression.parse(as, i + 2, j);
            return new AndExpression(expression2, expression6);
        }
        if(j >= i + 2 && as[i + 1].equals("or"))
        {
            com.dragonflow.Utils.Expression expression3 = com.dragonflow.Utils.SimpleExpression.parse(as, i, i);
            com.dragonflow.Utils.Expression expression7 = com.dragonflow.Utils.Expression.parse(as, i + 2, j);
            return new OrExpression(expression3, expression7);
        } else
        {
            return com.dragonflow.Utils.SimpleExpression.parse(as, i, j);
        }
    }

    public java.lang.Object interpretExpression(com.dragonflow.SiteView.SiteViewObject siteviewobject, com.dragonflow.SiteView.Rule rule)
        throws com.dragonflow.Utils.InterpreterException
    {
        return interpret(siteviewobject, rule);
    }

    abstract java.lang.Object interpret(com.dragonflow.SiteView.SiteViewObject siteviewobject, com.dragonflow.SiteView.Rule rule)
        throws com.dragonflow.Utils.InterpreterException;

    static String getTokenString(String as[], int i, int j)
    {
        String s = "";
        for(int k = 0; k < j; k++)
        {
            if(s.length() > 0)
            {
                s = s + " ";
            }
            if(as[k].indexOf(" ") >= 0)
            {
                s = s + "'" + as[k] + "'";
            } else
            {
                s = s + as[k];
            }
        }

        return s;
    }

    public abstract String toString(com.dragonflow.SiteView.SiteViewObject siteviewobject);
}
