package SiteView.ecc.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;

import SiteView.ecc.editors.UserInfor;
import SiteView.ecc.editors.UserManager;
import Siteview.AuthenticationSource;
import Siteview.IUserInfo;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.SiteviewValue;
import Siteview.UpdateResult;
import Siteview.User;
import Siteview.Api.BusinessObject;
import Siteview.Api.PanelDef;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.MsgBox;
import Siteview.Windows.Forms.SiteviewPanel;
import org.eclipse.wb.swt.SWTResourceManager;

import system.Xml.XmlElement;

public class AddUserDig extends Dialog {
	private User user;
	IUserInfo uinfo ;
	private boolean EditUser=false;
	private boolean m_bPwdChanged=false;
	private String oldLogId="";
	
	private PanelDef panelDef;
	private SiteviewPanel compUserPanel;
	
	private Button chkAllowInternalAuth;
	private TabFolder tabFolder;
	private Text txtLoginId;
	private Text cboSecurityGroup;
	private Combo cboBusUnit;
	private Button chkPersent;
	private Text txtID;
	private Text txtPasswd;
	private Text txtPasswdAgain;
	private Button rdoAuthLocal;
	private Button rdoAuthLDAP;
	private Button applyButton;
	private Button closeButton;
	
	public AddUserDig(Shell parent) {
		super(parent);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		 super.configureShell(newShell);
		 newShell.setSize(400,500);
		 newShell.setLocation(200,100);
		 newShell.setText("用户管理");
	}
	 
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
		String strFirstName = "新建用户";
		 if(user==null){
			 user= ConnectionBroker.get_SiteviewApi().get_AuthenticationService().CreateUser("User", strFirstName); 
		 }else{
			 EditUser=true;
			 uinfo=user.get_InternalUserInfo(); 
			 oldLogId=user.get_LoginId();
		 }
		tabFolder = new TabFolder(container, SWT.NONE);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u5E38\u89C4");
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("\u6269\u5C55");
		
		ScrolledComposite compExtend = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		compExtend.setExpandVertical(true);
		compExtend.setExpandHorizontal(true);
		tabItem_1.setControl(compExtend);
		panelDef = ConnectionBroker.get_SiteviewApi().get_Presentation()
				.GetPanelDef(ConnectionBroker.get_SiteviewApi().get_BusObDefinitions()
						.GetLoginProcedureDef("User").get_BusObName());
		 try {
			 compUserPanel = SiteviewPanel.CreateFromDef(ConnectionBroker.get_SiteviewApi(), panelDef,compExtend);
			 compExtend.setContent(compUserPanel);
			 compExtend.setMinSize(compUserPanel.getSize());
			 compExtend.layout();
			//更新用户扩展信息
			 BusinessObject Ob =GetBusOb(user.get_BusObId());
			if(compUserPanel!=null) {
				if (Ob!=null){
					compUserPanel.set_SuppressValidation(true);
					compUserPanel.SetDataFromBusOb(Ob);
					compUserPanel.set_SuppressValidation(false);
				}
			}
		 }catch (Exception e) {
			compUserPanel = null;
			e.printStackTrace();
		}
		Composite compBase = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(compBase);
		
		Label label = new Label(compBase, SWT.NONE);
		label.setBounds(10, 10, 54, 12);
		label.setText("\u767B\u5F55\u6807\u8BC6\uFF1A");
		label.pack();
		
		txtLoginId = new Text(compBase, SWT.BORDER);
		txtLoginId.setBounds(85, 4, 271, 18);
		txtLoginId.setText(user.get_LoginId());
		
		
		Label label_1 = new Label(compBase, SWT.NONE);
		label_1.setBounds(10, 44, 54, 12);
		label_1.setText("\u5B89\u5168\u7FA4\u7EC4\uFF1A");
		label_1.pack();
		
		cboSecurityGroup = new Text(compBase, SWT.BORDER | SWT.READ_ONLY);
		cboSecurityGroup.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		cboSecurityGroup.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		cboSecurityGroup.setBounds(85, 36, 271, 20);
		if(!EditUser){
			cboSecurityGroup.setText("监测经理");
		}else{
			cboSecurityGroup.setText(user.get_SecurityGroupName());
		}
		cboSecurityGroup.setEnabled(false);	
		Label label_2 = new Label(compBase, SWT.NONE);
		label_2.setBounds(10, 77, 54, 12);
		label_2.setText("\u4E1A\u52A1\u5355\u5143\uFF1A");
		label_2.pack();
		
		cboBusUnit = new Combo(compBase, SWT.READ_ONLY);
		cboBusUnit.setBounds(85, 69, 271, 20);
		
		Label label_3 = new Label(compBase, SWT.NONE);
		label_3.setBounds(10, 113, 54, 12);
		label_3.setText("\u8FFD\u8E2A\u51FA\u5E2D\uFF1A");
		label_3.pack();
		
