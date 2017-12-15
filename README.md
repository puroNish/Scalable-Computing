Nishant Purohit
Student ID :: 17319840
M.Sc. Computer Science


Cyclomatic Complexity
As per Wikipedia ,Cyclomatic complexity is a software metric (measurement), used to indicate the complexity of a program. It is a quantitative measure of the number of linearly independent paths through a program's source code.

I have calculated the cyclomatic complexity as per the McCabe Cyclomatic Complexity (MCC) with the following algorithm :

We start with an initial (default) value of one (1). Add one (1) for each occurrence of each of the following:

if statement
while statement
for statement
case statement
catch statement
&& and || boolean operations 
?: ternary operator and ?: Elvis operator.
?. null-check operator

We Add (1) for each condition in If statement, if it contains && or || operations

Also, we Subtract (1) for each occurrence return statement.


Architecture:
I have managed to create a Server and a client to mock the working of a multiple client environment.
Firstly, the Server reads the GIT repository upon initialisation. It creates a list of valid commits and keeps it ready to be assigned for the clients.

The client(s) could be initialised on as many nodes as possible. As soon as the client starts, it pings the server for check if it is ready. As soon as the Server is ready with the commits, its ‘repoListEmpty’ flag is changed and it allows the clients to be assigned.
The client starts with registering itself to the Server. The Server saves the unique clientID of the client and assigns a commit to the client. The client then connects to the GIT repository on their own and pulls the selected commit. It parse through all the files in the commit and returns the ‘cc’ back to the Server.
The Server has been configured to keep track of all commits and the clients assigned with the commit. A commit once assigned to a client is not reassigned and if the same client tries to register again or requests for a commit again, it responds with the same commit. Until the cc has been responded by the client, no other commit is assigned to the client.

The total amount of time spend is reduced as the number of clients increase. The server and client have been coded in such a way that there is no limitation of number of clients allowed to connect to the Server. The clients could be spawned on as many nodes as possible, and they will register with the Server on their own and collect a commit to work on.
