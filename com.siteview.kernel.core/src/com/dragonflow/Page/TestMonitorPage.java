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

import com.dragonflow.Api.APIMonitor;
import com.dragonflow.Api.APISiteView;
import com.dragonflow.Api.SSInstanceProperty;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class TestMonitorPage extends com.dragonflow.Page.CGI
{

    public TestMonitorPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
    	APIMonitor api = new APIMonitor();
    	SSInstanceProperty property[] = null;
    	outputStream.println("hell world");
//    	property[0] = new SSInstanceProperty("_hostname", "www.siteview.com");
//    	property[1] = new SSInstanceProperty("_name", "ping www.siteview.com");
//
//    	api.create("PingMonitor","Group0",property);
    	
    	property = api.getInstanceProperties("18", "Group0", APISiteView.FILTER_CONFIGURATION_ALL);
    	outputStream.println("hell world");
    }

}
