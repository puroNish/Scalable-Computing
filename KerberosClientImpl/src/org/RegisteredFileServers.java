package org;

public class RegisteredFileServers {
	String serverName;
	String address;
	String portNumber;
	
	public String getUsername() {
		return serverName;
	}
	public String getAddress() {
		return address;
	}
	public String getPortNumber() {
		return portNumber;
	}
	public void setUsername(String username) {
		this.serverName = username;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}
	
}
