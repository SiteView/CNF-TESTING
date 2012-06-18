package core.monitor.counter;

import java.util.Map;

import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Presentation.Common.BarDef;
import Siteview.Presentation.Common.ToolbarDef;
import Siteview.Windows.Forms.IPresentationRegionAccess;
import siteview.windows.forms.CommandEventArgs;
import siteview.windows.forms.PresentationRegionExtensionBase;

public class MonitorCounter extends PresentationRegionExtensionBase {

	public MonitorCounter() {
		
	}
	
	@Override
	public Boolean ExecuteCommand(CommandEventArgs e) {
		//Choose monitor counters
		if (e.get_Command().equals("SelectCounter")){
			SelectCounter();
			return true;
		}
		return super.ExecuteCommand(e);
	}

	private void SelectCounter() {
		IPresentationRegionAccess pra = (IPresentationRegionAccess) super.get_PresentationRegionAccess();
//		if (pra.get_PrimaryBusinessObject().get_Definition().get_Name().equals("Ecc.WinMediaServer")){
			CounterDlg dlg = new CounterDlg(null);
//			Siteview.Api.BusinessObjectCollection curCounters = pra.get_Relationship().get_BusinessObjects();

			dlg.open();
			if (dlg.getReturnCode() == CounterDlg.OK){
				Map<String,String> counters = dlg.getSelectedCounters();
				for(String str: counters.keySet()){
					BusinessObject bo = pra.get_Relationship().CreateNewObject(true);
					bo.GetField("Name").SetValue(new SiteviewValue(str));
					bo.GetField("Title").SetValue(new  SiteviewValue(counters.get(str)));
					bo.GetField("MonitorType").SetValue(new  SiteviewValue(pra.get_PrimaryBusinessObject().get_Definition().get_Name()));
//					bo.SaveObject(get_Api(), false,true);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pra.get_Relationship().Refresh(true);
			}
//		}
		
	}

	@Override
	public void CustomizeToolbar(ToolbarDef def) {
		BarDef.SeparatorPreceeding(def.AddCommand("SelectCounter", "[IMAGE]Siteview#Images.Icons.BusinessObject.OpenAttachment.png", "Ñ¡Ôñ¼ÆÊýÆ÷"));
	}

}
