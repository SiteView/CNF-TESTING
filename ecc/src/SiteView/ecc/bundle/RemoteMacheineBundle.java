package SiteView.ecc.bundle;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import SiteView.ecc.tools.TextUtils;
import Siteview.Api.BusinessObject;
import Siteview.Api.Relationship;

import COM.dragonflow.Api.APIInterfaces;

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
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
			Relationship relationship = bo
					.GetRelationship("ComputerAssociatedRemoteUNIX");
			if (relationship != null) {
				IEnumerator it3 = relationship.GetObjects().GetEnumerator();
				while (it3.MoveNext()) {
					Siteview.Api.BusinessObject remoteMachineBo = (Siteview.Api.BusinessObject) it3
							.get_Current();
					String password = TextUtils.obscure(remoteMachineBo
							.GetField("PasswordUNIX").get_NativeValue()
							.toString());
					remoteMachineInfo = "_remoteMachine= _secondaryResponse="
							+ remoteMachineBo.GetField("SecondaryResponse")
									.get_NativeValue().toString()
							+ " _disableCache="
							+ remoteMachineBo
									.GetField("DisableConnectionCaching")
									.get_NativeValue().toString()
							+ " _initShellEnvironment="
							+ remoteMachineBo.GetField("InitializeEnvironment")
									.get_NativeValue().toString()
							+ " _status="
							+ remoteMachineBo.GetField("Status")
									.get_NativeValue().toString()
							+ " _sshPort="
							+ remoteMachineBo.GetField("PortNumber")
									.get_NativeValue().toString()
							+ " _prompt="
							+ remoteMachineBo.GetField("Prompt")
									.get_NativeValue().toString()
							+ " _os="
							+ remoteMachineBo.GetField("OS").get_NativeValue()
									.toString()
							+ " _id"
							+ remoteMachineBo.GetField("RecId")
									.get_NativeValue().toString()
							+ " _version2="
							+ remoteMachineBo.GetField("SSHVersion2Only")
									.get_NativeValue().toString()
							+ " _passwordPrompt="
							+ remoteMachineBo.GetField("PasswordPrompt")
									.get_NativeValue().toString()
							+ " _trace="
							+ remoteMachineBo.GetField("Trace")
									.get_NativeValue().toString()
							+ " _sshClient="
							+ remoteMachineBo.GetField("SSHClient")
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
				}
				rmiServer.writeRemoteMachineToFile(remoteMachineInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
