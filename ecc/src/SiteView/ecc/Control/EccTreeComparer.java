package SiteView.ecc.Control;


import org.eclipse.jface.viewers.IElementComparer;

import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.Modle.SiteViewEcc;

public class EccTreeComparer implements IElementComparer {

	@Override
	public boolean equals(Object a, Object b) {
		// TODO Auto-generated method stub
		if(a!=null&&b!=null){
			if(a instanceof GroupModle && b instanceof GroupModle){
				GroupModle a1=(GroupModle) a;
				GroupModle b1=(GroupModle) b;
				return a1.equals(b1);
			}else if(a instanceof MachineModle && b instanceof MachineModle){
				MachineModle a1=(MachineModle) a;
				MachineModle b1=(MachineModle) b;
				return a1.equals(b1);
			}else if(a instanceof SiteViewEcc && b instanceof SiteViewEcc){
				SiteViewEcc a1=(SiteViewEcc) a;
				SiteViewEcc b1=(SiteViewEcc) b;
				return a1.equals(b1);
			}
		}
		return false;
	}

	@Override
	public int hashCode(Object element) {
		// TODO Auto-generated method stub
		if(element !=null && element instanceof MachineModle){
			MachineModle a=(MachineModle) element;
			return a.hashCode();
		}else if(element !=null && element instanceof GroupModle){
			GroupModle a=(GroupModle) element;
			return a.hashCode();
		}else if(element !=null && element instanceof SiteViewEcc){
			SiteViewEcc a=(SiteViewEcc) element;
			return a.hashCode();
		}
		return 0;
	}

}
