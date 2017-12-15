# Distributed-File-System

Nishant Purohit
Student ID :: 17319840
M.Sc. Computer Science


Distributed File Server

The task was to design and implement a distributed file system including a set of 7 optional features. The features/services to be implemented with the DFS were :

•	Distributed Transparent File Access
•	Security Service
•	Directory Service
•	Replication
•	Caching
•	Transactions
•	Lock Service

---------------------------------------------------------------------------------------------------------------------------------------

As of 15th December 2017, the following features have either been implemented in full or a Proof-of-code has been implemented and has not be integrated with the main code due to some run-time error. 

Distributed File System:  
As of now, the Server system consists of  the following servers –
Name Server :: This Server is the first one to start. It keeps the registry of all the files available on the network of File Servers along with the nodes they are saved on. It is an independent server and the File Servers connect to it to register on the network. As soon as the Name Server starts, it initialises a list of registered File Servers and registered clients. 
With every File Server registering with the Name Server, the Name server populates a registry where it keeps the data as two lists. One is for each File Server and all the files on it. The other one for each file on the system and all the servers it is hosted on the system.
It also manages the read-only or write-access requested through a client. This way it can ‘lock’  a file for one client if required and can deny it if already locked by another client.
Another feature Name Server manages is the replication. Every time a new file is introduced in the file system and the File Server is not the first one, the Name Server requests one of the other File Servers to download and save the file to its repository. A future implementation is to run a method to check the number of nodes file is present on. It would replicate the files on other nodes if less than two copis are present on the system. It could all be implemented within the Name Server.
 
File Servers :  These are the servers with the files in their file system. These servers , upon initialisation, creates a list of Meta data of all the files available on the server. It then registers with the Name Server with its unique ID and Name Server in return requests for the meta data list. 
I have created 3 File Servers as of now with two files each. All files are present on at least two servers.

Authentication Server: This server is used to authenticate the users before allowing them to be connected to the Name Server. The clients/users go through this server before connecting to the Name Server. The clients receive the link to the Name Server only after successful authentication.
The Kerberos, 3 key authentications could not be implemented on this server due to some unknown error I could not solve before the deadline.
For Kerberos, I had created three other servers to mock the working of the Kerberos.

DFS Client : This is the entry point of the entire system. 
It starts with the client checking if the Server is up or not. Once it is up, it request for Username & Password of the user. If successfully authenticated, the Authentication Server sends the ‘serverAddress’ and ‘serverPort’ of the Name Server . 
It then connects to the Name Server and lists all the files available on the network.
Once the user selects a correct file, it request for a read-only or a write access for the file. If the access requested is a read-only, it requests the file from the server and shows the output on the console.
If the access requested is ‘write’, the Name server checks for the availability of the file. If available, it locks the fileserver assigned to the client for the file. It only allows read-only access while the file is being written. 
The client allows the user to release the file from the lock upon completion of the write action. Once allowed, the client sends the request to the Name Server who then releases the write lock and updates the network.
Also, the client keeps  a copy of the file in a local working directory if it is the first time it is being requested. Everytime a ‘read-only’ request is assigned, it first checks the cache for the file. For write access, it always goes to the Name Server.

Features Implemented/attempted:
--------------------------------
Distributed Transparent File Server
Security Service
Replication
Caching
Lock Service
