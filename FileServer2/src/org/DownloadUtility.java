package org;

import java.io.File;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/downloadUtility")
public class DownloadUtility {
	final String workingDir = "C:\\FileServer\\FileServer1";
	public final static ArrayList<FileMetaServerDataBean> listOfFiles = new ArrayList<FileMetaServerDataBean>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String testServer() {
		return new String("Server is UP");
	}

	@GET
	@Path("/download/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("fileName") String fileName) {
		
		File file = new File(workingDir.concat("\\").concat(fileName));
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
	}
}
