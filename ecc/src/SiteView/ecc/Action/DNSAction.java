package SiteView.ecc.Action;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
import SiteView.ecc.view.EccTreeControl;
import Siteview.LegalUtils;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import core.busobmaint.BusObMaintView;
import core.busobmaint.BusObNewInput;

public class DNSAction extends Action {
public DNSAction(String name){
	this.setText(name);
}
public void run(){
	MachineModle machine = (MachineModle) EccTreeControl.item;
	BusinessObject machinebo = machine.getBo();
	String groupId = machinebo.GetField("Groups").get_NativeValue().toString();
	Siteview.Api.BusinessObject busOb = ConnectionBroker.get_SiteviewApi()
			.get_BusObService().Create(this.getText());
	busOb.GetField("Groups").SetValue(new SiteviewValue(groupId));
	String filePath = FileTools.getRealPath("\\files\\HostName.properties");
	String s = Config.getReturnStr(filePath, this.getText());
	if(s!=null){
		busOb.GetField(s).SetValue(new SiteviewValue(machinebo.GetField("ServerAddress").get_NativeValue().toString()));
	}
	try {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new BusObNewInput(
								ConnectionBroker.get_SiteviewApi(),this.getText(), busOb),BusObMaintView.ID);
	} catch (PartInitException e1) {
		MessageDialog.openError(null, LegalUtils.get_MessageBoxCaption(),
				e1.getMessage());
		e1.printStackTrace();
	}
	//BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(),this.getText());
}
}