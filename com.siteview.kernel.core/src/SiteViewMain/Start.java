package SiteViewMain;
import java.io.IOException;

import com.dragonflow.Api.ApiRmiServer;
import com.dragonflow.Log.LogManager;
import com.dragonflow.SiteView.Platform;


public class Start {
	 
	public Start()
	 {
	 }
	public static void main(String args[])
		    throws IOException
		    {
		        SiteViewSupport.argv = args;
		        try
		        {
		            SiteViewSupport.InitProcess();
		            SiteViewSupport.InitProcess2();
		            SiteViewSupport.StartProcess();/*∆Ù∂Øº‡≤‚œﬂ≥Ã*/
		            ApiRmiServer server = new ApiRmiServer();
		            SiteViewSupport.WaitForProcess();
		        }
		        catch(Exception exception)
		        {
		            exception.printStackTrace();
		            LogManager.log("Error", Platform.productName + " unexpected shutdown: " + exception);
		        }
		        SiteViewSupport.ShutdownProcess();
		    }

}
