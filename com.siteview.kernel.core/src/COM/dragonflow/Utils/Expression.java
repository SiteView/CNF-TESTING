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
// AndExpression, OrExpression, ParseException, InterpreterException,
// TextUtils, SimpleExpression

public abstract class Expression
{

    public Expression()
    {
    }

    public static COM.dragonflow.Utils.Expression parseString(String s)
        throws COM.dragonflow.Utils.ParseException
    {
        String as[] = COM.dragonflow.Utils.TextUtils.tokenize(s);
        return COM.dragonflow.Utils.Expression.parse(as, 0, as.length - 1);
    }

    static COM.dragonflow.Utils.Expression parse(String as[], int i, int j)
        throws COM.dragonflow.Utils.ParseException
    {
        if(j >= i + 4 && as[i + 3].equals("and"))
        {
            COM.dragonflow.Utils.Expression expression = COM.dragonflow.Utils.SimpleExpression.parse(as, i, i + 2);
            COM.dragonflow.Utils.Expression expression4 = COM.dragonflow.Utils.Expression.parse(as, i + 4, j);
            return new AndExpression(expression, expression4);
        }
        if(j >= i + 4 && as[i + 3].equals("or"))
        {
            COM.dragonflow.Utils.Expression expression1 = COM.dragonflow.Utils.SimpleExpression.parse(as, i, i + 2);
            COM.dragonflow.Utils.Expression expression5 = COM.dragonflow.Utils.Expression.parse(as, i + 4, j);
            return new OrExpression(expression1, expression5);
        }
        if(j >= i + 2 && as[i + 1].equals("and"))
        {
            COM.dragonflow.Utils.Expression expression2 = COM.dragonflow.Utils.SimpleExpression.parse(as, i, i);
            COM.dragonflow.Utils.Expression expression6 = COM.dragonflow.Utils.Expression.parse(as, i + 2, j);
            return new AndExpression(expression2, expression6);
        }
        if(j >= i + 2 && as[i + 1].equals("or"))
        {
            COM.dragonflow.Utils.Expression expression3 = COM.dragonflow.Utils.SimpleExpression.parse(as, i, i);
            COM.dragonflow.Utils.Expression expression7 = COM.dragonflow.Utils.Expression.parse(as, i + 2, j);
            return new OrExpression(expression3, expression7);
        } else
        {
            return COM.dragonflow.Utils.SimpleExpression.parse(as, i, j);
        }
    }

    public java.lang.Object interpretExpression(COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.SiteView.Rule rule)
        throws COM.dragonflow.Utils.InterpreterException
    {
        return interpret(siteviewobject, rule);
    }

    abstract java.lang.Object interpret(COM.dragonflow.SiteView.SiteViewObject siteviewobject, COM.dragonflow.SiteView.Rule rule)
        throws COM.dragonflow.Utils.InterpreterException;

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

    public abstract String toString(COM.dragonflow.SiteView.SiteViewObject siteviewobject);
}
