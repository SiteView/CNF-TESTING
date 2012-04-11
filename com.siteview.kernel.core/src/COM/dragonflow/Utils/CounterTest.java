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

// Referenced classes of package COM.dragonflow.Utils:
// CounterLock
class CounterTest extends java.lang.Thread {

    COM.dragonflow.Utils.CounterLock lock;

    int delay1;

    int delay2;

    static int nameCounter = 10;

    int name;

    CounterTest(COM.dragonflow.Utils.CounterLock counterlock, int i, int j) {
        lock = counterlock;
        delay1 = i;
        delay2 = j;
        name = nameCounter ++;
    }

    public void run() {
        for (int i = 1; i < 1000; i ++) {
            try {
                java.lang.Thread.sleep(delay1);
                java.lang.System.out.println(name + ": getting");
                int j = lock.get();
                java.lang.System.out.println(name + ":  got it=" + j);
                java.lang.Thread.sleep(delay2);
                j = lock.release();
                java.lang.System.out.println(name + ": release=" + j);
            } catch (java.lang.Exception exception) {
            }
        }

    }

}
