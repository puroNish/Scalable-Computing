import java.util.ArrayList;



public class testChatRoom {

	public  String name;
	public  ArrayList <ClientThread> users;
	
	 
	   public testChatRoom (String name) {
		   this.name = name;
		   this.users = new ArrayList <> ();
		
		   }
	  
	   
	
	  public String getName () {
              return this.name;
          }
	  
	  public void setName (String name) {
          this.name = name;
      }
	  
	  public  String enter(ClientThread u) {
		 if (!existsUser (u)) {
			 //System.out.println(u);
			  u.setConnected (true);
			  
			 users.add(u);
		     //broadcast (u.getUsername ()+"  " + "entered the room" +" "+ this.getName());
			 
			  updateListedUsers ();
			  //u.sendMessage ("RoomName"+" "+	this.getName());
		  } else {
	             // We disconnect the user
	             u.setConnected (false);
	             return "400 The user is already in the room";
		 }
		  return "ok";
		  
	  }
	  
	  public void exit(ClientThread u) {
		// If the user exists we leave the room 
		  
		  //System.out.println(u.getUsername());
		if (existsUser(u)) {// We remove it from the user list of the
			
			//broadcast(u.getUsername()+"  " +"to come out of the room"+" "+ this.getName());
			users.remove (u);
			//u.writeMsg("CHAT:"+1+"\n");
			
		// We spread the outgoing message to all members of the room broadcast 
		 //broadcast(u.getUsername()+"  " +"to come out of the room"+" "+ this.getName()); 
		// We send the updated list of users to all the users of the room 
		 updateListedUsers (); 
		
		}
	  }

	  
	  public boolean existsUser(ClientThread u) {
			//System.out.println("In Exists");
	        for (ClientThread usr : users) {
	        	//System.out.println("In for loop");
	            if (usr.getUsername().equalsIgnoreCase(u.getUsername())) {
	                return true;
	            }
	        }
	        return false;
	    }
	  

	  
	  

	  public ArrayList<ClientThread> getUsers () {
          return users;
      }
	  
	  public int getCountUsers() {
	        return users.size();
	    }
	  
	  public  void updateListedUsers () {
          
          for (ClientThread usr: users) {
                   usr.sendUserList();
 }
}
	  public void broadcast (String message) {
		  //System.out.println("Broadcasting now");
	         for (ClientThread usr: users) {
	        	 //System.out.println("Sending now");
	        	 //	System.out.println(message);
	             usr.sendMessage (message);
	         }
	     }

	  
}
