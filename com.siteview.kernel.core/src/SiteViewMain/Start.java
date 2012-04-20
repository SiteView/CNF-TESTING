package SiteViewMain;
import java.io.IOException;

import COM.dragonflow.Api.ApiRmiServer;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.Platform;
import SiteViewMain.SiteViewSupport;
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
