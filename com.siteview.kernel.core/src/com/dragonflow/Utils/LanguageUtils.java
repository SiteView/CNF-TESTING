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

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils

public class LanguageUtils {

    static HashMap languageCache = new HashMap();

    public LanguageUtils() {
    }

    static HashMap getLanguage(String s) {
        HashMap hashmap = (HashMap) languageCache.get(s);
        if (hashmap == null) {
            try {
                Array array = com.dragonflow.Properties.FrameFile.readFromFile(com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.language" + java.io.File.separator + s + ".txt");
                hashmap = (HashMap) array.front();
                languageCache.put(s, hashmap);
            } catch (java.lang.Exception exception) {
            }
        }
        return hashmap;
    }

    public static String getString(String s, String s1) {
        HashMap hashmap = com.dragonflow.Utils.LanguageUtils.getLanguage(s1);
        String s2;
        if (hashmap == null) {
            s2 = "(error: missing language for " + s1 + ".txt)";
        } else {
            s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, s);
            if (s2.length() == 0) {
                s2 = "(error: missing string for " + s + ")";
            }
        }
        return s2;
    }

}
