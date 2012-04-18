/*
 * 
 * Created on 2005-2-16 16:27:54
 *
 * RuleGroupIs.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>RuleGroupIs</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.recursionsw.jgl.UnaryPredicate;

// Referenced classes of package com.dragonflow.SiteView:
// Rule

public class RuleGroupIs implements UnaryPredicate {

    int ruleGroup;

    public RuleGroupIs(int i) {
        ruleGroup = i;
    }

    public boolean execute(Object obj) {
        Rule rule = (Rule) obj;
        return ruleGroup == rule.getRuleGroup();
    }
}
