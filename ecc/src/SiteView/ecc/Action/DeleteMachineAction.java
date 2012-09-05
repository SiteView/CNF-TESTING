package SiteView.ecc.Action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.internal.util.BundleUtility;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class DeleteMachineAction extends Action {
	
	
	
	public DeleteMachineAction() {
			URL  url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),"Image/Delete.bmp");
			ImageDescriptor temp = ImageDescriptor.createFromURL(url);
			setImageDescriptor(temp);
			setText("删除设备");
	}

	public void run() {
		MachineModle machine=(MachineModle) EccTreeControl.item;
		BusinessObject bo =machine.getBo();
		String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
		GroupModle parentGroup=SiteViewData.subgroups.get(groupId);
		List machines=parentGroup.getMachines();
		machines.remove(machine);
		SiteViewData.subgroups.put(groupId, parentGroup);
		EccTreeControl.treeViewer.remove(machine);
		EccTreeControl.treeViewer.update(parentGroup, new String[]{"machines"});
		EccTreeControl.treeViewer.refresh();
		if (bo != null) {
			bo.DeleteObject(ConnectionBroker.get_SiteviewApi());//删除数据库数据
		}
	}
	
	//移除相应数据以便刷新界面上的树形图
//	public void removeMachine(List<GroupModle> groups,String groupId){
//		IStructuredSelection selection = (IStructuredSelection) EccTreeControl.treeViewer.getSelection();
//		Object element = selection.getFirstElement();
//		GroupModle gm=null;
//		for(int i=0;i<groups.size();i++){
//			gm = groups.get(i);
//			if(groupId.equals(gm.getBo().get_RecId())){
//				break;				
//			}
//			else{
//				List<GroupModle> list = gm.getGroups();
//				if(list.size()!=0){					
//					removeMachine(list, groupId);
//				}
//			}
//		}
//		List<MachineModle> mm = gm.getMachines();
//		mm.remove(element);
//	}
}
