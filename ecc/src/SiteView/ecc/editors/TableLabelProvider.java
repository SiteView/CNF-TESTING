package SiteView.ecc.editors;

import java.text.SimpleDateFormat;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import siteview.windows.forms.ImageHelper;

import SiteView.ecc.Activator;
import SiteView.ecc.data.MonitorInfo;

public class TableLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 0) {
			return ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/state_red_hover.gif");
		}else if (columnIndex == 1) {
			return ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/refreshtab.gif");
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		if (element instanceof MonitorInfo) {
			MonitorInfo m = (MonitorInfo) element;
			if (columnIndex==0) {
				return m.getStatus();
			}else if(columnIndex==1){
				return m.getRefresh();
			}else if(columnIndex==2){
				return m.getMonitorname();
			}else if(columnIndex==3){
				return m.getDesc();
			}else if(columnIndex==4){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.format(m.getLastupdateDate());
			}
		}
		return null;
	}

}
