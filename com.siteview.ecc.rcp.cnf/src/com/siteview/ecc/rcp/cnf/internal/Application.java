package com.siteview.ecc.rcp.cnf.internal;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication
{

	public static final String PLUGIN_ID = "com.siteview.ecc";
    public Object start(IApplicationContext context)
    {
     
    	//create and register a backend server
    	//TODO: this info should be saved, not hard coded.
		try {
			MonitorServer server = new MonitorServer();
			server.connectAndLogin();
			MonitorServerManager.getInstance().registerServer(server);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Display display = PlatformUI.createDisplay();
        try
        {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            

            if (returnCode == PlatformUI.RETURN_RESTART)
            {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally
        {
            display.dispose();
        }
    }

    public void stop()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run()
            {
                if (!display.isDisposed())
                    workbench.close();
            }
        });
    }
}
