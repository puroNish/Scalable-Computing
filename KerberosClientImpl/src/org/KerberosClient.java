package org;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class KerberosClient {
	private final String userName = "Nishant Purohit";
	private final String keyOne = "85909090";
	private final String clientID = "Client_1";
	private final String autheticationServerAddress = "http://localhost:8080/KerberosPOCServer/rest/autheticate/getToken";
	private final String autheticationFileServerAddress = "http://localhost:8080/FileServer1/rest/kerberosAuth/getToken";
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		KerberosClient obj = new KerberosClient();
		obj.getToken();
	}
	
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	public void getToken() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(autheticationServerAddress).path(userName).path("FileServer1").path(clientID);
		String response = resource.type(MediaType.TEXT_PLAIN).get(String.class);
		System.out.println(response);
		String token = decrypt(response,keyOne);
		System.out.println(autheticateUser(token));
	}
	
	private String decrypt(String token, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String decryptedVal="";
		SecretKeySpec skeyspec=new SecretKeySpec(key.getBytes(),"Blowfish");
		Cipher cipher=Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, skeyspec);
		byte[] decrypted=cipher.doFinal(token.getBytes());
		decryptedVal=new String(decrypted);
		return decryptedVal;
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	private String autheticateUser(String token) {
		String response="";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(autheticationFileServerAddress).path(userName);
		response = resource.type(MediaType.TEXT_PLAIN).post(String.class,token);
		return response;
	}
	
	

}
