package a4.client;

import java.io.File;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class a4_client {
	
	public void downloadFileClient() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource("http://localhost:8080/A4_DistributedFileSystem");
		WebResource nameResource = resource.path("rest").path("getFile").path("download");
		Builder builder = nameResource.getRequestBuilder();
	}

}
