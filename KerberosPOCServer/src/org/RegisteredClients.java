package org;

public class RegisteredClients {
	String clientID;
	String address;
	String portNumber;
	
	public String getClientID() {
		return clientID;
	}
	public String getAddress() {
		return address;
	}
	public String getPortNumber() {
		return portNumber;
	}
	public void setClientID(String username) {
		this.clientID = username;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}
	
}
