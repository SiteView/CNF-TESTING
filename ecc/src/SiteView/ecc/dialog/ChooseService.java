package SiteView.ecc.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;

public class ChooseService extends Dialog {
	private String[] service;
	
	private String choose;
	
	private BusinessObject bo;

	public ChooseService(Shell parentShell,String[] service,BusinessObject bo) {
		super(parentShell);
		this.service=service;
		this.bo=bo;
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(300,400);
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		newShell.setLocation(400,250);
		newShell.setText("服务信息");
		super.configureShell(newShell);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		ScrolledComposite group_1 = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayout grid=new GridLayout();
		grid.numColumns=1;
		group_1.setExpandHorizontal(true);
		group_1.setExpandVertical(true);
		Composite com=new Composite(group_1, SWT.NONE);
		com.setLayout(grid);
		group_1.setContent(com);
		for(String s:service){
			if(s==null){
				continue;
			}
			final Button button = new Button(com, SWT.RADIO);
			button.setText(s);
			button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button.isEnabled()){
					choose=button.getText();
				}
				super.widgetSelected(e);
			}
			});
		}
		group_1.setMinSize(com.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return composite;
	}
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			bo.GetField("SerServer").SetValue(new SiteviewValue(choose));
		}
		super.buttonPressed(buttonId);
	}
}
