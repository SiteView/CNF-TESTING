/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.ConfigurationManager;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

public interface CfgChangesSink {

    public abstract void notifyAdjustedGroups(com.dragonflow.SiteView.SiteViewGroup siteviewgroup, java.util.Collection collection, java.util.Collection collection1, java.util.Collection collection2);
}
