package org;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileServerBean")
public class FileServerBean {
	String serverName;
	ArrayList<FileMetaServerDataBean> listOfFiles;
	String serverAddress;
	String portNumber;
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public ArrayList<FileMetaServerDataBean> getListOfFiles() {
		return listOfFiles;
	}
	public void setListOfFiles(ArrayList<FileMetaServerDataBean> listOfFiles) {
		this.listOfFiles = listOfFiles;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public String getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

}
