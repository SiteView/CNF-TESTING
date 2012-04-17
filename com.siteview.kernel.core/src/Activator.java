import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.Platform;
import SiteViewMain.SiteViewSupport;


public class Activator implements BundleActivator {  

	public void start(BundleContext context)throws Exception {  
        try
        {
            SiteViewSupport.InitProcess();
            SiteViewSupport.InitProcess2();
            SiteViewSupport.StartProcess();/*∆Ù∂Øº‡≤‚œﬂ≥Ã*/

            SiteViewSupport.WaitForProcess();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            LogManager.log("Error", Platform.productName + " unexpected shutdown: " + exception);
        }
	}  
	
	public void stop(BundleContext context)throws Exception {  
		SiteViewSupport.ShutdownProcess();
	}  
}