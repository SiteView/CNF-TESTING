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

import java.util.ArrayList;

import COM.dragonflow.Properties.HashMapOrdered;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.HashMapIterator;

public class jglUtils {

    public jglUtils() {
    }

    public static Array toJgl(java.util.List list) {
        Array array = new Array();
        for (java.util.Iterator iterator = list.iterator(); iterator.hasNext(); array.add(iterator.next())) {
        }
        return array;
    }

    public static java.util.List fromJgl(Array array) {
        java.util.ArrayList arraylist = new ArrayList();
        for (int i = 0; i < array.size(); i ++) {
            arraylist.add(array.get(i));
        }

        return arraylist;
    }

    public static HashMap toJgl(java.util.Map map) {
        HashMapOrdered hashmapordered = new HashMapOrdered(true);
        java.util.Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
            if (entry != null) {
                java.lang.Object obj = entry.getKey();
                java.lang.Object obj1 = entry.getValue();
                if (obj1 instanceof java.util.List) {
                    hashmapordered.add(obj, COM.dragonflow.Utils.jglUtils.toJgl((java.util.List) obj1));
                } else {
                    hashmapordered.add(obj, obj1);
                }
            }
        } 
        return hashmapordered;
    }

    public static java.util.HashMap fromJgl(HashMap hashmap) {
        java.util.HashMap hashmap1 = new java.util.HashMap();
        for (HashMapIterator hashmapiterator = hashmap.begin(); hashmapiterator.hasMoreElements();) {
            Pair pair = (Pair) hashmapiterator.nextElement();
            if (pair.second instanceof Array) {
                hashmap1.put(pair.first, COM.dragonflow.Utils.jglUtils.fromJgl((Array) pair.second));
            } else {
                hashmap1.put(pair.first, pair.second);
            }
        }

        return hashmap1;
    }
}
