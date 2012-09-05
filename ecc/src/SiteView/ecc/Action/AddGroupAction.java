package SiteView.ecc.Action;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;

import core.busobmaint.BusObMaintView;
import core.busobmaint.BusObNewInput;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.view.EccTreeControl;
import Siteview.LegalUtils;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class AddGroupAction extends Action {

	public AddGroupAction() {
		URL url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/AddGroup.bmp");
		ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		setImageDescriptor(temp);
		setText("增加子组");

	}

	public void run() {
		if(EccTreeControl.item instanceof SiteViewEcc){
			BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(),"EccGroup");
		}else{
			BusinessObject groupbo=((GroupModle) EccTreeControl.item).getBo();
			String groupId = groupbo.get_RecId();
			Siteview.Api.BusinessObject busOb = ConnectionBroker.get_SiteviewApi().get_BusObService().Create("EccGroup");
			busOb.GetField("ParentGroupId").SetValue(new SiteviewValue(groupId));
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new BusObNewInput(
										ConnectionBroker.get_SiteviewApi(),this.getText(), busOb),BusObMaintView.ID);
			} catch (PartInitException e1) {
				MessageDialog.openError(null, LegalUtils.get_MessageBoxCaption(),
						e1.getMessage());
				e1.printStackTrace();
			}
		}
	}
}
