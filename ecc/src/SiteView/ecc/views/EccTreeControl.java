package SiteView.ecc.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import SiteView.ecc.editors.EccControl;
import SiteView.ecc.editors.EccControlInput;


public class EccTreeControl extends ViewPart {

	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	@Override
	public void createPartControl(Composite cp) {
		// TODO Auto-generated method stub
		cp.setLayout(new FillLayout());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
