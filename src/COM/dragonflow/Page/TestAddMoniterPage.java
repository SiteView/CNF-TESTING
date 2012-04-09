/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import COM.dragonflow.Api.APIMonitor;
import COM.dragonflow.Api.SSInstanceProperty;
import COM.dragonflow.SiteViewException.SiteViewException;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class TestAddMoniterPage extends COM.dragonflow.Page.CGI {
	static COM.dragonflow.Api.APIMonitor apimonitor = new APIMonitor();
	public TestAddMoniterPage() {
	}

	public void printBody() throws java.lang.Exception {
		printBodyHeader("Hello");
		outputStream
				.println("<p>\n<CENTER><H2>hello</H2></CENTER><P>\n<p>\n"
						+ "<a href=http://localhost:9999/SiteView>"
						+ "<input type=\"button\" value=\"hello\" class=\"VerBl8\">\n"
						+"</a>"
						+ "</FORM>\n");
		createMonitor();
	}

	public static void main(String args[]) throws java.io.IOException {
		COM.dragonflow.Page.TestAddMoniterPage ftppage = new TestAddMoniterPage();
		if (args.length > 0) {
			ftppage.args = args;
		}
		ftppage.handleRequest();
	}
	public static void createMonitor() throws SiteViewException{
		SSInstanceProperty[] assinstanceproperty = new SSInstanceProperty[10];
		
		assinstanceproperty[0] = new SSInstanceProperty("_host", "192.168.0.248");
	    assinstanceproperty[1] = new SSInstanceProperty("_oidIndex", "0");
	    assinstanceproperty[2] = new SSInstanceProperty("_timeout", "5");
	    assinstanceproperty[3] = new SSInstanceProperty("_snmpversion", "v2");
	    assinstanceproperty[4] = new SSInstanceProperty("_retryDelay", "1");
	    assinstanceproperty[5] = new SSInstanceProperty("_community", "dragon");
	    assinstanceproperty[6] = new SSInstanceProperty("_frequency", "600");
	    assinstanceproperty[7] = new SSInstanceProperty("error-condition0", "default");
	    assinstanceproperty[8] = new SSInstanceProperty("warning-condition0", "default");
	    assinstanceproperty[9] = new SSInstanceProperty("good-condition0", "default");
        
		COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apimonitor.create("SNMPMonitor", "Group0", assinstanceproperty);
		
		
		
		
//		
//		
//		
//		
//		
//		
//		
//		SSInstanceProperty[] assinstanceproperty = new SSInstanceProperty[15];
//		
//		assinstanceproperty[0] = new SSInstanceProperty("_nbmonitorVersion", "-1");
//        assinstanceproperty[1] = new SSInstanceProperty("_nbmonitorVersion", "-1");
//        assinstanceproperty[2] = new SSInstanceProperty("_community", "dragon");
//        assinstanceproperty[3] = new SSInstanceProperty("_community", "dragon");
//        assinstanceproperty[4] = new SSInstanceProperty("_maxRTDataWindow", "24");
//        assinstanceproperty[5] = new SSInstanceProperty("_timeout", "5");
//        assinstanceproperty[6] = new SSInstanceProperty("_timeout", "5");
//        assinstanceproperty[10] = new SSInstanceProperty("_retries", "1");
//        assinstanceproperty[11] = new SSInstanceProperty("_retries", "1");
//        assinstanceproperty[12] = new SSInstanceProperty("_port", "161");
//        assinstanceproperty[13] = new SSInstanceProperty("_port", "161");
//        assinstanceproperty[13] = new SSInstanceProperty("_frequency", "600");
//        assinstanceproperty[7] = new SSInstanceProperty("error-condition0", "default");
//        assinstanceproperty[8] = new SSInstanceProperty("warning-condition0", "default");
//        assinstanceproperty[9] = new SSInstanceProperty("good-condition0", "default");
//        
//		COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apimonitor.create("SNMPMonitor", "Group0", assinstanceproperty);
//		
//		
		
		
		
		
	}
}
