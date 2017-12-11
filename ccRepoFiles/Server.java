import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;



/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	
	private static int uniqueId;
	
	private SimpleDateFormat sdf;
		private static int port;
	private static String IP;


	private static ServerSocket serverSocket;
	
	
    public static ArrayList <testChatRoom> ListRooms = new ArrayList<testChatRoom>();

	private static Socket socket;
    
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
		
		}
public static void main(String[] args) throws IOException {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 2150;
		
		ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("Server has been initialized with port"+portNumber);
		testChatRoom Main=new testChatRoom("Main");
		
		  addRoom(Main);
		 
		  while(true) {
			  
			  //We create a new instance by passing the socket and the main room
			   socket = serverSocket.accept(); 
		
			  new Thread(new ClientThread(socket, Main)).start();
			  
		  }
	}
	
	public static void close()
	{
		try {
			if (null != serverSocket) {
				serverSocket.close();
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
		Thread.currentThread().interrupt();
	}

	public   static void addRoom(testChatRoom s) {
	      if (getRooms (s.getName ()) == null) {
	            ListRooms.add(s);
	     
	      }
	 }      
	         
	 public static testChatRoom getRooms(String name) {
	        for (testChatRoom s: ListRooms) {
	            if (s.getName (). equalsIgnoreCase (name)) {
	                return s;
	            }
	        }
	        return null;
	    }
	 public static   testChatRoom [] getRoom() {
	    	testChatRoom [] s = new testChatRoom [ListRooms.size ()];
	    for (int i = 0; i <ListRooms.size (); i ++) {
	    s [i] = ListRooms.get (i);
	   }
	   return s;
	    	}
	 
	 public   static int getRoomRef(testChatRoom s) {
		int roomRef=0;
		 for (int i = 0; i <ListRooms.size (); i ++) {
	            if (ListRooms.get (i) .getName (). equalsIgnoreCase (s.getName ())) {
	               roomRef=ListRooms.indexOf(s) ;
	                
	            }
	        }
		 return roomRef;
	    }
	 
	 public static String getRoomName(int i) {
		 
	 if (i<=ListRooms.size()) {
		     
		     return ListRooms.get(i).name;
	 }
	 return "room does not exist";
	  }
	 
	 
	 
	 public   static boolean existRoom(testChatRoom s) {
			for (int i = 0; i <ListRooms.size (); i ++) {
	            if (ListRooms.get (i) .getName (). equalsIgnoreCase (s.getName ())) {
	                return true;
	            }
	        }
			return false;
		}
	
}

	
	


