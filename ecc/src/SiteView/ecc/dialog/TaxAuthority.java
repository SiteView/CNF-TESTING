package SiteView.ecc.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import SiteView.ecc.view.EccTreeControl;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TaxAuthority extends Dialog{
	private String title="用户授权";
	public TaxAuthority(Shell parent) {
		super(parent);
	}
	protected void configureShell(Shell newShell) {
		newShell.setSize(500,500);
		newShell.setLocation(250,150);
		newShell.setText(title);
		super.configureShell(newShell);
	}
	protected Control createDialogArea(Composite parent) {
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		
		SashForm sashForm = new SashForm(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(sashForm, SWT.VERTICAL);
		composite_1.setLayout(new FillLayout());
		
		SashForm sashForm_1 = new SashForm(composite_1, SWT.VERTICAL);
		
		Composite composite_3 = new Composite(sashForm_1, SWT.NONE);
		
		Button btnCheckButton = new Button(composite_3, SWT.CHECK);
		btnCheckButton.setBounds(0, 0, 51, 20);
		btnCheckButton.setText("\u5168\u9009");//全选
		
		Combo combo = new Combo(composite_3, SWT.NONE);
		combo.add(" ");
		combo.setBounds(112, 0, 92, 20);
		
		Label label = new Label(composite_3, SWT.NONE);
		label.setBounds(70, 4, 36, 12);
		label.setText("\u7528\u6237\uFF1A");
		
		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		sashForm_1.setWeights(new int[] {1, 10});
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(220);
		scrolledComposite.setMinHeight(500);
		
		//ViewPermissions
		
		SashForm sashForm_2=new SashForm(scrolledComposite, SWT.VERTICAL);
		
		Composite composite_2 = new Composite(sashForm_2, SWT.BORDER);
		composite_2.setToolTipText("SE权限");
		composite_2.setLayout(new FormLayout());
		
		Button btnCheckButton_1 = new Button(composite_2, SWT.CHECK);
		FormData fd_btnCheckButton_1 = new FormData();
		fd_btnCheckButton_1.top = new FormAttachment(0, 10);
		fd_btnCheckButton_1.left = new FormAttachment(0, 10);
		btnCheckButton_1.setLayoutData(fd_btnCheckButton_1);
		btnCheckButton_1.setText("\u6DFB\u52A0\u7EC4");
		
		Composite composite_5 = new Composite(sashForm_2, SWT.BORDER);
		composite_5.setToolTipText("组权限");
		
		Button button = new Button(composite_5, SWT.CHECK);
		button.setBounds(10, 10, 93, 16);
		button.setText("\u6DFB\u52A0\u5B50\u7EC4");
		
		Button btnCheckButton_2 = new Button(composite_5, SWT.CHECK);
		btnCheckButton_2.setBounds(10, 40, 93, 16);
		btnCheckButton_2.setText("\u6DFB\u52A0\u8BBE\u5907");
		
		Button btnCheckButton_3 = new Button(composite_5, SWT.CHECK);
		btnCheckButton_3.setBounds(10, 69, 93, 16);
		btnCheckButton_3.setText("\u7F16\u8F91\u7EC4");
		
		Button button_1 = new Button(composite_5, SWT.CHECK);
		button_1.setBounds(10, 101, 93, 16);
		button_1.setText("\u5220\u9664\u7EC4");
		
		Button button_2 = new Button(composite_5, SWT.CHECK);
		button_2.setBounds(10, 131, 93, 16);
		button_2.setText("\u6DFB\u52A0\u76D1\u6D4B\u5668");
		
		Composite composite_6 = new Composite(sashForm_2, SWT.BORDER);
		
		Button button_3 = new Button(composite_6, SWT.CHECK);
		button_3.setBounds(10, 10, 93, 16);
		button_3.setText("\u6DFB\u52A0\u76D1\u6D4B\u5668");
		
		Button button_4 = new Button(composite_6, SWT.CHECK);
		button_4.setBounds(10, 40, 93, 16);
		button_4.setText("\u6DFB\u52A0\u8BBE\u5907");
		
		Button button_5 = new Button(composite_6, SWT.CHECK);
		button_5.setBounds(10, 69, 93, 16);
		button_5.setText("\u7F16\u8F91\u8BBE\u5907");
		
		Button button_6 = new Button(composite_6, SWT.CHECK);
		button_6.setBounds(10, 101, 93, 16);
		button_6.setText("\u5220\u9664\u8BBE\u5907");
		
		Composite composite_7 = new Composite(sashForm_2, SWT.BORDER);
		composite_7.setToolTipText("监测器权限");
		
		Button button_7 = new Button(composite_7, SWT.CHECK);
		button_7.setBounds(10, 10, 93, 16);
		button_7.setText("\u7F16\u8F91\u76D1\u6D4B\u5668");
		
		Button button_8 = new Button(composite_7, SWT.CHECK);
		button_8.setBounds(10, 44, 93, 16);
		button_8.setText("\u5220\u9664\u76D1\u6D4B\u5668");
		
		Button button_9 = new Button(composite_7, SWT.CHECK);
		button_9.setBounds(10, 77, 93, 16);
		button_9.setText("\u5237\u65B0\u76D1\u6D4B\u5668");
		sashForm_2.setWeights(new int[] {26, 163, 117, 185});
		
		scrolledComposite.setContent(sashForm_2);
		
		
		sashForm.setWeights(new int[] {1, 1});
		return composite;
	}
	 protected Control createButtonBar(Composite parent) {
         Control btnBar = super.createButtonBar(parent);
         getButton(IDialogConstants.CANCEL_ID).setText("授予勾选功能");
         getButton(IDialogConstants.OK_ID).setVisible(false);
         return btnBar;
     }
}
