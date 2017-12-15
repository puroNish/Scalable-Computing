package org;

import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/autheticate")
public class AuthenticationServer {
	private static HashMap<String, String> userList = new HashMap<String, String>();
	private static HashMap<String, String> servers = new HashMap<String, String>();
	private static ArrayList<RegisteredClients> clientList = new ArrayList<RegisteredClients>();
	private static ArrayList<RegisteredFileServers> fileServerList = new ArrayList<RegisteredFileServers>();

	static {
		userList.put("Nishant Purohit", "85909090");
		userList.put("Purohit Purohit Purohit", "78787855");
		servers.put("FileServer1", "99886565");
		servers.put("FileServer2", "85859996");
		RegisteredClients testClientBean = new RegisteredClients();
		testClientBean.setAddress("127.0.0.1");
		testClientBean.setPortNumber("8080");
		testClientBean.setClientID("Client_1");
		clientList.add(testClientBean);
		RegisteredFileServers testFileServer = new RegisteredFileServers();
		testFileServer.setAddress("127.0.0.1");
		testFileServer.setPortNumber("8080");
		testFileServer.setServerName("FileServer1");
		fileServerList.add(testFileServer);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		AuthenticationServer obj = new AuthenticationServer();
		System.out.println(obj.getToken("Nishant Purohit", "FileServer1", "Client_1"));
	}

	@GET
	@Path("/getToken/{username}/{clientID}/{forFileServerName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getToken(@PathParam("username") String username,@PathParam("forFileServerName") String forFileServerName,@PathParam("clientID") String clientID) throws Exception {
		if(userList.containsKey(username)) {
			String token = encrypt(username,servers.get(forFileServerName));
			String encryptedToken = encrypt(token,userList.get(username));
//			sendTokenToClient(encryptedToken,clientID);
			return encryptedToken;
		}
		return "";
	}

/*	public static void sendTokenToClient(String token,String clientID) {
		for(RegisteredClients clientBean : clientList) {
			if(clientBean.getClientID().equals(clientID)) {
				Client client = ClientBuilder.newClient();
				URL repoUrl = new URL("http", clientBean.getAddress(), Integer.parseInt(clientBean.getPortNumber()), "/");
				WebTarget resource = client.target(repoUrl.toString()).path(clientBean.getClientID()).path("rest")
						.path("downloadUtility").path("download").path(fileName);
				Response response = resource.request(MediaType.APPLICATION_OCTET_STREAM).get();
			}
		}
	}*/

	public String encrypt(String details, String key) throws Exception {
		String token ="";
		
		try {
			SecretKeySpec keySpec=new SecretKeySpec(key.getBytes(),"Blowfish");
//			IvParameterSpec ivSpec = new IvParameterSpec(details.getBytes());
			Cipher cipher=Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encrypted=cipher.doFinal(details.getBytes());
			token=new String(encrypted);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return token;
	}

}
