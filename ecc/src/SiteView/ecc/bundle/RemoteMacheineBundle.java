package SiteView.ecc.bundle;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import SiteView.ecc.dialog.BatchAddMachine;
import SiteView.ecc.tools.TextUtils;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;

import siteview.IAutoTaskExtension;

public class RemoteMacheineBundle implements IAutoTaskExtension {
	String[] ss=null;
	APIInterfaces rmiServer;
	@Override
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		// TODO Auto-generated method stub
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		String remoteMachineInfo = "";
		List<String[]> c=null;
		String hostname=bo.GetField("ServerAddress").get_NativeValue().toString();
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
					String password = TextUtils.obscure(bo
							.GetField("PasswordMachine").get_NativeValue()
							.toString());
					String value=bo
							.GetField("DisableConnectionCaching")
							.get_NativeValue().toString();
					
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+="_disableCache="+"on";
					}else{
						remoteMachineInfo+="_disableCache= ";
					}
					value=bo.GetField("Trace").get_NativeValue().toString();
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+=";_trace="+"on";
					}else{
						remoteMachineInfo+=";_trace= ";
					}
					value=bo.GetField("SSHVersion2Only").get_NativeValue().toString();
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+=";_version2="+"on";
					}else{
						remoteMachineInfo+=";_version2= ";
					} 
					if(bo.get_Name().contains("NT")){
						remoteMachineInfo+=";_os=NT";
					}else{
						value=bo.GetField("OS").get_NativeValue().toString();
						if(value.equals("Red Hat Enterprise Linux")){
							remoteMachineInfo+=";_os=RHESLinux";
						}
					}
					if(bo.GetField("SSHClient").get_NativeValue().toString().contains(" Java ")){
						remoteMachineInfo+=";_sshClient=java";
					}
					if(bo.get_Name().contains("RemoteUnix")){
						remoteMachineInfo+=";_prompt="
								+ bo.GetField("Prompt").get_NativeValue().toString()
								+ ";_passwordPrompt="
								+ bo.GetField("PasswordPrompt").get_NativeValue().toString()
								+ ";_loginPrompt="
								+ bo.GetField("LoginPrompt").get_NativeValue().toString()
								+ ";_secondaryPrompt="
								+ bo.GetField("SecondaryPrompt").get_NativeValue().toString();
					}
					remoteMachineInfo+=";_status=unknown"
							+ ";_sshPort="
							+ bo.GetField("PortNumber").get_NativeValue().toString()
							+";_id="
							+ bo.GetField("RecId").get_NativeValue().toString()
							+";_method="
							+ bo.GetField("ConnectionMethod").get_NativeValue().toString()
							+ ";_sshCommand="
							+ bo.GetField("CustomCommandline").get_NativeValue().toString()
							+ ";_login="
							+ bo.GetField("UserName").get_NativeValue().toString()
							+ ";_host="
							+ bo.GetField("ServerAddress").get_NativeValue().toString()
							+ ";_sshAuthMethod="
							+ bo.GetField("SSHAuthentication").get_NativeValue().toString()
							+ ";_sshConnectionsLimit="
							+ bo.GetField("ConnectionLimit").get_NativeValue().toString()
							+ ";_keyFile="
							+ bo.GetField("KeyFileforSSHconnections").get_NativeValue().toString()
							+ ";_password="+ password
							+ ";_name="
							+ bo.GetField("Title").get_NativeValue().toString()+"\n";
					if(bo.get_Name().equals("RemoteMachine.RemoteNT")){
						remoteMachineInfo="_remoteNTMachine=;"+remoteMachineInfo;
					}else{
						remoteMachineInfo="_remoteMachine=;"+remoteMachineInfo;
					}
				c=rmiServer.doTestMachine(remoteMachineInfo,hostname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String s=c.get(0)[0].replaceAll(" ", ".");
		bo.GetField("Status").SetValue(new SiteviewValue(s));
		MessageDialog.openInformation(new Shell(), "link test", c.get(0)[0]);
		BatchAddMachine b=new BatchAddMachine(null);
		b.s=c;
		b.hostname=hostname;
		b.group=bo.GetField("Groups").get_NativeValue().toString();
		b.open();
		return null;
	}
}
