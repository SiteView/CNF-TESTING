package SiteView.ecc.Control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.SetUpModle;
import SiteView.ecc.Modle.SiteViewEcc;

public class EccTreeContentProvider implements ITreeContentProvider {
	private List<Object> siteview;
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		this.siteview= (List<Object>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return siteview.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if(parentElement instanceof SiteViewEcc){
			return ((SiteViewEcc) parentElement).getList().toArray();
		}else if(parentElement instanceof GroupModle) { 	
			GroupModle category = (GroupModle)parentElement;
			List<Object> list=new ArrayList();
			list.addAll(category.getGroups());
			list.addAll(category.getMachines());
			return list.toArray();
	     }else if(parentElement instanceof SetUpModle){
	    	 SetUpModle set=(SetUpModle) parentElement;
	    	 return set.getList().toArray();
	     }
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof SiteViewEcc){
			SiteViewEcc site=(SiteViewEcc) element;
			if(site.getList().size()>0){
				return true;
			}
		}else if(element instanceof GroupModle){
			GroupModle group=(GroupModle) element;
			if(group.getGroups().size()>0||group.getMachines().size()>0){
				return true;
			}
		}else if(element instanceof SetUpModle){
			return true;
		}
		return false;
	} 

}
