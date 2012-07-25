package SiteView.ecc.dialog;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class BatchAddMachine extends Dialog {
	Button btnPing;
	Button btnCheckButton;
	Button btnMemory;
	public BatchAddMachine(Shell Shell) {
		super(Shell);
	}
	/*
	 * 初始化配置
	 */
	protected void configureShell(Shell newShell) {
		newShell.setSize(300,300);
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		newShell.setLocation(250,150);
		newShell.setText("批量添加监测器");
		super.configureShell(newShell);
	}
	protected Control createDialogArea(Composite parent) {
		Composite composite=(Composite)super.createDialogArea(parent);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		composite.setLayout(null);
		Label lblNewLabel = new Label(composite, SWT.HIDE_SELECTION);
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel.setSize(444, 16);
		lblNewLabel.setLocation(10, 10);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel.setText("\u76D1\u6D4B\u5668ping\u6307\u5B9A\u670D\u52A1\u72B6\u51B5");
		
		btnPing = new Button(composite, SWT.CHECK);
		btnPing.setLocation(20, 32);
		btnPing.setSize(204, 32);
		btnPing.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		btnPing.setText("Ping");
		btnPing.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel_1.setLocation(10, 74);
		lblNewLabel_1.setSize(249, 16);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel_1.setText("\u68C0\u6D4BCPU\u7684\u4F7F\u7528\u7387");
		
		btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.setLocation(20, 96);
		btnCheckButton.setSize(57, 34);
		btnCheckButton.setText("CPU");
		btnCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel_2.setLocation(10, 148);
		lblNewLabel_2.setSize(286, 16);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		lblNewLabel_2.setText("\u76D1\u6D4B\u7CFB\u7EDF\u5185\u5B58\u7684\u4F7F\u7528\u7387\u53CA\u5269\u4F59\u53EF\u7528\u5185\u5B58\u7684\u5927\u5C0F");
		
		btnMemory = new Button(composite, SWT.CHECK);
		btnMemory.setLocation(21, 170);
		btnMemory.setSize(82, 32);
		btnMemory.setText("Memory");
		btnMemory.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		return composite;
	}
	
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
		}
		super.buttonPressed(buttonId);
	}
}
