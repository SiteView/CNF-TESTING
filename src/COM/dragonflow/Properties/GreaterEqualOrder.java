/*
 * 
 * Created on 2005-2-28 6:58:42
 *
 * GreaterEqualOrder.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>GreaterEqualOrder</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import jgl.BinaryPredicate;

//Referenced classes of package COM.dragonflow.Properties:
//         StringProperty

public class GreaterEqualOrder
 implements BinaryPredicate
{

 private boolean ignoreAdvanced;

 public GreaterEqualOrder(boolean flag)
 {
     ignoreAdvanced = false;
     flag = flag; //XXX Weird here 
     
//     Method public  <init> () -> void
//     0  aload_0                  
//     1  invokenonvirtual #1 <Method java/lang/Object.<init> ()V>       
//     4  aload_0                  
//     5  iconst_0                 
//     6  putfield #2 <Field COM/dragonflow/Properties/GreaterEqualOrder.ignoreAdvanced Z>       
//     9  return  
 }

 public GreaterEqualOrder()
 {
     ignoreAdvanced = false;
 }

 public boolean execute(Object obj, Object obj1)
 {
     StringProperty stringproperty = (StringProperty)obj;
     StringProperty stringproperty1 = (StringProperty)obj1;
     int i = stringproperty.getOrder();
     int j = stringproperty1.getOrder();
     if(!ignoreAdvanced)
     {
         if(stringproperty.isAdvanced)
         {
             i += 10000;
         }
         if(stringproperty1.isAdvanced)
         {
             j += 10000;
         }
     }
     return i < j;
 }
}