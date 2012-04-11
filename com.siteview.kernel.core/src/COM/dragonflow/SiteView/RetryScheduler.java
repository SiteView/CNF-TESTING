/*
 * 
 * Created on 2005-2-16 16:26:10
 *
 * RetryScheduler.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>RetryScheduler</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.SiteView:
// Scheduler, Action, ScheduleEvent, Rule

class RetryScheduler extends Scheduler {

    HashMap nameMap;

    RetryScheduler() {
        nameMap = new HashMap();
    }

    void unschedule(String s) {
        Action action = (Action) nameMap.get(s);
        if (action != null) {
            unschedule(action);
            nameMap.remove(s);
        }
    }

    protected void doAction(ScheduleEvent scheduleevent) {
        Rule rule = scheduleevent.getAction().getRule();
        Monitor monitor = scheduleevent.getAction().getMonitor();
        if (rule != null && monitor != null) {
            String s = Rule.getActionID(rule, monitor);
            nameMap.remove(s);
        }
        super.doAction(scheduleevent);
    }

    protected void schedule(ScheduleEvent scheduleevent) {
        Rule rule = scheduleevent.getAction().getRule();
        Monitor monitor = scheduleevent.getAction().getMonitor();
        if (rule != null && monitor != null) {
            String s = Rule.getActionID(rule, monitor);
            unschedule(s);
            nameMap.put(s, scheduleevent.getAction());
            super.schedule(scheduleevent);
        }
    }
}