		chkPersent = new Button(compBase, SWT.CHECK);
		chkPersent.setBounds(85, 111, 54, 16);
		chkPersent.setSelection(user.get_TrackPresence());
		
		TabFolder tabAuthMethod = new TabFolder(compBase, SWT.NONE);
		tabAuthMethod.setBounds(10, 149, 377, 254);
		
		TabItem tabItem1 = new TabItem(tabAuthMethod, SWT.NONE);
		tabItem1.setText("\u5185\u90E8");
		
		Composite compAuthInternal = new Composite(tabAuthMethod, SWT.NONE);
		tabItem1.setControl(compAuthInternal);
		
		chkAllowInternalAuth = new Button(compAuthInternal, SWT.CHECK);
		chkAllowInternalAuth.setBounds(10, 10, 93, 16);
		chkAllowInternalAuth.setText("\u5141\u8BB8\u5185\u90E8\u9A8C\u8BC1");
		chkAllowInternalAuth.setSelection(user.get_AllowInternalAuthentication());
		chkAllowInternalAuth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAllowInternalAuth_changed();
			}
		});
		chkAllowInternalAuth.setBounds(10, 10, 93, 16);
		chkAllowInternalAuth.setText("\u5141\u8BB8\u5185\u90E8\u9A8C\u8BC1");
		
		rdoAuthLocal = new Button(compAuthInternal, SWT.RADIO);
		rdoAuthLocal.setSelection(true);
		rdoAuthLocal.setBounds(10, 45, 93, 16);
		rdoAuthLocal.setText("\u672C\u5730\u8BA4\u8BC1");
		rdoAuthLocal.setSelection(user.get_TrackPresence());
		
		rdoAuthLDAP = new Button(compAuthInternal, SWT.RADIO);
		rdoAuthLDAP.setBounds(171, 45, 93, 16);
		rdoAuthLDAP.setText("LDAP\u8EAB\u4EFD\u9A8C\u8BC1");
		rdoAuthLDAP.setSelection(user.get_UseLDAP());
		rdoAuthLDAP.setEnabled(false);
		
		Group group = new Group(compAuthInternal, SWT.NONE);
		group.setText("\u767B\u5F55\u4FE1\u606F");
		group.setBounds(10, 67, 349, 152);
		
		Label lblId = new Label(group, SWT.NONE);
		lblId.setBounds(10, 28, 30, 12);
		lblId.setText("ID\uFF1A");
		lblId.pack();
		
		txtID = new Text(group, SWT.BORDER);
		txtID.setEditable(false);
		txtID.setBounds(79, 22, 242, 18);
		txtID.setText(user.get_LoginId());
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setBounds(10, 68, 54, 12);
		label_4.setText("\u5BC6\u7801\uFF1A");
		label_4.pack();
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setBounds(10, 107, 54, 12);
		label_5.setText("\u786E\u8BA4\u5BC6\u7801\uFF1A");
		label_5.pack();
		
		txtPasswd = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txtPasswd.setBounds(79, 62, 242, 18);
		String password="******";
		txtPasswd.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				m_bPwdChanged=true;
			}
		});
		if(!EditUser){
			txtPasswd.setEnabled(false);
		}		
		txtPasswd.setText(password);
		
		txtPasswdAgain = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txtPasswdAgain.setBounds(79, 101, 242, 18);
		txtPasswdAgain.setText(password);
		if(!EditUser){
			txtPasswdAgain.setEnabled(false);
		}
		
		txtLoginId.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				String strId = txtLoginId.getText();
				txtID.setText(strId);
				compUserPanel.get_BusinessObject().GetField("LoginID").SetValue(new SiteviewValue(strId));
				try {
					compUserPanel.SetDataFromBusOb(compUserPanel.get_BusinessObject());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}});
		return container;
	}
	
	protected void onAllowInternalAuth_changed() {
		if (chkAllowInternalAuth.getSelection()){
			enableInternalAuthControls(true,true);
			txtID.setText(txtLoginId.getText());
			rdoAuthLocal.setEnabled(true);
			//rdoAuthLDAP.setEnabled(true);
		}else{
			enableInternalAuthControls(false,true);
			rdoAuthLDAP.setEnabled(false);
			txtPasswd.setText("*****");
			txtPasswdAgain.setText("*****");
		}
	}
		
	private void enableInternalAuthControls(boolean bEnable, boolean bClear){
		txtID.setEnabled(chkAllowInternalAuth.getSelection());
		txtPasswd.setEnabled(chkAllowInternalAuth.getSelection());
		txtPasswdAgain.setEnabled(chkAllowInternalAuth.getSelection());
		if (bClear){
			txtID.setText("");
			txtPasswd.setText("");
			txtPasswdAgain.setText("");
		}
		rdoAuthLocal.setEnabled(bEnable);
	}
		@Override
	protected void createButtonsForButtonBar(Composite parent) {
		applyButton = createButton(parent, IDialogConstants.OK_ID, "确认",true);
		closeButton=createButton(parent, IDialogConstants.CANCEL_ID, "关闭", true);
	}
		
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			
			if(EditUser){
				if(!oldLogId.equals(txtLoginId.getText().trim())){
					if(UserInfor.usersid.contains(txtLoginId.getText().trim())){
						 MsgBox.Show(this.getShell(), "登录标识已存在！", "错误");
						 return;
					}
					UserInfor.usersid.remove(oldLogId);
				}else{
					UserInfor.usersid.add(txtLoginId.getText().trim());
				}
				
				UserInfor.list.remove(UserManager.tableItem.getData());
				//UserManager.TableViewer.remove(UserManager.tableItem.getData());
				//如果密码有修改，进行密码验证
				if(m_bPwdChanged){
					try {
						ValidateUserData();
					}catch(Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}else{
				if(UserInfor.usersid.contains(txtLoginId.getText().trim())){
					 MsgBox.Show(this.getShell(), "登录标识已存在！", "错误");
					 return;
				}
				try {
					ValidateUserData();
				}catch(Exception e) {
					e.printStackTrace();
					return;
				}
				UserInfor.usersid.add(txtLoginId.getText().trim());
				user.set_SecurityGroupName("监测经理");
				user.set_SecurityGroupId("0093C762577346CD8F016A47D42097D3");
			}
			user.set_LoginId(txtLoginId.getText().trim());
			user.set_TrackPresence(chkPersent.getSelection());
			user.set_AllowInternalAuthentication(chkAllowInternalAuth.getSelection());
			user.set_UseLDAP(rdoAuthLDAP.getSelection());
			user.set_BusObId(compUserPanel.get_BusinessObject().get_Id());
			if (!rdoAuthLDAP.getSelection()){
				uinfo = user.get_InternalUserInfo();
				if (uinfo == null){
					uinfo = ConnectionBroker.get_SiteviewApi().get_AuthenticationService().CreateUserInfo("User", AuthenticationSource.Internal);
					user.set_InternalUserInfo(uinfo);
				}
				uinfo.set_AuthenticationId(txtID.getText());
				if (true)
					uinfo.set_Password(this.txtPasswd.getText());
				}
			
			SiteView.ecc.editors.User u=new SiteView.ecc.editors.User(user.get_LoginId(), user.get_LoginId(), "允许", user.get_SecurityGroupName(), user);
			UserInfor.list.add(u);
			UserManager.TableViewer.setInput(UserInfor.list);
			UserManager.TableViewer.refresh();
			user.set_BusObId(compUserPanel.get_BusinessObject().get_Id());
			ConnectionBroker.get_SiteviewApi().get_AuthenticationService().SaveUser(user);
			compUserPanel.get_BusinessObject().SaveObject(ConnectionBroker.get_SiteviewApi(), true, false);
		}
		this.close();
	}
	private void ValidateUserData() throws Exception{
		if (this.chkAllowInternalAuth.getSelection()){
		   if (this.txtPasswd.getText().trim().length() <= 5){
		       this.txtPasswd.setFocus();
		       MsgBox.Show(this.getShell(), "无效的密码！", "错误");
		   }
		if (!this.txtPasswd.getText().equals(this.txtPasswdAgain.getText())){
		    this.txtPasswdAgain.setFocus();
		    MsgBox.Show(this.getShell(), "两次输入的密码不一致！", "错误");
		 }
		}
	}	
		            
	public BusinessObject GetBusOb(String strBusObId){
		//如果是新建用户
		if (!EditUser){
			return ConnectionBroker.get_SiteviewApi().get_BusObService().Create(ConnectionBroker.get_SiteviewApi().get_BusObDefinitions().GetLoginProcedureDef("User").get_BusObName());
		}else{
			SiteviewQuery query = new SiteviewQuery();
			query.AddBusObQuery(ConnectionBroker.get_SiteviewApi().get_BusObDefinitions().GetLoginProcedureDef("User").get_BusObName(), QueryInfoToGet.All);
			XmlElement element = query.get_CriteriaBuilder().FieldAndValueExpression(ConnectionBroker.get_SiteviewApi().get_BusObDefinitions().GetLoginProcedureDef("User").get_BusObName() + ".RecID", Siteview.Operators.Equals, strBusObId);
			query.set_BusObSearchCriteria(element);
			BusinessObject bu= ConnectionBroker.get_SiteviewApi().get_BusObService().GetBusinessObject(query);
			if (bu == null)
			   bu = ConnectionBroker.get_SiteviewApi().get_BusObService().Create(ConnectionBroker.get_SiteviewApi().get_BusObDefinitions().GetLoginProcedureDef("User").get_BusObName());
			   return bu;
		}
	}
	public void saveBusOb(BusinessObject busOb) throws Exception{
		UpdateResult result = new UpdateResult();
		result = busOb.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
		if (!result.get_Success())
        {
            throw new Exception(result.get_ErrorMessages(), null);
        }
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

