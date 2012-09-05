package SiteView.ecc.Action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.view.EccTreeControl;
import Siteview.LegalUtils;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import core.busobmaint.BusObMaintView;
import core.busobmaint.BusObNewInput;

public class UnixAction extends Action {
	public UnixAction() {
		setText("Linux…Ë±∏");
	}

	public void run() {
		GroupModle group = (GroupModle) EccTreeControl.item;
		BusinessObject groupbo = group.getBo();
		String groupId = groupbo.get_RecId();
		Siteview.Api.BusinessObject busOb = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().Create("RemoteMachine.RemoteUnix");
		busOb.GetField("Groups").SetValue(new SiteviewValue(groupId));
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new BusObNewInput(
									ConnectionBroker.get_SiteviewApi(),"RemoteMachine.RemoteUnix", busOb),BusObMaintView.ID);
		} catch (PartInitException e1) {
			MessageDialog.openError(null, LegalUtils.get_MessageBoxCaption(),
					e1.getMessage());
			e1.printStackTrace();
		}
		// BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(),
		// "RemoteMachine.RemoteUnix");
	}
}
