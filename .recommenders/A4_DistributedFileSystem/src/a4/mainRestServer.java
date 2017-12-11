package a4;
import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/getFile")
public class mainRestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@GET
	@Path("/testServer")
	@Produces(MediaType.TEXT_PLAIN)
	public String restChalaKeNahi() {
		return new String("Chala BC");
	}
	
	@GET
	@Path("/download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile() {
	  File file = new File("C:\\Users\\nishp\\Music\\demo.java");
	  return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
	      .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
	      .build();
	}

}
