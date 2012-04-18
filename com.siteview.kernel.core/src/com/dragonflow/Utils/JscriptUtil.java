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


public class JscriptUtil
{

    public JscriptUtil()
    {
    }

    public static String createFieldsAsUrlParamsFunc(String s, String as[])
    {
        String s1 = "inputsNamesArray";
        String s2 = com.dragonflow.Utils.JscriptUtil.createArray(s1, as);
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("<SCRIPT>\n   function " + s + "(){\n" + "       " + s2 + "       var urlParams = \"\";\n" + "       isFirst = true;\n" + "       for (i=0; i<" + s1 + ".length; i++){\n" + "           var colObj = document.getElementsByName(" + s1 + "[i]);\n" + "           for (j=0; j<colObj.length; j++){\n" + "               if (colObj[j].tagName.toLowerCase() == \"input\"){\n" + "                   if (!isFirst){\n" + "                       urlParams += \"&\";\n" + "                   }else{\n" + "                       isFirst = false;\n" + "                   }\n" + "               }\n" + "               urlParams += colObj[j].name + \"=\" + colObj[j].value;\n" + "               break;\n" + "           }\n" + "       }\n" + "       return urlParams;" + "   }\n" + "</SCRIPT>\n");
        return stringbuffer.toString();
    }

    private static String createArray(String s, String as[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("var " + s + "= new Array(");
        for(int i = 0; i < as.length; i++)
        {
            stringbuffer.append("\"" + as[i] + "\"");
            if(i < as.length - 1)
            {
                stringbuffer.append(",");
            }
        }

        stringbuffer.append(");\n");
        return stringbuffer.toString();
    }

    public static String createArrayFromInstancePropsFunc(String s, com.dragonflow.Properties.StringProperty astringproperty[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("function " + s + "(){\n" + "   var propsArray = new Array(\n");
        for(int i = 0; i < astringproperty.length; i++)
        {
            String s1 = astringproperty[i].getLabel();
            String s2 = astringproperty[i].getDescription();
            stringbuffer.append("       new Array(\"" + s1 + "\",\"" + s2 + "\")");
            if(i < astringproperty.length - 1)
            {
                stringbuffer.append(",\n");
            }
        }

        stringbuffer.append(");\n");
        stringbuffer.append("   return propsArray;\n");
        stringbuffer.append("}\n");
        return stringbuffer.toString();
    }
}
