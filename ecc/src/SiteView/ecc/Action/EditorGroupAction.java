package SiteView.ecc.Action;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;

import core.busobmaint.BusObMaintView;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class EditorGroupAction extends Action {

	public EditorGroupAction(){
		URL url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/EditorGroup.bmp");
			ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		    setImageDescriptor(temp);
			setText("±à¼­×é");
	}
	public void run(){
		GroupModle group=(GroupModle) EccTreeControl.item;
		BusinessObject bo=group.getBo();
		BusObMaintView.open(ConnectionBroker.get_SiteviewApi(), bo);
	}	
}
