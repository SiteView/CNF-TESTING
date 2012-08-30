package SiteView.ecc.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;

public class ChooseDiskSpace extends Dialog {
	private String[] disk;
	
	private String choose;
	
	private BusinessObject bo;

	public ChooseDiskSpace(Shell parentShell,String[] disk,BusinessObject bo) {
		super(parentShell);
		this.disk=disk;
		this.bo=bo;
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(300,200);
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		newShell.setLocation(300,150);
		newShell.setText("¥≈≈Ã–≈œ¢");
		super.configureShell(newShell);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setLayout(null);
		int i=0;
		for(String s:disk){
			final Button b=new Button(composite, SWT.RADIO);
			b.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(b.isEnabled()){
						choose=b.getText();
					}
					super.widgetSelected(e);
				}
			});
			b.setText(s);
			b.setBounds(130, (40*i)+10, 100, 20);
			i++;
		}
		return composite;
	}
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			bo.GetField("DiskName").SetValue(new SiteviewValue(choose));
		}
		super.buttonPressed(buttonId);
	}

}
