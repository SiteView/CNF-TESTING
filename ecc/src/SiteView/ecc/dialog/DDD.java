package SiteView.ecc.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DDD extends Dialog {

	public DDD(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("dialog name");
		newShell.setLocation(0,0);
		newShell.setSize(800,600);
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);
		
		return c;
	}
	
	@Override
	protected void initializeBounds() {
		super.getButton(IDialogConstants.OK_ID).setText("asdsada");
		//super.createButton(this, id, label, defaultButton)
	}
	
	@Override
	protected void okPressed() {
		
		super.okPressed();
	}
	
//	@Override
//	protected void buttonPressed(int buttonId) {
//		IDialogConstants.CANCEL_ID
//		super.buttonPressed(buttonId);
//	}
}
