package SiteView.ecc.bundle;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import SiteView.ecc.tools.TextUtils;
import Siteview.Api.BusinessObject;
import Siteview.Api.Relationship;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteView.SiteViewGroup;

import siteview.IAutoTaskExtension;
import system.Collections.IEnumerator;

public class RemoteMacheineBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	@Override
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		// TODO Auto-generated method stub
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		String remoteMachineInfo = "";
		try {//_disableCache= _status=unknown _sshPort=22 
			//_os=AIX _id=56 _version2= _trace= _sshClient=java
			//_method=telnet _sshCommand= 
			//_keyFile=E:\workspace\siteview9.2\com.siteview.kernel.core\groups\identity_
			//_password=(0x)LEMNOBMOME _sshConnectionsLimit=3 
			//_login=admin _host=192.168.9.224 _sshAuthMethod=password 
					//_name=192.168.9.224
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
					Siteview.Api.BusinessObject remoteMachineBo=bo;
					String password = TextUtils.obscure(remoteMachineBo
							.GetField("PasswordUNIX").get_NativeValue()
							.toString());
					String value=remoteMachineBo
							.GetField("DisableConnectionCaching")
							.get_NativeValue().toString();
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+="_disableCache="+"on";
					}else{
						remoteMachineInfo+="_disableCache= ";
					}
					value=remoteMachineBo.GetField("Trace").get_NativeValue().toString();
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+=" _trace="+"on";
					}else{
						remoteMachineInfo+=" _trace= ";
					}
					value=remoteMachineBo.GetField("SSHVersion2Only").get_NativeValue().toString();
					if(value.equals("true")||value.equals("1")){
						remoteMachineInfo+=" _version2="+"on";
					}else{
						remoteMachineInfo+=" _version2= ";
					} 
					value=remoteMachineBo.GetField("OS").get_NativeValue().toString();
					if(value.equals("Red Hat Enterprise Linux")){
						remoteMachineInfo+=" _os=RHESLinux";
					}
					
					if(remoteMachineBo.GetField("SSHClient").get_NativeValue().toString().contains(" Java ")){
						remoteMachineInfo+=" _sshClient=java";
					}
					remoteMachineInfo+=" _status=unknown"
							+ " _sshPort="
							+ remoteMachineBo.GetField("PortNumber")
									.get_NativeValue().toString()
							+ " _prompt="
							+ remoteMachineBo.GetField("Prompt")
									.get_NativeValue().toString()
							+ " _id="
							+ remoteMachineBo.GetField("RecId")
									.get_NativeValue().toString()
							+ " _passwordPrompt="
							+ remoteMachineBo.GetField("PasswordPrompt")
									.get_NativeValue().toString()
							+ " _method="
							+ remoteMachineBo.GetField("ConnectionMethod")
									.get_NativeValue().toString()
							+ " _sshCommand="
							+ remoteMachineBo.GetField("CustomCommandline")
									.get_NativeValue().toString()
							+ " _keyFile="
							+ remoteMachineBo
									.GetField("KeyFileforSSHconnections")
									.get_NativeValue().toString()
							+ " _password="
							+ password
							+ " _sshConnectionsLimit="
							+ remoteMachineBo.GetField("ConnectionLimit")
									.get_NativeValue().toString()
							+ " _login="
							+ remoteMachineBo.GetField("UserName")
									.get_NativeValue().toString()
							+ " _host="
							+ remoteMachineBo.GetField("ServerAddress")
									.get_NativeValue().toString()
							+ " _sshAuthMethod="
							+ remoteMachineBo.GetField("SSHAuthentication")
									.get_NativeValue().toString()
							+ " _loginPrompt="
							+ remoteMachineBo.GetField("LoginPrompt")
									.get_NativeValue().toString()
							+ " _secondaryPrompt="
							+ remoteMachineBo.GetField("SecondaryPrompt")
									.get_NativeValue().toString()
							+ " _name="
							+ remoteMachineBo.GetField("Title")
									.get_NativeValue().toString()+"\n";
				rmiServer.writeRemoteMachineToFile("_remoteMachine= _secondaryResponse= "+remoteMachineInfo);
				rmiServer.doTestMachine(remoteMachineInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
