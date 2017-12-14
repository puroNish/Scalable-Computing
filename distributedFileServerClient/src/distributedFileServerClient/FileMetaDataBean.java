package distributedFileServerClient;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileMetaDataBean")
public class FileMetaDataBean {
	String fileName;
	ArrayList<String> serversWithFile;
	String serverWithWriteAccess;
	boolean writeLock;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<String> getServersWithFile() {
		return serversWithFile;
	}
	public void setServersWithFile(ArrayList<String> serversWithFile) {
		this.serversWithFile = serversWithFile;
	}
	public String getServerWithWriteAccess() {
		return serverWithWriteAccess;
	}
	public void setServerWithWriteAccess(String serverWithWriteAccess) {
		this.serverWithWriteAccess = serverWithWriteAccess;
	}
	public boolean isWriteLock() {
		return writeLock;
	}
	public void setWriteLock(boolean writeLock) {
		this.writeLock = writeLock;
	}

}
