package SiteView.ecc.Action;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;

import core.busobmaint.BusObMaintView;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;



public class EditorMachineAction extends Action {

	public EditorMachineAction() { 
		URL url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/EditorMachine.bmp");
			ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		    setImageDescriptor(temp);
			setText("±‡º≠…Ë±∏");
		
	}
	public void run(){
		MachineModle machine=(MachineModle)EccTreeControl.item;
		BusinessObject bo =machine.getBo();
		if(bo !=null){			
			BusObMaintView.open(ConnectionBroker.get_SiteviewApi(), bo);
		}
	}
}
