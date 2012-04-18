/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Api;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public class APIAlertCacheManager implements com.dragonflow.ConfigurationManager.CfgChangesSink {

    private static com.dragonflow.Api.APIAlertCacheManager instance = null;

    private HashMap instanceInfoCache;

    private Array conditionsCache;

    static java.lang.Object instanceLock = new Object();

    public APIAlertCacheManager() {
        instanceInfoCache = null;
        conditionsCache = null;
    }

    static com.dragonflow.Api.APIAlertCacheManager getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new APIAlertCacheManager();
                    com.dragonflow.SiteView.ConfigurationChanger.registerCfgChangesSink(instance);
                }
            }
        }
        return instance;
    }

    public void notifyAdjustedGroups(com.dragonflow.SiteView.SiteViewGroup siteviewgroup, java.util.Collection collection, java.util.Collection collection1, java.util.Collection collection2) {
        instanceInfoCache = null;
        conditionsCache = null;
    }

    HashMap getInstanceInfoCache() {
        return instanceInfoCache;
    }

    void setInstanceInfoCache(HashMap hashmap) {
        instanceInfoCache = hashmap;
    }

    Array getConditionsCache() {
        return conditionsCache;
    }

    void setConditionsCache(Array array) {
        conditionsCache = array;
    }

}
