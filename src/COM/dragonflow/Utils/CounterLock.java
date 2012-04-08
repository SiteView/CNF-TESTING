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
import java.util.Vector;

// Referenced classes of package COM.dragonflow.Utils:
// CounterTest

public class CounterLock
{

    private int count;
    public int max;
    public String name;
    java.util.Vector waitingThreads;

    public CounterLock(int i)
    {
        count = 0;
        max = 0;
        name = "";
        waitingThreads = new Vector();
        count = i;
        max = i;
    }

    public int current()
    {
        return count;
    }

    public int used()
    {
        return max - count;
    }

    public synchronized int get()
    {
        do
        {
            if(count > 0)
            {
                count--;
                return count;
            }
            try
            {
                waitingThreads.addElement(java.lang.Thread.currentThread());
                wait();
            }
            catch(java.lang.InterruptedException interruptedexception) { }
        } while(true);
    }

    public synchronized int release()
    {
        count++;
        int i = count;
        if(waitingThreads.size() > 0)
        {
            java.lang.Thread thread = (java.lang.Thread)waitingThreads.remove(0);
            thread.interrupt();
        }
        return i;
    }

    public static void main(String args[])
    {
        COM.dragonflow.Utils.CounterLock counterlock = new CounterLock(4);
        (new CounterTest(counterlock, 0, 100)).start();
        (new CounterTest(counterlock, 0, 90)).start();
        (new CounterTest(counterlock, 0, 80)).start();
        (new CounterTest(counterlock, 0, 70)).start();
        (new CounterTest(counterlock, 0, 60)).start();
        (new CounterTest(counterlock, 0, 50)).start();
    }
}
