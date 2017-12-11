import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
//import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;





	public class ClientThread implements Runnable{
		
		// the socket where to listen/talk
		Socket socket;
		InputStream is;
	    InputStreamReader isr;
		BufferedReader br;
		OutputStream os;
		OutputStreamWriter osw;
		BufferedWriter bw;
		
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		private  String username;
		// the only type of message a will receive
		String cm;
		// the date I connect
		String date;
		private testChatRoom testChatRoom;
		private boolean connected;
		private SimpleDateFormat sdf;
		private int joinId;
		private int portNumber;
		private int roomRef;

		public ClientThread(String username) {
	        this.username = username;
	    }
		
		public ClientThread(Socket s, testChatRoom testChatRoom) throws IOException {
	        this.testChatRoom = testChatRoom;
	        this.socket=s;
	        this.username=username;
	        
	        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        bw = new BufferedWriter(new PrintWriter(s.getOutputStream()));
	        this.joinId=new Random().nextInt(6)+1;
	        this.portNumber=2150;
	       
	    }
		
	
		
		public  String isOnline() {
			 
			   
			    try {
			       
			    	   writeMsg("HELO BASE_TEST"+"\n"+"IP: " + Inet4Address.getLocalHost().getHostAddress() +"\n"+
			         "Port:"+this.portNumber+"\n"+"StudentID:17311921");
			        
			      } catch(UnknownHostException uhe) {
			         System.out.println("Host Not Found");
			      } catch(Exception e) {
			         System.out.println(e);
			      }
			   
			  return "Success";
			}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			connected=true;
		        // Wait to receive the client login message
		        String login = recieveMsg ();
		        System.out.println("Message received from client is "+login);
		        // Check that the login message is correct
		       if (! login.startsWith ("USER")) {
		            
		     writeMsg ("400 Invalid Package received");
		            
		        connected = false;
		        } else {
		            // Check that the size of the nick is not too long
		        if (login.split (":") [1] .length () >= 12) {
		                // Send an error and disconnect the user
		           	writeMsg ("400 The username chosen is too long, enter a username of a maximum of 12 characters");
		                
		          } else {
		                // Connect to the client if the user does not exist in the room
		                connected =! testChatRoom.existsUser(this);
		            }
		        }
		        // If everything is correct, it connects to the room
		     if (connected) {
		            // We get the nick received from the client
		            connected =true;
		                //String login = recieveMsg ();
		                username = login.split (":") [1];
		            
		            // We connect the user to the room
		            testChatRoom.enter(this);
		            // We send the list of users of the room
		            sendUserList ();
		            
		            // We send the name of the room
		            //writeMsg ("ROOM" + testChatRoom.getName ());
		            
		            // Loop that will last until the user disconnects (EXIT or errors)
		                 
		            do {
		                // We are waiting to receive a message from the client
		                String packet = recieveMsg ();
		                System.out.println("MESSAGE FROM CLIENT"+packet);
		                // If the package is not empty, we analyze it
		                if (packet != null &&! packet.isEmpty ()) {
		                	     
		                  	try {
								analyzeMessage (packet);
								//System.out.println(packet);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }
		                
		            } while (connected);
		            
		            // We send the disconnection message to the user
		            //writeMsg ("400 You have been disconnected from the chat");
		            
		            // The client is no longer connected, we take him out of the room
		             //testChatRoom.exit (this);
		      }
		    }

			
		
		
		

		
		private void analyzeMessage(String msg) throws UnknownHostException  {
			
			
			if  (msg.equalsIgnoreCase ("LISTROOMS")) {// Request for a list of available rooms
	                // We obtain an array of strings with the names of the rooms
				    Server server=new Server();
	                testChatRoom [] sl = server.getRoom();
	                // We send the information of the rooms to the user who makes the request
	                writeMsg ("=========================");
	               // bw.flush();
	                writeMsg("Rooms available:" + sl.length);
	                //bw.flush();
	                writeMsg ("=========================");
	                //bw.flush();
	                for (testChatRoom sl1: sl) {
	                	 writeMsg (sl1.getName () + "- Users:" + sl1.getCountUsers () +"");
	                   // bw.flush();
	                }
	               //bw.write ("=========================");
	                //bw.flush();
			} else if (msg.startsWith("JOIN_CHATROOM")) {
	            System.out.println(msg);
				String[] p;
	            p = msg.split(":");
	            
	            if (p.length > 3) {
	            	writeMsg("500 Incorrect Syntax");
	                
	            } else {
	            	  if (! Server.existRoom (new testChatRoom (p [1]))) {
                    testChatRoom sl = null;
                    // Room without password (1 parameter)
                    if (p.length == 2) {
                        // We created the room
                        sl = new testChatRoom (p [1]);
                    }/* else if (p.length == 3) {// Room with password (2 parameters)
                        // We created the room
                        sl = new testChatRoom (p [1], p [2]);
                    }*/
                    if (sl!= null) {
                        // We add the room to the list of rooms
                        Server.addRoom (sl);
                        // We take the user out of the current room
                        writeMsg ("JOINED_CHATROOM:" + sl.getName ()+"\n"+"SERVER_IP:"+ Inet4Address.getLocalHost().getHostAddress()+"\n"+
                          "PORT:"+this.portNumber +"\n"+"ROOM_REF:" + Server.getRoomRef (sl)+"\n"+"JOIN_ID:" +this.joinId);
                                
                        
                        
                        testChatRoom.exit (this);
                        // We put it in the new room
                        sl.enter (this);
                        // We change the room in the user
                        testChatRoom = sl;
                        // We send the name of the new room to the user
                       
                       
                        
                        
                        testChatRoom.updateListedUsers();
                        
                        testChatRoom.broadcast("CHAT:"+Server.getRoomRef(sl)+"\n"+"CLIENT_NAME:"+this.username+"\n"+"MESSAGE:"+this.username+ "has join this chatroom"+"\n");

                        
                        
                        
                    }
                }  else 
	              
	                 {
	                	
	                          if (p.length == 2) {
	                            
	                            testChatRoom sl = Server.getRooms(p[1]);
	                            
	                                testChatRoom.exit(this);
	                                
	                                sl.enter(this);
	                                
	                                testChatRoom = sl;
	                                writeMsg ("JOINED_CHATROOM:" + sl.getName ()+"\n"+"SERVER_IP:0"+ Inet4Address.getLocalHost().getHostAddress()+"\n"+
	                                        "PORT:"+this.portNumber +"\n"+"ROOM_REF:" + Server.getRoomRef (sl)+"\n"+"JOIN_ID:" +this.joinId);
	                                
	                                
	                               
	                                
	                                testChatRoom.updateListedUsers();
	                                testChatRoom.broadcast("CHAT:"+Server.getRoomRef(sl)+"\n"+"CLIENT_NAME:"+this.username+"\n"+"MESSAGE:"+this.username+ "has joined this chatroom"+"\n");
	                               
	                           
	                        } 
	                     
	                } 
	            }
	        }
			
			else if (msg.startsWith("LEAVE_CHATROOM") ) { 
	   	           System.out.println(msg); 
	            	   String[] p;
		            p = msg.split(":");
		            String temp=p[1];
		            int temp1 = Integer.parseInt(temp);
		            String sp=Server.getRoomName(temp1);
		            testChatRoom sl = Server.getRooms(sp);
		            writeMsg("LEFT_CHATROOM:"+Server.getRoomRef(sl)+"\n"+"JOIN_ID:"+this.joinId	);
		            testChatRoom.broadcast("CHAT:"+Server.getRoomRef(sl)+"\n"+"CLIENT_NAME:"+this.username+"\n"+"MESSAGE:"+this.username+ "has left  this chatroom"+"\n");

                    testChatRoom.exit(this);
                     testChatRoom.updateListedUsers();
                       
		         
		               
		               
		            
	               }else if(msg.startsWith("CHAT"))
	            	   
	               {
	            	   String[] p;
			            p = msg.split(":");
			            String temp=p[1];
			            int temp1 = Integer.parseInt(temp);
			            String sp=Server.getRoomName(temp1);
			            testChatRoom sl = Server.getRooms(sp);
	            	 
	            	   testChatRoom.broadcast("CHAT:"+Server.getRoomRef(sl)+"\n"+"CLIENT_NAME:" +this.username+"\n"+"MESSAGE:"+"hello world from"+" "+this.username+"\n");
	            	   
	               }
	               else if(msg.startsWith("HELO BASE_TEST"))
	               {
	            	   isOnline();
	               }
	               else if(msg.startsWith("DISCONNECT"))
	               {
	            	   
	            	   testChatRoom.broadcast(this.username+" "+"Has Been Disconnected from Server ");
	            	   close();
	            	   connected = false;
	            	   
	            	   
	               }
	               else if (msg.startsWith("KILL SERVICE")) {
	            	           writeMsg("Shutting down the server");
	            	           
	            	           
	            	        	        
							
								Server.close();
								System.exit(0);
								
							
	               }
			
	            }
 	
		
		

		public void writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				
			}
			// write the message to the stream
			try {
				bw.write(msg + "\n");
				bw.flush();
			}
			
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			
		}
		private void display(String msg) {
			String time = sdf.format(new Date()) + " " + msg;
			System.out.println(time);
			
			}
			
		public String recieveMsg() {
			// if Client is still connected send the message to it
			
			String msg="";
			if(!socket.isConnected()) {
				close();
				return msg;
			} else {
			// write the message to the stream
			
				try {
					msg= br.readLine();
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
			// if an error occurs, do not abort just inform the user
			   return msg;
			}
		}
		private void close() {
			// try to close the connection
			try {
				if(osw != null) osw.close();
			}
			catch(Exception e) {}
			try {
				if(isr != null) isr.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
		 	
		public String getUsername() {
			
	        return username;
	    }
		public void setUsername(String username) {
			
			this.username=username;
			
			
		}
	
	 public boolean isConnected () {
	        return connected;
	    }

	public void setConnected ( boolean connected) {
	        this.connected = connected;
	    }

	public void sendMessage(String s) {
		try {
			bw.write(s + "\n");
			bw.flush();
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
		
	}

	public void sendUserList () {
        StringBuilder strb = new StringBuilder ();
        strb.append ("LIST: ");
        for (ClientThread usr:testChatRoom.getUsers()) {
            strb.append (usr.getUsername ());
            strb.append ("");
        }
        //sendMessage (strb.toString ());
    }

	
	
	  
}
