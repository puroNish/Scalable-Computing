package distributedFileServerClient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileMetaServerDataBean")
public class FileMetaServerDataBean {
	String fileName;
	String serverName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
