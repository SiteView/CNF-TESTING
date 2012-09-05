package SiteView.ecc.Control;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import siteview.windows.forms.ImageHelper;
import SiteView.ecc.Activator;
import SiteView.ecc.Modle.UserModle;

public class UserManagerLabelProvider  extends LabelProvider implements ITableLabelProvider {
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		if(columnIndex==4){
			return ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/edit.jpg");
		}else if(columnIndex==5){
			if(((UserModle)element).getUserType().equals("管理员用户")){
				return ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/primission_jcgl.jpg");
			}
			return ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/primission.jpg");
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof UserModle){
			UserModle te=(UserModle) element;
			if (columnIndex==0) {
				return te.getUsername();
			}else if(columnIndex==1){
				return te.getLogname();
			}else if(columnIndex==2){
				return te.getStatus();
			}else if(columnIndex==3){
				return te.getUserType();
			}
		}
		return null;
	}
}
