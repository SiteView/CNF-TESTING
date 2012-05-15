package SiteView.ecc.views;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import SiteView.ecc.editors.EccControl;
import SiteView.ecc.editors.EccControlInput;


public class EccTreeControl extends ViewPart {
	public EccTreeControl() {
	}

	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	@Override
	public void createPartControl(Composite cp) {
		// TODO Auto-generated method stub
		cp.setLayout(new FillLayout());
		
		EccControlInput input = new EccControlInput();
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, EccControl.ID);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
