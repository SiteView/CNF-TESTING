/*
 * 
 * Created on 2005-2-28 6:56:30
 *
 * FindObjectsVisitor.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>FindObjectsVisitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Enumeration;

import jgl.Array;

//Referenced classes of package COM.dragonflow.Properties:
//         Visitor, PropertiedObject

public class FindObjectsVisitor extends Visitor
{

 Array results;
 boolean stopAfterFirst;

 public FindObjectsVisitor()
 {
     results = null;
     stopAfterFirst = false;
     results = new Array();
 }

 public Array getResults()
 {
     return results;
 }

 public Enumeration getResultsElements()
 {
     if(results != null)
     {
         return results.elements();
     } else
     {
         return null;
     }
 }

 public PropertiedObject getResult()
 {
     if(results != null && !results.isEmpty())
     {
         return (PropertiedObject)results.front();
     } else
     {
         return null;
     }
 }

 protected void setStopAfterFirst(boolean flag)
 {
     stopAfterFirst = flag;
 }

 public int visit(PropertiedObject propertiedobject)
 {
     if(test(propertiedobject))
     {
         results.add(propertiedobject);
         if(stopAfterFirst)
         {
             return 1;
         }
     }
     return 0;
 }

 protected boolean test(PropertiedObject propertiedobject)
 {
     return true;
 }
}