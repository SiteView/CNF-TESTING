package SiteView.ecc.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.TableViewer;


import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import SiteView.ecc.Control.UserManagerContentProvider;
import SiteView.ecc.Control.UserManagerLabelProvider;
import SiteView.ecc.Modle.UserModle;
import SiteView.ecc.data.UserInfor;
import SiteView.ecc.dialog.AddUserDig;
import SiteView.ecc.dialog.TaxAuthority;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Xml.Scope;
import adminloader.forms.security.UserManagerDlg;

public class UserManager extends EditorPart {
	public UserManager() {
	}
	public static final String ID = "SiteView.ecc.editors.UserManager";

	public static TableViewer TableViewer;
	public Table table;
	public static TableItem tableItem;


	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);// ����site
		this.setInput(input);// ���������IEditorInput����
		this.setPartName(input.getName());// ���ñ༭���Ϸ���ʾ������

	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);

		Composite composite = new Composite(sashForm, SWT.NONE);

		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm_1 = new SashForm(composite, SWT.VERTICAL);
		sashForm_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_FOREGROUND));

		Composite composite_2 = new Composite(sashForm_1, SWT.NONE);
		composite_2.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_FOREGROUND));

		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddUserDig u=new AddUserDig(null);
				u.open();
			}
		});
		btnNewButton.setBounds(0, 5, 50, 20);
		btnNewButton.setText("\u6DFB\u52A0");

		Button button = new Button(composite_2, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserModle u=(UserModle) tableItem.getData();
				UserInfor.usersid.remove(u.getLogname());
				UserInfor.list.remove(u);
				ConnectionBroker.get_SiteviewApi().get_AuthenticationService().DeleteUser(u.getUsers(), true);
				ConnectionBroker.get_SiteviewApi().get_SettingsService().Delete(Scope.User,u.getUsers().get_OriginalLoginId());
				TableViewer.setInput(UserInfor.list);
				TableViewer.refresh();
			}
		});
		button.setBounds(55, 5, 50, 20);
		button.setText("\u5220\u9664");
		Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btnNewButton_1.setBounds(110, 5, 50, 20);
		btnNewButton_1.setText("\u5141\u8BB8");

		Button btnNewButton_2 = new Button(composite_2, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btnNewButton_2.setBounds(165, 5, 50, 20);
		btnNewButton_2.setText("\u7981\u6B62");

		Button btnNewButton_3 = new Button(composite_2, SWT.NONE);
		btnNewButton_3.setBounds(220, 5, 50, 20);
		btnNewButton_3.setText("\u5237\u65B0");

		Button btnNewButton_4 = new Button(composite_2, SWT.NONE);
		btnNewButton_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableViewer.refresh();
			}
		});
		btnNewButton_4.setBounds(275, 5, 50, 20);
		btnNewButton_4.setText("\u5E2E\u52A9");

		Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
		lblNewLabel_1.setBounds(0, 27, 1000, 12);
		lblNewLabel_1.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1
				.setText("\u7528\u6237\u7BA1\u7406\u8BE6\u7EC6\u4FE1\u606F");

		Composite composite_3 = new Composite(sashForm_1, SWT.BORDER);
		composite_3.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_FOREGROUND));
		composite_3.setLayout(new FillLayout());

		 TableViewer =  new TableViewer(composite_3, SWT.MULTI
				 |SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.CHECK);

		table = TableViewer.getTable();
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				tableItem=(TableItem) e.item;
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		table.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
				if(e.x>509&&e.x<610){
					AddUserDig edit=new AddUserDig(null);
					int i= table.getSelectionIndex();
					edit.setUser(((UserModle)tableItem.getData()).getUsers());
					edit.open();
				}else if(e.x>610 && e.x<710){
					if(((UserModle)tableItem.getData()).getUserType().equals("����Ա�û�")){
						return;
					}
					TaxAuthority taxAuthority=new TaxAuthority(null);
					taxAuthority.open();
				}
			}
			public void mouseDown(MouseEvent e) {
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		TableColumn columUserName = new TableColumn(table, SWT.NONE );
		columUserName.setText("�û���");
		columUserName.setWidth(148);

		TableColumn columLoginName = new TableColumn(table, SWT.NONE);
		columLoginName.setText("��½��");
		columLoginName.setWidth(121);

		TableColumn columState = new TableColumn(table, SWT.NONE);
		columState.setText("״̬");
		columState.setWidth(120);

		TableColumn columUserType = new TableColumn(table, SWT.NONE);
		columUserType.setText("�û�����");
		columUserType.setWidth(117);

		TableColumn columEditor = new TableColumn(table, SWT.COMMAND);
		columEditor.setText("�༭");
		columEditor.setWidth(107);

		TableColumn columAccredit = new TableColumn(table, SWT.NONE);
		columAccredit.setText("��Ȩ");
		columAccredit.setWidth(100);
		
		sashForm_1.setWeights(new int[] {30, 167});
		

		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		sashForm.setWeights(new int[] { 200, 271 });
		
		TableViewer.setContentProvider(new UserManagerContentProvider());
		TableViewer.setLabelProvider(new UserManagerLabelProvider());
		TableViewer.setInput(UserInfor.getUserInfor());
		tableItem=table.getItem(0);
	}

	public void setFocus() {

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}
}
