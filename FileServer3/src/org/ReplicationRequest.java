package org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/replication")
public class ReplicationRequest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@GET
	@Path("/replicateFile/{fileName}/{serverName}/{serviceAddress}/{portNumber}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void replicateFileRequest(@PathParam("fileName") String fileName,
			@PathParam("serviceAddress") String serviceAddress, @PathParam("portNumber") String portNumber,
			@PathParam("serverName") String serverName) throws NumberFormatException, IOException {
		getFile(fileName, serviceAddress, portNumber, serverName);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public static void getFile(String fileName, String serviceAddress, String servicePort, String serverName)
			throws NumberFormatException, IOException {
		URL repoUrl = new URL("http", serviceAddress, Integer.parseInt(servicePort), "/");
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(repoUrl.toString()).path(serverName).path("rest").path("downloadUtility")
				.path("download").path(fileName);
		Response response = resource.request(MediaType.APPLICATION_OCTET_STREAM).get();
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			File file = new File(FileServer.workingDir.concat("/").concat(fileName));
			InputStream in = response.readEntity(InputStream.class);
			byte[] buffer = new byte[in.available()];
			OutputStream out = new FileOutputStream(file);
			out.write(buffer);
			in.close();
			out.close();
		}
	}
}
