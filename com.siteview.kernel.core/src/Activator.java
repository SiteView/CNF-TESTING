import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.dragonflow.Log.LogManager;
import com.dragonflow.SiteView.Platform;
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