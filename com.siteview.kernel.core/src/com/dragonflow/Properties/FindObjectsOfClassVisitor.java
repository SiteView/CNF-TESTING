/*
 * 
 * Created on 2005-2-28 6:55:54
 *
 * FindObjectsOfClassVisitor.java
 *
 * History:
 *
 */
package com.dragonflow.Properties;

/**
 * Comment for <code>FindObjectsOfClassVisitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
//Referenced classes of package com.dragonflow.Properties:
//         FindObjectsVisitor, PropertiedObject

public class FindObjectsOfClassVisitor extends FindObjectsVisitor
{

 private Class targetClass;

 public FindObjectsOfClassVisitor(String s)
 {
     targetClass = null;
     try
     {
         targetClass = Class.forName(s);
     }
     catch(ClassNotFoundException classnotfoundexception)
     {
         System.err.println("Could not find object of class: " + s);
     }
 }

 protected boolean test(PropertiedObject propertiedobject)
 {
     if(targetClass != null)
     {
         for(Class class1 = propertiedobject.getClass(); class1 != null; class1 = class1.getSuperclass())
         {
             if(class1 == targetClass)
             {
                 return true;
             }
         }

     }
     return false;
 }
}
