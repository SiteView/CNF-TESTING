/*
 * 
 * Created on 2005-2-28 6:58:18
 *
 * GreaterEqualNumber.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>GreaterEqualNumber</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import jgl.BinaryPredicate;
import jgl.HashMap;

public class GreaterEqualNumber
 implements BinaryPredicate
{

 public GreaterEqualNumber()
 {
 }

 public boolean execute(Object obj, Object obj1)
 {
     Integer integer = (Integer)((HashMap)obj).get("_order");
     Integer integer1 = (Integer)((HashMap)obj).get("_order");
     return integer.intValue() < integer1.intValue();
 }
}