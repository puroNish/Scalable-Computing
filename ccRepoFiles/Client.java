import java.net.*;
import java.io.*;
import java.util.*;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;
	static OutputStream os ;
    static OutputStreamWriter osw;
    static BufferedWriter bw;
    private static testChatRoom test;
    static InputStream is ;
    static InputStreamReader isr;
   static BufferedReader br;
	
	
	// if I use a GUI or not
	//private ClientGUI cg;
	
	// the server, the port and the username
	public String server, username;
	private int port;

	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		//this(server, port, username, null);
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	/*Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
		//this.cg = cg;
	}*/
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			bw.write("USER:"+ username +"\n");
			bw.flush();
			
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		System.out.println(msg);
		/*if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");*/		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendmessage(String s) {
		try {
			bw.write(s + "\n");
			bw.flush();
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
		
	}
	
	/*void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}*/

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		

		try { 
			if(isr != null) isr.close();
		}catch(Exception e) {} 
		

		try { 
			if(osw != null) osw.close();
		}catch(Exception e) {} 
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		/*if(cg != null)
			cg.connectionFailed();*/
			
	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 2150;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if(!client.start())
			return;
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		
		// loop forever for message from the user
		while(true) {
			//System.out.print("> ");
			// read message from u
			String msg = scan.nextLine();
			
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("ListRooms")) {
				client.sendmessage("LISTROOMS");
				// break to do the disconnect
				
			}else if(msg.equalsIgnoreCase("HELO BASE_TEST")) {
				client.sendmessage("HELO BASE_TEST");				
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendmessage("I am here");				
			}
			else if(msg.startsWith("JOIN_CHATROOM")) {
				  
				client.sendmessage("JOIN_CHATROOM:ROOM1"+"\n"+"CLIENT_IP:0"+"\n"+"PORT:0"+"\n"+"CLIENT_NAME:" +userName);
				//client.sendmessage("CLIENT_IP:");
				//client.sendmessage("PORT:");
				//client.sendmessage("CLIENT_NAME:" +userName);
				
			}else if(msg.startsWith("LEAVE")) {
				     
				client.sendmessage("LEAVE_CHATROOM:1");
				client.sendmessage("JOIN_ID:");
				client.sendmessage("CLIENT_NAME:"+userName);
			}
			
			else if(msg.startsWith("CLIENT_NAME")) {
			     
				client.sendmessage("CLIENT_NAME:"+userName);
				
			}
			else if(msg.equalsIgnoreCase("CREATE")) {
				client.sendmessage("Create:Room1");				
			}
			else if(msg.equalsIgnoreCase("KILL")) {
				client.sendmessage("KILL SERVICE \n");
				break;
			}
			else if(msg.equalsIgnoreCase("MESSAGE")) {
				//msg=scan.nextLine();
				client.sendmessage("CHAT:1");
				client.sendmessage("JOIN ID:");
				client.sendmessage("CLIENT_NAME:"+userName);
				client.sendmessage("MESSAGE:");
				
			}
			else if(msg.equalsIgnoreCase("DISCONNECT")) {
				
				client.sendmessage("DISCONNECT:0"+"\n"+"PORT:0"+"\n"+"CLIENT_NAME:"+userName);
				
				break;
				
			}
			else {				// default to ordinary message
				client.sendmessage("Client: I am in this Room");
			}
		}
		// done disconnect
		client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					
					String msg = br.readLine();	
					if(msg==null)
					{
						break;
					}
					// if console mode print the message and add back the prompt
					System.out.println(msg);
					
					
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					/*if(cg != null) 
						cg.connectionFailed();*/
					break;
				}
				// can't happen with a String object but need the catch anyhow
				
			}
		}
	}
}
