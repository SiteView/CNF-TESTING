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
import java.util.HashMap;

import jgl.Array;
import COM.dragonflow.Properties.HashMapOrdered;

public class jglUtils {

    public jglUtils() {
    }

    public static jgl.Array toJgl(java.util.List list) {
        jgl.Array array = new Array();
        for (java.util.Iterator iterator = list.iterator(); iterator.hasNext(); array.add(iterator.next())) {
        }
        return array;
    }

    public static java.util.List fromJgl(jgl.Array array) {
        java.util.ArrayList arraylist = new ArrayList();
        for (int i = 0; i < array.size(); i ++) {
            arraylist.add(array.at(i));
        }

        return arraylist;
    }

    public static jgl.HashMap toJgl(java.util.Map map) {
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
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

    public static java.util.HashMap fromJgl(jgl.HashMap hashmap) {
        java.util.HashMap hashmap1 = new HashMap();
        for (jgl.HashMapIterator hashmapiterator = hashmap.begin(); hashmapiterator.hasMoreElements();) {
            jgl.Pair pair = (jgl.Pair) hashmapiterator.nextElement();
            if (pair.second instanceof jgl.Array) {
                hashmap1.put(pair.first, COM.dragonflow.Utils.jglUtils.fromJgl((jgl.Array) pair.second));
            } else {
                hashmap1.put(pair.first, pair.second);
            }
        }

        return hashmap1;
    }
}
