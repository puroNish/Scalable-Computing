package org;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@Path("/AuthServer")
public class AuthenticationServer {
	final static String nameServerAddress = new String("http://localhost:8080/NameServer/rest/NameServer");
	// final static AuthUserList clients = new AuthUserList();
	final static ArrayList<AuthenticatedUsers> userDB = new ArrayList<>();
	static {
		AuthenticatedUsers adminBean = new AuthenticatedUsers();
		adminBean.setUsername("username");
		adminBean.setPassword("password");
		userDB.add(adminBean);
		System.out.println(adminBean.getUsername());
		AuthenticatedUsers userOneBean = new AuthenticatedUsers();
		adminBean.setUsername("nish");
		adminBean.setPassword("nishant");
		userDB.add(userOneBean);
		System.out.println(userOneBean.getUsername());
	}

/*	public AuthenticationServer() {
		// TODO Auto-generated constructor stub
		
	}*/

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AuthenticationServer obj = new AuthenticationServer();
//		System.out.println(usr);
	}

	@GET
	@Path("/authClient/{userName}/{clientID}/{password}")
	@Produces(MediaType.TEXT_PLAIN)
	public String authenticateClient(@PathParam("userName") String userName, @PathParam("clientID") String clientID,
			@PathParam("password") String password) {
		for (AuthenticatedUsers user : userDB) {
			System.out.println(user.getUsername());
			System.out.println(user.getPassword());
			if (userName.equals(user.getUsername()) && password.equals(user.getPassword())) {
				boolean registered = sendNameServerNewClients(clientID);
				return nameServerAddress;
			}
		}
		return "WRONG";
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	private static boolean sendNameServerNewClients(String clientID) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(nameServerAddress).path("registerClient");
		String response = resource.type(MediaType.TEXT_PLAIN).post(String.class, clientID);
		// String output = response.getEntity(String.class);
		if (response.equals("EXISTS") || response.equals("ClientID added")) {
			return true;
		}
		return false;
	}
	/*
	 * @GET
	 * 
	 * @Path("/authClient/{password}/{clientID}")
	 * 
	 * @Produces(MediaType.TEXT_PLAIN)
	 * 
	 * @Consumes(MediaType.TEXT_PLAIN) public String
	 * getPassword(@PathParam("password") String password, @PathParam("clientID")
	 * String clientID) { for (AuthenticatedUsers user : userDB) {
	 * if(userName.equals(user.getUsername())) { return "NEXT"; } } return "WRONG";
	 * }
	 */

	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String testServer() {
		return new String("Server is UP");
	}

}
