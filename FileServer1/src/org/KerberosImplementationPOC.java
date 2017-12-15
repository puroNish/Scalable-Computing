package org;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/kerberosAuth")
public class KerberosImplementationPOC {
	private final String keyTwo = "99886565";
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		KerberosImplementationPOC obj = new KerberosImplementationPOC();
		String str = new String("´:”™ƒ$sJb?'»Ä …þÍŠŸÄ)“");
		System.out.println(obj.authenticateClient("Nishant Purohit", str));
	}
	
	@POST
	@Path("/getToken/{username}")
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticateClient(@PathParam("username") String username,String token) throws Exception {
		System.out.println("Username::"+username);
		System.out.println("Token ::"+token);
		String response = "NOT AUTHENTICATED";
		String result = decrypt(token,keyTwo);
		if(result.equals(username)) {
			response="AUTHENTICATED";
		}
		return response;
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

}
