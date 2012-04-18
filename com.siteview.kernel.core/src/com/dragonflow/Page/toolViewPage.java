/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;


// Referenced classes of package com.dragonflow.Page:
// monitorPage

public class toolViewPage extends com.dragonflow.Page.monitorPage
{

    public toolViewPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("step");
        outputStream.println("<html> This is a test page<br>");
        outputStream.println("step=" + s);
        outputStream.println("</html>");
    }

    public static void main(String args[])
    {
    }
}
