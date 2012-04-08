/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Chart;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.awt.Frame;
import java.util.Hashtable;

// Referenced classes of package COM.dragonflow.Chart:
// DrawerAWT, AppletLineChart

public class Graph extends java.applet.Applet
    implements java.lang.Runnable
{

    int imageHeight;
    int imageWidth;
    static final String paramStartTime = "startTime";
    static final String paramEndTime = "endTime";
    static final String paramTimeOffset = "timeOffset";
    static final String paramData = "data";
    static final String paramMaxData = "maxdata";
    static final String paramName = "name";
    static final String paramVertMax = "vertMax";
    static final String paramPropertyName = "propertyName";
    static final String paramAverage = "average";
    static final String paramMaximum = "maximum";
    static final String paramAveragedSamples = "averagedSamples";
    static final String paramTitle = "title";
    static final String paramSubtitle = "subtitle";
    java.util.Hashtable properties;
    java.awt.Image offscreen;
    java.lang.Thread m_Graph;
    boolean m_fStandAlone;

    public Graph()
    {
        properties = new Hashtable();
        offscreen = null;
        m_Graph = null;
        m_fStandAlone = false;
    }

    public String getAppletInfo()
    {
        return "Name: SiteView Management Graph\r\nAuthor: Dragonflow Software, Inc.\r\n";
    }

    public String[][] getParameterInfo()
    {
        String as[][] = {
            {
                "title", "String", "title of graph"
            }, {
                "subtitle", "String", "subtitle of graph - appears below title"
            }, {
                "startTime", "String", "start time of graph, in form 10:00:00 12/12/97"
            }, {
                "endTime", "String", "start time of graph, in form 10:00:00 12/12/97"
            }, {
                "timeOffset", "String", "time offset in seconds"
            }, {
                "data", "String", "data, in form tttcvvv|tttcvv, where t is seconds past start time, c is category, and v is the value"
            }, {
                "maxdata", "String", "data for maximums, in same for as regular data - only include if averagedSamples is true"
            }, {
                "average", "String", "average of data"
            }, {
                "maximum", "String", "maximum of data"
            }, {
                "averagedSamples", "String", "true or false - are samples averaged values"
            }, {
                "vertMax", "String", "maximum vertical value"
            }, {
                "propertyName", "String", "name of the data that is being displayed"
            }, {
                "name", "String", "name of the data set"
            }
        };
        return as;
    }

    String getParameter(String s, String as[])
    {
        if(as == null)
        {
            return getParameter(s);
        }
        String s1 = s + "=";
        String s2 = null;
        for(int i = 0; i < as.length; i++)
        {
            if(!s1.equalsIgnoreCase(as[i].substring(0, s1.length())))
            {
                continue;
            }
            s2 = as[i].substring(s1.length());
            if(!s2.startsWith("\""))
            {
                continue;
            }
            s2 = s2.substring(1);
            if(s2.endsWith("\""))
            {
                s2 = s2.substring(0, s2.length() - 1);
            }
        }

        return s2;
    }

    void getParameters(String as[])
    {
        String s = getParameter("title", as);
        if(s != null)
        {
            properties.put("title", s);
        }
        s = getParameter("subtitle", as);
        if(s != null)
        {
            properties.put("subtitle", s);
        }
        s = getParameter("startTime", as);
        if(s != null)
        {
            properties.put("startTime", s);
        }
        s = getParameter("endTime", as);
        if(s != null)
        {
            properties.put("endTime", s);
        }
        s = getParameter("timeOffset", as);
        if(s != null)
        {
            properties.put("timeOffset", s);
        }
        s = getParameter("vertMax", as);
        if(s != null)
        {
            properties.put("vertMax", s);
        }
        s = getParameter("propertyName", as);
        if(s != null)
        {
            properties.put("propertyName", s);
        }
        int i = 1;
        do
        {
            boolean flag = false;
            String s1 = getParameter("data" + i, as);
            if(s1 != null)
            {
                properties.put("data" + i, s1);
                flag = true;
            }
            s1 = getParameter("maxdata" + i, as);
            if(s1 != null)
            {
                properties.put("maxdata" + i, s1);
                flag = true;
            }
            s1 = getParameter("name" + i, as);
            if(s1 != null)
            {
                properties.put("name" + i, s1);
                flag = true;
            }
            s1 = getParameter("average" + i, as);
            if(s1 != null)
            {
                properties.put("average" + i, s1);
                flag = true;
            }
            s1 = getParameter("maximum" + i, as);
            if(s1 != null)
            {
                properties.put("maximum" + i, s1);
                flag = true;
            }
            s1 = getParameter("averagedSamples" + i, as);
            if(s1 != null)
            {
                properties.put("averagedSamples" + i, s1);
                flag = true;
            }
            if(flag)
            {
                i++;
            } else
            {
                return;
            }
        } while(true);
    }

    public void init()
    {
        if(!m_fStandAlone)
        {
            getParameters(null);
        }
        java.awt.Dimension dimension = size();
        imageWidth = dimension.width;
        imageHeight = dimension.height;
        createChart();
    }

    public void createChart()
    {
        COM.dragonflow.Chart.DrawerAWT drawerawt = new DrawerAWT(this, imageWidth, imageHeight);
        new AppletLineChart(drawerawt, properties);
        offscreen = drawerawt.getImage();
    }

    public void start()
    {
        if(m_Graph == null)
        {
            m_Graph = new Thread(this);
            m_Graph.start();
        }
    }

    public void run()
    {
        do
        {
            long l = 60000L;
            try
            {
                java.lang.Thread.sleep(l);
            }
            catch(java.lang.InterruptedException interruptedexception)
            {
                stop();
            }
        } while(true);
    }

    public void stop()
    {
        if(m_Graph != null)
        {
            m_Graph.stop();
            m_Graph = null;
        }
    }

    public void paint(java.awt.Graphics g)
    {
        g.drawImage(offscreen, 0, 0, this);
    }

    public static void main(String args[])
    {
        java.awt.Frame frame = new Frame("Graph");
        frame.show();
        frame.hide();
        frame.resize(frame.insets().left + frame.insets().right + 640, frame.insets().top + frame.insets().bottom + 480);
        COM.dragonflow.Chart.Graph graph = new Graph();
        frame.add("Center", graph);
        graph.m_fStandAlone = true;
        graph.getParameters(args);
        graph.init();
        graph.start();
        frame.show();
    }
}
