package org;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileMetaServerDataBean")
public class FileMetaServerDataBean {
	String fileName;
	String serverName;
	String serverUrl;
	String serverPort;
	
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
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
