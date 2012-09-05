package SiteView.ecc.Action;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class DeleteGroupAction extends Action {

	public DeleteGroupAction(){
		URL  url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),"Image/Delete.bmp");
		ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		setImageDescriptor(temp);
		setText("É¾³ý×é");
	}
	public void run(){
		GroupModle group=(GroupModle) EccTreeControl.item;
		removeGroup(group);
		BusinessObject bo=group.getBo();
		if(bo.GetField("ParentGroupId").get_NativeValue().toString()==null||bo.GetField("ParentGroupId").get_NativeValue().toString().equals("")){
			List <SiteViewEcc> s=(List<SiteViewEcc>) EccTreeControl.treeViewer.getInput();
			SiteViewEcc site=s.get(0);
			List list=site.getList();
			list.remove(group);
			site.setList(list);
			s=new ArrayList<SiteViewEcc>();
			s.add(site);
			EccTreeControl.treeViewer.setInput(s);
		}
		EccTreeControl.treeViewer.refresh();
		bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
	
	}	
	
	public void removeGroup(GroupModle group){
		List<GroupModle> list=group.getGroups();
		for(GroupModle g:list){
			removeGroup(g);
			g.getBo().DeleteObject(ConnectionBroker.get_SiteviewApi());
		}
		List<MachineModle> machine=group.getMachines();
		for(MachineModle g:machine){
			g.getBo().DeleteObject(ConnectionBroker.get_SiteviewApi());
		}
	}
}
