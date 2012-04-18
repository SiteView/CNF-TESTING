/*
 * 
 * Created on 2005-2-28 7:00:23
 *
 * HashMapOrdered.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>HashMapOrdered</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class HashMapOrdered extends HashMap implements java.io.Serializable {

 public HashMapOrdered(boolean flag)
 {
     super(flag);
 }

 public int count(Object obj)
 {
     if(allowsDuplicates())
     {
         Object obj1 = get(obj);
         if(obj1 != null && (obj1 instanceof Array))
         {
             return ((Array)obj1).size();
         }
     }
     return super.count(obj);
 }

 public boolean add(Object obj, Object obj1)
 {
     if(allowsDuplicates())
     {
         Object obj2 = get(obj);
         if(obj2 != null)
         {
             if(obj2 instanceof Array)
             {
                 ((Array)obj2).add(obj1);
             } else
             {
                 Array array = new Array();
                 array.add(obj2);
                 array.add(obj1);
                 put(obj, array);
             }
             return false;
         }
     }
     return super.add(obj, obj1);
 }

 public synchronized Iterator values(Object obj)
 {
     if(allowsDuplicates())
     {
         Object obj1 = get(obj);
         if(obj1 != null && (obj1 instanceof Array))
         {
             return ((Array)obj1).iterator();
         }
     }
     return super.values(obj);
 }

 public void print(PrintWriter printwriter)
 {
     Object obj;
     for(Enumeration enumeration = (Enumeration) keys(); enumeration.hasMoreElements(); printwriter.println(obj + "=" + get(obj)))
     {
         obj = enumeration.nextElement();
     }

     printwriter.flush();
 }
}
