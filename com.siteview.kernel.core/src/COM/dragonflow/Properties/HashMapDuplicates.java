/*
 * 
 * Created on 2005-2-28 6:59:37
 *
 * HashMapDuplicates.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>HashMapDuplicates</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class HashMapDuplicates extends HashMap
{

 boolean allowDuplicates;

 public HashMapDuplicates(boolean flag)
 {
     allowDuplicates = false;
     allowDuplicates = flag;
 }

 public boolean allowsDuplicates()
 {
     return allowDuplicates;
 }

 public int count(Object obj)
 {
     Object obj1 = get(obj);
     if(allowsDuplicates() && obj1 != null && (obj1 instanceof Vector))
     {
         return ((Vector)obj1).size();
     }
     return obj1 == null ? 0 : 1;
 }

 public Object add(Object obj, Object obj1)
 {
     if(allowsDuplicates())
     {
         Object obj2 = get(obj);
         if(obj2 != null)
         {
             if(obj2 instanceof Vector)
             {
                 ((Vector)obj2).add(obj1);
             } else
             {
                 Vector vector = new Vector();
                 vector.add(obj2);
                 vector.add(obj1);
                 put(obj, vector);
             }
             return null;
         }
     }
     return put(obj, obj1);
 }

 public synchronized Enumeration values(Object obj)
 {
     if(allowsDuplicates())
     {
         Object obj1 = get(obj);
         if(obj1 != null && (obj1 instanceof Vector))
         {
             return ((Vector)obj1).elements();
         }
     }
     return null;
 }

 public void print(PrintWriter printwriter)
 {
     Object obj;
     for(Iterator iterator = keySet().iterator(); iterator.hasNext(); printwriter.println(obj + "=" + get(obj)))
     {
         obj = iterator.next();
     }

     printwriter.flush();
 }

 public static void main(String args[])
 {
     HashMapDuplicates hashmapduplicates = new HashMapDuplicates(true);
     String s = "my string one";
     String s1 = "another my string one";
     String s2 = "my string 2";
     String s3 = "my string 3";
     String s4 = "my string 4";
     hashmapduplicates.add("key1", s);
     hashmapduplicates.add("key1", s1);
     hashmapduplicates.add("key2", s2);
     hashmapduplicates.add("key3", s3);
     hashmapduplicates.add("key4", s4);
     hashmapduplicates.print(new PrintWriter(System.out));
     for(Enumeration enumeration = hashmapduplicates.values("key1"); enumeration.hasMoreElements(); System.out.println("2 values for key1: " + enumeration.nextElement())) { }
     System.out.println("key2: " + hashmapduplicates.get("key2"));
     System.out.println("key3: " + hashmapduplicates.get("key3"));
     System.out.println("key4: " + hashmapduplicates.get("key4"));
 }
}
